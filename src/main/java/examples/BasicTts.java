package examples;

import CambApiClient;
import core.ClientOptions;
import resources.texttospeech.requests.CreateStreamTtsRequestPayload;
import resources.texttospeech.types.CreateStreamTtsRequestPayloadLanguage;
import resources.texttospeech.types.CreateStreamTtsRequestPayloadSpeechModel;
import types.Languages;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.io.File;

public class BasicTts {
    public static void main(String[] args) {
        String apiKey = System.getenv("CAMB_API_KEY");
        if (apiKey == null) {
            System.out.println("Please set CAMB_API_KEY environment variable.");
            return;
        }

        CambApiClient client = CambApiClient.builder()
            .apiKey(apiKey)
            .build();

        System.out.println("Sending TTS request...");

        try {
            InputStream audioStream = client.textToSpeech().tts(CreateStreamTtsRequestPayload.builder()
                .text("Hello from Camb AI! This is a Java SDK test.")
                .voiceId(20303)
                // Using EN_US from API enum. In a real helper, we'd map types.Languages.EN_US.getValue() if needed
                .language(CreateStreamTtsRequestPayloadLanguage.EN_US) 
                .speechModel(CreateStreamTtsRequestPayloadSpeechModel.MARS_PRO)
                .build());

            File outputFile = new File("output.mp3");
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
