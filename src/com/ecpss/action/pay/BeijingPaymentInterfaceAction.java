package com.ecpss.action.pay;

import java.text.DecimalFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

import com.ecpss.action.BaseAction;
import com.ecpss.action.pay.dcc.DCCMessage;
import com.ecpss.action.pay.dcc.DccUtil;
import com.ecpss.model.payment.InternationalCardholdersInfo;
import com.ecpss.model.payment.InternationalTradeinfo;
import com.ecpss.util.AES;
import com.ecpss.util.GetBatchNo;

public class BeijingPaymentInterfaceAction extends BaseAction {

	/**
	 * ���ܲ�����Ϣ
	 */
	private String cardNo; // �ֿ��˿���
	private String cardCVV2; // �ֿ���cvv2
	private String cardExDate; // �ֿ������ÿ���Ч��

	private String tradeOrderNo; // ������ˮ��
	private String tradeAmount; // ����RMB���
	private String tradeRMBAmount; // ����RMB���
	private String posNumber; // �ն˺�
	private String posMerchantNo;//�̻���
	private String andPosNumber;//�����ն˺�
	private String andposMerchantNo;//�����ն˺��̻���
	
	//private Long terminalId;//�ն�id
	
	
	/**
	 * ����֧�������Ϣ
	 */
	private String finalPosNumer; // ����ʹ���ն˺�
	private String paymentResult; // ����֧�����    00:�ɹ�  , 01~99: ʧ�� , NC:���Ӳ���dcc
	private String authorizationNo; // ��Ȩ��

	public String beijingRequest() {
		// ��ȡ������ˮ��
		InternationalTradeinfo trade = new InternationalTradeinfo();
		GetBatchNo ut = new GetBatchNo();
		String merno="1000";
		if(tradeOrderNo.startsWith("7")){
			merno="1001";
			trade.setMerchantId(1001L);
			trade.setTradeChannel(2248L);
		}else{
			merno="1000";
			trade.setMerchantId(1L);
			trade.setTradeChannel(4L);
		}
		String rorderno = ut.getOrderinfo(merno);
		
		trade.setOrderNo(rorderno);
		trade.setIsTrackNo("EMS100000");
		trade.setMerchantOrderNo(tradeOrderNo);	
		trade.setTradeTime(new Date());
		trade.setBalanceRate(285L);
		trade.setTradeRate(286L);
		trade.setMoneyType(3L);
		trade.setTradeAmount(Double.valueOf(tradeRMBAmount));		
		trade.setRmbAmount(Double.valueOf(tradeRMBAmount));
		//trade.setMoneyType(Long.valueOf(Currency));
		trade.setTradeState("30000110000000000000");		
		//trade.setTradeRate(changerate.getId());
		//trade.setBalanceRate(settlementrate.getId());	
		//trade.setTradeUrl(tradeAdd);
		//trade.setReturnUrl(this.ReturnURL);
		trade.setLastDate(new Date());
		trade.setBackCount(0d);
		this.commonService.save(trade);
		InternationalCardholdersInfo ci = new InternationalCardholdersInfo();
		ci.setCardNo(AES.setCarNo(cardNo));
		ci.setCvv2(AES.setCarNo(cardCVV2));
		ci.setExpiryDate(AES.setCarNo(cardExDate));
		ci.setTradeId(trade.getId());
		this.commonService.save(ci);
		
		// ���ײ�ѯ
		DCCMessage dcc = new DCCMessage();
		dcc.setMessageType("0800");// ����
		dcc.setPrimary_Account_Number(cardNo);// �˺�2
		dcc.setProcessing_Code("970000");// �������3
		dcc.setAmount_Transaction_Local(this.buzero(tradeRMBAmount + ""));// 4
		// ���ؽ��׽��
		dcc.setSYSTEMS_TRACE_AUDIT_NUMBER(tradeOrderNo.substring(tradeOrderNo
				.length() - 6, tradeOrderNo.length()));// 11
		// ������ˮ��
		dcc.setDATE_EXPIRATION(cardExDate.substring(2, 4)
				+ cardExDate.substring(0, 2));// 14 ��Ч��
		dcc.setPOINT_OF_SERVICE_ENTRY_CODE("0012");// 22 POS����ģʽ
		dcc.setNETWORK_INTL_IDENTIFIER("0098");// 24 �յ��̻���
		dcc.setPOS_CONDITION_CODE("00");// 25 �̻�����
		dcc.setCARD_ACCEPTOR_TERMINAL_ID(posNumber);// 41 �̻��ն˺�
		dcc.setCARD_ACCEPTOR_ID_CODE(posMerchantNo);// 42 �̻����
		dcc.setInvoice_Number(tradeOrderNo.substring(tradeOrderNo.length() - 6,
				tradeOrderNo.length()));// 62
		DccUtil dc = new DccUtil();
		dc.setDccMessage(dcc);

		try {
			dcc = dc.getDccMessage();
			if (cardNo.startsWith("5")) {
				dcc.setRESPONSE_CODE("YX");
			}
		} catch (Exception ex) {
			//���Ӳ���DCC�ͷ��ظ�����NC,����Ϊ������
			this.paymentResult="NC";
			this.finalPosNumer=posNumber;
			this.authorizationNo="1";
			return SUCCESS;
			// // ���ݵĲ���
			// this.responseCode = 19;
			// message = "Your payment is being processed";
			// MD5info = trade.getMerchantOrderNo() + trade.getMoneyType()
			// + ordercountValue + responseCode + MD5key;
			// md5Value = md5.getMD5ofStr(MD5info);
			// this.commonService
			// .deleteBySql("update international_tradeinfo t set
			// t.tradestate='2'||substr(t.tradestate,2,(length(t.tradestate)-1))
			// where t.id='"
			// + trade.getId() + "'");
			// return INPUT;
		}
		System.out.println("DCC��++++++++++  " + dcc.getRESPONSE_CODE());
		// ��DCC����
		if (dcc.getRESPONSE_CODE().equals("YY")) {
			// ����

			DCCMessage msg2 = new DCCMessage();
			msg2.setMessageType("0200");// ����
			msg2.setPrimary_Account_Number(cardNo);// �˺�2
			msg2.setProcessing_Code("000000");// �������3
			msg2.setAmount_Transaction_Local(this.buzero(tradeRMBAmount + ""));// 4
			// ���ؽ��׽��
			msg2.setAmount_Transaction_Foreign(dcc
					.getAmount_Transaction_Foreign());// 5
			// 0810
			msg2.setConversion_Rate(dcc.getConversion_Rate());// 9
			// 0810
			msg2.setSYSTEMS_TRACE_AUDIT_NUMBER(tradeOrderNo.substring(
					tradeOrderNo.length() - 6, tradeOrderNo.length()));// 11
			// ������ˮ��
			msg2.setDATE_EXPIRATION(cardExDate.substring(2, 4)
					+ cardExDate.substring(0, 2));// 14 ��Ч��
			msg2.setPOINT_OF_SERVICE_ENTRY_CODE("0012");// 22 POS����ģʽ
			msg2.setNETWORK_INTL_IDENTIFIER("0017");// 24 �յ��̻���
			msg2.setPOS_CONDITION_CODE("00");// 25 �̻�����
			// msg2.setRETRIEVAL_REFERENCE_NUMBER("");//37
			msg2.setCARD_ACCEPTOR_TERMINAL_ID(posNumber);// 41
			// �̻��ն˺�
			msg2.setCARD_ACCEPTOR_ID_CODE(posMerchantNo);// 42 �̻����
			msg2.setCurrency_Code_Foreign(dcc.getCurrency_Code_Foreign());// 49
			// ���Ҵ���-----0810
			msg2.setCVV2_OR_CVC2(cardCVV2);// cv2
			msg2.setInvoice_Number(tradeOrderNo.substring(
					tradeOrderNo.length() - 6, tradeOrderNo.length()));// 62
			DccUtil dc2 = new DccUtil();
			dc2.setDccMessage(msg2);
			try {
				msg2 = dc2.getDccMessage();
			} catch (Exception ex) {
				this.paymentResult="NC";
				this.finalPosNumer=posNumber;
				this.authorizationNo="1";
				//return SUCCESS;
				// this.responseCode = 19;
				// message = "Your payment is being processed";
				// MD5info = trade.getMerchantOrderNo() + trade.getMoneyType()
				// + ordercountValue + responseCode + MD5key;
				// md5Value = md5.getMD5ofStr(MD5info);
				// this.commonService
				// .deleteBySql("update international_tradeinfo t set
				// t.tradestate='2'||substr(t.tradestate,2,(length(t.tradestate)-1))
				// where t.id='"
				// + trade.getId() + "'");
				// ���׳���
				DCCMessage msg6 = new DCCMessage();
				msg6.setMessageType("0400");// ����
				msg6.setPrimary_Account_Number(cardNo);// �˺�2
				msg6.setProcessing_Code("000000");// �������3
				msg6.setAmount_Transaction_Local(this.buzero(tradeRMBAmount
						+ ""));// 4 ���ؽ��׽��
				msg6.setAmount_Transaction_Foreign(dcc
						.getAmount_Transaction_Foreign());// 5 0810
				msg6.setConversion_Rate(dcc.getConversion_Rate());// 9
				// 0810
				msg6.setSYSTEMS_TRACE_AUDIT_NUMBER(tradeOrderNo.substring(
						tradeOrderNo.length() - 6, tradeOrderNo.length()));// 11
				// ������ˮ��
				msg6.setDATE_EXPIRATION(cardExDate.substring(2, 4)
						+ cardExDate.substring(0, 2));// 14
				// ��Ч��
				msg6.setPOINT_OF_SERVICE_ENTRY_CODE("0012");// 22
				// POS����ģʽ
				msg6.setNETWORK_INTL_IDENTIFIER("0017");// 24 �յ��̻���
				msg6.setPOS_CONDITION_CODE("00");// 25 �̻�����
				msg6.setRETRIEVAL_REFERENCE_NUMBER(msg2
						.getRETRIEVAL_REFERENCE_NUMBER());// 37
				msg6.setCARD_ACCEPTOR_TERMINAL_ID(posNumber);// 41
				// �̻��ն˺�
				msg6.setCARD_ACCEPTOR_ID_CODE(posMerchantNo);// 42
				// �̻����
				msg6.setCurrency_Code_Foreign(dcc.getCurrency_Code_Foreign());// 49
				// ���Ҵ���-----0810
				msg6.setInvoice_Number(tradeOrderNo.substring(tradeOrderNo
						.length() - 6, tradeOrderNo.length()));// 62
				DccUtil dc6 = new DccUtil();
				dc6.setDccMessage(msg6);

				msg6 = dc6.getDccMessage();
				System.out
						.println("===============================yy���׳���(��������):"
								+ msg6.getRESPONSE_CODE());

				return SUCCESS;
			}

			if (msg2.getRESPONSE_CODE().equals("00")) {
				//֧���ɹ�
				this.paymentResult=msg2.getRESPONSE_CODE();
				this.finalPosNumer=posNumber;
				this.authorizationNo=msg2.getAUTH_IDENTIFICATION_RESPONSE();
				//���׳ɹ�
				String message = "Payment Success!";
//				this.responseCode = 88;
				this.commonService
						.deleteBySql("update  international_tradeinfo t  set t.tradestate='1'||substr(t.tradestate,2,(length(t.tradestate)-1)),t.remark='"
								+ message
								+ "' ,t.VIPAuthorizationNo='"
								+ msg2.getAUTH_IDENTIFICATION_RESPONSE()
								+ "' ,t.VIPBatchNo='888888',t.VIPTerminalNo='"+posNumber+"' where t.id='"
								+ trade.getId() + "'");
////����ֿ���cvv,��Ч��

//				MD5info = trade.getMerchantOrderNo() + trade.getMoneyType()
//						+ ordercountValue + responseCode + MD5key;
//				md5Value = md5.getMD5ofStr(MD5info);
				return SUCCESS;
			} else {
				//����ʧ��
				this.paymentResult=msg2.getRESPONSE_CODE();
				this.finalPosNumer=posNumber;
				this.authorizationNo="1";
				
				
				String message = "Payment Declined!" + msg2.getRESPONSE_CODE();
				//this.responseCode = 0;
				this.commonService
						.deleteBySql("update  international_tradeinfo t  set t.tradestate='0'||substr(t.tradestate,2,(length(t.tradestate)-1)),t.remark='"
								+ message
								+ "' where t.id='"
								+ trade.getId()
								+ "'");
				//����ֿ���cvv,��Ч��

//				MD5info = trade.getMerchantOrderNo() + trade.getMoneyType()
//						+ ordercountValue + responseCode + MD5key;
//				md5Value = md5.getMD5ofStr(MD5info);
				return SUCCESS;
			}

		} else if (dcc.getRESPONSE_CODE().equals("YX")) {
//			InternationalTerminalManager tm = (InternationalTerminalManager) this.commonService
//					.load(InternationalTerminalManager.class, terminalId);
//			String ter = "select tm from InternationalTerminalManager tm where tm.terminalNo='"
//					+ tm.getAndterminalNo().trim() + "' ";
//			List<InternationalTerminalManager> list = this.commonService
//					.list(ter);
//			System.out.println("�ն˺ţ�+++++++++++++++"
//					+ list.get(0).getTerminalNo());
			DCCMessage dcc3 = new DCCMessage();
			dcc3.setMessageType("0200");// ����
			dcc3.setPrimary_Account_Number(cardNo);// �˺�2
			dcc3.setProcessing_Code("000000");// �������3
			dcc3.setAmount_Transaction_Local(this.buzero(tradeRMBAmount + ""));// 4
			// ���ؽ��׽��
			dcc3.setSYSTEMS_TRACE_AUDIT_NUMBER(tradeOrderNo.substring(
					tradeOrderNo.length() - 6, tradeOrderNo.length()));// 11
			// ������ˮ��
			dcc3.setDATE_EXPIRATION(cardExDate.substring(2, 4)
					+ cardExDate.substring(0, 2));// 14 ��Ч��
			dcc3.setPOINT_OF_SERVICE_ENTRY_CODE("0012");// 22 POS����ģʽ
			dcc3.setNETWORK_INTL_IDENTIFIER("0017");// 24 �յ��̻���
			dcc3.setCARD_ACCEPTOR_TERMINAL_ID(andPosNumber);// 41
			// �̻��ն˺�
			dcc3.setCARD_ACCEPTOR_ID_CODE(andposMerchantNo);// 42
			// �̻����
			dcc3.setCVV2_OR_CVC2(cardCVV2);// cv2 61
			dcc3.setInvoice_Number(tradeOrderNo.substring(
					tradeOrderNo.length() - 6, tradeOrderNo.length()));// 62
			DccUtil dc3 = new DccUtil();
			dc3.setDccMessage(dcc3);
			try {
				dcc3 = dc3.getDccMessage();
			} catch (Exception ex) {
				this.paymentResult="NC";
				this.finalPosNumer=andPosNumber;
				this.authorizationNo="1";
//				this.responseCode = 19;
//				message = "Your payment is being processed";
//				MD5info = trade.getMerchantOrderNo() + trade.getMoneyType()
//						+ ordercountValue + responseCode + MD5key;
//				md5Value = md5.getMD5ofStr(MD5info);
//				this.commonService
//						.deleteBySql("update  international_tradeinfo t  set t.tradestate='2'||substr(t.tradestate,2,(length(t.tradestate)-1)),t.VIPTerminalNo='"
//								+ list.get(0).getTerminalNo()
//								+ "' where t.id='" + trade.getId() + "'");

				DCCMessage msg7 = new DCCMessage();
				msg7.setMessageType("0400");// ����
				msg7.setPrimary_Account_Number(cardNo);// �˺�2
				msg7.setProcessing_Code("000000");// �������3
				msg7.setAmount_Transaction_Local(this.buzero(tradeRMBAmount
						+ ""));// 4 ���ؽ��׽��
				// msg7.setAmount_Transaction_Foreign(dcc.getAmount_Transaction_Foreign());//5
				// 0810
				// msg7.setConversion_Rate(dcc.getConversion_Rate());//9
				// 0810
				msg7.setSYSTEMS_TRACE_AUDIT_NUMBER(tradeOrderNo.substring(
						tradeOrderNo.length() - 6, tradeOrderNo.length()));// 11
				// ������ˮ��
				msg7.setDATE_EXPIRATION(cardExDate.substring(2, 4)
						+ cardExDate.substring(0, 2));// 14
				// ��Ч��
				msg7.setPOINT_OF_SERVICE_ENTRY_CODE("0012");// 22
				// POS����ģʽ
				msg7.setNETWORK_INTL_IDENTIFIER("0017");// 24 �յ��̻���
				msg7.setPOS_CONDITION_CODE("00");// 25 �̻�����
				msg7.setRETRIEVAL_REFERENCE_NUMBER(dcc3
						.getRETRIEVAL_REFERENCE_NUMBER());// 37
				msg7.setCARD_ACCEPTOR_TERMINAL_ID(this.andPosNumber);// 41
				// �̻��ն˺�
				msg7.setCARD_ACCEPTOR_ID_CODE(andposMerchantNo);// 42
				// �̻����
				// msg7.setCurrency_Code_Foreign(dcc.getCurrency_Code_Foreign());//49
				// ���Ҵ���-----0810
				// msg7.setCVV2_OR_CVC2(cvv2);//cv2
				msg7.setInvoice_Number(tradeOrderNo.substring(tradeOrderNo
						.length() - 6, tradeOrderNo.length()));// 62
				DccUtil dc7 = new DccUtil();
				dc7.setDccMessage(msg7);

				msg7 = dc7.getDccMessage();
				System.out.println("=====================yx���׳���:"
						+ msg7.getRESPONSE_CODE());
				return SUCCESS;
			}
			if (dcc3.getRESPONSE_CODE().equals("00")) {
				//֧���ɹ�
				this.paymentResult=dcc3.getRESPONSE_CODE();
				this.finalPosNumer=andPosNumber;
				this.authorizationNo=dcc3.getAUTH_IDENTIFICATION_RESPONSE();
				
				//���׳ɹ�
				String message = "Payment Success!";
//				this.responseCode = 88;
				this.commonService
						.deleteBySql("update  international_tradeinfo t  set t.tradestate='1'||substr(t.tradestate,2,(length(t.tradestate)-1)),t.remark='"
								+ message
								+ "' ,t.VIPAuthorizationNo='"
								+ dcc3.getAUTH_IDENTIFICATION_RESPONSE()
								+ "' ,t.VIPBatchNo='888888',t.VIPTerminalNo='"+andPosNumber+"' where t.id='"
								+ trade.getId() + "'");
				//����ֿ���cvv,��Ч��

				
				return SUCCESS;
				//���׳ɹ�
			} else {
				//����ʧ��
				this.paymentResult=dcc3.getRESPONSE_CODE();
				this.finalPosNumer=andPosNumber;
				this.authorizationNo="1";
				
				String message = "Payment Declined!" + dcc3.getRESPONSE_CODE();
				//this.responseCode = 0;
				this.commonService
						.deleteBySql("update  international_tradeinfo t  set t.tradestate='0'||substr(t.tradestate,2,(length(t.tradestate)-1)),t.remark='"
								+ message
								+ "' where t.id='"
								+ trade.getId()
								+ "'");
				//����ֿ���cvv,��Ч��

				return SUCCESS;
			}

		}
		return SUCCESS;
	}


	public String buzero(String refundRMBMoney){
		String refundRMB = "000000000000";
		String zero_12 = "000000000000";
		DecimalFormat decimalFormat = new DecimalFormat("##############0.00");
		if(StringUtils.isNotBlank(refundRMBMoney) && refundRMBMoney.replace(".", "").matches("\\d+")){		
				String refundRMBStr = Double.valueOf((decimalFormat.format(Double.valueOf(refundRMBMoney))))*100+"";
				String refundRMB_0 = zero_12 + refundRMBStr.substring(0, refundRMBStr.indexOf("."));
				refundRMB = refundRMB_0.substring(refundRMB_0.length()-12, refundRMB_0.length());
		}
		return refundRMB;
	}


	public String getCardNo() {
		return cardNo;
	}

	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}

	public String getCardCVV2() {
		return cardCVV2;
	}

	public void setCardCVV2(String cardCVV2) {
		this.cardCVV2 = cardCVV2;
	}

	public String getCardExDate() {
		return cardExDate;
	}

	public void setCardExDate(String cardExDate) {
		this.cardExDate = cardExDate;
	}

	public String getTradeOrderNo() {
		return tradeOrderNo;
	}

	public void setTradeOrderNo(String tradeOrderNo) {
		this.tradeOrderNo = tradeOrderNo;
	}

	public String getTradeRMBAmount() {
		return tradeRMBAmount;
	}

	public void setTradeRMBAmount(String tradeRMBAmount) {
		this.tradeRMBAmount = tradeRMBAmount;
	}

	public String getPosNumber() {
		return posNumber;
	}

	public void setPosNumber(String posNumber) {
		this.posNumber = posNumber;
	}

	public String getPosMerchantNo() {
		return posMerchantNo;
	}

	public void setPosMerchantNo(String posMerchantNo) {
		this.posMerchantNo = posMerchantNo;
	}

	public String getAndPosNumber() {
		return andPosNumber;
	}

	public void setAndPosNumber(String andPosNumber) {
		this.andPosNumber = andPosNumber;
	}

	public String getAndposMerchantNo() {
		return andposMerchantNo;
	}

	public void setAndposMerchantNo(String andposMerchantNo) {
		this.andposMerchantNo = andposMerchantNo;
	}

	public String getFinalPosNumer() {
		return finalPosNumer;
	}

	public void setFinalPosNumer(String finalPosNumer) {
		this.finalPosNumer = finalPosNumer;
	}

	public String getPaymentResult() {
		return paymentResult;
	}

	public void setPaymentResult(String paymentResult) {
		this.paymentResult = paymentResult;
	}

	public String getAuthorizationNo() {
		return authorizationNo;
	}

	public void setAuthorizationNo(String authorizationNo) {
		this.authorizationNo = authorizationNo;
	}

	public String getTradeAmount() {
		return tradeAmount;
	}

	public void setTradeAmount(String tradeAmount) {
		this.tradeAmount = tradeAmount;
	}

}
