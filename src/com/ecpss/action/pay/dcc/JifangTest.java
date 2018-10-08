package com.ecpss.action.pay.dcc;

public class JifangTest {
    
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String posNum="22222222";// �ն˺�
		String merchant="021209999000000";//�̻���
		String amount="000000000100";//���ؽ��׽��
//		String Transation_Local="";
		String Exp="1210";//��Ч��
		String cardno="4402602810763408";//����
		String cvv2="123";
		String orderNo="021021";//��ˮ��	
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
    	 System.out.println(dcc.getRESPONSE_CODE());
     	 
       //0200
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
    	 dc.setDccMessage(msg2);

    	 msg2=dc.getDccMessage();
    	 System.out.println(msg2.getRESPONSE_CODE());
     	 }
     	 else if(dcc.getRESPONSE_CODE().equals("YX")){
    	 
    //0200	 
     		/*
     		 * 		String posNum="";// �ն˺�
		String merchant="";//�̻���
		String amount="";//���ؽ��׽��
//		String Transation_Local="";
		String Exp="";//��Ч��
		String cardno="";//����
		String cvv2="";
		String orderNo="";//��ˮ��	
     		 */ 
     		 
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
       	 msg3.setCARD_ACCEPTOR_TERMINAL_ID(posNum);//41  �̻��ն˺�
       	 msg3.setCARD_ACCEPTOR_ID_CODE(merchant);//42 �̻���� 
       	 msg3.setCurrency_Code_Foreign("970");//49 ���Ҵ���-----0810
       	 msg3.setCVV2_OR_CVC2(cvv2);//cv2
       	 msg3.setInvoice_Number(orderNo);//62	
    	 DccUtil dc3=new DccUtil();
    	 dc3.setDccMessage(msg3);

    	 msg3=dc3.getDccMessage();
    	 System.out.println(msg3.getRESPONSE_CODE());
     	 }
	}

}
