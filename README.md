# Camb.ai Java SDK

<div id="top" align="center">

   ![Banner](assets/banner5_720.jpg)
   <h3>
   <a href="https://camb.ai/"> Camb AI Website </a></h3>

</div>

The official Java SDK for interacting with Camb AI's powerful voice and audio generation APIs. Create expressive speech, unique voices, and rich soundscapes with just a few lines of Java.

## ✨ Features

- **Dubbing**: Dub your videos into multiple languages with voice cloning!
- **Expressive Text-to-Speech**: Convert text into natural-sounding speech using a wide range of pre-existing voices.
- **Generative Voices**: Create entirely new, unique voices from text prompts and descriptions.
- **Soundscapes from Text**: Generate ambient audio and sound effects from textual descriptions.
- Access to voice cloning, translation, and more (refer to full API documentation).

## 📦 Installation

### Gradle

Add the dependency to your `build.gradle` file:

```groovy
dependencies {
    implementation 'ai.camb:cambai-java-sdk:0.0.1'
}
```

### Maven

Add the dependency to your `pom.xml` file:

```xml
<dependency>
    <groupId>ai.camb</groupId>
    <artifactId>cambai-java-sdk</artifactId>
    <version>0.0.1</version>
</dependency>
```

## 🔑 Authentication & Accessing Clients

To use the Camb AI SDK, you'll need an API key.

```java
import CambApiClient;

CambApiClient client = CambApiClient.builder()
    .apiKey("YOUR_CAMB_API_KEY")
    .build();
```

### Custom Hosting Provider (e.g. Baseten Mars8-Flash)

You can route TTS through a custom hosting provider like Baseten while keeping the same SDK interface.
`reference_audio` can be a public URL or base64-encoded audio file — Baseten caches it for faster inference.

```java
import resources.texttospeech.requests.CreateStreamTtsRequestPayload;
import resources.texttospeech.types.CreateStreamTtsRequestPayloadLanguage;
import java.io.InputStream;

// Initialize the Baseten Mars8-Flash custom hosting provider.
// BASETEN_REFERENCE_AUDIO can be a public URL or base64-encoded audio file.
ITtsProvider ttsProvider = new BasetenProvider(
    System.getenv("BASETEN_API_KEY"),
    System.getenv("BASETEN_URL"),
    System.getenv("BASETEN_REFERENCE_AUDIO"), // reference voice
    "en-us"                                   // reference audio language
);

// Use the provider to generate speech
InputStream audioStream = ttsProvider.tts(CreateStreamTtsRequestPayload.builder()
    .text("Hello from Java via Baseten Mars8-Flash!")
    .language(CreateStreamTtsRequestPayloadLanguage.EN_US)
    .voiceId(1) // Required by the SDK's staged builder; ignored by the Baseten provider
    .build(), null);
```

## 🚀 Getting Started: Examples

NOTE: For more examples and full runnable files refer to the `examples/` directory.

### Supported Models & Sample Rates

| Model Name | Sample Rate | Description |
| :--- | :--- | :--- |
| **mars-pro** | **48kHz** | High-fidelity, professional-grade speech synthesis. Ideal for long-form content and dubbing. |
| **mars-instruct** | **22.05kHz** | Optimized for instruction-following and nuance control. |
| **mars-flash** | **22.05kHz** | Low-latency model optimized for real-time applications and conversational AI. |

#### TTS request options

`client.textToSpeech().tts(...)` accepts the core request fields plus optional controls for model behavior and output format:

| Field | Description |
| :--- | :--- |
| `text(...)` | Text to synthesize. For MARS Instruct, you can include inline emotion or pacing tags. |
| `language(...)` | Locale such as `CreateStreamTtsRequestPayloadLanguage.EN_US`. |
| `voiceId(...)` | Voice profile ID from the voice list APIs. |
| `speechModel(...)` | Model to use, such as `MARSPRO`, `MARSINSTRUCT`, or `MARSFLASH`. |
| `userInstructions(...)` | Adds style, tone, pronunciation, or delivery guidance for the request. Available only with MARS Instruct. |
| `outputConfiguration(...)` | Output settings such as audio format, duration, and enhancement. |
| `voiceSettings(...)` | Voice behavior controls such as reference enhancement or accent preservation. |
| `inferenceOptions(...)` | Advanced generation controls for supported models. |
| `enhanceNamedEntitiesPronunciation(...)` | Improves pronunciation for names and other named entities when supported. |

```java
InputStream audioStream = client.textToSpeech().tts(CreateStreamTtsRequestPayload.builder()
    .text("[warm, friendly] Great to meet you!")
    .language(CreateStreamTtsRequestPayloadLanguage.EN_US)
    .voiceId(147320)
    .speechModel(CreateStreamTtsRequestPayloadSpeechModel.MARSINSTRUCT)
    .userInstructions("Speak in a warm, friendly tone")
    .outputConfiguration(StreamTtsOutputConfiguration.builder().format(OutputFormat.WAV).build())
    .build());
```

### 1. Text-to-Speech (TTS)

Convert text into spoken audio using one of Camb AI's high-quality voices.

```java
import resources.texttospeech.requests.CreateStreamTtsRequestPayload;
import resources.texttospeech.types.CreateStreamTtsRequestPayloadLanguage;
import resources.texttospeech.types.CreateStreamTtsRequestPayloadSpeechModel;
import types.OutputFormat;
import types.StreamTtsOutputConfiguration;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.io.File;

// ... initialize client ...

InputStream audioStream = client.textToSpeech().tts(CreateStreamTtsRequestPayload.builder()
    .text("Hello from Camb AI! This is a test.")
    .language(CreateStreamTtsRequestPayloadLanguage.EN_US) 
    .voiceId(20303)
    .speechModel(CreateStreamTtsRequestPayloadSpeechModel.MARSPRO)
    .outputConfiguration(StreamTtsOutputConfiguration.builder().format(OutputFormat.WAV).build())
    .build());

// Save InputStream to file
File outputFile = new File("tts_output.wav");
try (FileOutputStream outputStream = new FileOutputStream(outputFile)) {
    audioStream.transferTo(outputStream);
}
```

### 2. Text-to-Voice (Generative Voice)

Create completely new and unique voices from a textual description.

```java
import resources.texttovoice.requests.CreateTextToVoiceRequestPayload;

var result = client.textToVoice().createTextToVoice(CreateTextToVoiceRequestPayload.builder()
    .text("A smooth, rich baritone voice.")
    .voiceDescription("Ideal for storytelling.")
    .build());

System.out.println("Generated voice sample URLs: " + result);
```

### 3. Text-to-Audio (Sound Generation)

Generate sound effects or ambient audio from a descriptive prompt.

```java
import resources.texttoaudio.requests.CreateTextToAudioRequestPayload;
import java.util.Optional;

var response = client.textToAudio().createTextToAudio(CreateTextToAudioRequestPayload.builder()
    .prompt("A gentle breeze rustling through autumn leaves.")
    .duration(10)
    .audioType("sound")
    .build());

String taskId = response.getTaskId().get();
// Poll status and get result using client.textToAudio().getTextToAudioStatus(taskId)
```

### 4. End-to-End Dubbing

Dub videos into multiple languages with voice cloning.

```java
import resources.dub.requests.EndToEndDubbingRequestPayload;
import java.util.Collections;

var response = client.dub().endToEndDubbing(EndToEndDubbingRequestPayload.builder()
    .videoUrl("https://www.youtube.com/watch?v=dQw4w9WgXcQ") 
    .sourceLanguage(Languages.EN_US.getValue())
    .targetLanguages(Collections.singletonList(Languages.HI_IN.getValue()))
    .build());

String taskId = response.getTaskId().get();
// Poll status using client.dub().getEndToEndDubbingStatus(taskId)
```

## ⚙️ Advanced Usage & Other Features

The Camb AI SDK offers a wide range of capabilities beyond these examples, including:

- Voice Cloning
- Translated TTS
- Audio Dubbing
- Transcription
- And more!

Please refer to the [Official Camb AI API Documentation](https://docs.camb.ai) for a comprehensive list of features and advanced usage patterns.

## 📖 Examples

Check out the `examples/` directory for complete, runnable examples:

- `examples/BasicTts.java` - Basic text-to-speech example
- `examples/TextToAudioExample.java` - Sound generation example
- `examples/TextToVoiceExample.java` - Generative voice example
- `examples/DubbingExample.java` - Video dubbing workflow
- `examples/BasetenProvider.java` - Using custom hosting providers

## 🔗 Links

- [Official Documentation](https://docs.camb.ai)
- [GitHub Repository](https://github.com/Camb-ai/cambai-java-sdk)
- [Python SDK](https://github.com/Camb-ai/cambai-python-sdk)

## License

This project is licensed under the MIT License - see the LICENSE file for details
