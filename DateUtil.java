package cn.piesat.datautilservice.DateUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @description:时间工具类
 * @createTime: 2019.4.17 13:41
 * @author: XD
 * @version: 1.0
 */
public class DateUtil {

    /**
     * 将日期按照yyyy-MM-dd HH:mm:ss的格式转换为字符串
     *
     * @param date
     * @return
     */
    public static String dateToStr(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = sdf.format(date);
        return time;
    }

    /**
     * 将日期按照指定的格式转换为字符串
     *
     * @param date
     * @param format
     * @return
     */
    public static String dateToStrByFormat(Date date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        String time = sdf.format(date);
        return time;
    }

    /**
     * 将yyyy-MM-dd HH:mm:ss格式的字符串转化为日期
     *
     * @param str
     * @return
     * @throws ParseException
     */
    public static Date strToDate(String str) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = sdf.parse(str);
        return date;
    }

    /**
     * 将字符串按照指定格式转化为日期
     *
     * @param str
     * @param format
     * @return
     * @throws ParseException
     */
    public static Date strToDateByFormat(String str, String format) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date date = sdf.parse(str);
        return date;
    }

    /**
     * 将2019-06-13T19:34:28.000+0000 格式的时间转换为yyyy-MM-dd HH:mm:ss
     */
    public static String mysqlTimetoStr(String mysqlTime) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sssZ");
        Date date = formatter.parse(mysqlTime);
        return dateToStr(date);
    }

    /**
     * 获得当前时间
     *
     * @return Date
     */
    public static Date getNowTime() {
        return new Date();
    }

}
