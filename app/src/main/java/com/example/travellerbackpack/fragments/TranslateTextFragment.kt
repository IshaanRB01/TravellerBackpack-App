package com.example.travellerbackpack.fragments

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.travellerbackpack.utils.Constants
import com.example.travellerbackpack.adapter.CustomSelectLanguageAdapter
import com.example.travellerbackpack.adapter.ISelectItem
import com.example.travellerbackpack.databinding.DialogCustomLanguageListBinding
import com.example.travellerbackpack.databinding.FragmentTranslateTextBinding
import com.example.travellerbackpack.modal.Language
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.languageid.LanguageIdentification
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions


class TranslateTextFragment : Fragment(), ISelectItem {

    private var _binding: FragmentTranslateTextBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var mCustomChooseLanguageDialog: Dialog

    private var toTranslateLanguage: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTranslateTextBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.chooseLanguageBtn.setOnClickListener {
            customLanguageSelectionDialog(Constants.languageOptions())
        }
        binding.translateBtn.setOnClickListener {
            val text = binding.translateInputText.text.toString()
            closeKeybord()
            if (text.isEmpty()) {
                Toast.makeText(requireContext(), "Please Enter Text", Toast.LENGTH_SHORT).show()
            } else {
                detectLanguage(text)
            }
        }

        return view
    }

    private fun detectLanguage(text: String) {
        Log.e("Translate Activity", "detect language entered")

        val languageIdentifier = LanguageIdentification
            .getClient()
        languageIdentifier.identifyLanguage(text)
            .addOnSuccessListener { languageCode ->
                if (languageCode == "und") {
                    Log.i("Translate Activity", "Can't identify language.")
                    Toast.makeText(requireContext(), "Please Enter valid text", Toast.LENGTH_SHORT)
                        .show()
                    binding.translateOutputText.text.clear()

                } else {
                    Log.e("Translate", "Language Detected = $languageCode")
                    translateText(languageCode, text)
                }

            }
            .addOnFailureListener {
            }
    }


    private fun translateText(languageCode: String, text: String) {

        if (toTranslateLanguage == null) {
            toTranslateLanguage = "fr"
        }

        try {
            val options = TranslatorOptions.Builder()
                .setSourceLanguage(TranslateLanguage.fromLanguageTag(languageCode)!!)
                .setTargetLanguage(TranslateLanguage.fromLanguageTag(toTranslateLanguage!!)!!)
                .build()
            val translator = Translation.getClient(options)
            val conditions = DownloadConditions.Builder().build()
            translator.downloadModelIfNeeded(conditions)
                .addOnSuccessListener {
                    // Model downloaded successfully. Okay to start translating.
                    // (Set a flag, unhide the translation UI, etc.)
                    Toast.makeText(requireContext(), "Models Downloaded", Toast.LENGTH_SHORT).show()

                    //translateModel()
                    translator.translate(text)
                        .addOnSuccessListener { translatedText ->
                            // Translation successful.
                            binding.translateOutputText.setText(translatedText.toString())
                        }
                        .addOnFailureListener {
                        }
                }
                .addOnFailureListener {
                    // Model couldn’t be downloaded or other internal error.
                    // ...
                }

        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Some Error Occured", Toast.LENGTH_SHORT).show()
        }

    }


    private fun customLanguageSelectionDialog(itemsList: List<Language>) {
        mCustomChooseLanguageDialog = Dialog(requireContext())
        val binding: DialogCustomLanguageListBinding =
            DialogCustomLanguageListBinding.inflate(layoutInflater)

        mCustomChooseLanguageDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        mCustomChooseLanguageDialog.setCanceledOnTouchOutside(true)
        mCustomChooseLanguageDialog.setContentView(binding.root)
        binding.translateRvList.layoutManager = LinearLayoutManager(requireContext())

        val adapter = CustomSelectLanguageAdapter(requireContext(), itemsList, this)
        binding.translateRvList.adapter = adapter
        mCustomChooseLanguageDialog.show()
    }

    private fun closeKeybord() {
        val imm =
            requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view = requireActivity().currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(requireActivity())
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun selectedListItem(item: Language) {
        mCustomChooseLanguageDialog.dismiss()
        toTranslateLanguage = item.languageCode
        binding.chooseLanguageBtn.text = item.languageString
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}