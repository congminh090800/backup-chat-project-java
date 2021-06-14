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
public class LoadChatResponse extends BaseResponse{
    private String history;

    public String getHistory() {
        return history;
    }

    public void setHistory(String history) {
        this.history = history;
    }

    public LoadChatResponse(String history, StatusCode status) {
        super(status);
        this.history = history;
    }
    
}
