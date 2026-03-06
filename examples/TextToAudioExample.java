import resources.texttoaudio.requests.CreateTextToAudioRequestPayload;
import types.OrchestratorPipelineCallResult;
import types.OrchestratorPipelineResult;
import types.TaskStatus;
import types.TextToAudioType;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.util.Optional;

public class TextToAudioExample {
    public static void main(String[] args) {
        String apiKey = System.getenv("CAMB_API_KEY");
        if (apiKey == null) {
            System.err.println("Error: Missing required environment variable: CAMB_API_KEY");
            System.exit(1);
        }

        CambApiClient client = CambApiClient.builder()
            .apiKey(apiKey)
            .build();

        System.out.println("Creating Text-to-Audio task...");

        try {
            // 1. Create Task
            OrchestratorPipelineCallResult response = client.textToAudio().createTextToAudio(CreateTextToAudioRequestPayload.builder()
                .prompt("A futuristic sci-fi laser sound effect")
                .duration(3.0)
                .audioType(TextToAudioType.SOUND)
                .build());

            String taskId = response.getTaskId().orElseThrow(() -> new RuntimeException("Failed to get task ID"));
            System.out.println("Task created with ID: " + taskId);

            // 2. Poll for status
            System.out.println("Polling for status...");
            int attempts = 0;
            int maxAttempts = 30; // 60 seconds max

            while (attempts < maxAttempts) {
                OrchestratorPipelineResult statusResponse = client.textToAudio().getTextToAudioStatus(taskId);
                TaskStatus status = statusResponse.getStatus();
                System.out.println("Current Status: " + status);

                if (status == TaskStatus.SUCCESS) {
                    System.out.println("Task completed! Downloading result...");
                    Integer runId = statusResponse.getRunId().orElseThrow(() -> new RuntimeException("Run ID missing on success"));
                    
                    // 3. Get result stream
                    InputStream audioStream = client.textToAudio().getTextToAudioResult(Optional.of(runId));

                    File outputFile = new File("text_to_audio_output.wav");
                    try (FileOutputStream outputStream = new FileOutputStream(outputFile)) {
                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = audioStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, bytesRead);
                        }
                    }
                    System.out.println("✓ Audio saved to " + outputFile.getAbsolutePath());
                    break;
                } else if (status == TaskStatus.ERROR) {
                    System.err.println("Task failed: " + statusResponse.getMessage());
                    break;
                }

                Thread.sleep(2000); // Wait 2 seconds
                attempts++;
            }

            if (attempts >= maxAttempts) {
                System.err.println("Timeout waiting for task completion.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
