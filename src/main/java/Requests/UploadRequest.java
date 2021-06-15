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
public class UploadRequest extends BaseRequest{
    private int order;
    private byte data[];
    private String fileName;
    private boolean isLast;
    private String sender;

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public UploadRequest(int order, byte[] data, String fileName, boolean isLast, String sender, Command command) {
        super(command);
        this.order = order;
        this.data = data;
        this.fileName = fileName;
        this.isLast = isLast;
        this.sender = sender;
    }


    public boolean isIsLast() {
        return isLast;
    }

    public void setIsLast(boolean isLast) {
        this.isLast = isLast;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }    
}
