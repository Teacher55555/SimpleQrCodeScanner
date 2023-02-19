package com.example.qrcodescaner

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.qrcodescaner.databinding.ActivityResultBinding


private const val VALUE_BUNDLE = "value"

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding

    companion object {
        fun newIntent (context: Context, value: String) : Intent {
            val intent = Intent(context, ResultActivity::class.java).apply {
                putExtra(VALUE_BUNDLE, value)
            }
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            valueView.text = intent.getStringExtra(VALUE_BUNDLE)
            newScanButton.setOnClickListener {
                Intent(this@ResultActivity, MainActivity::class.java)
                    .run { startActivity(this) }
            }
            sendTextButton.setOnClickListener {
                val intentNew = Intent(Intent.ACTION_SEND)
                    .apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, intent.getStringExtra(VALUE_BUNDLE))
                    }
                startActivity(intentNew)
            }
        }
    }
}