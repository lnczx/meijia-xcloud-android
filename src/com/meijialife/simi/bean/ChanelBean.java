package com.meijialife.simi.bean;

import java.io.Serializable;

/**
 * @description：频道实体Bean
 * @author： kerryg
 * @date:2015年12月9日 
 */
public class ChanelBean implements Serializable {
    
    private String channel_id;
    private String name;
    public String getChannel_id() {
        return channel_id;
    }
    public void setChannel_id(String channel_id) {
        this.channel_id = channel_id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    
    
    

}
