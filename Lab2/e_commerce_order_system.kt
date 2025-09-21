data class Customer(
    val id: String,
    val name: String,
    val email: String?,
    val shippingAddress: Address?,
    val billingAddress: Address?,
    val loyaltyCard: LoyaltyCard?
)

data class Address(
    val street: String,
    val city: String,
    val state: String,
    val zipCode: String?,
    val country: String
)

data class LoyaltyCard(
    val cardNumber: String,
    val points: Int,
    val tier: String 
)

data class Product(
    val id: String,
    val name: String,
    val price: Double,
    val category: String,
    val discount: Discount?
)

data class Discount(
    val percentage: Double,
    val description: String,
    val validUntil: String?
)

data class OrderItem(
    val product: Product,
    val quantity: Int,
    val customizations: Map<String, String>?
)

data class Order(
    val id: String,
    val customer: Customer,
    val items: List<OrderItem>,
    val shippingMethod: ShippingMethod?,
    val paymentInfo: PaymentInfo?
)

data class ShippingMethod(
    val name: String,
    val cost: Double,
    val estimatedDays: Int?
)

data class PaymentInfo(
    val method: String,
    val lastFourDigits: String?,
    val isProcessed: Boolean = false
)

class OrderProcessor {
	fun calculateItemSubtotal (orderItem : OrderItem) : Double {
    	val price = orderItem.product.price * orderItem.quantity
        val discount = orderItem.product.discount
        val discountAmount = discount?.let {
        	price * (it.percentage / 100)
        } ?: 0.0
        return price - discountAmount
    }
    
    fun calculateOrderTotal(order: Order) : Double {
    	val itemsTotal = order.items.sumOf { calculateItemSubtotal(it) }
        val shippingCost = order.shippingMethod?.cost ?: 0.0
        return itemsTotal + shippingCost
    }
    
    fun calculateLoyaltyDiscount (customer: Customer, orderTotal : Double) : Double {
    	 return customer.loyaltyCard?.let { card ->
            when (card.tier) {
                "Bronze" -> orderTotal * 0.02
                "Silver" -> orderTotal * 0.05
                "Gold" -> orderTotal * 0.1
                "Platinum" -> orderTotal * 0.15
                else -> 0.0
            }
        } ?: 0.0
    }
    
    fun validateShippingAddress(customer: Customer): String {
    	 val address = customer.shippingAddress
        return if (address == null) {
            "Shipping address missing."
        } else {
            val missingFields = mutableListOf<String>()
            if (address.street.isBlank()) {missingFields.add("Street")}
            if (address.city.isBlank()) {missingFields.add("City")}
            if (address.state.isBlank()) {missingFields.add("State")}
            if (address.zipCode.isNullOrBlank()) {missingFields.add("ZipCode")}
            if (missingFields.isEmpty()) {"Address valid."}
            else {"Missing fields: ${missingFields.joinToString()}"}
        }
    }
    
    fun processPayment(order: Order): Boolean {
             return order.paymentInfo?.takeIf { it.method.isNotBlank() }?.let { payment ->
            println("Processing ${payment.method} payment for order ${order.id}...")
            payment.isProcessed
        } ?: false
    }
    
    fun generateOrderSummary(order: Order): String {
        val sb = StringBuilder()
        sb.appendLine("=== Order Summary (${order.id}) ===")
        sb.appendLine("Customer: ${order.customer.name}")
        sb.appendLine("Email: ${order.customer.email ?: "Not provided"}")
        sb.appendLine("Shipping Address: ${order.customer.shippingAddress?.let {
            "${it.street}, ${it.city}, ${it.state}, ${it.zipCode ?: "N/A"}, ${it.country}"
        } ?: "Not provided"}")
        sb.appendLine("Billing Address: ${order.customer.billingAddress?.let {
            "${it.street}, ${it.city}, ${it.state}, ${it.zipCode ?: "N/A"}, ${it.country}"
        } ?: "Not provided"}")

        sb.appendLine("\nItems:")
        order.items.forEach { item ->
            sb.appendLine("- ${item.product.name} x${item.quantity} = \$${calculateItemSubtotal(item)}")
        }

        val subtotal = calculateOrderTotal(order)
        val loyaltyDiscount = calculateLoyaltyDiscount(order.customer, subtotal)
        val finalTotal = subtotal - loyaltyDiscount

        sb.appendLine("\nSubtotal: \$$subtotal")
        sb.appendLine("Loyalty Discount: -\$$loyaltyDiscount")
        sb.appendLine("Total: \$$finalTotal")
        return sb.toString()
    }
    
    fun findCustomerOrders(customers: List<Customer>, customerId: String): List<Order>? {
        val customer = customers.find { it.id == customerId }
        return customer?.let {
            listOf(
                Order(
                    id = "O-${customer.id}",
                    customer = customer,
                    items = listOf(),
                    shippingMethod = null,
                    paymentInfo = null
                )
            )
        }
    }
    
     fun getCustomersWithIncompleteProfiles(customers: List<Customer>): List<String> {
        return customers.filter {
            it.email.isNullOrBlank() || it.shippingAddress == null || it.billingAddress == null
        }.map { it.name }
    }

}

fun main() {
    val processor = OrderProcessor()

    val customer1 = Customer(
        id = "C001",
        name = "John Doe",
        email = "john@email.com",
        shippingAddress = Address("123 Main St", "Springfield", "IL", "62701", "USA"),
        billingAddress = Address("123 Main St", "Springfield", "IL", "62701", "USA"),
        loyaltyCard = LoyaltyCard("LC001", 500, "Gold")
    )

    val customer2 = Customer(
        id = "C002",
        name = "Jane Smith",
        email = null,
        shippingAddress = null,
        billingAddress = Address("456 Oak Ave", "Portland", "OR", null, "USA"),
        loyaltyCard = null
    )

    val product1 = Product("P001", "Laptop", 1000.0, "Electronics",
        Discount(10.0, "Back to School", null))
    val product2 = Product("P002", "Mouse", 50.0, "Accessories", null)

    val order1 = Order(
        id = "O001",
        customer = customer1,
        items = listOf(
            OrderItem(product1, 1, null),
            OrderItem(product2, 2, null)
        ),
        shippingMethod = ShippingMethod("Standard", 15.0, 5),
        paymentInfo = PaymentInfo("CreditCard", "1234", isProcessed = true)
    )

    println(processor.generateOrderSummary(order1))
    println(processor.validateShippingAddress(customer1))
    println(processor.validateShippingAddress(customer2))
    println("Customers with incomplete profiles: ${processor.getCustomersWithIncompleteProfiles(listOf(customer1, customer2))}")
}
