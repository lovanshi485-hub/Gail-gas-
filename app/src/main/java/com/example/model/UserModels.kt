package com.example.model

enum class UserRole {
    ADMIN,
    EMPLOYEE,
    GUEST
}

data class UserProfile(
    val id: String = "EMP-1042",
    val name: String = "Rajesh Sharma",
    val email: String = "rajesh.pmc@gailgas.co.in",
    val role: UserRole = UserRole.EMPLOYEE,
    val designation: String = "Senior PMC Field Supervisor",
    val employeeId: String = "GG-PMC-4482",
    val mobile: String = "+91 98765 43210",
    val assignedProject: String = "Bhopal City Gas Distribution Phase 2",
    val profilePhotoUrl: String? = null
)

enum class SyncState {
    OFFLINE,
    SYNC_PENDING,
    SYNCING,
    SYNCED,
    SYNC_FAILED
}

enum class FeasibilityStatus {
    FEASIBLE,
    NON_FEASIBLE,
    PENDING,
    REVISIT_REQUIRED
}

enum class RegistrationStatus {
    COMPLETED,
    PENDING,
    DOCUMENT_VERIFICATION,
    PAYMENT_PENDING
}

enum class TnfStatus {
    OPEN,
    ASSIGNED,
    UNDER_REVIEW,
    MATERIAL_PENDING,
    CLOSED
}

enum class GasInStatus {
    NOT_STARTED,
    SCHEDULED,
    IN_PROGRESS,
    COMPLETED,
    FAILED_TEST
}

enum class LanguageMode {
    ENGLISH,
    HINDI
}
