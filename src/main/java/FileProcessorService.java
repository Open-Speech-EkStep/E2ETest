import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.io.File;
import dto.FileProcessor;
import java.util.Map;




public class FileProcessorService {

    private String newfilepath="src/main/resources/newconfig.yml";
    public FileProcessorService() {};




    public String processFile(FileProcessor fileProcessor) {

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        File file = new File(classLoader.getResource("config.yml").getFile());
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        try {

            Map<String, Object> conf = (Map<String, Object>) mapper.readValue(file,Map.class);
            Map<String, Object> downloader = (Map<String, Object>) conf.get("downloader");

            downloader.put("link",fileProcessor.getLink());
            downloader.put("mdoe",fileProcessor.getMode());
            downloader.put("filename_prefix",fileProcessor.getFilename_prefix());

            mapper.writeValue(new File(newfilepath), downloader);

        }


        catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        return newfilepath  ; // return file path
    }
}