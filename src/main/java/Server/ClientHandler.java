/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import Constants.Command;
import Constants.StatusCode;
import Constants.SystemCode;
import Requests.BaseRequest;
import Requests.LoadChatRequest;
import Requests.PrivateChatRequest;
import Requests.RegisterRequest;
import Requests.SaveChatRequest;
import Requests.ValidateRequest;
import Responses.AckResponse;
import Responses.BaseResponse;
import Responses.LoadChatResponse;
import Responses.OnlineUsersResponse;
import Responses.PrivateChatResponse;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Admin
 */
public class ClientHandler extends Thread implements Serializable{
    private transient Socket clientSocket;
    private String uid;
    private transient Server server;
    private String username;
    private Map<String,String> chatHistory = new HashMap<>();

    public Map<String, String> getChatHistory() {
        return chatHistory;
    }

    public void setChatHistory(Map<String, String> chatHistory) {
        this.chatHistory = chatHistory;
    }
    
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    

    public Socket getClientSocket() {
        return clientSocket;
    }

    public void setClientSocket(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }
       
    public ClientHandler(Socket socket, Server server) throws IOException {
        this.clientSocket = socket;
        this.server = server;
        this.uid = UUID.randomUUID().toString();
    }
    @Override
    public void run() {
        try{
            while(true){
                ObjectInputStream in= new ObjectInputStream(clientSocket.getInputStream());
                Object reqObject = in.readObject();
                handleRequest(reqObject);
            }            
        }catch (IOException | ClassNotFoundException ex) {
        }finally {
            try {
                if (clientSocket != null) {
                    clientSocket.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public void notifyDisconnection() throws IOException{
        BaseResponse ackRes = new AckResponse(uid, this.username, SystemCode.SERVER_DISCONNNECT, StatusCode.OK);
        ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
        out.writeObject(ackRes);
        out.flush();        
    }
    
    public String formatMessage(String username, String message, boolean isYou){       
        StringBuilder sb = new StringBuilder();
        String s;
        if (!isYou){
            s = "<p><span style=\"color:red\">" + username + ":" + "</span>";            
        }else {
            s = "<p><span style=\"color:green\">" + username + ":" + "</span>";                        
        }
        sb.append(s);
        sb.append(message.replaceAll("\n", "<br>")).append("</p>");
        return sb.toString();
    }
    
    public void handleRequest(Object reqObject) throws IOException{
        if (reqObject!=null){
            BaseRequest req = (BaseRequest) reqObject;
            Command command = req.getCommand();
            switch (command){
                case GET_ONLINE_USERS -> {
                    BaseResponse res = new OnlineUsersResponse(server.getClients(), StatusCode.OK);
                    ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
                    out.writeObject(res);
                    out.flush();
                    break;
                }
                case DISCONNECT -> {
                    Map<String,ClientHandler> users = server.getClients();
                    users.remove(this.getUid());
                    server.setClients(users);
                    server.removeUsers(this.getUsername());
                    server.logger("User " + this.getUsername() + " disconnected\n");
                    this.stop();
                    //send broadcast
                    BaseResponse ackRes = new AckResponse(uid, this.username, SystemCode.USER_DISCONNNECT, StatusCode.OK);
                    for (ClientHandler client : server.getClients().values()){
                        Socket socket = client.getClientSocket();
                        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                        out.writeObject(ackRes);
                        out.flush();
                    }
                    break;
                }
                case PRIVATE_CHAT -> {
                    String receiverName = ((PrivateChatRequest) req).getReceiver();
                    ClientHandler receiverHandler = server.getClients().get(receiverName);
                    String message = ((PrivateChatRequest) req).getMessage();
                    
                    //save to history chat
                    String oldHistory = chatHistory.get(receiverName);
                    String newHistory;
                    if (oldHistory == null){
                       newHistory = formatMessage(username, message, true);
                    }else{
                       newHistory = oldHistory + formatMessage(username, message, true);
                    }
                    chatHistory.put(receiverName, newHistory);
                    
                    if (receiverHandler != null){
                        String receiver = ((PrivateChatRequest) req).getReceiver();
                        //send message
                        BaseResponse res = new PrivateChatResponse(message,username,StatusCode.OK);
                        ObjectOutputStream out = new ObjectOutputStream(receiverHandler.getClientSocket().getOutputStream());
                        out.writeObject(res);
                        out.flush();
                        //return ack to sender
                        BaseResponse ackRes = new AckResponse(receiver, message, SystemCode.MESSAGE_SENT, StatusCode.OK);
                        out = new ObjectOutputStream(clientSocket.getOutputStream());
                        out.writeObject(ackRes);
                        out.flush();
                        break;
                    }else{
                        BaseResponse res = new PrivateChatResponse(StatusCode.NOT_FOUND);
                        res.setError("User not found or offline");
                        ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
                        out.writeObject(res);
                        out.flush();
                        break;                        
                    }
                }
                case GET_UID -> {
                    BaseResponse ackRes = new AckResponse(uid, null, SystemCode.GET_UID, StatusCode.OK);
                    ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
                    out.writeObject(ackRes);
                    out.flush();
                    break;                    
                }
                case VALIDATE_USERNAME -> {
                    Map<String,ClientHandler> users = server.getClients();
                    if (users == null) users = new HashMap<>();
                    String name = ((ValidateRequest)req).getUsername();
                    if (users.get(name)==null){
                        BaseResponse ackRes = new AckResponse(uid, name, SystemCode.AVAILABLE_NAME, StatusCode.OK);
                        ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
                        out.writeObject(ackRes);
                        out.flush();                        
                    }else{
                        BaseResponse ackRes = new AckResponse(uid, "Username is already existed", SystemCode.EXISTING_USERNAME, StatusCode.OK);
                        ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
                        out.writeObject(ackRes);
                        out.flush();                         
                    }
                    break;                      
                }
                case REGISTER -> {
                    String name = ((RegisterRequest)req).getUsername();
                    this.setUsername(name);
                    this.server.getClients().put(this.username, this);
                    server.logger("User " + name +" (id: " + this.uid + ") " + "connected\n");
                    server.addUserToList(name);
                    //send ack back to user
                    BaseResponse ackRes = new AckResponse(uid, this.username, SystemCode.USER_CONNECTED, StatusCode.OK);
                    for (ClientHandler client : server.getClients().values()){
                        if (client==this) continue;
                        Socket socket = client.getClientSocket();
                        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                        out.writeObject(ackRes);
                        out.flush();
                    }
                    ackRes = new AckResponse(uid, this.username, SystemCode.REGISTERED, StatusCode.OK);
                    ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
                    out.writeObject(ackRes);
                    out.flush();                    
                    break;
                }
                case SAVE_CHAT -> {
                    String senderName = ((SaveChatRequest)req).getSender();
                    String message = ((SaveChatRequest)req).getMessage();
                    String newHistory;
                    String oldHistory = chatHistory.get(senderName);
                    if (oldHistory == null){
                        newHistory = formatMessage(senderName, message, false);
                    }else {
                        newHistory = oldHistory + formatMessage(senderName, message, false);
                    }
                    chatHistory.put(senderName, newHistory);
                    break;
                }
                case LOAD_CHAT -> {
                    String target = ((LoadChatRequest) req).getTarger();
                    String history = chatHistory.get(target);
                    if (history!=null){
                        LoadChatResponse loadRes = new LoadChatResponse(history,StatusCode.OK);
                        ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
                        out.writeObject(loadRes);
                        out.flush();                         
                    }else{
                        LoadChatResponse loadRes = new LoadChatResponse("",StatusCode.OK);
                        ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
                        out.writeObject(loadRes);
                        out.flush();                        
                    }
                    break;
                }
                default -> {
                    ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
                    out.writeObject(null);
                    out.flush();                     
                    break;
                }
            }   
        }
    }
}
