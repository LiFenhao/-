package cn.piesat.datastatisservice.common;

public class FileSys {
	
	private String devName;
	
	private String dirName;
	
	private long flags;
	
	private String sysTypeName;
	
	private long usageTotal;
	
	private long usageFree;
	
	private long usageAvail;
	
	private long usageUsed;
	
	private String usePercent;

	public String getDevName() {
		return devName;
	}

	public void setDevName(String devName) {
		this.devName = devName;
	}

	public String getDirName() {
		return dirName;
	}

	public void setDirName(String dirName) {
		this.dirName = dirName;
	}

	public long getFlags() {
		return flags;
	}

	public void setFlags(long flags) {
		this.flags = flags;
	}

	public String getSysTypeName() {
		return sysTypeName;
	}

	public void setSysTypeName(String sysTypeName) {
		this.sysTypeName = sysTypeName;
	}

	public long getUsageTotal() {
		return usageTotal;
	}

	public void setUsageTotal(long usageTotal) {
		this.usageTotal = usageTotal;
	}

	public long getUsageFree() {
		return usageFree;
	}

	public void setUsageFree(long usageFree) {
		this.usageFree = usageFree;
	}

	public long getUsageAvail() {
		return usageAvail;
	}

	public void setUsageAvail(long usageAvail) {
		this.usageAvail = usageAvail;
	}

	public long getUsageUsed() {
		return usageUsed;
	}

	public void setUsageUsed(long usageUsed) {
		this.usageUsed = usageUsed;
	}

	public String getUsePercent() {
		return usePercent;
	}

	public void setUsePercent(String usePercent) {
		this.usePercent = usePercent;
	}

	

}
