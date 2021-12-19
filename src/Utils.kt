import java.io.File
import java.math.BigInteger
import java.security.MessageDigest

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = File("src", "$name.txt").readLines()
fun readText(name: String) = File("src", "$name.txt").readText()
fun readLineInt(name: String) = readText(name).trim().split(",").map {it.toInt()}
fun readDigitMatrix(name: String) = readInput(name).map { it.toCharArray().map { it.digitToInt() } }

/**
 * Converts string to md5 hash.
 */
fun String.md5(): String = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray())).toString(16)

fun cartesianProduct(c1: List<String>, c2: List<String>): List<Pair<String, String>> {
    return c1.flatMap { lhsElem -> c2.map { rhsElem -> lhsElem to rhsElem } }
}