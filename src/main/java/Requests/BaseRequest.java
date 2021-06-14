/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Requests;

import Constants.Command;
import java.io.Serializable;

/**
 *
 * @author Admin
 */
public abstract class BaseRequest implements Serializable {
    protected Command command;

    public Command getCommand() {
        return command;
    }

    public void setCommand(Command command) {
        this.command = command;
    }

    public BaseRequest(Command command) {
        this.command = command;
    }

}
