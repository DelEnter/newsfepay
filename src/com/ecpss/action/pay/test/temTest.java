package com.ecpss.action.pay.test;

import com.ecpss.action.pay.dcc.DCCMessage;
import com.ecpss.action.pay.dcc.DccUtil;

public class temTest {
    
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String posNum="22222222";// �ն˺�
		String merchant="021209999000000";//�̻���
		String amount="000000000010";//���ؽ��׽��
//		String Transation_Local="";
		String Exp="1210";//��Ч��
		String cardno="4402602810763408";//����
		String cvv2="123";
		String orderNo="021023";//��ˮ��	
		//0800
      	 DCCMessage dcc=new DCCMessage();
    	 dcc.setMessageType("0800");//����
    	 dcc.setPrimary_Account_Number(cardno);//�˺�2
    	 dcc.setProcessing_Code("970000");//�������3
    	 dcc.setAmount_Transaction_Local(amount);//4 ���ؽ��׽��
    	 dcc.setSYSTEMS_TRACE_AUDIT_NUMBER(orderNo);//11  ������ˮ��
    	 dcc.setDATE_EXPIRATION(Exp);//14   ��Ч��
    	 dcc.setPOINT_OF_SERVICE_ENTRY_CODE("0012");//22 POS����ģʽ
    	 dcc.setNETWORK_INTL_IDENTIFIER("0098");//24  �յ��̻���
    	 dcc.setPOS_CONDITION_CODE("00");//25 �̻�����
    	 dcc.setCARD_ACCEPTOR_TERMINAL_ID(posNum);//41  �̻��ն˺�
    	 dcc.setCARD_ACCEPTOR_ID_CODE(merchant);//42 �̻���� 
    	 dcc.setInvoice_Number(orderNo);//62	
    	 DccUtil dc=new DccUtil();
    	 dc.setDccMessage(dcc);
      	 dcc=dc.getDccMessage();
      	 System.out.println(dcc.getAmount_Transaction_Foreign());
     	 System.out.println(dcc.getConversion_Rate());
    	 System.out.println(dcc.getCurrency_Code_Foreign());
    	 System.out.println("=============================��ѯ���ؽ�������"+dcc.getRESPONSE_CODE());
     	 
       //0200  ����
     	 if(dcc.getRESPONSE_CODE().equals("YY")){
     	 DCCMessage msg2=new DCCMessage();
       	 msg2.setMessageType("0200");//����
       	 msg2.setPrimary_Account_Number(cardno);//�˺�2
       	 msg2.setProcessing_Code("000000");//�������3
       	 msg2.setAmount_Transaction_Local(amount);//4 ���ؽ��׽��
       	 msg2.setAmount_Transaction_Foreign(dcc.getAmount_Transaction_Foreign());//5  0810
       	 msg2.setConversion_Rate(dcc.getConversion_Rate());//9    0810
       	 msg2.setSYSTEMS_TRACE_AUDIT_NUMBER(orderNo);//11  ������ˮ��
       	 msg2.setDATE_EXPIRATION(Exp);//14   ��Ч��
       	 msg2.setPOINT_OF_SERVICE_ENTRY_CODE("0012");//22 POS����ģʽ
       	 msg2.setNETWORK_INTL_IDENTIFIER("0017");//24  �յ��̻���
       	 msg2.setPOS_CONDITION_CODE("00");//25 �̻�����
//       	 msg2.setRETRIEVAL_REFERENCE_NUMBER("");//37
       	 msg2.setCARD_ACCEPTOR_TERMINAL_ID(posNum);//41  �̻��ն˺�
       	 msg2.setCARD_ACCEPTOR_ID_CODE(merchant);//42 �̻���� 
       	 msg2.setCurrency_Code_Foreign(dcc.getCurrency_Code_Foreign());//49 ���Ҵ���-----0810
       	 msg2.setCVV2_OR_CVC2(cvv2);//cv2
       	 msg2.setInvoice_Number(orderNo);//62	
    	 DccUtil dc2=new DccUtil();
    	 dc2.setDccMessage(msg2);

    	 msg2=dc2.getDccMessage();
    	 System.out.println("===============================yy����:"+msg2.getRESPONSE_CODE());
     	 }
     	 else if(dcc.getRESPONSE_CODE().equals("YX")){
    	 
    //0200	 
     	 DCCMessage msg3=new DCCMessage();
       	 msg3.setMessageType("0200");//����
       	 msg3.setPrimary_Account_Number(cardno);//�˺�2
       	 msg3.setProcessing_Code("000000");//�������3
       	 msg3.setAmount_Transaction_Local(amount);//4 ���ؽ��׽��
       	 msg3.setAmount_Transaction_Foreign(dcc.getAmount_Transaction_Foreign());//5  0810
       	 msg3.setConversion_Rate(dcc.getConversion_Rate());//9    0810
       	 msg3.setSYSTEMS_TRACE_AUDIT_NUMBER(orderNo);//11  ������ˮ��
       	 msg3.setDATE_EXPIRATION(Exp);//14   ��Ч��
       	 msg3.setPOINT_OF_SERVICE_ENTRY_CODE("0012");//22 POS����ģʽ
       	 msg3.setNETWORK_INTL_IDENTIFIER("0017");//24  �յ��̻���
       	 msg3.setPOS_CONDITION_CODE("00");//25 �̻�����
//       	 msg3.setRETRIEVAL_REFERENCE_NUMBER("");//37
       	 msg3.setCARD_ACCEPTOR_TERMINAL_ID(dcc.getCARD_ACCEPTOR_TERMINAL_ID());//41  �̻��ն˺�
       	 msg3.setCARD_ACCEPTOR_ID_CODE(merchant);//42 �̻���� 
       	 msg3.setCurrency_Code_Foreign(dcc.getCurrency_Code_Foreign());//49 ���Ҵ���-----0810
       	 msg3.setCVV2_OR_CVC2(cvv2);//cv2
       	 msg3.setInvoice_Number(orderNo);//62	
    	 DccUtil dc3=new DccUtil();
    	 dc3.setDccMessage(msg3);

    	 msg3=dc3.getDccMessage();
    	 System.out.println("=====================yx����:"+msg3.getRESPONSE_CODE());
     	 }
  //�������� 0200(YY)   	 
     	 if(dcc.getRESPONSE_CODE().equals("YY")){
     	 DCCMessage msg4=new DCCMessage();
       	 msg4.setMessageType("0200");//����
       	 msg4.setPrimary_Account_Number(cardno);//�˺�2
       	 msg4.setProcessing_Code("020000");//�������3
       	 msg4.setAmount_Transaction_Local(amount);//4 ���ؽ��׽��
       	 msg4.setAmount_Transaction_Foreign(dcc.getAmount_Transaction_Foreign());//5  0810
       	 msg4.setConversion_Rate(dcc.getConversion_Rate());//9    0810
       	 msg4.setSYSTEMS_TRACE_AUDIT_NUMBER(orderNo);//11  ������ˮ��
       	 msg4.setDATE_EXPIRATION(Exp);//14   ��Ч��
       	 msg4.setPOINT_OF_SERVICE_ENTRY_CODE("0012");//22 POS����ģʽ
       	 msg4.setNETWORK_INTL_IDENTIFIER("0017");//24  �յ��̻���
       	 msg4.setPOS_CONDITION_CODE("00");//25 �̻�����
       	 msg4.setRETRIEVAL_REFERENCE_NUMBER(dcc.getRETRIEVAL_REFERENCE_NUMBER());//37
       	 msg4.setAUTH_IDENTIFICATION_RESPONSE(dcc.getAUTH_IDENTIFICATION_RESPONSE());//38
       	 msg4.setCARD_ACCEPTOR_TERMINAL_ID(posNum);//41  �̻��ն˺�
       	 msg4.setCARD_ACCEPTOR_ID_CODE(merchant);//42 �̻���� 
       	 msg4.setCurrency_Code_Foreign(dcc.getCurrency_Code_Foreign());//49 ���Ҵ���-----0810
//     	 msg4.setCVV2_OR_CVC2(cvv2);//cv2
       	 msg4.setInvoice_Number(orderNo);//62	
    	 DccUtil dc4=new DccUtil();
    	 dc4.setDccMessage(msg4);

    	 msg4=dc4.getDccMessage();
    	 System.out.println("============================YY��������:"+msg4.getRESPONSE_CODE());
     	 }else if(dcc.getRESPONSE_CODE().equals("YX")){
         	 DCCMessage msg5=new DCCMessage();
           	 msg5.setMessageType("0200");//����
           	 msg5.setPrimary_Account_Number(cardno);//�˺�2
           	 msg5.setProcessing_Code("020000");//�������3
           	 msg5.setAmount_Transaction_Local(amount);//4 ���ؽ��׽��
//          	 msg5.setAmount_Transaction_Foreign(dcc.getAmount_Transaction_Foreign());//5  0810
           	 msg5.setConversion_Rate(dcc.getConversion_Rate());//9    0810
           	 msg5.setSYSTEMS_TRACE_AUDIT_NUMBER(orderNo);//11  ������ˮ��
           	 msg5.setDATE_EXPIRATION(Exp);//14   ��Ч��
           	 msg5.setPOINT_OF_SERVICE_ENTRY_CODE("0012");//22 POS����ģʽ
           	 msg5.setNETWORK_INTL_IDENTIFIER("0017");//24  �յ��̻���
           	 msg5.setPOS_CONDITION_CODE("00");//25 �̻�����
           	 msg5.setRETRIEVAL_REFERENCE_NUMBER(dcc.getRETRIEVAL_REFERENCE_NUMBER());//37
           	 msg5.setAUTH_IDENTIFICATION_RESPONSE(dcc.getAUTH_IDENTIFICATION_RESPONSE());//38
           	 msg5.setCARD_ACCEPTOR_TERMINAL_ID(dcc.getCARD_ACCEPTOR_TERMINAL_ID());//41  �̻��ն˺�
           	 msg5.setCARD_ACCEPTOR_ID_CODE(merchant);//42 �̻���� 
           	 msg5.setCurrency_Code_Foreign(dcc.getCurrency_Code_Foreign());//49 ���Ҵ���-----0810
           	 msg5.setCVV2_OR_CVC2(cvv2);//cv2
           	 msg5.setInvoice_Number(orderNo);//62	
        	 DccUtil dc5=new DccUtil();
        	 dc5.setDccMessage(msg5);

        	 msg5=dc5.getDccMessage();
        	 System.out.println("=====================msg5����yx����:"+msg5.getRESPONSE_CODE());     		 
     		 
     	 }
     	 //����  0400
     	 if(dcc.getRESPONSE_CODE().equals("YY")){
         	 DCCMessage msg6=new DCCMessage();
           	 msg6.setMessageType("0400");//����
           	 msg6.setPrimary_Account_Number(cardno);//�˺�2
           	 msg6.setProcessing_Code("000000");//�������3
           	 msg6.setAmount_Transaction_Local(amount);//4 ���ؽ��׽��
           	 msg6.setAmount_Transaction_Foreign(dcc.getAmount_Transaction_Foreign());//5  0810
           	 msg6.setConversion_Rate(dcc.getConversion_Rate());//9    0810
           	 msg6.setSYSTEMS_TRACE_AUDIT_NUMBER(orderNo);//11  ������ˮ��
           	 msg6.setDATE_EXPIRATION(Exp);//14   ��Ч��
           	 msg6.setPOINT_OF_SERVICE_ENTRY_CODE("0012");//22 POS����ģʽ
           	 msg6.setNETWORK_INTL_IDENTIFIER("0017");//24  �յ��̻���
           	 msg6.setPOS_CONDITION_CODE("00");//25 �̻�����
           	 msg6.setRETRIEVAL_REFERENCE_NUMBER(dcc.getRETRIEVAL_REFERENCE_NUMBER());//37
           	 msg6.setCARD_ACCEPTOR_TERMINAL_ID(posNum);//41  �̻��ն˺�
           	 msg6.setCARD_ACCEPTOR_ID_CODE(merchant);//42 �̻���� 
           	 msg6.setCurrency_Code_Foreign(dcc.getCurrency_Code_Foreign());//49 ���Ҵ���-----0810
           	 msg6.setInvoice_Number(orderNo);//62	
        	 DccUtil dc6=new DccUtil();
        	 dc6.setDccMessage(msg6);

        	 msg6=dc6.getDccMessage();
        	 System.out.println("===============================yy���׳���(��������):"+msg6.getRESPONSE_CODE());
    		 
     		 
     	 }else if(dcc.getRESPONSE_CODE().equals("YX")){
         	 DCCMessage msg7=new DCCMessage();
           	 msg7.setMessageType("0400");//����
           	 msg7.setPrimary_Account_Number(cardno);//�˺�2
           	 msg7.setProcessing_Code("000000");//�������3
           	 msg7.setAmount_Transaction_Local(amount);//4 ���ؽ��׽��
//           	 msg7.setAmount_Transaction_Foreign(dcc.getAmount_Transaction_Foreign());//5  0810
//           	 msg7.setConversion_Rate(dcc.getConversion_Rate());//9    0810
           	 msg7.setSYSTEMS_TRACE_AUDIT_NUMBER(orderNo);//11  ������ˮ��
           	 msg7.setDATE_EXPIRATION(Exp);//14   ��Ч��
           	 msg7.setPOINT_OF_SERVICE_ENTRY_CODE("0012");//22 POS����ģʽ
           	 msg7.setNETWORK_INTL_IDENTIFIER("0017");//24  �յ��̻���
           	 msg7.setPOS_CONDITION_CODE("00");//25 �̻�����
           	 msg7.setRETRIEVAL_REFERENCE_NUMBER(dcc.getRETRIEVAL_REFERENCE_NUMBER());//37
           	 msg7.setCARD_ACCEPTOR_TERMINAL_ID(dcc.getCARD_ACCEPTOR_TERMINAL_ID());//41  �̻��ն˺�
           	 msg7.setCARD_ACCEPTOR_ID_CODE(merchant);//42 �̻���� 
 //          	 msg7.setCurrency_Code_Foreign(dcc.getCurrency_Code_Foreign());//49 ���Ҵ���-----0810
//           	 msg7.setCVV2_OR_CVC2(cvv2);//cv2
           	 msg7.setInvoice_Number(orderNo);//62	
        	 DccUtil dc7=new DccUtil();
        	 dc7.setDccMessage(msg7);

        	 msg7=dc7.getDccMessage();
        	 System.out.println("=====================yx���׳���:"+msg7.getRESPONSE_CODE());     		 
     		 
     	 }
     	 
     	 
     	 
	}

}
