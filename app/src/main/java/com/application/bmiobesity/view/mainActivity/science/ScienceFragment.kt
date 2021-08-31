package com.application.bmiobesity.view.mainActivity.science

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.View
import androidx.fragment.app.Fragment
import com.application.bmiobesity.R
import com.application.bmiobesity.databinding.MainScienceFragmentBinding

class ScienceFragment : Fragment(R.layout.main_science_fragment) {

    private var binding: MainScienceFragmentBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = MainScienceFragmentBinding.bind(view)

        initView()
    }

    private fun initView() {
        binding?.scienceAmericanMedicalAssociationTextView?.text = getText(
            resources.getString(R.string.science_american_medical_association_name),
            resources.getString(R.string.science_american_medical_association_link_text),
            resources.getString(R.string.science_american_medical_association_link_url),
            resources.getString(R.string.science_american_medical_association_content),
        )
        binding?.scienceUSAGovTextView?.text = getText(
            resources.getString(R.string.science_usa_gov_name),
            resources.getString(R.string.science_usa_gov_link_text),
            resources.getString(R.string.science_usa_gov_link_url),
            resources.getString(R.string.science_usa_gov_content),
        )
        binding?.sciencePubMedTextView?.text = getText(
            resources.getString(R.string.science_pub_med_name),
            resources.getString(R.string.science_pub_med_link_text),
            resources.getString(R.string.science_pub_med_link_url),
            resources.getString(R.string.science_pub_med_content),
        )
        binding?.scienceCochraneLibraryTextView?.text = getText(
            resources.getString(R.string.science_cochrane_library_name),
            resources.getString(R.string.science_cochrane_library_link_text),
            resources.getString(R.string.science_cochrane_library_link_url),
            resources.getString(R.string.science_cochrane_library_content),
        )
        binding?.scienceClinicalTrialsTextView?.text = getText(
            resources.getString(R.string.science_clinical_trials_name),
            resources.getString(R.string.science_clinical_trials_link_text),
            resources.getString(R.string.science_clinical_trials_link_url),
            resources.getString(R.string.science_clinical_trials_content),
        )
        binding?.scienceMimicTextView?.text = getText(
            resources.getString(R.string.science_mimic_name),
            resources.getString(R.string.science_mimic_link_text),
            resources.getString(R.string.science_mimic_link_url),
            resources.getString(R.string.science_mimic_content),
        )
        binding?.scienceAmericanMedicalAssociationTextView?.movementMethod =
            LinkMovementMethod.getInstance()
        binding?.scienceUSAGovTextView?.movementMethod = LinkMovementMethod.getInstance()
        binding?.sciencePubMedTextView?.movementMethod = LinkMovementMethod.getInstance()
        binding?.scienceCochraneLibraryTextView?.movementMethod = LinkMovementMethod.getInstance()
        binding?.scienceClinicalTrialsTextView?.movementMethod = LinkMovementMethod.getInstance()
        binding?.scienceMimicTextView?.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun getText(
        name: String,
        linkText: String,
        linkURL: String,
        content: String
    ): Spannable {
        // Init spannable string builder
        val spannableStringBuilder = SpannableStringBuilder()
        spannableStringBuilder.apply {
            append(name)
            append(" (")
            append(linkText)
            append(") ")
            append(content)
        }

        // Init styles
        val nameBoldSpan = StyleSpan(android.graphics.Typeface.BOLD)
        val nameColorSpan = ForegroundColorSpan(resources.getColor(R.color.color_black, null))
        val linkColorSpan = ForegroundColorSpan(resources.getColor(R.color.google_blue, null))
        val linkClickableSpan = object : ClickableSpan() {
            override fun onClick(p0: View) {
                // Open website
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse(linkURL)
                }
                startActivity(intent)
            }
        }

        // Set name = BOLD
        spannableStringBuilder.setSpan(
            nameBoldSpan,
            spannableStringBuilder.indexOf(name),
            spannableStringBuilder.indexOf(name) + name.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        // Set name color = black
        spannableStringBuilder.setSpan(
            nameColorSpan,
            spannableStringBuilder.indexOf(name),
            spannableStringBuilder.indexOf(name) + name.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        // Set link color = colorPrimary
        spannableStringBuilder.setSpan(
            linkColorSpan,
            spannableStringBuilder.indexOf(linkText),
            spannableStringBuilder.indexOf(linkText) + linkText.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        // Set link clickable
        spannableStringBuilder.setSpan(
            linkClickableSpan,
            spannableStringBuilder.indexOf(linkText),
            spannableStringBuilder.indexOf(linkText) + linkText.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        return spannableStringBuilder
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}