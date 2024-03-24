package com.app.news.utils

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import com.app.news.domain.model.ArticlesItem
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object Util {

    fun shareNews(context: Context, title: String?, newsUrl: String?) {
        if (title != null && newsUrl != null) {
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, "${title}\n${newsUrl}")
                type = "text/plain" // Set the MIME type to plain text
            }
            context.startActivity(Intent.createChooser(shareIntent, "Share News"))
        }
    }

    fun formatDate(inputTime: String): String {
        try {
            val inputFormatter = "yyyy-MM-dd'T'HH:mm:ss'Z'"
            val outputFormatter = "dd-MM-yyyy HH:mm"

            val inputFormat = SimpleDateFormat(inputFormatter, Locale.ENGLISH)
            inputFormat.timeZone = TimeZone.getTimeZone("UTC")
            val outputFormat = SimpleDateFormat(outputFormatter, Locale.ENGLISH)
            outputFormat.timeZone = TimeZone.getTimeZone("Asia/kolkata")
            var str: String?
            val date: Date? = inputFormat.parse(inputTime)
            str = date.let { date ->
                outputFormat.format(date)
            }
            return str ?: ""

        } catch (e: ParseException) {
            // Handle parsing exception
            e.printStackTrace()
        }
        return ""
    }
}