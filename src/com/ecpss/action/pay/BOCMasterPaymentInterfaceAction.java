package com.ecpss.action.pay;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.ecpss.action.BaseAction;
import com.ecpss.action.pay.dcc.DCCMessage;
import com.ecpss.action.pay.dcc.DccUtil;
import com.ecpss.action.pay.tc.ClientBoc;
import com.ecpss.action.pay.tc.XMLGetMessage;
import com.ecpss.model.payment.InternationalCardholdersInfo;
import com.ecpss.model.payment.InternationalTradeinfo;
import com.ecpss.model.shop.InternationalMerchantTerminal;
import com.ecpss.model.shop.InternationalTerminalManager;
import com.ecpss.util.AES;
import com.ecpss.util.BOCPayResult;
import com.ecpss.util.CCSendMail;
import com.ecpss.util.CN3DPayResult;
import com.ecpss.util.EmailInfo;
import com.ecpss.util.EvipPayResult;
import com.ecpss.util.GetBatchNo;

public class BOCMasterPaymentInterfaceAction extends BaseAction {

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
	
	private String merchantNo;
	//private Long terminalId;//�ն�id
	
	
	/**
	 * ����֧�������Ϣ
	 */
	private String finalPosNumer; // ����ʹ���ն˺�
	private String paymentResult; // ����֧�����    00:�ɹ�  , 01~99: ʧ�� , NC:���Ӳ���dcc
	private String authorizationNo; // ��Ȩ��

//	public String beijingRequest() {
//		// ��ȡ������ˮ��
//		InternationalTradeinfo trade = new InternationalTradeinfo();
//		GetBatchNo ut = new GetBatchNo();
//		String merno="1000";
//		if(tradeOrderNo.startsWith("7")){
//			merno="1001";
//			trade.setMerchantId(1001L);
//			trade.setTradeChannel(2248L);
//		}else{
//			merno="1000";
//			trade.setMerchantId(1L);
//			trade.setTradeChannel(4L);
//		}
//		String rorderno = ut.getOrderinfo(merno);
//		
//		trade.setOrderNo(rorderno);
//		trade.setIsTrackNo("EMS100000");
//		trade.setMerchantOrderNo(tradeOrderNo);	
//		trade.setTradeTime(new Date());
//		trade.setBalanceRate(285L);
//		trade.setTradeRate(286L);
//		trade.setMoneyType(3L);
//		trade.setTradeAmount(Double.valueOf(tradeRMBAmount));		
//		trade.setRmbAmount(Double.valueOf(tradeRMBAmount));
//		//trade.setMoneyType(Long.valueOf(Currency));
//		trade.setTradeState("30000110000000000000");		
//		//trade.setTradeRate(changerate.getId());
//		//trade.setBalanceRate(settlementrate.getId());	
//		//trade.setTradeUrl(tradeAdd);
//		//trade.setReturnUrl(this.ReturnURL);
//		trade.setLastDate(new Date());
//		trade.setBackCount(0d);
//		this.commonService.save(trade);
//		InternationalCardholdersInfo ci = new InternationalCardholdersInfo();
//		ci.setCardNo(AES.setCarNo(cardNo));
//		ci.setCvv2(AES.setCarNo(cardCVV2));
//		ci.setExpiryDate(AES.setCarNo(cardExDate));
//		ci.setTradeId(trade.getId());
//		this.commonService.save(ci);
//		
//		
//
//		SimpleDateFormat dateformate = new SimpleDateFormat("yyyyMMdd");
//		SimpleDateFormat timeformate = new SimpleDateFormat("HHmmss");
//		// ����
//		XMLGetMessage xgm = new XMLGetMessage();
//		xgm.setTxn_id("0200"); // ������
//		xgm.setPan(cardNo);
//		xgm.setCvv(cardCVV2);
//		xgm.setExp_date(year + month);
//		xgm.setSystrace(trade.getOrderNo().substring(
//				trade.getOrderNo().length() - 6));
//		xgm.setTxn_amt(trade.getRmbAmount() + "");
//		xgm.setTxn_date(dateformate.format(trade.getTradeTime()));
//		xgm.setTxn_time(timeformate.format(trade.getTradeTime()));
//		xgm.setPosid(posNumber);
//		ClientBoc cb = new ClientBoc();
//		cb.setXmlGetMessage(xgm);
//		try {
//			xgm = cb.getMessage("0200");
//		} catch (Exception ex) {
//			// ���ݵĲ���
//			this.responseCode = 19;
//			message = "Your payment is being processed";
//			MD5info = trade.getMerchantOrderNo() + trade.getMoneyType()
//					+ ordercountValue + responseCode + MD5key;
//			md5Value = md5.getMD5ofStr(MD5info);
//			if (merchant.getStatutes().subSequence(6, 7).equals("0")) {
//				this.commonService
//						.deleteBySql("update  international_tradeinfo t  set t.tradestate='2'||substr(t.tradestate,2,(length(t.tradestate)-1)) where t.id='"
//								+ trade.getId() + "'");
//				// -------�����̻��½����޶�-----------------
//				merchant.setMonthTradeMoney(merchant
//						.getMonthTradeMoney()
//						+ trade.getTradeAmount());
//				this.commonService.update(merchant);
//			} else {
//				this.responseCode = 0;
//				this.commonService
//						.deleteBySql("update  international_tradeinfo t  set t.tradestate='0'||substr(t.tradestate,2,(length(t.tradestate)-1)) where t.id='"
//								+ trade.getId() + "'");
//			}
//			return INPUT;
//		}
//		if (StringUtils.isBlank(xgm.getRespcode())) {
//			// ���ݵĲ���
//			this.responseCode = 19;
//			message = "Your payment is being processed";
//			MD5info = trade.getMerchantOrderNo() + trade.getMoneyType()
//					+ ordercountValue + responseCode + MD5key;
//			md5Value = md5.getMD5ofStr(MD5info);
//			if (merchant.getStatutes().subSequence(6, 7).equals("0")) {
//				this.commonService
//						.deleteBySql("update  international_tradeinfo t  set t.tradestate='2'||substr(t.tradestate,2,(length(t.tradestate)-1)) where t.id='"
//								+ trade.getId() + "'");
//				// -------�����̻��½����޶�-----------------
//				merchant.setMonthTradeMoney(merchant
//						.getMonthTradeMoney()
//						+ trade.getTradeAmount());
//				this.commonService.update(merchant);
//			} else {
//				this.responseCode = 0;
//				this.commonService
//						.deleteBySql("update  international_tradeinfo t  set t.tradestate='0'||substr(t.tradestate,2,(length(t.tradestate)-1)) where t.id='"
//								+ trade.getId() + "'");
//			}
//			return INPUT;
//		}
//		// ��DCC����
//		if (xgm.getRespcode().equals("00")) {
//			// ����
//			System.out.println("RRN------"+xgm.getRrn());
//			this.message = BOCPayResult.getName(xgm.getRespcode());
//			this.responseCode = 88;
//			this.commonService
//					.deleteBySql("update  international_tradeinfo t  set t.tradestate='1'||substr(t.tradestate,2,(length(t.tradestate)-1)),t.remark='"
//							+ message
//							+ "' ,t.VIPAuthorizationNo='"
//							+ xgm.getAuth_no()
//							+ "' ,t.VIPBatchNo='"
//							+ xgm.getInvoice()
//							+ "',t.boc_date='"+xgm.getTxn_date()+"',t.boc_time='"+xgm.getTxn_time()+"'," +
//									"t.boc_rrn='"+xgm.getRrn()+"' where t.id='"
//							+ trade.getId() + "'");
//			// -------�����̻��½����޶�-----------------
//			merchant.setMonthTradeMoney(merchant.getMonthTradeMoney()
//					+ trade.getTradeAmount());
//			this.commonService.update(merchant);
//			MD5info = trade.getMerchantOrderNo() + trade.getMoneyType()
//					+ ordercountValue + responseCode + MD5key;
//			md5Value = md5.getMD5ofStr(MD5info);
//
//			// /---------------���ֿ��˷����ʼ�-----------------------////
//			List<InternationalTerminalManager> tmm = this.commonService
//					.list("select tm from InternationalTerminalManager tm where tm.terminalNo='"
//							+ posNumber.trim() + "' ");
//			String billaddressby = null;
//			if (tmm.size() > 0) {
//				InternationalTerminalManager tm = tmm.get(0);
//				billaddressby = tm.getBillingAddress();
//			}
//			EmailInfo emailinfo = new EmailInfo();
//			String mailinfo = emailinfo.getPaymentResultEmail(card
//					.getEmail(), trade.getTradeAmount(), getStates()
//					.getCurrencyTypeByNo(
//							trade.getMoneyType().intValue()), trade
//					.getTradeUrl(), trade.getTradeTime(),
//					billaddressby, trade.getMerchantOrderNo(), trade
//							.getOrderNo(), merchant);
//			try {
//				if(merchant.getStatutes().substring(4, 5).equals("0")){
//					// �����ʼ�,�������ʧ�ܲ������ݿⷢ��
//					CCSendMail.setSendMail(card.getEmail(), mailinfo,
//					"visamasterpay@sitecomplaint.com");
//					System.out.println("TC�ʼ�������");
//					
//				}
//			} catch (Exception e) {
//				// �����ݿ����ȴ������ʼ�
//				
//				shopManagerService.addSendMessages(card.getEmail(),
//						"ecpss@ecpss.cc", mailinfo, "0");
//				System.out.println("TC�ʼ��ȴ��Ժ󷢳�");
//				return INPUT;
//			}
//			return INPUT;
//		} else if (xgm.getRespcode().equals("03")
//				|| xgm.getRespcode().equals("12")
//				|| xgm.getRespcode().equals("13")
//				|| xgm.getRespcode().equals("30")
//				|| xgm.getRespcode().equals("63")
//				|| xgm.getRespcode().equals("76")
//				|| xgm.getRespcode().equals("77")
//				|| xgm.getRespcode().equals("89")
//				|| xgm.getRespcode().equals("94")
//				|| xgm.getRespcode().equals("95")
//				|| xgm.getRespcode().equals("96")
//				|| xgm.getRespcode().equals("98")
//				|| xgm.getRespcode().equals("Z1")
//				|| xgm.getRespcode().equals("99")) {
//			// ���ݵĲ���
//			System.out.println("TC����ϵͳ����" + xgm.getRespcode());
//			this.responseCode = 19;
//			message = "Your payment is being processed";
//			MD5info = trade.getMerchantOrderNo() + trade.getMoneyType()
//					+ ordercountValue + responseCode + MD5key;
//			md5Value = md5.getMD5ofStr(MD5info);
//
//			if (merchant.getStatutes().subSequence(6, 7).equals("0")) {
//				this.commonService
//						.deleteBySql("update  international_tradeinfo t  set t.tradestate='2'||substr(t.tradestate,2,(length(t.tradestate)-1))," +
//								"t.remark='"+xgm.getRespcode()+"' where t.id='"
//								+ trade.getId() + "'");
//			} else {
//				this.responseCode = 0;
//				this.commonService
//						.deleteBySql("update  international_tradeinfo t  set t.tradestate='0'||substr(t.tradestate,2,(length(t.tradestate)-1)) where t.id='"
//								+ trade.getId() + "'");
//			}
//			return INPUT;
//		}
//		else {
//			//��ʾ��ϸʧ��ԭ��
//			message = "Payment Declined!"+xgm.getRespcode();
//			this.responseCode = 0;
//			this.commonService
//					.deleteBySql("update  international_tradeinfo t  set t.tradestate='0'||substr(t.tradestate,2,(length(t.tradestate)-1)),t.remark='"
//							+ message
//							+ "' where t.id='"
//							+ trade.getId() + "'");
//			MD5info = trade.getMerchantOrderNo() + trade.getMoneyType()
//					+ ordercountValue + responseCode + MD5key;
//			md5Value = md5.getMD5ofStr(MD5info);
//			return INPUT;
//		}
//
//	
//		
//		
//		
//		
//		return SUCCESS;
//	}


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
