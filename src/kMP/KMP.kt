package kMP

fun buildPrefixTable(s: String): Array<Int> {
    val p = Array(s.length) { _ -> 0}
    var k = 0
    for (i in 1 until s.length) {
        if (k > 0 && s[i] != s[k]) k = p[k-1]
        if (s[i] == s[k]) k++
        p[i] = k
    }
    return p
}

fun search(needle: String, haystack: String): List<Int> {
    val p = buildPrefixTable(needle)

    val matches = mutableListOf<Int>()
    var maxPrefixLength = 0
    for (i in haystack.indices) {
        while (maxPrefixLength > 0 && haystack[i] != needle[maxPrefixLength]) maxPrefixLength = p[maxPrefixLength - 1]
        if (haystack[i] == needle[maxPrefixLength]) maxPrefixLength++
        if (maxPrefixLength == needle.length) {
            matches.add(i - needle.length + 1)
            maxPrefixLength = p[maxPrefixLength - 1]
        }
    }

    return matches
}

fun main(args: Array<String>) {
    val needle = args[0]
    val haystack = args[1]
    println("Found $needle in $haystack at positions:")
    println(search(needle, haystack).joinToString())
}