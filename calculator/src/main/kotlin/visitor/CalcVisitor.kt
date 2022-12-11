package visitor

import tokenizer.*
import tokenizer.Number

class CalcVisitor : TokenVisitor {
    private val stack: ArrayDeque<Number> = ArrayDeque()

    override fun visit(token: Number) {
        stack.addLast(token)
    }

    override fun visit(token: Brace) {
        throw UnsupportedOperationException("unexpected brace")
    }

    override fun visit(token: Operation) {
        check(stack.size >= 2) { "Incomplete expression" }
        val b = stack.removeLast()
        val a = stack.removeLast()
        val res = when (token) {
            Divide -> {
                check(b.num != 0) { "division by zero" }
                a.num / b.num
            }
            Minus -> a.num - b.num
            Multiply -> a.num * b.num
            Plus -> a.num + b.num
        }
        stack.addLast(Number(res))
    }

    companion object {
        fun calculate(tokens: List<Token>): Int {
            val visitor = CalcVisitor()
            tokens.forEach { it.accept(visitor) }
            check(visitor.stack.size == 1) { "invalid expression" }
            return visitor
                .stack
                .removeLast()
                .num
        }
    }
}