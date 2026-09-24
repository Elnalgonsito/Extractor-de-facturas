package com.tecnoayuda.extractorfacturas.utils

data class ParsedInvoiceData(
    val date: String?,
    val total: String?
)

fun extraerDatosDelTicket(textoCrudo: String): ParsedInvoiceData {
    // Regex for dates: dd/mm/yyyy or yyyy-mm-dd
    val dateRegex = Regex("""(\d{2}/\d{2}/\d{4}|\d{4}-\d{2}-\d{2})""")
    val dateMatch = dateRegex.find(textoCrudo)
    val date = dateMatch?.value

    // Regex for total: 'TOTAL' optionally followed by non-digits, or '$' followed by optional spaces.
    // Captures the number with optional decimals.
    val totalRegex = Regex("""(?:(?i)total[^\d]*|[$]\s*)(\d+(?:[.,]\d{1,2})?)""")
    val totalMatch = totalRegex.find(textoCrudo)
    val total = totalMatch?.groups?.get(1)?.value

    return ParsedInvoiceData(date = date, total = total)
}
