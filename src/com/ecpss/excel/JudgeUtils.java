/**
 *
 */
package com.ecpss.excel;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

/**
 * <b>������</b>һЩ�жϷ���
 * @author yepeng
 * Jun 21, 2008
 */
public class JudgeUtils {
	/**
	 * value�ǲ��ǿ�String==" ",Collect.size=0,Array.length==0,Map.size==0
	 * @param value
	 * @return
	 */
	public static boolean isEmpty(Object value){
		if(value == null){
			return true;
		}
		if(value instanceof String && ((String)value).trim().length() == 0){
			return true;
		}else if(value instanceof Collection &&((Collection)value).size() == 0){
			return true;
		}else if(value.getClass().isArray() && Array.getLength(value) == 0){
			return true;
		}else if(value instanceof Map &&((Map)value).size() == 0){
			return true;
		}
		return false;
	}

	/**
	 * value��Ϊ��
	 * @param value
	 * @return
	 */
	public static boolean isNotEmpty(Object value){
		return !isEmpty(value);
	}
	/**
	 * value Ϊ null
	 * @param value
	 * @return
	 */
	public static boolean isNull(Object value){
		return value == null;
	}
	/**
	 * value ��Ϊnull
	 * @param value
	 * @return
	 */
	public static boolean isNotNull(Object value){
		return !isNull(value);
	}
}
