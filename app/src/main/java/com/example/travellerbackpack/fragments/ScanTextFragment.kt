package com.example.travellerbackpack.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.travellerbackpack.activities.CameraActivity
import com.example.travellerbackpack.databinding.FragmentScanTextBinding
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.io.File
import java.io.IOException


@Suppress("DEPRECATION")
class ScanTextFragment : Fragment() {
    private var _binding: FragmentScanTextBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScanTextBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.galleryChoseBtn.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Chose Image"), 122)
        }

        binding.cameraChoseBtn.setOnClickListener {
            Log.e(this.javaClass.name,"Camera called")
            val intent = Intent(requireContext(), CameraActivity::class.java)
            startActivityForResult(intent, 123)
        }
        return view
    }

    private fun scantextFromImage(uri: Uri) {
        binding.imageView.setImageURI(uri)

        val image: InputImage
        try {
            image = InputImage.fromFilePath(requireActivity(), uri)
            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
            recognizer.process(image)
                .addOnSuccessListener {

                    binding.editTxtView.setText(it.text)
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Cannot recognise Text", Toast.LENGTH_SHORT)
                        .show()
                }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        Log.e(this.javaClass.name,requestCode.toString())
        if (data == null) {
            Toast.makeText(requireContext(), "No Image Selected", Toast.LENGTH_SHORT).show()
            return
        }
        if (requestCode == 122) {
            scantextFromImage(data.data!!)
        }

        if (requestCode == 123) {
            val savedUri = data.getStringExtra("savedUri")

            val photoUri = Uri.parse(savedUri)!!

            val uri = Uri.fromFile(File(photoUri.path!!))
            scantextFromImage(uri)

        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
