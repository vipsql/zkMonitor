package com.gaodig.common.config;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

public class Config {
	
	
	private String hostname;
	private String agentPostUrl;
	private int step;
	
	private String	zkHost;
	private int[] zkPorts;
	
	public void init(String configPath) throws ConfigurationException{
		System.out.println("init config");
		PropertiesConfiguration config  = new PropertiesConfiguration(configPath);
		config.setThrowExceptionOnMissing(true);
		this.hostname = config.getString("hostname", Utils.getHostNameForLinux());
		this.agentPostUrl = config.getString("agent.posturl");
		this.step = config.getInt("step", 60);
		this.zkHost = config.getString("zkHost");
		if (this.hostname == null) {
			this.hostname = "localhost";
		}
		
		String[] jmxPortArray = config.getStringArray("zkPorts");
		zkPorts = new int[jmxPortArray.length];
		for (int i = 0; i < jmxPortArray.length; i++) {
			zkPorts[i] = Integer.parseInt(jmxPortArray[i]);
		}
		System.out.println("init OK");
	}

	public String getHostname() {
		return hostname;
	}

	public String getAgentPostUrl() {
		return agentPostUrl;
	}

	public int getStep() {
		return step;
	}
	
	public int[] getZkPorts() {
		return zkPorts;
	}
	
	public String getZkHost() {
		return zkHost;
	}
	
}
