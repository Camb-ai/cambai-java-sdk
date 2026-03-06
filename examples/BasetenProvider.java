
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

/**
 * Baseten TTS Provider using the Mars8-Flash model.
 *
 * API reference: https://www.baseten.co/library/mars8-flash/
 *
 * Constructor parameters:
 *   apiKey            - Baseten API key
 *   url               - Baseten model prediction endpoint
 *   referenceAudio    - Reference voice: public URL or base64-encoded audio file
 *   referenceLanguage - ISO locale of the reference audio (e.g. "en-us")
 */
public class BasetenProvider implements ITtsProvider {
    private final String apiKey;
    private final String url;
    private final String referenceAudio;
    private final String referenceLanguage;
    private final OkHttpClient httpClient;
    private final ObjectMapper objectMapper;

    public BasetenProvider(String apiKey, String url, String referenceAudio, String referenceLanguage) {
        this.apiKey = apiKey;
        this.url = url;
        this.referenceAudio = referenceAudio;
        this.referenceLanguage = referenceLanguage;
        this.httpClient = new OkHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public InputStream tts(CreateStreamTtsRequestPayload request, RequestOptions requestOptions) {
        // Normalise language: SDK enum is a string type, ensure lowercase ISO format.
        String language = request.getLanguage().toString().toLowerCase().replace("_", "-");

        // Build the Mars8-Flash payload.
        // Docs: https://www.baseten.co/library/mars8-flash/
        Map<String, Object> payload = new HashMap<>();
        payload.put("text", request.getText());
        payload.put("language", language);
        payload.put("output_duration", null);          // null = model infers optimal duration
        payload.put("reference_audio", referenceAudio);
        payload.put("reference_language", referenceLanguage);
        payload.put("output_format", "flac");          // flac is the default; wav also supported
        payload.put("apply_ner_nlp", false);           // disable NER (faster; pass pronunciation_dictionary instead)

        // Optional: override output format from request output configuration
        request.getOutputConfiguration().ifPresent(config ->
            config.getFormat().ifPresent(f -> payload.put("output_format", f.toString().toLowerCase()))
        );

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
                String errorBody = response.body() != null ? response.body().string() : "<no body>";
                throw new RuntimeException("Baseten API error " + response.code() + ": " + errorBody);
            }
            return response.body().byteStream();

        } catch (IOException e) {
            throw new RuntimeException("Network error calling Baseten: " + e.getMessage(), e);
        }
    }
}
