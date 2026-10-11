package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.model.CustomerEntity
import com.example.model.DprEntity
import com.example.model.FeasibilityEntity
import com.example.model.GasInEntity
import com.example.model.SiteVisitEntity
import com.example.model.TnfEntity

@Database(
    entities = [
        CustomerEntity::class,
        SiteVisitEntity::class,
        FeasibilityEntity::class,
        TnfEntity::class,
        GasInEntity::class,
        DprEntity::class
    ],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun gailGasDao(): GailGasDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "gail_gas_pmc.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
