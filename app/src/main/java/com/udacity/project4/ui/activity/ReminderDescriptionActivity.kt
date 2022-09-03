package com.udacity.project4.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.project4.model.Reminder
import com.udacity.project4.R
import com.udacity.project4.databinding.ActivityReminderDescriptionBinding

class ReminderDescriptionActivity : AppCompatActivity() {
    companion object {
        private const val EXTRA_ReminderDataItem = "EXTRA_Reminder"

        fun newIntent(context: Context, reminder: Reminder): Intent {
            val intent = Intent(context, ReminderDescriptionActivity::class.java)
            intent.putExtra(EXTRA_ReminderDataItem, reminder)
            return intent
        }
    }

    private lateinit var binding: ActivityReminderDescriptionBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_reminder_description
        )
        setContentView(binding.root)
        intent?.apply {
            extras?.apply {
                val reminder: Reminder? = getSerializable(EXTRA_ReminderDataItem) as Reminder?
                reminder?.let {
                    binding.reminder = reminder
                }
            }
        }


    }
}