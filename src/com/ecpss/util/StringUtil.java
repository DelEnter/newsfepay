package com.ecpss.util;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import java.util.*;

import java.util.ArrayList;

import java.util.regex.Matcher;

import java.util.regex.Pattern;

import java.net.URLDecoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;

import java.text.DateFormat;


public class StringUtil {
	
	/**
	 * 
	 * ɾ���ض��ַ�
	 */
	 public static String deleteAll(String source,char oldc)
     {
           StringBuffer sbu = new StringBuffer();
           //�ַ����ĳ���
          int lenOfsource=source.length();
          //ָ���ַ����ַ����е�λ��
          int i;
          //��ָ��λ�ÿ�ʼѰ��
           int posStart;
           //int indexOf(char ch,int intStart)
          //���ch���ַ����е�λ�ô���intStart
           //����ʵ�ʵ�λ��
           //���򷵻�0����-1
           //�˷���ԭ��:
           //���ַ����ĵ�һ��λ�ÿ�ʼ����
          //����ҵ�oldc,�ͽ�ȡ��posStart��ʼ��i�������ַ���
            //substring(int begin,int end)����������ʱ���ַ�
          //������ɾ���˵�һ���ҵ���ָ���ַ�
          //���ں����ָ���ַ��϶��ڵ�һ���ҵ���ָ���ַ�����
          //�����ٶ�ָ��λ��posStart�ڵ�һ���ҵ���λ���ϼ�1
           //posStart=i+1
           for(posStart=0;(i=source.indexOf(oldc,posStart))>=0;posStart=i+1)
          {
                   sbu.append(source.substring(posStart,i));
            }
 
           //������һ��ѭ��ɾ�������е�ָ���ַ�
            //������ڴ��ַ����滹���ַ���
            //��ҲҪ��ȡ��
            //��ʱposStart��ֵ�����һ��ָ���ַ���λ��+1
            //���posStartС���ַ�������,��϶�����δ��ӵ��ַ���
           //�����ټ���һ���ж�
            if(posStart<lenOfsource)
             {
                         sbu.append(source.substring(posStart));
             }
 
             return sbu.toString();
 
      }
	
	
	/** �չ���*/
	private StringUtil() {
	}
	
	/**
	 * ��ȡ�пո�Ĳ���ȥ���ո�
	 */
	
	public static String removeNullString(String value){
		String string = null;
		if(value==null){
			string = null;
		}else if(value!=null){
			StringTokenizer statSt = new StringTokenizer(value+""," ");
			while(statSt.hasMoreTokens()){		
				string = statSt.nextToken();
			}
		}
		return string;
	}
	
	/*public static void main(String args[]){
	
		System.out.println("----------"+removeNullString("d df"));
	}*/
	
	/**  
	 
	 * ��һ���ִ�������ĸ��д  
	 
	 * @param s String Դ�ִ�  
	 
	 * @return String ����ĸ��д����ִ�  
	 
	 */

	public static String toUpperCaseFirstLetter(String s) {

		return isNullStr(s)

		? s

		: s.substring(0, 1).toUpperCase() + s.substring(1);

	}

	/**  
	 
	 * �ѿ��ַ���ת��Ϊempty   
	 
	 * @param s  
	 
	 * @return  
	 
	 * @deprecated  
	 
	 * @see getNotNullStr  
	 
	 */

	public static final String nullToEmptyOfStr(String s) {

		if (s != null)

			return s.trim();

		else

			return "";

	}


	/**  
	 
	 * ȡָ���ַ�����ָ���������ִ�  
	 
	 * @param strAll  
	 
	 * @param strLen  
	 
	 * @return  
	 
	 */

	public static final String subStr(String strAll, int strLen) {

		String strNew = nullToEmptyOfStr(strAll);

		String myStr = "";

		if (strNew.length() >= strLen) {

			myStr = strNew.substring(0, strLen);

		} else {

			myStr = strNew;

		}

		return myStr;

	}

	


	//�ַ����滻 s �����ַ��� s1 Ҫ�����ַ��� s2 Ҫ�滻�ַ���    
	public static String replace(String s, String s1, String s2) {

		if (s == null)

			return null;

		int i = 0;

		if ((i = s.indexOf(s1, i)) >= 0) {

			char ac[] = s.toCharArray();

			char ac1[] = s2.toCharArray();

			int j = s1.length();

			StringBuffer stringbuffer = new StringBuffer(ac.length);

			stringbuffer.append(ac, 0, i).append(ac1);

			i += j;

			int k;

			for (k = i; (i = s.indexOf(s1, i)) > 0; k = i) {

				stringbuffer.append(ac, k, i - k).append(ac1);

				i += j;

			}

			stringbuffer.append(ac, k, ac.length - k);

			return stringbuffer.toString();

		} else {

			return s;

		}

	}

	
	

	/**  
	 
	 * convert Array to ArrayList  
	 
	 * @param sz String[]  
	 
	 * @param len int  
	 
	 * @return ArrayList  
	 
	 */

	public static ArrayList getArrayListFromArray(String[] sz, int len) {

		ArrayList aTmp = new ArrayList();

		if (isNullStr(sz)) {

			for (int i = 0; i < len; i++) {

				aTmp.add("");

			}

		} else {

			for (int i = 0; i < sz.length; i++) {

				aTmp.add(sz[i]);

			}

		}

		return aTmp;

	}

	/**  
	 
	 * Convert to int Base200312291149  
	 
	 * @param o Object  
	 
	 * @return int  
	 
	 */

	public static int cNum(Object o) {

		try {

			return Integer.parseInt(cStr(o));

		} catch (Exception ex) {

			return 0;

		}

	}

	/**  
	 
	 * Convert to String from object Base200312291148  
	 
	 * @param o Object  
	 
	 * @return String  
	 
	 */

	public static String cStr(Object o) {

		return o == null ? "" : o.toString();

	}

	/**  
	 
	 * �жϴ��ַ����Ƿ�Ϊ�ա����ַ�������"null"  
	 
	 * @param str  
	 
	 * @return  
	 
	 */

	public static boolean isNullStr(String s) {

		return (s == null || s.equals("null") || s.equals("")) ? true : false;

	}

	/**  
	 
	 * ����ַ���Ϊ�ա����ַ�������"null"ʱ����"0"  
	 
	 * @param str  
	 
	 * @return String  
	 
	 */

	public static String emptyToZero(String str) {

		if (isNullStr(str))

			return "0";

		else

			return str.trim();

	}

	/**  
	 
	 * �жϴ��ַ����Ƿ�Ϊ�ա����ַ�������"null"  
	 
	 * @param str  
	 
	 * @return  
	 
	 */

	public static boolean isNullStr(Object o) {

		return (

		o == null

		|| o.toString().equals("null")

		|| o.toString().equals(""))

		?

		//return (o==null||o.toString().equals(""))?   

		true
				: false;

	}

	/**  
	 
	 * ����ַ���strΪ����ת��Ϊstr1  
	 
	 * @param str  
	 
	 * @param str1  
	 
	 * @return  
	 
	 */

	public static String getNullStr(String str, String str1) {

		if (isNullStr(str))

			return str1;

		else

			return str;

	}

	/**  
	 
	 * ���ַ���strת��ΪGBK�����ʽ  
	 
	 * @param str  
	 
	 * @return  
	 
	 */

	public static String getGBKStr(String str) {

		try {

			String temp_p;

			temp_p = str;

			//temp_p = getNullStr(temp_p, "");    

			temp_p = nullToEmptyOfStr(temp_p);

			byte[] temp_t = temp_p.getBytes("ISO8859_1");

			String temp = new String(temp_t, "GBK");

			return temp;

		} catch (Exception e) {

			return "";

		}

	}

	public static String getISOStr(String str) {

		try {

			String temp_p;

			temp_p = str;

			temp_p = getNullStr(temp_p, "");

			byte[] temp_t = temp_p.getBytes("ISO8859_1");

			String temp = new String(temp_t);

			return temp;

		} catch (Exception e) {
		}

		return "null";

	}

	/**  
	 
	 * ���ִ�ת�����ڣ��ִ���ʽ: yyyy/MM/dd  
	 
	 * @author Base200312111725  
	 
	 * @param string String  
	 
	 * @return Date  
	 
	 */

	public static Date toDate(String string) {

		try {

			DateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");

			return (Date) formatter.parse(string);

		} catch (Exception ex) {

			System.err.println(

			"[ com.netgate.web.util.StringUtil.toDate(string) ]"

			+ ex.getMessage());

			return null;

		}

	}

	/**  
	 
	 * ���ִ�ת�����ں�ʱ�䣬�ִ���ʽ: yyyy/MM/dd HH:mm:ss  
	 
	 * @author Base200312111726  
	 
	 * @param string String  
	 
	 * @return Date  
	 
	 */

	public static Date toDatetime(String string) {

		try {

			DateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

			return (Date) formatter.parse(string);

		} catch (Exception ex) {

			System.err.println(

			"[ com.netgate.web.util.StringUtil.toDatetime(string) ]"

			+ ex.getMessage());

			return null;

		}

	}

	/**  
	 
	 * �жϵ�ѡ���Ƿ�ѡ��  
	 
	 * @author tempnc20031222  
	 
	 * @param inparam  
	 
	 * @param val  
	 
	 * @return �Ƿ�ѡ��  
	 
	 */

	public static String isChecked(String inparam, String val) {

		try {

			if (inparam.trim().equals(val.trim()))

				return "checked";

			else

				return "";

		} catch (Exception ex) {

			System.err.println(

			"[ com.netgate.web.util.StringUtil.toDatetime(string) ]"

			+ ex.getMessage());

			return null;

		}

	}

	/**  
	 
	 * �˷������������ַ���sourceʹ��delim����Ϊ�������顣  
	 
	 * @param source ��Ҫ���л��ֵ�ԭ�ַ���  
	 
	 * @param delim ���ʵķָ��ַ���  
	 
	 * @return �����Ժ�����飬���sourceΪnull��ʱ�򷵻���sourceΪΨһԪ�ص����飬  
	 
	 *         ���delimΪnull��ʹ�ö�����Ϊ�ָ��ַ�����  
	 
	 * @since  0.1  
	 
	 */

	public static String[] split(String source, String delim) {

		String[] wordLists;

		if (source == null) {

			wordLists = new String[1];

			wordLists[0] = source;

			return wordLists;

		}

		if (delim == null) {

			delim = ",";

		}

		StringTokenizer st = new StringTokenizer(source, delim);

		int total = st.countTokens();

		wordLists = new String[total];

		for (int i = 0; i < total; i++) {

			wordLists[i] = st.nextToken();

		}

		return wordLists;

	}

	/**  
	 
	 * �˷������������ַ���sourceʹ��delim����Ϊ�������顣  
	 
	 * @param source ��Ҫ���л��ֵ�ԭ�ַ���  
	 
	 * @param delim ���ʵķָ��ַ�  
	 
	 * @return �����Ժ�����飬���sourceΪnull��ʱ�򷵻���sourceΪΨһԪ�ص����顣  
	 
	 * @since  0.2  
	 
	 */

	public static String[] split(String source, char delim) {

		return split(source, String.valueOf(delim));

	}

	/**  
	 
	 * �˷������������ַ���sourceʹ�ö��Ż���Ϊ�������顣  
	 
	 * @param source ��Ҫ���л��ֵ�ԭ�ַ���  
	 
	 * @return �����Ժ�����飬���sourceΪnull��ʱ�򷵻���sourceΪΨһԪ�ص����顣  
	 
	 * @since  0.1  
	 
	 */

	public static String[] split(String source) {

		return split(source, ",");

	}

	/**  
	 
	 * ��set�����м�¼����һ���� delim �ָ����ַ���  
	 
	 * @param set  
	 
	 * @param delim  
	 
	 * @return  
	 
	 */

	public static String combine(Set set, String delim) {

		if (set == null || set.size() == 0) {

			return "";

		}

		if (delim == null) {

			delim = "";

		}

		StringBuffer sb = new StringBuffer(100);

		for (Iterator iter = set.iterator(); iter.hasNext();) {

			sb.append(iter.next());

			sb.append(delim);

		}

		if (sb.length() >= delim.length()) {

			sb.delete(sb.length() - 1 - delim.length(), sb.length() - 1);

		}

		return sb.toString();

	}

	/**  
	 
	 * ��set�����м�¼����һ���� , �ָ����ַ���  
	 
	 * @param set  
	 
	 * @param delim  
	 
	 * @return  
	 
	 */

	public static String combine(Set set) {

		return combine(set, ",");

	}

	/**  
	 
	 * ���ַ�������ϲ���һ���� delim �ָ����ַ���  
	 
	 * @param array �ַ�������  
	 
	 * @param delim �ָ�����Ϊnull��ʱ��ʹ��""��Ϊ�ָ�������û�зָ�����  
	 
	 * @return �ϲ�����ַ���  
	 
	 */

	public static String combineArray(String[] array, String delim) {

		if (array == null || array.length == 0) {

			return "";

		}

		int length = array.length - 1;

		if (delim == null) {

			delim = "";

		}

		StringBuffer result = new StringBuffer(length * 8);

		for (int i = 0; i < length; i++) {

			result.append(array[i]);

			result.append(delim);

		}

		result.append(array[length]);

		return result.toString();

	}

	/**  
	 
	 * ���ַ�������ϲ���һ����,�ŷָ����ַ���  
	 
	 * @param array �ַ�������  
	 
	 * @return �ϲ�����ַ���  
	 
	 */

	public static String combineArray(String[] array) {

		return combineArray(array, ",");

	}

	/**  
	 
	 * ���ַ�������ʹ��ָ���ķָ����ϲ���һ���ַ�����  
	 
	 * @param array �ַ�������  
	 
	 * @param delim �ָ�����Ϊnull��ʱ��ʹ��""��Ϊ�ָ�������û�зָ�����  
	 
	 * @return �ϲ�����ַ���  
	 
	 * @deprecated  
	 
	 */

	public static String combineStringArray(String[] array, String delim) {

		return combineArray(array, delim);

	}

	/**  
	 
	 * ��int����ʹ��ָ���ķָ����ϲ���һ���ַ���  
	 
	 * @param array int[] int ����  
	 
	 * @param delim String �ָ�����Ϊnull��ʱ��ʹ��""��Ϊ�ָ�������û�зָ�����  
	 
	 * @return String �ϲ�����ַ���  
	 
	 */

	public static String combineArray(int[] array, String delim) {

		if (array == null || array.length == 0) {

			return "";

		}

		int length = array.length - 1;

		if (delim == null) {

			delim = "";

		}

		StringBuffer result = new StringBuffer();

		for (int i = 0; i < length; i++) {

			result.append(Integer.toString(array[i]));

			result.append(delim);

		}

		result.append(Integer.toString(array[length]));

		return result.toString();

	}

	/**  
	 
	 * ��int����ϲ���һ����,�ŷָ����ַ���  
	 
	 * @param array �ַ�������  
	 
	 * @return �ϲ�����ַ���  
	 
	 */

	public static String combineArray(int[] array) {

		return combineArray(array, ",");

	}

	/**  
	 
	 * ���ַ���Listʹ��ָ���ķָ����ϲ���һ���ַ�����  
	 
	 * @param list List  
	 
	 * @param delim String  
	 
	 * @return String  
	 
	 */

	public static String combineList(List list, String delim) {

		if (list == null || list.size() == 0) {

			return "";

		} else {

			StringBuffer result = new StringBuffer();

			for (int i = 0; i < list.size() - 1; i++) {

				result.append(list.get(i));

				result.append(delim);

			}

			result.append(list.get(list.size() - 1));

			return result.toString();

		}

	}

	/**  
	 
	 * ���ַ���Listʹ�� , �ϲ���һ���ַ�����  
	 
	 * @param list List  
	 
	 * @return String  
	 
	 */

	public static String combineList(List list) {

		return combineList(list, ",");

	}

	/**  
	 
	 * ���ַ���Listʹ��ָ���ķָ����ϲ���һ���ַ�����  
	 
	 * @param list List  
	 
	 * @param delim String  
	 
	 * @return String  
	 
	 * @deprecated see combineList(list, delim)  
	 
	 */

	public static String combineStringList(List list, String delim) {

		return combineList(list, delim);

	}

	/**  
	 
	 * ��ָ�����ַ��ͳ�������һ�����ַ���ָ�����ȵ��ַ�����  
	 
	 * @param c ָ�����ַ�  
	 
	 * @param length ָ���ĳ���  
	 
	 * @return �������ɵ��ַ���  
	 
	 * @since  0.6  
	 
	 */

	public static String fillString(char c, int length) {

		String ret = "";

		for (int i = 0; i < length; i++) {

			ret += c;

		}

		return ret;

	}

	/**  
	 
	 * �ַ����������Ƿ����ָ�����ַ�����  
	 
	 * @param strings �ַ�������  
	 
	 * @param string �ַ���  
	 
	 * @param caseSensitive �Ƿ��Сд����  
	 
	 * @return ����ʱ����true�����򷵻�false  
	 
	 * @since  0.4  
	 
	 */

	public static boolean contains(String[] strings, String string,

	boolean caseSensitive) {

		for (int i = 0; i < strings.length; i++) {

			if (caseSensitive == true) {

				if (strings[i].equals(string)) {

					return true;

				}

			} else {

				if (strings[i].equalsIgnoreCase(string)) {

					return true;

				}

			}

		}

		return false;

	}

	/**  
	 
	 * �ַ����������Ƿ����ָ�����ַ�������Сд���С�  
	 
	 * @param strings �ַ�������  
	 
	 * @param string �ַ���  
	 
	 * @return ����ʱ����true�����򷵻�false  
	 
	 * @since  0.4  
	 
	 */

	public static boolean contains(String[] strings, String string) {

		return contains(strings, string, true);

	}

	/**  
	 
	 * �����ִ�Сд�ж��ַ����������Ƿ����ָ�����ַ�����  
	 
	 * @param strings �ַ�������  
	 
	 * @param string �ַ���  
	 
	 * @return ����ʱ����true�����򷵻�false  
	 
	 * @since  0.4  
	 
	 */

	public static boolean containsIgnoreCase(String[] strings, String string) {

		return contains(strings, string, false);

	}

	/**  
	 
	 * �õ��ַ������ֽڳ���  
	 
	 * @param source �ַ���  
	 
	 * @return �ַ������ֽڳ���  
	 
	 * @since  0.6  
	 
	 */

	public static int getByteLength(String source) {

		int len = 0;

		for (int i = 0; i < source.length(); i++) {

			char c = source.charAt(i);

			int highByte = c >>> 8;

			len += highByte == 0 ? 1 : 2;

		}

		return len;

	}

	/**  
	 
	 * �ж��ַ��Ƿ�Ϊ˫�ֽ��ַ���������  
	 
	 * @param c char  
	 
	 * @return boolean  
	 
	 */

	public static boolean isDoubleByte(char c) {

		return !((c >>> 8) == 0);

	}

	/**  
	 
	 * ����̶��ֽڳ��ȵ��ַ���  
	 
	 * @param source String  
	 
	 * @param len int  
	 
	 * @param exChar String  
	 
	 * @param exStr String  
	 
	 * @return String  
	 
	 */

	public static String getSubStr(String source, int len, String exChar,

	String exStr) {

		if (source == null || getByteLength(source) <= len) {

			return source;

		}

		StringBuffer result = new StringBuffer();

		char c = '\u0000';

		int i = 0, j = 0;

		for (; i < len; j++) {

			result.append(c);

			c = source.charAt(j);

			i += isDoubleByte(c) ? 2 : 1;

		}

		/**  
		 
		 * ������i���������������len����len+1�������len+1��˵����˫�ֽڣ������һ���ֽ�  
		 
		 * ��ʱ���ֻ��append(exChar)�������append(c)  
		 
		 */

		if (i > len) {

			result.append(exChar);

		} else {

			result.append(c);

		}

		result.append(exStr);

		return result.toString();

	}

	public static String getSubStr(String source, int len) {

		return getSubStr(source, len, ".", "...");

	}

	/**  
	 
	 * �ж���������Ƿ�Ϊnull����һ����null��ֵ  
	 
	 * @param s String �жϵ�ֵ  
	 
	 * @return String  
	 
	 */

	public static String getNotNullStr(String s) {

		return (s == null) ? "" : s.trim();

	}

	

	/**  
	 
	 * ���ַ����ĵ�һ����ĸ��д  
	 
	 * @param s String  
	 
	 * @return String  
	 
	 */

	public static String firstCharToUpperCase(String s) {

		if (s == null || s.length() < 1) {

			return "";

		}

		char[] arrC = s.toCharArray();

		arrC[0] = Character.toUpperCase(arrC[0]);

		return String.copyValueOf(arrC);

		/*  
		 
		 char c = s.charAt(0);  
		 
		 c = Character.toUpperCase(c);  
		 
		 return Character.toString(c) + s.substring(1);  
		 
		 */

	}

	/**  
	 
	 * ���ݵ�ǰ�ֽڳ���ȡ�����ϵ�ǰ�ֽڳ��Ȳ���������ֽڳ��ȵ��Ӵ�  
	 
	 * @param str  
	 
	 * @param currentLen  
	 
	 * @param MAX_LEN  
	 
	 * @return  
	 
	 */

	public static String getSubStr(String str, int currentLen, int MAX_LEN) {

		int i;

		for (i = 0; i < str.length(); i++) {

			if (str.substring(0, i + 1).getBytes().length + currentLen > MAX_LEN) {

				break;

			}

		}

		if (i == 0) {

			return "";

		} else {

			return str.substring(0, i);

		}

	}

	private static String[] arrAntiKeys = new String[] { "����", "�ջ�", "����",
			"ʾ��", "����", "���㵺", "����", "���羯��", "WWTTJJLL" };

	/**  
	 
	 * �ؼ����滻��??  
	 
	 * @param keys  
	 
	 * @param arg  
	 
	 * @return  
	 
	 */

	public static String replaceByKeys(String[] keys, String arg) {

		String sbf = arg;

		String[] getarrAntiKeys = arrAntiKeys;

		if ((keys != null) && (keys.length > 0))
			getarrAntiKeys = keys;

		for (int i = 0; i < getarrAntiKeys.length; i++) {

			sbf = sbf.replaceAll(getarrAntiKeys[i], "??");

		}

		return sbf;

	}

	/**  
	 
	 * �ؼ���ɾ��  
	 
	 * @param parm  
	 
	 * @return  
	 
	 */

	public static String unicodeReplDel(String parm) {

		int area1min = 9601;

		int area1max = 9794;

		int area2min = 12288;

		int area2max = 12311;

		StringBuffer result = new StringBuffer("");

		if (parm.trim().length() > 0) {

			for (int i = 0; i < parm.length(); i++) {

				char c = parm.charAt(i);

				if (!(((int) c >= area1min && (int) c <= area1max) || ((int) c >= area2min && (int) c <= area2max))) {

					result.append(c);

				}

			}

		}

		return result.toString();

	}
	
	
	/**
	 * �ָ��ַ���
	 * @param str Ҫ�ָ���ַ���
	 * @param spilit_sign �ַ����ķָ��־
	 * @return �ָ��õ����ַ�������
	 */
	public static String[] stringSpilit(String str, String spilit_sign) {
		String[] spilit_string = str.split(spilit_sign);
		if (spilit_string[0].equals("")) {
			String[] new_string = new String[spilit_string.length - 1];
			for (int i = 1; i < spilit_string.length; i++)
				new_string[i - 1] = spilit_string[i];
			return new_string;
		} else
			return spilit_string;
	}
	private static String[] FilterChars = {"<","'",">",","," ","%"};
	
	
	/**
	 * ��֤�Ƿ��ַ�
	 * @return
	 */
	public static boolean validateIllegal(String s){
		for (int j = 0; j < FilterChars.length; j++) {
			int i = s.indexOf(FilterChars[j]);
			if(i>=0){
				return false;
			}
		}
//		String[] str_arr = stringSpilit(s, "");
//		String temp;
//		for (int i = 0; i < str_arr.length; i++) {
//			for (int j = 0; j < FilterChars.length; j++) {
//				if(FilterChars[j].length() > 1){
//					s.substring(0, FilterChars[j].length()-1);
//				}
//				if(FilterChars[j].equals(str_arr[i])){
//					//����false��ʾ�зǷ��ַ�
//					return  false;
//				}
//			}
//		}
		return true;
	}
	
	public static void main(String[] args) {
		
		if(!StringUtil.validateIllegal("aaaaa'")){
			System.out.println("aaa");
		}else{
			System.out.println("bbb");
		}
	}
	/**
	 * ��֤�Ƿ��зǷ��ַ�
	 * @param s
	 * @return
	 */
	public static boolean validateStr(String s){
		if(s.replaceAll("[a-z]*[A-Z]*\\d*-*_*\\s*", "").length()==0){
		  return true;
		}
		else{
		  return false;
		}
	}
	public static Map createMapFromResponse(String queryString) {
		Map map = new HashMap();
		StringTokenizer st = new StringTokenizer(queryString, "&");
		while (st.hasMoreTokens()) {
			String token = st.nextToken();
			int i = token.indexOf('=');
			if (i > 0) {
				try {
					String key = token.substring(0, i);
					String value = URLDecoder.decode(token.substring(i + 1,
							token.length()));
					map.put(key, value);
				} catch (Exception ex) {
					// Do Nothing and keep looping through data
				}
			}
		}
		return map;
	}
	public static String Md5(String plainText ) { 
		String tem="";
		try { 
		MessageDigest md = MessageDigest.getInstance("MD5"); 
		md.update(plainText.getBytes()); 
		byte b[] = md.digest(); 

		int i; 

		StringBuffer buf = new StringBuffer(""); 
		for (int offset = 0; offset < b.length; offset++) { 
		i = b[offset]; 
		if(i<0) i+= 256; 
		if(i<16) 
		buf.append("0"); 
		buf.append(Integer.toHexString(i)); 
		} 

		tem= buf.toString();
		
//		System.out.println("result: " + buf.toString());//32λ�ļ��� 
//
//		System.out.println("result: " + buf.toString().substring(8,24));//16λ�ļ��� 

		} catch (NoSuchAlgorithmException e) { 
		// TODO Auto-generated catch block 
		e.printStackTrace(); 
		} 
		
		return tem;
		} 
}
