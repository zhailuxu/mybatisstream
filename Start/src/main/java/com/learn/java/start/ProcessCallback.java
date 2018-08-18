package com.learn.java.start;

import java.sql.ResultSet;

import org.mybatis.spring.batch.MyBatisCursorItemReader;

import com.zlx.user.dal.model.DeviceDO;


public interface ProcessCallback {

	void doAction(MyBatisCursorItemReader<DeviceDO> myMyBatisCursorItemReader);
	
}
