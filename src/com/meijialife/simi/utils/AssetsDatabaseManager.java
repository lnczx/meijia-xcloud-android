package com.meijialife.simi.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import u.aly.ci;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.meijialife.simi.bean.ExpressTypeData;
import com.meijialife.simi.bean.XcompanySetting;
import com.meijialife.simi.database.bean.AppTools;
import com.meijialife.simi.database.bean.City;
import com.meijialife.simi.database.bean.OpAd;

public class AssetsDatabaseManager {
    
    private static String tag = "AssetsDatabase"; // for LogCat  
    private static String databasepath = "/data/data/%s/database"; // %s is packageName  
      
      
    // A mapping from assets database file to SQLiteDatabase object  
    private static Map<String, SQLiteDatabase> databases = new HashMap<String, SQLiteDatabase>();  
      
    // Context of application  
    private Context context = null;  
      
    // Singleton Pattern  
    private static AssetsDatabaseManager mInstance = null;  
      
    /** 
     * Initialize AssetsDatabaseManager 
     * @param context, context of application 
     */  
    public static void initManager(Context context){  
        if(mInstance == null){  
            mInstance = new AssetsDatabaseManager(context);  
        }  
    }  
      
    /** 
     * Get a AssetsDatabaseManager object 
     * @return, if success return a AssetsDatabaseManager object, else return null 
     */  
    public static AssetsDatabaseManager getManager(){  
        return mInstance;  
    }  
      
    private AssetsDatabaseManager(Context context){  
        this.context = context;  
    }  
      
    /** 
     * Get a assets database, if this database is opened this method is only return a copy of the opened database 
     * @param dbfile, the assets file which will be opened for a database 
     * @return, if success it return a SQLiteDatabase object else return null 
     */  
    public SQLiteDatabase getDatabase(String dbfile) {  
        if(databases.get(dbfile) != null){  
            Log.i(tag, String.format("Return a database copy of %s", dbfile));  
            return (SQLiteDatabase) databases.get(dbfile);  
        }  
        if(context==null)  
            return null;  
          
        Log.i(tag, String.format("Create database %s", dbfile));  
        String spath = getDatabaseFilepath();  
        String sfile = getDatabaseFile(dbfile);  
          
        File file = new File(sfile);  
        SharedPreferences dbs = context.getSharedPreferences(AssetsDatabaseManager.class.toString(), 0);  
        boolean flag = dbs.getBoolean(dbfile, false); // Get Database file flag, if true means this database file was copied and valid  
        if(!flag || !file.exists()){  
            file = new File(spath);  
            if(!file.exists() && !file.mkdirs()){  
                Log.i(tag, "Create \""+spath+"\" fail!");  
                return null;  
            }  
            if(!copyAssetsToFilesystem(dbfile, sfile)){  
                Log.i(tag, String.format("Copy %s to %s fail!", dbfile, sfile));  
                return null;  
            }  
              
            dbs.edit().putBoolean(dbfile, true).commit();  
        }  
          
        SQLiteDatabase db = SQLiteDatabase.openDatabase(sfile, null, SQLiteDatabase.NO_LOCALIZED_COLLATORS);  
        if(db != null){  
            databases.put(dbfile, db);  
        }  
        return db;  
    }  
      
    private String getDatabaseFilepath(){  
        return String.format(databasepath, context.getApplicationInfo().packageName);  
    }  
      
    private String getDatabaseFile(String dbfile){  
        return getDatabaseFilepath()+"/"+dbfile;  
    }  
      
    private boolean copyAssetsToFilesystem(String assetsSrc, String des){  
        Log.i(tag, "Copy "+assetsSrc+" to "+des);  
        InputStream istream = null;  
        OutputStream ostream = null;  
        try{  
            AssetManager am = context.getAssets();  
            istream = am.open(assetsSrc);  
            ostream = new FileOutputStream(des);  
            byte[] buffer = new byte[1024];  
            int length;  
            while ((length = istream.read(buffer))>0){  
                ostream.write(buffer, 0, length);  
            }  
            istream.close();  
            ostream.close();  
        }  
        catch(Exception e){  
            e.printStackTrace();  
            try{  
                if(istream!=null)  
                    istream.close();  
                if(ostream!=null)  
                    ostream.close();  
            }  
            catch(Exception ee){  
                ee.printStackTrace();  
            }  
            return false;  
        }  
        return true;  
    }  
      
    /** 
     * Close assets database 
     * @param dbfile, the assets file which will be closed soon 
     * @return, the status of this operating 
     */  
    public static boolean closeDatabase(String dbfile){  
        if(databases.get(dbfile) != null){  
            SQLiteDatabase db = (SQLiteDatabase) databases.get(dbfile);  
            db.close();  
            databases.remove(dbfile);  
            return true;  
        }  
        return false;  
    }  
      
    /** 
     * Close all assets database 
     */  
    static public void closeAllDatabase(){  
        Log.i(tag, "closeAllDatabase");  
        if(mInstance != null){  
            for(int i=0; i<mInstance.databases.size(); ++i){  
                if(mInstance.databases.get(i)!=null){  
                    mInstance.databases.get(i).close();  
                }  
            }  
            mInstance.databases.clear();  
        }  
    }  
    
    /**
     * 根据表名字删除某张表
     * @param db
     * @param tableName
     */
    public static void deletTable(SQLiteDatabase db,String tableName ){
        try
        {   
            db.execSQL("delete from "+tableName);
//            db.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    /**
     * 资产管理表数据库插入
     * @param db
     * @param tableName
     */
    public static void insertXcompanySetting(SQLiteDatabase db,String tableName,XcompanySetting setting ){
        try
        {   
                ContentValues values = new ContentValues();
                values.put("id",setting.getId());
                values.put("company_id",setting.getCompany_id());
                values.put("name",setting.getName());
                values.put("setting_type",setting.getSetting_type());
                values.put("setting_json",setting.getSetting_json());
                values.put("is_enable",setting.getIs_enable());
                values.put("add_time",setting.getAdd_time());
                values.put("update_time",setting.getUpdate_time());
                
              /*  String sql = "insert Into xcompany_setting(id,company_id, name,setting_type,setting_json,is_enable,add_time,update_time) Values('"
                        + setting.getId() + "',"
                        + setting.getCompany_id() + "," 
                        + "'"+setting.getName()+"'" + "," 
                        + "'"+setting.getSetting_type()+"'" + "," 
                        + "'"+setting.getSetting_json()+"'" + "," 
                        + setting.getIs_enable() + "," 
                        + setting.getAdd_time() + "," 
                        + setting.getUpdate_time() + ")"; 
                db.execSQL(sql);*/
                db.insert("xcompany_setting", null, values);
//            db.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    /**
     * 应用中心表数据库插入
     * @param db
     * @param tableName
     * @param settingList
     */
    public static void insertAppTools(SQLiteDatabase db,String tableName,AppTools tools ){
        try
        {   
                ContentValues values = new ContentValues();
                values.put("t_id",tools.getT_id());
                values.put("no",tools.getNo());
                values.put("name",tools.getName());
                values.put("logo",tools.getLogo());
                values.put("app_type",tools.getApp_type());
                values.put("menu_type",tools.getMenu_type());
                values.put("open_type",tools.getOpen_type());
                values.put("url",tools.getUrl());
                values.put("action",tools.getAction());
                values.put("params",tools.getParams());
                values.put("is_default",tools.getIs_default());
                values.put("is_del",tools.getIs_del());
                values.put("is_partner",tools.getIs_partner());
                values.put("is_online",tools.getIs_online());
                values.put("app_provider",tools.getApp_provider());
                values.put("app_describe",tools.getApp_describe());
                values.put("auth_url",tools.getAuth_url());
                values.put("qr_code",tools.getQr_code());
                values.put("add_time",tools.getAdd_time());
                values.put("update_time",tools.getUpdate_time());
                db.insert(tableName, null, values);
//            db.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    /**
     * 插入快递表
     * @param db
     * @param tableName
     * @param expressTypes
     */
    public static void insertExpress(SQLiteDatabase db,String tableName,ExpressTypeData type ){
        try
        {   
                ContentValues values = new ContentValues();
                values.put("express_id",type.getExpress_id());
                values.put("ecode",type.getEcode());
                values.put("name",type.getName());
                values.put("is_hot",type.getIs_hot());
                values.put("website",type.getWebsite());
                values.put("api_order_url",type.getApi_order_url());
                values.put("api_search_url",type.getApi_search_url());
                values.put("add_time",type.getAdd_time());
                values.put("update_time",type.getUpdate_time());
                db.insert("express", null, values);
//            db.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
  /**
   * 服务大厅数据表的插入和更新
   * @param db
   * @param tableName
   * @param opads
   */
    public static void insertOpAd(SQLiteDatabase db,String tableName,OpAd opAd ){
        try
        {   
                ContentValues values = new ContentValues();
                values.put("id",opAd.getId());
                values.put("no",opAd.getNo());
                values.put("title",opAd.getTitle());
                values.put("ad_type",opAd.getAd_type());
                values.put("service_type_ids",opAd.getService_type_ids());
                values.put("img_url",opAd.getImg_url());
                values.put("goto_type",opAd.getGoto_type());
                values.put("goto_url",opAd.getGoto_url());
                values.put("add_time",opAd.getAdd_time());
                values.put("update_time",opAd.getUpdate_time());
                values.put("enable",opAd.getEnable());
                db.insert("op_ad", null, values);
//            db.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    /**
     * 插入城市表
     * @param db
     * @param tableName
     * @param city
     */
    public static void insertCity(SQLiteDatabase db,String tableName,City city ){
        try
        {   
            ContentValues values = new ContentValues();
            values.put("city_id",city.getCity_id());
            values.put("name",city.getName());
            values.put("provice_id",city.getProvince_id());
            values.put("is_enable",city.getIs_enable());
            values.put("zip_code",city.getZip_code());
            values.put("add_time",city.getAdd_time());
            db.insert("city", null, values);
//            db.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    
    /**
     * 查询资产列表
     * @param db
     * @return
     */
    public static List<XcompanySetting> searchAllXcompany(SQLiteDatabase db ){
        List<XcompanySetting> list=new ArrayList<XcompanySetting>();
        try
        {
            // 对数据库进行操作  
            String sql = "select * from xcompany_setting where setting_type = 'asset_type'";
            Cursor cur=db.rawQuery(sql, new String[]{});
            while(cur.moveToNext())
            {
                XcompanySetting setting = new XcompanySetting();
                setting.setId(cur.getString(0));
                setting.setCompany_id(cur.getString(1));
                setting.setName(cur.getString(2));
                setting.setSetting_type(cur.getString(3));
                setting.setSetting_json(cur.getString(4));
                setting.setIs_enable(cur.getShort(5));
                setting.setAdd_time(cur.getLong(6));
                setting.setUpdate_time(cur.getLong(7));
                list.add(setting);  //password 
            }
            cur.close();
//            closeDatabase("simi01.db");
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return list;        
    }
    /**
     * 查询城市表所有数据
     * @param db
     * @return
     */
    public static List<City> searchAllCity(SQLiteDatabase db ){
        List<City> list=new ArrayList<City>();
        try
        {
            // 对数据库进行操作  
            String sql = "select * from city";
            Cursor cur=db.rawQuery(sql, new String[]{});
            while(cur.moveToNext())
            {
                City city = new City();
                city.setCity_id(cur.getString(0));
                city.setName(cur.getString(1));
                city.setAdd_time(cur.getLong(2));
                city.setProvince_id(cur.getString(3));
                city.setIs_enable(cur.getInt(4));
                city.setZip_code(cur.getString(5));
                list.add(city);  //password 
            }
            cur.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return list;        
    }
    /**
     * 查询快递数据
     * @param db
     * @return
     */
    public static List<ExpressTypeData> searchAllExpress(SQLiteDatabase db ){
        List<ExpressTypeData> list=new ArrayList<ExpressTypeData>();
        try
        {
            // 对数据库进行操作  
            String sql = "select * from express where is_hot =1 ";
            Cursor cur=db.rawQuery(sql, new String[]{});
            while(cur.moveToNext())
            {
                ExpressTypeData expressData = new ExpressTypeData();
                expressData.setExpress_id(cur.getString(0));
                expressData.setEcode(cur.getString(1));
                expressData.setName(cur.getString(2));
                expressData.setIs_hot(cur.getShort(3));
                expressData.setWebsite(cur.getString(4));
                expressData.setApi_order_url(cur.getString(5));
                expressData.setApi_search_url(cur.getString(6));
                expressData.setAdd_time(cur.getLong(7));
                expressData.setUpdate_time(cur.getLong(8));
                list.add(expressData);  //password 
            }
            cur.close();
//            closeDatabase("simi01.db");
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return list;        
    }
    /**
     * 根据Id查看快递表
     * @param db
     * @param expressId
     * @return
     */
    public static int searchExpressTypeById(SQLiteDatabase db,String expressId ){
        int is_has = 0;//0=不存在，1=存在
        try
        {
            // 对数据库进行操作  
            String sql = "select * from express where express_id = ?";
            Cursor cur=db.rawQuery(sql, new String[]{expressId});
            if(cur.moveToNext())
            {
                is_has=1;
            }
            cur.close();
//            closeDatabase("simi01.db");
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return is_has;        
    }
   /**
    * 根据Id查询服务大厅表
    * @param db
    * @param id
    * @return
    */
    public static int searchOpAdById(SQLiteDatabase db,String id ){
        int is_has = 0;//0=不存在，1=存在
        try
        {
            // 对数据库进行操作  
            String sql = "select * from op_ad where id = ?";
            Cursor cur=db.rawQuery(sql, new String[]{id});
            while(cur.moveToNext())
            {
                is_has = 1;
            }
            cur.close();
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        return is_has;        
    }
    /**
     * 根据Id查询城市列表
     * @param db
     * @param id
     * @return
     */
    public static int searchCityById(SQLiteDatabase db,String id ){
        int is_has = 0;//0=不存在，1=存在
        try
        {
            // 对数据库进行操作  
            String sql = "select * from city where city_id = ?";
            Cursor cur=db.rawQuery(sql, new String[]{id});
            while(cur.moveToNext())
            {
                is_has = 1;
            }
            cur.close();
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        return is_has;        
    }
    /**
     * 根据Id查询应用中心表
     * @param db
     * @param tId
     * @return
     */
    public static int searchAppToolsById(SQLiteDatabase db,String tId ){
        int is_has = 0;//0=不存在，1=存在
        try
        {
            // 对数据库进行操作  
            String sql = "select * from app_tools where t_id = ?";
            Cursor cur=db.rawQuery(sql, new String[]{tId});
            while(cur.moveToNext())
            {
                is_has = 1;
            }
            cur.close();
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        return is_has;        
    }
    /**
     * 根据Id查询资产管理表
     * @param db
     * @param id
     * @return
     */
    public static int searchXcompanySettingById(SQLiteDatabase db,String id ){
        int is_has = 0;//0=不存在，1=存在
        try
        {
            // 对数据库进行操作  
            String sql = "select * from xcompany_setting where id = ?";
            Cursor cur=db.rawQuery(sql, new String[]{id});
            while(cur.moveToNext())
            {
                is_has = 1;
            }
            cur.close();
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        return is_has;        
    }
    /**
     * 资产管理更新表
     * @param db
     * @param id
     * @return
     */
    public static int updateXcompanySettingById(SQLiteDatabase db,XcompanySetting setting ){
        int is_has = -1;
        try
        {
            ContentValues values = new ContentValues();
            values.put("id",setting.getId());
            values.put("company_id",setting.getCompany_id());
            values.put("name",setting.getName());
            values.put("setting_type",setting.getSetting_type());
            values.put("setting_json",setting.getSetting_json());
            values.put("is_enable",setting.getIs_enable());
            values.put("add_time",setting.getAdd_time());
            values.put("update_time",setting.getUpdate_time());
            String []params = {setting.getId()};
            is_has =db.update("xcompany_setting", values, "id=?", params);
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        return is_has;        
    }
  /**
   * 更新服务大厅表
   * @param db
   * @param opAd
   * @return
   */
    public static int updateOpAdById(SQLiteDatabase db,OpAd opAd ){
        int is_has = -1;
        try
        {
            ContentValues values = new ContentValues();
            values.put("id",opAd.getId());
            values.put("no",opAd.getNo());
            values.put("title",opAd.getTitle());
            values.put("ad_type",opAd.getAd_type());
            values.put("service_type_ids",opAd.getService_type_ids());
            values.put("img_url",opAd.getImg_url());
            values.put("goto_type",opAd.getGoto_type());
            values.put("goto_url",opAd.getGoto_url());
            values.put("add_time",opAd.getAdd_time());
            values.put("update_time",opAd.getUpdate_time());
            values.put("enable",opAd.getEnable());
            String []params = {opAd.getId()};
            is_has =db.update("op_ad", values, "id=?", params);
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        return is_has;        
    }
    /**
     * 更新城市列表
     * @param db
     * @param opAd
     * @return
     */
    public static int updateCityById(SQLiteDatabase db,City city ){
        int is_has = -1;
        try
        {
            ContentValues values = new ContentValues();
            values.put("city_id",city.getCity_id());
            values.put("name",city.getName());
            values.put("provice_id",city.getProvince_id());
            values.put("is_enable",city.getIs_enable());
            values.put("zip_code",city.getZip_code());
            values.put("add_time",city.getAdd_time());
            String []params = {city.getCity_id()};
            is_has =db.update("op_ad", values, "city_id=?", params);
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        return is_has;        
    }
   /**
    * 更新快递表
    * @param db
    * @param type
    * @return
    */
    public static int updateExpressById(SQLiteDatabase db,ExpressTypeData type ){
        int is_has = -1;
        try
        {
            ContentValues values = new ContentValues();
            values.put("express_id",type.getExpress_id());
            values.put("ecode",type.getEcode());
            values.put("name",type.getName());
            values.put("is_hot",type.getIs_hot());
            values.put("website",type.getWebsite());
            values.put("api_order_url",type.getApi_order_url());
            values.put("api_search_url",type.getApi_search_url());
            values.put("add_time",type.getAdd_time());
            values.put("update_time",type.getUpdate_time());
            String [] params = {type.getExpress_id()};
            is_has =db.update("express", values, "express_id=?", params);
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        return is_has;        
    }
    /**
     * 更新应用中心
     * @param db
     * @param appTools
     * @return
     */
    public static int updateAppToolsId(SQLiteDatabase db,AppTools tools ){
        int is_has = -1;
        try
        {
            ContentValues values = new ContentValues();
            values.put("t_id",tools.getT_id());
            values.put("no",tools.getNo());
            values.put("name",tools.getName());
            values.put("logo",tools.getLogo());
            values.put("app_type",tools.getApp_type());
            values.put("menu_type",tools.getMenu_type());
            values.put("open_type",tools.getOpen_type());
            values.put("url",tools.getUrl());
            values.put("action",tools.getAction());
            values.put("params",tools.getParams());
            values.put("is_default",tools.getIs_default());
            values.put("is_del",tools.getIs_del());
            values.put("is_partner",tools.getIs_partner());
            values.put("is_online",tools.getIs_online());
            values.put("app_provider",tools.getApp_provider());
            values.put("app_describe",tools.getApp_describe());
            values.put("auth_url",tools.getAuth_url());
            values.put("qr_code",tools.getQr_code());
            values.put("add_time",tools.getAdd_time());
            values.put("update_time",tools.getUpdate_time());
            String [] params = {tools.getT_id()};
            is_has =db.update("app_tools", values, "t_id=?", params);
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        return is_has;        
    }
    
    /**
     * 
     * @param db
     * @return
     */
    
    public static ExpressTypeData searchExpressTypeByEcode(SQLiteDatabase db,String ecode ){
        ExpressTypeData typyData=new ExpressTypeData();
        try
        {
            // 对数据库进行操作  
            String sql = "select * from express where ecode = ?";
            Cursor cur=db.rawQuery(sql, new String[]{ecode});
            while(cur.moveToNext())
            {
                typyData.setExpress_id(cur.getString(0));
                typyData.setEcode(cur.getString(1));
                typyData.setName(cur.getString(2));
                typyData.setIs_hot(cur.getShort(3));
                typyData.setAdd_time(cur.getLong(7));
                typyData.setUpdate_time(cur.getLong(8));
            }
            cur.close();
//            closeDatabase("simi01.db");
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return typyData;        
    }
   /**
    * 查询资产管理更新时间
    * @param db
    * @return
    */
    public static Long searchExpressTypeUpdateTime(SQLiteDatabase db){
        Long  maxUpdateTime=0L;
        try
        {
            // 对数据库进行操作  
            String sql = "select max(update_time) from xcompany_setting";
            Cursor cur=db.rawQuery(sql,null);
            while(cur.moveToNext())
            {
                maxUpdateTime =cur.getLong(0);
            }
            cur.close();
//            closeDatabase("simi01.db");
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return maxUpdateTime;        
    }

    /**
     * 查询应用中心更新时间
     * @param db
     * @return
     */
    public static Long searchAppToolsUpdateTime(SQLiteDatabase db){
        Long  maxUpdateTime=0L;
        try
        {
            // 对数据库进行操作  
            String sql = "select max(update_time) from app_tools";
            Cursor cur=db.rawQuery(sql,null);
            while(cur.moveToNext())
            {
                maxUpdateTime =cur.getLong(0);
            }
            cur.close();
//            closeDatabase("simi01.db");
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return maxUpdateTime;        
    }
    /**
     * 查询用户表更新时间
     * @param db
     * @return
     */
    public static Long searchUserUpdateTime(SQLiteDatabase db){
        Long  maxUpdateTime=0L;
        try
        {
            // 对数据库进行操作  
            String sql = "select max(update_time) from user";
            Cursor cur=db.rawQuery(sql,null);
            while(cur.moveToNext())
            {
                maxUpdateTime =cur.getLong(0);
            }
            cur.close();
//            closeDatabase("simi01.db");
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return maxUpdateTime;        
    }
    /**
     * 查询服务大厅表更新时间
     * @param db
     * @return
     */
    public static Long searchOpAdsUpdateTime(SQLiteDatabase db){
        Long  maxUpdateTime=0L;
        try
        {
            // 对数据库进行操作  
            String sql = "select max(update_time) from op_ad";
            Cursor cur=db.rawQuery(sql,null);
            while(cur.moveToNext())
            {
                maxUpdateTime =cur.getLong(0);
            }
            cur.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return maxUpdateTime;        
    }
    /**
     * 获取城市表添加时间
     * @param db
     * @return
     */
    public static Long searchCityAddTime(SQLiteDatabase db){
        Long  maxUpdateTime=0L;
        try
        {
            // 对数据库进行操作  
            String sql = "select max(add_time) from city";
            Cursor cur=db.rawQuery(sql,null);
            while(cur.moveToNext())
            {
                maxUpdateTime =cur.getLong(0);
            }
            cur.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return maxUpdateTime;        
    }
}
