package examples;

import CambApiClient;
import resources.dub.requests.EndToEndDubbingRequestPayload;
import resources.texttospeech.types.CreateStreamTtsRequestPayloadLanguage;
import java.util.Collections;

public class DubbingExample {
    public static void main(String[] args) {
        String apiKey = System.getenv("CAMB_API_KEY");
        if (apiKey == null) {
            System.out.println("Please set CAMB_API_KEY environment variable.");
            return;
        }

        CambApiClient client = CambApiClient.builder()
            .apiKey(apiKey)
            .build();

        System.out.println("Starting Dubbing Task...");

        try {
            var result = client.dub().createDub(EndToEndDubbingRequestPayload.builder()
                .videoUrl("https://example.com/video.mp4")
                .sourceLanguage(CreateStreamTtsRequestPayloadLanguage.EN_US)
                .targetLanguages(Collections.singletonList(CreateStreamTtsRequestPayloadLanguage.FR_FR))
                .build());

            System.out.println("Dubbing Task Created. Details: " + result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
