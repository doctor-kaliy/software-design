package visitor

import tokenizer.*
import tokenizer.Number

interface TokenVisitor {
    fun visit(token: Number)
    fun visit(token: Brace)
    fun visit(token: Operation)
}
