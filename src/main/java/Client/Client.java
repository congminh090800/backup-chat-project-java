/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import Constants.Command;
import Constants.env;
import GUI.ChatApplication;
import Requests.BaseRequest;
import Requests.DownloadRequest;
import Requests.LoadChatRequest;
import Requests.NoParamRequest;
import Requests.PrivateChatRequest;
import Requests.RegisterRequest;
import Requests.SaveChatRequest;
import Requests.UploadRequest;
import Requests.ValidateRequest;
import Server.ClientHandler;
import Server.Server;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;
import java.security.Timestamp;
import java.util.ArrayList;
import java.util.Date;
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
    private UploadHandler uploadHandler;   
    public UploadHandler getUploadHandler() {
        return uploadHandler;
    }

    public void setUploadHandler(UploadHandler uploadHandler) {
        this.uploadHandler = uploadHandler;
    }

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
    public void sendRequest(BaseRequest req){
        try {
            ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
            out.writeObject(req);        
            out.flush();
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void fetchOnlineUsers(){
        BaseRequest req = new NoParamRequest(Command.GET_ONLINE_USERS);
        sendRequest(req);
    }
    
    public void disconnect(){
        BaseRequest req = new NoParamRequest(Command.DISCONNECT);
        sendRequest(req);
    }
    
    public void sendPrivateMessage(String message, String receiver){
        BaseRequest req = new PrivateChatRequest(message, receiver, Command.PRIVATE_CHAT);    
        sendRequest(req);
    }
    
    public void getOwnUid(){
        BaseRequest req = new NoParamRequest(Command.GET_UID);      
        sendRequest(req);
    }
    public void validateUsername(String username){
        BaseRequest req = new ValidateRequest(username, Command.VALIDATE_USERNAME);      
        sendRequest(req);
    }
    public void register(String username){
        BaseRequest req = new RegisterRequest(username, Command.REGISTER);      
        sendRequest(req);
    }
    public void saveChat(String sender, String message){
        BaseRequest req = new SaveChatRequest(sender, message, Command.SAVE_CHAT);      
        sendRequest(req);       
    }
    public void saveLink(String filename, Date timestamp, String sender){
        BaseRequest req = new SaveChatRequest(sender,filename,true,timestamp,Command.SAVE_CHAT);
        sendRequest(req);
    }
    public void loadChat(String target){
        BaseRequest req = new LoadChatRequest(target, Command.LOAD_CHAT);      
        sendRequest(req);       
    }
    public void sendLink(String filename, Date timestamp, String receiver){
        BaseRequest req = new PrivateChatRequest(filename ,receiver ,true ,timestamp ,Command.PRIVATE_CHAT);
        sendRequest(req);
    }
    public void uploadFile(File selectedFile){
        uploadHandler = new UploadHandler(selectedFile);
        uploadHandler.start();
    }
    public void downloadFile(String filename){ 
        String name = filename;
        int splitter = name.indexOf('_');
        if (splitter>0){
            name = name.substring(splitter+1);
        } 
        File file = new File(name);       
        if (file.exists()){
            int option = JOptionPane.showConfirmDialog(null,"This file is already existed, Do you want to override it?", "Warning", JOptionPane.YES_NO_OPTION );
            if (option == JOptionPane.YES_OPTION){
                file.delete();
                BaseRequest req = new DownloadRequest(filename, Command.DOWNLOAD_FILE);
                sendRequest(req);            
            }        
        }else{
            BaseRequest req = new DownloadRequest(filename, Command.DOWNLOAD_FILE);
            sendRequest(req);           
        }
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
        if (!isYou){
            s = "<p><span style=\"color:red\">" + username + ":" + "</span>";            
        }else {
            s = "<p><span style=\"color:green\">" + username + ":" + "</span>";                        
        }
        sb.append(s);
        sb.append(message.replaceAll("\n", "<br>")).append("</p>");
        return sb.toString();
    }
    
    public String formatAnchor (String filename, Date timestamp, String username, boolean isYou){
        StringBuilder sb = new StringBuilder();
        String file = timestamp.getTime() + "_" + filename;
        String s;
        if (!isYou){
            s = "<p><span style=\"color:red\">" + username + ":" + "</span>";            
        }else {
            s = "<p><><span style=\"color:green\">" + username + ":" + "</span>";                        
        }
        sb.append(s);
        sb.append("<a href='").append(file).append("'>").append(filename).append("</a>");
        sb.append("</p>");
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
    
    public class UploadHandler extends Thread implements Serializable{
        private boolean running;
        private File selectedFile;
        private int order;
        private Socket uploadSocket;
        private Date timestamp;

        public File getSelectedFile() {
            return selectedFile;
        }

        public void setSelectedFile(File selectedFile) {
            this.selectedFile = selectedFile;
        }

        public Date getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(Date timestamp) {
            this.timestamp = timestamp;
        }
        
        public UploadHandler(File selectedFile) {
            this.selectedFile = selectedFile;
            try {
                uploadSocket = new Socket(clientSocket.getInetAddress().getHostAddress(),clientSocket.getPort());
            } catch (IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
            this.timestamp = new Date();
        }

        public boolean isRunning() {
            return running;
        }

        public void setRunning(boolean running) {
            this.running = running;
        }

        public Socket getUploadSocket() {
            return uploadSocket;
        }

        public void setUploadSocket(Socket uploadSocket) {
            this.uploadSocket = uploadSocket;
        }
        
        public void uploadSnippet(int order, byte[] data, String fileName, boolean isLast){
            BaseRequest req = new UploadRequest(order, data, fileName, isLast, username, timestamp, Command.UPLOAD_FILE);
            try {
                ObjectOutputStream out = new ObjectOutputStream(uploadSocket.getOutputStream());
                out.writeObject(req);        
                out.flush();
            } catch (IOException ex) {
                try {
                    SwingUtilities.invokeAndWait(() -> {
                        chatApp.getUploadProgress().setValue(0);
                        chatApp.getUploadBtn().setEnabled(true);
                    });
                    JOptionPane.showConfirmDialog(null, "Failed to upload! please try again","FAILURE", JOptionPane.DEFAULT_OPTION);
                    running = false;
                    uploadSocket.close();
                } catch (InterruptedException | InvocationTargetException | IOException ex1) {
                    Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex1);
                }
            }
        }
        
        @Override
        public void run(){ 
            FileInputStream in = null;
            try {
                try {
                    SwingUtilities.invokeAndWait(() -> {
                        chatApp.getUploadBtn().setEnabled(false);
                    });
                } catch (InterruptedException | InvocationTargetException ex) {
                    Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                }
                running = true;
                order = 1;
                String path = selectedFile.getAbsolutePath();
                String fileName = selectedFile.getName();
                final long fileSize = selectedFile.length();
                byte data[] = new byte[env.SNIPPET_SIZE];
                int snippetSize;
                in = new FileInputStream(path);
                while (running && (snippetSize = in.read(data))>0){
                    uploadSnippet(order, data, fileName, false);
                    if (snippetSize!=-1){
                        int percent = (int)(((long)(order*env.SNIPPET_SIZE))*100.0/fileSize +0.5);
                        try {
                            SwingUtilities.invokeAndWait(() -> {
                                chatApp.getUploadProgress().setValue((int) percent);
                            });
                        } catch (InterruptedException | InvocationTargetException ex) {
                            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    order++;
                }
                uploadSnippet(order, null, fileName, true);
                try {
                    SwingUtilities.invokeAndWait(() -> {
                        chatApp.getUploadProgress().setValue(100);
                    });
                } catch (InterruptedException | InvocationTargetException ex) {
                    Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (FileNotFoundException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    in.close();
                } catch (IOException ex) {
                    Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        
    }
}
