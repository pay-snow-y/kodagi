import org.junit.jupiter.api.Test

/**
 * @author Snow
 */

class InAndOut {

    private val production1: Production<Food> = FoodStore()
    private val production2: Production<Food> = FastFoodStore()
    private val production3: Production<Food> = InOutBurger()

    @Test
    fun `OutTest`() {
        production1.produce()
        production2.produce()
        production3.produce()
    }

    private val consumer1: Consumer<Burger> = Everybody()
    private val consumer2: Consumer<Burger> = ModernPeople()
    private val consumer3: Consumer<Burger> = American()

    @Test
    fun `InTest`() {
        consumer1.consume(Burger())
        consumer2.consume(Burger())
        consumer3.consume(Burger())
    }
}

interface Production<out T> {
    fun produce(): T
}

interface Consumer<in T> {
    fun consume(item: T)
}

open class Food
open class FastFood : Food()
class Burger : FastFood()

// Out T
class FoodStore : Production<Food> {
    override fun produce(): Food {
        println("Produce food")
        return Food()
    }
}

class FastFoodStore : Production<FastFood> {
    override fun produce(): FastFood {
        println("Produce fast food")
        return FastFood()
    }
}

class InOutBurger : Production<Burger> {
    override fun produce(): Burger {
        println("Produce burger")
        return Burger()
    }
}

// In T
class Everybody : Consumer<Food> {
    override fun consume(item: Food) {
        println("Eat food")
    }
}

class ModernPeople : Consumer<FastFood> {
    override fun consume(item: FastFood) {
        println("Eat fast food")
    }
}

class American : Consumer<Burger> {
    override fun consume(item: Burger) {
        println("Eat burger")
    }
}
