import csv.CSVReader
import java.io.File

fun main(args: Array<String>) {
    val dataFile = File("data.csv")
    val rawData = dataFile.readText().replace("\r\n", "\n")
    val reader = CSVReader(rawData)

    while (reader.hasNext()) {
        println(reader.nextRow())
    }
}