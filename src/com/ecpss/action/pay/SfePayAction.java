package com.ecpss.action.pay;

import java.math.BigDecimal;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
import vpn.CaibaoMessage;
import vpn.CaibaoUtil;
import vpn.GQPayMessage;
import vpn.GQPayUtil;
import vpn.GooPayMessage;
import vpn.GooPayUtil;
import vpn.GrePayMessage;
import vpn.GrePayUtil;
import vpn.HJPayMessage;
import vpn.HJPayUtil;
import vpn.HJWPayMessage;
import vpn.HJWPayUtil;
import vpn.HRPayMessage;
import vpn.HRPayUtil;
import vpn.IPassPayMessage;
import vpn.IPassPayUtil;
import vpn.MasaPayMessage;
import vpn.MasaPayUtil;
import vpn.QuanQiuPayMessage;
import vpn.QuanQiuPayUtil;
import vpn.VpnUtil;
import vpn.VpnUtil_Moto;
import vpn.WPPayMessage;
import vpn.WPPayUtil;
import vpn.WRPayMessage;
import vpn.WRPayUtil;
import vpn.YinlianMessage;
import vpn.YinlianUtil;
import vpn.YoungPayMessage;
import vpn.YoungPayUtil;

import com.ecpss.action.BaseAction;
import com.ecpss.action.pay.fast.TradUtil;
import com.ecpss.action.pay.fast.TradeMessage;
import com.ecpss.action.pay.util.MaxMindExample;
import com.ecpss.model.channel.InternationalMerchantChannels;
import com.ecpss.model.payment.InternationalCardholdersInfo;
import com.ecpss.model.payment.InternationalTradeinfo;
import com.ecpss.model.risk.InternationalBackMaxMind;
import com.ecpss.model.risk.InternationalHighRisklist;
import com.ecpss.model.risk.InternationalRiskItems;
import com.ecpss.model.risk.InternationalSensitiveInfo;
import com.ecpss.model.risk.InternationalWhitelist;
import com.ecpss.model.shop.InternationalExchangerate;
import com.ecpss.model.shop.InternationalMerchant;
import com.ecpss.model.shop.InternationalMerchantManager;
import com.ecpss.model.shop.InternationalTerminalManager;
import com.ecpss.model.shop.InternationalTradecondition;
import com.ecpss.model.shop.InternationalWebchannels;
import com.ecpss.service.iservice.ShopManagerService;
import com.ecpss.util.AES;
import com.ecpss.util.CCSendMail;
import com.ecpss.util.EmailInfo;
import com.ecpss.util.GetBatchNo;
import com.ecpss.util.MD5;
import com.ecpss.util.StringUtil;

public class SfePayAction extends BaseAction {
	Logger logger = Logger.getLogger(SfePayAction.class.getName());
	@Autowired
	@Qualifier("tradeManager")
	private TradeManager tradeManager;
	@Autowired
	@Qualifier("shopManagerService")
	private ShopManagerService shopManagerService;
	private static Hashtable<String, Long> orderCache = new Hashtable<String, Long>();
	private static Thread orderExpiredThread;
	private String orderno;
	private String merNo;
	private String Amount;
	private double rmbmoney = 0; // 人民币交易金额
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
	private String isWeb="0";//是否添加网址
	private String isQWeb="0";
	private Double tradeMoney;// 支付金额
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
	private String billaddress;
	private String cardBank;
	private String xingChanel;
	private String maxmindRiskValue="0";
	private String maxMindInfo;
	//maxmind
	private HashMap h = new HashMap();
	private String maxmindValue;
	String bankName = null;
	String bankCountry = null;
	String bankPhone = null;
	private String values;
	private String SECURE_SECRET;
	// 定单	
	private Double orderAmout;// 订单金额
	
	private String csid;

	private MaxMindExample exam = new MaxMindExample();
	public String pay(){
		try{
		logger.info("进入sfe商户接口："+merNo);
		if (merNo == null) {
			logger.info("******************交易请求商户号错误，请检查****************"+this.getReturnURL());
			remark="merNo error!";
			responseCode = 10;
			message = "Payment Declined";
			logger.info("**********************商户号错误，请检查*********************************");
			return SUCCESS;
		}
		if(StringUtils.isBlank(firstname)){
			this.firstname = this.shippingFirstName;
			}
			if(StringUtils.isBlank(lastname)){
			this.lastname = this.shippingLastName;
			}
			if(StringUtils.isBlank(country)){
			this.country = this.shippingCountry;// 国家
			}
			if(StringUtils.isBlank(state)){
			this.state = this.shippingSstate; // 州
			}
			if(StringUtils.isBlank(city)){
			this.city = this.shippingCity; // 城市
			}
			if(StringUtils.isBlank(address)){
			this.address = this.shippingAddress; // 地址
			}
			if(StringUtils.isBlank(zipcode)){
			this.zipcode = this.shippingZipcode; // 邮编编号
			}
			if(StringUtils.isBlank(email)){
			this.email = this.shippingEmail;
			}
			if(StringUtils.isBlank(phone)){
			this.phone = this.shippingPhone;
			}
		GetBatchNo ut = new GetBatchNo();
		if("CNY".equals(Currency)){
			Currency="3";
		}
		logger.info("*****************打印订单信息******************");
		logger.info("MerNo:"+merNo);
		logger.info("cardbank:"+cardBank);
		logger.info("BillNo:"+merchantOrderNo);
		logger.info("Amount:"+Amount);
		logger.info("Currency:"+Currency);
		logger.info("MD5info:"+MD5info);
		logger.info("ReturnURL:"+ReturnURL);
		logger.info("shippingFirstName:"+shippingFirstName);
		logger.info("shippingLastName:"+shippingLastName);
		logger.info("shippingEmail:"+shippingEmail);
		logger.info("shippingPhone:"+shippingPhone);
		logger.info("shippingZipcode:"+shippingZipcode);
		logger.info("shippingAddress:"+shippingAddress);
		logger.info("shippingCity:"+shippingCity);
		logger.info("shippingSstate:"+shippingSstate);
		logger.info("shippingCountry:"+shippingCountry);
		logger.info("products:"+products);
		logger.info("maxMindInfo:"+maxMindInfo);
		logger.info("csid:"+csid);
		logger.info("*****************打印订单信息结束******************");
		//计算签名
		BASE64Decoder base64=new BASE64Decoder();
		HttpServletRequest request = ServletActionContext.getRequest();
		if(StringUtils.isNotBlank(csid)){
			csid=new String((base64.decodeBuffer(csid)));
			csid=URLDecoder.decode(csid);
		}
		logger.info("*****************打印csid信息******************："+csid);
		MD5 md5 = new MD5();
		if (orderExpiredThread == null)
		{
			orderExpiredThread = new Thread(){
				public void run() {
					while (true) {
						try {
							Thread.sleep(60 * 1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						//清除过期的缓存
						int expiredMin = 3;
						Iterator<Entry<String, Long>> it = orderCache.entrySet().iterator();
						List<String> expiredKeys = new ArrayList<String>();
						while (it.hasNext()) {
							Entry<String, Long> entry = it.next();
							long addTimeMill = entry.getValue();
							if ((System.currentTimeMillis() - addTimeMill) >= expiredMin * 60 * 1000)
							{
								expiredKeys.add(entry.getKey());
							}
						}
						for (String expiredKey : expiredKeys) {
							orderCache.remove(expiredKey);
							logger.info("移除超时的缓存订单：" + expiredKey);
						}
						logger.info("缓存中的订单数：" + orderCache.size());
					}
				}
			};
			orderExpiredThread.start();
		}
		
		//缓存订单
		String orderInfo = cardNo + Amount + ip + merNo;
		if (orderCache.get(orderInfo) != null)
		{
			logger.info("缓存中存在订单 " + orderInfo + "加入时间：" + orderCache.get(orderInfo));
			responseCode = 5;
			MD5info = merchantOrderNo + Currency + Amount + responseCode + MD5key;
			md5Value = md5.getMD5ofStr(MD5info);
			message = "Payment Declined";
			logger.info("*********************支付结果返回码***************************"+responseCode);
			return SUCCESS;
		}
		else
		{
			logger.info("新订单 加入缓存" + orderInfo);
			orderCache.put(orderInfo, System.currentTimeMillis());
		}
		
		InternationalMerchant merchant = (InternationalMerchant) this.commonService.uniqueResult("from InternationalMerchant where merno='"+merNo+"'");
		if(StringUtils.isBlank(MD5key)){
			MD5key=merchant.getMd5key();
		}
		if("4068".equals(merNo)){
			logger.info("***************************开始验证商户交易签名**********************************");
			String md5src = merNo + merchantOrderNo + Currency
					+ Amount + MD5key;
			logger.info("签名数据:"+md5src);
			md5src = md5.getMD5ofStr(md5src);
			logger.info("**********************传过来的签名："+MD5info);
			logger.info("**********************计算的签名："+md5src);
			if (!(md5src.equals(MD5info.toUpperCase()))) 
			{
				remark="Signature error!";
				responseCode = 13;
				message = "Payment Declined";
				logger.info("**********************商户交易签名错误，请检查*********************************");
				return SUCCESS;
			}
		}
		List changerates = this.commonService
				.getByList("select t.id,t.rate,t.type from international_exchangerate t,international_moneykindname m  where t.moneykindno=m.id "
						+ "and m.moneykindno='"
						+ this.Currency
						+ "' and t.executetime<sysdate-1   and t.type='1' order by t.executetime desc  "); // 交易汇率
		// 获取结算汇率
		List changeratesbalance = this.commonService
				.getByList("select t.id,t.rate,t.type from international_exchangerate t,international_moneykindname m  where t.moneykindno=m.id "
						+ "and m.moneykindno='"
						+ this.Currency
						+ "' and t.executetime<sysdate-1   and t.type='2' order by t.executetime desc  "); // 结算汇率
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
		if(tradetime!=null){
		tradeInfo.setTradeTime(tradetime);
		}else{
			tradetime = ut.getTime();
			tradeInfo.setTradeTime(tradetime);
		}
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
		tradeInfo.setCsid(csid);
		this.commonService.saveOrUpdate(tradeInfo);
		
		// / 获取持卡人的系统信息
		logger.info("*********************获取持卡人的系统信息结束***************************");
		String expirydate = month + year;

		String hql = "from InternationalTradeinfo t where t.orderNo='"
				+ orderno + "'";

			DecimalFormat decimalFormat = new DecimalFormat(
							"##############0.00");
			String ordercountValue = decimalFormat.format(tradeInfo.getTradeAmount());

			logger.info("*********************转换金额结束***************************");
		/** ******************风险控制******************** */
		// 获取信用卡前6位号码
		String backcardNo6 = (new StringBuilder(cardNo)).delete(6,
				cardNo.length()).toString();
		String backcardNo9 = (new StringBuilder(cardNo)).delete(9,
				cardNo.length()).toString();


		// 根据商户号,卡种信息获取通道
		logger.info("*********************卡种信息获取通道***************************"+cartype);
		String sql = "select a.channelname , d.id ,b.id as tid,d.priority from international_channels a ,international_creditcardtype b,international_mercreditcard c ,international_merchantchannels d where a.id=d.channelid and d.merchantid='"
				+ merchant.getId()
				+ "' and b.shortname='"
				+ cartype
				+ "' and d.id=c.merchannelid and b.id=c.creditcardid and d.onoff='1' and c.onoff='1' order by to_number(d.priority) asc";
		List chanellist = this.commonService.getByList(sql);
		String urlHql="select uc.channelName,uc.webUrl from InternationalURLChannel uc where uc.merchantId='"+ merchant.getId()+"' and uc.webUrl like'%"+tradeInfo.getTradeUrl()+"%'";
		List urllist=commonService.list(urlHql);
		// 商户通道ID
		String merchanID = "";
		// 通道名称
		String chanelName = "";
		// 卡种ID
		Long carType = 0l;
		Boolean V5Chanel=false;
		if(StringUtils.isNotBlank(country)&&country.length()>=5){	
			if("US".equals(country.substring(3, 5))||"CA".equals(country.substring(3, 5))){
				logger.info("交易国家："+country);
				V5Chanel=true;
			}
		}else{
			if("US".equals(country)||"CA".equals(country)){
				logger.info("交易国家："+country);
				V5Chanel=true;
			}
		}
		if (chanellist.size() > 0) {
			logger.info("通道名称："+xingChanel);
			//独立网站分配通道
			if (urllist.size() > 0) {
				for(int i=0;i<chanellist.size();i++){
					Object[] tem = (Object[]) chanellist.get(i);
					for(int j=0;j<urllist.size();j++){
						Object[] tem2 = (Object[]) urllist.get(j);
						if(tem2[0].toString().equals(tem[0].toString())){
							logger.info("出现网站："+tem2[1]+"匹配通道："+tem2[0]);
							merchanID = tem[1].toString();
							chanelName = tem[0].toString();
							carType = Long.valueOf(tem[2].toString());
							break;
						}
					}
				}
			}else{
				Object[] maxTem = (Object[]) chanellist.get(chanellist.size()-1);
				for(int i=0;i<chanellist.size();i++){
					Object[] tem1 = (Object[]) chanellist.get(i);
					String chanel1=(tem1[0].toString()).split("-")[0];
					String Schannel="S"+chanel1;
					String maxPri="";
					if(maxTem[3]==null){
						maxPri="0";
					}else{
						maxPri=maxTem[3].toString();
					}
					if(V5Chanel.equals(Boolean.TRUE)&& Integer.parseInt(maxPri)>=10){
						merchanID = maxTem[1].toString();
						chanelName = maxTem[0].toString();
						carType = Long.valueOf(maxTem[2].toString());
						break;
					}else if(Schannel.equals(xingChanel)){
						merchanID = tem1[1].toString();
						chanelName = tem1[0].toString();
						carType = Long.valueOf(tem1[2].toString());
						break;
					}else{
						Object[] minTem = (Object[]) chanellist.get(0);
						merchanID = minTem[1].toString();
						chanelName = minTem[0].toString();
						carType = Long.valueOf(minTem[2].toString());
					}
				}
			}
		} else {
			if("4".equals(cartype)){
			message = "Payment Declined (VisaCard cannot be used at this moment, please use your master card. Thank you)";
			}
			if("5".equals(cartype)){
			message = "Payment Declined (MasterCard cannot be used at this moment, please use your VISA card. Thank you)";
			}
			remark = "通道未设置/The channel is not set";
			responseCode = 16;

			String re[]=remark.split("/");
			tradeInfo.setRemark(re[0]);
			this.commonService.update(tradeInfo);
			logger.info("*********************支付结果返回码***************************"+responseCode);
			return SUCCESS;
		}
		logger.info("*********************卡种信息获取通道结束***************************");
						
		/*if("4132".equals(merNo)&&"USAUS".equals(country)){
			if("4".equals(cartype)){				
				chanelName = "HR-V";
				merchanID = "5183";
			}
		}*/
/*		InternationalMerchantChannels im = new InternationalMerchantChannels();
		if("USAUS".equals(country)){
			if("4".equals(cartype)){
				im = (InternationalMerchantChannels)  this.commonService.uniqueResult("from InternationalMerchantChannels where channelId=54 and merchantId='"+merchant.getId()+"'");
				merchanID = String.valueOf(im.getId());
				chanelName = "GQ-V";
			}else{
				im = (InternationalMerchantChannels)  this.commonService.uniqueResult("from InternationalMerchantChannels where channelId=40 and merchantId='"+merchant.getId()+"'");
				merchanID = String.valueOf(im.getId());
				chanelName = "HR-M";					
			}
		}else{
			im = (InternationalMerchantChannels) this.commonService
					.load(InternationalMerchantChannels.class,
							Long.valueOf(merchanID));				
		}*/
		// 商户通道
		logger.info("*********************商户通道信息***************************"+merchanID);
		InternationalMerchantChannels im = (InternationalMerchantChannels) this.commonService
				.load(InternationalMerchantChannels.class,
						Long.valueOf(merchanID));			
		logger.info("*********************商户通道名称***************************"+chanelName);
		String cardTypeNum = chanelName.substring(0, 3).toString();
		// 根据商户号,卡种信息获取通道
		logger.info(cartype + "开头的卡走的通道是:" + cardTypeNum);
		logger.info(tradeInfo.getId());
		String carlist = "select count(*) from international_cardholdersinfo t where t.tradeid='"
				+ tradeInfo.getId() + "'";
		int carInfoList = 0;
		carInfoList = this.tradeManager.intBySql(carlist);
		if (carInfoList > 0) {
			message = "Payment Declined";
			remark = "同一笔交易重复支付/The same transaction duplicate payment";
			responseCode = 31;
			tradeInfo.setTradeState("0"
					+ tradeInfo.getTradeState().substring(1,
							tradeInfo.getTradeState().length()));
			String re[]=remark.split("/");
			tradeInfo.setRemark(re[0]);
			this.commonService.update(tradeInfo);
			logger.info("*********************支付结果返回码***************************"+responseCode);
			return SUCCESS;
		}
		if("4132".equals(merNo)){
			List listc=commonService.list("select ti from InternationalTradeinfo ti where ti.merchantId='"+tradeInfo.getMerchantId()+"' and ti.merchantOrderNo='"+tradeInfo.getMerchantOrderNo()+"'");
			if(listc.size()>1){
				message = "Payment Declined";
				remark = "重复交易/Repeat business";
				responseCode = 7;
				MD5info = tradeInfo.getMerchantOrderNo() + tradeInfo.getMoneyType()
						+ ordercountValue + responseCode + MD5key;
				md5Value = md5.getMD5ofStr(MD5info);
				tradeInfo.setTradeState("3"
						+ tradeInfo.getTradeState().substring(1,
								tradeInfo.getTradeState().length()));
				String re[]=remark.split("/");
				tradeInfo.setRemark(re[0]);
				this.commonService.update(tradeInfo);
				logger.info("*********************支付结果返回码***************************"+responseCode);
				return SUCCESS;
			}
		}
		InternationalCardholdersInfo card = new InternationalCardholdersInfo();
		InternationalBackMaxMind bm=new InternationalBackMaxMind();
		if("1001".equals(merNo)||"4136".equals(merNo)&&StringUtils.isNotBlank(maxMindInfo)){
			logger.info("开始收集maxmindInfo"+maxMindInfo);
			JSONObject  jasonObject = JSONObject.fromObject(maxMindInfo);
			Map MaxMindMap = (Map)jasonObject;
			bm.setTradeId(tradeInfo.getId());
			bm.setRiskScore(MaxMindMap.get("values")+"");
			bm.setBinName(MaxMindMap.get("bankName")+"");
			bm.setBinCountry(MaxMindMap.get("bankCountry")+"");
			bm.setBinPhone(MaxMindMap.get("bankPhone")+"");
			bm.setIp_areaCode(MaxMindMap.get("ip_areaCode")+"");
			bm.setIp_postalCode(MaxMindMap.get("ip_postalCode")+"");
			bm.setIp_regionName(MaxMindMap.get("ip_regionName")+"");
			bm.setIp_region(MaxMindMap.get("ip_region")+"");
			bm.setIp_countryName(MaxMindMap.get("ip_countryName")+"");
			bm.setCountryCode(MaxMindMap.get("countryCode")+"");
			bm.setAnonymousProxy(MaxMindMap.get("anonymousProxy")+"");
			bm.setDistance(MaxMindMap.get("distance")+"");
			bm.setProxyScore(MaxMindMap.get("proxyScore")+"");
			bm.setPostalMatch(MaxMindMap.get("postalMatch")+"");
			bm.setCustPhoneInBillingLoc(MaxMindMap.get("custPhoneInBillingLoc")+"");
			bm.setShipCityPostalMatch(MaxMindMap.get("shipCityPostalMatch")+"");
			bm.setIp_city(MaxMindMap.get("ip_city")+"");
			maxmindRiskValue=MaxMindMap.get("values")+"";
			commonService.save(bm);
		}
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
		card.setEmail(email.trim());
		card.setPhone(phone);
		card.setUserBank(cardBank);
		logger.info("*********************国家简码***************************"+country);
		if(country.length()>=5){	
			card.setCountry(country.substring(3, 5));
		}else{
			card.setCountry(country);
		}
		card.setState(state);
		card.setCity(city);
		card.setAddress(address);
		card.setZipcode(zipcode);
		if(StringUtils.isNotBlank(maxmindRiskValue)){
		card.setMaxmindValue(Double.valueOf(maxmindRiskValue));
		}
		card.setShippingAddress(this.getShippingAddress());
		card.setShippingCity(this.getShippingCity());
		if(this.getShippingCountry().length()>=5){	
		card.setShippingCountry(this.getShippingCountry().substring(3, 5));
		}else{
			card.setShippingCountry(this.getShippingCountry());
		}
		card.setShippingEmail(this.getShippingEmail().trim());
		card.setShippingFullName(this.getShippingFirstName() + " "
				+ this.getShippingLastName());
		card.setShippingPhone(this.getShippingPhone());
		card.setShippingState(this.getShippingSstate());
		card.setShippingZip(this.getShippingZipcode());
		if(StringUtils.isNotBlank(products)){
			if(this.products.length()>2000){
				products=products.substring(0,200);
			}
		}
		card.setProductInfo(products);
		this.commonService.save(card);
		 tradeInfo.setTradeChannel(Long.valueOf(merchanID));
		 if(im.getChannelFee()!=null){
			 tradeInfo.setChannelFee(im.getChannelFee());
	     }
		 this.commonService.update(tradeInfo);
//		 List<Long> backCardValue=this.commonService.list("select t.merId from InternationalBacklist t where substr(t.cardno,1,6)='"
//					+ cardNo.substring(0,6) + "' and substr(t.cardno,13,4)='"+cardNo.substring(12,cardNo.length())+"' ");
//
//		 int riskCardValue = this.tradeManager
//			.intBySql("select count(1) from InternationalRisklist t where substr(t.cardno,1,6)='"
//					+ cardNo.substring(0,6) + "' and substr(t.cardno,13,4)='"+cardNo.substring(12,cardNo.length())+"' ");
		 List<Long> backCardValue=this.commonService.list("select t.merId from InternationalBacklist t where t.cardno='"
					+ cardNo + "' ");
		 int riskCardValue = this.tradeManager
					.intBySql("select count(1) from InternationalRisklist t where t.cardno='"
							+ cardNo + "' ");
		 int riskIPValue = this.tradeManager
							.intBySql("select count(1) from InternationalRisklist t where t.ip='"
									+ ip + "' ");
		 int riskEmailValue = this.tradeManager
							.intBySql("select count(1) from InternationalRisklist t where t.email='"
									+ email + "' ");
		 int risktradeUrlValue = this.tradeManager
							.intBySql("select count(1) from InternationalRisklist t where t.tradeUrl like '"
									+ tradeInfo.getTradeUrl() + "' ");
			if (riskCardValue>0||riskIPValue>0||riskEmailValue>0||risktradeUrlValue>0) {
					logger.info("riskCardValue:"+riskCardValue+"riskIPValue:"+riskIPValue+"riskEmailValue:"+riskEmailValue+"risktradeUrlValue:"+risktradeUrlValue);
					message = "Payment Declined";
					remark = "高风险交易/banned the trading of card";
					responseCode = 2;
					logger.info("返回状态码+++++++++" + responseCode);
					MD5info = tradeInfo.getMerchantOrderNo() + tradeInfo.getMoneyType()
								+ ordercountValue + responseCode + MD5key;
					md5Value = md5.getMD5ofStr(MD5info);
					tradeInfo.setTradeState("3"
							+ tradeInfo.getTradeState().substring(1,
									tradeInfo.getTradeState().length()));
					String re[]=remark.split("/");
					tradeInfo.setRemark(re[0]);
					this.commonService.update(tradeInfo);
					//收集高风险信息失败的交易信息
					if(riskCardValue>0){
						redInfo="1";
					}
					if(riskEmailValue>0){
						redInfo="2";
					}
					if(riskIPValue>0){
						redInfo="3";
					}
					if(risktradeUrlValue>0){
						redInfo="4";
					}
					InternationalHighRisklist rl=new InternationalHighRisklist();
					rl.setCardno(cardNo);
					rl.setEmail(email);
					rl.setIp(ip);
					rl.setPhone(phone);
					rl.setMerId(merchant.getMerno());
					rl.setTradeUrl(tradeInfo.getTradeUrl());
					if(StringUtils.isNotBlank(redInfo)){
						rl.setErrorColumn(redInfo);
					}
					rl.setOperator("systemRisk");
					rl.setCreateDate(new Date());
					rl.setIsWeb("1");
					rl.setIsQWeb("1");
					commonService.save(rl);
					logger.info("responseCode--------------" + responseCode);
					return SUCCESS;
					}
			
		Calendar calendar = Calendar.getInstance();// 此时打印它获取的是系统当前时间
		calendar.add(Calendar.DATE, -1); // 得到前一天
		String yestedayDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
				.format(calendar.getTime());
		// 根据ip或cardNober或Email，COCKET 信息查询持卡人24小时内交易的次数(成功的，待处理，待确认的交易)
		// 获取交易条件设置信息
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
		// 获取商户管理值
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

		// 获取POS
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
					remark = "通道未分配终端/Channel undistributed terminal";
					responseCode = 27;
					String re[]=remark.split("/");
					tradeInfo.setRemark(re[0]);
					this.commonService.update(tradeInfo);
					logger.info("*********************支付结果返回码***************************"+responseCode);
					return SUCCESS;
				}
			}
		}
		
		int carno = 0;
		carno = this.tradeManager
				.intBySql("select count(1) from international_cardholdersinfo f, international_tradeinfo t where f.tradeid=t.id and f.cardno='"
						+ cardNo
						+ "' and t.merchantid='"
						+ merchant.getId()
						+ "' and substr(t.tradestate,1,1) in(1,2,4,5,6) and t.tradetime>to_date('"
						+ yestedayDate + "','yyyy-MM-dd hh24:mi:ss')");
		if (Long.valueOf(carno) >= localCarNO) {
			message = "Payment Declined";
			remark = "重复交易/Repeat business";
			responseCode = 7;
			logger.info("返回状态码+++++++++" + responseCode);
			MD5info = tradeInfo.getMerchantOrderNo() + tradeInfo.getMoneyType()
					+ ordercountValue + responseCode + MD5key;
			md5Value = md5.getMD5ofStr(MD5info);
			tradeInfo.setTradeState("3"
					+ tradeInfo.getTradeState().substring(1,
							tradeInfo.getTradeState().length()));
			String re[]=remark.split("/");
			tradeInfo.setRemark(re[0]);
			this.commonService.update(tradeInfo);
			logger.info("*********************支付结果返回码***************************"+responseCode);
			return SUCCESS;
		}
		
		// 同一邮箱
					int emaillist = 0;
					emaillist = this.tradeManager
							.intBySql("select count(1) from international_cardholdersinfo f, international_tradeinfo t where f.tradeid=t.id and f.email='"
									+ this.email
									+ "' and t.merchantid='"
									+ merchant.getId()
									+ "' and substr(t.tradestate,1,1) in(1,2,4,5,6) and t.tradetime>to_date('"
									+ yestedayDate + "','yyyy-MM-dd hh24:mi:ss')");
//					List emailcount=this.commonService.list("select t from InternationalCardholdersInfo f,InternationalTradeinfo t where f.tradeId=t.id and f.email='"
//							+ this.email+ "' and t.merchantId='"+ merchant.getId()+ "' and substring(t.tradeState,1,1) in(1,2,4,5,6) and t.tradeTime>to_date('"
//							+ yestedayDate + "','yyyy-MM-dd hh24:mi:ss')");
					if (Long.valueOf(emaillist) >= localEMAIL) {
						message = "Payment Declined";
						remark = "重复交易/Repeat business";
						responseCode = 6;
						logger.info("返回状态码+++++++++" + responseCode);
						MD5info = tradeInfo.getMerchantOrderNo() + tradeInfo.getMoneyType()
								+ ordercountValue + responseCode + MD5key;
						md5Value = md5.getMD5ofStr(MD5info);
						tradeInfo.setTradeState("3"
								+ tradeInfo.getTradeState().substring(1,
										tradeInfo.getTradeState().length()));
						String re[]=remark.split("/");
						tradeInfo.setRemark(re[0]);
						this.commonService.update(tradeInfo);
						logger.info("*********************支付结果返回码***************************"+responseCode);
						return SUCCESS;
					}		
		
		String posNumber = "";
		String posMerchantNo = "";
		String bankBackRemark="";
		Long terminalId = 0L;
		if (it.size() > 0) {
			// /面向开通多个终端号的商户
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
					// /更新
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

			// /获取最终使用终端
			posNumber = it.get(0).getTerminalNo();
			posMerchantNo = it.get(0).getMerchantNo();
			terminalId = it.get(0).getId();
			bankBackRemark=it.get(0).getBankBackRemark();
		}
		logger.info("pos==================" + posNumber);
		this.commonService
				.deleteBySql("update international_terminalmanager t set t.isuses='1' where  t.id='"
						+ it.get(0).getId() + "'");
		// 更新终端
		this.commonService
				.deleteBySql("update  international_tradeinfo t  set t.VIPTerminalNo='"
						+ posNumber
						+ "' where t.id='"
						+ tradeInfo.getId()
						+ "'");

		// 获取交易金额
		// 单笔交易默认不能大于600元

		int backCardValue6 = this.tradeManager
				.intBySql("select count(*) from Internationalbacklist t where t.cardno='"
						+ backcardNo6 + "' ");
		int backCardValue9 = this.tradeManager
				.intBySql("select count(*) from Internationalbacklist t where t.cardno='"
						+ backcardNo9 + "' ");
		String chnals = chanelName.split("-")[0];
		// 验证黑邮箱
		List<Long> backEmailValue=this.commonService.list("select t.merId from InternationalBacklist t where lower(t.email)='"
						+ email.trim().toLowerCase() + "' ");
		// 验证黑IP
		List<Long> backIpValue=this.commonService.list("select t.merId from InternationalBacklist t where t.ip='"
						+ ip + "' ");
		if (tradeInfo.getTradeAmount() > merchantmanager.getPenQuota()) {
			message = "Payment Declined";
			remark = "单笔超限/Single pen limit";
			responseCode = 3;
			logger.info("返回状态码+++++++++" + responseCode);
			tradeInfo.setTradeState("0"
					+ tradeInfo.getTradeState().substring(1,
							tradeInfo.getTradeState().length()));
			String re[]=remark.split("/");
			tradeInfo.setRemark(re[0]);
			this.commonService.update(tradeInfo);
			logger.info("*********************支付结果返回码***************************"+responseCode);
			return SUCCESS;
		}
		//相同客户信息不同交易网站验证
		Calendar calendarUrl = Calendar.getInstance();// 此时打印它获取的是系统当前时间
		calendarUrl.add(Calendar.MONTH,-6); // 得到6月前
		String threeMouth = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
					.format(calendarUrl.getTime());
		List repUrllist = commonService.getByList
				("select t.orderNo,nvl(t.tradeUrl,0) tradeUrl from international_cardholdersinfo f, international_tradeinfo t where f.tradeid=t.id and (f.cardNo='"
						+ AES.setCarNo(cardNo)
						+ "' or f.ip='"+ip+"' or f.email='"+email+"') and t.tradeTime>to_date('"
			+ threeMouth + "','yyyy-MM-dd hh24:mi:ss')");
		Boolean  valiNoUrl=false;
		if(repUrllist!=null&&repUrllist.size()>0){
		for (int i=0;i<repUrllist.size();i++){
			Object[] queUrl=(Object[]) repUrllist.get(i);
			if(queUrl[0].toString().indexOf('*',1)>0){
				logger.info("****出现特高风险********");
				InternationalHighRisklist rl=new InternationalHighRisklist();
				rl.setCardno(cardNo);
				rl.setEmail(email);
				rl.setIp(ip);
				rl.setPhone(phone);
				rl.setMerId(merchant.getMerno());
				rl.setTradeUrl(tradeInfo.getTradeUrl());
				rl.setOperator("systemVisaRisk");
				rl.setCreateDate(new Date());
				rl.setIsWeb("1");
				rl.setIsQWeb("1");
				commonService.save(rl);
				message = "Payment Declined！";
				remark = "Payment Declined！04";
				responseCode = 2;
				tradeInfo.setTradeState("3"
						+ tradeInfo.getTradeState().substring(1,
								tradeInfo.getTradeState().length()));
				logger.info("返回状态码+++++++++" + responseCode);
				MD5info = tradeInfo.getMerchantOrderNo() + tradeInfo.getMoneyType()
						+ ordercountValue + responseCode + MD5key;
				md5Value = md5.getMD5ofStr(MD5info);
				tradeInfo.setRemark(remark);
				this.commonService.update(tradeInfo);
				return SUCCESS;
				}else if(!(tradeInfo.getTradeUrl().trim()).toLowerCase().equals(queUrl[1].toString().toLowerCase())){
					valiNoUrl=true;
				}
			}
		}
		
		//黑卡库循环判断（原先在风控等级控制里现在copy出来单独做一次）
		for(Long backCard:backCardValue){
				if (backCard==null||(merchant.getId()).equals(backCard)) {
					message = "Payment Declined";
					remark = "黑卡/banned the trading of card";
					responseCode = 2;
					logger.info("返回状态码+++++++++" + responseCode);
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
			
		// 黑卡bean（原先在风控等级控制里现在copy出来单独做一次）
		if (backCardValue6 > 0 || backCardValue9 > 0) {
			message = "Payment Declined";
			remark = "黑卡/banned the trading of card";
			responseCode = 17;
			logger.info("返回状态码+++++++++" + responseCode);
			tradeInfo.setTradeState("0"
					+ tradeInfo.getTradeState().substring(1,
							tradeInfo.getTradeState().length()));
			String re[]=remark.split("/");
			tradeInfo.setRemark(re[0]);
			this.commonService.update(tradeInfo);
			logger.info("*********************支付结果返回码***************************"+responseCode);
			return SUCCESS;
		}
		
		//返回true是指能找到对应风控等级的商户号、卡号、ip、邮箱、网址、国家
		Boolean whiteNoUrl=valWhiteList(merchant.getMerno()+"",cardNo,ip,email,tradeInfo.getTradeUrl(),country+","+bankCountry,"7");
		if(valiNoUrl.equals(Boolean.TRUE)&&whiteNoUrl.equals(Boolean.TRUE)){
			message = "Payment Declined！";
			remark = "Payment Declined！03";
			responseCode = 19;
			logger.info("白名单第七等级返回状态码:" + responseCode);
			MD5info = tradeInfo.getMerchantOrderNo() + tradeInfo.getMoneyType()
					+ ordercountValue + responseCode + MD5key;
			md5Value = md5.getMD5ofStr(MD5info);
			tradeInfo.setTradeState("2"
					+ tradeInfo.getTradeState().substring(1,
							tradeInfo.getTradeState().length()));
			tradeInfo.setRemark(remark);
			this.commonService.update(tradeInfo);
			shopManagerService.addTemporaryTradInfo(orderno, year, month,cvv2,country,MD5key, ip,"MSIE 10.0","Payment Declined！03");
			return SUCCESS;
			}	
		//历史拒付过的交易失败
		if(!"3918".equals(merchant.getMerno()+"")&&!"4087".equals(merchant.getMerno()+"")&&!"4110".equals(merchant.getMerno()+"")){
			int refuseCard = 0;
			refuseCard = this.tradeManager
					.intBySql("select count(1) from international_cardholdersinfo f, international_tradeinfo t where f.tradeid=t.id and (f.cardno='"
							+ AES.setCarNo(this.cardNo)
							+ "' or f.email='"+email
							+ "') and substr(t.tradestate,3,1)='1' ");
			if (Long.valueOf(refuseCard) >= 1) {
				message = "Payment Declined";
				remark = "已拒付过的交易/The transaction has dishonored";
				responseCode = 38;
				logger.info("返回状态码+++++++++" + responseCode);
				MD5info = tradeInfo.getMerchantOrderNo() + tradeInfo.getMoneyType()
						+ ordercountValue + responseCode + MD5key;
				md5Value = md5.getMD5ofStr(MD5info);
				tradeInfo.setTradeState("3"
						+ tradeInfo.getTradeState().substring(1,
								tradeInfo.getTradeState().length()));
				String re[]=remark.split("/");
				tradeInfo.setRemark(re[0]);
				this.commonService.update(tradeInfo);
				logger.info("*********************支付结果返回码***************************"+responseCode);
				return SUCCESS;
	
			}
		}
		// 连接maxmind系统
		if("3918".equals(merchant.getMerno()+"")){
			try {
				HashMap hm = new HashMap();
				// 加密串 license_key 
				int index = email.indexOf("@");
				String domian = email.substring(index + 1, email.length());
				hm.put("license_key", "9kbrHiIOJ9ZS");
				hm.put("i", ip);
				hm.put("domain", domian);
				hm.put("emailMD5", md5.getMD5ofStr(email.toLowerCase()));
				hm.put("custPhone", phone);
				if(country.length()>=5){
				hm.put("country", country.substring(3, 5));
				}else{
					hm.put("country", country);
				}
				hm.put("city", city);
				hm.put("region", state);
				hm.put("shipAddr", address);
				hm.put("postal", zipcode.toString());
				// hm.put("bin", cardnum);
				hm.put("bin", cardNo.substring(0, 6));
				hm.put("binName", cardBank);

				// standard 低级
				// premium 高级
				// 正式运行的时候要用这个 premium ; standard为调试用的标准
				hm.put("requested_type", "premium");

				Hashtable ht = getmmValue(hm,tradeInfo.getId());
				maxmindValue = (String) ht.get("values");
				bankName = (String) ht.get("bankName");
				bankCountry = (String) ht.get("bankCountry");
				bankPhone = (String) ht.get("bankPhone");
				logger.info("maxmindValue--------------" + maxmindValue);
			} catch (Exception ex) {
				try {
					CCSendMail.setSendMail("878701211@qq.com",
							"2.0 maxmind error", "sfepay@sfepay.com");
				} catch (Exception e) {
					// 继续执行下去
				}
			}

			Double maxmind = 0d;
			// 返回分值
			if (maxmindValue != null && maxmindValue != "") {
				maxmind = Double.valueOf(maxmindValue);
			}
			card.setMaxmindValue(maxmind);
			card.setBankcountry(bankCountry);
			card.setBankname(bankName);
			card.setBankphone(bankPhone);
			this.commonService.update(card);
			maxmindRiskValue=maxmind.toString();
//		
		}
		
		/**
		 * 禁止交易地区按照持卡人账单地区check
		 */
			if(("3918".equals(merNo)||"4110".equals(merNo))&&country.length()<5){
				country="USAUS";
				shippingCountry="USAUS";
			}
			if(!"3918".equals(merNo)&&!"4068".equals(merchant.getMerno()+"")&&!"4087".equals(merchant.getMerno()+"")&&!"4110".equals(merchant.getMerno()+"")){
			    String queryarea = "select m.id from MerchantRiskControl m where m.merchantId="
				+ merchant.getId()
				+ " and (substr(m.area,1,2) like '"
				+ country.substring(3, 5) + "' or substr(m.area,1,2) like '"
				+ bankCountry + "')";
			   	String allQueryarea = "select m.id from MerchantRiskControl m where m.merchantId is null"
			    		+ " and (substr(m.area,1,2) like '"
			    		+ country.substring(3, 5) + "' or substr(m.area,1,2) like '"
			    		+ bankCountry + "')";
			    logger.info(queryarea);
				List queryarealist = this.commonService.list(queryarea);
				List allQueryarealist = this.commonService.list(allQueryarea);
				logger.info(queryarealist.size());
				if (queryarealist.size() > 0||allQueryarealist.size()>0) {
					message = "Payment Declined";
					remark = "禁止交易地区/Prohibited transaction area";
					responseCode = 31;
					logger.info("返回状态码+++++++++" + responseCode);
					MD5info = tradeInfo.getMerchantOrderNo() + tradeInfo.getMoneyType()
							+ ordercountValue + responseCode + MD5key;
					md5Value = md5.getMD5ofStr(MD5info);
					tradeInfo.setTradeState("0"
							+ tradeInfo.getTradeState().substring(1,
									tradeInfo.getTradeState().length()));
					String re[]=remark.split("/");
					tradeInfo.setRemark(re[0]);
					this.commonService.update(tradeInfo);
					return SUCCESS;
				}
			}
			
			//风险分数
			if(StringUtils.isNotBlank(maxmindRiskValue)){
				if (Double.valueOf(maxmindRiskValue) > im.getMaxmind_lv2()) {
					message = "Payment Declined";
					remark = "高风险！/High-risk";
					responseCode = 19;
					logger.info("返回状态码+++++++++" + responseCode);
					MD5info = tradeInfo.getMerchantOrderNo() + tradeInfo.getMoneyType()
							+ ordercountValue + responseCode + MD5key;
					md5Value = md5.getMD5ofStr(MD5info);
					tradeInfo.setTradeState("2"
							+ tradeInfo.getTradeState().substring(1,
										tradeInfo.getTradeState().length()));
					String re[]=remark.split("/");
					tradeInfo.setRemark(re[0]);
					this.commonService.update(tradeInfo);
					shopManagerService.addTemporaryTradInfo(orderno, year, month,cvv2,country,MD5key, ip,"MSIE 10.0","risk");
					return SUCCESS;
				}
			}
			
			
			/*//08做的是一个月内只要卡号、邮箱、IP有做过交易的全部进待处理(单独拿到风控等级之外)
			Calendar calendarUrl6 = Calendar.getInstance();// 此时打印它获取的是系统当前时间
			calendarUrl6.add(Calendar.MONTH,-1); // 得到一月前
			String oneMouth6 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
						.format(calendarUrl6.getTime());
			List repUrllist6 = commonService.getByList
					("select t.orderNo from international_cardholdersinfo f, international_tradeinfo t where f.tradeid=t.id and (f.cardNo='"
							+ AES.setCarNo(cardNo)
							+ "' or f.ip='"+ip.trim()+"' or f.email='"+email.trim()+"') and t.tradeTime>to_date('"
				+ oneMouth6 + "','yyyy-MM-dd hh24:mi:ss')");
			if(repUrllist6!=null&&repUrllist6.size()>1){
				message = "Payment Declined！";
				remark = "Payment Declined！08";
				responseCode = 19;
				logger.info("返回状态码+++++++++" + responseCode);
				MD5info = tradeInfo.getMerchantOrderNo() + tradeInfo.getMoneyType()
						+ ordercountValue + responseCode + MD5key;
				md5Value = md5.getMD5ofStr(MD5info);
				tradeInfo.setTradeState("2"
						+ tradeInfo.getTradeState().substring(1,
								tradeInfo.getTradeState().length()));
				tradeInfo.setRemark(remark);
				this.commonService.update(tradeInfo);
				shopManagerService.addTemporaryTradInfo(orderno, year, month,cvv2,country,MD5key, ip,"MSIE 10.0","Payment Declined！08");
				return SUCCESS;				
			}*/
			
		//返回true是指能找到对应风控等级的商户号、卡号、ip、邮箱、网址、国家（第一等级返回true不等于false所有不进下列风控判断）
		Boolean oneRisk=valWhiteList(merchant.getMerno()+"",cardNo,ip,email,tradeInfo.getTradeUrl(),country+","+bankCountry,"1");
		if(oneRisk.equals(Boolean.FALSE)){
			//isweb为1时订单进来商户看到的是取消状态，为0时商户看到的是待处理状态，isweb为1时，isQweb必为1
			//isQWeb为1时网站不关联的状态
			String isWebHql="from InternationalHighRisklist where merId='"+merchant.getMerno()+"' and isQWeb='1'";
			List<InternationalHighRisklist> listweb=commonService.list(isWebHql);
			if(listweb.size()>0){
				isQWeb="1";
			}
			
		logger.info("****开始防风险验证********");
		//在InternationalHighRisklist黑卡库里有过记录的，返回true（注意这个判断如果有记录，会去查查出来的数据的isQWeb是否为1，如果是1，属性isQweb赋值为1，还会判断数据库中isweb是否为1，为1则属性isweb赋值为1）
		Boolean isVisaVal=validateVisa(cardNo,email,ip,tradeInfo.getTradeUrl(),phone,zipcode);
		String valCountry="";
		if(StringUtils.isNotBlank(bankCountry)){
			valCountry=bankCountry;
		}else{
			valCountry=country.substring(3, 5);
		}
		if(isVisaVal.equals(Boolean.TRUE)){
			//验证1国家  、2地区、3网站是否在这张表里并需要人工处理，在表里返回true
			Boolean isCountryVal=validateRiskItems(valCountry, "1");
			InternationalHighRisklist rl=new InternationalHighRisklist();
			//注意这查询和validateVisa不一样的地方虽然都是查同一张表，但是上面那个还有网址、电话和邮编）
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
			//如果不是因为卡号，邮箱，ip在InternationalHighRisklist查到有记录的（表里还有好多属性），这样做的目的是可能是钓鱼用了新的卡号，ip，邮箱
			if(c.equals(Boolean.FALSE)||e.equals(Boolean.FALSE)||i.equals(Boolean.FALSE)){
				rl.setCardno(cardNo);
				rl.setEmail(email);
				rl.setIp(ip);
				rl.setMerId(merchant.getMerno());
				rl.setTradeUrl(tradeInfo.getTradeUrl());
				rl.setPhone(phone);
				rl.setOperator("system");
				rl.setCreateDate(new Date());
				if(StringUtils.isNotBlank(redInfo)){
					rl.setErrorColumn(redInfo);
				}
				rl.setIsQWeb(isQWeb);
				rl.setIsWeb(isWeb);
				commonService.save(rl);
			}
			logger.info("采集风险信息结束");
			message = "Payment Declined！";
			//交易直接取消并且国家在风控项目表里有过记录（international_riskitems）
			if("1".equals(isWeb)&&isCountryVal.equals(Boolean.TRUE)){
				responseCode = 2;
				remark = "Payment Declined！04";
				tradeInfo.setTradeState("3"
						+ tradeInfo.getTradeState().substring(1,
								tradeInfo.getTradeState().length()));
			}else{
					responseCode = 19;
					//交易直接取消
					if("1".equals(isWeb)){
						remark = "Payment Declined！05";
					}else{
						remark = "Payment Declined！01";
					}
					tradeInfo.setTradeState("2"
							+ tradeInfo.getTradeState().substring(1,
									tradeInfo.getTradeState().length()));
			}
			logger.info("返回状态码+++++++++" + responseCode);
			tradeInfo.setRemark(remark);
			this.commonService.update(tradeInfo);
			shopManagerService.addTemporaryTradInfo(orderno, year, month,cvv2,country,MD5key, ip,"MSIE 10.0",remark);
			EmailInfo emailinfo = new EmailInfo();
			String mailinfo = emailinfo.getRiskInfoToSystem(merchant.getMerno(),merchantOrderNo,
					ordercountValue,new Long(Currency),cardNo,email,ip,tradeInfo.getTradeUrl(),redInfo);
			try {
				// 发送邮件,如果发送失败插入数据库发送
				CCSendMail.setSendMail("983321897@qq.com", mailinfo,
						"sfepay@sfepay.com");
				logger.info("风险信息邮件立马发出");
			} catch (Exception ex) {
				logger.error(ex);
				logger.info("风险信息邮件发送失败");
				return SUCCESS;
			}
			return SUCCESS;
		}
		logger.info("****结束防风险验证********");
		if("US".equals(country.substring(3, 5))||"CA".equals(country.substring(3, 5))||"GB".equals(country.substring(3, 5))||"US".equals(bankCountry)||"CA".equals(bankCountry)||"GB".equals(bankCountry)){
		Boolean twoRisk=valWhiteList(merchant.getMerno()+"",cardNo,ip,email,tradeInfo.getTradeUrl(),country+","+bankCountry,"2");
		if(twoRisk.equals(Boolean.FALSE)){
		//新卡bin处理
		List binlist = commonService.getByList
				("select f.id,f.cardNo6 from international_cardholdersinfo f, international_tradeinfo t where f.tradeid=t.id and f.cardNo6='"
						+ AES.setCarNo(backcardNo6)
						+ "'");
		if (binlist.size()<=1) {
			message = "Payment Declined！";
			remark = "Payment Declined！02";
			responseCode = 19;
			logger.info("返回状态码+++++++++" + responseCode);
			MD5info = tradeInfo.getMerchantOrderNo() + tradeInfo.getMoneyType()
					+ ordercountValue + responseCode + MD5key;
			md5Value = md5.getMD5ofStr(MD5info);
			tradeInfo.setTradeState("2"
					+ tradeInfo.getTradeState().substring(1,
							tradeInfo.getTradeState().length()));
			tradeInfo.setRemark(remark);
			this.commonService.update(tradeInfo);
			shopManagerService.addTemporaryTradInfo(orderno, year, month,cvv2,country,MD5key, ip,"MSIE 10.0","Payment Declined！02");
			return SUCCESS;
		}		
		//相同客户信息不同交易网站验证
		if(repUrllist!=null&&repUrllist.size()>0){
				Boolean  valiUrl=false;
				for (int i=0;i<repUrllist.size();i++){
					Object[] queUrl=(Object[]) repUrllist.get(i);
					if(!(tradeInfo.getTradeUrl().trim()).toLowerCase().equals(queUrl[1].toString().toLowerCase())){
						valiUrl=true;
						break;
					}
				}
			if(valiUrl.equals(Boolean.TRUE)){
				message = "Payment Declined！";
				remark = "Payment Declined！03";
				responseCode = 19;
				logger.info("返回状态码+++++++++" + responseCode);
				MD5info = tradeInfo.getMerchantOrderNo() + tradeInfo.getMoneyType()
						+ ordercountValue + responseCode + MD5key;
				md5Value = md5.getMD5ofStr(MD5info);
				tradeInfo.setTradeState("2"
						+ tradeInfo.getTradeState().substring(1,
								tradeInfo.getTradeState().length()));
				tradeInfo.setRemark(remark);
				this.commonService.update(tradeInfo);
				shopManagerService.addTemporaryTradInfo(orderno, year, month,cvv2,country,MD5key, ip,"MSIE 10.0","Payment Declined！03");
				return SUCCESS;
				}	
			}
		
		//08做的是一个月内只要卡号、邮箱、IP有做过交易的全部进待处理
		Calendar calendarUrl2 = Calendar.getInstance();// 此时打印它获取的是系统当前时间
		calendarUrl2.add(Calendar.MONTH,-1); // 得到一月前
		String oneMouth = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
					.format(calendarUrl2.getTime());
		List repUrllist2 = commonService.getByList
				("select t.orderNo from international_cardholdersinfo f, international_tradeinfo t where f.tradeid=t.id and (f.cardNo='"
						+ AES.setCarNo(cardNo)
						+ "' or f.ip='"+ip.trim()+"' or f.email='"+email.trim()+"') and t.tradeTime>to_date('"
			+ oneMouth + "','yyyy-MM-dd hh24:mi:ss')");
		if(repUrllist2!=null&&repUrllist2.size()>1){
			message = "Payment Declined！";
			remark = "Payment Declined！08";
			responseCode = 19;
			logger.info("返回状态码+++++++++" + responseCode);
			MD5info = tradeInfo.getMerchantOrderNo() + tradeInfo.getMoneyType()
					+ ordercountValue + responseCode + MD5key;
			md5Value = md5.getMD5ofStr(MD5info);
			tradeInfo.setTradeState("2"
					+ tradeInfo.getTradeState().substring(1,
							tradeInfo.getTradeState().length()));
			tradeInfo.setRemark(remark);
			this.commonService.update(tradeInfo);
			shopManagerService.addTemporaryTradInfo(orderno, year, month,cvv2,country,MD5key, ip,"MSIE 10.0","Payment Declined！08");
			return SUCCESS;				
		}
		
		
			//验证订单敏感信息
			if(StringUtils.isBlank(products)){
				products="sfepay";
			}
			logger.info("****开始验证敏感信息********");
			Boolean isValiBill=validateSensitive("1",merchantOrderNo, products);
			Boolean isValiEmail=validateSensitive("2",email, products);
			Boolean isValiTradurl=validateSensitive("3",tradeInfo.getTradeUrl(), products);
			Boolean isValiIp=validateSensitive("4",ip, products);
			logger.info("isValiBill:"+isValiBill);
			logger.info("isValiEmail:"+isValiEmail);
			logger.info("isValiTradurl:"+isValiTradurl);
			logger.info("isValiIp:"+isValiIp);
			if(isValiBill.equals(Boolean.TRUE)||isValiEmail.equals(Boolean.TRUE)||isValiTradurl.equals(Boolean.TRUE)||isValiIp.equals(Boolean.TRUE)){
				message = "Payment Declined！";
				remark = "Payment Declined！00";
				responseCode = 19;
				logger.info("返回状态码+++++++++" + responseCode);
				tradeInfo.setTradeState("2"
						+ tradeInfo.getTradeState().substring(1,
										tradeInfo.getTradeState().length()));
				tradeInfo.setRemark(remark);
				this.commonService.update(tradeInfo);
				shopManagerService.addTemporaryTradInfo(orderno, year, month,cvv2,country,MD5key, ip,"MSIE 10.0","Payment Declined！00");
				return SUCCESS;
			}
			logger.info("****验证敏感信息结束********");	
			//风控网站类型的控制
			logger.info("****开始验证网站分类信息********");
			Boolean isValiWebType=validateRiskItems(tradeInfo.getTradeUrl(), "3");
			if(isValiWebType.equals(Boolean.TRUE)){
				message = "Payment Declined！";
				remark = "Payment Declined！00";
				responseCode = 19;
				logger.info("返回状态码+++++++++" + responseCode);
				MD5info = tradeInfo.getMerchantOrderNo() + tradeInfo.getMoneyType()
						+ ordercountValue + responseCode + MD5key;
				md5Value = md5.getMD5ofStr(MD5info);
				tradeInfo.setTradeState("2"
						+ tradeInfo.getTradeState().substring(1,
								tradeInfo.getTradeState().length()));
				tradeInfo.setRemark(remark);
				this.commonService.update(tradeInfo);
				shopManagerService.addTemporaryTradInfo(orderno, year, month,cvv2,country,MD5key, ip,"MSIE 10.0","Payment Declined！07");
				return SUCCESS;
			}
			logger.info("****结束验证网站分类信息********");
			//风控地域的控制
			String ipRegionName=bm.getIp_regionName();
			Boolean isRegionType=false;
			if(StringUtils.isBlank(ipRegionName)){
				ipRegionName=state;
			}
			if(StringUtils.isBlank(ipRegionName)&&StringUtils.isBlank(state)){
				isRegionType=true;
			}else{
				logger.info("****开始验证地域信息********");
				isRegionType=validateRiskItems(ipRegionName, "2");
			}
			if(isRegionType.equals(Boolean.TRUE)){
					message = "Payment Declined！";
					remark = "Payment Declined！00";
					responseCode = 19;
					logger.info("返回状态码+++++++++" + responseCode);
					MD5info = tradeInfo.getMerchantOrderNo() + tradeInfo.getMoneyType()
							+ ordercountValue + responseCode + MD5key;
					md5Value = md5.getMD5ofStr(MD5info);
					tradeInfo.setTradeState("2"
							+ tradeInfo.getTradeState().substring(1,
									tradeInfo.getTradeState().length()));
					tradeInfo.setRemark(remark);
					this.commonService.update(tradeInfo);
					shopManagerService.addTemporaryTradInfo(orderno, year, month,cvv2,country,MD5key, ip,"MSIE 10.0","Payment Declined！06");
					return SUCCESS;
				}
				logger.info("****结束验证地域信息********");
		}
	}
		// 黑卡库
		for(Long backCard:backCardValue){
		if (backCard==null||(merchant.getId()).equals(backCard)) {
			message = "Payment Declined";
			remark = "黑卡/banned the trading of card";
			responseCode = 2;
			logger.info("返回状态码+++++++++" + responseCode);
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
		
		// 黑邮箱
		for(Long backEmail:backEmailValue){	
		if (backEmail==null||(merchant.getId()).equals(backEmail) ) {
			message = "Payment Declined";
			remark = "黑邮箱/banned the trading of email";
			responseCode = 2;
			logger.info("返回状态码+++++++++" + responseCode);
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
			remark = "黑IP/banned the trading of ip";
			responseCode = 2;
			logger.info("返回状态码+++++++++" + responseCode);
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
		// 黑卡bean
		if (backCardValue6 > 0 || backCardValue9 > 0) {
			message = "Payment Declined";
			remark = "黑卡/banned the trading of card";
			responseCode = 17;
			logger.info("返回状态码+++++++++" + responseCode);
			tradeInfo.setTradeState("0"
					+ tradeInfo.getTradeState().substring(1,
							tradeInfo.getTradeState().length()));
			String re[]=remark.split("/");
			tradeInfo.setRemark(re[0]);
			this.commonService.update(tradeInfo);
			logger.info("*********************支付结果返回码***************************"+responseCode);
			return SUCCESS;
		}
	}


		// 同一卡号失败次数
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
					remark = "重复失败次数过多/Repeated failure many times";
					responseCode = 7;
					logger.info("返回状态码+++++++++" + responseCode);
					tradeInfo.setTradeState("3"
							+ tradeInfo.getTradeState().substring(1,
									tradeInfo.getTradeState().length()));
					String re[]=remark.split("/");
					tradeInfo.setRemark(re[0]);
					this.commonService.update(tradeInfo);
					logger.info("*********************支付结果返回码***************************"+responseCode);
					return SUCCESS;
				}
			if("4132".equals(merNo)){
				int emailcount = 0;
				emailcount = this.tradeManager
						.intBySql("select count(1) from international_cardholdersinfo f, international_tradeinfo t where f.tradeid=t.id and f.email='"
								+ this.email
								+ "' and t.merchantid='"
								+ merchant.getId()
								+ "' and substr(t.tradestate,1,1) in(1,2,4,5,6) and t.tradetime>to_date('"
								+ yestedayDate + "','yyyy-MM-dd hh24:mi:ss')");
					if (Long.valueOf(emailcount) >= 1) {
						message = "Payment Declined";
						remark = "重复交易/Repeat business";
						responseCode = 6;
						logger.info("返回状态码+++++++++" + responseCode);
						tradeInfo.setTradeState("3"
								+ tradeInfo.getTradeState().substring(1,
										tradeInfo.getTradeState().length()));
						String re[]=remark.split("/");
						tradeInfo.setRemark(re[0]);
						this.commonService.update(tradeInfo);
						logger.info("*********************支付结果返回码***************************"+responseCode);
						return SUCCESS;
					}
			}
			if("GP-V".equals(chanelName)){
				Calendar today = Calendar.getInstance();
				today.set(Calendar.HOUR_OF_DAY, 0);
				today.set(Calendar.MINUTE, 0);
				today.set(Calendar.SECOND,0);
				String todayTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
				.format(today.getTime());	
	
/*					StringBuffer sb = new StringBuffer();
 					String todaytotalmoney = "select sum(ti.rmbAmount) ";
					sb.append("from InternationalTradeinfo ti where ti.merchantId='"+tradeInfo.getMerchantId()+"' and substr(ti.tradeState,0,1)='"+1+"' and ti.tradeTime>to_date('"
							+ todayTime + "','yyyy-MM-dd hh24:mi:ss')");
					if("4160".equals(merNo)){
						sb.append(" and ti.tradeChannel= '5504' ");
					}
	
				Double todaymoney = (Double) this.commonService.uniqueResult(todaytotalmoney+sb.toString());	*/
				String todaytotalmoney = "select sum(ti.rmbAmount) from InternationalTradeinfo ti where ti.merchantId='" + tradeInfo.getMerchantId() + "' and ti.tradeChannel='" + merchanID + "' and substr(ti.tradeState,0,1)='" + 1 + "' and ti.tradeTime>to_date('" +
						todayTime + "','yyyy-MM-dd hh24:mi:ss')";

				Double todaymoney = (Double) this.commonService.uniqueResult(todaytotalmoney);	
				
				//Double todaymoney2 = 0d;
				if (todaymoney != null){
					todaymoney = todaymoney;
				}else{
					todaymoney = 0d;
				}
				
				logger.info("----------todaymoney---------："+todaymoney);
				logger.info("----------todaymoney+rmbmoney----------:"+(todaymoney+rmbmoney));
				//日交易金额限额
				if (todaymoney + rmbmoney > merchantmanager.getDayQuota()) {
					// //先把需要发送邮件的信息保存到数据库
					// saveMailInfo(merEmail,num1,"ecpss@ecpss.com");
					message = "Payment Declined";
					remark = "日交易金额超限/transaction volume overload";
					responseCode = 4;
					logger.info("返回状态码+++++++++" + responseCode);
					MD5info = tradeInfo.getMerchantOrderNo() + tradeInfo.getMoneyType()
							+ ordercountValue + responseCode + MD5key;
					md5Value = md5.getMD5ofStr(MD5info);
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
	
					// 超过限额给商户发送邮件提醒
	/*				shopManagerService.addSendMessages(merchant.getMeremail(),
							"ecpss@ecpss.cc",
							merchant.getMerno() + " " + EmailInfo.getMoreMoney(),
							"0");
					
					logger.info("*********************支付结果返回码***************************"+responseCode);*/
					return SUCCESS;
	
				}			
			}	
			if("MS-V".equals(chanelName)){
				Calendar today2 = Calendar.getInstance();
				today2.set(Calendar.HOUR_OF_DAY, 0);
				today2.set(Calendar.MINUTE, 0);
				today2.set(Calendar.SECOND,0);
				String todayTime2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
				.format(today2.getTime());	
	
/*					StringBuffer sb = new StringBuffer();
 					String todaytotalmoney = "select sum(ti.rmbAmount) ";
					sb.append("from InternationalTradeinfo ti where ti.merchantId='"+tradeInfo.getMerchantId()+"' and substr(ti.tradeState,0,1)='"+1+"' and ti.tradeTime>to_date('"
							+ todayTime + "','yyyy-MM-dd hh24:mi:ss')");
					if("4160".equals(merNo)){
						sb.append(" and ti.tradeChannel= '5504' ");
					}
	
				Double todaymoney = (Double) this.commonService.uniqueResult(todaytotalmoney+sb.toString());	*/
				String todaytotalmoney2 = "select sum(ti.rmbAmount) from InternationalTradeinfo ti where ti.merchantId='" + tradeInfo.getMerchantId() + "' and ti.tradeChannel='" + merchanID + "' and substr(ti.tradeState,0,1)='" + 1 + "' and ti.tradeTime>to_date('" +
						todayTime2 + "','yyyy-MM-dd hh24:mi:ss')";

				Double todaymoney2 = (Double) this.commonService.uniqueResult(todaytotalmoney2);	
				
				//Double todaymoney2 = 0d;
				if (todaymoney2 != null){
					todaymoney2 = todaymoney2;
				}else{
					todaymoney2 = 0d;
				}
				
				logger.info("----------todaymoney---------："+todaymoney2);
				logger.info("----------todaymoney+rmbmoney----------:"+(todaymoney2+rmbmoney));
				//日交易金额限额
				if (todaymoney2 + rmbmoney > merchantmanager.getDayQuota()) {
					// //先把需要发送邮件的信息保存到数据库
					// saveMailInfo(merEmail,num1,"ecpss@ecpss.com");
					message = "Payment Declined";
					remark = "日交易金额超限/transaction volume overload";
					responseCode = 4;
					logger.info("返回状态码+++++++++" + responseCode);
					MD5info = tradeInfo.getMerchantOrderNo() + tradeInfo.getMoneyType()
							+ ordercountValue + responseCode + MD5key;
					md5Value = md5.getMD5ofStr(MD5info);
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
	
					// 超过限额给商户发送邮件提醒
	/*				shopManagerService.addSendMessages(merchant.getMeremail(),
							"ecpss@ecpss.cc",
							merchant.getMerno() + " " + EmailInfo.getMoreMoney(),
							"0");
					
					logger.info("*********************支付结果返回码***************************"+responseCode);*/
					return SUCCESS;
	
				}			
			}
		Boolean noPending=valWhiteList(merchant.getMerno()+"",cardNo,ip,email,tradeInfo.getTradeUrl(),country+","+bankCountry,"6");

		if (chnals.equals("V5")) {
			TradUtil tu= new TradUtil();
			TradeMessage tm=new TradeMessage();
			//卡号
			tm.setCardNo(cardNo);
			//CVV
			tm.setCvv(cvv2);
			//卡号有效期年
			tm.setExpirationYear("20"+year);
			//卡号有效期月
			tm.setExpirationMonth(month);
			tm.setPayNumber("0");
			tm.setMerNo("10000");
			tm.setShopName("迁易通");
			tm.setOrderNo(tradeInfo.getOrderNo());
			tm.setAmount(tradeInfo.getRmbAmount()+"");
			tm.setCurrency("CNY");
			tm.setGoodsName("物品名称");
			tm.setGoodsPrice(tradeInfo.getRmbAmount()+"");
			tm.setGoodsNumber("1");
			tm.setBillFirstName(this.firstname);
			tm.setBillLastName(this.lastname);
			tm.setBillAddress(this.address);
			tm.setBillCity(this.city);
			tm.setBillState(this.state);
			tm.setBillCountry(this.country);
			tm.setBillZip(this.zipcode);
			tm.setEmail(this.email);
			tm.setPhone(this.phone);
			tm.setShipFirstName(this.shippingFirstName);
			tm.setShipLastName(this.shippingLastName);
			tm.setShipAddress(this.shippingAddress);
			tm.setShipCity(this.shippingCity);
			tm.setShipState(this.shippingSstate);
			tm.setShipAddress(this.shippingAddress);
			tm.setShipCountry(this.shippingCountry);
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
					tradeInfo.setRemark("10分钟之内请不要重复提交！"+tm.getErrorCode());
					remark=tradeInfo.getRemark();
				}
				else if("0927".equals(tm.getErrorCode())){
					tradeInfo.setRemark("卡信息验证失败，请联系发卡行！"+tm.getErrorCode());
					remark=tradeInfo.getRemark();
				}
				else if("0931".equals(tm.getErrorCode())){
					tradeInfo.setRemark("请联系发卡行！"+tm.getErrorCode());
					remark=tradeInfo.getRemark();
				}
				else if("1078".equals(tm.getErrorCode())){
					tradeInfo.setRemark("cvv错误！"+tm.getErrorCode());
					remark=tradeInfo.getRemark();
				}else if("03".equals(tm.getErrorCode())){
					remark=tm.getErrorCode();
					tradeInfo.setRemark(tm.getErrorCode()+tm.getErrorMsg()+tm.getBankInfo());
				}else if("sfe01".equals(tm.getErrorCode())){
					tradeInfo.setRemark(tm.getErrorCode());
					remark=tm.getErrorCode();
				}else{
					tradeInfo.setRemark(tm.getErrorCode()+tm.getErrorMsg()+tm.getBankInfo());
					remark=tradeInfo.getRemark();
				}
				this.commonService.update(tradeInfo);
				logger.info("*********************支付结果返回码***************************"+responseCode);
				return SUCCESS;
								
			}else if(tm.getSucceed().equals("1")){

				// 支付成功
				this.message = "Payment Success!";
				this.responseCode = 88;
				billaddress=it.get(0).getBillingAddress();
				MD5info = merchantOrderNo
						+ Currency + ordercountValue
						+ responseCode + MD5key;
				md5Value = md5.getMD5ofStr(MD5info);
				tradeInfo.setTradeState("1"
						+ tradeInfo.getTradeState().substring(1,
								tradeInfo.getTradeState().length()));
				tradeInfo.setRemark(message);
				logger.info("交易成功返回:"+merchantOrderNo+"**"+Currency+"**"+ordercount+"**"+responseCode+"**"+message+"**"+ReturnURL+"**"+md5Value);
				this.commonService.update(tradeInfo);
				logger.info("*********************支付结果返回码***************************"+responseCode);
				return SUCCESS;
								
				
				
			}else if(tm.getSucceed().equals("2")||tm.getSucceed().equals("3")){

				// 支付成功
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
				logger.info("*********************支付结果返回码***************************"+responseCode);
				return SUCCESS;
			
			}
			
			
			
		}else if (chnals.equals("CA")) {
			CaibaoUtil tu= new CaibaoUtil();
			CaibaoMessage ms=new CaibaoMessage();
			ms.setMerNo("20668");
			ms.setGatewayNo(posMerchantNo);
			ms.setOrderNo(tradeInfo.getOrderNo());
			ms.setOrderCurrency("CNY");
			ms.setOrderAmount(tradeInfo.getRmbAmount()+"");
			ms.setReturnURL("www.baidu.com");
			ms.setCardNo(cardNo);
			ms.setCardExpireYear("20"+year);
			ms.setCardExpireMonth(month);
			ms.setCardSecurityCode(cvv2);
			if(StringUtils.isBlank(cardBank)||cardBank.length()<2){
				cardBank="test";
			}
			ms.setIssuingBank(cardBank);
			String ipstr[]=ip.split(",");
			if(ipstr.length>1){
				ms.setIp(ipstr[0]);
			}else{
				ms.setIp(ip);
			}
			ms.setEmail(this.email);
			ms.setPaymentMethod("Credit Card");
			ms.setPhone(this.phone);
			ms.setFirstName(this.firstname.replace("'",""));
			ms.setLastName(this.lastname.replace("'",""));
			if(country.length()>=3){	
			ms.setCountry(this.country.substring(0, 3));
			}else{
				ms.setCountry(this.country);
			}
			ms.setState(this.state);
			ms.setCity(this.city);
			ms.setAddress(this.address);
			ms.setZip(this.zipcode);
			ms.setIsAuthor("");
			ms.setRemark("");
			String sign=ms.getMerNo().trim()+ms.getGatewayNo().trim()+ms.getOrderNo().trim()+ms.getOrderCurrency().trim()+ms.getOrderAmount().trim()+ms.getFirstName().trim()+ms.getLastName().trim() + ms.getCardNo().trim() + ms.getCardExpireYear().trim()+ms.getCardExpireMonth().trim()+ms.getCardSecurityCode().trim()+ ms.getEmail().trim() + posNumber.trim();
			String strDes = getSha256(sign); 
			ms.setSignInfo(strDes);
			ms.setTcsid(csid);
			tu.get(ms);
			if(ms.getRes_orderStatus().equals("0")){
				if("sfe01".equals(ms.getRes_orderInfo())&&noPending.equals(Boolean.FALSE)){
					this.responseCode = 19;

					MD5info = tradeInfo.getMerchantOrderNo()
							+ tradeInfo.getMoneyType() + ordercountValue
							+ responseCode + MD5key;
					md5Value = md5.getMD5ofStr(MD5info);
					tradeInfo.setTradeState("2"
							+ tradeInfo.getTradeState().substring(1,
									tradeInfo.getTradeState().length()));
					tradeInfo.setRemark("timeout!");
					this.commonService.update(tradeInfo);
					shopManagerService.addTemporaryTradInfo(orderno, year, month,cvv2,country,MD5key, ip,"MSIE 10.0","timeOut!");
					logger.info("*********************支付结果返回码***************************"+responseCode);
					return SUCCESS;
					
				}else if(bankBackRemark.toLowerCase().indexOf(ms.getRes_orderInfo().toLowerCase())>=0&&noPending.equals(Boolean.FALSE)){
					this.responseCode = 19;

					MD5info = tradeInfo.getMerchantOrderNo()
							+ tradeInfo.getMoneyType() + ordercountValue
							+ responseCode + MD5key;
					md5Value = md5.getMD5ofStr(MD5info);
					tradeInfo.setTradeState("2"
							+ tradeInfo.getTradeState().substring(1,
									tradeInfo.getTradeState().length()));
					tradeInfo.setRemark("Waiting processing!");
					remark=tradeInfo.getRemark();
					this.commonService.update(tradeInfo);
					shopManagerService.addTemporaryTradInfo(tradeInfo.getOrderNo(), year, month,cvv2,country,MD5key, ip,"MSIE 10.0","CA"+ms.getRes_orderInfo());
					logger.info("*********************支付结果返回码***************************"+responseCode);
					return SUCCESS;
				}else{

				this.message = "Payment Declined!" + ms.getRes_orderInfo();
				remark=message;
				this.responseCode = 0;
				MD5info = tradeInfo.getMerchantOrderNo()
						+ tradeInfo.getMoneyType() + ordercountValue
						+ responseCode + MD5key;
				md5Value = md5.getMD5ofStr(MD5info);
				tradeInfo.setTradeState("0"
						+ tradeInfo.getTradeState().substring(1,
								tradeInfo.getTradeState().length()));
				tradeInfo.setRemark(message);
				this.commonService.update(tradeInfo);
				logger.info("*********************支付结果返回码***************************"+responseCode);
				return SUCCESS;
				}
								
				
			}else if(ms.getRes_orderStatus().equals("1")){

				// 支付成功
				this.message = "Payment Success!";
				this.responseCode = 88;
				billaddress=ms.getRes_billAddress();
				MD5info = tradeInfo.getMerchantOrderNo()
						+ tradeInfo.getMoneyType() + ordercountValue
						+ responseCode + MD5key;
				md5Value = md5.getMD5ofStr(MD5info);
				tradeInfo.setTradeState("1"
						+ tradeInfo.getTradeState().substring(1,
								tradeInfo.getTradeState().length()));
				tradeInfo.setRemark(message);
				tradeInfo.setVIPBatchNo(ms.getRes_tradeNo());
				this.commonService.update(tradeInfo);

				return SUCCESS;
								
				
				
			}else if(ms.getRes_orderStatus().equals("-2")||ms.getRes_orderStatus().equals("-1")){

				// 支付成功
				this.responseCode = 19;

				MD5info = tradeInfo.getMerchantOrderNo()
						+ tradeInfo.getMoneyType() + ordercountValue
						+ responseCode + MD5key;
				md5Value = md5.getMD5ofStr(MD5info);
				tradeInfo.setTradeState("2"
						+ tradeInfo.getTradeState().substring(1,
								tradeInfo.getTradeState().length()));
				tradeInfo.setRemark(message);
				this.commonService.update(tradeInfo);
				logger.info("*********************支付结果返回码***************************"+responseCode);
				return SUCCESS;
			
			}
			
		} else if (chnals.equals("VPN")) {//非3DMOTO交易
            logger.info("进入到VPN通道");
			vpn.DCCMessage dcc = new vpn.DCCMessage();
			dcc.setTrans_Type("enqrate");// 查询此卡是否支持DCC交易
			dcc.setMerchant_Id(posMerchantNo);// 42 商户编号
			dcc.setAuthor_Str(it.get(0).getAuthcode());
			dcc.setTerminal_Id(posNumber);// 41 商户终端号
			dcc.setInvoice_No(tradeInfo.getOrderNo().substring(
					tradeInfo.getOrderNo().length() - 6,
					tradeInfo.getOrderNo().length()));

			// 有效期
			dcc.setOrder_No(tradeInfo.getOrderNo());// 62
			dcc.setCustom(tradeInfo.getOrderNo());
			dcc.setHashCode(it.get(0).getHashcode());
			dcc.setCurrency_Code_T("156");// 货币代码 CNY
			dcc.setBocs_ReturnURL("http://172.20.66.2/sfe");
			dcc.setAmount_Loc(this.buzero(tradeInfo.getRmbAmount() + ""));// 4
			// 本地交易金额
			dcc.setCard_No(cardNo);// 账号2
			dcc.setExp_Date(year + month);// 14
			VpnUtil vu = new VpnUtil();
			Long tim1 = System.currentTimeMillis();
			try {
				// type 1 汇率查询
				 dcc = vu.getDCCvalue(dcc, "1");
//				dcc.setResp_Code("99YY");
			} catch (Exception e) {
				this.responseCode = 19;
				message = "Your payment is being processed";
				MD5info = tradeInfo.getMerchantOrderNo() + tradeInfo.getMoneyType()
						+ ordercountValue + responseCode + MD5key;
				md5Value = md5.getMD5ofStr(MD5info);

				tradeInfo.setTradeState("2"
						+ tradeInfo.getTradeState().substring(1,
								tradeInfo.getTradeState().length()));
				tradeInfo.setRemark(message);
				tradeInfo.setVIPDisposePorson("System");
				tradeInfo.setVIPDisposeDate(new Date());
				this.commonService.update(tradeInfo);
				logger.info("*********************支付结果返回码***************************"+responseCode);
				return SUCCESS;
			}
			logger.info("*********************DCC查汇返回码***************************"+dcc.getResp_Code());
			logger.info("DCC查汇返回码"+dcc.getResp_Code());
			if (dcc.getResp_Code().equals("99YY")){//此卡支持DCC交易
//				logger.info("MOTO DCC交易开始");
//				// 交易
//				vpn.MotoDCCMessage dcc2 = new vpn.MotoDCCMessage();
//				dcc2.setTrans_Type("risk");// 类型
//				dcc2.setMerchant_Id(posMerchantNo);// 42 商户编号
//				dcc2.setAuthor_Str(it.get(0).getAuthcode());
//				dcc2.setTerminal_Id(posNumber);// 41 商户终端号
//				dcc2.setInvoice_No(tradeInfo.getOrderNo().substring(
//						tradeInfo.getOrderNo().length() - 6,
//						tradeInfo.getOrderNo().length()));
//
//				dcc2.setTrans_Model("M");//moto通道
//				dcc2.setCurrency_Code_T("156");// 货币代码
//				dcc2.setAmount_Loc(this.buzero(tradeInfo.getRmbAmount() + ""));// 4
//				// 本地交易金额
//				dcc2.setCard_No(cardNo);// 账号2
//				dcc2.setExp_Date(year + month);// 14 有效期
//				dcc2.setCSC(cvv2);
//				dcc2.setCurrency_Code(dcc.getCurrency_Code());
//				dcc2.setBocs_ReturnURL("http://172.20.66.2/sfe");
//				dcc2.setAmount_For(dcc.getAmount_For());
//				dcc2.setOrder_No(tradeInfo.getOrderNo());
//				dcc2.setCustom(tradeInfo.getOrderNo());
//				dcc2.setHashCode(it.get(0).getHashcode());
//				//新增风险交易参数
//				dcc2.setCUST_FNAME(firstname);
//				dcc2.setCUST_LNAME(lastname);
//				dcc2.setCUST_CITY(city);
//				dcc2.setCUST_ADDR1(address);
//				if(!"3918".equals(merchant.getMerno()+"")){	
//				dcc2.setCUST_CNTRY_CD(country.substring(0, 3));
//				}
//				dcc2.setCUST_EMAIL(email);
//				dcc2.setCUST_IP_ADDR(ip);
////				dcc2.setCUST_HOME_PHONE(phone);
//				if(zipcode.replaceAll("-", "").length()>9){
//				dcc2.setCUST_POSTAL_CD(zipcode.replaceAll("-", "").substring(0, 9));
//				}else{
//				dcc2.setCUST_POSTAL_CD(zipcode.replaceAll("-", ""));
//				}
////				dcc2.setCUST_STPR_CD(state);
//				dcc2.setSHIP_FNAME(shippingFirstName);
//				dcc2.setSHIP_LNAME(shippingLastName);
//				dcc2.setSHIP_CITY(shippingCity);
//				dcc2.setSHIP_ADDR1(shippingAddress);
//				if(!"3918".equals(merchant.getMerno()+"")){	
//				dcc2.setSHIP_CNTRY_CD(shippingCountry.substring(0, 3));
//				}
//				dcc2.setSHIP_EMAIL(shippingEmail);
//				dcc2.setSHIP_IP_ADDR(ip);
////				dcc2.setSHIP_HOME_PHONE(shippingPhone);
//				if(shippingZipcode.replaceAll("-", "").length()>9){
//				dcc2.setSHIP_POSTAL_CD(shippingZipcode.replaceAll("-", "").substring(0, 9));
//				}else{
//					dcc2.setSHIP_POSTAL_CD(shippingZipcode.replaceAll("-", ""));
//				}
////				dcc2.setSHIP_STPR_CD(shippingSstate);
//				VpnUtil_Moto vu2=new VpnUtil_Moto();
//				//VpnUtil vu2 = new VpnUtil();
//				Long tim2 = System.currentTimeMillis();
//				try {
//					// type 2 dcc交易
//					logger.info("开始moto风控");
//					 dcc2 = vu2.getDCCvalue(dcc2, "21");
//				} catch (Exception e) {
//					responseCode = 19;
//					message = "Your payment is being processed";
//					MD5info = tradeInfo.getMerchantOrderNo()
//							+ tradeInfo.getMoneyType() + ordercountValue
//							+ responseCode + MD5key;
//					md5Value = md5.getMD5ofStr(MD5info);
//
//					tradeInfo.setTradeState("2"
//							+ tradeInfo.getTradeState().substring(1,
//									tradeInfo.getTradeState().length()));
//					tradeInfo.setRemark(message);
//					tradeInfo.setVIPDisposePorson("System");
//					tradeInfo.setVIPDisposeDate(new Date());
//					this.commonService.update(tradeInfo);
//					logger.info("*********************支付结果返回码***************************"+responseCode);
//					return SUCCESS;
//				}
//				if (dcc2.getResp_Code().equals("5600")) {
					vpn.MotoDCCMessage moto = new vpn.MotoDCCMessage();
					moto.setTrans_Type("dccsales");// 类型
					moto.setMerchant_Id(posMerchantNo);// 42 商户编号
					moto.setAuthor_Str(it.get(0).getAuthcode());
					moto.setTerminal_Id(posNumber);// 41 商户终端号
					moto.setInvoice_No(tradeInfo.getOrderNo().substring(
							tradeInfo.getOrderNo().length() - 6,
							tradeInfo.getOrderNo().length()));

					moto.setTrans_Model("M");//moto通道
					moto.setCurrency_Code_T("156");// 货币代码
					moto.setAmount_Loc(this.buzero(tradeInfo.getRmbAmount() + ""));// 4
					// 本地交易金额
					moto.setCard_No(cardNo);// 账号2
					moto.setExp_Date(year + month);// 14 有效期
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
						// type 2 dcc交易
						logger.info("开始moto DCC交易");
						moto = vm.getDCCvalue(moto, "2");
					} catch (Exception e) {
						responseCode = 19;
						message = "Your payment is being processed";
						MD5info = tradeInfo.getMerchantOrderNo()
								+ tradeInfo.getMoneyType() + ordercountValue
								+ responseCode + MD5key;
						md5Value = md5.getMD5ofStr(MD5info);

						tradeInfo.setTradeState("2"
								+ tradeInfo.getTradeState().substring(1,
										tradeInfo.getTradeState().length()));
						tradeInfo.setRemark(message);
						tradeInfo.setVIPDisposePorson("System");
						tradeInfo.setVIPDisposeDate(new Date());
						this.commonService.update(tradeInfo);
						logger.info("*********************支付结果返回码***************************"+responseCode);
						return SUCCESS;
					}
				if (moto.getResp_Code().equals("0000")) {//交易成功
					this.message = "Payment Success!";
					this.responseCode = 88;
					billaddress=it.get(0).getBillingAddress();
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
					MD5info = tradeInfo.getMerchantOrderNo()
							+ tradeInfo.getMoneyType() + ordercountValue
							+ responseCode + MD5key;
					md5Value = md5.getMD5ofStr(MD5info);
					
					logger.info("*********************支付结果返回码***************************"+responseCode);
					return SUCCESS;
				} else {
					this.message = "Payment Declined!"+moto.getResp_Code();
					remark=message;
					this.responseCode = 0;
					tradeInfo.setTradeState("0"
							+ tradeInfo.getTradeState().substring(1,
									tradeInfo.getTradeState().length()));
					tradeInfo.setRemark(message);
					tradeInfo.setVIPDisposePorson("System");
					tradeInfo.setVIPDisposeDate(new Date());
					this.commonService.update(tradeInfo);
					MD5info = tradeInfo.getMerchantOrderNo()
							+ tradeInfo.getMoneyType() + ordercountValue
							+ responseCode + MD5key;
					md5Value = md5.getMD5ofStr(MD5info);
					logger.info("*********************支付结果返回码***************************"+responseCode);
					return SUCCESS;
				}
//				}else {
//					message = "Payment Declined";
//					remark = "高风险交易/Risk of trading";
//					this.responseCode = Integer.valueOf(dcc2.getResp_Code());
//					tradeInfo.setTradeState("0"
//							+ tradeInfo.getTradeState().substring(1,
//									tradeInfo.getTradeState().length()));
//					String re[]=remark.split("/");
//					tradeInfo.setRemark(re[0]+this.responseCode);
//					tradeInfo.setVIPDisposePorson("System");
//					tradeInfo.setVIPDisposeDate(new Date());
//					this.commonService.update(tradeInfo);
//					MD5info = tradeInfo.getMerchantOrderNo()
//							+ tradeInfo.getMoneyType() + ordercountValue
//							+ responseCode + MD5key;
//					md5Value = md5.getMD5ofStr(MD5info);
//					logger.info("*********************支付结果返回码***************************"+responseCode);
//					return SUCCESS;
//				}

			} else if (dcc.getResp_Code().equals("99YX")) {//不支持DCC交易
				vpn.DCCMessage moto2 = new vpn.DCCMessage();
				moto2.setTrans_Type("sales");// 类型
				// 商户编号
				moto2.setMerchant_Id(it.get(0).getBanktype());// 42
				moto2.setAuthor_Str(it.get(0).getAuthcode());
				// 商户终端号
				moto2.setTerminal_Id(it.get(0).getAndterminalNo());// 41
				moto2.setInvoice_No(tradeInfo.getOrderNo().substring(
						tradeInfo.getOrderNo().length() - 6,
						tradeInfo.getOrderNo().length()));

				moto2.setOrder_No(tradeInfo.getOrderNo());// 62
				moto2.setCustom(tradeInfo.getOrderNo());
				moto2.setHashCode(it.get(0).getHashcode());

				moto2.setTrans_Model("M");//moto
				moto2.setCurrency_Code_T(dcc.getCurrency_Code_T());// 货币代码
				moto2.setAmount_Loc(this.buzero(tradeInfo.getTradeAmount() + ""));// 4
				// 本地交易金额
				moto2.setCard_No(cardNo);// 账号2
				moto2.setExp_Date(year + month);// 14 有效期
				moto2.setCSC(cvv2);

				VpnUtil vu3 = new VpnUtil();
				Long tim3 = System.currentTimeMillis();
				try {
					// type 3 edc交易
					moto2 = vu3.getDCCvalue(moto2, "3");
				} catch (Exception e) {
					responseCode = Integer.valueOf(moto2.getResp_Code());
					message = "Your payment is being processed";
					MD5info = tradeInfo.getMerchantOrderNo()
							+ tradeInfo.getMoneyType() + ordercountValue
							+ responseCode + MD5key;
					md5Value = md5.getMD5ofStr(MD5info);
					// this.commonService
					// .deleteBySql("update  international_tradeinfo t  set t.tradestate='0'||substr(t.tradestate,2,(length(t.tradestate)-1)),t.remark='"
					// + message
					// + "' ,t.VIPDisposePorson='System' "
					// + " ,t.VIPDisposeDate=sysdate "
					// + "  where t.id='"
					// + trade.getId()
					// + "'");
					tradeInfo.setTradeState("0"
							+ tradeInfo.getTradeState().substring(1,
									tradeInfo.getTradeState().length()));
					tradeInfo.setRemark(message);
					tradeInfo.setVIPDisposePorson("System");
					tradeInfo.setVIPDisposeDate(new Date());
					this.commonService.update(tradeInfo);
					logger.info("*********************支付结果返回码***************************"+responseCode);
					return SUCCESS;
				}
				if (moto2.getResp_Code().equals("0000")) {//交易成功
					this.message = "Payment Success!";
					this.responseCode = 88;
					// 清除持卡人cvv,有效期
					// this.commonService
					// .deleteBySql("update  international_cardholdersinfo t  set t.cvv2='XXX',t.expiryDate='0000' where t.tradeId='"
					// + trade.getId() + "'");
					billaddress=it.get(0).getBillingAddress();
					tradeInfo.setTradeState("1"
							+ tradeInfo.getTradeState().substring(1,
									tradeInfo.getTradeState().length()));
					tradeInfo.setRemark(message);
					tradeInfo.setVIPDisposePorson("System");
					tradeInfo.setVIPDisposeDate(new Date());
					tradeInfo.setVIPBatchNo(moto2.getAuth_Code());
					tradeInfo.setVIPTerminalNo(posNumber);
					tradeInfo.setVIPAuthorizationNo(moto2.getInvoice_No());
					tradeInfo.setRef_No(moto2.getRef_No());
					this.commonService.update(tradeInfo);
					card.setExpiryDate("0000");
					card.setCvv2("XXX");
					this.commonService.update(card);
					MD5info = tradeInfo.getMerchantOrderNo()
							+ tradeInfo.getMoneyType() + ordercountValue
							+ responseCode + MD5key;
					md5Value = md5.getMD5ofStr(MD5info);
					
					this.message = "Payment Success!";
					this.responseCode = 88;

					MD5info = tradeInfo.getMerchantOrderNo()
							+ tradeInfo.getMoneyType() + ordercountValue
							+ responseCode + MD5key;
					md5Value = md5.getMD5ofStr(MD5info);
					logger.info("*********************支付结果返回码***************************"+responseCode);
					return SUCCESS;
				} else {
					this.message = "Payment Declined!!"+moto2.getResp_Code();
					remark=message;
					this.responseCode = 0;
					tradeInfo.setTradeState("0"
							+ tradeInfo.getTradeState().substring(1,
									tradeInfo.getTradeState().length()));
					tradeInfo.setRemark(message);
					tradeInfo.setVIPDisposePorson("System");
					tradeInfo.setVIPDisposeDate(new Date());
					this.commonService.update(tradeInfo);
					MD5info = tradeInfo.getMerchantOrderNo()
							+ tradeInfo.getMoneyType() + ordercountValue
							+ responseCode + MD5key;
					md5Value = md5.getMD5ofStr(MD5info);
					
					logger.info("*********************支付结果返回码***************************"+responseCode);
					return SUCCESS;
				}
//				}else {
//					message = "Payment Declined";
//					remark = "高风险交易/Risk of trading";
//					this.responseCode = Integer.valueOf(dcc2.getResp_Code());
//					tradeInfo.setTradeState("0"
//							+ tradeInfo.getTradeState().substring(1,
//									tradeInfo.getTradeState().length()));
//					String re[]=remark.split("/");
//					tradeInfo.setRemark(re[0]+this.responseCode);
//					tradeInfo.setVIPDisposePorson("System");
//					tradeInfo.setVIPDisposeDate(new Date());
//					this.commonService.update(tradeInfo);
//					MD5info = tradeInfo.getMerchantOrderNo()
//							+ tradeInfo.getMoneyType() + ordercountValue
//							+ responseCode + MD5key;
//					md5Value = md5.getMD5ofStr(MD5info);
//					logger.info("*********************支付结果返回码***************************"+responseCode);
//					return SUCCESS;
//				}

			}
		}else if(chnals.equals("YL")){
			logger.info("进入银联通道");
			YinlianMessage msg=new YinlianMessage();
			YinlianUtil yu=new YinlianUtil();
			SimpleDateFormat sdf =new SimpleDateFormat("yyyyMMddHHmmss");
			msg.setTrnxDatetime(sdf.format(new Date()));
			msg.setCardNo(cardNo);
			msg.setAmt(this.buzero(tradeInfo.getRmbAmount() + ""));
			int posNo=this.tradeManager.intBySql("SELECT POS_SEQUENCE.NEXTVAL FROM DUAL");
			logger.info("pos流水号："+posNo);
			msg.setPosFlwNo(posNo+"");
			msg.setTermId(posNumber);
			msg.setMerchId(posMerchantNo);
			msg.setCvv2(cvv2);
			msg.setExpiredDate(year + month);
			yu.getYLPayMessage(msg);
			if (msg.getRes_resCode().equals("00")) {//交易成功
				this.message = "Payment Success!";
				this.responseCode = 88;
				billaddress=it.get(0).getBillingAddress();
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
				
				logger.info("交易成功返回:"+merchantOrderNo+"**"+Currency+"**"+ordercount+"**"+responseCode+"**"+message+"**"+ReturnURL+"**"+md5Value);
				logger.info("*********************支付结果返回码***************************"+responseCode);
				return SUCCESS;
			} else {
				this.message = "Payment Declined!Y"+msg.getRes_resCode();
				this.responseCode = 0;
				remark=message;
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
				logger.info("*********************支付结果返回码***************************"+responseCode);
				return SUCCESS;
			}
		}else if(chnals.equals("GP")){
				logger.info("进入GooPay通道");
				GooPayMessage msg=new GooPayMessage();
				GooPayUtil yu=new GooPayUtil();
				msg.setMerchantMID(posMerchantNo);
				msg.setNewcardtype(cartype);
				BASE64Encoder baseE=new BASE64Encoder(); 
				msg.setCardnum(baseE.encode(cardNo.getBytes()));
				msg.setCvv2(baseE.encode(cvv2.getBytes()));
				msg.setYear(baseE.encode(("20"+year).getBytes()));
				msg.setMonth(baseE.encode(month.getBytes()));
				msg.setCardbank(cardBank);
				msg.setBillNo(tradeInfo.getOrderNo());
				//msg.setAmount(tradeInfo.getRmbAmount() + "");
				Double amountAndFee=tradeInfo.getRmbAmount();
				if(tradeInfo.getChannelFee()!=null){
					amountAndFee=amountAndFee*(tradeInfo.getChannelFee()+1.0);
					amountAndFee = (double) (Math.round((double) amountAndFee * 100) / 100.00);
				}
				msg.setAmount(amountAndFee + "");
				
				msg.setCurrency("3");
				msg.setLanguage("en");
				msg.setWebsite(tradeInfo.getTradeUrl());
				msg.setReturnURL("www.sfepay.com");
				String md5Hash=msg.getMerchantMID()+msg.getBillNo() +msg.getCurrency() +msg.getAmount() +msg.getLanguage()+msg.getReturnURL()+it.get(0).getHashcode();
				msg.setHASH(md5.getMD5ofStr(md5Hash));
				msg.setShippingFirstName(shippingFirstName);
				msg.setShippingLastName(shippingLastName);
				msg.setShippingEmail(shippingEmail);
				msg.setShippingPhone(shippingPhone);
				msg.setShippingZipcode(shippingZipcode);
				msg.setShippingAddress(shippingAddress);
				msg.setShippingCity(shippingCity);
				msg.setShippingSstate(shippingSstate);
				msg.setShippingCountry(shippingCountry.substring(3, 5));
				msg.setProducts(products);
				msg.setFirstname(firstname);
				msg.setLastname(lastname);
				msg.setEmail(email);
				msg.setPhone(phone);
				msg.setZipcode(zipcode);
				msg.setAddress(address);
				msg.setCity(city);
				msg.setState(state);
				msg.setCountry(country.substring(3, 5));
				msg.setIpAddr(ip);
				yu.get(msg);
				if (msg.getSucceed().equals("88")) {//交易成功
					this.message = "Payment Success!";
					this.responseCode = 88;
					billaddress=msg.getBillingAddress();
					tradeInfo.setTradeState("1"
							+ tradeInfo.getTradeState().substring(1,
									tradeInfo.getTradeState().length()));
					tradeInfo.setRemark(message);
					tradeInfo.setVIPDisposePorson("System");
					tradeInfo.setVIPDisposeDate(new Date());
					tradeInfo.setVIPBatchNo(msg.getGrn());
					this.commonService.update(tradeInfo);
					card.setExpiryDate("0000");
					card.setCvv2("XXX");
					this.commonService.update(card);
					MD5info = merchantOrderNo
							+ Currency + ordercountValue
							+ responseCode + MD5key;
					md5Value = md5.getMD5ofStr(MD5info);
					
					logger.info("交易成功返回:"+merchantOrderNo+"**"+Currency+"**"+ordercount+"**"+responseCode+"**"+message+"**"+ReturnURL+"**"+md5Value);
					logger.info("*********************支付结果返回码***************************"+responseCode);
					return SUCCESS;
				}else if(msg.getSucceed().equals("90")&&noPending.equals(Boolean.FALSE)){
					// 支付成功
					this.responseCode = 19;

					MD5info = tradeInfo.getMerchantOrderNo()
							+ tradeInfo.getMoneyType() + ordercountValue
							+ responseCode + MD5key;
					md5Value = md5.getMD5ofStr(MD5info);
					tradeInfo.setTradeState("2"
							+ tradeInfo.getTradeState().substring(1,
									tradeInfo.getTradeState().length()));
					if(StringUtils.isNotBlank(msg.getResult())){
						tradeInfo.setRemark("GP*timeOut!");
					}else{					
						tradeInfo.setRemark("timeOut!");
					}
					this.commonService.update(tradeInfo);
					shopManagerService.addTemporaryTradInfo(orderno, year, month,cvv2,country,MD5key, ip,"MSIE 10.0",tradeInfo.getRemark());
					logger.info("*********************支付结果返回码***************************"+responseCode);
					return SUCCESS;
				}else if(bankBackRemark.toLowerCase().indexOf(msg.getRemark().toLowerCase())>=0&&noPending.equals(Boolean.FALSE)){
					this.responseCode = 19;
					
					MD5info = tradeInfo.getMerchantOrderNo()
							+ tradeInfo.getMoneyType() + ordercountValue
							+ responseCode + MD5key;
					md5Value = md5.getMD5ofStr(MD5info);
					tradeInfo.setTradeState("2"
							+ tradeInfo.getTradeState().substring(1,
									tradeInfo.getTradeState().length()));
					tradeInfo.setRemark("Waiting processing!");
					remark=tradeInfo.getRemark();
					this.commonService.update(tradeInfo);
					shopManagerService.addTemporaryTradInfo(tradeInfo.getOrderNo(), year, month,cvv2,country,MD5key, ip,"MSIE 10.0","GP"+msg.getRemark());
					logger.info("*********************支付结果返回码***************************"+responseCode);
					return SUCCESS;					
				} else {
						this.message = "Payment Declined!"+msg.getRemark();
						this.responseCode = 0;
						remark=message;
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
						logger.info("*********************支付结果返回码***************************"+responseCode);
						return SUCCESS;
				}
		}else if(chnals.equals("GD")){
			logger.info("进入GooPay3D通道");
			GooPayMessage msg=new GooPayMessage();
			GooPayUtil yu=new GooPayUtil();
			msg.setMerchantMID(posMerchantNo);
			msg.setNewcardtype(cartype);
			BASE64Encoder baseE=new BASE64Encoder(); 
			msg.setCardnum(baseE.encode(cardNo.getBytes()));
			msg.setCvv2(baseE.encode(cvv2.getBytes()));
			msg.setYear(baseE.encode(("20"+year).getBytes()));
			msg.setMonth(baseE.encode(month.getBytes()));
			msg.setCardbank(cardBank);
			msg.setBillNo(tradeInfo.getOrderNo());
			//msg.setAmount(tradeInfo.getRmbAmount() + "");
			
			//msg.setAmount(tradeInfo.getRmbAmount() + "");
			Double amountAndFee=tradeInfo.getRmbAmount();
			if(tradeInfo.getChannelFee()!=null){
				amountAndFee=amountAndFee*(tradeInfo.getChannelFee()+1.0);
				amountAndFee = (double) (Math.round((double) amountAndFee * 100) / 100.00);
			}
			msg.setAmount(amountAndFee + "");
			
			msg.setCurrency("3");
			msg.setLanguage("en");
			msg.setWebsite(tradeInfo.getTradeUrl());
			msg.setReturnURL("www.sfepay.com");
			String md5Hash=msg.getMerchantMID()+msg.getBillNo() +msg.getCurrency() +msg.getAmount() +msg.getLanguage()+msg.getReturnURL()+it.get(0).getHashcode();
			msg.setHASH(md5.getMD5ofStr(md5Hash));
			msg.setShippingFirstName(shippingFirstName);
			msg.setShippingLastName(shippingLastName);
			msg.setShippingEmail(shippingEmail);
			msg.setShippingPhone(shippingPhone);
			msg.setShippingZipcode(shippingZipcode);
			msg.setShippingAddress(shippingAddress);
			msg.setShippingCity(shippingCity);
			msg.setShippingSstate(shippingSstate);
			msg.setShippingCountry(shippingCountry.substring(3, 5));
			msg.setProducts(products);
			msg.setFirstname(firstname);
			msg.setLastname(lastname);
			msg.setEmail(email);
			msg.setPhone(phone);
			msg.setZipcode(zipcode);
			msg.setAddress(address);
			msg.setCity(city);
			msg.setState(state);
			msg.setCountry(country.substring(3, 5));
			msg.setIpAddr(ip);
			yu.get(msg);
			if (msg.getSucceed().equals("88")) {//交易成功
				this.message = "Payment Success!";
				this.responseCode = 88;
				billaddress=msg.getBillingAddress();
				tradeInfo.setTradeState("1"
						+ tradeInfo.getTradeState().substring(1,
								tradeInfo.getTradeState().length()));
				tradeInfo.setRemark(message);
				tradeInfo.setVIPDisposePorson("System");
				tradeInfo.setVIPDisposeDate(new Date());
				tradeInfo.setVIPBatchNo(msg.getGrn());
				this.commonService.update(tradeInfo);
				card.setExpiryDate("0000");
				card.setCvv2("XXX");
				this.commonService.update(card);
				MD5info = merchantOrderNo
						+ Currency + ordercountValue
						+ responseCode + MD5key;
				md5Value = md5.getMD5ofStr(MD5info);
				
				logger.info("交易成功返回:"+merchantOrderNo+"**"+Currency+"**"+ordercount+"**"+responseCode+"**"+message+"**"+ReturnURL+"**"+md5Value);
				logger.info("*********************支付结果返回码***************************"+responseCode);
				return SUCCESS;
			}else if(msg.getSucceed().equals("90")&&noPending.equals(Boolean.FALSE)){
				// 支付成功
				this.responseCode = 19;

				MD5info = tradeInfo.getMerchantOrderNo()
						+ tradeInfo.getMoneyType() + ordercountValue
						+ responseCode + MD5key;
				md5Value = md5.getMD5ofStr(MD5info);
				tradeInfo.setTradeState("2"
						+ tradeInfo.getTradeState().substring(1,
								tradeInfo.getTradeState().length()));
				if(StringUtils.isNotBlank(msg.getResult())){
					tradeInfo.setRemark("GD*timeOut!");
				}else{					
					tradeInfo.setRemark("timeOut!");
				}
				this.commonService.update(tradeInfo);
				shopManagerService.addTemporaryTradInfo(orderno, year, month,cvv2,country,MD5key, ip,"MSIE 10.0",tradeInfo.getRemark());
				logger.info("*********************支付结果返回码***************************"+responseCode);
				return SUCCESS;
			}else if(bankBackRemark.toLowerCase().indexOf(msg.getRemark().toLowerCase())>=0&&noPending.equals(Boolean.FALSE)){
				this.responseCode = 19;
				
				MD5info = tradeInfo.getMerchantOrderNo()
						+ tradeInfo.getMoneyType() + ordercountValue
						+ responseCode + MD5key;
				md5Value = md5.getMD5ofStr(MD5info);
				tradeInfo.setTradeState("2"
						+ tradeInfo.getTradeState().substring(1,
								tradeInfo.getTradeState().length()));
				tradeInfo.setRemark("Waiting processing!");
				remark=tradeInfo.getRemark();
				this.commonService.update(tradeInfo);
				shopManagerService.addTemporaryTradInfo(tradeInfo.getOrderNo(), year, month,cvv2,country,MD5key, ip,"MSIE 10.0","GD"+msg.getRemark());
				logger.info("*********************支付结果返回码***************************"+responseCode);
				return SUCCESS;					
			} else {
					this.message = "Payment Declined!"+msg.getRemark();
					this.responseCode = 0;
					remark=message;
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
					logger.info("*********************支付结果返回码***************************"+responseCode);
					return SUCCESS;
			}
		}else if(chnals.equals("GQ")){
			logger.info("进入GOfpay通道");
			GQPayMessage msg=new GQPayMessage();
			GQPayUtil yu=new GQPayUtil();		
			msg.setMode("Api");
			
			msg.setVersion("20180208");
			msg.setAppId("70227403");
			msg.setOrderId(tradeInfo.getOrderNo());
			msg.setSource(tradeInfo.getTradeUrl());
			msg.setEmail(email);
			msg.setIPAddress(ip);
			msg.setCurrency("CNY");
			Double amountAndFee=tradeInfo.getRmbAmount();
			if(tradeInfo.getChannelFee()!=null){
				amountAndFee=amountAndFee*(tradeInfo.getChannelFee()+1.0);
				amountAndFee = (double) (Math.round((double) amountAndFee * 100) / 100.00);
			}
			msg.setAmount(amountAndFee+ "");			
			String md5Hash = msg.getAppId() + msg.getOrderId() + msg.getEmail() + msg.getCurrency() + msg.getAmount() + "VKf0MK02O8iYewkb";
			msg.setSignature(md5.getMD5ofStr(md5Hash));
			
			msg.setProductSku1("ProductSku1");
			msg.setProductName1(products);	
			msg.setProductPrice1(amountAndFee+ "");
			msg.setProductQuantity1("1");
			msg.setShippingFirstName(shippingFirstName);
			msg.setShippingLastName(shippingLastName);
			msg.setShippingCountry(shippingCountry.substring(3, 5));
			msg.setShippingState(shippingSstate);//要改
			msg.setShippingCity(shippingCity);
			msg.setShippingAddress1(shippingAddress);
			msg.setShippingZipcode(shippingZipcode);
			msg.setShippingTelephone(shippingPhone);
			
			msg.setBillingFirstName(firstname);
			msg.setBillingLastName(lastname);
			msg.setBillingCountry(country.substring(3, 5));
			msg.setBillingState(state);//要改
			msg.setBillingCity(city);
			msg.setBillingAddress1(address);
			msg.setBillingZipcode(zipcode);
			msg.setBillingTelephone(phone);

			msg.setCreditCardName(firstname+lastname);
			msg.setCreditCardNumber(cardNo);
			msg.setCreditCardExpire("20"+year+month);
			msg.setCreditCardCsc2(cvv2);
			yu.get(msg);
				
			if (msg.getStatus().equals("Success")) {//交易成功
				this.message = "Payment Success!";
				this.responseCode = 88;
				//billaddress=msg.getBillingAddress();
				tradeInfo.setTradeState("1"
						+ tradeInfo.getTradeState().substring(1,
								tradeInfo.getTradeState().length()));
				tradeInfo.setRemark(message);
				tradeInfo.setVIPDisposePorson("System");
				tradeInfo.setVIPDisposeDate(new Date());
				tradeInfo.setVIPBatchNo(msg.getTransactionId());
				this.commonService.update(tradeInfo);
				card.setExpiryDate("0000");
				card.setCvv2("XXX");
				this.commonService.update(card);
				MD5info = merchantOrderNo
						+ Currency + ordercountValue
						+ responseCode + MD5key;
				md5Value = md5.getMD5ofStr(MD5info);
				
				logger.info("交易成功返回:"+merchantOrderNo+"**"+Currency+"**"+ordercount+"**"+responseCode+"**"+message+"**"+ReturnURL+"**"+md5Value);
				logger.info("*********************支付结果返回码***************************"+responseCode);
				return SUCCESS;
			}else if((msg.getStatus().equals("Processing")||msg.getStatus().equals("Pending"))&&noPending.equals(Boolean.FALSE)){
				// 支付成功
				this.responseCode = 19;

				MD5info = tradeInfo.getMerchantOrderNo()
						+ tradeInfo.getMoneyType() + ordercountValue
						+ responseCode + MD5key;
				md5Value = md5.getMD5ofStr(MD5info);
				tradeInfo.setTradeState("2"
						+ tradeInfo.getTradeState().substring(1,
								tradeInfo.getTradeState().length()));
				tradeInfo.setRemark("Waiting processing!");
				this.commonService.update(tradeInfo);
				
				logger.info("*********************支付结果返回码***************************"+responseCode);
				return SUCCESS;
			}else if(msg.getStatus().equals("sfe01")&&noPending.equals(Boolean.FALSE)){
				// 支付成功
				this.responseCode = 19;

				MD5info = tradeInfo.getMerchantOrderNo()
						+ tradeInfo.getMoneyType() + ordercountValue
						+ responseCode + MD5key;
				md5Value = md5.getMD5ofStr(MD5info);
				tradeInfo.setTradeState("2"
						+ tradeInfo.getTradeState().substring(1,
								tradeInfo.getTradeState().length()));
				tradeInfo.setRemark("timeOut!");
				this.commonService.update(tradeInfo);
				shopManagerService.addTemporaryTradInfo(tradeInfo.getOrderNo(), year, month,cvv2,country,MD5key, ip,"MSIE 10.0","timeOut!");
				logger.info("*********************支付结果返回码***************************"+responseCode);
				return SUCCESS;
			} else {
					this.message = "Payment Declined!"+msg.getReason();
					this.responseCode = 0;
					remark=message;
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
					logger.info("*********************支付结果返回码***************************"+responseCode);
					return SUCCESS;
			}
		}else if(chnals.equals("WR")){
			logger.info("进入WR通道");
			WRPayMessage wrp = new WRPayMessage();
		 	WRPayUtil wu = new WRPayUtil();
		 	wrp.setTransType("sales");
		 	wrp.setApiType("1");
		 	wrp.setTransModel("M");
		 	wrp.setEncryptionMode("SHA256");
		 	wrp.setCharacterSet("UTF8");
		 	wrp.setMerNo("1000041");
		 	wrp.setTerNo("88816");
		 	//wrp.setAmount(tradeInfo.getTradeAmount()+"");
		 	Double amountAndFee=tradeInfo.getRmbAmount();
			if(tradeInfo.getChannelFee()!=null){
				amountAndFee=amountAndFee*(tradeInfo.getChannelFee()+1.0);
				amountAndFee = (double) (Math.round((double) amountAndFee * 100) / 100.00);
			}
			wrp.setAmount(amountAndFee+"");
			
			/*if(tradeInfo.getMoneyType()==1){
				wrp.setCurrencyCode("USD");
			}else if (tradeInfo.getMoneyType()==2) {
				wrp.setCurrencyCode("EUR");
			}else if (tradeInfo.getMoneyType()==3) {
				wrp.setCurrencyCode("CNY");
			}else if (tradeInfo.getMoneyType()==4) {
				wrp.setCurrencyCode("GBP");
			}else if (tradeInfo.getMoneyType()==6) {
				wrp.setCurrencyCode("JPY");
			}else if (tradeInfo.getMoneyType()==7) {
				wrp.setCurrencyCode("AUD");
			}else if (tradeInfo.getMoneyType()==11) {
				wrp.setCurrencyCode("CAD");
			}*/
			wrp.setCurrencyCode("CNY");
					
			wrp.setOrderNo(tradeInfo.getOrderNo());
			wrp.setGoodsString(card.getProductInfo());
			wrp.setCardCountry(card.getCountry());
			wrp.setCardState(card.getState());
			wrp.setCardCity(card.getCity());
		 	wrp.setCardAddress(card.getAddress());
		 	wrp.setCardZipCode(card.getZipcode());
		 	wrp.setCardFullName(card.getFirstName()+"."+card.getLastName());
		 	wrp.setCardFullPhone(card.getPhone());
		 	wrp.setCardEmail(card.getEmail());
		 	wrp.setGrCountry(card.getShippingCountry());
			wrp.setGrState(card.getShippingState());
			wrp.setGrCity(card.getShippingCity());
			wrp.setGrAddress(card.getShippingAddress());
			wrp.setGrZipCode(card.getShippingZip());
			wrp.setGrphoneNumber(card.getShippingPhone());
			wrp.setGrPerName(card.getShippingFullName()+"."+card.getLastName());
		 	wrp.setGrEmail(card.getShippingEmail());
		 	wrp.setCardNO(cardNo);
		 	wrp.setCvv(cvv2);
		 	wrp.setExpYear("20"+year);
		 	wrp.setExpMonth(month);
		 	
		 	wrp.setPayIP(card.getIp().split(",")[0]);
			wrp.setMerMgrURL("www.sfepay.com");
			wrp.setReturnURL("www.sfepay.com");
			wrp.setNotifyURL("https://www.sfepay.com/onekpay");

			wrp.setWebInfo("MSIE 10.0");
			wrp.setLanguage("en");
		 	wrp.setMerremark("");
		 	wu.get(wrp);
			if (wrp.getRespCode().equals("00")) {//交易成功
				this.message = "Payment Success!";
				this.responseCode = 88;
				billaddress=wrp.getAcquirer();
				tradeInfo.setTradeState("1"
						+ tradeInfo.getTradeState().substring(1,
								tradeInfo.getTradeState().length()));
				tradeInfo.setRemark(message);
				tradeInfo.setVIPDisposePorson("System");
				tradeInfo.setVIPDisposeDate(new Date());
				tradeInfo.setVIPBatchNo(wrp.getTerNo());
				this.commonService.update(tradeInfo);
				card.setExpiryDate("0000");
				card.setCvv2("XXX");
				this.commonService.update(card);
				MD5info = merchantOrderNo
						+ Currency + ordercountValue
						+ responseCode + MD5key;
				md5Value = md5.getMD5ofStr(MD5info);
				
				logger.info("交易成功返回:"+merchantOrderNo+"**"+Currency+"**"+ordercount+"**"+responseCode+"**"+message+"**"+ReturnURL+"**"+md5Value);
				logger.info("*********************支付结果返回码***************************"+responseCode);
				return SUCCESS;
			}else if(wrp.getRespCode().equals("sfe01")&&noPending.equals(Boolean.FALSE)){
				// 支付成功
				this.responseCode = 19;

				MD5info = tradeInfo.getMerchantOrderNo()
						+ tradeInfo.getMoneyType() + ordercountValue
						+ responseCode + MD5key;
				md5Value = md5.getMD5ofStr(MD5info);
				tradeInfo.setTradeState("2"
						+ tradeInfo.getTradeState().substring(1,
								tradeInfo.getTradeState().length()));
				tradeInfo.setRemark("timeOut!");
				this.commonService.update(tradeInfo);
				shopManagerService.addTemporaryTradInfo(tradeInfo.getOrderNo(), year, month,cvv2,country,MD5key, ip,"MSIE 10.0","timeOut!");
				logger.info("*********************支付结果返回码***************************"+responseCode);
				return SUCCESS;
			}else if((wrp.getRespCode().equals("02")||wrp.getRespCode().equals("03"))&&noPending.equals(Boolean.FALSE)){
				// 支付成功
				this.responseCode = 19;

				MD5info = tradeInfo.getMerchantOrderNo()
						+ tradeInfo.getMoneyType() + ordercountValue
						+ responseCode + MD5key;
				md5Value = md5.getMD5ofStr(MD5info);
				tradeInfo.setTradeState("2"
						+ tradeInfo.getTradeState().substring(1,
								tradeInfo.getTradeState().length()));
				tradeInfo.setRemark("Waiting processing!");
				this.commonService.update(tradeInfo);
				logger.info("*********************支付结果返回码***************************"+responseCode);
				return SUCCESS;
			}else if(bankBackRemark.toLowerCase().indexOf(wrp.getRespMsg().toLowerCase())>=0&&noPending.equals(Boolean.FALSE)){
					//"Refused transaction".equals(hwm.getRes_addMsg())||"Unknown error".equals(hwm.getRes_addMsg())||"Daily, monthly or yearly limits have been reached".equals(hwm.getRes_addMsg())||"AMOUNT NOT ALLOWED".equals(hwm.getRes_addMsg())
					this.responseCode = 19;

					MD5info = tradeInfo.getMerchantOrderNo()
							+ tradeInfo.getMoneyType() + ordercountValue
							+ responseCode + MD5key;
					md5Value = md5.getMD5ofStr(MD5info);
					tradeInfo.setTradeState("2"
							+ tradeInfo.getTradeState().substring(1,
									tradeInfo.getTradeState().length()));
					tradeInfo.setRemark("Waiting processing!");
					this.commonService.update(tradeInfo);
					shopManagerService.addTemporaryTradInfo(tradeInfo.getOrderNo(), year, month,cvv2,country,MD5key, ip,"MSIE 10.0","WR"+wrp.getRespMsg());
					logger.info("*********************支付结果返回码***************************"+responseCode);
					return SUCCESS;
			} else {
					this.message = "Payment Declined!"+wrp.getRespMsg();
					this.responseCode = 0;
					remark=message;
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
					logger.info("*********************支付结果返回码***************************"+responseCode);
					return SUCCESS;
			}
		}else if(chnals.equals("HJ")){
			logger.info("进入HJ通道");
			HJPayUtil HJ=new HJPayUtil();
			HJPayMessage hm=new HJPayMessage();
			hm.setAcctNo(posMerchantNo);
			hm.setAgent_AcctNo(posNumber);
			hm.setOrderID(tradeInfo.getOrderNo());
			hm.setCurrCode("156");
			Double amountAndFee=tradeInfo.getRmbAmount();
			if(tradeInfo.getChannelFee()!=null){
				amountAndFee=amountAndFee*(tradeInfo.getChannelFee()+1.0);
				amountAndFee = (double) (Math.round((double) amountAndFee * 100) / 100.00);
			}
			hm.setAmount((int)(amountAndFee*100)+"");
			hm.setIpAddress(ip);
			hm.setCardType(chanelName.split("-")[1]);
			hm.setCardPAN(cardNo);
			hm.setPname(products);
			hm.setCname(firstname+lastname);
			hm.setExpDate(year+month);
			hm.setCvv2(cvv2);
			hm.setIssCountry(this.shippingCountry.substring(3, 5));
			hm.setBaddress(address);
			hm.setBcity(city);
			hm.setPostCode(zipcode);
			hm.setIversion("V5.0");
			hm.setTelephone(phone);
			hm.setRetURL(tradeInfo.getTradeUrl());
			hm.setEmail(email);
			String hash=it.get(0).getHashcode()+ hm.getAcctNo() + hm.getOrderID() + hm.getAmount() + hm.getCurrCode();
			logger.info("hash加密前："+hash);
			hm.setHashValue(getBase64E(hash));
			hm.setBrowserUserAgent(userbrowser);
			hm.setShipName(shippingFirstName+shippingLastName);
			hm.setShipAddress(shippingAddress);
			hm.setShipCity(shippingCity);
			hm.setShipstate(shippingSstate);
			hm.setShipCountry(this.shippingCountry.substring(3, 5));
			hm.setShipPostCode(shippingZipcode);
			hm.setShipphone(shippingPhone);
			hm.setTxnType("01");
			HJ.get(hm);
		
			if (hm.getRes_success().equals("00")) {//交易成功
				
				this.message = "Payment Success!";
				this.responseCode = 88;
				billaddress=hm.getRes_billaddress();
				tradeInfo.setTradeState("1"
						+ tradeInfo.getTradeState().substring(1,
								tradeInfo.getTradeState().length()));
				tradeInfo.setRemark(message);
				tradeInfo.setVIPDisposePorson("System");
				tradeInfo.setVIPDisposeDate(new Date());
				tradeInfo.setVIPAuthorizationNo(hm.getRes_queOrderNo());
				this.commonService.update(tradeInfo);
				card.setExpiryDate("0000");
				card.setCvv2("XXX");
				this.commonService.update(card);
				MD5info = tradeInfo.getMerchantOrderNo()
						+ tradeInfo.getMoneyType() + ordercountValue
						+ responseCode + MD5key;
				md5Value = md5.getMD5ofStr(MD5info);
				
				logger.info("交易成功返回:"+merchantOrderNo+"**"+Currency+"**"+ordercount+"**"+responseCode+"**"+message+"**"+ReturnURL+"**"+md5Value);
				logger.info("*********************支付结果返回码***************************"+responseCode);
				return SUCCESS;
			}if(hm.getRes_success().equals("sfe01")&&noPending.equals(Boolean.FALSE)){
				this.responseCode = 19;

				MD5info = tradeInfo.getMerchantOrderNo()
						+ tradeInfo.getMoneyType() + ordercountValue
						+ responseCode + MD5key;
				md5Value = md5.getMD5ofStr(MD5info);
				tradeInfo.setTradeState("2"
						+ tradeInfo.getTradeState().substring(1,
								tradeInfo.getTradeState().length()));
				tradeInfo.setRemark("timeOut!");
				this.commonService.update(tradeInfo);
				shopManagerService.addTemporaryTradInfo(tradeInfo.getOrderNo(), year, month,cvv2,country,MD5key, ip,"MSIE 10.0","timeOut!");
				logger.info("*********************支付结果返回码***************************"+responseCode);
				return SUCCESS;
				
			}else if(bankBackRemark.toLowerCase().indexOf(hm.getRes_message().toLowerCase())>=0&&noPending.equals(Boolean.FALSE)){
				//"拒绝交易".equals(hm.getRes_message())||"银行通讯故障".equals(hm.getRes_message())||"交易超时".equals(hm.getRes_message())
				this.responseCode = 19;
				
				MD5info = tradeInfo.getMerchantOrderNo()
						+ tradeInfo.getMoneyType() + ordercountValue
						+ responseCode + MD5key;
				md5Value = md5.getMD5ofStr(MD5info);
				tradeInfo.setTradeState("2"
						+ tradeInfo.getTradeState().substring(1,
								tradeInfo.getTradeState().length()));
				tradeInfo.setRemark("Waiting processing!");
				remark=tradeInfo.getRemark();
				this.commonService.update(tradeInfo);
				shopManagerService.addTemporaryTradInfo(tradeInfo.getOrderNo(), year, month,cvv2,country,MD5key, ip,"MSIE 10.0","HJ"+hm.getRes_message());
				logger.info("*********************支付结果返回码***************************"+responseCode);
				return SUCCESS;
			}else {
				this.message = "Payment Declined!"+hm.getRes_message();
				this.responseCode = 0;
				remark=message;
				tradeInfo.setTradeState("0"
						+ tradeInfo.getTradeState().substring(1,
								tradeInfo.getTradeState().length()));
				tradeInfo.setRemark(message);
				tradeInfo.setVIPDisposePorson("System");
				tradeInfo.setVIPDisposeDate(new Date());
				this.commonService.update(tradeInfo);
				MD5info = tradeInfo.getMerchantOrderNo()
						+ tradeInfo.getMoneyType() + ordercountValue
						+ responseCode + MD5key;
				md5Value = md5.getMD5ofStr(MD5info);
				logger.info("*********************支付结果返回码***************************"+responseCode);
				return SUCCESS;
				
			}
		}
			else if(chnals.equals("WP")){
				logger.info("进入WintoPay通道");
				WPPayMessage msg=new WPPayMessage();
				WPPayUtil yu=new WPPayUtil();
				msg.setMerchantMID(posMerchantNo);
				msg.setNewcardtype(cartype);
				BASE64Encoder baseE=new BASE64Encoder(); 
				msg.setCardnum(baseE.encode(cardNo.getBytes()));
				msg.setCvv2(baseE.encode(cvv2.getBytes()));
				msg.setYear(baseE.encode(("20"+year).getBytes()));
				msg.setMonth(baseE.encode(month.getBytes()));
				msg.setCardbank(cardBank);
				msg.setBillNo(tradeInfo.getOrderNo());
				Double amountAndFee=tradeInfo.getRmbAmount();
				if(tradeInfo.getChannelFee()!=null){
					amountAndFee=amountAndFee*(tradeInfo.getChannelFee()+1.0);
					amountAndFee = (double) (Math.round((double) amountAndFee * 100) / 100.00);
				}
				msg.setAmount(amountAndFee+ "");
				msg.setCurrency("3");
				msg.setLanguage("en");
				msg.setWebsite(tradeInfo.getTradeUrl());
				msg.setReturnURL("https://www.sfepay.com/Wintopay");
				String md5Hash=msg.getMerchantMID()+msg.getBillNo() +msg.getCurrency() +msg.getAmount() +msg.getLanguage()+msg.getReturnURL()+it.get(0).getHashcode();
				msg.setHASH(md5.getMD5ofStr(md5Hash));
				msg.setShippingFirstName(shippingFirstName);
				msg.setShippingLastName(shippingLastName);
				msg.setShippingEmail(shippingEmail);
				msg.setShippingPhone(shippingPhone);
				msg.setShippingZipcode(shippingZipcode);
				msg.setShippingAddress(shippingAddress);
				msg.setShippingCity(shippingCity);
				msg.setShippingSstate(shippingSstate);
				msg.setShippingCountry(this.shippingCountry.substring(3, 5));
				msg.setProducts(products);
				msg.setFirstname(firstname);
				msg.setLastname(lastname);
				msg.setEmail(email);
				msg.setPhone(phone);
				msg.setZipcode(zipcode);
				msg.setAddress(address);
				msg.setCity(city);
				msg.setState(state);
				msg.setCountry(this.country.substring(3, 5));
				msg.setIpAddr(ip.split(",")[0]);
				yu.get(msg);
				if (msg.getSucceed().equals("88")) {//交易成功
					this.message = "Payment Success!";
					this.responseCode = 88;
					tradeInfo.setTradeState("1"
							+ tradeInfo.getTradeState().substring(1,
									tradeInfo.getTradeState().length()));
					tradeInfo.setRemark(message);
					tradeInfo.setVIPDisposePorson("System");
					tradeInfo.setVIPDisposeDate(new Date());
					//tradeInfo.setVIPBatchNo(msg.getGrn());
					this.commonService.update(tradeInfo);
					card.setExpiryDate("0000");
					card.setCvv2("XXX");
					this.commonService.update(card);
					MD5info = tradeInfo.getMerchantOrderNo()
							+ tradeInfo.getMoneyType() + ordercountValue
							+ responseCode + MD5key;
					md5Value = md5.getMD5ofStr(MD5info);
					
					logger.info("交易成功返回:"+merchantOrderNo+"**"+Currency+"**"+ordercount+"**"+responseCode+"**"+message+"**"+ReturnURL+"**"+md5Value);
					// 发送邮件
//					List<InternationalTerminalManager> tmm = this.commonService
//							.list("select tm from InternationalTerminalManager tm where tm.terminalNo='"
//									+ posNumber.trim() + "' ");
//					String billaddressby = null;
//					if (tmm.size() > 0) {
//						InternationalTerminalManager tm = tmm.get(0);
//						billaddressby = tm.getBillingAddress();
//					}
					/*String mailinfo = null;
					String billingAddress = "FPT*clothinglive";
					try {
						EmailInfo emailinfo = new EmailInfo();
						mailinfo = emailinfo.getPaymentResultEmail(
								card.getEmail(),
								tradeInfo.getTradeAmount(),
								getStates().getCurrencyTypeByNo(
										tradeInfo.getMoneyType().intValue()),
										tradeInfo.getTradeUrl(), tradeInfo.getTradeTime(),
										billingAddress, tradeInfo.getMerchantOrderNo(),
								tradeInfo.getOrderNo(), merchant);
						// 发送邮件,如果发送失败插入数据库发送
						if (merchant.getStatutes().substring(4, 5)
								.equals("0")) {
							CCSendMail.setSendMail(card.getEmail(),
									mailinfo, "sfepay@sfepay.com");
							logger.info("邮件立马发出");
						}
					} catch (Exception e) {
						// 往数据库插入等待发送邮件
						shopManagerService.addSendMessages(card.getEmail(),
								"sfepay@sfepay.com", mailinfo, "0");
						logger.info("邮件等待稍后发出");
						logger.info("*********************支付结果返回码***************************"+responseCode);
						return SUCCESS;
					}*/
					logger.info("*********************支付结果返回码***************************"+responseCode);
					return SUCCESS;
				}else if(msg.getSucceed().equals("sfe01")){
					// 待处理
					this.responseCode = 19;

					MD5info = tradeInfo.getMerchantOrderNo()
							+ tradeInfo.getMoneyType() + ordercountValue
							+ responseCode + MD5key;
					md5Value = md5.getMD5ofStr(MD5info);
					tradeInfo.setTradeState("2"
							+ tradeInfo.getTradeState().substring(1,
									tradeInfo.getTradeState().length()));
					tradeInfo.setRemark("timeOut!");
					this.commonService.update(tradeInfo);
					shopManagerService.addTemporaryTradInfo(tradeInfo.getOrderNo(), year, month,cvv2,country,MD5key, ip,"MSIE 10.0","timeOut!");
					logger.info("*********************支付结果返回码***************************"+responseCode);
					return SUCCESS;
				}else if(msg.getSucceed().equals("19")){
					// 待处理
					this.responseCode = 19;

					MD5info = tradeInfo.getMerchantOrderNo()
							+ tradeInfo.getMoneyType() + ordercountValue
							+ responseCode + MD5key;
					md5Value = md5.getMD5ofStr(MD5info);
					tradeInfo.setTradeState("2"
							+ tradeInfo.getTradeState().substring(1,
									tradeInfo.getTradeState().length()));
					tradeInfo.setRemark("Waiting processing!");
					this.commonService.update(tradeInfo);
					//shopManagerService.addTemporaryTradInfo(tradeInfo.getOrderNo(), year, month,cvv2,country,MD5key, ip,"MSIE 10.0","timeOut!");
					logger.info("*********************支付结果返回码***************************"+responseCode);
					return SUCCESS;
				}else if(bankBackRemark.toLowerCase().indexOf(msg.getResult().toLowerCase())>=0&&noPending.equals(Boolean.FALSE)){
					//"Refused transaction".equals(hwm.getRes_addMsg())||"Unknown error".equals(hwm.getRes_addMsg())||"Daily, monthly or yearly limits have been reached".equals(hwm.getRes_addMsg())||"AMOUNT NOT ALLOWED".equals(hwm.getRes_addMsg())
					this.responseCode = 19;

					MD5info = tradeInfo.getMerchantOrderNo()
							+ tradeInfo.getMoneyType() + ordercountValue
							+ responseCode + MD5key;
					md5Value = md5.getMD5ofStr(MD5info);
					tradeInfo.setTradeState("2"
							+ tradeInfo.getTradeState().substring(1,
									tradeInfo.getTradeState().length()));
					tradeInfo.setRemark("Waiting processing!");
					this.commonService.update(tradeInfo);
					shopManagerService.addTemporaryTradInfo(tradeInfo.getOrderNo(), year, month,cvv2,country,MD5key, ip,"MSIE 10.0","WP"+msg.getResult());
					logger.info("*********************支付结果返回码***************************"+responseCode);
					return SUCCESS;
				} else {
					this.message = "Payment Declined!"+"WPhighrisk!";
					this.responseCode = 0;
					tradeInfo.setTradeState("0"
							+ tradeInfo.getTradeState().substring(1,
									tradeInfo.getTradeState().length()));
					tradeInfo.setRemark(message);
					tradeInfo.setVIPDisposePorson("System");
					tradeInfo.setVIPDisposeDate(new Date());
					this.commonService.update(tradeInfo);
					MD5info = tradeInfo.getMerchantOrderNo()
							+ tradeInfo.getMoneyType() + ordercountValue
							+ responseCode + MD5key;
					md5Value = md5.getMD5ofStr(MD5info);
					logger.info("*********************支付结果返回码***************************"+responseCode);
					return SUCCESS;
				}		
		}else if(chnals.equals("HW")){
			logger.info("进入HW通道");
			HJWPayUtil hjw=new HJWPayUtil();
			HJWPayMessage hwm=new HJWPayMessage();
			hwm.setMerchantId(posMerchantNo);
			hwm.setMd5key(it.get(0).getHashcode());
			Double amountAndFee=tradeInfo.getRmbAmount();
			if(tradeInfo.getChannelFee()!=null){
				amountAndFee=amountAndFee*(tradeInfo.getChannelFee()+1.0);
				amountAndFee = (double) (Math.round((double) amountAndFee * 100) / 100.00);
			}
			hwm.setAmount(amountAndFee+"");
			hwm.setCurrency("100");
			hwm.setBillNo(tradeInfo.getOrderNo());
			hwm.setCardAsn(cardNo);
			hwm.setValidity(year+month);
			hwm.setCvv(cvv2);
			if("V".equals(chanelName.split("-")[1].toString())){
				hwm.setCardType("1");
			}
			if("M".equals(chanelName.split("-")[1].toString())){
				hwm.setCardType("2");
			}
			hwm.setSrcUrl("www.sfepay.com");
			hwm.setFirstName(firstname);
			hwm.setLastName(lastname);
			hwm.setAddress(address);
			hwm.setRemark("shoesbag");
			hwm.setEmail(email);
			hwm.setTelephone(phone);
			hwm.setTradType("1");
			hjw.get(hwm);
		
			if (hwm.getRes_responseCode().equals("00")) {//交易成功
				this.message = "Payment Success!";
				this.responseCode = 88;
				billaddress=it.get(0).getBillingAddress();
				tradeInfo.setTradeState("1"
						+ tradeInfo.getTradeState().substring(1,
								tradeInfo.getTradeState().length()));
				tradeInfo.setRemark(message);
				tradeInfo.setVIPDisposePorson("System");
				tradeInfo.setVIPDisposeDate(new Date());
				tradeInfo.setVIPAuthorizationNo(hwm.getRes_jcTradeId());
				this.commonService.update(tradeInfo);
				card.setExpiryDate("0000");
				card.setCvv2("XXX");
				this.commonService.update(card);
				MD5info = tradeInfo.getMerchantOrderNo()
						+ tradeInfo.getMoneyType() + ordercountValue
						+ responseCode + MD5key;
				md5Value = md5.getMD5ofStr(MD5info);
				
				logger.info("*********************支付结果返回码***************************"+responseCode);
				return SUCCESS;
			}if(hwm.getRes_responseCode().equals("sfe01")&&noPending.equals(Boolean.FALSE)){
				this.responseCode = 19;

				MD5info = tradeInfo.getMerchantOrderNo()
						+ tradeInfo.getMoneyType() + ordercountValue
						+ responseCode + MD5key;
				md5Value = md5.getMD5ofStr(MD5info);
				tradeInfo.setTradeState("2"
						+ tradeInfo.getTradeState().substring(1,
								tradeInfo.getTradeState().length()));
				tradeInfo.setRemark("timeOut!");
				this.commonService.update(tradeInfo);
				shopManagerService.addTemporaryTradInfo(tradeInfo.getOrderNo(), year, month,cvv2,country,MD5key, ip,"MSIE 10.0","timeOut!");
				logger.info("*********************支付结果返回码***************************"+responseCode);
				return SUCCESS;
				
			}else if(bankBackRemark.toLowerCase().indexOf(hwm.getRes_addMsg().toLowerCase())>=0&&noPending.equals(Boolean.FALSE)){
				//"Refused transaction".equals(hwm.getRes_addMsg())||"Unknown error".equals(hwm.getRes_addMsg())||"Daily, monthly or yearly limits have been reached".equals(hwm.getRes_addMsg())||"AMOUNT NOT ALLOWED".equals(hwm.getRes_addMsg())
				this.responseCode = 19;

				MD5info = tradeInfo.getMerchantOrderNo()
						+ tradeInfo.getMoneyType() + ordercountValue
						+ responseCode + MD5key;
				md5Value = md5.getMD5ofStr(MD5info);
				tradeInfo.setTradeState("2"
						+ tradeInfo.getTradeState().substring(1,
								tradeInfo.getTradeState().length()));
				tradeInfo.setRemark("Waiting processing!");
				this.commonService.update(tradeInfo);
				shopManagerService.addTemporaryTradInfo(tradeInfo.getOrderNo(), year, month,cvv2,country,MD5key, ip,"MSIE 10.0","HW"+hwm.getRes_addMsg());
				logger.info("*********************支付结果返回码***************************"+responseCode);
				return SUCCESS;
			}else {
				this.message = "Payment Declined!"+hwm.getRes_addMsg();
				this.responseCode = 0;
				tradeInfo.setTradeState("0"
						+ tradeInfo.getTradeState().substring(1,
								tradeInfo.getTradeState().length()));
				tradeInfo.setRemark(message);
				tradeInfo.setVIPDisposePorson("System");
				tradeInfo.setVIPDisposeDate(new Date());
				this.commonService.update(tradeInfo);
				MD5info = tradeInfo.getMerchantOrderNo()
						+ tradeInfo.getMoneyType() + ordercountValue
						+ responseCode + MD5key;
				md5Value = md5.getMD5ofStr(MD5info);
				logger.info("*********************支付结果返回码***************************"+responseCode);
				return SUCCESS;
			}
		}else if(chnals.equals("HP")||chnals.equals("LP")){
			logger.info("进入"+chnals+"通道");
			YoungPayMessage yp=new YoungPayMessage();
			YoungPayUtil ypu=new YoungPayUtil();
			yp.setMerId(posMerchantNo);
			yp.setTransType("2P_SALES");
			yp.setB2mOrder(tradeInfo.getOrderNo());
			yp.setCardNo(cardNo);
			if("V".equals(chanelName.split("-")[1])){
				yp.setCardType("VISA");
			}else if("M".equals(chanelName.split("-")[1])){
				yp.setCardType("MASTER");
			}else if("J".equals(chanelName.split("-")[1])){
				yp.setCardType("JCB");
			}
			yp.setExpireDate(year+month);
			yp.setCvcCode(cvv2);
			yp.setB2mBank(cardBank);
			Double amountAndFee=tradeInfo.getRmbAmount();
			if(tradeInfo.getChannelFee()!=null){
				amountAndFee=amountAndFee*(tradeInfo.getChannelFee()+1.0);
				amountAndFee = (double) (Math.round((double) amountAndFee * 100) / 100.00);
			}
			yp.setB2mFee(amountAndFee+"");
			yp.setB2mCur("CNY");
			yp.setB2mWebsite("sfepay.com");
			yp.setB2mReturnUrl("https://www.sfepay.com");
			yp.setB2mNotifyUrl("https://www.sfepay.com");
			yp.setIp(ip);
			yp.setB2mCargoCountry(shippingCountry.substring(0,3));
			yp.setB2mHolderCountry(shippingCountry.substring(0,3));
			yp.setB2mCargoName(shippingFirstName+shippingLastName);
			yp.setB2mPhone(shippingPhone);
			yp.setB2mCargoEmail(shippingEmail);
			yp.setB2mCargoState(shippingSstate);
			yp.setB2mCargoCity(shippingCity);
			yp.setB2mCargoAddr(shippingAddress);
			yp.setB2mCargoZip(shippingZipcode);
			yp.setB2mHolderName(firstname+lastname);
			yp.setB2mHolderEmail(email);
			yp.setB2mHolderState(state);
			yp.setB2mHolderCity(city);
			yp.setB2mHolderAddr(address);
			yp.setB2mHolderZip(zipcode);
			yp.setSignKey(it.get(0).getHashcode());
			try {
				ypu.getHarMessage(yp, "0");
			} catch (HttpException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (yp.getRes_statusCode().equals("success")) {//交易成功
				this.message = "Payment Success!";
				this.responseCode = 88;
				billaddress=it.get(0).getBillingAddress();
				tradeInfo.setTradeState("1"
						+ tradeInfo.getTradeState().substring(1,
								tradeInfo.getTradeState().length()));
				tradeInfo.setRemark(message);
				tradeInfo.setVIPDisposePorson("System");
				tradeInfo.setVIPDisposeDate(new Date());
				this.commonService.update(tradeInfo);
				card.setExpiryDate("0000");
				card.setCvv2("XXX");
				this.commonService.update(card);
				MD5info = tradeInfo.getMerchantOrderNo()
						+ tradeInfo.getMoneyType() + ordercountValue
						+ responseCode + MD5key;
				md5Value = md5.getMD5ofStr(MD5info);
				logger.info("*********************支付结果返回码***************************"+responseCode);
				return SUCCESS;
			}if(yp.getRes_errorCode().equals("sfe01")&&noPending.equals(Boolean.FALSE)){
				this.responseCode = 19;

				MD5info = tradeInfo.getMerchantOrderNo()
						+ tradeInfo.getMoneyType() + ordercountValue
						+ responseCode + MD5key;
				md5Value = md5.getMD5ofStr(MD5info);
				tradeInfo.setTradeState("2"
						+ tradeInfo.getTradeState().substring(1,
								tradeInfo.getTradeState().length()));
				tradeInfo.setRemark("timeOut!");
				this.commonService.update(tradeInfo);
				shopManagerService.addTemporaryTradInfo(tradeInfo.getOrderNo(), year, month,cvv2,country,MD5key, ip,"MSIE 10.0","timeOut!");
				logger.info("*********************支付结果返回码***************************"+responseCode);
				return SUCCESS;
				
			}else if(bankBackRemark.toLowerCase().indexOf(yp.getRes_errorCode().toLowerCase())>=0&&noPending.equals(Boolean.FALSE)){
				//"拒绝交易".equals(hm.getRes_message())||"银行通讯故障".equals(hm.getRes_message())||"交易超时".equals(hm.getRes_message())
				this.responseCode = 19;

				MD5info = tradeInfo.getMerchantOrderNo()
						+ tradeInfo.getMoneyType() + ordercountValue
						+ responseCode + MD5key;
				md5Value = md5.getMD5ofStr(MD5info);
				tradeInfo.setTradeState("2"
						+ tradeInfo.getTradeState().substring(1,
								tradeInfo.getTradeState().length()));
				tradeInfo.setRemark("Waiting processing!");
				this.commonService.update(tradeInfo);
				shopManagerService.addTemporaryTradInfo(tradeInfo.getOrderNo(), year, month,cvv2,country,MD5key, ip,"MSIE 10.0","HP-"+yp.getRes_errorCode());
				logger.info("*********************支付结果返回码***************************"+responseCode);
				return SUCCESS;
			}else {
				this.message = "Payment Declined!"+yp.getRes_errorCode();
				remark=yp.getRes_errorCode();
				this.responseCode = 0;
				tradeInfo.setTradeState("0"
						+ tradeInfo.getTradeState().substring(1,
								tradeInfo.getTradeState().length()));
				tradeInfo.setRemark(message);
				tradeInfo.setVIPDisposePorson("System");
				tradeInfo.setVIPDisposeDate(new Date());
				this.commonService.update(tradeInfo);
				MD5info = tradeInfo.getMerchantOrderNo()
						+ tradeInfo.getMoneyType() + ordercountValue
						+ responseCode + MD5key;
				md5Value = md5.getMD5ofStr(MD5info);
				logger.info("*********************支付结果返回码***************************"+responseCode);
				return SUCCESS;
			}
		}else if(chnals.equals("HR")){
			logger.info("进入HR通道");
			HRPayUtil hr=new HRPayUtil();
			HRPayMessage hrm=new HRPayMessage();
			hrm.setMerNo(posMerchantNo);
			hrm.setTransType("sales");
			Double amountAndFee=tradeInfo.getRmbAmount();
			if(tradeInfo.getChannelFee()!=null){
				amountAndFee=amountAndFee*(tradeInfo.getChannelFee()+1.0);
				amountAndFee = (double) (Math.round((double) amountAndFee * 100) / 100.00);
			}
			hrm.setAmount(amountAndFee+"");
			hrm.setCurrencyCode("CNY");
			hrm.setOrderNo(tradeInfo.getOrderNo());
			hrm.setSiteUrl(tradeInfo.getTradeUrl());
			if(StringUtils.isNotBlank(userbrowser)){
			hrm.setWebInfo(userbrowser);
			}else {
				hrm.setWebInfo("MSIE 10.0");
			}
			hrm.setLanguage("En");
			hrm.setCardCountry(this.shippingCountry.substring(3, 5));
			hrm.setCardState(shippingSstate);
			hrm.setCardCity(shippingCity);
			hrm.setCardAddress(shippingAddress);
			hrm.setCardZipCode(zipcode);
			hrm.setPayIP(ip);
			hrm.setCardFirstName(firstname);
			hrm.setCardLastName(lastname);
			hrm.setCardFullPhone(phone);
			hrm.setGrCountry(this.shippingCountry.substring(3, 5));
			hrm.setGrState(shippingSstate);
			hrm.setGrCity(shippingCity);
			hrm.setGrAddress(shippingAddress);
			hrm.setGrZipCode(zipcode);
			hrm.setGrEmail(email);
			hrm.setGrphoneNumber(phone);
			hrm.setGrFirstName(firstname);
			hrm.setGrLastName(lastname);
			hrm.setpName(products);
			hrm.setMd5Key(it.get(0).getHashcode());
			hrm.setCardNO(cardNo);
			hrm.setExpYear("20"+year);
			hrm.setExpMonth(month);
			hrm.setCvv(cvv2);
			hr.get(hrm);
			billaddress=hrm.getRes_acquirer();
			if(StringUtils.isBlank(billaddress)||"null".equals(billaddress)){
				billaddress=it.get(0).getBillingAddress();
			}
			if (hrm.getRes_respCode().equals("00")) {//交易成功
				
				this.message = "Payment Success!";
				this.responseCode = 88;
//				billaddress=hrm.getRes_acquirer();
				tradeInfo.setTradeState("1"
						+ tradeInfo.getTradeState().substring(1,
								tradeInfo.getTradeState().length()));
				tradeInfo.setRemark(message);
				tradeInfo.setVIPDisposePorson("System");
				tradeInfo.setVIPDisposeDate(new Date());
				tradeInfo.setVIPAuthorizationNo(hrm.getRes_tradeNo());
				this.commonService.update(tradeInfo);
				card.setExpiryDate("0000");
				card.setCvv2("XXX");
				this.commonService.update(card);
				MD5info = tradeInfo.getMerchantOrderNo()
						+ tradeInfo.getMoneyType() + ordercountValue
						+ responseCode + MD5key;
				md5Value = md5.getMD5ofStr(MD5info);
				
				logger.info("交易成功返回:"+merchantOrderNo+"**"+Currency+"**"+ordercount+"**"+responseCode+"**"+message+"**"+ReturnURL+"**"+md5Value);
				logger.info("*********************支付结果返回码***************************"+responseCode);
				return SUCCESS;
			}else if(bankBackRemark.toLowerCase().indexOf(hrm.getRes_respMsg().toLowerCase())>=0&&noPending.equals(Boolean.FALSE)){
				this.responseCode = 19;
				
				MD5info = tradeInfo.getMerchantOrderNo()
						+ tradeInfo.getMoneyType() + ordercountValue
						+ responseCode + MD5key;
				md5Value = md5.getMD5ofStr(MD5info);
				tradeInfo.setTradeState("2"
						+ tradeInfo.getTradeState().substring(1,
								tradeInfo.getTradeState().length()));
				tradeInfo.setRemark("Waiting processing!");
				remark=tradeInfo.getRemark();
				this.commonService.update(tradeInfo);
				shopManagerService.addTemporaryTradInfo(tradeInfo.getOrderNo(), year, month,cvv2,country,MD5key, ip,"MSIE 10.0","HR"+hrm.getRes_respMsg());
				logger.info("*********************支付结果返回码***************************"+responseCode);
				return SUCCESS;
			}else {
				this.message = "Payment Declined!"+hrm.getRes_respMsg();
				this.responseCode = 0;
				remark=hrm.getRes_respMsg();
				tradeInfo.setTradeState("0"
						+ tradeInfo.getTradeState().substring(1,
								tradeInfo.getTradeState().length()));
				tradeInfo.setRemark(message);
				tradeInfo.setVIPDisposePorson("System");
				tradeInfo.setVIPDisposeDate(new Date());
				this.commonService.update(tradeInfo);
				MD5info = tradeInfo.getMerchantOrderNo()
						+ tradeInfo.getMoneyType() + ordercountValue
						+ responseCode + MD5key;
				md5Value = md5.getMD5ofStr(MD5info);
				logger.info("*********************支付结果返回码***************************"+responseCode);
				return SUCCESS;				
			}
			
		}else if(chnals.equals("MS")){
			//24小时之内卡号失败的订单不再推送第二次
			Calendar calendarUrl3 = Calendar.getInstance();// 此时打印它获取的是系统当前时间
			calendarUrl3.add(Calendar.DATE,-1); // 得到一天前
			String onedate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
						.format(calendarUrl3.getTime());
			List repUrllist3 = commonService.getByList
					("select t.orderNo from international_cardholdersinfo f, international_tradeinfo t where f.tradeid=t.id and (f.cardNo='"
							+ AES.setCarNo(cardNo)+"') and substr(t.tradestate,1,1)='0' and t.tradeTime>to_date('"
				+ onedate + "','yyyy-MM-dd hh24:mi:ss')");
			if(repUrllist3.size()>0){
				logger.info("24小时内第二次失败订单不进入MS通道");
				this.message = "Payment Declined!please try another card!";
				this.responseCode = 0;
				remark="please try another card!!!";
				tradeInfo.setTradeState("0"
						+ tradeInfo.getTradeState().substring(1,
								tradeInfo.getTradeState().length()));
				tradeInfo.setRemark(message);
				tradeInfo.setVIPDisposePorson("System");
				tradeInfo.setVIPDisposeDate(new Date());
				this.commonService.update(tradeInfo);
				MD5info = tradeInfo.getMerchantOrderNo()
						+ tradeInfo.getMoneyType() + ordercountValue
						+ responseCode + MD5key;
				md5Value = md5.getMD5ofStr(MD5info);
				logger.info("*********************支付结果返回码***************************"+responseCode);
				return SUCCESS;
			}else{
				 logger.info("进入MS通道");
				 MasaPayMessage masaM=new MasaPayMessage();
				 MasaPayUtil masaU=new MasaPayUtil();
				 masaM.setVersion("1.6");
				 masaM.setMerchantId(posMerchantNo);
				 masaM.setCharset("utf-8");
				 masaM.setLanguage("en");
				 masaM.setSignType("SHA256");
				 masaM.setMerchantOrderNo(tradeInfo.getOrderNo());
				 masaM.setGoodsName(card.getProductInfo());
				 masaM.setGoodsDesc(card.getProductInfo());
				 masaM.setOrderExchange("2");
				 masaM.setCurrencyCode("CNY");
				 Double amountAndFee=tradeInfo.getRmbAmount();
				 if(tradeInfo.getChannelFee()!=null){
					amountAndFee=amountAndFee*(tradeInfo.getChannelFee()+1.0);
					amountAndFee = (double) (Math.round((double) amountAndFee * 100) / 100.00);
				 }
				 masaM.setOrderAmount((int)(amountAndFee*100)+"");
				 SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmss");
				 Calendar msCalendar = Calendar.getInstance();
				 msCalendar.add(Calendar.MINUTE,2); 
				 masaM.setSubmitTime(sdf.format(new Date()));
				 masaM.setExpiryTime(sdf.format(msCalendar.getTime()));
				 masaM.setBgUrl("https://www.sfepay.com/masapay");
				 masaM.setPayMode("10");
				 if("V".equals(chanelName.split("-")[1])){
					 masaM.setOrgCode("VISA");
				  }else if("M".equals(chanelName.split("-")[1])){
						masaM.setOrgCode("MASTER");
				  }else if("J".equals(chanelName.split("-")[1])){
						masaM.setOrgCode("JCB");
				  }
				 masaM.setCardNumber(cardNo);
				 masaM.setCardHolderLastName(lastname);
				 masaM.setCardHolderFirstName(firstname);
				 masaM.setCardExpirationMonth(month);
				 masaM.setCardExpirationYear("20"+year);
				 masaM.setSecurityCode(cvv2);
				 masaM.setCardHolderEmail(email);
				 masaM.setCardHolderPhoneNumber(phone);
				 masaM.setBillName(firstname+" "+lastname);
				 masaM.setBillAddress(shippingAddress);
				 masaM.setBillPostalCode(zipcode);
				 masaM.setBillCountry(this.shippingCountry.substring(3, 5));
				 
				 if(StringUtils.isBlank(shippingSstate)){
					 shippingSstate = "state";
				 }
				 
				 masaM.setBillState(shippingSstate);
				 masaM.setBillCity(shippingCity);
				 masaM.setBillEmail(email);
				 masaM.setBillPhoneNumber(phone);
				 masaM.setShippingName(shippingFirstName+" "+shippingLastName);
				 masaM.setShippingAddress(shippingAddress);
				 masaM.setShippingPostalCode(shippingZipcode);
				 masaM.setShippingCountry(this.shippingCountry.substring(3, 5));
				 masaM.setShippingState(shippingSstate);
				 masaM.setShippingCity(shippingCity);
				 masaM.setShippingEmail(shippingEmail);
				 masaM.setShippingPhoneNumber(shippingPhone);
				 masaM.setDeviceFingerprintID("m"+masaM.getMerchantId()+"_"+UUID.randomUUID().toString());
				 masaM.setRegisterUserEmail(email);
				 masaM.setRegisterTime(sdf.format(new Date()));
				 masaM.setRegisterIp(ip.split(",")[0]);
				 masaM.setRegisterTerminal("00");
				 masaM.setOrderIp(ip.split(",")[0]);
				 masaM.setOrderTerminal("00");
				 masaM.setMd5key(it.get(0).getHashcode());
				 masaU.get(masaM);
				 billaddress=it.get(0).getBillingAddress();
				if (masaM.getRes_resultCode().equals("10")) {//交易成功
					
					this.message = "Payment Success!";
					this.responseCode = 88;
	//				billaddress=hrm.getRes_acquirer();
					tradeInfo.setTradeState("1"
							+ tradeInfo.getTradeState().substring(1,
									tradeInfo.getTradeState().length()));
					tradeInfo.setRemark(message);
					tradeInfo.setVIPDisposePorson("System");
					tradeInfo.setVIPDisposeDate(new Date());
					tradeInfo.setVIPAuthorizationNo(masaM.getRes_masapayOrderNo());
					this.commonService.update(tradeInfo);
					card.setExpiryDate("0000");
					card.setCvv2("XXX");
					this.commonService.update(card);
					MD5info = tradeInfo.getMerchantOrderNo()
							+ tradeInfo.getMoneyType() + ordercountValue
							+ responseCode + MD5key;
					md5Value = md5.getMD5ofStr(MD5info);
					
					logger.info("交易成功返回:"+merchantOrderNo+"**"+Currency+"**"+ordercount+"**"+responseCode+"**"+message+"**"+ReturnURL+"**"+md5Value);
					logger.info("*********************支付结果返回码***************************"+responseCode);
					return SUCCESS;
				}else if(masaM.getRes_resultCode().equals("00")){
					this.responseCode = 19;
					MD5info = tradeInfo.getMerchantOrderNo()
							+ tradeInfo.getMoneyType() + ordercountValue
							+ responseCode + MD5key;
					md5Value = md5.getMD5ofStr(MD5info);
					tradeInfo.setTradeState("2"
							+ tradeInfo.getTradeState().substring(1,
									tradeInfo.getTradeState().length()));
					tradeInfo.setRemark("Waiting processing!");
					if(StringUtils.isNotBlank(masaM.getRes_masapayOrderNo())){
						tradeInfo.setVIPAuthorizationNo(masaM.getRes_masapayOrderNo());
					}
					remark=tradeInfo.getRemark();
					this.commonService.update(tradeInfo);
					logger.info("*********************支付结果返回码***************************"+responseCode);
					return SUCCESS;
				}else if(bankBackRemark.toLowerCase().indexOf(masaM.getRes_errMsg().toLowerCase())>=0&&noPending.equals(Boolean.FALSE)){
					this.responseCode = 19;
					
					MD5info = tradeInfo.getMerchantOrderNo()
							+ tradeInfo.getMoneyType() + ordercountValue
							+ responseCode + MD5key;
					md5Value = md5.getMD5ofStr(MD5info);
					tradeInfo.setTradeState("2"
							+ tradeInfo.getTradeState().substring(1,
									tradeInfo.getTradeState().length()));
					tradeInfo.setRemark("Waiting processing!");
					remark=tradeInfo.getRemark();
					this.commonService.update(tradeInfo);
					shopManagerService.addTemporaryTradInfo(tradeInfo.getOrderNo(), year, month,cvv2,country,MD5key, ip,"MSIE 10.0","MS*"+masaM.getRes_errMsg());
					logger.info("*********************支付结果返回码***************************"+responseCode);
					return SUCCESS;
				}else {
					this.message = "Payment Declined!"+masaM.getRes_errMsg();
					this.responseCode = 0;
					remark=masaM.getRes_errMsg();
					tradeInfo.setTradeState("0"
							+ tradeInfo.getTradeState().substring(1,
									tradeInfo.getTradeState().length()));
					tradeInfo.setRemark(message);
					tradeInfo.setVIPDisposePorson("System");
					tradeInfo.setVIPDisposeDate(new Date());
					this.commonService.update(tradeInfo);
					MD5info = tradeInfo.getMerchantOrderNo()
							+ tradeInfo.getMoneyType() + ordercountValue
							+ responseCode + MD5key;
					md5Value = md5.getMD5ofStr(MD5info);
					logger.info("*********************支付结果返回码***************************"+responseCode);
					return SUCCESS;				
				}		
			}
		}else if(chnals.equals("IP")){
			logger.info("进入IPasspay通道");
				IPassPayMessage msg=new IPassPayMessage();
				IPassPayUtil yu=new IPassPayUtil();
				
				msg.setMid(posMerchantNo);
				msg.setOid(tradeInfo.getOrderNo());
				
				msg.setSite_id("371");
				Double amountAndFee=tradeInfo.getRmbAmount();
				if(tradeInfo.getChannelFee()!=null){
					amountAndFee=amountAndFee*(tradeInfo.getChannelFee()+1.0);
					amountAndFee = (double) (Math.round((double) amountAndFee * 100) / 100.00);
				}
				msg.setOrder_amount(amountAndFee+ "");
				
				msg.setOrder_currency("CNY");
				//'mid','site_id','order_id','order_amount','order_currency','api_key'.
				String sign=msg.getMid().trim()+msg.getSite_id().trim()+msg.getOid().trim()+msg.getOrder_amount().trim()+msg.getOrder_currency().trim()+it.get(0).getHashcode().trim();
				String strDes = getSha256(sign); 
				msg.setHash_info(strDes);
				
				msg.setCard_no(cardNo);
				msg.setCard_ex_month(month);
				msg.setCard_ex_year(year);
				msg.setCard_cvv(cvv2);
				
				msg.setBill_email(email);
				msg.setBill_phone(phone);
				msg.setBill_country(this.country.substring(3, 5));
				
				if(StringUtils.isBlank(state)){
					state="state";
				}
				
				msg.setBillingstate(state);
				msg.setBill_city(city);
				msg.setBill_street(address);
				msg.setBill_zip(zipcode);
				msg.setBill_firstname(shippingFirstName);
				msg.setBill_lastname(shippingLastName);
				msg.setSyn_url("www.sfepay.com");
				msg.setAsyn_url("https://www.sfepay.com/IPassPay");
				msg.setSource_ip(ip.split(",")[0]);
				msg.setSource_url(tradeInfo.getTradeUrl());//商户网站
				msg.setGateway_version("1.0");
				UUID uuid2 = UUID.randomUUID();
				msg.setUuid(uuid2.toString());
				
				//msg.setBillingstate(ic.getState());
							
				yu.get(msg);
				
				if (msg.getOrder_status().equals("2")) {//交易成功
					this.message = "Payment Success!";
					this.responseCode = 88;
	//				billaddress=hrm.getRes_acquirer();
					tradeInfo.setTradeState("1"
							+ tradeInfo.getTradeState().substring(1,
									tradeInfo.getTradeState().length()));
					tradeInfo.setRemark(message);
					tradeInfo.setVIPDisposePorson("System");
					tradeInfo.setVIPDisposeDate(new Date());
					tradeInfo.setVIPAuthorizationNo(msg.getPid());
					this.commonService.update(tradeInfo);
					card.setExpiryDate("0000");
					card.setCvv2("XXX");
					this.commonService.update(card);
					MD5info = tradeInfo.getMerchantOrderNo()
							+ tradeInfo.getMoneyType() + ordercountValue
							+ responseCode + MD5key;
					md5Value = md5.getMD5ofStr(MD5info);
					
					logger.info("交易成功返回:"+merchantOrderNo+"**"+Currency+"**"+ordercount+"**"+responseCode+"**"+message+"**"+ReturnURL+"**"+md5Value);
					logger.info("*********************支付结果返回码***************************"+responseCode);
					return SUCCESS;
				}else if(msg.getOrder_status().equals("sfe01")){
					this.responseCode = 19;

					MD5info = tradeInfo.getMerchantOrderNo()
							+ tradeInfo.getMoneyType() + ordercountValue
							+ responseCode + MD5key;
					md5Value = md5.getMD5ofStr(MD5info);
					tradeInfo.setTradeState("2"
							+ tradeInfo.getTradeState().substring(1,
									tradeInfo.getTradeState().length()));
					tradeInfo.setRemark("timeOut!");
					this.commonService.update(tradeInfo);
					shopManagerService.addTemporaryTradInfo(tradeInfo.getOrderNo(), year, month,cvv2,country,MD5key, ip,"MSIE 10.0","timeOut!");
					logger.info("*********************支付结果返回码***************************"+responseCode);
					return SUCCESS;
				}else if(msg.getOrder_status().equals("1")||msg.getOrder_status().equals("3")){
					this.responseCode = 19;

					MD5info = tradeInfo.getMerchantOrderNo()
							+ tradeInfo.getMoneyType() + ordercountValue
							+ responseCode + MD5key;
					md5Value = md5.getMD5ofStr(MD5info);
					tradeInfo.setTradeState("2"
							+ tradeInfo.getTradeState().substring(1,
									tradeInfo.getTradeState().length()));
					tradeInfo.setRemark("Waiting processing!");
					this.commonService.update(tradeInfo);
					shopManagerService.addTraderun(tradeInfo.getOrderNo(), year, month,cvv2,country,MD5key, ip,"MSIE 10.0","Waiting processing!");
					//shopManagerService.addTemporaryTradInfo(trade.getOrderNo(), year, month,cvv2,country,MD5key, ip,"MSIE 10.0","MS"+masaM.getRes_errMsg());
					logger.info("*********************支付结果返回码***************************"+responseCode);
					return SUCCESS;		
				}else if(bankBackRemark.toLowerCase().indexOf(msg.getInfo().toLowerCase())>=0){
					//"拒绝交易".equals(hm.getRes_message())||"银行通讯故障".equals(hm.getRes_message())||"交易超时".equals(hm.getRes_message())
					this.responseCode = 19;

					MD5info = tradeInfo.getMerchantOrderNo()
							+ tradeInfo.getMoneyType() + ordercountValue
							+ responseCode + MD5key;
					md5Value = md5.getMD5ofStr(MD5info);
					tradeInfo.setTradeState("2"
							+ tradeInfo.getTradeState().substring(1,
									tradeInfo.getTradeState().length()));
					tradeInfo.setRemark("Waiting processing!");
					this.commonService.update(tradeInfo);
					shopManagerService.addTemporaryTradInfo(tradeInfo.getOrderNo(), year, month,cvv2,country,MD5key, ip,"MSIE 10.0","IP"+msg.getInfo());
					logger.info("*********************支付结果返回码***************************"+responseCode);
					return SUCCESS;
				}else {
					this.message = "Payment Declined!"+msg.getInfo();
					this.responseCode = 0;
					tradeInfo.setTradeState("0"
							+ tradeInfo.getTradeState().substring(1,
									tradeInfo.getTradeState().length()));
					tradeInfo.setRemark(message);
					tradeInfo.setVIPDisposePorson("System");
					tradeInfo.setVIPDisposeDate(new Date());
					this.commonService.update(tradeInfo);
					MD5info = tradeInfo.getMerchantOrderNo()
							+ tradeInfo.getMoneyType() + ordercountValue
							+ responseCode + MD5key;
					md5Value = md5.getMD5ofStr(MD5info);
					logger.info("*********************支付结果返回码***************************"+responseCode);
					return SUCCESS;
				}	
		}else if(chnals.equals("QP")){
			logger.info("进入QuanQiuPay通道");
			QuanQiuPayMessage msg=new QuanQiuPayMessage();
			QuanQiuPayUtil yu=new QuanQiuPayUtil();		
			msg.setMode("Api");
			msg.setApplicationId(posMerchantNo);
			//msg.setAppId("70227403");
			msg.setOrderId(tradeInfo.getOrderNo());
			msg.setSource(tradeInfo.getTradeUrl());
			msg.setEmail(email);
			msg.setIPAddress(ip.split(",")[0]);
			msg.setCurrency("CNY");
			Double amountAndFee=tradeInfo.getRmbAmount();
			if(tradeInfo.getChannelFee()!=null){
				amountAndFee=amountAndFee*(tradeInfo.getChannelFee()+1.0);
				amountAndFee = (double) (Math.round((double) amountAndFee * 100) / 100.00);
			}
			msg.setAmount(amountAndFee+ "");			
			String md5Hash = msg.getApplicationId() + msg.getOrderId() + msg.getEmail() + msg.getCurrency() + msg.getAmount() + it.get(0).getHashcode();
			msg.setSignature(md5.getMD5ofStr(md5Hash));
			
			msg.setProductSku1("ProductSku1");
			msg.setProductName1(products);	
			msg.setProductPrice1(amountAndFee+ "");
			msg.setProductQuantity1("1");
			msg.setShippingFirstName(shippingFirstName);
			msg.setShippingLastName(shippingLastName);
			msg.setShippingCountry(shippingCountry.substring(3, 5));
			msg.setShippingState(shippingSstate);//要改
			msg.setShippingCity(shippingCity);
			msg.setShippingAddress1(shippingAddress);
			msg.setShippingZipcode(shippingZipcode);
			msg.setShippingTelephone(shippingPhone);
			
			msg.setBillingFirstName(firstname);
			msg.setBillingLastName(lastname);
			msg.setBillingCountry(country.substring(3, 5));
			msg.setBillingState(state);//要改
			msg.setBillingCity(city);
			msg.setBillingAddress1(address);
			msg.setBillingZipcode(zipcode);
			msg.setBillingTelephone(phone);

			msg.setCreditCardName(firstname+lastname);
			msg.setCreditCardNumber(cardNo);
			msg.setCreditCardExpire("20"+year+month);
			msg.setCreditCardCsc2(cvv2);
			yu.get(msg);
				
			if (msg.getStatus().equals("Success")) {//交易成功
				this.message = "Payment Success!";
				this.responseCode = 88;
				billaddress=it.get(0).getBillingAddress();
				tradeInfo.setTradeState("1"
						+ tradeInfo.getTradeState().substring(1,
								tradeInfo.getTradeState().length()));
				tradeInfo.setRemark(message);
				tradeInfo.setVIPDisposePorson("System");
				tradeInfo.setVIPDisposeDate(new Date());
				tradeInfo.setVIPAuthorizationNo(msg.getTransactionId());
				this.commonService.update(tradeInfo);
				card.setExpiryDate("0000");
				card.setCvv2("XXX");
				this.commonService.update(card);
				MD5info = tradeInfo.getMerchantOrderNo()
						+ tradeInfo.getMoneyType() + ordercountValue
						+ responseCode + MD5key;
				md5Value = md5.getMD5ofStr(MD5info);
				
				logger.info("交易成功返回:"+merchantOrderNo+"**"+Currency+"**"+ordercount+"**"+responseCode+"**"+message+"**"+ReturnURL+"**"+md5Value);
				logger.info("*********************支付结果返回码***************************"+responseCode);
				return SUCCESS;
			}else if((msg.getStatus().equals("Processing")||msg.getStatus().equals("Pending"))){
				// 支付成功
				this.responseCode = 19;

				MD5info = tradeInfo.getMerchantOrderNo()
						+ tradeInfo.getMoneyType() + ordercountValue
						+ responseCode + MD5key;
				md5Value = md5.getMD5ofStr(MD5info);
				tradeInfo.setTradeState("2"
						+ tradeInfo.getTradeState().substring(1,
								tradeInfo.getTradeState().length()));
				tradeInfo.setRemark("Waiting processing!");
				tradeInfo.setVIPAuthorizationNo(msg.getTransactionId());
				this.commonService.update(tradeInfo);
				
				logger.info("*********************支付结果返回码***************************"+responseCode);
				return SUCCESS;
			}else if(bankBackRemark.toLowerCase().indexOf(msg.getReason().toLowerCase())>=0){
				//"拒绝交易".equals(hm.getRes_message())||"银行通讯故障".equals(hm.getRes_message())||"交易超时".equals(hm.getRes_message())
				this.responseCode = 19;

				MD5info = tradeInfo.getMerchantOrderNo()
						+ tradeInfo.getMoneyType() + ordercountValue
						+ responseCode + MD5key;
				md5Value = md5.getMD5ofStr(MD5info);
				tradeInfo.setTradeState("2"
						+ tradeInfo.getTradeState().substring(1,
								tradeInfo.getTradeState().length()));
				tradeInfo.setRemark("Waiting processing!");
				this.commonService.update(tradeInfo);
				shopManagerService.addTemporaryTradInfo(tradeInfo.getOrderNo(), year, month,cvv2,country,MD5key, ip,"MSIE 10.0","QP"+msg.getReason());
				logger.info("*********************支付结果返回码***************************"+responseCode);
				return SUCCESS;
			}else if(msg.getStatus().equals("sfe01")){
				// 支付成功
				this.responseCode = 19;

				MD5info = tradeInfo.getMerchantOrderNo()
						+ tradeInfo.getMoneyType() + ordercountValue
						+ responseCode + MD5key;
				md5Value = md5.getMD5ofStr(MD5info);
				tradeInfo.setTradeState("2"
						+ tradeInfo.getTradeState().substring(1,
								tradeInfo.getTradeState().length()));
				tradeInfo.setRemark("timeOut!");
				this.commonService.update(tradeInfo);
				shopManagerService.addTemporaryTradInfo(tradeInfo.getOrderNo(), year, month,cvv2,country,MD5key, ip,"MSIE 10.0","timeOut!");
				logger.info("*********************支付结果返回码***************************"+responseCode);
				return SUCCESS;
			} else {
					this.message = "Payment Declined!"+msg.getReason();
					this.responseCode = 0;
					remark=message;
					tradeInfo.setTradeState("0"
							+ tradeInfo.getTradeState().substring(1,
									tradeInfo.getTradeState().length()));
					tradeInfo.setRemark(message);
					tradeInfo.setVIPDisposePorson("System");
					tradeInfo.setVIPDisposeDate(new Date());
					this.commonService.update(tradeInfo);
					MD5info = merchantOrderNo
							+ tradeInfo.getMoneyType() + ordercountValue
							+ responseCode + MD5key;
					md5Value = md5.getMD5ofStr(MD5info);
					logger.info("*********************支付结果返回码***************************"+responseCode);
					return SUCCESS;
			}		
		}else if(chnals.equals("GR")){
				logger.info("进入GR通道");
				 GrePayMessage masaM=new GrePayMessage();
				 GrePayUtil masaU=new GrePayUtil();
				 masaM.setVersion("1.6");
				 masaM.setMerchantId(posMerchantNo);
				 masaM.setCharset("utf-8");
				 masaM.setLanguage("en");
				 masaM.setSignType("SHA256");
				 masaM.setMerchantOrderNo(tradeInfo.getOrderNo());
				 masaM.setGoodsName(card.getProductInfo());
				 masaM.setGoodsDesc(card.getProductInfo());
				 masaM.setOrderExchange("2");
				 masaM.setCurrencyCode("CNY");
				 Double amountAndFee=tradeInfo.getRmbAmount();
				 if(tradeInfo.getChannelFee()!=null){
					amountAndFee=amountAndFee*(tradeInfo.getChannelFee()+1.0);
					amountAndFee = (double) (Math.round((double) amountAndFee * 100) / 100.00);
				 }
				 masaM.setOrderAmount((int)(amountAndFee*100)+"");
				 SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmss");
				 Calendar msCalendar = Calendar.getInstance();
				 msCalendar.add(Calendar.MINUTE,2); 
				 masaM.setSubmitTime(sdf.format(new Date()));
				 masaM.setExpiryTime(sdf.format(msCalendar.getTime()));
				 masaM.setBgUrl("https://www.sfepay.com/masapay");
				 masaM.setPayMode("10");
				 if("V".equals(chanelName.split("-")[1])){
					 masaM.setOrgCode("VISA");
				  }else if("M".equals(chanelName.split("-")[1])){
						masaM.setOrgCode("MASTER");
				  }else if("J".equals(chanelName.split("-")[1])){
						masaM.setOrgCode("JCB");
				  }
				 masaM.setCardNumber(cardNo);
				 masaM.setCardHolderLastName(lastname);
				 masaM.setCardHolderFirstName(firstname);
				 masaM.setCardExpirationMonth(month);
				 masaM.setCardExpirationYear("20"+year);
				 masaM.setSecurityCode(cvv2);
				 masaM.setCardHolderEmail(email);
				 masaM.setCardHolderPhoneNumber(phone);
				 masaM.setBillName(firstname+" "+lastname);
				 masaM.setBillAddress(shippingAddress);
				 masaM.setBillPostalCode(zipcode);
				 masaM.setBillCountry(this.shippingCountry.substring(3, 5));
				 masaM.setBillState(shippingSstate);
				 masaM.setBillCity(shippingCity);
				 masaM.setBillEmail(email);
				 masaM.setBillPhoneNumber(phone);
				 masaM.setShippingName(shippingFirstName+" "+shippingLastName);
				 masaM.setShippingAddress(shippingAddress);
				 masaM.setShippingPostalCode(shippingZipcode);
				 masaM.setShippingCountry(this.shippingCountry.substring(3, 5));
				 masaM.setShippingState(shippingSstate);
				 masaM.setShippingCity(shippingCity);
				 masaM.setShippingEmail(shippingEmail);
				 masaM.setShippingPhoneNumber(shippingPhone);
				 masaM.setDeviceFingerprintID("m"+masaM.getMerchantId()+"_"+UUID.randomUUID().toString());
				 masaM.setRegisterUserEmail(email);
				 masaM.setRegisterTime(sdf.format(new Date()));
				 masaM.setRegisterIp(ip.split(",")[0]);
				 masaM.setRegisterTerminal("00");
				 masaM.setOrderIp(ip.split(",")[0]);
				 masaM.setOrderTerminal("00");
				 masaM.setMd5key(it.get(0).getHashcode());
				 masaU.get(masaM);
				 billaddress=it.get(0).getBillingAddress();
				if (masaM.getRes_resultCode().equals("10")) {//交易成功
					
					this.message = "Payment Success!";
					this.responseCode = 88;
//					billaddress=hrm.getRes_acquirer();
					tradeInfo.setTradeState("1"
							+ tradeInfo.getTradeState().substring(1,
									tradeInfo.getTradeState().length()));
					tradeInfo.setRemark(message);
					tradeInfo.setVIPDisposePorson("System");
					tradeInfo.setVIPDisposeDate(new Date());
					tradeInfo.setVIPAuthorizationNo(masaM.getRes_masapayOrderNo());
					this.commonService.update(tradeInfo);
					card.setExpiryDate("0000");
					card.setCvv2("XXX");
					this.commonService.update(card);
					MD5info = tradeInfo.getMerchantOrderNo()
							+ tradeInfo.getMoneyType() + ordercountValue
							+ responseCode + MD5key;
					md5Value = md5.getMD5ofStr(MD5info);
					
					logger.info("交易成功返回:"+merchantOrderNo+"**"+Currency+"**"+ordercount+"**"+responseCode+"**"+message+"**"+ReturnURL+"**"+md5Value);
					logger.info("*********************支付结果返回码***************************"+responseCode);
					return SUCCESS;
				}else if(masaM.getRes_resultCode().equals("00")){
					this.responseCode = 19;
					MD5info = tradeInfo.getMerchantOrderNo()
							+ tradeInfo.getMoneyType() + ordercountValue
							+ responseCode + MD5key;
					md5Value = md5.getMD5ofStr(MD5info);
					tradeInfo.setTradeState("2"
							+ tradeInfo.getTradeState().substring(1,
									tradeInfo.getTradeState().length()));
					tradeInfo.setRemark("Waiting processing!");
					remark=tradeInfo.getRemark();
					this.commonService.update(tradeInfo);
					logger.info("*********************支付结果返回码***************************"+responseCode);
					return SUCCESS;
				}else if(bankBackRemark.toLowerCase().indexOf(masaM.getRes_errMsg().toLowerCase())>=0&&noPending.equals(Boolean.FALSE)){
					this.responseCode = 19;
					
					MD5info = tradeInfo.getMerchantOrderNo()
							+ tradeInfo.getMoneyType() + ordercountValue
							+ responseCode + MD5key;
					md5Value = md5.getMD5ofStr(MD5info);
					tradeInfo.setTradeState("2"
							+ tradeInfo.getTradeState().substring(1,
									tradeInfo.getTradeState().length()));
					tradeInfo.setRemark("Waiting processing!");
					remark=tradeInfo.getRemark();
					this.commonService.update(tradeInfo);
					shopManagerService.addTemporaryTradInfo(tradeInfo.getOrderNo(), year, month,cvv2,country,MD5key, ip,"MSIE 10.0","GR*"+masaM.getRes_errMsg());
					logger.info("*********************支付结果返回码***************************"+responseCode);
					return SUCCESS;
				}else {
					this.message = "Payment Declined!"+masaM.getRes_errMsg();
					this.responseCode = 0;
					remark=masaM.getRes_errMsg();
					tradeInfo.setTradeState("0"
							+ tradeInfo.getTradeState().substring(1,
									tradeInfo.getTradeState().length()));
					tradeInfo.setRemark(message);
					tradeInfo.setVIPDisposePorson("System");
					tradeInfo.setVIPDisposeDate(new Date());
					this.commonService.update(tradeInfo);
					MD5info = tradeInfo.getMerchantOrderNo()
							+ tradeInfo.getMoneyType() + ordercountValue
							+ responseCode + MD5key;
					md5Value = md5.getMD5ofStr(MD5info);
					logger.info("*********************支付结果返回码***************************"+responseCode);
					return SUCCESS;				
				}		
		}
		logger.info("*********************支付结束返回码***************************"+responseCode);
		return SUCCESS;
	} catch (Exception e) {
		logger.error(e);
		e.printStackTrace();
		this.responseCode = 35;
		message = "Payment Declined";
		logger.info("*********************支付结果返回码***************************"+responseCode);
		return SUCCESS;

	}
}

	public Hashtable getmmValue(HashMap hm,Long tradeId) {

		h = exam.maxMindScore(hm);
		InternationalBackMaxMind bmm=new InternationalBackMaxMind();
		bmm.setTradeId(tradeId);
		// 把MaxMind返回的参数打印出来,
		for (Iterator i = h.keySet().iterator(); i.hasNext();) {
			String key = (String) i.next();
			maxmindValue = (String) h.get(key);
			if (key.equals("riskScore")) {
				values = maxmindValue;
				bmm.setRiskScore(maxmindValue);
			}
			if (key.equals("binName")) {
				bankName = maxmindValue;
				bmm.setBinName(maxmindValue);
			}
			if (key.equals("binCountry")) {
				bankCountry = maxmindValue;
				bmm.setBinCountry(maxmindValue);
			}
			if (key.equals("binPhone")) {
				bankPhone = maxmindValue;
				bmm.setBinPhone(maxmindValue);
			}
			if (key.equals("ip_areaCode")) {
				bmm.setIp_areaCode(maxmindValue);
			}
			if (key.equals("ip_postalCode")) {
				bmm.setIp_postalCode(maxmindValue);
			}
			if (key.equals("ip_regionName")) {
				bmm.setIp_regionName(maxmindValue);
			}
			if (key.equals("ip_region")) {
				bmm.setIp_region(maxmindValue);
			}
			if (key.equals("ip_countryName")) {
				bmm.setIp_countryName(maxmindValue);
			}
			if (key.equals("countryCode")) {
				bmm.setCountryCode(maxmindValue);
			}
			if (key.equals("anonymousProxy")) {
				bmm.setAnonymousProxy(maxmindValue);
			}
			if (key.equals("distance")) {
				bmm.setDistance(maxmindValue);
			}
			if (key.equals("proxyScore")) {
				bmm.setProxyScore(maxmindValue);
			}
			if (key.equals("postalMatch")) {
				bmm.setPostalMatch(maxmindValue);
			}
			if (key.equals("custPhoneInBillingLoc")) {
				bmm.setCustPhoneInBillingLoc(maxmindValue);
			}
			if (key.equals("shipCityPostalMatch")) {
				bmm.setShipCityPostalMatch(maxmindValue);
			}
			if (key.equals("ip_city")) {
				bmm.setIp_city(maxmindValue);
			}
		}
		Hashtable ht = new Hashtable();
		if (values == null) {
			values = "0";
		}
		ht.put("values", values);
		ht.put("bankName", bankName);
		ht.put("bankCountry", bankCountry);
		ht.put("bankPhone", bankPhone);
		this.commonService.save(bmm);
		return ht;
	}
//验证敏感信息
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
					if(StringUtils.isNotBlank(sen.getEmail())&&sen.getEmail().indexOf("*")>=0){
						if(sBillNo.toLowerCase().indexOf(sen.getEmail().substring(1, sen.getEmail().length()).toLowerCase())>=0){
							break;
							}
						}
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
			}else if("4".equals(valiType)){
				if(StringUtils.isNotBlank(sBillNo)){
					String[] addIp=sBillNo.split("\\.");
					if(addIp.length>4){
						for(InternationalSensitiveInfo sen:list){
						String befIp="";
						String aftIp=sen.getIp1()+"."+sen.getIp2()+"."+sen.getIp3()+"."+sen.getIp4();
							if("*".equals(sen.getIp1())){
								befIp=befIp+"*.";
							}else{
								befIp=befIp+addIp[0]+".";
							}
							if("*".equals(sen.getIp2())){
								befIp=befIp+"*.";
							}else{
								befIp=befIp+addIp[1]+".";
							}
							if("*".equals(sen.getIp3())){
								befIp=befIp+"*.";
							}else{
								befIp=befIp+addIp[2]+".";
							}
							if("*".equals(sen.getIp4())){
								befIp=befIp+"*";
							}else{
								befIp=befIp+addIp[3];
							}
							if(befIp.equals(aftIp)){
								return true;
							}
						if(StringUtils.isNotBlank(sen.getProducts())){
							if(pro.toLowerCase().indexOf(sen.getProducts().toLowerCase())>=0){
								return true;
								}
							}
						}
					}
				}
			}
			
			return false;
		}
		//redinfo是黑名单中哪一项在表里有记录页面上就会显示红色
		public Boolean validateVisa(String valCardNo,String valEmail,String valIp,String valUrl,String strPhone,String strZipCode){
			String hql="from InternationalHighRisklist order by isweb desc nulls last";
			List<InternationalHighRisklist> list=commonService.list(hql);
			for(InternationalHighRisklist risk:list){
				if(StringUtils.isNotBlank(risk.getCardno())){
					if(valCardNo.toLowerCase().indexOf(risk.getCardno().toLowerCase())>=0){
						if("1".equals(risk.getIsQWeb())){
							isQWeb="1";
							if("1".equals(risk.getIsWeb())){
								isWeb="1";
							}
						}
						logger.info("出现防风险验证："+valCardNo+"风险匹配项："+risk.getCardno());
						redInfo="1";
						return true;
					}
				}
				if(StringUtils.isNotBlank(risk.getEmail())){
					if(valEmail.toLowerCase().indexOf(risk.getEmail().toLowerCase())>=0){
						if("1".equals(risk.getIsQWeb())){
							isQWeb="1";
							if("1".equals(risk.getIsWeb())){
								isWeb="1";
							}
						}
						logger.info("出现防风险验证："+valEmail+"风险匹配项："+risk.getEmail());
						redInfo="2";
						return true;
					}
					
				}
				if(StringUtils.isNotBlank(risk.getIp())){
					if(valIp.toLowerCase().indexOf(risk.getIp().toLowerCase())>=0){
						if("1".equals(risk.getIsQWeb())){
							isQWeb="1";
							if("1".equals(risk.getIsWeb())){
								isWeb="1";
							}
						}
						logger.info("出现防风险验证："+valIp+"风险匹配项："+risk.getIp());
						redInfo="3";
						return true;
					}
				}
				if(StringUtils.isNotBlank(risk.getTradeUrl())){
					if("1".equals(risk.getIsQWeb())&&"1".equals(risk.getIsWeb())){
							if(valUrl.toLowerCase().indexOf(risk.getTradeUrl().toLowerCase())>=0){
								isQWeb="1";
		//						if("1".equals(risk.getIsWeb())){
									isWeb="1";
		//						}
								logger.info("出现防风险验证："+valUrl+"风险匹配项："+risk.getTradeUrl());
								redInfo="4";
								return true;
							}
						}
					if(!"1".equals(risk.getIsQWeb())){
						if(valUrl.toLowerCase().indexOf(risk.getTradeUrl().toLowerCase())>=0){
							isQWeb=risk.getIsQWeb();
							logger.info("出现防风险验证："+valUrl+"风险匹配项："+risk.getTradeUrl());
							redInfo="4";
							return true;
						}
					}
			}
				if(StringUtils.isNotBlank(risk.getPhone())){
					if((strPhone.trim()).equals(risk.getPhone())){
						if("1".equals(risk.getIsQWeb())){
							isQWeb="1";
							if("1".equals(risk.getIsWeb())){
								isWeb="1";
							}
						}
						logger.info("出现防风险验证："+strPhone);
						redInfo="5";
						return true;
					}
				}
				if(StringUtils.isNotBlank(risk.getZipCode())){
					if(strZipCode.toLowerCase().indexOf(risk.getZipCode().toLowerCase())>=0){
						if("1".equals(risk.getIsQWeb())){
							isQWeb="1";
							if("1".equals(risk.getIsWeb())){
								isWeb="1";
							}
						}
						logger.info("出现防风险验证："+strZipCode+"风险匹配项："+risk.getZipCode());
						redInfo="6";
						return true;
					}
				}
			}
			
			return false;
		}
		//风控项匹配方法
		public Boolean validateRiskItems(String riskitems,String riskType){
				String hql=" from InternationalRiskItems t where t.itemType='"+riskType+"'";
				List<InternationalRiskItems> list=commonService.list(hql);
				for(InternationalRiskItems items:list){
					if("1".equals(riskType)){
						if(riskitems.toLowerCase().indexOf(items.getItemName().toLowerCase())>=0){
							logger.info("出现风控匹配项：国家>>"+riskitems);	
							return true;
						}
					}
					if("2".equals(riskType)){
						if(riskitems.toLowerCase().indexOf(items.getItemName().toLowerCase())>=0){
							logger.info("出现风控匹配项：区域>>"+riskitems);	
							return true;
						}
					}
					if("3".equals(riskType)){
						String hqlweb=" from InternationalWebchannels w where w.tradeWebsite='"+riskitems+"'";
						List<InternationalWebchannels> listWeb=commonService.list(hqlweb);
						for(InternationalWebchannels web:listWeb){
							if(StringUtils.isNotBlank(web.getWebSiteType())){
								if(web.getWebSiteType().toLowerCase().indexOf(items.getItemName().toLowerCase())>=0){
									logger.info("出现风控匹配项：网站分类>>"+web.getWebSiteType());	
									return true;
								}
							}
						}
			
					}
					}
			return false;
			}
		public Boolean valWhiteList(String merchantNo,String cardNo,String ip,String email,String webUrl,String country,String riskType){
			String hql=" from InternationalWhitelist t where t.risktype='"+riskType+"'";
			List<InternationalWhitelist> list=commonService.list(hql);
			for(InternationalWhitelist items:list){
				if("1".equals(items.getWhitetype())&&merchantNo.toLowerCase().indexOf(items.getWhitename().toLowerCase())>=0){
					System.out.println("商户号"+items.getWhitename());
					return true;
				}
				if("2".equals(items.getWhitetype())&&cardNo.toLowerCase().indexOf(items.getWhitename().toLowerCase())>=0){
					System.out.println("卡号"+items.getWhitename());
					return true;
				}
				if(StringUtils.isNotBlank(ip)){
					if("3".equals(items.getWhitetype())&&ip.toLowerCase().indexOf(items.getWhitename().toLowerCase())>=0){
						System.out.println("ip"+items.getWhitename());
						return true;
					}
				}
				if("4".equals(items.getWhitetype())&&email.toLowerCase().indexOf(items.getWhitename().toLowerCase())>=0){
					System.out.println("email"+items.getWhitename());
					return true;
				}
				if(StringUtils.isNotBlank(webUrl)){
					if("5".equals(items.getWhitetype())&&webUrl.toLowerCase().indexOf(items.getWhitename().toLowerCase())>=0){
						System.out.println("weburl"+items.getWhitename());
						return true;
					}
				}
				if(StringUtils.isNotBlank(country)){
					if("6".equals(items.getWhitetype())&&country.toLowerCase().indexOf(items.getWhitename().toLowerCase())>=0){
						System.out.println("国家"+items.getWhitename());
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
		 //base64+md5
		 public static String getBase64E(String strData) {
				BASE64Encoder base64E = new BASE64Encoder();
				String value = null;
				try {
					MessageDigest getMd5= MessageDigest.getInstance("MD5");
					value =  base64E.encode(getMd5.digest(strData.getBytes("UTF-8")));
				} catch (Exception e) {
					e.printStackTrace();
				}
		    return value;
		}
		 public static String getSha256(String strData) {
			 String output = "";
		     try {
		       MessageDigest digest = MessageDigest.getInstance("SHA-256");
		       byte[] hash = digest.digest(strData.getBytes("UTF-8"));
		       output = Hex.encodeHexString(hash);
		       System.out.println(output);
		      } catch (Exception e) {
		        // TODO Auto-generated catch block
		        e.printStackTrace();
		      }
		    return output;
		}
 
		 
		public Double getOrderAmout() {
			return orderAmout;
		}


		public void setOrderAmout(Double orderAmout) {
			this.orderAmout = orderAmout;
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


		public double get() {
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


		public String getBilladdress() {
			return billaddress;
		}


		public void setBilladdress(String billaddress) {
			this.billaddress = billaddress;
		}


		public String getCardBank() {
			return cardBank;
		}


		public void setCardBank(String cardBank) {
			this.cardBank = cardBank;
		}


		public String getXingChanel() {
			return xingChanel;
		}


		public void setXingChanel(String xingChanel) {
			this.xingChanel = xingChanel;
		}


		public String getIsWeb() {
			return isWeb;
		}


		public void setIsWeb(String isWeb) {
			this.isWeb = isWeb;
		}


		public String getMaxmindRiskValue() {
			return maxmindRiskValue;
		}


		public void setMaxmindRiskValue(String maxmindRiskValue) {
			this.maxmindRiskValue = maxmindRiskValue;
		}


		public String getIsQWeb() {
			return isQWeb;
		}


		public void setIsQWeb(String isQWeb) {
			this.isQWeb = isQWeb;
		}


		public String getMaxMindInfo() {
			return maxMindInfo;
		}


		public void setMaxMindInfo(String maxMindInfo) {
			this.maxMindInfo = maxMindInfo;
		}
}
