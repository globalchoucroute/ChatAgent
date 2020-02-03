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

    /**
     * Generates a System Message.
     * @param instruction is the instruction to send, which usually is a keyword like "begin", "disconnect", "change".
     * @param u is the user data of the user who sends the message.
     * @param p is only needed when sending a System Message for the beginning of a Chat Session. It's the port on which the other user needs to connect.
     */
    public systemMessage(String instruction, userData u, int p){
        this.instruction=instruction;
        userData=u;
        port=p;
    }

}
