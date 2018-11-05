package com.ecpss.action.pay;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
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
import java.util.StringTokenizer;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import vpn.HarbinPayMessage;
import vpn.HarbinPayUtil;
import vpn.IPassPayMessage;
import vpn.IPassPayUtil;
import vpn.MasaPayMessage;
import vpn.MasaPayUtil;
import vpn.QuanQiuPayMessage;
import vpn.QuanQiuPayUtil;
import vpn.VpnUtil;
import vpn.VpnUtil2;
import vpn.VpnUtil3;
import vpn.VpnUtil_Moto;
import vpn.WPPayMessage;
import vpn.WPPayUtil;
import vpn.WRPayMessage;
import vpn.WRPayUtil;
import vpn.YinlianMessage;
import vpn.YinlianUtil;
import vpn.YouPayMessage;
import vpn.YouPayUtil;
import vpn.YoungPayMessage;
import vpn.YoungPayUtil;

import com.ecpss.action.BaseAction;
import com.ecpss.action.pay.fast.TradUtil;
import com.ecpss.action.pay.fast.TradeMessage;
import com.ecpss.action.pay.util.CheckCardNo;
import com.ecpss.action.pay.util.MaxMindExample;
import com.ecpss.model.channel.InternationalMerchantChannels;
import com.ecpss.model.payment.InternationalCardholdersInfo;
import com.ecpss.model.payment.InternationalTradeinfo;
import com.ecpss.model.payment.InternationalURLChannel;
import com.ecpss.model.risk.InternationalBackMaxMind;
import com.ecpss.model.risk.InternationalBacklist;
import com.ecpss.model.risk.InternationalHighRisklist;
import com.ecpss.model.risk.InternationalRiskItems;
import com.ecpss.model.risk.InternationalSensitiveInfo;
import com.ecpss.model.risk.InternationalWhitelist;
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
import com.ecpss.web.VcpUtil;

public class DirectCarderInfoAction extends BaseAction {
	@Autowired
	@Qualifier("tradeManager")
	private TradeManager tradeManager;
	@Autowired
	@Qualifier("shopManagerService")
	private ShopManagerService shopManagerService;
	private String thesame = "";
	private HashMap h = new HashMap();
	// 抛过来的持卡人信息
	private String rorderno = ""; // 商户流水号
	private Date birthday; // 生日日期
	private String year; // 年
	private String month; // 月
	private Long date; // 日
	private String firstname;//名字的第一个字
	private String lastname;//名字后面的字
	private String country; // 国家
	private String state; // 州
	private String city; // 城市
	private String address; // 地址
	private String zipcode; // 邮编编号
	private String email;//邮箱
	private String phone;//电话
	private String cardbank;//发卡行

	private String ReturnURL;//返回网址
	private String MD5key;//MD5k
	// 卡信息
	private String cardnum; // 卡号
	private String cvv2;//银行卡后面的验证加密
	private String ip;
	private String cookie;
	private String cookieId;

	// 定单
	private Double ordercount; // 订单金额

	// 返回参数
	private int responseCode;

	private String remark; // 备注
	private String MD5info; // MD5加密

	// md5 数据校验
	private String tradeMoneyType;
	private String merchantOrderNo;
	private String md5Value; // 支付网关对商户信息进行加密
	private String message;

	// 2.5方支付参数
	private String vpc_Card;
	// maxmin参数
	private String maxmindValue;
	String bankName = null;
	String bankCountry = null;
	String bankPhone = null;
	String ipRegionName = null;
	private String values;
	private String SECURE_SECRET;
	private MaxMindExample exam = new MaxMindExample();
	// shipping Address 信息
	// shippingAddress
	private String shippingFirstName;
	private String shippingLastName;
	private String shippingEmail;
	private String shippingPhone;
	private String shippingZipcode;
	private String shippingAddress;
	private String shippingCity;
	private String shippingSstate;
	private String shippingCountry;
	private String products;

	// 非三D参数
	private String virtualPaymentClientURL;// 请求地址
	private String accessCode;// 接入码

	private String merchantId;// 商户ID
	private String vpc_CardExp;// 卡有效期
	private int tradeOrder;// 交易金额
	private String connectURL;// 返回地址
	private String vpc_CardNum;// 卡号

	// dcc 参数
	private String amount_Transaction_Foreign;
	private String conversion_Rate;// 汇率
	private String currency_Code_Foreign;
	private String tradeType; // 交易类型

	// dcc 参数

	private String local_money;// 本地
	private String foreign_money;// 外币
	private String conversion_Rate_show;
	private String car_termanal;// 返回终端
	private String cardMessage;

	private String newcardtype;

	//3d交易
	private String token_id=""; 
	private String HASH="";
	private String Send_Url="";
	//新增直连（urltype=1）
	private String urlType;
	private String addIp;
	private String redInfo;
	private String isWeb="0";
	private String isQWeb="0";
	
	private String csid;
	
	public String getCsid() {
		return csid;
	}

	public void setCsid(String csid) {
		this.csid = csid;
	}

	public String getRedInfo() {
		return redInfo;
	}

	public void setRedInfo(String redInfo) {
		this.redInfo = redInfo;
	}

	public String getToken_id() {
		return token_id;
	}

	public void setToken_id(String token_id) {
		this.token_id = token_id;
	}

	public String getHASH() {
		return HASH;
	}

	public void setHASH(String hASH) {
		HASH = hASH;
	}

	public String getIsWeb() {
		return isWeb;
	}

	public void setIsWeb(String isWeb) {
		this.isWeb = isWeb;
	}

	public String getSend_Url() {
		return Send_Url;
	}

	public void setSend_Url(String send_Url) {
		Send_Url = send_Url;
	}
	Logger logger = Logger.getLogger(DirectCarderInfoAction.class.getName());
	/**
	 * 添加持卡人信息 input vip success 非三D
	 * 
	 * @return添加流程 卡号，商户号确定通道，风控，mixmad， 通道抛人民币处理。
	 */
	public String addCardMessage() {
		try {
			if(StringUtils.isBlank(shippingCountry)||shippingCountry.length()<5){
				this.shippingCountry="USAUS";
			}
			if(StringUtils.isBlank(shippingFirstName)){
				this.shippingFirstName="test";
			}
			if(StringUtils.isBlank(shippingLastName)){
				this.shippingLastName="test";
			}
			//___________________________
			
				if(StringUtils.isBlank(firstname)){
				this.firstname = this.shippingFirstName;
				}
				if(StringUtils.isBlank(lastname)){
				this.lastname = this.shippingLastName;
				}
				if(StringUtils.isBlank(country)||country.length()<5){
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
			//直连通道卡信息解密
			BASE64Decoder base64=new BASE64Decoder();
			cardnum=new String((base64.decodeBuffer(cardnum)));
			logger.info("64位加密转过的卡号"+cardnum.substring(0, 6)+"******"+cardnum.substring(12, cardnum.length()));
			cvv2=new String((base64.decodeBuffer(cvv2)));
			month=new String((base64.decodeBuffer(month)));
			year=new String((base64.decodeBuffer(year)));
			if(year.length()>2){
				year=year.substring(2, year.length());
			}
			MD5 md5 = new MD5();
			HttpServletRequest request = ServletActionContext.getRequest();
			HttpServletResponse response = ServletActionContext.getResponse();
			// / 获取持卡人的系统信息
			logger.info("*********************获取持卡人的系统信息***************************");
			String buyerinformation = "";
			try {
				String agent = request.getHeader("User-Agent");
				logger.info("*********************下单浏览器信息***************************"+agent);
				if (null != agent && "" != agent) {
					String agent2 = this.getNavigator(agent);
					String os = this.getOS(agent);
					buyerinformation = agent2 + " " + os;
				} else {
					agent = "";
				}
			} catch (Exception e) {
			}
			logger.info("*********************获取持卡人的系统信息结束***************************");
			String expirydate = month + year;

			// 交易
			InternationalTradeinfo trade = new InternationalTradeinfo();
			String hql = "from InternationalTradeinfo t where t.orderNo='"
					+ rorderno + "'";

			List<InternationalTradeinfo> tradl = this.commonService.list(hql);
			trade = tradl.get(0);
			// 转换成标准金额
				ordercount = trade.getTradeAmount();

				DecimalFormat decimalFormat = new DecimalFormat(
								"##############0.00");
				String ordercountValue = decimalFormat.format(ordercount);

				tradeMoneyType = trade.getMoneyType() + "";
				merchantOrderNo = trade.getMerchantOrderNo() + "";
				logger.info("*********************转换金额结束***************************");
			// ECPSS流水号不唯一
			if (tradl == null) {
				this.responseCode = 0;
				MD5info = trade.getMerchantOrderNo()
						+ trade.getMoneyType() + ordercountValue
						+ responseCode + MD5key;
				md5Value = md5.getMD5ofStr(MD5info);
				message = "Payment Declined";
				logger.info("*********************支付结果返回码***************************"+responseCode);
				return SUCCESS;
			} else if (tradl.size() != 1) {
				this.responseCode = 0;
				MD5info = trade.getMerchantOrderNo()
						+ trade.getMoneyType() + ordercountValue
						+ responseCode + MD5key;
				md5Value = md5.getMD5ofStr(MD5info);
				message = "Payment Declined";
				logger.info("*********************支付结果返回码***************************"+responseCode);
				return SUCCESS;
			}
			Calendar calendar1 = Calendar.getInstance();// 此时打印它获取的是系统当前时间
			calendar1.add(Calendar.SECOND,-40); // 得到30秒
			String nowTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
					.format(calendar1.getTime());
			List listc=commonService.list("select ti from InternationalTradeinfo ti where ti.merchantId='"+trade.getMerchantId()+"' and ti.tradeAmount='"+trade.getTradeAmount()+"' and ti.returnUrl='"+trade.getReturnUrl()+"' and ti.tradeTime>to_date('"
					+ nowTime + "','yyyy-MM-dd hh24:mi:ss')");
			if(listc.size()>1){
				message = "Payment Declined";
				remark = "重复提交/Repeat business";
				responseCode = 7;
				logger.info("30秒之内重复提交！");
				MD5info = trade.getMerchantOrderNo() + trade.getMoneyType()
						+ ordercountValue + responseCode + MD5key;
				md5Value = md5.getMD5ofStr(MD5info);
				trade.setTradeState("3"
						+ trade.getTradeState().substring(1,
								trade.getTradeState().length()));
				String re[]=remark.split("/");
				trade.setRemark(re[0]);
				this.commonService.update(trade);
				logger.info("*********************支付结果返回码***************************"+responseCode);
				return SUCCESS;
			}
			// InternationalMerCreditCard
			this.ReturnURL = trade.getReturnUrl();
			InternationalMerchant merchant = (InternationalMerchant) this.commonService
					.load(InternationalMerchant.class, trade.getMerchantId());

			// 校验是否商户号对上交易
			if (merchant == null) {
				this.responseCode = 0;
				MD5info = trade.getMerchantOrderNo()
						+ trade.getMoneyType() + ordercountValue
						+ responseCode + MD5key;
				md5Value = md5.getMD5ofStr(MD5info);
				message = "Payment Declined";
				logger.info("*********************支付结果返回码***************************"+responseCode);
				return SUCCESS;
			}

			// 获取ip
			logger.info("*********************获取IP***************************");
			logger.info("addIp："+addIp);
			ip = addIp;//直连新增
			if(StringUtils.isBlank(ip)){
				ip= getIpAddr(request);
			}
			logger.info("ip："+ip);
			GetBatchNo ut = new GetBatchNo();
			if (cookieId == null || cookieId.length() == 5) {
				cookie = "ecpss.com" + ut.getCookies();
				Cookie cook = new Cookie("ids", cookie);
				// 生命周期
				cook.setMaxAge(60 * 60 * 24 * 365);
				response.addCookie(cook);
			}
			logger.info("*********************获取IP结束***************************");
			/**
			 * 读cookie
			 */
			// cookie = (String)session.getAttribute("id");
			logger.info("*********************读取cookie***************************");
			Cookie[] cookies = request.getCookies();

			if (cookies != null) {
				for (int i = 0; i < cookies.length; i++) {
					Cookie c = cookies[i];
					if (c.getName().equalsIgnoreCase("ids")) {
						cookie = c.getValue();
					}
				}
			}
			logger.info("*********************读取cookie结束***************************");
			/** ******************风险控制******************** */
			// 获取信用卡前6位号码
			String backCardnum6 = (new StringBuilder(cardnum)).delete(6,
					cardnum.length()).toString();
			String backCardnum9 = (new StringBuilder(cardnum)).delete(9,
					cardnum.length()).toString();

			// 判断卡类型
			logger.info("*********************判断卡类型***************************");
			int cartype = 0;
//			if (StringUtils.isNotBlank(newcardtype)) {
				if ((cardnum.startsWith("30") || cardnum
								.startsWith("35"))) {
					cartype = 3;
					vpc_Card = "Jcb";
				}
				if (cardnum.startsWith("4")) {
					if (cardnum.length() == 16) {
						cartype = 4;
						vpc_Card = "Visa";
					} else {
						remark = "visa卡信息错误" + cardnum+"/visa Card information error";
						responseCode = 26;
						MD5info = trade.getMerchantOrderNo()
								+ trade.getMoneyType() + ordercountValue
								+ responseCode + MD5key;
						md5Value = md5.getMD5ofStr(MD5info);
						String re[]=remark.split("/");
						trade.setRemark(re[0]);
						this.commonService.update(trade);
						logger.info("*********************支付结果返回码***************************"+responseCode);
						return SUCCESS;
					}
				}
				if (cardnum.startsWith("5")||cardnum.startsWith("2")) {
					if (cardnum.length() == 16) {
						cartype = 5;
						vpc_Card = "Mastercard";
					} else {
						remark = "master卡信息错误" + cardnum+"/master Card information error";
						responseCode = 26;
						MD5info = trade.getMerchantOrderNo()
								+ trade.getMoneyType() + ordercountValue
								+ responseCode + MD5key;
						md5Value = md5.getMD5ofStr(MD5info);
						String re[]=remark.split("/");
						trade.setRemark(re[0]);
						this.commonService.update(trade);
						logger.info("*********************支付结果返回码***************************"+responseCode);
						return SUCCESS;
					}
				}
				if ((cardnum.startsWith("34") || cardnum
								.startsWith("37"))) {
					cartype = 6;
					vpc_Card = "Aecard";
				}
				if (cardnum.startsWith("36")
								|| cardnum.startsWith("300")
								|| cardnum.startsWith("305") || cardnum
									.startsWith("38")) {
					cartype = 7;
					vpc_Card = "DcCard";
				}

//			}
			newcardtype=cartype+"";
			logger.info("*********************判断卡类型结束***************************");
			if (cartype == 5) {
				this.cardMessage = "I declare that I have been offered a choice of payment currencies and my choice is final.";
			} else {
				this.cardMessage = "I declare that I have been offered a choice of payment currencies and my choice is final.I understand that the currency conversion is not provided by Visa.";

			}

			// 根据商户号,卡种信息获取通道
			logger.info("*********************卡种信息获取通道***************************"+cartype);
			String sql = "select a.channelname , d.id ,b.id as tid,d.priority from international_channels a ,international_creditcardtype b,international_mercreditcard c ,international_merchantchannels d where a.id=d.channelid and d.merchantid='"
					+ merchant.getId()
					+ "' and b.shortname='"
					+ cartype
					+ "' and d.id=c.merchannelid and b.id=c.creditcardid and d.onoff='1' and c.onoff='1' order by to_number(d.priority) asc";
			List chanellist = this.commonService.getByList(sql);
			String urlHql="select uc.channelName,uc.webUrl from InternationalURLChannel uc where uc.merchantId='"+ merchant.getId()+"' and uc.webUrl like'%"+trade.getTradeUrl()+"%'";
			List urllist=commonService.list(urlHql);
			// 商户通道ID
			String merchanID = "";
			// 通道名称
			String chanelName = "";
			// 卡种ID
			Long carType = 0l;
			Boolean V5Chanel=false;
			if("US".equals(country.substring(3, 5))||"CA".equals(country.substring(3, 5))){
				logger.info("交易国家："+country);
				V5Chanel=true;
			}
			if (chanellist.size() > 0) {
				//独立网站分配通道
				if(urllist.size()>0){
					for(int i=0;i<chanellist.size();i++){
						Object[] tem = (Object[]) chanellist.get(i);
						for(int j=0;j<urllist.size();j++){
							Object[] tem2 = (Object[]) urllist.get(j);
							if(tem2[0].toString().equals(tem[0])){
								logger.info("出现网站："+tem2[1]+"匹配通道："+tem2[0]);
								merchanID = tem[1].toString();
								chanelName = tem[0].toString();
								carType = Long.valueOf(tem[2].toString());
								break;
							}
						}
					}
				}else{
					Object[] tem = (Object[]) chanellist.get(0);
					Object[] maxTem = (Object[]) chanellist.get(chanellist.size()-1);
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
				remark = "通道未设置/The channel is not set";
				responseCode = 16;

				MD5info = trade.getMerchantOrderNo() + trade.getMoneyType()
						+ ordercountValue + responseCode + MD5key;

				md5Value = md5.getMD5ofStr(MD5info);
				String re[]=remark.split("/");
				trade.setRemark(re[0]);
				this.commonService.update(trade);
				logger.info("*********************支付结果返回码***************************"+responseCode);
				return SUCCESS;
			}
			logger.info("*********************卡种信息获取通道结束***************************");
			// 输入值验证
			if (checkAll()) {
				remark = "卡信息错误/Card information error";
				responseCode = 26;

				MD5info = trade.getMerchantOrderNo() + trade.getMoneyType()
						+ ordercountValue + responseCode + MD5key;

				md5Value = md5.getMD5ofStr(MD5info);
				String re[]=remark.split("/");
				trade.setRemark(re[0]);
				this.commonService.update(trade);
				logger.info("*********************支付结果返回码***************************"+responseCode);
				return SUCCESS;
			}
/*			InternationalMerchantChannels im = new InternationalMerchantChannels();
			if("USAUS".equals(country)&&!"3860".equals(merchant.getMerno()+"")){
				if(cartype == 4){
					im = (InternationalMerchantChannels)  this.commonService.uniqueResult("from InternationalMerchantChannels where channelId=39 and merchantId='"+merchant.getId()+"'");
					merchanID = String.valueOf(im.getId());
					chanelName = "HR-V";
				}else{
					im = (InternationalMerchantChannels)  this.commonService.uniqueResult("from InternationalMerchantChannels where channelId=40 and merchantId='"+merchant.getId()+"'");
					merchanID = String.valueOf(im.getId());
					chanelName = "HR-M";					
				}
			}else if("3860".equals(merchant.getMerno()+"")){
				if(cartype == 4){
					im = (InternationalMerchantChannels)  this.commonService.uniqueResult("from InternationalMerchantChannels where channelId=27 and merchantId='"+merchant.getId()+"'");
					merchanID = String.valueOf(im.getId());
					chanelName = "GP-V";
				}else{
					im = (InternationalMerchantChannels)  this.commonService.uniqueResult("from InternationalMerchantChannels where channelId=28 and merchantId='"+merchant.getId()+"'");
					merchanID = String.valueOf(im.getId());
					chanelName = "GP-M";					
				}
			}else{
				im = (InternationalMerchantChannels) this.commonService
						.load(InternationalMerchantChannels.class,
								Long.valueOf(merchanID));				
			}*/
			logger.info("*********************商户通道信息***************************"+merchanID);
			InternationalMerchantChannels im = (InternationalMerchantChannels) this.commonService
					.load(InternationalMerchantChannels.class,
							Long.valueOf(merchanID));	
			logger.info("*********************商户通道名称***************************"+chanelName);
			String cardTypeNum = chanelName.substring(0, 3).toString();
			// 根据商户号,卡种信息获取通道
			logger.info(cartype + "开头的卡走的通道是:" + cardTypeNum);
			logger.info(trade.getId());
			String carlist = "select count(*) from international_cardholdersinfo t where t.tradeid='"
					+ trade.getId() + "'";
			int carInfoList = 0;
			carInfoList = this.tradeManager.intBySql(carlist);
			if (carInfoList > 0) {
				message = "Payment Declined";
				remark = "同一笔交易重复支付/The same transaction duplicate payment";
				responseCode = 31;

				MD5info = trade.getMerchantOrderNo() + trade.getMoneyType()
						+ ordercountValue + responseCode + MD5key;
				md5Value = md5.getMD5ofStr(MD5info);
				trade.setTradeState("0"
						+ trade.getTradeState().substring(1,
								trade.getTradeState().length()));
				String re[]=remark.split("/");
				trade.setRemark(re[0]);
				this.commonService.update(trade);
				logger.info("*********************支付结果返回码***************************"+responseCode);
				return SUCCESS;
			}
			InternationalCardholdersInfo card = new InternationalCardholdersInfo();
			card.setTradeId(trade.getId());
			// card.setTradetime(trade.getTradeTime());
			card.setIp(ip);
			card.setCookie(cookie);
			// card.setRiskvalue(maxmind);
			card.setCardNo(AES.setCarNo(cardnum));
			card.setCardNo6(AES.setCarNo(cardnum.substring(0,6)));
			card.setCardNo9(AES.setCarNo(cardnum.substring(0,9)));
			card.setCardNo12(AES.setCarNo(cardnum.substring(0,12)));
			card.setCardNo4(AES.setCarNo(cardnum.substring(cardnum.length()-4)));
			card.setFirstName(firstname);
			card.setLastName(lastname);
			// card.setUserName(firstname+" "+lastname);
			card.setEmail(email.trim());
			card.setPhone(phone);
			// card.setResulturl(ReturnURL);
			logger.info("*********************国家简码***************************"+country);
			card.setCountry(country.substring(3, 5));
			card.setState(state);
			card.setCity(city);
			card.setAddress(address);
			card.setZipcode(zipcode);
			card.setUserBank(cardbank);
			card.setShippingAddress(this.getShippingAddress());
			card.setShippingCity(this.getShippingCity());
			card.setShippingCountry(this.getShippingCountry().substring(3, 5));
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
			 if(im.getChannelFee()!=null){
				trade.setChannelFee(im.getChannelFee());
			 }
			 trade.setTradeChannel(Long.valueOf(merchanID));
			 this.commonService.update(trade);
//			 List<Long> backCardValue=this.commonService.list("select t.merId from InternationalBacklist t where substr(t.cardno,1,6)='"
//							+ cardnum.substring(0,6) + "' and substr(t.cardno,13,4)='"+cardnum.substring(12,cardnum.length())+"' ");
//
//			int riskCardValue = this.tradeManager
//					.intBySql("select count(1) from InternationalRisklist t where substr(t.cardno,1,6)='"
//							+ cardnum.substring(0,6) + "' and substr(t.cardno,13,4)='"+cardnum.substring(12,cardnum.length())+"' ");
			 List<Long> backCardValue=this.commonService.list("select t.merId from InternationalBacklist t where t.cardno='"
						+ cardnum + "' ");
			 int riskCardValue = this.tradeManager
				.intBySql("select count(1) from InternationalRisklist t where t.cardno='"
						+ cardnum + "' ");
			 int riskIPValue = this.tradeManager
						.intBySql("select count(1) from InternationalRisklist t where t.ip='"
								+ ip + "' ");
			 int riskEmailValue = this.tradeManager
						.intBySql("select count(1) from InternationalRisklist t where t.email='"
								+ email + "' ");
			 int risktradeUrlValue = this.tradeManager
						.intBySql("select count(1) from InternationalRisklist t where t.tradeUrl like '"
								+ trade.getTradeUrl() + "' ");
			 if (riskCardValue>0||riskIPValue>0||riskEmailValue>0||risktradeUrlValue>0) {
				 	logger.info("riskCardValue:"+riskCardValue+"riskIPValue:"+riskIPValue+"riskEmailValue:"+riskEmailValue+"risktradeUrlValue:"+risktradeUrlValue);
					message = "Payment Declined";
					remark = "高风险交易/banned the trading of card";
					responseCode = 2;
					logger.info("返回状态码+++++++++" + responseCode);
					MD5info = trade.getMerchantOrderNo() + trade.getMoneyType()
							+ ordercountValue + responseCode + MD5key;
					md5Value = md5.getMD5ofStr(MD5info);
					trade.setTradeState("3"
							+ trade.getTradeState().substring(1,
									trade.getTradeState().length()));
					String re[]=remark.split("/");
					trade.setRemark(re[0]);
					this.commonService.update(trade);
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
					rl.setCardno(cardnum);
					rl.setEmail(email);
					rl.setIp(ip);
					rl.setPhone(phone);
					rl.setMerId(merchant.getMerno());
					rl.setTradeUrl(trade.getTradeUrl());
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
			// 根据ip或cardNumber或Email，COCKET 信息查询持卡人24小时内交易的次数(成功的，待处理，待确认的交易)
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

						MD5info = trade.getMerchantOrderNo()
								+ trade.getMoneyType() + ordercountValue
								+ responseCode + MD5key;

						md5Value = md5.getMD5ofStr(MD5info);
						trade.setTradeState("0"
								+ trade.getTradeState().substring(1,
										trade.getTradeState().length()));
						String re[]=remark.split("/");
						trade.setRemark(re[0]);
						this.commonService.update(trade);
						logger.info("*********************支付结果返回码***************************"+responseCode);
						return SUCCESS;
					}
				}
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
							+ trade.getId()
							+ "'");

			// 获取交易金额
			// 单笔交易默认不能大于600元

			int backCardValue6 = this.tradeManager
					.intBySql("select count(*) from Internationalbacklist t where t.cardno='"
							+ backCardnum6 + "' ");
			int backCardValue9 = this.tradeManager
					.intBySql("select count(*) from Internationalbacklist t where t.cardno='"
							+ backCardnum9 + "' ");
			String chnals = chanelName.split("-")[0];
			// 验证黑邮箱
			List<Long> backEmailValue=this.commonService.list("select t.merId from InternationalBacklist t where lower(t.email)='"
							+ email.trim().toLowerCase() + "' ");
			// 验证黑IP
			List<Long> backIpValue=this.commonService.list("select t.merId from InternationalBacklist t where t.ip='"
							+ ip + "' ");
			// 输入值验证
			if (checkAll()) {
				this.responseCode = 25;
				remark = "信息有误/Incorrect information";
				logger.info("返回状态码+++++++++" + responseCode);
				message = "Payment Declined";
				MD5info = trade.getMerchantOrderNo() + trade.getMoneyType()
						+ ordercountValue + responseCode + MD5key;

				md5Value = md5.getMD5ofStr(MD5info);
				trade.setTradeState("0"
						+ trade.getTradeState().substring(1,
								trade.getTradeState().length()));
				String re[]=remark.split("/");
				trade.setRemark(re[0]);
				this.commonService.update(trade);
				logger.info("*********************支付结果返回码***************************"+responseCode);
				return SUCCESS;
			}else if (trade.getTradeAmount() > merchantmanager.getPenQuota()&&!"6".equals(trade.getMoneyType()+"")) {
				message = "Payment Declined";
				remark = "单笔超限/Single pen limit";
				responseCode = 3;
				logger.info("返回状态码+++++++++" + responseCode);
				MD5info = trade.getMerchantOrderNo() + trade.getMoneyType()
						+ ordercountValue + responseCode + MD5key;

				md5Value = md5.getMD5ofStr(MD5info);
				trade.setTradeState("0"
						+ trade.getTradeState().substring(1,
								trade.getTradeState().length()));
				String re[]=remark.split("/");
				trade.setRemark(re[0]);
				this.commonService.update(trade);
				logger.info("*********************支付结果返回码***************************"+responseCode);
				return SUCCESS;
			}else if (trade.getTradeAmount() > 100000&&"6".equals(trade.getMoneyType()+"")) {
				message = "Payment Declined";
				remark = "单笔超限/Single pen limit";
				responseCode = 3;
				logger.info("返回状态码+++++++++" + responseCode);
				MD5info = trade.getMerchantOrderNo() + trade.getMoneyType()
						+ ordercountValue + responseCode + MD5key;

				md5Value = md5.getMD5ofStr(MD5info);
				trade.setTradeState("0"
						+ trade.getTradeState().substring(1,
								trade.getTradeState().length()));
				String re[]=remark.split("/");
				trade.setRemark(re[0]);
				this.commonService.update(trade);
				logger.info("*********************支付结果返回码***************************"+responseCode);
				return SUCCESS;
			}
			//相同客户信息不同交易网站验证
			Calendar calendarUrl = Calendar.getInstance();// 此时打印它获取的是系统当前时间
			calendarUrl.add(Calendar.MONTH,-6); // 得到3月前
			String threeMouth = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
						.format(calendarUrl.getTime());
			List repUrllist = commonService.getByList
						("select t.orderNo,t.tradeUrl from international_cardholdersinfo f, international_tradeinfo t where f.tradeid=t.id and (f.cardNo='"
								+ AES.setCarNo(cardnum)
								+ "' or f.ip='"+ip.trim()+"' or f.email='"+email.trim()+"') and t.tradeTime>to_date('"
					+ threeMouth + "','yyyy-MM-dd hh24:mi:ss')");
			if(repUrllist!=null&&repUrllist.size()>0){
				for (int i=0;i<repUrllist.size();i++){
					Object[] queUrl=(Object[]) repUrllist.get(i);
					if(queUrl[0].toString().indexOf('*',1)>0){
						logger.info("****出现特高风险********");
						InternationalHighRisklist rl=new InternationalHighRisklist();
						rl.setCardno(cardnum);
						rl.setEmail(email.trim());
						rl.setIp(ip.trim());
						rl.setPhone(phone);
						rl.setMerId(merchant.getMerno());
						rl.setTradeUrl(trade.getTradeUrl());
						rl.setOperator("systemVisaRisk");
						rl.setCreateDate(new Date());
						rl.setIsWeb("1");
						rl.setIsQWeb("1");
						commonService.save(rl);
						message = "Payment Declined！";
						remark = "Payment Declined！04";
						responseCode = 2;
						trade.setTradeState("3"
								+ trade.getTradeState().substring(1,
										trade.getTradeState().length()));
						logger.info("返回状态码+++++++++" + responseCode);
						MD5info = trade.getMerchantOrderNo() + trade.getMoneyType()
								+ ordercountValue + responseCode + MD5key;
						md5Value = md5.getMD5ofStr(MD5info);
						trade.setRemark(remark);
						this.commonService.update(trade);
						return SUCCESS;
					}
				}
			}
			//历史拒付过的交易失败
			int refuseCard = 0;
			refuseCard = this.tradeManager
					.intBySql("select count(1) from international_cardholdersinfo f, international_tradeinfo t where f.tradeid=t.id and (f.cardno='"
							+ AES.setCarNo(this.cardnum)
							+ "' or f.email='"+email
							+ "') and substr(t.tradestate,3,1)='1' ");
			if (Long.valueOf(refuseCard) >= 1) {
				message = "Payment Declined";
				remark = "已拒付过的交易/The transaction has dishonored";
				responseCode = 38;
				logger.info("返回状态码+++++++++" + responseCode);
				MD5info = trade.getMerchantOrderNo() + trade.getMoneyType()
						+ ordercountValue + responseCode + MD5key;
				md5Value = md5.getMD5ofStr(MD5info);
				trade.setTradeState("3"
						+ trade.getTradeState().substring(1,
								trade.getTradeState().length()));
				String re[]=remark.split("/");
				trade.setRemark(re[0]);
				this.commonService.update(trade);
				logger.info("*********************支付结果返回码***************************"+responseCode);
				return SUCCESS;

			}
			// 与商户的风控制对比，然后进行对比
			// 连接maxmind系统
			if(!"411111".equals(backCardnum6)){
			try {
				HashMap hm = new HashMap();
				// 加密串 license_key : UxQh0mA4aLqw 调试和正式运行时要加上,才会返回分数
				// 上海key: CxsRZ1xPPRbR;
				// 广州key: UxQh0mA4aLqw
				int index = email.indexOf("@");
				String domian = email.substring(index + 1, email.length());
				hm.put("license_key", "9kbrHiIOJ9ZS");
				String ip1[]=ip.split(",");
				hm.put("i", ip1[0]);
				hm.put("domain", domian);
				hm.put("emailMD5", md5.getMD5ofStr(email.toLowerCase()));
				hm.put("custPhone", phone);
				hm.put("country", country.substring(3, 5));
				hm.put("city", city);
				hm.put("region", state);
				hm.put("shipAddr", address);
				hm.put("postal", zipcode.toString());
				// hm.put("bin", cardnum);
				hm.put("bin", backCardnum6);
				hm.put("binName", cardbank);
	
				// standard 低级
				// premium 高级
				// 正式运行的时候要用这个 premium ; standard为调试用的标准
				hm.put("requested_type", "premium");
	
				Hashtable ht = getmmValue(hm,trade.getId());
				maxmindValue = (String) ht.get("values");
				bankName = (String) ht.get("bankName");
				bankCountry = (String) ht.get("bankCountry");
				bankPhone = (String) ht.get("bankPhone");
				logger.info("maxmindValue--------------" + maxmindValue);
					// System.out.println("riskValue--------------"+riskValue);
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
	//			this.commonService
	//					.deleteBySql(" update international_cardholdersinfo t set t.maxmindValue='"
	//						+ maxmind
	//						+ "',t.bankcountry='"
	//						+ "',t.bankname='"
	//						+ bankName
	//						+ "',t.bankphone='"
	//						+ bankPhone
	//						+ "' where t.id='"
	//						+ card.getId()
	//						+ "' ");
				// 高风险卡 VIP为待确认 ，其他的为失败
	//			if ((maxmind >= im.getMaxmind_lv1())
	//						&& (maxmind <= im.getMaxmind_lv2())) {
	//				message = "Your Payment is being Processed! ";
	//				remark = "中风险！/The risk";
	//				responseCode = 9;
	//				logger.info("返回状态码+++++++++" + responseCode);
	//				MD5info = trade.getMerchantOrderNo() + trade.getMoneyType()
	//							+ ordercountValue + responseCode + MD5key;
	//				md5Value = md5.getMD5ofStr(MD5info);
	//				if (chnals.equals("VIP")
	//						|| (chnals.equals("EVIP") && merchant.getStatutes()
	//								.subSequence(6, 7).equals("0"))
	//						|| chnals.equals("PRE")) {
	//					trade.setTradeState("4"
	//							+ trade.getTradeState().substring(1,
	//									trade.getTradeState().length()));
	//					String re[]=remark.split("/");
	//					trade.setRemark(re[0]);
	//					this.commonService.update(trade);
	//					// -------更新商户月交易限额-----------------
	//					merchant.setMonthTradeMoney(merchant.getMonthTradeMoney()
	//							+ trade.getTradeAmount());
	//					this.commonService.update(merchant);
	//				} else {
	//					trade.setTradeState("0"
	//							+ trade.getTradeState().substring(1,
	//									trade.getTradeState().length()));
	//					String re[]=remark.split("/");
	//					trade.setRemark(re[0]);
	//					this.commonService.update(trade);
			//
	//				}
	//				logger.info("*********************支付结果返回码***************************"+responseCode);
	//				return SUCCESS;
	//			} 
//				if (maxmind > im.getMaxmind_lv2()) {
//					message = "Payment Declined";
//					remark = "高风险！/High-risk";
//					responseCode = 19;
//					logger.info("返回状态码+++++++++" + responseCode);
//					MD5info = trade.getMerchantOrderNo() + trade.getMoneyType()
//							+ ordercountValue + responseCode + MD5key;
//					md5Value = md5.getMD5ofStr(MD5info);
//					trade.setTradeState("2"
//							+ trade.getTradeState().substring(1,
//									trade.getTradeState().length()));
//					String re[]=remark.split("/");
//					trade.setRemark(re[0]);
//					this.commonService.update(trade);
//					shopManagerService.addTemporaryTradInfo(rorderno, year, month,cvv2,country,MD5key, ip,"MSIE 10.0","risk");
//					return SUCCESS;
//				}	
			}
			/**
			 * 禁止交易地区按照持卡人账单地区check
			 */
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
					MD5info = trade.getMerchantOrderNo() + trade.getMoneyType()
							+ ordercountValue + responseCode + MD5key;
					md5Value = md5.getMD5ofStr(MD5info);
					trade.setTradeState("0"
							+ trade.getTradeState().substring(1,
									trade.getTradeState().length()));
					String re[]=remark.split("/");
					trade.setRemark(re[0]);
					this.commonService.update(trade);
					return SUCCESS;
				}
			//风险分数大于设定分数
			if(card.getMaxmindValue()!=null){
				if (card.getMaxmindValue()> im.getMaxmind_lv2()) {
					message = "Payment Declined";
					remark = "高风险！/High-risk";
					responseCode = 19;
					logger.info("返回状态码+++++++++" + responseCode);
					MD5info = trade.getMerchantOrderNo() + trade.getMoneyType()
							+ ordercountValue + responseCode + MD5key;
					md5Value = md5.getMD5ofStr(MD5info);
					trade.setTradeState("2"
							+ trade.getTradeState().substring(1,
									trade.getTradeState().length()));
					String re[]=remark.split("/");
					trade.setRemark(re[0]);
					this.commonService.update(trade);
					shopManagerService.addTemporaryTradInfo(rorderno, year, month,cvv2,country,MD5key, ip,"MSIE 10.0","risk");
					return SUCCESS;
				}
			}
//		if(!"3970".equals(merchant.getMerno()+"")&&!"4034".equals(merchant.getMerno()+"")&&!"4035".equals(merchant.getMerno()+"")){	
//			Boolean riskWebVal=validateWebItems(trade.getTradeUrl(), country.substring(3, 5),bankCountry,ipRegionName,state);
//			if(riskWebVal.equals(Boolean.TRUE)){
//				logger.info("出现网站匹配区域项");
//				message = "Payment Declined！";
//				remark = "Payment Declined！08";
//				responseCode = 2;
//				trade.setTradeState("3"
//						+ trade.getTradeState().substring(1,
//								trade.getTradeState().length()));
//				logger.info("返回状态码+++++++++" + responseCode);
//				MD5info = trade.getMerchantOrderNo() + trade.getMoneyType()
//						+ ordercountValue + responseCode + MD5key;
//				md5Value = md5.getMD5ofStr(MD5info);
//				trade.setRemark(remark);
//				this.commonService.update(trade);
//				return SUCCESS;
//			}
			//风控开始
			Boolean oneRisk=valWhiteList(merchant.getMerno()+"",cardnum,ip,email,trade.getTradeUrl(),country+","+bankCountry,"1");
			if(oneRisk.equals(Boolean.FALSE)){
				if(StringUtils.isBlank(products)){
					products="sfepay";
				}
			String tradWeb[] = ReturnURL.split("/");
			String isWebHql="from InternationalHighRisklist where merId='"+merchant.getMerno()+"' and isQWeb='1'";
			List<InternationalHighRisklist> listweb=commonService.list(isWebHql);
			if(listweb.size()>0){
					isQWeb="1";
				}
			logger.info("****开始防风险验证********");
			Boolean isVisaVal=validateVisa(cardnum,email,ip,tradWeb[2],phone,zipcode);
			String valCountry="";
			if(StringUtils.isNotBlank(bankCountry)){
				valCountry=bankCountry;
			}else{
				valCountry=country.substring(3, 5);
			}
			if(isVisaVal.equals(Boolean.TRUE)){
				Boolean isCountryVal=validateRiskItems(valCountry, "1",merchant.getMerno()+"");
				InternationalHighRisklist rl=new InternationalHighRisklist();
				rl.setCardno(cardnum);
				rl.setEmail(email);
				rl.setIp(ip);
				rl.setPhone(phone);
//				rl.setZipCode(zipcode);
				rl.setMerId(merchant.getMerno());
//				if(!"1".equals(isWeb)){
					rl.setTradeUrl(tradWeb[2]);
//				}
				rl.setOperator("system");
				rl.setCreateDate(new Date());
				if(StringUtils.isNotBlank(redInfo)){
					rl.setErrorColumn(redInfo);
				}
				rl.setIsQWeb(isQWeb);
				rl.setIsWeb(isWeb);
				commonService.save(rl);
				logger.info("采集风险信息结束");
//				this.commonService.deleteBySql("update InternationalWebchannels set isblack='1' where tradeWebsite='"+a[2]+"'");
//				logger.info("关闭"+merchant.getMerno()+"的交易网址");
				message = "Payment Declined！";
				if("1".equals(isWeb)&&isCountryVal.equals(Boolean.TRUE)){
				remark = "Payment Declined！04";
				responseCode = 2;
				trade.setTradeState("3"
						+ trade.getTradeState().substring(1,
								trade.getTradeState().length()));
				}else{
					if("1".equals(isWeb)){
						remark = "Payment Declined！05";
					}else{
					remark = "Payment Declined！01";
					}
					responseCode = 19;
					trade.setTradeState("2"
							+ trade.getTradeState().substring(1,
									trade.getTradeState().length()));
				}
				logger.info("返回状态码+++++++++" + responseCode);
				MD5info = trade.getMerchantOrderNo() + trade.getMoneyType()
						+ ordercountValue + responseCode + MD5key;
				md5Value = md5.getMD5ofStr(MD5info);
//				trade.setTradeState("2"
//						+ trade.getTradeState().substring(1,
//								trade.getTradeState().length()));
				trade.setRemark(remark);
				this.commonService.update(trade);
				shopManagerService.addTemporaryTradInfo(rorderno, year, month,cvv2,country,MD5key, ip,"MSIE 10.0",remark);
				EmailInfo emailinfo = new EmailInfo();
				String mailinfo = emailinfo.getRiskInfoToSystem(merchant.getMerno(),trade.getMerchantOrderNo(),
						ordercountValue,trade.getMoneyType(),cardnum,email,ip,tradWeb[2],redInfo);
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
//			Boolean twoRisk=valWhiteList(merchant.getMerno()+"",cardnum,ip,email,trade.getTradeUrl(),country+","+bankCountry,"2");
//			if(twoRisk.equals(Boolean.FALSE)){
		if("US".equals(country.substring(3, 5))||"CA".equals(country.substring(3, 5))||"GB".equals(country.substring(3, 5))||"US".equals(bankCountry)||"CA".equals(bankCountry)||"GB".equals(bankCountry)){
			//新卡bin处理
			List binlist = commonService.getByList
					("select f.id,f.cardNo6 from international_cardholdersinfo f, international_tradeinfo t where f.tradeid=t.id and f.cardNo6='"
							+ AES.setCarNo(cardnum.substring(0,6))
							+ "'");
			if (binlist.size()<=1) {
				message = "Payment Declined！";
				remark = "Payment Declined！02";
				responseCode = 19;
				logger.info("返回状态码+++++++++" + responseCode);
				MD5info = trade.getMerchantOrderNo() + trade.getMoneyType()
						+ ordercountValue + responseCode + MD5key;
				md5Value = md5.getMD5ofStr(MD5info);
				trade.setTradeState("2"
						+ trade.getTradeState().substring(1,
								trade.getTradeState().length()));
				trade.setRemark(remark);
				this.commonService.update(trade);
				shopManagerService.addTemporaryTradInfo(rorderno, year, month,cvv2,country,MD5key, ip,"MSIE 10.0","Payment Declined！02");
				return SUCCESS;
			}
//			Boolean threeRisk=valWhiteList(merchant.getMerno()+"",cardnum,ip,email,trade.getTradeUrl(),country+","+bankCountry,"3");
//			if(threeRisk.equals(Boolean.FALSE)){
			//相同客户信息不同交易网站验证
			if(repUrllist!=null&&repUrllist.size()>0){
				Boolean  valiUrl=false;
				for (int i=0;i<repUrllist.size();i++){
					Object[] queUrl=(Object[]) repUrllist.get(i);
					if(!(trade.getTradeUrl().trim()).toLowerCase().equals(queUrl[1].toString().toLowerCase())){
						valiUrl=true;
						break;
					}
				}
			if(valiUrl.equals(Boolean.TRUE)){
				message = "Payment Declined！";
				remark = "Payment Declined！03";
				responseCode = 19;
				logger.info("返回状态码+++++++++" + responseCode);
				MD5info = trade.getMerchantOrderNo() + trade.getMoneyType()
						+ ordercountValue + responseCode + MD5key;
				md5Value = md5.getMD5ofStr(MD5info);
				trade.setTradeState("2"
						+ trade.getTradeState().substring(1,
								trade.getTradeState().length()));
				trade.setRemark(remark);
				this.commonService.update(trade);
				shopManagerService.addTemporaryTradInfo(rorderno, year, month,cvv2,country,MD5key, ip,"MSIE 10.0","Payment Declined！03");
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
							+ AES.setCarNo(cardnum)
							+ "' or f.ip='"+ip.trim()+"' or f.email='"+email.trim()+"') and t.tradeTime>to_date('"
				+ oneMouth + "','yyyy-MM-dd hh24:mi:ss')");
			if(repUrllist2!=null&&repUrllist2.size()>1){
				message = "Payment Declined！";
				remark = "Payment Declined！08";
				responseCode = 19;
				logger.info("返回状态码+++++++++" + responseCode);
				MD5info = trade.getMerchantOrderNo() + trade.getMoneyType()
						+ ordercountValue + responseCode + MD5key;
				md5Value = md5.getMD5ofStr(MD5info);
				trade.setTradeState("2"
						+ trade.getTradeState().substring(1,
								trade.getTradeState().length()));
				trade.setRemark(remark);
				this.commonService.update(trade);
				shopManagerService.addTemporaryTradInfo(rorderno, year, month,cvv2,country,MD5key, ip,"MSIE 10.0","Payment Declined！08");
				return SUCCESS;				
			}
			
//			Boolean fourRisk=valWhiteList(merchant.getMerno()+"",cardnum,ip,email,trade.getTradeUrl(),country+","+bankCountry,"4");
//			if(fourRisk.equals(Boolean.FALSE)){
			logger.info("****开始验证敏感信息********");
			Boolean isValiBill=validateSensitive("1",trade.getMerchantOrderNo(), products);
			Boolean isValiEmail=validateSensitive("2",email, products);
			Boolean isValiTradurl=validateSensitive("3",tradWeb[2], products);
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
				MD5info = trade.getMerchantOrderNo() + trade.getMoneyType()
						+ ordercountValue + responseCode + MD5key;
				md5Value = md5.getMD5ofStr(MD5info);
				trade.setTradeState("2"
						+ trade.getTradeState().substring(1,
								trade.getTradeState().length()));
				trade.setRemark(remark);
				this.commonService.update(trade);
				shopManagerService.addTemporaryTradInfo(rorderno, year, month,cvv2,country,MD5key, ip,"MSIE 10.0","Payment Declined！00");
				return SUCCESS;
			}
			logger.info("****验证敏感信息结束********");
			//风控网站类型的控制
//			Boolean fiveRisk=valWhiteList(merchant.getMerno()+"",cardnum,ip,email,trade.getTradeUrl(),country+","+bankCountry,"5");
//			if(fiveRisk.equals(Boolean.FALSE)){
			logger.info("****开始验证网站分类信息********");
			Boolean isValiWebType=validateRiskItems(trade.getTradeUrl(), "3",merchant.getMerno()+"");
			if(isValiWebType.equals(Boolean.TRUE)){
				message = "Payment Declined！";
				remark = "Payment Declined！00";
				responseCode = 19;
				logger.info("返回状态码+++++++++" + responseCode);
				MD5info = trade.getMerchantOrderNo() + trade.getMoneyType()
						+ ordercountValue + responseCode + MD5key;
				md5Value = md5.getMD5ofStr(MD5info);
				trade.setTradeState("2"
						+ trade.getTradeState().substring(1,
								trade.getTradeState().length()));
				trade.setRemark(remark);
				this.commonService.update(trade);
				shopManagerService.addTemporaryTradInfo(rorderno, year, month,cvv2,country,MD5key, ip,"MSIE 10.0","Payment Declined！07");
				return SUCCESS;
			}
			logger.info("****结束验证网站分类信息********");
			//风控地域的控制
			Boolean isRegionType=false;
				if(StringUtils.isBlank(ipRegionName)){
					ipRegionName=state;
				}
				if(StringUtils.isBlank(ipRegionName)&&StringUtils.isBlank(state)){
					isRegionType=true;
				}else{
					logger.info("****开始验证地域信息********");
					isRegionType=validateRiskItems(ipRegionName, "2",merchant.getMerno()+"");
				}
				if(isRegionType.equals(Boolean.TRUE)){
					message = "Payment Declined！";
					remark = "Payment Declined！00";
					responseCode = 19;
					logger.info("返回状态码+++++++++" + responseCode);
					MD5info = trade.getMerchantOrderNo() + trade.getMoneyType()
							+ ordercountValue + responseCode + MD5key;
					md5Value = md5.getMD5ofStr(MD5info);
					trade.setTradeState("2"
							+ trade.getTradeState().substring(1,
									trade.getTradeState().length()));
					trade.setRemark(remark);
					this.commonService.update(trade);
					shopManagerService.addTemporaryTradInfo(rorderno, year, month,cvv2,country,MD5key, ip,"MSIE 10.0","Payment Declined！06");
					return SUCCESS;
				}
				logger.info("****结束验证地域信息********");
				}
		}
//			 黑卡库
			for(Long backCard:backCardValue){
			if (backCard==null||(merchant.getId()).equals(backCard)) {
				message = "Payment Declined";
				remark = "黑卡/banned the trading of card";
				responseCode = 2;
				logger.info("返回状态码+++++++++" + responseCode);
				MD5info = trade.getMerchantOrderNo() + trade.getMoneyType()
						+ ordercountValue + responseCode + MD5key;
				md5Value = md5.getMD5ofStr(MD5info);
				trade.setTradeState("0"
						+ trade.getTradeState().substring(1,
								trade.getTradeState().length()));
				String re[]=remark.split("/");
				trade.setRemark(re[0]);
				this.commonService.update(trade);
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
				MD5info = trade.getMerchantOrderNo() + trade.getMoneyType()
						+ ordercountValue + responseCode + MD5key;
				md5Value = md5.getMD5ofStr(MD5info);
				trade.setTradeState("0"
						+ trade.getTradeState().substring(1,
								trade.getTradeState().length()));
				String re[]=remark.split("/");
				trade.setRemark(re[0]);
				this.commonService.update(trade);
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
				MD5info = trade.getMerchantOrderNo() + trade.getMoneyType()
						+ ordercountValue + responseCode + MD5key;
				md5Value = md5.getMD5ofStr(MD5info);
				trade.setTradeState("0"
						+ trade.getTradeState().substring(1,
								trade.getTradeState().length()));
				String re[]=remark.split("/");
				trade.setRemark(re[0]);
				this.commonService.update(trade);
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
				MD5info = trade.getMerchantOrderNo() + trade.getMoneyType()
						+ ordercountValue + responseCode + MD5key;
				md5Value = md5.getMD5ofStr(MD5info);
				trade.setTradeState("0"
						+ trade.getTradeState().substring(1,
								trade.getTradeState().length()));
				String re[]=remark.split("/");
				trade.setRemark(re[0]);
				this.commonService.update(trade);
				logger.info("*********************支付结果返回码***************************"+responseCode);
				return SUCCESS;
				// response.sendRedirect(returnurl);
			}
//			if("US".equals(country.substring(3, 5))||"CA".equals(country.substring(3, 5))||"US".equals(bankCountry)||"CA".equals(bankCountry)){
//				if("5".equals(cardnum.substring(0, 1))){
//				responseCode = 19;
//				logger.info("返回状态码+++++++++" + responseCode);
//				MD5info = trade.getMerchantOrderNo() + trade.getMoneyType()
//						+ ordercountValue + responseCode + MD5key;
//				md5Value = md5.getMD5ofStr(MD5info);
//				trade.setTradeState("2"
//						+ trade.getTradeState().substring(1,
//								trade.getTradeState().length()));
//				trade.setRemark("Payment Declined！03");
//				this.commonService.update(trade);
//				shopManagerService.addTemporaryTradInfo(rorderno, year, month,cvv2,country,MD5key, ip,"MSIE 10.0","USorCA");
//				return SUCCESS;
//				}
//			}
			Calendar calendar3 = Calendar.getInstance();// 此时打印它获取的是系统当前时间
			calendar3.add(Calendar.MINUTE, -10); // 得到前10分钟
			String shiDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
					.format(calendar3.getTime());
			//10分钟之内不让重复交易
			int chongfu=0;
			chongfu=this.tradeManager.intBySql("select count(1) from international_cardholdersinfo f, international_tradeinfo t where f.tradeid=t.id and f.cardno='"
					+ AES.setCarNo(this.cardnum)
					+ "' and f.ip='"
					+ ip+"' and f.email='"+email+"' and t.tradeAmount='"+trade.getTradeAmount()
					+ "' and substr(t.tradestate,1,1)='0' and t.tradetime>to_date('"
					+ shiDate + "','yyyy-MM-dd hh24:mi:ss')");
			if (Long.valueOf(chongfu) >=1) {
				message = "Payment Declined！";
				remark = "10分钟内不能重复提交/Repeat business";
				responseCode = 7;
				logger.info("返回状态码+++++++++" + responseCode);
				MD5info = trade.getMerchantOrderNo() + trade.getMoneyType()
						+ ordercountValue + responseCode + MD5key;
				md5Value = md5.getMD5ofStr(MD5info);
				trade.setTradeState("3"
						+ trade.getTradeState().substring(1,
								trade.getTradeState().length()));
				String re[]=remark.split("/");
				trade.setRemark(re[0]);
				this.commonService.update(trade);
				logger.info("*********************支付结果返回码***************************"+responseCode);
				return SUCCESS;

			}
			// 同一卡号
			int carno = 0;
//			List carno=this.commonService.list("select t from InternationalCardholdersInfo f,InternationalTradeinfo t where f.tradeId=t.id and f.cardNo='"
//					+ AES.setCarNo(this.cardnum)+ "' and t.merchantId='"+ merchant.getId()+ "' and substring(t.tradeState,1,1) in(1,2,4,5,6) and t.tradeTime>to_date('"
//					+ yestedayDate + "','yyyy-MM-dd hh24:mi:ss')");
			carno = this.tradeManager
					.intBySql("select count(1) from international_cardholdersinfo f, international_tradeinfo t where f.tradeid=t.id and f.cardno='"
							+ AES.setCarNo(this.cardnum)
							+ "' and t.merchantid='"
							+ merchant.getId()
							+ "' and substr(t.tradestate,1,1) in(1,2,4,5,6) and t.tradetime>to_date('"
							+ yestedayDate + "','yyyy-MM-dd hh24:mi:ss')");
			if (Long.valueOf(carno) >= localCarNO) {
				message = "Payment Declined";
				remark = "重复交易/Repeat business";
				responseCode = 7;
				logger.info("返回状态码+++++++++" + responseCode);
				MD5info = trade.getMerchantOrderNo() + trade.getMoneyType()
						+ ordercountValue + responseCode + MD5key;
				md5Value = md5.getMD5ofStr(MD5info);
				trade.setTradeState("3"
						+ trade.getTradeState().substring(1,
								trade.getTradeState().length()));
				String re[]=remark.split("/");
				trade.setRemark(re[0]);
				this.commonService.update(trade);
				logger.info("*********************支付结果返回码***************************"+responseCode);
				return SUCCESS;

			}
			//3天之内前3笔都是失败的卡号
			Calendar calendar2 = Calendar.getInstance();// 此时打印它获取的是系统当前时间
			calendar2.add(Calendar.DATE, -3); // 得到前3天
			String sanshiDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
					.format(calendar2.getTime());
			int cardno30error = 0;
			cardno30error = this.tradeManager
					.intBySql("select count(*) from(select cardno,tradestate,tradetime from (select f.cardno,t.tradestate,t.tradetime "
							+ "from international_cardholdersinfo f, international_tradeinfo t where f.tradeid=t.id "
							+ "and f.cardno='"+AES.setCarNo(this.cardnum)+"' and t.merchantid='"+ merchant.getId()
							+ "' and t.tradetime>to_date('"+sanshiDate+"','yyyy-MM-dd hh24:mi:ss') order by t.tradetime desc) "
							+ "where rownum<=3) s where substr(s.tradestate,1,1)='0'");
			if (Long.valueOf(cardno30error) >= 3) {
				logger.info("连续失败的卡号："+cardnum);
				message = "Payment Declined";
				remark = "重复失败次数过多卡号/Repeated failure many times";
				responseCode = 7;
				logger.info("返回状态码+++++++++" + responseCode);
				MD5info = trade.getMerchantOrderNo() + trade.getMoneyType()
						+ ordercountValue + responseCode + MD5key;
				md5Value = md5.getMD5ofStr(MD5info);
				trade.setTradeState("3"
						+ trade.getTradeState().substring(1,
								trade.getTradeState().length()));
				String re[]=remark.split("/");
				trade.setRemark(re[0]);
				this.commonService.update(trade);
				InternationalBacklist back=new InternationalBacklist();
				back.setCardno(cardnum);
				this.commonService.save(back);
				logger.info("*********************支付结果返回码***************************"+responseCode);
				return SUCCESS;
			}
			int ip30error = 0;
			ip30error = this.tradeManager
					.intBySql("select count(*) from(select cardno,tradestate,tradetime from (select f.cardno,t.tradestate,t.tradetime "
							+ "from international_cardholdersinfo f, international_tradeinfo t where f.tradeid=t.id "
							+ "and f.ip='"+ip+"' and t.merchantid='"+ merchant.getId()
							+ "' and t.tradetime>to_date('"+sanshiDate+"','yyyy-MM-dd hh24:mi:ss') order by t.tradetime desc) "
							+ "where rownum<=3) s where substr(s.tradestate,1,1)='0'");
			if (Long.valueOf(ip30error) >= 3) {
				logger.info("连续失败的ip："+ip);
				message = "Payment Declined";
				remark = "重复失败次数过多Ip/Repeated failure many times";
				responseCode = 7;
				logger.info("返回状态码+++++++++" + responseCode);
				MD5info = trade.getMerchantOrderNo() + trade.getMoneyType()
						+ ordercountValue + responseCode + MD5key;
				md5Value = md5.getMD5ofStr(MD5info);
				trade.setTradeState("3"
						+ trade.getTradeState().substring(1,
								trade.getTradeState().length()));
				String re[]=remark.split("/");
				trade.setRemark(re[0]);
				this.commonService.update(trade);
				InternationalBacklist back=new InternationalBacklist();
				back.setIp(ip);
				this.commonService.save(back);
				logger.info("*********************支付结果返回码***************************"+responseCode);
				return SUCCESS;
			}
			int email30error = 0;
			email30error = this.tradeManager
					.intBySql("select count(*) from(select cardno,tradestate,tradetime from (select f.cardno,t.tradestate,t.tradetime "
							+ "from international_cardholdersinfo f, international_tradeinfo t where f.tradeid=t.id "
							+ "and f.email='"+email+"' and t.merchantid='"+ merchant.getId()
							+ "' and t.tradetime>to_date('"+sanshiDate+"','yyyy-MM-dd hh24:mi:ss') order by t.tradetime desc) "
							+ "where rownum<=3) s where substr(s.tradestate,1,1)='0'");
			if (Long.valueOf(email30error) >= 3) {
				logger.info("连续失败的email："+email);
				message = "Payment Declined";
				remark = "重复失败次数过多email/Repeated failure many times";
				responseCode = 7;
				logger.info("返回状态码+++++++++" + responseCode);
				MD5info = trade.getMerchantOrderNo() + trade.getMoneyType()
						+ ordercountValue + responseCode + MD5key;
				md5Value = md5.getMD5ofStr(MD5info);
				trade.setTradeState("3"
						+ trade.getTradeState().substring(1,
								trade.getTradeState().length()));
				String re[]=remark.split("/");
				trade.setRemark(re[0]);
				this.commonService.update(trade);
				InternationalBacklist back=new InternationalBacklist();
				back.setEmail(email);
				this.commonService.save(back);
				logger.info("*********************支付结果返回码***************************"+responseCode);
				return SUCCESS;
			}
			// 同一卡号失败次数
			int carnoerror = 0;
			carnoerror = this.tradeManager
					.intBySql("select count(1) from international_cardholdersinfo f, international_tradeinfo t where f.tradeid=t.id and f.cardno='"
							+ AES.setCarNo(this.cardnum)
							+ "' and t.merchantid='"
							+ merchant.getId()
							+ "' and substr(t.tradestate,1,1) ='0' and t.tradetime>to_date('"
							+ yestedayDate + "','yyyy-MM-dd hh24:mi:ss')");
			if (Long.valueOf(carnoerror) >= 2) {
				message = "Payment Declined";
				remark = "重复失败次数过多/Repeated failure many times";
				responseCode = 7;
				logger.info("返回状态码+++++++++" + responseCode);
				MD5info = trade.getMerchantOrderNo() + trade.getMoneyType()
						+ ordercountValue + responseCode + MD5key;
				md5Value = md5.getMD5ofStr(MD5info);
				trade.setTradeState("3"
						+ trade.getTradeState().substring(1,
								trade.getTradeState().length()));
				String re[]=remark.split("/");
				trade.setRemark(re[0]);
				this.commonService.update(trade);
				logger.info("*********************支付结果返回码***************************"+responseCode);
				return SUCCESS;
			}
			
//			//同一ip失败次数
			int iperror = 0;
			iperror = this.tradeManager
					.intBySql("select count(1) from international_cardholdersinfo f, international_tradeinfo t where f.tradeid=t.id and f.ip='"
							+ ip
							+ "' and t.merchantid='"
							+ merchant.getId()
							+ "' and substr(t.tradestate,1,1) ='0' and t.tradetime>to_date('"
							+ yestedayDate + "','yyyy-MM-dd hh24:mi:ss')");
			if (Long.valueOf(iperror) >= 3) {
				message = "Payment Declined";
				remark = "重复失败次数过多/Repeated failure many times";
				responseCode = 7;
				logger.info("返回状态码+++++++++" + responseCode);
				MD5info = trade.getMerchantOrderNo() + trade.getMoneyType()
						+ ordercountValue + responseCode + MD5key;
				md5Value = md5.getMD5ofStr(MD5info);
				trade.setTradeState("3"
						+ trade.getTradeState().substring(1,
								trade.getTradeState().length()));
				String re[]=remark.split("/");
				trade.setRemark(re[0]);
				this.commonService.update(trade);
				logger.info("*********************支付结果返回码***************************"+responseCode);
				return SUCCESS;
			}
			
			// 同一ip
			int ipcount = 0;
			ipcount = this.tradeManager
					.intBySql("select count(1) from international_cardholdersinfo f, international_tradeinfo t where f.tradeid=t.id and f.ip='"
							+ ip
							+ "' and t.merchantid='"
							+ merchant.getId()
							+ "' and substr(t.tradestate,1,1) in(1,2,4,5,6) and t.tradetime>to_date('"
							+ yestedayDate + "','yyyy-MM-dd hh24:mi:ss')");
//			List ipcount=this.commonService.list("select t from InternationalCardholdersInfo f,InternationalTradeinfo t where f.tradeId=t.id and f.ip='"
//					+ ip+ "' and t.merchantId='"+ merchant.getId()+ "' and substring(t.tradeState,1,1) in(1,2,4,5,6) and t.tradeTime>to_date('"
//					+ yestedayDate + "','yyyy-MM-dd hh24:mi:ss')");
			if (Long.valueOf(ipcount) >= localIP) {
				message = "Payment Declined";
				remark = "重复交易/Repeat business";
				responseCode = 5;
				logger.info("返回状态码+++++++++" + responseCode);
				MD5info = trade.getMerchantOrderNo() + trade.getMoneyType()
						+ ordercountValue + responseCode + MD5key;
				md5Value = md5.getMD5ofStr(MD5info);
				trade.setTradeState("3"
						+ trade.getTradeState().substring(1,
								trade.getTradeState().length()));
				String re[]=remark.split("/");
				trade.setRemark(re[0]);
				this.commonService.update(trade);
				logger.info("*********************支付结果返回码***************************"+responseCode);
				return SUCCESS;
			}
			// 同一邮箱
			int emailcount = 0;
			emailcount = this.tradeManager
					.intBySql("select count(1) from international_cardholdersinfo f, international_tradeinfo t where f.tradeid=t.id and f.email='"
							+ this.email
							+ "' and t.merchantid='"
							+ merchant.getId()
							+ "' and substr(t.tradestate,1,1) in(1,2,4,5,6) and t.tradetime>to_date('"
							+ yestedayDate + "','yyyy-MM-dd hh24:mi:ss')");
//			List emailcount=this.commonService.list("select t from InternationalCardholdersInfo f,InternationalTradeinfo t where f.tradeId=t.id and f.email='"
//					+ this.email+ "' and t.merchantId='"+ merchant.getId()+ "' and substring(t.tradeState,1,1) in(1,2,4,5,6) and t.tradeTime>to_date('"
//					+ yestedayDate + "','yyyy-MM-dd hh24:mi:ss')");
			if (Long.valueOf(emailcount) >= localEMAIL) {
				message = "Payment Declined";
				remark = "重复交易/Repeat business";
				responseCode = 6;
				logger.info("返回状态码+++++++++" + responseCode);
				MD5info = trade.getMerchantOrderNo() + trade.getMoneyType()
						+ ordercountValue + responseCode + MD5key;
				md5Value = md5.getMD5ofStr(MD5info);
				trade.setTradeState("3"
						+ trade.getTradeState().substring(1,
								trade.getTradeState().length()));
				String re[]=remark.split("/");
				trade.setRemark(re[0]);
				this.commonService.update(trade);
				logger.info("*********************支付结果返回码***************************"+responseCode);
				return SUCCESS;
			}
			// 同一电话
			int telcout = 0;
			telcout = this.tradeManager
					.intBySql("select count(1) from international_cardholdersinfo f, international_tradeinfo t where f.tradeid=t.id and f.phone='"
							+ this.phone
							+ "' and t.merchantid='"
							+ merchant.getId()
							+ "' and substr(t.tradestate,1,1) in(1,2,4,5,6) and t.tradetime>to_date('"
							+ yestedayDate + "','yyyy-MM-dd hh24:mi:ss')");
//			List telcout=this.commonService.list("select t from InternationalCardholdersInfo f,InternationalTradeinfo t where f.tradeId=t.id and f.phone='"
//					+ this.phone+ "' and t.merchantId='"+ merchant.getId()+ "' and substring(t.tradeState,1,1) in(1,2,4,5,6) and t.tradeTime>to_date('"
//					+ yestedayDate + "','yyyy-MM-dd hh24:mi:ss')");
			if (Long.valueOf(telcout) >= localPhone) {
				message = "Payment Declined";
				remark = "重复交易/Repeat business";
				responseCode = 30;
				logger.info("返回状态码+++++++++" + responseCode);
				MD5info = trade.getMerchantOrderNo() + trade.getMoneyType()
						+ ordercountValue + responseCode + MD5key;
				md5Value = md5.getMD5ofStr(MD5info);
				trade.setTradeState("3"
						+ trade.getTradeState().substring(1,
								trade.getTradeState().length()));
				String re[]=remark.split("/");
				trade.setRemark(re[0]);
				this.commonService.update(trade);
				logger.info("*********************支付结果返回码***************************"+responseCode);
				return SUCCESS;
			}
			// 同一cookies
			int cocketcount = 0;
			cocketcount = this.tradeManager
					.intBySql("select count(1) from international_cardholdersinfo f, international_tradeinfo t where f.tradeid=t.id and f.cookie='"
							+ this.cookie
							+ "' and t.merchantid='"
							+ merchant.getId()
							+ "' and substr(t.tradestate,1,1) in(1,2,4,5,6) and t.tradetime>to_date('"
							+ yestedayDate + "','yyyy-MM-dd hh24:mi:ss')");
//			List cocketcount=this.commonService.list("select t from InternationalCardholdersInfo f,InternationalTradeinfo t where f.tradeId=t.id and f.cookie='"
//					+ this.cookie+ "' and t.merchantId='"+ merchant.getId()+ "' and substring(t.tradeState,1,1) in(1,2,4,5,6) and t.tradeTime>to_date('"
//					+ yestedayDate + "','yyyy-MM-dd hh24:mi:ss')");
			if (Long.valueOf(cocketcount) >= localCOCKET) {
				message = "Payment Declined";
				remark = "重复交易/Repeat business";
				responseCode = 8;
				logger.info("返回状态码+++++++++" + responseCode);
				MD5info = trade.getMerchantOrderNo() + trade.getMoneyType()
						+ ordercountValue + responseCode + MD5key;
				md5Value = md5.getMD5ofStr(MD5info);
				trade.setTradeState("0"
						+ trade.getTradeState().substring(1,
								trade.getTradeState().length()));
				String re[]=remark.split("/");
				trade.setRemark(re[0]);
				this.commonService.update(trade);
				logger.info("*********************支付结果返回码***************************"+responseCode);
				return SUCCESS;
			}

		/*	Calendar today = Calendar.getInstance();
			today.set(Calendar.HOUR_OF_DAY, 0);
			today.set(Calendar.MINUTE, 0);
			today.set(Calendar.SECOND,0);
			String todayTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
			.format(today.getTime());			
			String todaytotalmoney = "select sum(ti.rmbAmount) from InternationalTradeinfo ti where ti.merchantId='"+trade.getMerchantId()+"' and substr(ti.tradeState,0,1)='"+1+"' and ti.tradeTime>to_date('"
					+ todayTime + "','yyyy-MM-dd hh24:mi:ss')";
			Double todaymoney = (Double) this.commonService.uniqueResult(todaytotalmoney);
			//日交易金额限额
			if (todaymoney + trade.getRmbAmount() > merchantmanager
					.getDayQuota()) {
				// //先把需要发送邮件的信息保存到数据库
				// saveMailInfo(merEmail,num1,"ecpss@ecpss.com");
				message = "Payment Declined";
				remark = "日交易金额超限/transaction volume overload";
				responseCode = 4;
				logger.info("返回状态码+++++++++" + responseCode);
				MD5info = trade.getMerchantOrderNo() + trade.getMoneyType()
						+ ordercountValue + responseCode + MD5key;
				md5Value = md5.getMD5ofStr(MD5info);
				if (chnals.equals("VIP")
						|| (chnals.equals("EVIP") && merchant.getStatutes()
								.subSequence(6, 7).equals("0"))) {
					trade.setTradeState("4"
							+ trade.getTradeState().substring(1,
									trade.getTradeState().length()));
					String re[]=remark.split("/");
					trade.setRemark(re[0]);
					this.commonService.update(trade);
				} else {
					trade.setTradeState("0"
							+ trade.getTradeState().substring(1,
									trade.getTradeState().length()));
					String re[]=remark.split("/");
					trade.setRemark(re[0]);
					this.commonService.update(trade);
				}

				// 超过限额给商户发送邮件提醒
				shopManagerService.addSendMessages(merchant.getMeremail(),
						"ecpss@ecpss.cc",
						merchant.getMerno() + " " + EmailInfo.getMoreMoney(),
						"0");
				
				logger.info("*********************支付结果返回码***************************"+responseCode);
				return SUCCESS;

			}	*/	
			
			if (merchant.getMonthTradeMoney() + ordercount > merchantmanager
					.getMonthQuota()) {
				// //先把需要发送邮件的信息保存到数据库
				// saveMailInfo(merEmail,num1,"ecpss@ecpss.com");
				message = "Payment Declined";
				remark = "月交易量超限/transaction volume overload";
				responseCode = 4;
				logger.info("返回状态码+++++++++" + responseCode);
				MD5info = trade.getMerchantOrderNo() + trade.getMoneyType()
						+ ordercountValue + responseCode + MD5key;
				md5Value = md5.getMD5ofStr(MD5info);
				if (chnals.equals("VIP")
						|| (chnals.equals("EVIP") && merchant.getStatutes()
								.subSequence(6, 7).equals("0"))) {
					trade.setTradeState("4"
							+ trade.getTradeState().substring(1,
									trade.getTradeState().length()));
					String re[]=remark.split("/");
					trade.setRemark(re[0]);
					this.commonService.update(trade);
				} else {
					trade.setTradeState("0"
							+ trade.getTradeState().substring(1,
									trade.getTradeState().length()));
					String re[]=remark.split("/");
					trade.setRemark(re[0]);
					this.commonService.update(trade);
				}

				// 超过限额给商户发送邮件提醒
				shopManagerService.addSendMessages(merchant.getMeremail(),
						"ecpss@ecpss.cc",
						merchant.getMerno() + " " + EmailInfo.getMoreMoney(),
						"0");
				
				logger.info("*********************支付结果返回码***************************"+responseCode);
				return SUCCESS;

			}
			// 高风险卡 VIP为待确认 ，其他的为失败
			if (riskCardValue > 0) {
				message = "Your Payment is being Processed! ";
				remark = "高风险卡/High risk cards";
				responseCode = 1;
				logger.info("返回状态码+++++++++" + responseCode);
				MD5info = trade.getMerchantOrderNo() + trade.getMoneyType()
						+ ordercountValue + responseCode + MD5key;
				md5Value = md5.getMD5ofStr(MD5info);
				if (chnals.equals("VIP")) {
				trade.setTradeState("4"
						+ trade.getTradeState().substring(1,
							trade.getTradeState().length()));
				String re[]=remark.split("/");
				trade.setRemark(re[0]);
				this.commonService.update(trade);
				} else {
					trade.setTradeState("0"
					+ trade.getTradeState().substring(1,
					trade.getTradeState().length()));
					String re[]=remark.split("/");
					trade.setRemark(re[0]);
					this.commonService.update(trade);
				}
				logger.info("*********************支付结果返回码***************************"+responseCode);
				return SUCCESS;

			}
			// 当商户交易的金额达到商户交易条件设置金额的85%则以邮件的方式通知商户
			if (merchant.getMonthTradeMoney() + ordercount > merchantmanager
						.getMonthQuota() * 0.85) {
					// 发送邮件
					shopManagerService.addSendMessages(
							merchant.getMeremail(),
								"ecpss@ecpss.cc",
								merchant.getMerno()
									+ " "
									+ EmailInfo.getMoreMoneyPart(merchant
									.getMonthTradeMoney() + ordercount),
								"0");
					// this.saveMailInfo(merEmail, num2, "ecpss@ecpss.com");
				}

			
			if (chnals.equals("EVIP") || chnals.equals("CN3D")
					|| chnals.equals("SFE")) {
				// /连接ECPSS系统 s-to-s模式支付
				java.text.DateFormat format2 = new java.text.SimpleDateFormat(
						"yyyyMMddhhmmss");
				String dateTime = format2.format(trade.getTradeTime());

				String md5src; // 加密字符串
				md5src = "1002" + trade.getOrderNo() + "3"
						+ trade.getRmbAmount() + "en" + ReturnURL + dateTime
						+ "sfepaymd";
				String MD5info_; // MD5加密后的字符串
				MD5info_ = md5.getMD5ofStr(md5src);
				String returnurl = "https://security.sslepay.com/evippayment";
				URL url;
				logger.info("post网址+++++++++++" + returnurl);
				try {
					url = new URL(returnurl);

					URLConnection connection = url.openConnection();
					connection.setDoOutput(true);
					OutputStreamWriter out = new OutputStreamWriter(
							connection.getOutputStream(), "8859_1");
					String parte = "cardnum=" + cardnum + "&cvv2=" + this.cvv2
							+ "&month=" + this.month + "&year=" + this.year
							+ "&cardbank=" + cardbank +

							"&MerNo=" + "1002" + "&BillNo="
							+ trade.getOrderNo() + "&Amount="
							+ trade.getRmbAmount() + "&Currency=" + "3"
							+ "&email=" + email + "&Language=" + "en"
							+ "&MD5info=" + MD5info_ +

							"&dateTime=" + dateTime + "&ip=" + ip
							+ "&firstname=" + this.firstname + "&lastname="
							+ this.lastname + "&phone=" + this.phone
							+ "&zipcode=" + this.zipcode + "&address="
							+ this.address + "&city=" + this.city + "&state="
							+ this.state + "&country=" + this.country.substring(3, 5)
							+ "&products=" + buyerinformation +

							"&ReturnURL=" + ReturnURL;

					out.write(parte); // 这里组织提交信息
					out.flush();
					out.close();
					// 获取返回数据
					BufferedReader in = new BufferedReader(
							new InputStreamReader(connection.getInputStream()));
					String line = null;
					StringBuffer content = new StringBuffer();
					while ((line = in.readLine()) != null) {
						// line为返回值，这就可以判断是否成功、
						content.append(line);
						logger.info(content);
					}
					Map responseFields = createMapFromResponse(content
							.toString().trim());
					// 金额:
					// String PaymentOrderNo =
					// VcpUtil.null2unknown("PaymentOrderNo",responseFields);
					String Succeed = VcpUtil.null2unknown("Succeed",
							responseFields);
					String paymentmessage = VcpUtil.null2unknown("Result",
							responseFields);
					String billaddress = VcpUtil.null2unknown("BillingAddress",
							responseFields);
					if (Succeed.equals("88")) {
						// 支付成功
						this.message = "Payment Success!";
						this.responseCode = 88;
						MD5info = trade.getMerchantOrderNo()
								+ trade.getMoneyType() + ordercountValue
								+ responseCode + MD5key;
						md5Value = md5.getMD5ofStr(MD5info);
						trade.setTradeState("1"
								+ trade.getTradeState().substring(1,
										trade.getTradeState().length()));
						trade.setRemark(message);
						logger.info("交易成功返回:"+merchantOrderNo+"**"+tradeMoneyType+"**"+ordercount+"**"+responseCode+"**"+message+"**"+ReturnURL+"**"+md5Value);
						this.commonService.update(trade);

						// ///---------------给持卡人发送邮件-----------------------////
						if(!"4212".equals((trade.getOrderNo()).substring(0,4))){
							EmailInfo emailinfo = new EmailInfo();
							String mailinfo = emailinfo.getPaymentResultEmail(
									card.getEmail(),
									trade.getTradeAmount(),
									getStates().getCurrencyTypeByNo(
											trade.getMoneyType().intValue()),
									trade.getTradeUrl(), trade.getTradeTime(),
									billaddress, trade.getMerchantOrderNo(),
									trade.getOrderNo());
							
							
							try {
								// 发送邮件,如果发送失败插入数据库发送
								CCSendMail.setSendMail(card.getEmail(), mailinfo,
										"sfepay@sfepay.com");
								logger.info("邮件立马发出");
							} catch (Exception e) {
								// 往数据库插入等待发送邮件
								shopManagerService.addSendMessages(card.getEmail(),
										"sfepay@sfepay.com", mailinfo, "0");
								logger.info("邮件等待稍后发出");
								logger.info("*********************支付结果返回码***************************"+responseCode);
								return SUCCESS;
							}
						}	
						logger.info("*********************支付结果返回码***************************"+responseCode);
						return SUCCESS;
					} else if (Succeed.equals("19")) {
						// 支付成功
						this.responseCode = 19;

						MD5info = trade.getMerchantOrderNo()
								+ trade.getMoneyType() + ordercountValue
								+ responseCode + MD5key;
						md5Value = md5.getMD5ofStr(MD5info);
						trade.setTradeState("2"
								+ trade.getTradeState().substring(1,
										trade.getTradeState().length()));
						trade.setRemark(message);
						this.commonService.update(trade);
						logger.info("*********************支付结果返回码***************************"+responseCode);
						return SUCCESS;
					} else {
						this.message = "Payment Declined!" + Succeed;
						this.responseCode = 0;
						MD5info = trade.getMerchantOrderNo()
								+ trade.getMoneyType() + ordercountValue
								+ responseCode + MD5key;
						md5Value = md5.getMD5ofStr(MD5info);
						trade.setTradeState("0"
								+ trade.getTradeState().substring(1,
										trade.getTradeState().length()));
						trade.setRemark(paymentmessage);
						this.commonService.update(trade);
						logger.info("*********************支付结果返回码***************************"+responseCode);
						return SUCCESS;
					}

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					message = "Your payment is being processed";
					responseCode = 0;
					logger.info("返回状态码+++++++++" + responseCode);
					MD5info = trade.getMerchantOrderNo() + trade.getMoneyType()
							+ ordercountValue + responseCode + MD5key;
					md5Value = md5.getMD5ofStr(MD5info);
					trade.setTradeState("0"
							+ trade.getTradeState().substring(1,
									trade.getTradeState().length()));
					this.commonService.update(trade);
					
					try {
						CCSendMail.setSendMail("89610614@qq.com",
								"sfepay can not connect to ecpss",
								"sfepay@sfepay.com");
					} catch (Exception ea) {
						logger.info("*********************支付结果返回码***************************"+responseCode);
						return SUCCESS;
					}
					logger.info("*********************支付结果返回码***************************"+responseCode);
					return SUCCESS;
				}

			} else if (chnals.equals("VIP")) {
				// remark = "VIP待处理";
				card.setCvv2(AES.setCarNo(cvv2));
				card.setExpiryDate(AES.setCarNo(expirydate));
				this.commonService.update(card);
				message = "Your payment is being processed";
				responseCode = 19;
				logger.info("返回状态码+++++++++" + responseCode);
				MD5info = trade.getMerchantOrderNo() + trade.getMoneyType()
						+ ordercountValue + responseCode + MD5key;
				md5Value = md5.getMD5ofStr(MD5info);
				trade.setTradeState("2"
						+ trade.getTradeState().substring(1,
								trade.getTradeState().length()));
				this.commonService.update(trade);
				logger.info("*********************支付结果返回码***************************"+responseCode);
				return SUCCESS;

			} else if (chnals.equals("VC")) {

			} else if (chnals.equals("VVIP")) {

			} else if (chnals.equals("MC")) {

			}else if (chnals.equals("V5")) {
				TradUtil tu= new TradUtil();
				TradeMessage tm=new TradeMessage();
				//卡号
				tm.setCardNo(cardnum);
				//CVV
				tm.setCvv(cvv2);
				//卡号有效期年
				tm.setExpirationYear("20"+year);
				//卡号有效期月
				tm.setExpirationMonth(month);
				tm.setPayNumber("0");
				tm.setMerNo("10000");
				tm.setShopName("迁易通");
				tm.setOrderNo(trade.getOrderNo());
				tm.setAmount(trade.getRmbAmount()+"");
				tm.setCurrency("CNY");
				tm.setGoodsName("物品名称");
				tm.setGoodsPrice(trade.getRmbAmount()+"");
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
//				tm.setShipCity(this.shippingCity);
//				tm.setShipState(this.shippingSstate);
				tm.setShipCountry(this.shippingCountry.substring(3, 5));
				tm.setShipZip(this.shippingZipcode);
				tm.setReturnURL("www.baidu.com");
				tm.setLanguage("");
				tm.setRemark("qianyitong");

				
				String mdfind=tm.getMerNo()+"LzVZD7"+tm.getOrderNo()+tm.getAmount()+tm.getCurrency()+tm.getEmail()+tm.getReturnURL();
				
				String md5info = StringUtil.Md5(mdfind);
				String Agent="";
				String userbrowser="";
				try{
				Agent = request.getHeader("User-Agent");  				
				StringTokenizer st = new StringTokenizer(Agent,";");  
				st.nextToken();  
				//得到用户的浏览器名  
				userbrowser = st.nextToken();
				}catch(Exception e){
					logger.error(e);
					userbrowser=Agent;
				}
				if(StringUtils.isBlank(userbrowser)){
					userbrowser="MSIE 10.0";
				}
				tm.setMd5Info(md5info);
				String ipstr[]=ip.split(",");
				if(ipstr.length>1){
					tm.setPayIp(ipstr[0]);
				}else{
				tm.setPayIp(this.ip);
				}
				tm.setAcceptLanguage("zh-CN");
				tm.setUserAgent(userbrowser);
				tu.get(tm);				
				
				if(tm.getSucceed().equals("0")){

					this.message = "Payment Declined!" + tm.getErrorCode();
					this.responseCode = 0;
					MD5info = trade.getMerchantOrderNo()
							+ trade.getMoneyType() + ordercountValue
							+ responseCode + MD5key;
					md5Value = md5.getMD5ofStr(MD5info);
					trade.setTradeState("0"
							+ trade.getTradeState().substring(1,
									trade.getTradeState().length()));
					if("1100".equals(tm.getErrorCode())){
						trade.setRemark("10分钟之内请不要重复提交！"+tm.getErrorCode());
					}
					else if("0927".equals(tm.getErrorCode())){
						trade.setRemark("卡信息验证失败，请联系发卡行！"+tm.getErrorCode());
					}
					else if("0931".equals(tm.getErrorCode())){
						trade.setRemark("请联系发卡行！"+tm.getErrorCode());
					}
					else if("1078".equals(tm.getErrorCode())){
						trade.setRemark("cvv错误！"+tm.getErrorCode());
					}else if("03".equals(tm.getErrorCode())){
						logger.info("无效商户，开始添加重跑数据。。。。");
						shopManagerService.addTemporaryTradInfo(rorderno, year, month,cvv2,country,MD5key, ip,"MSIE 10.0","v5_03");
					}else if("sfe01".equals(tm.getErrorCode())){
						trade.setRemark("请求银行超时，请重新支付！");
						logger.info("请求银行超时，请重新支付！");
						trade.setTradeState("3"
								+ trade.getTradeState().substring(1,
										trade.getTradeState().length()));
						shopManagerService.addTemporaryTradInfo(rorderno, year, month,cvv2,country,MD5key, ip,"MSIE 10.0","超时");
					}else{
						trade.setRemark(tm.getErrorCode()+tm.getErrorMsg()+tm.getBankInfo());
					}
					this.commonService.update(trade);
					logger.info("*********************支付结果返回码***************************"+responseCode);
					return SUCCESS;
									
				}else if(tm.getSucceed().equals("1")){

					// 支付成功
					this.message = "Payment Success!";
					this.responseCode = 88;
					MD5info = trade.getMerchantOrderNo()
							+ trade.getMoneyType() + ordercountValue
							+ responseCode + MD5key;
					md5Value = md5.getMD5ofStr(MD5info);
					trade.setTradeState("1"
							+ trade.getTradeState().substring(1,
									trade.getTradeState().length()));
					trade.setRemark(message);
					logger.info("交易成功返回:"+merchantOrderNo+"**"+tradeMoneyType+"**"+ordercount+"**"+responseCode+"**"+message+"**"+ReturnURL+"**"+md5Value);
					this.commonService.update(trade);

					// ///---------------给持卡人发送邮件-----------------------////
					
					if(!"4212".equals((trade.getOrderNo()).substring(0,4))){
						EmailInfo emailinfo = new EmailInfo();
						String mailinfo = emailinfo.getPaymentResultEmail(
								card.getEmail(),
								trade.getTradeAmount(),
								getStates().getCurrencyTypeByNo(
										trade.getMoneyType().intValue()),
								trade.getTradeUrl(), trade.getTradeTime(),
								it.get(0).getBillingAddress(), trade.getMerchantOrderNo(),
								trade.getOrderNo());
						
						
						try {
							// 发送邮件,如果发送失败插入数据库发送
							CCSendMail.setSendMail(card.getEmail(), mailinfo,
									"sfepay@sfepay.com");
							logger.info("邮件立马发出");
						} catch (Exception e) {
							// 往数据库插入等待发送邮件
							shopManagerService.addSendMessages(card.getEmail(),
									"sfepay@sfepay.com", mailinfo, "0");
							logger.info("邮件等待稍后发出");
							logger.info("*********************支付结果返回码***************************"+responseCode);
							return SUCCESS;
						}
					
					}
					logger.info("*********************支付结果返回码***************************"+responseCode);
					return SUCCESS;					
					
				}else if(tm.getSucceed().equals("2")||tm.getSucceed().equals("3")){

					// 支付成功
					this.responseCode = 19;

					MD5info = trade.getMerchantOrderNo()
							+ trade.getMoneyType() + ordercountValue
							+ responseCode + MD5key;
					md5Value = md5.getMD5ofStr(MD5info);
					trade.setTradeState("2"
							+ trade.getTradeState().substring(1,
									trade.getTradeState().length()));
					trade.setRemark(message);
					this.commonService.update(trade);
					logger.info("*********************支付结果返回码***************************"+responseCode);
					return SUCCESS;
				
				}				
			}else if (chnals.equals("CA")) {
				CaibaoUtil tu= new CaibaoUtil();
				CaibaoMessage ms=new CaibaoMessage();
				ms.setMerNo("20668");
				ms.setGatewayNo(posMerchantNo);
				ms.setOrderNo(trade.getOrderNo());
				ms.setOrderCurrency("CNY");
				ms.setOrderAmount(trade.getRmbAmount()+"");
				ms.setReturnURL(trade.getTradeUrl());
				ms.setCardNo(cardnum);
				ms.setCardExpireYear("20"+year);
				if(month.length()<2){
					month="0"+month;
				}
				ms.setCardExpireMonth(month);
				ms.setCardSecurityCode(cvv2);
				if(StringUtils.isBlank(cardbank)||cardbank.length()<2){
					cardbank="test";
				}
				ms.setIssuingBank(cardbank);
				String ipstr[]=ip.split(",");
				if(ipstr.length>1){
					ms.setIp(ipstr[0]);
				}else{
					ms.setIp(ip);
				}
				ms.setEmail(this.email);
				ms.setPaymentMethod("Credit Card");
				if(StringUtils.isBlank(phone)){
					phone="021-12345678";
				}
				ms.setPhone(this.phone);
				ms.setFirstName(this.firstname.replace("'",""));
				ms.setLastName(this.lastname.replace("'",""));
				ms.setCountry(this.country.substring(0, 3));
				ms.setState(this.state);
				ms.setCity(this.city);
				ms.setAddress(this.address);
				if(StringUtils.isBlank(zipcode)){
					zipcode="123456";
				}
				ms.setZip(this.zipcode);
				ms.setIsAuthor("");
				ms.setRemark("");
				String sign=ms.getMerNo().trim()+ms.getGatewayNo().trim()+ms.getOrderNo().trim()+ms.getOrderCurrency().trim()+ms.getOrderAmount().trim()+ms.getFirstName().trim()+ms.getLastName().trim() + ms.getCardNo().trim() + ms.getCardExpireYear().trim()+ms.getCardExpireMonth().trim()+ms.getCardSecurityCode().trim()+ ms.getEmail().trim() + posNumber.trim();
				String strDes = getSha256(sign); 
				ms.setTcsid(trade.getCsid());
				ms.setSignInfo(strDes);
				tu.get(ms);
				if(ms.getRes_orderStatus().equals("0")){
					if("sfe01".equals(ms.getRes_orderInfo())){
						this.responseCode = 19;

						MD5info = trade.getMerchantOrderNo()
								+ trade.getMoneyType() + ordercountValue
								+ responseCode + MD5key;
						md5Value = md5.getMD5ofStr(MD5info);
						trade.setTradeState("2"
								+ trade.getTradeState().substring(1,
										trade.getTradeState().length()));
						trade.setRemark("timeOut!");
						this.commonService.update(trade);
						shopManagerService.addTemporaryTradInfo(trade.getOrderNo(), year, month,cvv2,country,MD5key, ip,"MSIE 10.0","timeOut!");
						logger.info("*********************支付结果返回码***************************"+responseCode);
						return SUCCESS;
						
					}else if(bankBackRemark.toLowerCase().indexOf(ms.getRes_orderInfo().toLowerCase())>=0){
						//"Gateway No.is disabled".equals(ms.getRes_orderInfo())||"Failed".equals(ms.getRes_orderInfo())||"Declined".equals(ms.getRes_orderInfo())
						this.responseCode = 19;

						MD5info = trade.getMerchantOrderNo()
								+ trade.getMoneyType() + ordercountValue
								+ responseCode + MD5key;
						md5Value = md5.getMD5ofStr(MD5info);
						trade.setTradeState("2"
								+ trade.getTradeState().substring(1,
										trade.getTradeState().length()));
						trade.setRemark("Waiting processing!");
						this.commonService.update(trade);
						shopManagerService.addTemporaryTradInfo(trade.getOrderNo(), year, month,cvv2,country,MD5key, ip,"MSIE 10.0","CA"+ms.getRes_orderInfo());
						logger.info("*********************支付结果返回码***************************"+responseCode);
						return SUCCESS;
					}else{
					this.message = "Payment Declined!" + ms.getRes_orderInfo();
					this.responseCode = 0;
					MD5info = trade.getMerchantOrderNo()
							+ trade.getMoneyType() + ordercountValue
							+ responseCode + MD5key;
					md5Value = md5.getMD5ofStr(MD5info);
					trade.setTradeState("0"
							+ trade.getTradeState().substring(1,
									trade.getTradeState().length()));
					trade.setRemark(message);
					this.commonService.update(trade);
					logger.info("*********************支付结果返回码***************************"+responseCode);
					return SUCCESS;
					}
									
				}else if(ms.getRes_orderStatus().equals("1")){

					// 支付成功
					this.message = "Payment Success!";
					this.responseCode = 88;
					MD5info = trade.getMerchantOrderNo()
							+ trade.getMoneyType() + ordercountValue
							+ responseCode + MD5key;
					md5Value = md5.getMD5ofStr(MD5info);
					trade.setTradeState("1"
							+ trade.getTradeState().substring(1,
									trade.getTradeState().length()));
					trade.setRemark(message);
					trade.setVIPBatchNo(ms.getRes_tradeNo());
					logger.info("交易成功返回:"+merchantOrderNo+"**"+tradeMoneyType+"**"+ordercount+"**"+responseCode+"**"+message+"**"+ReturnURL+"**"+md5Value);
					this.commonService.update(trade);

					// ///---------------给持卡人发送邮件-----------------------////
					if(!"4212".equals((trade.getOrderNo()).substring(0,4))){
						EmailInfo emailinfo = new EmailInfo();
						String mailinfo = emailinfo.getPaymentResultEmail(
								card.getEmail(),
								trade.getTradeAmount(),
								getStates().getCurrencyTypeByNo(
										trade.getMoneyType().intValue()),
								trade.getTradeUrl(), trade.getTradeTime(),
								ms.getRes_billAddress(), trade.getMerchantOrderNo(),
								trade.getOrderNo());
						
						
						try {
							// 发送邮件,如果发送失败插入数据库发送
							CCSendMail.setSendMail(card.getEmail(), mailinfo,
									"sfepay@sfepay.com");
							logger.info("邮件立马发出");
						} catch (Exception e) {
							// 往数据库插入等待发送邮件
							shopManagerService.addSendMessages(card.getEmail(),
									"sfepay@sfepay.com", mailinfo, "0");
							logger.info("邮件等待稍后发出");
							logger.info("*********************支付结果返回码***************************"+responseCode);
							return SUCCESS;
						}
					}
					logger.info("*********************支付结果返回码***************************"+responseCode);
					return SUCCESS;
									
					
					
				}else if(ms.getRes_orderStatus().equals("-2")||ms.getRes_orderStatus().equals("-1")){

					// 等待银行处理
					this.responseCode = 19;

					MD5info = trade.getMerchantOrderNo()
							+ trade.getMoneyType() + ordercountValue
							+ responseCode + MD5key;
					md5Value = md5.getMD5ofStr(MD5info);
					trade.setTradeState("2"
							+ trade.getTradeState().substring(1,
									trade.getTradeState().length()));
					trade.setRemark(message);
					this.commonService.update(trade);
					logger.info("*********************支付结果返回码***************************"+responseCode);
					return SUCCESS;
				
				}
				
			} 
			//测试同道
			else if(chnals.equals("TC"))
			{
				this.message = "Payment Success!";
				this.responseCode = 88;
				MD5info = trade.getMerchantOrderNo()
						+ trade.getMoneyType() + ordercountValue
						+ responseCode + MD5key;
				md5Value = md5.getMD5ofStr(MD5info);
				trade.setTradeState("1"
						+ trade.getTradeState().substring(1,
								trade.getTradeState().length()));
				trade.setRemark(message);
				logger.info("交易成功返回:"+merchantOrderNo+"**"+tradeMoneyType+"**"+ordercount+"**"+responseCode+"**"+message+"**"+ReturnURL+"**"+md5Value);
				this.commonService.update(trade);

				// ///---------------给持卡人发送邮件-----------------------////
				
				if(!"4212".equals((trade.getOrderNo()).substring(0,4))){
					EmailInfo emailinfo = new EmailInfo();
					String mailinfo = emailinfo.getPaymentResultEmail(
							card.getEmail(),
							trade.getTradeAmount(),
							getStates().getCurrencyTypeByNo(
									trade.getMoneyType().intValue()),
							trade.getTradeUrl(), trade.getTradeTime(),
							it.get(0).getBillingAddress(), trade.getMerchantOrderNo(),
							trade.getOrderNo());
					
					
					try {
						// 发送邮件,如果发送失败插入数据库发送
						CCSendMail.setSendMail(card.getEmail(), mailinfo,
								"sfepay@sfepay.com");
						logger.info("邮件立马发出");
					} catch (Exception e) {
						// 往数据库插入等待发送邮件
						shopManagerService.addSendMessages(card.getEmail(),
								"sfepay@sfepay.com", mailinfo, "0");
						logger.info("邮件等待稍后发出");
						logger.info("*********************支付结果返回码***************************"+responseCode);
						return SUCCESS;
					}
				}
				logger.info("*********************支付结果返回码***************************"+responseCode);
				return SUCCESS;
			} 
			else if (chnals.equals("BOD-S")) {

				vpn.DCCMessage dcc = new vpn.DCCMessage();
				dcc.setTrans_Type("enqrate");// 类型
				dcc.setMerchant_Id(posMerchantNo);// 42 商户编号
				dcc.setAuthor_Str(it.get(0).getAuthcode());
				dcc.setTerminal_Id(posNumber);// 41 商户终端号
				dcc.setInvoice_No(trade.getOrderNo().substring(
						trade.getOrderNo().length() - 6,
						trade.getOrderNo().length()));

				// 有效期
				dcc.setOrder_No(trade.getOrderNo());// 62
				dcc.setCustom(trade.getOrderNo());
                dcc.setBocs_ReturnURL("http://172.20.66.2/sfe");
				dcc.setHashCode(it.get(0).getHashcode());
				dcc.setCurrency_Code_T("156");// 货币代码
				dcc.setAmount_Loc(this.buzero(trade.getRmbAmount() + ""));// 4
				// 本地交易金额
				dcc.setCard_No(cardnum);// 账号2
				dcc.setExp_Date(year + month);// 14
				VpnUtil3 vu = new VpnUtil3();
				Long tim1 = System.currentTimeMillis();
				try {
					// type 1 汇率查询
					 dcc = vu.getDCCvalue(dcc, "1");
//					dcc.setResp_Code("99YY");
				} catch (Exception e) {
					
				this.message = "Payment Declined!"+"1111" ;
				this.responseCode = 0;
				MD5info = trade.getMerchantOrderNo()
						+ trade.getMoneyType() + ordercountValue
						+ responseCode + MD5key;
				md5Value = md5.getMD5ofStr(MD5info);
				trade.setTradeState("0"
						+ trade.getTradeState().substring(1,
								trade.getTradeState().length()));
				trade.setRemark(this.message);
				this.commonService.update(trade);
				logger.info("*********************支付结果返回码***************************"+responseCode);
				return SUCCESS;}
				if (dcc.getResp_Code().equals("99YY")) {
					// 交易
					vpn.DCCMessage dcc2 = new vpn.DCCMessage();
					dcc2.setTrans_Type("dccsales");
					dcc2.setTrans_Model("E");
					dcc2.setMerchant_Id(posMerchantNo);// 42 商户编号
					dcc2.setTerminal_Id(posNumber);// 41 商户终端号
					
//					dcc3.setMerchant_Id("021817326000001");
//					dcc3.setTerminal_Id("07882349");

					dcc2.setAuthor_Str(it.get(0).getAuthcode());
					dcc2.setInvoice_No(trade.getOrderNo().substring(
							trade.getOrderNo().length() - 6,
							trade.getOrderNo().length()));
					dcc2.setCurrency_Code_T("156");
					dcc2.setAmount_Loc(this.buzero(trade.getRmbAmount() + ""));// 4
					dcc2.setCSC(cvv2);
					dcc2.setCard_No(cardnum);// 账号2
					dcc2.setExp_Date(year + month);// 14
					dcc2.setOrder_No(trade.getOrderNo());// 62
					dcc2.setCustom(trade.getOrderNo());
					dcc2.setCurrency_Code(dcc.getCurrency_Code());
					dcc2.setAmount_For(dcc.getAmount_For());
					dcc2.setBocs_ReturnURL("https://www.sfepay.com/Pays/payResponseBOD");
					dcc2.setEnd_ReturnURL("https://www.sfepay.com/Pays/payResponseBODAction");
					
					dcc2.setHashCode(it.get(0).getHashcode());    
 					VpnUtil2 vpn = new VpnUtil2();

					Long tim2 = System.currentTimeMillis();
					try {
						dcc = vpn.getDCCvalue(dcc2, "13");
					} catch (Exception e) {
						
						this.message = "Payment Declined!"+"1112" ;
						this.responseCode = 0;
						MD5info = trade.getMerchantOrderNo()
								+ trade.getMoneyType() + ordercountValue
								+ responseCode + MD5key;
						md5Value = md5.getMD5ofStr(MD5info);
						trade.setTradeState("0"
								+ trade.getTradeState().substring(1,
										trade.getTradeState().length()));
						trade.setRemark(this.message );
						this.commonService.update(trade);
						logger.info("*********************支付结果返回码***************************"+responseCode);
						return SUCCESS;}
					if (dcc.getResp_Code().equals("1000")) {
						logger.info("444444444444444444444444444"+"fanhui11111111");

						trade.setToken_id(dcc.getToken_id().trim());
						trade.setTradeState("5"
								+ trade.getTradeState().substring(1,
										trade.getTradeState().length()));
						this.commonService.update(trade);
					
						this.ReturnURL=dcc.getSend_Url();
						this.token_id=dcc.getToken_id();
						logger.info("333333333333333333333333"+"fanhui11111111");
//
//						StringBuffer buf = new StringBuffer();
//						buf.append(SECURE_SECRET);
						StringBuffer buf = new StringBuffer();
						buf.append(it.get(0).getHashcode());
						buf.append(this.token_id);
						
						MessageDigest md52 = null;
						byte[] ba = (byte[]) null;
						try {
							md52 = MessageDigest.getInstance("SHA-256");
							ba = md52.digest(buf.toString().getBytes("ISO-8859-1"));
						} catch (Exception localException) {
						}
						
						char[] HEX_TABLE = new char[] { '0', '1', '2', '3', '4', '5',
								'6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
						
						StringBuffer sb2 = new StringBuffer(ba.length * 2);
						for (int i = 0; i < ba.length; i++) {
							sb2.append(HEX_TABLE[(ba[i] >> 4) & 0xf]);
							sb2.append(HEX_TABLE[ba[i] & 0xf]);
						}
						logger.info("1111111111111111111111111111"+buf+"fanhui11111111");
						
						
						this.HASH=sb2.toString();	
						logger.info("222222222222222222222222222222"+"fanhui11111111");

 						return "bodreturn";
						
					} else {
						this.message = "Payment Declined!";
						this.responseCode = 0;

						trade.setTradeState("0"
								+ trade.getTradeState().substring(1,
										trade.getTradeState().length()));
						trade.setRemark(message);
						trade.setVIPDisposePorson("System");
						trade.setVIPDisposeDate(new Date());
						this.commonService.update(trade);
						MD5info = trade.getMerchantOrderNo()
								+ trade.getMoneyType() + ordercountValue
								+ responseCode + MD5key;
						md5Value = md5.getMD5ofStr(MD5info);
						logger.info("*********************支付结果返回码***************************"+responseCode);
						return SUCCESS;
					}

				} else if (dcc.getResp_Code().equals("99YX")) {
					List<InternationalTerminalManager> it2 = this.commonService
							.list(" from InternationalTerminalManager where andterminalNo='"+it.get(0).getTerminalNo()+"'  ");
					
					
					// 交易
					vpn.DCCMessage dcc2 = new vpn.DCCMessage();
					dcc2.setTrans_Type("sales");
					dcc2.setTrans_Model("E");
					dcc2.setMerchant_Id(it2.get(0).getMerchantNo());// 42 商户编号
					dcc2.setTerminal_Id(it2.get(0).getTerminalNo());// 41 商户终端号
					
//					dcc3.setMerchant_Id("021817326000001");
//					dcc3.setTerminal_Id("07882349");

					dcc2.setAuthor_Str(it2.get(0).getAuthcode());
					dcc2.setInvoice_No(trade.getOrderNo().substring(
							trade.getOrderNo().length() - 6,
							trade.getOrderNo().length()));
					dcc2.setCurrency_Code_T("156");
					dcc2.setAmount_Loc(this.buzero(trade.getRmbAmount() + ""));// 4
					dcc2.setCSC(cvv2);
					dcc2.setCard_No(cardnum);// 账号2
					dcc2.setExp_Date(year + month);// 14
					dcc2.setOrder_No(trade.getOrderNo());// 62
					dcc2.setCustom(trade.getOrderNo());

					dcc2.setBocs_ReturnURL("https://www.sfepay.com/Pays/payResponseBOD");
					dcc2.setEnd_ReturnURL("https://www.sfepay.com/Pays/payResponseBODAction");
					
					dcc2.setHashCode(it2.get(0).getHashcode());    
 					VpnUtil2 vpn = new VpnUtil2();

					Long tim2 = System.currentTimeMillis();
					try {
						dcc = vpn.getDCCvalue(dcc2, "12");
					} catch (Exception e) {
						
						this.message = "Payment Declined!"+"1112" ;
						this.responseCode = 0;
						MD5info = trade.getMerchantOrderNo()
								+ trade.getMoneyType() + ordercountValue
								+ responseCode + MD5key;
						md5Value = md5.getMD5ofStr(MD5info);
						trade.setTradeState("0"
								+ trade.getTradeState().substring(1,
										trade.getTradeState().length()));
						trade.setRemark(this.message );
						this.commonService.update(trade);
						logger.info("*********************支付结果返回码***************************"+responseCode);
						return SUCCESS;}
					if (dcc.getResp_Code().equals("1000")) {
						logger.info("55555555555555555555"+"fanhui11111111");

						trade.setTradeState("5"
								+ trade.getTradeState().substring(1,
										trade.getTradeState().length()));
						trade.setToken_id(dcc.getToken_id().trim());
						this.commonService.update(trade);
						this.ReturnURL=dcc.getSend_Url();
						this.token_id=dcc.getToken_id();
						logger.info("666666666666666666666"+"fanhui11111111");

						StringBuffer buf = new StringBuffer();
						buf.append(it.get(0).getHashcode());
						buf.append(this.token_id);
						MessageDigest md52 = null;
						byte[] ba = (byte[]) null;
						try {
							md52 = MessageDigest.getInstance("SHA-256");
							ba = md52.digest(buf.toString().getBytes("ISO-8859-1"));
						} catch (Exception localException) {
						}
						
						char[] HEX_TABLE = new char[] { '0', '1', '2', '3', '4', '5',
								'6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
						logger.info("77777777777777777777777"+"fanhui11111111");

						StringBuffer sb2 = new StringBuffer(ba.length * 2);
						for (int i = 0; i < ba.length; i++) {
							sb2.append(HEX_TABLE[(ba[i] >> 4) & 0xf]);
							sb2.append(HEX_TABLE[ba[i] & 0xf]);
						}
						
						logger.info("88888888888888888"+"fanhui11111111");

						
						this.HASH=sb2.toString();
						return "bodreturn";
						
					} else {
						this.message = "Payment Declined!";
						this.responseCode = 0;

						trade.setTradeState("0"
								+ trade.getTradeState().substring(1,
										trade.getTradeState().length()));
						trade.setRemark(message);
						trade.setVIPDisposePorson("System");
						trade.setVIPDisposeDate(new Date());
						this.commonService.update(trade);
						MD5info = trade.getMerchantOrderNo()
								+ trade.getMoneyType() + ordercountValue
								+ responseCode + MD5key;
						md5Value = md5.getMD5ofStr(MD5info);
						logger.info("*********************支付结果返回码***************************"+responseCode);
						return SUCCESS;
					}

				}
			}else if (chnals.equals("BOD")) {
				vpn.DCCMessage dcc = new vpn.DCCMessage();
				dcc.setTrans_Type("enqrate");// 类型
				dcc.setMerchant_Id(posMerchantNo);// 42 商户编号
				dcc.setAuthor_Str(it.get(0).getAuthcode());
				dcc.setTerminal_Id(posNumber);// 41 商户终端号
				dcc.setInvoice_No(trade.getOrderNo().substring(
						trade.getOrderNo().length() - 6,
						trade.getOrderNo().length()));

				// 有效期
				dcc.setOrder_No(trade.getOrderNo());// 62
				dcc.setCustom(trade.getOrderNo());
                dcc.setBocs_ReturnURL("http://172.20.66.2/sfe");
				dcc.setHashCode(it.get(0).getHashcode());
				dcc.setCurrency_Code_T("156");// 货币代码
				dcc.setAmount_Loc(this.buzero(trade.getRmbAmount() + ""));// 4
				// 本地交易金额
				dcc.setCard_No(cardnum);// 账号2
				dcc.setExp_Date(year + month);// 14
				VpnUtil3 vu = new VpnUtil3();
				Long tim1 = System.currentTimeMillis();
				try {
					// type 1 汇率查询
					 dcc = vu.getDCCvalue(dcc, "1");
//					dcc.setResp_Code("99YY");
				} catch (Exception e) {
				logger.info("******查汇错误*****"+e);
				this.message = "Payment Declined!"+"1111" ;
				this.responseCode = 0;
				MD5info = trade.getMerchantOrderNo()
						+ trade.getMoneyType() + ordercountValue
						+ responseCode + MD5key;
				md5Value = md5.getMD5ofStr(MD5info);
				trade.setTradeState("0"
						+ trade.getTradeState().substring(1,
								trade.getTradeState().length()));
				trade.setRemark(this.message);
				this.commonService.update(trade);
				logger.info("*********************支付结果返回码***************************"+responseCode);
				return SUCCESS;}
				//
				if (dcc.getResp_Code().equals("99YY")) {//支持此卡DCC交易
					// 交易
					vpn.DCCMessage dcc2 = new vpn.DCCMessage();
					dcc2.setTrans_Type("dccsales");
					dcc2.setTrans_Model("E");//E 3D交易 M moto交易
					dcc2.setMerchant_Id(posMerchantNo);// 42 商户编号
					dcc2.setTerminal_Id(posNumber);// 41 商户终端号
					
//					dcc3.setMerchant_Id("021817326000001");
//					dcc3.setTerminal_Id("07882349");

					dcc2.setAuthor_Str(it.get(0).getAuthcode());
					dcc2.setInvoice_No(trade.getOrderNo().substring(
							trade.getOrderNo().length() - 6,
							trade.getOrderNo().length()));
					dcc2.setCurrency_Code_T("156");
					dcc2.setAmount_Loc(this.buzero(trade.getRmbAmount() + ""));// 4
					dcc2.setCSC(cvv2);
					dcc2.setCard_No(cardnum);// 账号2
					dcc2.setExp_Date(year + month);// 14
					dcc2.setOrder_No(trade.getOrderNo());// 62
					dcc2.setCustom(trade.getOrderNo());
					dcc2.setCurrency_Code(dcc.getCurrency_Code());
					dcc2.setAmount_For(dcc.getAmount_For());
					dcc2.setBocs_ReturnURL("https://www.sfepay.com/Pays/payResponseBOD");
					dcc2.setEnd_ReturnURL("https://www.sfepay.com/Pays/payResponseBODAction");
					
					dcc2.setHashCode(it.get(0).getHashcode());    
 					VpnUtil2 vpn = new VpnUtil2();

					Long tim2 = System.currentTimeMillis();
					try {
						dcc = vpn.getDCCvalue(dcc2, "13");
					} catch (Exception e) {
						
						this.message = "Payment Declined!"+"1112" ;
						this.responseCode = 0;
						MD5info = trade.getMerchantOrderNo()
								+ trade.getMoneyType() + ordercountValue
								+ responseCode + MD5key;
						md5Value = md5.getMD5ofStr(MD5info);
						trade.setTradeState("0"
								+ trade.getTradeState().substring(1,
										trade.getTradeState().length()));
						trade.setRemark(this.message );
						this.commonService.update(trade);
						logger.info("*********************支付结果返回码***************************"+responseCode);
						return SUCCESS;}
					if (dcc.getResp_Code().equals("1000")) {//交易处理中
						logger.info("444444444444444444444444444"+"fanhui11111111");

						trade.setToken_id(dcc.getToken_id().trim());
						trade.setTradeState("5"
								+ trade.getTradeState().substring(1,
										trade.getTradeState().length()));
						this.commonService.update(trade);
					
						this.ReturnURL=dcc.getSend_Url();
						this.token_id=dcc.getToken_id();
						logger.info("333333333333333333333333"+"fanhui11111111");
//
//						StringBuffer buf = new StringBuffer();
//						buf.append(SECURE_SECRET);
						StringBuffer buf = new StringBuffer();
						buf.append(it.get(0).getHashcode());
						buf.append(this.token_id);
						
						MessageDigest md52 = null;
						byte[] ba = (byte[]) null;
						try {
							md52 = MessageDigest.getInstance("SHA-256");
							ba = md52.digest(buf.toString().getBytes("ISO-8859-1"));
						} catch (Exception localException) {
						}
						
						char[] HEX_TABLE = new char[] { '0', '1', '2', '3', '4', '5',
								'6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
						
						StringBuffer sb2 = new StringBuffer(ba.length * 2);
						for (int i = 0; i < ba.length; i++) {
							sb2.append(HEX_TABLE[(ba[i] >> 4) & 0xf]);
							sb2.append(HEX_TABLE[ba[i] & 0xf]);
						}
						logger.info("1111111111111111111111111111"+buf+"fanhui11111111");
						
						
						this.HASH=sb2.toString();	
						logger.info("222222222222222222222222222222"+"fanhui11111111");

 						return "bodreturn";
						
					} else {
						this.message = "Payment Declined!";
						this.responseCode = 0;

						trade.setTradeState("0"
								+ trade.getTradeState().substring(1,
										trade.getTradeState().length()));
						trade.setRemark(message);
						trade.setVIPDisposePorson("System");
						trade.setVIPDisposeDate(new Date());
						this.commonService.update(trade);
						MD5info = trade.getMerchantOrderNo()
								+ trade.getMoneyType() + ordercountValue
								+ responseCode + MD5key;
						md5Value = md5.getMD5ofStr(MD5info);
						logger.info("*********************支付结果返回码***************************"+responseCode);
						return SUCCESS;
					}

				} else if (dcc.getResp_Code().equals("99YX")) {//不支持DCC交易
					List<InternationalTerminalManager> it2 = this.commonService
							.list(" from InternationalTerminalManager where andterminalNo='"+it.get(0).getTerminalNo()+"'  ");
					
					
					// 交易
					vpn.DCCMessage dcc2 = new vpn.DCCMessage();
					dcc2.setTrans_Type("sales");
					dcc2.setTrans_Model("E");
					dcc2.setMerchant_Id(it2.get(0).getMerchantNo());// 42 商户编号
					dcc2.setTerminal_Id(it2.get(0).getTerminalNo());// 41 商户终端号
					
//					dcc3.setMerchant_Id("021817326000001");
//					dcc3.setTerminal_Id("07882349");

					dcc2.setAuthor_Str(it2.get(0).getAuthcode());
					dcc2.setInvoice_No(trade.getOrderNo().substring(
							trade.getOrderNo().length() - 6,
							trade.getOrderNo().length()));
					dcc2.setCurrency_Code_T("156");
					dcc2.setAmount_Loc(this.buzero(trade.getRmbAmount() + ""));// 4
					dcc2.setCSC(cvv2);
					dcc2.setCard_No(cardnum);// 账号2
					dcc2.setExp_Date(year + month);// 14
					dcc2.setOrder_No(trade.getOrderNo());// 62
					dcc2.setCustom(trade.getOrderNo());

					dcc2.setBocs_ReturnURL("https://www.sfepay.com/Pays/payResponseBOD");
					dcc2.setEnd_ReturnURL("https://www.sfepay.com/Pays/payResponseBODAction");
					
					dcc2.setHashCode(it2.get(0).getHashcode());    
 					VpnUtil2 vpn = new VpnUtil2();

					Long tim2 = System.currentTimeMillis();
					try {
						dcc = vpn.getDCCvalue(dcc2, "12");
					} catch (Exception e) {
						
						this.message = "Payment Declined!"+"1112" ;
						this.responseCode = 0;
						MD5info = trade.getMerchantOrderNo()
								+ trade.getMoneyType() + ordercountValue
								+ responseCode + MD5key;
						md5Value = md5.getMD5ofStr(MD5info);
						trade.setTradeState("0"
								+ trade.getTradeState().substring(1,
										trade.getTradeState().length()));
						trade.setRemark(this.message );
						this.commonService.update(trade);
						logger.info("*********************支付结果返回码***************************"+responseCode);
						return SUCCESS;}
					if (dcc.getResp_Code().equals("1000")) {//非DCC交易处理中
						logger.info("55555555555555555555"+"fanhui11111111");

						trade.setTradeState("5"
								+ trade.getTradeState().substring(1,
										trade.getTradeState().length()));
						trade.setToken_id(dcc.getToken_id().trim());
						this.commonService.update(trade);
						this.ReturnURL=dcc.getSend_Url();
						this.token_id=dcc.getToken_id();
						logger.info("666666666666666666666"+"fanhui11111111");

						StringBuffer buf = new StringBuffer();
						buf.append(it.get(0).getHashcode());
						buf.append(this.token_id);
						MessageDigest md52 = null;
						byte[] ba = (byte[]) null;
						try {
							md52 = MessageDigest.getInstance("SHA-256");
							ba = md52.digest(buf.toString().getBytes("ISO-8859-1"));
						} catch (Exception localException) {
						}
						
						char[] HEX_TABLE = new char[] { '0', '1', '2', '3', '4', '5',
								'6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
						logger.info("77777777777777777777777"+"fanhui11111111");

						StringBuffer sb2 = new StringBuffer(ba.length * 2);
						for (int i = 0; i < ba.length; i++) {
							sb2.append(HEX_TABLE[(ba[i] >> 4) & 0xf]);
							sb2.append(HEX_TABLE[ba[i] & 0xf]);
						}
						
						logger.info("88888888888888888"+"fanhui11111111");

						
						this.HASH=sb2.toString();
						return "bodreturn";
						
					} else {
						this.message = "Payment Declined!";
						this.responseCode = 0;

						trade.setTradeState("0"
								+ trade.getTradeState().substring(1,
										trade.getTradeState().length()));
						trade.setRemark(message);
						trade.setVIPDisposePorson("System");
						trade.setVIPDisposeDate(new Date());
						this.commonService.update(trade);
						MD5info = trade.getMerchantOrderNo()
								+ trade.getMoneyType() + ordercountValue
								+ responseCode + MD5key;
						md5Value = md5.getMD5ofStr(MD5info);
						logger.info("*********************支付结果返回码***************************"+responseCode);
						return SUCCESS;
					}

				}
			}else if (chnals.equals("VPN")) {//非3DMOTO交易
                logger.info("进入到VPN通道");
				vpn.DCCMessage dcc = new vpn.DCCMessage();
				dcc.setTrans_Type("enqrate");// 查询此卡是否支持DCC交易
				dcc.setMerchant_Id(posMerchantNo);// 42 商户编号
				dcc.setAuthor_Str(it.get(0).getAuthcode());
				dcc.setTerminal_Id(posNumber);// 41 商户终端号
				dcc.setInvoice_No(trade.getOrderNo().substring(
						trade.getOrderNo().length() - 6,
						trade.getOrderNo().length()));

				// 有效期
				dcc.setOrder_No(trade.getOrderNo());// 62
				dcc.setCustom(trade.getOrderNo());
				dcc.setHashCode(it.get(0).getHashcode());
				dcc.setCurrency_Code_T("156");// 货币代码 CNY
				dcc.setBocs_ReturnURL("http://172.20.66.2/sfe");
				dcc.setAmount_Loc(this.buzero(trade.getRmbAmount() + ""));// 4
				// 本地交易金额
				dcc.setCard_No(cardnum);// 账号2
				dcc.setExp_Date(year + month);// 14
				VpnUtil vu = new VpnUtil();
				Long tim1 = System.currentTimeMillis();
				try {
					// type 1 汇率查询
					 dcc = vu.getDCCvalue(dcc, "1");
//					dcc.setResp_Code("99YY");
				} catch (Exception e) {
					this.responseCode = 19;
					message = "Your payment is being processed";
					MD5info = trade.getMerchantOrderNo() + trade.getMoneyType()
							+ ordercountValue + responseCode + MD5key;
					md5Value = md5.getMD5ofStr(MD5info);

					trade.setTradeState("0"
							+ trade.getTradeState().substring(1,
									trade.getTradeState().length()));
					trade.setRemark(message);
					trade.setVIPDisposePorson("System");
					trade.setVIPDisposeDate(new Date());
					this.commonService.update(trade);
					logger.info("*********************支付结果返回码***************************"+responseCode);
					return SUCCESS;
				}
				logger.info("*********************DCC查汇返回码***************************"+dcc.getResp_Code());
				logger.info("DCC查汇返回码"+dcc.getResp_Code());
				if (dcc.getResp_Code().equals("99YY")){//此卡支持DCC交易
//					logger.info("MOTO DCC交易开始");
//					// 交易
//					vpn.MotoDCCMessage dcc2 = new vpn.MotoDCCMessage();
//					dcc2.setTrans_Type("risk");// 类型   中银风控
//					dcc2.setMerchant_Id(posMerchantNo);// 42 商户编号
//					dcc2.setAuthor_Str(it.get(0).getAuthcode());
//					dcc2.setTerminal_Id(posNumber);// 41 商户终端号
//					dcc2.setInvoice_No(trade.getOrderNo().substring(
//							trade.getOrderNo().length() - 6,
//							trade.getOrderNo().length()));
//
//					dcc2.setTrans_Model("M");//moto通道
//					dcc2.setCurrency_Code_T("156");// 货币代码
//					dcc2.setAmount_Loc(this.buzero(trade.getRmbAmount() + ""));// 4
//					// 本地交易金额
//					dcc2.setCard_No(cardnum);// 账号2
//					dcc2.setExp_Date(year + month);// 14 有效期
//					dcc2.setCSC(cvv2);
//					dcc2.setCurrency_Code(dcc.getCurrency_Code());
//					dcc2.setBocs_ReturnURL("http://172.20.66.2/sfe");
//					dcc2.setAmount_For(dcc.getAmount_For());
//					dcc2.setOrder_No(trade.getOrderNo());
//					dcc2.setCustom(trade.getOrderNo());
//					dcc2.setHashCode(it.get(0).getHashcode());
//					//新增风险交易参数
//					dcc2.setCUST_FNAME(firstname);
//					dcc2.setCUST_LNAME(lastname);
//					dcc2.setCUST_CITY(city);
//					dcc2.setCUST_ADDR1(address);
//					dcc2.setCUST_CNTRY_CD(country.substring(0, 3));
//					dcc2.setCUST_EMAIL(email);
//					dcc2.setCUST_IP_ADDR(ip);
////					dcc2.setCUST_HOME_PHONE(phone);
//					if(zipcode.replaceAll("-", "").length()>9){
//					dcc2.setCUST_POSTAL_CD(zipcode.replaceAll("-", "").substring(0, 9));
//					}else{
//					dcc2.setCUST_POSTAL_CD(zipcode.replaceAll("-", ""));
//					}
////					dcc2.setCUST_STPR_CD(state);
//					dcc2.setSHIP_FNAME(shippingFirstName);
//					dcc2.setSHIP_LNAME(shippingLastName);
//					dcc2.setSHIP_CITY(shippingCity);
//					dcc2.setSHIP_ADDR1(shippingAddress);
//					dcc2.setSHIP_CNTRY_CD(shippingCountry.substring(0, 3));
//					dcc2.setSHIP_EMAIL(shippingEmail);
//					dcc2.setSHIP_IP_ADDR(ip);
////					dcc2.setSHIP_HOME_PHONE(shippingPhone);
//					if(shippingZipcode.replaceAll("-", "").length()>9){
//					dcc2.setSHIP_POSTAL_CD(shippingZipcode.replaceAll("-", "").substring(0, 9));
//					}else{
//						dcc2.setSHIP_POSTAL_CD(shippingZipcode.replaceAll("-", ""));
//					}
////					dcc2.setSHIP_STPR_CD(shippingSstate);
//					VpnUtil_Moto vu2=new VpnUtil_Moto();
//					//VpnUtil vu2 = new VpnUtil();
//					Long tim2 = System.currentTimeMillis();
//					try {
//						// type 2 dcc交易
//						logger.info("开始moto风控");
//						 dcc2 = vu2.getDCCvalue(dcc2, "21");
//					} catch (Exception e) {
//						responseCode = 19;
//						message = "Your payment is being processed";
//						MD5info = trade.getMerchantOrderNo()
//								+ trade.getMoneyType() + ordercountValue
//								+ responseCode + MD5key;
//						md5Value = md5.getMD5ofStr(MD5info);
//
//						trade.setTradeState("0"
//								+ trade.getTradeState().substring(1,
//										trade.getTradeState().length()));
//						trade.setRemark(message);
//						trade.setVIPDisposePorson("System");
//						trade.setVIPDisposeDate(new Date());
//						this.commonService.update(trade);
//						logger.info("*********************支付结果返回码***************************"+responseCode);
//						return SUCCESS;
//					}
//					if (dcc2.getResp_Code().equals("5600")) {
						vpn.MotoDCCMessage moto = new vpn.MotoDCCMessage();
						moto.setTrans_Type("dccsales");// 类型
						moto.setMerchant_Id(posMerchantNo);// 42 商户编号
						moto.setAuthor_Str(it.get(0).getAuthcode());
						moto.setTerminal_Id(posNumber);// 41 商户终端号
						moto.setInvoice_No(trade.getOrderNo().substring(
								trade.getOrderNo().length() - 6,
								trade.getOrderNo().length()));

						moto.setTrans_Model("M");//moto通道
						moto.setCurrency_Code_T("156");// 货币代码
						moto.setAmount_Loc(this.buzero(trade.getRmbAmount() + ""));// 4
						// 本地交易金额
						moto.setCard_No(cardnum);// 账号2
						moto.setExp_Date(year + month);// 14 有效期
						moto.setCSC(cvv2);
						moto.setCurrency_Code(dcc.getCurrency_Code());
						moto.setBocs_ReturnURL("http://172.20.66.2/sfe");
						moto.setAmount_For(dcc.getAmount_For());
						moto.setOrder_No(trade.getOrderNo());
						moto.setCustom(trade.getOrderNo());
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
							MD5info = trade.getMerchantOrderNo()
									+ trade.getMoneyType() + ordercountValue
									+ responseCode + MD5key;
							md5Value = md5.getMD5ofStr(MD5info);

							trade.setTradeState("0"
									+ trade.getTradeState().substring(1,
											trade.getTradeState().length()));
							trade.setRemark(message);
							trade.setVIPDisposePorson("System");
							trade.setVIPDisposeDate(new Date());
							this.commonService.update(trade);
							logger.info("交易成功返回:"+merchantOrderNo+"**"+tradeMoneyType+"**"+ordercount+"**"+responseCode+"**"+message+"**"+ReturnURL+"**"+md5Value);
							logger.info("*********************支付结果返回码***************************"+responseCode);
							return SUCCESS;
						}
					if (moto.getResp_Code().equals("0000")) {//交易成功
						this.message = "Payment Success!";
						this.responseCode = 88;
						// 清除持卡人cvv,有效期
						// this.commonService
						// .deleteBySql("update  international_cardholdersinfo t  set t.cvv2='XXX',t.expiryDate='0000' where t.tradeId='"
						// + trade.getId() + "'");
						trade.setTradeState("1"
								+ trade.getTradeState().substring(1,
										trade.getTradeState().length()));
						trade.setRemark(message);
						trade.setVIPDisposePorson("System");
						trade.setVIPDisposeDate(new Date());
						trade.setVIPBatchNo(moto.getAuth_Code());
						trade.setVIPTerminalNo(posNumber);
						trade.setVIPAuthorizationNo(moto.getInvoice_No());
						trade.setRef_No(moto.getRef_No());
						this.commonService.update(trade);
						card.setExpiryDate("0000");
						card.setCvv2("XXX");
						this.commonService.update(card);
						MD5info = trade.getMerchantOrderNo()
								+ trade.getMoneyType() + ordercountValue
								+ responseCode + MD5key;
						md5Value = md5.getMD5ofStr(MD5info);
						
						logger.info("交易成功返回:"+merchantOrderNo+"**"+tradeMoneyType+"**"+ordercount+"**"+responseCode+"**"+message+"**"+ReturnURL+"**"+md5Value);
						// 发送邮件
						List<InternationalTerminalManager> tmm = this.commonService
								.list("select tm from InternationalTerminalManager tm where tm.terminalNo='"
										+ posNumber.trim() + "' ");
						String billaddressby = null;
						if (tmm.size() > 0) {
							InternationalTerminalManager tm = tmm.get(0);
							billaddressby = tm.getBillingAddress();
						}						
							String mailinfo = null;
						if(!"4212".equals((trade.getOrderNo()).substring(0,4))){
							try {
								EmailInfo emailinfo = new EmailInfo();
								mailinfo = emailinfo.getPaymentResultEmail(
										card.getEmail(),
										trade.getTradeAmount(),
										getStates().getCurrencyTypeByNo(
												trade.getMoneyType().intValue()),
										trade.getTradeUrl(), trade.getTradeTime(),
										billaddressby, trade.getMerchantOrderNo(),
										trade.getOrderNo(), merchant);
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
							}
						}
						logger.info("*********************支付结果返回码***************************"+responseCode);
						return SUCCESS;
					} else {
						this.message = "Payment Declined!"+moto.getResp_Code();
						this.responseCode = 0;
						trade.setTradeState("0"
								+ trade.getTradeState().substring(1,
										trade.getTradeState().length()));
						trade.setRemark(message);
						trade.setVIPDisposePorson("System");
						trade.setVIPDisposeDate(new Date());
						this.commonService.update(trade);
						MD5info = trade.getMerchantOrderNo()
								+ trade.getMoneyType() + ordercountValue
								+ responseCode + MD5key;
						md5Value = md5.getMD5ofStr(MD5info);
						logger.info("*********************支付结果返回码***************************"+responseCode);
						return SUCCESS;
					}
//					}else {
//						message = "Payment Declined";
//						remark = "高风险交易/Risk of trading";
//						this.responseCode = Integer.valueOf(dcc2.getResp_Code());
//						trade.setTradeState("0"
//								+ trade.getTradeState().substring(1,
//										trade.getTradeState().length()));
//						String re[]=remark.split("/");
//						trade.setRemark(re[0]+this.responseCode);
//						trade.setVIPDisposePorson("System");
//						trade.setVIPDisposeDate(new Date());
//						this.commonService.update(trade);
//						MD5info = trade.getMerchantOrderNo()
//								+ trade.getMoneyType() + ordercountValue
//								+ responseCode + MD5key;
//						md5Value = md5.getMD5ofStr(MD5info);
//						logger.info("*********************支付结果返回码***************************"+responseCode);
//						return SUCCESS;
//					}

				} else if (dcc.getResp_Code().equals("99YX")) {//不支持DCC交易
//					logger.info("MOTO 非DCC交易开始");
//					// 交易
//					vpn.MotoDCCMessage dcc2 = new vpn.MotoDCCMessage();
//					dcc2.setTrans_Type("risk");// 类型
//					dcc2.setMerchant_Id(posMerchantNo);// 42 商户编号
//					dcc2.setAuthor_Str(it.get(0).getAuthcode());
//					dcc2.setTerminal_Id(posNumber);// 41 商户终端号
//					dcc2.setInvoice_No(trade.getOrderNo().substring(
//							trade.getOrderNo().length() - 6,
//							trade.getOrderNo().length()));
//
//					dcc2.setTrans_Model("M");//moto通道
//					dcc2.setCurrency_Code_T("156");// 货币代码
//					dcc2.setAmount_Loc(this.buzero(trade.getRmbAmount() + ""));// 4
//					// 本地交易金额
//					dcc2.setCard_No(cardnum);// 账号2
//					dcc2.setExp_Date(year + month);// 14 有效期
//					dcc2.setCSC(cvv2);
//					dcc2.setCurrency_Code(dcc.getCurrency_Code());
//					dcc2.setBocs_ReturnURL("http://172.20.66.2/sfe");
//					dcc2.setAmount_For(dcc.getAmount_For());
//					dcc2.setOrder_No(trade.getOrderNo());
//					dcc2.setCustom(trade.getOrderNo());
//					dcc2.setHashCode(it.get(0).getHashcode());
//					//新增风险交易参数
//					dcc2.setCUST_FNAME(firstname);
//					dcc2.setCUST_LNAME(lastname);
//					dcc2.setCUST_CITY(city);
//					dcc2.setCUST_ADDR1(address);
//					dcc2.setCUST_CNTRY_CD(country.substring(0, 3));
//					dcc2.setCUST_EMAIL(email);
//					dcc2.setCUST_IP_ADDR(ip);
////					dcc2.setCUST_HOME_PHONE(phone);
//					if(zipcode.replaceAll("-", "").length()>9){
//					dcc2.setCUST_POSTAL_CD(zipcode.replaceAll("-", "").substring(0, 9));
//					}else{
//					dcc2.setCUST_POSTAL_CD(zipcode.replaceAll("-", ""));
//					}
////					dcc2.setCUST_STPR_CD(state);
//					dcc2.setSHIP_FNAME(shippingFirstName);
//					dcc2.setSHIP_LNAME(shippingLastName);
//					dcc2.setSHIP_CITY(shippingCity);
//					dcc2.setSHIP_ADDR1(shippingAddress);
//					dcc2.setSHIP_CNTRY_CD(shippingCountry.substring(0, 3));
//					dcc2.setSHIP_EMAIL(shippingEmail);
//					dcc2.setSHIP_IP_ADDR(ip);
////					dcc2.setSHIP_HOME_PHONE(shippingPhone);
//					if(shippingZipcode.replaceAll("-", "").length()>9){
//					dcc2.setSHIP_POSTAL_CD(shippingZipcode.replaceAll("-", "").substring(0, 9));
//					}else{
//						dcc2.setSHIP_POSTAL_CD(shippingZipcode.replaceAll("-", ""));
//					}
////					dcc2.setSHIP_STPR_CD(shippingSstate);
//					VpnUtil_Moto vu2=new VpnUtil_Moto();
//					//VpnUtil vu2 = new VpnUtil();
//					Long tim2 = System.currentTimeMillis();
//					try {
//						// type 2 dcc交易
//						logger.info("开始moto风控");
//						 dcc2 = vu2.getDCCvalue(dcc2, "21");
//					} catch (Exception e) {
//						responseCode = 19;
//						message = "Your payment is being processed";
//						MD5info = trade.getMerchantOrderNo()
//								+ trade.getMoneyType() + ordercountValue
//								+ responseCode + MD5key;
//						md5Value = md5.getMD5ofStr(MD5info);
//
//						trade.setTradeState("0"
//								+ trade.getTradeState().substring(1,
//										trade.getTradeState().length()));
//						trade.setRemark(message);
//						trade.setVIPDisposePorson("System");
//						trade.setVIPDisposeDate(new Date());
//						this.commonService.update(trade);
//						logger.info("*********************支付结果返回码***************************"+responseCode);
//						return SUCCESS;
//					}
//					if (dcc2.getResp_Code().equals("5600")) {

					vpn.DCCMessage moto2 = new vpn.DCCMessage();
					moto2.setTrans_Type("sales");// 类型
					// 商户编号
					moto2.setMerchant_Id(it.get(0).getBanktype());// 42
					moto2.setAuthor_Str(it.get(0).getAuthcode());
					// 商户终端号
					moto2.setTerminal_Id(it.get(0).getAndterminalNo());// 41
					moto2.setInvoice_No(trade.getOrderNo().substring(
							trade.getOrderNo().length() - 6,
							trade.getOrderNo().length()));

					moto2.setOrder_No(trade.getOrderNo());// 62
					moto2.setCustom(trade.getOrderNo());
					moto2.setHashCode(it.get(0).getHashcode());

					moto2.setTrans_Model("M");//moto
					moto2.setCurrency_Code_T(dcc.getCurrency_Code_T());// 货币代码
					moto2.setAmount_Loc(this.buzero(trade.getTradeAmount() + ""));// 4
					// 本地交易金额
					moto2.setCard_No(cardnum);// 账号2
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
						MD5info = trade.getMerchantOrderNo()
								+ trade.getMoneyType() + ordercountValue
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
						trade.setTradeState("0"
								+ trade.getTradeState().substring(1,
										trade.getTradeState().length()));
						trade.setRemark(message);
						trade.setVIPDisposePorson("System");
						trade.setVIPDisposeDate(new Date());
						this.commonService.update(trade);
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
						trade.setTradeState("1"
								+ trade.getTradeState().substring(1,
										trade.getTradeState().length()));
						trade.setRemark(message);
						trade.setVIPDisposePorson("System");
						trade.setVIPDisposeDate(new Date());
						trade.setVIPBatchNo(moto2.getAuth_Code());
						trade.setVIPTerminalNo(posNumber);
						trade.setVIPAuthorizationNo(moto2.getInvoice_No());
						trade.setRef_No(moto2.getRef_No());
						this.commonService.update(trade);
						card.setExpiryDate("0000");
						card.setCvv2("XXX");
						this.commonService.update(card);
						MD5info = trade.getMerchantOrderNo()
								+ trade.getMoneyType() + ordercountValue
								+ responseCode + MD5key;
						md5Value = md5.getMD5ofStr(MD5info);
						
						logger.info("交易成功返回:"+merchantOrderNo+"**"+tradeMoneyType+"**"+ordercount+"**"+responseCode+"**"+message+"**"+ReturnURL+"**"+md5Value);
						// 发送邮件
						List<InternationalTerminalManager> tmm = this.commonService
								.list("select tm from InternationalTerminalManager tm where tm.terminalNo='"
										+ posNumber.trim() + "' ");
						String billaddressby = null;
						if (tmm.size() > 0) {
							InternationalTerminalManager tm = tmm.get(0);
							billaddressby = tm.getBillingAddress();
						}
							String mailinfo = null;
						if(!"4212".equals((trade.getOrderNo()).substring(0,4))){
							try {
								EmailInfo emailinfo = new EmailInfo();
								mailinfo = emailinfo.getPaymentResultEmail(
										card.getEmail(),
										trade.getTradeAmount(),
										getStates().getCurrencyTypeByNo(
												trade.getMoneyType().intValue()),
										trade.getTradeUrl(), trade.getTradeTime(),
										billaddressby, trade.getMerchantOrderNo(),
										trade.getOrderNo(), merchant);
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
							}
						}
						this.message = "Payment Success!";
						this.responseCode = 88;

						MD5info = trade.getMerchantOrderNo()
								+ trade.getMoneyType() + ordercountValue
								+ responseCode + MD5key;
						md5Value = md5.getMD5ofStr(MD5info);
						logger.info("交易成功返回:"+merchantOrderNo+"**"+tradeMoneyType+"**"+ordercount+"**"+responseCode+"**"+message+"**"+ReturnURL+"**"+md5Value);
						logger.info("*********************支付结果返回码***************************"+responseCode);
						return SUCCESS;
					} else {
						this.message = "Payment Declined!"+moto2.getResp_Code();
						this.responseCode = 0;
						trade.setTradeState("0"
								+ trade.getTradeState().substring(1,
										trade.getTradeState().length()));
						trade.setRemark(message);
						trade.setVIPDisposePorson("System");
						trade.setVIPDisposeDate(new Date());
						this.commonService.update(trade);
						MD5info = trade.getMerchantOrderNo()
								+ trade.getMoneyType() + ordercountValue
								+ responseCode + MD5key;
						md5Value = md5.getMD5ofStr(MD5info);
						
						logger.info("*********************支付结果返回码***************************"+responseCode);
						return SUCCESS;
					}
//					}else {
//						message = "Payment Declined";
//						remark = "高风险交易/Risk of trading";
//						this.responseCode = Integer.valueOf(dcc2.getResp_Code());
//						trade.setTradeState("0"
//								+ trade.getTradeState().substring(1,
//										trade.getTradeState().length()));
//						String re[]=remark.split("/");
//						trade.setRemark(re[0]+this.responseCode);
//						trade.setVIPDisposePorson("System");
//						trade.setVIPDisposeDate(new Date());
//						this.commonService.update(trade);
//						MD5info = trade.getMerchantOrderNo()
//								+ trade.getMoneyType() + ordercountValue
//								+ responseCode + MD5key;
//						md5Value = md5.getMD5ofStr(MD5info);
//						logger.info("*********************支付结果返回码***************************"+responseCode);
//						return SUCCESS;
//					}

				}
			}else if(chnals.equals("YL")){
				logger.info("进入银联通道");
				YinlianMessage msg=new YinlianMessage();
				YinlianUtil yu=new YinlianUtil();
				SimpleDateFormat sdf =new SimpleDateFormat("yyyyMMddHHmmss");
				msg.setTrnxDatetime(sdf.format(new Date()));
				msg.setCardNo(cardnum);
				msg.setAmt(this.buzero(trade.getRmbAmount() + ""));
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
					trade.setTradeState("1"
							+ trade.getTradeState().substring(1,
									trade.getTradeState().length()));
					trade.setRemark(message);
					trade.setVIPDisposePorson("System");
					trade.setVIPDisposeDate(new Date());
					trade.setVIPBatchNo(posNo+"");
					trade.setVIPTerminalNo(posNumber);
					trade.setVIPAuthorizationNo(msg.getRes_authResCode());
					trade.setRef_No(msg.getRes_referenceNo());
					trade.setBoc_time(msg.getRes_settlementDate());
					this.commonService.update(trade);
					card.setExpiryDate("0000");
					card.setCvv2("XXX");
					this.commonService.update(card);
					MD5info = trade.getMerchantOrderNo()
							+ trade.getMoneyType() + ordercountValue
							+ responseCode + MD5key;
					md5Value = md5.getMD5ofStr(MD5info);
					
					logger.info("交易成功返回:"+merchantOrderNo+"**"+tradeMoneyType+"**"+ordercount+"**"+responseCode+"**"+message+"**"+ReturnURL+"**"+md5Value);
					// 发送邮件
					List<InternationalTerminalManager> tmm = this.commonService
							.list("select tm from InternationalTerminalManager tm where tm.terminalNo='"
									+ posNumber.trim() + "' ");
					String billaddressby = null;
					if (tmm.size() > 0) {
						InternationalTerminalManager tm = tmm.get(0);
						billaddressby = tm.getBillingAddress();
					}
					String mailinfo = null;
					if(!"4212".equals((trade.getOrderNo()).substring(0,4))){
						try {
							EmailInfo emailinfo = new EmailInfo();
							mailinfo = emailinfo.getPaymentResultEmail(
									card.getEmail(),
									trade.getTradeAmount(),
									getStates().getCurrencyTypeByNo(
											trade.getMoneyType().intValue()),
									trade.getTradeUrl(), trade.getTradeTime(),
									billaddressby, trade.getMerchantOrderNo(),
									trade.getOrderNo(), merchant);
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
						}
					}
					logger.info("*********************支付结果返回码***************************"+responseCode);
					return SUCCESS;
				} else {
					this.message = "Payment Declined!Y"+msg.getRes_resCode();
					this.responseCode = 0;
					trade.setTradeState("0"
							+ trade.getTradeState().substring(1,
									trade.getTradeState().length()));
					trade.setRemark(message);
					trade.setVIPDisposePorson("System");
					trade.setVIPDisposeDate(new Date());
					this.commonService.update(trade);
					MD5info = trade.getMerchantOrderNo()
							+ trade.getMoneyType() + ordercountValue
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
			msg.setNewcardtype(newcardtype);
			BASE64Encoder baseE=new BASE64Encoder(); 
			msg.setCardnum(baseE.encode(cardnum.getBytes()));
			msg.setCvv2(baseE.encode(cvv2.getBytes()));
			msg.setYear(baseE.encode(("20"+year).getBytes()));
			msg.setMonth(baseE.encode(month.getBytes()));
			msg.setCardbank(cardbank);//选填
			msg.setBillNo(trade.getOrderNo());
			//msg.setAmount(trade.getRmbAmount() + "");
			
			Double amountAndFee=trade.getRmbAmount();
			if(trade.getChannelFee()!=null){
				amountAndFee=amountAndFee*(trade.getChannelFee()+1.0);
				amountAndFee = (double) (Math.round((double) amountAndFee * 100) / 100.00);
			}
			msg.setAmount(amountAndFee+ "");
			
			msg.setCurrency("3");
			msg.setLanguage("en");
			msg.setWebsite(trade.getTradeUrl());
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
				trade.setTradeState("1"
						+ trade.getTradeState().substring(1,
								trade.getTradeState().length()));
				trade.setRemark(message);
				trade.setVIPDisposePorson("System");
				trade.setVIPDisposeDate(new Date());
				trade.setVIPBatchNo(msg.getGrn());
				this.commonService.update(trade);
				card.setExpiryDate("0000");
				card.setCvv2("XXX");
				this.commonService.update(card);
				MD5info = trade.getMerchantOrderNo()
						+ trade.getMoneyType() + ordercountValue
						+ responseCode + MD5key;
				md5Value = md5.getMD5ofStr(MD5info);
				
				logger.info("交易成功返回:"+merchantOrderNo+"**"+tradeMoneyType+"**"+ordercount+"**"+responseCode+"**"+message+"**"+ReturnURL+"**"+md5Value);
				// 发送邮件
//				List<InternationalTerminalManager> tmm = this.commonService
//						.list("select tm from InternationalTerminalManager tm where tm.terminalNo='"
//								+ posNumber.trim() + "' ");
//				String billaddressby = null;
//				if (tmm.size() > 0) {
//					InternationalTerminalManager tm = tmm.get(0);
//					billaddressby = tm.getBillingAddress();
//				}
				String mailinfo = null;				
				if(!"4212".equals((trade.getOrderNo()).substring(0,4))){
					try {
						EmailInfo emailinfo = new EmailInfo();
						mailinfo = emailinfo.getPaymentResultEmail(
								card.getEmail(),
								trade.getTradeAmount(),
								getStates().getCurrencyTypeByNo(
										trade.getMoneyType().intValue()),
								trade.getTradeUrl(), trade.getTradeTime(),
								msg.getBillingAddress(), trade.getMerchantOrderNo(),
								trade.getOrderNo(), merchant);
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
					}
				}
				logger.info("*********************支付结果返回码***************************"+responseCode);
				return SUCCESS;
			}else if(msg.getSucceed().equals("90")){
				// 待处理
				this.responseCode = 19;

				MD5info = trade.getMerchantOrderNo()
						+ trade.getMoneyType() + ordercountValue
						+ responseCode + MD5key;
				md5Value = md5.getMD5ofStr(MD5info);
				trade.setTradeState("2"
						+ trade.getTradeState().substring(1,
								trade.getTradeState().length()));

				if(StringUtils.isNotBlank(msg.getResult())){
					trade.setRemark("GP*timeOut!");
				}else{					
					trade.setRemark("timeOut!");
				}
				this.commonService.update(trade);
				shopManagerService.addTemporaryTradInfo(trade.getOrderNo(), year, month,cvv2,country,MD5key, ip,"MSIE 10.0",trade.getRemark());
				logger.info("*********************支付结果返回码***************************"+responseCode);
				return SUCCESS;
			}else if(bankBackRemark.toLowerCase().indexOf(msg.getRemark().toLowerCase())>=0){
				this.responseCode = 19;
				
				MD5info = trade.getMerchantOrderNo()
						+ trade.getMoneyType() + ordercountValue
						+ responseCode + MD5key;
				md5Value = md5.getMD5ofStr(MD5info);
				trade.setTradeState("2"
						+ trade.getTradeState().substring(1,
								trade.getTradeState().length()));
				trade.setRemark("Waiting processing!");
				remark=trade.getRemark();
				this.commonService.update(trade);
				shopManagerService.addTemporaryTradInfo(trade.getOrderNo(), year, month,cvv2,country,MD5key, ip,"MSIE 10.0","GP"+msg.getRemark());
				logger.info("*********************支付结果返回码***************************"+responseCode);
				return SUCCESS;	
			} else {
				this.message = "Payment Declined!"+msg.getRemark();
				this.responseCode = 0;
				trade.setTradeState("0"
						+ trade.getTradeState().substring(1,
								trade.getTradeState().length()));
				trade.setRemark(message);
				trade.setVIPDisposePorson("System");
				trade.setVIPDisposeDate(new Date());
				this.commonService.update(trade);
				MD5info = trade.getMerchantOrderNo()
						+ trade.getMoneyType() + ordercountValue
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
			msg.setNewcardtype(newcardtype);
			BASE64Encoder baseE=new BASE64Encoder(); 
			msg.setCardnum(baseE.encode(cardnum.getBytes()));
			msg.setCvv2(baseE.encode(cvv2.getBytes()));
			msg.setYear(baseE.encode(("20"+year).getBytes()));
			msg.setMonth(baseE.encode(month.getBytes()));
			msg.setCardbank(cardbank);//选填
			msg.setBillNo(trade.getOrderNo());
			//msg.setAmount(trade.getRmbAmount() + "");
			
			Double amountAndFee=trade.getRmbAmount();
			if(trade.getChannelFee()!=null){
				amountAndFee=amountAndFee*(trade.getChannelFee()+1.0);
				amountAndFee = (double) (Math.round((double) amountAndFee * 100) / 100.00);
			}
			msg.setAmount(amountAndFee + "");
			
			msg.setCurrency("3");
			msg.setLanguage("en");
			msg.setWebsite(trade.getTradeUrl());
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
				trade.setTradeState("1"
						+ trade.getTradeState().substring(1,
								trade.getTradeState().length()));
				trade.setRemark(message);
				trade.setVIPDisposePorson("System");
				trade.setVIPDisposeDate(new Date());
				trade.setVIPBatchNo(msg.getGrn());
				this.commonService.update(trade);
				card.setExpiryDate("0000");
				card.setCvv2("XXX");
				this.commonService.update(card);
				MD5info = trade.getMerchantOrderNo()
						+ trade.getMoneyType() + ordercountValue
						+ responseCode + MD5key;
				md5Value = md5.getMD5ofStr(MD5info);
				
				logger.info("交易成功返回:"+merchantOrderNo+"**"+tradeMoneyType+"**"+ordercount+"**"+responseCode+"**"+message+"**"+ReturnURL+"**"+md5Value);
				// 发送邮件
//				List<InternationalTerminalManager> tmm = this.commonService
//						.list("select tm from InternationalTerminalManager tm where tm.terminalNo='"
//								+ posNumber.trim() + "' ");
//				String billaddressby = null;
//				if (tmm.size() > 0) {
//					InternationalTerminalManager tm = tmm.get(0);
//					billaddressby = tm.getBillingAddress();
//				}
				String mailinfo = null;				
				if(!"4212".equals((trade.getOrderNo()).substring(0,4))){
					try {
						EmailInfo emailinfo = new EmailInfo();
						mailinfo = emailinfo.getPaymentResultEmail(
								card.getEmail(),
								trade.getTradeAmount(),
								getStates().getCurrencyTypeByNo(
										trade.getMoneyType().intValue()),
								trade.getTradeUrl(), trade.getTradeTime(),
								msg.getBillingAddress(), trade.getMerchantOrderNo(),
								trade.getOrderNo(), merchant);
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
					}
				}
				logger.info("*********************支付结果返回码***************************"+responseCode);
				return SUCCESS;
			}else if(msg.getSucceed().equals("90")){
				// 待处理
				this.responseCode = 19;

				MD5info = trade.getMerchantOrderNo()
						+ trade.getMoneyType() + ordercountValue
						+ responseCode + MD5key;
				md5Value = md5.getMD5ofStr(MD5info);
				trade.setTradeState("2"
						+ trade.getTradeState().substring(1,
								trade.getTradeState().length()));

				if(StringUtils.isNotBlank(msg.getResult())){
					trade.setRemark("GD*timeOut!");
				}else{					
					trade.setRemark("timeOut!");
				}
				this.commonService.update(trade);
				shopManagerService.addTemporaryTradInfo(trade.getOrderNo(), year, month,cvv2,country,MD5key, ip,"MSIE 10.0",trade.getRemark());
				logger.info("*********************支付结果返回码***************************"+responseCode);
				return SUCCESS;
			}else if(bankBackRemark.toLowerCase().indexOf(msg.getRemark().toLowerCase())>=0){
				this.responseCode = 19;
				
				MD5info = trade.getMerchantOrderNo()
						+ trade.getMoneyType() + ordercountValue
						+ responseCode + MD5key;
				md5Value = md5.getMD5ofStr(MD5info);
				trade.setTradeState("2"
						+ trade.getTradeState().substring(1,
								trade.getTradeState().length()));
				trade.setRemark("Waiting processing!");
				remark=trade.getRemark();
				this.commonService.update(trade);
				shopManagerService.addTemporaryTradInfo(trade.getOrderNo(), year, month,cvv2,country,MD5key, ip,"MSIE 10.0","GD"+msg.getRemark());
				logger.info("*********************支付结果返回码***************************"+responseCode);
				return SUCCESS;	
			} else {
				this.message = "Payment Declined!"+msg.getRemark();
				this.responseCode = 0;
				trade.setTradeState("0"
						+ trade.getTradeState().substring(1,
								trade.getTradeState().length()));
				trade.setRemark(message);
				trade.setVIPDisposePorson("System");
				trade.setVIPDisposeDate(new Date());
				this.commonService.update(trade);
				MD5info = trade.getMerchantOrderNo()
						+ trade.getMoneyType() + ordercountValue
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
			msg.setOrderId(trade.getOrderNo());
			msg.setSource(trade.getTradeUrl());
			msg.setEmail(email);
			msg.setIPAddress(ip.split(",")[0]);
			msg.setCurrency("CNY");
			Double amountAndFee=trade.getRmbAmount();
			if(trade.getChannelFee()!=null){
				amountAndFee=amountAndFee*(trade.getChannelFee()+1.0);
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
			msg.setCreditCardNumber(cardnum);
			msg.setCreditCardExpire("20"+year+month);
			msg.setCreditCardCsc2(cvv2);
			yu.get(msg);
				
			if (msg.getStatus().equals("Success")) {//交易成功
				this.message = "Payment Success!";
				this.responseCode = 88;
				//billaddress=msg.getBillingAddress();
				trade.setTradeState("1"
						+ trade.getTradeState().substring(1,
								trade.getTradeState().length()));
				trade.setRemark(message);
				trade.setVIPDisposePorson("System");
				trade.setVIPDisposeDate(new Date());
				trade.setVIPBatchNo(msg.getTransactionId());
				this.commonService.update(trade);
				card.setExpiryDate("0000");
				card.setCvv2("XXX");
				this.commonService.update(card);
				MD5info = merchantOrderNo
						+ trade.getMoneyType() + ordercountValue
						+ responseCode + MD5key;
				md5Value = md5.getMD5ofStr(MD5info);
				
				logger.info("交易成功返回:"+merchantOrderNo+"**"+trade.getMoneyType()+"**"+ordercount+"**"+responseCode+"**"+message+"**"+ReturnURL+"**"+md5Value);
				logger.info("*********************支付结果返回码***************************"+responseCode);
				return SUCCESS;
			}else if((msg.getStatus().equals("Processing")||msg.getStatus().equals("Pending"))){
				// 支付成功
				this.responseCode = 19;

				MD5info = trade.getMerchantOrderNo()
						+ trade.getMoneyType() + ordercountValue
						+ responseCode + MD5key;
				md5Value = md5.getMD5ofStr(MD5info);
				trade.setTradeState("2"
						+ trade.getTradeState().substring(1,
								trade.getTradeState().length()));
				trade.setRemark("Waiting processing!");
				this.commonService.update(trade);
				
				logger.info("*********************支付结果返回码***************************"+responseCode);
				return SUCCESS;
			}else if(msg.getStatus().equals("sfe01")){
				// 支付成功
				this.responseCode = 19;

				MD5info = trade.getMerchantOrderNo()
						+ trade.getMoneyType() + ordercountValue
						+ responseCode + MD5key;
				md5Value = md5.getMD5ofStr(MD5info);
				trade.setTradeState("2"
						+ trade.getTradeState().substring(1,
								trade.getTradeState().length()));
				trade.setRemark("timeOut!");
				this.commonService.update(trade);
				shopManagerService.addTemporaryTradInfo(trade.getOrderNo(), year, month,cvv2,country,MD5key, ip,"MSIE 10.0","timeOut!");
				logger.info("*********************支付结果返回码***************************"+responseCode);
				return SUCCESS;
			} else {
					this.message = "Payment Declined!"+msg.getReason();
					this.responseCode = 0;
					remark=message;
					trade.setTradeState("0"
							+ trade.getTradeState().substring(1,
									trade.getTradeState().length()));
					trade.setRemark(message);
					trade.setVIPDisposePorson("System");
					trade.setVIPDisposeDate(new Date());
					this.commonService.update(trade);
					MD5info = merchantOrderNo
							+ trade.getMoneyType() + ordercountValue
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
			msg.setOrderId(trade.getOrderNo());
			//msg.setSource(trade.getTradeUrl());
			msg.setEmail(email);
			msg.setIPAddress(ip.split(",")[0]);
			msg.setCurrency("CNY");
			Double amountAndFee=trade.getRmbAmount();
			if(trade.getChannelFee()!=null){
				amountAndFee=amountAndFee*(trade.getChannelFee()+1.0);
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
			msg.setCreditCardNumber(cardnum);
			msg.setCreditCardExpire("20"+year+month);
			msg.setCreditCardCsc2(cvv2);
			yu.get(msg);
				
			if (msg.getStatus().equals("Success")) {//交易成功
				this.message = "Payment Success!";
				this.responseCode = 88;
				//billaddress=msg.getBillingAddress();
				trade.setTradeState("1"
						+ trade.getTradeState().substring(1,
								trade.getTradeState().length()));
				trade.setRemark(message);
				trade.setVIPDisposePorson("System");
				trade.setVIPDisposeDate(new Date());
				trade.setVIPAuthorizationNo(msg.getTransactionId());
				this.commonService.update(trade);
				card.setExpiryDate("0000");
				card.setCvv2("XXX");
				this.commonService.update(card);
				MD5info = merchantOrderNo
						+ trade.getMoneyType() + ordercountValue
						+ responseCode + MD5key;
				md5Value = md5.getMD5ofStr(MD5info);
				
				logger.info("交易成功返回:"+merchantOrderNo+"**"+trade.getMoneyType()+"**"+ordercount+"**"+responseCode+"**"+message+"**"+ReturnURL+"**"+md5Value);
				String mailinfo = null;				
				if(!"4212".equals((trade.getOrderNo()).substring(0,4))){
					try {
						EmailInfo emailinfo = new EmailInfo();
						mailinfo = emailinfo.getPaymentResultEmail(
								card.getEmail(),
								trade.getTradeAmount(),
								getStates().getCurrencyTypeByNo(
										trade.getMoneyType().intValue()),
								trade.getTradeUrl(), trade.getTradeTime(),
								it.get(0).getBillingAddress(), trade.getMerchantOrderNo(),
								trade.getOrderNo(), merchant);
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
					}
				}
				logger.info("*********************支付结果返回码***************************"+responseCode);
				return SUCCESS;
			}else if((msg.getStatus().equals("Processing")||msg.getStatus().equals("Pending"))){
				// 支付成功
				this.responseCode = 19;

				MD5info = trade.getMerchantOrderNo()
						+ trade.getMoneyType() + ordercountValue
						+ responseCode + MD5key;
				md5Value = md5.getMD5ofStr(MD5info);
				trade.setTradeState("2"
						+ trade.getTradeState().substring(1,
								trade.getTradeState().length()));
				trade.setRemark("Waiting processing!");
				this.commonService.update(trade);
				
				logger.info("*********************支付结果返回码***************************"+responseCode);
				return SUCCESS;
			}else if(bankBackRemark.toLowerCase().indexOf(msg.getReason().toLowerCase())>=0){
				//"拒绝交易".equals(hm.getRes_message())||"银行通讯故障".equals(hm.getRes_message())||"交易超时".equals(hm.getRes_message())
				this.responseCode = 19;

				MD5info = trade.getMerchantOrderNo()
						+ trade.getMoneyType() + ordercountValue
						+ responseCode + MD5key;
				md5Value = md5.getMD5ofStr(MD5info);
				trade.setTradeState("2"
						+ trade.getTradeState().substring(1,
								trade.getTradeState().length()));
				trade.setRemark("Waiting processing!");
				this.commonService.update(trade);
				shopManagerService.addTemporaryTradInfo(trade.getOrderNo(), year, month,cvv2,country,MD5key, ip,"MSIE 10.0","QP"+msg.getReason());
				logger.info("*********************支付结果返回码***************************"+responseCode);
				return SUCCESS;
			}else if(msg.getStatus().equals("sfe01")){
				// 支付成功
				this.responseCode = 19;

				MD5info = trade.getMerchantOrderNo()
						+ trade.getMoneyType() + ordercountValue
						+ responseCode + MD5key;
				md5Value = md5.getMD5ofStr(MD5info);
				trade.setTradeState("2"
						+ trade.getTradeState().substring(1,
								trade.getTradeState().length()));
				trade.setRemark("timeOut!");
				this.commonService.update(trade);
				shopManagerService.addTemporaryTradInfo(trade.getOrderNo(), year, month,cvv2,country,MD5key, ip,"MSIE 10.0","timeOut!");
				logger.info("*********************支付结果返回码***************************"+responseCode);
				return SUCCESS;
			} else {
					this.message = "Payment Declined!"+msg.getReason();
					this.responseCode = 0;
					remark=message;
					trade.setTradeState("0"
							+ trade.getTradeState().substring(1,
									trade.getTradeState().length()));
					trade.setRemark(message);
					trade.setVIPDisposePorson("System");
					trade.setVIPDisposeDate(new Date());
					this.commonService.update(trade);
					MD5info = merchantOrderNo
							+ trade.getMoneyType() + ordercountValue
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
			hm.setOrderID(trade.getOrderNo());
			hm.setCurrCode("156");
			Double amountAndFee=trade.getRmbAmount();
			if(trade.getChannelFee()!=null){
				amountAndFee=amountAndFee*(trade.getChannelFee()+1.0);
				amountAndFee = (double) (Math.round((double) amountAndFee * 100) / 100.00);
			}
			hm.setAmount((int)(amountAndFee*100)+"");
			hm.setIpAddress(ip.split(",")[0]);
			hm.setCardType(chanelName.split("-")[1]);
			hm.setCardPAN(cardnum);
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
			hm.setRetURL(trade.getTradeUrl());
			hm.setEmail(email);
			String hash=it.get(0).getHashcode()+ hm.getAcctNo() + hm.getOrderID() + hm.getAmount() + hm.getCurrCode();
			logger.info("hash加密前："+hash);
			hm.setHashValue(getBase64E(hash));
			String Agent="";
			String userbrowser="";
			try{
			Agent = request.getHeader("User-Agent");  
			StringTokenizer st = new StringTokenizer(Agent,";");  
			st.nextToken();  
			//得到用户的浏览器名  
			userbrowser = st.nextToken();
			}catch(Exception e){
				logger.error(e);
				userbrowser=Agent;
			}
			if(StringUtils.isBlank(userbrowser)){
				userbrowser="MSIE 10.0";
			}
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
				trade.setTradeState("1"
						+ trade.getTradeState().substring(1,
								trade.getTradeState().length()));
				trade.setRemark(message);
				trade.setVIPDisposePorson("System");
				trade.setVIPDisposeDate(new Date());
				trade.setVIPAuthorizationNo(hm.getRes_queOrderNo());
				this.commonService.update(trade);
				card.setExpiryDate("0000");
				card.setCvv2("XXX");
				this.commonService.update(card);
				MD5info = trade.getMerchantOrderNo()
						+ trade.getMoneyType() + ordercountValue
						+ responseCode + MD5key;
				md5Value = md5.getMD5ofStr(MD5info);
				
				logger.info("交易成功返回:"+merchantOrderNo+"**"+tradeMoneyType+"**"+ordercount+"**"+responseCode+"**"+message+"**"+ReturnURL+"**"+md5Value);
//				// 发送邮件
//				List<InternationalTerminalManager> tmm = this.commonService
//						.list("select tm from InternationalTerminalManager tm where tm.terminalNo='"
//								+ posNumber.trim() + "' ");
//				String billaddressby = null;
//				if (tmm.size() > 0) {
//					InternationalTerminalManager tm = tmm.get(0);
//					billaddressby = tm.getBillingAddress();
//				}
				String mailinfo = null;
				if(!"4212".equals((trade.getOrderNo()).substring(0,4))){
					try {
						EmailInfo emailinfo = new EmailInfo();
						mailinfo = emailinfo.getPaymentResultEmail(
								card.getEmail(),
								trade.getTradeAmount(),
								getStates().getCurrencyTypeByNo(
										trade.getMoneyType().intValue()),
								trade.getTradeUrl(), trade.getTradeTime(),
								hm.getRes_billaddress(), trade.getMerchantOrderNo(),
								trade.getOrderNo(), merchant);
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
					}
				}
				logger.info("*********************支付结果返回码***************************"+responseCode);
				return SUCCESS;
			}if(hm.getRes_success().equals("sfe01")){
				this.responseCode = 19;

				MD5info = trade.getMerchantOrderNo()
						+ trade.getMoneyType() + ordercountValue
						+ responseCode + MD5key;
				md5Value = md5.getMD5ofStr(MD5info);
				trade.setTradeState("2"
						+ trade.getTradeState().substring(1,
								trade.getTradeState().length()));
				trade.setRemark("timeOut!");
				this.commonService.update(trade);
				shopManagerService.addTemporaryTradInfo(trade.getOrderNo(), year, month,cvv2,country,MD5key, ip,"MSIE 10.0","timeOut!");
				logger.info("*********************支付结果返回码***************************"+responseCode);
				return SUCCESS;
				
			}else if(bankBackRemark.toLowerCase().indexOf(hm.getRes_message().toLowerCase())>=0){
				//"拒绝交易".equals(hm.getRes_message())||"银行通讯故障".equals(hm.getRes_message())||"交易超时".equals(hm.getRes_message())
				this.responseCode = 19;

				MD5info = trade.getMerchantOrderNo()
						+ trade.getMoneyType() + ordercountValue
						+ responseCode + MD5key;
				md5Value = md5.getMD5ofStr(MD5info);
				trade.setTradeState("2"
						+ trade.getTradeState().substring(1,
								trade.getTradeState().length()));
				trade.setRemark("Waiting processing!");
				this.commonService.update(trade);
				shopManagerService.addTemporaryTradInfo(trade.getOrderNo(), year, month,cvv2,country,MD5key, ip,"MSIE 10.0","HJ"+hm.getRes_message());
				logger.info("*********************支付结果返回码***************************"+responseCode);
				return SUCCESS;
			}else {
				this.message = "Payment Declined!"+hm.getRes_message();
				this.responseCode = 0;
				trade.setTradeState("0"
						+ trade.getTradeState().substring(1,
								trade.getTradeState().length()));
				trade.setRemark(message);
				trade.setVIPDisposePorson("System");
				trade.setVIPDisposeDate(new Date());
				this.commonService.update(trade);
				MD5info = trade.getMerchantOrderNo()
						+ trade.getMoneyType() + ordercountValue
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
			Double amountAndFee=trade.getRmbAmount();
			if(trade.getChannelFee()!=null){
				amountAndFee=amountAndFee*(trade.getChannelFee()+1.0);
				amountAndFee = (double) (Math.round((double) amountAndFee * 100) / 100.00);
			}
			hwm.setAmount(amountAndFee+"");
			hwm.setCurrency("100");
			hwm.setBillNo(trade.getOrderNo());
			hwm.setCardAsn(cardnum);
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
				trade.setTradeState("1"
						+ trade.getTradeState().substring(1,
								trade.getTradeState().length()));
				trade.setRemark(message);
				trade.setVIPDisposePorson("System");
				trade.setVIPDisposeDate(new Date());
				trade.setVIPAuthorizationNo(hwm.getRes_jcTradeId());
				this.commonService.update(trade);
				card.setExpiryDate("0000");
				card.setCvv2("XXX");
				this.commonService.update(card);
				MD5info = trade.getMerchantOrderNo()
						+ trade.getMoneyType() + ordercountValue
						+ responseCode + MD5key;
				md5Value = md5.getMD5ofStr(MD5info);
				
				logger.info("交易成功返回:"+merchantOrderNo+"**"+tradeMoneyType+"**"+ordercount+"**"+responseCode+"**"+message+"**"+ReturnURL+"**"+md5Value);
				// 发送邮件
//				List<InternationalTerminalManager> tmm = this.commonService
//						.list("select tm from InternationalTerminalManager tm where tm.terminalNo='"
//								+ posNumber.trim() + "' ");
//				String billaddressby = null;
//				if (tmm.size() > 0) {
//					InternationalTerminalManager tm = tmm.get(0);
//					billaddressby = tm.getBillingAddress();
//				}
				String mailinfo = null;
				if(!"4212".equals((trade.getOrderNo()).substring(0,4))){
					try {
						EmailInfo emailinfo = new EmailInfo();
						mailinfo = emailinfo.getPaymentResultEmail(
								card.getEmail(),
								trade.getTradeAmount(),
								getStates().getCurrencyTypeByNo(
										trade.getMoneyType().intValue()),
								trade.getTradeUrl(), trade.getTradeTime(),
								it.get(0).getBillingAddress(), trade.getMerchantOrderNo(),
								trade.getOrderNo(), merchant);
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
					}
				}
				logger.info("*********************支付结果返回码***************************"+responseCode);
				return SUCCESS;
			}if(hwm.getRes_responseCode().equals("sfe01")){
				this.responseCode = 19;

				MD5info = trade.getMerchantOrderNo()
						+ trade.getMoneyType() + ordercountValue
						+ responseCode + MD5key;
				md5Value = md5.getMD5ofStr(MD5info);
				trade.setTradeState("2"
						+ trade.getTradeState().substring(1,
								trade.getTradeState().length()));
				trade.setRemark("timeOut!");
				this.commonService.update(trade);
				shopManagerService.addTemporaryTradInfo(trade.getOrderNo(), year, month,cvv2,country,MD5key, ip,"MSIE 10.0","timeOut!");
				logger.info("*********************支付结果返回码***************************"+responseCode);
				return SUCCESS;
				
			}else if(bankBackRemark.toLowerCase().indexOf(hwm.getRes_addMsg().toLowerCase())>=0){
				//"Refused transaction".equals(hwm.getRes_addMsg())||"Unknown error".equals(hwm.getRes_addMsg())||"Daily, monthly or yearly limits have been reached".equals(hwm.getRes_addMsg())||"AMOUNT NOT ALLOWED".equals(hwm.getRes_addMsg())
				this.responseCode = 19;

				MD5info = trade.getMerchantOrderNo()
						+ trade.getMoneyType() + ordercountValue
						+ responseCode + MD5key;
				md5Value = md5.getMD5ofStr(MD5info);
				trade.setTradeState("2"
						+ trade.getTradeState().substring(1,
								trade.getTradeState().length()));
				trade.setRemark("Waiting processing!");
				this.commonService.update(trade);
				shopManagerService.addTemporaryTradInfo(trade.getOrderNo(), year, month,cvv2,country,MD5key, ip,"MSIE 10.0","HW"+hwm.getRes_addMsg());
				logger.info("*********************支付结果返回码***************************"+responseCode);
				return SUCCESS;
			}else {
				this.message = "Payment Declined!"+hwm.getRes_addMsg();
				this.responseCode = 0;
				trade.setTradeState("0"
						+ trade.getTradeState().substring(1,
								trade.getTradeState().length()));
				trade.setRemark(message);
				trade.setVIPDisposePorson("System");
				trade.setVIPDisposeDate(new Date());
				this.commonService.update(trade);
				MD5info = trade.getMerchantOrderNo()
						+ trade.getMoneyType() + ordercountValue
						+ responseCode + MD5key;
				md5Value = md5.getMD5ofStr(MD5info);
				logger.info("*********************支付结果返回码***************************"+responseCode);
				return SUCCESS;
			}
		}else if(chnals.equals("YP")){
			logger.info("进入YP通道");
			YouPayUtil yu=new YouPayUtil();
			YouPayMessage ym=new YouPayMessage();
			ym.setMerNo(posMerchantNo);
			ym.setBillNo(trade.getOrderNo());
			ym.setAmount(trade.getRmbAmount()+"");
			ym.setCurrency("1");
			ym.setLanguage("2");
			ym.setReturnURL("www.sfepay.com");
			String MD5Hash=md5.getMD5ofStr(ym.getMerNo()+ym.getBillNo()+ym.getCurrency()+ym.getAmount()+ym.getLanguage()+ym.getReturnURL()+"{P}V{Fpn");
			ym.setMD5info(MD5Hash);
			ym.setIpAddr(ip.split(",")[0]);
			ym.setRemark("sfepay");
			ym.setTradeUrl("www.sfepay.com");
			SimpleDateFormat sdf =new SimpleDateFormat("yyyyMMdd");
			ym.setTradeDate(sdf.format(new Date()));
			ym.setCardNum(cardnum);
			ym.setCvv2(cvv2);
			ym.setMonth(month);
			ym.setYear(year);
			ym.setCardbank(cardbank);
			ym.setCardType(cartype+"");
			ym.setFirstname(firstname);
			ym.setLastname(lastname);
			ym.setEmail(email);
			ym.setCountry(country.substring(3, 5));
			ym.setState(state);
			ym.setCity(city);
			ym.setAddress(address);
			ym.setPhone(phone);
			ym.setZipcode(zipcode);
			ym.setShippingFirstName(shippingFirstName);
			ym.setShippingLastName(shippingLastName);
			ym.setShippingEmail(shippingEmail);
			ym.setShippingPhone(shippingPhone);
			ym.setShippingZipcode(shippingZipcode);
			ym.setShippingAddress(shippingAddress);
			ym.setShippingCity(shippingCity);
			ym.setShippingSstate(shippingSstate);
			ym.setShippingCountry(shippingCountry.substring(3,5));
			ym.setProducts(products);
			ym.setProductCategory("");
			yu.paySfe(ym);
			if (ym.getStatus().equals("0")) {//交易成功
				this.message = "Payment Success!";
				this.responseCode = 88;
				trade.setTradeState("1"
						+ trade.getTradeState().substring(1,
								trade.getTradeState().length()));
				trade.setRemark(message);
				trade.setVIPDisposePorson("System");
				trade.setVIPDisposeDate(new Date());
				this.commonService.update(trade);
				card.setExpiryDate("0000");
				card.setCvv2("XXX");
				this.commonService.update(card);
				MD5info = trade.getMerchantOrderNo()
						+ trade.getMoneyType() + ordercountValue
						+ responseCode + MD5key;
				md5Value = md5.getMD5ofStr(MD5info);
				
				logger.info("交易成功返回:"+merchantOrderNo+"**"+tradeMoneyType+"**"+ordercount+"**"+responseCode+"**"+message+"**"+ReturnURL+"**"+md5Value);
//				// 发送邮件
//				List<InternationalTerminalManager> tmm = this.commonService
//						.list("select tm from InternationalTerminalManager tm where tm.terminalNo='"
//								+ posNumber.trim() + "' ");
//				String billaddressby = null;
//				if (tmm.size() > 0) {
//					InternationalTerminalManager tm = tmm.get(0);
//					billaddressby = tm.getBillingAddress();
//				}
				String mailinfo = null;
				if(!"4212".equals((trade.getOrderNo()).substring(0,4))){
					try {
						EmailInfo emailinfo = new EmailInfo();
						mailinfo = emailinfo.getPaymentResultEmail(
								card.getEmail(),
								trade.getTradeAmount(),
								getStates().getCurrencyTypeByNo(
										trade.getMoneyType().intValue()),
								trade.getTradeUrl(), trade.getTradeTime(),
								"", trade.getMerchantOrderNo(),
								trade.getOrderNo(), merchant);
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
					}
				}
				logger.info("*********************支付结果返回码***************************"+responseCode);
				return SUCCESS;
			}else {
				this.message = "Payment Declined!"+ym.getDesc();
				this.responseCode = 0;
				trade.setTradeState("0"
						+ trade.getTradeState().substring(1,
								trade.getTradeState().length()));
				trade.setRemark(message);
				trade.setVIPDisposePorson("System");
				trade.setVIPDisposeDate(new Date());
				this.commonService.update(trade);
				MD5info = trade.getMerchantOrderNo()
						+ trade.getMoneyType() + ordercountValue
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
		yp.setB2mOrder(trade.getOrderNo());
		yp.setCardNo(cardnum);
		if("V".equals(chanelName.split("-")[1])){
			yp.setCardType("VISA");
		}else if("M".equals(chanelName.split("-")[1])){
			yp.setCardType("MASTER");
		}else if("J".equals(chanelName.split("-")[1])){
			yp.setCardType("JCB");
		}
		yp.setExpireDate(year+month);
		yp.setCvcCode(cvv2);
		yp.setB2mBank(cardbank);
		Double amountAndFee=trade.getRmbAmount();
		if(trade.getChannelFee()!=null){
			amountAndFee=amountAndFee*(trade.getChannelFee()+1.0);
			amountAndFee = (double) (Math.round((double) amountAndFee * 100) / 100.00);
		}
		yp.setB2mFee(amountAndFee+"");
		yp.setB2mCur("CNY");
		yp.setB2mWebsite("sfepay.com");
		yp.setB2mReturnUrl("https://www.sfepay.com");
		yp.setB2mNotifyUrl("https://www.sfepay.com");
		yp.setIp(ip.split(",")[0]);
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
			trade.setTradeState("1"
					+ trade.getTradeState().substring(1,
							trade.getTradeState().length()));
			trade.setRemark(message);
			trade.setVIPDisposePorson("System");
			trade.setVIPDisposeDate(new Date());
			this.commonService.update(trade);
			card.setExpiryDate("0000");
			card.setCvv2("XXX");
			this.commonService.update(card);
			MD5info = trade.getMerchantOrderNo()
					+ trade.getMoneyType() + ordercountValue
					+ responseCode + MD5key;
			md5Value = md5.getMD5ofStr(MD5info);
			
			logger.info("交易成功返回:"+merchantOrderNo+"**"+tradeMoneyType+"**"+ordercount+"**"+responseCode+"**"+message+"**"+ReturnURL+"**"+md5Value);
//			// 发送邮件
//			List<InternationalTerminalManager> tmm = this.commonService
//					.list("select tm from InternationalTerminalManager tm where tm.terminalNo='"
//							+ posNumber.trim() + "' ");
//			String billaddressby = null;
//			if (tmm.size() > 0) {
//				InternationalTerminalManager tm = tmm.get(0);
//				billaddressby = tm.getBillingAddress();
//			}
			String mailinfo = null;
			if(!"4212".equals((trade.getOrderNo()).substring(0,4))){
				try {
					EmailInfo emailinfo = new EmailInfo();
					mailinfo = emailinfo.getPaymentResultEmail(
							card.getEmail(),
							trade.getTradeAmount(),
							getStates().getCurrencyTypeByNo(
									trade.getMoneyType().intValue()),
							trade.getTradeUrl(), trade.getTradeTime(),
							it.get(0).getBillingAddress(), trade.getMerchantOrderNo(),
							trade.getOrderNo(), merchant);
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
				}
			}
			logger.info("*********************支付结果返回码***************************"+responseCode);
			return SUCCESS;
		}if(yp.getRes_errorCode().equals("sfe01")){
			this.responseCode = 19;

			MD5info = trade.getMerchantOrderNo()
					+ trade.getMoneyType() + ordercountValue
					+ responseCode + MD5key;
			md5Value = md5.getMD5ofStr(MD5info);
			trade.setTradeState("2"
					+ trade.getTradeState().substring(1,
							trade.getTradeState().length()));
			trade.setRemark("timeOut!");
			this.commonService.update(trade);
			shopManagerService.addTemporaryTradInfo(trade.getOrderNo(), year, month,cvv2,country,MD5key, ip,"MSIE 10.0","timeOut!");
			logger.info("*********************支付结果返回码***************************"+responseCode);
			return SUCCESS;
			
		}else if(bankBackRemark.toLowerCase().indexOf(yp.getRes_errorCode().toLowerCase())>=0){
			//"拒绝交易".equals(hm.getRes_message())||"银行通讯故障".equals(hm.getRes_message())||"交易超时".equals(hm.getRes_message())
			this.responseCode = 19;

			MD5info = trade.getMerchantOrderNo()
					+ trade.getMoneyType() + ordercountValue
					+ responseCode + MD5key;
			md5Value = md5.getMD5ofStr(MD5info);
			trade.setTradeState("2"
					+ trade.getTradeState().substring(1,
							trade.getTradeState().length()));
			trade.setRemark("Waiting processing!");
			this.commonService.update(trade);
			shopManagerService.addTemporaryTradInfo(trade.getOrderNo(), year, month,cvv2,country,MD5key, ip,"MSIE 10.0","HP-"+yp.getRes_errorCode());
			logger.info("*********************支付结果返回码***************************"+responseCode);
			return SUCCESS;
		}else {
			this.message = "Payment Declined!"+yp.getRes_errorCode();
			remark=message;
			this.responseCode = 0;
			trade.setTradeState("0"
					+ trade.getTradeState().substring(1,
							trade.getTradeState().length()));
			trade.setRemark(message);
			trade.setVIPDisposePorson("System");
			trade.setVIPDisposeDate(new Date());
			this.commonService.update(trade);
			MD5info = trade.getMerchantOrderNo()
					+ trade.getMoneyType() + ordercountValue
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
		//msg.setMerchantMID("4521");
		msg.setMerchantMID(posMerchantNo);
		msg.setNewcardtype(newcardtype);
		BASE64Encoder baseE=new BASE64Encoder(); 
		msg.setCardnum(baseE.encode(cardnum.getBytes()));
		msg.setCvv2(baseE.encode(cvv2.getBytes()));
		msg.setYear(baseE.encode(("20"+year).getBytes()));
		msg.setMonth(baseE.encode(month.getBytes()));
		msg.setCardbank(cardbank);
		msg.setBillNo(trade.getOrderNo());
		Double amountAndFee=trade.getRmbAmount();
		if(trade.getChannelFee()!=null){
			amountAndFee=amountAndFee*(trade.getChannelFee()+1.0);
			amountAndFee = (double) (Math.round((double) amountAndFee * 100) / 100.00);
		}
		msg.setAmount(amountAndFee+ "");
		msg.setCurrency("3");
		msg.setLanguage("en");
		msg.setWebsite(trade.getTradeUrl());
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
			trade.setTradeState("1"
					+ trade.getTradeState().substring(1,
							trade.getTradeState().length()));
			trade.setRemark(message);
			trade.setVIPDisposePorson("System");
			trade.setVIPDisposeDate(new Date());
			//trade.setVIPBatchNo(msg.getGrn());
			this.commonService.update(trade);
			card.setExpiryDate("0000");
			card.setCvv2("XXX");
			this.commonService.update(card);
			MD5info = trade.getMerchantOrderNo()
					+ trade.getMoneyType() + ordercountValue
					+ responseCode + MD5key;
			md5Value = md5.getMD5ofStr(MD5info);
			
			logger.info("交易成功返回:"+merchantOrderNo+"**"+tradeMoneyType+"**"+ordercount+"**"+responseCode+"**"+message+"**"+ReturnURL+"**"+md5Value);
			// 发送邮件
//			List<InternationalTerminalManager> tmm = this.commonService
//					.list("select tm from InternationalTerminalManager tm where tm.terminalNo='"
//							+ posNumber.trim() + "' ");
//			String billaddressby = null;
//			if (tmm.size() > 0) {
//				InternationalTerminalManager tm = tmm.get(0);
//				billaddressby = tm.getBillingAddress();
//			}
			String mailinfo = null;
			String billingAddress = "FPT*clothinglive";
			if(!"4212".equals((trade.getOrderNo()).substring(0,4))){
				try {
					EmailInfo emailinfo = new EmailInfo();
					mailinfo = emailinfo.getPaymentResultEmail(
							card.getEmail(),
							trade.getTradeAmount(),
							getStates().getCurrencyTypeByNo(
									trade.getMoneyType().intValue()),
							trade.getTradeUrl(), trade.getTradeTime(),
							billingAddress, trade.getMerchantOrderNo(),
							trade.getOrderNo(), merchant);
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
				}
			}
			logger.info("*********************支付结果返回码***************************"+responseCode);
			return SUCCESS;
		}else if(msg.getSucceed().equals("sfe01")){
			// 待处理
			this.responseCode = 19;

			MD5info = trade.getMerchantOrderNo()
					+ trade.getMoneyType() + ordercountValue
					+ responseCode + MD5key;
			md5Value = md5.getMD5ofStr(MD5info);
			trade.setTradeState("2"
					+ trade.getTradeState().substring(1,
							trade.getTradeState().length()));
			trade.setRemark("timeOut!");
			this.commonService.update(trade);
			shopManagerService.addTemporaryTradInfo(trade.getOrderNo(), year, month,cvv2,country,MD5key, ip,"MSIE 10.0","timeOut!");
			logger.info("*********************支付结果返回码***************************"+responseCode);
			return SUCCESS;
		}else if(msg.getSucceed().equals("19")){
			// 待处理
			this.responseCode = 19;

			MD5info = trade.getMerchantOrderNo()
					+ trade.getMoneyType() + ordercountValue
					+ responseCode + MD5key;
			md5Value = md5.getMD5ofStr(MD5info);
			trade.setTradeState("2"
					+ trade.getTradeState().substring(1,
							trade.getTradeState().length()));
			trade.setRemark("Waiting processing!");
			this.commonService.update(trade);
			//shopManagerService.addTemporaryTradInfo(tradeInfo.getOrderNo(), year, month,cvv2,country,MD5key, ip,"MSIE 10.0","timeOut!");
			logger.info("*********************支付结果返回码***************************"+responseCode);
			return SUCCESS;
		}else if(bankBackRemark.toLowerCase().indexOf(msg.getResult().toLowerCase())>=0){
			//"Refused transaction".equals(hwm.getRes_addMsg())||"Unknown error".equals(hwm.getRes_addMsg())||"Daily, monthly or yearly limits have been reached".equals(hwm.getRes_addMsg())||"AMOUNT NOT ALLOWED".equals(hwm.getRes_addMsg())
			this.responseCode = 19;

			MD5info = trade.getMerchantOrderNo()
					+ trade.getMoneyType() + ordercountValue
					+ responseCode + MD5key;
			md5Value = md5.getMD5ofStr(MD5info);
			trade.setTradeState("2"
					+ trade.getTradeState().substring(1,
							trade.getTradeState().length()));
			trade.setRemark("Waiting processing!");
			this.commonService.update(trade);
			shopManagerService.addTemporaryTradInfo(trade.getOrderNo(), year, month,cvv2,country,MD5key, ip,"MSIE 10.0","WP"+msg.getResult());
			logger.info("*********************支付结果返回码***************************"+responseCode);
			return SUCCESS;
		} else {
			this.message = "Payment Declined!"+"WPhighrisk!";
			this.responseCode = 0;
			trade.setTradeState("0"
					+ trade.getTradeState().substring(1,
							trade.getTradeState().length()));
			trade.setRemark(message);
			trade.setVIPDisposePorson("System");
			trade.setVIPDisposeDate(new Date());
			this.commonService.update(trade);
			MD5info = trade.getMerchantOrderNo()
					+ trade.getMoneyType() + ordercountValue
					+ responseCode + MD5key;
			md5Value = md5.getMD5ofStr(MD5info);
			logger.info("*********************支付结果返回码***************************"+responseCode);
			return SUCCESS;
		}
	}
	else if(chnals.equals("WR")){
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
		//wrp.setAmount(trade.getTradeAmount()+"");
	 	
	 	Double amountAndFee=trade.getRmbAmount();
		if(trade.getChannelFee()!=null){
			amountAndFee=amountAndFee*(trade.getChannelFee()+1.0);
			amountAndFee = (double) (Math.round((double) amountAndFee * 100) / 100.00);
		}
		wrp.setAmount(amountAndFee+"");
		
		/*if(trade.getMoneyType()==1){
			wrp.setCurrencyCode("USD");
		}else if (trade.getMoneyType()==2) {
			wrp.setCurrencyCode("EUR");
		}else if (trade.getMoneyType()==3) {
			wrp.setCurrencyCode("CNY");
		}else if (trade.getMoneyType()==4) {
			wrp.setCurrencyCode("GBP");
		}else if (trade.getMoneyType()==6) {
			wrp.setCurrencyCode("JPY");
		}else if (trade.getMoneyType()==7) {
			wrp.setCurrencyCode("AUD");
		}else if (trade.getMoneyType()==11) {
			wrp.setCurrencyCode("CAD");
		}*/
		wrp.setCurrencyCode("CNY");
				
		wrp.setOrderNo(trade.getOrderNo());
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
	 	wrp.setCardNO(cardnum);
	 	wrp.setCvv(cvv2);
	 	wrp.setExpYear("20"+year);
	 	wrp.setExpMonth(month);
	 	
	 	wrp.setPayIP(card.getIp().split(",")[0]);
		wrp.setMerMgrURL("www.sfepay.com");
		wrp.setReturnURL("www.sfepay.com");
		wrp.setNotifyURL("https://www.sfepay.com/onekpay");
		
		String Agent="";
		String userbrowser="";
		try{
		Agent = request.getHeader("User-Agent");  
		StringTokenizer st = new StringTokenizer(Agent,";");  
		st.nextToken();  
		//得到用户的浏览器名  
		userbrowser = st.nextToken();
		}catch(Exception e){
			logger.error(e);
			userbrowser=Agent;
		}
		if(StringUtils.isBlank(userbrowser)){
			userbrowser="MSIE 10.0";
		}
		wrp.setWebInfo(userbrowser);
		wrp.setLanguage("en");
	 	wrp.setMerremark("");
	 	wu.get(wrp);
				
		if (wrp.getRespCode().equals("00")) {//交易成功
			this.message = "Payment Success!";
			this.responseCode = 88;
			trade.setTradeState("1"
					+ trade.getTradeState().substring(1,
							trade.getTradeState().length()));
			trade.setRemark(message);
			trade.setVIPDisposePorson("System");
			trade.setVIPDisposeDate(new Date());
			trade.setVIPAuthorizationNo(wrp.getTerNo());
			this.commonService.update(trade);
			card.setExpiryDate("0000");
			card.setCvv2("XXX");
			this.commonService.update(card);
			MD5info = trade.getMerchantOrderNo()
					+ trade.getMoneyType() + ordercountValue
					+ responseCode + MD5key;
			md5Value = md5.getMD5ofStr(MD5info);
			
			logger.info("交易成功返回:"+merchantOrderNo+"**"+tradeMoneyType+"**"+ordercount+"**"+responseCode+"**"+message+"**"+ReturnURL+"**"+md5Value);
			String mailinfo = null;
			String hrBilladdress=wrp.getAcquirer();
			if(StringUtils.isBlank(hrBilladdress)||"null".equals(hrBilladdress)){
				hrBilladdress=it.get(0).getBillingAddress();
			}
			if(!"4212".equals((trade.getOrderNo()).substring(0,4))){
				try {
					EmailInfo emailinfo = new EmailInfo();
					mailinfo = emailinfo.getPaymentResultEmail(
							card.getEmail(),
							trade.getTradeAmount(),
							getStates().getCurrencyTypeByNo(
									trade.getMoneyType().intValue()),
							trade.getTradeUrl(), trade.getTradeTime(),
							hrBilladdress, trade.getMerchantOrderNo(),
							trade.getOrderNo(), merchant);
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
				}
			}
			logger.info("*********************支付结果返回码***************************"+responseCode);
			return SUCCESS;
		}
		if(wrp.getRespCode().equals("sfe01")){
			this.responseCode = 19;

			MD5info = trade.getMerchantOrderNo()
					+ trade.getMoneyType() + ordercountValue
					+ responseCode + MD5key;
			md5Value = md5.getMD5ofStr(MD5info);
			trade.setTradeState("2"
					+ trade.getTradeState().substring(1,
							trade.getTradeState().length()));
			trade.setRemark("timeOut!");
			this.commonService.update(trade);
			shopManagerService.addTemporaryTradInfo(trade.getOrderNo(), year, month,cvv2,country,MD5key, ip,"MSIE 10.0","timeOut!");
			logger.info("*********************支付结果返回码***************************"+responseCode);
			return SUCCESS;			
		}else if(bankBackRemark.toLowerCase().indexOf(wrp.getRespMsg().toLowerCase())>=0){
			//"拒绝交易".equals(hm.getRes_message())||"银行通讯故障".equals(hm.getRes_message())||"交易超时".equals(hm.getRes_message())
			this.responseCode = 19;

			MD5info = trade.getMerchantOrderNo()
					+ trade.getMoneyType() + ordercountValue
					+ responseCode + MD5key;
			md5Value = md5.getMD5ofStr(MD5info);
			trade.setTradeState("2"
					+ trade.getTradeState().substring(1,
							trade.getTradeState().length()));
			trade.setRemark("Waiting processing!");
			this.commonService.update(trade);
			shopManagerService.addTemporaryTradInfo(trade.getOrderNo(), year, month,cvv2,country,MD5key, ip,"MSIE 10.0","WR"+wrp.getRespMsg());
			logger.info("*********************支付结果返回码***************************"+responseCode);
			return SUCCESS;
		}else if(wrp.getRespCode().equals("02")||wrp.getRespCode().equals("03")){
			//"拒绝交易".equals(hm.getRes_message())||"银行通讯故障".equals(hm.getRes_message())||"交易超时".equals(hm.getRes_message())
			this.responseCode = 19;

			MD5info = trade.getMerchantOrderNo()
					+ trade.getMoneyType() + ordercountValue
					+ responseCode + MD5key;
			md5Value = md5.getMD5ofStr(MD5info);
			trade.setTradeState("2"
					+ trade.getTradeState().substring(1,
							trade.getTradeState().length()));
			trade.setRemark("Waiting processing!");
			this.commonService.update(trade);
			logger.info("*********************支付结果返回码***************************"+responseCode);
			return SUCCESS;
		}else {
			this.message = "Payment Declined!"+wrp.getRespMsg();
			this.responseCode = 0;
			trade.setTradeState("0"
					+ trade.getTradeState().substring(1,
							trade.getTradeState().length()));
			trade.setRemark(message);
			trade.setVIPDisposePorson("System");
			trade.setVIPDisposeDate(new Date());
			this.commonService.update(trade);
			MD5info = trade.getMerchantOrderNo()
					+ trade.getMoneyType() + ordercountValue
					+ responseCode + MD5key;
			md5Value = md5.getMD5ofStr(MD5info);
			logger.info("*********************支付结果返回码***************************"+responseCode);
			return SUCCESS;
		}
	}
	else if(chnals.equals("HR")){
		logger.info("进入HR通道");
		HRPayUtil hr=new HRPayUtil();
		HRPayMessage hrm=new HRPayMessage();
		hrm.setMerNo(posMerchantNo);
		hrm.setTransType("sales");
		Double amountAndFee=trade.getRmbAmount();
		if(trade.getChannelFee()!=null){
			amountAndFee=amountAndFee*(trade.getChannelFee()+1.0);
			amountAndFee = (double) (Math.round((double) amountAndFee * 100) / 100.00);
		}
		hrm.setAmount(amountAndFee+"");
		hrm.setCurrencyCode("CNY");
		hrm.setOrderNo(trade.getOrderNo());
		hrm.setSiteUrl(trade.getTradeUrl());
		String Agent="";
		String userbrowser="";
		try{
		Agent = request.getHeader("User-Agent");  
		StringTokenizer st = new StringTokenizer(Agent,";");  
		st.nextToken();  
		//得到用户的浏览器名  
		userbrowser = st.nextToken();
		}catch(Exception e){
			logger.error(e);
			userbrowser=Agent;
		}
		if(StringUtils.isBlank(userbrowser)){
			userbrowser="MSIE 10.0";
		}
		hrm.setWebInfo(userbrowser);
		hrm.setLanguage("En");
		hrm.setCardCountry(this.country.substring(3, 5));
		hrm.setCardState(state);
		hrm.setCardCity(city);
		hrm.setCardAddress(address);
		hrm.setCardZipCode(zipcode);
		hrm.setPayIP(ip.split(",")[0]);
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
		hrm.setGrFirstName(shippingFirstName);
		hrm.setGrLastName(shippingLastName);
		hrm.setpName(products);
		hrm.setMd5Key(it.get(0).getHashcode());
		hrm.setCardNO(cardnum);
		hrm.setExpYear("20"+year);
		hrm.setExpMonth(month);
		hrm.setCvv(cvv2);
		hr.get(hrm);
		
		if (hrm.getRes_respCode().equals("00")) {//交易成功
			this.message = "Payment Success!";
			this.responseCode = 88;
			trade.setTradeState("1"
					+ trade.getTradeState().substring(1,
							trade.getTradeState().length()));
			trade.setRemark(message);
			trade.setVIPDisposePorson("System");
			trade.setVIPDisposeDate(new Date());
			trade.setVIPAuthorizationNo(hrm.getRes_tradeNo());
			this.commonService.update(trade);
			card.setExpiryDate("0000");
			card.setCvv2("XXX");
			this.commonService.update(card);
			MD5info = trade.getMerchantOrderNo()
					+ trade.getMoneyType() + ordercountValue
					+ responseCode + MD5key;
			md5Value = md5.getMD5ofStr(MD5info);
			
			logger.info("交易成功返回:"+merchantOrderNo+"**"+tradeMoneyType+"**"+ordercount+"**"+responseCode+"**"+message+"**"+ReturnURL+"**"+md5Value);
			String mailinfo = null;
			String hrBilladdress=hrm.getRes_acquirer();
			if(StringUtils.isBlank(hrBilladdress)||"null".equals(hrBilladdress)){
				hrBilladdress=it.get(0).getBillingAddress();
			}
			if(!"4212".equals((trade.getOrderNo()).substring(0,4))){
				try {
					EmailInfo emailinfo = new EmailInfo();
					mailinfo = emailinfo.getPaymentResultEmail(
							card.getEmail(),
							trade.getTradeAmount(),
							getStates().getCurrencyTypeByNo(
									trade.getMoneyType().intValue()),
							trade.getTradeUrl(), trade.getTradeTime(),
							hrBilladdress, trade.getMerchantOrderNo(),
							trade.getOrderNo(), merchant);
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
				}
			}
			logger.info("*********************支付结果返回码***************************"+responseCode);
			return SUCCESS;
		}if(hrm.getRes_respCode().equals("sfe01")){
			this.responseCode = 19;

			MD5info = trade.getMerchantOrderNo()
					+ trade.getMoneyType() + ordercountValue
					+ responseCode + MD5key;
			md5Value = md5.getMD5ofStr(MD5info);
			trade.setTradeState("2"
					+ trade.getTradeState().substring(1,
							trade.getTradeState().length()));
			trade.setRemark("timeOut!");
			this.commonService.update(trade);
			shopManagerService.addTemporaryTradInfo(trade.getOrderNo(), year, month,cvv2,country,MD5key, ip,"MSIE 10.0","timeOut!");
			logger.info("*********************支付结果返回码***************************"+responseCode);
			return SUCCESS;
			
		}else if(bankBackRemark.toLowerCase().indexOf(hrm.getRes_respMsg().toLowerCase())>=0){
			//"拒绝交易".equals(hm.getRes_message())||"银行通讯故障".equals(hm.getRes_message())||"交易超时".equals(hm.getRes_message())
			this.responseCode = 19;

			MD5info = trade.getMerchantOrderNo()
					+ trade.getMoneyType() + ordercountValue
					+ responseCode + MD5key;
			md5Value = md5.getMD5ofStr(MD5info);
			trade.setTradeState("2"
					+ trade.getTradeState().substring(1,
							trade.getTradeState().length()));
			trade.setRemark("Waiting processing!");
			this.commonService.update(trade);
			shopManagerService.addTemporaryTradInfo(trade.getOrderNo(), year, month,cvv2,country,MD5key, ip,"MSIE 10.0","HR"+hrm.getRes_respMsg());
			logger.info("*********************支付结果返回码***************************"+responseCode);
			return SUCCESS;
		}else {
			this.message = "Payment Declined!"+hrm.getRes_respMsg();
			this.responseCode = 0;
			trade.setTradeState("0"
					+ trade.getTradeState().substring(1,
							trade.getTradeState().length()));
			trade.setRemark(message);
			trade.setVIPDisposePorson("System");
			trade.setVIPDisposeDate(new Date());
			this.commonService.update(trade);
			MD5info = trade.getMerchantOrderNo()
					+ trade.getMoneyType() + ordercountValue
					+ responseCode + MD5key;
			md5Value = md5.getMD5ofStr(MD5info);
			logger.info("*********************支付结果返回码***************************"+responseCode);
			return SUCCESS;
		}
	}else if(chnals.equals("IP")){
		logger.info("进入IPasspay通道");
			IPassPayMessage msg=new IPassPayMessage();
			IPassPayUtil yu=new IPassPayUtil();
			
			msg.setMid(posMerchantNo);
			msg.setOid(trade.getOrderNo());
			
			msg.setSite_id("371");
			Double amountAndFee=trade.getRmbAmount();
			if(trade.getChannelFee()!=null){
				amountAndFee=amountAndFee*(trade.getChannelFee()+1.0);
				amountAndFee = (double) (Math.round((double) amountAndFee * 100) / 100.00);
			}
			msg.setOrder_amount(amountAndFee+ "");
			
			msg.setOrder_currency("CNY");
			//'mid','site_id','order_id','order_amount','order_currency','api_key'.
			String sign=msg.getMid().trim()+msg.getSite_id().trim()+msg.getOid().trim()+msg.getOrder_amount().trim()+msg.getOrder_currency().trim()+it.get(0).getHashcode().trim();
			String strDes = getSha256(sign); 
			msg.setHash_info(strDes);
			
			msg.setCard_no(cardnum);
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
			msg.setSource_url(trade.getTradeUrl());//商户网站
			msg.setGateway_version("1.0");
			UUID uuid2 = UUID.randomUUID();
			msg.setUuid(uuid2.toString());
			
			//msg.setBillingstate(ic.getState());
						
			yu.get(msg);
			
			if (msg.getOrder_status().equals("2")) {//交易成功
				this.message = "Payment Success!";
				this.responseCode = 88;
				trade.setTradeState("1"
						+ trade.getTradeState().substring(1,
								trade.getTradeState().length()));
				trade.setRemark(message);
				trade.setVIPDisposePorson("System");
				trade.setVIPDisposeDate(new Date());
				trade.setVIPAuthorizationNo(msg.getPid());
				this.commonService.update(trade);
				card.setExpiryDate("0000");
				card.setCvv2("XXX");
				this.commonService.update(card);
				MD5info = trade.getMerchantOrderNo()
						+ trade.getMoneyType() + ordercountValue
						+ responseCode + MD5key;
				md5Value = md5.getMD5ofStr(MD5info);
				
				logger.info("交易成功返回:"+merchantOrderNo+"**"+tradeMoneyType+"**"+ordercount+"**"+responseCode+"**"+message+"**"+ReturnURL+"**"+md5Value);
				String mailinfo = null;
				String Billaddress=msg.getBilling_desc();
				if(!"4212".equals((trade.getOrderNo()).substring(0,4))){
					try {
						EmailInfo emailinfo = new EmailInfo();
						mailinfo = emailinfo.getPaymentResultEmail(
								card.getEmail(),
								trade.getTradeAmount(),
								getStates().getCurrencyTypeByNo(
										trade.getMoneyType().intValue()),
								trade.getTradeUrl(), trade.getTradeTime(),
								Billaddress, trade.getMerchantOrderNo(),
								trade.getOrderNo(), merchant);
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
					}
				}
				logger.info("*********************支付结果返回码***************************"+responseCode);
				return SUCCESS;
			}else if(msg.getOrder_status().equals("sfe01")){
				this.responseCode = 19;

				MD5info = trade.getMerchantOrderNo()
						+ trade.getMoneyType() + ordercountValue
						+ responseCode + MD5key;
				md5Value = md5.getMD5ofStr(MD5info);
				trade.setTradeState("2"
						+ trade.getTradeState().substring(1,
								trade.getTradeState().length()));
				trade.setRemark("timeOut!");
				this.commonService.update(trade);
				shopManagerService.addTemporaryTradInfo(trade.getOrderNo(), year, month,cvv2,country,MD5key, ip,"MSIE 10.0","timeOut!");
				logger.info("*********************支付结果返回码***************************"+responseCode);
				return SUCCESS;
			}else if(msg.getOrder_status().equals("1")||msg.getOrder_status().equals("3")){
				this.responseCode = 19;

				MD5info = trade.getMerchantOrderNo()
						+ trade.getMoneyType() + ordercountValue
						+ responseCode + MD5key;
				md5Value = md5.getMD5ofStr(MD5info);
				trade.setTradeState("2"
						+ trade.getTradeState().substring(1,
								trade.getTradeState().length()));
				trade.setRemark("Waiting processing!");
				this.commonService.update(trade);
				shopManagerService.addTraderun(trade.getOrderNo(), year, month,cvv2,country,MD5key, ip,"MSIE 10.0","Waiting processing!");
				//shopManagerService.addTemporaryTradInfo(trade.getOrderNo(), year, month,cvv2,country,MD5key, ip,"MSIE 10.0","MS"+masaM.getRes_errMsg());
				logger.info("*********************支付结果返回码***************************"+responseCode);
				return SUCCESS;		
			}else if(bankBackRemark.toLowerCase().indexOf(msg.getInfo().toLowerCase())>=0){
				//"拒绝交易".equals(hm.getRes_message())||"银行通讯故障".equals(hm.getRes_message())||"交易超时".equals(hm.getRes_message())
				this.responseCode = 19;

				MD5info = trade.getMerchantOrderNo()
						+ trade.getMoneyType() + ordercountValue
						+ responseCode + MD5key;
				md5Value = md5.getMD5ofStr(MD5info);
				trade.setTradeState("2"
						+ trade.getTradeState().substring(1,
								trade.getTradeState().length()));
				trade.setRemark("Waiting processing!");
				this.commonService.update(trade);
				shopManagerService.addTemporaryTradInfo(trade.getOrderNo(), year, month,cvv2,country,MD5key, ip,"MSIE 10.0","IP"+msg.getInfo());
				logger.info("*********************支付结果返回码***************************"+responseCode);
				return SUCCESS;
			}else {
				this.message = "Payment Declined!"+msg.getInfo();
				this.responseCode = 0;
				trade.setTradeState("0"
						+ trade.getTradeState().substring(1,
								trade.getTradeState().length()));
				trade.setRemark(message);
				trade.setVIPDisposePorson("System");
				trade.setVIPDisposeDate(new Date());
				this.commonService.update(trade);
				MD5info = trade.getMerchantOrderNo()
						+ trade.getMoneyType() + ordercountValue
						+ responseCode + MD5key;
				md5Value = md5.getMD5ofStr(MD5info);
				logger.info("*********************支付结果返回码***************************"+responseCode);
				return SUCCESS;
			}	
	}else if(chnals.equals("MS")){
		 logger.info("进入MS通道");
		 MasaPayMessage masaM=new MasaPayMessage();
		 MasaPayUtil masaU=new MasaPayUtil();
		 masaM.setVersion("1.6");
		 masaM.setMerchantId(posMerchantNo);
		 masaM.setCharset("utf-8");
		 masaM.setLanguage("en");
		 masaM.setSignType("SHA256");
		 masaM.setMerchantOrderNo(trade.getOrderNo());
		 masaM.setGoodsName(card.getProductInfo());
		 masaM.setGoodsDesc(card.getProductInfo());
		 masaM.setOrderExchange("2");
		 masaM.setCurrencyCode("CNY");
		 Double amountAndFee=trade.getRmbAmount();
		 if(trade.getChannelFee()!=null){
			amountAndFee=amountAndFee*(trade.getChannelFee()+1.0);
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
		 masaM.setCardNumber(cardnum);
		 masaM.setCardHolderLastName(lastname);
		 masaM.setCardHolderFirstName(firstname);
		 masaM.setCardExpirationMonth(month);
		 masaM.setCardExpirationYear("20"+year);
		 masaM.setSecurityCode(cvv2);
		 masaM.setCardHolderEmail(email);
		 masaM.setCardHolderPhoneNumber(phone);
		 masaM.setBillName(firstname+" "+lastname);
		 masaM.setBillAddress(address);
		 masaM.setBillPostalCode(zipcode);
		 masaM.setBillCountry(this.country.substring(3, 5));
		 
		 if(StringUtils.isBlank(state)){
			 state = "state";
		 }
		 
		 masaM.setBillState(state);
		 masaM.setBillCity(city);
		 masaM.setBillEmail(email);
		 masaM.setBillPhoneNumber(phone);
		 masaM.setShippingName(shippingFirstName+shippingLastName);
		 masaM.setShippingAddress(shippingAddress);
		 masaM.setShippingPostalCode(shippingZipcode);
		 masaM.setShippingCountry(this.shippingCountry.substring(3, 5));
		 
		 if(StringUtils.isBlank(shippingSstate)){
			 shippingSstate = "state";
		 }
		 
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
		
		if (masaM.getRes_resultCode().equals("10")) {//交易成功
			this.message = "Payment Success!";
			this.responseCode = 88;
			trade.setTradeState("1"
					+ trade.getTradeState().substring(1,
							trade.getTradeState().length()));
			trade.setRemark(message);
			trade.setVIPDisposePorson("System");
			trade.setVIPDisposeDate(new Date());
			trade.setVIPAuthorizationNo(masaM.getRes_masapayOrderNo());
			this.commonService.update(trade);
			card.setExpiryDate("0000");
			card.setCvv2("XXX");
			this.commonService.update(card);
			MD5info = trade.getMerchantOrderNo()
					+ trade.getMoneyType() + ordercountValue
					+ responseCode + MD5key;
			md5Value = md5.getMD5ofStr(MD5info);
			
			logger.info("交易成功返回:"+merchantOrderNo+"**"+tradeMoneyType+"**"+ordercount+"**"+responseCode+"**"+message+"**"+ReturnURL+"**"+md5Value);
			String mailinfo = null;			
			if(!"4212".equals((trade.getOrderNo()).substring(0,4))){
				try {
					EmailInfo emailinfo = new EmailInfo();
					mailinfo = emailinfo.getPaymentResultEmail(
							card.getEmail(),
							trade.getTradeAmount(),
							getStates().getCurrencyTypeByNo(
									trade.getMoneyType().intValue()),
							trade.getTradeUrl(), trade.getTradeTime(),
							it.get(0).getBillingAddress(), trade.getMerchantOrderNo(),
							trade.getOrderNo(), merchant);
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
				}
			}
			logger.info("*********************支付结果返回码***************************"+responseCode);
			return SUCCESS;
		}if(masaM.getRes_resultCode().equals("19")){
			this.responseCode = 19;

			MD5info = trade.getMerchantOrderNo()
					+ trade.getMoneyType() + ordercountValue
					+ responseCode + MD5key;
			md5Value = md5.getMD5ofStr(MD5info);
			trade.setTradeState("2"
					+ trade.getTradeState().substring(1,
							trade.getTradeState().length()));
			trade.setRemark("timeOut!");
			this.commonService.update(trade);
			shopManagerService.addTemporaryTradInfo(trade.getOrderNo(), year, month,cvv2,country,MD5key, ip,"MSIE 10.0","timeOut!");
			logger.info("*********************支付结果返回码***************************"+responseCode);
			return SUCCESS;
		}else if(masaM.getRes_resultCode().equals("00")){
			this.responseCode = 19;

			MD5info = trade.getMerchantOrderNo()
					+ trade.getMoneyType() + ordercountValue
					+ responseCode + MD5key;
			md5Value = md5.getMD5ofStr(MD5info);
			trade.setTradeState("2"
					+ trade.getTradeState().substring(1,
							trade.getTradeState().length()));
			trade.setRemark("Waiting processing!");
			if(StringUtils.isNotBlank(masaM.getRes_masapayOrderNo())){
				trade.setVIPAuthorizationNo(masaM.getRes_masapayOrderNo());
			}
			this.commonService.update(trade);
			shopManagerService.addTraderun(trade.getOrderNo(), year, month,cvv2,country,MD5key, ip,"MSIE 10.0","Waiting processing!");
			//shopManagerService.addTemporaryTradInfo(trade.getOrderNo(), year, month,cvv2,country,MD5key, ip,"MSIE 10.0","MS"+masaM.getRes_errMsg());
			logger.info("*********************支付结果返回码***************************"+responseCode);
			return SUCCESS;	
		}else if(bankBackRemark.toLowerCase().indexOf(masaM.getRes_errMsg().toLowerCase())>=0){
			this.responseCode = 19;

			MD5info = trade.getMerchantOrderNo()
					+ trade.getMoneyType() + ordercountValue
					+ responseCode + MD5key;
			md5Value = md5.getMD5ofStr(MD5info);
			trade.setTradeState("2"
					+ trade.getTradeState().substring(1,
							trade.getTradeState().length()));
			trade.setRemark("Waiting processing!");
			this.commonService.update(trade);
			//shopManagerService.addTraderun(trade.getOrderNo(), year, month,cvv2,country,MD5key, ip,"MSIE 10.0","MS"+masaM.getRes_errMsg());
			shopManagerService.addTemporaryTradInfo(trade.getOrderNo(), year, month,cvv2,country,MD5key, ip,"MSIE 10.0","MS"+masaM.getRes_errMsg());
			logger.info("*********************支付结果返回码***************************"+responseCode);
			return SUCCESS;
		}else {
			this.message = "Payment Declined!"+masaM.getRes_errMsg();
			this.responseCode = 0;
			trade.setTradeState("0"
					+ trade.getTradeState().substring(1,
							trade.getTradeState().length()));
			trade.setRemark(message);
			trade.setVIPDisposePorson("System");
			trade.setVIPDisposeDate(new Date());
			this.commonService.update(trade);
			MD5info = trade.getMerchantOrderNo()
					+ trade.getMoneyType() + ordercountValue
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
		 masaM.setMerchantOrderNo(trade.getOrderNo());
		 masaM.setGoodsName(card.getProductInfo());
		 masaM.setGoodsDesc(card.getProductInfo());
		 masaM.setOrderExchange("2");
		 masaM.setCurrencyCode("CNY");
		 Double amountAndFee=trade.getRmbAmount();
		 if(trade.getChannelFee()!=null){
			amountAndFee=amountAndFee*(trade.getChannelFee()+1.0);
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
		 masaM.setCardNumber(cardnum);
		 masaM.setCardHolderLastName(lastname);
		 masaM.setCardHolderFirstName(firstname);
		 masaM.setCardExpirationMonth(month);
		 masaM.setCardExpirationYear("20"+year);
		 masaM.setSecurityCode(cvv2);
		 masaM.setCardHolderEmail(email);
		 masaM.setCardHolderPhoneNumber(phone);
		 masaM.setBillName(firstname+" "+lastname);
		 masaM.setBillAddress(address);
		 masaM.setBillPostalCode(zipcode);
		 masaM.setBillCountry(this.country.substring(3, 5));
		 masaM.setBillState(state);
		 masaM.setBillCity(city);
		 masaM.setBillEmail(email);
		 masaM.setBillPhoneNumber(phone);
		 masaM.setShippingName(shippingFirstName+shippingLastName);
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
		
		if (masaM.getRes_resultCode().equals("10")) {//交易成功
			this.message = "Payment Success!";
			this.responseCode = 88;
			trade.setTradeState("1"
					+ trade.getTradeState().substring(1,
							trade.getTradeState().length()));
			trade.setRemark(message);
			trade.setVIPDisposePorson("System");
			trade.setVIPDisposeDate(new Date());
			trade.setVIPAuthorizationNo(masaM.getRes_masapayOrderNo());
			this.commonService.update(trade);
			card.setExpiryDate("0000");
			card.setCvv2("XXX");
			this.commonService.update(card);
			MD5info = trade.getMerchantOrderNo()
					+ trade.getMoneyType() + ordercountValue
					+ responseCode + MD5key;
			md5Value = md5.getMD5ofStr(MD5info);
			
			logger.info("交易成功返回:"+merchantOrderNo+"**"+tradeMoneyType+"**"+ordercount+"**"+responseCode+"**"+message+"**"+ReturnURL+"**"+md5Value);
			String mailinfo = null;			
			if(!"4212".equals((trade.getOrderNo()).substring(0,4))){
				try {
					EmailInfo emailinfo = new EmailInfo();
					mailinfo = emailinfo.getPaymentResultEmail(
							card.getEmail(),
							trade.getTradeAmount(),
							getStates().getCurrencyTypeByNo(
									trade.getMoneyType().intValue()),
							trade.getTradeUrl(), trade.getTradeTime(),
							it.get(0).getBillingAddress(), trade.getMerchantOrderNo(),
							trade.getOrderNo(), merchant);
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
				}
			}
			logger.info("*********************支付结果返回码***************************"+responseCode);
			return SUCCESS;
		}if(masaM.getRes_resultCode().equals("19")){
			this.responseCode = 19;

			MD5info = trade.getMerchantOrderNo()
					+ trade.getMoneyType() + ordercountValue
					+ responseCode + MD5key;
			md5Value = md5.getMD5ofStr(MD5info);
			trade.setTradeState("2"
					+ trade.getTradeState().substring(1,
							trade.getTradeState().length()));
			trade.setRemark("timeOut!");
			this.commonService.update(trade);
			shopManagerService.addTemporaryTradInfo(trade.getOrderNo(), year, month,cvv2,country,MD5key, ip,"MSIE 10.0","timeOut!");
			logger.info("*********************支付结果返回码***************************"+responseCode);
			return SUCCESS;
		}else if(masaM.getRes_resultCode().equals("00")){
			this.responseCode = 19;

			MD5info = trade.getMerchantOrderNo()
					+ trade.getMoneyType() + ordercountValue
					+ responseCode + MD5key;
			md5Value = md5.getMD5ofStr(MD5info);
			trade.setTradeState("2"
					+ trade.getTradeState().substring(1,
							trade.getTradeState().length()));
			trade.setRemark("Waiting processing!");
			this.commonService.update(trade);
			shopManagerService.addTraderun(trade.getOrderNo(), year, month,cvv2,country,MD5key, ip,"MSIE 10.0","Waiting processing!");
			//shopManagerService.addTemporaryTradInfo(trade.getOrderNo(), year, month,cvv2,country,MD5key, ip,"MSIE 10.0","MS"+masaM.getRes_errMsg());
			logger.info("*********************支付结果返回码***************************"+responseCode);
			return SUCCESS;	
		}else if(bankBackRemark.toLowerCase().indexOf(masaM.getRes_errMsg().toLowerCase())>=0){
			this.responseCode = 19;

			MD5info = trade.getMerchantOrderNo()
					+ trade.getMoneyType() + ordercountValue
					+ responseCode + MD5key;
			md5Value = md5.getMD5ofStr(MD5info);
			trade.setTradeState("2"
					+ trade.getTradeState().substring(1,
							trade.getTradeState().length()));
			trade.setRemark("Waiting processing!");
			this.commonService.update(trade);
			//shopManagerService.addTraderun(trade.getOrderNo(), year, month,cvv2,country,MD5key, ip,"MSIE 10.0","MS"+masaM.getRes_errMsg());
			shopManagerService.addTemporaryTradInfo(trade.getOrderNo(), year, month,cvv2,country,MD5key, ip,"MSIE 10.0","GR"+masaM.getRes_errMsg());
			logger.info("*********************支付结果返回码***************************"+responseCode);
			return SUCCESS;
		}else {
			this.message = "Payment Declined!"+masaM.getRes_errMsg();
			this.responseCode = 0;
			trade.setTradeState("0"
					+ trade.getTradeState().substring(1,
							trade.getTradeState().length()));
			trade.setRemark(message);
			trade.setVIPDisposePorson("System");
			trade.setVIPDisposeDate(new Date());
			this.commonService.update(trade);
			MD5info = trade.getMerchantOrderNo()
					+ trade.getMoneyType() + ordercountValue
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

	public boolean checkAll() {
		boolean checkemail = false;
		boolean checkcvv = false;
		boolean checkmonth = false;
		boolean checkyear = false;
		boolean checkcardno = false;
		// 验证邮箱
		Pattern p = Pattern
				.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
		Matcher m = p.matcher(this.getEmail());
		// Matcher m1 = p.matcher(this.getShippingEmail());
		if (!m.find()) {
			checkemail = true;
		}
		// else if(!m1.find()){
		// check = true;
		// }
		if (this.getCvv2().length() != 3) {
			checkcvv = true;
		}
		if (!StringUtils.isNotBlank(this.getMonth())) {
			checkmonth = true;
		}
		if (!StringUtils.isNotBlank(this.getYear())) {
			checkyear = true;
		}
		if (!CheckCardNo.isValid(this.getCardnum())) {
			checkcardno = true;
		}
		if (!checkemail && !checkcvv && !checkmonth && !checkyear
				&& !checkcardno) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * ip
	 */
	public static String getIpAddr(HttpServletRequest request) {
		String ip = request.getHeader("X-Forwarded-For");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_CLIENT_IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_X_FORWARDED_FOR");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		// System.out.println("ip----------------"+ip);
		return ip;

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
				ipRegionName=maxmindValue;
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

	private Map createMapFromResponse(String queryString) {
		Map map = new HashMap();
		StringTokenizer st = new StringTokenizer(queryString, "&");
		while (st.hasMoreTokens()) {
			String token = st.nextToken();
			int i = token.indexOf('=');
			if (i > 0) {
				try {
					String key = token.substring(0, i);
					String value = URLDecoder.decode(token.substring(i + 1,
							token.length()));
					map.put(key, value);
				} catch (Exception ex) {
					// Do Nothing and keep looping through data
				}
			}
		}
		return map;
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
	public Boolean validateRiskItems(String riskitems,String riskType,String riskMerNo){
			String hql=" from InternationalRiskItems t where t.itemType='"+riskType+"' and t.merchantNo='"+riskMerNo+"'";
			List<InternationalRiskItems> list=commonService.list(hql);
			if(list==null||list.size()==0){
				String hql2=" from InternationalRiskItems t where t.itemType='"+riskType+"' and t.merchantNo is null";
				list=commonService.list(hql2);
			}
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
	//网站所属项失败
//		public Boolean validateWebItems(String riskWeb,String riskCountry,String maxMindCountry,String maxMindState,String riskState){
//				String hql=" from InternationalRiskItems t where t.tradeWeb='"+riskWeb+"'";
//				List<InternationalRiskItems> list=commonService.list(hql);
//				for(InternationalRiskItems items:list){
//					if("1".equals(items.getItemType())){
//						if(StringUtils.isNotBlank(riskCountry)){
//							if(riskCountry.toLowerCase().indexOf(items.getItemName().toLowerCase())>=0){
//								logger.info("网站风险匹配项：国家>>"+items.getItemName());	
//								return true;
//							}
//						}
//						if(StringUtils.isNotBlank(maxMindCountry)){
//							if(maxMindCountry.toLowerCase().indexOf(items.getItemName().toLowerCase())>=0){
//								logger.info("网站风险匹配项：国家>>"+items.getItemName());	
//								return true;
//							}
//						}
//					}
//					if("2".equals(items.getItemType())){
//						if(StringUtils.isNotBlank(maxMindState)){
//							if(maxMindState.toLowerCase().indexOf(items.getItemName().toLowerCase())>=0){
//								logger.info("网站风险匹配项：区域>>"+items.getItemName());	
//								return true;
//							}
//						}
//						if(StringUtils.isNotBlank(riskState)){
//							if(riskState.toLowerCase().indexOf(items.getItemName().toLowerCase())>=0){
//								logger.info("网站风险匹配项：区域>>"+items.getItemName());	
//								return true;
//							}
//						}
//					}
//				}
//			return false;
//			}
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
	public String getRorderno() {
		return rorderno;
	}

	public void setRorderno(String rorderno) {
		this.rorderno = rorderno;
	}

	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public Long getDate() {
		return date;
	}

	public void setDate(Long date) {
		this.date = date;
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

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
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

	public String getCardbank() {
		return cardbank;
	}

	public void setCardbank(String cardbank) {
		this.cardbank = cardbank;
	}

	public String getCardnum() {
		return cardnum;
	}

	public void setCardnum(String cardnum) {
		this.cardnum = cardnum;
	}

	public String getCvv2() {
		return cvv2;
	}

	public void setCvv2(String cvv2) {
		this.cvv2 = cvv2;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getCookie() {
		return cookie;
	}

	public void setCookie(String cookie) {
		this.cookie = cookie;
	}

	public String getCookieId() {
		return cookieId;
	}

	public void setCookieId(String cookieId) {
		this.cookieId = cookieId;
	}

	public String getVpc_Card() {
		return vpc_Card;
	}

	public void setVpc_Card(String vpc_Card) {
		this.vpc_Card = vpc_Card;
	}

	public String getMaxmindValue() {
		return maxmindValue;
	}

	public void setMaxmindValue(String maxmindValue) {
		this.maxmindValue = maxmindValue;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getBankCountry() {
		return bankCountry;
	}

	public void setBankCountry(String bankCountry) {
		this.bankCountry = bankCountry;
	}

	public String getBankPhone() {
		return bankPhone;
	}

	public void setBankPhone(String bankPhone) {
		this.bankPhone = bankPhone;
	}

	public String getValues() {
		return values;
	}

	public void setValues(String values) {
		this.values = values;
	}

	public MaxMindExample getExam() {
		return exam;
	}

	public void setExam(MaxMindExample exam) {
		this.exam = exam;
	}

	public String getReturnURL() {
		return ReturnURL;
	}

	public void setReturnURL(String returnURL) {
		ReturnURL = returnURL;
	}

	public int getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}

	public String getMD5key() {
		return MD5key;
	}

	public void setMD5key(String md5key) {
		MD5key = md5key;
	}

	public Double getOrdercount() {
		return ordercount;
	}

	public void setOrdercount(Double ordercount) {
		this.ordercount = ordercount;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getMD5info() {
		return MD5info;
	}

	public void setMD5info(String md5info) {
		MD5info = md5info;
	}

	public String getTradeMoneyType() {
		return tradeMoneyType;
	}

	public void setTradeMoneyType(String tradeMoneyType) {
		this.tradeMoneyType = tradeMoneyType;
	}

	public String getMerchantOrderNo() {
		return merchantOrderNo;
	}

	public void setMerchantOrderNo(String merchantOrderNo) {
		this.merchantOrderNo = merchantOrderNo;
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

	public String getShippingEmail() {
		return shippingEmail;
	}

	public void setShippingEmail(String shippingEmail) {
		this.shippingEmail = shippingEmail;
	}

	public String getShippingPhone() {
		return shippingPhone;
	}

	public void setShippingPhone(String shippingPhone) {
		this.shippingPhone = shippingPhone;
	}

	public String getShippingZipcode() {
		return shippingZipcode;
	}

	public void setShippingZipcode(String shippingZipcode) {
		this.shippingZipcode = shippingZipcode;
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

	public String getVpc_CardExp() {
		return vpc_CardExp;
	}

	public void setVpc_CardExp(String vpc_CardExp) {
		this.vpc_CardExp = vpc_CardExp;
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

	public String getAmount_Transaction_Foreign() {
		return amount_Transaction_Foreign;
	}

	public void setAmount_Transaction_Foreign(String amount_Transaction_Foreign) {
		this.amount_Transaction_Foreign = amount_Transaction_Foreign;
	}

	public String getConversion_Rate() {
		return conversion_Rate;
	}

	public void setConversion_Rate(String conversion_Rate) {
		this.conversion_Rate = conversion_Rate;
	}

	public String getCurrency_Code_Foreign() {
		return currency_Code_Foreign;
	}

	public void setCurrency_Code_Foreign(String currency_Code_Foreign) {
		this.currency_Code_Foreign = currency_Code_Foreign;
	}

	public String getTradeType() {
		return tradeType;
	}

	public void setTradeType(String tradeType) {
		this.tradeType = tradeType;
	}

	public TradeManager getTradeManager() {
		return tradeManager;
	}

	public void setTradeManager(TradeManager tradeManager) {
		this.tradeManager = tradeManager;
	}

	public String getVirtualPaymentClientURL() {
		return virtualPaymentClientURL;
	}

	public void setVirtualPaymentClientURL(String virtualPaymentClientURL) {
		this.virtualPaymentClientURL = virtualPaymentClientURL;
	}

	public String getAccessCode() {
		return accessCode;
	}

	public void setAccessCode(String accessCode) {
		this.accessCode = accessCode;
	}

	public String getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}

	public int getTradeOrder() {
		return tradeOrder;
	}

	public void setTradeOrder(int tradeOrder) {
		this.tradeOrder = tradeOrder;
	}

	public String getConnectURL() {
		return connectURL;
	}

	public void setConnectURL(String connectURL) {
		this.connectURL = connectURL;
	}

	public String getVpc_CardNum() {
		return vpc_CardNum;
	}

	public void setVpc_CardNum(String vpc_CardNum) {
		this.vpc_CardNum = vpc_CardNum;
	}

	public String getProducts() {
		return products;
	}

	public void setProducts(String products) {
		this.products = products;
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

	public String getThesame() {
		return thesame;
	}

	public void setThesame(String thesame) {
		this.thesame = thesame;
	}

	public String getSECURE_SECRET() {
		return SECURE_SECRET;
	}

	public void setSECURE_SECRET(String secure_secret) {
		SECURE_SECRET = secure_secret;
	}

	public String getLocal_money() {
		return local_money;
	}

	public void setLocal_money(String local_money) {
		this.local_money = local_money;
	}

	public String getForeign_money() {
		return foreign_money;
	}

	public void setForeign_money(String foreign_money) {
		this.foreign_money = foreign_money;
	}

	public String getConversion_Rate_show() {
		return conversion_Rate_show;
	}

	public void setConversion_Rate_show(String conversion_Rate_show) {
		this.conversion_Rate_show = conversion_Rate_show;
	}

	public String getCar_termanal() {
		return car_termanal;
	}

	public void setCar_termanal(String car_termanal) {
		this.car_termanal = car_termanal;
	}

	public String getCardMessage() {
		return cardMessage;
	}

	public void setCardMessage(String cardMessage) {
		this.cardMessage = cardMessage;
	}

	public String getNewcardtype() {
		return newcardtype;
	}

	public void setNewcardtype(String newcardtype) {
		this.newcardtype = newcardtype;
	}

	public String getUrlType() {
		return urlType;
	}

	public void setUrlType(String urlType) {
		this.urlType = urlType;
	}

	public String getAddIp() {
		return addIp;
	}

	public void setAddIp(String addIp) {
		this.addIp = addIp;
	}

	public String getIsQWeb() {
		return isQWeb;
	}

	public void setIsQWeb(String isQWeb) {
		this.isQWeb = isQWeb;
	}

	private String getNavigator(String userAgent) {

		try {
			if (userAgent.indexOf("TencentTraveler") > 0)
				return "TT(QQ) Browser";
			if (userAgent.indexOf("Maxthon") > 0)
				return "Maxthon Browser";
			if (userAgent.indexOf("MyIE2") > 0)
				return "MyIE2 Browser";
			if (userAgent.indexOf("Opera") > 0) {
				int i = userAgent.indexOf("Opera");
				String tt = userAgent.substring(i, i + 10);
				return tt + " Browser";
			}
			if (userAgent.indexOf("Chrome") > 0
					&& userAgent.indexOf("Safari") > 0) {
				int i = userAgent.indexOf("Chrome");
				String tt = userAgent.substring(i, i + 10);
				return tt + " Browser";
			}
			if (userAgent.indexOf("Safari") > 0)
				return "Safari Browser";
			if (userAgent.indexOf("Firefox") > 0) {
				int i = userAgent.indexOf("Firefox");
				String tt = userAgent.substring(i, i + 12);
				return "Firefox Browser";
			}
			if (userAgent.indexOf("MSIE 6") > 0)
				return "IE6 Browser";
			if (userAgent.indexOf("MSIE 7") > 0)
				return "IE7 Browser";
			if (userAgent.indexOf("MSIE 8") > 0)
				return "IE8 Browser";
			if (userAgent.indexOf("MSIE 9") > 0)
				return "IE9 Browser";
		} catch (Exception e) {
			// 出错时使用另外的方式获取买家浏览器信息
			// UserAgent userAgentOther = UserAgent.parseUserAgentString(request
			// .getHeader("User-Agent"));
			// return userAgentOther.getBrowser().getName();
			return "unkown Browser";
		}
		return "unkown Browser";
	}

	private String getOS(String userAgent) {
		try {
			if (userAgent.indexOf("Windows NT 5.1") > 0)
				return "Windows XP";
			if (userAgent.indexOf("Windows NT 5.2") > 0)
				return "Windows 2003";
			if (userAgent.indexOf("Windows 98") > 0)
				return "Windows 98";
			if (userAgent.indexOf("Windows NT 5.0") > 0)
				return "Windows 2000";
			if (userAgent.indexOf("Linux") > 0)
				return "Linux";
			if (userAgent.indexOf("Mac") > 0)
				return "Mac OS";
			if (userAgent.indexOf("Windows NT 6.0") > 0)
				return "Windows Vista";
			if (userAgent.indexOf("Windows NT 6.1") > 0)
				return "Windows 7";
			if (userAgent.indexOf("Unix") > 0)
				return "Unix";
		} catch (Exception e) {
			// 出错时使用另外方式获取买家操作系统以及终端信息
			// UserAgent userAgentOther = UserAgent.parseUserAgentString(request
			// .getHeader("User-Agent"));
			// return userAgentOther.getOperatingSystem().getName()
			// + "##终端类型:"
			// + userAgentOther.getOperatingSystem().getDeviceType()
			// .getName();
			return "unkown OS";
		}
		return "unkown OS";
	}
}
