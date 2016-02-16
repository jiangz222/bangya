package com.bangbang.webapi.server.repository;

import java.sql.SQLException;

import com.bangbang.webapi.server.model.SystemDAO;

public class SystemRepository implements ISystemRepository {
	public DBRepositoryOperator dbOp = new DBRepositoryOperator();

	@Override
	public SystemDAO getSystemInfo() {
		// TODO Auto-generated method stub
		SystemDAO systemDao = new SystemDAO();
		try {
			System.out.println("try to check db");
			systemDao = (SystemDAO)dbOp.queryObjectWithoutCondition("system", systemDao);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return systemDao;
	}

}
