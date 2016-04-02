package com.meijialife.simi.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.meijialife.simi.bean.ExpressData;
import com.meijialife.simi.bean.ExpressTypeData;
import com.meijialife.simi.bean.XcompanySetting;

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
            closeDatabase("simi01.db");
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
                expressData.setAdd_time(cur.getLong(7));
                expressData.setUpdate_time(cur.getLong(8));
                list.add(expressData);  //password 
            }
            cur.close();
            closeDatabase("simi01.db");
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return list;        
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
            closeDatabase("simi01.db");
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return typyData;        
    }

}
