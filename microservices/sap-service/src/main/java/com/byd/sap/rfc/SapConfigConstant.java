package com.byd.sap.rfc;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "sap")
public class SapConfigConstant {
	private String host;
	private String user;
	private String password;
	private String client;
	private String language;
	private String system_id;
	private String max;
	private String pool_name;
	private String program_id;
	private String idoc_system_id;
	private String r3name;
	private String group;
	private String time_out;
	private boolean group_flag;
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getClient() {
		return client;
	}
	public void setClient(String client) {
		this.client = client;
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public String getSystem_id() {
		return system_id;
	}
	public void setSystem_id(String system_id) {
		this.system_id = system_id;
	}
	public String getMax() {
		return max;
	}
	public void setMax(String max) {
		this.max = max;
	}
	public String getPool_name() {
		return pool_name;
	}
	public void setPool_name(String pool_name) {
		this.pool_name = pool_name;
	}
	public String getProgram_id() {
		return program_id;
	}
	public void setProgram_id(String program_id) {
		this.program_id = program_id;
	}
	public String getIdoc_system_id() {
		return idoc_system_id;
	}
	public void setIdoc_system_id(String idoc_system_id) {
		this.idoc_system_id = idoc_system_id;
	}
	public String getR3name() {
		return r3name;
	}
	public void setR3name(String r3name) {
		this.r3name = r3name;
	}
	public String getGroup() {
		return group;
	}
	public void setGroup(String group) {
		this.group = group;
	}
	public String getTime_out() {
		return time_out;
	}
	public void setTime_out(String time_out) {
		this.time_out = time_out;
	}
	public boolean getGroup_flag() {
		return group_flag;
	}
	public void setGroup_flag(boolean group_flag) {
		this.group_flag = group_flag;
	}
	
}
