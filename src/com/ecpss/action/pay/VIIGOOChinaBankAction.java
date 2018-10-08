package com.ecpss.action.pay;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ecpss.action.BaseAction;
import com.ecpss.model.channel.InternationalMigsMerchantNo;
import com.ecpss.model.payment.InternationalCardholdersInfo;
import com.ecpss.model.payment.InternationalTradeinfo;
import com.ecpss.model.shop.InternationalMerchant;
import com.ecpss.model.shop.InternationalTerminalManager;
import com.ecpss.service.iservice.ShopManagerService;
import com.ecpss.util.CCSendMail;
import com.ecpss.util.EmailInfo;
import com.ecpss.util.MD5;

public class VIIGOOChinaBankAction extends BaseAction {
	// �������Ա���
	@Autowired
	@Qualifier("shopManagerService")
	private ShopManagerService shopManagerService;

	private String BillNo;
	// private String Currency;
	private String tradeMoneyType;
	private String Amount;
	private Long Succeed;
	private String Result;
	// private String MD5info;
	private String md5Value;
	private String mD5key; // MD5keyֵ

	private Long merchantno; // �̻�ID
	private String merchantOrderNo; // �������
	private String rorderno; // ������ˮ��
	private Date tradetime; // ����ʱ��

	private String ordercount; // ֧�����
	private String language; // ֧������
	private String bankName; // ֧����������
	private String ReturnURL;
	private String responseCode; // ֧���������
	private String currencyName; // ��������

	private String merchantType; // �̻�����

	private String vpc_Merchant = "0000";

	private String message = null;
	// private String remark = null;

	private String vpc_SecureHash; // ���а�ȫ������

	private String status3DS;
	private String vCSCRequestCode;
	private String txnResponseCode;
	private String hashKeys = new String();
	private String hashValues = new String();
	
	private String authno="";
	private String datetime;
	private String batchno="";
	// �������Գ���
	// ������12B9DF804E9FDC08E8049B35C8D3D4B20
	// ���ԣ�461A89BB77F352F849BAE09EBD2D9910
	static String SECURE_SECRET = "2B9DF804E9FDC08E8049B35C8D3D4B20";
	// This is an array for creating hex chars
	static final char[] HEX_TABLE = new char[] { '0', '1', '2', '3', '4', '5',
			'6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

	// ֧����Ӧ���ؽ��
	public String payResponse() {
		try {
			HttpServletRequest request = ServletActionContext.getRequest();
			HttpServletResponse response = ServletActionContext.getResponse();

			String authorizeno = null2unknown((String) request
					.getParameter("vpc_AuthorizeId"));
			String vipbacthno = (String) request
					.getParameter("vpc_TransactionNo");
			txnResponseCode = (String) request
					.getParameter("vpc_TxnResponseCode");
			ordercount = (String) request.getParameter("vpc_Amount");// ���з��ص�����ҽ��
			rorderno = (String) request.getParameter("vpc_MerchTxnRef");
			InternationalTradeinfo trade = new InternationalTradeinfo();
			String hql = "from InternationalTradeinfo t where t.orderNo='"
					+ rorderno + "'";

			List<InternationalTradeinfo> tradl = this.commonService.list(hql);
			// // ECPSS��ˮ�Ų�Ψһ
			// if (tradl==null) {
			//
			// }else if(tradl.size()!=1){
			//				
			// }
			trade = tradl.get(0);
			InternationalCardholdersInfo icf = (InternationalCardholdersInfo) this.commonService
					.list(
							"from InternationalCardholdersInfo t where t.tradeId ='"
									+ trade.getId() + "' ").get(0);
			ordercount = trade.getTradeAmount() + ""; // ���׽��
			tradeMoneyType = trade.getMoneyType() + ""; // ����
			merchantno = trade.getMerchantId();
			merchantOrderNo = trade.getMerchantOrderNo();
			authno=authorizeno;
			datetime=trade.getLastMan();
			batchno=vipbacthno;
			String email = icf.getEmail();
			Date tradetime = trade.getTradeTime();
			// / StringBuffer channel = new StringBuffer(trade.getChannels());
			// / channel.delete(3, channel.length());
			// / String channels = channel.toString();
			// / String currencyName = tradeService.findParameter("moneyType",
			// Long.valueOf(tradeMoneyType));//������д��
			List<InternationalTerminalManager> timlist = this.commonService
					.list(" from InternationalTerminalManager t where t.terminalNo='88888888'");
			String merBill = "";
			if (timlist.size() == 1) {
				InternationalTerminalManager tim = timlist.get(0);
				merBill = tim.getBillingAddress();
			}

			bankName = "migs";
			// ��ȡ���еķ��ز���
			Map fields = new HashMap();
			for (Enumeration e = request.getParameterNames(); e
					.hasMoreElements();) {
				String fieldName = (String) e.nextElement();
				String fieldValue = request.getParameter(fieldName);
				if ((fieldValue != null) && (fieldValue.length() > 0)) {
					fields.put(fieldName, fieldValue);
				}
			}
			String vpc_Txn_Secure_Hash = null2unknown((String) fields
					.remove("vpc_SecureHash"));

			if (SECURE_SECRET != null
					&& SECURE_SECRET.length() > 0
					&& (fields.get("vpc_TxnResponseCode") != null || fields
							.get("vpc_TxnResponseCode") != "No Value Returned")) {
				String secureHash = hashAllFields(fields);
				// System.out.println("vpc_Txn_Secure_Hash--------------"+vpc_Txn_Secure_Hash);
				// System.out.println("secureHash--------------"+secureHash);
				// if (vpc_Txn_Secure_Hash.equalsIgnoreCase(secureHash)) {
				if (txnResponseCode.equals("0")) {
					Succeed = 1l;// �嵽���ݿ��״̬
					responseCode = "88";
					message = getResponseDescription(txnResponseCode);

					// ��ֿ��˷����ʼ�
					// ====================��ʽʹ��Ҫ�⿪=============================
					// �ʼ�����
					// ����POS��ѯ���˵���ַ

				} else {
					/*
					 * if(channels.equals("vip")){ Succeed="2"; }else{
					 * Succeed="0"; }
					 */
					Succeed = 0l;
					responseCode = "100" + txnResponseCode;
					message = "Your payment failed "
							+ getResponseDescription(txnResponseCode);
				}
				/*
				 * }else { Succeed = 0l; message = "INVALID HASH"; }
				 */
			} else {
				Succeed = 0l;
				message = "No Value Returned";
			}
			InternationalMerchant merchant = new InternationalMerchant();
			merchant = (InternationalMerchant) this.commonService.load(
					InternationalMerchant.class, trade.getMerchantId());
			// �������з��صĽ���޸Ľ��ױ�
			Long id = null;
			if (trade != null) {
				id = trade.getId();

				if (txnResponseCode.equals("0")) {
					merchant.setMonthTradeMoney(merchant.getMonthTradeMoney()
							+ trade.getTradeAmount());
					this.commonService.update(merchant);
					Succeed = 1l;
				}
				this.commonService
						.deleteBySql("update  international_tradeinfo t  "
								+ "set t.tradestate='"
								+ Succeed
								+ "'||substr(t.tradestate,2,(length(t.tradestate)-1)) ,"
								+ "t.remark='" + message + "', "
								+ "t.VIPAuthorizationNo='" + authorizeno + "',"
								+ "t.VIPBatchNo='" + vipbacthno + "' "
								+ "where t.id='" + trade.getId() + "' ");
			}
			
			// �����̻��ķ��ؽ��Md5
			MD5 md5 = new MD5();

			merchantno = merchant.getMerno();
			mD5key = merchant.getMd5key();
			String mD5str = merchant.getMerno() + trade.getMerchantOrderNo() + trade.getMoneyType()
					+ ordercount + responseCode + datetime + mD5key;
			this.ReturnURL = trade.getReturnUrl();
			md5Value = md5.getMD5ofStr(mD5str);
			// /---------------���ֿ��˷����ʼ�-----------------------////
			List<InternationalMigsMerchantNo> tmm = this.commonService
					.list("select tm from InternationalMigsMerchantNo tm where tm.bankMerchantId='"
							+ trade.getVIPTerminalNo() + "' ");
			String terminalNo = null;
			if (tmm.size() > 0) {
				InternationalMigsMerchantNo tm = tmm.get(0);
				terminalNo = tm.getBillingaddress();
			}
			EmailInfo emailinfo = new EmailInfo();
			String mailinfo = emailinfo.getPaymentResultEmail(icf.getEmail(),
					trade.getTradeAmount(), getStates().getCurrencyTypeByNo(
							trade.getMoneyType().intValue()), trade
							.getTradeUrl(), trade.getTradeTime(), terminalNo,
					trade.getMerchantOrderNo(), trade.getOrderNo());
			try {
				// �����ʼ�,�������ʧ�ܲ������ݿⷢ��
				if (Succeed == 1L) {
					CCSendMail.setSendMail(icf.getEmail(), mailinfo,
							"ecpss@ecpss.cc");
					System.out.println("VC�ʼ�������");
				}
			} catch (Exception e) {
				// �����ݿ����ȴ������ʼ�
				shopManagerService.addSendMessages(icf.getEmail(),
						"ecpss@ecpss.cc", mailinfo, "0");
				System.out.println("�ʼ��ȴ��Ժ󷢳�");
				return SUCCESS;
			}

			return SUCCESS;
		} catch (Exception e) {
			e.printStackTrace();
			return INPUT;
		}
	}

	/**
	 * ����MD5��ķ���
	 */
	String hashAllFields(Map fields) {

		// create a list and sort it
		List fieldNames = new ArrayList(fields.keySet());
		Collections.sort(fieldNames);

		// create a buffer for the md5 input and add the secure secret first
		StringBuffer buf = new StringBuffer();
		buf.append(SECURE_SECRET);

		// iterate through the list and add the remaining field values
		Iterator itr = fieldNames.iterator();

		while (itr.hasNext()) {
			String fieldName = (String) itr.next();
			String fieldValue = (String) fields.get(fieldName);
			// System.out.println("fieldName: "+fieldName + "**************"
			// +"fieldValue: "+fieldValue);
			if ((fieldValue != null) && (fieldValue.length() > 0)) {
				buf.append(fieldValue);
			}
		}

		MessageDigest md5 = null;
		byte[] ba = null;

		// create the md5 hash and ISO-8859-1 encode it
		try {
			md5 = MessageDigest.getInstance("MD5");
			ba = md5.digest(buf.toString().getBytes("ISO-8859-1"));
		} catch (Exception e) {
		} // wont happen

		return hex(ba);

	} // end hashAllFields()

	// ----------------------------------------------------------------------------

	/*
	 * This method takes a byte array and returns a string of its contents
	 * 
	 * @param input - byte array containing the input data @return String
	 * containing the output String
	 */
	static String hex(byte[] input) {
		// create a StringBuffer 2x the size of the hash array
		StringBuffer sb = new StringBuffer(input.length * 2);

		// retrieve the byte array data, convert it to hex
		// and add it to the StringBuffer
		for (int i = 0; i < input.length; i++) {
			sb.append(HEX_TABLE[(input[i] >> 4) & 0xf]);
			sb.append(HEX_TABLE[input[i] & 0xf]);
		}
		return sb.toString();
	}

	// ----------------------------------------------------------------------------

	/*
	 * This method takes a data String and returns a predefined value if empty
	 * If data Sting is null, returns string "No Value Returned", else returns
	 * input
	 * 
	 * @param in String containing the data String @return String containing the
	 * output String
	 */
	private static String null2unknown(String in) {
		if (in == null || in.length() == 0) {
			return "No Value Returned";
		} else {
			return in;
		}
	}

	String getResponseDescription(String vResponseCode) {

		String result = "";
		// check if a single digit response code
		// Java cannot switch on a string so turn everything to a char
		char input = vResponseCode.charAt(0);
		switch (input) {
		case '0':
			result = "Transaction Successful";
			break;
		case '1':
			result = "Unknown Error";
			break;
		case '2':
			result = "Bank Declined Transaction";
			break;
		case '3':
			result = "No Reply from Bank";
			break;
		case '4':
			result = "Expired Card";
			break;
		case '5':
			result = "Insufficient Funds";
			break;
		case '6':
			result = "Error Communicating with Bank";
			break;
		case '7':
			result = "Payment Server System Error";
			break;
		case '8':
			result = "Transaction Type Not Supported";
			break;
		case '9':
			result = "Bank declined transaction (Do not contact Bank)";
			break;
		case 'A':
			result = "Transaction Aborted";
			break;
		case 'C':
			result = "Transaction Cancelled";
			break;
		case 'D':
			result = "Deferred transaction has been received and is awaiting processing";
			break;
		case 'F':
			result = "3D Secure Authentication failed";
			break;
		case 'I':
			result = "Card Security Code verification failed";
			break;
		case 'L':
			result = "Shopping Transaction Locked (Please try the transaction again later)";
			break;
		case 'N':
			result = "Cardholder is not enrolled in Authentication Scheme";
			break;
		case 'P':
			result = "Transaction has been received by the Payment Adaptor and is being processed";
			break;
		case 'R':
			result = "Transaction was not processed - Reached limit of retry attempts allowed";
			break;
		case 'S':
			result = "Duplicate SessionID (OrderInfo)";
			break;
		case 'T':
			result = "Address Verification Failed";
			break;
		case 'U':
			result = "Card Security Code Failed";
			break;
		case 'V':
			result = "Address Verification and Card Security Code Failed";
			break;
		case '?':
			result = "Transaction status is unknown";
			break;
		default:
			result = "Unable to be determined";
		}
		return result;
	} // getResponseDescription()

	// ----------------------------------------------------------------------------

	/**
	 * This function uses the QSI AVS Result Code retrieved from the Digital
	 * Receipt and returns an appropriate description for this code.
	 * 
	 * @param vAVSResultCode
	 *            String containing the vpc_AVSResultCode
	 * @return description String containing the appropriate description
	 */
	private String displayAVSResponse(String vAVSResultCode) {

		String result = "";
		if (vAVSResultCode != null || vAVSResultCode.length() == 0) {

			if (vAVSResultCode.equalsIgnoreCase("Unsupported")
					|| vAVSResultCode.equalsIgnoreCase("No Value Returned")) {
				result = "AVS not supported or there was no AVS data provided";
			} else {
				// Java cannot switch on a string so turn everything to a char
				char input = vAVSResultCode.charAt(0);

				switch (input) {
				case 'X':
					result = "Exact match - address and 9 digit ZIP/postal code";
					break;
				case 'Y':
					result = "Exact match - address and 5 digit ZIP/postal code";
					break;
				case 'S':
					result = "Service not supported or address not verified (international transaction)";
					break;
				case 'G':
					result = "Issuer does not participate in AVS (international transaction)";
					break;
				case 'A':
					result = "Address match only";
					break;
				case 'W':
					result = "9 digit ZIP/postal code matched, Address not Matched";
					break;
				case 'Z':
					result = "5 digit ZIP/postal code matched, Address not Matched";
					break;
				case 'R':
					result = "Issuer system is unavailable";
					break;
				case 'U':
					result = "Address unavailable or not verified";
					break;
				case 'E':
					result = "Address and ZIP/postal code not provided";
					break;
				case 'N':
					result = "Address and ZIP/postal code not matched";
					break;
				case '0':
					result = "AVS not requested";
					break;
				default:
					result = "Unable to be determined";
				}
			}
		} else {
			result = "null response";
		}
		return result;
	}

	// ----------------------------------------------------------------------------

	/**
	 * This function uses the QSI CSC Result Code retrieved from the Digital
	 * Receipt and returns an appropriate description for this code.
	 * 
	 * @param vCSCResultCode
	 *            String containing the vpc_CSCResultCode
	 * @return description String containing the appropriate description
	 */
	private String displayCSCResponse(String vCSCResultCode) {

		String result = "";
		if (vCSCResultCode != null || vCSCResultCode.length() == 0) {

			if (vCSCResultCode.equalsIgnoreCase("Unsupported")
					|| vCSCResultCode.equalsIgnoreCase("No Value Returned")) {
				result = "CSC not supported or there was no CSC data provided";
			} else {
				// Java cannot switch on a string so turn everything to a char
				char input = vCSCResultCode.charAt(0);

				switch (input) {
				case 'M':
					result = "Exact code match";
					break;
				case 'S':
					result = "Merchant has indicated that CSC is not present on the card (MOTO situation)";
					break;
				case 'P':
					result = "Code not processed";
					break;
				case 'U':
					result = "Card issuer is not registered and/or certified";
					break;
				case 'N':
					result = "Code invalid or not matched";
					break;
				default:
					result = "Unable to be determined";
				}
			}

		} else {
			result = "null response";
		}
		return result;
	}

	// ----------------------------------------------------------------------------

	/**
	 * This method uses the 3DS verStatus retrieved from the Response and
	 * returns an appropriate description for this code.
	 * 
	 * @param vpc_VerStatus
	 *            String containing the status code
	 * @return description String containing the appropriate description
	 */
	private String getStatusDescription(String vStatus) {

		String result = "";
		if (vStatus != null && !vStatus.equals("")) {

			if (vStatus.equalsIgnoreCase("Unsupported")
					|| vStatus.equals("No Value Returned")) {
				result = "3DS not supported or there was no 3DS data provided";
			} else {

				// Java cannot switch on a string so turn everything to a
				// character
				char input = vStatus.charAt(0);

				switch (input) {
				case 'Y':
					result = "The cardholder was successfully authenticated.";
					break;
				case 'E':
					result = "The cardholder is not enrolled.";
					break;
				case 'N':
					result = "The cardholder was not verified.";
					break;
				case 'U':
					result = "The cardholder's Issuer was unable to authenticate due to some system error at the Issuer.";
					break;
				case 'F':
					result = "There was an error in the format of the request from the merchant.";
					break;
				case 'A':
					result = "Authentication of your Merchant ID and Password to the ACS Directory Failed.";
					break;
				case 'D':
					result = "Error communicating with the Directory Server.";
					break;
				case 'C':
					result = "The card type is not supported for authentication.";
					break;
				case 'S':
					result = "The signature on the response received from the Issuer could not be validated.";
					break;
				case 'P':
					result = "Error parsing input from Issuer.";
					break;
				case 'I':
					result = "Internal Payment Server system error.";
					break;
				default:
					result = "Unable to be determined";
					break;
				}
			}
		} else {
			result = "null response";
		}
		return result;
	}

	public String getHashKeys() {
		return hashKeys;
	}

	public void setHashKeys(String hashKeys) {
		this.hashKeys = hashKeys;
	}

	public String getHashValues() {
		return hashValues;
	}

	public void setHashValues(String hashValues) {
		this.hashValues = hashValues;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getMD5key() {
		return mD5key;
	}

	public void setMD5key(String md5key) {
		mD5key = md5key;
	}

	public Long getMerchantno() {
		return merchantno;
	}

	public void setMerchantno(Long merchantno) {
		this.merchantno = merchantno;
	}

	public String getMerchantOrderNo() {
		return merchantOrderNo;
	}

	public void setMerchantOrderNo(String merchantOrderNo) {
		this.merchantOrderNo = merchantOrderNo;
	}

	public String getOrdercount() {
		return ordercount;
	}

	public void setOrdercount(String ordercount) {
		this.ordercount = ordercount;
	}

	public String getStatus3DS() {
		return status3DS;
	}

	public void setStatus3DS(String status3DS) {
		this.status3DS = status3DS;
	}

	public String getTxnResponseCode() {
		return txnResponseCode;
	}

	public void setTxnResponseCode(String txnResponseCode) {
		this.txnResponseCode = txnResponseCode;
	}

	public String getVCSCRequestCode() {
		return vCSCRequestCode;
	}

	public void setVCSCRequestCode(String requestCode) {
		vCSCRequestCode = requestCode;
	}

	/*
	 * public String getVpc_ReturnURL() { return vpc_ReturnURL; }
	 * 
	 * public void setVpc_ReturnURL(String vpc_ReturnURL) { this.vpc_ReturnURL =
	 * vpc_ReturnURL; }
	 */

	public String getRorderno() {
		return rorderno;
	}

	public void setRorderno(String rorderno) {
		this.rorderno = rorderno;
	}

	public String getVpc_SecureHash() {
		return vpc_SecureHash;
	}

	public void setVpc_SecureHash(String vpc_SecureHash) {
		this.vpc_SecureHash = vpc_SecureHash;
	}

	public String getVpc_Merchant() {
		return vpc_Merchant;
	}

	public void setVpc_Merchant(String vpc_Merchant) {
		this.vpc_Merchant = vpc_Merchant;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getMerchantType() {
		return merchantType;
	}

	public void setMerchantType(String merchantType) {
		this.merchantType = merchantType;
	}

	public String getReturnURL() {
		return ReturnURL;
	}

	public void setReturnURL(String returnURL) {
		ReturnURL = returnURL;
	}

	public String getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}

	public String getCurrencyName() {
		return currencyName;
	}

	public void setCurrencyName(String currencyName) {
		this.currencyName = currencyName;
	}

	public String getBillNo() {
		return BillNo;
	}

	public void setBillNo(String billNo) {
		BillNo = billNo;
	}

	public String getAmount() {
		return Amount;
	}

	public void setAmount(String amount) {
		Amount = amount;
	}

	public String getResult() {
		return Result;
	}

	public void setResult(String result) {
		Result = result;
	}

	public String getMd5Value() {
		return md5Value;
	}

	public void setMd5Value(String md5Value) {
		this.md5Value = md5Value;
	}

	public String getTradeMoneyType() {
		return tradeMoneyType;
	}

	public void setTradeMoneyType(String tradeMoneyType) {
		this.tradeMoneyType = tradeMoneyType;
	}

	public Long getSucceed() {
		return Succeed;
	}

	public void setSucceed(Long succeed) {
		Succeed = succeed;
	}

	public String getAuthno() {
		return authno;
	}

	public void setAuthno(String authno) {
		this.authno = authno;
	}

	public String getDatetime() {
		return datetime;
	}

	public void setDatetime(String datetime) {
		this.datetime = datetime;
	}

	public String getBatchno() {
		return batchno;
	}

	public void setBatchno(String batchno) {
		this.batchno = batchno;
	}

}
