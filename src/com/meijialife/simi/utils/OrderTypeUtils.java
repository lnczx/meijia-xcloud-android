package com.meijialife.simi.utils;


/**
 * @description：订单类型工具类型
 * @author： kerryg
 * @date:2016年3月11日 
 */
public class OrderTypeUtils {
    
    public static String getLeaveTypeName(String typeId){
        if(StringUtils.isEquals(typeId, "0")){
            return "病假";
        }else if (StringUtils.isEquals(typeId, "1")) {
            return "事假";
        }else if (StringUtils.isEquals(typeId, "2")) {
            return "婚假";
        }else if (StringUtils.isEquals(typeId, "3")) {
            return "丧假";
        }else if (StringUtils.isEquals(typeId, "4")) {
            return "产假";
        }else if (StringUtils.isEquals(typeId, "5")) {
            return "年休假";
        }else if (StringUtils.isEquals(typeId, "6")) {
            return "其他";
        }else {
            return "";
        }
       
    }
    
    

}
