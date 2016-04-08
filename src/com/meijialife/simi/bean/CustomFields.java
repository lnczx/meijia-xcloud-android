package com.meijialife.simi.bean;

import java.io.Serializable;
import java.util.List;

/**
 * @description：用户浏览次数
 * @author： kerryg
 * @date:2016年4月7日 
 */
public class CustomFields implements Serializable {

    private List<String> views;

    public List<String> getViews() {
        return views;
    }

    public void setViews(List<String> views) {
        this.views = views;
    }

   
    
    
    
}
