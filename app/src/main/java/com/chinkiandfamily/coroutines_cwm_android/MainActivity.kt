package com.chinkiandfamily.coroutines_cwm_android

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.chinkiandfamily.coroutines_cwm_android.databinding.ActivityMainBinding
import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.Job

class MainActivity : AppCompatActivity() {
    private val PROGRESS_MAX = 100
    private val PROGRESS_START = 0
    private val JOB_TIME = 4000
    private lateinit var job: CompletableJob
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)
    }

    fun initJob() {
        with(binding ?: return) {
            btnStartJob.text = "Start Job #1"
            tvCompleteText.text = ""
            job = Job()
            job.invokeOnCompletion {
                it?.message.let {
                    var msg = it
                    if (msg.isNullOrBlank()) {
                        msg = "Unknown Cancellation Error"
                    }
                    println("$job was cancelled $msg")
                }
            }
        }
    }

    fun showToast(text: String) {
        Toast.makeText(this@MainActivity, text, Toast.LENGTH_LONG).show()
    }
}