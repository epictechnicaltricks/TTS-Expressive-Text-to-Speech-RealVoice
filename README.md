# Text-to-Speech with ElevenLabs API

This Android application demonstrates how to use the ElevenLabs Text-to-Speech (TTS) API to convert text into natural-sounding speech with different voice options.

## Features

- Convert text to speech using ElevenLabs API
- Support for multiple languages (via ElevenLabs multilingual model)
- Choice between male and female voices
- Adjustable voice settings (stability and similarity boost)
- Audio playback within the app

## Code Overview

The main functionality is implemented in the `speakViaElevenLabs` function:

```kotlin
private fun speakViaElevenLabs(text: String) {
    val apiKey = "your_api_key_here"
    val voiceId = "Sm1seazb4gs7RSlUVw7c" // female voice
    
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

    // REST of the implementation...
}
