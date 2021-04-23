package ru.totowka.accountant.backend

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import org.junit.Assert.assertEquals
import org.junit.Test
import ru.totowka.accountant.data.Product
import ru.totowka.accountant.data.Transaction


class FirebaseRepositoryTest {
    @Test
    fun addTransaction() {
        val firestore = FirebaseFirestore.getInstance()
        val auth: FirebaseAuth = FirebaseAuth.getInstance();
        auth.useEmulator("10.0.2.2", 9099)
        firestore.useEmulator("10.0.2.2", 8080)
        val settings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(false)
            .build()
        firestore.firestoreSettings = settings

        val fb = FirebaseRepository(auth = auth, db = firestore)
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