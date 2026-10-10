package com.example.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "customers")
data class CustomerEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: String, // YYYY-MM-DD
    val complaintNumber: String = "",
    val bpNumber: String, // Business Partner No (e.g., 3400022314 from JMR)
    val customerName: String, // Name of Consumer (e.g., Devendra Kumar)
    val mobileNumber: String, // Mobile Number (e.g., 9910921088)
    val houseNumber: String = "", // House No. / Flat No. (e.g., FF-01)
    val address: String, // Address / Premise / Colony (e.g., Indus Realty Narmada)
    val city: String = "Mandideep", // City Name (e.g., Mandideep, Bhopal)
    val pincode: String = "462046", // Pincode
    val requestType: String = "Domestic", // Domestic / Commercial
    val connectionType: String = "New", // New / Reconnection
    val registrationStatus: String = RegistrationStatus.PENDING.name,
    // Status can be: Feasible, TNF, GI Pending, GC Pending, GI Done, GC Done
    val feasibilityStatus: String = "Feasible",
    val departmentStatus: String = "Survey Done",
    val project: String = "GAIL Gas Mandideep CGD",
    val gasInDate: String = "",
    val remarks: String = "",
    val ward: String = "",
    val colony: String = "",
    val employeeName: String = "",
    // JMR & Meter Details
    val meterNumber: String = "", // Meter No. (e.g., 21241466)
    val meterModel: String = "G1.6", // Model No.
    val meterMake: String = "Raychem RPG", // Meter Type / Make
    val meterReading: String = "0.022", // Initial Meter Reading
    val regulatorNo: String = "", // Regulator No.
    val burnersCount: String = "02", // Burner (e.g., 02)
    val extraPipe: String = "Nill", // Extra Pipe (e.g., Nill / 5m)
    val meterInstallDate: String = "",
    val nextFollowUpDate: String = "",
    val customFieldsJson: String = "{}",
    val latitude: Double? = null,
    val longitude: Double? = null,
    val jmrPhotoUri: String? = null, // Direct JMR document photo URI
    val meterPhotoUri: String? = null, // Direct Meter photo URI
    val photoUrisJson: String = "[]",
    val syncState: String = SyncState.SYNCED.name,
    val isArchived: Boolean = false,
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "site_visits")
data class SiteVisitEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val customerBpNumber: String,
    val customerName: String,
    val houseNumber: String = "",
    val city: String = "",
    val project: String = "",
    val visitDate: String, // YYYY-MM-DD
    val visitTime: String, // HH:mm
    val employeeName: String,
    val visitPurpose: String, // Feasibility, Registration, Metering, TNF, Complaint, Follow-up
    val workPerformed: String,
    val visitOutcome: String,
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
    val bpNumber: String,
    val customerName: String,
    val mobileNumber: String,
    val houseNumber: String = "",
    val address: String = "",
    val city: String = "",
    val project: String = "",
    val visitDate: String,
    // Status options: Feasible, TNF, GI Pending, GC Pending, GI Done, GC Done
    val status: String = "Feasible",
    val remarks: String = "",
    val syncState: String = SyncState.SYNCED.name,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "tnf_cases")
data class TnfEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val caseNumber: String,
    val bpNumber: String,
    val customerName: String,
    val mobileNumber: String,
    val address: String,
    val project: String,
    val category: String = "Customer Non-Availability",
    val assignedEmployee: String,
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
    val bpNumber: String,
    val customerName: String,
    val requestDate: String,
    val completionDate: String = "",
    val gasInStatus: String = GasInStatus.NOT_STARTED.name,
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
    val jmrPhotoUri: String? = null, // Optional JMR photo in Gas In
    val meterPhotoUri: String? = null, // Optional Meter photo in Gas In
    val syncState: String = SyncState.SYNCED.name,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "dpr_reports")
data class DprEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val reportDate: String, // YYYY-MM-DD
    val title: String,
    val project: String,
    val preparedBy: String,
    val formattedContentHtml: String,
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
