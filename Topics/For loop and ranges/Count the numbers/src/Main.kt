// You can experiment here, it won’t be checked

fun main(args: Array<String>) {
    // put your code here
    val lower = readln().toInt()
    val upper = readln().toInt()
    val n = readln().toInt()
    var count = 0
    for (i in lower..upper) {
        count += if (i % n == 0) 1 else 0
    }
    println(count)
}
