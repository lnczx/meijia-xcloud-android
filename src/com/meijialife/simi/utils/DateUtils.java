package com.meijialife.simi.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {
    
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 获取指定时间5分钟前的时间
     * @param date
     * @return
     */
    public static Date getDate5(Date date){
        Date date_5 = new Date(date.getTime() - 1000*60*5); //5分钟前的时间
//        String nowTime_5 = dateFormat.format(date_5);
//        LogOut.i("===========", "5分钟前"+nowTime_5);
        return date_5;
    }
    
    /**
     * 获取指定时间15分钟前的时间
     * @param date
     * @return
     */
    public static Date getDate15(Date date){
        Date date_15 = new Date(date.getTime() - 1000*60*15); //15分钟前的时间
//        String nowTime_15 = dateFormat.format(date_15);
//        LogOut.i("===========", "15分钟前"+nowTime_15);
        return date_15;
    }
    
    /**
     * 获取指定时间30分钟前的时间
     * @param date
     * @return
     */
    public static Date getDate30(Date date){
        Date date_30 = new Date(date.getTime() - 1000*60*30); //30分钟前的时间
//        String nowTime_30 = dateFormat.format(date_30);
//        LogOut.i("===========", "30分钟前"+nowTime_30);
        return date_30;
    }
    
    /**
     * 获取指定时间1小时前的时间
     * @param date
     * @return
     */
    public static Date getDate1(Date date){
        Date date_1 = new Date(date.getTime() - 1000*60*60*1); //1小时前的时间
//        String nowTime_1 = dateFormat.format(date_1);
//        LogOut.i("===========", "1小时前"+nowTime_1);
        return date_1;
    }
    
    /**
     * 获取指定时间2小时前的时间
     * @param date
     * @return
     */
    public static Date getDate2(Date date){
        Date date_2 = new Date(date.getTime() - 1000*60*60*2); //2小时前的时间
//        String nowTime_2 = dateFormat.format(date_2);
//        LogOut.i("===========", "2小时前"+nowTime_2);
        return date_2;
    }
    
    /**
     * 获取指定时间6小时前的时间
     * @param date
     * @return
     */
    public static Date getDate6(Date date){
        Date date_6 = new Date(date.getTime() - 1000*60*60*6); //6小时前的时间
//        String nowTime_6 = dateFormat.format(date_6);
//        LogOut.i("===========", "6小时前"+nowTime_6);
        return date_6;
    }
    
    /**
     * 获取指定时间1天前的时间
     * @param date
     * @return
     */
    public static Date getDate1d(Date date){
        Date date_1d = new Date(date.getTime() - 1000*60*60*24); //1天前的时间
//        String nowTime_1d = dateFormat.format(date_1d);
//        LogOut.i("===========", "1天前前"+nowTime_1d);
        return date_1d;
    }
    
    /**
     * 获取指定时间2天前的时间
     * @param date
     * @return
     */
    public static Date getDate2d(Date date){
        Date date_2d = new Date(date.getTime() - 1000*60*60*48); //2天前的时间
//        String nowTime_2d = dateFormat.format(date_2d);
//        LogOut.i("===========", "2天前前"+nowTime_2d);
        return date_2d;
    }
    
}
