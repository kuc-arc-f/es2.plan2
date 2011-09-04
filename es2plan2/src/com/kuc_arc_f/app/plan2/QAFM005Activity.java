package com.kuc_arc_f.app.plan2;

import java.util.Calendar;

import android.app.ListActivity;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;


import com.kuc_arc_f.fw.*;

public class QAFM005Activity extends ListActivity implements OnClickListener , SimpleCursorAdapter.ViewBinder {
	
	private final String TAG="QAFM005Activity";
	
	private static final int[] TO = { R.id._id_fm005, R.id.lbl_row_fm005_dt, R.id.lbl_row_fm005_txt };
	
	private final int COL_ID   =0;
	private final int COL_DATE =COL_ID +1;
	private final int COL_TXT  =COL_ID +2;
	
	//db
    private SQLiteDatabase mDb;
    
	//fw
	private com.kuc_arc_f.fw.AppConst m_Const = new AppConst();
	private com.kuc_arc_f.fw.ComUtil m_Util = new ComUtil();
	private com.kuc_arc_f.fw.StringUtil m_String = new StringUtil();
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qafm005 );
        String s_ver = m_Util.comGet_VersionName(this, m_Const.APP_PKG_NAME );
		setTitle(m_Const.APP_NAME + " v"+  s_ver);
		
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }
    @Override
    protected void onResume() {
        super.onResume();
        try
        {
            // データベースを書き込み用にオープンする
            planDb_Helper h = 
                new planDb_Helper(getApplicationContext());
            try {
                mDb = h.getWritableDatabase();
            } catch (SQLiteException e) {
                // ディスクフルなどでデータが書き込めない場合
                Log.e(TAG,e.toString());
                mDb = h.getReadableDatabase();
            }
            
    		android.content.SharedPreferences common = getSharedPreferences(
    				m_Const.KEY_PREF_USR,
                MODE_WORLD_READABLE | MODE_WORLD_WRITEABLE);
    		String s_id =  common.getString( m_Const.KEY_FM002_ID , "");
    		
            build_list3(s_id);
        }catch(Exception e){ 
        	Log.d(TAG, e.getMessage());
        	m_Util.errorDialog(this, e.getMessage());
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
		mDb.close();
    }
    //
    public void onClick(View v){
    }
    //
	public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
		switch (columnIndex) {
		case COL_ID:
			TextView number = (TextView) view;
			number.setText( cursor.getString(columnIndex));
			return true;
		case COL_DATE:
			TextView txt_htm = (TextView) view;
			String s_buff =cursor.getString(columnIndex);
			CharSequence source  = Html.fromHtml(s_buff);
			txt_htm.setText( source);
			return true;
		case COL_TXT:
			TextView txt_htm2 = (TextView) view;
			String s_text =cursor.getString(columnIndex);
			CharSequence source2  = Html.fromHtml(s_text);
			txt_htm2.setText( source2 );
			return true;
		default:
			break;
		}
		return false;
	}
    // リストがタップされた時の処理
    @Override
    protected void onListItemClick(
        ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        try
        {
            // タップされた行のIDとメモの内容をテキスト編集ビューに設定
            LinearLayout ll = (LinearLayout)v;
            TextView t = (TextView)ll.findViewById(R.id._id_fm005 );
            String s_id = t.getText().toString();        

            android.content.SharedPreferences common = getSharedPreferences(
    				m_Const.KEY_PREF_USR,
                MODE_WORLD_READABLE | MODE_WORLD_WRITEABLE);
            android.content.SharedPreferences.Editor editor = common.edit();
            editor.putString(m_Const.KEY_FM005_ID, s_id );
            editor.commit(); 

            //next
            Intent intent = new Intent(getApplicationContext(),QAFM004Activity.class);
            startActivity(intent);            
	    }catch(Exception e){
	    	m_Util.errorDialog(this, e.getMessage());
	    }        
	}
	//
	private void build_list3(String s_fm2 ) throws Exception{
		try
		{
        	String sql ="select _id, strftime('%H:%M' , start_dt), strftime('%H:%M' , end_dt), yote_txt  from tr_plan_dt where hd_id="+s_fm2+";";

        	Cursor c = mDb.rawQuery(sql, null );
Log.d(TAG, "sql=" +sql);
        	
        	String[] list = { "_id",  "s_date", "s_txt"};
    		MatrixCursor ma = new MatrixCursor(list);
            if (c.moveToFirst()) {
                do {
                    String s_id   = c.getString(0 );
                    String s_st   = c.getString(1 );
                    String s_end  = c.getString(2 );
                    String s_txt  = c.getString(3 );
                    s_txt = s_txt.replace("\n", "<BR>");
                    
                    String s_dt2="";
                    if(s_st !=null ){
                        Log.d(TAG, "s_st_len=" +s_st.length() );
            	    	s_dt2 =  "<font color='Blue'><b>" +s_st +"-"+ s_end +": </B></font>";
                    }else{
            	    	s_dt2 =  "";
                    }
            	    String s_text = "<font color='#000'>" + s_txt  +"</font><BR>";

            	    ma.addRow(new Object[] { s_id, s_dt2, s_text });
                } while(c.moveToNext());
            }
            // ベースクラスにカーソルのライフサイクルを管理させる
           startManagingCursor(c);
           
    		SimpleCursorAdapter adapter = new SimpleCursorAdapter(getApplicationContext(), 
    				R.layout.fm005_row , ma, list, TO);
    		adapter.setViewBinder(this);
    		setListAdapter(adapter);    
			
		}catch(Exception e){ 
			throw e; 
		}
	}


}