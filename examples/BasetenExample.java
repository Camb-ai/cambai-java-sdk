import resources.texttospeech.requests.CreateStreamTtsRequestPayload;
import resources.texttospeech.types.CreateStreamTtsRequestPayloadLanguage;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.io.File;

/**
 * Example: Baseten Mars8-Flash custom hosting provider via the Camb.ai Java SDK.
 *
 * Required environment variables:
 *   CAMB_API_KEY            - Your Camb.ai API key
 *   BASETEN_API_KEY         - Your Baseten API key
 *   BASETEN_URL             - Your Baseten model prediction endpoint URL
 *   BASETEN_REFERENCE_AUDIO - Reference voice audio: public URL or base64-encoded file
 *                             e.g. https://github.com/Camb-ai/mars6-turbo/raw/refs/heads/master/assets/example.wav
 *
 * Optional:
 *   BASETEN_REFERENCE_LANGUAGE - ISO locale of the reference audio (default: en-us)
 *
 * API reference: https://www.baseten.co/library/mars8-flash/
 */
public class BasetenExample {
    public static void main(String[] args) {
        String cambApiKey         = System.getenv("CAMB_API_KEY");
        String basetenApiKey      = System.getenv("BASETEN_API_KEY");
        String basetenUrl         = System.getenv("BASETEN_URL");
        String referenceAudio     = System.getenv("BASETEN_REFERENCE_AUDIO");
        String referenceLanguage  = System.getenv("BASETEN_REFERENCE_LANGUAGE");

        // Loud fail for missing required env vars
        boolean hasError = false;
        if (cambApiKey == null)     { System.err.println("  - CAMB_API_KEY"); hasError = true; }
        if (basetenApiKey == null)  { System.err.println("  - BASETEN_API_KEY"); hasError = true; }
        if (basetenUrl == null)     { System.err.println("  - BASETEN_URL (your Baseten model prediction endpoint)"); hasError = true; }
        if (referenceAudio == null) { System.err.println("  - BASETEN_REFERENCE_AUDIO (public URL or base64-encoded audio file)"); hasError = true; }

        if (hasError) {
            System.err.println("Error: Missing required environment variables (see above).");
            System.exit(1);
        }

        // Default reference language to en-us if not set
        if (referenceLanguage == null || referenceLanguage.isEmpty()) {
            referenceLanguage = "en-us";
        }

        // Initialise the Baseten custom hosting provider
        ITtsProvider basetenProvider = new BasetenProvider(
            basetenApiKey,
            basetenUrl,
            referenceAudio,
            referenceLanguage
        );

        System.out.println("Generating speech via Baseten Mars8-Flash custom hosting provider...");

        try {
            // Build the TTS request.
            // voiceId is required by the Camb SDK payload builder but is ignored
            // when routing through a custom hosting provider.
            CreateStreamTtsRequestPayload request = CreateStreamTtsRequestPayload.builder()
                .text("Hello. This is speech generated via a Baseten Mars8-Flash custom hosting provider.")
                .language(CreateStreamTtsRequestPayloadLanguage.EN_US)
                .voiceId(1) // Required by the SDK's staged builder; ignored by the Baseten provider
                .build();

            InputStream audioStream = basetenProvider.tts(request, null);

            File outputFile = new File("baseten_output.flac");
            try (FileOutputStream outputStream = new FileOutputStream(outputFile)) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = audioStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }

            System.out.println("✓ Success! Audio saved to " + outputFile.getAbsolutePath());
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
