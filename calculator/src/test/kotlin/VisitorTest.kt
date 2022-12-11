import org.junit.jupiter.api.Test
import tokenizer.Tokenizer
import visitor.CalcVisitor
import visitor.ParseVisitor
import visitor.PrintVisitor
import java.io.ByteArrayOutputStream
import kotlin.test.assertEquals

internal class VisitorTest {
    @Test
    fun `print visitor test`() {
        val input = " (   1    - 2    )    /    3".byteInputStream()
        val tokenizer = Tokenizer(input)
        val out = ByteArrayOutputStream()
        PrintVisitor.print(out, tokenizer.tokenize())
        assertEquals("( 1 - 2 ) / 3", out.toString().trim())
    }

    @Test
    fun `parse visitor test`() {
        val input = "   2 +  2 *   228  +   1337 -  ( 100   - 10 ) / 2  ".byteInputStream()
        val tokenizer = Tokenizer(input)
        val out = ByteArrayOutputStream()
        PrintVisitor.print(out, ParseVisitor.toPolish(tokenizer.tokenize()))
        assertEquals("2 2 228 * + 1337 + 100 10 - 2 / -", out.toString().trim())
    }

    @Test
    fun `calc visitor test`() {
        val expected = (42 * 21) - 123 + 12 / 123 * (12 / 3 * 5)
        val input = "(42 * 21) - 123 + 12 / 123 * (12 / 3 * 5)".byteInputStream()
        val tokenizer = Tokenizer(input)
        val result = CalcVisitor.calculate(ParseVisitor.toPolish(tokenizer.tokenize()))
        assertEquals(expected, result)
    }
}