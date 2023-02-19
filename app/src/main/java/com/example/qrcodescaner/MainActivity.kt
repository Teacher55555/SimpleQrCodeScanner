package com.example.qrcodescaner

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.SurfaceHolder
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.qrcodescaner.databinding.ActivityMainBinding
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import java.io.IOException

private const val requestCodeCameraPermission = 1

class MainActivity : AppCompatActivity() {
    private lateinit var cameraSource: CameraSource
    private lateinit var barcodeDetector: BarcodeDetector
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (ContextCompat.checkSelfPermission(
                this@MainActivity, android.Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            askForCameraPermission()
        } else {
            scan()
        }

        binding.barcodeLine.startAnimation(
            AnimationUtils.loadAnimation(this@MainActivity, R.anim.scanner_animation))
    }

    private fun askForCameraPermission() {
        ActivityCompat.requestPermissions(
            this@MainActivity,
            arrayOf(android.Manifest.permission.CAMERA),
            requestCodeCameraPermission
        )
    }

    private fun scan() {
        barcodeDetector =
            BarcodeDetector.Builder(this).setBarcodeFormats(Barcode.ALL_FORMATS).build()

        cameraSource = CameraSource.Builder(this, barcodeDetector)
            .setRequestedPreviewSize(1920, 1080)
            .setAutoFocusEnabled(true)
            .build()

        binding.cameraSurfaceView.holder.addCallback(object : SurfaceHolder.Callback {
            @SuppressLint("MissingPermission")
            override fun surfaceCreated(holder: SurfaceHolder) {
                try { cameraSource.start(holder) }
                catch (e: IOException) { e.printStackTrace() }
            }

            @SuppressLint("MissingPermission")
            override fun surfaceChanged(
                holder: SurfaceHolder,
                format: Int,
                width: Int,
                height: Int
            ) {
                try { cameraSource.start(holder) }
                catch (e: IOException) { e.printStackTrace() }
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                cameraSource.stop()
            }
        })

       barcodeDetector.setProcessor(object : Detector.Processor<Barcode> {
           override fun release() {
               Toast.makeText(
                   applicationContext,
                   "Scanner has been closed",
                   Toast.LENGTH_SHORT)
               .show()
           }

           override fun receiveDetections(p0: Detector.Detections<Barcode>) {
               val barCodes = p0.detectedItems
               if (barCodes.size() == 1) {
                   val scannedValue = barCodes.valueAt(0).rawValue
                   runOnUiThread { cameraSource.stop() }
                   startActivity(ResultActivity.newIntent(this@MainActivity, scannedValue))
               }
           }
       })
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == requestCodeCameraPermission && grantResults.isNotEmpty()) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                scan()
            } else {
                Toast.makeText(
                    applicationContext,
                    "Permission Denied",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraSource.stop()
    }
}


