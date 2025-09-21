class Book(
    val isbn: String,
    val title: String,
    val author: String,
    val publicationYear: Int
) {
	var isAvailable: Boolean = true
    	private set
        
    var borrowedBy: String? = null
    	private set
        
    fun borrowBook(memberName: String) : Boolean {
    	return if (isAvailable) {
        	borrowedBy = memberName
            isAvailable = false
            true
        } else {
        	false
        }
    }
    
    fun returnBook(): Boolean {
    	return if (!isAvailable) {
        	borrowedBy = null
            isAvailable = true
            true
        } else {
        	false
        }
    }
    
    fun getBookInfo() : String {
    	return """
        |
        |=== BOOK INFORMATION ===
        |ISBN: ${isbn}
        |Title: ${title}
        |Author: ${author}
        |Publication Year: ${publicationYear}
        |Available: $isAvailable
        |Borrowed By: ${borrowedBy ?: "Nobody"}
        |========================
        """.trimMargin()
    }
}
class LibraryMember(val memberId: String, val name: String) {
    private val borrowedBooks = mutableListOf<String>()

    fun borrowBook(book: Book): Boolean {
        return if (borrowedBooks.size < 3 && book.borrowBook(name)) {
            borrowedBooks.add(book.isbn) 
            println("$name borrowed '${book.title}'")
            true
        } else {
            println("$name can't borrow '${book.title}'!! (limit reached or unavailable)")
            false
        }
    }

    fun returnBook(isbn: String, book: Book): Boolean {
        return if (borrowedBooks.contains(isbn)) {
            borrowedBooks.remove(isbn)
            book.returnBook()
            println("$name returned '${book.title}'")
            true
        } else {
            false
        }
    }

    fun getBorrowedBooks(): List<String> = borrowedBooks.toList()

    fun getMemberInfo(): String {
        return """
        |
        |=== MEMBER INFORMATION ===
        |ID: $memberId
        |Name: $name
        |Borrowed Books: ${if (borrowedBooks.isEmpty()) "None" else borrowedBooks.joinToString(", ")}
        |==========================
        """.trimMargin()
    }
}


class Library {
	private val books = mutableMapOf<String, Book>()
	private val members = mutableMapOf<String, LibraryMember>()
    
    fun addBook(book : Book) {
    	 books[book.isbn] = book  
    }
    
    fun registerMember(member: LibraryMember) {
    	 members[member.memberId] = member 
    }
    
 	fun borrowBook(memberId: String, isbn: String): Boolean {
        val member = members[memberId]     
        val book = books[isbn]             

        return if (member != null && book != null) {
            member.borrowBook(book)         
        } else {
            false
        }
    }
    
   fun returnBook(memberId: String, isbn: String): Boolean {
    val member = members[memberId]
    val book = books[isbn]

    return if (member != null && book != null) {
        member.returnBook(isbn, book)   
    } else {
        false
    }
}

    fun getAvailableBooks(): List<Book> {
        return books.values.filter { it.isAvailable }
    }

    fun generateLibraryReport(): String {
        val totalBooks = books.size
        val availableBooks = books.values.count { it.isAvailable }
        val borrowedBooks = totalBooks - availableBooks
        val totalMembers = members.size

        val bookDetails = books.values.joinToString("\n") { it.getBookInfo() }
        val memberDetails = members.values.joinToString("\n") { it.getMemberInfo() }

        return """
            |=== LIBRARY REPORT ===
            |Total Books: $totalBooks
            |Available Books: $availableBooks
            |Borrowed Books: $borrowedBooks
            |Total Members: $totalMembers
            |
            |------- Books --------
            |
            |$bookDetails
            |
            |------ Members -------
            |
            |$memberDetails
            |=======================
        """.trimMargin()
    }
    
}


fun main() {
    val library = Library()
    library.addBook(Book("978-0134685991", "Effective Java", "Joshua Bloch", 2017))
    library.addBook(Book("978-0135166307", "Clean Code", "Robert Martin", 2008))

    library.registerMember(LibraryMember("M001", "Alice Johnson"))
    library.registerMember(LibraryMember("M002", "Bob Smith"))

    println("Borrowing: " + library.borrowBook("M001", "978-0134685991"))
    println(library.generateLibraryReport())
}
