package com.example.myalarmmanager

import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myalarmmanager.databinding.ActivityMainBinding
import java.text.SimpleDateFormat
import java.util.Locale

class MainActivity : AppCompatActivity(), View.OnClickListener, DatePickerFragment.DialogDateListener, TimePickerFragment.DialogTimeListener {
    private var binding:ActivityMainBinding? = null
    private lateinit var alarmReceiver: AlarmReceiver
    companion object{
        private const val DATE_PICKER_TAG = "DatePicker"
        private const val TIME_PICKER_ONCE_TAG = "TimePickerOnce"
        private const val TIME_PICKER_REPEAT_TAG = "TimePickerRepeat"
    }
    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){
        isGranted: Boolean ->
        if (isGranted){
            Toast.makeText(this, "Notification permission granted", Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(this, "Notification permission rejected", Toast.LENGTH_SHORT).show()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        if (Build.VERSION.SDK_INT >= 33){
            requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        }

        binding?.btnOnceDate?.setOnClickListener(this)
        binding?.btnOnceTime?.setOnClickListener(this)
        binding?.btnSetOnceAlarm?.setOnClickListener(this)
        binding?.btnRepeatingTime?.setOnClickListener(this)
        binding?.btnSetRepeatingAlarm?.setOnClickListener(this)
        binding?.btnCancelRepeatingAlarm?.setOnClickListener(this)
        alarmReceiver = AlarmReceiver()
    }

    override fun onClick(v: View?) {
        when (v?.id){
            R.id.btn_once_time -> {
                val timePickerFragmentOne = TimePickerFragment()
                timePickerFragmentOne.show(supportFragmentManager, TIME_PICKER_ONCE_TAG)
            }
            R.id.btn_once_date -> {
                val datePickerFragment = DatePickerFragment()
                datePickerFragment.show(supportFragmentManager, DATE_PICKER_TAG)
            }
            R.id.btn_set_once_alarm -> {
                val onceTime = binding?.tvOnceTime?.text.toString()
                val onceDate = binding?.tvOnceDate?.text.toString()
                val message = binding?.edtOnceMessage?.text.toString()
                alarmReceiver.setOneTimeAlarm(this, AlarmReceiver.TYPE_ONE_TIME, onceDate, onceTime, message)
            }
            R.id.btn_repeating_time -> {
                val timePickerFragmentRepeat = TimePickerFragment()
                timePickerFragmentRepeat.show(supportFragmentManager, TIME_PICKER_REPEAT_TAG)
            }
            R.id.btn_set_repeating_alarm -> {
                val repeatTime = binding?.tvRepeatingTime?.text.toString()
                val repeatMessage = binding?.tvRepeatingMessage?.text.toString()
                alarmReceiver.setRepeatingAlarm(this, AlarmReceiver.TYPE_REPEATING, repeatTime, repeatMessage)
            }
            R.id.btn_cancel_repeating_alarm -> {
                alarmReceiver.cancelAlarm(this, AlarmReceiver.TYPE_REPEATING)
            }
        }
    }

    override fun onDialogDateSet(tag: String?, year: Int, month: Int, dayOfMonth: Int) {
        val calender = Calendar.getInstance()
        calender.set(year, month, dayOfMonth)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        binding?.tvOnceDate?.text = dateFormat.format(calender.time)
    }

    override fun onDialogTimeSet(tag: String?, hourOfDay: Int, minute: Int) {
        val calender = Calendar.getInstance()
        calender.set(Calendar.HOUR_OF_DAY, hourOfDay)
        calender.set(Calendar.MINUTE, minute)
        val dateFormatOneTime = SimpleDateFormat("HH:mm", Locale.getDefault())
        when (tag) {
            TIME_PICKER_ONCE_TAG -> binding?.tvOnceTime?.text =
                dateFormatOneTime.format(calender.time)

            TIME_PICKER_REPEAT_TAG -> binding?.tvRepeatingTime?.text = dateFormatOneTime.format(calender.time)
            else -> {
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}