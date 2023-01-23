fun main() {
    // write your code here
    println(when(readln()) {
        "1", "3", "4" -> "No!"
        "2" -> "Yes!"
        else -> "Unknown number"
    })
}