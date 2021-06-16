/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import Constants.StatusCode;
import Responses.AckResponse;
import Responses.DownloadResponse;
import Responses.LoadChatResponse;
import Responses.OnlineUsersResponse;
import Responses.PrivateChatResponse;
import Server.ClientHandler;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JScrollBar;
import javax.swing.SwingUtilities;
import static javax.swing.SwingUtilities.invokeAndWait;

/**
 *
 * @author Admin
 */
public class ResponseHandler extends Thread implements Serializable{
    private transient Client client;

    public ResponseHandler(Client client) {
        this.client = client;
    }
    
    @Override
    public void run() {
        try {
            while(true){
                ObjectInputStream in = new ObjectInputStream(client.getClientSocket().getInputStream());
                Object resObject = in.readObject();
                if (resObject == null){
                    continue;
                }
                if (resObject instanceof OnlineUsersResponse) {
                    OnlineUsersResponse res = (OnlineUsersResponse) resObject;
                    if (StatusCode.OK.equals(res.getStatus())) {
                        List<ClientHandler> list = new ArrayList<>();
                        res.getClients().values().forEach(c -> {
                            list.add(c);
                        });
                        client.setOnlineUsers(list);
                    } else {
                        client.showOptionPane(res.getStatus().getValue() + " " + res.getStatus(),"Failue",JOptionPane.DEFAULT_OPTION);
                    }
                }
                if (resObject instanceof PrivateChatResponse) {
                    PrivateChatResponse res = (PrivateChatResponse) resObject;
                    if (StatusCode.OK.equals(res.getStatus())){
                        if (client.getChatApp().getOnlineList().getSelectedValue()!=null && client.getChatApp().getOnlineList().getSelectedValue().equals(res.getSender())){
                            if (res.isIsAnchor()){
                                client.appendToChat(client.formatAnchor(res.getMessage(), res.getTimestamp(), res.getSender(), false));                                
                            }else{
                                client.appendToChat(client.formatMessage(res.getSender(), res.getMessage(), false));
                            }
                        }
                        if (res.isIsAnchor()){
                            client.saveLink(res.getMessage(), res.getTimestamp(), res.getSender());
                        }else{
                            client.saveChat(res.getSender(), res.getMessage());
                        }
                    }else if(StatusCode.NOT_FOUND.equals(res.getStatus())){
                        client.showOptionPane(StatusCode.NOT_FOUND +": "+ "Please select a user to chat", "FAILURE", JOptionPane.DEFAULT_OPTION);
                    }
                }
                if (resObject instanceof LoadChatResponse){
                    LoadChatResponse res = (LoadChatResponse) resObject;
                    if (StatusCode.OK.equals(res.getStatus())){
                        client.getChatApp().getChatHistoryArea().setText(res.getHistory());
                        try {
                            SwingUtilities.invokeAndWait(() -> {
                                JScrollBar sb = client.getChatApp().getChatHistoryScroll().getVerticalScrollBar();
                                sb.setValue(sb.getMaximum());
                            });
                        } catch (InterruptedException | InvocationTargetException ex) {
                            Logger.getLogger(ResponseHandler.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }else if(StatusCode.NOT_FOUND.equals(res.getStatus())){
                        client.showOptionPane(StatusCode.NOT_FOUND +": "+ res.getError(), "FAILURE", JOptionPane.DEFAULT_OPTION);
                    }                    
                }
                if (resObject instanceof DownloadResponse){
                    DownloadResponse res = (DownloadResponse) resObject;
                    if (res.isIsDone()){
                        client.showOptionPane("Download successfully", "Download file", JOptionPane.DEFAULT_OPTION);
                    }else{
                        String filename = res.getFilename();
                        int splitter = filename.indexOf('_');
                        if (splitter>0){
                            filename = filename.substring(splitter+1);
                        }
                        File file = new File(filename);
                        if (!file.exists()){
                            file.createNewFile();
                        }
                        try (FileOutputStream output = new FileOutputStream(file, true)) {
                            output.write(res.getData());
                        }                     
                    }
                }
                if (resObject instanceof AckResponse) {
                    AckResponse res = (AckResponse) resObject;
                    if (StatusCode.OK.equals(res.getStatus())){
                        switch(res.getSystemCode()){
                            case MESSAGE_SENT -> {
                                if (client.getChatApp().getOnlineList().getSelectedValue()!=null && client.getChatApp().getOnlineList().getSelectedValue().equals(res.getUid())){   
                                    client.appendToChat(client.formatMessage(client.getUsername(), res.getMessage(), true));
                                }
                                break;
                            }
                            case GET_UID -> {
                                client.setUid(res.getUid());
                                SwingUtilities.invokeLater(() -> {
                                    client.getChatApp().getUserInfoLabel().setText("username: " + client.getUsername() +"  |  uid: " + client.getUid());
                                });
                                break;
                            }
                            case EXISTING_USERNAME -> {
                                client.disconnect();
                                client.showOptionPane(res.getMessage(), res.getMessage(), JOptionPane.DEFAULT_OPTION);
                                break;
                            }
                            case AVAILABLE_NAME -> {
                                String name = res.getMessage();
                                client.register(name);
                                break;
                            }
                            case REGISTERED -> {      
                                client.fetchOnlineUsers();
                                break;
                            }
                            case USER_DISCONNNECT -> {
                                String name = res.getMessage();
                                List<ClientHandler> online = client.getOnlineUsers();
                                online.removeIf((t) -> {
                                    return t.getUsername().equals(name);
                                });
                                client.setOnlineUsers(online);
                                break;
                            }
                            case USER_CONNECTED -> {
                                client.fetchOnlineUsers();
                                break;
                            }
                            case SERVER_DISCONNNECT -> {
                                client.showOptionPane("Server will not response until on", "Server is closed!!", JOptionPane.DEFAULT_OPTION);
                                break;
                            }
                            case UPLOAD_SUCCESSFUL -> {
                                Date timestamp = client.getUploadHandler().getTimestamp();
                                String filename = client.getUploadHandler().getSelectedFile().getName();
                                try {
                                    invokeAndWait(() -> {
                                        client.getChatApp().getUploadProgress().setValue(0);
                                        client.getChatApp().getUploadBtn().setEnabled(true);
                                    });
                                } catch (InterruptedException | InvocationTargetException ex) {
                                    Logger.getLogger(ResponseHandler.class.getName()).log(Level.SEVERE, null, ex);
                                }
                                client.appendToChat(client.formatAnchor(filename, timestamp, client.getUsername(), true));
                                String receiver = client.getChatApp().getOnlineList().getSelectedValue();
                                client.sendLink(filename,timestamp,receiver);
                                client.getUploadHandler().getUploadSocket().close();
                                break;
                            }
                            case UPLOAD_FAILED -> {
                                try {
                                    invokeAndWait(() -> {
                                        client.getChatApp().getUploadProgress().setValue(0);
                                        client.getChatApp().getUploadBtn().setEnabled(true);
                                        client.showOptionPane(res.getStatus()+":"+res.getMessage(), "Upload file", JOptionPane.DEFAULT_OPTION);
                                        client.getUploadHandler().setRunning(false);
                                    });
                                } catch (InterruptedException | InvocationTargetException ex) {
                                    Logger.getLogger(ResponseHandler.class.getName()).log(Level.SEVERE, null, ex);
                                }
                                client.getUploadHandler().getUploadSocket().close();
                                break;
                            }
                            case DOWNLOAD_FAIL -> {
                                client.showOptionPane(res.getStatus()+":"+res.getMessage(), "Download file", JOptionPane.DEFAULT_OPTION);
                                break;
                            }
                            default -> {
                                break;
                            }
                        }
                    }
                }
            }
        } catch (IOException | ClassNotFoundException ex) {
        }
    }
    
}
