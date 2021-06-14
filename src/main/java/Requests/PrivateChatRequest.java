/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Requests;

import Constants.Command;

/**
 *
 * @author Admin
 */
public class PrivateChatRequest extends BaseRequest{
    private String message;
    private String receiver;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public PrivateChatRequest(String message, String receiver, Command command) {
        super(command);
        this.message = message;
        this.receiver = receiver;
    }

    public PrivateChatRequest(Command command) {
        super(command);
    }
    
}
