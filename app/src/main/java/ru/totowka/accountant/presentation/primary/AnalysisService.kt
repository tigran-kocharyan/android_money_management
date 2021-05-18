package ru.totowka.accountant.presentation.primary

import ru.totowka.accountant.data.type.ChartTransactionState
import java.time.LocalDate
import java.time.YearMonth

class AnalysisService {
    companion object {
        fun createTotalState(
            values: List<ChartTransactionState>,
            startDate: LocalDate,
            endDate: LocalDate
        ): ArrayList<Double> {
            val startMonth = ArrayList<ChartTransactionState>().apply {
                for (i in 1..YearMonth.of(startDate.year, startDate.month).lengthOfMonth()) {
                    add(
                        ChartTransactionState(
                            0.0,
                            LocalDate.of(startDate.year, startDate.month, i)
                        )
                    )
                }
                while (size < 31) {
                    add(ChartTransactionState(0.0, LocalDate.of(startDate.year, 8, size)))
                }
            }
            val endMonth = ArrayList<ChartTransactionState>().apply {
                for (i in 1..YearMonth.of(endDate.year, endDate.month).lengthOfMonth()) {
                    add(ChartTransactionState(0.0, LocalDate.of(endDate.year, endDate.month, i)))
                }
                while (size < 31) {
                    add(ChartTransactionState(0.0, LocalDate.of(endDate.year, 8, size)))
                }
            }
            for (value in values) {
                if (value.date.month == startDate.month) {
                    startMonth[value.date.dayOfMonth - 1] = value
                } else {
                    endMonth[value.date.dayOfMonth - 1] = value
                }
            }
            return ArrayList<Double>().apply {
                for (i in 0..30) {
                    add(endMonth[i].total - startMonth[i].total)
                }
            }
        }
    }
}