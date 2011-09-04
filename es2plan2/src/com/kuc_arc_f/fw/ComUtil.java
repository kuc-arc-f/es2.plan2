package com.kuc_arc_f.fw;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import android.app.Activity;
import android.app.ProgressDialog;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;

public class ComUtil extends Activity {

	private StringUtil m_String = new StringUtil();
	
	//
	// @purpose : Calender -> String(YYYY-MM-DD HH:MI:SS)
	public String comConv_CalenderToString( GregorianCalendar c )
	{
		String sRet="";
		
		sRet = String.valueOf(c.get(Calendar.YEAR));
		sRet = sRet + "-" +String.valueOf(c.get(Calendar.MONTH)+1);
		sRet = sRet + "-" +String.valueOf(c.get(Calendar.DAY_OF_MONTH));

		sRet = sRet + " " +String.valueOf(c.get(Calendar.HOUR_OF_DAY));
		sRet = sRet + ":" +String.valueOf(c.get(Calendar.MINUTE));
		sRet = sRet + ":" +String.valueOf(c.get(Calendar.SECOND));

		return sRet;
	}
	//
	// @purpose : Calender -> String(YYYY-MM-DD)
	public String comConv_DateToString2( GregorianCalendar c )
	{
		String sRet="";
		
		String s_yy = String.valueOf(c.get(Calendar.YEAR));
		s_yy = m_String.comConv_ZeroToStr(s_yy, 4);
		String s_mm = String.valueOf(c.get(Calendar.MONTH)+1);
		s_mm = m_String.comConv_ZeroToStr(s_mm, 2);
		String s_dd = String.valueOf(c.get(Calendar.DAY_OF_MONTH));
		s_dd = m_String.comConv_ZeroToStr(s_dd, 2);
		
		sRet = s_yy + "-" +s_mm + "-"+ s_dd;

		return sRet;
	}
	//
	// @purpose : Calender -> String(YYYY-MM-DD)
	public String comConv_DateToS( Calendar  c )
	{
		String sRet="";
		
		String s_yy = String.valueOf(c.get(Calendar.YEAR));
		s_yy = m_String.comConv_ZeroToStr(s_yy, 4);
		String s_mm = String.valueOf(c.get(Calendar.MONTH)+1);
		s_mm = m_String.comConv_ZeroToStr(s_mm, 2);
		String s_dd = String.valueOf(c.get(Calendar.DAY_OF_MONTH));
		s_dd = m_String.comConv_ZeroToStr(s_dd, 2);
		
		sRet = s_yy + "-" +s_mm + "-"+ s_dd;

		return sRet;
	}
	
    // （15）完成時にダイアログを表示する
	public void com_MsgBox(String s_title, String s_msg, String s_msg2){
//        long msec = stopChronometer();
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setTitle( s_title );
        b.setMessage(s_msg);
//        b.setIcon(R.drawable.congratulations);
        b.setPositiveButton(
        		s_msg2,
            new DialogInterface.OnClickListener(){
                // ボタンが押されたらダイアログを閉じる
                public void onClick(DialogInterface dialog,int which) {
                    dialog.dismiss();
                }
            });
        b.show();
    }
	
    // ダイアログの表示
    public  void showDialog(final Activity activity, String title, String text) {
        AlertDialog.Builder ad = new AlertDialog.Builder(activity);
        ad.setTitle(title);
        ad.setMessage(text);
        ad.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
               activity.setResult(Activity.RESULT_OK);
            }
        });
        ad.create();
        ad.show();
    }
    
    public void errorDialog(final Activity activity,  String text){
    	showDialog(activity, "Errror", text);
    }
    
    //
    public String comGet_VersionName(final Activity activity, String s_pnm){
        PackageManager pm = activity.getPackageManager();
		String s_ver="";
        //---get the package info---
		try{
	        PackageInfo pi =  
	            pm.getPackageInfo(s_pnm , 0);
	        s_ver = pi.versionName;
		}catch(Exception e){
			Log.d("comGet_VersionName", "Error_PackageInfo" );
			Log.d("comGet_VersionName", e.getMessage() );
		}   	
    	return s_ver;
    } 

    
}
