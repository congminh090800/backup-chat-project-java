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
public class DownloadResponse extends BaseResponse{
    private String filename;
    private boolean isDone;
    private byte data[];

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
    

    public boolean isIsDone() {
        return isDone;
    }

    public void setIsDone(boolean isDone) {
        this.isDone = isDone;
    }
    
    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public DownloadResponse(String filename, boolean isDone, StatusCode status) {
        super(status);
        this.filename = filename;
        this.isDone = isDone;
    }

    public DownloadResponse(String filename, boolean isDone, byte[] data, StatusCode status) {
        super(status);
        this.filename = filename;
        this.isDone = isDone;
        this.data = data;
    }
    
}
