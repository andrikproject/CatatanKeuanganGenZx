package com.genzx.keuangan.data.local.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import android.content.Context
import com.genzx.keuangan.data.local.dao.AccountDao
import com.genzx.keuangan.data.local.dao.BudgetDao
import com.genzx.keuangan.data.local.dao.TransactionDao
import com.genzx.keuangan.data.local.entity.AccountEntity
import com.genzx.keuangan.data.local.entity.BudgetEntity
import com.genzx.keuangan.data.local.entity.TransactionEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime

@Database(
    entities = [TransactionEntity::class, AccountEntity::class, BudgetEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun transactionDao(): TransactionDao
    abstract fun accountDao(): AccountDao
    abstract fun budgetDao(): BudgetDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "genzx_keuangan_db"
                )
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            INSTANCE?.let { database ->
                                CoroutineScope(Dispatchers.IO).launch {
                                    database.accountDao().insertAccount(
                                        AccountEntity(
                                            name = "Dompet",
                                            type = "CASH",
                                            balance = 0.0,
                                            color = "#006B7A",
                                            icon = "wallet",
                                            isDefault = true,
                                            createdAt = LocalDateTime.now().toString()
                                        )
                                    )
                                }
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
