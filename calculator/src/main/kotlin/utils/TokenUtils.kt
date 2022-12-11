package utils

object TokenUtils {
    fun isDigit(char: Char?): Boolean = char?.let(Character::isDigit) ?: false

    fun isOperand(char: Char?): Boolean =
        when (char) {
            '+', '-', '*', '/' -> true
            else -> false
        }
}