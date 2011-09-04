package com.kuc_arc_f.app.plan2;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import android.util.Log;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.kuc_arc_f.fw.*;

public class QAFM004Activity extends Activity implements OnClickListener {
	
	private final String TAG="QAFM003Activity";
	
    private static final int REQUEST_CODE = 0;
    
    private String m_FM5_ID="";
    private String m_FM5_DT="";
    private String m_FM5_YMD="";
    private int  m_hour   =0;
    private int  m_minute =0;
    private int  m_hour_end   =0;
    private int  m_minute_end =0;
    private String m_Memo="";
    
    DatePickerDialog datePickerDialog;
	//db
    private SQLiteDatabase mDb;
    //
    private int m_VO_FLG=0;
	//fw
	private com.kuc_arc_f.fw.AppConst m_Const = new AppConst();
	private com.kuc_arc_f.fw.ComUtil m_Util = new ComUtil();
	private com.kuc_arc_f.fw.HttpUtil m_Http = new HttpUtil();
	private com.kuc_arc_f.fw.StringUtil m_String= new StringUtil();
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qafm004 );
        String s_ver = m_Util.comGet_VersionName(this, m_Const.APP_PKG_NAME );
		setTitle(m_Const.APP_NAME + " v"+  s_ver);        
        
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //
        ImageButton button = (ImageButton)findViewById(R.id.btn_fm04_rec) ;
        button.setOnClickListener(this);

        Button btn_start = (Button)findViewById(R.id.btn_fm4_start ) ;
        btn_start.setOnClickListener(this);

        Button btn_end = (Button)findViewById(R.id.btn_fm4_end ) ;
        btn_end.setOnClickListener(this);
        
        ImageButton btn_save = (ImageButton)findViewById(R.id.btn_fm04_save ) ;
        btn_save.setOnClickListener(this);
        
        ImageButton btn_del = (ImageButton)findViewById(R.id.btn_fm04_del ) ;
        btn_del.setOnClickListener(this);
    }
    //
    @Override
    protected void onResume() {
        super.onResume();
        
        try
        {
Log.d(TAG, "onResume=" );
    		
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
    		m_FM5_ID =  common.getString( m_Const.KEY_FM005_ID , "");
    		m_FM5_DT =  common.getString( m_Const.KEY_FM002_DT , "");
    		m_FM5_YMD=  common.getString( m_Const.KEY_FM002_YMD , "");
    		
    		if(m_VO_FLG==0){
        		Log.d(TAG, "m_FM5_ID=" +m_FM5_ID);
        		
   	         TextView txt1 = (TextView)findViewById(R.id.lbl_fm04_date ) ;
                txt1.setText(m_FM5_DT);

                TextView txt_tm = (TextView)findViewById(R.id.lbl_fm04_start) ;
                txt_tm.setText("Nothing");
                TextView txt_tm2 = (TextView)findViewById(R.id.lbl_fm04_end ) ;
                txt_tm2.setText("Nothing");
                
                proc_select(m_FM5_ID);
    		}
    		m_VO_FLG=0;
        }catch(Exception e){
        	m_Util.errorDialog(this, e.getMessage() );
        }
    }
    // アクティビティがフォアグラウンドでなくなったら、DBをcloseする
    @Override
    protected void onPause() {
        super.onPause();
		mDb.close();
    }
    //
    public void onClick(View v){
    	//
    	if(v.getId() == R.id.btn_fm04_rec){
       	 //Log.d( "onClick", "R.id.Button01.finish()" );
            try {
                // インテント作成
                Intent intent = new Intent(
                        RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(
                        RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(
                        RecognizerIntent.EXTRA_PROMPT,
                        "Input Voice");
                
                // インテント発行
                startActivityForResult(intent, REQUEST_CODE);
            } catch (ActivityNotFoundException e) {
                // このインテントに応答できるアクティビティがインストールされていない場合
            	m_Util.errorDialog(this, "[Not Install Activity]"+ e.getMessage() );
            }catch(Exception e){
            	m_Util.errorDialog(this, e.getMessage() );
            }
    	}else if(v.getId() == R.id.btn_fm4_start){
    		show_Time();
    	}else if(v.getId() == R.id.btn_fm4_end){
    		show_TimeEnd();
    	}else if(v.getId() == R.id.btn_fm04_save ){
    		try
    		{
    			if(IsCheck_Val() == true){
            		proc_update();
    			}
    		}catch(Exception e){
    			m_Util.errorDialog(this, e.getMessage());
    		}
    	}else if(v.getId() == R.id.btn_fm04_del ){
    		disp_dialog("Delete OK ?");
//    		proc_delete(m_FM5_ID);
    	}
    }
    
    // アクティビティ終了時に呼び出される
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	
    	try
    	{
            if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
                String resultsString = "";
                
                // 結果文字列リスト
                ArrayList<String> results = data.getStringArrayListExtra(
                        RecognizerIntent.EXTRA_RESULTS);
                
                for (int i = 0; i< results.size(); i++) {
                    // ここでは、文字列が複数あった場合に結合しています
                    resultsString += results.get(i);
                }
                
                // トーストを使って結果を表示
                Toast.makeText(this, resultsString, Toast.LENGTH_LONG).show();
                
                //Set
                EditText txt1 = (EditText)findViewById(R.id.txt_fm04_memo) ;
                String s_txt = txt1.getText().toString();
                txt1.setText(s_txt +resultsString );
            }
            m_VO_FLG=1;
            
            super.onActivityResult(requestCode, resultCode, data);
        }catch(Exception e){
        	m_Util.errorDialog(this, e.getMessage() );
        }
    }  

    //
    private String proc_select(String s_key ) throws Exception{
    	String ret="";
    	try
    	{
            String[] param = new String[] { s_key };
        	String sql = "select _id, strftime('%H' , start_dt), strftime('%M' , start_dt)";
			sql = sql+ " , strftime('%H' , end_dt), strftime('%M' , end_dt), yote_txt";
			sql = sql+ "  from tr_plan_dt where _id=?;";
        	Cursor c = mDb.rawQuery(sql, param);
            if (c.moveToFirst()) {
                do {
             	  String s_id       = c.getString( 0 );
             	  String s_st_h     = c.getString( 1 );
             	  String s_st_m     = c.getString( 2 );
             	  String s_end_h     = c.getString( 3 );
             	  String s_end_m     = c.getString( 4 );
             	  m_Memo     = c.getString( 5 );
             	  
             	  if(s_st_h !=null){
             		  if(s_st_h.length() > 0){
                  		 m_hour= Integer.parseInt(s_st_h);
             		  }
             	  }
             	  if(s_st_m !=null){
             		  if(s_st_m.length() > 0){
             			 m_minute= Integer.parseInt(s_st_m);
             		  }
             	  }
             	  if(s_end_h !=null){
             		  if(s_end_h.length() > 0){
             			 m_hour_end= Integer.parseInt(s_end_h);
             		  }
             	  }
             	  if(s_end_m !=null){
             		  if(s_end_m.length() > 0){
             			 m_minute_end= Integer.parseInt(s_end_m);
             		  }
             	  }
             	  //lbl
             	  if ((m_hour > 0) || (m_minute > 0)){
                 	  String s_start= String.valueOf(m_hour) + ":"+ String.valueOf(m_minute);
                      TextView txt_tm = (TextView)findViewById(R.id.lbl_fm04_start) ; 
                      txt_tm.setText(s_start);
             	  }
             	  if ((m_hour_end > 0) || (m_minute_end > 0)){
                 	  String s_end= String.valueOf(m_hour_end) + ":"+ String.valueOf(m_minute_end);
                      TextView txt_tm2 = (TextView)findViewById(R.id.lbl_fm04_end ) ; 
                      txt_tm2.setText(s_end);
             	  }
             	  //
             	 EditText txt_memo = (EditText)findViewById(R.id.txt_fm04_memo) ; 
             	 txt_memo.setText(m_Memo);
             	  
                } while(c.moveToNext());
            }
            c.close();
    	}catch(Exception e){
    		throw e;
    	}
    	return ret;
    }       
    //
    public void proc_delete(String id) {

    	try
    	{
	        mDb.delete( m_Const.TBL_TR_PLAN_DT , "_id = ?", new String[] { id });
	        
	        complete_dialog( m_Const.APP_NAME, "Complete Sucsess");
    	}catch(Exception e){
    		m_Util.errorDialog(this, e.getMessage() );
    	}
    }
    
    //
    private void proc_update() throws Exception {
    	
    	try
    	{
             String s_st= m_String.comConv_ZeroToStr(String.valueOf(m_hour), 2);
             s_st=s_st+":" + m_String.comConv_ZeroToStr(String.valueOf(m_minute), 2)+":00";
             if(s_st.equals( m_Const.STR_FM003_ZERO_TM )== true){
                 s_st = "";
             }else{
            	 s_st =m_FM5_YMD + " " + s_st;
             }
Log.d(TAG, "s_st=" +s_st);
             
             String s_end= m_String.comConv_ZeroToStr(String.valueOf(m_hour_end), 2);
             s_end=s_end+":" + m_String.comConv_ZeroToStr(String.valueOf(m_minute_end), 2)+":00";
             if(s_end.equals( m_Const.STR_FM003_ZERO_TM )== true){
            	 s_end = "";
             }else{
            	 s_end =m_FM5_YMD + " " + s_end;
             }
 Log.d(TAG, "s_end=" +s_end);             

     		EditText txt_price = (EditText)findViewById(R.id.txt_fm04_memo);
    		String s_txt = txt_price.getText().toString();

			//dt
			String sql="update tr_plan_dt set ";
			sql =sql+"  start_dt=datetime('"+s_st+"')";
			sql =sql+" ,end_dt=datetime('"+s_end+"')";
			sql =sql+" ,yote_txt='"+ s_txt +"'";
			sql =sql + " where _id= "+m_FM5_ID+";";
			mDb.execSQL(sql);
            
    		complete_dialog( m_Const.APP_NAME, "Complete Sucsess");
            return;
    	}catch(Exception e){
    		throw e;
    	}
    } 

    //
    private int get_dtCount(String s_id) throws Exception{
    	int ret=0;
    	try
    	{
            String[] param = new String[] { s_id };
        	String sql = "select _id from tr_plan_dt where hd_id = ? limit 1;";
        	Cursor c = mDb.rawQuery(sql, param);
        	ret= c.getCount();
        	
            c.close();
    	}catch(Exception e){
    		throw e;
    	}
    	return ret;
    }    
    //
    private boolean IsCheck_Val() throws Exception{
    	try{

    		//Price
    		EditText txt_price = (EditText)findViewById(R.id.txt_fm04_memo);
    		String s_txt = txt_price.getText().toString();
    		if(s_txt.length() < 1){
    			m_Util.errorDialog(this,  "[Memo] Required input");
    			return false;
    		}
    		if(s_txt.length() > m_Const.NUM_FM003_CHK_MEMO ){
    			m_Util.errorDialog(this,  "[Memo] max len=140");
    			return false;
    		}
    	}catch(Exception e){
			throw e;
    	}
    	return true;
    }    
    //
    private void show_Time(){
    	try
    	{
            final TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                    new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        	m_hour  = hourOfDay;
                        	m_minute =minute;
                        	
                       	  String s_bf = m_String.comConv_ZeroToStr(String.valueOf(m_hour), 2);
             	          s_bf =s_bf + ":" + m_String.comConv_ZeroToStr(String.valueOf(m_minute), 2);
             	         
                          TextView txt1 = (TextView)findViewById(R.id.lbl_fm04_start) ;
                          txt1.setText(s_bf);
                        }
                    }, m_hour, m_minute, true);
	            timePickerDialog.setTitle("Start Time");
                timePickerDialog.show();    		
    		
    	}catch(Exception e){
    		m_Util.errorDialog(this, e.getMessage() );
    	}
    }
    //
    private void show_TimeEnd(){
    	try
    	{
            final TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                    new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        	m_hour_end  =hourOfDay;
                        	m_minute_end =minute;
                        	
                     	  String s_bf = m_String.comConv_ZeroToStr(String.valueOf(m_hour_end), 2);
             	          s_bf =s_bf + ":" + m_String.comConv_ZeroToStr(String.valueOf(m_minute_end), 2);
             	         
                          TextView txt1 = (TextView)findViewById(R.id.lbl_fm04_end ) ;
                          txt1.setText(s_bf);
                        }
                    }, m_hour_end, m_minute_end, true);
            timePickerDialog.setTitle("End Time");
            timePickerDialog.show();    		
    		
    	}catch(Exception e){
    		m_Util.errorDialog(this, e.getMessage() );
    	}
    }  
    
    //
    private void complete_dialog(String s_title, String s_msg){
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setTitle( s_title );
        b.setMessage(s_msg);
        b.setPositiveButton("Yes",  new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog,int which) {
                	finish();
                }
            });
        b.show();
    }
    //
    private void disp_dialog(String s_msg){
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setTitle("Warning!");
        b.setMessage(s_msg);
        b.setPositiveButton("Yes",  new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog,int which) {
               	 proc_delete(m_FM5_ID);
                }
            });
        b.setNegativeButton("No", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog,int which) {
            	dialog.cancel();
            }
        });        
        b.show();
    	
    }    
    


}