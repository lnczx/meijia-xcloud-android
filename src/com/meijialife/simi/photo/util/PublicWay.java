package com.meijialife.simi.photo.util;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;


/**
 * @description：存放所有的list在最后退出时一起关闭
 * @author： kerryg
 * @date:2015年11月10日 
 */
public class PublicWay {
	public static List<Activity> activityList = new ArrayList<Activity>();
	
	public static int num = 12;
	
}
