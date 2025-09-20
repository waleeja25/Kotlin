fun calculateLetterGrade(percentage: Double) : String {
	val grade = 
    if (percentage >= 90.0) { "A" }
    else if (percentage >= 80.0) {"B"}
    else if (percentage >= 70.0) {"C"}
    else if (percentage >= 60.0) {"D"}
    else {"F"}
    return grade
}

fun calculateGPA(grades: List<String>): Double {
    val points = grades.map { grade ->
        when (grade) {
            "A" -> 4.0
            "B" -> 3.0
            "C" -> 2.0
            "D" -> 1.0
            else -> 0.0
        }
    }
    return points.average()
}


fun generateGradeReport(
    studentName: String,
    studentId: String,
    courses: List<String>,
    percentages: List<Double>
): String {
    val letterGrades = percentages.map { calculateLetterGrade(it) }
    val gpa = calculateGPA(letterGrades)

    val report = """
    |=== Grade Report ===
    |Student Name: $studentName
    |Student ID: $studentId
    |----------------------------
    |Course   | Percentage | Grade
    |----------------------------
    |${courses[0]}     | ${"%.2f".format(percentages[0])}       | ${letterGrades[0]}
    |${courses[1]}  | ${"%.2f".format(percentages[1])}       | ${letterGrades[1]}
    |${courses[2]}  | ${"%.2f".format(percentages[2])}       | ${letterGrades[2]}
    |${courses[3]}  | ${"%.2f".format(percentages[3])}       | ${letterGrades[3]}
    |----------------------------
    |GPA: ${"%.2f".format(gpa)}
    |============================
    """.trimMargin()

    return report
}

fun main() {
    val courses = listOf("Math", "Science", "English", "History")
    val percentages = listOf(85.5, 92.0, 78.5, 88.0)

    val report = generateGradeReport("John Smith", "STU001", courses, percentages)
    println(report)
}
