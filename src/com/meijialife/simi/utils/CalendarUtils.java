package com.meijialife.simi.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * 日历工具类，封装一些日期和时间的方法
 * @author baojiarui
 *
 */
public class CalendarUtils {
	
	/**
	 * 获取某一天的日期：yyyy-MM-dd
	 * 
	 * @param which 
	 * 				哪一天，默认0今天，1：明天（整数往后推,负数往前移动）
	 * @return
	 */
	public static String getDateStr_(int which) {
		String dateString = "";
		
		Date date = new Date();// 取时间
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		calendar.add(calendar.DATE, which);// 把日期往后增加一天.整数往后推,负数往前移动
		date = calendar.getTime(); // 这个时间就是日期往后推一天的结果
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		dateString = formatter.format(date);
		
		return dateString;
	}
	
	/**
	 * 获取当前时间	格式：yyyy-MM-ddHH:mm
	 * 
	 * @return
	 */
	public static String getDateStrYMDHM() {
		String dateString = "";
		
		Date date = new Date();// 取时间
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		date = calendar.getTime(); // 这个时间就是日期往后推一天的结果
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-ddHH:mm");
		dateString = formatter.format(date);
		
		return dateString;
	}
	
	/**
	 * 获取当前年份	格式：YYYY
	 * 
	 * @return
	 */
	public static int getCurrentYear() {
	    String dateString = "";
	    
	    Date date = new Date();// 取时间
//	    Calendar calendar = new GregorianCalendar();
//	    calendar.setTime(date);
//	    date = calendar.getTime();
	    SimpleDateFormat formatter = new SimpleDateFormat("yyyy");
	    dateString = formatter.format(date);
	    
	    return Integer.parseInt(dateString);
	}
	
	/**
	 * 获取当前月份	格式：MM
	 * 
	 * @return
	 */
	public static int getCurrentMonth() {
	    String dateString = "";
	    
	    Date date = new Date();// 取时间
//	    Calendar calendar = new GregorianCalendar();
//	    calendar.setTime(date);
//	    date = calendar.getTime();
	    SimpleDateFormat formatter = new SimpleDateFormat("MM");
	    dateString = formatter.format(date);
	    
	    return Integer.parseInt(dateString);
	}

}
