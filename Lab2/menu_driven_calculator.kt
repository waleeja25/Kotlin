fun showMenu() {
	println(""" 
    	|=== CALCULATOR MENU ===
        |
        |1. Addition
        |2. Subtraction
        |3. Multiplication
        |4. Division
        |5. Power
        |6. Exit
        |Choose an option (1 - 6):
    """.trimMargin())
}

fun performCalculation(operation: Int) {
	val a = 10.0
    val b = 5.0
    when (operation) {
    	1 -> println("$a + $b = ${a + b}")
        2 -> println("$a - $b = ${a - b}")
        3 -> println("$a * $b = ${a * b}")
        4 -> {
        	if (b != 0.0) {
            	println("$a / $b = ${a / b}")
            } else {
            	println("Error! Division by zero")
            }
        } 
        5 -> {
        	var result = 1.0
            repeat(b.toInt()) { result *= a}
            println("$a ^ ${b.toInt()} = ${result}")
        }
        else -> println("Invalid operation!")
    }
}
fun main() {
	val simulatedChoices = listOf(1, 2, 3, 4, 5, 6)
    for (choice in simulatedChoices) {
    	showMenu()
        println("Choice: ${choice}")
        
        if (choice == 6) {
        	println("Thankyou for using the calculator")
            break
        } else {
        	performCalculation(choice)
        }
        
        println()
    }
}