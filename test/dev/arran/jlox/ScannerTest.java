package dev.arran.jlox;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static dev.arran.jlox.TokenType.*;

public class ScannerTest {

    @Test
    void shouldAddEOFToEnd() {
        var tokens = new Scanner("").scanTokens();
        assertEquals(1, tokens.size());
        assertEquals(EOF, tokens.get(0).type);
    }

    @ParameterizedTest
    @MethodSource("provideSimpleTokens")
    void shouldHandleSimpleTokens(String input, TokenType expectedToken) {
        var tokens = new Scanner(input).scanTokens();
        assertEquals(expectedToken, tokens.get(0).type);
    }

    @Test
    void shouldTreatSingleForwardSlashAsSlash() {
        var tokens = new Scanner("/").scanTokens();
        assertEquals(SLASH, tokens.get(0).type);
    }

    @Test
    void shouldTreatDoubleForwardSlashAsComment() {
        var tokens = new Scanner("// hello, world!").scanTokens();
        assertEquals(EOF, tokens.get(0).type);
    }

    @Test
    void shouldIgnoreWhitespace() {
        var tokens = new Scanner("\r\n      \n\r\r\r\n").scanTokens();
        assertEquals(EOF, tokens.get(0).type);
    }

    @Test
    void shouldIncrementLineNumber_whenNewLine() {
        var tokens = new Scanner("+-\n+-").scanTokens();
        assertEquals(1, tokens.get(0).line);
        assertEquals(1, tokens.get(1).line);
        assertEquals(2, tokens.get(2).line);
        assertEquals(2, tokens.get(3).line);
    }

    @Test
    void shouldHandleStrings() {
        var tokens = new Scanner("\"hello\" + \"world\"").scanTokens();
        assertEquals(STRING, tokens.get(0).type);
        assertEquals("hello", tokens.get(0).literal);
        assertEquals(PLUS, tokens.get(1).type);
        assertEquals(STRING, tokens.get(2).type);
        assertEquals("world", tokens.get(2).literal);
    }

    @Test
    void shouldHandleNumbers() {
        var tokens = new Scanner("1 123 3.14").scanTokens();
        assertEquals(NUMBER, tokens.get(0).type);
        assertEquals(1.0, tokens.get(0).literal);
        assertEquals(NUMBER, tokens.get(1).type);
        assertEquals(123.0, tokens.get(1).literal);
        assertEquals(NUMBER, tokens.get(2).type);
        assertEquals(3.14, tokens.get(2).literal);
    }

    @ParameterizedTest
    @MethodSource("provideKeywords")
    void shouldHandleKeywords(String keyword, TokenType expectedToken) {
        var tokens = new Scanner(keyword).scanTokens();
        assertEquals(expectedToken, tokens.get(0).type);
    }

    @Test
    void shouldHandleMaximalMunch() {
        var tokens = new Scanner("orchid").scanTokens();
        assertEquals(IDENTIFIER, tokens.get(0).type);
        assertEquals("orchid", tokens.get(0).lexeme);
    }

    private static Stream<Arguments> provideSimpleTokens() {
        return Stream.of(
                // Single purpose tokens.
                Arguments.of("(", LEFT_PAREN),
                Arguments.of(")", RIGHT_PAREN),
                Arguments.of("{", LEFT_BRACE),
                Arguments.of("}", RIGHT_BRACE),
                Arguments.of(",", COMMA),
                Arguments.of(".", DOT),
                Arguments.of("-", MINUS),
                Arguments.of("+", PLUS),
                Arguments.of(";", SEMICOLON),
                Arguments.of("*", STAR),

                // Dual purpose tokens.
                Arguments.of("!", BANG),
                Arguments.of("!=", BANG_EQUAL),
                Arguments.of("=", EQUAL),
                Arguments.of("==", EQUAL_EQUAL),
                Arguments.of("<", LESS),
                Arguments.of("<=", LESS_EQUAL),
                Arguments.of(">", GREATER),
                Arguments.of(">=", GREATER_EQUAL)
        );
    }

    private static Stream<Arguments> provideKeywords() {
        return Scanner.keywords.entrySet().stream()
                .map(e -> Arguments.of(e.getKey(), e.getValue()));
    }
}
