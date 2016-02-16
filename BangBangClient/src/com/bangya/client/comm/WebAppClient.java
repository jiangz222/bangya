package com.bangya.client.comm;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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


import org.apache.http.HttpStatus;

import org.json.JSONArray;
import org.json.JSONObject;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import com.android.volley.toolbox.Volley;




import android.content.Context;
import android.os.Message;
import android.util.Log;

import com.bangya.client.BBUI.BaseUtil;
import com.bangya.client.BBUI.NewBB;
import com.bangya.client.Util.Constants;
import com.bangya.client.Util.MessageId;
import com.bangya.client.model.Job;
import com.bangya.client.model.JobDTO;
import com.bangya.client.model.JobStatus;
import com.bangya.client.model.Messages;
import com.bangya.client.model.MessagesDTO;
import com.bangya.client.model.ProfileDTO;
import com.bangya.client.model.RewardType;
import com.bangya.client.model.SystemDTO;
import com.bangya.client.model.User;
import com.bangya.client.model.UserType;
import com.bangya.client.widget.MultipartRequest;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;



public class WebAppClient {
    public static RequestQueue mRequestQueue;
    private String TAG = "WebAppClient";
  // public static String serverIP = "192.168.2.105";
    public static String serverIP = "115.29.246.145";

    public static final String BASE_URI = "http://"+serverIP+":8080/bangbang";

    public static String MY_AUTH = "Wa6SeqOuU8Oz1q1KKHbqUj4lIrwMhiNrD1Z6vTKB8YZbibNTFztDjG8s8RqLg+beiWyxPQvtmUcNd1CDu0EheTpaMz8r8EL9Ly17hN8nGYWgpXw0s/lRPqDJFNgx6NxB7ee9HeIVKFmAitivaosARLruDU5nQ/uASH4q0j9Ak3Q=";

	public void init(Context context) {
        System.out.println("HttpGet ");

        mRequestQueue = Volley.newRequestQueue(context);
	}
	/**
	 * 
	 * @param nickname
	 * @param password
	 * @param email
	 * @param userType
	 * @param externalId
	 */
	public void register(String nickname,String password,String email,UserType userType,String externalId,String gender)
	{
		Log.e(TAG, "register  with: "+email+" "+password);

		String URItmp = BASE_URI+"/signup"; 
		URI uri;
		try {
			uri = new URI("UTF-8");
		} catch (URISyntaxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}  
       try {
			uri = new URIBuilder()
			.setPath(URItmp)
			.setParameter("nickname", nickname)
			.setParameter("password", password)
			.setParameter("email", email)
			.setParameter("usertype", String.valueOf(userType.getValue()))
			.setParameter("externalId", externalId)
			.setParameter("gender", gender)
			.build();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}

       JsonObjectRequest myReq = new JsonObjectRequest(Method.POST, 
        		uri.toString(),
        		null,
                RegisterSuccessListener(),
                RegisterErrorListener()
                );
       myReq.setRetryPolicy(new DefaultRetryPolicy(10000, 
               0,// not retry //DefaultRetryPolicy.DEFAULT_MAX_RETRIES, 
               DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        	mRequestQueue.add(myReq);
	}
    private Response.Listener<JSONObject> RegisterSuccessListener() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
					Message msg = new Message();

            		String jstring = response.toString();
            		ObjectMapper objectMapper =new ObjectMapper();
            		try {
						ProfileDTO myProfileDTO = objectMapper.readValue(jstring, ProfileDTO.class);
						User myUserInfo = myProfileDTO.ConvertToDBObject();
						msg.obj = myUserInfo;
						msg.what = MessageId.REGISTER_SUCCESS_UI;
						InnerCommAdapter.sendMessage(msg);
						return;
					} catch (JsonParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (JsonMappingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					msg.what = MessageId.REGISTER_FAIL_UI;
					InnerCommAdapter.sendMessage(msg);

            }
        };
    }
    private Response.ErrorListener RegisterErrorListener() {
        return new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError arg0) {
				// TODO Auto-generated method stub
				Message msg = new Message();
				if(null == arg0.networkResponse)
				{// connect to server error
					System.out.println("connection error:"+arg0);
					InnerCommAdapter.sendEmptyMessage(MessageId.CONNECT_SERVER_ERROR);
					return;
				}
				if(HttpStatus.SC_CONFLICT == arg0.networkResponse.statusCode)
				{//email conflict
					msg.what = MessageId.REGISTER_FAIL_EMAIL_CONFLICT_UI;

				}else{
					msg.what = MessageId.REGISTER_FAIL_UI;
				}
				InnerCommAdapter.sendMessage(msg);
			}
        };
}
    /**
     *  log on to server
     * @param userName
     * @param passWord
     */
	public void Logon(final String username,final String password)
	{

		Log.e(TAG, "log on with: "+username+" "+password);
		String URItmp = BASE_URI+"/logon"; 
		URI uri;
		try {
			uri = new URI("UTF-8");
		} catch (URISyntaxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}  
       try {
			uri = new URIBuilder()
			.setPath(URItmp)
			.setParameter("username", username)
			.setParameter("password", password)
			.build();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		StringRequest myReq = new StringRequest(Method.POST, 
				uri.toString(),
                logonSuccessListener(),
                logonErrorListener()
                );
        mRequestQueue.add(myReq);
	}
    private Response.Listener<String> logonSuccessListener() {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
				System.out.println("connection succesful MY_AUTH:"+response);

				MY_AUTH = response;
				InnerCommAdapter.sendEmptyMessage(MessageId.LOGIN_SUCCESS_UI);
				return;
            }
        };
    }
    private Response.ErrorListener logonErrorListener() {
        return new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError arg0) {
				// TODO Auto-generated method stub
				if(null == arg0.networkResponse)
				{// connect to server error
					System.out.println("connection error:"+arg0);
					InnerCommAdapter.sendEmptyMessage(MessageId.CONNECT_SERVER_ERROR);
					return;
				}
				if(HttpStatus.SC_BAD_REQUEST == arg0.networkResponse.statusCode)
				{
					System.out.println("result err"+arg0.networkResponse.statusCode);
					InnerCommAdapter.sendEmptyMessage(MessageId.LOGIN_FAIL_UI);
					return;
				}

			}
        };
	}
    /**
     * 
     * @param userName
     */
	public void getUserInfoByUserName(String userName)
	{
		String URItmp = BASE_URI+"/user/getuserbyname"; 
		URI uri = null;
		try {
			uri = new URI("UTF-8");
		} catch (URISyntaxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}  
		
		try {
			uri = new URIBuilder()
			.setPath(URItmp)
			.setParameter("userName", userName)
			.build();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JsonObjectRequest myReq = new JsonObjectRequest(Method.GET, 
	        		uri.toString(),
	        		null,
	                getUserByUserNameSuccessListener(),
	                getUserByUserNamerrorListener())
		{     
	        @Override
	        public Map<String, String> getHeaders() throws AuthFailureError { 
	                Map<String, String>  params = new HashMap<String, String>();  
	                params.put("X-bangbang-Auth", MY_AUTH);  
	                return params;  
	        }

	    };
	    
		mRequestQueue.add(myReq);
	}
    private Response.Listener<JSONObject> getUserByUserNameSuccessListener() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
					Message msg = new Message();
            		String jstring = null;
					try {
						jstring = new String(response.toString().getBytes(),"UTF-8");

					} catch (UnsupportedEncodingException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
            		ObjectMapper objectMapper =new ObjectMapper();
            		try {
						ProfileDTO myProfileDTO = objectMapper.readValue(jstring, ProfileDTO.class);
						User myUserInfo = myProfileDTO.ConvertToDBObject();
						msg.obj = myUserInfo;
						msg.what = MessageId.GET_USER_BY_NAME_SUCCESS_UI;
						InnerCommAdapter.sendMessage(msg);        	
						System.out.println("get user by email success");

						return;
					} catch (JsonParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (JsonMappingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					msg.what = MessageId.GET_USER_BY_NAME_FAIL_UI;
					InnerCommAdapter.sendMessage(msg);

            }
        };
    }
    private Response.ErrorListener getUserByUserNamerrorListener() {
        return new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError arg0) {
				// TODO Auto-generated method stub
				if(null == arg0.networkResponse)
				{// connect to server error
					System.out.println("connection error:"+arg0);
					InnerCommAdapter.sendEmptyMessage(MessageId.CONNECT_SERVER_ERROR);
					return;
				}
				Message msg = new Message();
				msg.what = MessageId.GET_USER_BY_NAME_FAIL_UI;
				InnerCommAdapter.sendMessage(msg);
			}
        };
}
	public void sendEditUserInfoToSvr(User user)
	{
		String URItmp = BASE_URI+"/user/update"; 
		URI uri = null;
		try {
			uri = new URI("UTF-8");
		} catch (URISyntaxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}  
		
		try {
			uri = new URIBuilder()
			.setPath(URItmp)
			.setParameter("uid", String.valueOf(user.getUserId()))
			.setParameter("nickName", user.getNickName())
			.setParameter("email", user.getEmail())
			.setParameter("gender", user.getGender())
			.setParameter("city", String.valueOf(user.getCityCode()))
			.setParameter("birthDay", User.getBirthDayAsStirng(user.getBirthDay()))
			.setParameter("age",  String.valueOf(user.getAge()))
			.setParameter("image",  user.getImageHead())
			.build();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JsonObjectRequest myReq = new JsonObjectRequest(Method.PUT, 
        		uri.toString(),
        		null,
                editUserSuccessListener(),
                eidtUsererrorListener())
	{     
        @Override
        public Map<String, String> getHeaders() throws AuthFailureError { 
                Map<String, String>  params = new HashMap<String, String>();  
                params.put("X-bangbang-Auth", MY_AUTH);  
                return params;  
        }
    };
                
	mRequestQueue.add(myReq);
		return;
	}
    private Response.Listener<JSONObject> editUserSuccessListener() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
					Message msg = new Message();

            		String jstring = response.toString();
            		ObjectMapper objectMapper =new ObjectMapper();
            		try {
						ProfileDTO myProfileDTO = objectMapper.readValue(jstring, ProfileDTO.class);
						User myUserInfo = myProfileDTO.ConvertToDBObject();
						msg.obj = myUserInfo;
						msg.what = MessageId.UPDATE_USERINFO_SUCCESS_UI;
						InnerCommAdapter.sendMessage(msg);
						return;
					} catch (JsonParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (JsonMappingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					msg.what = MessageId.UPDATE_USERINFO_FAIL_UI;
					InnerCommAdapter.sendMessage(msg);

            }
        };
    }
    private Response.ErrorListener eidtUsererrorListener() {
        return new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError arg0) {
				// TODO Auto-generated method stub
				if(null == arg0.networkResponse)
				{// connect to server error
					System.out.println("connection error:"+arg0);
					InnerCommAdapter.sendEmptyMessage(MessageId.CONNECT_SERVER_ERROR);
					return;
				}
				System.out.println("result err"+arg0.networkResponse.statusCode);
				InnerCommAdapter.sendEmptyMessage(MessageId.UPDATE_USERINFO_FAIL_UI);
			}
        };
}
    

    public void ReqPublishedBB(int reqJobNumbers,JobStatus jobstauts,int uid)
    {
		String URItmp = BASE_URI+"/job/getPulishedJob"; 
		URI uri = null;
		try {
			uri = new URI("UTF-8");
		} catch (URISyntaxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}  
		
		try {
			uri = new URIBuilder()
			.setPath(URItmp)
			.setParameter("owneruid", String.valueOf(uid))
			.setParameter("reqjobstatus", String.valueOf(jobstauts.getValue()))
			.setParameter("reqpage",String.valueOf(reqJobNumbers))
			.build();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//the default method of JsonArrayRequest is GET
		//if need other method, change source code may be the only way --!
		JsonArrayRequest myReq = new JsonArrayRequest( 
	        		uri.toString(),
	        		reqPublishedJobSuccessListener(),
	                reqPublishedJObErrorListener())
		{     
	        @Override
	        public Map<String, String> getHeaders() throws AuthFailureError { 
	                Map<String, String>  params = new HashMap<String, String>();  
	                params.put("X-bangbang-Auth", MY_AUTH);  
	                return params;  
	        }
	    };
	                
		mRequestQueue.add(myReq);
    }
    
    private Response.Listener<JSONArray> reqPublishedJobSuccessListener() {
        return new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
					Message msg = new Message();
            		String jstring = response.toString();
            		ObjectMapper objectMapper =new ObjectMapper();

            		try {
            			msg.obj = objectMapper.readValue(jstring, new TypeReference<ArrayList<JobDTO>>(){});
						msg.what = MessageId.REQUEST_PUBLISHED_SUCCESS_UI;
						InnerCommAdapter.sendMessage(msg);
						return;
					} catch (JsonParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (JsonMappingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					msg.what = MessageId.REQUEST_PUBLISHED_FAIL_UI;
					InnerCommAdapter.sendMessage(msg);

            }
        };
    }
    private Response.ErrorListener reqPublishedJObErrorListener() {
        return new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError arg0) {
				// TODO Auto-generated method stub
				if(null == arg0.networkResponse)
				{// connect to server error
					System.out.println("connection error:"+arg0);
					InnerCommAdapter.sendEmptyMessage(MessageId.CONNECT_SERVER_ERROR);
					return;
				}
				System.out.println("result err"+arg0.networkResponse.statusCode);
				InnerCommAdapter.sendEmptyMessage(MessageId.REQUEST_PUBLISHED_FAIL_UI);
			}
        };
}
    public void ReqPickedBB(int reqJobNumbers,JobStatus jobstauts,int uid)
    {
		String URItmp = BASE_URI+"/job/getPickedJob"; 
		URI uri = null;
		try {
			uri = new URI("UTF-8");
		} catch (URISyntaxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}  
		
		try {
			uri = new URIBuilder()
			.setPath(URItmp)
			.setParameter("pickeduid", String.valueOf(uid))
			.setParameter("reqjobstatus", String.valueOf(jobstauts.getValue()))
			.setParameter("reqpage",String.valueOf(reqJobNumbers))
			.build();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//the default method of JsonArrayRequest is GET
		//if need other method, change source code may be the only way --!
		JsonArrayRequest myReq = new JsonArrayRequest( 
	        		uri.toString(),
	        		reqPickedJobSuccessListener(),
	                reqPickedJObErrorListener())
		{     
	        @Override
	        public Map<String, String> getHeaders() throws AuthFailureError { 
	                Map<String, String>  params = new HashMap<String, String>();  
	                params.put("X-bangbang-Auth", MY_AUTH);  
	                return params;  
	        }
	    };
	                
		mRequestQueue.add(myReq);
    }
    
    private Response.Listener<JSONArray> reqPickedJobSuccessListener() {
        return new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
					Message msg = new Message();
            		String jstring = response.toString();
            		ObjectMapper objectMapper =new ObjectMapper();

            		try {
            			msg.obj = objectMapper.readValue(jstring, new TypeReference<ArrayList<JobDTO>>(){});
						msg.what = MessageId.REQUEST_ACCEPTED_SUCCESS_UI;
						InnerCommAdapter.sendMessage(msg);
						return;
					} catch (JsonParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (JsonMappingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					msg.what = MessageId.REQUEST_ACCEPTED_FAIL_UI;
					InnerCommAdapter.sendMessage(msg);

            }
        };
    }
    private Response.ErrorListener reqPickedJObErrorListener() {
        return new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError arg0) {
				// TODO Auto-generated method stub
				if(null == arg0.networkResponse)
				{// connect to server error
					System.out.println("connection error:"+arg0);
					InnerCommAdapter.sendEmptyMessage(MessageId.CONNECT_SERVER_ERROR);
					return;
				}
				System.out.println("result err"+arg0.networkResponse.statusCode);
				InnerCommAdapter.sendEmptyMessage(MessageId.REQUEST_ACCEPTED_FAIL_UI);
			}
        };
}
    

    /**
     * 
     * @param jobid
     * @param points --0 for not exist
     * @param pointDirection  --0 for not exist
     * @param pickerUid valid when published->picked
     * @param fromJobStatus
     * @param toJobStatus
     */
   public void UpdateJobStatusToSvr(int jobid, int points,int pointDirection,int pickerUid, JobStatus fromJobStatus,JobStatus toJobStatus)
   {
		String URITMP = BASE_URI+"/job/updateStatus"; 
		URI uri = null;

		try {
			uri = new URI("UTF-8");
		} catch (URISyntaxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}  
		
		try {
			uri = new URIBuilder()
			.setPath(URITMP)
			.setParameter("jobid", String.valueOf(jobid))
			.setParameter("points", String.valueOf(points))
			.setParameter("pointDirection", String.valueOf(pointDirection))
			.setParameter("pickerUid", String.valueOf(pickerUid))
			.setParameter("fromJobStatus",String.valueOf(fromJobStatus.getValue()))
			.setParameter("toJobStatus",String.valueOf(toJobStatus.getValue()))
			.build();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		StringRequest myReq = new StringRequest(Method.POST, 
        		uri.toString(),
        		updateJobStatusSuccessListener(),
        		updateJobStatuserrorListener())
		{
	        @Override
	        public Map<String, String> getHeaders() throws AuthFailureError { 
	                Map<String, String>  params = new HashMap<String, String>();  
	                params.put("X-bangbang-Auth", MY_AUTH);  
	                return params;  
	                }
		};
       mRequestQueue.add(myReq);
   }
    
   private Response.Listener<String> updateJobStatusSuccessListener() {
       return new Response.Listener<String>() {
           @Override
           public void onResponse(String response) {
					Message msg = new Message();
					msg.what = MessageId.UPDATE_BB_STATUS_SUCCESS_UI;
					InnerCommAdapter.sendMessage(msg);
           }
       };
   }
   private Response.ErrorListener updateJobStatuserrorListener() {
       return new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError arg0) {
				// TODO Auto-generated method stub
				if(null == arg0.networkResponse)
				{// connect to server error
					System.out.println("connection error:"+arg0);
					InnerCommAdapter.sendEmptyMessage(MessageId.CONNECT_SERVER_ERROR);
					return;
				}
				System.out.println("result err"+arg0.networkResponse.statusCode);
				InnerCommAdapter.sendEmptyMessage(MessageId.UPDATE_BB_STATUS_FAIL_UI);
			}
       };
}
   public void updateHeadImage(int uid,String headImage)
   {
		String URITMP = BASE_URI+"/user/image"; 
		URI uri = null;

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
		System.out.println("uid"+uid+"image:"+headImage);
		MultipartRequest myReq = new MultipartRequest(uri.toString(), 
				updateJobStatuserrorListener(),
				updateJobStatusSuccessListener(),
       		new File(headImage),
       		String.valueOf(uid))
		{
	        @Override
	        public Map<String, String> getHeaders() throws AuthFailureError { 
	                Map<String, String>  params = new HashMap<String, String>();  
	                params.put("X-bangbang-Auth", MY_AUTH);  
	                return params;  
	                }
		};
      mRequestQueue.add(myReq);
      

   }

	public void sendSuggestion(int uid,String suggestion)
	{
		String URITMP = BASE_URI+"/user/suggestion"; 
		URI uri = null;

		try {
			uri = new URI("UTF-8");
		} catch (URISyntaxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}  
		
		try {
			uri = new URIBuilder()
			.setPath(URITMP)
			.setParameter("uid", String.valueOf(uid))
			.setParameter("suggestion", suggestion)
			.build();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		StringRequest myReq = new StringRequest(Method.POST, 
        		uri.toString(),
        		sendSuggestionsuccessListener(),
        		sendSuggestionerrorListener())
		{
	        @Override
	        public Map<String, String> getHeaders() throws AuthFailureError { 
	                Map<String, String>  params = new HashMap<String, String>();  
	                params.put("X-bangbang-Auth", MY_AUTH);  
	                return params;  
	                }
		};
       mRequestQueue.add(myReq);
	}
    private Response.Listener<String> sendSuggestionsuccessListener() {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
				System.out.println("connection succesful MY_AUTH:"+response);

				InnerCommAdapter.sendEmptyMessage(MessageId.SEND_SUGGESTION_SUCCESS_UI);
				return;
            }
        };
    }
    private Response.ErrorListener sendSuggestionerrorListener() {
        return new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError arg0) {
				// TODO Auto-generated method stub
				if(null == arg0.networkResponse)
				{// connect to server error
					System.out.println("connection error:"+arg0);
					InnerCommAdapter.sendEmptyMessage(MessageId.CONNECT_SERVER_ERROR);
					return;
				}
				if(HttpStatus.SC_BAD_REQUEST == arg0.networkResponse.statusCode)
				{
					System.out.println("result err"+arg0.networkResponse.statusCode);
					InnerCommAdapter.sendEmptyMessage(MessageId.SEND_SUGGESTION_FAIL_UI);
					return;
				}

			}
        };
        

	}
	public void reqMessages(int jobId)
	{
		String URItmp = BASE_URI+"/messages/byjobid"; 
		URI uri = null;
		try {
			uri = new URIBuilder()
			.setPath(URItmp)
			.setParameter("jobid", String.valueOf(jobId))

			.build();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//the default method of JsonArrayRequest is GET
		//if need other method, change source code may be the only way --!
		JsonArrayRequest myReq = new JsonArrayRequest( 
	        		uri.toString(),
	        		JsonArraySuccessListener(),
	        		JsonArrayErrorListener())
		{     
	        @Override
	        public Map<String, String> getHeaders() throws AuthFailureError { 
	                Map<String, String>  params = new HashMap<String, String>();  
	                params.put("X-bangbang-Auth", MY_AUTH);  
	                return params;  
	        }
	    };
	                
		mRequestQueue.add(myReq);
    }
    
    private Response.Listener<JSONArray> JsonArraySuccessListener() {
        return new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
					Message msg = new Message();
            		String jstring = response.toString();
            		ObjectMapper objectMapper =new ObjectMapper();

            		try {
            			msg.obj =  objectMapper.readValue(jstring, new TypeReference<List<MessagesDTO>>(){});
						msg.what = MessageId.GET_MSGS_SUCCESS_UI;
						InnerCommAdapter.sendMessage(msg);
						return;
					} catch (JsonParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (JsonMappingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					msg.what = MessageId.GET_MSGS_FAIL_UI;
					InnerCommAdapter.sendMessage(msg);

            }
        };
    }
    private Response.ErrorListener JsonArrayErrorListener() {
        return new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError arg0) {
				// TODO Auto-generated method stub
				if(null == arg0.networkResponse)
				{// connect to server error
					System.out.println("connection error:"+arg0);
					InnerCommAdapter.sendEmptyMessage(MessageId.CONNECT_SERVER_ERROR);
					return;
				}
				System.out.println("result err"+arg0.networkResponse.statusCode);
				InnerCommAdapter.sendEmptyMessage(MessageId.GET_MSGS_FAIL_UI);
			}
        };
}
public void sendMessage(int jobId,int ownerUid,int type,String msgContent)
{
	String URITMP = BASE_URI+"/messages/insertOneMsg"; 
	URI uri = null;

	try {
		uri = new URIBuilder()
		.setPath(URITMP)
		.setParameter("jobid", String.valueOf(jobId))
		.setParameter("owneruid", String.valueOf(ownerUid))
		.setParameter("type", String.valueOf(type))
		.setParameter("msgContent", msgContent)
		.build();
	} catch (URISyntaxException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	JsonObjectRequest myReq = new JsonObjectRequest(Method.POST, 
    		uri.toString(),
    		null,
            JsonObjectSuccessListener(),
            JsonObjectErrorListener())
	{
        @Override
        public Map<String, String> getHeaders() throws AuthFailureError { 
                Map<String, String>  params = new HashMap<String, String>();  
                params.put("X-bangbang-Auth", MY_AUTH);  
                return params;  
                }
	};
   mRequestQueue.add(myReq);
}
private Response.Listener<JSONObject> JsonObjectSuccessListener() {
    return new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
			Message msg = new Message();
    		String jstring = response.toString();
    		ObjectMapper objectMapper =new ObjectMapper();
    		try {
    			MessagesDTO msgDTO = objectMapper.readValue(jstring, MessagesDTO.class);
				msg.obj = msgDTO;
				msg.what = MessageId.SEND_MSG_SUCCESS_UI;
				InnerCommAdapter.sendMessage(msg);
				return;
			} catch (JsonParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JsonMappingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			msg.what = MessageId.SEND_MSG_FAIL_UI;
			InnerCommAdapter.sendMessage(msg);
			return;
        }
    };
}
private Response.ErrorListener JsonObjectErrorListener() {
    return new Response.ErrorListener() {

		@Override
		public void onErrorResponse(VolleyError arg0) {
			// TODO Auto-generated method stub
			if(null == arg0.networkResponse)
			{// connect to server error
				System.out.println("connection error:"+arg0);
				InnerCommAdapter.sendEmptyMessage(MessageId.CONNECT_SERVER_ERROR);
				return;
			}
			if(HttpStatus.SC_BAD_REQUEST == arg0.networkResponse.statusCode)
			{
				System.out.println("result err"+arg0.networkResponse.statusCode);
				InnerCommAdapter.sendEmptyMessage(MessageId.SEND_MSG_FAIL_UI);
				return;
			}

		}
    };
}
public void updateClientId(int uid,String clientId)
{
	Log.i(TAG, "updateClientId:"+uid+" "+clientId);
	if(uid == 0 || clientId == null)
	{
		InnerCommAdapter.sendEmptyMessage(MessageId.UPDATE_CLIENT_ID_FAIL);
		return;
	}
	String URItmp = BASE_URI+"/updateclientid"; 
	URI uri;
   try {
		uri = new URIBuilder()
		.setPath(URItmp)
		.setParameter("uid", String.valueOf(uid))
		.setParameter("clientid", clientId)
		.build();
	} catch (URISyntaxException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		return;
	}
	StringRequest myReq = new StringRequest(Method.POST, 
			uri.toString(),
            StringReqSuccessListener(),
           StringReqErrorListener()
            )
	{
        @Override
        public Map<String, String> getHeaders() throws AuthFailureError { 
                Map<String, String>  params = new HashMap<String, String>();  
                params.put("X-bangbang-Auth", MY_AUTH);  
                return params;  
                }
	};
    mRequestQueue.add(myReq);
}
private Response.Listener<String> StringReqSuccessListener() {
    return new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
			System.out.println("success update clietid:");
			return;
        }
    };
}
private Response.ErrorListener StringReqErrorListener() {
    return new Response.ErrorListener() {

		@Override
		public void onErrorResponse(VolleyError arg0) {
			// TODO Auto-generated method stub
			if(null == arg0.networkResponse)
			{// connect to server error
				System.out.println("connection error:"+arg0);
				InnerCommAdapter.sendEmptyMessage(MessageId.CONNECT_SERVER_ERROR);
				return;
			}
			if(HttpStatus.SC_BAD_REQUEST == arg0.networkResponse.statusCode)
			{
				System.out.println("result err"+arg0.networkResponse.statusCode);
				InnerCommAdapter.sendEmptyMessage(MessageId.UPDATE_CLIENT_ID_FAIL);
				return;
			}

		}
    };
}

public void ReqJobFromSvrByLocation(int uid,int reqJobNumbers,JobStatus jobstatus,double longitude,double latitude,int range,String gender)
{
	String URItmp = WebAppClient.BASE_URI+"/job/getJobbyLocation"; 
	URI uri = null;
	try {
		uri = new URI("UTF-8");
	} catch (URISyntaxException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}  
	
	try {
		uri = new URIBuilder()
		.setPath(URItmp)
		.setParameter("requid", String.valueOf(uid))
		.setParameter("reqjobnumbers", String.valueOf(reqJobNumbers))
		.setParameter("reqjobstatus", String.valueOf(jobstatus.getValue()))
		.setParameter("longitude",String.valueOf(longitude))
		.setParameter("latitude",String.valueOf(latitude))
		.setParameter("range",String.valueOf(range))
		.setParameter("gender",gender)
		.build();
	} catch (URISyntaxException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	//the default method of JsonArrayRequest is GET
	//if need other method, change source code may be the only way --!
	JsonArrayRequest myReq = new JsonArrayRequest( 
        		uri.toString(),
        		reqLocationJobSuccessListener(),
        		reqLocationJObErrorListener())
	{     
        @Override
        public Map<String, String> getHeaders() throws AuthFailureError { 
                Map<String, String>  params = new HashMap<String, String>();  
                params.put("X-bangbang-Auth", WebAppClient.MY_AUTH);  
                return params;  
        }
    };
	Log.i(TAG, "send find bb succ");

    WebAppClient.mRequestQueue.add(myReq);
}

private Response.Listener<JSONArray> reqLocationJobSuccessListener() {
    return new Response.Listener<JSONArray>() {
        @Override
        public void onResponse(JSONArray response) {
				Message msg = new Message();
        		String jstring = response.toString();
        		ObjectMapper objectMapper =new ObjectMapper();
        		Log.i(TAG, "recv find bb succ");
        		try {
        			msg.obj = objectMapper.readValue(jstring, new TypeReference<ArrayList<JobDTO>>(){});
					msg.what = MessageId.REQUEST_FINDBB_SUCCESS_UI;
					InnerCommAdapter.sendMessage(msg);
					return;
				} catch (JsonParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JsonMappingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				msg.what = MessageId.REQUEST_FINDBB_FAIL_UI;
				InnerCommAdapter.sendMessage(msg);

        }
    };
}
private Response.ErrorListener reqLocationJObErrorListener() {
    return new Response.ErrorListener() {

		@Override
		public void onErrorResponse(VolleyError arg0) {
			// TODO Auto-generated method stub
    		Log.i(TAG, "recv find bb fail");

			if(null == arg0.networkResponse)
			{// connect to server error
				System.out.println("connection error:"+arg0);
				InnerCommAdapter.sendEmptyMessage(MessageId.CONNECT_SERVER_ERROR);
				return;
			}
			System.out.println("result err"+arg0.networkResponse.statusCode);
			InnerCommAdapter.sendEmptyMessage(MessageId.REQUEST_FINDBB_FAIL_UI);
		}
    };
}
public void createNewBBToSvr(int ownerUid,double longitude,double latitude,String bbTitle,String bbDetail,RewardType rewardType,String rewardDetail,String dueTime)
{
	String URItmp = WebAppClient.BASE_URI+"/job/newjob"; 
	URI uri = null;
	try {
		uri = new URI("UTF-8");
	} catch (URISyntaxException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}  
	
	try {
		uri = new URIBuilder()
		.setPath(URItmp)
		.setParameter("owneruid", String.valueOf(ownerUid))
		.setParameter("title", bbTitle)
		.setParameter("description", bbDetail)
		.setParameter("duetime", dueTime)
		.setParameter("longitude",String.valueOf(longitude))
		.setParameter("latitude", String.valueOf(latitude))
		.setParameter("rewardtype", String.valueOf(rewardType.getValue()))
		.setParameter("rewarddescription",  rewardDetail)
		.build();
	} catch (URISyntaxException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	StringRequest myReq = new StringRequest(Method.POST, 
    		uri.toString(),
    		createNewJobSuccessListener(),
    		createNewJoberrorListener())
{     
    @Override
    public Map<String, String> getHeaders() throws AuthFailureError { 
            Map<String, String>  params = new HashMap<String, String>();  
            params.put("X-bangbang-Auth", WebAppClient.MY_AUTH);  
            return params;  
    }
};
WebAppClient.mRequestQueue.add(myReq);

}
private Response.Listener<String> createNewJobSuccessListener() {
    return new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
			InnerCommAdapter.sendEmptyMessage(MessageId.NEW_BB_SUCCESS_UI);

        }
    };
}
private Response.ErrorListener createNewJoberrorListener() {
    return new Response.ErrorListener() {

		@Override
		public void onErrorResponse(VolleyError arg0) {
			// TODO Auto-generated method stub
			if(null == arg0.networkResponse)
			{// connect to server error
				System.out.println("connection error:"+arg0);
				InnerCommAdapter.sendEmptyMessage(MessageId.CONNECT_SERVER_ERROR);
				return;
			}
			InnerCommAdapter.sendEmptyMessage(MessageId.NEW_BB_FAIL_UI);


		}
    };
}

public void ChangePassword(int uid,String oldPwd,String newPwd)
{
	String URItmp = BASE_URI+"/user/changePassword"; 
	URI uri = null;			
	try {
		uri = new URIBuilder()
		.setPath(URItmp)
		.setParameter("oldPassword", oldPwd)
		.setParameter("newPwd", newPwd)
		.setParameter("uid",String.valueOf(uid))
		.build();
	} catch (URISyntaxException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	StringRequest myReq = new StringRequest(Method.POST, 
        		uri.toString(),
        		ChangePasswordByIdCodeSuccessListener(),
        		ChangePasswordByIdCodeErrorListener())
        		{     
    @Override
    public Map<String, String> getHeaders() throws AuthFailureError { 
            Map<String, String>  params = new HashMap<String, String>();  
            params.put("X-bangbang-Auth", WebAppClient.MY_AUTH);  
            return params;  
    			}
        	};
    
	mRequestQueue.add(myReq);
}
private Response.Listener<String> ChangePasswordByIdCodeSuccessListener() {
    return new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
				InnerCommAdapter.sendEmptyMessage(MessageId.CHANGE_PASSWORD_SUCCESS);
        }
    };
}
private Response.ErrorListener ChangePasswordByIdCodeErrorListener() {
    return new Response.ErrorListener() {

		@Override
		public void onErrorResponse(VolleyError arg0) {
			// TODO Auto-generated method stub
			if(null == arg0.networkResponse)
			{// connect to server error
				System.out.println("connection error:"+arg0);
				InnerCommAdapter.sendEmptyMessage(MessageId.CONNECT_SERVER_ERROR);
				return;
			}
			if(HttpStatus.SC_BAD_REQUEST == arg0.networkResponse.statusCode){
				InnerCommAdapter.sendEmptyMessage(MessageId.EMAIL_NOT_EXIST);
			// email not found	
			}else
			if(HttpStatus.SC_CONFLICT == arg0.networkResponse.statusCode)
			{// old password is mistch tO DB
				InnerCommAdapter.sendEmptyMessage(MessageId.CHANGE_PASSWORD_OLD_MISMATCH);

			}else
			{// something like update db fail
				InnerCommAdapter.sendEmptyMessage(MessageId.RESET_PASSWORD_FAIL);
			}

		}
    };
}	
	public void ResetPassword(String email,String pwd,String identifyCode)
		{
			String URItmp = BASE_URI+"/user/ResetPasswordByIdCode"; 
			URI uri = null;			
			try {
				uri = new URIBuilder()
				.setPath(URItmp)
				.setParameter("email", email)
				.setParameter("pwd", pwd)
				.setParameter("identifyCode",identifyCode)
				.build();
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			StringRequest myReq = new StringRequest(Method.POST, 
		        		uri.toString(),
		        		ResetPasswordByIdCodeSuccessListener(),
		        		ResetPasswordByIdCodeErrorListener());
		    
			mRequestQueue.add(myReq);
		}
	    private Response.Listener<String> ResetPasswordByIdCodeSuccessListener() {
	        return new Response.Listener<String>() {
	            @Override
	            public void onResponse(String response) {
						InnerCommAdapter.sendEmptyMessage(MessageId.RESET_PASSWORD_SUCCESS);
	            }
	        };
	    }
	    private Response.ErrorListener ResetPasswordByIdCodeErrorListener() {
	        return new Response.ErrorListener() {

				@Override
				public void onErrorResponse(VolleyError arg0) {
					// TODO Auto-generated method stub
					if(null == arg0.networkResponse)
					{// connect to server error
						System.out.println("connection error:"+arg0);
						InnerCommAdapter.sendEmptyMessage(MessageId.CONNECT_SERVER_ERROR);
						return;
					}
					if(HttpStatus.SC_BAD_REQUEST == arg0.networkResponse.statusCode){
						InnerCommAdapter.sendEmptyMessage(MessageId.EMAIL_NOT_EXIST);

					// email not found	
					}else
					if(HttpStatus.SC_CONFLICT == arg0.networkResponse.statusCode)
					{// identify code not match
						InnerCommAdapter.sendEmptyMessage(MessageId.IDENTIFY_CODE_MISMATCH);

					}else
					{// something like update db fail
						InnerCommAdapter.sendEmptyMessage(MessageId.RESET_PASSWORD_FAIL);
					}

				}
	        };
	}	
		public void reqIdentifyCode(String email)
		{
			String URItmp = BASE_URI+"/user/reqIdentifyCode"; 
			URI uri = null;			
			try {
				uri = new URIBuilder()
				.setPath(URItmp)
				.setParameter("email", email)

				.build();
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			StringRequest myReq = new StringRequest(Method.POST, 
		        		uri.toString(),
		        		reqIdentifyCodeSuccessListener(),
		        		reqIdentifyCodeErrorListener());
		    
			mRequestQueue.add(myReq);
		}
	    private Response.Listener<String> reqIdentifyCodeSuccessListener() {
	        return new Response.Listener<String>() {
	            @Override
	            public void onResponse(String response) {
						InnerCommAdapter.sendEmptyMessage(MessageId.REQ_IDENTIFY_CODE_SUCCESS);
	            }
	        };
	    }
	    private Response.ErrorListener reqIdentifyCodeErrorListener() {
	        return new Response.ErrorListener() {

				@Override
				public void onErrorResponse(VolleyError arg0) {
					// TODO Auto-generated method stub
					if(null == arg0.networkResponse)
					{// connect to server error
						System.out.println("connection error:"+arg0);
						InnerCommAdapter.sendEmptyMessage(MessageId.CONNECT_SERVER_ERROR);
						return;
					}
					if(HttpStatus.SC_BAD_REQUEST == arg0.networkResponse.statusCode){
						InnerCommAdapter.sendEmptyMessage(MessageId.EMAIL_NOT_EXIST);
					// email not found	
					}else
					{// something like update db fail, NOT_MODIFY
						InnerCommAdapter.sendEmptyMessage(MessageId.REQ_IDENTIFY_CODE_FAIL);
					}

				}
	        };
	}	
		public void getSystemInfo()
		{
			String URItmp = BASE_URI+"/system/get";
			URI uri = null;			
			try {
				uri = new URIBuilder()
				.setPath(URItmp)
				.build();
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//the default method of JsonArrayRequest is GET

			JsonObjectRequest myReq = new JsonObjectRequest(Method.GET, 
	        		uri.toString(),
	        		null,
	        		getSystemInfoSuccessListener(),
	        		getSystemInfoErrorListener())
		{     
	        @Override
	        public Map<String, String> getHeaders() throws AuthFailureError { 
	                Map<String, String>  params = new HashMap<String, String>();  
	                params.put("X-bangbang-Auth", MY_AUTH);  
	                return params;  
	        }

	    };
		mRequestQueue.add(myReq);
	}
	  
	private Response.Listener<JSONObject> getSystemInfoSuccessListener() {
	    	    return new Response.Listener<JSONObject>() {
	    	        @Override
	    	        public void onResponse(JSONObject response) {
	    					Message msg = new Message();
	    	        		String jstring = response.toString();
	    	        		ObjectMapper objectMapper =new ObjectMapper();
	    	        		Log.i(TAG, "recv system info");
	    	        		try {
	    	        			msg.obj = objectMapper.readValue(jstring, SystemDTO.class);
	    						msg.what = MessageId.GET_SYSTEM_INFO_SUCCESS;
	    						InnerCommAdapter.sendMessage(msg);
	    						return;
	    					} catch (JsonParseException e) {
	    						// TODO Auto-generated catch block
	    						e.printStackTrace();
	    					} catch (JsonMappingException e) {
	    						// TODO Auto-generated catch block
	    						e.printStackTrace();
	    					} catch (IOException e) {
	    						// TODO Auto-generated catch block
	    						e.printStackTrace();
	    					}
	    					msg.what = MessageId.GET_SYSTEM_INFO_FAIL;
	    					InnerCommAdapter.sendMessage(msg);

	    	        }
	    	    };
	    	}
	    	private Response.ErrorListener getSystemInfoErrorListener() {
	    	    return new Response.ErrorListener() {

	    			@Override
	    			public void onErrorResponse(VolleyError arg0) {
	    				// TODO Auto-generated method stub
	    	    		Log.i(TAG, "recv get system info fail");

	    				if(null == arg0.networkResponse)
	    				{// connect to server error
	    					System.out.println("connection error:"+arg0);
	    					InnerCommAdapter.sendEmptyMessage(MessageId.CONNECT_SERVER_ERROR);
	    					return;
	    				}
	    				System.out.println("result err"+arg0.networkResponse.statusCode);
	    				InnerCommAdapter.sendEmptyMessage(MessageId.GET_SYSTEM_INFO_FAIL);
	    			}
	    	    };
	    	}	


}
