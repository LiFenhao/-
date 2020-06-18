package cn.piesat.datastatisservice.controller;

import cn.piesat.datastatisservice.common.FileSys;
import cn.piesat.datastatisservice.common.Memory;
import cn.piesat.datastatisservice.common.SendHttp;
import cn.piesat.datastatisservice.common.ServerNodeState;
import cn.piesat.datautilservice.common.ServiceResult;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import io.swagger.annotations.Api;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hyperic.sigar.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

import java.io.BufferedReader;
import java.io.InputStreamReader;


/**
 * @description:
 * @createTime: 2019.5.23 13:34
 * @version: 1.0
 */
@Component
@CrossOrigin
@Api(tags = "服务器状态采集")
public class DiskStatusCollect {

	@Value("${collect.callback.url}")
	private String collecCallbackUrl;
	
	@Value("${collect.nfs.disk.enable}")
	private boolean nfsEnable;
	
	@Value("${collect.nfs.disk.path}")
	private String nfsPath;
	
	@Value("${collect.nfs.disk.node.num}")
	private String nfsNum;
	
	@Value("${collect.hdfs.disk.enable}")
	private boolean hdfsEnable;
	
	@Value("${collect.hdfs.disk.path}")
	private String hdfsPath;
	
	@Value("${collect.hdfs.disk.node.num}")
	private String hdfsNum;
	
	@Value("${collect.mysql.disk.enable}")
	private boolean mysqlEnable;
	
	@Value("${collect.mysql.disk.path}")
	private String mysqlPath;
		
	@Value("${collect.mysql.disk.node.num}")
	private String mysqlNum;

 
	private static final Logger logger = LogManager.getLogger(DiskStatusCollect.class);
	   
	
	@Scheduled(cron = "${server.info.collect.cronTrigger:*/15 * * * * ?}")
	public ServiceResult getSysInfo() {
		logger.info("开始采集服务器信息：");
		ServiceResult serviceResult = new ServiceResult(false);
		List<ServerNodeState> info = new 	ArrayList<ServerNodeState>();

    	try {
    		if(nfsEnable) {
    			ServerNodeState sns = new ServerNodeState();
    			sns.setNodeNum(nfsNum);
    			sns.setStorageType("nfs");
    			getDiskInfo(nfsPath,sns);
    			info.add(sns);
    		}
    		
    		if(hdfsEnable) {
    			ServerNodeState hdfs = new ServerNodeState();
    			hdfs.setNodeNum(hdfsNum);
    			hdfs.setStorageType("hdfs");
    			getDiskInfo(hdfsPath,hdfs);
    			info.add(hdfs);
    		}
    		
    		if(mysqlEnable) {
    			ServerNodeState mysql = new ServerNodeState();
    			mysql.setNodeNum(mysqlNum);
    			mysql.setStorageType("mysql");
    			getDiskInfo(mysqlPath,mysql);
    			info.add(mysql);
    		}
    		
    		logger.info("采集服务器信息完成：" + new Gson().toJson(info)); 	
    		SendHttp.httpURLConnectionPOST(collecCallbackUrl, new Gson().toJson(info));
        } catch (Exception e1) {
            e1.printStackTrace();
        }
		return serviceResult;

    }
	
	public static void getDiskInfo(String sharePath ,ServerNodeState sns) {
		//全部
        String all = exeCmd("df -hl "+sharePath+" | tail -n 1 | awk '{ print $2 }'");
        sns.setUsageTotal(all);
        //可用
        String canUse = exeCmd("df -hl "+sharePath+" | tail -n 1 | awk '{ print $4 }'");
        sns.setUsageAvail(canUse);
        //已用
        String used = exeCmd("df -hl "+sharePath+" | tail -n 1 | awk '{ print $3 }'");
        sns.setUsageUsed(used);
        //百分比
        String diskPro = exeCmd("df -hl "+sharePath+" | tail -n 1 | awk '{ print $5 }'");
        sns.setUsePercent(diskPro);
        sns.setDateTime(getDate());
	}
	
	public static String exeCmd(String commandStr) {

        String result = null;
        try {
            String[] cmd = new String[]{"/bin/sh", "-c", commandStr};
            Process ps = Runtime.getRuntime().exec(cmd);

            BufferedReader br = new BufferedReader(new InputStreamReader(ps.getInputStream()));
            StringBuffer sb = new StringBuffer();
            String line;
            while ((line = br.readLine()) != null) {
                //执行结果加上回车
                sb.append(line).append("\n");
            }
            result = sb.toString().split("/")[0].trim();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
 



	

		public static String getDefaultIpAddress() {
			    String address = null;
			    try {
			        address = InetAddress.getLocalHost().getHostAddress();
			        // 没有出现异常而正常当取到的IP时，如果取到的不是网卡循回地址时就返回
			      // 否则再通过Sigar工具包中的方法来获取
			if (!NetFlags.LOOPBACK_ADDRESS.equals(address)) {
			             return address;
			        }
			    } catch (UnknownHostException e) {
			        //hostname not in DNS or /etc/hosts
			    }
			    Sigar sigar = new Sigar();
			    try {
			        address = sigar.getNetInterfaceConfig().getAddress();
			    } catch (SigarException e) {
			        address = NetFlags.LOOPBACK_ADDRESS;
			    } finally {
			        sigar.close();
			    }
			return address;
		}
		
		public static String getDate()  {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			return sdf.format(new Date());
		}
		
	

}
