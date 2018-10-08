package com.ecpss.util;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**   
 * Title: ����ʱ��   
 * Description: ������   
 * @author huanglq   
 * @version 1.0   
 */
public class DateUtil {

	/**����*/
	private static String ClassName = "com.huanglq.util.DateTime";

	/**���ػ�*/
	private static Locale locale = Locale.SIMPLIFIED_CHINESE;

	/**ȱʡ��DateFormat���󣬿��Խ�һ��java.util.Date��ʽ���� yyyy-mm-dd ���*/
	private static DateFormat dateDF = DateFormat.getDateInstance(
			DateFormat.MEDIUM);

	/**ȱʡ��DateFormat���󣬿��Խ�һ��java.util.Date��ʽ���� HH:SS:MM ���*/
	private static DateFormat timeDF = DateFormat.getTimeInstance(
			DateFormat.MEDIUM);

	/**ȱʡ��DateFormat���󣬿��Խ�һ��java.util.Date��ʽ���� yyyy-mm-dd HH:SS:MM ���*/
	private static DateFormat datetimeDF = DateFormat.getDateTimeInstance(
			DateFormat.MEDIUM, DateFormat.MEDIUM);

	/**   
	 * ˽�й��캯������ʾ����ʵ����   
	 */
	private DateUtil() {
	}

	/**   
	 * ����һ����ǰ��ʱ�䣬������ʽת��Ϊ�ַ���   
	 * ����17:27:03   
	 * @return String   
	 */
	public static String getTime() {
		GregorianCalendar gcNow = new GregorianCalendar();
		java.util.Date dNow = gcNow.getTime();
		return timeDF.format(dNow);
	}

	/**   
	 * ����һ����ǰ���ڣ�������ʽת��Ϊ�ַ���   
	 * ����2004-4-30   
	 * @return String   
	 */
	public static String getDate() {
		GregorianCalendar gcNow = new GregorianCalendar();
		java.util.Date dNow = gcNow.getTime();
		return dateDF.format(dNow);
	}

	/**   
	 * ����һ����ǰ���ں�ʱ�䣬������ʽת��Ϊ�ַ���   
	 * ����2004-4-30 17:27:03   
	 * @return String   
	 */
	public static String getDateTime() {
		GregorianCalendar gcNow = new GregorianCalendar();
		java.util.Date dNow = gcNow.getTime();
		return datetimeDF.format(dNow);
	}

	/**   
	 * ���ص�ǰ������   
	 * @return int   
	 */
	public static int getYear() {
		GregorianCalendar gcNow = new GregorianCalendar();
		return gcNow.get(GregorianCalendar.YEAR);
	}

	/**   
	 * ���ر����ºţ��� 0 ��ʼ   
	 * @return int   
	 */
	public static int getMonth() {
		GregorianCalendar gcNow = new GregorianCalendar();
		return gcNow.get(GregorianCalendar.MONTH);
	}

	/**   
	 * ���ؽ����Ǳ��µĵڼ���   
	 * @return int ��1��ʼ   
	 */
	public static int getToDayOfMonth() {
		GregorianCalendar gcNow = new GregorianCalendar();
		return gcNow.get(GregorianCalendar.DAY_OF_MONTH);
	}

	/**   
	 * ����һ��ʽ��������   
	 * @param date java.util.Date   
	 * @return String yyyy-mm-dd ��ʽ   
	 */
	public static String formatDate(java.util.Date date) {
		return dateDF.format(date);
	}

	/**   
	 * ����һ��ʽ��������   
	 * @param date   
	 * @return   
	 */
	public static String formatDate(long date) {
		return formatDate(new java.util.Date(date));
	}

	/**   
	 * ����һ��ʽ����ʱ��   
	 * @param date Date   
	 * @return String hh:ss:mm ��ʽ   
	 */
	public static String formatTime(java.util.Date date) {
		return timeDF.format(date);
	}

	/**   
	 * ����һ��ʽ����ʱ��   
	 * @param date   
	 * @return   
	 */
	public static String formatTime(long date) {
		return formatTime(new java.util.Date(date));
	}

	
	/**   
	 * ����һ��ʽ��������ʱ��   
	 * @param date Date   
	 * @return String yyyy-mm-dd hh:ss:mm ��ʽ   
	 */
	public static String formatDateTime(java.util.Date date) {
		return datetimeDF.format(date);
	}

	/**   
	 * ����һ��ʽ��������ʱ��   
	 * @param date   
	 * @return   
	 */
	public static String formatDateTime(long date) {
		return formatDateTime(new java.util.Date(date));
	}

	/**   
	 * ���ִ�ת�����ں�ʱ�䣬�ִ���ʽ: yyyy-MM-dd HH:mm:ss   
	 * @param string String   
	 * @return Date   
	 */
	public static java.util.Date toDateTime(String string) {
		try {
			DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			return (java.util.Date) formatter.parse(string);
		} catch (Exception ex) {
			return null;
		}
	}

	/**   
	 * ���ִ�ת�����ڣ��ִ���ʽ: yyyy/MM/dd   
	 * @param string String   
	 * @return Date   
	 */
	public static java.util.Date toDate(String string) {
		try {
			DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			return (java.util.Date) formatter.parse(string);
		} catch (Exception ex) {
			return null;
		}
	}

	/**   
	 * ȡֵ��ĳ���ڵ����   
	 * @param date ��ʽ: yyyy/MM/dd   
	 * @return   
	 */
	public static int getYear(String date) {
		java.util.Date d = toDate(date);
		if (d == null)
			return 0;

		Calendar calendar = Calendar.getInstance(locale);
		calendar.setTime(d);
		return calendar.get(Calendar.YEAR);
	}

	/**   
	 * ȡֵ��ĳ���ڵ��º�   
	 * @param date ��ʽ: yyyy/MM/dd   
	 * @return   
	 */
	public static int getMonth(String date) {
		java.util.Date d = toDate(date);
		if (d == null)
			return 0;

		Calendar calendar = Calendar.getInstance(locale);
		calendar.setTime(d);
		return calendar.get(Calendar.MONTH);
	}

	/**   
	 * ȡֵ��ĳ���ڵ��պ�   
	 * @param date ��ʽ: yyyy/MM/dd   
	 * @return ��1��ʼ   
	 */
	public static int getDayOfMonth(String date) {
		java.util.Date d = toDate(date);
		if (d == null)
			return 0;

		Calendar calendar = Calendar.getInstance(locale);
		calendar.setTime(d);
		return calendar.get(Calendar.DAY_OF_MONTH);
	}

	/**   
	 * �����������ڵ�������   
	 * @param one ��ʽ: yyyy/MM/dd   
	 * @param two ��ʽ: yyyy/MM/dd   
	 * @return   
	 */
	public static int compareYear(String one, String two) {
		return getYear(one) - getYear(two);
	}

	/**   
	 * ��������   
	 * @param date ��ʽ: yyyy/MM/dd   
	 * @return   
	 */
	public static int compareYear(String date) {
		return getYear() - getYear(date);
	}
	
	public static String returnGreenwich(Date date){
		TimeZone   tz=TimeZone.getTimeZone("Etc/Greenwich");   
        SimpleDateFormat   sdf   =   new   SimpleDateFormat("MM/dd/yyyy HH:mm:ss z");   
        sdf.setTimeZone(tz);   
        return sdf.format(date);
	}

	public static String getDateString(Date date){
		SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyyMMddHHmmss");
		String time1 = dateFormat1.format(date).toString();
		return time1;
	}
	
}
