package visitor

import tokenizer.*
import tokenizer.Number

class ParseVisitor: TokenVisitor {
    private val polish: MutableList<Token> = mutableListOf()
    private val stack: ArrayDeque<Token> = ArrayDeque()

    override fun visit(token: Number) {
        polish.add(token)
    }

    override fun visit(token: Brace) {
        if (token is LeftBrace) {
            stack.addLast(token)
            return
        }

        while (stack.isNotEmpty() && stack.last() !is LeftBrace) {
            polish.add(stack.removeLast())
        }
        check(stack.isNotEmpty()) { "mismatched braces" }
        stack.removeLast()
    }

    override fun visit(token: Operation) {
        while (stack.isNotEmpty()) {
            val last = stack.last()

            if (last !is Operation || token.priority > last.priority) {
                break
            }

            polish.add(stack.removeLast())
        }
        stack.addLast(token)
    }
    companion object {
        fun toPolish(tokens: List<Token>): List<Token> {
            val visitor = ParseVisitor()
            tokens.forEach { it.accept(visitor) }

            while (visitor.stack.isNotEmpty()) {
                val token: Token = visitor.stack.removeLast()
                check(token is Operation) { "mismatched braces" }
                visitor.polish.add(token)
            }
            return visitor.polish
        }
    }
}