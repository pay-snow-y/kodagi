import java.io.Serializable

/**
 * @author Snow
 */
class Chapter7 {

}

sealed class Either<out E, out A> {
    abstract fun <B> map(f: (A) -> B): Either<E, B>
    abstract fun <B> flatMap(f: (A) -> Either<@UnsafeVariance E, B>): Either<E, B>
    fun getOrElse(defaultValue: () -> @UnsafeVariance A): A = when (this) {
        is Left -> defaultValue()
        is Right -> this.value
    }

    fun orElse(defaultValue: () -> Either<@UnsafeVariance E, @UnsafeVariance A>): Either<E, A> =
        map { this }.getOrElse(defaultValue)

    internal class Left<out E, out A>(private val value: E) : Either<E, A>() {
        override fun toString(): String = "Left($value)"
        override fun <B> map(f: (A) -> B): Either<E, B> = left(value)
        override fun <B> flatMap(f: (A) -> Either<@UnsafeVariance E, B>): Either<E, B> =
            left(value)
    }

    internal class Right<out E, out A>(internal val value: A) : Either<E, A>() {
        override fun toString(): String = "Right($value)"
        override fun <B> map(f: (A) -> B): Either<E, B> = right(f(value))
        override fun <B> flatMap(f: (A) -> Either<@UnsafeVariance E, B>): Either<E, B> =
            f(value)

    }

    companion object {
        fun <A, B> left(value: A): Either<A, B> = Left(value)
        fun <A, B> right(value: B): Either<A, B> = Right(value)
    }
}

sealed class Result<out A> : Serializable {
    abstract fun mapFailure(message: String): Result<A>
    abstract fun forEach(
        onSuccess: (A) -> Unit = {},
        onFailure: (RuntimeException) -> Unit = {},
        onEmpty: () -> Unit = {}
    )

    fun <B> map(f: (A) -> B): Result<B> = when (this) {
        is Empty -> Empty
        is Failure -> Failure(exception)
        is Success -> Result(f(value))
    }

    fun <B> flatMap(f: (A) -> Result<B>): Result<B> = when (this) {
        is Empty -> Empty
        is Failure -> Failure(exception)
        is Success -> f(value)
    }

    fun getOrElse(defaultValue: @UnsafeVariance A): A = when (this) {
        is Success -> this.value
        else -> defaultValue
    }

    fun getOrElse(defaultValue: () -> @UnsafeVariance A): A = when (this) {
        is Success -> this.value
        else -> defaultValue()
    }

    fun filter(p: (A) -> Boolean): Result<A> =
        filter(p, "Condition not matched")

    fun filter(p: (A) -> Boolean, failureMessage: String): Result<A> =
        flatMap { if (p(it)) this else failure(failureMessage) }

    fun exists(p: (A) -> Boolean): Boolean = map(p).getOrElse(false)

    fun orElse(defaultValue: () -> Result<@UnsafeVariance A>): Result<A> =
        when (this) {
            is Success -> this
            else -> try {
                defaultValue()
            } catch (e: RuntimeException) {
                failure(e)
            } catch (e: Exception) {
                failure(RuntimeException(e))
            }
        }

    internal object Empty : Result<Nothing>() {
        override fun toString(): String = "Empty"
        override fun mapFailure(message: String): Result<Nothing> = this
        override fun forEach(
            onSuccess: (Nothing) -> Unit,
            onFailure: (RuntimeException) -> Unit,
            onEmpty: () -> Unit
        ) = onEmpty()
    }

    internal class Failure<out A>(
        internal val exception: RuntimeException
    ) : Result<A>() {
        override fun toString(): String = "Failure(${exception.message})"
        override fun mapFailure(message: String): Result<A> =
            Failure(RuntimeException(message, exception))

        override fun forEach(
            onSuccess: (A) -> Unit,
            onFailure: (RuntimeException) -> Unit,
            onEmpty: () -> Unit
        ) = onFailure(exception)
    }

    internal class Success<out A>(
        internal val value: A
    ) : Result<A>() {
        override fun toString(): String = "Success(${value})"
        override fun mapFailure(message: String): Result<A> = this
        override fun forEach(
            onSuccess: (A) -> Unit,
            onFailure: (RuntimeException) -> Unit,
            onEmpty: () -> Unit
        ) = onSuccess(value)
    }

    companion object {
        operator fun <A> invoke(a: A? = null): Result<A> = when (a) {
            null -> Failure(NullPointerException())
            else -> Success(a)
        }

        operator fun <A> invoke(): Result<A> = Empty

        operator fun <A> invoke(a: A? = null, message: String): Result<A> =
            when (a) {
                null -> Failure(NullPointerException(message))
                else -> Success(a)
            }

        operator fun <A> invoke(a: A? = null, p: (A) -> Boolean): Result<A> =
            when (a) {
                null -> Failure(NullPointerException())
                else -> when {
                    p(a) -> Success(a)
                    else -> Empty
                }
            }

        operator fun <A> invoke(
            a: A? = null,
            message: String,
            p: (A) -> Boolean
        ): Result<A> = when (a) {
            null -> Failure(NullPointerException(message))
            else -> when {
                p(a) -> Success(a)
                else -> Empty
            }
        }

        fun <A> failure(message: String): Result<A> =
            Failure(IllegalStateException(message))

        fun <A> failure(exception: RuntimeException): Result<A> =
            Failure(exception)

        fun <A> failure(exception: Exception): Result<A> =
            Failure(IllegalStateException(exception))
    }
}

fun <A, B> liftResult(f: (A) -> B): (Result<A>) -> Result<B> = { it.map(f) }
fun <A, B, C> lift2(f: (A) -> (B) -> C): (Result<A>) -> (Result<B>) -> Result<C> =
    { ra ->
        { rb ->
            ra.flatMap { a ->
                rb.map { b ->
                    f(a)(b)
                }
            }
        }
    }

fun <A, B, C, D> lift3(f: (A) -> (B) -> (C) -> D):
            (Result<A>) -> (Result<B>) -> (Result<C>) -> Result<D> =
    { ra ->
        { rb ->
            { rc ->
                ra.flatMap { a ->
                    rb.flatMap { b ->
                        rc.map { c ->
                            f(a)(b)(c)
                        }
                    }
                }
            }
        }
    }

fun <A, B, C> map2(
    rA: Result<A>,
    rB: Result<B>,
    f: (A) -> (B) -> C
): Result<C> = lift2(f)(rA)(rB)
