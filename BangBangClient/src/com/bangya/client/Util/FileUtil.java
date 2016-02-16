package com.bangya.client.Util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.bangya.client.BBUI.BaseUtil;
import com.joeapp.bangya.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

public class FileUtil {
    public final static String HEAD_PATH=Environment.getExternalStorageDirectory()+File.separator +"bangya"+File.separator +"head";
    public final static String APK_PATH=Environment.getExternalStorageDirectory()+File.separator +"bangya"+File.separator +"apk"+File.separator;

    private final static String  TAG = "FileUtil";
    
    public static void createBangYaDIR()
    {
        File avatarPath = new File(HEAD_PATH);
        if (avatarPath.exists() == false) {
        	Log.e(TAG,"create dir:"+HEAD_PATH);
        	avatarPath.mkdirs();
        }
        File apkDownLoadPath = new File(APK_PATH);
        if (apkDownLoadPath.exists() == false) {
        	Log.e(TAG,"create dir:"+APK_PATH);
        	apkDownLoadPath.mkdirs();
        }
    }
    
    

    /**
	 * 创建一个以userId为文件名的头像文件
	 */
    public static File createHeadImage(String userid)
    {
        File fileParent = new File(HEAD_PATH);
        if (fileParent.exists() == false) {
        	Log.e(TAG,"create dir:"+HEAD_PATH);
            fileParent.mkdirs();
        }
        File file = null;
        file = new File(HEAD_PATH + File.separator  + userid+ ".jpg");
        if (file.exists() == false) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "file create fail"+e);
            }
        }
        Log.i(TAG, "create file: "+file);
        return file;
    }
	public static boolean writeFile(ContentResolver cr, File file, Uri uri) {
	    Log.i("FileUtil", "cr="+cr+", file="+file+", uri="+uri);
		boolean result=true;
		try {
			FileOutputStream fout = new FileOutputStream(file);
			Log.i(TAG, "fout="+fout);
			Bitmap bitmap=BitmapFactory.decodeStream(cr.openInputStream(uri));
			Log.i(TAG, "bitmap="+bitmap);
			//bitmap转为jpeg
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fout);
			
			try {
				fout.flush();
				fout.close();
			} catch (IOException e) {
				e.printStackTrace();
				Log.e(TAG, "flush file to sdcard exception: "+file);
				result=false;
			}
		} catch (FileNotFoundException e) {
			Log.e(TAG, "file not find exception: "+file);
			e.printStackTrace();
			result=false;
		}catch(Exception e){
		    Log.e(TAG, "exception="+e.toString());
		}
		return result;
	}


}
