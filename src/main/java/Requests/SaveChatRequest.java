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
public class SaveChatRequest extends BaseRequest {
    private String sender;
    private String message;

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public SaveChatRequest(String sender, String message, Command command) {
        super(command);
        this.sender = sender;
        this.message = message;
    }

    public SaveChatRequest(Command command) {
        super(command);
    }
    
}
