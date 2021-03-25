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
}

//fun sum(intList: List<Int>): Int = when (intList) {
//    List.Nil -> 0
//    is List.Cons -> intList.head + sum(intList.tail)
//}

sealed class List<out A> {
    abstract fun isEmpty(): Boolean
    fun cons(newItem: @UnsafeVariance A): List<A> = Cons(newItem, this)

    fun setHead(newHead: @UnsafeVariance A): List<A> = when (this) {
        is Nil -> throw IllegalArgumentException()
        is Cons -> tail.cons(newHead)
    }

    fun drop(n: Int): List<A> = drop(this, n)
    fun dropWhile(p: (A) -> Boolean): List<A> = dropWhile(this, p)
    fun reverse(): List<A> = reverse(invoke(), this)
    fun init(): List<A> = this.reverse().drop(1).reverse()


    private object Nil : List<Nothing>() {
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
    }
}
