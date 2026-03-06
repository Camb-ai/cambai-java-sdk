import resources.texttospeech.requests.CreateStreamTtsRequestPayload;
import resources.texttospeech.types.CreateStreamTtsRequestPayloadLanguage;
import resources.texttospeech.types.CreateStreamTtsRequestPayloadSpeechModel;
import types.OutputFormat;
import types.StreamTtsOutputConfiguration;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.io.File;

public class BasetenExample {
    public static void main(String[] args) {
        // Environment variables for Baseten and Camb AI
        String cambApiKey = System.getenv("CAMB_API_KEY");
        String basetenApiKey = System.getenv("BASETEN_API_KEY");
        String basetenUrl = System.getenv("BASETEN_URL");

        if (cambApiKey == null || basetenApiKey == null || basetenUrl == null) {
            System.err.println("Error: Missing required environment variables:");
            if (cambApiKey == null) System.err.println(" - CAMB_API_KEY");
            if (basetenApiKey == null) System.err.println(" - BASETEN_API_KEY");
            if (basetenUrl == null) System.err.println(" - BASETEN_URL (e.g. your Baseten model endpoint URL)");
            System.exit(1);
        }

        // Initialize the custom Baseten provider
        ITtsProvider basetenProvider = new BasetenProvider(basetenApiKey, basetenUrl);

        System.out.println("Generating speech via Baseten provider...");

        try {
            // Build the payload
            CreateStreamTtsRequestPayload request = CreateStreamTtsRequestPayload.builder()
                .text("Hello. This is speech generated using a custom Baseten provider.")
                .language(CreateStreamTtsRequestPayloadLanguage.EN_US)
                .voiceId(1) // Ignored by custom hosting provider but required by payload
                .speechModel(CreateStreamTtsRequestPayloadSpeechModel.MARSPRO)
                .outputConfiguration(StreamTtsOutputConfiguration.builder().format(OutputFormat.WAV).build())
                .build();

            // Use the provider
            InputStream audioStream = basetenProvider.tts(request, null);

            File outputFile = new File("baseten_output.wav");
            try (FileOutputStream outputStream = new FileOutputStream(outputFile)) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = audioStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }

            System.out.println("✓ Success! Baseten generated audio saved to " + outputFile.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
