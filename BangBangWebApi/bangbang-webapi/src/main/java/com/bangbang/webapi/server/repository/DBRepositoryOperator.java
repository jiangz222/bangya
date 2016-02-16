package com.bangbang.webapi.server.repository;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;


import com.bangbang.webapi.server.model.Job;
import com.bangbang.webapi.server.model.JobDTO;
import com.bangbang.webapi.server.model.JobStatus;
import com.bangbang.webapi.server.model.MessagesDTO;
import com.bangbang.webapi.server.model.RewardType;
import com.bangbang.webapi.server.model.User;
import com.bangbang.webapi.server.model.UserType;
import com.bangbang.webapi.server.util.BYConstants;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;




public class DBRepositoryOperator {
	private static final String DRIVER = "com.mysql.jdbc.Driver";
	//DB settings
	private static final String DB_USER_NAME = "root";
	private static final String DB_NAME = "bangbangdb";
	
	private static final String CONNECTION_URL = "jdbc:mysql://localhost:3306/"+DB_NAME+"?useUnicode=true&characterEncoding=utf8";
	private static final String USER_PASSWORD = "";

//	private static final String USER_PASSWORD = "bbpwd.123$";
//	private static final String CONNECTION_URL = "jdbc:mysql://10.162.51.174:3306/"+DB_NAME+"?useUnicode=true&characterEncoding=utf8"; //inner net ip here in aliyun

//	private static boolean jdbcDriverconnected = false;
    private static HikariDataSource ds = null;
    
	public DBRepositoryOperator()
	{
/*
		if(false == jdbcDriverconnected)
		{//Connect JDBC driver
			connectJDBCDriver();
			jdbcDriverconnected = true;
		}
*/		
		if(ds == null)
		{
	        HikariConfig config = new HikariConfig();
	        config.setMaximumPoolSize(100);
	        config.setConnectionTestQuery("VALUES 1");
	        config.setDriverClassName(DRIVER);
	        config.setJdbcUrl(CONNECTION_URL);
	        config.addDataSourceProperty("user", DB_USER_NAME);
	        config.addDataSourceProperty("password", USER_PASSWORD);
	        ds = new HikariDataSource(config);
	        System.out.println("init Connection Pool successful");
	        
	   }
	}
	private Connection getConnection() throws SQLException
	{
        Connection connection = ds.getConnection();
        return connection;
	}
	
	private void closeConnection(Connection connection) throws SQLException 
	{
		connection.close();
	}
	/**
	 * Connects JDBC driver
	 */
	private void connectJDBCDriver(){
		try {
			//Connect
			Class.forName(DRIVER).newInstance();
			System.out.println("Driver started succesfully");
			System.out.println("Connected to MYSQL database");

		} catch (ClassNotFoundException e) {

			// Error message
			e.printStackTrace();
		} catch (InstantiationException e) {

			e.printStackTrace();
		} catch (IllegalAccessException e) {

			e.printStackTrace();
		}
	}
	/**
	 *  insert new db with object for any class ,and tablename
	 * @param object
	 * @param tablename
	 */
	public Object insertOnelineDB(HashMap<String,Object> map,String tablename) throws Exception{
	{
		int size = map.size();
		int iloop = 0;
		String columns = new String();
		String qustionmarks = new String();
		Object value = new Object();
		Object keyGeneratedByInsert = new Object();

		Connection connection = getConnection();

		for(Entry<String, Object> entry:map.entrySet()){    
			   System.out.println("get iterator: "+map.entrySet().iterator());
			   
				System.out.println(entry.getKey()+"--->"+entry.getValue());    
				if(iloop == size-1)
		     	{
		     		columns += entry.getKey();
		    		qustionmarks += "?";
		     	}
		     	else
		     	{
		     		columns += entry.getKey()+" , ";
					qustionmarks += "?, ";


		     	}
		     	iloop++;
			}  		
	PreparedStatement genericStatement = 
			connection.prepareStatement("INSERT INTO " + tablename + 
					" ("+ columns +" )  VALUES  ( "+ qustionmarks +" )",Statement.RETURN_GENERATED_KEYS);

	iloop =0;
	for(Entry<String, Object> entry:map.entrySet()){    
	  	
		 	System.out.println(entry.getKey()+"--->"+entry.getValue());   
		 	value = entry.getValue();
		 	genericStatement.setObject(iloop+1, value);
		 	iloop++;
	  }
		
		//Execute the prepared statement
		genericStatement.execute();
		ResultSet rs = genericStatement.getGeneratedKeys();
		while(rs.next()){
				/* so KEY MUST be 1st column in row ,and no type restricted */
				keyGeneratedByInsert= rs.getObject(1);
			}
		//Close resources
		genericStatement.close();
		closeConnection(connection);
		return keyGeneratedByInsert;

}
}
	/**
	 *  insert new db with object for any class ,and tablename
	 * @param object
	 * @param tablename
	 * replace by map paramaters
	 */
	public Object insertOnelineDB_drop(Object object,String tablename) throws Exception{
	{
		String type = new String();
		ArrayList<Field> mainValidObjectFields = new ArrayList<Field>();
		Field field = null;
		String columns = new String();
		String qustionmarks = new String();
		Object value = new Object();
		Object keyGeneratedByInsert = new Object();

		Field[] objectFields =  object.getClass().getDeclaredFields();
		Connection connection = getConnection();

		for(Field fieldtmp : objectFields){
			fieldtmp.setAccessible(true);
			type= fieldtmp.getType().getName();
			System.out.println("type:"+type+", name:"+fieldtmp.getName());
			mainValidObjectFields.add(fieldtmp);
			}
		for(int i=0;i<mainValidObjectFields.size();i++){
		field = mainValidObjectFields.get(i);
		if(i == mainValidObjectFields.size()-1){
		columns += field.getName();
		qustionmarks += "?";
		}else
		{
			columns += field.getName() + ", " ;
			qustionmarks += "?, ";

		}	
	}
	PreparedStatement genericStatement = 
			connection.prepareStatement("INSERT INTO " + tablename + 
					" ("+ columns +" )  VALUES  ( "+ qustionmarks +" )",Statement.RETURN_GENERATED_KEYS);
	//For each field add to prepared statement
	for(int i=0;i<mainValidObjectFields.size();i++){
		field = mainValidObjectFields.get(i);
		if("com.bangbang.webapi.server.model.JobStatus" == field.getType().getName())
		{
			JobStatus jb = (JobStatus)field.get(object);
			if(jb != null)
			value = jb.getValue();
		}
		else if("com.bangbang.webapi.server.model.UserType" == field.getType().getName())
		{
			UserType ut = (UserType)field.get(object);
			if(ut != null)
			value = ut.getValue();
		}
		else if("com.bangbang.webapi.server.model.RewardType" == field.getType().getName())
		{
			RewardType rt = (RewardType)field.get(object);
			if(rt != null)
			value = rt.getValue();
		}
		else if("java.util.Date" == field.getType().getName())
		{
			Date date = (Date)field.get(object);
			if(date != null)
			value = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
		}else{
			value = field.get(object);
		}
		genericStatement.setObject(i+1, value);
	}
		//Execute the prepared statement
		genericStatement.execute();
		ResultSet rs = genericStatement.getGeneratedKeys();
		while(rs.next()){
				/* so KEY MUST be 1st column in row ,and no type restricted */
				keyGeneratedByInsert= rs.getObject(1);
			}
		//Close resources
		genericStatement.close();
		closeConnection(connection);

		return keyGeneratedByInsert;

}
}
	/**
	 * 
	 * @param map
	 * @param tablename
	 * @param tableKeyName
	 * @return
	 * @throws Exception
	 */
	public boolean updateOneRowDB(HashMap<String,Object> map, String tablename,String tableKeyName,int updateRowCount)throws Exception{
	
		int size = map.size();
		int iloop=0;
		String columns = new String();
		Object keyValue = null;
		Object value = new Object();
		boolean rtn = true;
		Connection connection = getConnection();

		for(Entry<String, Object> entry:map.entrySet()){    			   
		     	if(tableKeyName != entry.getKey())
		     	{
				     System.out.println(entry.getKey()+"--->"+entry.getValue());    
		     		if(iloop == size-1)
		     		{
		     			columns += entry.getKey()+" = ?";
		     		}
		     		else
		     		{
					   columns += entry.getKey()+" = ?, ";

		     		}
		     	}
		     	if(entry.getKey() == tableKeyName)
		     	{
		     		keyValue = entry.getValue();
				   System.out.println("get key value"+entry.getValue());    

		     	}
		     	iloop++;
			}  			
	PreparedStatement genericStatement = 
		connection.prepareStatement("update " + tablename + " SET "+ columns +" WHERE "+ tableKeyName +" = "+keyValue);
	iloop =0;
	for(Entry<String, Object> entry:map.entrySet()){    
	 if(tableKeyName != entry.getKey())
	  	{
		 	value = entry.getValue();
		 	genericStatement.setObject(iloop+1, value);
		 	iloop++;
	  		}
		}  
	//Execute the prepared statement
		updateRowCount = genericStatement.executeUpdate();
		if(1 != updateRowCount)
		{
			rtn = false;
		}
		
	//Close resources
		genericStatement.close();
		closeConnection(connection);

		return rtn;
	}
	/**
	 *   update table with object for any class,any value in this table,
	 * @param object
	 * @param tablename
	 * @param tableKeyName    only support one key now
	 * @throws Exception
	 * */
	/*
	public Object updateOneLineDB(Object object,String tablename,String tableKeyName) throws Exception{
	{
		Object keyValue = null;
		String type = new String();
		ArrayList<Field> mainValidObjectFields = new ArrayList<Field>();
		String columns = new String();
		Object value = new Object();
		Field field = null;

		Field[] objectFields =  object.getClass().getDeclaredFields();
		Connection connection = DriverManager.getConnection(CONNECTION_URL+DB_NAME,DB_USER_NAME,USER_PASSWORD);
		//Set which fields are not relationships
		ArrayList<String>allowedFields = new ArrayList<String>();
		allowedFields.add("int");
		allowedFields.add("java.lang.String");
		allowedFields.add("double");
		allowedFields.add("com.bangbang.webapi.server.mpdel.JobStatus");
		allowedFields.add("com.bangbang.webapi.server.model.UserType");
		allowedFields.add("java.util.Date");

		for(Field fieldtmp : objectFields){
			fieldtmp.setAccessible(true);
			type= fieldtmp.getType().getName();
			System.out.println("type:"+type+", name:"+fieldtmp.getName());
			//If the array has the field, add to array for main insert
			if (allowedFields.contains(type) && !fieldtmp.getName().equals(tableKeyName)) {
				mainValidObjectFields.add(fieldtmp);
				System.out.println("update object in main fields:"+fieldtmp.get(object));

			}else if (fieldtmp.getName().equals(tableKeyName)) {
				//Get myId
				keyValue = fieldtmp.get(object);
				System.out.println("get key value: "+keyValue);

			}
			//
			if(type == "java.lang.String" && !(null == (String)fieldtmp.get(object))){
				// if string is null, don't update this one 
				System.out.println("String:"+fieldtmp.get(object)+", add into main fields ");
				mainValidObjectFields.add(fieldtmp);
			}else
			if(type == "int" && !fieldtmp.getName().equals(tableKeyName)&& (!fieldtmp.get(object).equals(0)) )			{// if int is invalid  and  isn't key 
				System.out.println("int and not key:"+fieldtmp.get(object)+",add into main fields ");
				mainValidObjectFields.add(fieldtmp);
			}else if((null != fieldtmp.get(object)) && (!fieldtmp.get(object).equals(0))){
				System.out.println("update object in main fields:"+fieldtmp.get(object));
				mainValidObjectFields.add(fieldtmp);
			}
			else if(tableKeyName.equals(fieldtmp.getName()))
			{
				keyValue = fieldtmp.get(object);
				System.out.println("get key value: "+keyValue);
			}
		}
		if(keyValue == null)
		{
			System.out.println("invalid order_number: "+keyValue);
			return null;
		}

		for(int i=0;i<mainValidObjectFields.size();i++){
			field = mainValidObjectFields.get(i);
			if(i == mainValidObjectFields.size()-1){
				columns +=field.getName()+" = ?";
			}else
			{
				columns += field.getName() + " = ?, ";
			}
			
		}
		
		PreparedStatement genericStatement = 
	connection.prepareStatement("update " + tablename + " SET "+ columns +" WHERE "+ tableKeyName +" = "+keyValue);
		//For each field add to prepared statement
		for(int i=0;i<mainValidObjectFields.size();i++){
			field = mainValidObjectFields.get(i);
			if("com.bangbang.webapi.server.mpdel.JobStatus" == field.getType().getName())
			{
				JobStatus jb = (JobStatus)field.get(object);
				if(jb != null)
				value = jb.getValue();
			}
			else if("com.bangbang.webapi.server.model.UserType" == field.getType().getName())
			{
				UserType ut = (UserType)field.get(object);
				if(ut != null)
				value = ut.getValue();
			}
			else if("java.util.Date" == field.getType().getName())
			{
				Date date = (Date)field.get(object);
				if(date != null)
				value = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
			}else{
			value = field.get(object);
			}
			genericStatement.setObject(i+1, value);
		}
			//Execute the prepared statement
			genericStatement.execute();

			//Close resources
			genericStatement.close();

	}
	}*/
	/**
	 * Executes a SQL query and returns an arrayList of objects from the result
	 * @param sqlQuery
	 * @param Object template
	 * @return arrayList of objects
	 
	public ArrayList<Object> getObjectResultsFromQuery(String sqlQuery, Object template) throws Exception{

		//Init results
		ArrayList<Object> results = new ArrayList<Object>();

		//Connect and execute query
		Connection connection = getConnection();
		Statement statement = connection.createStatement();

		//Handle queries without results needed
		if (template==null) {

			if (sqlQuery.contains("DELETE")) {
				statement.executeUpdate(sqlQuery);
				return null;
			}

			statement.executeQuery(sqlQuery);
			return null;
		}

		//execute sql query
		ResultSet resultSet = statement.executeQuery(sqlQuery);
		// return from here if parse data to format like json uplayer 
		
		Class<?> newClass = Class.forName(template.getClass().getName());
		Object result;
		
		//Extract results
		while (resultSet.next()) {

			//Get object back
			result = newClass.newInstance();
		// parser to json here 	
			//result = MYSQLParser.mapSQLResultToObject(result, resultSet);
 
			//Add to results
			results.add(result);
		}

		//Close resources
		statement.close();
		resultSet.close();
		closeConnection(connection);

		//Return results
		return results;
	}
	*/
	
	/**
	 * 
	 * @param username
	 * @param classTmplate
	 * @return
	 * @throws SQLException
	 */
	public Object queryUserByName(String username,Object classTmplate) throws SQLException
	{
		Object objrtn = new Object();
		Connection connection = getConnection();
		
		PreparedStatement genericStatement = 
	connection.prepareStatement("select *  FROM  User  WHERE (username = ? OR email = ?)");
		//For each field add to prepared statement
		genericStatement.setString(1, username);
		genericStatement.setString(2, username);

		//Execute the prepared statement
		
		ResultSet rs  = genericStatement.executeQuery();

		if(true == rs.last())
		{
			if(1 !=rs.getRow() )
			{//more than one row,HUGE error
				objrtn=null;
			}
			else{
				objrtn	= DBParser.mapSQLResultToObject(classTmplate, rs);
			}
		}
		else
		{
			objrtn=null;
		}
			
		//Close resources
		genericStatement.close();
		closeConnection(connection);

		return objrtn;
	}
	/**
	 * 
	 * @param keyid
	 * @param classTmplate
	 * @param tableName
	 * @param keyIdName
	 * @return
	 * @throws SQLException
	 */
	public Object queryObjectWithoutCondition(String tableName,Object classTmplate) throws SQLException
	{
		Object objrtn = new Object();
		Connection connection = getConnection();
		
		PreparedStatement genericStatement = 
	connection.prepareStatement("select *  FROM "+ tableName);
		
		ResultSet rs  = genericStatement.executeQuery();
		if(true == rs.last())
		{
			if(1 !=rs.getRow() )
			{// more than one row,HUGE error,system info only have one row
				objrtn=null;
			}
			else{
				// need db parse here
				objrtn	= DBParser.mapSQLResultToObject(classTmplate, rs);
			}
		}
		else
		{
			objrtn=null;
		}		
		genericStatement.close();
		closeConnection(connection);

		return objrtn;
	}
	/**
	 * queryObjectByKeyid
	 * @param keyid
	 * @param classTmplate
	 * @param tableName
	 * @param keyIdName
	 * @return
	 * @throws SQLException
	 */
	public Object queryObjectByKeyid(int keyid,Object classTmplate,String tableName,String keyIdName) throws SQLException
	{
		Object objrtn = new Object();
		Connection connection = getConnection();
		
		PreparedStatement genericStatement = 
	connection.prepareStatement("select *  FROM "+ tableName +" WHERE "+ keyIdName +" = ?");
		//For each field add to prepared statement
		genericStatement.setObject(1, keyid);
		
		ResultSet rs  = genericStatement.executeQuery();
		if(true == rs.last())
		{
			if(1 !=rs.getRow() )
			{//more than one row,HUGE error
				objrtn=null;
			}
			else{
				// need db parse here
				objrtn	= DBParser.mapSQLResultToObject(classTmplate, rs);
			}
		}
		else
		{
			objrtn=null;
		}		
		genericStatement.close();
		closeConnection(connection);

		return objrtn;
	}
	/**
	 *  
	 * @param condition name
	 * @param condition value
	 * @param tableName
	 * @param classTmplate  - calss tmplate of what should return 
	 * @return null for no exist 
	 * @throws SQLException
	 */
	public Object queryOneObjectbySingleCondition(String conditonName,Object conditionValue,String tableName,Object classTmplate) throws SQLException
	{
		Object rtnObject= new Object();
		
		Connection connection = getConnection();
		PreparedStatement genericStatement = 
	connection.prepareStatement("select *  FROM "+ tableName +" WHERE "+ conditonName +" = ?");
		genericStatement.setObject(1, conditionValue);
		ResultSet rs  = genericStatement.executeQuery();
		if(true == rs.last())
		{
			if(1 !=rs.getRow() )
			{//more than one row,error
				rtnObject=null;
			}
			else{
				// need db parse here
				rtnObject	= DBParser.mapSQLResultToObject(classTmplate, rs);
			}
		}
		else
		{
			rtnObject=null;
		}		
		rs.close();
		genericStatement.close();
		closeConnection(connection);

		return rtnObject;
	}
	/**
	 *  get one single column value by column Name ,tablename and keyid
	 * @param keyId
	 * @param columnName
	 * @param tableName
	 * @return 0 if query failed,or column value
	 * @throws SQLException
	 */
	public Object queryColumnValueByName(int conditionValue,String conditionName,String columnName,String tableName) throws SQLException
	{
		Object rtn= null;
		
		Connection connection = getConnection();
		PreparedStatement genericStatement = 
		connection.prepareStatement("select "+ columnName +" FROM "+ tableName + " WHERE "+ conditionName +" = ?");
		genericStatement.setObject(1, conditionValue);

		ResultSet rs  = genericStatement.executeQuery();
		
		if(true == rs.last())
		{
			if(1 !=rs.getRow() )
			{//more than one row,error
				rtn=null;
			}
			else{
				// need db parse here
				rtn = rs.getObject(columnName);
			}
		}
		else
		{
			rtn=null;
		}		
		closeConnection(connection);

		return rtn;

	}
	/**
	 * get job of User which owneruid stand of
	 * @param owneruid
	 * @param page
	 * @param jobstatus
	 * @return
	 * @throws SQLException 
	 */
	public ArrayList<JobDTO> queryPublishedJob(int owneruid,int page,JobStatus jobstatus) throws SQLException
	{
		ArrayList<JobDTO> results = new ArrayList<JobDTO>();
		Connection connection = getConnection();

		PreparedStatement genericStatement = null;

		genericStatement = 
					connection.prepareStatement("select Job.* ," +
			"owner.nickName AS ownerNickName,owner.image AS ownerImage ,owner.points AS ownerPoints, owner.jobCompleteCount AS ownerCompleteCount,owner.gender AS ownerGender ,owner.head_photo_modify_at AS owner_head_photo_modify_at, owner.birthDay AS owner_birthDay,owner.age AS owner_age, " +
			"picker.nickName AS pickerNickName,picker.image AS pickerImage ,picker.points AS pickerPoints,picker.jobCompleteCount AS pickerCompleteCount,picker.gender AS pickerGender,picker.head_photo_modify_at AS picker_head_photo_modify_at, picker.birthDay AS picker_birthDay, picker.age AS picker_age "+
			"FROM Job INNER JOIN User owner ON Job.ownerId = owner.uid " +
			"INNER JOIN User picker ON picker.uid = Job.pickerId "+
			"WHERE (ownerId = ? AND status LIKE ? ) ORDER by modifyTime DESC LIMIT ? ");
					
		genericStatement.setObject(1, owneruid);
		if(jobstatus.equals(JobStatus.ALL))
		{
			genericStatement.setObject(2, "_");

		}else
		{
			genericStatement.setObject(2, jobstatus.getValue());

		}
		genericStatement.setObject(3, page);

		ResultSet rs  = genericStatement.executeQuery();
		
		JobDTO result;
		//Extract results
		while (rs.next()) {

			result = new JobDTO();
			result = (JobDTO)DBParser.mapSQLResultToObject(result, rs);
			//Add to results
			results.add(result);
		}
		genericStatement.close();
		closeConnection(connection);

		return results;
	}
	/**
	 *  get job of picker who pickeruid stand of
	 * @param pickeruid
	 * @param page
	 * @param jobstatus
	 * @return
	 * @throws SQLException
	 */
	public ArrayList<JobDTO> queryPickedJob(int pickeruid,int page,JobStatus jobstatus) throws SQLException
	{
		ArrayList<JobDTO> results = new ArrayList<JobDTO>();
		
		Connection connection = getConnection();
		PreparedStatement genericStatement = null;

		genericStatement = 
				connection.prepareStatement("select Job.* ," +
		"owner.nickName AS ownerNickName,owner.image AS ownerImage ,owner.points AS ownerPoints, owner.jobCompleteCount AS ownerCompleteCount,owner.gender AS ownerGender,owner.head_photo_modify_at AS owner_head_photo_modify_at, owner.birthDay AS owner_birthDay,owner.age AS owner_age," +
		"picker.nickName AS pickerNickName,picker.image AS pickerImage ,picker.points AS pickerPoints,picker.jobCompleteCount AS pickerCompleteCount,picker.gender AS pickerGender,picker.head_photo_modify_at AS picker_head_photo_modify_at,picker.birthDay AS picker_birthDay, picker.age AS picker_age "+
		"FROM Job INNER JOIN User owner ON Job.ownerId = owner.uid " +
		"INNER JOIN User picker ON picker.uid = Job.pickerId "+
		"WHERE (pickerId = ? AND status LIKE ? AND status <> ?) ORDER by modifyTime DESC LIMIT ? ");
					
		genericStatement.setObject(1, pickeruid);
		if(jobstatus.equals(JobStatus.ALL))
			{
				genericStatement.setObject(2, "_");
				genericStatement.setObject(3, JobStatus.PUBLISHED.getValue());

			}else
			{
				genericStatement.setObject(2, jobstatus.getValue());
				genericStatement.setObject(3, 0);

			}
			
		genericStatement.setObject(4, page);

		ResultSet rs  = genericStatement.executeQuery();
		
		JobDTO result = null;
		//Extract results
		while (rs.next()) {

			result = new JobDTO();
			result = (JobDTO)DBParser.mapSQLResultToObject(result, rs);
			//Add to results
			results.add(result);
		}
		genericStatement.close();
		closeConnection(connection);

		return results;
	}
	/**
	 * 
	 * @param reqJobNumbers
	 * @param jobStatus
	 * @param longitude
	 * @param latitude
	 * @param range  not used yet
	 * @param sex
	 * @return
	 * @throws SQLException
	 */
	public ArrayList<JobDTO> queryJobByLocation(int reqUid,int reqJobNumbers,int jobStatus,double longitude,double latitude,int range,String gender) throws SQLException
	{
		ArrayList<JobDTO> results = new ArrayList<JobDTO>();
		if(!BYConstants.SEX_ALL.equals(gender) && !BYConstants.SEX_FEMALE.equals(gender) &&!BYConstants.SEX_MALE.equals(gender) )
		{
			System.out.println("BAD gender :"+gender);
			return null;
		}
		Connection connection = getConnection();
		PreparedStatement genericStatement = null;
		
		genericStatement = 
					connection.prepareStatement(
						"select Job.* ," +
						"owner.nickName AS ownerNickName,owner.image AS ownerImage ,owner.points AS ownerPoints,owner.jobCompleteCount AS ownerCompleteCount,owner.gender AS ownerGender, owner.head_photo_modify_at AS owner_head_photo_modify_at,owner.birthDay AS owner_birthDay,owner.age AS owner_age," +
							"( 6371 * ACOS(SIN( RADIANS( ? ) )* SIN( RADIANS(Job.latitude ) ) + COS( RADIANS(?) )* COS( RADIANS( Job.latitude  ) ) * COS( RADIANS( Job.longitude ) - RADIANS( ? ) )) ) AS distance " +
							"FROM Job INNER JOIN User owner ON Job.ownerId = owner.uid " +
							"WHERE (owner.gender LIKE ? AND Job.status = ? AND Job.ownerId <> ?) ORDER BY distance ASC LIMIT ? ");


		genericStatement.setObject(1, latitude);
		genericStatement.setObject(2, latitude);
		genericStatement.setObject(3, longitude);

		if(BYConstants.SEX_ALL.equals(gender))
		{
			genericStatement.setObject(4, "_");
		}
		else
		{
			genericStatement.setObject(4, gender);
		}
		genericStatement.setObject(5, JobStatus.PUBLISHED.getValue());
		genericStatement.setObject(6, reqUid);
		genericStatement.setObject(7, reqJobNumbers);
		ResultSet rs  = genericStatement.executeQuery();
		
		JobDTO result = null;
		//Extract results
		while (rs.next()) {

			result = new JobDTO();
			result = (JobDTO)DBParser.mapSQLResultToObject(result, rs);
			//Add to results
			results.add(result);
		}
		genericStatement.close();
		closeConnection(connection);

		return results;
		
	}
	/** INNER JOIN TABLE OWNER AND PICKER
	 * select Job.title ,owner.nickname AS ownername,picker.nickname AS pickerName from Job 
	 * INNER JOIN User owner ON owner.uid = Job.ownerId 
	 * INNER JOIN User picker ON picker.uid = Job.pickerId AND picker.uid <> owner.uid;
	 * @throws SQLException 
	 */
	/**
	 * 
	 * @param ownerId
	 * @return
	 * @throws SQLException
	 */
	public boolean updatePublishCounter(int ownerId) throws SQLException
	{
		boolean rtn = true;
		Connection connection = getConnection();
		PreparedStatement genericStatement = 
				connection.prepareStatement("UPDATE  User SET jobPublishCount = jobPublishCount+1  WHERE  uid = ? " );

		genericStatement.setObject(1, ownerId);
		//Execute the prepared statement
		if(1 != genericStatement.executeUpdate())
		{
			rtn = false;
		}
		//Close resources
		genericStatement.close();
		closeConnection(connection);

		return rtn;
	}
	/**
	 * 
	 * @param uid
	 * @param points
	 * @return
	 * @throws SQLException
	 */
	public boolean updateUserCompleteCounterAndPoints(int uid,int points) throws SQLException
	{
		boolean rtn = true;
		Connection connection = getConnection();
		PreparedStatement genericStatement = 
				connection.prepareStatement("UPDATE  User SET jobCompleteCount = jobCompleteCount + 1 ,points = points +? WHERE  uid = ? " );

		genericStatement.setObject(1, points);
		genericStatement.setObject(2, uid);

		//Execute the prepared statement
		if(1 != genericStatement.executeUpdate())
		{
			rtn = false;
		}
		//Close resources
		genericStatement.close();
		closeConnection(connection);

		return rtn;
	}
	public ArrayList<MessagesDTO> queryMsgsByJobId(int jobId) throws SQLException
	{
		ArrayList<MessagesDTO> results = new ArrayList<MessagesDTO>();

		Connection connection = getConnection();
		PreparedStatement genericStatement = null;
		
		genericStatement = 
					connection.prepareStatement(
						"select * FROM  messages WHERE jobid=? ORDER BY createtime ASC");


		genericStatement.setObject(1, jobId);

		ResultSet rs  = genericStatement.executeQuery();
		
		MessagesDTO result = null;
		//Extract results
		while (rs.next()) {

			result = new MessagesDTO();
			result = (MessagesDTO)DBParser.mapSQLResultToObject(result, rs);
			//Add to results
			results.add(result);
		}
		genericStatement.close();
		closeConnection(connection);

		return results;
	}
	
}
