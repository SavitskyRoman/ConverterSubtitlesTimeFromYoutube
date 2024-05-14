import java.io.*
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds


fun main() {
    try {
        writeToFile(
            "/home/bartimeys/Загрузки/testFin",
            readFromFile("/home/bartimeys/Загрузки/fullRusSubForTransform")
        )
    } catch (e: FileNotFoundException) {
        println("Err type is FileNotFoundException")
        e.printStackTrace()
    } catch (e: Exception) {
        println("Err type is Exception")
        e.printStackTrace()
    }
}

fun readFromFile(fileName: String): String {
    val file = File(fileName)
    val content = StringBuilder()
    var LineText = ""
    var firstDuration = 0.seconds
    var secondDuration = 0.seconds
    var counterToThree = 0
    var counterSubLine = 0

    try {
        val reader = BufferedReader(FileReader(file))
        var line: String?
        while (reader.readLine().also { line = it } != null) {
            val levelOnePatternTime = "^\\d:\\d\\d$"
            val levelTwoPatternTime = "^\\d\\d:\\d\\d$"
            val levelThreePatternTime = "^\\d:\\d\\d:\\d\\d$"
            val levelFourPatternTime = "^\\d\\d:\\d\\d:\\d\\d$"
            if (line!!.matches(levelOnePatternTime.toRegex()) || line!!.matches(levelTwoPatternTime.toRegex())
                || line!!.matches(levelThreePatternTime.toRegex()) || line!!.matches(levelFourPatternTime.toRegex())
            ) {
                if (counterToThree == 1) {
                    secondDuration = convertTimeStringToDuration(line.toString())
                    counterToThree = 2
                } else if (counterToThree == 0) {
                    firstDuration = convertTimeStringToDuration(line.toString())
                    counterToThree = 1
                }
                if (counterToThree == 2) {
                    content.append(counterSubLine).append("\n")
                    content.append(convertTimeDurationToStringBuilder(firstDuration, secondDuration))
                    content.append(LineText).append("\n")
                    firstDuration = secondDuration
                    counterToThree = 1
                    counterSubLine++

                }
            } else {
                LineText = line.toString()

            }
        }
        //in the end
        content.append(counterSubLine).append("\n")
        secondDuration = firstDuration + 5.seconds
        content.append(convertTimeDurationToStringBuilder(firstDuration, secondDuration))
        content.append(LineText).append("\n")

        reader.close()
    } catch (e: Exception) {
        println("Произошла ошибка при чтении файла: ${e.message}")
        e.printStackTrace()
    }

    return content.toString()
}

fun convertTimeDurationToStringBuilder(firstDuration: Duration, secondDuration: Duration): StringBuilder {
    var lineTime = StringBuilder()
    var stringForDropLastFromDuration = ""
    var differenceDuration = secondDuration - 0.5.seconds

    firstDuration.toComponents { hours, minutes, seconds, nanoseconds ->
        lineTime.append("$hours:$minutes:$seconds,000 --> ")
    }

    differenceDuration.toComponents { hours, minutes, seconds, nanoseconds ->
        stringForDropLastFromDuration = "$hours:$minutes:$seconds,$nanoseconds".dropLast(6)
    }

    lineTime.append(stringForDropLastFromDuration).append("\n")
    return lineTime
}

fun convertTimeStringToDuration(str: String): Duration {
    var timeConverting: (Duration) = 0.seconds

    if (str.length == 4) {
        timeConverting = str.get(0).digitToInt().minutes +
                (str.get(2).digitToInt() * 10).seconds +
                str.get(3).digitToInt().seconds
    } else if (str.length == 5) {
        timeConverting = (str.get(0).digitToInt() * 10).minutes +
                str.get(1).digitToInt().minutes +
                (str.get(3).digitToInt() * 10).seconds +
                str.get(4).digitToInt().seconds
    } else if (str.length == 7) {
        timeConverting = str.get(0).digitToInt().hours +
                (str.get(2).digitToInt() * 10).minutes +
                str.get(3).digitToInt().minutes +
                (str.get(5).digitToInt() * 10).seconds +
                str.get(6).digitToInt().seconds
    } else if (str.length == 8) {
        timeConverting = (str.get(0).digitToInt() * 10).hours +
                str.get(1).digitToInt().hours +
                (str.get(3).digitToInt() * 10).minutes +
                str.get(4).digitToInt().minutes +
                (str.get(6).digitToInt() * 10).seconds +
                str.get(7).digitToInt().seconds
    }

    return timeConverting
}

fun writeToFile(fileName: String, content: String) {
    try {
        val file = File(fileName)
        val writer = FileWriter(file)
        writer.write(content)
        writer.close()
        println("Запись в файл успешно завершена.")
    } catch (e: Exception) {
        println("Произошла ошибка при записи в файл: ${e.message}")
    }
}
