fun main() {
    // write your code here
    val n = readln().toInt()
    val sequence = mutableListOf<Int>()
    val sequencesSize = mutableListOf<Int>()
    var counter = 0
    if (n == 1) println(n)
    else {
        repeat(n) {
            val currentNumber = readln().toInt()
            if (sequence.isEmpty() || sequence.last() <= currentNumber) sequence.add(currentNumber)
            else {
                sequencesSize.add(sequence.size)
                sequence.clear()
                sequence.add(currentNumber)
            }
        }
        if (sequence.isNotEmpty()) sequencesSize.add(sequence.size)
        val maxSequence: Int = sequencesSize.maxOrNull() ?: 0
        println(maxSequence)
    }
}