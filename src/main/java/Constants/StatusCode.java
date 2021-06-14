/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Constants;

/**
 *
 * @author Admin
 */
public enum StatusCode {
    OK(200),NOT_FOUND(404),INTERNAL_ERROR(500),SERVICE_UNAVAILABLE(503);
    private int value;

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
    
    private StatusCode(int value) {
        this.value = value;
    }    
}
