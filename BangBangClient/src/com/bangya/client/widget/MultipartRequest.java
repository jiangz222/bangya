package com.bangya.client.widget;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import khandroid.ext.apache.http.entity.ContentType;
import khandroid.ext.apache.http.entity.mime.MultipartEntity;
import khandroid.ext.apache.http.entity.mime.content.FileBody;
import khandroid.ext.apache.http.entity.mime.content.StringBody;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyLog;
/**
 *  impletement for volley mutipart data request 
 */
public class MultipartRequest extends Request<String> {
    private MultipartEntity entity = new MultipartEntity();
    private final Response.Listener<String> mListener;
    private final File mFilePart;
    private final String mStringPart;
    private static final String FILE_PART_NAME = "file";
    private static final String STRING_PART_NAME = "text";
    public static  byte[] byteArrayGlobal;
    public MultipartRequest(String url, Response.ErrorListener errorListener, Response.Listener<String> listener, File file, String stringPart)
    {
        super(Method.POST, url, errorListener);

        mListener = listener;
        mFilePart = file;
        mStringPart = stringPart;
        buildMultipartEntity();
    }
	   private void buildMultipartEntity()
	    {
	        entity.addPart(FILE_PART_NAME, new FileBody(mFilePart));
	        try
	        {
	            entity.addPart(STRING_PART_NAME, new StringBody(mStringPart));
	        }
	        catch (UnsupportedEncodingException e)
	        {
	            VolleyLog.e("UnsupportedEncodingException");
	        }
	    }

	    @Override
	    public String getBodyContentType()
	    {
        	System.out.println("entity.getContentType().getValue():"+entity.getContentType().getValue());
	      return entity.getContentType().getValue();
	    }

	    @Override
	    public byte[] getBody() throws AuthFailureError
	    {
	        ByteArrayOutputStream bos = new ByteArrayOutputStream();
	        try
	        {
	            entity.writeTo(bos);
	        }
	        catch (IOException e)
	        {
	            VolleyLog.e("IOException writing to ByteArrayOutputStream");
	        }

	     //   return bos.toByteArray();
        	System.out.println("write entity:"+byteArrayGlobal.toString());
	        return byteArrayGlobal;


	    }

	    @Override
	    protected Response<String> parseNetworkResponse(NetworkResponse response)
	    {
	        return Response.success("Uploaded", getCacheEntry());
	    }

	    @Override
	    protected void deliverResponse(String response)
	    {
	        mListener.onResponse(response);
	    }




}
