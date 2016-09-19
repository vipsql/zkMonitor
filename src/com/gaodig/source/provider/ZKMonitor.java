package com.gaodig.source.provider;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.xml.DOMConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gaodig.common.config.Config;
import com.gaodig.common.tools.HttpClientUtils;
import com.gaodig.common.tools.HttpClientUtils.HttpResult;
import com.gaodig.common.tools.JacksonUtil;
import com.gaodig.zkmon.bean.FalconItem;

public class ZKMonitor {
	
	private static Logger logger = LoggerFactory.getLogger(ZKMonitor.class);
	private static Config config = new Config();
	
	
	public static void main(String[] args){
		if (args.length != 1) {
			throw new IllegalArgumentException("Usage: configFile");
		}
		//set log4j console
		String log4jfile = "log4j.properties";
		BasicConfigurator.configure();														//自动快速地使用缺省Log4j环境.
		PropertyConfigurator.configure(log4jfile); //读取使用Java的特性文件编写的配置文件。	
		//DOMConfigurator.configure (log4jfile);//读取XML形式的配置文件。
		try {
			config.init(args[0]);
		} catch (Exception e) {
			e.printStackTrace();
			throw new IllegalStateException(e);	// 抛出异常便于外部脚本感知
		}
		
		//定时循环执行
		ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
		executor.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				runTask();
			}
		}, 0, config.getStep(), TimeUnit.SECONDS);
	}
	
	private static void runTask() {
		List<FalconItem> items = new ArrayList<FalconItem>();
		String zkHost = config.getZkHost();
		String endpoint = config.getHostname();
		int[] zkPorts = config.getZkPorts();
		if(StringUtils.isNotEmpty(zkHost)&&zkPorts!=null){
			for (int zkPort : config.getZkPorts()) {
				List<FalconItem> reitems = new DataProvider().getMonitorData(zkHost, zkPort,endpoint);
				if(reitems!=null&&reitems.size()>0){
					items.addAll(reitems);
				}
			}
		}else{
			logger.warn("hostname or zkPorts is null");
		}
		
		//发送数据至openfalconAgent
		try {
			String content = JacksonUtil.writeBeanToString(items, false);
			HttpResult postResult = HttpClientUtils.getInstance().post(config.getAgentPostUrl(), content);
			logger.info("post status=" + postResult.getStatusCode() + ", post url=" + config.getAgentPostUrl() + ", content=" + content);
			if (postResult.getStatusCode() != HttpClientUtils.okStatusCode ||
					postResult.getT() != null) {
				logger.warn("Message not send!!");
			}
		} catch (Exception e) {
			// TODO: handle exception
			logger.error(e.getMessage());
		}
	}	

}
