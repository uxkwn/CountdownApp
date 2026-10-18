package com.example.countdownapp

import android.Manifest
import android.app.DatePickerDialog
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColorInt
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.card.MaterialCardView
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputEditText
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    private lateinit var preferences: CountdownPreferences

    private lateinit var etEventTitle: TextInputEditText
    private lateinit var btnSelectDate: Button
    private lateinit var tvSelectedDate: TextView
    private lateinit var chipGroupColors: ChipGroup
    private lateinit var btnStartCountdown: Button
    private lateinit var btnCancelCountdown: Button
    private lateinit var cardActiveCountdown: MaterialCardView
    private lateinit var tvActiveCountdownTitle: TextView
    private lateinit var tvActiveCountdownTarget: TextView

    private var selectedTargetMillis: Long = 0L

    private val requestNotificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                applyCountdown()
            } else {
                Toast.makeText(
                    this,
                    getString(R.string.notification_permission_required),
                    Toast.LENGTH_LONG,
                ).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        preferences = CountdownPreferences(this)
        NotificationHelper.createNotificationChannel(this)

        initViews()
        setupListeners()
        updateUiFromPreferences()
    }

    private fun initViews() {
        etEventTitle = findViewById(R.id.etEventTitle)
        btnSelectDate = findViewById(R.id.btnSelectDate)
        tvSelectedDate = findViewById(R.id.tvSelectedDate)
        chipGroupColors = findViewById(R.id.chipGroupColors)
        btnStartCountdown = findViewById(R.id.btnStartCountdown)
        btnCancelCountdown = findViewById(R.id.btnCancelCountdown)
        cardActiveCountdown = findViewById(R.id.cardActiveCountdown)
        tvActiveCountdownTitle = findViewById(R.id.tvActiveCountdownTitle)
        tvActiveCountdownTarget = findViewById(R.id.tvActiveCountdownTarget)
    }

    private fun setupListeners() {
        btnSelectDate.setOnClickListener {
            showDatePicker()
        }

        chipGroupColors.setOnCheckedStateChangeListener { _, checkedIds ->
            val colorInt = if (checkedIds.isNotEmpty()) {
                getColorFromChipId(checkedIds.first())
            } else {
                0
            }
            preferences.textColorInt = colorInt

            if (preferences.isCountdownActive) {
                NotificationHelper.showCountdownNotification(
                    this,
                    preferences.eventTitle,
                    preferences.targetDateMillis,
                )
            }
        }

        btnStartCountdown.setOnClickListener {
            startOrUpdateCountdown()
        }

        btnCancelCountdown.setOnClickListener {
            cancelCountdown()
        }
    }

    private fun getColorFromChipId(chipId: Int): Int {
        return when (chipId) {
            R.id.chipColorWhite -> "#FFFFFF".toColorInt()
            R.id.chipColorBlack -> "#000000".toColorInt()
            R.id.chipColorRed -> "#E53935".toColorInt()
            R.id.chipColorBlue -> "#1E88E5".toColorInt()
            R.id.chipColorGreen -> "#43A047".toColorInt()
            R.id.chipColorOrange -> "#FB8C00".toColorInt()
            R.id.chipColorPurple -> "#8E24AA".toColorInt()
            else -> 0 // Default (0 = system theme default)
        }
    }

    private fun getChipIdFromColor(colorInt: Int): Int {
        return when (colorInt) {
            "#FFFFFF".toColorInt() -> R.id.chipColorWhite
            "#000000".toColorInt() -> R.id.chipColorBlack
            "#E53935".toColorInt() -> R.id.chipColorRed
            "#1E88E5".toColorInt() -> R.id.chipColorBlue
            "#43A047".toColorInt() -> R.id.chipColorGreen
            "#FB8C00".toColorInt() -> R.id.chipColorOrange
            "#8E24AA".toColorInt() -> R.id.chipColorPurple
            else -> R.id.chipColorDefault
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        if (selectedTargetMillis > 0L) {
            calendar.timeInMillis = selectedTargetMillis
        }

        val year = calendar[Calendar.YEAR]
        val month = calendar[Calendar.MONTH]
        val day = calendar[Calendar.DAY_OF_MONTH]

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedCalendar = Calendar.getInstance().apply {
                    set(Calendar.YEAR, selectedYear)
                    set(Calendar.MONTH, selectedMonth)
                    set(Calendar.DAY_OF_MONTH, selectedDay)
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                selectedTargetMillis = selectedCalendar.timeInMillis
                tvSelectedDate.text = CountdownUtils.formatDateString(selectedTargetMillis)
            },
            year,
            month,
            day,
        )
        datePickerDialog.show()
    }

    private fun startOrUpdateCountdown() {
        val title = etEventTitle.text.toString().trim()
        if (title.isEmpty()) {
            Toast.makeText(this, getString(R.string.please_enter_title), Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedTargetMillis == 0L) {
            Toast.makeText(this, getString(R.string.please_select_date), Toast.LENGTH_SHORT).show()
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS,
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                return
            }
        }

        applyCountdown()
    }

    private fun applyCountdown() {
        val title = etEventTitle.text.toString().trim()
        val checkedChipId = chipGroupColors.checkedChipId
        val selectedColor = getColorFromChipId(checkedChipId)

        preferences.saveCountdown(title, selectedTargetMillis, selectedColor)

        NotificationHelper.showCountdownNotification(this, title, selectedTargetMillis)
        CountdownWorker.scheduleDailyWorker(this)

        updateUiFromPreferences()
        Toast.makeText(this, "Countdown started!", Toast.LENGTH_SHORT).show()
    }

    private fun cancelCountdown() {
        preferences.clearCountdown()
        NotificationHelper.cancelNotification(this)
        CountdownWorker.cancelWorker(this)

        selectedTargetMillis = 0L
        etEventTitle.setText("")
        tvSelectedDate.text = getString(R.string.no_date_selected)
        chipGroupColors.check(R.id.chipColorDefault)

        updateUiFromPreferences()
        Toast.makeText(this, "Countdown cancelled", Toast.LENGTH_SHORT).show()
    }

    private fun updateUiFromPreferences() {
        val currentColor = preferences.textColorInt
        chipGroupColors.check(getChipIdFromColor(currentColor))

        if (preferences.isCountdownActive) {
            val title = preferences.eventTitle
            val targetMillis = preferences.targetDateMillis
            selectedTargetMillis = targetMillis

            etEventTitle.setText(title)
            tvSelectedDate.text = CountdownUtils.formatDateString(targetMillis)

            val daysRemaining = CountdownUtils.calculateDaysRemaining(targetMillis)
            val countdownText = CountdownUtils.formatCountdownText(title, daysRemaining)
            val dateText = "Target: " + CountdownUtils.formatDateString(targetMillis)

            tvActiveCountdownTitle.text = countdownText
            tvActiveCountdownTarget.text = dateText
            cardActiveCountdown.visibility = View.VISIBLE

            // Refresh notification content
            NotificationHelper.showCountdownNotification(this, title, targetMillis)
        } else {
            cardActiveCountdown.visibility = View.GONE
        }
    }
}
