package com.example.meongku.ui.main.scan

import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.example.meongku.databinding.ActivityScanBinding

class ScanActivity : AppCompatActivity() {
    private lateinit var binding : ActivityScanBinding
    private var imageCapture: ImageCapture? = null
    private var cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.captureGallery.setOnClickListener { startGallery() }
        binding.captureImage.setOnClickListener {
            takePhoto()
        }
        binding.SwitchCamera.setOnClickListener {
            cameraSelector = if (cameraSelector.equals(CameraSelector.DEFAULT_BACK_CAMERA)) CameraSelector.DEFAULT_FRONT_CAMERA
            else CameraSelector.DEFAULT_BACK_CAMERA
            startCamera()
        }
    }

    public override fun onResume() {
        super.onResume()
        hideSystemUI()
        startCamera()
    }
    private fun takePhoto() {
        Log.d("ScanActivity", "Taking photo...")
        val imageCapture = imageCapture ?: return
        val photoFile = createFile(application)
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.d("ScanActivity", "Error taking photo: ${exc.localizedMessage}")
                    Toast.makeText(
                        this@ScanActivity,
                        "Gagal mengambil gambar.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    Log.d("ScanActivity", "Photo saved.")
                    val myFile = photoFile
                    startScanResultActivity(myFile.absolutePath, cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA)

                }
            }
        )
    }

    private fun startScanResultActivity(imagePath: String, isBackCamera: Boolean) {
        val resultIntent = Intent(this@ScanActivity, ScanResultActivity::class.java).apply {
            putExtra("picture", imagePath)
            putExtra("isBackCamera", isBackCamera)
        }
        try {
            startActivity(resultIntent)
        } catch (e: Exception) {
            Log.d("ScanActivity", "Error starting ScanResultActivity: ${e.localizedMessage}")
        }
    }

    private fun startCamera() {
        Log.d("ScanActivity", "Starting camera...")
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder().build()

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    preview,
                    imageCapture
                )
            } catch (exc: Exception) {
                Log.d("ScanActivity", "Error starting camera: ${exc.localizedMessage}")
                Toast.makeText(
                    this@ScanActivity,
                    "Gagal memunculkan kamera.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun hideSystemUI() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun startGallery() {
        Log.d("ScanActivity", "Starting gallery...")
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            Log.d("ScanActivity", "Image selected from gallery.")
            val selectedImg = result.data?.data as Uri
            val myFile = uriToFile(selectedImg, this@ScanActivity)
            val resultIntent = Intent(this@ScanActivity, ScanResultActivity::class.java).apply {
                putExtra("picture", myFile)
            }
            try {
                startActivity(resultIntent)
            } catch (e: Exception) {
                Log.d("ScanActivity", "Error starting ScanResultActivity with image from gallery: ${e.localizedMessage}")
            }
        }
    }
}