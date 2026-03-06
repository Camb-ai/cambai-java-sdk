import core.ClientOptions;
import resources.texttospeech.requests.CreateStreamTtsRequestPayload;
import resources.texttospeech.types.CreateStreamTtsRequestPayloadLanguage;
import resources.texttospeech.types.CreateStreamTtsRequestPayloadSpeechModel;
import types.Languages;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.io.File;
import types.OutputFormat;
import types.StreamTtsOutputConfiguration;

public class BasicTts {
    public static void main(String[] args) {
        String apiKey = System.getenv("CAMB_API_KEY");
        if (apiKey == null) {
            System.err.println("Error: Missing required environment variable: CAMB_API_KEY");
            System.exit(1);
        }

        CambApiClient client = CambApiClient.builder()
            .apiKey(apiKey)
            .build();

        System.out.println("Sending TTS request...");

        try {
            // Using the builder to construct the request payload
            // Model: MARSPRO, Format: WAV
            InputStream audioStream = client.textToSpeech().tts(CreateStreamTtsRequestPayload.builder()
                .text("Hello from Camb AI! This is a demonstration of our advanced text-to-speech technology using the MARS Pro model.")
                .language(CreateStreamTtsRequestPayloadLanguage.EN_US) 
                .voiceId(20303)
                .speechModel(CreateStreamTtsRequestPayloadSpeechModel.MARSPRO)
                .outputConfiguration(StreamTtsOutputConfiguration.builder().format(OutputFormat.WAV).build())
                .build());

            File outputFile = new File("tts_output.wav");
            try (FileOutputStream outputStream = new FileOutputStream(outputFile)) {
                // Buffer reading to handle the InputStream efficiently
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = audioStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }

            System.out.println("Success! Audio saved to " + outputFile.getAbsolutePath());
        } catch (Exception e) {
            System.err.println("Error during TTS generation: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
