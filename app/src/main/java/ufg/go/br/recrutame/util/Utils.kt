package ufg.go.br.recrutame.util

import android.app.Activity
import android.content.res.Resources
import android.graphics.Point
import android.util.DisplayMetrics
import android.os.Build
import android.view.WindowManager
import org.joda.time.LocalDate
import org.joda.time.Period
import java.text.Collator
import java.text.SimpleDateFormat
import java.util.*
import org.joda.time.PeriodType
import android.util.Patterns
import android.text.TextUtils
import ufg.go.br.recrutame.R

class Utils {
    companion object {
        fun dpToPx(dp: Int): Int {
            return (dp * Resources.getSystem().getDisplayMetrics().density).toInt()
        }

        fun getCurrentDate(): String{
            return getSimpleDateFormat().format(Calendar.getInstance().time)
        }

        fun convertToDate(date: String):Date{
            return getSimpleDateFormat().parse(date)
        }

        fun getSimpleDateFormat():SimpleDateFormat{
            return SimpleDateFormat("dd/MM/YYYY HH:mm:ss")
        }

        fun getDisplaySize(windowManager: WindowManager): Point {
            try {
                if (Build.VERSION.SDK_INT > 16) {
                    val display = windowManager.defaultDisplay
                    val displayMetrics = DisplayMetrics()
                    display.getMetrics(displayMetrics)
                    return Point(displayMetrics.widthPixels, displayMetrics.heightPixels)
                } else {
                    return Point(0, 0)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                return Point(0, 0)
            }

        }

        fun isNullOrWhiteSpace(value: String) : Boolean {
            return value.isEmpty() || value.trim().isEmpty()
        }

        fun getDayOfMonth(dateAsString: String): String {
            if (dateAsString.isEmpty()) {
                return ""
            }

            return dateAsString.substring(6, 8)
        }

        fun getMonth(dateAsString: String): String {
            if (dateAsString.isEmpty()) {
                return ""
            }

            return dateAsString.substring(4, 6)
        }

        fun getYear(dateAsString: String): String {
            if (dateAsString.isEmpty()) {
                return ""
            }

            return dateAsString.substring(0, 4)
        }

        fun getFormatedDate(date: Int?, dateFormat: String): String {
            if (date != null) {
                val dateAsString = date.toString()
                val dayOfMonth = Utils.getDayOfMonth(dateAsString)
                val monthOfYear = Utils.getMonth(dateAsString)
                val year = Utils.getYear(dateAsString)
               return getFormatedDate(year.toInt(), monthOfYear.toInt(), dayOfMonth.toInt(), dateFormat)
            }

            return ""
        }

        fun getFormatedDate(year: Int, monthOfYear: Int, dayOfMonth: Int, dateFormat: String): String {
            val calendar = GregorianCalendar(year, monthOfYear - 1, dayOfMonth)
            val fmt = SimpleDateFormat(dateFormat)
            fmt.calendar  = calendar
            return fmt.format(calendar.time)
        }

        fun getFullDateFromMonthYear(dateFormated: String): Int? {
            if (dateFormated.isEmpty()) {
                return null
            }

            val fullDateFormated = "01/$dateFormated"
            val dayOfMonth = fullDateFormated.substring(0, 2)
            val month = fullDateFormated.substring(3, 5)
            val year = fullDateFormated.substring(6, 10)
            return "$year$month$dayOfMonth".toInt()
        }

        fun getFullDate(dateFormated: String): Int? {
            if (dateFormated.isEmpty()) {
                return null
            }

            val dayOfMonth = dateFormated.substring(0, 2)
            val month = dateFormated.substring(3, 5)
            val year = dateFormated.substring(6, 10)
            return "$year$month$dayOfMonth".toInt()
        }

        fun orderList(list: List<String>) {
            val usCollator = Collator.getInstance(Locale.getDefault())
            usCollator.strength = Collator.PRIMARY
            Collections.sort(list, usCollator)
        }

        fun calculatePeriod(startFullDate: Int, endFullDate: Int?): Period {
            val dob = LocalDate(
                                getYear(startFullDate.toString()).toInt(),
                                getMonth(startFullDate.toString()).toInt(),
                                getDayOfMonth(startFullDate.toString()).toInt())
            val date: LocalDate = if (endFullDate == null) {
                LocalDate()
            } else {
                LocalDate(
                          getYear(endFullDate.toString()).toInt(),
                          getMonth(endFullDate.toString()).toInt(),
                          getDayOfMonth(endFullDate.toString()).toInt())
            }

            return Period(dob, date, PeriodType.yearMonthDay())
        }

        fun isValidEmail(email: CharSequence): Boolean {
            return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()
        }

        fun isValidUri(uri: CharSequence): Boolean {
            return !TextUtils.isEmpty(uri) && Patterns.WEB_URL.matcher(uri).matches()
        }

        fun getFromToAsText(startFullDate: Int, endFullDate: Int?, activity: Activity, showPeriod: Boolean): StringBuilder {
            val periodText = StringBuilder()
            periodText.append(Utils.getFormatedDate(startFullDate, activity.getString(R.string.format_date_no_day)))
            periodText.append(" - ")
            if (endFullDate != null){
                periodText.append(Utils.getFormatedDate(endFullDate, activity.getString(R.string.format_date_no_day)))
            } else {
                periodText.append(activity.getString(R.string.present))
            }

            if (showPeriod) {
                periodText.append(" ◔ ")

                val calculatedPeriod = Utils.calculatePeriod(startFullDate, endFullDate)
                periodText.append(calculatedPeriod.years)
                periodText.append(" ")
                periodText.append(activity.getString(R.string.years))
                if (calculatedPeriod.months > 0) {
                    periodText.append(" ")
                    periodText.append(calculatedPeriod.months)
                    periodText.append(" ")
                    periodText.append(activity.getString(R.string.months))
                }
            }

            return periodText
        }
    }
}