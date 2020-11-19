package restCommunication;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;




public class RestClient {

    private ObjectMapper objectMapper = new ObjectMapper();

    public RestResponse get(String url, Map<String, String> paramMap, Map<String, String> headerMap) throws IOException, URISyntaxException {
        HttpGet get = new HttpGet(buildURIUsingRequestParams(url, paramMap));
        setRequestHeaders(get, headerMap);
        CloseableHttpResponse response = execute(get);
        return mapRestResponse(response);
    }

    public RestResponse post(String url, Map<String, String> paramMap, Map<String, String> headerMap, Map body) throws IOException, URISyntaxException {
        HttpPost post = new HttpPost(buildURIUsingRequestParams(url, paramMap));
        setRequestHeaders(post, headerMap);
        post.setEntity(getBody(body));
        CloseableHttpResponse response = execute(post);
        return mapRestResponse(response);
    }



    private CloseableHttpResponse execute(HttpUriRequest request) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        return httpClient.execute(request);
    }

    private HttpEntity getBody(Map body) throws JsonProcessingException, UnsupportedEncodingException {
        return new StringEntity(objectMapper.writeValueAsString(body));
    }

    private boolean is2xxSuccessful(int statusCode) {
        return statusCode >= 200 && statusCode < 300;
    }

    private URI buildURIUsingRequestParams(String uri, Map<String, String> paramMap) throws URISyntaxException {
        URIBuilder builder = new URIBuilder(uri);
        if (paramMap != null && !paramMap.isEmpty()) {
            paramMap.forEach((key, value) -> builder.addParameter(key, value));
        }
        return builder.build();
    }


    private void setRequestHeaders(HttpRequestBase request, Map<String, String> headerMap) {
        if (headerMap != null && !headerMap.isEmpty()) {
            headerMap.forEach((key, value) -> request.addHeader(key, value));
        }
        request.addHeader("content-type", "application/json");
    }


    private RestResponse mapRestResponse(CloseableHttpResponse response) throws IOException {
        RestResponse restResponse = new RestResponse();
        if (response != null) {
            restResponse.setStatusCode(response.getStatusLine().getStatusCode());
            restResponse.setResponse(EntityUtils.toString(response.getEntity()));
            restResponse.setStatusMessage(response.getStatusLine().getReasonPhrase());
            restResponse.setHttpHeaders(response.getAllHeaders());
            String status = is2xxSuccessful(response.getStatusLine().getStatusCode()) ? "SUCCESS" : "FAILURE";
            restResponse.setStatus(status);
        } else {
            restResponse.setStatus("FAILURE");
        }
        return restResponse;
    }
    public String getJSONvaluesfromResponse(RestResponse restResponse,String key) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonnode = mapper.readTree(restResponse.getResponse());
        JsonNode jsonNode1 = jsonnode.get(key);
        if (key.equals("output"))
        {
            JsonNode stdout = jsonNode1.get("stdout");
            return stdout.toString();
        }

        return jsonNode1.toString();
    }


}
