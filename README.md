# Camb.ai Java SDK

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

### Client with Specific MARS Pro Provider (e.g. Baseten)

You can use a custom provider like Baseten for specialized deployments.

```java
import resources.texttospeech.requests.CreateStreamTtsRequestPayload;
import resources.texttospeech.types.CreateStreamTtsRequestPayloadLanguage;
import resources.texttospeech.types.CreateStreamTtsRequestPayloadSpeechModel;
import types.OutputFormat;
import types.StreamTtsOutputConfiguration;
import java.io.InputStream;

// Initialize custom provider
ITtsProvider ttsProvider = new BasetenProvider(
    "YOUR_BASETEN_API_KEY",
    "YOUR_BASETEN_URL"
);

// Use the provider to generate speech
InputStream audioStream = ttsProvider.tts(CreateStreamTtsRequestPayload.builder()
    .text("Hello from Java via Baseten!")
    .language(CreateStreamTtsRequestPayloadLanguage.EN_US)
    .voiceId(1) // Required but ignored by custom provider
    .speechModel(CreateStreamTtsRequestPayloadSpeechModel.MARSPRO)
    .outputConfiguration(StreamTtsOutputConfiguration.builder().format(OutputFormat.WAV).build())
    .build(), null);
```

## 🚀 Getting Started: Examples

NOTE: For more examples and full runnable files refer to the `src/main/java` directory.

### Supported Models & Sample Rates

| Model Name | Sample Rate | Description |
| :--- | :--- | :--- |
| **mars-pro** | **48kHz** | High-fidelity, professional-grade speech synthesis. Ideal for long-form content and dubbing. |
| **mars-instruct** | **22.05kHz** | Optimized for instruction-following and nuance control. |
| **mars-flash** | **22.05kHz** | Low-latency model optimized for real-time applications and conversational AI. |

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
    .voiceId(20303)
    .language(CreateStreamTtsRequestPayloadLanguage.EN_US) 
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

## 🔗 Links

- [Official Documentation](https://docs.camb.ai)
- [API Reference](https://docs.camb.ai/api-reference)
- [TypeScript SDK](https://github.com/Camb-ai/cambai-node-sdk)
- [Python SDK](https://github.com/Camb-ai/cambai-python-sdk)

## License

This project is licensed under the MIT License.
