// You can experiment here, it won’t be checked

fun main(args: Array<String>) {
    // put your code here
    val lower = readln().toInt()
    val upper = readln().toInt()
    val stepSize = readln().toInt()
    var count = 0
    for (i in lower..upper step stepSize) {
        count++
    }
    println(count)
}
