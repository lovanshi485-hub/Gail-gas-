package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.model.CustomerEntity
import com.example.model.DprEntity
import com.example.model.FeasibilityEntity
import com.example.model.GasInEntity
import com.example.model.LanguageMode
import com.example.model.SiteVisitEntity
import com.example.model.SyncState
import com.example.model.TnfEntity
import com.example.model.UserProfile
import com.example.model.UserRole
import com.example.util.SampleDataGenerator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class GailGasViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val dao = db.gailGasDao()

    // Current User Session
    private val _currentUser = MutableStateFlow(
        UserProfile(
            id = "EMP-1042",
            name = "Rajesh Sharma",
            email = "rajesh.pmc@gailgas.co.in",
            role = UserRole.SUPERVISOR,
            designation = "Senior PMC Field Supervisor",
            employeeId = "GG-PMC-4482",
            mobile = "9826012345",
            assignedProject = "Mandideep & Bhopal CGD"
        )
    )
    val currentUser: StateFlow<UserProfile> = _currentUser.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(true)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    // App Preferences
    private val _languageMode = MutableStateFlow(LanguageMode.ENGLISH)
    val languageMode: StateFlow<LanguageMode> = _languageMode.asStateFlow()

    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    private val _fontSizeScale = MutableStateFlow(1.0f)
    val fontSizeScale: StateFlow<Float> = _fontSizeScale.asStateFlow()

    // Global Sync State
    private val _syncState = MutableStateFlow(SyncState.SYNCED)
    val syncState: StateFlow<SyncState> = _syncState.asStateFlow()

    private val _lastSyncTimestamp = MutableStateFlow(System.currentTimeMillis())
    val lastSyncTimestamp: StateFlow<Long> = _lastSyncTimestamp.asStateFlow()

    // Search query for customer directory
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Selected project filter for dashboard
    private val _selectedProjectFilter = MutableStateFlow("All Projects")
    val selectedProjectFilter: StateFlow<String> = _selectedProjectFilter.asStateFlow()

    // Database Flows
    val allCustomers: StateFlow<List<CustomerEntity>> = dao.getAllActiveCustomers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allVisits: StateFlow<List<SiteVisitEntity>> = dao.getAllVisits()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allFeasibilities: StateFlow<List<FeasibilityEntity>> = dao.getAllFeasibility()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allTnfCases: StateFlow<List<TnfEntity>> = dao.getAllTnfCases()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allGasInRecords: StateFlow<List<GasInEntity>> = dao.getAllGasInRecords()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allDprReports: StateFlow<List<DprEntity>> = dao.getAllDprReports()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered Customers based on search
    val filteredCustomers: StateFlow<List<CustomerEntity>> = combine(
        allCustomers,
        searchQuery
    ) { list, query ->
        if (query.isBlank()) {
            list
        } else {
            val q = query.lowercase().trim()
            list.filter {
                it.bpNumber.lowercase().contains(q) ||
                it.customerName.lowercase().contains(q) ||
                it.mobileNumber.contains(q) ||
                it.houseNumber.lowercase().contains(q) ||
                it.colony.lowercase().contains(q)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        // App starts with clean empty state - no pre-saved data, user enters their own data
    }

    /**
     * Purges all locally stored records for a clean slate.
     */
    fun clearAllData(onComplete: () -> Unit = {}) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.clearCustomers()
            dao.clearVisits()
            dao.clearFeasibility()
            dao.clearTnf()
            dao.clearGasIn()
            dao.clearDpr()
            launch(Dispatchers.Main) { onComplete() }
        }
    }

    /**
     * Optional sample dataset loader for testing or demonstration.
     */
    fun loadSampleData(onComplete: () -> Unit = {}) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.insertCustomers(SampleDataGenerator.generateInitialCustomers())
            SampleDataGenerator.generateInitialVisits().forEach { dao.insertVisit(it) }
            SampleDataGenerator.generateInitialFeasibilities().forEach { dao.insertFeasibility(it) }
            SampleDataGenerator.generateInitialTnf().forEach { dao.insertTnf(it) }
            SampleDataGenerator.generateInitialGasIn().forEach { dao.insertGasIn(it) }
            dao.insertDpr(SampleDataGenerator.generateInitialDpr())
            launch(Dispatchers.Main) { onComplete() }
        }
    }

    // --- AUTHENTICATION & PROFILE ACTIONS ---
    fun loginWithRole(role: UserRole, identifier: String = "", name: String = "") = login(role, identifier, name)

    fun login(role: UserRole, identifier: String = "", name: String = "") {
        when (role) {
            UserRole.ADMIN -> {
                _currentUser.value = UserProfile(
                    id = "ADM-001",
                    name = "Virendra Singh",
                    email = if (identifier.contains("@")) identifier else "admin.pmc@gailgas.co.in",
                    role = UserRole.ADMIN,
                    designation = "PMC Chief Project Manager",
                    employeeId = "GG-PMC-ADMIN-01",
                    assignedProject = "Madhya Pradesh CGD Operations"
                )
            }
            UserRole.SUPERVISOR -> {
                _currentUser.value = UserProfile(
                    id = "SUP-1042",
                    name = if (name.isNotBlank()) name else "Rajesh Sharma",
                    email = if (identifier.contains("@")) identifier else "rajesh.pmc@gailgas.co.in",
                    role = UserRole.SUPERVISOR,
                    designation = "Senior PMC Field Supervisor",
                    employeeId = "GG-PMC-4482",
                    mobile = if (identifier.all { it.isDigit() } && identifier.isNotBlank()) identifier else "9826012345",
                    assignedProject = "Mandideep & Bhopal CGD"
                )
            }
            UserRole.FIELD_ENGINEER -> {
                _currentUser.value = UserProfile(
                    id = "ENG-2019",
                    name = if (name.isNotBlank()) name else "Pawan / Gaurav",
                    email = if (identifier.contains("@")) identifier else "field.survey@gailgas.co.in",
                    role = UserRole.FIELD_ENGINEER,
                    designation = "Field Gas & JMR Engineer",
                    employeeId = "GG-ENG-2019",
                    mobile = if (identifier.all { it.isDigit() } && identifier.isNotBlank()) identifier else "9910921088",
                    assignedProject = "Mandideep Industrial & Domestic"
                )
            }
        }
        _isLoggedIn.value = true
    }

    fun registerNewOfficer(name: String, mobile: String, email: String, designation: String, project: String) {
        _currentUser.value = UserProfile(
            id = "REG-" + (1000..9999).random(),
            name = name,
            email = email,
            role = UserRole.SUPERVISOR,
            designation = designation,
            employeeId = "GG-PMC-" + (1000..9999).random(),
            mobile = mobile,
            assignedProject = project
        )
        _isLoggedIn.value = true
    }

    fun logout() {
        _isLoggedIn.value = false
    }

    fun updateProfile(name: String, mobile: String, designation: String, project: String) {
        _currentUser.value = _currentUser.value.copy(
            name = name,
            mobile = mobile,
            designation = designation,
            assignedProject = project
        )
    }

    fun toggleLanguage() {
        _languageMode.value = if (_languageMode.value == LanguageMode.ENGLISH) {
            LanguageMode.HINDI
        } else {
            LanguageMode.ENGLISH
        }
    }

    fun toggleTheme() {
        _isDarkMode.value = !_isDarkMode.value
    }

    fun setFontSizeScale(scale: Float) {
        _fontSizeScale.value = scale
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setProjectFilter(project: String) {
        _selectedProjectFilter.value = project
    }

    // --- DATA MUTATIONS ---
    fun saveCustomer(customer: CustomerEntity, onComplete: (Long) -> Unit = {}) {
        viewModelScope.launch(Dispatchers.IO) {
            val syncState = if (_syncState.value == SyncState.OFFLINE) SyncState.SYNC_PENDING.name else SyncState.SYNCED.name
            val toSave = customer.copy(
                syncState = syncState,
                updatedAt = System.currentTimeMillis()
            )
            val id = if (toSave.id == 0L) {
                dao.insertCustomer(toSave)
            } else {
                dao.updateCustomer(toSave)
                toSave.id
            }
            if (_syncState.value != SyncState.OFFLINE) {
                _syncState.value = SyncState.SYNC_PENDING
                triggerSync()
            }
            launch(Dispatchers.Main) { onComplete(id) }
        }
    }

    fun deleteCustomer(customer: CustomerEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.deleteCustomer(customer)
        }
    }

    fun deleteSiteVisit(visit: SiteVisitEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.deleteVisit(visit)
        }
    }

    fun deleteFeasibility(feasibility: FeasibilityEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.deleteFeasibility(feasibility)
        }
    }

    fun deleteTnf(tnf: TnfEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.deleteTnf(tnf)
        }
    }

    fun deleteGasIn(gasIn: GasInEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.deleteGasIn(gasIn)
        }
    }

    fun deleteDpr(dpr: DprEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.deleteDpr(dpr)
        }
    }

    fun recordSiteVisit(visit: SiteVisitEntity, onComplete: () -> Unit = {}) {
        viewModelScope.launch(Dispatchers.IO) {
            val sync = if (_syncState.value == SyncState.OFFLINE) SyncState.SYNC_PENDING.name else SyncState.SYNCED.name
            dao.insertVisit(visit.copy(syncState = sync))
            launch(Dispatchers.Main) { onComplete() }
        }
    }

    fun saveFeasibility(feasibility: FeasibilityEntity, onComplete: () -> Unit = {}) {
        viewModelScope.launch(Dispatchers.IO) {
            val sync = if (_syncState.value == SyncState.OFFLINE) SyncState.SYNC_PENDING.name else SyncState.SYNCED.name
            if (feasibility.id == 0L) {
                dao.insertFeasibility(feasibility.copy(syncState = sync))
            } else {
                dao.updateFeasibility(feasibility.copy(syncState = sync))
            }
            // Update parent customer status if present, or create a customer record if new
            val cust = dao.getCustomerByBpNumber(feasibility.bpNumber)
            if (cust != null) {
                dao.updateCustomer(cust.copy(
                    feasibilityStatus = feasibility.status,
                    houseNumber = if (cust.houseNumber.isBlank()) feasibility.houseNumber else cust.houseNumber,
                    city = if (cust.city.isBlank()) feasibility.city else cust.city,
                    remarks = feasibility.remarks
                ))
            } else {
                // Auto create customer record from this feasibility survey
                dao.insertCustomer(CustomerEntity(
                    date = feasibility.visitDate,
                    bpNumber = feasibility.bpNumber,
                    customerName = feasibility.customerName,
                    mobileNumber = feasibility.mobileNumber,
                    houseNumber = feasibility.houseNumber,
                    address = feasibility.address,
                    city = feasibility.city,
                    project = feasibility.project,
                    feasibilityStatus = feasibility.status,
                    departmentStatus = "Survey Completed",
                    remarks = feasibility.remarks,
                    syncState = sync
                ))
            }
            launch(Dispatchers.Main) { onComplete() }
        }
    }

    fun saveTnf(tnf: TnfEntity, onComplete: () -> Unit = {}) {
        viewModelScope.launch(Dispatchers.IO) {
            val sync = if (_syncState.value == SyncState.OFFLINE) SyncState.SYNC_PENDING.name else SyncState.SYNCED.name
            if (tnf.id == 0L) {
                dao.insertTnf(tnf.copy(syncState = sync))
            } else {
                dao.updateTnf(tnf.copy(syncState = sync))
            }
            val cust = dao.getCustomerByBpNumber(tnf.bpNumber)
            cust?.let {
                dao.updateCustomer(it.copy(
                    departmentStatus = "TNF - ${tnf.category}",
                    remarks = tnf.remarks
                ))
            }
            launch(Dispatchers.Main) { onComplete() }
        }
    }

    fun saveGasIn(gasIn: GasInEntity, onComplete: () -> Unit = {}) {
        viewModelScope.launch(Dispatchers.IO) {
            val sync = if (_syncState.value == SyncState.OFFLINE) SyncState.SYNC_PENDING.name else SyncState.SYNCED.name
            if (gasIn.id == 0L) {
                dao.insertGasIn(gasIn.copy(syncState = sync))
            } else {
                dao.updateGasIn(gasIn.copy(syncState = sync))
            }
            val cust = dao.getCustomerByBpNumber(gasIn.bpNumber)
            cust?.let {
                dao.updateCustomer(it.copy(
                    gasInDate = gasIn.completionDate,
                    meterNumber = gasIn.meterNumber,
                    meterMake = gasIn.meterMake,
                    meterReading = gasIn.meterReading,
                    meterInstallDate = gasIn.meterInstallationDate,
                    departmentStatus = "Gas-in Completed"
                ))
            }
            launch(Dispatchers.Main) { onComplete() }
        }
    }

    fun saveDpr(dpr: DprEntity, onComplete: () -> Unit = {}) {
        viewModelScope.launch(Dispatchers.IO) {
            val sync = if (_syncState.value == SyncState.OFFLINE) SyncState.SYNC_PENDING.name else SyncState.SYNCED.name
            if (dpr.id == 0L) {
                dao.insertDpr(dpr.copy(syncState = sync))
            } else {
                dao.updateDpr(dpr.copy(syncState = sync))
            }
            launch(Dispatchers.Main) { onComplete() }
        }
    }

    // --- SYNC ACTIONS ---
    fun toggleOfflineMode() {
        if (_syncState.value == SyncState.OFFLINE) {
            triggerSync()
        } else {
            _syncState.value = SyncState.OFFLINE
        }
    }

    fun triggerSync() {
        viewModelScope.launch(Dispatchers.IO) {
            _syncState.value = SyncState.SYNCING
            // Simulate enterprise cloud synchronization to secure GAIL Gas PMC server
            delay(1200)
            dao.markAllCustomersSynced()
            dao.markAllVisitsSynced()
            dao.markAllFeasibilitySynced()
            dao.markAllTnfSynced()
            dao.markAllGasInSynced()
            dao.markAllDprSynced()
            _lastSyncTimestamp.value = System.currentTimeMillis()
            _syncState.value = SyncState.SYNCED
        }
    }

    fun importDemoExcelBatch() {
        viewModelScope.launch(Dispatchers.IO) {
            val batch = SampleDataGenerator.generateInitialCustomers()
            dao.insertCustomers(batch)
            triggerSync()
        }
    }
}
