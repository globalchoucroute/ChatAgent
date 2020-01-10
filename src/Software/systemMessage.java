package Software;

import java.io.Serializable;

public class systemMessage implements Serializable {
    private static final long serialVersionUID = 3333L;

    public String instruction;
    public userData userData;
    public int port;

    public systemMessage(String instruction, userData u, int p){
        this.instruction=instruction;
        userData=u;
        port=p;
    }

}
