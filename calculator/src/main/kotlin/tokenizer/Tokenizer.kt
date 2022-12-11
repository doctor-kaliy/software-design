package tokenizer

import state.State
import utils.TokenUtils
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.Character.isDigit
import java.lang.Character.isWhitespace
import java.text.ParseException


class Tokenizer(stream: InputStream) {
    private val input: InputStreamReader = InputStreamReader(stream)

    private var index = -1
    var curChar: Char = EOF
        private set

    fun tokenize(): List<Token> {
        val tokens: MutableList<Token> = mutableListOf()
        var state: State = state.Start

        nextChar()
        skipWhitespaces()
        var char = nextChar()
        skipWhitespaces()

        while (state !is state.Error && state !is state.End) {
            val (nextState, token) = state.next(char)
            if (token != null) {
                tokens.add(token)
                skipWhitespaces()
                if (isWhitespace(char)) {
                    char = nextChar()
                }
            }
            if (state !is state.Number || nextState !is state.Start) {
                char = nextChar()
            }
            if (nextState is state.Error) {
                throw Exception("Wrong character $char at $index")
            }
            state = nextState
        }
        return tokens
    }

    private fun nextChar(): Char {
        index++
        return curChar.also { curChar = input.read().toChar() }
    }

    private fun skipWhitespaces() {
        while (curChar != EOF && isWhitespace(curChar)) {
            nextChar()
        }
    }

    companion object {
        private const val EOF = Char.MAX_VALUE
    }
}
