import List.Companion.foldLeft
import org.junit.jupiter.api.Test

/**
 * @author Snow
 */
class Chapter5 {
    @Test
    fun `5-1`() {
        println(List.Cons(1, List(1, 2)))
    }

    @Test
    fun `5-3`() {
        println(List.Cons(1, List(2, 3, 4, 5)).drop(2))
    }

    @Test
    fun `5-5`() {
        // 리스트의 마지막 원소를 제거하는 함수를 작성하라
        // fun init(): List<A>
        println(List.Cons(1, List(2, 3, 4, 5)).init())
    }

    @Test
    fun `228p`() {
        println(foldRight(List(1, 2, 3), List()) { x ->
            { acc: List<Int> -> acc.cons(x) }
        })
    }

    @Test
    fun `5-8`() {
        println(List(1, 2, 3).length())
    }

    @Test
    fun `5-11`() {
        // reverse():List<A>를 작성하라
        println(List(1, 2, 3).reverse())
    }

    @Test
    fun `5-14`() {
        println(List(1, 2, 3).concat(List(4, 5, 6)))
    }

    @Test
    fun `5-15`() {
        // List<Int> -> 3배곱
        println(times3(List(1, 2, 3)))
    }

    @Test
    fun `5-16`() {
        // List<Double> -> List<String>
        println(doubleToString(List(1.1, 2.2, 3.3)))
    }

    @Test
    fun `5-18`() {
        // map 함수 작성
        println(List(1, 2, 3).map { it * 3 })
    }

    @Test
    fun `5-19`() {
        // filter 작성
        println(List(1, 2, 3).filter { it % 2 == 0 })
    }

    @Test
    fun `5-20`() {
        // flatMap 구현

    }
}

fun times3(intList: List<Int>): List<Int> =
    intList.reverse().foldLeft(List()) { acc -> { a -> List.Cons(a * 3, acc) } }

fun doubleToString(doubleList: List<Double>): List<String> =
    doubleList.reverse()
        .foldLeft(List()) { acc -> { a -> List.Cons("$a", acc) } }

fun sum(intList: List<Int>): Int =
    foldLeft(intList, 0) { a -> { it + a } }

fun product(doubleList: List<Double>): Double =
    foldLeft(doubleList, 1.0) { a -> { a * it } }

fun <A> operation(
    list: List<A>,
    identity: A,
    operator: (A) -> (A) -> A
): A = when (list) {
    List.Nil -> identity
    is List.Cons -> operator(list.head)(
        operation(
            list.tail,
            identity,
            operator
        )
    )
}

fun <A, B> foldRight(
    list: List<A>,
    identity: B,
    f: (A) -> (B) -> B
): B = when (list) {
    List.Nil -> identity
    is List.Cons -> f(list.head)(foldRight(list.tail, identity, f))
}

sealed class List<out A> {
    abstract fun isEmpty(): Boolean
    fun cons(newItem: @UnsafeVariance A): List<A> = Cons(newItem, this)

    fun setHead(newHead: @UnsafeVariance A): List<A> = when (this) {
        is Nil -> throw IllegalArgumentException()
        is Cons -> tail.cons(newHead)
    }

    fun drop(n: Int): List<A> = drop(this, n)
    fun dropWhile(p: (A) -> Boolean): List<A> = dropWhile(this, p)
    fun init(): List<A> = this.reverse().drop(1).reverse()
    fun concat(list: List<@UnsafeVariance A>): List<A> = concat(this, list)

    fun <B> coFoldRight(identify: B, f: (A) -> (B) -> B): B =
        coFoldRight(this.reverse(), identify, f)

    fun <B> foldLeft(identify: B, f: (B) -> (A) -> B): B =
        foldLeft(this, identify, f)

    fun length(): Int = foldLeft(0) { acc -> { acc + 1 } }
    fun reverse(): List<A> =
        foldLeft(invoke()) { acc -> { acc.cons(it) } }

    fun <B> map(f: (A) -> B): List<B> = map(this, f)
    fun filter(p: (A) -> Boolean): List<A> = filter(this, p)
    fun <B> flatMap(f: (A) -> List<B>): List<B> = flatten(map(f))
    fun fliterByFlatMap(p: (A) -> Boolean): List<A> =
        flatMap { if (p(it)) List(it) else Nil }


    object Nil : List<Nothing>() {
        override fun isEmpty() = true
        override fun toString(): String = "[NIL]"
    }

    class Cons<A>(
        internal val head: A,
        internal val tail: List<A>,
    ) : List<A>() {
        override fun isEmpty(): Boolean = false
        override fun toString(): String = "[${toString("", this)}NIL]"

        private tailrec fun toString(acc: String, list: List<A>): String =
            when (list) {
                is Nil -> acc
                is Cons -> toString("$acc+${list.head}, ", list.tail)
            }

    }

    companion object {
        operator
        fun <A> invoke(vararg az: A): List<A> = az.foldRight(Nil as List<A>)
        { a: A, list: List<A> -> Cons(a, list) }

        tailrec fun <A> drop(list: List<A>, n: Int): List<A> = when (list) {
            Nil -> list
            is Cons -> if (n <= 0) list else drop(list.tail, n - 1)
        }

        tailrec fun <A> dropWhile(list: List<A>, p: (A) -> Boolean): List<A> =
            when (list) {
                Nil -> list
                is Cons -> if (!p(list.head)) list else dropWhile(list.tail, p)
            }

        tailrec fun <A> reverse(acc: List<A>, list: List<A>): List<A> =
            when (list) {
                Nil -> acc
                is Cons -> reverse(acc.cons(list.head), list.tail)
            }

        fun <A> concat(list1: List<A>, list2: List<A>): List<A> = when (list1) {
            Nil -> list2
            is Cons -> foldRight(list1, list2) { a ->
                { acc ->
                    acc.cons(a)
                }
            }
        }

        tailrec fun <A, B> foldLeft(
            list: List<A>,
            acc: B,
            f: (B) -> (A) -> B
        ): B = when (list) {
            Nil -> acc
            is Cons -> foldLeft(list.tail, f(acc)(list.head), f)
        }

        tailrec fun <A, B> coFoldRight(
            list: List<A>,
            acc: B,
            f: (A) -> (B) -> B
        ): B = when (list) {
            Nil -> acc
            is Cons -> coFoldRight(list.tail, f(list.head)(acc), f)
        }

        fun <A, B> foldRight(
            list: List<A>,
            identity: B,
            f: (A) -> (B) -> B
        ): B = when (list) {
            Nil -> identity
            is Cons -> f(list.head)(foldRight(list.tail, identity, f))
        }

        fun <A> flatten(llist: List<List<A>>): List<A> = foldLeft(
            llist,
            invoke()
        ) { acc: List<A> -> acc::concat }

        fun <A, B> map(list: List<A>, f: (A) -> B): List<B> =
            list.reverse()
                .foldLeft(invoke()) { acc -> { a -> Cons(f(a), acc) } }

        fun <A> filter(list: List<A>, p: (A) -> Boolean): List<A> =
            list.reverse().foldLeft(invoke()) { acc ->
                { a ->
                    if (p(a)) Cons(
                        a,
                        acc
                    ) else acc
                }
            }

        fun <A, B> flatMap(list: List<A>, f: (A) -> List<B>): List<B> =
            flatten(map(list, f))
    }
}
