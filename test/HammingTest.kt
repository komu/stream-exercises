import org.junit.jupiter.api.Test
import java.math.BigInteger
import kotlin.test.assertEquals

class HammingTest {

    private val one = 1.toBigInteger()
    private val two = 2.toBigInteger()
    private val three = 3.toBigInteger()
    private val five = 5.toBigInteger()

    val hamming: Stream<BigInteger> =
        cons(one) { (two * hamming) union (three * hamming) union (five * hamming) }

    private operator fun BigInteger.times(s: Stream<BigInteger>) = s.map { this * it }

    infix fun <T : Comparable<T>> Stream<T>.union(b: Stream<T>): Stream<T> {
        val a = this
        val x = a.headOption()!!
        val y = b.headOption()!!
        return when {
            x < y -> cons(x) { a.tail().union(b) }
            x > y -> cons(y) { a.union(b.tail()) }
            else -> cons(x) { a.tail().union(b.tail()) }
        }
    }

    @Test
    fun `hamming 20`() {
        assertEquals(
            listOf(1, 2, 3, 4, 5, 6, 8, 9, 10, 12, 15, 16, 18, 20, 24, 25, 27, 30, 32, 36).map { it.toBigInteger() },
            hamming.take(20).toList()
        )
    }

    @Test
    fun `hamming 999999`() {
        assertEquals(
            BigInteger("519312780448388736089589843750000000000000000000000000000000000000000000000000000000"),
            hamming.drop(999999).headOption()
        )
    }
}
