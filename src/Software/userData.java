package Software;

import java.io.Serializable;

public class userData implements Serializable {
    private static final long serialVersionUID = 3334L;

    public String username;
    private String macAddress;
    private String IPAddress;
    private String status;

    public userData(String u, String a, String i, String s){
        username = u;
        macAddress = a;
        IPAddress = i;
        status = s;
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

    public String getStatus() {return status;}
}
