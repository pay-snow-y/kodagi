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
}

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

    fun <B> coFoldRight(identify: B, f: (A) -> (B) -> B): B =
        coFoldRight(this, identify, f)

    fun <B> foldLeft(identify: B, f: (B) -> (A) -> B): B =
        foldLeft(this, identify, f)

    fun length(): Int = foldLeft(0) { acc -> { acc + 1 } }
    fun reverse(): List<A> =
        foldLeft(invoke()) { acc -> { acc.cons(it) } }


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
    }
}
