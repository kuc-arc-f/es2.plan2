package com.kuc_arc_f.fw;

import org.apache.http.*;
//import org.apache.http.HttpEntity;
//import org.apache.http.HttpResponse;
//import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
//import org.apache.http.params.HttpConnectionParams;
//import org.apache.http.params.HttpParams;
//import org.apache.http.protocol.BasicHttpContext;
//import org.apache.http.protocol.DefaultedHttpContext;
//import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import android.util.Log;

public class HttpUtil {
	
	private String TAG ="HttpUtil";

    //
    public String doGet( String url )
    {
        try
        {
        	
            HttpGet method = new HttpGet( url );

            DefaultHttpClient client = new DefaultHttpClient();

            // ヘッダを設定する
            method.setHeader( "Connection", "Keep-Alive" );
            
            HttpResponse response = client.execute( method );
            int status = response.getStatusLine().getStatusCode();
            if ( status != HttpStatus.SC_OK )
                throw new Exception( "" );
            
            return EntityUtils.toString( response.getEntity(), "UTF-8" );
        }
        catch ( Exception e )
        {
        	Log.d( "doGet",  e.getMessage().toString() );
            return null;
        }
    }
    //
	public String doPost( String url, String params )
	{
	    try
	    {            
	        HttpPost method = new HttpPost( url );
	        
	        DefaultHttpClient client = new DefaultHttpClient();
	        
	        // POST データの設定
	        StringEntity paramEntity = new StringEntity( params );
	        paramEntity.setChunked( false );
	        paramEntity.setContentType( "application/x-www-form-urlencoded" );
	        method.setEntity( paramEntity );
	        
	        HttpResponse response = client.execute( method );
	        int status = response.getStatusLine().getStatusCode();
	        if ( status != HttpStatus.SC_OK )
	            throw new Exception( "" );
	        
	        return EntityUtils.toString( response.getEntity(), "UTF-8" );
	    }
	    catch ( Exception e )
	    {
	        return null;
	    }
	}
    //
    public String check_URL( String url ) throws Exception
    {
        try
        {
        	
            HttpGet method = new HttpGet( url );

            DefaultHttpClient client = new DefaultHttpClient();

            // ヘッダを設定する
            method.setHeader( "Connection", "Keep-Alive" );
            
            HttpResponse response = client.execute( method );
            int status = response.getStatusLine().getStatusCode();
Log.d(TAG, "status="+ status );
            if ( status != HttpStatus.SC_OK ){
            	String s_buff="Http ErrorCode="+ String.valueOf(status);
                throw new Exception( s_buff );
            }
            
            return EntityUtils.toString( response.getEntity(), "UTF-8" );
        }
        catch ( Exception e )
        {
        	throw e;
        }
    }
    
}
