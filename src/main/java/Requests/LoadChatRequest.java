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
public class LoadChatRequest extends BaseRequest{
    private String targer;

    public String getTarger() {
        return targer;
    }

    public void setTarger(String targer) {
        this.targer = targer;
    }

    public LoadChatRequest(String targer, Command command) {
        super(command);
        this.targer = targer;
    }

}
