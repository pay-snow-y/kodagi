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
    }
}

sealed class List<A> {
    abstract fun isEmpty(): Boolean
    fun cons(newItem: A): List<A> = Cons(newItem, this)

    fun setHead(newHead: A): List<A> = when (this) {
        is Nil -> throw IllegalArgumentException()
        is Cons -> tail.cons(newHead)
    }

    fun drop(n: Int): List<A> {
        tailrec fun drop_(list: List<A>, nn: Int): List<A> = when {
            nn == 0 -> list
            list is Cons -> drop_(list.tail, nn - 1)
            list is Nil -> list
            else -> throw IllegalArgumentException()
        }

        return drop_(this, n)
    }

    private object Nil : List<Nothing>() {
        override fun isEmpty() = true
        override fun toString(): String = "[NIL]"
    }

    class Cons<A>(
        internal val head: A,
        internal val tail: List<A>,
    ) :
        List<A>() {
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
    }
}
