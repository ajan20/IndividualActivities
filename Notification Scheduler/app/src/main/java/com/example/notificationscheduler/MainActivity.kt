package com.example.notificationscheduler

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.os.Bundle
import android.widget.Button
import android.widget.RadioGroup
import android.widget.SeekBar
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private var mScheduler: JobScheduler? = null
    private val JOB_ID = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val scheduleButton = findViewById<Button>(R.id.scheduleButton)
        val cancelButton = findViewById<Button>(R.id.cancelButton)
        val seekBar = findViewById<SeekBar>(R.id.seekBar)
        val seekBarProgress = findViewById<TextView>(R.id.seekBarProgress)

        scheduleButton.setOnClickListener { scheduleJob() }
        cancelButton.setOnClickListener { cancelJobs() }

        // Set up SeekBar
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (progress > 0) {
                    seekBarProgress.text = "$progress s"
                } else {
                    seekBarProgress.text = getString(R.string.not_set)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun scheduleJob() {
        val networkOptions = findViewById<RadioGroup>(R.id.networkOptions)
        val deviceIdle = findViewById<Switch>(R.id.idleSwitch)
        val deviceCharging = findViewById<Switch>(R.id.chargingSwitch)
        val seekBar = findViewById<SeekBar>(R.id.seekBar)

        val selectedNetworkId = networkOptions.checkedRadioButtonId
        var selectedNetworkOption = JobInfo.NETWORK_TYPE_NONE

        when (selectedNetworkId) {
            R.id.noNetwork -> selectedNetworkOption = JobInfo.NETWORK_TYPE_NONE
            R.id.anyNetwork -> selectedNetworkOption = JobInfo.NETWORK_TYPE_ANY
            R.id.wifiNetwork -> selectedNetworkOption = JobInfo.NETWORK_TYPE_UNMETERED
        }

        mScheduler = getSystemService(JOB_SCHEDULER_SERVICE) as JobScheduler

        val componentName = ComponentName(this, NotificationJobService::class.java)
        val builder = JobInfo.Builder(JOB_ID, componentName)
            .setRequiredNetworkType(selectedNetworkOption)
            .setRequiresDeviceIdle(deviceIdle.isChecked)
            .setRequiresCharging(deviceCharging.isChecked)

        // Add deadline constraint if set
        val seekBarProgress = seekBar.progress
        val seekBarSet = seekBarProgress > 0
        if (seekBarSet) {
            builder.setOverrideDeadline(seekBarProgress * 1000L)
        }

        // Check if at least one constraint is set
        val constraintSet = selectedNetworkOption != JobInfo.NETWORK_TYPE_NONE ||
                deviceIdle.isChecked ||
                deviceCharging.isChecked ||
                seekBarSet

        if (constraintSet) {
            val jobInfo = builder.build()
            mScheduler?.schedule(jobInfo)
            Toast.makeText(this, R.string.job_scheduled, Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, R.string.no_constraint_toast, Toast.LENGTH_SHORT).show()
        }
    }

    private fun cancelJobs() {
        if (mScheduler != null) {
            mScheduler?.cancelAll()
            mScheduler = null
            Toast.makeText(this, R.string.jobs_cancelled, Toast.LENGTH_SHORT).show()
        }
    }
} 