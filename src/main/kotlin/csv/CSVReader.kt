package csv

/**
 * Allows CSV files to be read row-by-row.
 */
class CSVReader(private val source: String) {
    private var tokenStart: Int = 0
    private var currentIdx: Int = 0
    private val header: List<String> = readHeader()
    private var inQuote: Boolean = false

    private fun readHeader(): List<String> {
        return parseRow()
    }

    fun hasNext(): Boolean {
        return !endOfFile()
    }

    /**
     * Read the next row of the CSV file.
     * @throws Error if the current row does not mach the header format
     */
    fun nextRow(): Map<String, String> {
        val data: MutableMap<String, String> = mutableMapOf()
        if (!endOfFile()) {
            val row = parseRow()
            if (row.size != header.size) {
                throw Error("Row: $row does not match header: $header")
            }
            for (i in header.indices) {
                val column = header[i]
                data[column] = row[i]
            }
        }

        return data.toMap()
    }

    /**
     * Convert the current row to a list of tokens (Strings)
     * @return the entries of the current row
     */
    private fun parseRow(): List<String> {
        val entries: MutableList<String> = mutableListOf()

        // Parse each entry of the row
        while (!endOfLine()) {
            scanToken(entries)
            tokenStart = currentIdx
        }

        // Skip the remaining newline
        if (!endOfFile()) consume()
        tokenStart = currentIdx

        return entries.toList()
    }

    /**
     * Scans the next token in the CSV file.
     * @param entries the list in which to store any entries found in the row
     */
    private fun scanToken(entries: MutableList<String>) {
        val ch = consume()
        when (ch) {
            ',' -> return
            '\n' -> return
            '\"' -> {
                inQuote = if (inQuote) {
                    false
                } else {
                    quotedToken(entries)
                    true
                }
            }
            else -> {
                while (!endOfLine() && peek() != ',') consume()
                entries.add(source.substring(tokenStart..<currentIdx))
            }
        }
    }

    /**
     * Parses a token that is found in quotes (ignores commas)
     * @param entries the list in which to store any entries found in the row
     */
    private fun quotedToken(entries: MutableList<String>) {
        while (!endOfLine() && peek() != '\"') consume()
        val token = source.substring(tokenStart + 1..<currentIdx)

        if (peekNext() != ',') {
            throw Error("Syntax Error: Expected ',' after '\"' @ $token")
        }

        entries.add(token)
    }

    private fun peekNext(): Char {
        if (currentIdx + 1 >= source.length) {
            return 0.toChar()
        }
        return source[currentIdx + 1]
    }

    private fun consume(): Char {
        return source[currentIdx++]
    }

    private fun peek(): Char {
        if (endOfFile()) {
            return 0.toChar()
        }
        return source[currentIdx]
    }
    private fun endOfLine(): Boolean {
        return endOfFile() || peek() == '\n'
    }

    private fun endOfFile(): Boolean {
        return currentIdx >= source.length
    }
}