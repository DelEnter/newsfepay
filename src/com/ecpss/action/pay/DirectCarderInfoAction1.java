package com.ecpss.action.pay;

import com.ecpss.action.BaseAction;
import com.ecpss.action.pay.fast.TradUtil;
import com.ecpss.action.pay.fast.TradeMessage;
import com.ecpss.action.pay.util.CheckCardNo;
import com.ecpss.action.pay.util.MaxMindExample;
import com.ecpss.model.channel.InternationalMerchantChannels;
import com.ecpss.model.payment.InternationalCardholdersInfo;
import com.ecpss.model.payment.InternationalTradeinfo;
import com.ecpss.model.risk.InternationalBacklist;
import com.ecpss.model.risk.InternationalHighRisklist;
import com.ecpss.model.risk.InternationalSensitiveInfo;
import com.ecpss.model.shop.InternationalMerchant;
import com.ecpss.model.shop.InternationalMerchantManager;
import com.ecpss.model.shop.InternationalTerminalManager;
import com.ecpss.model.shop.InternationalTradecondition;
import com.ecpss.service.common.CommonService;
import com.ecpss.service.iservice.ShopManagerService;
import com.ecpss.util.AES;
import com.ecpss.util.CCSendMail;
import com.ecpss.util.EmailInfo;
import com.ecpss.util.GetBatchNo;
import com.ecpss.util.MD5;
import com.ecpss.util.StateUtils;
import com.ecpss.util.StringUtil;

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
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import sun.misc.BASE64Decoder;
import vpn.CaibaoMessage;
import vpn.CaibaoUtil;
import vpn.DCCMessage;
import vpn.MotoDCCMessage;
import vpn.SfeMessage;
import vpn.SfeUtil;
import vpn.VpnUtil;
import vpn.VpnUtil_Moto;
import vpn.YinlianMessage;
import vpn.YinlianUtil;

public class DirectCarderInfoAction1 extends BaseAction
{

  @Autowired
  @Qualifier("tradeManager")
  private TradeManager tradeManager;

  @Autowired
  @Qualifier("shopManagerService")
  private ShopManagerService shopManagerService;
  private String thesame = "";
  private HashMap h = new HashMap();
  private String rorderno = "";
  private Date birthday;
  private String year;
  private String month;
  private Long date;
  private String firstname;
  private String lastname;
  private String country;
  private String state;
  private String city;
  private String address;
  private String zipcode;
  private String email;
  private String phone;
  private String cardbank;
  private String ReturnURL;
  private String MD5key;
  private String cardnum;
  private String cvv2;
  private String ip;
  private String cookie;
  private String cookieId;
  private Double ordercount;
  private int responseCode;
  private String remark;
  private String MD5info;
  private String tradeMoneyType;
  private String merchantOrderNo;
  private String md5Value;
  private String message;
  private String vpc_Card;
  private String maxmindValue;
  String bankName = null;
  String bankCountry = null;
  String bankPhone = null;
  private String values;
  private String SECURE_SECRET;
  private MaxMindExample exam = new MaxMindExample();
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
  private String virtualPaymentClientURL;
  private String accessCode;
  private String merchantId;
  private String vpc_CardExp;
  private int tradeOrder;
  private String connectURL;
  private String vpc_CardNum;
  private String amount_Transaction_Foreign;
  private String conversion_Rate;
  private String currency_Code_Foreign;
  private String tradeType;
  private String local_money;
  private String foreign_money;
  private String conversion_Rate_show;
  private String car_termanal;
  private String cardMessage;
  private String newcardtype;
  private String token_id = "";
  private String HASH = "";
  private String Send_Url = "";
  private String urlType;
  private String addIp;
  private String redInfo;
  private String userbrowser;
  private Hashtable maxMindInfo;
  Logger logger = Logger.getLogger(DirectCarderInfoAction.class.getName());

  public String getRedInfo()
  {
    return this.redInfo;
  }

  public void setRedInfo(String redInfo) {
    this.redInfo = redInfo;
  }

  public String getToken_id() {
    return this.token_id;
  }

  public void setToken_id(String token_id) {
    this.token_id = token_id;
  }

  public String getHASH() {
    return this.HASH;
  }

  public void setHASH(String hASH) {
    this.HASH = hASH;
  }

  public String getSend_Url() {
    return this.Send_Url;
  }

  public void setSend_Url(String send_Url) {
    this.Send_Url = send_Url;
  }

  public String addCardMessage()
  {
    try
    {
    	if(StringUtils.isBlank(shippingCountry)||shippingCountry.length()<5){
			this.shippingCountry="USAUS";
		}
		if(StringUtils.isBlank(shippingFirstName)){
			this.shippingFirstName="test";
		}
		if(StringUtils.isBlank(shippingLastName)){
			this.shippingLastName="test";
		}
		if(StringUtils.isBlank(cardbank)){
			this.cardbank="test";
		}
		//___________________
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
				calendar1.add(Calendar.SECOND,-30); // 得到前30秒
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
//				if (StringUtils.isNotBlank(newcardtype)) {
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
					if (cardnum.startsWith("5")) {
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
					if ((cardnum.startsWith("36")
									|| cardnum.startsWith("300")
									|| cardnum.startsWith("305") || cardnum
										.startsWith("38"))) {
						cartype = 7;
						vpc_Card = "DcCard";
					}

//				}
				logger.info("*********************判断卡类型结束***************************");
				if (cartype == 5) {
					this.cardMessage = "I declare that I have been offered a choice of payment currencies and my choice is final.";
				} else {
					this.cardMessage = "I declare that I have been offered a choice of payment currencies and my choice is final.I understand that the currency conversion is not provided by Visa.";

				}
				// 根据商户号,卡种信息获取通道
				logger.info("*********************卡种信息获取通道***************************"+cartype);
				this.newcardtype=cartype+"";
				String sql = "select a.channelname , d.id ,b.id as tid from international_channels a ,international_creditcardtype b,international_mercreditcard c ,international_merchantchannels d where a.id=d.channelid and d.merchantid='"
						+ merchant.getId()
						+ "' and b.shortname='"
						+ cartype
						+ "' and d.id=c.merchannelid and b.id=c.creditcardid and d.onoff='1' and c.onoff='1'";
				List chanellist = this.commonService.getByList(sql);
				// 商户通道ID
				String merchanID = "";
				// 通道名称
				String chanelName = "";
				// 卡种ID
				Long carType = 0l;
				if (chanellist.size() > 0) {
					for(int i=0;i<chanellist.size();i++){
						Object[] tem = (Object[]) chanellist.get(i);
						merchanID = tem[1].toString();
						chanelName = tem[0].toString();
						carType = Long.valueOf(tem[2].toString());
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
				// 商户通道
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
				card.setEmail(email);
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
				card.setShippingEmail(this.getShippingEmail());
				card.setShippingFullName(this.getShippingFirstName() + " "
						+ this.getShippingLastName());
				card.setShippingPhone(this.getShippingPhone());
				card.setShippingState(this.getState());
				card.setShippingZip(this.getShippingZipcode());
				if(StringUtils.isNotBlank(products)){
					if(this.products.length()>2000){
						products=products.substring(0, 200);
					}
				}
				card.setProductInfo(products);
				this.commonService.save(card);
				 trade.setTradeChannel(Long.valueOf(merchanID));
				 this.commonService.update(trade);
				 List<Long> backCardValue=this.commonService.list("select t.merId from InternationalBacklist t where t.cardno='"
								+ cardnum + "' ");
				int riskCardValue = this.tradeManager
						.intBySql("select count(1) from InternationalRisklist t where t.cardno='"
								+ cardnum + "' ");
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
					} else if (trade.getTradeAmount() > merchantmanager.getPenQuota()&&!"6".equals(trade.getMoneyType()+"")) {
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
//					List carno=this.commonService.list("select t from InternationalCardholdersInfo f,InternationalTradeinfo t where f.tradeId=t.id and f.cardNo='"
//							+ AES.setCarNo(this.cardnum)+ "' and t.merchantId='"+ merchant.getId()+ "' and substring(t.tradeState,1,1) in(1,2,4,5,6) and t.tradeTime>to_date('"
//							+ yestedayDate + "','yyyy-MM-dd hh24:mi:ss')");
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

					//30天之内前3笔都是失败的卡号
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
					
					//同一ip失败次数
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
//					List ipcount=this.commonService.list("select t from InternationalCardholdersInfo f,InternationalTradeinfo t where f.tradeId=t.id and f.ip='"
//							+ ip+ "' and t.merchantId='"+ merchant.getId()+ "' and substring(t.tradeState,1,1) in(1,2,4,5,6) and t.tradeTime>to_date('"
//							+ yestedayDate + "','yyyy-MM-dd hh24:mi:ss')");
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
//					List emailcount=this.commonService.list("select t from InternationalCardholdersInfo f,InternationalTradeinfo t where f.tradeId=t.id and f.email='"
//							+ this.email+ "' and t.merchantId='"+ merchant.getId()+ "' and substring(t.tradeState,1,1) in(1,2,4,5,6) and t.tradeTime>to_date('"
//							+ yestedayDate + "','yyyy-MM-dd hh24:mi:ss')");
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
//					List telcout=this.commonService.list("select t from InternationalCardholdersInfo f,InternationalTradeinfo t where f.tradeId=t.id and f.phone='"
//							+ this.phone+ "' and t.merchantId='"+ merchant.getId()+ "' and substring(t.tradeState,1,1) in(1,2,4,5,6) and t.tradeTime>to_date('"
//							+ yestedayDate + "','yyyy-MM-dd hh24:mi:ss')");
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
//					List cocketcount=this.commonService.list("select t from InternationalCardholdersInfo f,InternationalTradeinfo t where f.tradeId=t.id and f.cookie='"
//							+ this.cookie+ "' and t.merchantId='"+ merchant.getId()+ "' and substring(t.tradeState,1,1) in(1,2,4,5,6) and t.tradeTime>to_date('"
//							+ yestedayDate + "','yyyy-MM-dd hh24:mi:ss')");
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
					// 与商户的风控制对比，然后进行对比
					// 连接maxmind系统
					try {
						HashMap hm = new HashMap();
						// 加密串 license_key : UxQh0mA4aLqw 调试和正式运行时要加上,才会返回分数
						// 上海key: CxsRZ1xPPRbR;
						// 广州key: UxQh0mA4aLqw
						int index = email.indexOf("@");
						String domian = email.substring(index + 1, email.length());
						hm.put("license_key", "9kbrHiIOJ9ZS");
						hm.put("i", ip);
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
		
						maxMindInfo = getmmValue(hm);
						maxmindValue = (String) maxMindInfo.get("values");
						bankName = (String) maxMindInfo.get("bankName");
						bankCountry = (String) maxMindInfo.get("bankCountry");
						bankPhone = (String) maxMindInfo.get("bankPhone");
						logger.info("maxmindValue--------------" + maxmindValue);
						// System.out.println("riskValue--------------"+riskValue);
					} catch (Exception ex) {
						try {
							CCSendMail.setSendMail("878701211@qq.com",
									"2.0 maxmind error", "xingbill@xingbill.com");
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
//					this.commonService
//							.deleteBySql(" update international_cardholdersinfo t set t.maxmindValue='"
//									+ maxmind
//									+ "',t.bankcountry='"
//									+ bankCountry
//									+ "',t.bankname='"
//									+ bankName
//									+ "',t.bankphone='"
//									+ bankPhone
//									+ "' where t.id='"
//									+ card.getId()
//									+ "' ");
					// 高风险卡 VIP为待确认 ，其他的为失败
//					if ((maxmind >= im.getMaxmind_lv1())
//							&& (maxmind <= im.getMaxmind_lv2())) {
//						message = "Your Payment is being Processed! ";
//						remark = "中风险！/The risk";
//						responseCode = 9;
//						logger.info("返回状态码+++++++++" + responseCode);
//						MD5info = trade.getMerchantOrderNo() + trade.getMoneyType()
//								+ ordercountValue + responseCode + MD5key;
//						md5Value = md5.getMD5ofStr(MD5info);
//						if (chnals.equals("VIP")
//								|| (chnals.equals("EVIP") && merchant.getStatutes()
//										.subSequence(6, 7).equals("0"))
//								|| chnals.equals("PRE")) {
//							trade.setTradeState("4"
//									+ trade.getTradeState().substring(1,
//											trade.getTradeState().length()));
//							String re[]=remark.split("/");
//							trade.setRemark(re[0]);
//							this.commonService.update(trade);
//							// -------更新商户月交易限额-----------------
//							merchant.setMonthTradeMoney(merchant.getMonthTradeMoney()
//									+ trade.getTradeAmount());
//							this.commonService.update(merchant);
//						} else {
//							trade.setTradeState("0"
//									+ trade.getTradeState().substring(1,
//											trade.getTradeState().length()));
//							String re[]=remark.split("/");
//							trade.setRemark(re[0]);
//							this.commonService.update(trade);
//		
//						}
//						logger.info("*********************支付结果返回码***************************"+responseCode);
//						return SUCCESS;
//					} else 
//						if (maxmind > im.getMaxmind_lv2()) {
//						message = "Payment Declined";
//						remark = "1093 High-risk";
//						responseCode = 0;
//						logger.info("返回状态码+++++++++" + responseCode);
//						MD5info = trade.getMerchantOrderNo() + trade.getMoneyType()
//								+ ordercountValue + responseCode + MD5key;
//						md5Value = md5.getMD5ofStr(MD5info);
//						trade.setTradeState("0"
//								+ trade.getTradeState().substring(1,
//										trade.getTradeState().length()));
////						String re[]=remark.split("/");
//						trade.setRemark(remark);
//						this.commonService.update(trade);
//						return SUCCESS;
//					}


					/**
					 * 禁止交易地区按照持卡人账单地区check
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
      if (("S").equals(chnals.substring(0, 1))) {
        String[] re;
        SfeUtil su = new SfeUtil();
        SfeMessage sm = new SfeMessage();
        sm.setMerNo("1001");
        sm.setAmount(trade.getRmbAmount().toString());
        sm.setCurrency("CNY");
        String[] tradWeb = this.ReturnURL.split("/");
        sm.setTradeAdd(tradWeb[2]);
        sm.setTradetime(trade.getTradeTime());
        sm.setReturnURL(this.ReturnURL);
        sm.setCardNo(this.cardnum);
        sm.setCvv2(this.cvv2);
        sm.setYear(this.year);
        sm.setMonth(this.month);
        sm.setMerchantOrderNo(trade.getOrderNo());
        sm.setMD5key(this.MD5key);
        sm.setIp(this.ip);
        sm.setCartype(this.newcardtype);
//        if(card.getMaxmindValue()==null){
//        	card.setMaxmindValue(0.0);
//        }
//        sm.setMaxmindRiskValue(card.getMaxmindValue().toString());
        if(maxMindInfo!=null){
	        JSONObject jsMaxMind = JSONObject.fromObject(maxMindInfo);
	        sm.setMaxMindInfo(jsMaxMind.toString());
        }
        String Agent = "";
        try {
          Agent = request.getHeader("User-Agent");
          StringTokenizer st = new StringTokenizer(Agent, ";");
          st.nextToken();

          this.userbrowser = st.nextToken();
        } catch (Exception e) {
          this.logger.error(e);
          this.userbrowser = Agent;
        }
        if (StringUtils.isBlank(this.userbrowser))
          this.userbrowser = "MSIE 10.0";

        sm.setUserbrowser(this.userbrowser);
        sm.setCookie(this.cookie);
        sm.setFirstname(this.firstname);
        sm.setLastname(this.lastname);
        sm.setAddress(this.address);
        sm.setCity(this.city);
        sm.setState(this.state);
        sm.setCountry(this.country);
        sm.setZipcode(this.zipcode);
        sm.setEmail(this.email);
        sm.setPhone(this.phone);
        sm.setShippingFirstName(this.shippingFirstName);
        sm.setShippingLastName(this.shippingLastName);
        sm.setShippingAddress(this.shippingAddress);
        sm.setShippingCity(this.shippingCity);
        sm.setShippingSstate(this.shippingSstate);
        sm.setShippingCountry(this.shippingCountry);
        sm.setShippingZipcode(this.shippingZipcode);
        sm.setShippingEmail(this.shippingEmail);
        sm.setShippingPhone(this.shippingPhone);
        sm.setProducts(this.products);
        sm.setCardBank(cardbank);
        sm.setXingChanel(chnals);
        su.paySfe(sm);
        if ("88".equals(sm.getResponseCode()+""))
        {
          this.message = "Payment Success!";
          this.responseCode = 88;
          this.MD5info = trade.getMerchantOrderNo() + 
            trade.getMoneyType() + ordercountValue + 
            this.responseCode + this.MD5key;
          this.md5Value = md5.getMD5ofStr(this.MD5info);
          trade.setTradeState("1" + 
            trade.getTradeState().substring(1, 
            trade.getTradeState().length()));
          trade.setRemark(this.message);
          this.logger.info("交易成功返回:" + this.merchantOrderNo + "**" + this.tradeMoneyType + "**" + this.ordercount + "**" + this.responseCode + "**" + this.message + "**" + this.ReturnURL + "**" + this.md5Value);
          this.commonService.update(trade);
          if(!"1731".equals((trade.getOrderNo()).substring(0,4))){
	          EmailInfo emailinfo = new EmailInfo();
	          String mailinfo = emailinfo.getPaymentResultEmail(
	            card.getEmail(), 
	            trade.getTradeAmount(), 
	            getStates().getCurrencyTypeByNo(
	            trade.getMoneyType().intValue()), 
	            trade.getTradeUrl(), trade.getTradeTime(), 
	            sm.getBilladdress(), trade.getMerchantOrderNo(), 
	            trade.getOrderNo());
	          try
	          {
	            CCSendMail.setSendMail(card.getEmail(), mailinfo, 
	              "xingbill@xingbill.com");
	            this.logger.info("邮件立马发出");
	          }
	          catch (Exception e) {
	            this.shopManagerService.addSendMessages(card.getEmail(), 
	              "xingbill@xingbill.com", mailinfo, "0");
	            this.logger.info("邮件等待稍后发出");
	            this.logger.info("*********************支付结果返回码***************************" + this.responseCode);
	            return SUCCESS;
	          }
          }
          this.logger.info("*********************支付结果返回码***************************" + this.responseCode);
          return "success"; }
        if ("19".equals(sm.getResponseCode()+"")) {
        	this.message = "Waiting processing!";
	        this.remark = "Waiting processing!";
	        this.responseCode = sm.getResponseCode();
	        this.MD5info = trade.getMerchantOrderNo() + 
            trade.getMoneyType() + ordercountValue + 
            this.responseCode + this.MD5key;
	        this.md5Value = md5.getMD5ofStr(this.MD5info);
	        trade.setTradeState("2" + 
            trade.getTradeState().substring(1, 
            trade.getTradeState().length()));
	        trade.setRemark(remark);
          this.commonService.update(trade);
          this.logger.info("*********************支付结果返回码***************************" + this.responseCode);
          return SUCCESS;
        }
        if (("7".equals(sm.getResponseCode()+"")) || ("16".equals(sm.getResponseCode()+"")) || ("27".equals(sm.getResponseCode()+""))) {
          this.message = "Payment Declined!";
          this.remark = sm.getRemark();
          this.responseCode = sm.getResponseCode();
          trade.setTradeState("3" + 
            trade.getTradeState().substring(1, 
            trade.getTradeState().length()));
          re = this.remark.split("/");
          trade.setRemark(re[0]);
          this.commonService.update(trade);
          this.MD5info = trade.getMerchantOrderNo() + 
            trade.getMoneyType() + ordercountValue + 
            this.responseCode + this.MD5key;
          this.md5Value = md5.getMD5ofStr(this.MD5info);
          this.logger.info("*********************支付结果返回码***************************" + this.responseCode);
          return SUCCESS;
        }else{
	        this.message = "Payment Declined!";
	        this.remark = sm.getRemark();
	        if (("sfe01".equals(this.remark)) || ("03".equals(this.remark))) {
	          this.logger.info("写入重跑数据！" + this.remark);
	          trade.setRemark("请求银行超时！");
	          shopManagerService.addTemporaryTradInfo(rorderno, year, month,cvv2,country,MD5key, ip,userbrowser,"超时");
	        } else if ("sfe02".equals(this.remark)) {
	          this.logger.info("写入重跑数据！");
	          trade.setRemark("请求超时！");
	          shopManagerService.addTemporaryTradInfo(rorderno, year, month,cvv2,country,MD5key, ip,userbrowser,"超时");
	        } else {
	          re = this.remark.split("/");
	          trade.setRemark(re[0]);
	        }
	        this.responseCode = sm.getResponseCode();
	        trade.setTradeState("0" + 
	          trade.getTradeState().substring(1, 
	          trade.getTradeState().length()));
	        trade.setVIPDisposePorson("System");
	        trade.setVIPDisposeDate(new Date());
	        this.commonService.update(trade);
	        this.MD5info = trade.getMerchantOrderNo() + 
	          trade.getMoneyType() + ordercountValue + 
	          this.responseCode + this.MD5key;
	        this.md5Value = md5.getMD5ofStr(this.MD5info);
	        this.logger.info("*********************支付结果返回码***************************" + this.responseCode);
	        return SUCCESS;
	      }
      }
      if (("X").equals(chnals.substring(0, 1))) {
          String[] re;
          SfeUtil su = new SfeUtil();
          SfeMessage sm = new SfeMessage();
          sm.setMerNo("4136");
          sm.setAmount(trade.getRmbAmount().toString());
          sm.setCurrency("CNY");
          String[] tradWeb = this.ReturnURL.split("/");
          sm.setTradeAdd(tradWeb[2]);
          sm.setTradetime(trade.getTradeTime());
          sm.setReturnURL(this.ReturnURL);
          sm.setCardNo(this.cardnum);
          sm.setCvv2(this.cvv2);
          sm.setYear(this.year);
          sm.setMonth(this.month);
          sm.setMerchantOrderNo(trade.getOrderNo());
          sm.setMD5key(this.MD5key);
          sm.setIp(this.ip);
          sm.setCartype(this.newcardtype);
//          if(card.getMaxmindValue()==null){
//          	card.setMaxmindValue(0.0);
//          }
//          sm.setMaxmindRiskValue(card.getMaxmindValue().toString());
          if(maxMindInfo!=null){
  	        JSONObject jsMaxMind = JSONObject.fromObject(maxMindInfo);
  	        sm.setMaxMindInfo(jsMaxMind.toString());
          }
          String Agent = "";
          try {
            Agent = request.getHeader("User-Agent");
            StringTokenizer st = new StringTokenizer(Agent, ";");
            st.nextToken();

            this.userbrowser = st.nextToken();
          } catch (Exception e) {
            this.logger.error(e);
            this.userbrowser = Agent;
          }
          if (StringUtils.isBlank(this.userbrowser))
            this.userbrowser = "MSIE 10.0";

          sm.setUserbrowser(this.userbrowser);
          sm.setCookie(this.cookie);
          sm.setFirstname(this.firstname);
          sm.setLastname(this.lastname);
          sm.setAddress(this.address);
          sm.setCity(this.city);
          sm.setState(this.state);
          sm.setCountry(this.country);
          sm.setZipcode(this.zipcode);
          sm.setEmail(this.email);
          sm.setPhone(this.phone);
          sm.setShippingFirstName(this.shippingFirstName);
          sm.setShippingLastName(this.shippingLastName);
          sm.setShippingAddress(this.shippingAddress);
          sm.setShippingCity(this.shippingCity);
          sm.setShippingSstate(this.shippingSstate);
          sm.setShippingCountry(this.shippingCountry);
          sm.setShippingZipcode(this.shippingZipcode);
          sm.setShippingEmail(this.shippingEmail);
          sm.setShippingPhone(this.shippingPhone);
          sm.setProducts(this.products);
          sm.setCardBank(cardbank);
          sm.setXingChanel(chnals);
          su.paySfe(sm);
          if ("88".equals(sm.getResponseCode()+""))
          {
            this.message = "Payment Success!";
            this.responseCode = 88;
            this.MD5info = trade.getMerchantOrderNo() + 
              trade.getMoneyType() + ordercountValue + 
              this.responseCode + this.MD5key;
            this.md5Value = md5.getMD5ofStr(this.MD5info);
            trade.setTradeState("1" + 
              trade.getTradeState().substring(1, 
              trade.getTradeState().length()));
            trade.setRemark(this.message);
            this.logger.info("交易成功返回:" + this.merchantOrderNo + "**" + this.tradeMoneyType + "**" + this.ordercount + "**" + this.responseCode + "**" + this.message + "**" + this.ReturnURL + "**" + this.md5Value);
            this.commonService.update(trade);
            if(!"1731".equals((trade.getOrderNo()).substring(0,4))){
  	          EmailInfo emailinfo = new EmailInfo();
  	          String mailinfo = emailinfo.getPaymentResultEmail(
  	            card.getEmail(), 
  	            trade.getTradeAmount(), 
  	            getStates().getCurrencyTypeByNo(
  	            trade.getMoneyType().intValue()), 
  	            trade.getTradeUrl(), trade.getTradeTime(), 
  	            sm.getBilladdress(), trade.getMerchantOrderNo(), 
  	            trade.getOrderNo());
  	          try
  	          {
  	            CCSendMail.setSendMail(card.getEmail(), mailinfo, 
  	              "xingbill@xingbill.com");
  	            this.logger.info("邮件立马发出");
  	          }
  	          catch (Exception e) {
  	            this.shopManagerService.addSendMessages(card.getEmail(), 
  	              "xingbill@xingbill.com", mailinfo, "0");
  	            this.logger.info("邮件等待稍后发出");
  	            this.logger.info("*********************支付结果返回码***************************" + this.responseCode);
  	            return SUCCESS;
  	          }
            }
            this.logger.info("*********************支付结果返回码***************************" + this.responseCode);
            return "success"; }
          if ("19".equals(sm.getResponseCode()+"")) {
          	this.message = "Waiting processing!";
  	        this.remark = "Waiting processing!";
  	        this.responseCode = sm.getResponseCode();
  	        this.MD5info = trade.getMerchantOrderNo() + 
              trade.getMoneyType() + ordercountValue + 
              this.responseCode + this.MD5key;
  	        this.md5Value = md5.getMD5ofStr(this.MD5info);
  	        trade.setTradeState("2" + 
              trade.getTradeState().substring(1, 
              trade.getTradeState().length()));
  	        trade.setRemark(remark);
            this.commonService.update(trade);
            this.logger.info("*********************支付结果返回码***************************" + this.responseCode);
            return SUCCESS;
          }
          if (("7".equals(sm.getResponseCode()+"")) || ("16".equals(sm.getResponseCode()+"")) || ("27".equals(sm.getResponseCode()+""))) {
            this.message = "Payment Declined!";
            this.remark = sm.getRemark();
            this.responseCode = sm.getResponseCode();
            trade.setTradeState("3" + 
              trade.getTradeState().substring(1, 
              trade.getTradeState().length()));
            re = this.remark.split("/");
            trade.setRemark(re[0]);
            this.commonService.update(trade);
            this.MD5info = trade.getMerchantOrderNo() + 
              trade.getMoneyType() + ordercountValue + 
              this.responseCode + this.MD5key;
            this.md5Value = md5.getMD5ofStr(this.MD5info);
            this.logger.info("*********************支付结果返回码***************************" + this.responseCode);
            return SUCCESS;
          }else{
  	        this.message = "Payment Declined!";
  	        this.remark = sm.getRemark();
  	        if (("sfe01".equals(this.remark)) || ("03".equals(this.remark))) {
  	          this.logger.info("写入重跑数据！" + this.remark);
  	          trade.setRemark("请求银行超时！");
  	          shopManagerService.addTemporaryTradInfo(rorderno, year, month,cvv2,country,MD5key, ip,userbrowser,"超时");
  	        } else if ("sfe02".equals(this.remark)) {
  	          this.logger.info("写入重跑数据！");
  	          trade.setRemark("请求超时！");
  	          shopManagerService.addTemporaryTradInfo(rorderno, year, month,cvv2,country,MD5key, ip,userbrowser,"超时");
  	        } else {
  	          re = this.remark.split("/");
  	          trade.setRemark(re[0]);
  	        }
  	        this.responseCode = sm.getResponseCode();
  	        trade.setTradeState("0" + 
  	          trade.getTradeState().substring(1, 
  	          trade.getTradeState().length()));
  	        trade.setVIPDisposePorson("System");
  	        trade.setVIPDisposeDate(new Date());
  	        this.commonService.update(trade);
  	        this.MD5info = trade.getMerchantOrderNo() + 
  	          trade.getMoneyType() + ordercountValue + 
  	          this.responseCode + this.MD5key;
  	        this.md5Value = md5.getMD5ofStr(this.MD5info);
  	        this.logger.info("*********************支付结果返回码***************************" + this.responseCode);
  	        return SUCCESS;
  	      }
        }
      if (chnals.equals("V5")) {

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
//			tm.setShipCity(this.shippingCity);
//			tm.setShipState(this.shippingSstate);
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
					shopManagerService.addTemporaryTradInfo(rorderno, year, month,cvv2,country,MD5key, ip,userbrowser,"v5_03");
				}else if("sfe01".equals(tm.getErrorCode())){
					trade.setRemark("请求银行超时，请重新支付！");
					logger.info("请求银行超时，请重新支付！");
					trade.setTradeState("3"
							+ trade.getTradeState().substring(1,
									trade.getTradeState().length()));
					shopManagerService.addTemporaryTradInfo(rorderno, year, month,cvv2,country,MD5key, ip,userbrowser,"超时");
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
				if(!"1731".equals((trade.getOrderNo()).substring(0,4))){
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
								"xingbill@xingbill.com");
						logger.info("邮件立马发出");
					} catch (Exception e) {
						// 往数据库插入等待发送邮件
						shopManagerService.addSendMessages(card.getEmail(),
								"xingbill@xingbill.com", mailinfo, "0");
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
			ms.setReturnURL("www.baidu.com");
			ms.setCardNo(cardnum);
			ms.setCardExpireYear("20"+year);
			ms.setCardExpireMonth(month);
			ms.setCardSecurityCode(cvv2);
			ms.setIssuingBank(cardbank);
			String ipstr[]=ip.split(",");
			if(ipstr.length>1){
				ms.setIp(ipstr[0]);
			}else{
				ms.setIp(ip);
			}
			ms.setEmail(this.email);
			ms.setPaymentMethod("Credit Card");
			ms.setPhone(this.phone);
			ms.setFirstName(this.firstname);
			ms.setLastName(this.lastname);
			ms.setCountry(this.country.substring(0, 3));
			ms.setState(this.state);
			ms.setCity(this.city);
			ms.setAddress(this.address);
			ms.setZip(this.zipcode);
			ms.setIsAuthor("");
			ms.setRemark("");
			String sign=ms.getMerNo()+ms.getGatewayNo()+ms.getOrderNo()+ms.getOrderCurrency()+ms.getOrderAmount()+ms.getFirstName()+ms.getLastName() + ms.getCardNo() + ms.getCardExpireYear()+ms.getCardExpireMonth()+ms.getCardSecurityCode()+ ms.getEmail() + posNumber;
			String strDes = getSha256(sign); 
			ms.setSignInfo(strDes);
			tu.get(ms);
			if(ms.getRes_orderStatus().equals("0")){

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
				if(!"1731".equals((trade.getOrderNo()).substring(0,4))){
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
								"xingbill@xingbill.com");
						logger.info("邮件立马发出");
					} catch (Exception e) {
						// 往数据库插入等待发送邮件
						shopManagerService.addSendMessages(card.getEmail(),
								"xingbill@xingbill.com", mailinfo, "0");
						logger.info("邮件等待稍后发出");
						logger.info("*********************支付结果返回码***************************"+responseCode);
						return SUCCESS;
					}
				}
				logger.info("*********************支付结果返回码***************************"+responseCode);
				return SUCCESS;
								
				
				
			}else if(ms.getRes_orderStatus().equals("-2")||ms.getRes_orderStatus().equals("-1")){

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
			
		} else if (chnals.equals("VPN")) {//非3DMOTO交易
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
//				dcc.setResp_Code("99YY");
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
				logger.info("MOTO DCC交易开始");
				// 交易
				vpn.MotoDCCMessage dcc2 = new vpn.MotoDCCMessage();
				dcc2.setTrans_Type("risk");// 类型
				dcc2.setMerchant_Id(posMerchantNo);// 42 商户编号
				dcc2.setAuthor_Str(it.get(0).getAuthcode());
				dcc2.setTerminal_Id(posNumber);// 41 商户终端号
				dcc2.setInvoice_No(trade.getOrderNo().substring(
						trade.getOrderNo().length() - 6,
						trade.getOrderNo().length()));

				dcc2.setTrans_Model("M");//moto通道
				dcc2.setCurrency_Code_T("156");// 货币代码
				dcc2.setAmount_Loc(this.buzero(trade.getRmbAmount() + ""));// 4
				// 本地交易金额
				dcc2.setCard_No(cardnum);// 账号2
				dcc2.setExp_Date(year + month);// 14 有效期
				dcc2.setCSC(cvv2);
				dcc2.setCurrency_Code(dcc.getCurrency_Code());
				dcc2.setBocs_ReturnURL("http://172.20.66.2/sfe");
				dcc2.setAmount_For(dcc.getAmount_For());
				dcc2.setOrder_No(trade.getOrderNo());
				dcc2.setCustom(trade.getOrderNo());
				dcc2.setHashCode(it.get(0).getHashcode());
				//新增风险交易参数
				dcc2.setCUST_FNAME(firstname);
				dcc2.setCUST_LNAME(lastname);
				dcc2.setCUST_CITY(city);
				dcc2.setCUST_ADDR1(address);
				dcc2.setCUST_CNTRY_CD(country.substring(0, 3));
				dcc2.setCUST_EMAIL(email);
				dcc2.setCUST_IP_ADDR(ip);
//				dcc2.setCUST_HOME_PHONE(phone);
				if(zipcode.replaceAll("-", "").length()>9){
				dcc2.setCUST_POSTAL_CD(zipcode.replaceAll("-", "").substring(0, 9));
				}else{
				dcc2.setCUST_POSTAL_CD(zipcode.replaceAll("-", ""));
				}
//				dcc2.setCUST_STPR_CD(state);
				dcc2.setSHIP_FNAME(shippingFirstName);
				dcc2.setSHIP_LNAME(shippingLastName);
				dcc2.setSHIP_CITY(shippingCity);
				dcc2.setSHIP_ADDR1(shippingAddress);
				dcc2.setSHIP_CNTRY_CD(shippingCountry.substring(0, 3));
				dcc2.setSHIP_EMAIL(shippingEmail);
				dcc2.setSHIP_IP_ADDR(ip);
//				dcc2.setSHIP_HOME_PHONE(shippingPhone);
				if(shippingZipcode.replaceAll("-", "").length()>9){
				dcc2.setSHIP_POSTAL_CD(shippingZipcode.replaceAll("-", "").substring(0, 9));
				}else{
					dcc2.setSHIP_POSTAL_CD(shippingZipcode.replaceAll("-", ""));
				}
//				dcc2.setSHIP_STPR_CD(shippingSstate);
				VpnUtil_Moto vu2=new VpnUtil_Moto();
				//VpnUtil vu2 = new VpnUtil();
				Long tim2 = System.currentTimeMillis();
				try {
					// type 2 dcc交易
					logger.info("开始moto风控");
					 dcc2 = vu2.getDCCvalue(dcc2, "21");
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
					logger.info("*********************支付结果返回码***************************"+responseCode);
					return SUCCESS;
				}
				if (dcc2.getResp_Code().equals("5600")) {
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
					if(!"1731".equals((trade.getOrderNo()).substring(0,4))){
						String mailinfo = null;
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
										mailinfo, "xingbill@xingbill.com");
								logger.info("邮件立马发出");
							}
						} catch (Exception e) {
							// 往数据库插入等待发送邮件
							shopManagerService.addSendMessages(card.getEmail(),
									"xingbill@xingbill.com", mailinfo, "0");
							logger.info("邮件等待稍后发出");
							logger.info("*********************支付结果返回码***************************"+responseCode);
							return SUCCESS;
						}
					}
					logger.info("*********************支付结果返回码***************************"+responseCode);
					return SUCCESS;
				} else {
					this.message = "Payment Declined!"+moto.getResp_Code();
					this.responseCode = Integer.valueOf(moto.getResp_Code());
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
				}else {
					message = "Payment Declined";
					remark = "高风险交易/Risk of trading";
					this.responseCode = Integer.valueOf(dcc2.getResp_Code());
					trade.setTradeState("0"
							+ trade.getTradeState().substring(1,
									trade.getTradeState().length()));
					String re[]=remark.split("/");
					trade.setRemark(re[0]+this.responseCode);
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

				vpn.DCCMessage dcc3 = new vpn.DCCMessage();
				dcc3.setTrans_Type("sales");// 类型
				// 商户编号
				dcc3.setMerchant_Id(it.get(0).getMerchantNo());// 42
				dcc3.setAuthor_Str(it.get(0).getAuthcode());
				// 商户终端号
				dcc3.setTerminal_Id(it.get(0).getTerminalNo());// 41
				dcc3.setInvoice_No(trade.getOrderNo().substring(
						trade.getOrderNo().length() - 6,
						trade.getOrderNo().length()));

				dcc3.setOrder_No(trade.getOrderNo());// 62
				dcc3.setCustom(trade.getOrderNo());
				dcc3.setHashCode(it.get(0).getHashcode());

				dcc3.setTrans_Model("M");//moto
				dcc3.setCurrency_Code_T(dcc.getCurrency_Code_T());// 货币代码
				dcc3.setAmount_Loc(this.buzero(trade.getTradeAmount() + ""));// 4
				// 本地交易金额
				dcc3.setCard_No(cardnum);// 账号2
				dcc3.setExp_Date(year + month);// 14 有效期
				dcc3.setCSC(cvv2);

				VpnUtil vu3 = new VpnUtil();
				Long tim2 = System.currentTimeMillis();
				try {
					// type 3 edc交易
					dcc3 = vu3.getDCCvalue(dcc3, "3");
				} catch (Exception e) {
					responseCode = Integer.valueOf(dcc3.getResp_Code());
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
				if (dcc3.getResp_Code().equals("0000")) {//交易成功
					trade.setTradeState("1"
							+ trade.getTradeState().substring(1,
									trade.getTradeState().length()));
					trade.setRemark(message);
					trade.setVIPDisposePorson("System");
					trade.setVIPDisposeDate(new Date());
					trade.setVIPBatchNo(dcc3.getAuth_Code());
					trade.setVIPTerminalNo(posNumber);
					trade.setVIPAuthorizationNo(dcc3.getInvoice_No());
					trade.setRef_No(dcc3.getRef_No());
					this.commonService.update(trade);
					card.setExpiryDate("0000");
					card.setCvv2("XXX");
					this.commonService.update(card);
					// 发送邮件
					List<InternationalTerminalManager> tmm = this.commonService
							.list("select tm from InternationalTerminalManager tm where tm.terminalNo='"
									+ posNumber.trim() + "' ");
					String billaddressby = null;
					if (tmm.size() > 0) {
						InternationalTerminalManager tm = tmm.get(0);
						billaddressby = tm.getBillingAddress();
					}
					if(!"1731".equals((trade.getOrderNo()).substring(0,4))){
						String mailinfo = null;
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
										mailinfo, "xingbill@xingbill.com");
								logger.info("邮件立马发出");
							}
						} catch (Exception e) {
							// 往数据库插入等待发送邮件
							shopManagerService.addSendMessages(card.getEmail(),
									"xingbill@xingbill.com", mailinfo, "0");
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
					this.message = "Payment Declined!!";
					this.responseCode = Integer
							.valueOf(dcc3.getResp_Code());
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
				if(!"1731".equals((trade.getOrderNo()).substring(0,4))){
					String mailinfo = null;
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
									mailinfo, "xingbill@xingbill.com");
							logger.info("邮件立马发出");
						}
					} catch (Exception e) {
						// 往数据库插入等待发送邮件
						shopManagerService.addSendMessages(card.getEmail(),
								"xingbill@xingbill.com", mailinfo, "0");
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

	public Hashtable getmmValue(HashMap hm) {

		h = exam.maxMindScore(hm);
		// 把MaxMind返回的参数打印出来,
		Hashtable ht = new Hashtable();
		for (Iterator i = h.keySet().iterator(); i.hasNext();) {
			String key = (String) i.next();
			maxmindValue = (String) h.get(key);
			if (key.equals("riskScore")) {
				values = maxmindValue;

			}
			if (key.equals("binName")) {
				bankName = maxmindValue;
				ht.put("bankName", bankName);
			}
			if (key.equals("binCountry")) {
				bankCountry = maxmindValue;
				ht.put("bankCountry", bankCountry);
			}
			if (key.equals("binPhone")) {
				bankPhone = maxmindValue;
				ht.put("bankPhone", bankPhone);
			}if (key.equals("ip_areaCode")) {
				ht.put("ip_areaCode", maxmindValue);
			}
			if (key.equals("ip_postalCode")) {
				ht.put("ip_postalCode", maxmindValue);
			}
			if (key.equals("ip_regionName")) {
				ht.put("ip_regionName", maxmindValue);
			}
			if (key.equals("ip_region")) {
				ht.put("ip_region", maxmindValue);
			}
			if (key.equals("ip_countryName")) {
				ht.put("ip_countryName", maxmindValue);
			}
			if (key.equals("countryCode")) {
				ht.put("countryCode", maxmindValue);
			}
			if (key.equals("anonymousProxy")) {
				ht.put("anonymousProxy", maxmindValue);
			}
			if (key.equals("distance")) {
				ht.put("distance", maxmindValue);
			}
			if (key.equals("proxyScore")) {
				ht.put("proxyScore", maxmindValue);
			}
			if (key.equals("postalMatch")) {
				ht.put("postalMatch", maxmindValue);
			}
			if (key.equals("custPhoneInBillingLoc")) {
				ht.put("custPhoneInBillingLoc", maxmindValue);
			}
			if (key.equals("shipCityPostalMatch")) {
				ht.put("shipCityPostalMatch", maxmindValue);
			}
			if (key.equals("ip_city")) {
				ht.put("ip_city", maxmindValue);
			}
		}
		if (values == null) {
			values = "0";
		}
		ht.put("values", values);
//		ht.put("bankName", bankName);
//		ht.put("bankCountry", bankCountry);
//		ht.put("bankPhone", bankPhone);
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
					logger.info("出现防风险验证："+valCardNo);
					redInfo="1";
					return true;
				}
			}
			if(StringUtils.isNotBlank(risk.getEmail())){
				if(valEmail.toLowerCase().indexOf(risk.getEmail().toLowerCase())>=0){
					logger.info("出现防风险验证："+valEmail);
					redInfo="2";
					return true;
				}
			}
			if(StringUtils.isNotBlank(risk.getIp())){
				if(valIp.toLowerCase().indexOf(risk.getIp().toLowerCase())>=0){
					logger.info("出现防风险验证："+valIp);
					redInfo="3";
					return true;
				}
			}
			if(StringUtils.isNotBlank(risk.getTradeUrl())){
				if(valUrl.toLowerCase().indexOf(risk.getTradeUrl().toLowerCase())>=0){
					logger.info("出现防风险验证："+valUrl);
					redInfo="4";
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