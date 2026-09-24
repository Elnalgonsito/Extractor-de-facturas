package com.tecnoayuda.extractorfacturas.utils

import org.junit.Assert.assertEquals
import org.junit.Test

class InvoiceParserTest {

    @Test
    fun testParseDates() {
        var result = extraerDatosDelTicket("El ticket tiene fecha 12/03/2023 y algunos datos.")
        assertEquals("12/03/2023", result.date)

        result = extraerDatosDelTicket("Fecha: 2023-12-01 total: 100.00")
        assertEquals("2023-12-01", result.date)
    }

    @Test
    fun testParseTotal() {
        var result = extraerDatosDelTicket("TOTAL: 123.45")
        assertEquals("123.45", result.total)

        result = extraerDatosDelTicket("total $ 123.45")
        assertEquals("123.45", result.total)

        result = extraerDatosDelTicket("$ 123.45")
        assertEquals("123.45", result.total)

        result = extraerDatosDelTicket("Total: $123.45")
        assertEquals("123.45", result.total)

        result = extraerDatosDelTicket("Compra por $1000.")
        assertEquals("1000", result.total)

        result = extraerDatosDelTicket("TOTAL 50,50")
        assertEquals("50,50", result.total)
    }
}
