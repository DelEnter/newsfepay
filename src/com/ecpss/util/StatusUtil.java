package com.ecpss.util;
//״ֵ̬�Ļ�ȡ�����
public class StatusUtil {
	/*
	 * ��ȡ״ֵ̬ resource ״̬ index ռλ
	 */
	public static String getStatus(String resource, int index) {
		String str = resource.charAt(index-1) + "";
		return str;
	}

	/*
	 * ����״̬ resource ԭ״̬��index ռλ �� updateStatus ���µ�״̬
	 * 
	 */
	public static String updateStatus(String resource, int index,
			String updateStatus) {
		char[] st = resource.toCharArray();
		st[index - 1] = updateStatus.toCharArray()[0];
		return String.valueOf(st);
	}
}
