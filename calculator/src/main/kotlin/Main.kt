import tokenizer.Tokenizer
import visitor.CalcVisitor
import visitor.ParseVisitor
import visitor.PrintVisitor

fun main() {
    val tokens = Tokenizer("2 + 2 * 228 + 1337 - (100 - 10) / 2".byteInputStream()).tokenize()
    PrintVisitor.print(System.`out`, tokens)
    PrintVisitor.print(System.`out`, ParseVisitor.toPolish(tokens))
    println(CalcVisitor.calculate(ParseVisitor.toPolish(tokens)))
}