package examples;

import CambApiClient;
import resources.texttovoice.requests.CreateTextToVoiceRequestPayload;
import types.GetTextToVoiceResultOut;

public class TextToVoiceExample {
    public static void main(String[] args) {
        String apiKey = System.getenv("CAMB_API_KEY");
        if (apiKey == null) {
            System.out.println("Please set CAMB_API_KEY environment variable.");
            return;
        }

        CambApiClient client = CambApiClient.builder()
            .apiKey(apiKey)
            .build();

        System.out.println("Generating new voice...");

        // Note: The generated SDK return type might be a specific Task object or result
        // Adjust based on exact generated signature.
        try {
           var result = client.textToVoice().createTextToVoice(CreateTextToVoiceRequestPayload.builder()
                .text("A calm and relaxed voice.")
                .voiceDescription("Soft male voice with American accent.")
                .build());
            
            System.out.println("Request Sent. Result: " + result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
