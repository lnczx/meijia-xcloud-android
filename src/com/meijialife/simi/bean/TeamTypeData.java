package com.meijialife.simi.bean;

import java.io.Serializable;

public class TeamTypeData implements Serializable {

    private String team_type_id;//类型Id
    
    private String team_type_name;//类型名称

    public String getTeam_type_id() {
        return team_type_id;
    }

    public void setTeam_type_id(String team_type_id) {
        this.team_type_id = team_type_id;
    }

    public String getTeam_type_name() {
        return team_type_name;
    }

    public void setTeam_type_name(String team_type_name) {
        this.team_type_name = team_type_name;
    }

    public TeamTypeData(String team_type_id, String team_type_name) {
        super();
        this.team_type_id = team_type_id;
        this.team_type_name = team_type_name;
    }

    public TeamTypeData() {
        super();
    }
}
