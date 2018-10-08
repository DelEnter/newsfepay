package com.ecpss.util;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class AES {

	private static String rawKey = "54a0902703a0a6f347d4d63d879a0bab";

	/**
	 * AES
	 */

	private final static String algorithm = "AES";

	/*
	 * תΪʮ������
	 */
	private static String asHex(byte buf[]) {
		StringBuffer strbuf = new StringBuffer(buf.length * 2);
		int i;

		for (i = 0; i < buf.length; i++) {
			if (((int) buf[i] & 0xff) < 0x10)
				strbuf.append("0");

			strbuf.append(Long.toString((int) buf[i] & 0xff, 16));
		}
		System.out.println(strbuf.toString());
		return strbuf.toString();
	}

	/*
	 * תΪ������
	 */
	private static byte[] asBin(String src) {
		if (src.length() < 1)
			return null;
		byte[] encrypted = new byte[src.length() / 2];
		for (int i = 0; i < src.length() / 2; i++) {
			int high = Integer.parseInt(src.substring(i * 2, i * 2 + 1), 16);
			int low = Integer.parseInt(src.substring(i * 2 + 1, i * 2 + 2), 16);

			encrypted[i] = (byte) (high * 16 + low);
		}
		return encrypted;
	}

	/*
	 * �õ���Կkey
	 */
	public static String getRawKey() {

		try {
			KeyGenerator kgen = KeyGenerator.getInstance(algorithm);// ��ȡ�ܳ�������
			kgen.init(256); // 192 and 256 bits may not be available

			SecretKey skey = kgen.generateKey();// �����ܳף����ö��ַ����������ܳ�
			byte[] raw = skey.getEncoded();
			return asHex(raw);
		} catch (Exception e) {
			return "";
		}

	}

	/*
	 * �����Ž��м���
	 */
	public static String setCarNo(String message) {

		return getEncrypt(message, rawKey);
	}

	/*
	 * �����Ž��н���
	 */

	public static String getCarNo(String message) {

		return getDecrypt(message, rawKey);
	}

	/*
	 * ����
	 */
	public static String getEncrypt(String message, String rawKey) {
		byte[] key = asBin(rawKey);
		try {
			SecretKeySpec skeySpec = new SecretKeySpec(key, algorithm);

			Cipher cipher = Cipher.getInstance(algorithm);// ����������

			cipher.init(Cipher.ENCRYPT_MODE, skeySpec);

			byte[] encrypted = cipher.doFinal(message.getBytes());

			return asHex(encrypted);
		} catch (Exception e) {
			return "";
		}

	}

	/*
	 * ����
	 */
	public static String getDecrypt(String encrypted, String rawKey) {
		byte[] tmp = asBin(encrypted);
		byte[] key = asBin(rawKey);

		try {
			SecretKeySpec skeySpec = new SecretKeySpec(key, algorithm);

			Cipher cipher = Cipher.getInstance(algorithm);

			cipher.init(Cipher.DECRYPT_MODE, skeySpec);

			byte[] decrypted = cipher.doFinal(tmp);

			return new String(decrypted);
		} catch (Exception e) {
			return "";
		}

	}

	public static void main(String[] args) throws Exception {

		String message = "4000000000000002";

		 String rawKey = getRawKey(); // �õ�Կ��
//		String rawKey = "54a0902703a0a6f347d4d63d879a0bab";
		System.out.println("Key = " + rawKey);

		String encrypted = getEncrypt(message, rawKey);
		System.out.println("org text = " + message);// ԭ����
		System.out.println("encrypted = " + encrypted);// �����Ժ�

		String decrypted = getDecrypt(encrypted, rawKey);// ���ܴ�

		System.out.println("decrypted = " + decrypted);

	}
}
