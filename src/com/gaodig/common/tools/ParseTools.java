package com.gaodig.common.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.gaodig.common.config.Constants;
import com.gaodig.common.config.Constants.CounterType;
import com.gaodig.zkmon.bean.FalconItem;


public class ParseTools {
	
	/**
	 * 解析四字指令mntr
	 * @param rs
	 * @return
	 */
	 public static Map<String, String> parseTelnetResult(String rs) {  
		   Map<String, String> resultMap = new HashMap<String, String>();
		   if(StringUtils.isEmpty(rs)){
			   return resultMap;
		   }
		   
	        String[] resultArray = rs.split("\n");  
	        for (String recordLine : resultArray) {  
	            String[] recordKeyValue = recordLine.split("\t");  
	            if (recordKeyValue != null && recordKeyValue.length == 2) {  
	                resultMap.put(recordKeyValue[0], recordKeyValue[1]);  
	            }  
	        }  
	        return resultMap;  
	    } 
	 
	   public Long translateStrToLong(String value) {  
	        if (StringUtils.isAlphanumeric(value)) {  
	            return Long.valueOf(value);  
	        }  
	        return 0L;  
	    }
	   
	   /**
	    * 封装mntr指令信息
	    * @param zkResultData
	    * @param hostname
	    * @param zkport
	    * @return
	    * @throws Exception
	    */
		public static List<FalconItem> mntrbuild(Map<String, String> zkResultData,String hostname,int zkport){
			List<FalconItem> items = new ArrayList<FalconItem>();
 			// 将zk信息封装成openfalcon格式数据
			for (String zkmetricBean : zkResultData.keySet()) {
				String value = zkResultData.get(zkmetricBean);
				if(!StringUtils.isNumeric(value)||zkmetricBean.equals("zk_version")||zkmetricBean.equals("zk_server_state")){
					continue;
				}
				String countType = CounterType.GAUGE.toString();
				if(zkmetricBean.startsWith("zk_")){
					zkmetricBean = zkmetricBean.replaceAll("zk_", "");
				}
				FalconItem avgTimeItem = new FalconItem();
				if(zkmetricBean.endsWith("received")||zkmetricBean.endsWith("sent")){
					countType = CounterType.COUNTER.toString();
				}
				avgTimeItem.setCounterType(countType);
				avgTimeItem.setEndpoint(hostname);
				avgTimeItem.setMetric(StringUtils.lowerCase( "zookeeper"+ Constants.metricSeparator +zkmetricBean));
				avgTimeItem.setStep(Constants.defaultStep);
				avgTimeItem.setTags(StringUtils.lowerCase("zkport=" + zkport));	
				avgTimeItem.setTimestamp(System.currentTimeMillis() / 1000);
				avgTimeItem.setValue(Integer.valueOf(value));
				items.add(avgTimeItem);
			}
			return items;
		}
	   
		

}
