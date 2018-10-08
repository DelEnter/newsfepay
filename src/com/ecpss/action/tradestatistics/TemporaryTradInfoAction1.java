package com.ecpss.action.tradestatistics;

import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import sun.misc.BASE64Encoder;
import vpn.CaibaoMessage;
import vpn.CaibaoUtil;
import vpn.HJPayMessage;
import vpn.HJPayUtil;
import vpn.SfeMessage;
import vpn.SfeUtil;
import vpn.VpnUtil;
import vpn.VpnUtil_Moto;

import com.ecpss.action.BaseAction;
import com.ecpss.action.TemporarySynThread;
import com.ecpss.action.pay.fast.TradUtil;
import com.ecpss.action.pay.fast.TradeMessage;
import com.ecpss.action.pay.util.MaxMindExample;
import com.ecpss.model.channel.InternationalMerchantChannels;
import com.ecpss.model.payment.InternationalCardholdersInfo;
import com.ecpss.model.payment.InternationalTemporaryTradInfo;
import com.ecpss.model.payment.InternationalTradeinfo;
import com.ecpss.model.shop.InternationalTerminalManager;
import com.ecpss.util.AES;
import com.ecpss.util.CCSendMail;
import com.ecpss.util.EmailInfo;
import com.ecpss.util.MD5;
import com.ecpss.util.StatusUtil;
import com.ecpss.util.StringUtil;
import com.ecpss.web.PageInfo;

public class TemporaryTradInfoAction1 extends BaseAction {
	Logger logger = Logger.getLogger(TemporaryTradInfoAction1.class.getName());
	private PageInfo info = new PageInfo();
	private MaxMindExample exam = new MaxMindExample();
	private HashMap h = new HashMap();
	String bankCountry = null;
	private String orderNo;
	private String startDate;
	private String endDate;
	private String channelName;
	private String ids;
	private String msg;
	private String orderId;
	private String statusType;//����ҳ������ 0 ����ҳ��   1��������ҳ��
	public String toTemporaryInfo(){
		StringBuffer sb=new StringBuffer("select tt.id,tt.orderNo,it.tradeAmount,it.moneyType,it.rmbAmount,tt.createTime,ic,it.tradeUrl,tt.remark from InternationalTemporaryTradInfo tt,InternationalTradeinfo it,InternationalCardholdersInfo ic where tt.orderNo=it.orderNo and it.id=ic.tradeId ");
		if(StringUtils.isNotBlank(statusType)){
			sb.append("  and tt.status='"+statusType+"'");
		}else{
			sb.append("  and tt.status='0'");
		}
		if(StringUtils.isNotBlank(orderId)){
			sb.append(" and tt.id='"+orderId+"'");
		}
		if(StringUtils.isNotBlank(orderNo)){
			sb.append(" and tt.orderNo='"+orderNo+"'");
		}
		if(StringUtils.isNotBlank(startDate)){
			sb.append(" and tt.createTime>=to_date('"+startDate+" 00:00:00','yyyy-MM-dd hh24:mi:ss') ");
		}
		if(StringUtils.isNotBlank(endDate)){
			sb.append(" and tt.createTime<=to_date('"+endDate+" 23:59:59','yyyy-MM-dd hh24:mi:ss') ");
		}
		sb.append(" order by tt.createTime desc");
		info = commonService.listQueryResultByHql(sb.toString(), info);
		return SUCCESS;
	}
	public String auditTemporaryInfo(){
		if(StringUtils.isNotBlank(ids)){
			String[] id=ids.split(",");
			for(int i=0;i<id.length;i++){
				InternationalTemporaryTradInfo tem=(InternationalTemporaryTradInfo) commonService.uniqueResult("from InternationalTemporaryTradInfo where id='"+id[i]+"'");
				tem.setStatus("1");//����״̬
				commonService.update(tem);
			}
		}
		toTemporaryInfo();
		msg="���ύȥ����";
		return SUCCESS;
	}
	public String delTemporaryInfo(){
		if(StringUtils.isNotBlank(ids)){
			String[] id=ids.split(",");
			for(int i=0;i<id.length;i++){
				InternationalTemporaryTradInfo tem=(InternationalTemporaryTradInfo) commonService.uniqueResult("from InternationalTemporaryTradInfo where id='"+id[i]+"'");
				InternationalTradeinfo trade=(InternationalTradeinfo) commonService.uniqueResult("from InternationalTradeinfo where orderNo='"+tem.getOrderNo()+"'");
				trade.setTradeState("0"+ trade.getTradeState().substring(1,trade.getTradeState().length()));
				trade.setRemark("1093high risk!");
				commonService.update(trade);
				commonService.delete(InternationalTemporaryTradInfo.class, new Long(id[i]));
				if("1001".equals((trade.getOrderNo()).substring(0,4))||"4136".equals((trade.getOrderNo()).substring(0,4))){
					TemporarySynThread ts=new TemporarySynThread("https://www.xingbill.com/synTradeInfo",trade.getMerchantOrderNo(),"0", "1093high risk! ");
					ts.start();
					}
				if("3918".equals((trade.getOrderNo()).substring(0,4))){
					TemporarySynThread ts=new TemporarySynThread("http://www.win4mall.com/OrderAutomatic",trade.getMerchantOrderNo(), "0", "1093high risk! ");
					ts.start();
				}
			}
		}
		toTemporaryInfo();
		msg="ɾ���ɹ���";
		return SUCCESS;
	}
	public String temporaryInfo(){
		String orderList="";
		if(StringUtils.isNotBlank(ids)){
			String[] id=ids.split(",");
			for(int i=0;i<id.length;i++){
				InternationalTemporaryTradInfo tem=(InternationalTemporaryTradInfo) commonService.uniqueResult("from InternationalTemporaryTradInfo where id='"+id[i]+"'");
				InternationalTradeinfo trade=(InternationalTradeinfo) commonService.uniqueResult("from InternationalTradeinfo where orderNo='"+tem.getOrderNo()+"'");
				InternationalCardholdersInfo ic=(InternationalCardholdersInfo) commonService.uniqueResult("from InternationalCardholdersInfo where tradeId='"+trade.getId()+"'");
				String cardNo=AES.getCarNo(ic.getCardNo());
				String sql = "select a.channelname  from international_channels a ,international_merchantchannels d where a.id=d.channelid and d.merchantid='"
						+ trade.getMerchantId()+"'";
				List chanellist = this.commonService.getByList(sql);
				Boolean channelType=false;
				if (chanellist.size() > 0) {
					for(int j=0;j<chanellist.size();j++){
						String chName = chanellist.get(j).toString();
						if(channelName.equals(chName.split("-")[0])){
							channelType=true;
							break;
						}
						if(channelName.equals(chName.split("-")[0])){
							channelType=true;
							break;
						}
					}
				}
				if(channelType.equals(Boolean.FALSE)){
					toTemporaryInfo();
					msg="��δ��ͨ��ѡͨ�����̻�!";
					return SUCCESS;
				}
//				SfeUtil su=new SfeUtil();
//				SfeMessage sm=new SfeMessage();
//				sm.setMerNo("1001");
//				sm.setAmount(trade.getRmbAmount().toString());
//				sm.setCurrency("3");
//				if(StringUtils.isNotBlank(trade.getTradeUrl())){
//					sm.setTradeAdd(trade.getTradeUrl());
//					}else{
//						String tradWeb[] = trade.getReturnUrl().split("/");
//						sm.setTradeAdd(tradWeb[2]);
//					}
//				sm.setTradetime(trade.getTradeTime());
//				sm.setReturnURL(trade.getReturnUrl());
//				String cardNo=AES.getCarNo(ic.getCardNo());
//				sm.setCardNo(cardNo);
//				sm.setCvv2(tm.getCvv2());
//				sm.setYear(tm.getExpirationYear());
//				sm.setMonth(tm.getExpirationMonth());
//				sm.setMerchantOrderNo(trade.getOrderNo());
//				sm.setMD5key(tm.getMd5key());
//				sm.setIp(tm.getPayIp());
//				if("4".equals(cardNo.substring(0, 1))){
//					sm.setCartype("4");
//				}else{
//				sm.setCartype("5");
//				}
//				sm.setUserbrowser(tm.getUserAgent());
//				sm.setCookie(ic.getCookie());
//				sm.setFirstname(ic.getFirstName());
//				sm.setLastname(ic.getLastName());
//				sm.setAddress(ic.getAddress());
//				sm.setCity(ic.getCity());
//				sm.setState(ic.getState());
//				sm.setCountry(ic.getCountry());
//				sm.setZipcode(ic.getZipcode());
//				sm.setEmail(ic.getEmail());
//				sm.setPhone(ic.getPhone());
//				sm.setShippingFirstName(ic.getShippingFullName());
//				sm.setShippingLastName("");
//				sm.setShippingAddress(ic.getShippingAddress());
//				sm.setShippingCity(ic.getShippingCity());
//				sm.setShippingSstate(ic.getShippingState());
//				sm.setShippingCountry(ic.getShippingCountry());
//				sm.setShippingZipcode(ic.getShippingZip());
//				sm.setShippingEmail(ic.getShippingEmail());
//				sm.setShippingPhone(ic.getShippingPhone());
//				sm.setProducts(ic.getProductInfo());
//				su.paySfe(sm);
//				TradUtil tu= new TradUtil();
//				TradeMessage tm=new TradeMessage();
//				//����
//				String cardNo=AES.getCarNo(ic.getCardNo());
//				tm.setCardNo(cardNo);
//				//CVV
//				tm.setCvv(tem.getCvv2());
//				//������Ч����
//				tm.setExpirationYear("20"+tem.getExpirationYear());
//				//������Ч����
//				tm.setExpirationMonth(tem.getExpirationMonth());
//				tm.setPayNumber("0");
//				tm.setMerNo("10000");
//				tm.setShopName("Ǩ��ͨ");
//				tm.setOrderNo(trade.getOrderNo());
//				tm.setAmount(trade.getRmbAmount()+"");
//				tm.setCurrency("CNY");
//				tm.setGoodsName("��Ʒ����");
//				tm.setGoodsPrice(trade.getRmbAmount()+"");
//				tm.setGoodsNumber("1");
//				tm.setBillFirstName(ic.getFirstName());
//				tm.setBillLastName(ic.getLastName());
//				tm.setBillAddress(ic.getAddress());
//				tm.setBillCity(ic.getCity());
//				tm.setBillState(ic.getState());
//				tm.setBillCountry(ic.getCountry());
//				tm.setBillZip(ic.getZipcode());
//				tm.setEmail(ic.getEmail());
//				tm.setPhone(ic.getPhone());
//				String shipName[]=ic.getShippingFullName().split(" ");
//				if(shipName.length>1){
//				tm.setShipFirstName(shipName[0]);
//				tm.setShipLastName(shipName[1]);
//				}else{
//					tm.setShipFirstName(ic.getShippingFullName());
//					tm.setShipLastName(ic.getShippingFullName());	
//				}
//				tm.setShipAddress(ic.getShippingAddress());
//				tm.setShipCity(ic.getShippingCity());
//				tm.setShipState(ic.getShippingState());
//				tm.setShipAddress(ic.getShippingAddress());
//				tm.setShipCountry(ic.getShippingCountry());
//				tm.setShipZip(ic.getShippingZip());
//				tm.setReturnURL("www.baidu.com");
//				tm.setLanguage("");
//				tm.setRemark("qianyitong");
//
//				
//				String mdfind=tm.getMerNo()+"LzVZD7"+tm.getOrderNo()+tm.getAmount()+tm.getCurrency()+tm.getEmail()+tm.getReturnURL();
//				
//				String md5info = StringUtil.Md5(mdfind);
//				
//				tm.setMd5Info(md5info);
//				
//				tm.setPayIp(ic.getIp());
//				tm.setAcceptLanguage("zh-CN");
//				tm.setUserAgent(tem.getUserAgent());
//				tu.get(tm);	
//				MD5 md5=new MD5();
//				if(!"timeOut!".equals(tem.getRemark())){
//				try {
//					HashMap hm = new HashMap();
//					// ���ܴ� license_key : UxQh0mA4aLqw ���Ժ���ʽ����ʱҪ����,�Ż᷵�ط���
//					// �Ϻ�key: CxsRZ1xPPRbR;
//					// ����key: UxQh0mA4aLqw
//					int index = ic.getEmail().indexOf("@");
//					String domian = ic.getEmail().substring(index + 1, ic.getEmail().length());
//					hm.put("license_key", "9kbrHiIOJ9ZS");
//					hm.put("i", ic.getIp());
//					hm.put("domain", domian);
//					hm.put("emailMD5", md5.getMD5ofStr(ic.getEmail().toLowerCase()));
//					hm.put("custPhone", ic.getPhone());
//					hm.put("country", tem.getCountry().substring(3, 5));
//					hm.put("city", ic.getCity());
//					hm.put("region", ic.getState());
//					hm.put("shipAddr", ic.getAddress());
//					hm.put("postal", ic.getZipcode().toString());
//					// hm.put("bin", cardnum);
//					hm.put("bin", cardNo.substring(0, 6));
//					hm.put("binName", ic.getUserBank());
//
//					// standard �ͼ�
//					// premium �߼�
//					// ��ʽ���е�ʱ��Ҫ����� premium ; standardΪ�����õı�׼
//					hm.put("requested_type", "premium");
//
//					Hashtable ht = getmmValue(hm);
//					bankCountry = (String) ht.get("bankCountry");
//					} catch (Exception ex) {
//						try {
//							CCSendMail.setSendMail("878701211@qq.com",
//									"2.0 maxmind error", "sfepay@sfepay.com");
//						} catch (Exception e) {
//									// ����ִ����ȥ
//								}
//					}
//				}
//				String queryarea="";
//				String allQueryarea="";
//				if(StringUtils.isNotBlank(tem.getCountry())){
//				queryarea = "select m.id from MerchantRiskControl m where m.merchantId="
//						+ trade.getMerchantId()
//						+ " and (substr(m.area,1,2) like '"
//						+ tem.getCountry().substring(3, 5) + "' or substr(m.area,1,2) like '"
//						+ bankCountry + "')";
//				allQueryarea = "select m.id from MerchantRiskControl m where m.merchantId is null"
//					    		+ " and (substr(m.area,1,2) like '"
//					    		+ tem.getCountry().substring(3, 5) + "' or substr(m.area,1,2) like '"
//					    		+ bankCountry + "')";
//				}else{
//				queryarea ="select m.id from MerchantRiskControl m where m.merchantId="
//						+ trade.getMerchantId()
//						+ " and (substr(m.area,1,2) like '"
//						+ bankCountry + "')";
//				allQueryarea = "select m.id from MerchantRiskControl m where m.merchantId is null"
//					    		+ " and (substr(m.area,1,2) like '"
//					    		+ bankCountry + "')";
//				}
//					    logger.info(queryarea);
//						List queryarealist = this.commonService.list(queryarea);
//						List allQueryarealist = this.commonService.list(allQueryarea);
//						logger.info(queryarealist.size());
//						if (queryarealist.size() > 0||allQueryarealist.size()>0) {
//							
//							trade.setTradeState("0"
//									+ trade.getTradeState().substring(1,
//											trade.getTradeState().length()));
//							trade.setRemark("��ֹ���׵���");
//							commonService.update(trade);
//							commonService.delete(tem);
//							if("1001".equals((trade.getOrderNo()).substring(0,4))){
//							TemporarySynThread ts=new TemporarySynThread(trade.getMerchantOrderNo(), "0","high risk!");
//							ts.start();
//							}
//						}
//						else{
						if("CA".equals(channelName)){
							logger.info("��ʼCA���ף�"+trade.getOrderNo());
							CaibaoUtil tu= new CaibaoUtil();
							CaibaoMessage ms=new CaibaoMessage();
							//�ն�idֱ��д�̶��ģ�����Ժ��иı䣬��ط�ҲҪ�޸�
							String channelId="";
							if("4".equals(cardNo.substring(0, 1))){
								channelId="265";
							}else if("5".equals(cardNo.substring(0, 1))){
								channelId="266";
							}
							InternationalTerminalManager tim=(InternationalTerminalManager) commonService.uniqueResult("from InternationalTerminalManager where id='"+channelId+"'");
							ms.setMerNo("20668");
							ms.setGatewayNo(tim.getMerchantNo());
							ms.setOrderNo(trade.getOrderNo());
							ms.setOrderCurrency("CNY");
							ms.setOrderAmount(trade.getRmbAmount()+"");
							ms.setReturnURL("www.baidu.com");
							ms.setCardNo(cardNo);
							ms.setCardExpireYear("20"+tem.getExpirationYear());
							String month=tem.getExpirationMonth();
							if(month.length()<2){
								month="0"+month;
							}
							ms.setCardExpireMonth(month);
							ms.setCardSecurityCode(tem.getCvv2());
							String cardbank=ic.getUserBank();
							if(StringUtils.isBlank(cardbank)||cardbank.length()<2){
								cardbank="test";
							}
							ms.setIssuingBank(cardbank);
							String ipstr[]=ic.getIp().split(",");
							if(ipstr.length>1){
								ms.setIp(ipstr[0]);
							}else{
								ms.setIp(ic.getIp());
							}
							ms.setEmail(ic.getEmail());
							ms.setPaymentMethod("Credit Card");
							String phone=ic.getPhone();
							if(StringUtils.isBlank(phone)){
								phone="021-12345678";
							}
							ms.setPhone(phone);
							ms.setFirstName(ic.getFirstName());
							ms.setLastName(ic.getLastName());
//							if(!"3880".equals(tem.getOrderNo().substring(0, 4)+"")){
							if(tem.getCountry().length()>3){
							ms.setCountry(tem.getCountry().substring(0, 3));
							}else{
								ms.setCountry(tem.getCountry());
							}
							ms.setState(ic.getState());
							ms.setCity(ic.getCity());
							ms.setAddress(ic.getAddress());
							String zipcode=ic.getZipcode();
							if(StringUtils.isBlank(zipcode)){
								zipcode="123456";
							}
							ms.setZip(zipcode);
							ms.setIsAuthor("");
							ms.setRemark("");
							String sign=ms.getMerNo().trim()+ms.getGatewayNo().trim()+ms.getOrderNo().trim()+ms.getOrderCurrency().trim()+ms.getOrderAmount().trim()+ms.getFirstName().trim()+ms.getLastName().trim() + ms.getCardNo().trim() + ms.getCardExpireYear().trim()+ms.getCardExpireMonth().trim()+ms.getCardSecurityCode().trim()+ ms.getEmail().trim() + tim.getTerminalNo().trim();
							String strDes = getSha256(sign); 
							ms.setSignInfo(strDes);
							tu.get(ms);
							logger.info("vip���׽����"+ms.getRes_orderStatus());
							if(ms.getRes_orderStatus().equals("1")){
								trade.setTradeState("1"
										+ trade.getTradeState().substring(1,
												trade.getTradeState().length()));
								trade.setTradeState(StatusUtil.updateStatus(trade.getTradeState(), 15, "3"));
			//					trade.setRemark("Payment Declined!" + tm.getErrorCode());
								trade.setRemark("Payment Success!");
								
								orderList=orderList+trade.getOrderNo()+"��";
								if(!"1001".equals((trade.getOrderNo()).substring(0,4))&&!"4136".equals((trade.getOrderNo()).substring(0,4))){
									EmailInfo emailinfo = new EmailInfo();
									String mailinfo = emailinfo.getPaymentResultEmail(
											ic.getEmail(),
											trade.getTradeAmount(),
											getStates().getCurrencyTypeByNo(
													trade.getMoneyType().intValue()),
											trade.getTradeUrl(), trade.getTradeTime(),
											tim.getBillingAddress(), trade.getMerchantOrderNo(),
											trade.getOrderNo());
									try {
										CCSendMail.setSendMail(ic.getEmail(), mailinfo,
												"sfepay@sfepay.com");
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							}else if(ms.getRes_orderStatus().equals("0")){
								trade.setTradeState("0"
										+ trade.getTradeState().substring(1,
												trade.getTradeState().length()));
								if(!"sfe01".equals(ms.getRes_orderInfo())){
									trade.setRemark("1093high risk!" + ms.getRes_orderInfo());
								}
							}else if(ms.getRes_orderStatus().equals("-2")||ms.getRes_orderStatus().equals("-1")){
								trade.setTradeState("2"
										+ trade.getTradeState().substring(1,
												trade.getTradeState().length()));
							}
					if(StringUtils.isNotBlank(ms.getRes_orderStatus())&&!"sfe01".equals(ms.getRes_orderInfo())){
						List<InternationalMerchantChannels> merChannel=this.commonService.list("from InternationalMerchantChannels mc where mc.merchantId='"+trade.getMerchantId()+"' and mc.channelId='"+tim.getChannelId()+"'");
						if(merChannel!=null&&merChannel.size()>0){
							InternationalMerchantChannels mc=merChannel.get(0);
							logger.info("*********����ͨ��*************");
							trade.setTradeChannel(Long.valueOf(mc.getId()));
						}
						commonService.update(trade);
						commonService.delete(tem);
						if("1001".equals((trade.getOrderNo()).substring(0,4))||"4136".equals((trade.getOrderNo()).substring(0,4))){
						TemporarySynThread ts=new TemporarySynThread("https://www.xingbill.com/synTradeInfo",trade.getMerchantOrderNo(), ms.getRes_orderStatus(), ms.getRes_orderInfo());
						ts.start();
						}
						if("3918".equals((trade.getOrderNo()).substring(0,4))){
							TemporarySynThread ts=new TemporarySynThread("http://www.win4mall.com/OrderAutomatic",trade.getMerchantOrderNo(),ms.getRes_orderStatus(), ms.getRes_orderInfo());
							ts.start();
						}
					}
				}else if("HJ".equals(channelName)){
					logger.info("����HJͨ��");
					HJPayUtil HJ=new HJPayUtil();
					HJPayMessage hm=new HJPayMessage();
					//�ն�idֱ��д�̶��ģ�����Ժ��иı䣬��ط�ҲҪ�޸�
					String channelId="";
					if("4".equals(cardNo.substring(0, 1))){
						hm.setCardType("V");
						channelId="270";
					}
					if("5".equals(cardNo.substring(0, 1))){
						hm.setCardType("M");
						channelId="271";
					}
					if("3".equals(cardNo.substring(0, 1))){
						hm.setCardType("J");
						channelId="272";
					}
					InternationalTerminalManager tim=(InternationalTerminalManager) commonService.uniqueResult("from InternationalTerminalManager where id='"+channelId+"'");
					hm.setAcctNo(tim.getMerchantNo());
					hm.setAgent_AcctNo(tim.getTerminalNo());
					hm.setOrderID(trade.getOrderNo());
					hm.setCurrCode("156");
					Double amountAndFee=trade.getRmbAmount();
					if(trade.getChannelFee()!=null){
						amountAndFee=amountAndFee*(trade.getChannelFee()+1.0);
						amountAndFee = (double) (Math.round((double) amountAndFee * 100) / 100.00);
					}
					hm.setAmount((int)(trade.getRmbAmount()*100)+"");
					String ipstr[]=ic.getIp().split(",");
					if(ipstr.length>1){
						hm.setIpAddress(ipstr[0]);
					}else{
						hm.setIpAddress(ic.getIp());
					}
					hm.setCardPAN(cardNo);
					hm.setPname(ic.getProductInfo());
					hm.setCname(ic.getFirstName()+ic.getLastName());
					hm.setExpDate(tem.getExpirationYear()+tem.getExpirationMonth());
					hm.setCvv2(tem.getCvv2());
					hm.setIssCountry(tem.getCountry().substring(3, 5));
					hm.setBaddress(ic.getAddress());
					hm.setBcity(ic.getCity());
					hm.setPostCode(ic.getZipcode());
					hm.setIversion("V5.0");
					hm.setTelephone(ic.getPhone());
					hm.setRetURL("www.sfepay.com");
					hm.setEmail(ic.getEmail());
					String hash=tim.getHashcode()+ hm.getAcctNo() + hm.getOrderID() + hm.getAmount() + hm.getCurrCode();
					logger.info("hash����ǰ��"+hash);
					hm.setHashValue(getBase64E(hash));
					hm.setBrowserUserAgent(tem.getUserAgent());
					hm.setShipName(ic.getShippingFullName());
					hm.setShipAddress(ic.getShippingAddress());
					hm.setShipCity(ic.getShippingCity());
					hm.setShipstate(ic.getShippingState());
					hm.setShipCountry(tem.getCountry().substring(3, 5));
					hm.setShipPostCode(ic.getShippingZip());
					hm.setShipphone(ic.getShippingPhone());
					hm.setTxnType("01");
					HJ.get(hm);
					
					logger.info("vip���׽����"+hm.getRes_success());
					String backSussess="";
					String backMessage="";
					if(hm.getRes_success().equals("00")){
						trade.setTradeState("1"
								+ trade.getTradeState().substring(1,
										trade.getTradeState().length()));
						trade.setTradeState(StatusUtil.updateStatus(trade.getTradeState(), 15, "3"));
	//					trade.setRemark("Payment Declined!" + tm.getErrorCode());
						trade.setVIPAuthorizationNo(hm.getRes_queOrderNo());
						trade.setRemark("Payment Success!");
						orderList=orderList+trade.getOrderNo()+"��";
						backSussess="1";
						backMessage=hm.getRes_billaddress();
						if(!"1001".equals((trade.getOrderNo()).substring(0,4))&&!"4136".equals((trade.getOrderNo()).substring(0,4))){
							EmailInfo emailinfo = new EmailInfo();
							String mailinfo = emailinfo.getPaymentResultEmail(
									ic.getEmail(),
									trade.getTradeAmount(),
									getStates().getCurrencyTypeByNo(
											trade.getMoneyType().intValue()),
									trade.getTradeUrl(), trade.getTradeTime(),
									hm.getRes_billaddress(), trade.getMerchantOrderNo(),
									trade.getOrderNo());
							try {
								CCSendMail.setSendMail(ic.getEmail(), mailinfo,
										"sfepay@sfepay.com");
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}else{
						trade.setTradeState("0"
								+ trade.getTradeState().substring(1,
										trade.getTradeState().length()));
						if(!"sfe01".equals(hm.getRes_success())){
							trade.setRemark("1093high risk!" +hm.getRes_message());
						}
						backSussess="0";
						backMessage=hm.getRes_message();
					}
			if(StringUtils.isNotBlank(hm.getRes_success())&&!"sfe01".equals(hm.getRes_success())){
				List<InternationalMerchantChannels> merChannel=this.commonService.list("from InternationalMerchantChannels mc where mc.merchantId='"+trade.getMerchantId()+"' and mc.channelId='"+tim.getChannelId()+"'");
				if(merChannel!=null&&merChannel.size()>0){
					InternationalMerchantChannels mc=merChannel.get(0);
					logger.info("*********����ͨ��*************");
					trade.setTradeChannel(Long.valueOf(mc.getId()));
				}
				commonService.update(trade);
				commonService.delete(tem);
				if("1001".equals((trade.getOrderNo()).substring(0,4))||"4136".equals((trade.getOrderNo()).substring(0,4))){
				TemporarySynThread ts=new TemporarySynThread("https://www.xingbill.com/synTradeInfo",trade.getMerchantOrderNo(), backSussess, "hj"+backMessage);
				ts.start();
				}
				if("3918".equals((trade.getOrderNo()).substring(0,4))){
					TemporarySynThread ts=new TemporarySynThread("http://www.win4mall.com/OrderAutomatic",trade.getMerchantOrderNo(),backSussess, backMessage);
					ts.start();
				}
				}
			}else if("VPN".equals(channelName)){
					String backSussess="";
					String backMessage="";
					//��3DMOTO����
	                logger.info("���뵽VPNͨ��");
					vpn.DCCMessage dcc = new vpn.DCCMessage();
					//�ն�idֱ��д�̶��ģ�����Ժ��иı䣬��ط�ҲҪ�޸�
					InternationalTerminalManager VpnTim=(InternationalTerminalManager) commonService.uniqueResult("from InternationalTerminalManager where id='259' and onoff='1'");
					dcc.setTrans_Type("enqrate");// ��ѯ�˿��Ƿ�֧��DCC����
					dcc.setMerchant_Id(VpnTim.getMerchantNo());// 42 �̻����
					dcc.setAuthor_Str(VpnTim.getAuthcode());
					dcc.setTerminal_Id(VpnTim.getTerminalNo());// 41 �̻��ն˺�
					dcc.setInvoice_No(trade.getOrderNo().substring(
							trade.getOrderNo().length() - 6,
							trade.getOrderNo().length()));

					// ��Ч��
					dcc.setOrder_No(trade.getOrderNo());// 62
					dcc.setCustom(trade.getOrderNo());
					dcc.setHashCode(VpnTim.getHashcode());
					dcc.setCurrency_Code_T("156");// ���Ҵ��� CNY
					dcc.setBocs_ReturnURL("http://172.20.66.2/sfe");
					dcc.setAmount_Loc(this.buzero(trade.getRmbAmount() + ""));// 4
					// ���ؽ��׽��
					dcc.setCard_No(cardNo);// �˺�2
					dcc.setExp_Date(tem.getExpirationYear()+tem.getExpirationMonth());// 14
					VpnUtil vu = new VpnUtil();
					Long tim1 = System.currentTimeMillis();
					try {
						// type 1 ���ʲ�ѯ
						 dcc = vu.getDCCvalue(dcc, "1");
					} catch (Exception e) {
						e.printStackTrace();
						logger.error(e);
					}
					logger.info("*********************��㷵����***************************"+dcc.getResp_Code());
					if (dcc.getResp_Code().equals("99YY")){//�˿�֧��DCC����
							logger.info("��ʼmoto DCC����");
							vpn.MotoDCCMessage moto = new vpn.MotoDCCMessage();
							moto.setTrans_Type("dccsales");// ����
							moto.setMerchant_Id(VpnTim.getMerchantNo());// 42 �̻����
							moto.setAuthor_Str(VpnTim.getAuthcode());
							moto.setTerminal_Id(VpnTim.getTerminalNo());// 41 �̻��ն˺�
							moto.setInvoice_No(trade.getOrderNo().substring(
									trade.getOrderNo().length() - 6,
									trade.getOrderNo().length()));

							moto.setTrans_Model("M");//motoͨ��
							moto.setCurrency_Code_T("156");// ���Ҵ���
							moto.setAmount_Loc(this.buzero(trade.getRmbAmount() + ""));// 4
							// ���ؽ��׽��
							moto.setCard_No(cardNo);// �˺�2
							moto.setExp_Date(tem.getExpirationYear()+tem.getExpirationMonth());// 14 ��Ч��
							moto.setCSC(tem.getCvv2());
							moto.setCurrency_Code(dcc.getCurrency_Code());
							moto.setBocs_ReturnURL("http://172.20.66.2/sfe");
							moto.setAmount_For(dcc.getAmount_For());
							moto.setOrder_No(trade.getOrderNo());
							moto.setCustom(trade.getOrderNo());
							moto.setHashCode(VpnTim.getHashcode());
							VpnUtil_Moto vm=new VpnUtil_Moto();
							try {
								// type 2 dcc����
								moto = vm.getDCCvalue(moto, "2");
							} catch (Exception e) {
								e.printStackTrace();
								logger.error(e);
							}
						if (moto.getResp_Code().equals("0000")) {//���׳ɹ�
							trade.setTradeState("1"
									+ trade.getTradeState().substring(1,
											trade.getTradeState().length()));
							trade.setTradeState(StatusUtil.updateStatus(trade.getTradeState(), 15, "3"));
							trade.setRemark("Payment Success!");
							trade.setVIPDisposePorson("System");
							trade.setVIPDisposeDate(new Date());
							trade.setVIPBatchNo(moto.getAuth_Code());
							trade.setVIPTerminalNo(VpnTim.getTerminalNo());
							trade.setVIPAuthorizationNo(moto.getInvoice_No());
							trade.setRef_No(moto.getRef_No());
							this.commonService.update(trade);
							orderList=orderList+trade.getOrderNo()+"��";
							backSussess="1";
							backMessage=VpnTim.getBillingAddress();
							if(!"1001".equals((trade.getOrderNo()).substring(0,4))&&!"4136".equals((trade.getOrderNo()).substring(0,4))){
								EmailInfo emailinfo = new EmailInfo();
								String mailinfo = emailinfo.getPaymentResultEmail(
										ic.getEmail(),
										trade.getTradeAmount(),
										getStates().getCurrencyTypeByNo(
												trade.getMoneyType().intValue()),
										trade.getTradeUrl(), trade.getTradeTime(),
										backMessage, trade.getMerchantOrderNo(),
										trade.getOrderNo());
								try {
									CCSendMail.setSendMail(ic.getEmail(), mailinfo,
											"sfepay@sfepay.com");
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}else{
							trade.setTradeState("0"
									+ trade.getTradeState().substring(1,
											trade.getTradeState().length()));
							if(!"sfe01".equals(moto.getResp_Code())){
								trade.setRemark("1093high risk!" +moto.getResp_Code());
							}
							backSussess="0";
							backMessage=moto.getResp_Code();
						}

					} else if (dcc.getResp_Code().equals("99YX")) {//��֧��DCC����
						vpn.DCCMessage moto2 = new vpn.DCCMessage();
						moto2.setTrans_Type("sales");// ����
						// �̻����
						moto2.setMerchant_Id(VpnTim.getBanktype());// 42
						moto2.setAuthor_Str(VpnTim.getAuthcode());
						// �̻��ն˺�
						moto2.setTerminal_Id(VpnTim.getAndterminalNo());// 41
						moto2.setInvoice_No(trade.getOrderNo().substring(
								trade.getOrderNo().length() - 6,
								trade.getOrderNo().length()));

						moto2.setOrder_No(trade.getOrderNo());// 62
						moto2.setCustom(trade.getOrderNo());
						moto2.setHashCode(VpnTim.getHashcode());

						moto2.setTrans_Model("M");//moto
						moto2.setCurrency_Code_T(dcc.getCurrency_Code_T());// ���Ҵ���
						moto2.setAmount_Loc(this.buzero(trade.getTradeAmount() + ""));// 4
						// ���ؽ��׽��
						moto2.setCard_No(cardNo);// �˺�2
						moto2.setExp_Date(tem.getExpirationYear()+tem.getExpirationMonth());// 14 ��Ч��
						moto2.setCSC(tem.getCvv2());
						VpnUtil vu3 = new VpnUtil();
						Long tim3 = System.currentTimeMillis();
						try {
							// type 3 edc����
							moto2 = vu3.getDCCvalue(moto2, "3");
						} catch (Exception e) {
							e.printStackTrace();
							logger.error(e);
						}
						if (moto2.getResp_Code().equals("0000")) {//���׳ɹ�
							trade.setTradeState("1"
									+ trade.getTradeState().substring(1,
											trade.getTradeState().length()));
							trade.setTradeState(StatusUtil.updateStatus(trade.getTradeState(), 15, "3"));
							trade.setRemark("Payment Success!");
							trade.setVIPDisposePorson("System");
							trade.setVIPDisposeDate(new Date());
							trade.setVIPBatchNo(moto2.getAuth_Code());
							trade.setVIPTerminalNo(VpnTim.getTerminalNo());
							trade.setVIPAuthorizationNo(moto2.getInvoice_No());
							trade.setRef_No(moto2.getRef_No());
							this.commonService.update(trade);
							orderList=orderList+trade.getOrderNo()+"��";
							backSussess="1";
							backMessage=VpnTim.getBillingAddress();
							if(!"1001".equals((trade.getOrderNo()).substring(0,4))&&!"4136".equals((trade.getOrderNo()).substring(0,4))){
								EmailInfo emailinfo = new EmailInfo();
								String mailinfo = emailinfo.getPaymentResultEmail(
										ic.getEmail(),
										trade.getTradeAmount(),
										getStates().getCurrencyTypeByNo(
												trade.getMoneyType().intValue()),
										trade.getTradeUrl(), trade.getTradeTime(),
										backMessage, trade.getMerchantOrderNo(),
										trade.getOrderNo());
								try {
									CCSendMail.setSendMail(ic.getEmail(), mailinfo,
											"sfepay@sfepay.com");
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}else{
							trade.setTradeState("0"
									+ trade.getTradeState().substring(1,
											trade.getTradeState().length()));
							if(!"sfe01".equals(moto2.getResp_Code())){
								trade.setRemark("1093high risk!" +moto2.getResp_Code());
							}
							backSussess="0";
							backMessage=moto2.getResp_Code();
						}
					}
				
			if(StringUtils.isNotBlank(backSussess)&&!"sfe01".equals(backMessage)){
				List<InternationalMerchantChannels> merChannel=this.commonService.list("from InternationalMerchantChannels mc where mc.merchantId='"+trade.getMerchantId()+"' and mc.channelId='"+VpnTim.getChannelId()+"'");
				if(merChannel!=null&&merChannel.size()>0){
					InternationalMerchantChannels mc=merChannel.get(0);
					logger.info("*********����ͨ��*************");
					trade.setTradeChannel(Long.valueOf(mc.getId()));
				}
				commonService.update(trade);
				commonService.delete(tem);
				if("1001".equals((trade.getOrderNo()).substring(0,4))||"4136".equals((trade.getOrderNo()).substring(0,4))){
				TemporarySynThread ts=new TemporarySynThread("https://www.xingbill.com/synTradeInfo",trade.getMerchantOrderNo(), backSussess, "VPN"+backMessage);
				ts.start();
				}
				if("3918".equals((trade.getOrderNo()).substring(0,4))){
					TemporarySynThread ts=new TemporarySynThread("http://www.win4mall.com/OrderAutomatic",trade.getMerchantOrderNo(),backSussess, backMessage);
					ts.start();
				}
			}
		}
						
				}
				}
//			}
		toTemporaryInfo();
		msg="���ܳɹ����ɹ��Ķ�����"+orderList;
		return SUCCESS;
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
	 public Hashtable getmmValue(HashMap hm) {

			h = exam.maxMindScore(hm);
			// ��MaxMind���صĲ�����ӡ����,
			for (Iterator i = h.keySet().iterator(); i.hasNext();) {
				String key = (String) i.next();
				String maxmindValue = (String) h.get(key);
				if (key.equals("binCountry")) {
					bankCountry = maxmindValue;
				}
			}
			Hashtable ht = new Hashtable();
			ht.put("bankCountry", bankCountry);
			return ht;
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
	public PageInfo getInfo() {
		return info;
	}
	public void setInfo(PageInfo info) {
		this.info = info;
	}
	public String getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	public String getIds() {
		return ids;
	}
	public void setIds(String ids) {
		this.ids = ids;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public MaxMindExample getExam() {
		return exam;
	}
	public void setExam(MaxMindExample exam) {
		this.exam = exam;
	}
	public String getBankCountry() {
		return bankCountry;
	}
	public void setBankCountry(String bankCountry) {
		this.bankCountry = bankCountry;
	}
	public String getChannelName() {
		return channelName;
	}
	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}
	public String getStatusType() {
		return statusType;
	}
	public void setStatusType(String statusType) {
		this.statusType = statusType;
	}

}
