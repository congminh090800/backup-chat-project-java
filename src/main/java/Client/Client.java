/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import Constants.Command;
import GUI.ChatApplication;
import Requests.BaseRequest;
import Requests.LoadChatRequest;
import Requests.NoParamRequest;
import Requests.PrivateChatRequest;
import Requests.RegisterRequest;
import Requests.SaveChatRequest;
import Requests.ValidateRequest;
import Server.ClientHandler;
import Server.Server;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.JScrollBar;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

/**
 *
 * @author Admin
 */
public class Client implements Serializable{
    private Socket clientSocket;
    private List<ClientHandler> onlineUsers;
    private Scanner scanner;
    private boolean connecting;
    private String uid;
    private String username;
    private ChatApplication chatApp;

    public ChatApplication getChatApp() {
        return chatApp;
    }

    public void setChatApp(ChatApplication chatApp) {
        this.chatApp = chatApp;
    }
    

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Client() {
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
    
    public Scanner getScanner() {
        return scanner;
    }

    public void setScanner(Scanner scanner) {
        this.scanner = scanner;
    }    

    public boolean isConnecting() {
        return connecting;
    }

    public void setConnecting(boolean connecting) {
        this.connecting = connecting;
    } 

    public List<ClientHandler> getOnlineUsers() {
        return onlineUsers;
    }

    public void setOnlineUsers(List<ClientHandler> onlineUsers) {
        this.onlineUsers = onlineUsers;
        DefaultListModel listModel = new DefaultListModel<>();
        onlineUsers.forEach((t) -> {
            listModel.addElement(t.getUsername());
        });
        SwingUtilities.invokeLater(() -> {
            this.chatApp.getOnlineList().setModel(listModel);
        });
    }

    public Socket getClientSocket() {
        return clientSocket;
    }
    
    public void setClientSocket(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }
    
    public void fetchOnlineUsers()throws IOException{
        BaseRequest req = new NoParamRequest(Command.GET_ONLINE_USERS);
        ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
        out.writeObject(req);
        out.flush();
    }
    
    public void disconnect() throws IOException{
        BaseRequest req = new NoParamRequest(Command.DISCONNECT);
        ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
        out.writeObject(req);
        out.flush();
    }
    
    public void sendPrivateMessage(String message, String receiver) throws IOException{
        BaseRequest req = new PrivateChatRequest(message, receiver, Command.PRIVATE_CHAT);    
        ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
        out.writeObject(req);
        out.flush();
    }
    
    public void getOwnUid() throws IOException{
        BaseRequest req = new NoParamRequest(Command.GET_UID);      
        ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
        out.writeObject(req);
        out.flush();
    }
    public void validateUsername(String username) throws IOException{
        BaseRequest req = new ValidateRequest(username, Command.VALIDATE_USERNAME);      
        ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
        out.writeObject(req);
        out.flush();
    }
    public void register(String username) throws IOException{
        BaseRequest req = new RegisterRequest(username, Command.REGISTER);      
        ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
        out.writeObject(req);
        out.flush();
    }
    public void saveChat(String sender, String message) throws IOException{
        BaseRequest req = new SaveChatRequest(sender, message, Command.SAVE_CHAT);      
        ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
        out.writeObject(req);
        out.flush();        
    }
    public void loadChat(String target) throws IOException{
        BaseRequest req = new LoadChatRequest(target, Command.LOAD_CHAT);      
        ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
        out.writeObject(req);
        out.flush();       
    }
    public void connect(String ip, int port, String username) throws IOException{
        this.clientSocket = new Socket(ip, port);
        onlineUsers = new ArrayList<>();
        connecting = true;
        this.username = username;
        Thread responseHandler = new ResponseHandler(this);
        responseHandler.start();
        validateUsername(this.username);
        getOwnUid();
    }

    public void showOptionPane(String message, String title, int option){
        try {
            SwingUtilities.invokeAndWait(() -> {
                JOptionPane.showConfirmDialog(null, message, title, option);                                
            });
        } catch (InterruptedException | InvocationTargetException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }  
    }
    public String formatMessage(String username, String message, boolean isYou){       
        StringBuilder sb = new StringBuilder();
        String s;
        if (isYou){
            s = "<p><span style=\"color:red\">" + username + ":" + "</span>";            
        }else {
            s = "<p><span style=\"color:green\">" + username + ":" + "</span>";                        
        }
        sb.append(s);
        sb.append(message.replaceAll("\n", "<br>")).append("</p>");
        return sb.toString();
    }
    public void appendToChat(String formattedString){
        try {
            HTMLDocument doc = (HTMLDocument)getChatApp().getChatHistoryArea().getDocument();
            HTMLEditorKit editorKit = (HTMLEditorKit)getChatApp().getChatHistoryArea().getEditorKit();        
            editorKit.insertHTML(doc, doc.getLength(), formattedString, 0, 0, null);
            try {
                SwingUtilities.invokeAndWait(() -> {
                    JScrollBar sb = getChatApp().getChatHistoryScroll().getVerticalScrollBar();
                    sb.setValue(sb.getMaximum());
                });
            } catch (InterruptedException | InvocationTargetException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (BadLocationException | IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void uploadFile(File selectedFile) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
