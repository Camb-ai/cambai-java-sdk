import resources.texttospeech.requests.CreateStreamTtsRequestPayload;
import resources.texttospeech.types.CreateStreamTtsRequestPayloadLanguage;
import resources.texttospeech.types.CreateStreamTtsRequestPayloadSpeechModel;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.io.File;

public class StreamingTtsExample {
    public static void main(String[] args) {
        String apiKey = System.getenv("CAMB_API_KEY");
        if (apiKey == null || apiKey.isEmpty()) {
            System.out.println("Please set CAMB_API_KEY environment variable.");
            return;
        }

        CambApiClient client = CambApiClient.builder()
            .apiKey(apiKey)
            .build();

        System.out.println("Sending Streaming TTS request...");

        try {
            InputStream audioStream = client.textToSpeech().tts(CreateStreamTtsRequestPayload.builder()
                .text("Hello from Camb AI! This is a Java SDK streaming test.")
                .language(CreateStreamTtsRequestPayloadLanguage.EN_US) 
                .voiceId(20303)
                .speechModel(CreateStreamTtsRequestPayloadSpeechModel.MARS8)
                .build());

            File outputFile = new File("streaming_output.mp3");
            try (FileOutputStream outputStream = new FileOutputStream(outputFile)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = audioStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }

            System.out.println("Success! Streaming Audio saved to " + outputFile.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
