/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Map;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

/**
 *
 * @author Admin
 */
public class Server{
    private ServerSocket serverSocket;
    private Map<String,ClientHandler> clients;
    private boolean isAvailable;
    private JTextArea log;
    private JList list;


    public JTextArea getLog() {
        return log;
    }

    public void setLog(JTextArea log) {
        this.log = log;
    }

    public JList getList() {
        return list;
    }

    public void setList(JList list) {
        this.list = list;
    }
    
    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    public void setServerSocket(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public Map<String, ClientHandler> getClients() {
        return clients;
    }

    public void setClients(Map<String, ClientHandler> clients) {
        this.clients = clients;
    }

    public boolean isIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(boolean isAvailable) {
        this.isAvailable = isAvailable;
    }
    
    public Server(){
        clients = new HashMap<>();
    }
    
    public Server(String ip, int port){
        clients = new HashMap<>();
        InetAddress host = null;
        try {
            host = InetAddress.getByName(ip);
        } catch (UnknownHostException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            showOptionPane("Unknown host", "Create failed", JOptionPane.DEFAULT_OPTION);
        }

        try {
            serverSocket = new ServerSocket(port,100,host);
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            showOptionPane("Port is already in use or non-local ip address", "Create failed", JOptionPane.DEFAULT_OPTION);
        } 
    }
    
    public void listen(){
        try{
            isAvailable = true;
            try {
                SwingUtilities.invokeAndWait(() -> {
                    log.append("Server is listening on port: " + serverSocket.getInetAddress().getHostAddress() + ":" + serverSocket.getLocalPort() +"\n");
                });
            } catch (InterruptedException | InvocationTargetException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }

            while (isAvailable) {
                ClientHandler client = new ClientHandler(serverSocket.accept(),this);
                client.start();
            }            
        } catch(IOException err) {
            System.err.println(err);
        } finally {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    public void logger(String message){
        SwingUtilities.invokeLater(() -> {
            log.append(message);
        });        
    }
    public void addUserToList(String name){
        SwingUtilities.invokeLater(() -> {
            DefaultListModel listModel = (DefaultListModel) list.getModel();
            listModel.addElement(name);
        });        
    }
    public static void showOptionPane(String message, String title, int option){
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showConfirmDialog(null, message, title, option);
        });  
    }
    public void removeUsers(String name){
        SwingUtilities.invokeLater(() -> {
            DefaultListModel listModel = (DefaultListModel) list.getModel();
            listModel.removeElement(name);
        });        
    }
}
