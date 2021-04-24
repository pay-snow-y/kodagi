import org.junit.jupiter.api.Test

/**
 * @author Snow
 */
class Chapter6 {
    @Test
    fun `6-1`() {
//        println(
//            "max data is ${
//                max(
//                    listOf(
//                        3,
//                        5,
//                        7,
//                        2,
//                        1
//                    )
//                ).getOrElse { getDefault() }
//            }"
//        )
        println("max data is ${max(listOf()).getOrElse { getDefault() }}")
    }

    @Test
    fun `6-3`() {

    }

    @Test
    fun `6-6`() {
        // filter 작성
    }

    @Test
    fun `6-7`() {
        // variance 정의하기
    }

    @Test
    fun `6-8`() {
        // A->B 함수를 인자로 받아서 Option<A>->Option<B>로 가는 함수를 반환하는 LIFt 정의
    }

    @Test
    fun `6-9`() {
        // lift 함수를 예외 발생시에도 동작하도록 만들라
    }

    @Test
    fun `6-10`() {
        // Option<A>, Option<B> 값과 (A)->(B)->C 타입의 커리한 함수 값을 인자로 받아 Option<C>를 반환하는 map2를 만들어라
    }

    @Test
    fun `6-11`() {
        // List<Option<A>> -> Option<List<A>>로 변경해주는 sequence를 작성하라
    }
}

fun <A> sequence(list: List<Option<A>>): Option<List<A>> {
    Option.Some(list.foldLeft(List<A>()) { acc ->
        { opA ->
            when (opA) {
//                Option.None -> return Option.None
                is Option.Some -> acc.cons(opA.value)
                else -> acc.cons(Option.None)
            }
        }
    })
}

fun <A, B, C> map2Book(
    opA: Option<A>,
    opB: Option<B>,
    f: (A) -> (B) -> C
): Option<C> = opA.flatMap { a -> opB.map { b -> f(a)(b) } }

fun <A, B, C, D> map3Book(
    opA: Option<A>,
    opB: Option<B>,
    opC: Option<C>,
    f: (A) -> (B) -> (C) -> D
): Option<D> =
    opA.flatMap { a -> opB.flatMap { b -> opC.map { c -> f(a)(b)(c) } } }

fun <A, B, C> map2(
    opA: Option<A>,
    opB: Option<B>,
    f: (A) -> (B) -> C
): Option<C> {
    return opA.map(f).flatMap { opB.map(it) }
}

fun <A, B, C, D> map3(
    opA: Option<A>,
    opB: Option<B>,
    opC: Option<C>,
    f: (A) -> (B) -> (C) -> D
): Option<D> {
    return opA.map(f).flatMap { opB.map(it) }.flatMap { opC.map(it) }
}

fun <A, B> lift(f: (A) -> B): (Option<A>) -> Option<B> = {
    try {
        it.map(f)
    } catch (e: Exception) {
        Option()
    }
}

val parseWithRadix: (Int) -> (String) -> Int =
    { radix -> { string -> Integer.parseInt(string, radix) } }

val abs: (Double) -> Double = { d -> if (d > 0) d else -d }
val abs0: (Option<Double>) -> Option<Double> = lift(abs)
//val mean: (List<Double>) -> Option<Double> =
//    {
//        when {
//            it.isEmpty() -> Option()
//            else -> Option(it.sum() / it.size)
//        }
//    }

//val variance: (List<Double>) -> Option<Double> = {
//    mean(it).flatMap { m -> mean(it.map { x -> (x - m).pow(2.0) }) }
//}

fun getDefault(): Int = throw RuntimeException()
//fun max(list: List<Int>): Option<Int> = Option(list.maxOrNull())

sealed class Option<out A> {
    abstract fun isEmpty(): Boolean
    fun getOrElse(default: () -> @UnsafeVariance A): A = when (this) {
        is None -> default()
        is Some -> value
    }

    fun <B> map(f: (A) -> B): Option<B> = when (this) {
        is None -> None
        is Some -> Some(f(value))
    }

    /* Option<Option<B>> -> Option<B> */
    fun <B> flatMap(f: (A) -> Option<B>): Option<B> = map(f).getOrElse { None }

    fun orElse(default: () -> Option<@UnsafeVariance A>): Option<A> =
        map { this }.getOrElse(default)

    fun filter(p: (A) -> Boolean): Option<A> =
        flatMap { a -> if (p(a)) Some(a) else None }

    internal object None : Option<Nothing>() {
        override fun isEmpty(): Boolean = true
        override fun toString(): String = "None"
        override fun equals(other: Any?): Boolean = other === None
        override fun hashCode(): Int = 0
    }

    internal data class Some<out A>(internal val value: A) : Option<A>() {
        override fun isEmpty(): Boolean = false
    }

    companion object {
        operator fun <A> invoke(a: A? = null): Option<A> = when (a) {
            null -> None
            else -> Some(a)
        }
    }
}
