package visitor

import tokenizer.Brace
import tokenizer.Operation
import tokenizer.Token
import tokenizer.Number
import java.io.OutputStream
import java.io.PrintStream

class PrintVisitor(stream: OutputStream) : TokenVisitor {
    private val out: PrintStream = PrintStream(stream)

    override fun visit(token: Number) {
        out.print(token.toString())
    }

    override fun visit(token: Brace) {
        out.print(token.toString())
    }

    override fun visit(token: Operation) {
        out.print(token.toString())
    }

    companion object {
        fun print(stream: OutputStream, tokens: List<Token>) {
            val visitor = PrintVisitor(stream)
            tokens.forEach {
                it.accept(visitor)
                visitor.out.print(' ')
            }
            visitor.out.println()
        }
    }
}