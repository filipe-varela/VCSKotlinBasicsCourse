fun main() {
    val input = readLine()!!
    // write code here
    println(if (input.isNotEmpty()) when(input.first()) {
        'i' -> input.drop(1).toInt() + 1
        's' -> input.drop(1).reversed()
        else -> input
    } else "")
}