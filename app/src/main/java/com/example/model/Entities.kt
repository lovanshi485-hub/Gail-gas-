package com.example.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "customers")
data class CustomerEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: String, // YYYY-MM-DD
    val complaintNumber: String = "",
    val bpNumber: String = "", // Not mandatory
    val customerName: String = "", // Not mandatory
    val mobileNumber: String = "",
    val houseNumber: String = "",
    val address: String = "",
    val city: String = "Mandideep",
    val pincode: String = "462046",
    val requestType: String = "Domestic",
    val connectionType: String = "New",
    val registrationStatus: String = RegistrationStatus.PENDING.name,
    // Can contain multiple comma-separated statuses like "Feasible, GI Pending"
    val feasibilityStatus: String = "Feasible",
    val departmentStatus: String = "Survey Done",
    val project: String = "GAIL Gas Mandideep CGD",
    val gasInDate: String = "",
    val remarks: String = "",
    val ward: String = "",
    val colony: String = "",
    val employeeName: String = "",
    // JMR & Meter Details
    val meterNumber: String = "",
    val meterModel: String = "G1.6",
    val meterMake: String = "Raychem RPG",
    val meterReading: String = "0.022",
    val regulatorNo: String = "",
    val burnersCount: String = "02",
    val extraPipe: String = "Nill",
    val meterInstallDate: String = "",
    val nextFollowUpDate: String = "",
    val customFieldsJson: String = "{}",
    val latitude: Double? = null,
    val longitude: Double? = null,
    val jmrPhotoUri: String? = null,
    val meterPhotoUri: String? = null,
    val photoUrisJson: String = "[]",
    val syncState: String = SyncState.SYNCED.name,
    val isArchived: Boolean = false,
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "site_visits")
data class SiteVisitEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val customerBpNumber: String = "",
    val customerName: String = "",
    val houseNumber: String = "",
    val city: String = "",
    val project: String = "",
    val visitDate: String, // YYYY-MM-DD
    val visitTime: String, // HH:mm
    val employeeName: String = "",
    val visitPurpose: String = "",
    val workPerformed: String = "",
    val visitOutcome: String = "",
    val remarks: String = "",
    val nextVisitDate: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null,
    val distanceFromPrevVisitMeters: Double? = null,
    val photoUri: String? = null,
    val videoUri: String? = null,
    val syncState: String = SyncState.SYNCED.name,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "feasibility_surveys")
data class FeasibilityEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val bpNumber: String = "", // None mandatory
    val customerName: String = "",
    val mobileNumber: String = "",
    val houseNumber: String = "",
    val address: String = "",
    val city: String = "",
    val project: String = "",
    val visitDate: String,
    // Comma-separated multi-select statuses e.g. "Feasible, GI Done, GC Pending"
    val status: String = "Feasible",
    val remarks: String = "",
    val photoUri: String? = null, // Optional Photo
    val videoUri: String? = null, // Optional Video
    val syncState: String = SyncState.SYNCED.name,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "tnf_cases")
data class TnfEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val caseNumber: String = "",
    val bpNumber: String = "",
    val customerName: String = "",
    val mobileNumber: String = "",
    val address: String = "",
    val project: String = "",
    val category: String = "Customer Non-Availability",
    val assignedEmployee: String = "",
    val status: String = TnfStatus.OPEN.name,
    val remarks: String = "",
    val followUpDate: String = "",
    val photoUri: String? = null,
    val syncState: String = SyncState.SYNCED.name,
    val createdAt: Long = System.currentTimeMillis(),
    val closedAt: Long? = null
)

@Entity(tableName = "gas_in_records")
data class GasInEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val bpNumber: String = "",
    val customerName: String = "",
    val requestDate: String = "",
    val completionDate: String = "",
    val gasInStatus: String = GasInStatus.COMPLETED.name,
    val meterNumber: String = "",
    val meterMake: String = "",
    val meterReading: String = "",
    val meterInstallationDate: String = "",
    val regulatorDetails: String = "",
    val leakTestStatus: String = "Passed",
    val pressureTestStatus: String = "21 mbar OK",
    val remarks: String = "",
    val employeeName: String = "",
    val photoUri: String? = null,
    val jmrPhotoUri: String? = null, // Optional JMR photo
    val meterPhotoUri: String? = null, // Optional Meter photo
    val syncState: String = SyncState.SYNCED.name,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "dpr_reports")
data class DprEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val reportDate: String, // YYYY-MM-DD
    val title: String = "",
    val project: String = "",
    val preparedBy: String = "",
    val formattedContentHtml: String = "",
    val totalHousesVisited: Int = 0,
    val uniqueCustomers: Int = 0,
    val feasibleCount: Int = 0,
    val nonFeasibleCount: Int = 0,
    val registrationCount: Int = 0,
    val gasInCount: Int = 0,
    val tnfCount: Int = 0,
    val photoUri: String? = null,
    val isDraft: Boolean = false,
    val syncState: String = SyncState.SYNCED.name,
    val updatedAt: Long = System.currentTimeMillis()
)
