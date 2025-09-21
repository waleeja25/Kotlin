data class Book(
    val isbn: String,
    val title: String,
    val author: String,
    val genre: String,
    val publishYear: Int,
    val pages: Int,
    val rating: Double
)

data class Member(
    val id: String,
    val name: String,
    val age: Int,
    val membershipType: String,
    val joinDate: String
)

data class BorrowRecord(
    val memberId: String,
    val isbn: String,
    val borrowDate: String,
    val returnDate: String?,
    val daysOverdue: Int = 0
)

class LibraryAnalytics {

    fun analyzeBooks(books: List<Book>) {
        println("=== Books by Genre with Average Rating ===")
        val avgRatingByGenre = books.groupBy { it.genre }
            .mapValues { (_, books) -> books.map { it.rating }.average() }
        avgRatingByGenre.forEach { (genre, avg) ->
            println("$genre -> Average Rating: ${String.format("%.2f", avg)}")
        }

        println("\n=== Top 5 Highest Rated Books ===")
        books.sortedByDescending { it.rating }.take(5).forEach { book ->
            println("${book.title} by ${book.author} - Rating: ${book.rating}")
        }

        println("\n=== Books Published in Last 10 Years ===")
        val currentYear = 2024
        books.filter { it.publishYear >= currentYear - 10 }.forEach { book ->
            println("${book.title} (${book.publishYear})")
        }

        println("\n=== Average Pages by Genre ===")
        val avgPagesByGenre = books.groupBy { it.genre }
            .mapValues { (_, books) -> books.map { it.pages }.average() }
        avgPagesByGenre.forEach { (genre, avg) ->
            println("$genre -> Average Pages: ${String.format("%.0f", avg)}")
        }

        println("\n=== Authors with Multiple Books ===")
        val authorsWithMultipleBooks = books.groupBy { it.author }
            .filter { it.value.size > 1 }
            .keys
        authorsWithMultipleBooks.forEach { println(it) }
    }

    fun analyzeBorrowingPatterns(records: List<BorrowRecord>, members: List<Member>, books: List<Book>) {
        println("=== Most Popular Books (Most Borrowed) ===")
        val mostBorrowed = records.groupBy { it.isbn }
            .mapValues { it.value.size }
            .toList().sortedByDescending { it.second }
        mostBorrowed.forEach { (isbn, count) ->
            val title = books.find { it.isbn == isbn }?.title ?: "Unknown"
            println("$title -> Borrowed $count times")
        }

        println("\n=== Most Active Members ===")
        val activeMembers = records.groupBy { it.memberId }
            .mapValues { it.value.size }
            .toList().sortedByDescending { it.second }
        activeMembers.forEach { (memberId, count) ->
            val name = members.find { it.id == memberId }?.name ?: "Unknown"
            println("$name -> $count borrowings")
        }

        println("\n=== Average Borrowing Duration ===")
        val borrowDurations = records.mapNotNull { record ->
            if (record.returnDate != null) {
                val diff = java.time.LocalDate.parse(record.returnDate)
                    .toEpochDay() - java.time.LocalDate.parse(record.borrowDate).toEpochDay()
                diff
            } else null
        }
        val avgDuration = if (borrowDurations.isNotEmpty()) borrowDurations.average() else 0.0
        println("Average borrowing duration: ${String.format("%.1f", avgDuration)} days")

	println("\n=== Overdue Statistics ===")
		val totalOverdue = records.count { it.daysOverdue > 0 }
		println("Total overdue records: $totalOverdue")
		val overdueDaysList = records.filter { it.daysOverdue > 0 }.map { it.daysOverdue }
		val avgOverdueDays = if (overdueDaysList.isNotEmpty()) overdueDaysList.average() else 0.0
		println("Average overdue days: ${String.format("%.1f", avgOverdueDays)}")


        println("\n=== Genre Preferences by Membership Type ===")
        val recordsWithBooks = records.mapNotNull { record ->
            val book = books.find { it.isbn == record.isbn }
            val member = members.find { it.id == record.memberId }
            if (book != null && member != null) Pair(member.membershipType, book.genre) else null
        }
        val genreByMembership = recordsWithBooks.groupBy({ it.first }, { it.second })
            .mapValues { it.value.groupingBy { genre -> genre }.eachCount() }
        genreByMembership.forEach { (membership, genreCounts) ->
            println("$membership -> $genreCounts")
        }
    }

    fun generateRecommendations(memberId: String, records: List<BorrowRecord>, books: List<Book>): List<Book> {
        val borrowedIsbns = records.filter { it.memberId == memberId }.map { it.isbn }.toSet()
        val borrowedGenres = records.filter { it.memberId == memberId }
            .mapNotNull { rec -> books.find { it.isbn == rec.isbn }?.genre }.toSet()

        return books.filter { it.isbn !in borrowedIsbns && it.genre in borrowedGenres }
            .sortedByDescending { it.rating }
            .take(5)
    }

    fun generateLibraryReport(
    books: List<Book>,
    members: List<Member>,
    records: List<BorrowRecord>
): String {
    val totalBooks = books.size
    val totalMembers = members.size
    val borrowedBooks = records.size
    val overdueCount = records.count { it.daysOverdue > 0 }

    val mostPopularBooks = records.groupBy { it.isbn }
        .mapValues { it.value.size }
        .toList()
        .sortedByDescending { it.second }
        .take(5)
        .joinToString("\n") { (isbn, count) ->
            val title = books.find { it.isbn == isbn }?.title ?: "Unknown"
            "$title -> Borrowed $count times"
        }

    return """
        |=== Library Report ===
        |Total Books: $totalBooks
        |Total Members: $totalMembers
        |Total Borrowed Books: $borrowedBooks
        |Total Overdue Records: $overdueCount
        |
        |Most Popular Books:
        |$mostPopularBooks
    """.trimMargin()
}

}

fun main() {
    val books = listOf(
        Book("978-0547928227", "The Hobbit", "J.R.R. Tolkien", "Fantasy", 1937, 310, 4.7),
        Book("978-0439708180", "Harry Potter and the Sorcerer's Stone", "J.K. Rowling", "Fantasy", 1997, 309, 4.6),
        Book("978-0062315007", "The Alchemist", "Paulo Coelho", "Fiction", 1988, 163, 4.2),
        Book("978-0307476463", "The Girl with the Dragon Tattoo", "Stieg Larsson", "Thriller", 2005, 465, 4.1),
        Book("978-0141439600", "Pride and Prejudice", "Jane Austen", "Romance", 1813, 279, 4.5),
        Book("978-0345339683", "The Lord of the Rings", "J.R.R. Tolkien", "Fantasy", 1954, 1178, 4.9)
    )

    val members = listOf(
        Member("M001", "Alice Johnson", 25, "Premium", "2023-01-15"),
        Member("M002", "Bob Smith", 19, "Student", "2023-03-22"),
        Member("M003", "Carol Brown", 30, "Basic", "2022-12-05")
    )

    val records = listOf(
        BorrowRecord("M001", "978-0547928227", "2024-01-10", "2024-01-24", 0),
        BorrowRecord("M002", "978-0439708180", "2024-01-15", null, 5),
        BorrowRecord("M001", "978-0345339683", "2024-02-01", null, 0),
        BorrowRecord("M003", "978-0062315007", "2024-01-20", "2024-02-05", 0),
        BorrowRecord("M002", "978-0307476463", "2024-02-01", "2024-02-15", 0)
    )

    val analytics = LibraryAnalytics()

    println("=== Book Analysis ===")
    analytics.analyzeBooks(books)
    println("\n=== Borrowing Patterns ===")
    analytics.analyzeBorrowingPatterns(records, members, books)
    println("\n=== Recommendations for Member M002 ===")
    val recommendations = analytics.generateRecommendations("M002", records, books)
    recommendations.forEach { println("- ${it.title} (${it.genre})") }
    println(analytics.generateLibraryReport(books, members, records))
}
