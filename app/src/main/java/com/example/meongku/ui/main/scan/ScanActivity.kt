package com.example.meongku.ui.main.scan

import android.content.ContentValues.TAG
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
import com.example.meongku.R
import com.example.meongku.api.ApiService
import com.example.meongku.api.CatBreedAPI
import com.example.meongku.api.RetrofitClient
import com.example.meongku.api.ml.PredictionResponse
import com.example.meongku.databinding.ActivityScanBinding
import com.example.meongku.preference.UserPreferences
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

class ScanActivity : AppCompatActivity() {
    private lateinit var binding : ActivityScanBinding
    private var imageCapture: ImageCapture? = null
    private var cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    private lateinit var retrofitClient: RetrofitClient
    private val api: ApiService = CatBreedAPI.api

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanBinding.inflate(layoutInflater)
        setContentView(binding.root)

       // binding.captureGallery.setOnClickListener { startGallery() }
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
                    uploadImage(myFile)
                }
            }
        )
    }

    private fun startScanResultActivity(imagePath: String, isBackCamera: Boolean, predictedClass: String) {
        val resultIntent = Intent(this@ScanActivity, ScanResultActivity::class.java).apply {
            putExtra("picture", imagePath)
            putExtra("isBackCamera", isBackCamera)
            putExtra("predicted_class", predictedClass)
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

//    private fun startGallery() {
//        Log.d("ScanActivity", "Starting gallery...")
//        val intent = Intent()
//        intent.action = Intent.ACTION_GET_CONTENT
//        intent.type = "image/*"
//        val chooser = Intent.createChooser(intent, "Choose a Picture")
//        launcherIntentGallery.launch(chooser)
//    }

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

    private fun uploadImage(file: File) {
        // Membuat RequestBody instance dari file
        val requestFile: RequestBody = file.asRequestBody("image/*".toMediaTypeOrNull())

        // Membuat MultipartBody.Part menggunakan file request body dan nama part ("image_file")
        val body: MultipartBody.Part = MultipartBody.Part.createFormData("image_file", file.name, requestFile)

        // Memanggil fungsi uploadImage() pada API
        val call: Call<PredictionResponse> = api.uploadImage(body)

        call.enqueue(object : Callback<PredictionResponse> {
            override fun onResponse(call: Call<PredictionResponse>, response: Response<PredictionResponse>) {
                if (response.isSuccessful) {
                    val prediction = response.body()?.predicted_class
                    Log.d("ScanActivity", "Image uploaded successfully!")

                    // Mulai ScanResultActivity dengan hasil prediksi
                    startScanResultActivity(file.absolutePath, cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA, prediction ?: "")
                } else {
                    // Kode yang dijalankan ketika request gagal
                    Log.d("ScanActivity", "Image upload failed: ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<PredictionResponse>, t: Throwable) {
                // Kode yang dijalankan ketika terjadi kesalahan saat melakukan request
                Log.d("ScanActivity", "Error: ${t.message}")
            }
        })
    }
}





//class ScanActivity : AppCompatActivity() {
//
//    private var imageCapture: ImageCapture? = null
//    private lateinit var outputDirectory: File
//    private lateinit var userPreferences: UserPreferences
//    private lateinit var binding: ActivityScanBinding
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        binding = ActivityScanBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        userPreferences = UserPreferences(this)
//
//        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
//
//        cameraProviderFuture.addListener(Runnable {
//            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
//
//            val preview = Preview.Builder()
//                .build()
//                .also {
//                    it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
//                }
//
//            Log.d("CameraXApp", "Preview initialized") // Log debug
//
//            imageCapture = ImageCapture.Builder()
//                .build()
//
//            Log.d("CameraXApp", "ImageCapture initialized") // Log debug
//
//            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
//
//            try {
//                cameraProvider.unbindAll()
//                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
//
//                Log.d("CameraXApp", "Use cases bound to lifecycle")
//
//            } catch(exc: Exception) {
//                Log.e("CameraXApp", "Use case binding failed", exc)
//            }
//
//        }, ContextCompat.getMainExecutor(this))
//
//        binding.captureImage.setOnClickListener { takePhoto() }
//
//        outputDirectory = getOutputDirectory()
//
//    }
//
//    private fun takePhoto() {
//        val imageCapture = imageCapture ?: return
//
//        val photoFile = File(
//            outputDirectory,
//            SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(System.currentTimeMillis()) + ".jpg")
//
//        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
//
//        try {
//            imageCapture.takePicture(
//                outputOptions,
//                ContextCompat.getMainExecutor(this),
//                object : ImageCapture.OnImageSavedCallback {
//
//                    override fun onError(exc: ImageCaptureException) {
//                        Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
//                    }
//
//                    override fun onImageSaved(output: ImageCapture.OutputFileResults) {
//                        Log.d("CameraXApp", "Image saved successfully") // Log debug
//
//                        uploadImageToServer(photoFile)
//                    }
//                })
//        } catch (exc: Exception) {
//            Log.e("CameraXApp", "Error taking picture", exc)
//        }
//    }
//
//    private fun getOutputDirectory(): File {
//        val mediaDir = externalMediaDirs.firstOrNull()?.let {
//            File(it, resources.getString(R.string.app_name)).apply { mkdirs() } }
//        return if (mediaDir != null && mediaDir.exists())
//            mediaDir else filesDir
//    }
//
//    private fun uploadImageToServer(file: File) {
//        try {
//            val requestFile: RequestBody =
//                RequestBody.create("image/jpg".toMediaTypeOrNull(), file)
//            val body: MultipartBody.Part =
//                MultipartBody.Part.createFormData("image", file.name, requestFile)
//
//// Membuat deskripsi
//            val descriptionPart =
//                MultipartBody.Part.createFormData("description", "your_description_here")
//
//            val retrofit = RetrofitClient(userPreferences)
//            val call = retrofit.apiInstance().uploadImage(descriptionPart, body)
//
//            call.enqueue(object : Callback<ResponseBody> {
//                override fun onResponse(
//                    call: Call<ResponseBody>,
//                    response: Response<ResponseBody>
//                ) {
//                    // setelah gambar diunggah, pergi ke halaman hasil
//                    val intent = Intent(this@ScanActivity, ScanResultActivity::class.java)
//                    startActivity(intent)
//                }
//
//                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
//                    Toast.makeText(applicationContext, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
//                    Log.e("CameraXApp", "Upload image to server failed: ${t.message}", t)
//                }
//            })
//        } catch (e: Exception) {
//            Toast.makeText(applicationContext, "Exception: ${e.message}", Toast.LENGTH_SHORT).show()
//            Log.e("CameraXApp", "Exception in uploadImageToServer: ${e.message}", e)
//        }
//    }
//
//}