package com.ecpss.action.pay;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import sun.misc.BASE64Decoder;
import vpn.VpnUtil;
import vpn.VpnUtil_Moto;
import vpn.YinlianMessage;
import vpn.YinlianUtil;

import com.ecpss.action.BaseAction;
import com.ecpss.action.pay.fast.TradUtil;
import com.ecpss.action.pay.fast.TradeMessage;
import com.ecpss.model.channel.InternationalMerchantChannels;
import com.ecpss.model.payment.InternationalCardholdersInfo;
import com.ecpss.model.payment.InternationalTradeinfo;
import com.ecpss.model.risk.InternationalHighRisklist;
import com.ecpss.model.risk.InternationalSensitiveInfo;
import com.ecpss.model.shop.InternationalExchangerate;
import com.ecpss.model.shop.InternationalMerchant;
import com.ecpss.model.shop.InternationalMerchantManager;
import com.ecpss.model.shop.InternationalTerminalManager;
import com.ecpss.model.shop.InternationalTradecondition;
import com.ecpss.service.iservice.ShopManagerService;
import com.ecpss.util.AES;
import com.ecpss.util.CCSendMail;
import com.ecpss.util.EmailInfo;
import com.ecpss.util.GetBatchNo;
import com.ecpss.util.MD5;
import com.ecpss.util.StringUtil;

public class SfePayAction1 extends BaseAction {
	Logger logger = Logger.getLogger(SfePayAction.class.getName());
	@Autowired
	@Qualifier("tradeManager")
	private TradeManager tradeManager;
	@Autowired
	@Qualifier("shopManagerService")
	private ShopManagerService shopManagerService;
	private String orderno;
	private String merNo;
	private String Amount;
	private double rmbmoney = 0; // ����ҽ��׽��
	private String Currency;
	private String tradeAdd;
	private String ReturnURL;
	private Date tradetime; 
	private String cardNo;
	private String cvv2;
	private String month;
	private String year;
	private String ordercount;
	private String merchantOrderNo;
	private int responseCode;
	private String MD5info;
	private String MD5key;
	private String md5Value;
	private String message;
	private String remark;
	private String ip;
	private String cartype;
	private String country;
	private String cookie;
	private String firstname;
	private String lastname;
	private String email;
	private String phone;
	private String state;
	private String city;
	private String address;
	private String zipcode;
	private String products;
	private String redInfo;
	private Double tradeMoney;// ֧�����
	private String shippingFirstName;
	private String shippingLastName;
	private String shippingAddress;
	private String shippingCity;
	private String shippingSstate;
	private String shippingCountry;
	private String shippingZipcode;
	private String shippingEmail;
	private String shippingPhone;
	private String userbrowser;
	private String billaddressby;
	public String pay(){
		try{
		logger.info("����sfe�̻��ӿ�"+merNo);
		GetBatchNo ut = new GetBatchNo();
		InternationalMerchant merchant = (InternationalMerchant) this.commonService.uniqueResult("from InternationalMerchant where merno='"+merNo+"'");
		List changerates = this.commonService
				.getByList("select t.id,t.rate,t.type from international_exchangerate t,international_moneykindname m  where t.moneykindno=m.id "
						+ "and m.moneykindno="
						+ this.Currency
						+ " and t.executetime<sysdate-1   and t.type='1' order by t.executetime desc  "); // ���׻���
		// ��ȡ�������
		List changeratesbalance = this.commonService
				.getByList("select t.id,t.rate,t.type from international_exchangerate t,international_moneykindname m  where t.moneykindno=m.id "
						+ "and m.moneykindno="
						+ this.Currency
						+ " and t.executetime<sysdate-1   and t.type='2' order by t.executetime desc  "); // �������
		InternationalExchangerate changerate = new InternationalExchangerate();
		InternationalExchangerate settlementrate = new InternationalExchangerate();
		for (int i = 0; i < changerates.size(); i++) {
			Object[] tem = (Object[]) changerates.get(0);
			changerate.setId(Long.valueOf(tem[0].toString()));
			changerate.setRate(Double.valueOf(tem[1].toString()));
			changerate.setType("1");
		}
		for (int i = 0; i < changeratesbalance.size(); i++) {
			Object[] tem = (Object[]) changeratesbalance.get(0);
			settlementrate.setId(Long.valueOf(tem[0].toString()));
			settlementrate.setRate(Double.valueOf(tem[1].toString()));
			settlementrate.setType("2");
		}
		Double traderate = Double.valueOf(changerate.getRate());
		if (StringUtils.isNotBlank(Amount)) {
			ordercount = Amount.replace(",", "");
			ordercount = ordercount.replace(" 0", "");
			tradeMoney = Double.valueOf(ordercount);
		}
		if (traderate != null) {
			rmbmoney = traderate * tradeMoney;
			
			BigDecimal b = new BigDecimal(rmbmoney);  
			rmbmoney = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();  

		}
		orderno = ut.getOrderinfo(merNo);
		InternationalTradeinfo tradeInfo = new InternationalTradeinfo();
		tradeInfo.setOrderNo(orderno);
		tradeInfo.setMerchantOrderNo(merchantOrderNo);
		tradeInfo.setMerchantId(merchant.getId());
		tradeInfo.setTradeTime(tradetime);
		tradeInfo.setTradeAmount(Double.valueOf(this.Amount));
		tradeInfo.setRmbAmount(Double.valueOf(rmbmoney));
		tradeInfo.setMoneyType(Long.valueOf(Currency));
		tradeInfo.setTradeState("30000000000000000000");
		tradeInfo.setTradeRate(changerate.getId());
		tradeInfo.setBalanceRate(settlementrate.getId());
		tradeInfo.setTradeUrl(tradeAdd);
		tradeInfo.setReturnUrl(this.ReturnURL);
		tradeInfo.setLastDate(new Date());
		tradeInfo.setBackCount(0d);
		this.commonService.saveOrUpdate(tradeInfo);
		MD5 md5 = new MD5();
		// / ��ȡ�ֿ��˵�ϵͳ��Ϣ
		logger.info("*********************��ȡ�ֿ��˵�ϵͳ��Ϣ����***************************");
		String expirydate = month + year;

		String hql = "from InternationalTradeinfo t where t.orderNo='"
				+ orderno + "'";

			DecimalFormat decimalFormat = new DecimalFormat(
							"##############0.00");
			String ordercountValue = decimalFormat.format(tradeInfo.getTradeAmount());

			logger.info("*********************ת��������***************************");
		/** ******************���տ���******************** */
		// ��ȡ���ÿ�ǰ6λ����
		String backcardNo6 = (new StringBuilder(cardNo)).delete(6,
				cardNo.length()).toString();
		String backcardNo9 = (new StringBuilder(cardNo)).delete(9,
				cardNo.length()).toString();


		// �����̻���,������Ϣ��ȡͨ��
		logger.info("*********************������Ϣ��ȡͨ��***************************"+cartype);
		String sql = "select a.channelname , d.id ,b.id as tid from international_channels a ,international_creditcardtype b,international_mercreditcard c ,international_merchantchannels d where a.id=d.channelid and d.merchantid='"
				+ merchant.getId()
				+ "' and b.shortname='"
				+ cartype
				+ "' and d.id=c.merchannelid and b.id=c.creditcardid and d.onoff='1' and c.onoff='1'";
		List chanellist = this.commonService.getByList(sql);
		// �̻�ͨ��ID
		String merchanID = "";
		// ͨ������
		String chanelName = "";
		// ����ID
		Long carType = 0l;
		Boolean V5Chanel=false;
		if("US".equals(country.substring(3, 5))||"CA".equals(country.substring(3, 5))){
			logger.info("���׹��ң�"+country);
			V5Chanel=true;
		}
		if (chanellist.size() > 0) {
			for(int i=0;i<chanellist.size();i++){
				Object[] tem = (Object[]) chanellist.get(i);
				String YlChanel=(tem[0].toString()).split("-")[0];
				if(V5Chanel.equals(Boolean.FALSE)&&"V5".equals(YlChanel)){
					merchanID = tem[1].toString();
					chanelName = tem[0].toString();
					carType = Long.valueOf(tem[2].toString());
					break;
				}else if(V5Chanel.equals(Boolean.FALSE)&&"YL".equals(YlChanel)){
					merchanID = tem[1].toString();
					chanelName = tem[0].toString();
					carType = Long.valueOf(tem[2].toString());
					break;
				}else{
					merchanID = tem[1].toString();
					chanelName = tem[0].toString();
					carType = Long.valueOf(tem[2].toString());
				}
			}
		} else {
			if("4".equals(cartype)){
			message = "Payment Declined (VisaCard cannot be used at this moment, please use your master card. Thank you)";
			}
			if("5".equals(cartype)){
			message = "Payment Declined (MasterCard cannot be used at this moment, please use your VISA card. Thank you)";
			}
			remark = "ͨ��δ����/The channel is not set";
			responseCode = 16;

			String re[]=remark.split("/");
			tradeInfo.setRemark(re[0]);
			this.commonService.update(tradeInfo);
			logger.info("*********************֧�����������***************************"+responseCode);
			return SUCCESS;
		}
		logger.info("*********************������Ϣ��ȡͨ������***************************");
		// �̻�ͨ��
		logger.info("*********************�̻�ͨ����Ϣ***************************"+merchanID);
		InternationalMerchantChannels im = (InternationalMerchantChannels) this.commonService
				.load(InternationalMerchantChannels.class,
						Long.valueOf(merchanID));
		logger.info("*********************�̻�ͨ������***************************"+chanelName);
		String cardTypeNum = chanelName.substring(0, 3).toString();
		// �����̻���,������Ϣ��ȡͨ��
		logger.info(cartype + "��ͷ�Ŀ��ߵ�ͨ����:" + cardTypeNum);
		logger.info(tradeInfo.getId());
		String carlist = "select count(*) from international_cardholdersinfo t where t.tradeid='"
				+ tradeInfo.getId() + "'";
		int carInfoList = 0;
		carInfoList = this.tradeManager.intBySql(carlist);
		if (carInfoList > 0) {
			message = "Payment Declined";
			remark = "ͬһ�ʽ����ظ�֧��/The same transaction duplicate payment";
			responseCode = 31;
			tradeInfo.setTradeState("0"
					+ tradeInfo.getTradeState().substring(1,
							tradeInfo.getTradeState().length()));
			String re[]=remark.split("/");
			tradeInfo.setRemark(re[0]);
			this.commonService.update(tradeInfo);
			logger.info("*********************֧�����������***************************"+responseCode);
			return SUCCESS;
		}
		InternationalCardholdersInfo card = new InternationalCardholdersInfo();
		card.setTradeId(tradeInfo.getId());
		card.setIp(ip);
		card.setCookie(cookie);
		card.setCardNo(AES.setCarNo(cardNo));
		card.setCardNo6(AES.setCarNo(cardNo.substring(0,6)));
		card.setCardNo9(AES.setCarNo(cardNo.substring(0,9)));
		card.setCardNo12(AES.setCarNo(cardNo.substring(0,12)));
		card.setCardNo4(AES.setCarNo(cardNo.substring(cardNo.length()-4)));
		card.setFirstName(firstname);
		card.setLastName(lastname);
		card.setEmail(email);
		card.setPhone(phone);

		logger.info("*********************���Ҽ���***************************"+country);
		card.setCountry(country.substring(3, 5));
		card.setState(state);
		card.setCity(city);
		card.setAddress(address);
		card.setZipcode(zipcode);
		card.setShippingAddress(this.getShippingAddress());
		card.setShippingCity(this.getShippingCity());
		card.setShippingCountry(this.getShippingCountry().substring(3, 5));
		card.setShippingEmail(this.getShippingEmail());
		card.setShippingFullName(this.getShippingFirstName() + " "
				+ this.getShippingLastName());
		card.setShippingPhone(this.getShippingPhone());
		card.setShippingState(this.getState());
		card.setShippingZip(this.getShippingZipcode());
		card.setProductInfo(this.getProducts());
		this.commonService.save(card);
		 tradeInfo.setTradeChannel(Long.valueOf(merchanID));
		 this.commonService.update(tradeInfo);
		 List<Long> backCardValue=this.commonService.list("select t.merId from InternationalBacklist t where t.cardno='"
						+ cardNo + "' ");
		int riskCardValue = this.tradeManager
				.intBySql("select count(1) from InternationalRisklist t where t.cardno='"
						+ cardNo + "' ");
		Calendar calendar = Calendar.getInstance();// ��ʱ��ӡ����ȡ����ϵͳ��ǰʱ��
		calendar.add(Calendar.DATE, -1); // �õ�ǰһ��
		String yestedayDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
				.format(calendar.getTime());
		// ����ip��cardNober��Email��COCKET ��Ϣ��ѯ�ֿ���24Сʱ�ڽ��׵Ĵ���(�ɹ��ģ���������ȷ�ϵĽ���)
		// ��ȡ��������������Ϣ
		List<InternationalTradecondition> localrisk;
		localrisk = this.commonService
				.list("from  InternationalTradecondition f where f.merchantId='"
						+ merchant.getId() + "'");
		if (localrisk.size() == 0) {
			localrisk = this.commonService
					.list("from  InternationalTradecondition f where f.merchantId is null");
		}
		Long localIP = 0l;
		Long localEMAIL = 0l;
		Long localCOCKET = 0l;
		Long localCarNO = 0l;
		Long localPhone = 0l;
		for (int i = 0; i < localrisk.size(); i++) {
			InternationalTradecondition tem = localrisk.get(i);
			if ("1".equals(tem.getItemno() + "")) {
				localIP = tem.getTradenumber();
			}
			if ("2".equals(tem.getItemno() + "")) {
				localEMAIL = tem.getTradenumber();
			}
			if ("3".equals(tem.getItemno() + "")) {
				localCarNO = tem.getTradenumber();
			}
			if ("4".equals(tem.getItemno() + "")) {
				localPhone = tem.getTradenumber();
			}
			if ("5".equals(tem.getItemno() + "")) {
				localCOCKET = tem.getTradenumber();
			}

		}
		// ��ȡ�̻�����ֵ
		List<InternationalMerchantManager> merchantmanagers = new ArrayList();
		merchantmanagers = this.commonService
				.list("from  InternationalMerchantManager f where f.merchantId='"
						+ merchant.getId() + "'");
		if (merchantmanagers.size() == 0) {
			merchantmanagers = this.commonService
					.list("from  InternationalMerchantManager f where f.merchantId is null ");
		}
		InternationalMerchantManager merchantmanager = merchantmanagers
				.get(0);

		// ��ȡPOS
		List<InternationalTerminalManager> it = this.commonService
				.list("select tm from InternationalMerchantTerminal mt,InternationalTerminalManager tm "
						+ "where tm.id=mt.terminalId "
						+ "and tm.channelId='"
						+ im.getChannelId()
						+ "' "
						+ "and mt.isopen=1 "
						+ "and mt.merchantId="
						+ merchant.getId());
		if (it.size() == 0) {
			it = this.commonService
					.list("from InternationalTerminalManager t where t.channelId='"
							+ im.getChannelId()
							+ "' and t.creditCardId='"
							+ carType
							+ "' and t.onoff='1' and t.isuses='0'");
			if (it.size() <= 0) {

				this.commonService
						.deleteBySql("update international_terminalmanager t set t.isuses='0' where  t.id in (select tf.id  from International_TerminalManager tf where tf.channelId='"
								+ im.getChannelId()
								+ "' and tf.creditCardId='"
								+ carType
								+ "')");
				it = this.commonService
						.list("from InternationalTerminalManager t where t.channelId='"
								+ im.getChannelId()
								+ "' and t.creditCardId='"
								+ carType
								+ "' and t.onoff='1' and t.isuses='0'");
				if (it.size() <= 0) {	
					message = "Payment Declined";
					remark = "ͨ��δ�����ն�/Channel undistributed terminal";
					responseCode = 27;
					String re[]=remark.split("/");
					tradeInfo.setRemark(re[0]);
					this.commonService.update(tradeInfo);
					logger.info("*********************֧�����������***************************"+responseCode);
					return SUCCESS;
				}
			}
		}

		String posNumber = "";
		String posMerchantNo = "";
		Long terminalId = 0L;
		if (it.size() > 0) {
			// /����ͨ����ն˺ŵ��̻�
			List<InternationalTerminalManager> itmerchant = this.commonService
					.list("select tm from InternationalMerchantTerminal mt,InternationalTerminalManager tm "
							+ "where tm.id=mt.terminalId "
							+ "and tm.channelId='"
							+ im.getChannelId()
							+ "' "
							+ "and mt.isopen=1 "
							+ "and mt.merchantId=" + merchant.getId());
			if (itmerchant.size() > 1) {
				it = this.commonService
						.list("select tm from InternationalMerchantTerminal mt,InternationalTerminalManager tm "
								+ "where tm.id=mt.terminalId "
								+ "and tm.channelId='"
								+ im.getChannelId()
								+ "'  "
								+ "and mt.merchantId="
								+ merchant.getId() + " and tm.isuses='0'");

				if (it.size() == 0) {
					// /����
					this.commonService
							.deleteBySql("update international_terminalmanager t set t.isuses='0' where  t.id in ("
									+ "select tm.id from International_Terminalmerchant mt,international_terminalmanager tm "
									+ "where tm.id=mt.terminalId "
									+ "and tm.channelId='"
									+ im.getChannelId()
									+ "' "
									+ "and mt.merchantId="
									+ merchant.getId() + " )");
					it = this.commonService
							.list("select tm from InternationalMerchantTerminal mt,InternationalTerminalManager tm "
									+ "where tm.id=mt.terminalId "
									+ "and tm.channelId='"
									+ im.getChannelId()
									+ "' "
									+ "and mt.isopen=1 "
									+ "and mt.merchantId="
									+ merchant.getId()
									+ " and tm.isuses='0'");
				}
			}

			// /��ȡ����ʹ���ն�
			posNumber = it.get(0).getTerminalNo();
			posMerchantNo = it.get(0).getMerchantNo();
			terminalId = it.get(0).getId();
		}
		logger.info("pos==================" + posNumber);
		this.commonService
				.deleteBySql("update international_terminalmanager t set t.isuses='1' where  t.id='"
						+ it.get(0).getId() + "'");
		// �����ն�
		this.commonService
				.deleteBySql("update  international_tradeinfo t  set t.VIPTerminalNo='"
						+ posNumber
						+ "' where t.id='"
						+ tradeInfo.getId()
						+ "'");

		// ��ȡ���׽��
		// ���ʽ���Ĭ�ϲ��ܴ���600Ԫ

		int backCardValue6 = this.tradeManager
				.intBySql("select count(*) from Internationalbacklist t where t.cardno='"
						+ backcardNo6 + "' ");
		int backCardValue9 = this.tradeManager
				.intBySql("select count(*) from Internationalbacklist t where t.cardno='"
						+ backcardNo9 + "' ");
		String chnals = chanelName.split("-")[0];
		// ��֤������
		List<Long> backEmailValue=this.commonService.list("select t.merId from InternationalBacklist t where lower(t.email)='"
						+ email.trim().toLowerCase() + "' ");
		// ��֤��IP
		List<Long> backIpValue=this.commonService.list("select t.merId from InternationalBacklist t where t.ip='"
						+ ip + "' ");
		if (tradeInfo.getTradeAmount() > merchantmanager.getPenQuota()) {
			message = "Payment Declined";
			remark = "���ʳ���/Single pen limit";
			responseCode = 3;
			logger.info("����״̬��+++++++++" + responseCode);
			tradeInfo.setTradeState("0"
					+ tradeInfo.getTradeState().substring(1,
							tradeInfo.getTradeState().length()));
			String re[]=remark.split("/");
			tradeInfo.setRemark(re[0]);
			this.commonService.update(tradeInfo);
			logger.info("*********************֧�����������***************************"+responseCode);
			return SUCCESS;
		}
		//��֤����������Ϣ
		if(StringUtils.isBlank(products)){
			products="sfepay";
		}
		logger.info("****��ʼ��֤������Ϣ********");
		Boolean isValiBill=validateSensitive("1",merchantOrderNo, products);
		Boolean isValiEmail=validateSensitive("2",email, products);
		String tradWeb[] = ReturnURL.split("/");
		Boolean isValiTradurl=validateSensitive("3",tradWeb[2], products);
		logger.info("isValiBill:"+isValiBill);
		logger.info("isValiEmail:"+isValiEmail);
		logger.info("isValiTradurl:"+isValiTradurl);
		if(isValiBill.equals(Boolean.TRUE)||isValiEmail.equals(Boolean.TRUE)||isValiTradurl.equals(Boolean.TRUE)){
			message = "Payment Declined��";
			remark = "Payment Declined��00";
			responseCode = 0;
			logger.info("����״̬��+++++++++" + responseCode);
			tradeInfo.setTradeState("0"
					+ tradeInfo.getTradeState().substring(1,
							tradeInfo.getTradeState().length()));
			tradeInfo.setRemark(remark);
			this.commonService.update(tradeInfo);
			return SUCCESS;
		}
		logger.info("****��֤������Ϣ����********");
		logger.info("****��ʼ��������֤********");
		Boolean isVisaVal=validateVisa(cardNo,email,ip,tradWeb[2]);
		if(isVisaVal.equals(Boolean.TRUE)){
			InternationalHighRisklist rl=new InternationalHighRisklist();
			List<InternationalHighRisklist> lrl=commonService.list("from InternationalHighRisklist where cardno='"+cardNo+"' or email='"+email+"' or ip='"+ip+"'");
			Boolean c=false;
			Boolean e=false;
			Boolean i=false;
			for(InternationalHighRisklist rl2:lrl){
				if(cardNo.equals(rl2.getCardno())){
					c=true;
				}
				if(email.equals(rl2.getEmail())){
					e=true;
				}
				if(ip.equals(rl2.getIp())){
					i=true;
				}
			}
			if(c.equals(Boolean.FALSE)||e.equals(Boolean.FALSE)||i.equals(Boolean.FALSE)){
				rl.setCardno(cardNo);
				rl.setEmail(email);
				rl.setIp(ip);
				rl.setMerId(merchant.getMerno());
				rl.setTradeUrl(tradWeb[2]);
				rl.setOperator("system");
				rl.setCreateDate(new Date());
				if(StringUtils.isNotBlank(redInfo)){
					rl.setErrorColumn(redInfo);
				}
				commonService.save(rl);
			}
			logger.info("�ɼ�������Ϣ����");
//			this.commonService.deleteBySql("update InternationalWebchannels set isblack='1' where tradeWebsite='"+a[2]+"'");
//			logger.info("�ر�"+merchant.getMerno()+"�Ľ�����ַ");
			message = "Payment Declined��";
			remark = "Payment Declined��01";
			responseCode = 0;
			logger.info("����״̬��+++++++++" + responseCode);
			tradeInfo.setTradeState("0"
					+ tradeInfo.getTradeState().substring(1,
							tradeInfo.getTradeState().length()));
			tradeInfo.setRemark(remark);
			this.commonService.update(tradeInfo);
			EmailInfo emailinfo = new EmailInfo();
			String mailinfo = emailinfo.getRiskInfoToSystem(merchant.getMerno(),merchantOrderNo,
					ordercountValue,new Long(Currency),cardNo,email,ip,tradWeb[2],redInfo);
			try {
				// �����ʼ�,�������ʧ�ܲ������ݿⷢ��
				CCSendMail.setSendMail("983321897@qq.com", mailinfo,
						"sfepay@sfepay.com");
				logger.info("������Ϣ�ʼ�������");
			} catch (Exception ex) {
				logger.error(ex);
				logger.info("������Ϣ�ʼ�����ʧ��");
				return SUCCESS;
			}
			return SUCCESS;
		}
		logger.info("****������������֤********");
		// �ڿ���
		for(Long backCard:backCardValue){
		if (backCard==null||(merchant.getId()).equals(backCard)) {
			message = "Payment Declined";
			remark = "�ڿ�/banned the trading of card";
			responseCode = 2;
			logger.info("����״̬��+++++++++" + responseCode);
			tradeInfo.setTradeState("0"
					+ tradeInfo.getTradeState().substring(1,
							tradeInfo.getTradeState().length()));
			String re[]=remark.split("/");
			tradeInfo.setRemark(re[0]);
			this.commonService.update(tradeInfo);
			logger.info("responseCode--------------" + responseCode);
			return SUCCESS;
			}
		}
		
		// ������
		for(Long backEmail:backEmailValue){	
		if (backEmail==null||(merchant.getId()).equals(backEmail) ) {
			message = "Payment Declined";
			remark = "������/banned the trading of email";
			responseCode = 2;
			logger.info("����״̬��+++++++++" + responseCode);
			tradeInfo.setTradeState("0"
					+ tradeInfo.getTradeState().substring(1,
							tradeInfo.getTradeState().length()));
			String re[]=remark.split("/");
			tradeInfo.setRemark(re[0]);
			this.commonService.update(tradeInfo);
			logger.info("responseCode--------------" + responseCode);
			return SUCCESS;
		}
		}
		// IP
		for(Long backIp:backIpValue){
		if (backIp==null||(merchant.getId()).equals(backIp)) {
			message = "Payment Declined";
			remark = "��IP/banned the trading of ip";
			responseCode = 2;
			logger.info("����״̬��+++++++++" + responseCode);
			tradeInfo.setTradeState("0"
					+ tradeInfo.getTradeState().substring(1,
							tradeInfo.getTradeState().length()));
			String re[]=remark.split("/");
			tradeInfo.setRemark(re[0]);
			this.commonService.update(tradeInfo);
			logger.info("responseCode--------------" + responseCode);
			return SUCCESS;
		}
		}
		// �ڿ�bean
		if (backCardValue6 > 0 || backCardValue9 > 0) {
			message = "Payment Declined";
			remark = "�ڿ�/banned the trading of card";
			responseCode = 17;
			logger.info("����״̬��+++++++++" + responseCode);
			tradeInfo.setTradeState("0"
					+ tradeInfo.getTradeState().substring(1,
							tradeInfo.getTradeState().length()));
			String re[]=remark.split("/");
			tradeInfo.setRemark(re[0]);
			this.commonService.update(tradeInfo);
			logger.info("*********************֧�����������***************************"+responseCode);
			return SUCCESS;
			// response.sendRedirect(returnurl);
		}
		// ͬһ����
		int carno = 0;
//		List carno=this.commonService.list("select t from InternationalCardholdersInfo f,InternationalTradeinfo t where f.tradeId=t.id and f.cardNo='"
//				+ AES.setCarNo(this.cardNo)+ "' and t.merchantId='"+ merchant.getId()+ "' and substring(t.tradeState,1,1) in(1,2,4,5,6) and t.tradeTime>to_date('"
//				+ yestedayDate + "','yyyy-MM-dd hh24:mi:ss')");
		carno = this.tradeManager
				.intBySql("select count(1) from international_cardholdersinfo f, international_tradeinfo t where f.tradeid=t.id and f.cardno='"
						+ AES.setCarNo(this.cardNo)
						+ "' and t.merchantid='"
						+ merchant.getId()
						+ "' and substr(t.tradestate,1,1) in(1,2,4,5,6) and t.tradetime>to_date('"
						+ yestedayDate + "','yyyy-MM-dd hh24:mi:ss')");
		if (Long.valueOf(carno) >= localCarNO) {
			message = "Payment Declined";
			remark = "�ظ�����/Repeat business";
			responseCode = 7;
			logger.info("����״̬��+++++++++" + responseCode);
			tradeInfo.setTradeState("3"
					+ tradeInfo.getTradeState().substring(1,
							tradeInfo.getTradeState().length()));
			String re[]=remark.split("/");
			tradeInfo.setRemark(re[0]);
			this.commonService.update(tradeInfo);
			logger.info("*********************֧�����������***************************"+responseCode);
			return SUCCESS;

		}

		// ͬһ����ʧ�ܴ���
		int carnoerror = 0;
		carnoerror = this.tradeManager
				.intBySql("select count(1) from international_cardholdersinfo f, international_tradeinfo t where f.tradeid=t.id and f.cardno='"
						+ AES.setCarNo(this.cardNo)
						+ "' and t.merchantid='"
						+ merchant.getId()
						+ "' and substr(t.tradestate,1,1) ='0' and t.tradetime>to_date('"
						+ yestedayDate + "','yyyy-MM-dd hh24:mi:ss')");
		if (Long.valueOf(carnoerror) >= 2) {
			message = "Payment Declined";
			remark = "�ظ�ʧ�ܴ�������/Repeated failure many times";
			responseCode = 7;
			logger.info("����״̬��+++++++++" + responseCode);
			tradeInfo.setTradeState("3"
					+ tradeInfo.getTradeState().substring(1,
							tradeInfo.getTradeState().length()));
			String re[]=remark.split("/");
			tradeInfo.setRemark(re[0]);
			this.commonService.update(tradeInfo);
			logger.info("*********************֧�����������***************************"+responseCode);
			return SUCCESS;
		}
		
		// ͬһip
		int ipcount = 0;
		ipcount = this.tradeManager
				.intBySql("select count(1) from international_cardholdersinfo f, international_tradeinfo t where f.tradeid=t.id and f.ip='"
						+ ip
						+ "' and t.merchantid='"
						+ merchant.getId()
						+ "' and substr(t.tradestate,1,1) in(1,2,4,5,6) and t.tradetime>to_date('"
						+ yestedayDate + "','yyyy-MM-dd hh24:mi:ss')");
//		List ipcount=this.commonService.list("select t from InternationalCardholdersInfo f,InternationalTradeinfo t where f.tradeId=t.id and f.ip='"
//				+ ip+ "' and t.merchantId='"+ merchant.getId()+ "' and substring(t.tradeState,1,1) in(1,2,4,5,6) and t.tradeTime>to_date('"
//				+ yestedayDate + "','yyyy-MM-dd hh24:mi:ss')");
		if (Long.valueOf(ipcount) >= localIP) {
			message = "Payment Declined";
			remark = "�ظ�����/Repeat business";
			responseCode = 5;
			logger.info("����״̬��+++++++++" + responseCode);
			tradeInfo.setTradeState("3"
					+ tradeInfo.getTradeState().substring(1,
							tradeInfo.getTradeState().length()));
			String re[]=remark.split("/");
			tradeInfo.setRemark(re[0]);
			this.commonService.update(tradeInfo);
			logger.info("*********************֧�����������***************************"+responseCode);
			return SUCCESS;
		}
		// ͬһ����
		int emailcount = 0;
		emailcount = this.tradeManager
				.intBySql("select count(1) from international_cardholdersinfo f, international_tradeinfo t where f.tradeid=t.id and f.email='"
						+ this.email
						+ "' and t.merchantid='"
						+ merchant.getId()
						+ "' and substr(t.tradestate,1,1) in(1,2,4,5,6) and t.tradetime>to_date('"
						+ yestedayDate + "','yyyy-MM-dd hh24:mi:ss')");
//		List emailcount=this.commonService.list("select t from InternationalCardholdersInfo f,InternationalTradeinfo t where f.tradeId=t.id and f.email='"
//				+ this.email+ "' and t.merchantId='"+ merchant.getId()+ "' and substring(t.tradeState,1,1) in(1,2,4,5,6) and t.tradeTime>to_date('"
//				+ yestedayDate + "','yyyy-MM-dd hh24:mi:ss')");
		if (Long.valueOf(emailcount) >= localEMAIL) {
			message = "Payment Declined";
			remark = "�ظ�����/Repeat business";
			responseCode = 6;
			logger.info("����״̬��+++++++++" + responseCode);
			tradeInfo.setTradeState("3"
					+ tradeInfo.getTradeState().substring(1,
							tradeInfo.getTradeState().length()));
			String re[]=remark.split("/");
			tradeInfo.setRemark(re[0]);
			this.commonService.update(tradeInfo);
			logger.info("*********************֧�����������***************************"+responseCode);
			return SUCCESS;
		}
		// ͬһ�绰
		int telcout = 0;
		telcout = this.tradeManager
				.intBySql("select count(1) from international_cardholdersinfo f, international_tradeinfo t where f.tradeid=t.id and f.phone='"
						+ this.phone
						+ "' and t.merchantid='"
						+ merchant.getId()
						+ "' and substr(t.tradestate,1,1) in(1,2,4,5,6) and t.tradetime>to_date('"
						+ yestedayDate + "','yyyy-MM-dd hh24:mi:ss')");
//		List telcout=this.commonService.list("select t from InternationalCardholdersInfo f,InternationalTradeinfo t where f.tradeId=t.id and f.phone='"
//				+ this.phone+ "' and t.merchantId='"+ merchant.getId()+ "' and substring(t.tradeState,1,1) in(1,2,4,5,6) and t.tradeTime>to_date('"
//				+ yestedayDate + "','yyyy-MM-dd hh24:mi:ss')");
		if (Long.valueOf(telcout) >= localPhone) {
			message = "Payment Declined";
			remark = "�ظ�����/Repeat business";
			responseCode = 30;
			logger.info("����״̬��+++++++++" + responseCode);
			tradeInfo.setTradeState("3"
					+ tradeInfo.getTradeState().substring(1,
							tradeInfo.getTradeState().length()));
			String re[]=remark.split("/");
			tradeInfo.setRemark(re[0]);
			this.commonService.update(tradeInfo);
			logger.info("*********************֧�����������***************************"+responseCode);
			return SUCCESS;
		}
		// ͬһcookies
		int cocketcount = 0;
		cocketcount = this.tradeManager
				.intBySql("select count(1) from international_cardholdersinfo f, international_tradeinfo t where f.tradeid=t.id and f.cookie='"
						+ this.cookie
						+ "' and t.merchantid='"
						+ merchant.getId()
						+ "' and substr(t.tradestate,1,1) in(1,2,4,5,6) and t.tradetime>to_date('"
						+ yestedayDate + "','yyyy-MM-dd hh24:mi:ss')");
//		List cocketcount=this.commonService.list("select t from InternationalCardholdersInfo f,InternationalTradeinfo t where f.tradeId=t.id and f.cookie='"
//				+ this.cookie+ "' and t.merchantId='"+ merchant.getId()+ "' and substring(t.tradeState,1,1) in(1,2,4,5,6) and t.tradeTime>to_date('"
//				+ yestedayDate + "','yyyy-MM-dd hh24:mi:ss')");
		if (Long.valueOf(cocketcount) >= localCOCKET) {
			message = "Payment Declined";
			remark = "�ظ�����/Repeat business";
			responseCode = 8;
			logger.info("����״̬��+++++++++" + responseCode);
			tradeInfo.setTradeState("0"
					+ tradeInfo.getTradeState().substring(1,
							tradeInfo.getTradeState().length()));
			String re[]=remark.split("/");
			tradeInfo.setRemark(re[0]);
			this.commonService.update(tradeInfo);
			logger.info("*********************֧�����������***************************"+responseCode);
			return SUCCESS;
		}
		if (merchant.getMonthTradeMoney() + Double.valueOf(ordercount) > merchantmanager
				.getMonthQuota()) {
			// //�Ȱ���Ҫ�����ʼ�����Ϣ���浽���ݿ�
			// saveMailInfo(merEmail,num1,"ecpss@ecpss.com");
			message = "Payment Declined";
			remark = "�½���������/transaction volume overload";
			responseCode = 4;
			logger.info("����״̬��+++++++++" + responseCode);
			if (chnals.equals("VIP")
					|| (chnals.equals("EVIP") && merchant.getStatutes()
							.subSequence(6, 7).equals("0"))) {
				tradeInfo.setTradeState("4"
						+ tradeInfo.getTradeState().substring(1,
								tradeInfo.getTradeState().length()));
				String re[]=remark.split("/");
				tradeInfo.setRemark(re[0]);
				this.commonService.update(tradeInfo);
			} else {
				tradeInfo.setTradeState("0"
						+ tradeInfo.getTradeState().substring(1,
								tradeInfo.getTradeState().length()));
				String re[]=remark.split("/");
				tradeInfo.setRemark(re[0]);
				this.commonService.update(tradeInfo);
			}

			// �����޶���̻������ʼ�����
			shopManagerService.addSendMessages(merchant.getMeremail(),
					"ecpss@ecpss.cc",
					merchant.getMerno() + " " + EmailInfo.getMoreMoney(),
					"0");
			
			logger.info("*********************֧�����������***************************"+responseCode);
			return SUCCESS;

		}

		// �߷��տ� VIPΪ��ȷ�� ��������Ϊʧ��
		else if (riskCardValue > 0) {
			message = "Your Payment is being Processed! ";
			remark = "�߷��տ�/High risk cards";
			responseCode = 1;
			logger.info("����״̬��+++++++++" + responseCode);
			if (chnals.equals("VIP")) {
				tradeInfo.setTradeState("4"
						+ tradeInfo.getTradeState().substring(1,
								tradeInfo.getTradeState().length()));
				String re[]=remark.split("/");
				tradeInfo.setRemark(re[0]);
				this.commonService.update(tradeInfo);
			} else {
				tradeInfo.setTradeState("0"
						+ tradeInfo.getTradeState().substring(1,
								tradeInfo.getTradeState().length()));
				String re[]=remark.split("/");
				tradeInfo.setRemark(re[0]);
				this.commonService.update(tradeInfo);
			}
			logger.info("*********************֧�����������***************************"+responseCode);
			return SUCCESS;

		}
		// ���̻����׵Ľ��ﵽ�̻������������ý���85%�����ʼ��ķ�ʽ֪ͨ�̻�
		if (merchant.getMonthTradeMoney() + Double.valueOf(ordercount)  > merchantmanager
				.getMonthQuota() * 0.85) {
			// �����ʼ�
			shopManagerService.addSendMessages(
					merchant.getMeremail(),
					"ecpss@ecpss.cc",
					merchant.getMerno()
							+ " "
							+ EmailInfo.getMoreMoneyPart(merchant
									.getMonthTradeMoney() + Double.valueOf(ordercount) ),
					"0");
			// this.saveMailInfo(merEmail, num2, "ecpss@ecpss.com");
		}
		// ���̻��ķ���ƶԱȣ�Ȼ����жԱ�

		// ����maxmindϵͳ
//		try {
//			HashMap hm = new HashMap();
//			// ���ܴ� license_key : UxQh0mA4aLqw ���Ժ���ʽ����ʱҪ����,�Ż᷵�ط���
//			// �Ϻ�key: CxsRZ1xPPRbR;
//			// ����key: UxQh0mA4aLqw
//			int index = email.indexOf("@");
//			String domian = email.substring(index + 1, email.length());
//			hm.put("license_key", "9kbrHiIOJ9ZS");
//			hm.put("i", ip);
//			hm.put("domain", domian);
//			hm.put("emailMD5", md5.getMD5ofStr(email.toLowerCase()));
//			hm.put("custPhone", phone);
//			hm.put("country", country.substring(3, 5));
//			hm.put("city", city);
//			hm.put("region", state);
//			hm.put("shipAddr", address);
//			hm.put("postal", zipcode.toString());
//			// hm.put("bin", cardNo);
//			hm.put("bin", backcardNo6);
//			hm.put("binName", cardbank);
//
//			// standard �ͼ�
//			// premium �߼�
//			// ��ʽ���е�ʱ��Ҫ����� premium ; standardΪ�����õı�׼
//			hm.put("requested_type", "standard");
//
//			Hashtable ht = getmmValue(hm);
//			maxmindValue = (String) ht.get("values");
//			bankName = (String) ht.get("bankName");
//			bankCountry = (String) ht.get("bankCountry");
//			bankPhone = (String) ht.get("bankPhone");
//			logger.info("maxmindValue--------------" + maxmindValue);
//			// System.out.println("riskValue--------------"+riskValue);
//		} catch (Exception ex) {
//			try {
//				CCSendMail.setSendMail("89610614@qq.com",
//						"2.0 maxmind error", "ecpss@ecpss.cc");
//			} catch (Exception e) {
//				// ����ִ����ȥ
//			}
//		}

//		Double maxmind = 0d;
//		// ���ط�ֵ
//		if (maxmindValue != null && maxmindValue != "") {
//			maxmind = Double.valueOf(maxmindValue);
//		}
//		this.commonService
//				.deleteBySql(" update international_cardholdersinfo t set t.maxmindValue='"
//						+ maxmind
//						+ "',t.bankcountry='"
//						+ bankCountry
//						+ "',t.bankname='"
//						+ bankName
//						+ "',t.bankphone='"
//						+ bankPhone
//						+ "' where t.id='"
//						+ card.getId()
//						+ "' ");
//		// �߷��տ� VIPΪ��ȷ�� ��������Ϊʧ��
//		if ((maxmind >= im.getMaxmind_lv1())
//				&& (maxmind <= im.getMaxmind_lv2())) {
//			message = "Your Payment is being Processed! ";
//			remark = "�з��գ�/The risk";
//			responseCode = 9;
//			logger.info("����״̬��+++++++++" + responseCode);
//			MD5info = merchantOrderNo + Currency
//					+ ordercountValue + responseCode + MD5key;
//			md5Value = md5.getMD5ofStr(MD5info);
//			if (chnals.equals("VIP")
//					|| (chnals.equals("EVIP") && merchant.getStatutes()
//							.subSequence(6, 7).equals("0"))
//					|| chnals.equals("PRE")) {
//				tradeInfo.setTradeState("4"
//						+ tradeInfo.getTradeState().substring(1,
//								tradeInfo.getTradeState().length()));
//				String re[]=remark.split("/");
//				tradeInfo.setRemark(re[0]);
//				this.commonService.update(tradeInfo);
//				// -------�����̻��½����޶�-----------------
//				merchant.setMonthTradeMoney(merchant.getMonthTradeMoney()
//						+ tradeInfo.getTradeAmount());
//				this.commonService.update(merchant);
//			} else {
//				tradeInfo.setTradeState("0"
//						+ tradeInfo.getTradeState().substring(1,
//								tradeInfo.getTradeState().length()));
//				String re[]=remark.split("/");
//				tradeInfo.setRemark(re[0]);
//				this.commonService.update(tradeInfo);
//
//			}
//			logger.info("*********************֧�����������***************************"+responseCode);
//			return SUCCESS;
//		} else if (maxmind > im.getMaxmind_lv2()) {
//			message = "Payment Declined";
//			remark = "�߷��գ�/High-risk";
//			responseCode = 0;
//			logger.info("����״̬��+++++++++" + responseCode);
//			MD5info = merchantOrderNo + Currency
//					+ ordercountValue + responseCode + MD5key;
//			md5Value = md5.getMD5ofStr(MD5info);
//			tradeInfo.setTradeState("0"
//					+ tradeInfo.getTradeState().substring(1,
//							tradeInfo.getTradeState().length()));
//			String re[]=remark.split("/");
//			tradeInfo.setRemark(re[0]);
//			this.commonService.update(tradeInfo);
//			return SUCCESS;
//		}

		/**
		 * ��ֹ���׵������ճֿ����˵�����check
		 */
		String queryarea = "select m.id from MerchantRiskControl m where m.merchantId="
		+ merchant.getId()
		+ " and substr(m.area,1,2) like '"
		+ country.substring(3, 5) + "'";
		String allQueryarea = "select m.id from MerchantRiskControl m where m.merchantId is null"
				+ " and substr(m.area,1,2) like '"
				+ country.substring(3, 5) + "'";
    	logger.info(queryarea);
		List queryarealist = this.commonService.list(queryarea);
		List allQueryarealist = this.commonService.list(allQueryarea);
		logger.info(queryarealist.size());
		if (queryarealist.size() > 0||allQueryarealist.size()>0) {
			message = "Payment Declined";
			remark = "��ֹ���׵���/Prohibited transaction area";
			responseCode = 31;
			logger.info("����״̬��+++++++++" + responseCode);
			tradeInfo.setTradeState("0"
					+ tradeInfo.getTradeState().substring(1,
							tradeInfo.getTradeState().length()));
			String re[]=remark.split("/");
			tradeInfo.setRemark(re[0]);
			this.commonService.update(tradeInfo);
			return SUCCESS;
		}
		if (chnals.equals("V5")) {
			TradUtil tu= new TradUtil();
			TradeMessage tm=new TradeMessage();
			//����
			tm.setCardNo(cardNo);
			//CVV
			tm.setCvv(cvv2);
			//������Ч����
			tm.setExpirationYear("20"+year);
			//������Ч����
			tm.setExpirationMonth(month);
			tm.setPayNumber("0");
			tm.setMerNo("10000");
			tm.setShopName("Ǩ��ͨ");
			tm.setOrderNo(tradeInfo.getOrderNo());
			tm.setAmount(tradeInfo.getRmbAmount()+"");
			tm.setCurrency("CNY");
			tm.setGoodsName("��Ʒ����");
			tm.setGoodsPrice(tradeInfo.getRmbAmount()+"");
			tm.setGoodsNumber("1");
			tm.setBillFirstName(this.firstname);
			tm.setBillLastName(this.lastname);
			tm.setBillAddress(this.address);
			tm.setBillCity(this.city);
			tm.setBillState(this.state);
			tm.setBillCountry(this.country.substring(3, 5));
			tm.setBillZip(this.zipcode);
			tm.setEmail(this.email);
			tm.setPhone(this.phone);
			tm.setShipFirstName(this.shippingFirstName);
			tm.setShipLastName(this.shippingLastName);
			tm.setShipAddress(this.shippingAddress);
			tm.setShipCity(this.shippingCity);
			tm.setShipState(this.shippingSstate);
			tm.setShipAddress(this.shippingAddress);
			tm.setShipCountry(this.shippingCountry.substring(3, 5));
			tm.setShipZip(this.shippingZipcode);
			tm.setReturnURL("www.baidu.com");
			tm.setLanguage("");
			tm.setRemark("qianyitong");

			
			String mdfind=tm.getMerNo()+"LzVZD7"+tm.getOrderNo()+tm.getAmount()+tm.getCurrency()+tm.getEmail()+tm.getReturnURL();
			
			String md5info = StringUtil.Md5(mdfind);
			tm.setMd5Info(md5info);
			tm.setPayIp(this.ip);
			tm.setAcceptLanguage("zh-CN");
			tm.setUserAgent(userbrowser);
			tu.get(tm);				
			
			if(tm.getSucceed().equals("0")){

				this.message = "Payment Declined!" + tm.getErrorCode();
				this.responseCode = 0;
				MD5info = merchantOrderNo
						+ Currency + ordercountValue
						+ responseCode + MD5key;
				md5Value = md5.getMD5ofStr(MD5info);
				tradeInfo.setTradeState("0"
						+ tradeInfo.getTradeState().substring(1,
								tradeInfo.getTradeState().length()));
				if("1100".equals(tm.getErrorCode())){
					tradeInfo.setRemark("10����֮���벻Ҫ�ظ��ύ��"+tm.getErrorCode());
				}
				else if("0927".equals(tm.getErrorCode())){
					tradeInfo.setRemark("����Ϣ��֤ʧ�ܣ�����ϵ�����У�"+tm.getErrorCode());
				}
				else if("0931".equals(tm.getErrorCode())){
					tradeInfo.setRemark("����ϵ�����У�"+tm.getErrorCode());
				}
				else if("1078".equals(tm.getErrorCode())){
					tradeInfo.setRemark("cvv����"+tm.getErrorCode());
//				}else if("03".equals(tm.getErrorCode())){
//					logger.info("��Ч�̻�����ʼ����������ݡ�������");
//					shopManagerService.addTemporaryTradInfo(rorderno, year, month, MD5info, ip,userbrowser);
//				}else if("sfe01".equals(tm.getErrorCode())){
//					logger.info("ͨ������ʱ����ʼ����������ݡ�������");
//					shopManagerService.addTemporaryTradInfo(rorderno, year, month, MD5info, ip,userbrowser);
				}else{
					tradeInfo.setRemark(tm.getErrorCode()+tm.getErrorMsg()+tm.getBankInfo());
				}
				this.commonService.update(tradeInfo);
				logger.info("*********************֧�����������***************************"+responseCode);
				return SUCCESS;
								
			}else if(tm.getSucceed().equals("1")){

				// ֧���ɹ�
				this.message = "Payment Success!";
				this.responseCode = 88;
				MD5info = merchantOrderNo
						+ Currency + ordercountValue
						+ responseCode + MD5key;
				md5Value = md5.getMD5ofStr(MD5info);
				tradeInfo.setTradeState("1"
						+ tradeInfo.getTradeState().substring(1,
								tradeInfo.getTradeState().length()));
				tradeInfo.setRemark(message);
				logger.info("���׳ɹ�����:"+merchantOrderNo+"**"+Currency+"**"+ordercount+"**"+responseCode+"**"+message+"**"+ReturnURL+"**"+md5Value);
				this.commonService.update(tradeInfo);

				// ///---------------���ֿ��˷����ʼ�-----------------------////
				EmailInfo emailinfo = new EmailInfo();
				String mailinfo = emailinfo.getPaymentResultEmail(
						card.getEmail(),
						tradeInfo.getTradeAmount(),
						getStates().getCurrencyTypeByNo(
								Integer.valueOf(Currency)),
						tradeInfo.getTradeUrl(), tradeInfo.getTradeTime(),
						it.get(0).getBillingAddress(), merchantOrderNo,
						tradeInfo.getOrderNo());
				
				
				try {
					// �����ʼ�,�������ʧ�ܲ������ݿⷢ��
					CCSendMail.setSendMail(card.getEmail(), mailinfo,
							"sfepay@sfepay.com");
					logger.info("�ʼ�������");
				} catch (Exception e) {
					// �����ݿ����ȴ������ʼ�
					shopManagerService.addSendMessages(card.getEmail(),
							"sfepay@sfepay.com", mailinfo, "0");
					logger.info("�ʼ��ȴ��Ժ󷢳�");
					logger.info("*********************֧�����������***************************"+responseCode);
					return SUCCESS;
				}
				logger.info("*********************֧�����������***************************"+responseCode);
				return SUCCESS;
								
				
				
			}else if(tm.getSucceed().equals("2")||tm.getSucceed().equals("3")){

				// ֧���ɹ�
				this.responseCode = 19;

				MD5info = merchantOrderNo
						+ Currency + ordercountValue
						+ responseCode + MD5key;
				md5Value = md5.getMD5ofStr(MD5info);
				tradeInfo.setTradeState("2"
						+ tradeInfo.getTradeState().substring(1,
								tradeInfo.getTradeState().length()));
				tradeInfo.setRemark(message);
				this.commonService.update(tradeInfo);
				logger.info("*********************֧�����������***************************"+responseCode);
				return SUCCESS;
			
			}
			
			
			
		}else if (chnals.equals("VPN")) {//��3DMOTO����
            logger.info("���뵽VPNͨ��");
			vpn.DCCMessage dcc = new vpn.DCCMessage();
			dcc.setTrans_Type("enqrate");// ��ѯ�˿��Ƿ�֧��DCC����
			dcc.setMerchant_Id(posMerchantNo);// 42 �̻����
			dcc.setAuthor_Str(it.get(0).getAuthcode());
			dcc.setTerminal_Id(posNumber);// 41 �̻��ն˺�
			dcc.setInvoice_No(tradeInfo.getOrderNo().substring(
					tradeInfo.getOrderNo().length() - 6,
					tradeInfo.getOrderNo().length()));

			// ��Ч��
			dcc.setOrder_No(tradeInfo.getOrderNo());// 62
			dcc.setCustom(tradeInfo.getOrderNo());
			dcc.setHashCode(it.get(0).getHashcode());
			dcc.setCurrency_Code_T("156");// ���Ҵ��� CNY
			dcc.setBocs_ReturnURL("http://172.20.66.2/sfe");
			dcc.setAmount_Loc(this.buzero(tradeInfo.getRmbAmount() + ""));// 4
			// ���ؽ��׽��
			dcc.setCard_No(cardNo);// �˺�2
			dcc.setExp_Date(year + month);// 14
			VpnUtil vu = new VpnUtil();
			Long tim1 = System.currentTimeMillis();
			try {
				// type 1 ���ʲ�ѯ
				 dcc = vu.getDCCvalue(dcc, "1");
//				dcc.setResp_Code("99YY");
			} catch (Exception e) {
				this.responseCode = 19;
				message = "Your payment is being processed";
				MD5info = merchantOrderNo + Currency
						+ ordercountValue + responseCode + MD5key;
				md5Value = md5.getMD5ofStr(MD5info);

				tradeInfo.setTradeState("0"
						+ tradeInfo.getTradeState().substring(1,
								tradeInfo.getTradeState().length()));
				tradeInfo.setRemark(message);
				tradeInfo.setVIPDisposePorson("System");
				tradeInfo.setVIPDisposeDate(new Date());
				this.commonService.update(tradeInfo);
				logger.info("*********************֧�����������***************************"+responseCode);
				return SUCCESS;
			}
			logger.info("*********************DCC��㷵����***************************"+dcc.getResp_Code());
			logger.info("DCC��㷵����"+dcc.getResp_Code());
			if (dcc.getResp_Code().equals("99YY")){//�˿�֧��DCC����
				logger.info("MOTO DCC���׿�ʼ");
				// ����
				vpn.MotoDCCMessage dcc2 = new vpn.MotoDCCMessage();
				dcc2.setTrans_Type("risk");// ����
				dcc2.setMerchant_Id(posMerchantNo);// 42 �̻����
				dcc2.setAuthor_Str(it.get(0).getAuthcode());
				dcc2.setTerminal_Id(posNumber);// 41 �̻��ն˺�
				dcc2.setInvoice_No(tradeInfo.getOrderNo().substring(
						tradeInfo.getOrderNo().length() - 6,
						tradeInfo.getOrderNo().length()));

				dcc2.setTrans_Model("M");//motoͨ��
				dcc2.setCurrency_Code_T("156");// ���Ҵ���
				dcc2.setAmount_Loc(this.buzero(tradeInfo.getRmbAmount() + ""));// 4
				// ���ؽ��׽��
				dcc2.setCard_No(cardNo);// �˺�2
				dcc2.setExp_Date(year + month);// 14 ��Ч��
				dcc2.setCSC(cvv2);
				dcc2.setCurrency_Code(dcc.getCurrency_Code());
				dcc2.setBocs_ReturnURL("http://172.20.66.2/sfe");
				dcc2.setAmount_For(dcc.getAmount_For());
				dcc2.setOrder_No(tradeInfo.getOrderNo());
				dcc2.setCustom(tradeInfo.getOrderNo());
				dcc2.setHashCode(it.get(0).getHashcode());
				//�������ս��ײ���
				dcc2.setCUST_FNAME(firstname);
				dcc2.setCUST_LNAME(lastname);
				dcc2.setCUST_CITY(city);
				dcc2.setCUST_ADDR1(address);
				dcc2.setCUST_CNTRY_CD(country.substring(0, 3));
				dcc2.setCUST_EMAIL(email);
				dcc2.setCUST_IP_ADDR(ip);
				if(zipcode.replaceAll("-", "").length()>9){
				dcc2.setCUST_POSTAL_CD(zipcode.replaceAll("-", "").substring(0, 9));
				}else{
				dcc2.setCUST_POSTAL_CD(zipcode.replaceAll("-", ""));
				}
				dcc2.setSHIP_FNAME(shippingFirstName);
				dcc2.setSHIP_LNAME(shippingLastName);
				dcc2.setSHIP_CITY(shippingCity);
				dcc2.setSHIP_ADDR1(shippingAddress);
				dcc2.setSHIP_CNTRY_CD(shippingCountry.substring(0, 3));
				dcc2.setSHIP_EMAIL(shippingEmail);
				dcc2.setSHIP_IP_ADDR(ip);
				if(shippingZipcode.replaceAll("-", "").length()>9){
				dcc2.setSHIP_POSTAL_CD(shippingZipcode.replaceAll("-", "").substring(0, 9));
				}else{
					dcc2.setSHIP_POSTAL_CD(shippingZipcode.replaceAll("-", ""));
				}
				VpnUtil_Moto vu2=new VpnUtil_Moto();
				Long tim2 = System.currentTimeMillis();
				try {
					// type 2 dcc����
					logger.info("��ʼmoto���");
					 dcc2 = vu2.getDCCvalue(dcc2, "21");
				} catch (Exception e) {
					responseCode = 19;
					message = "Your payment is being processed";
					MD5info = merchantOrderNo
							+ Currency + ordercountValue
							+ responseCode + MD5key;
					md5Value = md5.getMD5ofStr(MD5info);

					tradeInfo.setTradeState("0"
							+ tradeInfo.getTradeState().substring(1,
									tradeInfo.getTradeState().length()));
					tradeInfo.setRemark(message);
					tradeInfo.setVIPDisposePorson("System");
					tradeInfo.setVIPDisposeDate(new Date());
					this.commonService.update(tradeInfo);
					logger.info("*********************֧�����������***************************"+responseCode);
					return SUCCESS;
				}
				if (dcc2.getResp_Code().equals("5600")) {
					vpn.MotoDCCMessage moto = new vpn.MotoDCCMessage();
					moto.setTrans_Type("dccsales");// ����
					moto.setMerchant_Id(posMerchantNo);// 42 �̻����
					moto.setAuthor_Str(it.get(0).getAuthcode());
					moto.setTerminal_Id(posNumber);// 41 �̻��ն˺�
					moto.setInvoice_No(tradeInfo.getOrderNo().substring(
							tradeInfo.getOrderNo().length() - 6,
							tradeInfo.getOrderNo().length()));

					moto.setTrans_Model("M");//motoͨ��
					moto.setCurrency_Code_T("156");// ���Ҵ���
					moto.setAmount_Loc(this.buzero(tradeInfo.getRmbAmount() + ""));// 4
					// ���ؽ��׽��
					moto.setCard_No(cardNo);// �˺�2
					moto.setExp_Date(year + month);// 14 ��Ч��
					moto.setCSC(cvv2);
					moto.setCurrency_Code(dcc.getCurrency_Code());
					moto.setBocs_ReturnURL("http://172.20.66.2/sfe");
					moto.setAmount_For(dcc.getAmount_For());
					moto.setOrder_No(tradeInfo.getOrderNo());
					moto.setCustom(tradeInfo.getOrderNo());
					moto.setHashCode(it.get(0).getHashcode());
					VpnUtil_Moto vm=new VpnUtil_Moto();
					//VpnUtil vu2 = new VpnUtil();
					try {
						// type 2 dcc����
						logger.info("��ʼmoto DCC����");
						moto = vm.getDCCvalue(moto, "2");
					} catch (Exception e) {
						responseCode = 19;
						message = "Your payment is being processed";
						MD5info = merchantOrderNo
								+ Currency + ordercountValue
								+ responseCode + MD5key;
						md5Value = md5.getMD5ofStr(MD5info);

						tradeInfo.setTradeState("0"
								+ tradeInfo.getTradeState().substring(1,
										tradeInfo.getTradeState().length()));
						tradeInfo.setRemark(message);
						tradeInfo.setVIPDisposePorson("System");
						tradeInfo.setVIPDisposeDate(new Date());
						this.commonService.update(tradeInfo);
						logger.info("���׳ɹ�����:"+merchantOrderNo+"**"+Currency+"**"+ordercount+"**"+responseCode+"**"+message+"**"+ReturnURL+"**"+md5Value);
						logger.info("*********************֧�����������***************************"+responseCode);
						return SUCCESS;
					}
				if (moto.getResp_Code().equals("0000")) {//���׳ɹ�
					this.message = "Payment Success!";
					this.responseCode = 88;
					// ����ֿ���cvv,��Ч��
					// this.commonService
					// .deleteBySql("update  international_cardholdersinfo t  set t.cvv2='XXX',t.expiryDate='0000' where t.tradeId='"
					// + tradeInfo.getId() + "'");
					tradeInfo.setTradeState("1"
							+ tradeInfo.getTradeState().substring(1,
									tradeInfo.getTradeState().length()));
					tradeInfo.setRemark(message);
					tradeInfo.setVIPDisposePorson("System");
					tradeInfo.setVIPDisposeDate(new Date());
					tradeInfo.setVIPBatchNo(moto.getAuth_Code());
					tradeInfo.setVIPTerminalNo(posNumber);
					tradeInfo.setVIPAuthorizationNo(moto.getInvoice_No());
					tradeInfo.setRef_No(moto.getRef_No());
					this.commonService.update(tradeInfo);
					card.setExpiryDate("0000");
					card.setCvv2("XXX");
					this.commonService.update(card);
					MD5info = merchantOrderNo
							+ Currency + ordercountValue
							+ responseCode + MD5key;
					md5Value = md5.getMD5ofStr(MD5info);
					
					logger.info("���׳ɹ�����:"+merchantOrderNo+"**"+Currency+"**"+ordercount+"**"+responseCode+"**"+message+"**"+ReturnURL+"**"+md5Value);
					// �����ʼ�
					List<InternationalTerminalManager> tmm = this.commonService
							.list("select tm from InternationalTerminalManager tm where tm.terminalNo='"
									+ posNumber.trim() + "' ");
					if (tmm.size() > 0) {
						InternationalTerminalManager tm = tmm.get(0);
						billaddressby = tm.getBillingAddress();
					}
					String mailinfo = null;
					try {
						EmailInfo emailinfo = new EmailInfo();
						mailinfo = emailinfo.getPaymentResultEmail(
								card.getEmail(),
								tradeInfo.getTradeAmount(),
								getStates().getCurrencyTypeByNo(
										Integer.valueOf(Currency)),
								tradeInfo.getTradeUrl(), tradeInfo.getTradeTime(),
								billaddressby, merchantOrderNo,
								tradeInfo.getOrderNo(), merchant);
						// �����ʼ�,�������ʧ�ܲ������ݿⷢ��
						if (merchant.getStatutes().substring(4, 5)
								.equals("0")) {
							CCSendMail.setSendMail(card.getEmail(),
									mailinfo, "sfepay@sfepay.com");
							logger.info("�ʼ�������");
						}
					} catch (Exception e) {
						// �����ݿ����ȴ������ʼ�
						shopManagerService.addSendMessages(card.getEmail(),
								"sfepay@sfepay.com", mailinfo, "0");
						logger.info("�ʼ��ȴ��Ժ󷢳�");
						logger.info("*********************֧�����������***************************"+responseCode);
						return SUCCESS;
					}
					logger.info("*********************֧�����������***************************"+responseCode);
					return SUCCESS;
				} else {
					this.message = "Payment Declined!"+moto.getResp_Code();
					this.responseCode = Integer.valueOf(moto.getResp_Code());
					tradeInfo.setTradeState("0"
							+ tradeInfo.getTradeState().substring(1,
									tradeInfo.getTradeState().length()));
					tradeInfo.setRemark(message);
					tradeInfo.setVIPDisposePorson("System");
					tradeInfo.setVIPDisposeDate(new Date());
					this.commonService.update(tradeInfo);
					MD5info = merchantOrderNo
							+ Currency + ordercountValue
							+ responseCode + MD5key;
					md5Value = md5.getMD5ofStr(MD5info);
					logger.info("*********************֧�����������***************************"+responseCode);
					return SUCCESS;
				}
				}else {
					message = "Payment Declined";
					remark = "�߷��ս���/Risk of trading";
					this.responseCode = Integer.valueOf(dcc2.getResp_Code());
					tradeInfo.setTradeState("0"
							+ tradeInfo.getTradeState().substring(1,
									tradeInfo.getTradeState().length()));
					String re[]=remark.split("/");
					tradeInfo.setRemark(re[0]+this.responseCode);
					tradeInfo.setVIPDisposePorson("System");
					tradeInfo.setVIPDisposeDate(new Date());
					this.commonService.update(tradeInfo);
					MD5info = merchantOrderNo
							+ Currency + ordercountValue
							+ responseCode + MD5key;
					md5Value = md5.getMD5ofStr(MD5info);
					logger.info("*********************֧�����������***************************"+responseCode);
					return SUCCESS;
				}

			} else if (dcc.getResp_Code().equals("99YX")) {//��֧��DCC����

				vpn.DCCMessage dcc3 = new vpn.DCCMessage();
				dcc3.setTrans_Type("sales");// ����
				// �̻����
				dcc3.setMerchant_Id(it.get(0).getMerchantNo());// 42
				dcc3.setAuthor_Str(it.get(0).getAuthcode());
				// �̻��ն˺�
				dcc3.setTerminal_Id(it.get(0).getTerminalNo());// 41
				dcc3.setInvoice_No(tradeInfo.getOrderNo().substring(
						tradeInfo.getOrderNo().length() - 6,
						tradeInfo.getOrderNo().length()));

				dcc3.setOrder_No(tradeInfo.getOrderNo());// 62
				dcc3.setCustom(tradeInfo.getOrderNo());
				dcc3.setHashCode(it.get(0).getHashcode());

				dcc3.setTrans_Model("M");//moto
				dcc3.setCurrency_Code_T(dcc.getCurrency_Code_T());// ���Ҵ���
				dcc3.setAmount_Loc(this.buzero(tradeInfo.getTradeAmount() + ""));// 4
				// ���ؽ��׽��
				dcc3.setCard_No(cardNo);// �˺�2
				dcc3.setExp_Date(year + month);// 14 ��Ч��
				dcc3.setCSC(cvv2);

				VpnUtil vu3 = new VpnUtil();
				Long tim2 = System.currentTimeMillis();
				try {
					// type 3 edc����
					dcc3 = vu3.getDCCvalue(dcc3, "3");
				} catch (Exception e) {
					responseCode = Integer.valueOf(dcc3.getResp_Code());
					message = "Your payment is being processed";
					MD5info = merchantOrderNo
							+ Currency + ordercountValue
							+ responseCode + MD5key;
					md5Value = md5.getMD5ofStr(MD5info);
					// this.commonService
					// .deleteBySql("update  international_tradeinfo t  set t.tradestate='0'||substr(t.tradestate,2,(length(t.tradestate)-1)),t.remark='"
					// + message
					// + "' ,t.VIPDisposePorson='System' "
					// + " ,t.VIPDisposeDate=sysdate "
					// + "  where t.id='"
					// + tradeInfo.getId()
					// + "'");
					tradeInfo.setTradeState("0"
							+ tradeInfo.getTradeState().substring(1,
									tradeInfo.getTradeState().length()));
					tradeInfo.setRemark(message);
					tradeInfo.setVIPDisposePorson("System");
					tradeInfo.setVIPDisposeDate(new Date());
					this.commonService.update(tradeInfo);
					logger.info("*********************֧�����������***************************"+responseCode);
					return SUCCESS;
				}
				if (dcc3.getResp_Code().equals("0000")) {//���׳ɹ�
					tradeInfo.setTradeState("1"
							+ tradeInfo.getTradeState().substring(1,
									tradeInfo.getTradeState().length()));
					tradeInfo.setRemark(message);
					tradeInfo.setVIPDisposePorson("System");
					tradeInfo.setVIPDisposeDate(new Date());
					tradeInfo.setVIPBatchNo(dcc3.getAuth_Code());
					tradeInfo.setVIPTerminalNo(posNumber);
					tradeInfo.setVIPAuthorizationNo(dcc3.getInvoice_No());
					tradeInfo.setRef_No(dcc3.getRef_No());
					this.commonService.update(tradeInfo);
					card.setExpiryDate("0000");
					card.setCvv2("XXX");
					this.commonService.update(card);
					// �����ʼ�
					List<InternationalTerminalManager> tmm = this.commonService
							.list("select tm from InternationalTerminalManager tm where tm.terminalNo='"
									+ posNumber.trim() + "' ");
					String billaddressby = null;
					if (tmm.size() > 0) {
						InternationalTerminalManager tm = tmm.get(0);
						billaddressby = tm.getBillingAddress();
					}
					String mailinfo = null;
					try {
						EmailInfo emailinfo = new EmailInfo();
						mailinfo = emailinfo.getPaymentResultEmail(
								card.getEmail(),
								tradeInfo.getTradeAmount(),
								getStates().getCurrencyTypeByNo(
										Integer.valueOf(Currency)),
								tradeInfo.getTradeUrl(), tradeInfo.getTradeTime(),
								billaddressby, merchantOrderNo,
								tradeInfo.getOrderNo(), merchant);
						// �����ʼ�,�������ʧ�ܲ������ݿⷢ��
						if (merchant.getStatutes().substring(4, 5)
								.equals("0")) {
							CCSendMail.setSendMail(card.getEmail(),
									mailinfo, "sfepay@sfepay.com");
							logger.info("�ʼ�������");
						}
					} catch (Exception e) {
						// �����ݿ����ȴ������ʼ�
						shopManagerService.addSendMessages(card.getEmail(),
								"sfepay@sfepay.com", mailinfo, "0");
						logger.info("�ʼ��ȴ��Ժ󷢳�");
						logger.info("*********************֧�����������***************************"+responseCode);
						return SUCCESS;
					}
					this.message = "Payment Success!";
					this.responseCode = 88;

					MD5info = merchantOrderNo
							+ Currency + ordercountValue
							+ responseCode + MD5key;
					md5Value = md5.getMD5ofStr(MD5info);
					logger.info("���׳ɹ�����:"+merchantOrderNo+"**"+Currency+"**"+ordercount+"**"+responseCode+"**"+message+"**"+ReturnURL+"**"+md5Value);
					logger.info("*********************֧�����������***************************"+responseCode);
					return SUCCESS;
				} else {
					this.message = "Payment Declined!!";
					this.responseCode = Integer
							.valueOf(dcc3.getResp_Code());
					tradeInfo.setTradeState("0"
							+ tradeInfo.getTradeState().substring(1,
									tradeInfo.getTradeState().length()));
					tradeInfo.setRemark(message);
					tradeInfo.setVIPDisposePorson("System");
					tradeInfo.setVIPDisposeDate(new Date());
					this.commonService.update(tradeInfo);
					MD5info = merchantOrderNo
							+ Currency + ordercountValue
							+ responseCode + MD5key;
					md5Value = md5.getMD5ofStr(MD5info);
					
					logger.info("*********************֧�����������***************************"+responseCode);
					return SUCCESS;
				}

			}
		}else if(chnals.equals("YL")){
			logger.info("��������ͨ��");
			YinlianMessage msg=new YinlianMessage();
			YinlianUtil yu=new YinlianUtil();
			SimpleDateFormat sdf =new SimpleDateFormat("yyyyMMddHHmmss");
			msg.setTrnxDatetime(sdf.format(new Date()));
			msg.setCardNo(cardNo);
			msg.setAmt(this.buzero(tradeInfo.getRmbAmount() + ""));
			int posNo=this.tradeManager.intBySql("SELECT POS_SEQUENCE.NEXTVAL FROM DUAL");
			logger.info("pos��ˮ�ţ�"+posNo);
			msg.setPosFlwNo(posNo+"");
			msg.setTermId(posNumber);
			msg.setMerchId(posMerchantNo);
			msg.setCvv2(cvv2);
			msg.setExpiredDate(year + month);
			yu.getYLPayMessage(msg);
			if (msg.getRes_resCode().equals("00")) {//���׳ɹ�
				this.message = "Payment Success!";
				this.responseCode = 88;
				tradeInfo.setTradeState("1"
						+ tradeInfo.getTradeState().substring(1,
								tradeInfo.getTradeState().length()));
				tradeInfo.setRemark(message);
				tradeInfo.setVIPDisposePorson("System");
				tradeInfo.setVIPDisposeDate(new Date());
				tradeInfo.setVIPBatchNo(posNo+"");
				tradeInfo.setVIPTerminalNo(posNumber);
				tradeInfo.setVIPAuthorizationNo(msg.getRes_authResCode());
				tradeInfo.setRef_No(msg.getRes_referenceNo());
				tradeInfo.setBoc_time(msg.getRes_settlementDate());
				this.commonService.update(tradeInfo);
				card.setExpiryDate("0000");
				card.setCvv2("XXX");
				this.commonService.update(card);
				MD5info = merchantOrderNo
						+ Currency + ordercountValue
						+ responseCode + MD5key;
				md5Value = md5.getMD5ofStr(MD5info);
				
				logger.info("���׳ɹ�����:"+merchantOrderNo+"**"+Currency+"**"+ordercount+"**"+responseCode+"**"+message+"**"+ReturnURL+"**"+md5Value);
				// �����ʼ�
				List<InternationalTerminalManager> tmm = this.commonService
						.list("select tm from InternationalTerminalManager tm where tm.terminalNo='"
								+ posNumber.trim() + "' ");
				String billaddressby = null;
				if (tmm.size() > 0) {
					InternationalTerminalManager tm = tmm.get(0);
					billaddressby = tm.getBillingAddress();
				}
				String mailinfo = null;
				try {
					EmailInfo emailinfo = new EmailInfo();
					mailinfo = emailinfo.getPaymentResultEmail(
							card.getEmail(),
							tradeInfo.getTradeAmount(),
							getStates().getCurrencyTypeByNo(
									Integer.valueOf(Currency)),
							tradeInfo.getTradeUrl(), tradeInfo.getTradeTime(),
							billaddressby, merchantOrderNo,
							tradeInfo.getOrderNo(), merchant);
					// �����ʼ�,�������ʧ�ܲ������ݿⷢ��
					if (merchant.getStatutes().substring(4, 5)
							.equals("0")) {
						CCSendMail.setSendMail(card.getEmail(),
								mailinfo, "sfepay@sfepay.com");
						logger.info("�ʼ�������");
					}
				} catch (Exception e) {
					// �����ݿ����ȴ������ʼ�
					shopManagerService.addSendMessages(card.getEmail(),
							"sfepay@sfepay.com", mailinfo, "0");
					logger.info("�ʼ��ȴ��Ժ󷢳�");
					logger.info("*********************֧�����������***************************"+responseCode);
					return SUCCESS;
				}
				logger.info("*********************֧�����������***************************"+responseCode);
				return SUCCESS;
			} else {
				this.message = "Payment Declined!Y"+msg.getRes_resCode();
				this.responseCode = 0;
				tradeInfo.setTradeState("0"
						+ tradeInfo.getTradeState().substring(1,
								tradeInfo.getTradeState().length()));
				tradeInfo.setRemark(message);
				tradeInfo.setVIPDisposePorson("System");
				tradeInfo.setVIPDisposeDate(new Date());
				this.commonService.update(tradeInfo);
				MD5info = merchantOrderNo
						+ Currency + ordercountValue
						+ responseCode + MD5key;
				md5Value = md5.getMD5ofStr(MD5info);
				logger.info("*********************֧�����������***************************"+responseCode);
				return SUCCESS;
			}
	}
		logger.info("*********************֧������������***************************"+responseCode);
		return SUCCESS;
	} catch (Exception e) {
		logger.error(e);
		e.printStackTrace();
		this.responseCode = 35;
		message = "Payment Declined";
		logger.info("*********************֧�����������***************************"+responseCode);
		return SUCCESS;

	}
}


//public Hashtable getmmValue(HashMap hm) {
//
//	h = exam.maxMindScore(hm);
//	// ��MaxMind���صĲ�����ӡ����,
//	for (Iterator i = h.keySet().iterator(); i.hasNext();) {
//		String key = (String) i.next();
//		maxmindValue = (String) h.get(key);
//		if (key.equals("score")) {
//			values = maxmindValue;
//
//		}
//		if (key.equals("binName")) {
//			bankName = maxmindValue;
//		}
//		if (key.equals("binCountry")) {
//			bankCountry = maxmindValue;
//		}
//		if (key.equals("binPhone")) {
//			bankPhone = maxmindValue;
//		}
//	}
//	Hashtable ht = new Hashtable();
//	if (values == null) {
//		values = "0";
//	}
//	ht.put("values", values);
//	ht.put("bankName", bankName);
//	ht.put("bankCountry", bankCountry);
//	ht.put("bankPhone", bankPhone);
//	return ht;
//}
//��֤������Ϣ
		public Boolean validateSensitive(String valiType,String sBillNo,String pro){
			String hql="From InternationalSensitiveInfo";
			List<InternationalSensitiveInfo> list=commonService.list(hql);
			if("1".equals(valiType)){
				for(InternationalSensitiveInfo sen:list){
					if(StringUtils.isNotBlank(sen.getBillNo())){
						if(sBillNo.toLowerCase().indexOf(sen.getBillNo().toLowerCase())>=0){
							return true;
							}
						}
					if(StringUtils.isNotBlank(sen.getProducts())){
						if(pro.toLowerCase().indexOf(sen.getProducts()+" ".toLowerCase())>=0||pro.toLowerCase().indexOf(" "+sen.getProducts().toLowerCase())>=0){
							return true;
							}
						}
					}
			}else if("2".equals(valiType)){
				for(InternationalSensitiveInfo sen:list){
					if(StringUtils.isNotBlank(sen.getEmail())){
						if(sBillNo.toLowerCase().indexOf(sen.getEmail().toLowerCase())>=0){
							return true;
							}
						}
					if(StringUtils.isNotBlank(sen.getProducts())){
						if(pro.toLowerCase().indexOf(sen.getProducts().toLowerCase())>=0){
							return true;
							}
						}
					}
			}else if("3".equals(valiType)){
				for(InternationalSensitiveInfo sen:list){
					if(StringUtils.isNotBlank(sen.getTradeUrl())){
						if(sBillNo.toLowerCase().indexOf(sen.getTradeUrl().toLowerCase())>=0){
							return true;
							}
						}
					if(StringUtils.isNotBlank(sen.getProducts())){
						if(pro.toLowerCase().indexOf(sen.getProducts().toLowerCase())>=0){
							return true;
							}
						}
					}
			}
			
			return false;
		}
		public Boolean validateVisa(String valCardNo,String valEmail,String valIp,String valUrl){
			String hql="from InternationalHighRisklist";
			List<InternationalHighRisklist> list=commonService.list(hql);
			for(InternationalHighRisklist risk:list){
				if(StringUtils.isNotBlank(risk.getCardno())){
					if(valCardNo.toLowerCase().indexOf(risk.getCardno().toLowerCase())>=0){
						logger.info("���ַ�������֤��"+valCardNo);
						redInfo="1";
						return true;
					}
				}
				if(StringUtils.isNotBlank(risk.getEmail())){
					if(valEmail.toLowerCase().indexOf(risk.getEmail().toLowerCase())>=0){
						logger.info("���ַ�������֤��"+valEmail);
						redInfo="2";
						return true;
					}
				}
				if(StringUtils.isNotBlank(risk.getIp())){
					if(valIp.toLowerCase().indexOf(risk.getIp().toLowerCase())>=0){
						logger.info("���ַ�������֤��"+valIp);
						redInfo="3";
						return true;
					}
				}
				if(StringUtils.isNotBlank(risk.getTradeUrl())){
					if(valUrl.toLowerCase().indexOf(risk.getTradeUrl().toLowerCase())>=0){
						logger.info("���ַ�������֤��"+valUrl);
						redInfo="4";
						return true;
					}
				}
			}
			
			return false;
		}
		public String buzero(String refundRMBMoney) {
			String refundRMB = "000000000000";
			String zero_12 = "000000000000";
			DecimalFormat decimalFormat = new DecimalFormat("##############0.00");
			if (StringUtils.isNotBlank(refundRMBMoney)
					&& refundRMBMoney.replace(".", "").matches("\\d+")) {
				String refundRMBStr = Double.valueOf((decimalFormat.format(Double
						.valueOf(refundRMBMoney)))) * 100 + "";
				String refundRMB_0 = zero_12
						+ refundRMBStr.substring(0, refundRMBStr.indexOf("."));
				refundRMB = refundRMB_0.substring(refundRMB_0.length() - 12,
						refundRMB_0.length());
			}
			return refundRMB;
		}

		public TradeManager getTradeManager() {
			return tradeManager;
		}

		public void setTradeManager(TradeManager tradeManager) {
			this.tradeManager = tradeManager;
		}

		public ShopManagerService getShopManagerService() {
			return shopManagerService;
		}

		public void setShopManagerService(ShopManagerService shopManagerService) {
			this.shopManagerService = shopManagerService;
		}

		public String getOrderno() {
			return orderno;
		}

		public void setOrderno(String orderno) {
			this.orderno = orderno;
		}

		public String getAmount() {
			return Amount;
		}

		public void setAmount(String amount) {
			Amount = amount;
		}

		public String getCurrency() {
			return Currency;
		}

		public void setCurrency(String currency) {
			Currency = currency;
		}

		public String getTradeAdd() {
			return tradeAdd;
		}

		public void setTradeAdd(String tradeAdd) {
			this.tradeAdd = tradeAdd;
		}

		public String getReturnURL() {
			return ReturnURL;
		}

		public void setReturnURL(String returnURL) {
			ReturnURL = returnURL;
		}

		public String getCardNo() {
			return cardNo;
		}

		public void setCardNo(String cardNo) {
			this.cardNo = cardNo;
		}

		public String getCvv2() {
			return cvv2;
		}

		public void setCvv2(String cvv2) {
			this.cvv2 = cvv2;
		}

		public String getMonth() {
			return month;
		}

		public void setMonth(String month) {
			this.month = month;
		}

		public String getYear() {
			return year;
		}

		public void setYear(String year) {
			this.year = year;
		}

		public String getMerchantOrderNo() {
			return merchantOrderNo;
		}

		public void setMerchantOrderNo(String merchantOrderNo) {
			this.merchantOrderNo = merchantOrderNo;
		}

		public int getResponseCode() {
			return responseCode;
		}

		public void setResponseCode(int responseCode) {
			this.responseCode = responseCode;
		}

		public String getMD5info() {
			return MD5info;
		}

		public void setMD5info(String mD5info) {
			MD5info = mD5info;
		}

		public String getMD5key() {
			return MD5key;
		}

		public void setMD5key(String mD5key) {
			MD5key = mD5key;
		}

		public String getMd5Value() {
			return md5Value;
		}

		public void setMd5Value(String md5Value) {
			this.md5Value = md5Value;
		}

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}

		public String getRemark() {
			return remark;
		}

		public void setRemark(String remark) {
			this.remark = remark;
		}

		public String getIp() {
			return ip;
		}

		public void setIp(String ip) {
			this.ip = ip;
		}

		public String getCartype() {
			return cartype;
		}

		public void setCartype(String cartype) {
			this.cartype = cartype;
		}

		public String getCountry() {
			return country;
		}

		public void setCountry(String country) {
			this.country = country;
		}

		public String getCookie() {
			return cookie;
		}

		public void setCookie(String cookie) {
			this.cookie = cookie;
		}

		public String getFirstname() {
			return firstname;
		}

		public void setFirstname(String firstname) {
			this.firstname = firstname;
		}

		public String getLastname() {
			return lastname;
		}

		public void setLastname(String lastname) {
			this.lastname = lastname;
		}

		public String getEmail() {
			return email;
		}

		public void setEmail(String email) {
			this.email = email;
		}

		public String getPhone() {
			return phone;
		}

		public void setPhone(String phone) {
			this.phone = phone;
		}

		public String getState() {
			return state;
		}

		public void setState(String state) {
			this.state = state;
		}

		public String getCity() {
			return city;
		}

		public void setCity(String city) {
			this.city = city;
		}

		public String getAddress() {
			return address;
		}

		public void setAddress(String address) {
			this.address = address;
		}

		public String getZipcode() {
			return zipcode;
		}

		public void setZipcode(String zipcode) {
			this.zipcode = zipcode;
		}

		public String getProducts() {
			return products;
		}

		public void setProducts(String products) {
			this.products = products;
		}

		public String getRedInfo() {
			return redInfo;
		}

		public void setRedInfo(String redInfo) {
			this.redInfo = redInfo;
		}

		public String getShippingFirstName() {
			return shippingFirstName;
		}

		public void setShippingFirstName(String shippingFirstName) {
			this.shippingFirstName = shippingFirstName;
		}

		public String getShippingLastName() {
			return shippingLastName;
		}

		public void setShippingLastName(String shippingLastName) {
			this.shippingLastName = shippingLastName;
		}

		public String getShippingAddress() {
			return shippingAddress;
		}

		public void setShippingAddress(String shippingAddress) {
			this.shippingAddress = shippingAddress;
		}

		public String getShippingCity() {
			return shippingCity;
		}

		public void setShippingCity(String shippingCity) {
			this.shippingCity = shippingCity;
		}

		public String getShippingSstate() {
			return shippingSstate;
		}

		public void setShippingSstate(String shippingSstate) {
			this.shippingSstate = shippingSstate;
		}

		public String getShippingCountry() {
			return shippingCountry;
		}

		public void setShippingCountry(String shippingCountry) {
			this.shippingCountry = shippingCountry;
		}

		public String getShippingZipcode() {
			return shippingZipcode;
		}

		public void setShippingZipcode(String shippingZipcode) {
			this.shippingZipcode = shippingZipcode;
		}

		public String getShippingEmail() {
			return shippingEmail;
		}

		public void setShippingEmail(String shippingEmail) {
			this.shippingEmail = shippingEmail;
		}

		public String getUserbrowser() {
			return userbrowser;
		}

		public void setUserbrowser(String userbrowser) {
			this.userbrowser = userbrowser;
		}

		public String getShippingPhone() {
			return shippingPhone;
		}

		public void setShippingPhone(String shippingPhone) {
			this.shippingPhone = shippingPhone;
		}

		public String getMerNo() {
			return merNo;
		}


		public void setMerNo(String merNo) {
			this.merNo = merNo;
		}


		public double getRmbmoney() {
			return rmbmoney;
		}


		public void setRmbmoney(double rmbmoney) {
			this.rmbmoney = rmbmoney;
		}


		public String getOrdercount() {
			return ordercount;
		}


		public void setOrdercount(String ordercount) {
			this.ordercount = ordercount;
		}


		public Double getTradeMoney() {
			return tradeMoney;
		}


		public void setTradeMoney(Double tradeMoney) {
			this.tradeMoney = tradeMoney;
		}


		public Date getTradetime() {
			return tradetime;
		}


		public void setTradetime(Date tradetime) {
			this.tradetime = tradetime;
		}


		public String getBilladdressby() {
			return billaddressby;
		}


		public void setBilladdressby(String billaddressby) {
			this.billaddressby = billaddressby;
		}
		
}
