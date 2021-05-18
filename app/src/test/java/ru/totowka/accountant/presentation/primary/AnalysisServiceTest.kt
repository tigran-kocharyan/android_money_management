package ru.totowka.accountant.presentation.primary

import org.junit.Assert.*
import org.junit.Test
import ru.totowka.accountant.data.type.ChartTransactionState
import java.time.LocalDate

class AnalysisServiceTest {
    @Test
    fun shouldCorrectlyCompareToAlignedMonths() {
        // given
        val inputValues = listOf(ChartTransactionState(1.0 , LocalDate.MIN))

        // when
        val result = AnalysisService.createTotalState(inputValues, LocalDate.MIN, LocalDate.MAX);

        // then
        assertTrue(result == listOf(ChartTransactionState(1.0 , LocalDate.MIN)))
    }
}