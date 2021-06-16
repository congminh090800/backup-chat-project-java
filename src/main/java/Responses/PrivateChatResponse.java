/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Responses;

import Constants.StatusCode;
import java.util.Date;

/**
 *
 * @author Admin
 */
public class PrivateChatResponse extends BaseResponse {
    private String message;
    private String sender;
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

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public PrivateChatResponse(String message, String sender, StatusCode status) {
        super(status);
        this.message = message;
        this.sender = sender;
    }

    public PrivateChatResponse(String message, String sender, boolean isAnchor, Date timestamp, StatusCode status) {
        super(status);
        this.message = message;
        this.sender = sender;
        this.isAnchor = isAnchor;
        this.timestamp = timestamp;
    }

    public PrivateChatResponse(StatusCode status) {
        super(status);
    }
    
}
