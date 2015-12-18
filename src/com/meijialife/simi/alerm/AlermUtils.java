package com.meijialife.simi.alerm;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.meijialife.simi.utils.DateUtils;
import com.meijialife.simi.utils.LogOut;

/**
 * 提醒工具类
 *
 */
public class AlermUtils {
	
    public static final String TAG = "Alerm";
    
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static SimpleDateFormat formatYear = new SimpleDateFormat("yyyy");
    private static SimpleDateFormat formatMonth = new SimpleDateFormat("MM");
    private static SimpleDateFormat formatDay = new SimpleDateFormat("dd");
    private static SimpleDateFormat formatHours = new SimpleDateFormat("HH");
    private static SimpleDateFormat formatMinutes = new SimpleDateFormat("mm");
    private static SimpleDateFormat formatSeconds = new SimpleDateFormat("ss");
    private static SimpleDateFormat formatCode = new SimpleDateFormat("MMddHHmm");
    
    /**
     * 初始化本地提醒闹钟
     * 
     * @param context
     * @param remindAlerm 提醒设置 0 = 不提醒 1 = 按时提醒 2 = 5分钟 3 = 15分钟 4 = 提前30分钟 5 = 提前一个小时 6 = 提前2小时 7 = 提前6小时 8 = 提前一天 9 = 提前两天
     * @param date 时间   yyyy-MM-dd HH:mm:ss
     * @param title 提醒类型 差旅规划 or 会议安排 or 事务提醒。。。。。等等等
     * 
     */
    public static void initAlerm(Context context, int remindAlerm, Date date, String title, String msg){
        if(date == null){
            return;
        }
//        Date date = new Date(lDate.getYear()-1900, lDate.getMonthOfYear()-1, lDate.getDayOfMonth(), lTime.getHourOfDay(), lTime.getMinuteOfHour(), lTime.getSecondOfMinute());
        
        LogOut.i("===========", "任务时间"+formatYear.format(date)+"-" + formatMonth.format(date)+"-" + formatDay.format(date)+" " + formatHours.format(date)+":" + formatMinutes.format(date)+":" + formatSeconds.format(date));
        
        switch (remindAlerm) {
        case 0://不提醒
            LogOut.i(TAG, "不提醒");
            break;
        case 1://按时提醒
            setAlerm(context, date, title, msg);
            break;
        case 2://5分钟
            setAlerm(context, DateUtils.getDate5(date), title, msg);
            break;
        case 3://15分钟
            setAlerm(context, DateUtils.getDate15(date), title, msg);
            break;
        case 4://提前30分钟
            setAlerm(context, DateUtils.getDate30(date), title, msg);
            break;
        case 5://提前一个小时
            setAlerm(context, DateUtils.getDate1(date), title, msg);
            break;
        case 6://提前2小时
            setAlerm(context, DateUtils.getDate2(date), title, msg);
            break;
        case 7://提前6小时
            setAlerm(context, DateUtils.getDate6(date), title, msg);
            break;
        case 8://提前一天
            setAlerm(context, DateUtils.getDate1d(date), title, msg);
            break;
        case 9://提前两天
            setAlerm(context, DateUtils.getDate2d(date), title, msg);
            break;

        default:
            break;
        }
        
    }
    
    /**
     * 初始化本地提醒闹钟
     * 
     * @param context
     * @param date 时间   yyyy-MM-dd HH:mm:ss
     * @param title 提醒类型 差旅规划 or 会议安排 or 事务提醒。。。。。等等等
     * 
     */
    public static void initAlerm(Context context, Date date, String title, String msg){
        if(date == null){
            return;
        }
        LogOut.i("===========", "任务时间"+formatYear.format(date)+"-" + formatMonth.format(date)+"-" + formatDay.format(date)+" " + formatHours.format(date)+":" + formatMinutes.format(date)+":" + formatSeconds.format(date));
        
        setAlerm(context, date, title, msg);
    }
    
	/**
	 * 设置提醒闹钟
	 */
	private static void setAlerm(Context context, Date date, String title, String text){
	    if(date == null){
	        return;
	    }
		
		Calendar c = Calendar.getInstance();		//提醒时间
//		c.set(Calendar.YEAR, 2015);   //年
//		c.set(Calendar.MONTH, 9);     //月（一月为0）
//		c.set(Calendar.DAY_OF_MONTH, 18); //日
//		c.set(Calendar.HOUR_OF_DAY, 22);  //时
//        c.set(Calendar.MINUTE, 54);   //分
//        c.set(Calendar.SECOND, 10);   //秒
//        c.set(Calendar.MILLISECOND, 0);
		
		c.set(Calendar.YEAR, Integer.parseInt(formatYear.format(date)));   //年
        c.set(Calendar.MONTH, (Integer.parseInt(formatMonth.format(date))-1));     //月（一月为0）
        c.set(Calendar.DAY_OF_MONTH, Integer.parseInt(formatDay.format(date))); //日
        c.set(Calendar.HOUR_OF_DAY, Integer.parseInt(formatHours.format(date)));  //时
        c.set(Calendar.MINUTE, Integer.parseInt(formatMinutes.format(date)));   //分
        c.set(Calendar.SECOND, 2);   //秒
        c.set(Calendar.MILLISECOND, 0);

		Intent intent = new Intent(context,AlermReceiver.class);
		intent.putExtra("title", title);
		intent.putExtra("text", text);
		int requestCode = Integer.parseInt(formatCode.format(date));//为每个闹钟单独设置requestCode，防止被覆盖
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode,intent, PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager am;

	/*	Calendar calendar = Calendar.getInstance();  
        calendar.setTimeInMillis(System.currentTimeMillis());  
        calendar.add(Calendar.SECOND, 10); */
		// 获取系统进程
		am = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
//		am.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,c.getTimeInMillis(),pendingIntent);
		am.set(AlarmManager.RTC_WAKEUP,c.getTimeInMillis(),pendingIntent);
		
		String tmps = "提醒设置时间为：" + c.get(Calendar.YEAR) + "年" + (c.get(Calendar.MONTH)+1) + "月" + c.get(Calendar.DAY_OF_MONTH) + "日" + c.get(Calendar.HOUR_OF_DAY) + "时"+ ":" + c.get(Calendar.MINUTE) + "分";
		LogOut.i(TAG, tmps);
	    LogOut.debug(tmps);
	}
	
	/**
	 * 取消提醒闹钟
	 */
	public static void cancelSigninAlerm(Context context){
		
		Intent intent = new Intent(context,AlermReceiver.class);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager am;
		// 获取系统进程
		am = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
		// cancel
		am.cancel(pendingIntent);
		
//		Log.e("提醒 已取消");
	}

}
