package com.mango.fakestore.core.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class MangoDatabaseIntegrationTest {

    private lateinit var context: Context
    private lateinit var db: TestMangoDatabase

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        db = Room.inMemoryDatabaseBuilder(context, TestMangoDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun tearDown() {
        if (::db.isInitialized && db.isOpen) db.close()
    }

    @Test
    fun database_opens_successfully() {
        // Force the database to open by accessing the writable database
        db.openHelper.writableDatabase
        assertThat(db.isOpen).isTrue()
    }

    @Test
    fun database_version_is_1() {
        assertThat(db.openHelper.readableDatabase.version).isEqualTo(1)
    }
}
