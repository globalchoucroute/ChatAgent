package Software;

import java.io.Serializable;

public class systemMessage implements Serializable {

    //Serialized version of the system message.
    //  It contains the userData of the user sending it, and the associated instruction;
    //  For the instruction asking the beginning of the session, a port number is also specified
    private static final long serialVersionUID = 3333L;

    String instruction;
    public userData userData;
    int port;

    public systemMessage(String instruction, userData u, int p){
        this.instruction=instruction;
        userData=u;
        port=p;
    }

}
