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
public class PrivateChatRequest extends BaseRequest{
    private String message;
    private String receiver;
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
        isAnchor = false;
    }

    public PrivateChatRequest(String message, String receiver, boolean isAnchor, Date timestamp, Command command) {
        super(command);
        this.message = message;
        this.receiver = receiver;
        this.isAnchor = isAnchor;
        this.timestamp = timestamp;
    }
    
    public PrivateChatRequest(Command command) {
        super(command);
    }
    
}
