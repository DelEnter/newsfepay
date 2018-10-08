package com.ecpss.action.pay.util;

public class CheckCardNo {
	/**
 	 * ��֤���ÿ�����
 	 * @return
 	 */
	 public static boolean isValid (String cardNumber) {
		 	//sum���ܺ�
		    int sum = 0;
		    //digit�����ŵ�ÿһ��������ʲô
		    int digit = 0;
		    //С��...ÿ����������������ż��,,,������ֱ�Ӽӽ�sum,ż��*2�ж��Ƿ����10  ����10��-9
		    int subtotal = 0;
		    //��һ��boolean���ж����������������������ż��
		    boolean flag = false;
		    for (int i = cardNumber.length()-1; i >= 0; i--) {
		      digit = Integer.parseInt(cardNumber.substring(i, i + 1));
		      if (flag) {
		    	  subtotal = digit * 2;
		        if (subtotal > 9) {
		        	subtotal -= 9;
		        }
		      }else {
		    	  subtotal = digit;
		      }
		      sum += subtotal;
		      flag = !flag;
		    }
		    if(sum%10==0){
		    	return true;
		    }else{
		    	return false;
		    }
	 }
	 public static void main(String[] args) {
		 CheckCardNo c = new CheckCardNo();
		 System.out.println(c.isValid("4111111111111111"));  // AE
//		 System.out.println(c.isValid("3566002020360505"));  // jcb
//		 System.out.println(c.isValid("30000000000004"));  // jcb
 	}
}
