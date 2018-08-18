package com.learn.java.start;

import java.util.Iterator;
import java.util.List;

import org.mybatis.spring.batch.MyBatisCursorItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;
import org.springframework.remoting.support.DefaultRemoteInvocationExecutor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.zlx.user.dal.mapper.DeviceDOMapper;
import com.zlx.user.dal.model.DeviceDO;
import com.zlx.user.dal.model.DeviceDOExample;

@RestController
@SpringBootApplication
@ImportResource("datasource.xml")
@ComponentScan(basePackages = { "com.learn.java.start" })
public class App {

	@Autowired
	private DeviceDOMapper deviceDOMapper;

	@Autowired
	private DeviceBoImpl deviceBoImpl;

	@RequestMapping("/")
	String home() {
		return "Hello World!";
	}

	@RequestMapping("/selectByStream")
	String selectByStream() {

		DeviceDOExample example = new DeviceDOExample();
		// example.createCriteria().andIdLessThan(1000);

		String result = deviceBoImpl.selectDataByStream(example, new ProcessCallback() {

			@Override
			public void doAction(MyBatisCursorItemReader<DeviceDO> myMyBatisCursorItemReader) {

				try {
					long begin = System.currentTimeMillis();

					DeviceDO deviceDO = null;
					while ((deviceDO = myMyBatisCursorItemReader.read()) != null) {
						System.out.println(JSON.toJSON(deviceDO));
					}
					long cost = System.currentTimeMillis() - begin;
					System.out.println("process time :" + cost);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		return result;
	}

	@RequestMapping("/selectAll")
	String selectAll() {

		DeviceDOExample example = new DeviceDOExample();
		// example.createCriteria().andIdLessThan(1000);

		long begin = System.currentTimeMillis();
		List<DeviceDO> result = deviceBoImpl.selectData(example);
		long waitCost = System.currentTimeMillis() - begin;
		System.out.println("wait time :" + waitCost);

		begin = System.currentTimeMillis();
		Iterator<DeviceDO> itr = result.iterator();
		while (itr.hasNext()) {
			System.out.println(JSON.toJSONString(itr.next()));
		}

		long processtime = System.currentTimeMillis() - begin;
		System.out.println("process time :" + processtime);

		return "wait time:" + waitCost + ", process time:" + processtime;
	}

	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
	}
}
