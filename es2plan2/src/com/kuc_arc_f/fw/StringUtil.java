package com.kuc_arc_f.fw;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class StringUtil {

	//class
	// mode(0=YYYY/MM/DD, 1=HH:MM, 2=HH:MM:SS, 3=YYYY/MM/DD HH:MI:SS)
	public final int DateMODE_YMD      =0;
	public final int DateMODE_HHMM     =1;
	public final int DateMODE_HHMMSS   =2;
	public final int DateMODE_YMD_HMS  =3;
	public final int DateMODE_MMDD     =4;
	
	//
	public StringUtil(){
		
	}
	//
	// @purpose : 指定桁数分、ゼロ埋めで返す。(半角のみ, max=10桁)
	public String comConv_ZeroToStr( String src, int i_num )
	{
		String sRet="";
		String s_zero = "0000000000";

		if(src.length() > 10){
			return  src;
		}
		
		String buff = s_zero+ src;
		
		int i_w = buff.length();

		return buff.substring( (i_w -i_num), i_w);
	}
	
	// @arg: src=str(14 byte), typ=mode(0=YYYY/MM/DD, 1=HH:MM, 2=HH:MM:SS, 3=YYYY/MM/DD HH:MI:SS, 4=MM/DD)
	public String comConv_ToDate(String src,  int typ){
		String s_ret="";
		
		if(src.length() != 14){
			return "";
		}
		
		String s_yy = src.substring(0,4);
		String s_mm = src.substring(4,6);
		String s_dd = src.substring(6,8);
		String s_hh = src.substring(8,10);
		String s_mi = src.substring(10,12);
		String s_ss = src.substring(12,14);
		
//		com.kuc_arc_f.fw.AppConst.clsDate cls = new clsDate();
		
		if(typ== DateMODE_YMD ){
			s_ret= s_yy + "/" + s_mm + "/"+ s_dd;
		}else if(typ== DateMODE_HHMM){
			s_ret= s_hh + ":"+ s_mi;
		}else if( typ == DateMODE_MMDD){
			s_ret= s_mm + "/"+ s_dd;
		}
		return s_ret;
	}
	// @arg: src=str(14 byte), typ=mode(0=YYYY/MM/DD, 1=HH:MM, 2=HH:MM:SS, 3=YYYY/MM/DD HH:MI:SS, 4=MM/DD)
	public String comConv_ToDate02(String src,  int typ){
		String s_ret="";
		
		if(src.length() != 19){
			return "";
		}
		
		String s_yy = src.substring(0,4);
		String s_mm = src.substring(5,7);
		String s_dd = src.substring(8,10);
		String s_hh = src.substring(11,13);
		String s_mi = src.substring(14,16);
		
		if(typ== DateMODE_YMD ){
			s_ret= s_yy + "/" + s_mm + "/"+ s_dd;
		}else if(typ== DateMODE_HHMM){
			s_ret= s_hh + ":"+ s_mi;
		}else if( typ == DateMODE_MMDD){
			s_ret= s_mm + "/"+ s_dd;
		}
		return s_ret;
	}	

}
