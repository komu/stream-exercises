import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Nested
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class StreamTest {

    @Test
    fun `empty stream is empty`() {
        assertTrue(emptyStream<Int>().isEmpty())
    }

    @Test
    fun `empty stream has no head`() {
        assertNull(emptyStream<Int>().headOption())
    }

    @Test
    fun `empty stream converts to an empty list`() {
        assertEquals(emptyList(), emptyStream<Int>().toList())
    }

    @Test
    fun `cons builds a non-empty stream with the given head`() {
        val s = cons(1) { emptyStream() }
        assertFalse(s.isEmpty())
        assertEquals(1, s.headOption())
    }

    @Test
    fun `cons does not evaluate the tail eagerly`() {
        var tailEvaluated = false
        cons(1) {
            tailEvaluated = true
            emptyStream()
        }
        assertFalse(tailEvaluated, "Tail should not be evaluated upon stream construction")
    }

    @Test
    fun `cons does not evaluate the tail when only the head is read`() {
        var tailEvaluated = false
        val s = cons(1) {
            tailEvaluated = true
            emptyStream()
        }
        s.headOption()
        assertFalse(tailEvaluated, "Tail should not be evaluated by headOption")
    }

    @Test
    fun `cons evaluates the tail once tail() is called`() {
        var tailEvaluated = false
        val s = cons(1) {
            tailEvaluated = true
            emptyStream()
        }
        val tail = s.tail()
        assertTrue(tailEvaluated, "Tail should be evaluated when tail() is called")
        assertTrue(tail.isEmpty())
    }

    @Test
    fun `streamOf with no arguments produces an empty stream`() {
        val empty = streamOf<Int>()
        assertTrue(empty.isEmpty())
        assertEquals(emptyList(), empty.toList())
    }

    @Test
    fun `streamOf with a single argument produces a single-element stream`() {
        val single = streamOf("a")
        assertFalse(single.isEmpty())
        assertEquals("a", single.headOption())
        assertEquals(listOf("a"), single.toList())
    }

    @Test
    fun `streamOf preserves the order of its arguments`() {
        assertEquals(listOf(1, 2, 3), streamOf(1, 2, 3).toList())
    }

    @Test
    fun `isEmpty is true for empty streams and false for non-empty streams`() {
        assertTrue(emptyStream<Int>().isEmpty())
        assertTrue(streamOf<String>().isEmpty())
        assertFalse(streamOf(1).isEmpty())
        assertFalse(cons(1) { emptyStream() }.isEmpty())
    }

    @Test
    fun `headOption is null for an empty stream`() {
        assertNull(emptyStream<Int>().headOption())
    }

    @Test
    fun `headOption returns the first element of a non-empty stream`() {
        assertEquals(10, streamOf(10, 20, 30).headOption())
    }

    @Test
    fun `headOption does not evaluate the tail`() {
        var evaluatedSecond = false
        val s = cons(1) {
            evaluatedSecond = true
            cons(2) { emptyStream() }
        }
        assertEquals(1, s.headOption())
        assertFalse(evaluatedSecond, "headOption should not evaluate the tail")
    }

    @Test
    fun `tail of an empty stream is empty`() {
        assertTrue(emptyStream<Int>().tail().isEmpty())
    }

    @Test
    fun `tail drops the first element of the stream`() {
        val s = streamOf(1, 2, 3)
        assertEquals(listOf(2, 3), s.tail().toList())
        assertEquals(listOf(3), s.tail().tail().toList())
        assertTrue(s.tail().tail().tail().isEmpty())
    }

    @Test
    fun `toList converts an empty stream to an empty list`() {
        assertEquals(emptyList(), emptyStream<Int>().toList())
    }

    @Test
    fun `toList converts a stream to a list preserving order`() {
        assertEquals(listOf(1), streamOf(1).toList())
        assertEquals(listOf(1, 2, 3, 4, 5), streamOf(1, 2, 3, 4, 5).toList())
    }

    @Test
    fun `take returns the first n elements`() {
        val s = streamOf(1, 2, 3, 4, 5)
        assertEquals(listOf(1, 2, 3), s.take(3).toList())
    }

    @Test
    fun `take returns the whole stream when n exceeds its length`() {
        assertEquals(listOf(1, 2, 3, 4, 5), streamOf(1, 2, 3, 4, 5).take(10).toList())
    }

    @Test
    fun `take with zero or negative n returns an empty stream`() {
        val s = streamOf(1, 2, 3, 4, 5)
        assertEquals(emptyList(), s.take(0).toList())
        assertEquals(emptyList(), s.take(-1).toList())
    }

    @Test
    fun `take on an empty stream is empty`() {
        assertEquals(emptyList(), emptyStream<Int>().take(3).toList())
    }

    @Test
    fun `take works on an infinite stream`() {
        assertEquals(listOf(1, 2, 3), from(1).take(3).toList())
    }

    @Test
    fun `drop removes the first n elements`() {
        val s = streamOf(1, 2, 3, 4, 5)
        assertEquals(listOf(3, 4, 5), s.drop(2).toList())
    }

    @Test
    fun `drop returns an empty stream when n reaches or exceeds its length`() {
        val s = streamOf(1, 2, 3, 4, 5)
        assertEquals(emptyList(), s.drop(5).toList())
        assertEquals(emptyList(), s.drop(10).toList())
    }

    @Test
    fun `drop with zero or negative n returns the whole stream`() {
        val s = streamOf(1, 2, 3, 4, 5)
        assertEquals(listOf(1, 2, 3, 4, 5), s.drop(0).toList())
        assertEquals(listOf(1, 2, 3, 4, 5), s.drop(-1).toList())
    }

    @Test
    fun `drop on an empty stream is empty`() {
        assertEquals(emptyList(), emptyStream<Int>().drop(3).toList())
    }

    @Test
    fun `drop works on an infinite stream`() {
        assertEquals(listOf(6, 7, 8), from(1).drop(5).take(3).toList())
    }

    @Test
    fun `takeWhile returns the leading elements matching the predicate`() {
        val s = streamOf(1, 2, 3, 4, 5, 2, 1)
        assertEquals(listOf(1, 2), s.takeWhile { it < 3 }.toList())
    }

    @Test
    fun `takeWhile returns an empty stream when the first element does not match`() {
        val s = streamOf(1, 2, 3, 4, 5, 2, 1)
        assertEquals(emptyList(), s.takeWhile { it > 10 }.toList())
    }

    @Test
    fun `takeWhile on an empty stream is empty`() {
        assertEquals(emptyList(), emptyStream<Int>().takeWhile { true }.toList())
    }

    @Test
    fun `takeWhile works on an infinite stream`() {
        assertEquals(listOf(1, 2, 3, 4), from(1).takeWhile { it < 5 }.toList())
    }

    @Test
    fun `exists is true when some element matches the predicate`() {
        val s = streamOf(1, 2, 3, 4, 5)
        assertTrue(s.exists { it == 3 })
        assertTrue(s.exists { it % 2 == 0 })
    }

    @Test
    fun `exists is false when no element matches the predicate`() {
        assertFalse(streamOf(1, 2, 3, 4, 5).exists { it > 10 })
    }

    @Test
    fun `exists on an empty stream is false`() {
        assertFalse(emptyStream<Int>().exists { true })
    }

    @Test
    fun `exists short-circuits on an infinite stream`() {
        assertTrue(from(1).exists { it == 5 })
    }

    @Test
    fun `forAll on an empty stream is vacuously true`() {
        assertTrue(emptyStream<Int>().forAll { false })
    }

    @Test
    fun `forAll is true when every element matches the predicate`() {
        assertTrue(streamOf(2, 4, 6, 8).forAll { it % 2 == 0 })
    }

    @Test
    fun `forAll is false when some element does not match the predicate`() {
        assertFalse(streamOf(2, 4, 5, 8).forAll { it % 2 == 0 })
    }

    @Test
    fun `forAll short-circuits on an infinite stream`() {
        assertFalse(from(1).forAll { it < 5 })
    }

    @Test
    fun `find returns the first matching element`() {
        val s = streamOf(1, 2, 3, 4, 5)
        assertEquals(2, s.find { it % 2 == 0 })
        assertEquals(4, s.find { it > 3 })
    }

    @Test
    fun `find returns null when no element matches`() {
        assertNull(streamOf(1, 2, 3, 4, 5).find { it > 10 })
    }

    @Test
    fun `find on an empty stream returns null`() {
        assertNull(emptyStream<Int>().find { true })
    }

    @Test
    fun `find short-circuits on an infinite stream`() {
        assertEquals(10, from(1).find { it == 10 })
    }

    @Test
    fun `map on an empty stream is empty`() {
        assertEquals(emptyList(), emptyStream<Int>().map { it * 2 }.toList())
    }

    @Test
    fun `map transforms every element`() {
        assertEquals(listOf("1", "2", "3"), streamOf(1, 2, 3).map { it.toString() }.toList())
    }

    @Test
    fun `map works on an infinite stream`() {
        assertEquals(listOf(2, 4, 6), from(1).map { it * 2 }.take(3).toList())
    }

    @Test
    fun `filter on an empty stream is empty`() {
        assertEquals(emptyList(), emptyStream<Int>().filter { it % 2 == 0 }.toList())
    }

    @Test
    fun `filter keeps only the elements matching the predicate`() {
        assertEquals(listOf(2, 4), streamOf(1, 2, 3, 4, 5).filter { it % 2 == 0 }.toList())
    }

    @Test
    fun `filter works on an infinite stream`() {
        assertEquals(listOf(2, 4, 6), from(1).filter { it % 2 == 0 }.take(3).toList())
    }

    @Test
    fun `append concatenates two streams`() {
        val s1 = streamOf(1, 2)
        val s2 = streamOf(3, 4)
        assertEquals(listOf(1, 2, 3, 4), s1.append { s2 }.toList())
    }

    @Test
    fun `append with an empty argument returns the receiver unchanged`() {
        assertEquals(listOf(1, 2), streamOf(1, 2).append { emptyStream() }.toList())
    }

    @Test
    fun `append to an empty stream returns the argument unchanged`() {
        assertEquals(listOf(3, 4), emptyStream<Int>().append { streamOf(3, 4) }.toList())
    }

    @Test
    fun `append of two empty streams is empty`() {
        assertEquals(emptyList(), emptyStream<Int>().append { emptyStream() }.toList())
    }

    @Test
    fun `append does not evaluate its argument unless the receiver is exhausted`() {
        var secondEvaluated = false
        val appended = streamOf(1, 2).append {
            secondEvaluated = true
            streamOf(3, 4)
        }
        assertEquals(listOf(1), appended.take(1).toList())
        assertFalse(secondEvaluated, "Appended stream should not be evaluated when not accessed")
    }

    @Test
    fun `flatMap on an empty stream is empty`() {
        assertEquals(emptyList(), emptyStream<Int>().flatMap { streamOf(it, it) }.toList())
    }

    @Test
    fun `flatMap concatenates the streams produced for each element`() {
        assertEquals(
            listOf(1, 10, 2, 20, 3, 30),
            streamOf(1, 2, 3).flatMap { streamOf(it, it * 10) }.toList()
        )
    }

    @Test
    fun `flatMap works on an infinite stream`() {
        assertEquals(
            listOf(1, 1, 2, 2, 3, 3),
            from(1).flatMap { streamOf(it, it) }.take(6).toList()
        )
    }

    @Test
    fun `foldRight combines elements from right to left`() {
        val sum = streamOf(1, 2, 3, 4).foldRight({ 0 }) { x, acc -> x + acc() }
        assertEquals(10, sum)
    }

    @Test
    fun `foldRight on an empty stream returns the default value`() {
        val defaultVal = emptyStream<Int>().foldRight({ 42 }) { x, acc -> x + acc() }
        assertEquals(42, defaultVal)
    }

    @Test
    fun `foldRight short-circuits when the accumulator is not forced`() {
        var elementsEvaluated = 0
        val found = streamOf(1, 2, 3, 4).foldRight({ false }) { x, acc ->
            elementsEvaluated++
            x == 2 || acc()
        }
        assertTrue(found)
        assertEquals(2, elementsEvaluated, "foldRight should short-circuit when accumulator is not forced")
    }

    @Test
    fun `unfold generates elements until the function returns null`() {
        val finite = unfold(1) { s -> if (s <= 5) Pair(s, s + 1) else null }
        assertEquals(listOf(1, 2, 3, 4, 5), finite.toList())
    }

    @Test
    fun `unfold returning null immediately produces an empty stream`() {
        val empty = unfold<Int, Int>(1) { null }
        assertEquals(emptyList<Int>(), empty.toList())
    }

    @Test
    fun `unfold can generate an infinite stream`() {
        val powersOfTwo = unfold(1) { s -> Pair(s, s * 2) }
        assertEquals(listOf(1, 2, 4, 8, 16), powersOfTwo.take(5).toList())
    }

    @Test
    fun `constant repeats the same value forever`() {
        assertEquals(listOf("a", "a", "a", "a"), constant("a").take(4).toList())
        assertEquals(listOf(42, 42), constant(42).take(2).toList())
    }

    @Test
    fun `constant taken zero times is empty`() {
        assertEquals(emptyList(), constant(1).take(0).toList())
    }

    @Test
    fun `from produces consecutive integers starting at n`() {
        assertEquals(listOf(1, 2, 3, 4, 5), from(1).take(5).toList())
        assertEquals(listOf(10, 11, 12), from(10).take(3).toList())
    }

    @Test
    fun `from supports negative starting values`() {
        assertEquals(listOf(-2, -1, 0, 1), from(-2).take(4).toList())
    }

    @Test
    fun `fibs produces the Fibonacci sequence starting at 1`() {
        assertEquals(
            listOf(1L, 1L, 2L, 3L, 5L, 8L, 13L, 21L, 34L, 55L),
            fibs().take(10).toList()
        )
    }

    @Test
    fun `fibs taken zero, one or two times returns the corresponding prefix`() {
        assertEquals(emptyList(), fibs().take(0).toList())
        assertEquals(listOf(1L), fibs().take(1).toList())
        assertEquals(listOf(1L, 1L), fibs().take(2).toList())
    }

    /**
     * These tests do not check the documented contract of [Stream] but rather characteristics that
     * follow from *how* it happens to be implemented, such as how finely-grained the laziness is, or
     * whether operations are stack-safe for very large streams. A correct implementation of the
     * exercise is not required to satisfy these, so the whole class is disabled by default - remove
     * the [Disabled] annotation to explore how the reference implementation behaves.
     */
    @Nested
    @Disabled("Optional: describes implementation characteristics, not required behaviour")
    inner class ImplementationCharacteristics {

        @Test
        fun `map only applies its function to elements that are actually consumed`() {
            var count = 0
            val mapped = streamOf(1, 2, 3, 4, 5).map {
                count++
                it * 2
            }
            assertEquals(0, count, "map should be lazily evaluated")
            assertEquals(listOf(2, 4), mapped.take(2).toList())
            assertEquals(2, count, "only taken elements should be mapped")
        }

        @Test
        fun `filter eagerly scans up to the next matching element, even before the stream is consumed`() {
            var count = 0
            val filtered = streamOf(1, 2, 3, 4, 5, 6).filter {
                count++
                it % 2 == 0
            }
            assertEquals(2, count, "filter must scan to the first match just to know the stream isn't empty")
            assertEquals(listOf(2, 4), filtered.take(2).toList())
            // take(2) also has to determine whether a third element exists before it can discard it,
            // which forces filter to scan one match further than what was actually requested.
            assertEquals(6, count, "filter scans past the requested elements to resolve take's boundary check")
        }

        @Test
        fun `toList does not overflow the stack for a stream with a million elements`() {
            val n = 1_000_000
            assertEquals(n, from(1).take(n).toList().size)
        }

        @Test
        fun `map does not overflow the stack when converting a million-element stream to a list`() {
            val n = 1_000_000
            assertEquals(n, from(1).take(n).map { it * 2 }.toList().size)
        }

        @Test
        fun `filter does not overflow the stack when converting a million-element stream to a list`() {
            val n = 1_000_000
            assertEquals(n / 2, from(1).take(n).filter { it % 2 == 0 }.toList().size)
        }

        @Test
        fun `append does not overflow the stack when converting a million-element stream to a list`() {
            val n = 1_000_000
            assertEquals(n, from(1).take(n).append { emptyStream() }.toList().size)
        }

        @Test
        fun `drop is tail-recursive, so it does not overflow the stack for a huge count`() {
            val n = 2_000_000
            assertEquals(n + 1, from(1).drop(n).headOption())
        }
    }
}
