/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import Constants.StatusCode;
import Responses.AckResponse;
import Responses.LoadChatResponse;
import Responses.OnlineUsersResponse;
import Responses.PrivateChatResponse;
import Server.ClientHandler;
import java.awt.Rectangle;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JScrollBar;
import javax.swing.SwingUtilities;
import javax.swing.text.DefaultCaret;
import org.w3c.dom.css.Rect;

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
                            client.appendToChat(client.formatMessage(res.getSender(), res.getMessage(), true));
                        }
                        client.saveChat(res.getSender(), res.getMessage());
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
                if (resObject instanceof AckResponse) {
                    AckResponse res = (AckResponse) resObject;
                    if (StatusCode.OK.equals(res.getStatus())){
                        switch(res.getSystemCode()){
                            case MESSAGE_SENT -> {
                                if (client.getChatApp().getOnlineList().getSelectedValue()!=null && client.getChatApp().getOnlineList().getSelectedValue().equals(res.getUid())){   
                                    client.appendToChat(client.formatMessage(client.getUsername(), res.getMessage(), false));
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
