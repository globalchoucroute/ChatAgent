package Software;

import GUI.chatWindow;

public class session {

    //Attributes
    public chatSession chatSession;
    public chatWindow chatWindow;
    public chatMessages chatMessages;
    public String otherUsername;
    public String username;

    public session(int port, userData otherUserData, boolean isServer, String myUsername){
        otherUsername = otherUserData.getUsername();
        username = myUsername;
        chatSession = new chatSession(port, otherUserData, isServer);
        chatWindow = new chatWindow(username, otherUsername, chatSession);
    }

    public chatSession getChatSession(){
        return chatSession;
    }

    public chatWindow getChatWindow(){
        return chatWindow;
    }

    public void sendMessage(String message){
        chatSession.sendMessage(message);
        chatWindow.messageDisplay.append("\n" + username + " : " + message);
    }

}

