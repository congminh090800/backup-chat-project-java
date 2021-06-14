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
public class NoParamRequest extends BaseRequest implements Serializable {

    public NoParamRequest(Command command) {
        super(command);
    }
    
}
