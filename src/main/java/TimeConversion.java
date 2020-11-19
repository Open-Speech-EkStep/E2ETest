
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.sql.Timestamp;
import java.util.Date;
import java.util.TimeZone;

public class TimeConversion {

    public static String scheduletime()
    {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        Instant instant = timestamp.toInstant();
        SimpleDateFormat sdf;
        instant =  instant.plusSeconds(10);
        Date date = Date.from(instant);
        sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        String formatteddate = sdf.format(date);
        System.out.println(formatteddate);
        return formatteddate;

    }


}
