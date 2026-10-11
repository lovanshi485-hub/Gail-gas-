package com.example.util

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import androidx.core.content.FileProvider
import com.example.model.CustomerEntity
import com.example.model.DprEntity
import com.example.model.FeasibilityEntity
import com.example.model.GasInEntity
import com.example.model.SiteVisitEntity
import java.io.File
import java.io.FileOutputStream
import java.io.FileWriter

object ExportUtil {

    /**
     * Generates an actual Excel-compatible spreadsheet (CSV with UTF-8 BOM).
     * Opens directly in Microsoft Excel, Google Sheets, LibreOffice, WPS.
     */
    fun exportCustomersToExcel(context: Context, customers: List<CustomerEntity>): File {
        val fileName = "GAIL_Customers_${System.currentTimeMillis()}.csv"
        val file = File(context.cacheDir, fileName)
        val writer = FileWriter(file)
        writer.write("\uFEFF") // UTF-8 BOM for Microsoft Excel

        val headers = listOf(
            "Date", "Complaint No", "BP Number", "Customer Name", "Mobile Number",
            "House No", "City", "Address", "Pincode", "Category", "Connection Type",
            "Feasibility Status", "Meter No", "Meter Make", "Meter Reading",
            "Burners", "Extra Pipe", "Gas-in Date", "Remarks", "JMR Attached"
        )
        writer.append(headers.joinToString(","))
        writer.append("\n")

        for (c in customers) {
            val row = listOf(
                escapeCsv(c.date),
                escapeCsv(c.complaintNumber),
                escapeCsv(c.bpNumber),
                escapeCsv(c.customerName),
                escapeCsv(c.mobileNumber),
                escapeCsv(c.houseNumber),
                escapeCsv(c.city),
                escapeCsv(c.address),
                escapeCsv(c.pincode),
                escapeCsv(c.requestType),
                escapeCsv(c.connectionType),
                escapeCsv(c.feasibilityStatus),
                escapeCsv(c.meterNumber),
                escapeCsv(c.meterMake),
                escapeCsv(c.meterReading),
                escapeCsv(c.burnersCount),
                escapeCsv(c.extraPipe),
                escapeCsv(c.gasInDate),
                escapeCsv(c.remarks),
                escapeCsv(if (!c.jmrPhotoUri.isNullOrBlank()) "Yes" else "No")
            )
            writer.append(row.joinToString(","))
            writer.append("\n")
        }
        writer.flush()
        writer.close()
        return file
    }

    /**
     * Exports Feasibility Surveys to a dedicated Excel Sheet
     */
    fun exportFeasibilityToExcel(context: Context, list: List<FeasibilityEntity>): File {
        val fileName = "GAIL_Feasibility_Surveys_${System.currentTimeMillis()}.csv"
        val file = File(context.cacheDir, fileName)
        val writer = FileWriter(file)
        writer.write("\uFEFF")

        val headers = listOf("Date", "BP Number", "Customer Name", "Mobile Number", "House No", "City", "Address", "Status", "Remarks", "Photo/Video Attached")
        writer.append(headers.joinToString(","))
        writer.append("\n")

        for (f in list) {
            val hasMedia = if (!f.photoUri.isNullOrBlank() || !f.videoUri.isNullOrBlank()) "Yes" else "No"
            val row = listOf(
                escapeCsv(f.visitDate),
                escapeCsv(f.bpNumber),
                escapeCsv(f.customerName),
                escapeCsv(f.mobileNumber),
                escapeCsv(f.houseNumber),
                escapeCsv(f.city),
                escapeCsv(f.address),
                escapeCsv(f.status),
                escapeCsv(f.remarks),
                escapeCsv(hasMedia)
            )
            writer.append(row.joinToString(","))
            writer.append("\n")
        }
        writer.flush()
        writer.close()
        return file
    }

    /**
     * Exports Gas-In Completed / Meter Commissioning Records to dedicated Excel Sheet
     */
    fun exportGasInToExcel(context: Context, list: List<GasInEntity>): File {
        val fileName = "GAIL_GasIn_Commissioning_${System.currentTimeMillis()}.csv"
        val file = File(context.cacheDir, fileName)
        val writer = FileWriter(file)
        writer.write("\uFEFF")

        val headers = listOf("Completion Date", "BP Number", "Customer Name", "Status", "Meter Number", "Meter Make", "Reading", "Leak Test", "Pressure Test", "Remarks", "JMR Attached", "Meter Photo Attached")
        writer.append(headers.joinToString(","))
        writer.append("\n")

        for (g in list) {
            val row = listOf(
                escapeCsv(g.completionDate),
                escapeCsv(g.bpNumber),
                escapeCsv(g.customerName),
                escapeCsv(g.gasInStatus),
                escapeCsv(g.meterNumber),
                escapeCsv(g.meterMake),
                escapeCsv(g.meterReading),
                escapeCsv(g.leakTestStatus),
                escapeCsv(g.pressureTestStatus),
                escapeCsv(g.remarks),
                escapeCsv(if (!g.jmrPhotoUri.isNullOrBlank()) "Yes" else "No"),
                escapeCsv(if (!g.meterPhotoUri.isNullOrBlank()) "Yes" else "No")
            )
            writer.append(row.joinToString(","))
            writer.append("\n")
        }
        writer.flush()
        writer.close()
        return file
    }

    /**
     * Exports Field Survey / Site Visits to dedicated Excel Sheet (.csv with UTF-8 BOM)
     */
    fun exportSiteVisitsToExcel(context: Context, list: List<SiteVisitEntity>): File {
        val fileName = "GAIL_Field_Visits_${System.currentTimeMillis()}.csv"
        val file = File(context.cacheDir, fileName)
        val writer = FileWriter(file)
        writer.write("\uFEFF")

        val headers = listOf("Visit Date", "Time", "BP Number", "Customer Name", "House No", "Project", "Officer", "Purpose", "Work Performed", "Outcome", "Remarks", "GPS Lat", "GPS Lng")
        writer.append(headers.joinToString(","))
        writer.append("\n")

        for (v in list) {
            val row = listOf(
                escapeCsv(v.visitDate),
                escapeCsv(v.visitTime),
                escapeCsv(v.customerBpNumber),
                escapeCsv(v.customerName),
                escapeCsv(v.houseNumber),
                escapeCsv(v.project),
                escapeCsv(v.employeeName),
                escapeCsv(v.visitPurpose),
                escapeCsv(v.workPerformed),
                escapeCsv(v.visitOutcome),
                escapeCsv(v.remarks),
                escapeCsv(v.latitude?.toString() ?: ""),
                escapeCsv(v.longitude?.toString() ?: "")
            )
            writer.append(row.joinToString(","))
            writer.append("\n")
        }
        writer.flush()
        writer.close()
        return file
    }

    /**
     * Exports Feasibility Surveys into a clean Official PDF Document
     */
    fun exportFeasibilityToPdf(context: Context, list: List<FeasibilityEntity>): File {
        val fileName = "GAIL_Feasibility_Report_${System.currentTimeMillis()}.pdf"
        val file = File(context.cacheDir, fileName)

        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = document.startPage(pageInfo)
        val canvas = page.canvas
        val paint = Paint().apply { isAntiAlias = true }

        // Navy Header
        paint.color = Color.rgb(11, 37, 69)
        canvas.drawRect(0f, 0f, 595f, 90f, paint)

        paint.color = Color.WHITE
        paint.textSize = 17f
        paint.isFakeBoldText = true
        canvas.drawText("GAIL GAS PMC - FEASIBILITY SURVEY REPORT", 30f, 40f, paint)

        paint.textSize = 11f
        paint.isFakeBoldText = false
        paint.color = Color.rgb(255, 183, 3)
        canvas.drawText("Total Surveys Cleared: ${list.size} • Generated: ${System.currentTimeMillis()}", 30f, 65f, paint)

        var y = 120f
        paint.color = Color.rgb(11, 37, 69)
        paint.textSize = 12f
        paint.isFakeBoldText = true
        canvas.drawText("Field Feasibility Registry:", 30f, y, paint)
        y += 24f

        paint.textSize = 9.5f
        paint.isFakeBoldText = false
        paint.color = Color.rgb(30, 41, 59)

        for (f in list.take(24)) {
            val hasMedia = if (!f.photoUri.isNullOrBlank() || !f.videoUri.isNullOrBlank()) " [Media Attached]" else ""
            val line = "• BP: ${f.bpNumber.ifBlank { "N/A" }} | ${f.customerName} | H.No: ${f.houseNumber} | Status: ${f.status} | City: ${f.city}$hasMedia"
            canvas.drawText(line.take(90), 30f, y, paint)
            y += 22f
        }

        paint.color = Color.rgb(100, 116, 139)
        paint.textSize = 9f
        canvas.drawText("Certified GAIL Gas PMC Technical Feasibility Record • Field Manager Pro", 30f, 815f, paint)

        document.finishPage(page)
        val out = FileOutputStream(file)
        document.writeTo(out)
        document.close()
        out.close()
        return file
    }

    /**
     * Exports Gas-In / JMR Commissioning into an Official PDF Document
     */
    fun exportGasInToPdf(context: Context, list: List<GasInEntity>): File {
        val fileName = "GAIL_GasIn_Commissioning_${System.currentTimeMillis()}.pdf"
        val file = File(context.cacheDir, fileName)

        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = document.startPage(pageInfo)
        val canvas = page.canvas
        val paint = Paint().apply { isAntiAlias = true }

        // Navy Header
        paint.color = Color.rgb(11, 37, 69)
        canvas.drawRect(0f, 0f, 595f, 90f, paint)

        paint.color = Color.WHITE
        paint.textSize = 17f
        paint.isFakeBoldText = true
        canvas.drawText("GAIL GAS PMC - GAS-IN COMMISSIONING REPORT", 30f, 40f, paint)

        paint.textSize = 11f
        paint.isFakeBoldText = false
        paint.color = Color.rgb(255, 183, 3)
        canvas.drawText("Total Live Gas-in Records: ${list.size} • Certified PMC Inspection", 30f, 65f, paint)

        var y = 120f
        paint.color = Color.rgb(11, 37, 69)
        paint.textSize = 12f
        paint.isFakeBoldText = true
        canvas.drawText("Commissioned Gas Meters & Pressure Verification:", 30f, y, paint)
        y += 24f

        paint.textSize = 9.5f
        paint.isFakeBoldText = false
        paint.color = Color.rgb(30, 41, 59)

        for (g in list.take(24)) {
            val jmr = if (!g.jmrPhotoUri.isNullOrBlank()) " [JMR Card]" else ""
            val line = "• BP: ${g.bpNumber} | ${g.customerName} | Meter: ${g.meterNumber} | Leak: ${g.leakTestStatus}$jmr"
            canvas.drawText(line.take(90), 30f, y, paint)
            y += 22f
        }

        paint.color = Color.rgb(100, 116, 139)
        paint.textSize = 9f
        canvas.drawText("GAIL Gas PMC Field Manager Pro • Certified Official Commissioning", 30f, 815f, paint)

        document.finishPage(page)
        val out = FileOutputStream(file)
        document.writeTo(out)
        document.close()
        out.close()
        return file
    }

    /**
     * Exports Site Visits / Survey into an Official PDF Document
     */
    fun exportSiteVisitsToPdf(context: Context, list: List<SiteVisitEntity>): File {
        val fileName = "GAIL_Field_Visits_Report_${System.currentTimeMillis()}.pdf"
        val file = File(context.cacheDir, fileName)

        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = document.startPage(pageInfo)
        val canvas = page.canvas
        val paint = Paint().apply { isAntiAlias = true }

        // Navy Header
        paint.color = Color.rgb(11, 37, 69)
        canvas.drawRect(0f, 0f, 595f, 90f, paint)

        paint.color = Color.WHITE
        paint.textSize = 17f
        paint.isFakeBoldText = true
        canvas.drawText("GAIL GAS PMC - FIELD VISITS & SURVEY REPORT", 30f, 40f, paint)

        paint.textSize = 11f
        paint.isFakeBoldText = false
        paint.color = Color.rgb(255, 183, 3)
        canvas.drawText("Total House Visits: ${list.size} • Unique Inspections", 30f, 65f, paint)

        var y = 120f
        paint.color = Color.rgb(11, 37, 69)
        paint.textSize = 12f
        paint.isFakeBoldText = true
        canvas.drawText("Site Visits Log:", 30f, y, paint)
        y += 24f

        paint.textSize = 9.5f
        paint.isFakeBoldText = false
        paint.color = Color.rgb(30, 41, 59)

        for (v in list.take(24)) {
            val line = "• ${v.visitDate} | BP: ${v.customerBpNumber} | ${v.customerName} | H.No: ${v.houseNumber} | ${v.visitOutcome}"
            canvas.drawText(line.take(90), 30f, y, paint)
            y += 22f
        }

        paint.color = Color.rgb(100, 116, 139)
        paint.textSize = 9f
        canvas.drawText("GAIL Gas PMC Field Manager Pro • Certified Daily Survey Log", 30f, 815f, paint)

        document.finishPage(page)
        val out = FileOutputStream(file)
        document.writeTo(out)
        document.close()
        out.close()
        return file
    }

    /**
     * Exports a Daily Progress Report to a genuine PDF document using Android Native PdfDocument API.
     */
    fun exportDprToPdf(
        context: Context,
        reportDate: String,
        project: String,
        preparedBy: String,
        content: String,
        statsSummary: String
    ): File {
        val fileName = "GAIL_PMC_DPR_${reportDate.replace("-", "_")}.pdf"
        val file = File(context.cacheDir, fileName)

        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = document.startPage(pageInfo)
        val canvas = page.canvas
        val paint = Paint().apply { isAntiAlias = true }

        // Navy Header Box
        paint.color = Color.rgb(11, 37, 69)
        canvas.drawRect(0f, 0f, 595f, 90f, paint)

        // Title
        paint.color = Color.WHITE
        paint.textSize = 17f
        paint.isFakeBoldText = true
        canvas.drawText("GAIL GAS PMC - DAILY PROGRESS REPORT (DPR)", 30f, 40f, paint)

        // Subtitle
        paint.textSize = 11f
        paint.isFakeBoldText = false
        paint.color = Color.rgb(255, 183, 3)
        canvas.drawText("Project: $project | Date: $reportDate | Prepared By: $preparedBy", 30f, 65f, paint)

        // Stats Summary Box
        paint.color = Color.rgb(240, 243, 246)
        canvas.drawRoundRect(30f, 105f, 565f, 160f, 8f, 8f, paint)

        paint.color = Color.rgb(15, 23, 42)
        paint.textSize = 11f
        paint.isFakeBoldText = true
        canvas.drawText("Operational Metrics:", 45f, 125f, paint)
        paint.isFakeBoldText = false
        paint.textSize = 10f
        canvas.drawText(statsSummary, 45f, 145f, paint)

        // Content
        paint.textSize = 12f
        paint.isFakeBoldText = true
        paint.color = Color.rgb(11, 37, 69)
        canvas.drawText("Field Progress Log & Remarks:", 30f, 185f, paint)

        paint.textSize = 10f
        paint.isFakeBoldText = false
        paint.color = Color.rgb(30, 41, 59)

        var y = 210f
        val lines = content.split("\n")
        for (line in lines) {
            if (y > 780f) break
            val truncated = if (line.length > 85) line.substring(0, 85) + "..." else line
            canvas.drawText(truncated, 30f, y, paint)
            y += 18f
        }

        // Footer
        paint.color = Color.rgb(100, 116, 139)
        paint.textSize = 9f
        canvas.drawText("Generated via GAIL Gas PMC Field Manager Pro • Certified & Verified", 30f, 815f, paint)

        document.finishPage(page)
        val out = FileOutputStream(file)
        document.writeTo(out)
        document.close()
        out.close()

        return file
    }

    /**
     * Generates a comprehensive Multi-Module All-in-One Field PDF Summary
     */
    fun exportComprehensiveSummaryPdf(
        context: Context,
        customers: List<CustomerEntity>,
        feasibilities: List<FeasibilityEntity>,
        gasInRecords: List<GasInEntity>
    ): File {
        val fileName = "GAIL_PMC_Comprehensive_Summary_${System.currentTimeMillis()}.pdf"
        val file = File(context.cacheDir, fileName)

        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = document.startPage(pageInfo)
        val canvas = page.canvas
        val paint = Paint().apply { isAntiAlias = true }

        // Header
        paint.color = Color.rgb(11, 37, 69)
        canvas.drawRect(0f, 0f, 595f, 85f, paint)
        paint.color = Color.WHITE
        paint.textSize = 18f
        paint.isFakeBoldText = true
        canvas.drawText("GAIL GAS PMC - FIELD OPERATIONS SUMMARY", 30f, 40f, paint)
        paint.textSize = 11f
        paint.color = Color.rgb(255, 183, 3)
        paint.isFakeBoldText = false
        canvas.drawText("Total Customers: ${customers.size} | Feasibility: ${feasibilities.size} | Gas-in Live: ${gasInRecords.size}", 30f, 65f, paint)

        var y = 110f
        paint.color = Color.rgb(11, 37, 69)
        paint.textSize = 13f
        paint.isFakeBoldText = true
        canvas.drawText("1. Customer & Feasibility Records", 30f, y, paint)
        y += 22f

        paint.textSize = 10f
        paint.isFakeBoldText = false
        paint.color = Color.rgb(30, 41, 59)
        for (f in feasibilities.take(12)) {
            val line = "• BP: ${f.bpNumber.ifBlank { "N/A" }} | ${f.customerName} | H.No: ${f.houseNumber} | Status: ${f.status} | City: ${f.city}"
            canvas.drawText(line.take(85), 30f, y, paint)
            y += 18f
        }

        y += 10f
        paint.color = Color.rgb(11, 37, 69)
        paint.textSize = 13f
        paint.isFakeBoldText = true
        canvas.drawText("2. Commissioned Gas-In & Meter Records", 30f, y, paint)
        y += 22f

        paint.textSize = 10f
        paint.isFakeBoldText = false
        paint.color = Color.rgb(30, 41, 59)
        for (g in gasInRecords.take(12)) {
            val line = "• BP: ${g.bpNumber} | ${g.customerName} | Meter: ${g.meterNumber} (${g.meterMake}) | Leak: ${g.leakTestStatus}"
            canvas.drawText(line.take(85), 30f, y, paint)
            y += 18f
        }

        paint.color = Color.rgb(100, 116, 139)
        paint.textSize = 9f
        canvas.drawText("GAIL Gas PMC Field Manager Pro • Certified Official Report", 30f, 815f, paint)

        document.finishPage(page)
        val out = FileOutputStream(file)
        document.writeTo(out)
        document.close()
        out.close()
        return file
    }

    /**
     * Exports DPR to a DOCX-compatible document readable in MS Word & WPS Office.
     */
    fun exportDprToDocx(
        context: Context,
        reportDate: String,
        project: String,
        preparedBy: String,
        content: String
    ): File {
        val fileName = "GAIL_PMC_DPR_${reportDate.replace("-", "_")}.doc"
        val file = File(context.cacheDir, fileName)

        val htmlContent = """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="utf-8">
                <title>GAIL Gas PMC DPR</title>
                <style>
                    body { font-family: Arial, sans-serif; margin: 40px; color: #1e293b; }
                    .header { background-color: #0b2545; color: white; padding: 20px; border-radius: 4px; }
                    h1 { margin: 0; font-size: 20px; }
                    .meta { color: #ffb703; font-size: 13px; margin-top: 8px; }
                    .content { margin-top: 30px; font-size: 13px; line-height: 1.6; }
                    .footer { margin-top: 50px; font-size: 11px; color: #64748b; border-top: 1px solid #cbd5e1; padding-top: 10px; }
                </style>
            </head>
            <body>
                <div class="header">
                    <h1>GAIL GAS PMC - DAILY PROGRESS REPORT (DPR)</h1>
                    <div class="meta">Project: $project | Date: $reportDate | Prepared By: $preparedBy</div>
                </div>
                <div class="content">
                    <pre style="white-space: pre-wrap; font-family: Arial, sans-serif;">$content</pre>
                </div>
                <div class="footer">
                    GAIL Gas PMC Field Manager Pro • Official Field Report
                </div>
            </body>
            </html>
        """.trimIndent()

        file.writeText(htmlContent, Charsets.UTF_8)
        return file
    }

    /**
     * Helper to open Android share chooser with FileProvider.
     */
    fun shareFile(context: Context, file: File, mimeType: String, title: String = "Share Document") {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = mimeType
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, title)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        val chooser = Intent.createChooser(intent, title).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(chooser)
    }

    private fun escapeCsv(value: String): String {
        var str = value.replace("\r", " ").replace("\n", " ")
        if (str.contains(",") || str.contains("\"")) {
            str = str.replace("\"", "\"\"")
            str = "\"$str\""
        }
        return str
    }
}
