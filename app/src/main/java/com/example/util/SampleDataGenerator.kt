package com.example.util

import com.example.model.CustomerEntity
import com.example.model.DprEntity
import com.example.model.FeasibilityEntity
import com.example.model.GasInEntity
import com.example.model.SiteVisitEntity
import com.example.model.TnfEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object SampleDataGenerator {

    private val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    fun generateInitialCustomers(): List<CustomerEntity> {
        return listOf(
            CustomerEntity(
                date = "2026-09-22",
                complaintNumber = "JMR-2026-001",
                bpNumber = "3400022314",
                customerName = "Devendra Kumar",
                houseNumber = "FF-01",
                address = "Indus Realty Narmada",
                city = "Mandideep",
                pincode = "462046",
                mobileNumber = "9910921088",
                requestType = "Domestic",
                connectionType = "New",
                registrationStatus = "COMPLETED",
                feasibilityStatus = "FEASIBLE",
                departmentStatus = "Gas-in Completed",
                project = "GAIL Gas Mandideep CGD",
                gasInDate = "2026-09-22",
                meterNumber = "21241466",
                meterModel = "G1.6",
                meterMake = "Raychem RPG",
                meterReading = "0.022",
                burnersCount = "02",
                extraPipe = "Nill",
                employeeName = "Pawan / Gaurav",
                remarks = "JMR card verified and signed. Flame test normal.",
                nextFollowUpDate = today
            ),
            CustomerEntity(
                date = today,
                complaintNumber = "JMR-2026-002",
                bpNumber = "3400022315",
                customerName = "Anita Deshmukh",
                houseNumber = "115",
                address = "Gulmohar Enclave, E-8",
                city = "Bhopal",
                pincode = "462016",
                mobileNumber = "9425098765",
                requestType = "Domestic",
                connectionType = "New",
                registrationStatus = "COMPLETED",
                feasibilityStatus = "FEASIBLE",
                departmentStatus = "Survey Done",
                project = "Bhopal CGD Project",
                remarks = "Customer ready for gas pipe connection.",
                nextFollowUpDate = today
            ),
            CustomerEntity(
                date = today,
                complaintNumber = "JMR-2026-003",
                bpNumber = "3400022316",
                customerName = "Mohammad Irfan",
                houseNumber = "RH-302",
                address = "Royal Heights, Shahpura",
                city = "Bhopal",
                pincode = "462039",
                mobileNumber = "9893112233",
                requestType = "Domestic",
                connectionType = "New",
                registrationStatus = "COMPLETED",
                feasibilityStatus = "FEASIBLE",
                departmentStatus = "Gas-in Completed",
                project = "Bhopal CGD Project",
                gasInDate = today,
                meterNumber = "21241467",
                meterModel = "G1.6",
                meterMake = "Raychem RPG",
                meterReading = "0.015",
                burnersCount = "02",
                extraPipe = "Nill",
                employeeName = "Rajesh Sharma",
                remarks = "Gas-in valve commissioned.",
                nextFollowUpDate = today
            )
        )
    }

    fun generateInitialVisits(): List<SiteVisitEntity> {
        return listOf(
            SiteVisitEntity(
                customerBpNumber = "3400022314",
                customerName = "Devendra Kumar",
                houseNumber = "FF-01",
                city = "Mandideep",
                project = "GAIL Gas Mandideep CGD",
                visitDate = today,
                visitTime = "11:30",
                employeeName = "Pawan",
                visitPurpose = "JMR Meter Installation",
                workPerformed = "Installed Raychem RPG G1.6 meter #21241466, reading 0.022. Burners tested: 02.",
                visitOutcome = "Completed & JMR Signed",
                remarks = "Owner signature obtained on JMR card."
            )
        )
    }

    fun generateInitialFeasibilities(): List<FeasibilityEntity> {
        return listOf(
            FeasibilityEntity(
                bpNumber = "3400022314",
                customerName = "Devendra Kumar",
                mobileNumber = "9910921088",
                houseNumber = "FF-01",
                address = "Indus Realty Narmada",
                city = "Mandideep",
                project = "GAIL Gas Mandideep CGD",
                visitDate = today,
                remarks = "Feasible - JMR conversion ready."
            )
        )
    }

    fun generateInitialTnf(): List<TnfEntity> {
        return emptyList()
    }

    fun generateInitialGasIn(): List<GasInEntity> {
        return listOf(
            GasInEntity(
                bpNumber = "3400022314",
                customerName = "Devendra Kumar",
                requestDate = today,
                completionDate = today,
                gasInStatus = "COMPLETED",
                meterNumber = "21241466",
                meterMake = "Raychem RPG G1.6",
                meterReading = "0.022",
                meterInstallationDate = today,
                regulatorDetails = "Standard 21 mbar",
                leakTestStatus = "Passed",
                pressureTestStatus = "21 mbar OK",
                remarks = "JMR verified",
                employeeName = "Pawan"
            )
        )
    }

    fun generateInitialDpr(): DprEntity {
        return DprEntity(
            reportDate = today,
            title = "GAIL Gas PMC Field Progress - Mandideep & Bhopal",
            project = "GAIL Gas Mandideep CGD",
            preparedBy = "Pawan / Rajesh",
            formattedContentHtml = "1. Feasibility & JMR completed for consumer Devendra Kumar (BP: 3400022314) at Indus Realty Narmada, Mandideep.",
            totalHousesVisited = 1,
            uniqueCustomers = 1,
            feasibleCount = 1,
            nonFeasibleCount = 0,
            registrationCount = 1,
            gasInCount = 1,
            tnfCount = 0,
            isDraft = false
        )
    }
}
