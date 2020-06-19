package dto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FileProcessor {

    private FileProcessor(){}

    public String link;
    public String mode;
    public String filename_prefix;

// only definition
}
