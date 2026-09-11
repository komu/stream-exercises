import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class StreamTest {

    @Test
    fun testEmptyStream() {
        val s = emptyStream<Int>()
        assertTrue(s.isEmpty())
        assertNull(s.headOption())
        assertEquals(emptyList(), s.toList())
    }

    @Test
    fun testCons() {
        var tailEvaluated = false
        val s = cons(1) {
            tailEvaluated = true
            emptyStream()
        }
        assertFalse(s.isEmpty())
        assertEquals(1, s.headOption())
        assertFalse(tailEvaluated, "Tail should not be evaluated upon stream construction or headOption")

        val tail = s.tail()
        assertTrue(tailEvaluated, "Tail should be evaluated when tail() is called")
        assertTrue(tail.isEmpty())
    }

    @Test
    fun testStreamOf() {
        val empty = streamOf<Int>()
        assertTrue(empty.isEmpty())
        assertEquals(emptyList(), empty.toList())

        val single = streamOf("a")
        assertFalse(single.isEmpty())
        assertEquals("a", single.headOption())
        assertEquals(listOf("a"), single.toList())

        val multiple = streamOf(1, 2, 3)
        assertEquals(listOf(1, 2, 3), multiple.toList())
    }

    @Test
    fun testIsEmpty() {
        assertTrue(emptyStream<Int>().isEmpty())
        assertTrue(streamOf<String>().isEmpty())
        assertFalse(streamOf(1).isEmpty())
        assertFalse(cons(1) { emptyStream() }.isEmpty())
    }

    @Test
    fun testHeadOption() {
        assertNull(emptyStream<Int>().headOption())
        assertEquals(10, streamOf(10, 20, 30).headOption())

        var evaluatedSecond = false
        val s = cons(1) {
            evaluatedSecond = true
            cons(2) { emptyStream() }
        }
        assertEquals(1, s.headOption())
        assertFalse(evaluatedSecond, "headOption should not evaluate the tail")
    }

    @Test
    fun testTail() {
        val s = streamOf(1, 2, 3)
        val tail = s.tail()
        assertEquals(listOf(2, 3), tail.toList())
        assertEquals(listOf(3), tail.tail().toList())
        assertTrue(tail.tail().tail().isEmpty())
    }

    @Test
    fun testToList() {
        assertEquals(emptyList(), emptyStream<Int>().toList())
        assertEquals(listOf(1), streamOf(1).toList())
        assertEquals(listOf(1, 2, 3, 4, 5), streamOf(1, 2, 3, 4, 5).toList())
    }

    @Test
    fun testTake() {
        val s = streamOf(1, 2, 3, 4, 5)
        assertEquals(listOf(1, 2, 3), s.take(3).toList())
        assertEquals(listOf(1, 2, 3, 4, 5), s.take(10).toList())
        assertEquals(emptyList(), s.take(0).toList())
        assertEquals(emptyList(), s.take(-1).toList())
        assertEquals(emptyList(), emptyStream<Int>().take(3).toList())

        // Test laziness with infinite stream
        assertEquals(listOf(1, 2, 3), from(1).take(3).toList())
    }

    @Test
    fun testDrop() {
        val s = streamOf(1, 2, 3, 4, 5)
        assertEquals(listOf(3, 4, 5), s.drop(2).toList())
        assertEquals(emptyList(), s.drop(5).toList())
        assertEquals(emptyList(), s.drop(10).toList())
        assertEquals(listOf(1, 2, 3, 4, 5), s.drop(0).toList())
        assertEquals(listOf(1, 2, 3, 4, 5), s.drop(-1).toList())
        assertEquals(emptyList(), emptyStream<Int>().drop(3).toList())

        // Test laziness with infinite stream
        assertEquals(listOf(6, 7, 8), from(1).drop(5).take(3).toList())
    }

    @Test
    fun testTakeWhile() {
        val s = streamOf(1, 2, 3, 4, 5, 2, 1)
        assertEquals(listOf(1, 2), s.takeWhile { it < 3 }.toList())
        assertEquals(emptyList(), s.takeWhile { it > 10 }.toList())
        assertEquals(emptyList(), emptyStream<Int>().takeWhile { true }.toList())

        // Test laziness with infinite stream
        assertEquals(listOf(1, 2, 3, 4), from(1).takeWhile { it < 5 }.toList())
    }

    @Test
    fun testExists() {
        val s = streamOf(1, 2, 3, 4, 5)
        assertTrue(s.exists { it == 3 })
        assertTrue(s.exists { it % 2 == 0 })
        assertFalse(s.exists { it > 10 })
        assertFalse(emptyStream<Int>().exists { true })

        // Short-circuit check on infinite stream
        assertTrue(from(1).exists { it == 5 })
    }

    @Test
    fun testForAll() {
        assertTrue(emptyStream<Int>().forAll { false }, "forAll on empty stream should return true")
        assertTrue(streamOf(2, 4, 6, 8).forAll { it % 2 == 0 })
        assertFalse(streamOf(2, 4, 5, 8).forAll { it % 2 == 0 })

        // Short-circuit check on infinite stream
        assertFalse(from(1).forAll { it < 5 })
    }

    @Test
    fun testFind() {
        val s = streamOf(1, 2, 3, 4, 5)
        assertEquals(2, s.find { it % 2 == 0 })
        assertEquals(4, s.find { it > 3 })
        assertNull(s.find { it > 10 })
        assertNull(emptyStream<Int>().find { true })

        // Short-circuit check on infinite stream
        assertEquals(10, from(1).find { it == 10 })
    }

    @Test
    fun testMap() {
        assertEquals(emptyList(), emptyStream<Int>().map { it * 2 }.toList())
        assertEquals(listOf("1", "2", "3"), streamOf(1, 2, 3).map { it.toString() }.toList())

        // Laziness check
        var count = 0
        val mapped = streamOf(1, 2, 3, 4, 5).map {
            count++
            it * 2
        }
        assertEquals(0, count, "map should be lazily evaluated")
        assertEquals(listOf(2, 4), mapped.take(2).toList())
        assertEquals(2, count, "only taken elements should be mapped")

        // Infinite stream
        assertEquals(listOf(2, 4, 6), from(1).map { it * 2 }.take(3).toList())
    }

    @Test
    fun testFilter() {
        assertEquals(emptyList(), emptyStream<Int>().filter { it % 2 == 0 }.toList())
        assertEquals(listOf(2, 4), streamOf(1, 2, 3, 4, 5).filter { it % 2 == 0 }.toList())

        // Laziness check
        assertEquals(listOf(2, 4, 6), from(1).filter { it % 2 == 0 }.take(3).toList())
    }

    @Test
    fun testAppend() {
        val s1 = streamOf(1, 2)
        val s2 = streamOf(3, 4)
        assertEquals(listOf(1, 2, 3, 4), s1.append { s2 }.toList())

        assertEquals(listOf(1, 2), s1.append { emptyStream() }.toList())
        assertEquals(listOf(3, 4), emptyStream<Int>().append { s2 }.toList())
        assertEquals(emptyList(), emptyStream<Int>().append { emptyStream() }.toList())

        // Laziness check: second stream should not be evaluated if not reached
        var secondEvaluated = false
        val appended = streamOf(1, 2).append {
            secondEvaluated = true
            streamOf(3, 4)
        }
        assertEquals(listOf(1), appended.take(1).toList())
        assertFalse(secondEvaluated, "Appended stream should not be evaluated when not accessed")
    }

    @Test
    fun testFlatMap() {
        assertEquals(emptyList(), emptyStream<Int>().flatMap { streamOf(it, it) }.toList())
        assertEquals(
            listOf(1, 10, 2, 20, 3, 30),
            streamOf(1, 2, 3).flatMap { streamOf(it, it * 10) }.toList()
        )

        // Laziness check on infinite stream
        assertEquals(
            listOf(1, 1, 2, 2, 3, 3),
            from(1).flatMap { streamOf(it, it) }.take(6).toList()
        )
    }

    @Test
    fun testFoldRight() {
        val s = streamOf(1, 2, 3, 4)
        val sum = s.foldRight({ 0 }) { x, acc -> x + acc() }
        assertEquals(10, sum)

        val empty = emptyStream<Int>()
        val defaultVal = empty.foldRight({ 42 }) { x, acc -> x + acc() }
        assertEquals(42, defaultVal)

        // Laziness / early termination check in foldRight
        var elementsEvaluated = 0
        val found = s.foldRight({ false }) { x, acc ->
            elementsEvaluated++
            x == 2 || acc()
        }
        assertTrue(found)
        assertEquals(2, elementsEvaluated, "foldRight should short-circuit when accumulator is not forced")
    }

    @Test
    fun testUnfold() {
        val finite = unfold(1) { s ->
            if (s <= 5) Pair(s, s + 1) else null
        }
        assertEquals(listOf(1, 2, 3, 4, 5), finite.toList())

        val empty = unfold<Int, Int>(1) { null }
        assertEquals(emptyList<Int>(), empty.toList())

        val powersOfTwo = unfold(1) { s -> Pair(s, s * 2) }
        assertEquals(listOf(1, 2, 4, 8, 16), powersOfTwo.take(5).toList())
    }

    @Test
    fun testConstant() {
        assertEquals(listOf("a", "a", "a", "a"), constant("a").take(4).toList())
        assertEquals(listOf(42, 42), constant(42).take(2).toList())
        assertEquals(emptyList(), constant(1).take(0).toList())
    }

    @Test
    fun testFrom() {
        assertEquals(listOf(1, 2, 3, 4, 5), from(1).take(5).toList())
        assertEquals(listOf(10, 11, 12), from(10).take(3).toList())
        assertEquals(listOf(-2, -1, 0, 1), from(-2).take(4).toList())
    }

    @Test
    fun testFibs() {
        assertEquals(
            listOf(0L, 1L, 1L, 2L, 3L, 5L, 8L, 13L, 21L, 34L),
            fibs().take(10).toList()
        )
        assertEquals(emptyList(), fibs().take(0).toList())
        assertEquals(listOf(0L), fibs().take(1).toList())
        assertEquals(listOf(0L, 1L), fibs().take(2).toList())
    }
}
