package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.model.CustomerEntity
import com.example.model.DprEntity
import com.example.model.FeasibilityEntity
import com.example.model.GasInEntity
import com.example.model.SiteVisitEntity
import com.example.model.TnfEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GailGasDao {

    // --- CUSTOMERS ---
    @Query("SELECT * FROM customers WHERE isArchived = 0 ORDER BY updatedAt DESC")
    fun getAllActiveCustomers(): Flow<List<CustomerEntity>>

    @Query("SELECT * FROM customers WHERE bpNumber = :bpNumber LIMIT 1")
    suspend fun getCustomerByBpNumber(bpNumber: String): CustomerEntity?

    @Query("SELECT * FROM customers WHERE bpNumber LIKE '%' || :query || '%' OR customerName LIKE '%' || :query || '%' OR mobileNumber LIKE '%' || :query || '%' OR houseNumber LIKE '%' || :query || '%'")
    fun searchCustomers(query: String): Flow<List<CustomerEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomer(customer: CustomerEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomers(customers: List<CustomerEntity>)

    @Update
    suspend fun updateCustomer(customer: CustomerEntity)

    @Delete
    suspend fun deleteCustomer(customer: CustomerEntity)

    @Query("SELECT * FROM customers WHERE bpNumber = :bpNumber")
    suspend fun findDuplicatesByBp(bpNumber: String): List<CustomerEntity>

    // --- SITE VISITS ---
    @Query("SELECT * FROM site_visits ORDER BY createdAt DESC")
    fun getAllVisits(): Flow<List<SiteVisitEntity>>

    @Query("SELECT * FROM site_visits WHERE visitDate = :date ORDER BY createdAt DESC")
    fun getVisitsByDate(date: String): Flow<List<SiteVisitEntity>>

    @Query("SELECT * FROM site_visits WHERE customerBpNumber = :bpNumber ORDER BY createdAt DESC")
    fun getVisitsByBp(bpNumber: String): Flow<List<SiteVisitEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVisit(visit: SiteVisitEntity): Long

    // --- FEASIBILITY ---
    @Query("SELECT * FROM feasibility_surveys ORDER BY createdAt DESC")
    fun getAllFeasibility(): Flow<List<FeasibilityEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeasibility(feasibility: FeasibilityEntity): Long

    @Update
    suspend fun updateFeasibility(feasibility: FeasibilityEntity)

    // --- TNF ---
    @Query("SELECT * FROM tnf_cases ORDER BY createdAt DESC")
    fun getAllTnfCases(): Flow<List<TnfEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTnf(tnf: TnfEntity): Long

    @Update
    suspend fun updateTnf(tnf: TnfEntity)

    // --- GAS-IN ---
    @Query("SELECT * FROM gas_in_records ORDER BY createdAt DESC")
    fun getAllGasInRecords(): Flow<List<GasInEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGasIn(gasIn: GasInEntity): Long

    @Update
    suspend fun updateGasIn(gasIn: GasInEntity)

    // --- DPR ---
    @Query("SELECT * FROM dpr_reports ORDER BY reportDate DESC, updatedAt DESC")
    fun getAllDprReports(): Flow<List<DprEntity>>

    @Query("SELECT * FROM dpr_reports WHERE reportDate = :date LIMIT 1")
    suspend fun getDprByDate(date: String): DprEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDpr(dpr: DprEntity): Long

    @Update
    suspend fun updateDpr(dpr: DprEntity)

    // --- OFFLINE & SYNC QUERIES ---
    @Query("SELECT COUNT(*) FROM customers WHERE syncState != 'SYNCED'")
    suspend fun getUnsyncedCustomerCount(): Int

    @Query("UPDATE customers SET syncState = 'SYNCED'")
    suspend fun markAllCustomersSynced()

    @Query("UPDATE site_visits SET syncState = 'SYNCED'")
    suspend fun markAllVisitsSynced()

    @Query("UPDATE feasibility_surveys SET syncState = 'SYNCED'")
    suspend fun markAllFeasibilitySynced()

    @Query("UPDATE tnf_cases SET syncState = 'SYNCED'")
    suspend fun markAllTnfSynced()

    @Query("UPDATE gas_in_records SET syncState = 'SYNCED'")
    suspend fun markAllGasInSynced()

    @Query("UPDATE dpr_reports SET syncState = 'SYNCED'")
    suspend fun markAllDprSynced()
}
