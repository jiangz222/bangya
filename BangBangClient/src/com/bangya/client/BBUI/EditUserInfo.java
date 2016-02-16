package com.bangya.client.BBUI;


import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


import com.bangya.client.Util.FileUtil;
import com.bangya.client.Util.MessageId;
import com.bangya.client.adapter.UILProvider;
import com.bangya.client.comm.InnerCommInterface;
import com.bangya.client.model.User;
import com.joeapp.bangya.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

public class EditUserInfo extends Activity implements InnerCommInterface {
	private final String TAG = "EditUsrInfo";
	private final int PHOTO_FROM_CAMERA = 1;
	private final int PHOTO_FROM_FILE = 2;
	private final int PHOTO_FROM_CROP = 3;

	private String TITLE_NAME = "编辑个人信息";
	private static Uri fileUri;
	private String tmpHeadImage = null;
	private User editNewUserInfo  = new User();;
	public  static String  iii;
	TextView nicknameview;
	TextView emailview;
	TextView sexview;
	TextView cityview;
	TextView ageview;
	TextView birthdayview;
	Button edit_confirm;
	Button edit_cancel;
	private CircleProgressDiaglog cpd;
	BaseUtil bu=null;
	DisplayImageOptions options;
	User user;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "on create");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edituserinfo);
		getUserInfo();
		bu = new BaseUtil();
		 
		options = new DisplayImageOptions.Builder()
			.showImageOnLoading(R.drawable.default_head)
			.showImageForEmptyUri(R.drawable.default_head)
			.showImageOnFail(R.drawable.default_head)
			.cacheOnDisk(false)
			.cacheInMemory(false)
			.considerExifParams(true)
			.bitmapConfig(Bitmap.Config.RGB_565)
			.build();
		BaseUtil.innerComm.addCurrentActivity(this);
		user = (User)this.getIntent().getExtras().getSerializable("user");  
		initUI();
		getActionBar().setTitle(TITLE_NAME);   

	}
	@Override
	protected void onResume(){
		Log.i(TAG, "onresume");
		super.onResume();
	}
	@Override
	protected void onDestroy() {
		Log.i(TAG, "onDestroy");
		super.onDestroy();
	}
	private void initUI()
	{
		String points = "0";
		nicknameview = (TextView)findViewById(R.id.edit_username);
		RatingBar pointsview = (RatingBar)findViewById(R.id.edit_points);
		TextView tvPoints = (TextView)findViewById(R.id.edit_points_desc);
		TextView uidview = (TextView)findViewById(R.id.edit_userid);
		emailview = (TextView)findViewById(R.id.edit_email);
		TextView editImageHeadInput = (TextView)findViewById(R.id.edit_image_head_input);

		
		ageview = (TextView)findViewById(R.id.edit_age);
		sexview = (TextView)findViewById(R.id.edit_sex);
		cityview = (TextView)findViewById(R.id.edit_city);
		birthdayview = (TextView)findViewById(R.id.edit_birthday);
		edit_confirm = (Button)findViewById(R.id.edit_confirm);
		edit_cancel = (Button)findViewById(R.id.edit_cancel);
		
		LinearLayout emailLinearlayout = (LinearLayout)findViewById(R.id.edit_email_linearlayout);
		LinearLayout editUsernameLayout = (LinearLayout)findViewById(R.id.edit_username_layout);
		LinearLayout editSexLinearlayout = (LinearLayout)findViewById(R.id.edit_sex_linearlayout);
		LinearLayout editBirthdayLinearlayout = (LinearLayout)findViewById(R.id.edit_birthday_linearlayout);
		LinearLayout editAgeLinearlayout = (LinearLayout)findViewById(R.id.edit_age_linearlayout);
		LinearLayout editImageHeadLayout = (LinearLayout)findViewById(R.id.edit_image_head_layout);

		
		LinearLayout cityLayout = (LinearLayout)findViewById(R.id.edit_city_layout);
		cityLayout.setVisibility(View.GONE); // because we locate anywhere needed, do not need city
		//because city is invisible, we hide the dividerLine here, if city option is open, delete this too
		View dividerBelowCity = (View)findViewById(R.id.dividerBelowCity);
		dividerBelowCity.setVisibility(View.GONE);
		if(user.getUserId() != BaseUtil.getSelfUserInfo().getUserId())
		{// show user info is not my information
			// do not show email to others
			emailLinearlayout.setVisibility(View.GONE);
			// invisible divider below email
			View dividerBelowEmail = (View)findViewById(R.id.divider_below_email);
			dividerBelowEmail.setVisibility(View.GONE);
			// do not need confirm and cancel
			edit_confirm.setVisibility(View.GONE);
			edit_cancel.setVisibility(View.GONE);
			editImageHeadInput.setVisibility(View.GONE);

		}else{
			//  allow to edit  information of user only to myslef
			editUsernameLayout.setOnClickListener(listener);
			// do not allow edit email in current version
		//	emailLinearlayout.setOnClickListener(listener); 
			editSexLinearlayout.setOnClickListener(listener);
			editBirthdayLinearlayout.setOnClickListener(listener);
			editAgeLinearlayout.setOnClickListener(listener);
			editImageHeadLayout.setOnClickListener(listener);
		}
		ImageView headimage = (ImageView)findViewById(R.id.edit_image_head);
		headimage.setOnClickListener(listener);
		
		nicknameview.setText(user.getNickName());
		if(0 != user.getCompleteJobCount()){
			points = (new DecimalFormat("#.##")).format((float)user.getPoints()/user.getCompleteJobCount());
		}
		String showPoints = points+"分 /"+Integer.toString(user.getCompleteJobCount())+"次参与帮助";
		pointsview.setRating((float)user.getPoints()/user.getCompleteJobCount());
		tvPoints.setText(showPoints);
		
		// email only works for myself,otherwise  it is invisible
		emailview.setText(BaseUtil.getSelfUserInfo().getEmail());
		
		ageview.setText(Integer.toString(user.getAge()));
		
		sexview.setText(user.getGenderName());
		
		cityview.setText(BaseUtil.getSelfUserInfo().getCityName());
		birthdayview.setText(new SimpleDateFormat("yyyy-MM-dd").format(user.getBirthDay()));

		uidview.setText(Integer.toString(user.getUserId()));
		ImageLoader.getInstance().displayImage(UILProvider.getLocalPhotowithPath(user.getUserId()), headimage, options);

		//先保存好更改前信息,后面有更新后更新，并上传给服务器
		//这样当返回时，把editNewUserInfo赋值给self时不会覆盖self中没有修改的信息
		editNewUserInfo = (User)BaseUtil.getSelfUserInfo().clone();
	} 

/**
 * 按键时监听
*/
private View.OnClickListener listener = new View.OnClickListener() {
	@Override
	public void onClick(View view) {
		if(view.getId() == R.id.edit_image_head){
			Intent it=new Intent(EditUserInfo.this,ShowImageInFullScreen.class);
        	it.putExtra("uid",user.getUserId());
			startActivity(it);
		}else if(view.getId() == R.id.edit_username_layout){
			editUserName();
		}else if (view.getId() == R.id.edit_email_linearlayout){
			editEmail();
		}else if(view.getId() == R.id.edit_sex_linearlayout){
			editSex();
		}else if (view.getId() == R.id.edit_birthday_linearlayout){
			editBirthDay();
		}else if (view.getId() == R.id.edit_age_linearlayout){
			editAge();
		}else if (view.getId() == R.id.edit_image_head_layout){
			editImageHead();
		}
	}
	}; 
	/**
	 * edit user name
	 * @param view
	 */
	public void editUserName()
	{
		final EditText edittextname = new EditText(this);
		
		new AlertDialog.Builder(this)
			.setTitle("请输入昵称")
			.setView(edittextname)
			.setPositiveButton("确定",new DialogInterface.OnClickListener() {  
	            public void onClick(DialogInterface dialog, int which) {	            	
	            	String username = edittextname.getText().toString().trim();
	            	nicknameview.setText(username);
	            	editNewUserInfo.setNickName(username);
	        		Log.i(TAG, "edit username: "+username);
	            }
			})
			.setNegativeButton("取消", null)
			.show();
	}
	/**
	 * edit Email
	 * @param view
	 */
	public void editEmail()
	{
		final EditText editemail = new EditText(this);
		editemail.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
		new AlertDialog.Builder(this)
			.setTitle("请输入邮箱地址")
			.setView(editemail)
			.setPositiveButton("确定",new DialogInterface.OnClickListener() {  
	            public void onClick(DialogInterface dialog, int which) {	            	
	            	String email = editemail.getText().toString().trim();
	            	Log.e(TAG, "return "+ email.contains("@"));
	        		if(false == email.contains("@"))
	        		{
	        			Log.e(TAG, "invalide email"+email);
	                	Toast.makeText(EditUserInfo.this,"请输入正确的邮箱地址",Toast.LENGTH_LONG).show();
	                	return;
	        		}
	            	emailview.setText(email);
	            	editNewUserInfo.setEmail(email);
	            }
			})
			.setNegativeButton("取消", null)
			.show();
	}
	
	/**
	 * edit sex
	 * @param view
	 */
	public void editSex()
	{		
		final String[] sexname = new String[]{"女","男"} ;
		final String[] sexstring = new String[]{"f","m"} ;

		final int[] index=new int[]{0,0};
		new AlertDialog.Builder(this)
			.setTitle("请选择性别")
			.setSingleChoiceItems(sexname,0,
				      new DialogInterface.OnClickListener() {
			       public void onClick(DialogInterface dialog,
			         int which) {
			    	   if(0 == which)
			    	   {
			    		   index[0] = 0; //female
			    	   }else
			    	   {
			    		   index[0] = 1; //male
			    	   }
			    }
			      })
			    .setPositiveButton("确定", new DialogInterface.OnClickListener() {  
			    	public void onClick(DialogInterface dialog, int which) {	            	
			    			sexview.setText(sexname[index[0]]);
			            	editNewUserInfo.setGender(sexstring[index[0]]);
			            }
					})
			      .setNegativeButton("取消", null)
			.show();
	}
	/**
	 * edit city
	 * @param view
	 */
	public void editCity(View view)
	{		
		final String[] city = new String[]{"北京","广州","南京","上海"};
		final int[] index=new int[]{0,0};

		new AlertDialog.Builder(this)
			.setTitle("请选择您常驻城市")
			.setSingleChoiceItems(city,0,
				      new DialogInterface.OnClickListener() {
			       public void onClick(DialogInterface dialog,
			         int which) {
			    	   index[0]=which;
			    }
			      })
			.setPositiveButton("确定", new DialogInterface.OnClickListener() {  
			    	public void onClick(DialogInterface dialog, int which) {	            	
				    	   cityview.setText(city[index[0]]);
			    		   editNewUserInfo.setCityCode(index[0]);
			            }
					})
			.show();
	}
	/**
	 * edit AGE
	 * @param view
	 */
	public void editBirthDay()
	{		
		final Calendar c = Calendar.getInstance();
		
		// 直接创建一个DatePickerDialog对话框实例，并将它显示出来
		new DatePickerDialog(this,
			// 绑定监听器
			new DatePickerDialog.OnDateSetListener()
			{
				@Override
				public void onDateSet(DatePicker dp, int year,
					int month, int dayOfMonth)
				{
					String birthday = year+"-"+(month+1)+"-"+dayOfMonth;
					Date date = null;
					try {
						date = new SimpleDateFormat("yyyy-MM-dd").parse(birthday);
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
	                	Toast.makeText(EditUserInfo.this,"时间获取失败，请重试",Toast.LENGTH_LONG).show();
						return;
					}

					editNewUserInfo.setBirthDay(date);
					birthdayview.setText(""+year+"-"+(month+1)+"-"+dayOfMonth);					

				}
			}
		//设置初始日期
		, c.get(Calendar.YEAR)
		, c.get(Calendar.MONTH)
		, c.get(Calendar.DAY_OF_MONTH)).show();
	}
	/**
	 * edit age
	 * @param view
	 */
	public void editAge()
	{
		final EditText editage = new EditText(this);
		editage.setInputType(InputType.TYPE_CLASS_NUMBER);
		new AlertDialog.Builder(this)
			.setTitle("请输入年龄")
			.setView(editage)
			.setPositiveButton("确定",new DialogInterface.OnClickListener() {  
	            public void onClick(DialogInterface dialog, int which) {
	            	String age = editage.getText().toString().trim();
	        		//年龄为不能超过3位的数字,且高位不能为0
	        		if(age.length() > 3 ) 
	        		{
	                	Toast.makeText(EditUserInfo.this,"年龄不能超过3位数字",Toast.LENGTH_LONG).show();
	                	return;
	        		}
	        		if( !age.matches("[1-9][0-9]*"))
	        		{
	                	Toast.makeText(EditUserInfo.this,"年龄最高位不能为0",Toast.LENGTH_LONG).show();
	                	return;
	        		}
	            	ageview.setText(age);
	            	int iAge = Integer.parseInt(age);
	            	editNewUserInfo.setAge(iAge);
	            }
			})
			.setNegativeButton("取消", null)
			.show();
	}
	/**
	 * set headimage
	 * @param view
	 */
	public void editImageHead()
	{
		Log.i(TAG, "select head image");		
		AlertDialog.Builder builder=new AlertDialog.Builder(this);
		tmpHeadImage = FileUtil.createHeadImage("tmp"+String.valueOf(BaseUtil.getSelfUserInfo().getUserId())).getPath();
		builder.setTitle("设置头像").setItems(new String[]{"拍照","相册"}, new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(which==0){
					Intent it = new Intent("android.media.action.IMAGE_CAPTURE");
					File file=FileUtil.createHeadImage("tmp"+String.valueOf(BaseUtil.getSelfUserInfo().getUserId()));
					fileUri=Uri.fromFile(file);
					Log.i(TAG, "create file: "+file+"fileUri: "+fileUri);
					//此设定使图像保存到制定位置
					it.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
					startActivityForResult(it, PHOTO_FROM_CAMERA);
				}
				else{
					//跳转到图片浏览器的应用，选取要发送的图片
				Intent i = new Intent();
				i.setType("image/*");
				i.putExtra("return-data", true);
				i.setAction(Intent.ACTION_GET_CONTENT);
				startActivityForResult(i, PHOTO_FROM_FILE);
				}
			}
		}).create().show();
	}
	/**
	 * zoom photo
	 * @param uri
	 */
	private void startPhotoZoom(Uri uri){
		  Intent intent = new Intent("com.android.camera.action.CROP");  
          intent.setDataAndType(uri, "image/*");  
          intent.putExtra("crop", "true");
          intent.putExtra("aspectX", 1);  
          intent.putExtra("aspectY", 1);  
          // size of X and Y ,indicate the size of image
          intent.putExtra("outputX", 600);  
          intent.putExtra("outputY", 600); 
          intent.putExtra("noFaceDetection", true);  
          intent.putExtra("return-data", true);
          startActivityForResult(intent, PHOTO_FROM_CROP);  
	}
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{//可在此添加图片截取，编辑
		Log.i(TAG, "resultCode="+resultCode+" , requestCode="+requestCode);
		if(0 == requestCode)
		{
			return;
		}
		// 拍照 显示图片页面  
	    if (requestCode == PHOTO_FROM_CAMERA) {  
	       startPhotoZoom(fileUri);  
	    }  
	  
	    // 读取相册缩放图片 显示图片页面  
	    if (requestCode == PHOTO_FROM_FILE) {  
			if(data == null)
			{
				return;
			}
			fileUri=data.getData();
			startPhotoZoom(fileUri);  
	    }  
		
	    if (requestCode == PHOTO_FROM_CROP) {  
			if(data == null)
			{
				return;
			}
	        Bundle extras = data.getExtras();
			ImageView  headimage = (ImageView)findViewById(R.id.edit_image_head);

	        if (extras != null) {	        	
	        	//normally, comes here
	          Bitmap photo = extras.getParcelable("data");
	          // create tmp file again, no matter From_camera did it before
				headimage.setImageBitmap(photo);

			    File imageFile = new File(tmpHeadImage);
					 OutputStream os;
					  try {
					    os = new FileOutputStream(imageFile);
					    photo.compress(Bitmap.CompressFormat.JPEG, 100, os);
					    os.flush();
					    os.close();
					  } catch (Exception e) {
					    Log.e(getClass().getSimpleName(), "Error writing bitmap", e);
					  }
				bu.ftpImage(tmpHeadImage);
	
	        }else{
	        	//otherwise ,here
	        	System.out.println("no idea why come here");
	        }

	    }
		/*
		ImageView  headimage = (ImageView)findViewById(R.id.edit_image_head);
		if(requestCode==Activity.DEFAULT_KEYS_DIALER){
			//写sd卡的任务已经由MediaStore完成
		//	File file = new File(fileUri.getPath());
		//	file.renameTo(new File(fileUri.getPath().replaceAll("tmp",String.valueOf(BaseUtil.getSelfUserInfo().getUserId()) )));
		//	tmpHeadImage = fileUri.getPath().replaceAll("tmp",String.valueOf(BaseUtil.getSelfUserInfo().getUserId()) );
			tmpHeadImage = fileUri.getPath();
			//bitmap显示到view
			Bitmap bitmap=BitmapFactory.decodeFile(tmpHeadImage);
			if(bitmap!=null){
				BaseUtil.innerComm.webappclient.ftpImage(tmpHeadImage);
				headimage.setImageBitmap(bitmap);
			//	editNewUserInfo.setImageHead(CHANGE_HEAD_IMAGE);
			}

		}
		else if(requestCode==Activity.DEFAULT_KEYS_SHORTCUT){  
			if(data == null)
			{
				return;
			}
			Uri uri=data.getData();
			File file=FileUtil.createHeadImage("tmp"+String.valueOf(BaseUtil.getSelfUserInfo().getUserId()));
			tmpHeadImage=file.getAbsolutePath();

			boolean result=FileUtil.writeFile(getContentResolver(), file, uri);
			//bitmap显示到view
			Bitmap bitmap=BitmapFactory.decodeFile(file.getAbsolutePath());
		    if(bitmap!=null){
				BaseUtil.innerComm.webappclient.ftpImage(tmpHeadImage);
				headimage.setImageBitmap(bitmap);
			//	editNewUserInfo.setImageHead(CHANGE_HEAD_IMAGE);

			}
			Log.i(TAG, "图片浏览获得图片写入本地SD卡："+result);

		}*/

	}

	/**
	 * edit confirm
	 * @param view
	 */
	public void editConfirm(View view)
	{
		cpd = new CircleProgressDiaglog();
		cpd.CreateProgressDiaglog(this,null);
		BaseUtil.innerComm.webappclient.sendEditUserInfoToSvr(editNewUserInfo);
	}
	/**
	 * edit cancel
	 * @param view
	 */
	public void editCancel(View view)
	{
		
        this.finishSelf();
	}
	@Override
    public void onBackPressed() {
        this.finishSelf();

    }
	private void getUserInfo(){
		BaseUtil.innerComm.webappclient.getUserInfoByUserName(BaseUtil.getSelfUserInfo().getEmail());
	}
	@Override
	public void processMessage(Message msg) {
		// TODO Auto-generated method stub
		if(null != cpd) cpd.CancleProgressDialog();
		switch(msg.what){
		case MessageId.UPDATE_USERINFO_SUCCESS_UI:
		{
			//更新成功，更新图像，删除老图像，更名新图像
	        String newPath="";
	        if(tmpHeadImage!=null && tmpHeadImage.length()>0){// 有头像,将原有的头像图片改名字
	           // newPath=FileUtil.HEAD_PATH+"/"+editNewUserInfo.getUserId()+".jpg";
	            File file=new File(tmpHeadImage);
	            file.renameTo(new File(tmpHeadImage.replaceAll("tmp","")));
	            // have to clear here, because to same file, if mainlistadpater will enable cache
	            // EditUserInfo disable cache here doesn't work,use cache file which is store by mainlistadpater
	            // so if owner's cache is updated, clear cache make it work
	            ImageLoader.getInstance().clearDiskCache();
	            ImageLoader.getInstance().clearMemoryCache();
            }
	        else
	        {
	        	Log.e(TAG, "update userinfo succ ,but no input headfile,so user default headimage");
	        }
	        BaseUtil.setSelfUserInfo((User)msg.obj);
	        bu.toastShow(this,"更新成功");
	        this.finishSelf();
	        break;
		}
		case MessageId.UPDATE_USERINFO_FAIL_UI:
		{
        	Log.e(TAG, "update userinfo fail ");
        	bu.toastShow(this,"更新个人信息失败,请重试");
	        if(tmpHeadImage!=null && tmpHeadImage.length()>0){// 有头像,将原有的头像图片改名字
		           // newPath=FileUtil.HEAD_PATH+"/"+editNewUserInfo.getUserId()+".jpg";
		            File file=new File(tmpHeadImage);
		            file.delete();
	            }
        	break;
		}
		case MessageId.GET_USER_BY_NAME_SUCCESS_UI:
		{
			BaseUtil.setSelfUserInfo((User)msg.obj);
			initUI();
			break;
		}
		case MessageId.GET_USER_BY_NAME_FAIL_UI:
		{
			//Let it go, use old dates
			Log.e(TAG, "get user info by name fail");

			break;
		}
		/*
		case MessageId.CONNECT_SERVER_ERROR:
		{
			Log.e(TAG, "connect server error");
			toastShow(this,"网络连接失败，请等待连接恢复后再试");
			break;
		}*/
		default:
		{
			Log.e(TAG, "recv unkonw msg: "+msg.what);
			break;
		}
		}
	}
	private void finishSelf()
	{
	    //销毁Activity栈中的本Activity
        if(tmpHeadImage!=null && tmpHeadImage.length()>0){// 有头像,将原有的头像图片改名字
	           // newPath=FileUtil.HEAD_PATH+"/"+editNewUserInfo.getUserId()+".jpg";
	            File file=new File(tmpHeadImage);
	            file.delete();
         }


		BaseUtil.innerComm.delCurrentActivity(this);
		finish();
	}

}
