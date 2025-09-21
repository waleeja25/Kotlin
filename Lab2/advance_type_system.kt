import java.time.LocalDate

// --- Payment Methods ---
sealed class PaymentMethod {
    data class CreditCard(
        val number: String,
        val expiryMonth: Int,
        val expiryYear: Int,
        val cvv: String
    ) : PaymentMethod()

    data class BankTransfer(
        val accountNumber: String,
        val routingNumber: String
    ) : PaymentMethod()

    data class DigitalWallet(
        val walletType: WalletType,
        val walletId: String
    ) : PaymentMethod()

    data class Cash(
        val amount: Double
    ) : PaymentMethod()
}

// --- Wallet Types ---
enum class WalletType(val displayName: String, val processingFee: Double) {
    PAYPAL("PayPal", 0.029),
    VENMO("Venmo", 0.025),
    APPLE_PAY("Apple Pay", 0.015),
    GOOGLE_PAY("Google Pay", 0.015);

    fun calculateFee(amount: Double): Double = amount * processingFee
}

// --- Payment Results ---
sealed class PaymentResult {
    data class Success(val transactionId: String, val amount: Double, val fee: Double) : PaymentResult()
    data class Failure(val reason: String, val errorCode: Int) : PaymentResult()
    object Pending : PaymentResult()
    data class RequiresVerification(val verificationMethod: String) : PaymentResult()
}

// --- Transaction Status ---
enum class TransactionStatus {
    INITIATED, PROCESSING, COMPLETED, FAILED, REFUNDED;

    fun canTransitionTo(newStatus: TransactionStatus): Boolean {
        return when (this) {
            INITIATED -> newStatus == PROCESSING || newStatus == FAILED
            PROCESSING -> newStatus == COMPLETED || newStatus == FAILED
            COMPLETED -> newStatus == REFUNDED
            FAILED -> false
            REFUNDED -> false
        }
    }
}

// --- Transaction ---
data class Transaction(
    val id: String,
    val amount: Double,
    val paymentMethod: PaymentMethod,
    var status: TransactionStatus,
    val timestamp: String,
    var result: PaymentResult? = null
)

// --- Payment Processor ---
class PaymentProcessor {

    fun processPayment(transaction: Transaction): PaymentResult {
        // Validate first
        if (!validatePaymentMethod(transaction.paymentMethod)) {
            transaction.status = TransactionStatus.FAILED
            val failure = PaymentResult.Failure("Invalid payment method", 1001)
            transaction.result = failure
            return failure
        }

        val totalCost = calculateTotalCost(transaction.amount, transaction.paymentMethod)

        // Simulate processing
        return when (val method = transaction.paymentMethod) {
            is PaymentMethod.CreditCard -> {
                transaction.status = TransactionStatus.PROCESSING
                val fee = totalCost - transaction.amount
                transaction.status = TransactionStatus.COMPLETED
                val success = PaymentResult.Success(transaction.id, transaction.amount, fee)
                transaction.result = success
                success
            }
            is PaymentMethod.BankTransfer -> {
                transaction.status = TransactionStatus.PROCESSING
                val fee = 0.0
                transaction.status = TransactionStatus.COMPLETED
                val success = PaymentResult.Success(transaction.id, transaction.amount, fee)
                transaction.result = success
                success
            }
            is PaymentMethod.DigitalWallet -> {
                transaction.status = TransactionStatus.PROCESSING
                val fee = method.walletType.calculateFee(transaction.amount)
                transaction.status = TransactionStatus.COMPLETED
                val success = PaymentResult.Success(transaction.id, transaction.amount, fee)
                transaction.result = success
                success
            }
            is PaymentMethod.Cash -> {
                if (method.amount < transaction.amount) {
                    transaction.status = TransactionStatus.FAILED
                    val failure = PaymentResult.Failure("Insufficient cash", 2001)
                    transaction.result = failure
                    failure
                } else {
                    transaction.status = TransactionStatus.COMPLETED
                    val success = PaymentResult.Success(transaction.id, transaction.amount, 0.0)
                    transaction.result = success
                    success
                }
            }
        }
    }

    fun validatePaymentMethod(method: PaymentMethod): Boolean {
        return when (method) {
            is PaymentMethod.CreditCard -> {
                method.number.length == 16 &&
                        method.cvv.length in 3..4 &&
                        method.expiryYear >= LocalDate.now().year
            }
            is PaymentMethod.BankTransfer -> method.accountNumber.isNotEmpty() && method.routingNumber.isNotEmpty()
            is PaymentMethod.DigitalWallet -> method.walletId.isNotEmpty()
            is PaymentMethod.Cash -> method.amount > 0
        }
    }

    fun calculateTotalCost(amount: Double, method: PaymentMethod): Double {
        return when (method) {
            is PaymentMethod.DigitalWallet -> amount + method.walletType.calculateFee(amount)
            else -> amount
        }
    }

    fun handlePaymentResult(result: PaymentResult): String {
        return when (result) {
            is PaymentResult.Success -> "Transaction ${result.transactionId} succeeded! Amount: ${result.amount}, Fee: ${result.fee}"
            is PaymentResult.Failure -> "Transaction failed! Reason: ${result.reason} (Code: ${result.errorCode})"
            is PaymentResult.Pending -> "Transaction is pending..."
            is PaymentResult.RequiresVerification -> "Transaction requires verification via ${result.verificationMethod}"
        }
    }
}

// --- Main / Test ---
fun main() {
    val processor = PaymentProcessor()
    val transactions = listOf(
        Transaction(
            "T001", 100.0,
            PaymentMethod.CreditCard("1234567890123456", 12, 2025, "123"),
            TransactionStatus.INITIATED,
            "2024-01-20"
        ),
        Transaction(
            "T002", 50.0,
            PaymentMethod.DigitalWallet(WalletType.PAYPAL, "user@paypal.com"),
            TransactionStatus.INITIATED,
            "2024-01-20"
        ),
        Transaction(
            "T003", 25.0,
            PaymentMethod.Cash(25.0),
            TransactionStatus.INITIATED,
            "2024-01-20"
        )
    )

    transactions.forEach { tx ->
        val result = processor.processPayment(tx)
        println(processor.handlePaymentResult(result))
    }
}
