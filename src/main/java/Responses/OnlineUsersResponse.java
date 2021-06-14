/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Responses;

import Constants.StatusCode;
import Server.ClientHandler;
import java.io.Serializable;
import java.util.Map;

/**
 *
 * @author Admin
 */
public class OnlineUsersResponse extends BaseResponse implements Serializable{
    private Map<String,ClientHandler> clients;

    public Map<String,ClientHandler> getClients() {
        return clients;
    }

    public void setClients(Map<String,ClientHandler> clients) {
        this.clients = clients;
    }

    public OnlineUsersResponse(Map<String,ClientHandler> clients, StatusCode status) {
        super(status);
        this.clients = clients;
    }

}
