package providers;

import resources.texttospeech.requests.CreateStreamTtsRequestPayload;
import core.RequestOptions;
import java.io.InputStream;

public interface ITtsProvider {
    InputStream tts(CreateStreamTtsRequestPayload request, RequestOptions requestOptions);
}
