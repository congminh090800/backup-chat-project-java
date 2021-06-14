/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Responses;

import Constants.StatusCode;

/**
 *
 * @author Admin
 */
public class PrivateChatResponse extends BaseResponse {
    private String message;
    private String sender;

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

    public PrivateChatResponse(StatusCode status) {
        super(status);
    }
    
}
