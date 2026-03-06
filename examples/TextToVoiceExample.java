import resources.texttovoice.requests.CreateTextToVoiceRequestPayload;
import types.GetTextToVoiceResultOut;
import types.OrchestratorPipelineCallResult;
import types.OrchestratorPipelineResult;
import types.TaskStatus;
import java.util.Optional;

public class TextToVoiceExample {
    public static void main(String[] args) {
        String apiKey = System.getenv("CAMB_API_KEY");
        if (apiKey == null) {
            System.err.println("Error: Missing required environment variable: CAMB_API_KEY");
            System.exit(1);
        }

        CambApiClient client = CambApiClient.builder()
            .apiKey(apiKey)
            .build();

        System.out.println("Creating Text-to-Voice (Generative) task...");

        try {
            // 1. Create Task
            OrchestratorPipelineCallResult response = client.textToVoice().createTextToVoice(CreateTextToVoiceRequestPayload.builder()
                .text("Crafting a truly unique and captivating voice.")
                .voiceDescription("A smooth, rich baritone voice with gentle warmth.")
                .build());

            String taskId = response.getTaskId().orElseThrow(() -> new RuntimeException("Failed to get task ID"));
            System.out.println("Task created with ID: " + taskId);

            // 2. Poll for status
            System.out.println("Polling for status...");
            int attempts = 0;
            int maxAttempts = 30; // 60 seconds max

            while (attempts < maxAttempts) {
                OrchestratorPipelineResult statusResponse = client.textToVoice().getTextToVoiceStatus(taskId);
                TaskStatus status = statusResponse.getStatus();
                System.out.println("Current Status: " + status);

                if (status == TaskStatus.SUCCESS) {
                    System.out.println("Voice generation completed!");
                    Integer runId = statusResponse.getRunId().orElseThrow(() -> new RuntimeException("Run ID missing on success"));
                    
                    // 3. Get generated preview URLs
                    GetTextToVoiceResultOut result = client.textToVoice().getTextToVoiceResult(Optional.of(runId));
                    System.out.println("Generated Previews:");
                    for (String url : result.getPreviews()) {
                        System.out.println(" - " + url);
                    }
                    break;
                } else if (status == TaskStatus.ERROR) {
                    System.err.println("Task failed: " + statusResponse.getMessage());
                    break;
                }

                Thread.sleep(2000); // Wait 2 seconds
                attempts++;
            }

            if (attempts >= maxAttempts) {
                System.err.println("Timeout waiting for voice generation.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
