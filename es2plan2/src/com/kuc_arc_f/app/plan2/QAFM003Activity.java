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

public class QAFM003Activity extends Activity implements OnClickListener {
	
	private final String TAG="QAFM003Activity";
	
    private static final int REQUEST_CODE = 0;
    
    final Calendar calendar = Calendar.getInstance();
    // カレンダーから現在の '年' を取得
    private int mYear=0;
    // カレンダーから現在の '月' を取得
    private int mMonth =0;
    // カレンダーから現在の '日' を取得
    private int  mDay = 0;
    private int  m_hour   =0;
    private int  m_minute =0;
    private int  m_hour_end   =0;
    private int  m_minute_end =0;
    
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
        setContentView(R.layout.qafm003);
        String s_ver = m_Util.comGet_VersionName(this, m_Const.APP_PKG_NAME );
		setTitle(m_Const.APP_NAME + " v"+  s_ver);        
        
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //
        ImageButton button = (ImageButton)findViewById(R.id.btn_fm03_rec) ;
        button.setOnClickListener(this);
        
        Button btn_dt = (Button)findViewById(R.id.btn_fm3_date ) ;
        btn_dt.setOnClickListener(this);

        Button btn_start = (Button)findViewById(R.id.btn_fm3_start ) ;
        btn_start.setOnClickListener(this);

        Button btn_end = (Button)findViewById(R.id.btn_fm3_end ) ;
        btn_end.setOnClickListener(this);
        
        ImageButton btn_save = (ImageButton)findViewById(R.id.btn_fm03_save2 ) ;
        btn_save.setOnClickListener(this);
    }
    //
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
            
            if(m_VO_FLG==0){
    			mYear = calendar.get(Calendar.YEAR);
    			mMonth = calendar.get(Calendar.MONTH);
    			mDay = calendar.get(Calendar.DAY_OF_MONTH);
    			
    	       	 String s_bf = m_String.comConv_ZeroToStr(String.valueOf(mMonth+1), 2);
    	                s_bf =s_bf + "/" + m_String.comConv_ZeroToStr(String.valueOf(mDay), 2);

    	         TextView txt1 = (TextView)findViewById(R.id.lbl_fm03_date) ;
                 txt1.setText(s_bf);

                 TextView txt_tm = (TextView)findViewById(R.id.lbl_fm03_start) ;
                 txt_tm.setText("Nothing");
                 TextView txt_tm2 = (TextView)findViewById(R.id.lbl_fm03_end ) ;
                 txt_tm2.setText("Nothing");
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
    	if(v.getId() == R.id.btn_fm03_rec){
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
//                Toast.makeText( hello_01.this,
  //                  "ActivityNotFoundException", Toast.LENGTH_LONG).show();
            	m_Util.errorDialog(this, "[Not Install Activity]"+ e.getMessage() );
            }catch(Exception e){
            	m_Util.errorDialog(this, e.getMessage() );
            }
    	}else if(v.getId() == R.id.btn_fm3_date){
    		show_Date();
    	}else if(v.getId() == R.id.btn_fm3_start){
    		show_Time();
    	}else if(v.getId() == R.id.btn_fm3_end){
    		show_TimeEnd();
    	}else if(v.getId() == R.id.btn_fm03_save2 ){
    		try
    		{
    			if(IsCheck_Val() == true){
            		proc_add();
    			}
    		}catch(Exception e){
    			m_Util.errorDialog(this, e.getMessage());
    		}
    	}
    }
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
                //m_Util.showDialog(this,  "ResultsString", resultsString);
                
                //Set
                EditText txt1 = (EditText)findViewById(R.id.txt_fm03_memo) ;
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
    private void proc_add() throws Exception {
    	
    	try
    	{
	       	 String s_bf = m_String.comConv_ZeroToStr(String.valueOf(mYear), 4) ;
	       	 s_bf = s_bf + "-" +m_String.comConv_ZeroToStr(String.valueOf(mMonth+1), 2);
             s_bf = s_bf + "-" + m_String.comConv_ZeroToStr(String.valueOf(mDay), 2);
             
             String s_st= m_String.comConv_ZeroToStr(String.valueOf(m_hour), 2);
             s_st=s_st+":" + m_String.comConv_ZeroToStr(String.valueOf(m_minute), 2)+":00";
             if(s_st.equals( m_Const.STR_FM003_ZERO_TM )== true){
                 s_st = "";
             }else{
            	 s_st =s_bf + " " + s_st;
             }

             String s_end= m_String.comConv_ZeroToStr(String.valueOf(m_hour_end), 2);
             s_end=s_end+":" + m_String.comConv_ZeroToStr(String.valueOf(m_minute_end), 2)+":00";
             if(s_end.equals( m_Const.STR_FM003_ZERO_TM )== true){
            	 s_end = "";
             }else{
            	 s_end =s_bf + " " + s_end;
             }

     		EditText txt_price = (EditText)findViewById(R.id.txt_fm03_memo);
    		String s_txt = txt_price.getText().toString();
    		
//    		String s_ct = get_hdCount2( "date('" +  s_bf + "')");
    		String s_ct = get_hdCount2( s_bf );
     Log.d(TAG, "s_ct=" + s_ct);
     Log.d(TAG, "s_ct_len=" + String.valueOf(s_ct.length()) );
    		if ((s_ct.length()<1)==true) {
              	//hd
                 ContentValues values = new ContentValues();
                 values.put("s_date"  , s_bf );
                 long id = mDb.insert( m_Const.TBL_TR_PLAN_HD , null, values);
                 if (id < 0) {
                 	Log.d(TAG, "proc_add NG") ;
                     return;
                 }
     Log.d(TAG, "id=" + String.valueOf(id));
			     s_ct = String.valueOf(id);
            }
             
 Log.d(TAG, "s_bf=" +s_bf);
			//dt
			String sql="insert into tr_plan_dt(hd_id , start_dt, end_dt, yote_txt, createat)values ";
			sql =sql + "("+ s_ct;
			sql =sql + ",datetime('"+ s_st   +"')";
			sql =sql + ",datetime('"+ s_end  +"')";
			sql =sql + ",'"+ s_txt +"'";
			sql =sql + ", datetime('now') );";
			mDb.execSQL(sql);
			
			complete_dialog( m_Const.APP_NAME, "Complete Sucsess");
            return;
    	}catch(Exception e){
    		throw e;
    	}
    } 
    //
    private String get_hdCount2(String s_dt) throws Exception{
    	String ret="";
    	try
    	{
            String[] param = new String[] { s_dt };
        	String sql = "select _id from tr_plan_hd where s_date = ? limit 1;";
        	Cursor c = mDb.rawQuery(sql, param);
            if (c.moveToFirst()) {
                do {
             	  ret     = c.getString( 0 );
                } while(c.moveToNext());
            }
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
    		EditText txt_price = (EditText)findViewById(R.id.txt_fm03_memo);
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
    private void show_Date()
    {
    	try
    	{
        	final DatePickerDialog datePickerDialog = new DatePickerDialog(
                    this,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            mYear = year;
                            mMonth = monthOfYear;
                            mDay = dayOfMonth;

                            Log.d(TAG, "mMonth=" +String.valueOf(mMonth +1)+ "/" +String.valueOf(mDay));
                            Toast.makeText(QAFM003Activity.this,
                                    String.valueOf(year) + "/" +
                                    String.valueOf(monthOfYear + 1) + "/" +
                                    String.valueOf(dayOfMonth),
                                    Toast.LENGTH_SHORT).show();
                            
                         	  String s_bf = m_String.comConv_ZeroToStr(String.valueOf(mMonth+1), 2);
                 	          s_bf =s_bf + "/" + m_String.comConv_ZeroToStr(String.valueOf(mDay), 2);
                 	         
                              TextView txt1 = (TextView)findViewById(R.id.lbl_fm03_date) ;
                              txt1.setText(s_bf);
                        }
                    },
                    mYear, mMonth, mDay);
                datePickerDialog.show();
    		
    	}catch(Exception e){
    		m_Util.errorDialog(this, e.getMessage() );
    	}
    }
    //
    private void show_Time(){
    	try
    	{
            final Calendar calendar = Calendar.getInstance();
            final int hour = calendar.get(Calendar.HOUR_OF_DAY);
            final int minute = calendar.get(Calendar.MINUTE);
            
            final TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                    new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        	m_hour  = hourOfDay;
                        	m_minute =minute;
                        	
                       	  String s_bf = m_String.comConv_ZeroToStr(String.valueOf(m_hour), 2);
             	          s_bf =s_bf + ":" + m_String.comConv_ZeroToStr(String.valueOf(m_minute), 2);
             	         
                          TextView txt1 = (TextView)findViewById(R.id.lbl_fm03_start) ;
                          txt1.setText(s_bf);
                        }
                    }, hour, minute, true);
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
            final Calendar calendar = Calendar.getInstance();
            final int hour = calendar.get(Calendar.HOUR_OF_DAY);
            final int minute = calendar.get(Calendar.MINUTE);
    		
            final TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                    new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        	m_hour_end  =hourOfDay;
                        	m_minute_end =minute;
                        	
                     	  String s_bf = m_String.comConv_ZeroToStr(String.valueOf(m_hour_end), 2);
             	          s_bf =s_bf + ":" + m_String.comConv_ZeroToStr(String.valueOf(m_minute_end), 2);
             	         
                          TextView txt1 = (TextView)findViewById(R.id.lbl_fm03_end ) ;
                          txt1.setText(s_bf);
                        }
                    }, hour, minute, true);
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
//                	dialog.dismiss();
                	finish();
                }
            });
        b.show();
    }



}