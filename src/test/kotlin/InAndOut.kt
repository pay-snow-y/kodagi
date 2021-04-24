import org.junit.jupiter.api.Test

/**
 * @author Snow
 */

class InAndOut {

    private val production1: Production<Food> = FoodStore()
    private val production2: Production<Food> = FastFoodStore() // Production<FastFood>
    private val production3: Production<Food> = InOutBurger()   // Production<Burger>

    @Test
    fun `OutTest`() {
        production1.produce()   // Food
        production2.produce()   // FastFood
        eatFood(production3.produce())   // Burger
    }

    private val consumer1: Consumer<Burger> = Everybody()   // Consumer<Food>
    private val consumer2: Consumer<Burger> = ModernPeople()    // Consumer<FastFood>
    private val consumer3: Consumer<Burger> = American()    // Consumer<Burger>

    @Test
    fun `InTest`() {
        consumer1.consume(Burger()) // Food
        consumer2.consume(Burger())
        consumer3.consume(Burger())
    }
}

fun eatFood(food:Food) {
    println(food)
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
