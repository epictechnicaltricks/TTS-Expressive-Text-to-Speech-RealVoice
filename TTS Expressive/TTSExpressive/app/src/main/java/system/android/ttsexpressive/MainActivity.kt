package system.android.ttsexpressive

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.util.*


import android.media.MediaPlayer
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.io.File
import java.io.IOException


class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var tts: TextToSpeech
    private lateinit var etEnglish: EditText
    private lateinit var etHindi: EditText
    private lateinit var etOdia: EditText
    private lateinit var speedSeekBar: SeekBar
    private lateinit var pitchSeekBar: SeekBar


    private lateinit var etElevenLabs: EditText
    private lateinit var btnSpeakElevenLabs: Button


    private fun speakViaElevenLabs(text: String) {
        val apiKey = "sk_0f4a605b63ab9ba0c4c6b27cfaf892b753996c7c8c692534"
        //val voiceId = "zT03pEAEi0VHKciJODfn" - male
        val voiceId = "Sm1seazb4gs7RSlUVw7c" // female

        val json = """
        {
          "text": "$text",
          "model_id": "eleven_multilingual_v2",
          "voice_settings": {
            "stability": 0.75,
            "similarity_boost": 0.75
          }
        }
    """.trimIndent()

        val client = OkHttpClient()
        val body = RequestBody.create("application/json".toMediaTypeOrNull(), json)
        val request = Request.Builder()
            .url("https://api.elevenlabs.io/v1/text-to-speech/$voiceId")
            .addHeader("xi-api-key", apiKey)
            .addHeader("accept", "audio/mpeg")
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@MainActivity, "API Failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val inputStream = response.body?.byteStream()
                    val tempFile = File.createTempFile("tts_audio", ".mp3", cacheDir)
                    tempFile.outputStream().use { inputStream?.copyTo(it) }

                    val mediaPlayer = MediaPlayer()
                    mediaPlayer.setDataSource(tempFile.absolutePath)
                    mediaPlayer.prepare()
                    mediaPlayer.start()
                } else {
                    runOnUiThread {
                        Toast.makeText(this@MainActivity, "TTS API Error", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etEnglish = findViewById(R.id.etEnglish)
        etHindi = findViewById(R.id.etHindi)
        etOdia = findViewById(R.id.etOdia)
        speedSeekBar = findViewById(R.id.speedSeekBar)
        pitchSeekBar = findViewById(R.id.pitchSeekBar)

        val btnSpeakEnglish = findViewById<Button>(R.id.btnSpeakEnglish)
        val btnSpeakHindi = findViewById<Button>(R.id.btnSpeakHindi)
        val btnSpeakOdia = findViewById<Button>(R.id.btnSpeakOdia)


        tts = TextToSpeech(this, this, "com.google.android.tts")



        etElevenLabs = findViewById(R.id.etElevenLabs)
        btnSpeakElevenLabs = findViewById(R.id.btnSpeakElevenLabs)

        btnSpeakElevenLabs.setOnClickListener {
            val text = etElevenLabs.text.toString()
            speakViaElevenLabs(text)
        }






        btnSpeakEnglish.setOnClickListener {
            speakText(etEnglish.text.toString(), Locale("en", "IN"))
        }

        btnSpeakHindi.setOnClickListener {
            speakText(etHindi.text.toString(), Locale("hi", "IN"))
        }

        btnSpeakOdia.setOnClickListener {
            speakText(etOdia.text.toString(), Locale("hi", "IN")) // Oriya (Odia)
        }
    }

    private fun speakText(text: String, locale: Locale) {
        val speed = 0.5f + (speedSeekBar.progress / 100f)
        val pitch = 0.5f + (pitchSeekBar.progress / 100f)

        tts.language = locale
        tts.setSpeechRate(speed)
        tts.setPitch(pitch)

        // Pick best voice matching language + female
        tts.voice = tts.voices.find {
            it.locale.language == locale.language && it.name.contains("female", true)
        } ?: tts.defaultVoice

        Log.d("TTS", "Speaking in ${locale.displayLanguage} | Voice: ${tts.voice?.name}")
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "TTS_ID")
    }

    override fun onInit(status: Int) {
        if (status != TextToSpeech.SUCCESS) {
            Toast.makeText(this, "TTS init failed", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        if (::tts.isInitialized) {
            tts.stop()
            tts.shutdown()
        }
        super.onDestroy()
    }
}
