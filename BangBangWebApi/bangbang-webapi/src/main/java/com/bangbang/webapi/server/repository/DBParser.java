package com.bangbang.webapi.server.repository;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.bangbang.webapi.server.model.JobStatus;
import com.bangbang.webapi.server.model.UserType;
import com.bangbang.webapi.server.model.RewardType;

public class DBParser {
	/**
	 * Give it an object as a template and a result set row and it will parse it for you
	 * and give back the parsed result
	 * 
	 * Query has to be SELECT *
	 * @param templateObject
	 * @param setObject
	 * @return parsedObject
	 * @throws Exception
	 */
	public static Object mapSQLResultToObject(Object template, ResultSet resultObject) throws SQLException{

		//Init result
		Object result = (Object)template;

		//Get the object's fields
		Field [] objectFields = template.getClass().getDeclaredFields();

		//Iterate over result set results
		for (Field field : objectFields) {
			field.setAccessible(true);

			try {
				if("com.bangbang.webapi.server.model.JobStatus" == field.getType().getName())
				{
					int dbValue = resultObject.getInt(field.getName());
					JobStatus enumValue = JobStatus.values()[dbValue-1];
					field.set(result, enumValue);
					continue;
				}
				else if("com.bangbang.webapi.server.model.UserType" == field.getType().getName())
				{
					int usertype = resultObject.getInt(field.getName());
					UserType enumValue = UserType.values()[usertype-1];
					field.set(result, enumValue);
					continue;
				}
				else if("com.bangbang.webapi.server.model.RewardType" == field.getType().getName())
				{
					int rewardType = resultObject.getInt(field.getName());
					RewardType enumValue = RewardType.values()[rewardType-1];
					field.set(result, enumValue);
					continue;
				}
				else if("java.util.Date" == field.getType().getName())
				{
					String dateString = resultObject.getString(field.getName());
					Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateString);
					System.out.println("get data:"+date);

					field.set(result, date);
					continue;
				}else
				{
					//Get the value from the DB object
					Object dbValue = resultObject.getObject(field.getName());
					if (dbValue!=null) {
						System.out.println("field.getName():"+field.getName()+":"+dbValue);

						//Set the value in the object
						field.set(result, dbValue);
					}
				}
				//Get the value from the DB object
				

			} catch (Exception e) {
				System.err.println("Self generated " +e.getMessage());

			}
		}

		//Return the result
		return result;
	}
}
