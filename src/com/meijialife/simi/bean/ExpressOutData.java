package com.meijialife.simi.bean;

import java.io.Serializable;

/**
 * @description：外部对应快递单号查询实体
 * @author： kerryg
 * @date:2016年4月1日 
 */
public class ExpressOutData implements Serializable {

    private String comCode;//
    
    private String id;
    
    private String noCount;
    
    private String noPre;
    
    private String startTime;

    public String getComCode() {
        return comCode;
    }

    public void setComCode(String comCode) {
        this.comCode = comCode;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNoCount() {
        return noCount;
    }

    public void setNoCount(String noCount) {
        this.noCount = noCount;
    }

    public String getNoPre() {
        return noPre;
    }

    public void setNoPre(String noPre) {
        this.noPre = noPre;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }
    
    
    
    
}
