package ru.totowka.accountant.presentation.primary

import com.google.android.gms.common.util.CollectionUtils.listOf
import org.junit.Assert.assertTrue
import org.junit.Test
import ru.totowka.accountant.Controller
import ru.totowka.accountant.data.type.ChartTransactionState
import java.time.LocalDate

class AnalysisServiceTest {
    @Test
    fun shouldCorrectlyCompareToAlignedMonths() {
        // given
        val inputValues = listOf(ChartTransactionState(1.0, LocalDate.MIN))

        // when
        val result = Controller().createTotalState(inputValues, LocalDate.MIN, LocalDate.MAX);

        // then
        assertTrue(result == listOf(ChartTransactionState(1.0, LocalDate.MIN)))
    }
}