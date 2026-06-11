package com.example.util

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.Locale

class TtsHelper(
    private val context: Context,
    private val onInitSuccess: () -> Unit = {}
) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var isInitialized = false

    init {
        tts = TextToSpeech(context.applicationContext, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            isInitialized = true
            onInitSuccess()
            Log.d("TtsHelper", "TextToSpeech Initialized successfully.")
        } else {
            Log.e("TtsHelper", "Initialization of TextToSpeech failed.")
        }
    }

    fun speak(text: String, isBangla: Boolean) {
        if (!isInitialized) {
            Log.e("TtsHelper", "TTS is not initialized yet.")
            return
        }

        val locale = if (isBangla) {
            Locale("bn", "BD")
        } else {
            Locale.US
        }

        val result = tts?.setLanguage(locale)
        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            // Fallback for Bangla sometimes: Try general Indian English or default if Bangla voice package is not downloaded on device
            if (isBangla) {
                Log.w("TtsHelper", "Bangla language packaging is missing. Trying a fallback...")
                tts?.setLanguage(Locale("bn", "IN"))
            } else {
                Log.e("TtsHelper", "Language is not supported.")
            }
        }

        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "BanglaDictSpeechId")
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        isInitialized = false
        tts = null
    }
}
