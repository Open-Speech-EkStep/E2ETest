import org.testng.Assert;
import org.testng.annotations.Test;

public class TestDownloadUtility {

    public JobProcessor jobProcessor = new JobProcessor();

    @Test (enabled = false)
    public void testVideoMode() {
        String url = "https://www.youtube.com/watch?v=ruqMj-RQ_zA";
        jobProcessor.porcessor(url,"video","cluster","enc-2","yey1");
       // Assert true;

    }


    @Test (enabled = false)
    public void testPlaylistMode() {

    }


    @Test (enabled = false)
    public void testVideolistMode() {

    }

}
