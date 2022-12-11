package state

import tokenizer.LeftBrace
import tokenizer.Operation
import tokenizer.RightBrace
import tokenizer.Token
import utils.TokenUtils

sealed interface State {
    fun next(char: Char?): Pair<State, Token?>
}

object Start : State {
    override fun next(char: Char?): Pair<State, Token?> =
        when {
            char == null -> Pair(Error, null)
            char == Char.MAX_VALUE -> Pair(End, null)
            TokenUtils.isDigit(char) -> Pair(Number(char), null)
            TokenUtils.isOperand(char) -> Pair(Start, Operation.create(char))
            char == '(' -> Pair(Start, LeftBrace)
            char == ')' -> Pair(Start, RightBrace)
            else -> Pair(Error, null)
        }
}

class Number(char: Char) : State {
    private val tokenBuilder: StringBuilder = StringBuilder("$char")

    override fun next(char: Char?): Pair<State, Token?> =
        when {
            TokenUtils.isDigit(char) -> {
                tokenBuilder.append(char)
                Pair(this, null)
            }
            else -> Pair(Start, tokenizer.Number(tokenBuilder.toString().toInt()))
        }
}

object End : State {
    override fun next(char: Char?): Pair<State, Token?> = Pair(End, null)
}

object Error : State {
    override fun next(char: Char?): Pair<State, Token?> = Pair(Error, null)
}