package Software;

public class userData {
    public String username;
    public String address;

    public userData(String u, String a){
        username = u;
        address = a;
    }

    public void setUsername(String u){
        username=u;
    }
    public String getUsername() {
        return username;
    }

    public void setAddress(String a){
        address=a;
    }
    public String getAddress() {
        return address;
    }

}
