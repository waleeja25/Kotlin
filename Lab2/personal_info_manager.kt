fun displayPersonalInfo(name: String, age: Int, email: String?, phone: String?, address: String) :  String{
    return """
    |
    |=== Personal Information ===
    |Name: ${name.uppercase()}
    |Age: ${age} years old
    |Email: ${email ?: "Not Provided"}
    |Phone: ${phone ?: "Not Provided"}
    |Address: ${address}
    |Status: ${if (age >= 18) "Adult" else "Minor"}
    |============================
    """.trimMargin()
}
 fun main() {
     val profile1 =displayPersonalInfo("John Doe", 30, "john@email.com", null, "123 Main St")
     val profile2 =displayPersonalInfo("Jane Smith", 25, null, "555-1234", "456 Oak Ave")
     println(profile1)
     println(profile2)
 }