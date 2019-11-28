package Software;

public class connection {

    //Attributes
    private String username;
    protected String[][] activeList = new String[2][];

    //Methods
    boolean checkUsername(String usr){
        return true;
    }

    String[][] sendHello(String bdcastaddress){
        this.activeList = null;
        return activeList;
    }
}
