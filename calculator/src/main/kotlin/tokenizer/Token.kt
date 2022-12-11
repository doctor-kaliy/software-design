package tokenizer

import visitor.TokenVisitor
import java.lang.Exception

sealed interface Token {
    fun accept(visitor: TokenVisitor)
}

data class Number(val num: Int): Token {
    override fun accept(visitor: TokenVisitor) = visitor.visit(this)
    override fun toString(): String = "$num"
}

sealed interface Brace: Token {
    override fun accept(visitor: TokenVisitor) = visitor.visit(this)
}

object LeftBrace : Brace {
    override fun toString(): String = "("
}

object RightBrace : Brace {
    override fun toString(): String = ")"
}

sealed interface Operation: Token {
    val priority: Int
    override fun accept(visitor: TokenVisitor) = visitor.visit(this)

    companion object {
        fun create(char: Char): Operation =
            when(char) {
                '+' -> Plus
                '-' -> Minus
                '*' -> Multiply
                '/' -> Divide
                else -> throw Exception("non operand char $char")
            }
    }
}

object Plus: Operation {
    override val priority = 0
    override fun toString(): String = "+"
}

object Minus: Operation {
    override val priority = 0
    override fun toString(): String = "-"
}

object Multiply: Operation {
    override val priority = 1
    override fun toString(): String = "*"
}

object Divide: Operation {
    override val priority = 1
    override fun toString(): String = "/"
}