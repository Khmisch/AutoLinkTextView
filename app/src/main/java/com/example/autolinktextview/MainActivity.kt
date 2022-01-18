package com.example.autolinktextview

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.style.StyleSpan
import android.text.style.TypefaceSpan
import android.text.style.UnderlineSpan
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import io.github.armcha.autolink.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val autoLinkTextView = findViewById<AutoLinkTextView>(R.id.autoLinkTextView);

        val custom = MODE_CUSTOM("\\sAndroid\\b")
        autoLinkTextView.addAutoLinkMode(
            MODE_HASHTAG,
            MODE_EMAIL,
            MODE_URL,
            MODE_PHONE,
            custom,
            MODE_MENTION)

        autoLinkTextView.addUrlTransformations(
            "https://en.wikipedia.org/wiki/Wear_OS" to "Wear OS",
            "https://en.wikipedia.org/wiki/Fire_OS" to "FIRE")

        autoLinkTextView.attachUrlProcessor {
            when {
                it.contains("google") -> "Google"
                it.contains("github") -> "Github"
                else -> it
            }
        }

        autoLinkTextView.addSpan(MODE_URL, StyleSpan(Typeface.ITALIC), UnderlineSpan())
        autoLinkTextView.addSpan(MODE_HASHTAG, UnderlineSpan(), TypefaceSpan("monospace"))
        autoLinkTextView.addSpan(custom, StyleSpan(Typeface.BOLD))

        autoLinkTextView.hashTagModeColor = ContextCompat.getColor(this, R.color.teal_700)
        autoLinkTextView.phoneModeColor = ContextCompat.getColor(this, R.color.phone)
        autoLinkTextView.urlModeColor = ContextCompat.getColor(this, R.color.url)
        autoLinkTextView.mentionModeColor = ContextCompat.getColor(this, R.color.mention)

        autoLinkTextView.text = getString(R.string.textView)

        autoLinkTextView.onAutoLinkClick {
            val message = if (it.originalText == it.transformedText) it.originalText
            else "Original text - ${it.originalText} \n\nTransformed text - ${it.transformedText}"
            val url = if (it.mode is MODE_URL) it.originalText else null
            showDialog(it.mode.modeName, message, url)
        }
    }
    fun Context.showDialog(title: String, message: String, url: String? = null) {
        val builder = AlertDialog.Builder(this)
            .setMessage(message)
            .setTitle(title)
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
        if (url != null) {
            builder.setNegativeButton("Browse") { dialog, _ -> browse(url);dialog.dismiss() }
        }
        builder.create().show()
    }

    fun Context.browse(url: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        startActivity(intent)
    }
}