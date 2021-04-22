package ru.totowka.accountant.backend

import com.google.firebase.Timestamp
import org.junit.Test
import org.junit.Assert.*
import ru.totowka.accountant.data.Product
import ru.totowka.accountant.data.Transaction

class FirebaseRepositoryTest {
    @Test
    fun addTransaction() {
        val fb = FirebaseRepository()
        val transaction = Transaction(
            "qrinfo_test",
            Timestamp.now(),
            listOf(
                Product(1, 1.0, "product_1"),
                Product(2, 2.0, "product_2")
            )
        )
        fb.addTransaction(transaction)
        assertEquals(4, 2 + 2);
    }
}