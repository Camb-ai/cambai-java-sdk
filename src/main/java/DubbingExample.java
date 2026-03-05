
import resources.dub.requests.EndToEndDubbingRequestPayload;
import resources.dub.types.GetDubbedRunInfoDubResultRunIdGetResponse;
import types.DubbingResult;
import types.Languages;
import types.OrchestratorPipelineCallResult;
import types.OrchestratorPipelineResult;
import types.TaskStatus;
import java.util.Collections;
import java.util.Optional;

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
            // 1. Create Dubbing Task
            OrchestratorPipelineCallResult response = client.dub().endToEndDubbing(EndToEndDubbingRequestPayload.builder()
                .videoUrl("https://www.youtube.com/watch?v=dQw4w9WgXcQ") 
                .sourceLanguage(Languages.EN_US.getValue())
                .targetLanguages(Collections.singletonList(Languages.HI_IN.getValue()))
                .build());

            String taskId = response.getTaskId().orElseThrow(() -> new RuntimeException("Failed to get task ID"));
            System.out.println("Dubbing Task Created. Task ID: " + taskId);

            // 2. Poll for status
            System.out.println("Polling for dubbing status...");
            int attempts = 0;
            int maxAttempts = 60; // Wait up to 5 minutes

            while (attempts < maxAttempts) {
                OrchestratorPipelineResult statusResponse = client.dub().getEndToEndDubbingStatus(taskId);
                TaskStatus status = statusResponse.getStatus();
                System.out.println("Current Status: " + status);

                if (status == TaskStatus.SUCCESS) {
                    System.out.println("Dubbing completed successfully!");
                    Integer runId = statusResponse.getRunId().orElseThrow(() -> new RuntimeException("Run ID missing on success"));
                    
                    // 3. Get results
                    GetDubbedRunInfoDubResultRunIdGetResponse wrappedResult = client.dub().getDubbedRunInfo(Optional.of(runId));
                    
                    // The response can be a single DubbingResult or a Map. Use the Visitor or check type.
                    wrappedResult.visit(new GetDubbedRunInfoDubResultRunIdGetResponse.Visitor<Void>() {
                        @Override
                        public Void visit(DubbingResult result) {
                            System.out.println("Dubbed Result Details:");
                            System.out.println(" - Dubbed Video URL: " + result.getVideoUrl().orElse("N/A"));
                            System.out.println(" - Dubbed Audio URL: " + result.getAudioUrl());
                            System.out.println(" - Transcript matches: " + result.getTranscript().size());
                            return null;
                        }

                        @Override
                        public Void visit(java.util.Map<String, DubbingResult> value) {
                            System.out.println("Multiple Dubbed Results Found:");
                            value.forEach((lang, res) -> {
                                System.out.println("Language: " + lang + " -> URL: " + res.getAudioUrl());
                            });
                            return null;
                        }
                    });
                    break;
                } else if (status == TaskStatus.ERROR) {
                    System.err.println("Dubbing task failed: " + statusResponse.getMessage());
                    break;
                }

                Thread.sleep(5000); // Wait 5 seconds
                attempts++;
            }

            if (attempts >= maxAttempts) {
                System.err.println("Timeout: Dubbing task did not complete in time.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
