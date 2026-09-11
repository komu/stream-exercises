class Stream<out T>

fun <T> emptyStream(): Stream<T> = TODO()
fun <T> cons(head: T, tail: () -> Stream<T>): Stream<T> = TODO()
fun <T> streamOf(vararg xs: T): Stream<T> = TODO()

fun <T> Stream<T>.isEmpty(): Boolean = TODO()
fun <T> Stream<T>.headOption(): T? = TODO()
fun <T> Stream<T>.tail(): Stream<T> = TODO()

fun <T> Stream<T>.toList(): List<T> = TODO()
fun <T> Stream<T>.take(n: Int): Stream<T> = TODO()
fun <T> Stream<T>.drop(n: Int): Stream<T> = TODO()
fun <T> Stream<T>.takeWhile(p: (T) -> Boolean): Stream<T> = TODO()

fun <T> Stream<T>.exists(p: (T) -> Boolean): Boolean = TODO()
fun <T> Stream<T>.forAll(p: (T) -> Boolean): Boolean = TODO()
fun <T> Stream<T>.find(p: (T) -> Boolean): T? = TODO()

fun <T, B> Stream<T>.map(f: (T) -> B): Stream<B> = TODO()
fun <T> Stream<T>.filter(p: (T) -> Boolean): Stream<T> = TODO()
fun <T> Stream<T>.append(other: () -> Stream<T>): Stream<T> = TODO()
fun <T, B> Stream<T>.flatMap(f: (T) -> Stream<B>): Stream<B> = TODO()

fun <T, B> Stream<T>.foldRight(z: () -> B, f: (T, () -> B) -> B): B = TODO()
fun <T, S> unfold(z: S, f: (S) -> Pair<T, S>?): Stream<T> = TODO()

fun <T> constant(a: T): Stream<T> = TODO()
fun from(n: Int): Stream<Int> = TODO()
fun fibs(): Stream<Long> = TODO()
