package restCommunication;
import lombok.Data;
import org.apache.http.Header;


@Data
public class RestResponse {
    private String response;
    private int statusCode;
    private String statusMessage;
    private String  status;
    private String calltime;
    private Header[]  httpHeaders;
}

