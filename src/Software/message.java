package Software;
import java.util.Date;
import java.sql.Timestamp;

public class message {
    //Attributes
    public String content;
    public Timestamp timestamp;

    public message(){

    }

    public String getContent(){
        return content;
    }

    public Timestamp getTimestamp(){
        return timestamp;
    }
}
