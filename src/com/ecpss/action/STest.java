package com.ecpss.action;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

import com.ecpss.util.MD5;

public class STest {
	public static void main(String[] args) {
		
		
		//MD5 md5 = new MD5();
		//String a = md5.getMD5ofStr("5004213720821025.01enhttps://securepayments.telemoneyworld.com/easypay2/wedopayendmessage.do]rOHEFAd");
		//System.out.println(a);
		
	    String MD5key; //MD5keyֵ
	    MD5key = "]rOHEFAd";

	    String MerNo;   //�̻�ID
	    MerNo = "50042";

	    String BillNo;  //�������
	    BillNo = "1372082";
	    String Currency;    //����
	    Currency = "10";
	    String Amount;  //֧�����
	    Amount = "25.01";

	    String Language;    //֧������
	    Language = "en";

	    String ReturnURL;   //���ص�ַ
	    
	    ReturnURL = "https://securepayments.telemoneyworld.com/easypay2/wedopayendmessage.do";

	    String md5src;  //�����ַ���    
	    md5src = MerNo + BillNo + Currency + Amount + Language + ReturnURL + MD5key ;
	    MD5 md5 = new MD5();
	    String MD5info; //MD5���ܺ���ַ���
	    MD5info = md5.getMD5ofStr(md5src);
	    
	    
	    //String returnurl="http://security.ecpss.com/merchantToPaymentGateway";
		String returnurl="https://secure.wedopay.net/straightLinePay";
	    //String returnurl="http://192.168.2.102:8888/wedopay/straightLinePay";
		//----------------���̻���վpost����-----------
		URL url;
		System.out.println("post��ַ+++++++++++"+returnurl);
		try {
			url = new URL(returnurl);
		
			URLConnection connection = url.openConnection(); 
			connection.setDoOutput(true); 
			OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream(), "8859_1");
			String parte = "cardnum="+"4000000000000002"+
			"&cvv2="+"156"+
			"&month="+"05"+
			"&year="+"12"+
			"&cardbank="+"bank of china"+
			"&Amount="+Amount+
			
			"&Currency="+Currency+
			"&MerNo="+MerNo+
			"&BillNo="+BillNo+
			"&email="+"89610614@qq.com"+
			"&Language="+Language+
			"&MD5info="+MD5info+
			"&ReturnURL="+ReturnURL;
			
//			String poststr = "OType="+"001"+
//			"&PayOrderNo="+"1685114296382642645"+
//			"&MerchantOrderNo="+"315190521"+
//			"&Amount="+"0.01"+
//			"&RefundAmount="+"0.01"+
//			"&ReturnURL="+ReturnURL;
			
			out.write(parte); //������֯�ύ��Ϣ 
			out.flush(); 
			out.close(); 
			//��ȡ�������� 
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream())); 
			String line = null; 
			StringBuffer content= new StringBuffer(); 
			while((line = in.readLine()) != null) 
			{ 
			   //lineΪ����ֵ����Ϳ����ж��Ƿ�ɹ��� 
			    content.append(line);
			    System.out.println(content);
			} 
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		
		
		
		
//
//		String socket_ip = "192.168.2.101";
//		int socket_port = 5000;
//		// socket������ip��ַ
//		// socket�����������˿�
//		Socket socket = null;
//		BufferedReader in = null;
//		PrintWriter out = null;
//		// ������
//
//		String answerMSG = "";
//		try {
//			socket = new Socket(socket_ip, socket_port);
//			if (socket.isConnected()) {
//				in = new BufferedReader(new InputStreamReader(socket
//						.getInputStream()));
//				out = new PrintWriter(new BufferedWriter(
//						new OutputStreamWriter(socket.getOutputStream())), true);
//				// ����������
//				out.println("http://www.google.com.hk");
//				// ����Ӧ����
//				//answerMSG = in.readLine();
//			}
//		} catch (UnknownHostException e) {
//			// System.err.println("δ֪������λ�ã�" + socket_ip);
//			// e.printStackTrace();
//		} catch (IOException e) {
//			// System.err.println("IO�����쳣");
//			// e.printStackTrace();
//		} finally {
//			try {
//				in.close();
//				out.close();
//				socket.close();
//			} catch (IOException e) {
//				// e.printStackTrace();
//			}
//			
//		}
//		
//		System.err.println("δ֪������λ�ã�" + socket_ip);
//		System.out.println("ִ�����...");
	}
}
