# Camb.ai Java SDK

The official Java SDK for interacting with Camb AI's powerful voice and audio generation APIs. Create expressive speech, unique voices, and rich soundscapes with just a few lines of Java.

## ✨ Features

- **Dubbing**: Dub your videos into multiple languages with voice cloning!
- **Expressive Text-to-Speech**: Convert text into natural-sounding speech using a wide range of pre-existing voices.
- **Generative Voices**: Create entirely new, unique voices from text prompts and descriptions.
- **Soundscapes from Text**: Generate ambient audio and sound effects from textual descriptions.
- Access to voice cloning, translation, and more (refer to full API documentation).

## 📦 Installation

To be added (Maven/Gradle instructions).

## 🔑 Authentication & Accessing Clients

To use the Camb AI SDK, you'll need an API key.

```java
import core.ClientOptions;
import CambApiClient;

CambApiClient client = CambApiClient.builder()
    .apiKey("YOUR_CAMB_API_KEY")
    .build();
```

### Client with Specific MARS Pro Provider (e.g. Baseten)

You can use the **TtsProvider** pattern to switch between the default Camb.ai provider and custom providers like Baseten.

```java
// Initialize custom provider
ITtsProvider ttsProvider = new BasetenProvider(
    "YOUR_BASETEN_API_KEY",
    "YOUR_BASETEN_URL"
);

// Unified interface call using the custom provider
// Note: Requires passing ITtsProvider or similar abstraction in your app
```

## 🚀 Getting Started: Examples

NOTE: For more examples and full runnable files refer to the `examples/` directory.

### 1. Text-to-Speech (TTS)

Convert text into spoken audio using one of Camb AI's high-quality voices.

```java
import types.Languages;
import resources.texttospeech.requests.CreateStreamTtsRequestPayload;
import resources.texttospeech.types.CreateStreamTtsRequestPayloadLanguage;

// ... initialize client ...

var response = client.textToSpeech().tts(CreateStreamTtsRequestPayload.builder()
    .text("Hello from Camb AI! This is a test.")
    .voiceId(20303)
    .language(CreateStreamTtsRequestPayloadLanguage.EN_US) // Or map from Languages.EN_US
    .speechModel(CreateStreamTtsRequestPayloadSpeechModel.MARS_PRO)
    .build());

// Response is an InputStream, save to file
```

### 2. Text-to-Voice (Generative Voice)

Create completely new and unique voices from a textual description.

```java
var result = client.textToVoice().createTextToVoice(CreateTextToVoiceRequestPayload.builder()
    .text("A smooth, rich baritone voice.")
    .voiceDescription("Ideal for storytelling.")
    .build());
```

## License

This project is licensed under the MIT License.
