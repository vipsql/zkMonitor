package com.gaodig.source.provider;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gaodig.common.tools.JacksonUtil;
import com.gaodig.common.tools.ParseTools;
import com.gaodig.common.tools.TelnetTools;
import com.gaodig.zkmon.bean.FalconItem;

public class DataProvider {
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	TelnetTools telnet = null;

	/**
	 * conf 输出相关服务配置的详细信息。 
	 * cons 列出所有连接到服务器的客户端的完全的连接 /会话的详细信息。包括“接受 / 发送”的包数量、会话
	 * id 、操作延迟、最后的操作执行等等信息。
	 *  dump 列出未经处理的会话和临时节点。 envi 输出关于服务环境的详细信息（区别于 conf命令）。
	 *  reqs 列出未经处理的请求 ruok 测试服务是否处于正确状态。如果确实如此，那么服务返回“imok ”，否则不做任何相应。
	 * stat 输出关于性能和连接的客户端的列表。 
	 * wchs 列出服务器 watch的详细信息。
	 *  wchc 通过 session列出服务器
	 * watch的详细信息，它的输出是一个与watch相关的会话的列表。
	 *  wchp 通过路径列出服务器 watch的详细信息。它输出一个与 session相关的路径。
	 */
	String[] FourOrder = { "ruok","mntr","cons", "dump", "reqs", "conf", "stat", "wchs", "wchc", "wchp" };
	
	public List<FalconItem> getMonitorData(String hostname, int port,String endpoint)   {
		Map<String, String> telnetResultMap = new HashMap<String,String>();
		telnet = new TelnetTools();
		try {
			telnet.login(hostname, port);
			telnetResultMap.put("alive", "1");
			String rs = telnet.sendCommand("mntr");
			telnetResultMap.putAll(ParseTools.parseTelnetResult(rs));
		} catch (Exception e) {
			telnetResultMap.put("alive", "0");
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
		}
		List<FalconItem> items = ParseTools.mntrbuild(telnetResultMap, endpoint, port);
		return items;
	}

	public static void main(String[] args) throws Exception {
		DataProvider provider = new DataProvider();
		List<FalconItem> items = provider.getMonitorData("172.16.2.112", 2181,"master");
		String content = JacksonUtil.writeBeanToString(items, false);
		System.out.println(content);
	}
}
