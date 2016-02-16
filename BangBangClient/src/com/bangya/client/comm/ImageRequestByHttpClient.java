package com.bangya.client.comm;

import it.sauronsoftware.ftp4j.FTPAbortedException;
import it.sauronsoftware.ftp4j.FTPClient;
import it.sauronsoftware.ftp4j.FTPDataTransferException;
import it.sauronsoftware.ftp4j.FTPException;
import it.sauronsoftware.ftp4j.FTPIllegalReplyException;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;

import khandroid.ext.apache.http.HttpResponse;
import khandroid.ext.apache.http.client.ClientProtocolException;
import khandroid.ext.apache.http.client.HttpClient;
import khandroid.ext.apache.http.client.methods.HttpPost;
import khandroid.ext.apache.http.client.utils.URIBuilder;
import khandroid.ext.apache.http.entity.mime.MultipartEntity;
import khandroid.ext.apache.http.entity.mime.content.FileBody;
import khandroid.ext.apache.http.entity.mime.content.StringBody;
import khandroid.ext.apache.http.impl.client.DefaultHttpClient;
import khandroid.ext.apache.http.protocol.BasicHttpContext;
import khandroid.ext.apache.http.protocol.HttpContext;

public class ImageRequestByHttpClient implements Runnable {
	private int uid;
	private String filename;
	private byte[] by;
	public ImageRequestByHttpClient(byte[] tby, int tuid, String tfilename){
		uid = tuid;
		filename = tfilename;
		by = tby;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
	//	updateImage(uid,filename);
	}

    public void updateImage(int uid, String filename){
		String URITMP = WebAppClient.BASE_URI+"/user/image"; 
		URI uri = null;
		  HttpClient httpClient = new DefaultHttpClient();
		try {
			uri = new URI("UTF-8");
		} catch (URISyntaxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}  
		
		try {
			uri = new URIBuilder()
			.setPath(URITMP)
			.build();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        HttpPost req = new HttpPost(uri);
        req.addHeader("X-bangbang-Auth", WebAppClient.MY_AUTH);
        req.setHeader("Content-Type", "multipart/form-data");
        MultipartEntity entity = new MultipartEntity();
        entity.addPart("file", new FileBody(new File(filename)));

        try {
            entity.addPart("text", new StringBody(String.valueOf(uid)));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
  
        req.setEntity(entity);

        try {
			HttpResponse response = httpClient.execute(req);
			System.out.println("response of httpImageReq:"+response.toString());
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    }
}
