package com.ecpss.util;

import java.util.Date;

import com.ecpss.model.payment.InternationalTradeinfo;
import com.ecpss.model.shop.InternationalMerchant;

public class EmailInfo {
	
	/**
	 * ���֧���ɹ������ʼ�����
	 * @return
	 */
	public  String getPaymentResultEmail(String cardholderEmail,Double ordercount,String currency,
			String tradeUrl,Date tradeDate,String billadd,String merchantorderno,String orderno,InternationalMerchant merchant){
		StringBuffer setText = new StringBuffer();
		setText.append("The system e-mail,Please do not reply");
		setText.append(" \n\n\n  ");
		setText.append("Dear " + cardholderEmail+" , \n  ");
		setText.append("Thank you for choosing \" "+tradeUrl+" \" to purchase on "+ DateUtil.returnGreenwich(tradeDate) + " and your transaction amount is " + ordercount +" "+ currency + ". \n");
		setText.append("The actual amount is subject to the bill."+"\n");
		setText.append("The charge will appear on your credit card statement as payment to "+billadd +". \n\n");
		//setText.append("You have consumed " + ordercount +" "+ currency + " on \" "+ tradeUrl +" \"on "+DateUtil.returnGreenwich(tradeDate)+". "+"This payment is for your merchant \" "+tradeUrl+" \"  \n ");
		//setText.append("Attention:The charge will appear on your credit card statement as payment to "+billadd+". "+"\n\n ");
		
		setText.append("Order details: \n ");	
		setText.append("Merchant Order No. :  " + merchantorderno+"\n ");				
		setText.append("Order No.          :  " +	orderno +"\n ");			
		setText.append("Payment Date&Time  :  " + DateUtil.returnGreenwich(tradeDate) + "\n ");				
		setText.append("Amount             :  " + ordercount +" "+ currency + "\n \n");	
		setText.append("If you have any disputes about this transaction, please feel free to complain on http://Sitecomplaint.com in time."+"\n ");
		setText.append("We are the professional third party institution engaged in dealing with all disputes among sellers, payment processor and customers."+"\n");		
		setText.append("We'll try our best to help you until you are satisfied with it.and you also can send complaint E-mail to csreason@xingbill.com"+"\n\n");		
		
		setText.append("**Due to floating exchange rate, tiny disparity of order value may exist."+ "\n ");												
		setText.append("**Please note that your bank may apply a small charge for handling this transaction."+ "\n\n ");												
		
		setText.append("Contact details:"+ "\n ");												
		setText.append("Email: xingbill@xingbill.com"+ "\n\n ");	
		
		if(merchant.getStatutes().substring(2, 3).equals("1")){
			setText.append("Merchant's contact details:"+ "\n ");												
			setText.append("Customer service line��"+ merchant.getLinkmanmobile() + "\n ");	
			setText.append("Customer service line��"+ merchant.getLinkmanphone() + "\n ");	
			setText.append("Email:"+merchant.getLinkmanemail()+ "\n\n ");	
		}
	
		
		return setText.toString();
	}
	/**
	 * ���֧���ɹ������ʼ�����
	 * @return
	 */
	public  String getPaymentResultEmail(String cardholderEmail,Double ordercount,String currency,
			String tradeUrl,Date tradeDate,String billadd,String merchantorderno,String orderno){
		StringBuffer setText = new StringBuffer();
		setText.append("The system e-mail,Please do not reply");
		setText.append(" \n\n\n  ");
		setText.append("Dear " + cardholderEmail+" , \n  ");
		setText.append("Thank you for choosing \" "+tradeUrl+" \" to purchase on "+ DateUtil.returnGreenwich(tradeDate) + " and your transaction amount is " + ordercount +" "+ currency + ". \n");
		
		setText.append("The charge will appear on your credit card statement as payment to "+billadd +". \n\n");
		//setText.append("You have consumed " + ordercount +" "+ currency + " on \" "+ tradeUrl +" \"on "+DateUtil.returnGreenwich(tradeDate)+". "+"This payment is for your merchant \" "+tradeUrl+" \"  \n ");
		//setText.append("Attention:The charge will appear on your credit card statement as payment to "+billadd+". "+"\n\n ");
		
		setText.append("Order details: \n ");	
		setText.append("Merchant Order No. :  " + merchantorderno+"\n ");				
		setText.append("Order No.          :  " +	orderno +"\n ");			
		setText.append("Payment Date&Time  :  " + DateUtil.returnGreenwich(tradeDate) + "\n ");				
		setText.append("Amount             :  " + ordercount +" "+ currency + "\n \n");	
		setText.append("If you have any disputes about this transaction, please feel free to complain on https://www.xingbill.com in time."+"\n ");
		setText.append("We (http://xingbill.com) are the professional third party institution engaged in dealing with all disputes among sellers, payment processor and customers."+"\n");		
		setText.append("We'll try our best to help you until you are satisfied with it.and you also can send complaint E-mail to csreason@xingbill.com"+"\n\n");		
		
		setText.append("**Due to floating exchange rate, tiny disparity of order value may exist."+ "\n ");												
		setText.append("**Please note that your bank may apply a small charge for handling this transaction."+ "\n\n ");												
		
		setText.append("Contact details:"+ "\n ");					
		setText.append("Email: csreason@xingbill.com"+ "\n\n ");	
		
		return setText.toString();
	}
	/**
	 * ��ȡ���͸��ٵ����ʼ�����
	 * @return
	 */
	public static String getTrackingEmail(String cardholderemail,String trackType,String trackNo,InternationalTradeinfo trade){
		String checkUrl=null;
		if(trackType.equals("EMS")){
			checkUrl = "http://www.ems.com.cn/english-main.jsp' or 'http://www.usps.com";
		}else if(trackType.equals("DHL")){
			checkUrl = "http://www.dhl.com";
		}else if(trackType.equals("UPS")){
			checkUrl = "http://www.ups.com";
		}else if(trackType.equals("TNT")){
			checkUrl = "http://www.tnt.com";
		}else if(trackType.equals("FedEx")){
			checkUrl = "http://fedex.com/";
		}else if(trackType.equals("ChinaPost")){
			checkUrl = "http://intmail.183.com.cn/";
		}else if(trackType.equals("HkPost")){
			checkUrl = "http://app3.hongkongpost.com/CGI/mt/enquiry.jsp";
		}else{
			checkUrl = "your Shipping Method website";
		}
			
		//��ӵ������ʼ�����
		StringBuffer setText = new StringBuffer();
		setText.append("The system e-mail,Please do not reply");
		setText.append(" \n\n\n  ");
		setText.append("Dear " + cardholderemail +" , \n\n  ");
		
		
//		setText.append("Order details: \n ");	
//		setText.append("    Merchant Order No. :  " + trade.getMerchantOrderNo()+"\n ");				
//		setText.append("    SFEPAY Order No.    :  " +	trade.getOrderNo() +"\n ");			
//		setText.append("    Payment Date&Time  :  " + DateUtil.returnGreenwich(trade.getTradeTime()) + "\n ");				
//		setText.append("    Transaction amount :  " + trade.getTradeAmount() +" "+ trade.getMoneyType() + "\n\n ");	
		
		setText.append("The merchandise you purchased has been sent, the tracking No. is '"+trackNo+"', you can track it on '"+checkUrl+"' to get the tracking information, thank you!");
		setText.append("If you could not get anything by this tracking no.Maybe the post office haven't updated the information so please track it again later!"+"\n\n");		

		setText.append("If you have any disputes about this transaction, please feel free to complain on xingbill.com in time."+"\n ");
		setText.append("We (xingbill.com) are the professional third party institution engaged in dealing with all disputes among sellers, payment processor and customers."+"\n");		
		setText.append("We'll try our best to help you until you are satisfied with it.and you also can send complaint E-mail to csreason@Sitecomplaint.com"+"\n\n");		
		
		setText.append("Contact details:"+ "\n ");					
		//setText.append("Customer service line��+1-315-2791168"+ "\n ");	
		setText.append("Customer service line��+86-15800917127"+ "\n ");	
		//setText.append("Customer service line��+86-18930279312"+ "\n ");												
		setText.append("Email: csreason@xingbill.com"+ "\n ");	
		return setText.toString();
	}
	/**
	 * �˿��email
	 * @param cardholderemail
	 * @param trade
	 * @return
	 */
	public String getRefundEmail(String cardholderemail,InternationalTradeinfo trade){
		StringBuffer setText = new StringBuffer();
		setText.append("The system e-mail,Please do not reply");
		setText.append(" \n\n\n  ");
		setText.append("Dear " +cardholderemail +" , \n\n  ");
		setText.append("We are the dependent and fair third party payment gateway xingbill as the payment service."+"\n  ");
		setText.append("The refund is under processing by bank, and you will receive it within 10 days, normally within 2-7 days.Please wait patiently, Thank you."+"\n \n  ");

		setText.append("Order details: \n ");	
		setText.append("    Merchant Order No. :  " + trade.getMerchantOrderNo()+"\n ");				
		setText.append("    xingbill Order No.    :  " +	trade.getOrderNo() +"\n ");			
		setText.append("    Payment Date&Time  :  " + DateUtil.returnGreenwich(trade.getTradeTime()) + "\n\n\n ");	
		
		//setText.append("    Transaction amount :  " + trade.getOrdercount() +" "+ currency + "\n ");	
		//setText.append("    Refund amount      :  " + refundMoney +" "+ currency + "\n \n \n");	
		
		setText.append("Please check your credit card statement whether the refund reach within 30 days.");
		setText.append("Any question,Please contact us."+"\n\n");		
		setText.append("Best Regards."+ "\n ");												
		setText.append("Contact details:"+ "\n ");												
		setText.append("Customer service line��+86-15800917127"+ "\n ");	
		setText.append("Email: csreason@xingbill.com/cs@xingbill.com"+ "\n ");												
		setText.append("Fax:  +86-21-59189991"+ "\n ");	
		return setText.toString();
	}
	
	/**
	 * ��ȡ�����ʼ�����
	 * @param merchant
	 * @param generateRandStr
	 * @return
	 */
	public String getResetPwdByEmail(InternationalMerchant merchant,String generateRandStr){
		StringBuffer setText = new StringBuffer();
		setText.append("Dear "+merchant.getMeremail()+"\n\n");
		setText.append("         Your password has been revised to : " + generateRandStr+"\n");
		setText.append("              Merchant No.:"+merchant.getMerno()+"\n");
		setText.append("              User name   :"+merchant.getUsername()+"\n");
		setText.append("              Password    :"+generateRandStr+"\n\n");
		setText.append("Contact details:"+ "\n ");												
		setText.append("Customer service line��+86-021-61629498"+ "\n ");												
		setText.append("Email: xingbill@xingbill.com"+ "\n ");												
		return setText.toString();
	}
	/**
	 * �����޶���ʼ�
	 * @return
	 */
	public static String getMoreMoney(){
		StringBuffer setText = new StringBuffer();
		setText.append("�̻�����:\n" +
			"��ֹĿǰ��Ľ��������Ѿ��ۼ����½����޶�\n" +
			"�ͷ���ϵ�绰��+86-21-59189991  " +
			"�ͷ������ַ��cs@xingbill.com " +
			" ��ַ��www.xingbill.com\n"+
			"(ע�����ʼ�Ϊϵͳ�ʼ����ܻظ�!)\n");
		return setText.toString();
	}
	/**
	 * �����޶�85%�����ʼ�
	 * @return
	 */
	public static String getMoreMoneyPart(Double total){
		StringBuffer setText = new StringBuffer();
		setText.append("�̻�����:\n" +
			"��ֹ"+DateUtil.getDate()+"��Ľ������Ѵﵽ���"+total+"���Ѿ��ۼ����½������޶��85%\n" +
			"������ܽ��׾ܸ�����С��0.5%��������ҹ�˾��������½����޶����룬\n" +
			"��˾��ʵͨ���󼴿��������½����޶����������̿����ҹ�˾�ͷ���ϵ��\n" +
			"�ͷ���ϵ�绰��+86-21-59189991 " +
			"�ͷ������ַ��cs@xingbill.com " +
			" ��ַ��www.xingbill.com\n"+
			"(ע�����ʼ�Ϊϵͳ�ʼ����ܻظ�!)\n");
		return setText.toString();
	}
	/*
	 * �ܸ������ʼ����̻�
	 * 
	 */
	public String getRefundToMerchant(InternationalMerchant merchant,InternationalTradeinfo trade){
		StringBuffer setText = new StringBuffer();
		setText.append("Dear merchant : "+merchant.getMerno()+"\n\n");
		setText.append("   You've got a chargeback notification."+"\n");
		setText.append("               Merchant order number:"+trade.getMerchantOrderNo()+"\n");
		setText.append("               Reason for chargeback: No cardholder authorization"+"\n\n");
		setText.append("   Kindly note that the Acquiring Bank will deduct these transaction amounts from your account. Please update your record in time."+"\n\n\n");
		setText.append("**Please keep an eye, on your chargeback!!"+"\n");
		setText.append("                                              Best Regards "+"\n");
		setText.append("                                              Chargeback and Dispute Processing Support Services"+"\n");

		return  setText.toString();
	}
	
	public String getSuccessToMerchant(InternationalMerchant merchant,InternationalTradeinfo trade){
		StringBuffer setText = new StringBuffer();
		setText.append("Dear merchant : "+merchant.getMerno()+"\n\n");
		setText.append("      Congratulations! One more order has been successful."+"\n");
		setText.append("      Order details:"+"\n");
		setText.append("             Merchant Order No. : "+trade.getMerchantOrderNo()+"\n");
		setText.append("             Order No.          : "+trade.getOrderNo()+"\n");
		setText.append("             Amount             :"+trade.getTradeAmount()+"\n\n\n");
		setText.append("             Thank you for your supprort and wish you successful business!"+"\n");
		setText.append("                                                                    xingbill ");
	
		
		return  setText.toString();
	}
	//������ָ�����䷢���ս���
	public String getRiskInfoToSystem(Long merNo,String orderNo,String amount,Long moneyType,String cardNo,String email,String ip,String tradWeb,String errId){
		StateUtils ul=new StateUtils();
		StringBuffer setText = new StringBuffer();
		setText.append("����һ�ʸ߷��ս���:"+"\n\n");
		setText.append("    �̻���:"+merNo+"\n");
		setText.append("    ������:"+orderNo+"\n");
		setText.append("    ���:"+amount+"\n");
		setText.append("    ����:"+ul.getCurrencyTypeByNo(moneyType.intValue())+"\n");
		setText.append("    ����:"+cardNo+"\n");
		setText.append("    email:"+email+"\n");
		setText.append("    ip:"+ip+"\n");
		setText.append("    ������ַ:"+tradWeb+"\n");
		String stateName = null;
		switch (Integer.parseInt(errId)) {
		case 1: stateName="����";		break;
		case 2: stateName="email";		break;
		case 3: stateName="ip";		break;
		case 4: stateName="������ַ";		break;
		}
		setText.append("    ������:"+stateName+"\n");
		setText.append("                                     ������:xingbillSystem");
		return setText.toString();
		}
	
}
