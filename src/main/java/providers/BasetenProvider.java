package providers;

import resources.texttospeech.requests.CreateStreamTtsRequestPayload;
import core.RequestOptions;
import java.io.InputStream;
import java.io.IOException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.MediaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;

public class BasetenProvider implements ITtsProvider {
    private final String apiKey;
    private final String url;
    private final OkHttpClient httpClient;
    private final ObjectMapper objectMapper;

    public BasetenProvider(String apiKey, String url) {
        this.apiKey = apiKey;
        this.url = url != null ? url : "https://model-5qeryx53.api.baseten.co/environments/production/predict";
        this.httpClient = new OkHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public InputStream tts(CreateStreamTtsRequestPayload request, RequestOptions requestOptions) {
        // NOTE: In a real scenario, you'd extend CreateStreamTtsRequestPayload or use a custom DTO 
        // to pass reference_audio/language. For this example, we assume they are passed via a side channel 
        // or we'd cast a custom subclass. 
        // Java's strong typing makes this trickier than JS/Python without altering the generated class.
        // Assuming we have reference audio/language from somewhere:
        
        String referenceAudio = "DUMMY_BASE64..."; // Placeholder
        String referenceLanguage = "en-us";

        Map<String, Object> payload = new HashMap<>();
        payload.put("text", request.getText());
        payload.put("stream", true);
        payload.put("output_format", "mp3");
        payload.put("language", request.getLanguage().toString()); // Use .toString() or map Enum
        payload.put("reference_audio", referenceAudio);
        payload.put("audio_ref", referenceAudio);
        payload.put("reference_language", referenceLanguage);
        payload.put("apply_ner_nlp", false);

        try {
            String json = objectMapper.writeValueAsString(payload);
            RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));
            
            Request req = new Request.Builder()
                .url(this.url)
                .addHeader("Authorization", "Api-Key " + this.apiKey)
                .post(body)
                .build();

            Response response = httpClient.newCall(req).execute();
            if (!response.isSuccessful()) {
                throw new RuntimeException("Baseten Error: " + response.code());
            }
            return response.body().byteStream();

        } catch (IOException e) {
            throw new RuntimeException("Network error", e);
        }
    }
}
