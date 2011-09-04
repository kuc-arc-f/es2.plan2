package com.kuc_arc_f.app.plan2;

import android.util.Log;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import com.kuc_arc_f.fw.*;

public class QAFM001Activity extends Activity {
	
	private final String TAG="QAFM001Activity";
	
	//fw
	private com.kuc_arc_f.fw.AppConst m_Const = new AppConst();
	
	private com.kuc_arc_f.fw.ComUtil m_Util = new ComUtil();

	private com.kuc_arc_f.fw.HttpUtil m_Http = new HttpUtil();
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }


}