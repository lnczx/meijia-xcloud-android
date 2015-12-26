package com.meijialife.simi.bean;

public class CardComment {
    private Long id;

    private Long card_id;

    private Long user_id;

    private String comment;

    private Long add_time;
    
    private String name;
    private String head_img;
    
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Long getCard_id() {
        return card_id;
    }
    public void setCard_id(Long card_id) {
        this.card_id = card_id;
    }
    public Long getUser_id() {
        return user_id;
    }
    public void setUser_id(Long user_id) {
        this.user_id = user_id;
    }
    public String getComment() {
        return comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }
    public Long getAdd_time() {
        return add_time;
    }
    public void setAdd_time(Long add_time) {
        this.add_time = add_time;
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

}