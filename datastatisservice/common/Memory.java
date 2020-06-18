package cn.piesat.datastatisservice.common;

public class Memory {

	private long memTotal;
	
	private long memUsed;
	
	private long memFree;
	
	private long swapTotal;
	
	private long swapfree;
	
	private long swapUsed;
	
	private String usedPercent;

	

	public String getUsedPercent() {
		return usedPercent;
	}

	public void setUsedPercent(String usedPercent) {
		this.usedPercent = usedPercent;
	}

	public long getMemTotal() {
		return memTotal;
	}

	public void setMemTotal(long memTotal) {
		this.memTotal = memTotal;
	}

	public long getMemUsed() {
		return memUsed;
	}

	public void setMemUsed(long memUsed) {
		this.memUsed = memUsed;
	}

	public long getMemFree() {
		return memFree;
	}

	public void setMemFree(long memFree) {
		this.memFree = memFree;
	}

	public long getSwapTotal() {
		return swapTotal;
	}

	public void setSwapTotal(long swapTotal) {
		this.swapTotal = swapTotal;
	}

	public long getSwapfree() {
		return swapfree;
	}

	public void setSwapfree(long swapfree) {
		this.swapfree = swapfree;
	}

	public long getSwapUsed() {
		return swapUsed;
	}

	public void setSwapUsed(long swapUsed) {
		this.swapUsed = swapUsed;
	}
	
	
}
