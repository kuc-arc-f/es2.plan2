package com.kuc_arc_f.app.plan2;

import java.util.Calendar;
import java.util.GregorianCalendar;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.ListView;

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

public class QAFM002Activity extends ListActivity implements OnClickListener , SimpleCursorAdapter.ViewBinder {
	
	private final String TAG="QAFM002Activity";
	
	private static final int[] TO = { R.id._id_fm002, R.id.lbl_row_fm002_dt, R.id.lbl_row_fm002_txt ,R.id.lbl_row_fm002_ymd , R.id.lbl_row_fm002_ct };
	
	private final int COL_ID   =0;
	private final int COL_DATE =COL_ID +1;
	private final int COL_TXT  =COL_ID +2;
	private final int COL_YMD  =COL_ID +3;	
	private final int COL_CNT  =COL_ID +4;
	
	//db
    private SQLiteDatabase mDb;
    
    //
    private String m_NOW_YYMM ="";
    
	private String  m_YYMM_DLG="";
    //
	private static final int MENU1_ID = Menu.FIRST;
	private static final int MENU2_ID = Menu.FIRST+1;
    
	//fw
	private com.kuc_arc_f.fw.AppConst m_Const = new AppConst();
	private com.kuc_arc_f.fw.ComUtil m_Util = new ComUtil();
	private com.kuc_arc_f.fw.StringUtil m_String = new StringUtil();
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qafm002);
        String s_ver = m_Util.comGet_VersionName(this, m_Const.APP_PKG_NAME );
		setTitle(m_Const.APP_NAME + " v"+  s_ver);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        
        //
        ImageButton button = (ImageButton)findViewById(R.id.btn_fm02_add ) ;
        button.setOnClickListener(this);
    }
    
    //
    @Override
    protected void onResume() {
        super.onResume();
        
        try
        {
			String s_yymm = get_monthStr();
			
        	//set_lbl
            TextView txt_tm = (TextView)findViewById(R.id.lbl_fm02_yymm) ;
            if(m_NOW_YYMM.length() > 0){
                s_yymm = m_NOW_YYMM;
            }
            txt_tm.setText(s_yymm);
            
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
            init_proc(s_yymm);
            Thread.sleep(100);
            
            build_list3();
        }catch(Exception e){ 
        	Log.d(TAG, e.getMessage());
        	m_Util.errorDialog(this, e.getMessage());
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
    	if(v.getId() == R.id.btn_fm02_add){
            Intent intent = new Intent(getApplicationContext(),QAFM003Activity.class);
            startActivity(intent);	
    	}
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
		case COL_YMD:
			TextView txt_ymd = (TextView) view;
			txt_ymd.setText( cursor.getString(columnIndex));
			return true;
		case COL_CNT:
			TextView txt_cnt = (TextView) view;
			txt_cnt.setText( cursor.getString(columnIndex));
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

        // タップされた行のIDとメモの内容をテキスト編集ビューに設定
        LinearLayout ll = (LinearLayout)v;
        TextView t    = (TextView)ll.findViewById(R.id._id_fm002);
        TextView t_dt = (TextView)ll.findViewById(R.id.lbl_row_fm002_dt );
        TextView t_ymd = (TextView)ll.findViewById(R.id.lbl_row_fm002_ymd );
        TextView t_cnt = (TextView)ll.findViewById(R.id.lbl_row_fm002_ct  );
        
        String s_id = t.getText().toString();
        String s_dt = t_dt.getText().toString();
        String s_ymd = t_ymd.getText().toString();
        String s_cnt = t_cnt.getText().toString();
        
		android.content.SharedPreferences common = getSharedPreferences(
				m_Const.KEY_PREF_USR,
            MODE_WORLD_READABLE | MODE_WORLD_WRITEABLE);		        
        android.content.SharedPreferences.Editor editor = common.edit();
        editor.putString(m_Const.KEY_FM002_ID, s_id );
        editor.putString(m_Const.KEY_FM002_DT, s_dt );        
        editor.putString(m_Const.KEY_FM002_YMD, s_ymd );        
        editor.commit(); 

        //next
        if(s_cnt.length() > 0){
        	if(s_cnt.equals("0") == false){
                Intent intent = new Intent(getApplicationContext(),QAFM005Activity.class);
                startActivity(intent);	
        	}
        }
    }	
	//
	private String get_monthStr(){
		
	    Calendar calendar = Calendar.getInstance();
		int yy = calendar.get(Calendar.YEAR);
		int mm = calendar.get(Calendar.MONTH);
		
		String s_bf = m_String.comConv_ZeroToStr(String.valueOf(yy), 4);
			   s_bf = s_bf + "-" +m_String.comConv_ZeroToStr(String.valueOf(mm+1), 2);
		return s_bf;
	}
	
	//
	private void build_list3() throws Exception{
		
		try
		{
			
			String s_yymm = get_monthStr();
			if(m_NOW_YYMM.length() > 0){
				s_yymm= m_NOW_YYMM;
			}
        	String sql ="SELECT tr_plan_hd._id, substr(ms_date.b_date, 6)"; 
        	sql =sql +" , (SELECT yote_txt from tr_plan_dt where hd_id= tr_plan_hd._id) as s_txt";
        	sql =sql +" , (SELECT COUNT(*) from tr_plan_dt where hd_id= tr_plan_hd._id limit 1) as s_ct ";
        	sql =sql +" , tr_plan_hd.s_date";
        	sql =sql +" from ms_date LEFT OUTER JOIN tr_plan_hd ON(tr_plan_hd.s_date =ms_date.b_date )";
        	sql =sql +" where ms_date.b_date  like '" +s_yymm+ "%' order by ms_date.b_date;";

        	Cursor c = mDb.rawQuery(sql, null );
Log.d(TAG, "sql=" +sql);
        	
            // すべてのデータのカーソルを取得
        	String[] list = { "_id",  "s_date", "s_txt", "s_ymd" ,"s_ct" };
    		MatrixCursor ma = new MatrixCursor(list);
            if (c.moveToFirst()) {
                do {
                    String s_id   = c.getString(COL_ID );
                    String s_dt   = c.getString(COL_DATE );
                    String s_txt  = c.getString(COL_TXT );
                    String s_ct   = c.getString(3);
                    String s_ymd  = c.getString(4);
                    
            	    String s_dt2 =  "<font color='Blue'><b>" +s_dt +": </B></font>";
            	    String s_text = "";
            	    if(s_txt !=null){
            	    	s_text = "<font color='#000'>" + s_txt  +"</font><BR>";
            	    }else{
            	    	s_text="";
            	    }
            	    if(s_ct.length() > 0){
            	    	int i_dtCt= Integer.parseInt(s_ct);
            	    	if(i_dtCt > 1){
            	    		s_text = s_text+"<font color='#C0C0C0'><b>Other..</b></font>";
            	    	}
            	    }

            	    ma.addRow(new Object[] { s_id, s_dt2, s_text , s_ymd, s_ct });
                } while(c.moveToNext());
            }
            // ベースクラスにカーソルのライフサイクルを管理させる
           startManagingCursor(c);
           
    		SimpleCursorAdapter adapter = new SimpleCursorAdapter(getApplicationContext(), 
    				R.layout.fm002_row , ma, list, TO);
    		adapter.setViewBinder(this);
    		setListAdapter(adapter);    
			
		}catch(Exception e){ 
			throw e; 
		}
	}
	//
	private void init_proc(String s_yymm) throws Exception{
		try
		{
		    Calendar start = Calendar.getInstance();
		    Calendar end   = Calendar.getInstance();

			//2011-03
			if(s_yymm.length()==7){
				String s_yy_bf=s_yymm.substring(0, 4);
				String s_mm_bf=s_yymm.substring(5, 7);
				
				int i_yy= Integer.parseInt(s_yy_bf);
				int i_mm= Integer.parseInt(s_mm_bf);				

				start.set(Calendar.YEAR, i_yy);
				start.set(Calendar.MONTH, i_mm -1);
				end.set(Calendar.YEAR, i_yy);
				end.set(Calendar.MONTH, i_mm -1);
			}
		    start.set(Calendar.DAY_OF_MONTH, 1);
		    end.set(Calendar.DAY_OF_MONTH,  1);
		    
	    	end.add(Calendar.MONTH ,1);
	    	
	    	String s_dt= m_Util.comConv_DateToS(start);
	    	if(get_msCount(s_dt) > 0){
	    		return;
	    	}
	    	
	    	long diff = (end.getTimeInMillis() -start.getTimeInMillis()) / 1000 / 60 / 60 / 24 ;
	    	for(int i=0; i<diff; i++){
	    		
Log.d(TAG, "start=" +start.toString());
	    		String s_add= m_Util.comConv_DateToS(start);
Log.d(TAG, "s_add=" + s_add);

	    		//
                ContentValues values = new ContentValues();
                values.put("b_date"  , s_add );
                long id = mDb.insert( m_Const.TBL_MS_DATE , null, values);
                if (id < 0) {
                	Log.d(TAG, "init_proc NG") ;
                    return;
                }
                start.add(Calendar.DAY_OF_MONTH, 1);
	    	}
	    	// m_Util.showDialog(this,"Info2", String.valueOf(diff));
		}catch(Exception e){ throw e; }
	}
    //
    private void disp_dialog() throws Exception{
    	try
    	{
    		int i_span=24;
			final String[] str_items = new String[i_span];
			String s_dlg_tit ="Select Month";
			int l_rec =0;
    		
		    Calendar cal = Calendar.getInstance();
		    
		    for(int i=0; i< i_span; i++ ){
			    
				String s_yy = String.valueOf(cal.get(Calendar.YEAR));
				s_yy = m_String.comConv_ZeroToStr(s_yy, 4);
				String s_mm = String.valueOf(cal.get(Calendar.MONTH)+1);
				s_mm = m_String.comConv_ZeroToStr(s_mm, 2);
				
				String s_yymm= s_yy +"-" + s_mm;
				str_items[i] = s_yymm;
				if(s_yymm.equals( m_NOW_YYMM ) ==true){
					l_rec = i;
				}
			    cal.add(Calendar.MONTH, -1);
		    }
			  new AlertDialog.Builder(this)
	            .setTitle(s_dlg_tit)
	            .setSingleChoiceItems(str_items, l_rec,  new DialogInterface.OnClickListener(){
	            	public void onClick(DialogInterface dialog, int which) {
	            		m_YYMM_DLG= str_items[which];
	            	}
	            })
	            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
	            	public void onClick(DialogInterface dialog, int whichButton) {
	    				try
	    				{
	    					m_NOW_YYMM = m_YYMM_DLG;
	    					Log.d(TAG, m_NOW_YYMM);
	    		            init_proc(m_NOW_YYMM);
	    		            build_list3();
	    					// lbl
	    		            TextView txt_tm = (TextView)findViewById(R.id.lbl_fm02_yymm) ;	    					
	    		            txt_tm.setText(m_NOW_YYMM);
	    				}catch(Exception e){};
	    				dialog.dismiss();
	            	}
	            })
	            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	            	public void onClick(DialogInterface dialog, int whichButton) {
	    				dialog.dismiss();
	            	}
	            })
	            .show();

		    
	    }catch(Exception e){ throw e; }    	
    }
	// Menu Build
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
    	boolean result = super.onCreateOptionsMenu(menu);

    	menu.add(0, MENU1_ID, Menu.NONE, "Select Month");
    	
    	return result;
    }
    
	// Menu Click
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
    	switch( item.getItemId() ){
    	case MENU1_ID :
    		try
    		{
        		disp_dialog();
	    	}catch(Exception e){
	    		m_Util.errorDialog(this, e.getMessage());
	    	}    		
    		return true;
    	}
    	
    	return super.onOptionsItemSelected(item);
    }
	//
    private int get_msCount(String s_dt) {

        int i_ct = 0;

        String[] param = new String[] { s_dt };
    	String sql = "select * from ms_date where b_date = ?;";
    	Cursor c = mDb.rawQuery(sql, param);
    	
        i_ct = c.getCount();
        c.close();
        
        return i_ct;
    }


}