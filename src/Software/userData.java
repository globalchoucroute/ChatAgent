package Software;

import java.io.Serializable;

public class userData implements Serializable {
    private static final long serialVersionUID = 3334L;

    public String username;
    private String macAddress;
    private String IPAddress;

    public userData(String u, String a, String i){
        username = u;
        macAddress = a;
        IPAddress = i;
    }

    public void setUsername(String u){
        username=u;
    }
    public String getUsername() {
        return username;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public String getIPAddress() {return IPAddress;}
}
