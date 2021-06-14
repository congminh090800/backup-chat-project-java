/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Responses;
import Constants.StatusCode;
import java.io.Serializable;
/**
 *
 * @author Admin
 */
public abstract class BaseResponse implements Serializable{
    protected StatusCode status;
    protected String error;
    public StatusCode getStatus() {
        return status;
    }

    public void setStatus(StatusCode status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public BaseResponse(StatusCode status) {
        this.status = status;
    }
    
}
