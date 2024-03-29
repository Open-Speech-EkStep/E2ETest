import Constants.Constants;
import com.google.api.client.http.*;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.IdTokenCredentials;
import com.google.auth.oauth2.IdTokenProvider;
import com.google.common.base.Preconditions;
import java.io.IOException;
import java.util.Collections;


public class TokenGenerator implements Constants {



    private static final HttpTransport httpTransport = new NetHttpTransport();
    private TokenGenerator() {}

    private static IdTokenProvider getIdTokenProvider() throws IOException {
        GoogleCredentials credentials =
                GoogleCredentials.getApplicationDefault().createScoped(Collections.singleton(IAM_SCOPE));

        Preconditions.checkNotNull(credentials, "Expected to load credentials");
        Preconditions.checkState(
                credentials instanceof IdTokenProvider,
                String.format(
                        "Expected credentials that can provide id tokens, got %s instead",
                        credentials.getClass().getName()));
        return (IdTokenProvider) credentials;
    }

    /**
     * Clone request and add an IAP Bearer Authorization header with signed JWT token.
     *
     * @param request Request to add authorization header
     * @param iapClientId OAuth 2.0 client ID for IAP protected resource
     * @return Clone of request with Bearer style authorization header with signed jwt token.
     * @throws IOException exception creating signed JWT
     */
    public static HttpRequest buildIapRequest(HttpRequest request, String iapClientId)
            throws IOException {

        IdTokenProvider idTokenProvider = getIdTokenProvider();
        IdTokenCredentials credentials =
                IdTokenCredentials.newBuilder()
                        .setIdTokenProvider(idTokenProvider)
                        .setTargetAudience(iapClientId)
                        .build();

        HttpRequestInitializer httpRequestInitializer = new HttpCredentialsAdapter(credentials);

        return httpTransport
                .createRequestFactory(httpRequestInitializer)
                .buildRequest(request.getRequestMethod(), request.getUrl(), request.getContent());
    }



    public static String getToken() throws IOException {

        HttpRequestFactory requestFactory  = new NetHttpTransport().createRequestFactory();
        HttpRequest request = requestFactory.buildGetRequest(new GenericUrl(COMPOSER_ENDPOINT));
        request = buildIapRequest(request,CLIENT_ID);
        // System.out.println(request.getHeaders().getAuthorization());
        return request.getHeaders().getAuthorization();

    }


}