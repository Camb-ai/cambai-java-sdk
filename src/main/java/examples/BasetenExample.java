package examples;

import providers.BasetenProvider;
import providers.ITtsProvider;
import resources.texttospeech.requests.CreateStreamTtsRequestPayload;
import resources.texttospeech.types.CreateStreamTtsRequestPayloadLanguage;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.io.File;

public class BasetenExample {
    public static void main(String[] args) {
        String apiKey = System.getenv("BASETEN_API_KEY");
        String url = System.getenv("BASETEN_URL");
        
        if (apiKey == null) {
            System.out.println("Please set BASETEN_API_KEY environment variable.");
            return;
        }

        ITtsProvider provider = new BasetenProvider(apiKey, url);

        System.out.println("Sending TTS request to Baseten...");

        try {
            // Note: BasetenProvider in this example uses hardcoded ref audio/lang. 
            // In production, extend payload or use context.
            InputStream audioStream = provider.tts(CreateStreamTtsRequestPayload.builder()
                .text("Hello from Java Custom Provider via Baseten!")
                .voiceId(0) // Ignored
                .language(CreateStreamTtsRequestPayloadLanguage.EN_US) 
                .build(), null);

            File outputFile = new File("baseten_output.mp3");
            try (FileOutputStream outputStream = new FileOutputStream(outputFile)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = audioStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }

            System.out.println("Success! Audio saved to " + outputFile.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
