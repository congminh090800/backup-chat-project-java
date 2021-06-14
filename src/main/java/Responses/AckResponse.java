/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Responses;

import Constants.StatusCode;
import Constants.SystemCode;

/**
 *
 * @author Admin
 */
public class AckResponse extends BaseResponse{
    private String uid;
    private String message;
    private SystemCode systemCode;

    public AckResponse(String uid, String message, SystemCode systemCode, StatusCode status) {
        super(status);
        this.uid = uid;
        this.message = message;
        this.systemCode = systemCode;
    }
    public AckResponse(StatusCode status) {
        super(status);
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public SystemCode getSystemCode() {
        return systemCode;
    }

    public void setSystemCode(SystemCode systemCode) {
        this.systemCode = systemCode;
    }
    
}
