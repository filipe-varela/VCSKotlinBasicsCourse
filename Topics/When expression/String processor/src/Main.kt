fun main() {
    // write your code here
    val first = readln()
    val op = readln()
    val second = readln()

    println(when(op) {
        "equals" -> first == second
        "plus" -> first + second
        "endsWith" -> first.endsWith(second)
        else -> "Unknown operation"
    })
}