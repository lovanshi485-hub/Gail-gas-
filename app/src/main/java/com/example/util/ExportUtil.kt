package com.example.util

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import androidx.core.content.FileProvider
import com.example.model.CustomerEntity
import java.io.File
import java.io.FileOutputStream
import java.io.FileWriter

object ExportUtil {

    /**
     * Generates a CSV/Excel-compatible spreadsheet for customer data.
     * Excel natively opens CSV with UTF-8 BOM, supporting Hindi/English characters.
     */
    fun exportCustomersToExcel(context: Context, customers: List<CustomerEntity>): File {
        val fileName = "GAIL_PMC_Customers_${System.currentTimeMillis()}.csv"
        val file = File(context.cacheDir, fileName)
        
        val writer = FileWriter(file)
        // Write UTF-8 BOM so Microsoft Excel correctly displays Hindi and special characters
        writer.write("\uFEFF")
        
        // Headers strictly matching user's GAIL Gas Excel structure + extended fields
        val headers = listOf(
            "Date", "Complaint Number", "BP Number", "Customer Name", "Address", "Mobile Number",
            "Request Type", "Registration Status", "Feasibility Status", "Department Status",
            "Project", "Gas-in Date", "Remarks", "House Number", "Ward", "Colony",
            "Employee Name", "Meter Number", "Meter Make", "Meter Reading", "Next Follow-up Date", "Sync State"
        )
        writer.append(headers.joinToString(","))
        writer.append("\n")

        for (c in customers) {
            val row = listOf(
                escapeCsv(c.date),
                escapeCsv(c.complaintNumber),
                escapeCsv(c.bpNumber),
                escapeCsv(c.customerName),
                escapeCsv(c.address),
                escapeCsv(c.mobileNumber),
                escapeCsv(c.requestType),
                escapeCsv(c.registrationStatus),
                escapeCsv(c.feasibilityStatus),
                escapeCsv(c.departmentStatus),
                escapeCsv(c.project),
                escapeCsv(c.gasInDate),
                escapeCsv(c.remarks),
                escapeCsv(c.houseNumber),
                escapeCsv(c.ward),
                escapeCsv(c.colony),
                escapeCsv(c.employeeName),
                escapeCsv(c.meterNumber),
                escapeCsv(c.meterMake),
                escapeCsv(c.meterReading),
                escapeCsv(c.nextFollowUpDate),
                escapeCsv(c.syncState)
            )
            writer.append(row.joinToString(","))
            writer.append("\n")
        }
        writer.flush()
        writer.close()
        return file
    }

    /**
     * Exports a Daily Progress Report to a clean PDF document using Android Native PdfDocument API.
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
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // Standard A4 points
        val page = document.startPage(pageInfo)
        val canvas = page.canvas

        val paint = Paint().apply { isAntiAlias = true }

        // Header Background (GAIL Gas Navy)
        paint.color = Color.rgb(11, 37, 69)
        canvas.drawRect(0f, 0f, 595f, 90f, paint)

        // Title
        paint.color = Color.WHITE
        paint.textSize = 18f
        paint.isFakeBoldText = true
        canvas.drawText("GAIL GAS PMC - DAILY PROGRESS REPORT (DPR)", 30f, 40f, paint)

        // Subtitle
        paint.textSize = 11f
        paint.isFakeBoldText = false
        paint.color = Color.rgb(255, 183, 3) // Gold accent
        canvas.drawText("Project: $project | Date: $reportDate | Prepared By: $preparedBy", 30f, 65f, paint)

        // Stats Box
        paint.color = Color.rgb(240, 243, 246)
        canvas.drawRoundRect(30f, 105f, 565f, 165f, 8f, 8f, paint)

        paint.color = Color.rgb(15, 23, 42)
        paint.textSize = 11f
        paint.isFakeBoldText = true
        canvas.drawText("Summary Metrics:", 45f, 125f, paint)
        paint.isFakeBoldText = false
        paint.textSize = 10f
        canvas.drawText(statsSummary, 45f, 145f, paint)

        // Content Header
        paint.textSize = 12f
        paint.isFakeBoldText = true
        paint.color = Color.rgb(11, 37, 69)
        canvas.drawText("Detailed Field Progress Log:", 30f, 195f, paint)

        // Content lines
        paint.textSize = 10f
        paint.isFakeBoldText = false
        paint.color = Color.rgb(30, 41, 59)

        var y = 220f
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
        canvas.drawText("Generated via GAIL Gas PMC Field Manager Pro • Confirmed & Authorized", 30f, 815f, paint)

        document.finishPage(page)

        val out = FileOutputStream(file)
        document.writeTo(out)
        document.close()
        out.close()

        return file
    }

    /**
     * Exports DPR to a DOCX-compatible XML formatted file readable by MS Word & WPS Office.
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
                    GAIL Gas PMC Field Manager Pro • System Generated Official Field Record
                </div>
            </body>
            </html>
        """.trimIndent()

        file.writeText(htmlContent, Charsets.UTF_8)
        return file
    }

    /**
     * Helper to share a file using Android Intent chooser with FileProvider.
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
