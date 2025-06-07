package com.example.baseappandroid.common

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.provider.MediaStore
import android.provider.Settings
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.util.Patterns
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.example.baseappandroid.R
import java.io.BufferedReader
import java.io.ByteArrayOutputStream
import java.io.InputStreamReader
import java.text.DateFormat
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern


object CommonUtils {
    var isFirstLogin = true

    fun isValidEmail(target: CharSequence): Boolean {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }

    @SuppressLint("HardwareIds")
    fun getID(context: Context): String? {
        return Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ANDROID_ID
        )
    }

    fun versionName(context: Context): String? {
        try {
            val pInfo: PackageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            return pInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return null
    }

    private fun getActivityRoot(window: Window): View {
        return (window.decorView.rootView.findViewById<View>(android.R.id.content) as ViewGroup).getChildAt(
            0
        )
    }

    fun getDPtoPX(context: Context, dp: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, dp,
            context.resources.displayMetrics
        ).toInt()
    }

    private const val KEYBOARD_VISIBLE_THRESHOLD_DP = 100f


    open interface KeyboardVisibilityEventListener {
        fun onVisibilityChanged(isOpen: Boolean)
    }


    fun getHeightScreen(context: Context): Int {
        val displayMetrics = DisplayMetrics()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val display = context.display
            display?.getRealMetrics(displayMetrics)
        } else {
            (context as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)
        }
        return displayMetrics.heightPixels
    }

    fun getWidthScreen(context: Context): Int {
        val displayMetrics = DisplayMetrics()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val display = context.display
            display?.getRealMetrics(displayMetrics)
        } else {
            (context as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)
        }
        return displayMetrics.widthPixels
    }

    fun setEventListener(
        activity: Activity,
        listener: KeyboardVisibilityEventListener?,
        window: Window
    ) {
        if (listener == null) {
            return
        }

        val activityRoot = getActivityRoot(window) ?: return
        activityRoot!!.viewTreeObserver
            .addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {

                private val r = Rect()

                private val visibleThreshold = Math.round(
                    getDPtoPX(
                        activity,
                        KEYBOARD_VISIBLE_THRESHOLD_DP
                    ).toFloat()
                )

                private var wasOpened = false

                override fun onGlobalLayout() {
                    activityRoot.getWindowVisibleDisplayFrame(r)

                    val heightDiff = activityRoot.rootView.height - r.height()

                    val isOpen = heightDiff > visibleThreshold
                    if (isOpen == wasOpened) {
                        return
                    }
                    wasOpened = isOpen
                    listener.onVisibilityChanged(isOpen)
                }
            })
    }

    fun getImageUri(context: Context, inImage: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.PNG, 50, bytes)
        val path = MediaStore.Images.Media.insertImage(context.contentResolver, inImage, "f5", null)
        return Uri.parse(path)
    }

    fun getStatusBarHeight(context: Context): Int {
        var result = 0
        val resourceId =
            context.resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = context.resources.getDimensionPixelSize(resourceId)
        }
        return result
    }

    fun convertMillieToHMmSs(millie: Long): String? {
        val seconds = millie / 1000
        val second = seconds % 60
        val minute = seconds / 60 % 60
        val hour = seconds / (60 * 60) % 24
        val result = ""
        return if (hour > 0) {
            String.format("%02d:%02d:%02d", hour, minute, second)
        } else {
            String.format("%02d:%02d", minute, second)
        }
    }

    fun setStatusBarColor(activity: Activity) {
        val window: Window = activity.window
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(activity, R.color.black)
        }
    }

    fun setStatusColorIcon(view: View) {
        view.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    }

    fun showCustomUI(activity: Activity) {
        activity.window.statusBarColor = 0x00000000
        activity.window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
    }

    fun pxFromDp(context: Context, dp: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, dp,
            context.resources.displayMetrics
        ).toInt()
    }


    fun setViewGroupCenter(viewGroup: ViewGroup) {

        Handler(Looper.getMainLooper()).postDelayed({
            val layoutParams =
                viewGroup.layoutParams as ConstraintLayout.LayoutParams
            layoutParams.topMargin =
                (getHeightScreen(viewGroup.context) - viewGroup.measuredHeight - getStatusBarHeight(
                    viewGroup.context
                )) / 2
            viewGroup.layoutParams = layoutParams
        }, INIT_VIEW_DELAY)
    }


    fun Activity.hideSoftKeyboard() {
        currentFocus?.let {
            val inputMethodManager = ContextCompat.getSystemService(
                this,
                InputMethodManager::class.java
            )
            inputMethodManager?.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }

    fun isValidIndianMobileNumber(s: String): Boolean {
        val p = Pattern.compile("((09|03|07|08|05)+([0-9]{8})\\b)")
        val m = p.matcher(s)
        return m.find() && m.group() == s
    }

    fun getFirstDayOfWeek(): String? {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        val date = calendar.time
        val dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        return dateFormat.format(date)
    }

    fun getLastDayOfWeek(): String? {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        calendar.add(Calendar.DATE, 6)
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        val date = calendar.time
        val dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        return dateFormat.format(date)
    }

    fun getFirstDayOfMonth(): String? {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        val date = calendar.time
        val dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        return dateFormat.format(date)
    }

    fun getLastDayOfMonth(): String? {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DATE))
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        val date = calendar.time
        val dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        return dateFormat.format(date)
    }

    fun getFirstDayOfYear(): String? {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_YEAR, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        val date = calendar.time
        val dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        return dateFormat.format(date)
    }

    fun getLastDayOfYear(): String? {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.MONTH, 11)
        calendar.set(Calendar.DATE, 31)
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        val date = calendar.time
        val dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        return dateFormat.format(date)
    }

    fun compareTwoDates(date1: String, date2: String): Boolean {
        val simpleDateFormat = SimpleDateFormat("dd-MM-yyyy")
        val d1 = simpleDateFormat.parse(date1)
        val d2 = simpleDateFormat.parse(date2)
        return when {
            d1 > d2 -> {
                false
            }

            else -> {
                true
            }
        }
    }

    fun formatMoney(edt: EditText) {
        edt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence,
                start: Int,
                before: Int,
                count: Int
            ) {
            }

            override fun afterTextChanged(s: Editable) {
                edt.removeTextChangedListener(this)
                try {
                    var originalString = s.toString()
                    if (originalString.contains(".")) {
                        originalString = originalString.replace(".", "")
                    }
                    val longValue = originalString.toLong()
                    val formatter =
                        NumberFormat.getInstance(Locale.US) as DecimalFormat
                    formatter.applyPattern("#,###,###,###")
                    val formattedString = formatter.format(longValue)
                    edt.setText(formattedString.replace(",", "."))
                    edt.setSelection(edt.text.length)

                } catch (e: NumberFormatException) {
                    e.printStackTrace()
                }
                edt.addTextChangedListener(this)
            }
        })
    }


    fun readFile(fileName: String, activity: Activity): String {
        var content: String = ""
        try {
            val bufferedReader =
                BufferedReader(InputStreamReader(activity.assets.open(fileName), "UTF-8"))
            var line: String = bufferedReader.readLine()
            while (line != null) {
                content += line
                line = bufferedReader.readLine()
            }
        } catch (e: Exception) {
        }
        return content
    }

    private val DOUBLE_PRESS_INTERVAL: Long = 200
    private var mLastClickTime: Long = 0
    open fun isDoubleClick(): Boolean {
        if (SystemClock.elapsedRealtime() - mLastClickTime < DOUBLE_PRESS_INTERVAL) {
            return true
        }
        mLastClickTime = SystemClock.elapsedRealtime()
        return false
    }

}