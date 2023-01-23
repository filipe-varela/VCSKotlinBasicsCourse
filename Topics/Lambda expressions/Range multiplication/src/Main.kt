val lambda: (Long, Long) -> Long = { leftBorder: Long, rightBorder: Long ->
    if (leftBorder == rightBorder)
        leftBorder
    else {
        var result: Long = 1
        for (i in leftBorder..rightBorder) {
            result *= i
        }
        result
    }
}