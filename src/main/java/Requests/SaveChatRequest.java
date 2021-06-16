/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Requests;

import Constants.Command;
import java.util.Date;

/**
 *
 * @author Admin
 */
public class SaveChatRequest extends BaseRequest {
    private String sender;
    private String message;
    private boolean isAnchor;
    private Date timestamp;

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
    
    public boolean isIsAnchor() {
        return isAnchor;
    }

    public void setIsAnchor(boolean isAnchor) {
        this.isAnchor = isAnchor;
    }
    public String getSender() {
        return sender;
    }

    public SaveChatRequest(String sender, String message, boolean isAnchor, Date timestamp, Command command) {
        super(command);
        this.sender = sender;
        this.message = message;
        this.isAnchor = isAnchor;
        this.timestamp = timestamp;
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
