package com.chinkiandfamily.coroutines_cwm_android

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ProgressBar
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.chinkiandfamily.coroutines_cwm_android.databinding.ActivityMainBinding
import kotlinx.coroutines.*

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
        binding?.btnStartJob?.setOnClickListener {
            if (!::job.isInitialized) {
                initJob()
                binding?.progressBarJob?.startJobCancel(job)
            }
        }
    }

    private fun initJob() {
        updateJobCompleteText("")
        with(binding ?: return) {
            btnStartJob.text = "Start Job #1"
            job = Job()
            job.invokeOnCompletion {
                it?.message.let {
                    var msg = it
                    if (msg.isNullOrBlank()) {
                        msg = "Unknown Cancellation Error."
                    }
                    println("$job was cancelled $msg")
                    showToast(msg)
                }
            }
            progressBarJob.max = PROGRESS_MAX
            progressBarJob.progress = PROGRESS_START
        }
    }

    /**
     * Extension Function for ProgressBar
     * */
    private fun ProgressBar.startJobCancel(job: Job) {
        if (this.progress > 0) {
            println("$job is already active. Cancelling...")
            resetJob()
        } else {
            binding?.btnStartJob?.text = "Cancel Job #1"
            CoroutineScope(Dispatchers.IO + job).launch {
                println("Coroutine $this is activated with job: $job")
                for (i in PROGRESS_START..PROGRESS_MAX) {
                    delay((JOB_TIME / PROGRESS_MAX).toLong())
                    this@startJobCancel.progress = i
                }
                updateJobCompleteText("Job is complete")
            }
        }
    }

    private fun resetJob() {
        if (job.isActive || job.isCompleted) {
            job.cancel(CancellationException("Resetting Job"))
        }
        initJob()
    }

    private fun updateJobCompleteText(text: String) {
        CoroutineScope(Dispatchers.Main).launch {
            binding?.tvCompleteText?.text = text
        }
    }

    private fun showToast(text: String) {
        CoroutineScope(Dispatchers.Main).launch {
            Toast.makeText(this@MainActivity, text, Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}