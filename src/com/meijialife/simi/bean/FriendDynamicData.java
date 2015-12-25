package com.meijialife.simi.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @description：好友动态实体
 * @author： kerryg
 * @date:2015年12月23日 
 */
public class FriendDynamicData implements Serializable {
    
    private String fid;//动态Id
    private String title;//动态内容
    private String user_id;//用户Id
    private String name;//名称
    private String head_img;//头像url
    private Integer total_zan;//点赞数
    private Integer total_comment;//评论数量
    private Integer total_view;//浏览次数
    private String add_time_str;//创建动态时间
    private ArrayList<DynamicImaData> feed_imgs;//发表图片
    private ArrayList<DynamicZan> zan_top10;//赞
    private String feed_extra;
    
    
    
    
    public String getFeed_extra() {
        return feed_extra;
    }
    public void setFeed_extra(String feed_extra) {
        this.feed_extra = feed_extra;
    }
    public ArrayList<DynamicZan> getZan_top10() {
        return zan_top10;
    }
    public void setZan_top10(ArrayList<DynamicZan> zan_top10) {
        this.zan_top10 = zan_top10;
    }
    public String getFid() {
        return fid;
    }
    public void setFid(String fid) {
        this.fid = fid;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public Integer getTotal_zan() {
        return total_zan;
    }
    public void setTotal_zan(Integer total_zan) {
        this.total_zan = total_zan;
    }
    public Integer getTotal_comment() {
        return total_comment;
    }
    public void setTotal_comment(Integer total_comment) {
        this.total_comment = total_comment;
    }
    public Integer getTotal_view() {
        return total_view;
    }
    public void setTotal_view(Integer total_view) {
        this.total_view = total_view;
    }
    public String getAdd_time_str() {
        return add_time_str;
    }
    public void setAdd_time_str(String add_time_str) {
        this.add_time_str = add_time_str;
    }
    public String getUser_id() {
        return user_id;
    }
    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getHead_img() {
        return head_img;
    }
    public void setHead_img(String head_img) {
        this.head_img = head_img;
    }
    public ArrayList<DynamicImaData> getFeed_imgs() {
        return feed_imgs;
    }
    public void setFeed_imgs(ArrayList<DynamicImaData> feed_imgs) {
        this.feed_imgs = feed_imgs;
    }
 
    
    
}
