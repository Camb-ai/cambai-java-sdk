package providers;

import resources.texttospeech.requests.CreateStreamTtsRequestPayload;
import core.RequestOptions;
import java.io.InputStream;
import CambApiClient;

public class DefaultProvider implements ITtsProvider {
    private final CambApiClient client;

    public DefaultProvider(CambApiClient client) {
        this.client = client;
    }

    @Override
    public InputStream tts(CreateStreamTtsRequestPayload request, RequestOptions requestOptions) {
        return client.textToSpeech().tts(request, requestOptions);
    }
}
