package cn.piesat.datastatisservice.common;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: PC-HTHT
 * @date: 2019/3/6 16:38
 * @description:
 */
public class ServerNodeState {


    private String ipAddress;
    
    private  String storageType;
    
    private String nodeNum;
    
    private String usageTotal;
	
	private String usageFree;
	
	private String usageAvail;
	
	private String usageUsed;
	
	private String usePercent;
    
    private String dateTime;

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getStorageType() {
		return storageType;
	}

	public void setStorageType(String storageType) {
		this.storageType = storageType;
	}

	public String getNodeNum() {
		return nodeNum;
	}

	public void setNodeNum(String nodeNum) {
		this.nodeNum = nodeNum;
	}

	public String getUsageTotal() {
		return usageTotal;
	}

	public void setUsageTotal(String usageTotal) {
		this.usageTotal = usageTotal;
	}

	public String getUsageFree() {
		return usageFree;
	}

	public void setUsageFree(String usageFree) {
		this.usageFree = usageFree;
	}

	public String getUsageAvail() {
		return usageAvail;
	}

	public void setUsageAvail(String usageAvail) {
		this.usageAvail = usageAvail;
	}

	public String getUsageUsed() {
		return usageUsed;
	}

	public void setUsageUsed(String usageUsed) {
		this.usageUsed = usageUsed;
	}

	public String getUsePercent() {
		return usePercent;
	}

	public void setUsePercent(String usePercent) {
		this.usePercent = usePercent;
	}

	public String getDateTime() {
		return dateTime;
	}

	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}

	
	

}
