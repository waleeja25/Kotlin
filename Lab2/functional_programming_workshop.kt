
fun <T, R> List<T>.customMap(transform: (T) -> R): List<R> {
    val result = mutableListOf<R>()
    for (item in this) result.add(transform(item))
    return result
}

fun <T> List<T>.customFilter(predicate: (T) -> Boolean): List<T> {
    val result = mutableListOf<T>()
    for (item in this) if (predicate(item)) result.add(item)
    return result
}

fun <T, R> List<T>.customFold(initial: R, operation: (R, T) -> R): R {
    var accumulator = initial
    for (item in this) accumulator = operation(accumulator, item)
    return accumulator
}


object FunctionUtils {
    fun <A, B, C> curry(f: (A, B) -> C): (A) -> (B) -> C = { a -> { b -> f(a, b) } }

    fun <A, B, C> compose(f: (B) -> C, g: (A) -> B): (A) -> C = { a -> f(g(a)) }

    fun <A, B, C> partial(f: (A, B) -> C, a: A): (B) -> C = { b -> f(a, b) }

    fun <A, B> memoize(f: (A) -> B): (A) -> B {
        val cache = mutableMapOf<A, B>()
        return { a ->
            cache.getOrPut(a) { f(a) }
        }
    }
}


class DataProcessor<T> {
    private var data: List<T> = emptyList()

    fun withData(newData: List<T>): DataProcessor<T> {
        data = newData
        return this
    }

    fun filter(predicate: (T) -> Boolean): DataProcessor<T> {
        return DataProcessor<T>().withData(data.filter(predicate))
    }

    fun <R> map(transform: (T) -> R): DataProcessor<R> {
        return DataProcessor<R>().withData(data.map(transform))
    }

    fun <R : Comparable<R>> sortedBy(selector: (T) -> R): DataProcessor<T> {
        return DataProcessor<T>().withData(data.sortedBy(selector))
    }

    fun take(count: Int): DataProcessor<T> {
        return DataProcessor<T>().withData(data.take(count))
    }

    fun distinct(): DataProcessor<T> {
        return DataProcessor<T>().withData(data.distinct())
    }

    fun <R> reduce(operation: (R, T) -> R, initial: R): R {
        return data.fold(initial, operation)
    }

    fun toList(): List<T> = data
}

fun main() {
    println("=== FUNCTIONAL PROGRAMMING WORKSHOP ===")

    val numbers = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
    println("Original numbers: $numbers")

    val mapped = numbers.customMap { it * 2 }
    val filtered = numbers.customFilter { it % 2 == 0 }
    val folded = numbers.customFold(0) { acc, n -> acc + n }
    println("Mapped numbers (x2): $mapped")
    println("Filtered even numbers: $filtered")
    println("Folded sum: $folded")

    val add = { a: Int, b: Int -> a + b }
    val multiply = { x: Int -> x * 2 }
    val addOne = { x: Int -> x + 1 }

    val curriedAdd = FunctionUtils.curry(add)
    println("Curried add: ${curriedAdd(3)(5)}")

    val composed = FunctionUtils.compose(multiply, addOne)
    println("Composed function (multiply ∘ addOne) of 4: ${composed(4)}")

    val partialAdd = FunctionUtils.partial(add, 10)
    println("Partial application (10 + x) with 5: ${partialAdd(5)}")

    val memoizedSquare = FunctionUtils.memoize { x: Int -> x * x }
    println("Memoized square of 6: ${memoizedSquare(6)}")

    val processor = DataProcessor<Int>()
        .withData(listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 1, 2, 3))
        .filter { it % 2 == 0 }
        .distinct()
        .map { it * 3 }
        .sortedBy { -it }
        .take(5)

    println("Processed data: ${processor.toList()}")
}
