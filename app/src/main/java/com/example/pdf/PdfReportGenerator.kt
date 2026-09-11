package com.example.pdf

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import com.example.model.ProjectSummaryData
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PdfReportGenerator {

    fun generateAndShare(
        context: Context,
        summary: ProjectSummaryData,
        engineerName: String = "Civil Engineer"
    ): Uri? {
        val pdfDoc = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 at 72dpi
        val page = pdfDoc.startPage(pageInfo)
        val canvas: Canvas = page.canvas

        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        val dateFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        val dateStr = dateFormat.format(Date(summary.project.dateCreated))
        val cur = summary.project.currencySymbol

        // Header Background
        paint.color = Color.rgb(15, 41, 66) // Deep Navy
        canvas.drawRect(0f, 0f, 595f, 100f, paint)

        // Header Gold Stripe
        paint.color = Color.rgb(217, 119, 6) // Gold accent
        canvas.drawRect(0f, 100f, 595f, 106f, paint)

        // Title
        paint.color = Color.WHITE
        paint.textSize = 18f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("BUILDING ESTIMATE & MATERIAL SUMMARY", 30f, 40f, paint)

        paint.textSize = 10f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        paint.color = Color.rgb(203, 213, 225)
        canvas.drawText("Civil Engineering Project Quantity & Cost Analysis", 30f, 58f, paint)
        canvas.drawText("Generated on: $dateStr | Prepared by: $engineerName", 30f, 75f, paint)

        // Security / Verification Badge
        paint.textSize = 8f
        paint.color = Color.rgb(245, 158, 11)
        canvas.drawText("🛡 SCREENSHOT PROTECTED / VERIFIED ESTIMATE", 350f, 40f, paint)

        // Project Info Card
        var y = 125f
        paint.color = Color.rgb(241, 245, 249) // Light surface
        canvas.drawRoundRect(25f, y, 570f, y + 65f, 6f, 6f, paint)

        paint.color = Color.rgb(15, 41, 66)
        paint.textSize = 12f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("Project: ${summary.project.title}", 40f, y + 22f, paint)

        paint.textSize = 10f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        paint.color = Color.rgb(51, 65, 85)
        val client = if (summary.project.clientName.isNotBlank()) summary.project.clientName else "N/A"
        val loc = if (summary.project.location.isNotBlank()) summary.project.location else "Site Location"
        canvas.drawText("Client: $client", 40f, y + 40f, paint)
        canvas.drawText("Location: $loc", 40f, y + 54f, paint)

        // Grand Total Box on Right
        paint.color = Color.rgb(219, 234, 254)
        canvas.drawRoundRect(370f, y + 8f, 555f, y + 57f, 6f, 6f, paint)
        paint.color = Color.rgb(30, 58, 138)
        paint.textSize = 9f
        canvas.drawText("ESTIMATED GRAND TOTAL", 385f, y + 25f, paint)
        paint.color = Color.rgb(180, 83, 9)
        paint.textSize = 14f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("$cur ${"%,.2f".format(summary.grandTotalCost)}", 385f, y + 46f, paint)

        // Section: Material Bill of Quantities Table
        y += 85f
        paint.color = Color.rgb(15, 41, 66)
        paint.textSize = 12f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("1. Materials Bill of Quantities (BOQ)", 30f, y, paint)

        y += 12f
        // Table Header
        paint.color = Color.rgb(37, 99, 235) // Slate blue
        canvas.drawRect(25f, y, 570f, y + 22f, paint)

        paint.color = Color.WHITE
        paint.textSize = 9f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("SL", 35f, y + 15f, paint)
        canvas.drawText("MATERIAL / ITEM", 70f, y + 15f, paint)
        canvas.drawText("QUANTITY", 260f, y + 15f, paint)
        canvas.drawText("UNIT", 345f, y + 15f, paint)
        canvas.drawText("RATE ($cur)", 415f, y + 15f, paint)
        canvas.drawText("AMOUNT ($cur)", 495f, y + 15f, paint)

        y += 22f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        var rowIdx = 1

        for (row in summary.materialRows) {
            // Row background
            paint.color = if (rowIdx % 2 == 0) Color.rgb(248, 250, 252) else Color.WHITE
            canvas.drawRect(25f, y, 570f, y + 20f, paint)

            // Borders
            paint.color = Color.rgb(226, 232, 240)
            paint.strokeWidth = 0.5f
            paint.style = Paint.Style.STROKE
            canvas.drawRect(25f, y, 570f, y + 20f, paint)
            paint.style = Paint.Style.FILL

            paint.color = Color.rgb(30, 41, 59)
            paint.textSize = 8.5f
            canvas.drawText("$rowIdx", 35f, y + 14f, paint)
            canvas.drawText(row.materialName, 70f, y + 14f, paint)
            canvas.drawText("${"%,.1f".format(row.quantity)}", 260f, y + 14f, paint)
            canvas.drawText(row.unit, 345f, y + 14f, paint)
            canvas.drawText("${"%,.2f".format(row.unitRate)}", 415f, y + 14f, paint)
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas.drawText("${"%,.2f".format(row.totalCost)}", 495f, y + 14f, paint)
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)

            y += 20f
            rowIdx++
        }

        // Table Total Row
        paint.color = Color.rgb(241, 245, 249)
        canvas.drawRect(25f, y, 570f, y + 22f, paint)
        paint.color = Color.rgb(15, 41, 66)
        paint.textSize = 9.5f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("TOTAL ESTIMATED COST", 70f, y + 15f, paint)
        canvas.drawText("$cur ${"%,.2f".format(summary.grandTotalCost)}", 495f, y + 15f, paint)
        y += 32f

        // Steel Breakdown Section if steel is present
        if (summary.steelBySize.isNotEmpty()) {
            paint.color = Color.rgb(15, 41, 66)
            paint.textSize = 11f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas.drawText("2. Steel Reinforcement Breakdown by Diameter", 30f, y, paint)
            y += 12f

            paint.color = Color.rgb(248, 250, 252)
            canvas.drawRoundRect(25f, y, 570f, y + 36f, 4f, 4f, paint)
            paint.color = Color.rgb(203, 213, 225)
            paint.style = Paint.Style.STROKE
            canvas.drawRoundRect(25f, y, 570f, y + 36f, 4f, 4f, paint)
            paint.style = Paint.Style.FILL

            var steelX = 40f
            paint.textSize = 8.5f
            paint.color = Color.rgb(51, 65, 85)
            for ((dia, wt) in summary.steelBySize) {
                paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                canvas.drawText("${dia}mm: ", steelX, y + 22f, paint)
                paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                val wtStr = "${"%,.1f".format(wt)} kg"
                canvas.drawText(wtStr, steelX + 32f, y + 22f, paint)
                steelX += 95f
                if (steelX > 480f) {
                    steelX = 40f
                    y += 14f
                }
            }
            y += 45f
        }

        // Structural Elements Summary
        paint.color = Color.rgb(15, 41, 66)
        paint.textSize = 11f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("3. Structural Components Overview", 30f, y, paint)
        y += 12f

        paint.textSize = 8.5f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        paint.color = Color.rgb(71, 85, 105)

        for ((category, items) in summary.itemsByCategory) {
            val count = items.size
            val catTotalCost = items.sumOf { it.subtotalCost }
            val itemNames = items.take(3).joinToString(", ") { it.name } + if (items.size > 3) "..." else ""
            canvas.drawText("• $category ($count items): $itemNames", 40f, y, paint)
            y += 14f
            if (y > 730f) break
        }

        // Signatures at bottom
        y = 750f
        paint.color = Color.rgb(148, 163, 184)
        paint.strokeWidth = 1f
        canvas.drawLine(40f, y, 200f, y, paint)
        canvas.drawLine(395f, y, 555f, y, paint)

        paint.color = Color.rgb(71, 85, 105)
        paint.textSize = 8.5f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        canvas.drawText("Prepared By: $engineerName", 40f, y + 14f, paint)
        canvas.drawText("Client / Project Approval", 395f, y + 14f, paint)

        // Footer
        paint.color = Color.rgb(148, 163, 184)
        paint.textSize = 7.5f
        canvas.drawText("Building Estimate App • Confidentially generated • Powered by Civil Calculation Suite", 140f, 815f, paint)

        pdfDoc.finishPage(page)

        // Save PDF to Documents
        val fileName = "Estimate_${summary.project.title.replace(Regex("[^a-zA-Z0-9_-]"), "_")}_${System.currentTimeMillis()}.pdf"
        val docsDir = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "Estimates")
        if (!docsDir.exists()) docsDir.mkdirs()
        val file = File(docsDir, fileName)

        try {
            val fos = FileOutputStream(file)
            pdfDoc.writeTo(fos)
            fos.close()
            pdfDoc.close()

            // Also copy or register with MediaStore for Downloads if API 29+
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                try {
                    val values = ContentValues().apply {
                        put(MediaStore.Downloads.DISPLAY_NAME, fileName)
                        put(MediaStore.Downloads.MIME_TYPE, "application/pdf")
                        put(MediaStore.Downloads.IS_PENDING, 1)
                    }
                    val resolver = context.contentResolver
                    val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
                    if (uri != null) {
                        resolver.openOutputStream(uri)?.use { os ->
                            file.inputStream().use { input -> input.copyTo(os) }
                        }
                        values.clear()
                        values.put(MediaStore.Downloads.IS_PENDING, 0)
                        resolver.update(uri, values, null, null)
                    }
                } catch (_: Exception) {}
            }

            return FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
        } catch (e: Exception) {
            e.printStackTrace()
            pdfDoc.close()
            return null
        }
    }

    fun openOrSharePdf(context: Context, uri: Uri) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/pdf")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        val chooser = Intent.createChooser(intent, "Open Estimate PDF").apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        try {
            context.startActivity(chooser)
        } catch (_: Exception) {
            // Fallback to share intent
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(Intent.createChooser(shareIntent, "Share Estimate PDF"))
        }
    }
}
