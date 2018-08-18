package com.learn.java.start;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.batch.MyBatisCursorItemReader;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zlx.user.dal.mapper.DeviceDOMapper;
import com.zlx.user.dal.model.DeviceDO;
import com.zlx.user.dal.model.DeviceDOExample;

@Component
public class DeviceBoImpl {

	private final static String QUERY_ID = "com.zlx.user.dal.mapper.DeviceDOMapper.selectByExampleForStream";
	@Autowired
	private DeviceDOMapper deviceDOMapper;

	@Autowired
	private SqlSessionFactory sqlSessionFactory;

	/**
	 * 普通查询
	 * 
	 * @param example
	 * @param callBack
	 * @return
	 */
	public List<DeviceDO> selectData(DeviceDOExample example) {
		List<DeviceDO> result = deviceDOMapper.selectByExample(example);
		return result;
	}

	/**
	 * 流式查询
	 */
	public String selectDataByStream(DeviceDOExample example, ProcessCallback callBack) {
		MyBatisCursorItemReader<DeviceDO> myBatisCursorItemReader = new MyBatisCursorItemReader<DeviceDO>();

		try {
			// 1.设置查询id
			myBatisCursorItemReader.setQueryId(QUERY_ID);
			myBatisCursorItemReader.setSqlSessionFactory(sqlSessionFactory);

			// 2.设置条件
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("oredCriteria", example.getOredCriteria());
			param.put("orderByClause", example.getOrderByClause());

			// 3.设置参数
			myBatisCursorItemReader.setParameterValues(param);

			// 4. 打开连接
			long begin = System.currentTimeMillis();
			myBatisCursorItemReader.open(new ExecutionContext());
			long cost = System.currentTimeMillis() - begin;
			System.out.println("wait time :" + cost);

			// 5.执行业务
			begin = System.currentTimeMillis();

			callBack.doAction(myBatisCursorItemReader);
			long pocsesscost = System.currentTimeMillis() - begin;
			System.out.println("process time :" + pocsesscost);
			return "wait cost:" + cost + ", process time:" + pocsesscost;

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 6.关闭连接
			myBatisCursorItemReader.close();

		}

		return null;

	}

}
