package Software;

public class userData {
    public String username;
    public String macAddress;
    public String IPAddress;

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

    public void setMacAddress(String a){
        macAddress=a;
    }
    public String getMacAddress() {
        return macAddress;
    }

    public void setIPAddress(String i) {IPAddress=i;}
    public String getIPAddress() {return IPAddress;}
}
