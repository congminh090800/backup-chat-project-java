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
public class RegisterRequest extends BaseRequest {
    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public RegisterRequest(String username, Command command) {
        super(command);
        this.username = username;
    }
}
