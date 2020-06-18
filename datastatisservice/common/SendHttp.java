package cn.piesat.datastatisservice.common;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author zb  
 */
public class SendHttp {
    public static String httpURLConnectionPOST (String url, String data) {
        StringBuffer sbf = new StringBuffer();
        try {
            URL realUrl = new URL(url);
            //将realUrl以 open方法返回的urlConnection  连接强转为HttpURLConnection连接  (标识一个url所引用的远程对象连接)
            HttpURLConnection connection = (HttpURLConnection) realUrl.openConnection();// 此时cnnection只是为一个连接对象,待连接中
            //设置连接输出流为true,默认false (post请求是以流的方式隐式的传递参数)
            connection.setDoOutput(true);
            //设置连接输入流为true
            connection.setDoInput(true);
            //设置请求方式为post
            connection.setRequestMethod("POST");
            //post请求缓存设为false
            connection.setUseCaches(false);
            //设置该HttpURLConnection实例是否自动执行重定向
            connection.setInstanceFollowRedirects(true);
            //设置请求头里面的各个属性 (以下为设置内容的类型,设置为经过urlEncoded编码过的from参数)
            connection.setRequestProperty("Content-Type", "application/json;charset=utf-8");
            //建立连接 (请求未开始,直到connection.getInputStream()方法调用时才发起,以上各个参数设置需在此方法之前进行)
            connection.connect();
            //创建输入输出流,用于往连接里面输出携带的参数,(输出内容为?后面的内容)
            DataOutputStream dataout = new DataOutputStream(connection.getOutputStream());
            //String query = data.toString();
            //将参数输出到连接
            dataout.write(data.getBytes("UTF-8"));
            // 输出完成后刷新并关闭流
            dataout.flush();
            dataout.close(); // 重要且易忽略步骤 (关闭流,切记!)
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            String lines;
            while ((lines = reader.readLine()) != null) {
                lines = new String(lines.getBytes(), "utf-8");
                sbf.append(lines);
            }
            reader.close();
            connection.disconnect();
           
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sbf.toString();
    }
}
