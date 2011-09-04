// HelloSQLiteActivity.java
package com.kuc_arc_f.app.plan2;

import com.kuc_arc_f.fw.AppConst;

import android.content.Context;
// import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

    // データベースオープン用のヘルパークラス
public class planDb_Helper extends SQLiteOpenHelper {

    static final String TAG = "planDb_Helper";
    
	private  com.kuc_arc_f.fw.AppConst m_Const = new AppConst();
	
        // データベースのファイル名
        static final String DB="plan.db";

        // テーブルの名前
//       private String TABLE = m_Const.TBL_TR_PLAN_HD;
        
        // データベースのバージョン
        static final int DB_VERSION= 3;

        // テーブル作成のSQL文
        static final String CREATE_TABLE_HD =
            "create table tr_plan_hd (" +
            "_id integer primary key autoincrement, " +
            " s_date text null," +
            " title text null," +
            " userid integer null," +
            " createat text null," +
            " updateat text null" +
            " );";
        static final String CREATE_TABLE_DT =
            "create table tr_plan_dt (" +
            "_id integer primary key autoincrement, " +
            " hd_id integer null," +
            " s_date text null," +
            " start_dt text null," +
            " end_dt   text null," +
            " yote_txt text null," +
            " userid integer null," +
            " createat text null," +
            " updateat text null" +
            " );";
        static final String CREATE_TABLE_MS =
            "create table ms_date (" +
            "_id integer primary key autoincrement, " +
            " b_date text null," +
            " createat text null" +
            " );";
        
        // テーブル削除のSQL文
        static final String DROP_TABLE_HD =
            "drop table tr_plan_hd;";

        static final String DROP_TABLE_DT =
            "drop table tr_plan_dt;";
        static final String DROP_TABLE_MS =
            "drop table ms_date;";
        
        public planDb_Helper(Context c) {
            // データベースのファイル名とバージョンを指定
            super(c, DB, null, DB_VERSION);
        }

        // データベースを新規に作成した後呼ばれる
        public void onCreate(SQLiteDatabase db) {
            // 内部にテーブルを作成する
            db.execSQL(CREATE_TABLE_HD);
            db.execSQL(CREATE_TABLE_DT);
            db.execSQL(CREATE_TABLE_MS);
        }

        // 存在するデータベースと定義しているバージョンが異なるとき
        public void onUpgrade(SQLiteDatabase db, 
                              int oldVersion, int newVersion) {
            Log.w(TAG, "Version mismatch :" + oldVersion + 
                  " to " + newVersion );
            // ここでは、テーブルを削除して新規に作成している
            // 通常は、テーブル内のデータの変換を行う
            db.execSQL(DROP_TABLE_HD);
            db.execSQL(DROP_TABLE_DT );
            db.execSQL(DROP_TABLE_MS );
            
            onCreate(db);
        }
        
}

