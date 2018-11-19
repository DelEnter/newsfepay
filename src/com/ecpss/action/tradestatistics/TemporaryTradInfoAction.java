package com.ecpss.action.tradestatistics;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.UUID;

import net.sf.json.JSONObject;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

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
import vpn.PayClubMessage;
import vpn.PayClubUtil;
import vpn.QuanQiuPayMessage;
import vpn.QuanQiuPayUtil;
import vpn.SfeMessage;
import vpn.SfeUtil;
import vpn.VpnUtil;
import vpn.VpnUtil_Moto;
import vpn.WPPayMessage;
import vpn.WPPayUtil;
import vpn.WRPayMessage;
import vpn.WRPayUtil;
import vpn.YoungPayMessage;
import vpn.YoungPayUtil;
import vpn.ZMTPayMessage;
import vpn.ZMTPayUtil;

import com.ecpss.action.BaseAction;
import com.ecpss.action.TemporarySynThread;
import com.ecpss.action.pay.fast.TradUtil;
import com.ecpss.action.pay.fast.TradeMessage;
import com.ecpss.action.pay.util.MaxMindExample;
import com.ecpss.model.channel.InternationalMerchantChannels;
import com.ecpss.model.payment.InternationalCardholdersInfo;
import com.ecpss.model.payment.InternationalTemporaryTradInfo;
import com.ecpss.model.payment.InternationalTradeinfo;
import com.ecpss.model.risk.InternationalRiskItems;
import com.ecpss.model.shop.InternationalTerminalManager;
import com.ecpss.model.shop.InternationalWebchannels;
import com.ecpss.util.AES;
import com.ecpss.util.CCSendMail;
import com.ecpss.util.EmailInfo;
import com.ecpss.util.MD5;
import com.ecpss.util.StatusUtil;
import com.ecpss.util.StringUtil;
import com.ecpss.web.PageInfo;
import com.opensymphony.xwork2.ActionContext;

public class TemporaryTradInfoAction extends BaseAction {
	Logger logger = Logger.getLogger(TemporaryTradInfoAction.class.getName());
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
	private String statusType;//重跑页面类型 0 重跑页面   1复审重跑页面   2重跑失败复审
	private String merchantNo;
	private String remark;
	private String qip;
	private String qemail;
	private List tempDetail=new ArrayList();
	private String tranUrl;
	private String tradtime;
	private String isRisk;
	private String webType;
	private String openTime;
	private String tradeNum;
	private String tturl;
	
	private String d1;
	private String d2;
	private String d3;
	
	public String toTemporaryInfo(){
		StringBuffer sb=new StringBuffer("select tt.id,tt.orderNo,it.tradeAmount,it.moneyType,it.rmbAmount,tt.createTime,ic,it.tradeUrl,tt.remark,it.merchantOrderNo from InternationalTemporaryTradInfo tt,InternationalTradeinfo it,InternationalCardholdersInfo ic where tt.orderNo=it.orderNo and it.id=ic.tradeId ");
		if(StringUtils.isNotBlank(statusType)){
			sb.append("  and tt.status='"+statusType+"'");
		}else{
			sb.append("  and tt.status='0'");
		}
		if(StringUtils.isNotBlank(orderId)){
			sb.append(" and tt.id='"+orderId+"'");
		}
		if(StringUtils.isNotBlank(merchantNo)){
			sb.append(" and substr(tt.orderNo,1,4)='"+merchantNo+"'");
		}
		if(StringUtils.isNotBlank(orderNo)){
			sb.append(" and tt.orderNo='"+orderNo+"'");
		}
		if(StringUtils.isNotBlank(tturl)){
			sb.append(" and it.tradeUrl='"+tturl+"'");
		}
		if(StringUtils.isNotBlank(startDate)){
			sb.append(" and tt.createTime>=to_date('"+startDate+" 00:00:00','yyyy-MM-dd hh24:mi:ss') ");
		}
		if(StringUtils.isNotBlank(endDate)){
			sb.append(" and tt.createTime<=to_date('"+endDate+" 23:59:59','yyyy-MM-dd hh24:mi:ss') ");
		}
		if(StringUtils.isNotBlank(remark)){
			sb.append(" and tt.remark like '%"+remark+"%'");
		}
		if(StringUtils.isNotBlank(qip)){
			sb.append(" and ic.ip like '%"+qip+"%'");
		}
		if(StringUtils.isNotBlank(qemail)){
			sb.append(" and ic.email like '%"+qemail+"%'");
		}
		sb.append(" order by tt.createTime desc");
		info = commonService.listQueryResultByHql(sb.toString(), info);
		return SUCCESS;
	}
	public String getTemporaryDetail(){
		if(StringUtils.isNotBlank(orderId)){
			String sql="select tt.id,tt.orderNo,it.tradeAmount,it.moneyType,it.rmbAmount,tt.createTime,ic.cardNo,ic.email,ic.ip,ic.firstName,ic.lastName,ic.userBank,"
					+ "ic.country,ic.address,ic.productInfo,ic.zipcode,ic.city,ic.state,it.tradeUrl,tt.remark,ic.phone,bmm.* from "
				+ "International_Tradeinfo it left join InternationalBackMaxMind bmm on bmm.tradeId=it.id,International_TempTradInfo tt,"
				+ "International_CardholdersInfo ic where tt.orderNo=it.orderNo and ic.tradeId=it.id and tt.id=' "+orderId+"'";
			tempDetail=commonService.getByList(sql);
			getWebTradeDetail();
		}
		//InternationalTempTradInfo trade=(InternationalTempTradInfo) commonService.uniqueResult("from InternationalTempTradInfo where id='"+orderId.toString()+"'");
		//30-60
		String sql1 =  "select tt.orderNo from International_TempTradInfo tt where tt.id=' "+orderId+"'";
		List tradelist = commonService.getByList(sql1.toString());
		InternationalTradeinfo trade=(InternationalTradeinfo) commonService.uniqueResult("from InternationalTradeinfo where orderNo='"+tradelist.get(0).toString()+"'");
		Calendar c11 = Calendar.getInstance();
		c11.add(Calendar.DAY_OF_MONTH, -60);
		Calendar c12 = Calendar.getInstance();
		c12.add(Calendar.DAY_OF_MONTH, -30);
		String tmp8 = " and t.tradetime>=to_date('" + c11.getTime().toLocaleString()
				+ "','yyyy-MM-dd hh24:mi:ss') "
				+ "and t.tradetime<=to_date('" + c12.getTime().toLocaleString()
				+ "','yyyy-MM-dd hh24:mi:ss') ";
	
		StringBuffer sb5 = new StringBuffer();
		StringBuffer sb6 = new StringBuffer();
		String merNo = tradelist.get(0).toString().substring(0, 4);
		sb5.append("select count(*) from international_tradeinfo t,international_merchant m where substr(t.tradestate, 3, 1) = '1' and t.merchantid = m.id and m.merno = "
						+"'"+merNo+"'"+" and t.tradeurl= "+"'"+trade.getTradeUrl()+"'"+tmp8);
		sb6.append("select count(*) from international_tradeinfo t,international_merchant m where substr(t.tradestate, 0, 1) = '1' and t.merchantid = m.id and m.merno = "
						+"'"+merNo+"'"+" and t.tradeurl= "+"'"+trade.getTradeUrl()+"'"+tmp8);
		List tradeFourthListurl1 = commonService.getByList(sb5.toString());
		List tradeFourthListurl2 = commonService.getByList(sb6.toString());
		
		Double a1 = Double.valueOf(tradeFourthListurl1.get(0).toString());
		Double b1 = Double.valueOf(tradeFourthListurl2.get(0).toString());

		Double c1 = (double) ((a1 / b1)*100);
		d1 =  String.format("%.2f", c1);
		
		//30天
		Calendar c13 = Calendar.getInstance();
		c13.add(Calendar.DAY_OF_MONTH, -30);
		String tmp9 = " and t.tradetime>=to_date('" + c13.getTime().toLocaleString()
				+ "','yyyy-MM-dd hh24:mi:ss') "
				+ "and t.tradetime<=sysdate ";
	
		StringBuffer sb7 = new StringBuffer();
		StringBuffer sb8 = new StringBuffer();
		
		sb7.append("select count(*) from international_tradeinfo t,international_merchant m where substr(t.tradestate, 3, 1) = '1' and t.merchantid = m.id and m.merno = "
						+"'"+merNo+"'"+" and t.tradeurl= "+"'"+trade.getTradeUrl()+"'"+tmp9);
		sb8.append("select count(*) from international_tradeinfo t,international_merchant m where substr(t.tradestate, 0, 1) = '1' and t.merchantid = m.id and m.merno = "
						+"'"+merNo+"'"+" and t.tradeurl= "+"'"+trade.getTradeUrl()+"'"+tmp9);
		List tradeFourthListurl3 = commonService.getByList(sb7.toString());
		List tradeFourthListurl4 = commonService.getByList(sb8.toString());
		
		Double a2 = Double.valueOf(tradeFourthListurl3.get(0).toString());
		Double b2 = Double.valueOf(tradeFourthListurl4.get(0).toString());

		Double c2 = (double) ((a2 / b2)*100);
		d2 =  String.format("%.2f", c2);
		
		//60天
		Calendar c14 = Calendar.getInstance();
		c14.add(Calendar.DAY_OF_MONTH, -60);
		String tmp10 = " and t.tradetime>=to_date('" + c14.getTime().toLocaleString()
				+ "','yyyy-MM-dd hh24:mi:ss') "
				+ "and t.tradetime<=sysdate ";
	
		StringBuffer sb9 = new StringBuffer();
		StringBuffer sb10 = new StringBuffer();
		
		sb9.append("select count(*) from international_tradeinfo t,international_merchant m where substr(t.tradestate, 3, 1) = '1' and t.merchantid = m.id and m.merno = "
						+"'"+merNo+"'"+" and t.tradeurl= "+"'"+trade.getTradeUrl()+"'"+tmp10);
		sb10.append("select count(*) from international_tradeinfo t,international_merchant m where substr(t.tradestate, 0, 1) = '1' and t.merchantid = m.id and m.merno = "
						+"'"+merNo+"'"+" and t.tradeurl= "+"'"+trade.getTradeUrl()+"'"+tmp10);
		List tradeFourthListurl5 = commonService.getByList(sb9.toString());
		List tradeFourthListurl6 = commonService.getByList(sb10.toString());
		
		Double a3 = Double.valueOf(tradeFourthListurl5.get(0).toString());
		Double b3 = Double.valueOf(tradeFourthListurl6.get(0).toString());

		Double c3 = (double) ((a3 / b3)*100);
		d3 =  String.format("%.2f", c3);				
		return SUCCESS;
	}
	public String auditTemporaryInfo(){
		if(StringUtils.isNotBlank(ids)){
			String[] id=ids.split(",");
			for(int i=0;i<id.length;i++){
				InternationalTemporaryTradInfo tem=(InternationalTemporaryTradInfo) commonService.uniqueResult("from InternationalTemporaryTradInfo where id='"+id[i]+"'");
				tem.setStatus("1");//复审状态
				commonService.update(tem);
			}
		}
		toTemporaryInfo();
		msg="已提交去复审！";
		return SUCCESS;
	}
	
	public String riskTemporaryInfo(){
		if(StringUtils.isNotBlank(ids)){
			String[] id=ids.split(",");
			for(int i=0;i<id.length;i++){
				InternationalTemporaryTradInfo tem=(InternationalTemporaryTradInfo) commonService.uniqueResult("from InternationalTemporaryTradInfo where id='"+id[i]+"'");
				tem.setStatus("5");//疑似风险订单状态
				commonService.update(tem);
			}
		}
		toTemporaryInfo();
		msg="已提交去疑似风险订单复审！";
		return SUCCESS;
	}
	
	public String delTemporaryInfo(){
		if(StringUtils.isNotBlank(ids)){
			logger.info("删除类型："+statusType+ "  >> 订单号"+ids+"操作人："+getUserBean().getUserName());
			String[] id=ids.split(",");
			for(int i=0;i<id.length;i++){
				InternationalTemporaryTradInfo tem=(InternationalTemporaryTradInfo) commonService.uniqueResult("from InternationalTemporaryTradInfo where id='"+id[i]+"'");
				InternationalTradeinfo trade=(InternationalTradeinfo) commonService.uniqueResult("from InternationalTradeinfo where orderNo='"+tem.getOrderNo()+"'");
				if("3".equals(statusType)){
					trade.setTradeState("3"+ trade.getTradeState().substring(1,trade.getTradeState().length()));
				}else{
					trade.setTradeState("0"+ trade.getTradeState().substring(1,trade.getTradeState().length()));
				}
				if(!"2".equals(statusType)&&!"3".equals(statusType)){
				trade.setRemark("1093high risk!");
				}
				commonService.update(trade);
				commonService.delete(InternationalTemporaryTradInfo.class, new Long(id[i]));
				if("1001".equals((trade.getOrderNo()).substring(0,4))||"4136".equals((trade.getOrderNo()).substring(0,4))){
					TemporarySynThread ts=new TemporarySynThread("http://www.xingbill.com/synTradeInfo",trade.getMerchantOrderNo(),"0", trade.getRemark());
					ts.start();
					}
				if("3918".equals((trade.getOrderNo()).substring(0,4))||"4110".equals((trade.getOrderNo()).substring(0,4))){
					TemporarySynThread ts=new TemporarySynThread("http://www.win4mall.com/OrderAutomatic",trade.getMerchantOrderNo(), "0", trade.getRemark());
					ts.start();
				}
				if("4160".equals((trade.getOrderNo()).substring(0,4))||"4161".equals((trade.getOrderNo()).substring(0,4))){
					TemporarySynThread ts=new TemporarySynThread("http://www.ipasspay.biz/index.php/Thirdpay/Sfepay/notifyUrl",trade.getMerchantOrderNo(), "0", trade.getRemark());
					ts.start();
				}
				if("4165".equals((trade.getOrderNo()).substring(0,4))){
					TemporarySynThread ts=new TemporarySynThread("http://www.youkutuan.com/do.php?act=charge_okok",trade.getMerchantOrderNo(), "0", trade.getRemark());
					ts.start();
				}
				if("4169".equals((trade.getOrderNo()).substring(0,4))){
					TemporarySynThread ts=new TemporarySynThread("http://www.jjqsc.com/PayResult.php",trade.getMerchantOrderNo(), "0", trade.getRemark());
					ts.start();
				}
				if("4066".equals((trade.getOrderNo()).substring(0,4))||"4216".equals((trade.getOrderNo()).substring(0,4))){
					if("maxmaillots.org".equals(trade.getTradeUrl())){
						TemporarySynThread ts=new TemporarySynThread("http://www.maxmaillots.org/payment_online_feback.php",trade.getMerchantOrderNo(),"0", trade.getRemark());
						ts.start();
					}else{
						TemporarySynThread ts=new TemporarySynThread("http://"+trade.getTradeUrl()+"/payment_online_feback.php",trade.getMerchantOrderNo(),"0", trade.getRemark());
						ts.start();
					}
				}
			}
		}
		toTemporaryInfo();
		msg="删除成功！";
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
					msg="有未开通所选通道的商户!";
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
//				//卡号
//				String cardNo=AES.getCarNo(ic.getCardNo());
//				tm.setCardNo(cardNo);
//				//CVV
//				tm.setCvv(tem.getCvv2());
//				//卡号有效期年
//				tm.setExpirationYear("20"+tem.getExpirationYear());
//				//卡号有效期月
//				tm.setExpirationMonth(tem.getExpirationMonth());
//				tm.setPayNumber("0");
//				tm.setMerNo("10000");
//				tm.setShopName("迁易通");
//				tm.setOrderNo(trade.getOrderNo());
//				tm.setAmount(trade.getRmbAmount()+"");
//				tm.setCurrency("CNY");
//				tm.setGoodsName("物品名称");
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
//					// 加密串 license_key : UxQh0mA4aLqw 调试和正式运行时要加上,才会返回分数
//					// 上海key: CxsRZ1xPPRbR;
//					// 广州key: UxQh0mA4aLqw
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
//					// standard 低级
//					// premium 高级
//					// 正式运行的时候要用这个 premium ; standard为调试用的标准
//					hm.put("requested_type", "premium");
//
//					Hashtable ht = getmmValue(hm);
//					bankCountry = (String) ht.get("bankCountry");
//					} catch (Exception ex) {
//						try {
//							CCSendMail.setSendMail("878701211@qq.com",
//									"2.0 maxmind error", "sfepay@sfepay.com");
//						} catch (Exception e) {
//									// 继续执行下去
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
//							trade.setRemark("禁止交易地区");
//							commonService.update(trade);
//							commonService.delete(tem);
//							if("1001".equals((trade.getOrderNo()).substring(0,4))){
//							TemporarySynThread ts=new TemporarySynThread(trade.getMerchantOrderNo(), "0","high risk!");
//							ts.start();
//							}
//						}
//						else{
						if("CA".equals(channelName)){
							logger.info("开始CA交易："+trade.getOrderNo());
							CaibaoUtil tu= new CaibaoUtil();
							CaibaoMessage ms=new CaibaoMessage();
							//终端id直接写固定的，如果以后有改变，这地方也要修改
							String channelId="";
							if("3".equals(cardNo.substring(0, 1))){
								channelId="267";
							}else if("4".equals(cardNo.substring(0, 1))){
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
							ms.setReturnURL(trade.getTradeUrl());
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
							ms.setFirstName(ic.getFirstName().replace("'",""));
							ms.setLastName(ic.getLastName().replace("'",""));
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
							ms.setTcsid(trade.getCsid());
							tu.get(ms);
							logger.info("vip交易结果："+ms.getRes_orderStatus());
							String backMessage="";
							if(ms.getRes_orderStatus().equals("1")){
								trade.setTradeState("1"
										+ trade.getTradeState().substring(1,
												trade.getTradeState().length()));
								trade.setTradeState(StatusUtil.updateStatus(trade.getTradeState(), 15, "3"));
			//					trade.setRemark("Payment Declined!" + tm.getErrorCode());
								trade.setRemark("Payment Success!");
								backMessage=ms.getRes_billAddress();
								orderList=orderList+trade.getOrderNo()+"（Payment Success!）、";
								if(!"1001".equals((trade.getOrderNo()).substring(0,4))&&!"4136".equals((trade.getOrderNo()).substring(0,4))&&!"3918".equals((trade.getOrderNo()).substring(0,4))&&!"4110".equals((trade.getOrderNo()).substring(0,4))&&!"4212".equals((trade.getOrderNo()).substring(0,4))){
									logger.info("*******发送成功邮件："+trade.getOrderNo());
									EmailInfo emailinfo = new EmailInfo();
									String mailinfo = emailinfo.getPaymentResultEmail(
											ic.getEmail(),
											trade.getTradeAmount(),
											getStates().getCurrencyTypeByNo(
													trade.getMoneyType().intValue()),
											trade.getTradeUrl(), trade.getTradeTime(),
											ms.getRes_billAddress(), trade.getMerchantOrderNo(),
											trade.getOrderNo());
									try {
										CCSendMail.setSendMail(ic.getEmail(), mailinfo,
												"sfepay@sfepay.com");
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							}else if(ms.getRes_orderStatus().equals("0")){
								orderList=orderList+trade.getOrderNo()+"("+ms.getRes_orderInfo()+"）、";
//								trade.setTradeState("0"
//										+ trade.getTradeState().substring(1,
//												trade.getTradeState().length()));
								if(!"sfe01".equals(ms.getRes_orderInfo())){
									trade.setRemark("1093high risk!" + ms.getRes_orderInfo());
								}
								backMessage=trade.getRemark();
							}else if(ms.getRes_orderStatus().equals("-2")||ms.getRes_orderStatus().equals("-1")){
								trade.setTradeState("2"
										+ trade.getTradeState().substring(1,
												trade.getTradeState().length()));
							}
					if(StringUtils.isNotBlank(ms.getRes_orderStatus())&&!"sfe01".equals(ms.getRes_orderInfo())){
						List<InternationalMerchantChannels> merChannel=this.commonService.list("from InternationalMerchantChannels mc where mc.merchantId='"+trade.getMerchantId()+"' and mc.channelId='"+tim.getChannelId()+"'");
						if(merChannel!=null&&merChannel.size()>0){
							InternationalMerchantChannels mc=merChannel.get(0);
							logger.info("*********更新通道*************");
							trade.setTradeChannel(Long.valueOf(mc.getId()));
						}
						commonService.update(trade);
						if("1".equals(ms.getRes_orderStatus())){
							commonService.delete(tem);
							if("1001".equals((trade.getOrderNo()).substring(0,4))||"4136".equals((trade.getOrderNo()).substring(0,4))){
							TemporarySynThread ts=new TemporarySynThread("http://www.xingbill.com/synTradeInfo",trade.getMerchantOrderNo(), ms.getRes_orderStatus(),backMessage);
							ts.start();
							}
							if("3918".equals((trade.getOrderNo()).substring(0,4))||"4110".equals((trade.getOrderNo()).substring(0,4))){
								TemporarySynThread ts=new TemporarySynThread("http://www.win4mall.com/OrderAutomatic",trade.getMerchantOrderNo(),ms.getRes_orderStatus(), backMessage);
								ts.start();
							}
							if("4160".equals((trade.getOrderNo()).substring(0,4))||"4161".equals((trade.getOrderNo()).substring(0,4))){
								TemporarySynThread ts=new TemporarySynThread("http://www.ipasspay.biz/index.php/Thirdpay/Sfepay/notifyUrl",trade.getMerchantOrderNo(),ms.getRes_orderStatus(), backMessage);
								ts.start();
							}
							if("4165".equals((trade.getOrderNo()).substring(0,4))){
								TemporarySynThread ts=new TemporarySynThread("http://www.youkutuan.com/do.php?act=charge_okok",trade.getMerchantOrderNo(), ms.getRes_orderStatus(),backMessage);
								ts.start();
							}
							if("4169".equals((trade.getOrderNo()).substring(0,4))){
								TemporarySynThread ts=new TemporarySynThread("http://www.jjqsc.com/PayResult.php",trade.getMerchantOrderNo(),ms.getRes_orderStatus(), backMessage);
								ts.start();
							}
							if("4066".equals((trade.getOrderNo()).substring(0,4))||"4216".equals((trade.getOrderNo()).substring(0,4))){
								if("maxmaillots.org".equals(trade.getTradeUrl())){
									TemporarySynThread ts=new TemporarySynThread("http://www.maxmaillots.org/payment_online_feback.php",trade.getMerchantOrderNo(),ms.getRes_orderStatus(), backMessage);
									ts.start();
								}else{
									TemporarySynThread ts=new TemporarySynThread("http://"+trade.getTradeUrl()+"/payment_online_feback.php",trade.getMerchantOrderNo(),ms.getRes_orderStatus(), backMessage);
									ts.start();
								}
							}
						}else{
							logger.info("更新vip交易失败："+trade.getOrderNo());
							tem.setRemark(ms.getRes_orderInfo());
							tem.setStatus("2");
							commonService.update(tem);
						}
					}
				}else if("HJ".equals(channelName)){
					logger.info("进入HJ通道");
					HJPayUtil HJ=new HJPayUtil();
					HJPayMessage hm=new HJPayMessage();
					//终端id直接写固定的，如果以后有改变，这地方也要修改
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
					hm.setRetURL(trade.getTradeUrl());
					hm.setEmail(ic.getEmail());
					String hash=tim.getHashcode()+ hm.getAcctNo() + hm.getOrderID() + hm.getAmount() + hm.getCurrCode();
					logger.info("hash加密前："+hash);
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
					
					logger.info("vip交易结果："+hm.getRes_success());
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
						orderList=orderList+trade.getOrderNo()+"（Payment Success!）、";
						backSussess="1";
						backMessage=hm.getRes_billaddress();
						if(!"1001".equals((trade.getOrderNo()).substring(0,4))&&!"4136".equals((trade.getOrderNo()).substring(0,4))&&!"3918".equals((trade.getOrderNo()).substring(0,4))&&!"4110".equals((trade.getOrderNo()).substring(0,4))&&!"4212".equals((trade.getOrderNo()).substring(0,4))){
							logger.info("*******发送成功邮件："+trade.getOrderNo());
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
						orderList=orderList+trade.getOrderNo()+"("+hm.getRes_message()+"）、";
//						trade.setTradeState("0"
//								+ trade.getTradeState().substring(1,
//										trade.getTradeState().length()));
						if(!"sfe01".equals(hm.getRes_success())){
							trade.setRemark("1093high risk!" +hm.getRes_message());
						}
						backSussess="0";
						backMessage=trade.getRemark();
					}
			if(StringUtils.isNotBlank(hm.getRes_success())&&!"sfe01".equals(hm.getRes_success())){
				List<InternationalMerchantChannels> merChannel=this.commonService.list("from InternationalMerchantChannels mc where mc.merchantId='"+trade.getMerchantId()+"' and mc.channelId='"+tim.getChannelId()+"'");
				if(merChannel!=null&&merChannel.size()>0){
					InternationalMerchantChannels mc=merChannel.get(0);
					logger.info("*********更新通道*************");
					trade.setTradeChannel(Long.valueOf(mc.getId()));
				}
				commonService.update(trade);
				if("1".equals(backSussess)){
					commonService.delete(tem);
					if("1001".equals((trade.getOrderNo()).substring(0,4))||"4136".equals((trade.getOrderNo()).substring(0,4))){
					TemporarySynThread ts=new TemporarySynThread("http://www.xingbill.com/synTradeInfo",trade.getMerchantOrderNo(), backSussess, backMessage);
					ts.start();
					}
					if("3918".equals((trade.getOrderNo()).substring(0,4))||"4110".equals((trade.getOrderNo()).substring(0,4))){
						TemporarySynThread ts=new TemporarySynThread("http://www.win4mall.com/OrderAutomatic",trade.getMerchantOrderNo(),backSussess, backMessage);
						ts.start();
					}
					if("4160".equals((trade.getOrderNo()).substring(0,4))||"4161".equals((trade.getOrderNo()).substring(0,4))){
						TemporarySynThread ts=new TemporarySynThread("http://www.ipasspay.biz/index.php/Thirdpay/Sfepay/notifyUrl",trade.getMerchantOrderNo(), backSussess, backMessage);
						ts.start();
					}
					if("4165".equals((trade.getOrderNo()).substring(0,4))){
						TemporarySynThread ts=new TemporarySynThread("http://www.youkutuan.com/do.php?act=charge_okok",trade.getMerchantOrderNo(),backSussess,backMessage);
						ts.start();
					}
					if("4169".equals((trade.getOrderNo()).substring(0,4))){
						TemporarySynThread ts=new TemporarySynThread("http://www.jjqsc.com/PayResult.php",trade.getMerchantOrderNo(),backSussess, backMessage);
						ts.start();
					}
					if("4066".equals((trade.getOrderNo()).substring(0,4))||"4216".equals((trade.getOrderNo()).substring(0,4))){
						if("maxmaillots.org".equals(trade.getTradeUrl())){
							TemporarySynThread ts=new TemporarySynThread("http://www.maxmaillots.org/payment_online_feback.php",trade.getMerchantOrderNo(),backSussess, backMessage);
							ts.start();
						}else{
							TemporarySynThread ts=new TemporarySynThread("http://"+trade.getTradeUrl()+"/payment_online_feback.php",trade.getMerchantOrderNo(),backSussess, backMessage);
							ts.start();
						}
					}
				}else{
					logger.info("更新vip交易失败："+trade.getOrderNo());
					tem.setRemark(hm.getRes_message());
					tem.setStatus("2");
					commonService.update(tem);
				}
				}
			}else if("HW".equals(channelName)){
				logger.info("进入HW通道");
				HJWPayUtil hjw=new HJWPayUtil();
				HJWPayMessage hwm=new HJWPayMessage();
				//终端id直接写固定的，如果以后有改变，这地方也要修改
				String channelId="";
				if("4".equals(cardNo.substring(0, 1))){
					hwm.setCardType("1");
					channelId="273";
				}
				if("5".equals(cardNo.substring(0, 1))){
					hwm.setCardType("2");
					channelId="274";
				}
				InternationalTerminalManager tim=(InternationalTerminalManager) commonService.uniqueResult("from InternationalTerminalManager where id='"+channelId+"'");
				hwm.setMerchantId(tim.getMerchantNo());
				hwm.setMd5key(tim.getHashcode());
				Double amountAndFee=trade.getRmbAmount();
				if(trade.getChannelFee()!=null){
					amountAndFee=amountAndFee*(trade.getChannelFee()+1.0);
					amountAndFee = (double) (Math.round((double) amountAndFee * 100) / 100.00);
				}
				hwm.setAmount(amountAndFee+"");
				hwm.setCurrency("100");
				hwm.setBillNo(trade.getOrderNo());
				hwm.setCardAsn(cardNo);
				hwm.setValidity(tem.getExpirationYear()+tem.getExpirationMonth());
				hwm.setCvv(tem.getCvv2());
				hwm.setSrcUrl("www.sfepay.com");
				hwm.setFirstName(ic.getFirstName());
				hwm.setLastName(ic.getLastName());
				hwm.setAddress(ic.getAddress());
				hwm.setRemark("shoesbag");
				hwm.setEmail(ic.getEmail());
				hwm.setTelephone(ic.getPhone());
				hwm.setTradType("1");
				hjw.get(hwm);
	
				logger.info("vip交易结果："+hwm.getRes_responseCode());
				String backSussess="";
				String backMessage="";
				if(hwm.getRes_responseCode().equals("00")){
					trade.setTradeState("1"
							+ trade.getTradeState().substring(1,
									trade.getTradeState().length()));
//					trade.setTradeState(StatusUtil.updateStatus(trade.getTradeState(), 15, "3"));
//					trade.setRemark("Payment Declined!" + tm.getErrorCode());
					trade.setVIPAuthorizationNo(hwm.getRes_jcTradeId());
					trade.setRemark("Payment Success!");
					orderList=orderList+trade.getOrderNo()+"（Payment Success!）、";
					backSussess="1";
					backMessage=tim.getBillingAddress();
					if(!"1001".equals((trade.getOrderNo()).substring(0,4))&&!"4136".equals((trade.getOrderNo()).substring(0,4))&&!"3918".equals((trade.getOrderNo()).substring(0,4))&&!"4110".equals((trade.getOrderNo()).substring(0,4))&&!"4212".equals((trade.getOrderNo()).substring(0,4))){
						logger.info("*******发送成功邮件："+trade.getOrderNo());
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
				}else{
					orderList=orderList+trade.getOrderNo()+"("+hwm.getRes_addMsg()+"）、";
//					trade.setTradeState("0"
//							+ trade.getTradeState().substring(1,
//									trade.getTradeState().length()));
					if(!"sfe01".equals(hwm.getRes_responseCode())){
						trade.setRemark("1093high risk!" +hwm.getRes_addMsg());
					}
					backSussess="0";
					backMessage=trade.getRemark();
				}
		if(StringUtils.isNotBlank(hwm.getRes_responseCode())&&!"sfe01".equals(hwm.getRes_responseCode())){
			List<InternationalMerchantChannels> merChannel=this.commonService.list("from InternationalMerchantChannels mc where mc.merchantId='"+trade.getMerchantId()+"' and mc.channelId='"+tim.getChannelId()+"'");
			if(merChannel!=null&&merChannel.size()>0){
				InternationalMerchantChannels mc=merChannel.get(0);
				logger.info("*********更新通道*************");
				trade.setTradeChannel(Long.valueOf(mc.getId()));
			}
			commonService.update(trade);
			if("1".equals(backSussess)){
				commonService.delete(tem);
				if("1001".equals((trade.getOrderNo()).substring(0,4))||"4136".equals((trade.getOrderNo()).substring(0,4))){
				TemporarySynThread ts=new TemporarySynThread("http://www.xingbill.com/synTradeInfo",trade.getMerchantOrderNo(), backSussess, backMessage);
				ts.start();
				}
				if("3918".equals((trade.getOrderNo()).substring(0,4))||"4110".equals((trade.getOrderNo()).substring(0,4))){
					TemporarySynThread ts=new TemporarySynThread("http://www.win4mall.com/OrderAutomatic",trade.getMerchantOrderNo(),backSussess, backMessage);
					ts.start();
				}
				if("4160".equals((trade.getOrderNo()).substring(0,4))||"4161".equals((trade.getOrderNo()).substring(0,4))){
					TemporarySynThread ts=new TemporarySynThread("http://www.ipasspay.biz/index.php/Thirdpay/Sfepay/notifyUrl",trade.getMerchantOrderNo(), backSussess, trade.getRemark());
					ts.start();
				}
				if("4165".equals((trade.getOrderNo()).substring(0,4))){
					TemporarySynThread ts=new TemporarySynThread("http://www.youkutuan.com/do.php?act=charge_okok",trade.getMerchantOrderNo(),backSussess,backMessage);
					ts.start();
				}
				if("4169".equals((trade.getOrderNo()).substring(0,4))){
					TemporarySynThread ts=new TemporarySynThread("http://www.jjqsc.com/PayResult.php",trade.getMerchantOrderNo(),backSussess, backMessage);
					ts.start();
				}
				if("4066".equals((trade.getOrderNo()).substring(0,4))||"4216".equals((trade.getOrderNo()).substring(0,4))){
					if("maxmaillots.org".equals(trade.getTradeUrl())){
						TemporarySynThread ts=new TemporarySynThread("http://www.maxmaillots.org/payment_online_feback.php",trade.getMerchantOrderNo(),backSussess, backMessage);
						ts.start();
					}else{
						TemporarySynThread ts=new TemporarySynThread("http://"+trade.getTradeUrl()+"/payment_online_feback.php",trade.getMerchantOrderNo(),backSussess, backMessage);
						ts.start();
					}
				}
			}else{
				logger.info("更新vip交易失败："+trade.getOrderNo());
				tem.setRemark(hwm.getRes_addMsg());
				tem.setStatus("2");
				commonService.update(tem);
			}
			}
		}else if("VPN".equals(channelName)){
					String backSussess="";
					String backMessage="";
					//非3DMOTO交易
	                logger.info("进入到VPN通道");
					vpn.DCCMessage dcc = new vpn.DCCMessage();
					//终端id直接写固定的，如果以后有改变，这地方也要修改
					InternationalTerminalManager VpnTim=(InternationalTerminalManager) commonService.uniqueResult("from InternationalTerminalManager where id='259' and onoff='1'");
					dcc.setTrans_Type("enqrate");// 查询此卡是否支持DCC交易
					dcc.setMerchant_Id(VpnTim.getMerchantNo());// 42 商户编号
					dcc.setAuthor_Str(VpnTim.getAuthcode());
					dcc.setTerminal_Id(VpnTim.getTerminalNo());// 41 商户终端号
					dcc.setInvoice_No(trade.getOrderNo().substring(
							trade.getOrderNo().length() - 6,
							trade.getOrderNo().length()));

					// 有效期
					dcc.setOrder_No(trade.getOrderNo());// 62
					dcc.setCustom(trade.getOrderNo());
					dcc.setHashCode(VpnTim.getHashcode());
					dcc.setCurrency_Code_T("156");// 货币代码 CNY
					dcc.setBocs_ReturnURL("http://172.20.66.2/sfe");
					dcc.setAmount_Loc(this.buzero(trade.getRmbAmount() + ""));// 4
					// 本地交易金额
					dcc.setCard_No(cardNo);// 账号2
					dcc.setExp_Date(tem.getExpirationYear()+tem.getExpirationMonth());// 14
					VpnUtil vu = new VpnUtil();
					Long tim1 = System.currentTimeMillis();
					try {
						// type 1 汇率查询
						 dcc = vu.getDCCvalue(dcc, "1");
					} catch (Exception e) {
						e.printStackTrace();
						logger.error(e);
					}
					logger.info("*********************查汇返回码***************************"+dcc.getResp_Code());
					if (dcc.getResp_Code().equals("99YY")){//此卡支持DCC交易
							logger.info("开始moto DCC交易");
							vpn.MotoDCCMessage moto = new vpn.MotoDCCMessage();
							moto.setTrans_Type("dccsales");// 类型
							moto.setMerchant_Id(VpnTim.getMerchantNo());// 42 商户编号
							moto.setAuthor_Str(VpnTim.getAuthcode());
							moto.setTerminal_Id(VpnTim.getTerminalNo());// 41 商户终端号
							moto.setInvoice_No(trade.getOrderNo().substring(
									trade.getOrderNo().length() - 6,
									trade.getOrderNo().length()));

							moto.setTrans_Model("M");//moto通道
							moto.setCurrency_Code_T("156");// 货币代码
							moto.setAmount_Loc(this.buzero(trade.getRmbAmount() + ""));// 4
							// 本地交易金额
							moto.setCard_No(cardNo);// 账号2
							moto.setExp_Date(tem.getExpirationYear()+tem.getExpirationMonth());// 14 有效期
							moto.setCSC(tem.getCvv2());
							moto.setCurrency_Code(dcc.getCurrency_Code());
							moto.setBocs_ReturnURL("http://172.20.66.2/sfe");
							moto.setAmount_For(dcc.getAmount_For());
							moto.setOrder_No(trade.getOrderNo());
							moto.setCustom(trade.getOrderNo());
							moto.setHashCode(VpnTim.getHashcode());
							VpnUtil_Moto vm=new VpnUtil_Moto();
							try {
								// type 2 dcc交易
								moto = vm.getDCCvalue(moto, "2");
							} catch (Exception e) {
								e.printStackTrace();
								logger.error(e);
							}
						if (moto.getResp_Code().equals("0000")) {//交易成功
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
							orderList=orderList+trade.getOrderNo()+"（Payment Success!）、";
							backSussess="1";
							backMessage=VpnTim.getBillingAddress();
							if(!"1001".equals((trade.getOrderNo()).substring(0,4))&&!"4136".equals((trade.getOrderNo()).substring(0,4))&&!"3918".equals((trade.getOrderNo()).substring(0,4))&&!"4110".equals((trade.getOrderNo()).substring(0,4))&&!"4212".equals((trade.getOrderNo()).substring(0,4))){
								logger.info("*******发送成功邮件："+trade.getOrderNo());
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
							orderList=orderList+trade.getOrderNo()+"("+moto.getResp_Code()+"）、";
//							trade.setTradeState("0"
//									+ trade.getTradeState().substring(1,
//											trade.getTradeState().length()));
							if(!"sfe01".equals(moto.getResp_Code())){
								trade.setRemark("1093high risk!" +moto.getResp_Code());
							}
							backSussess="0";
							backMessage=trade.getRemark();
						}

					} else if (dcc.getResp_Code().equals("99YX")) {//不支持DCC交易
						vpn.DCCMessage moto2 = new vpn.DCCMessage();
						moto2.setTrans_Type("sales");// 类型
						// 商户编号
						moto2.setMerchant_Id(VpnTim.getBanktype());// 42
						moto2.setAuthor_Str(VpnTim.getAuthcode());
						// 商户终端号
						moto2.setTerminal_Id(VpnTim.getAndterminalNo());// 41
						moto2.setInvoice_No(trade.getOrderNo().substring(
								trade.getOrderNo().length() - 6,
								trade.getOrderNo().length()));

						moto2.setOrder_No(trade.getOrderNo());// 62
						moto2.setCustom(trade.getOrderNo());
						moto2.setHashCode(VpnTim.getHashcode());

						moto2.setTrans_Model("M");//moto
						moto2.setCurrency_Code_T(dcc.getCurrency_Code_T());// 货币代码
						moto2.setAmount_Loc(this.buzero(trade.getTradeAmount() + ""));// 4
						// 本地交易金额
						moto2.setCard_No(cardNo);// 账号2
						moto2.setExp_Date(tem.getExpirationYear()+tem.getExpirationMonth());// 14 有效期
						moto2.setCSC(tem.getCvv2());
						VpnUtil vu3 = new VpnUtil();
						Long tim3 = System.currentTimeMillis();
						try {
							// type 3 edc交易
							moto2 = vu3.getDCCvalue(moto2, "3");
						} catch (Exception e) {
							e.printStackTrace();
							logger.error(e);
						}
						if (moto2.getResp_Code().equals("0000")) {//交易成功
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
							orderList=orderList+trade.getOrderNo()+"（Payment Success!）、";
							backSussess="1";
							backMessage=VpnTim.getBillingAddress();
							if(!"1001".equals((trade.getOrderNo()).substring(0,4))&&!"4136".equals((trade.getOrderNo()).substring(0,4))&&!"3918".equals((trade.getOrderNo()).substring(0,4))&&!"4110".equals((trade.getOrderNo()).substring(0,4))&&!"4212".equals((trade.getOrderNo()).substring(0,4))){
								logger.info("*******发送成功邮件："+trade.getOrderNo());
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
							orderList=orderList+trade.getOrderNo()+"("+moto2.getResp_Code()+"）、";
//							trade.setTradeState("0"
//									+ trade.getTradeState().substring(1,
//											trade.getTradeState().length()));
							if(!"sfe01".equals(moto2.getResp_Code())){
								trade.setRemark("1093high risk!" +moto2.getResp_Code());
							}
							backSussess="0";
							backMessage=trade.getRemark();
						}
					}
				
			if(StringUtils.isNotBlank(backSussess)&&!"sfe01".equals(backMessage)){
				List<InternationalMerchantChannels> merChannel=this.commonService.list("from InternationalMerchantChannels mc where mc.merchantId='"+trade.getMerchantId()+"' and mc.channelId='"+VpnTim.getChannelId()+"'");
				if(merChannel!=null&&merChannel.size()>0){
					InternationalMerchantChannels mc=merChannel.get(0);
					logger.info("*********更新通道*************");
					trade.setTradeChannel(Long.valueOf(mc.getId()));
				}
				commonService.update(trade);
				if("1".equals(backSussess)){
					commonService.delete(tem);
					if("1001".equals((trade.getOrderNo()).substring(0,4))||"4136".equals((trade.getOrderNo()).substring(0,4))){
					TemporarySynThread ts=new TemporarySynThread("http://www.xingbill.com/synTradeInfo",trade.getMerchantOrderNo(), backSussess, backMessage);
					ts.start();
					}
					if("3918".equals((trade.getOrderNo()).substring(0,4))||"4110".equals((trade.getOrderNo()).substring(0,4))){
						TemporarySynThread ts=new TemporarySynThread("http://www.win4mall.com/OrderAutomatic",trade.getMerchantOrderNo(),backSussess, backMessage);
						ts.start();
					}
					if("4160".equals((trade.getOrderNo()).substring(0,4))||"4161".equals((trade.getOrderNo()).substring(0,4))){
						TemporarySynThread ts=new TemporarySynThread("http://www.ipasspay.biz/index.php/Thirdpay/Sfepay/notifyUrl",trade.getMerchantOrderNo(), backSussess, trade.getRemark());
						ts.start();
					}
					if("4165".equals((trade.getOrderNo()).substring(0,4))){
						TemporarySynThread ts=new TemporarySynThread("http://www.youkutuan.com/do.php?act=charge_okok",trade.getMerchantOrderNo(),backSussess,backMessage);
						ts.start();
					}
					if("4169".equals((trade.getOrderNo()).substring(0,4))){
						TemporarySynThread ts=new TemporarySynThread("http://www.jjqsc.com/PayResult.php",trade.getMerchantOrderNo(),backSussess, backMessage);
						ts.start();
					}
					if("4066".equals((trade.getOrderNo()).substring(0,4))||"4216".equals((trade.getOrderNo()).substring(0,4))){
						if("maxmaillots.org".equals(trade.getTradeUrl())){
							TemporarySynThread ts=new TemporarySynThread("http://www.maxmaillots.org/payment_online_feback.php",trade.getMerchantOrderNo(),backSussess, backMessage);
							ts.start();
						}else{
							TemporarySynThread ts=new TemporarySynThread("http://"+trade.getTradeUrl()+"/payment_online_feback.php",trade.getMerchantOrderNo(),backSussess, backMessage);
							ts.start();
						}
					}
				}else{
					logger.info("更新vip交易失败："+trade.getOrderNo());
					tem.setRemark(backMessage);
					tem.setStatus("2");
					commonService.update(tem);
				}
			}
		}else if("GP".equals(channelName)){
			logger.info("进入GooPay通道");
			GooPayMessage msg=new GooPayMessage();
			GooPayUtil yu=new GooPayUtil();
			msg.setMerchantMID("2295");
			//msg.setMerchantMID("2558");
			String channelId="";
			if("4".equals(cardNo.substring(0, 1))){
				msg.setNewcardtype("4");
				channelId="268";
			}
			if("5".equals(cardNo.substring(0, 1))){
				msg.setNewcardtype("5");
				channelId="269";
			}
			InternationalTerminalManager tim=(InternationalTerminalManager) commonService.uniqueResult("from InternationalTerminalManager where id='"+channelId+"'");
			
			List<InternationalMerchantChannels> imc=this.commonService.list("from InternationalMerchantChannels mc where mc.merchantId='"+trade.getMerchantId()+"' and mc.channelId='"+tim.getChannelId()+"'");
			if(imc!=null&&imc.size()>0){
				InternationalMerchantChannels channel=imc.get(0);
				logger.info("*********更新通道使用费*************");
				trade.setChannelFee(channel.getChannelFee());
			}
			
			
			BASE64Encoder baseE=new BASE64Encoder(); 
			msg.setCardnum(baseE.encode(cardNo.getBytes()));
			msg.setCvv2(baseE.encode(tem.getCvv2().getBytes()));
			msg.setYear(baseE.encode(("20"+tem.getExpirationYear()).getBytes()));
			msg.setMonth(baseE.encode(tem.getExpirationMonth().getBytes()));
			msg.setCardbank(ic.getUserBank());
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
			msg.setReturnURL("www.sfepay.com");
			MD5 md5=new MD5();
//			String md5Hash = msg.getMerchantMID() + msg.getBillNo() + msg.getCurrency() + msg.getAmount() + msg.getLanguage() + msg.getReturnURL() + "tLYzLyUN";
			String md5Hash = msg.getMerchantMID() + msg.getBillNo() + msg.getCurrency() + msg.getAmount() + msg.getLanguage() + msg.getReturnURL() + "vxXEJXmk";
			//String md5Hash=msg.getMerchantMID()+msg.getBillNo() +msg.getCurrency() +msg.getAmount() +msg.getLanguage()+msg.getReturnURL()+"Yu^HJXBd";
			msg.setHASH(md5.getMD5ofStr(md5Hash));
			msg.setShippingFirstName(ic.getShippingFullName());
			msg.setShippingLastName(ic.getLastName());
			msg.setShippingEmail(ic.getShippingEmail());
			msg.setShippingPhone(ic.getShippingPhone());
			msg.setShippingZipcode(ic.getShippingZip());
			msg.setShippingAddress(ic.getShippingAddress());
			msg.setShippingCity(ic.getShippingCity());
			msg.setShippingSstate(ic.getShippingState());
			msg.setShippingCountry(ic.getShippingCountry());
			msg.setProducts(ic.getProductInfo());
			msg.setFirstname(ic.getFirstName());
			msg.setLastname(ic.getLastName());
			msg.setEmail(ic.getEmail());
			msg.setPhone(ic.getPhone());
			msg.setZipcode(ic.getZipcode());
			msg.setAddress(ic.getAddress());
			msg.setCity(ic.getCity());
			msg.setState(ic.getState());
			msg.setCountry(ic.getCountry());
			msg.setIpAddr(ic.getIp().split(",")[0]);
			yu.get(msg);

			logger.info("vip交易结果："+msg.getSucceed());
			String backSussess="";
			String backMessage="";
			if(msg.getSucceed().equals("88")){
				trade.setTradeState("1"
						+ trade.getTradeState().substring(1,
								trade.getTradeState().length()));
//				trade.setTradeState(StatusUtil.updateStatus(trade.getTradeState(), 15, "3"));
//				trade.setRemark("Payment Declined!" + tm.getErrorCode());
				trade.setVIPDisposePorson("System");
				trade.setVIPDisposeDate(new Date());
				trade.setVIPBatchNo(msg.getGrn());
				trade.setRemark("Payment Success!");
				orderList=orderList+trade.getOrderNo()+"（Payment Success!）、";
				backSussess="1";
				backMessage=msg.getBillingAddress();
				if(!"1001".equals((trade.getOrderNo()).substring(0,4))&&!"4136".equals((trade.getOrderNo()).substring(0,4))&&!"3918".equals((trade.getOrderNo()).substring(0,4))&&!"4110".equals((trade.getOrderNo()).substring(0,4))&&!"4212".equals((trade.getOrderNo()).substring(0,4))){
					logger.info("*******发送成功邮件："+trade.getOrderNo());
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
				orderList=orderList+trade.getOrderNo()+"("+msg.getRemark()+")、";
//				trade.setTradeState("0"
//						+ trade.getTradeState().substring(1,
//								trade.getTradeState().length()));
				if(!"sfe01".equals(msg.getSucceed())){
					trade.setRemark("1093high risk!" +msg.getRemark());
				}
				backSussess="0";
				backMessage=trade.getRemark();
			}
	if(StringUtils.isNotBlank(msg.getSucceed())&&!"sfe01".equals(msg.getSucceed())){
		List<InternationalMerchantChannels> merChannel=this.commonService.list("from InternationalMerchantChannels mc where mc.merchantId='"+trade.getMerchantId()+"' and mc.channelId='"+tim.getChannelId()+"'");
		if(merChannel!=null&&merChannel.size()>0){
			InternationalMerchantChannels mc=merChannel.get(0);
			logger.info("*********更新通道*************");
			trade.setTradeChannel(Long.valueOf(mc.getId()));
		}
		commonService.update(trade);
		if("1".equals(backSussess)){
			commonService.delete(tem);
			if("1001".equals((trade.getOrderNo()).substring(0,4))||"4136".equals((trade.getOrderNo()).substring(0,4))){
			TemporarySynThread ts=new TemporarySynThread("http://www.xingbill.com/synTradeInfo",trade.getMerchantOrderNo(), backSussess, backMessage);
			ts.start();
			}
			if("3918".equals((trade.getOrderNo()).substring(0,4))||"4110".equals((trade.getOrderNo()).substring(0,4))){
				TemporarySynThread ts=new TemporarySynThread("http://www.win4mall.com/OrderAutomatic",trade.getMerchantOrderNo(),backSussess, backMessage);
				ts.start();
			}
			if("4160".equals((trade.getOrderNo()).substring(0,4))||"4161".equals((trade.getOrderNo()).substring(0,4))){
				TemporarySynThread ts=new TemporarySynThread("http://www.ipasspay.biz/index.php/Thirdpay/Sfepay/notifyUrl",trade.getMerchantOrderNo(), backSussess, trade.getRemark());
				ts.start();
			}
			if("4165".equals((trade.getOrderNo()).substring(0,4))){
				TemporarySynThread ts=new TemporarySynThread("http://www.youkutuan.com/do.php?act=charge_okok",trade.getMerchantOrderNo(),backSussess,backMessage);
				ts.start();
			}
			if("4169".equals((trade.getOrderNo()).substring(0,4))){
				TemporarySynThread ts=new TemporarySynThread("http://www.jjqsc.com/PayResult.php",trade.getMerchantOrderNo(),backSussess, backMessage);
				ts.start();
			}
			if("4066".equals((trade.getOrderNo()).substring(0,4))||"4216".equals((trade.getOrderNo()).substring(0,4))){
				if("maxmaillots.org".equals(trade.getTradeUrl())){
					TemporarySynThread ts=new TemporarySynThread("http://www.maxmaillots.org/payment_online_feback.php",trade.getMerchantOrderNo(),backSussess, backMessage);
					ts.start();
				}else{
					TemporarySynThread ts=new TemporarySynThread("http://"+trade.getTradeUrl()+"/payment_online_feback.php",trade.getMerchantOrderNo(),backSussess, backMessage);
					ts.start();
				}
			}
		}else{
			logger.info("更新vip交易失败："+trade.getOrderNo());
			tem.setRemark(msg.getRemark());
			tem.setStatus("2");
			commonService.update(tem);
		}
		}
		}else if("GD".equals(channelName)){
			logger.info("进入GooPay3D通道");
			GooPayMessage msg=new GooPayMessage();
			GooPayUtil yu=new GooPayUtil();
			//msg.setMerchantMID("2295");
			msg.setMerchantMID("2558");
			String channelId="";
			if("4".equals(cardNo.substring(0, 1))){
				msg.setNewcardtype("4");
				channelId="307";
			}
			if("5".equals(cardNo.substring(0, 1))){
				msg.setNewcardtype("5");
				channelId="308";
			}
			InternationalTerminalManager tim=(InternationalTerminalManager) commonService.uniqueResult("from InternationalTerminalManager where id='"+channelId+"'");
			
			List<InternationalMerchantChannels> imc=this.commonService.list("from InternationalMerchantChannels mc where mc.merchantId='"+trade.getMerchantId()+"' and mc.channelId='"+tim.getChannelId()+"'");
			if(imc!=null&&imc.size()>0){
				InternationalMerchantChannels channel=imc.get(0);
				logger.info("*********更新通道使用费*************");
				trade.setChannelFee(channel.getChannelFee());
			}
			
			BASE64Encoder baseE=new BASE64Encoder(); 
			msg.setCardnum(baseE.encode(cardNo.getBytes()));
			msg.setCvv2(baseE.encode(tem.getCvv2().getBytes()));
			msg.setYear(baseE.encode(("20"+tem.getExpirationYear()).getBytes()));
			msg.setMonth(baseE.encode(tem.getExpirationMonth().getBytes()));
			msg.setCardbank(ic.getUserBank());
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
			msg.setReturnURL("www.sfepay.com");
			MD5 md5=new MD5();
			String md5Hash = msg.getMerchantMID() + msg.getBillNo() + msg.getCurrency() + msg.getAmount() + msg.getLanguage() + msg.getReturnURL() + "tLYzLyUN";
			//String md5Hash = msg.getMerchantMID() + msg.getBillNo() + msg.getCurrency() + msg.getAmount() + msg.getLanguage() + msg.getReturnURL() + "vxXEJXmk";
			//String md5Hash=msg.getMerchantMID()+msg.getBillNo() +msg.getCurrency() +msg.getAmount() +msg.getLanguage()+msg.getReturnURL()+"Yu^HJXBd";
			msg.setHASH(md5.getMD5ofStr(md5Hash));
			msg.setShippingFirstName(ic.getShippingFullName());
			msg.setShippingLastName(ic.getLastName());
			msg.setShippingEmail(ic.getShippingEmail());
			msg.setShippingPhone(ic.getShippingPhone());
			msg.setShippingZipcode(ic.getShippingZip());
			msg.setShippingAddress(ic.getShippingAddress());
			msg.setShippingCity(ic.getShippingCity());
			msg.setShippingSstate(ic.getShippingState());
			msg.setShippingCountry(ic.getShippingCountry());
			msg.setProducts(ic.getProductInfo());
			msg.setFirstname(ic.getFirstName());
			msg.setLastname(ic.getLastName());
			msg.setEmail(ic.getEmail());
			msg.setPhone(ic.getPhone());
			msg.setZipcode(ic.getZipcode());
			msg.setAddress(ic.getAddress());
			msg.setCity(ic.getCity());
			msg.setState(ic.getState());
			msg.setCountry(ic.getCountry());
			msg.setIpAddr(ic.getIp().split(",")[0]);
			yu.get(msg);

			logger.info("vip交易结果："+msg.getSucceed());
			String backSussess="";
			String backMessage="";
			if(msg.getSucceed().equals("88")){
				trade.setTradeState("1"
						+ trade.getTradeState().substring(1,
								trade.getTradeState().length()));
//				trade.setTradeState(StatusUtil.updateStatus(trade.getTradeState(), 15, "3"));
//				trade.setRemark("Payment Declined!" + tm.getErrorCode());
				trade.setVIPDisposePorson("System");
				trade.setVIPDisposeDate(new Date());
				trade.setVIPBatchNo(msg.getGrn());
				trade.setRemark("Payment Success!");
				orderList=orderList+trade.getOrderNo()+"（Payment Success!）、";
				backSussess="1";
				backMessage=msg.getBillingAddress();
				if(!"1001".equals((trade.getOrderNo()).substring(0,4))&&!"4136".equals((trade.getOrderNo()).substring(0,4))&&!"3918".equals((trade.getOrderNo()).substring(0,4))&&!"4110".equals((trade.getOrderNo()).substring(0,4))&&!"4212".equals((trade.getOrderNo()).substring(0,4))){
					logger.info("*******发送成功邮件："+trade.getOrderNo());
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
				orderList=orderList+trade.getOrderNo()+"("+msg.getRemark()+")、";
//				trade.setTradeState("0"
//						+ trade.getTradeState().substring(1,
//								trade.getTradeState().length()));
				if(!"sfe01".equals(msg.getSucceed())){
					trade.setRemark("1093high risk!" +msg.getRemark());
				}
				backSussess="0";
				backMessage=trade.getRemark();
			}
	if(StringUtils.isNotBlank(msg.getSucceed())&&!"sfe01".equals(msg.getSucceed())){
		List<InternationalMerchantChannels> merChannel=this.commonService.list("from InternationalMerchantChannels mc where mc.merchantId='"+trade.getMerchantId()+"' and mc.channelId='"+tim.getChannelId()+"'");
		if(merChannel!=null&&merChannel.size()>0){
			InternationalMerchantChannels mc=merChannel.get(0);
			logger.info("*********更新通道*************");
			trade.setTradeChannel(Long.valueOf(mc.getId()));
		}
		commonService.update(trade);
		if("1".equals(backSussess)){
			commonService.delete(tem);
			if("1001".equals((trade.getOrderNo()).substring(0,4))||"4136".equals((trade.getOrderNo()).substring(0,4))){
			TemporarySynThread ts=new TemporarySynThread("http://www.xingbill.com/synTradeInfo",trade.getMerchantOrderNo(), backSussess, backMessage);
			ts.start();
			}
			if("3918".equals((trade.getOrderNo()).substring(0,4))||"4110".equals((trade.getOrderNo()).substring(0,4))){
				TemporarySynThread ts=new TemporarySynThread("http://www.win4mall.com/OrderAutomatic",trade.getMerchantOrderNo(),backSussess, backMessage);
				ts.start();
			}
			if("4160".equals((trade.getOrderNo()).substring(0,4))||"4161".equals((trade.getOrderNo()).substring(0,4))){
				TemporarySynThread ts=new TemporarySynThread("http://www.ipasspay.biz/index.php/Thirdpay/Sfepay/notifyUrl",trade.getMerchantOrderNo(), backSussess, trade.getRemark());
				ts.start();
			}
			if("4165".equals((trade.getOrderNo()).substring(0,4))){
				TemporarySynThread ts=new TemporarySynThread("http://www.youkutuan.com/do.php?act=charge_okok",trade.getMerchantOrderNo(),backSussess,backMessage);
				ts.start();
			}
			if("4169".equals((trade.getOrderNo()).substring(0,4))){
				TemporarySynThread ts=new TemporarySynThread("http://www.jjqsc.com/PayResult.php",trade.getMerchantOrderNo(),backSussess, backMessage);
				ts.start();
			}
			if("4066".equals((trade.getOrderNo()).substring(0,4))||"4216".equals((trade.getOrderNo()).substring(0,4))){
				if("maxmaillots.org".equals(trade.getTradeUrl())){
					TemporarySynThread ts=new TemporarySynThread("http://www.maxmaillots.org/payment_online_feback.php",trade.getMerchantOrderNo(),backSussess, backMessage);
					ts.start();
				}else{
					TemporarySynThread ts=new TemporarySynThread("http://"+trade.getTradeUrl()+"/payment_online_feback.php",trade.getMerchantOrderNo(),backSussess, backMessage);
					ts.start();
				}
			}
		}else{
			logger.info("更新vip交易失败："+trade.getOrderNo());
			tem.setRemark(msg.getRemark());
			tem.setStatus("2");
			commonService.update(tem);
		}
		}
		}else if("ZF".equals(channelName)){
			logger.info("进入ZMT通道");
			ZMTPayMessage msg=new ZMTPayMessage();
			ZMTPayUtil yu=new ZMTPayUtil();

			String channelId="";
			if("4".equals(cardNo.substring(0, 1))){
				//msg.setNewcardtype("4");
				channelId="300";
			}
			if("5".equals(cardNo.substring(0, 1))){
				//msg.setNewcardtype("5");
				channelId="301";
			}
			InternationalTerminalManager tim=(InternationalTerminalManager) commonService.uniqueResult("from InternationalTerminalManager where id='"+channelId+"'");
			msg.setMerNo(tim.getMerchantNo());
			msg.setBillNo(trade.getOrderNo());
			
			
			msg.setPayCurrency("CNY");
			Double amountAndFee=trade.getRmbAmount();
			if(trade.getChannelFee()!=null){
				amountAndFee=amountAndFee*(trade.getChannelFee()+1.0);
				amountAndFee = (double) (Math.round((double) amountAndFee * 100) / 100.00);
			}
			msg.setAmount(amountAndFee+ "");
			
			msg.setCurrency("15");
			msg.setWebsite(trade.getTradeUrl());
			msg.setIe_language("EN");
			msg.setDrawee("sfepay");
			JSONObject json = new JSONObject();
			json.put("name", ic.getProductInfo()+"");
			json.put("price", msg.getAmount()+"");
			json.put("num", "1");
						
			msg.setET_GOODS("["+json.toString()+"]");//json数组
			
			msg.setRemark("sfepay");
			
			
			msg.setCardNo(cardNo);
			msg.setCardExpireMonth(tem.getExpirationMonth());
			msg.setCardExpireYear("20"+tem.getExpirationYear());
			
			
			
			msg.setFirstName(ic.getShippingFullName());
			msg.setLastName(ic.getLastName());
			msg.setAddress(ic.getAddress());
			msg.setCity(ic.getCity());
			msg.setIp(ic.getIp().split(",")[0]);
			
			msg.setCvv2(tem.getCvv2());
			msg.setZip(ic.getZipcode());
			msg.setCountry(ic.getCountry());//待定
			msg.setEmail(ic.getEmail());
			msg.setPhone(ic.getPhone());
			
			String cardbank=ic.getUserBank();
			if(StringUtils.isBlank(cardbank)||cardbank.length()<2){
				cardbank="Bank";
			}

			msg.setIssuingBank(cardbank);

			String sign=msg.getMerNo().trim()+msg.getBillNo().trim()+msg.getAmount().trim()+msg.getCurrency().trim()+msg.getCardNo().trim()+msg.getCardExpireMonth().trim()+msg.getCardExpireYear().trim() + msg.getFirstName().trim() + msg.getLastName().trim()+msg.getAddress().trim()+msg.getCity().trim()+msg.getIp().trim()+msg.getCvv2().trim()+msg.getZip().trim()+msg.getCountry().trim()+msg.getEmail().trim()+msg.getPhone().trim()+msg.getIssuingBank().trim()+tim.getHashcode().trim();
			String strDes = getSha256(sign); 
			msg.setSHA256info(strDes);
			
			String state=ic.getState();
			if(StringUtils.isBlank(state)){
				state="state";
			}
			msg.setBillingstate(state);
						
			yu.get(msg);

			logger.info("vip交易结果："+msg.getRespCode());
			String backSussess="";
			String backMessage="";
			if(msg.getRespCode().equals("0000")||msg.getRespCode().equals("1")){
				trade.setTradeState("1"
						+ trade.getTradeState().substring(1,
								trade.getTradeState().length()));
//				trade.setTradeState(StatusUtil.updateStatus(trade.getTradeState(), 15, "3"));
//				trade.setRemark("Payment Declined!" + tm.getErrorCode());
				trade.setVIPDisposePorson("System");
				trade.setVIPDisposeDate(new Date());
				trade.setVIPBatchNo(msg.getTradeNo());
				trade.setRemark("Payment Success!");
				orderList=orderList+trade.getOrderNo()+"（Payment Success!）、";
				backSussess="1";
				if("4".equals(cardNo.substring(0, 1))){
					backMessage="FTP*Neocube INC";//账单地址
				}else{
					backMessage="younthtech";
				}
				if(!"1001".equals((trade.getOrderNo()).substring(0,4))&&!"4136".equals((trade.getOrderNo()).substring(0,4))&&!"3918".equals((trade.getOrderNo()).substring(0,4))&&!"4110".equals((trade.getOrderNo()).substring(0,4))&&!"4212".equals((trade.getOrderNo()).substring(0,4))){
					logger.info("*******发送成功邮件："+trade.getOrderNo());
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
				orderList=orderList+trade.getOrderNo()+"("+msg.getRespCode()+")、";
//				trade.setTradeState("0"
//						+ trade.getTradeState().substring(1,
//								trade.getTradeState().length()));
				if(!"sfe01".equals(msg.getRespCode())){
					trade.setRemark("1093high risk!" +msg.getRespCode());
				}
				backSussess="0";
				backMessage=trade.getRemark();
			}
	if(StringUtils.isNotBlank(msg.getRespCode())&&!"sfe01".equals(msg.getRespCode())){
		List<InternationalMerchantChannels> merChannel=this.commonService.list("from InternationalMerchantChannels mc where mc.merchantId='"+trade.getMerchantId()+"' and mc.channelId='"+tim.getChannelId()+"'");
		if(merChannel!=null&&merChannel.size()>0){
			InternationalMerchantChannels mc=merChannel.get(0);
			logger.info("*********更新通道*************");
			trade.setTradeChannel(Long.valueOf(mc.getId()));
		}
		commonService.update(trade);
		if("1".equals(backSussess)){
			commonService.delete(tem);
			if("1001".equals((trade.getOrderNo()).substring(0,4))||"4136".equals((trade.getOrderNo()).substring(0,4))){
			TemporarySynThread ts=new TemporarySynThread("http://www.xingbill.com/synTradeInfo",trade.getMerchantOrderNo(), backSussess, backMessage);
			ts.start();
			}
			if("3918".equals((trade.getOrderNo()).substring(0,4))||"4110".equals((trade.getOrderNo()).substring(0,4))){
				TemporarySynThread ts=new TemporarySynThread("http://www.win4mall.com/OrderAutomatic",trade.getMerchantOrderNo(),backSussess, backMessage);
				ts.start();
			}
			if("4160".equals((trade.getOrderNo()).substring(0,4))||"4161".equals((trade.getOrderNo()).substring(0,4))){
				TemporarySynThread ts=new TemporarySynThread("http://www.ipasspay.biz/index.php/Thirdpay/Sfepay/notifyUrl",trade.getMerchantOrderNo(), backSussess, trade.getRemark());
				ts.start();
			}
			if("4165".equals((trade.getOrderNo()).substring(0,4))){
				TemporarySynThread ts=new TemporarySynThread("http://www.youkutuan.com/do.php?act=charge_okok",trade.getMerchantOrderNo(),backSussess,backMessage);
				ts.start();
			}
			if("4169".equals((trade.getOrderNo()).substring(0,4))){
				TemporarySynThread ts=new TemporarySynThread("http://www.jjqsc.com/PayResult.php",trade.getMerchantOrderNo(),backSussess, backMessage);
				ts.start();
			}
			if("4066".equals((trade.getOrderNo()).substring(0,4))||"4216".equals((trade.getOrderNo()).substring(0,4))){
				if("maxmaillots.org".equals(trade.getTradeUrl())){
					TemporarySynThread ts=new TemporarySynThread("http://www.maxmaillots.org/payment_online_feback.php",trade.getMerchantOrderNo(),backSussess, backMessage);
					ts.start();
				}else{
					TemporarySynThread ts=new TemporarySynThread("http://"+trade.getTradeUrl()+"/payment_online_feback.php",trade.getMerchantOrderNo(),backSussess, backMessage);
					ts.start();
				}
			}
		}else{
			logger.info("更新vip交易失败："+trade.getOrderNo());
			tem.setRemark(msg.getRemark());
			tem.setStatus("2");
			commonService.update(tem);
		}
		}
	
		}else if("IP".equals(channelName)){
			logger.info("进入IPasspay通道");
			IPassPayMessage msg=new IPassPayMessage();
			IPassPayUtil yu=new IPassPayUtil();

			String channelId="";
			if("4".equals(cardNo.substring(0, 1))){
				//msg.setNewcardtype("4");
				channelId="302";
			}
			if("5".equals(cardNo.substring(0, 1))){
				//msg.setNewcardtype("5");
				channelId="303";
			}
			InternationalTerminalManager tim=(InternationalTerminalManager) commonService.uniqueResult("from InternationalTerminalManager where id='"+channelId+"'");
			
			List<InternationalMerchantChannels> imc=this.commonService.list("from InternationalMerchantChannels mc where mc.merchantId='"+trade.getMerchantId()+"' and mc.channelId='"+tim.getChannelId()+"'");
			if(imc!=null&&imc.size()>0){
				InternationalMerchantChannels channel=imc.get(0);
				logger.info("*********更新通道使用费*************");
				trade.setChannelFee(channel.getChannelFee());
			}			
			
			msg.setMid(tim.getMerchantNo());
			msg.setOid(trade.getOrderNo());
			//msg.setMid("20331");
			//msg.setOid("1234567891011");
			msg.setSite_id("371");
			Double amountAndFee=trade.getRmbAmount();
			if(trade.getChannelFee()!=null){
				amountAndFee=amountAndFee*(trade.getChannelFee()+1.0);
				amountAndFee = (double) (Math.round((double) amountAndFee * 100) / 100.00);
			}
			msg.setOrder_amount(amountAndFee+ "");
			
			msg.setOrder_currency("CNY");
			//'mid','site_id','order_id','order_amount','order_currency','api_key'.
			String sign=msg.getMid().trim()+msg.getSite_id().trim()+msg.getOid().trim()+msg.getOrder_amount().trim()+msg.getOrder_currency().trim()+tim.getHashcode().trim();
			String strDes = getSha256(sign); 
			msg.setHash_info(strDes);
			
			msg.setCard_no(cardNo);
			msg.setCard_ex_month(tem.getExpirationMonth());
			msg.setCard_ex_year(tem.getExpirationYear());
			msg.setCard_cvv(tem.getCvv2());
			
			msg.setBill_email(ic.getEmail());
			msg.setBill_phone(ic.getShippingPhone());
			msg.setBill_country(ic.getCountry());
			
			if(ic.getShippingState()!=null){
				 msg.setBillingstate(ic.getShippingState().trim());//美国加拿大州县转换
			 }else{
				 msg.setBillingstate("state");
			 }
			
			//msg.setBillingstate(state);
			msg.setBill_city(ic.getShippingCity());
			msg.setBill_street(ic.getShippingAddress());
			msg.setBill_zip(ic.getShippingZip());
			msg.setBill_firstname(ic.getShippingFullName());
			msg.setBill_lastname(ic.getLastName());
			msg.setSyn_url("www.sfepay.com");
			msg.setAsyn_url("https://www.sfepay.com/IPassPay");
			msg.setSource_ip(ic.getIp().split(",")[0]);
			msg.setSource_url(trade.getTradeUrl());//商户网站
			msg.setGateway_version("1.0");
			UUID uuid2 = UUID.randomUUID();
			msg.setUuid(uuid2.toString());
		
			yu.get(msg);

			logger.info("vip交易结果："+msg.getOrder_status());
			String backSussess="";
			String backMessage="";
			if(msg.getOrder_status().equals("2")){
				trade.setTradeState("1"
						+ trade.getTradeState().substring(1,
								trade.getTradeState().length()));
//				trade.setTradeState(StatusUtil.updateStatus(trade.getTradeState(), 15, "3"));
//				trade.setRemark("Payment Declined!" + tm.getErrorCode());
				trade.setVIPDisposePorson("System");
				trade.setVIPDisposeDate(new Date());
				trade.setVIPBatchNo(msg.getPid());
				trade.setRemark("Payment Success!");
				orderList=orderList+trade.getOrderNo()+"（Payment Success!）、";
				backSussess="1";
				backMessage=msg.getBilling_desc();
				if(!"1001".equals((trade.getOrderNo()).substring(0,4))&&!"4136".equals((trade.getOrderNo()).substring(0,4))&&!"3918".equals((trade.getOrderNo()).substring(0,4))&&!"4110".equals((trade.getOrderNo()).substring(0,4))&&!"4212".equals((trade.getOrderNo()).substring(0,4))){
					logger.info("*******发送成功邮件："+trade.getOrderNo());
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
			}else if(msg.getOrder_status().equals("1")||msg.getOrder_status().equals("3")){
				trade.setTradeState("2"
						+ trade.getTradeState().substring(1,
								trade.getTradeState().length()));
				backSussess="6";			
			}else{
				orderList=orderList+trade.getOrderNo()+"("+msg.getInfo()+")、";
//				trade.setTradeState("0"
//						+ trade.getTradeState().substring(1,
//								trade.getTradeState().length()));
				if(!"sfe01".equals(msg.getOrder_status())){
					trade.setRemark("1093high risk!" +msg.getInfo());
				}
				backSussess="0";
				backMessage=trade.getRemark();
			}
	if(StringUtils.isNotBlank(msg.getOrder_status())&&!"sfe01".equals(msg.getOrder_status())){
		List<InternationalMerchantChannels> merChannel=this.commonService.list("from InternationalMerchantChannels mc where mc.merchantId='"+trade.getMerchantId()+"' and mc.channelId='"+tim.getChannelId()+"'");
		if(merChannel!=null&&merChannel.size()>0){
			InternationalMerchantChannels mc=merChannel.get(0);
			logger.info("*********更新通道*************");
			trade.setTradeChannel(Long.valueOf(mc.getId()));
		}
		commonService.update(trade);
		if("1".equals(backSussess)){
			commonService.delete(tem);
			if("1001".equals((trade.getOrderNo()).substring(0,4))||"4136".equals((trade.getOrderNo()).substring(0,4))){
				TemporarySynThread ts=new TemporarySynThread("http://www.xingbill.com/synTradeInfo",trade.getMerchantOrderNo(), backSussess, backMessage);
				ts.start();
			}
			if("3918".equals((trade.getOrderNo()).substring(0,4))||"4110".equals((trade.getOrderNo()).substring(0,4))){
				TemporarySynThread ts=new TemporarySynThread("http://www.win4mall.com/OrderAutomatic",trade.getMerchantOrderNo(),backSussess, backMessage);
				ts.start();
			}
			if("4160".equals((trade.getOrderNo()).substring(0,4))||"4161".equals((trade.getOrderNo()).substring(0,4))){
				TemporarySynThread ts=new TemporarySynThread("http://www.ipasspay.biz/index.php/Thirdpay/Sfepay/notifyUrl",trade.getMerchantOrderNo(), backSussess, trade.getRemark());
				ts.start();
			}
			if("4165".equals((trade.getOrderNo()).substring(0,4))){
				TemporarySynThread ts=new TemporarySynThread("http://www.youkutuan.com/do.php?act=charge_okok",trade.getMerchantOrderNo(),backSussess,backMessage);
				ts.start();
			}
			if("4169".equals((trade.getOrderNo()).substring(0,4))){
				TemporarySynThread ts=new TemporarySynThread("http://www.jjqsc.com/PayResult.php",trade.getMerchantOrderNo(),backSussess, backMessage);
				ts.start();
			}
			if("4066".equals((trade.getOrderNo()).substring(0,4))||"4216".equals((trade.getOrderNo()).substring(0,4))){
				if("maxmaillots.org".equals(trade.getTradeUrl())){
					TemporarySynThread ts=new TemporarySynThread("http://www.maxmaillots.org/payment_online_feback.php",trade.getMerchantOrderNo(),backSussess, backMessage);
					ts.start();
				}else{
					TemporarySynThread ts=new TemporarySynThread("http://"+trade.getTradeUrl()+"/payment_online_feback.php",trade.getMerchantOrderNo(),backSussess, backMessage);
					ts.start();
				}
			}
		}else if("6".equals(backSussess)){
			commonService.delete(tem);
		}else{
			logger.info("更新vip交易失败："+trade.getOrderNo());
			tem.setRemark(msg.getInfo());
			tem.setStatus("2");
			commonService.update(tem);
		}
		}
		}else if("QP".equals(channelName)){
			logger.info("进入QuanQiuPay通道");
			QuanQiuPayMessage msg=new QuanQiuPayMessage();
			QuanQiuPayUtil yu=new QuanQiuPayUtil();
			
			String channelId="";
			if("4".equals(cardNo.substring(0, 1))){			
				channelId="309";
			}
			if("5".equals(cardNo.substring(0, 1))){				
				channelId="310";
			}

			InternationalTerminalManager tim=(InternationalTerminalManager) commonService.uniqueResult("from InternationalTerminalManager where id='"+channelId+"'");
			List<InternationalMerchantChannels> imc=this.commonService.list("from InternationalMerchantChannels mc where mc.merchantId='"+trade.getMerchantId()+"' and mc.channelId='"+tim.getChannelId()+"'");
			if(imc!=null&&imc.size()>0){
				InternationalMerchantChannels channel=imc.get(0);
				logger.info("*********更新通道使用费*************");
				trade.setChannelFee(channel.getChannelFee());
			}
			msg.setMode("Api");
			//msg.setVersion("20180208");
			msg.setApplicationId(tim.getMerchantNo());
			msg.setOrderId(trade.getOrderNo());
			msg.setSource(trade.getTradeUrl());
			msg.setEmail(ic.getEmail());
			msg.setIPAddress(ic.getIp().split(",")[0]);
			msg.setCurrency("CNY");
			Double amountAndFee=trade.getRmbAmount();
			if(trade.getChannelFee()!=null){
				amountAndFee=amountAndFee*(trade.getChannelFee()+1.0);
				amountAndFee = (double) (Math.round((double) amountAndFee * 100) / 100.00);
			}
			msg.setAmount(amountAndFee+ "");			
			MD5 md5=new MD5();
			String md5Hash = msg.getApplicationId() + msg.getOrderId() + msg.getEmail() + msg.getCurrency() + msg.getAmount() + tim.getHashcode();
			msg.setSignature(md5.getMD5ofStr(md5Hash));
			
			msg.setProductSku1("ProductSku1");
			//msg.setProductName1(ic.getProductInfo());	
			
			String pro=ic.getProductInfo();
			if(pro.indexOf("#")>=0||pro.indexOf("&")>=0){
				pro = pro.replace("#", " ");
				pro = pro.replace("&", " ");
			}
			msg.setProductName1(pro);
			
			msg.setProductPrice1(amountAndFee+ "");
			msg.setProductQuantity1("1");
			msg.setShippingFirstName(ic.getShippingFullName());
			msg.setShippingLastName(ic.getLastName());
			
			if(ic.getShippingCountry().length()>=3){
				msg.setShippingCountry(ic.getShippingCountry().substring(3, 5));
			}else{
				msg.setShippingCountry(ic.getShippingCountry());
			}
			
			//msg.setShippingCountry(ic.getCountry());
			
			if(ic.getShippingState()!=null){
				 msg.setShippingState(ic.getShippingState().trim());//美国加拿大州县转换
			 }else{
				 msg.setShippingState("state");
			 }
	
			msg.setShippingCity(ic.getShippingCity());
			msg.setShippingAddress1(ic.getShippingAddress());
			msg.setShippingZipcode(ic.getShippingZip());
			msg.setShippingTelephone(ic.getShippingPhone());
			
			msg.setBillingFirstName(ic.getFirstName());
			msg.setBillingLastName(ic.getLastName());
			
			if(ic.getCountry().length()>=3){
				msg.setBillingCountry(ic.getCountry().substring(3, 5).trim());
			}else{
				msg.setBillingCountry(ic.getCountry());
			}
			//msg.setBillingCountry(ic.getCountry());
			

			if(ic.getState()!=null){
				 msg.setBillingState(ic.getState());//美国加拿大州县转换
			 }else{
				 msg.setBillingState("state");
			 }
			//msg.setBillingState(state);//要改
			msg.setBillingCity(ic.getShippingCity());
			msg.setBillingAddress1(ic.getShippingAddress());
			msg.setBillingZipcode(ic.getShippingZip());
			msg.setBillingTelephone(ic.getShippingPhone());

			//都要改
			
			msg.setCreditCardName(ic.getFirstName()+ic.getLastName());
			msg.setCreditCardNumber(cardNo);
			msg.setCreditCardExpire("20"+tem.getExpirationYear()+tem.getExpirationMonth());
			msg.setCreditCardCsc2(tem.getCvv2());
			yu.get(msg);

			logger.info("vip交易结果："+msg.getStatus());
			String backSussess="";
			String backMessage="";
			if(msg.getStatus().equals("Success")){
				trade.setTradeState("1"
						+ trade.getTradeState().substring(1,
								trade.getTradeState().length()));
//				trade.setTradeState(StatusUtil.updateStatus(trade.getTradeState(), 15, "3"));
//				trade.setRemark("Payment Declined!" + tm.getErrorCode());
				trade.setVIPDisposePorson("System");
				trade.setVIPDisposeDate(new Date());
				trade.setVIPAuthorizationNo(msg.getTransactionId());
				trade.setRemark("Payment Success!");
				orderList=orderList+trade.getOrderNo()+"（Payment Success!）、";
				backSussess="1";
				backMessage=tim.getBillingAddress();
				if(!"1001".equals((trade.getOrderNo()).substring(0,4))&&!"4136".equals((trade.getOrderNo()).substring(0,4))&&!"3918".equals((trade.getOrderNo()).substring(0,4))&&!"4110".equals((trade.getOrderNo()).substring(0,4))&&!"4212".equals((trade.getOrderNo()).substring(0,4))){
					logger.info("*******发送成功邮件："+trade.getOrderNo());
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
			}else if(msg.getStatus().equals("Pending")||msg.getStatus().equals("Processing")){
				trade.setTradeState("2"
						+ trade.getTradeState().substring(1,
								trade.getTradeState().length()));
				if(StringUtils.isNotBlank(msg.getTransactionId())){
					trade.setVIPAuthorizationNo(msg.getTransactionId());
				}
				backSussess="6";				
			}else{
				orderList=orderList+trade.getOrderNo()+"("+msg.getReason()+")、";
//				trade.setTradeState("0"
//						+ trade.getTradeState().substring(1,
//								trade.getTradeState().length()));
				if(!"sfe01".equals(msg.getStatus())){
					trade.setRemark("1093high risk!" +msg.getReason());
				}
				backSussess="0";
				backMessage=trade.getRemark();
			}
	if(StringUtils.isNotBlank(msg.getStatus())&&!"sfe01".equals(msg.getStatus())){
		List<InternationalMerchantChannels> merChannel=this.commonService.list("from InternationalMerchantChannels mc where mc.merchantId='"+trade.getMerchantId()+"' and mc.channelId='"+tim.getChannelId()+"'");
		if(merChannel!=null&&merChannel.size()>0){
			InternationalMerchantChannels mc=merChannel.get(0);
			logger.info("*********更新通道*************");
			trade.setTradeChannel(Long.valueOf(mc.getId()));
		}
		commonService.update(trade);
		if("1".equals(backSussess)){
			commonService.delete(tem);
			if("1001".equals((trade.getOrderNo()).substring(0,4))||"4136".equals((trade.getOrderNo()).substring(0,4))){
			TemporarySynThread ts=new TemporarySynThread("http://www.xingbill.com/synTradeInfo",trade.getMerchantOrderNo(), backSussess, backMessage);
			ts.start();
			}
			if("3918".equals((trade.getOrderNo()).substring(0,4))||"4110".equals((trade.getOrderNo()).substring(0,4))){
				TemporarySynThread ts=new TemporarySynThread("http://www.win4mall.com/OrderAutomatic",trade.getMerchantOrderNo(),backSussess, backMessage);
				ts.start();
			}
			if("4160".equals((trade.getOrderNo()).substring(0,4))||"4161".equals((trade.getOrderNo()).substring(0,4))){
				TemporarySynThread ts=new TemporarySynThread("http://www.ipasspay.biz/index.php/Thirdpay/Sfepay/notifyUrl",trade.getMerchantOrderNo(), backSussess, trade.getRemark());
				ts.start();
			}
			if("4165".equals((trade.getOrderNo()).substring(0,4))){
				TemporarySynThread ts=new TemporarySynThread("http://www.youkutuan.com/do.php?act=charge_okok",trade.getMerchantOrderNo(),backSussess,backMessage);
				ts.start();
			}
			if("4169".equals((trade.getOrderNo()).substring(0,4))){
				TemporarySynThread ts=new TemporarySynThread("http://www.jjqsc.com/PayResult.php",trade.getMerchantOrderNo(),backSussess, backMessage);
				ts.start();
			}
			if("4066".equals((trade.getOrderNo()).substring(0,4))||"4216".equals((trade.getOrderNo()).substring(0,4))){
				if("maxmaillots.org".equals(trade.getTradeUrl())){
					TemporarySynThread ts=new TemporarySynThread("http://www.maxmaillots.org/payment_online_feback.php",trade.getMerchantOrderNo(),backSussess, backMessage);
					ts.start();
				}else{
					TemporarySynThread ts=new TemporarySynThread("http://"+trade.getTradeUrl()+"/payment_online_feback.php",trade.getMerchantOrderNo(),backSussess, backMessage);
					ts.start();
				}
			}
		}else if("6".equals(backSussess)){
			commonService.delete(tem);
		}else{
			logger.info("更新vip交易失败："+trade.getOrderNo());
			tem.setRemark(msg.getReason());
			tem.setStatus("2");
			commonService.update(tem);
		}
		}
		}else if("GQ".equals(channelName)){
			logger.info("进入GofPay通道");
			GQPayMessage msg=new GQPayMessage();
			GQPayUtil yu=new GQPayUtil();
			msg.setMode("Api");
			msg.setVersion("20180208");
			msg.setAppId("70227403");
			msg.setOrderId(trade.getOrderNo());
			msg.setSource(trade.getTradeUrl());
			msg.setEmail(ic.getEmail());
			msg.setIPAddress(ic.getIp().split(",")[0]);
			msg.setCurrency("CNY");
			String channelId="";
			if("4".equals(cardNo.substring(0, 1))){			
				channelId="298";
			}
			if("5".equals(cardNo.substring(0, 1))){				
				channelId="297";
			}
			if("3".equals(cardNo.substring(0, 1))){				
				channelId="299";
			}
			InternationalTerminalManager tim=(InternationalTerminalManager) commonService.uniqueResult("from InternationalTerminalManager where id='"+channelId+"'");
			
			List<InternationalMerchantChannels> imc=this.commonService.list("from InternationalMerchantChannels mc where mc.merchantId='"+trade.getMerchantId()+"' and mc.channelId='"+tim.getChannelId()+"'");
			if(imc!=null&&imc.size()>0){
				InternationalMerchantChannels channel=imc.get(0);
				logger.info("*********更新通道使用费*************");
				trade.setChannelFee(channel.getChannelFee());
			}
			
			Double amountAndFee=trade.getRmbAmount();
			if(trade.getChannelFee()!=null){
				amountAndFee=amountAndFee*(trade.getChannelFee()+1.0);
				amountAndFee = (double) (Math.round((double) amountAndFee * 100) / 100.00);
			}
			msg.setAmount(amountAndFee+ "");			
			MD5 md5=new MD5();
			String md5Hash = msg.getAppId() + msg.getOrderId() + msg.getEmail() + msg.getCurrency() + msg.getAmount() + "VKf0MK02O8iYewkb";
			msg.setSignature(md5.getMD5ofStr(md5Hash));
			
			msg.setProductSku1("ProductSku1");
			msg.setProductName1(ic.getProductInfo());	
			msg.setProductPrice1(amountAndFee+ "");
			msg.setProductQuantity1("1");
			msg.setShippingFirstName(ic.getShippingFullName());
			msg.setShippingLastName(ic.getLastName());
			msg.setShippingCountry(ic.getCountry());
			
			String shippingstate=ic.getShippingState();
			if(StringUtils.isBlank(ic.getShippingState())){
				shippingstate="state";
			}
			
			msg.setShippingState(shippingstate);//要改
			msg.setShippingCity(ic.getShippingCity());
			msg.setShippingAddress1(ic.getShippingAddress());
			msg.setShippingZipcode(ic.getShippingZip());
			msg.setShippingTelephone(ic.getShippingPhone());
			
			msg.setBillingFirstName(ic.getShippingFullName());
			msg.setBillingLastName(ic.getLastName());
			msg.setBillingCountry(ic.getCountry());
			
			String state=ic.getState();
			if(StringUtils.isBlank(ic.getState())){
				state="state";
			}			
			msg.setBillingState(state);//要改
			msg.setBillingCity(ic.getShippingCity());
			msg.setBillingAddress1(ic.getShippingAddress());
			msg.setBillingZipcode(ic.getShippingZip());
			msg.setBillingTelephone(ic.getShippingPhone());

			//都要改
			
			msg.setCreditCardName(ic.getShippingFullName()+ic.getLastName());
			msg.setCreditCardNumber(cardNo);
			msg.setCreditCardExpire("20"+tem.getExpirationYear()+tem.getExpirationMonth());
			msg.setCreditCardCsc2(tem.getCvv2());
			yu.get(msg);

			logger.info("vip交易结果："+msg.getStatus());
			String backSussess="";
			String backMessage="";
			if(msg.getStatus().equals("Success")){
				trade.setTradeState("1"
						+ trade.getTradeState().substring(1,
								trade.getTradeState().length()));
//				trade.setTradeState(StatusUtil.updateStatus(trade.getTradeState(), 15, "3"));
//				trade.setRemark("Payment Declined!" + tm.getErrorCode());
				trade.setVIPDisposePorson("System");
				trade.setVIPDisposeDate(new Date());
				//trade.setVIPBatchNo(msg.getGrn());
				trade.setRemark("Payment Success!");
				orderList=orderList+trade.getOrderNo()+"（Payment Success!）、";
				backSussess="1";
				backMessage="Payment Success!";
				if(!"1001".equals((trade.getOrderNo()).substring(0,4))&&!"4136".equals((trade.getOrderNo()).substring(0,4))&&!"3918".equals((trade.getOrderNo()).substring(0,4))&&!"4110".equals((trade.getOrderNo()).substring(0,4))&&!"4212".equals((trade.getOrderNo()).substring(0,4))){
					logger.info("*******发送成功邮件："+trade.getOrderNo());
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
			}else if(msg.getStatus().equals("Processing")||msg.getStatus().equals("Pending")){
				trade.setTradeState("2"
						+ trade.getTradeState().substring(1,
								trade.getTradeState().length()));
				backSussess="6";				
			}else{
				orderList=orderList+trade.getOrderNo()+"("+msg.getReason()+")、";
//				trade.setTradeState("0"
//						+ trade.getTradeState().substring(1,
//								trade.getTradeState().length()));
				if(!"sfe01".equals(msg.getStatus())){
					trade.setRemark("1093high risk!" +msg.getReason());
				}
				backSussess="0";
				backMessage=trade.getRemark();
			}
	if(StringUtils.isNotBlank(msg.getStatus())&&!"sfe01".equals(msg.getStatus())){
		List<InternationalMerchantChannels> merChannel=this.commonService.list("from InternationalMerchantChannels mc where mc.merchantId='"+trade.getMerchantId()+"' and mc.channelId='"+tim.getChannelId()+"'");
		if(merChannel!=null&&merChannel.size()>0){
			InternationalMerchantChannels mc=merChannel.get(0);
			logger.info("*********更新通道*************");
			trade.setTradeChannel(Long.valueOf(mc.getId()));
		}
		commonService.update(trade);
		if("1".equals(backSussess)){
			commonService.delete(tem);
			if("1001".equals((trade.getOrderNo()).substring(0,4))||"4136".equals((trade.getOrderNo()).substring(0,4))){
			TemporarySynThread ts=new TemporarySynThread("http://www.xingbill.com/synTradeInfo",trade.getMerchantOrderNo(), backSussess, backMessage);
			ts.start();
			}
			if("3918".equals((trade.getOrderNo()).substring(0,4))||"4110".equals((trade.getOrderNo()).substring(0,4))){
				TemporarySynThread ts=new TemporarySynThread("http://www.win4mall.com/OrderAutomatic",trade.getMerchantOrderNo(),backSussess, backMessage);
				ts.start();
			}
			if("4160".equals((trade.getOrderNo()).substring(0,4))||"4161".equals((trade.getOrderNo()).substring(0,4))){
				TemporarySynThread ts=new TemporarySynThread("http://www.ipasspay.biz/index.php/Thirdpay/Sfepay/notifyUrl",trade.getMerchantOrderNo(), backSussess, trade.getRemark());
				ts.start();
			}
			if("4165".equals((trade.getOrderNo()).substring(0,4))){
				TemporarySynThread ts=new TemporarySynThread("http://www.youkutuan.com/do.php?act=charge_okok",trade.getMerchantOrderNo(),backSussess,backMessage);
				ts.start();
			}
			if("4169".equals((trade.getOrderNo()).substring(0,4))){
				TemporarySynThread ts=new TemporarySynThread("http://www.jjqsc.com/PayResult.php",trade.getMerchantOrderNo(),backSussess, backMessage);
				ts.start();
			}
			if("4066".equals((trade.getOrderNo()).substring(0,4))||"4216".equals((trade.getOrderNo()).substring(0,4))){
				if("maxmaillots.org".equals(trade.getTradeUrl())){
					TemporarySynThread ts=new TemporarySynThread("http://www.maxmaillots.org/payment_online_feback.php",trade.getMerchantOrderNo(),backSussess, backMessage);
					ts.start();
				}else{
					TemporarySynThread ts=new TemporarySynThread("http://"+trade.getTradeUrl()+"/payment_online_feback.php",trade.getMerchantOrderNo(),backSussess, backMessage);
					ts.start();
				}
			}
		}else if("6".equals(backSussess)){
			commonService.delete(tem);
		}else{
			logger.info("更新vip交易失败："+trade.getOrderNo());
			tem.setRemark(msg.getReason());
			tem.setStatus("2");
			commonService.update(tem);
		}
		}
		}else if("WP".equals(channelName)){
			logger.info("进入WintoPay通道");
			WPPayMessage msg=new WPPayMessage();
			WPPayUtil yu=new WPPayUtil();
			msg.setMerchantMID("4521");
			//只有visa通道
			String channelId="";
			if("4".equals(cardNo.substring(0, 1))){
				msg.setNewcardtype("4");
				channelId="296";
			}
			if("5".equals(cardNo.substring(0, 1))){
				msg.setNewcardtype("5");
				channelId="295";
			}
			InternationalTerminalManager tim=(InternationalTerminalManager) commonService.uniqueResult("from InternationalTerminalManager where id='"+channelId+"'");
			
			List<InternationalMerchantChannels> imc=this.commonService.list("from InternationalMerchantChannels mc where mc.merchantId='"+trade.getMerchantId()+"' and mc.channelId='"+tim.getChannelId()+"'");
			if(imc!=null&&imc.size()>0){
				InternationalMerchantChannels channel=imc.get(0);
				logger.info("*********更新通道使用费*************");
				trade.setChannelFee(channel.getChannelFee());
			}
			
			BASE64Encoder baseE=new BASE64Encoder(); 
			msg.setCardnum(baseE.encode(cardNo.getBytes()));
			msg.setCvv2(baseE.encode(tem.getCvv2().getBytes()));
			msg.setYear(baseE.encode(("20"+tem.getExpirationYear()).getBytes()));
			msg.setMonth(baseE.encode(tem.getExpirationMonth().getBytes()));
			msg.setCardbank(ic.getUserBank());
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
			MD5 md5=new MD5();
			String md5Hash = msg.getMerchantMID() + msg.getBillNo() + msg.getCurrency() + msg.getAmount() + msg.getLanguage() + msg.getReturnURL() + "u~h_rFJi";
			//String md5Hash=msg.getMerchantMID()+msg.getBillNo() +msg.getCurrency() +msg.getAmount() +msg.getLanguage()+msg.getReturnURL()+"Yu^HJXBd";
			msg.setHASH(md5.getMD5ofStr(md5Hash));
			msg.setShippingFirstName(ic.getFirstName());
			msg.setShippingLastName(ic.getLastName());
			msg.setShippingEmail(ic.getShippingEmail());
			msg.setShippingPhone(ic.getShippingPhone());
			msg.setShippingZipcode(ic.getShippingZip());
			msg.setShippingAddress(ic.getAddress());
			msg.setShippingCity(ic.getShippingCity());
			msg.setShippingSstate(ic.getShippingState());
			msg.setShippingCountry(ic.getShippingCountry());
			msg.setProducts(ic.getProductInfo());
			msg.setFirstname(ic.getFirstName());
			msg.setLastname(ic.getLastName());
			msg.setEmail(ic.getEmail());
			msg.setPhone(ic.getPhone());
			msg.setZipcode(ic.getZipcode());
			msg.setAddress(ic.getAddress());
			msg.setCity(ic.getCity());
			msg.setState(ic.getState());
			msg.setCountry(ic.getCountry());
			msg.setIpAddr(ic.getIp().split(",")[0]);
			yu.get(msg);

			logger.info("vip交易结果："+msg.getSucceed());
			String backSussess="";
			String backMessage="";
			if(msg.getSucceed().equals("88")){
				trade.setTradeState("1"
						+ trade.getTradeState().substring(1,
								trade.getTradeState().length()));
//				trade.setTradeState(StatusUtil.updateStatus(trade.getTradeState(), 15, "3"));
//				trade.setRemark("Payment Declined!" + tm.getErrorCode());
				trade.setVIPDisposePorson("System");
				trade.setVIPDisposeDate(new Date());
				//trade.setVIPBatchNo(msg.getGrn());
				trade.setRemark("Payment Success!");
				orderList=orderList+trade.getOrderNo()+"（Payment Success!）、";
				backSussess="1";
				backMessage="FPT*clothinglive";
				if(!"1001".equals((trade.getOrderNo()).substring(0,4))&&!"4136".equals((trade.getOrderNo()).substring(0,4))&&!"3918".equals((trade.getOrderNo()).substring(0,4))&&!"4110".equals((trade.getOrderNo()).substring(0,4))&&!"4212".equals((trade.getOrderNo()).substring(0,4))){
					logger.info("*******发送成功邮件："+trade.getOrderNo());
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
			}else if(msg.getSucceed().equals("19")){
				orderList=orderList+trade.getOrderNo()+"（Payment Pending!）、";
				trade.setTradeState("2"
						+ trade.getTradeState().substring(1,
								trade.getTradeState().length()));
				trade.setRemark("Payment Pending!");
				backSussess="6";
			}else{
				orderList=orderList+trade.getOrderNo()+"("+msg.getResult()+")、";
//				trade.setTradeState("0"
//						+ trade.getTradeState().substring(1,
//								trade.getTradeState().length()));
				if(!"sfe01".equals(msg.getSucceed())){
					trade.setRemark("1093high risk!" +msg.getResult());
				}
				backSussess="0";
				backMessage=trade.getRemark();
			}
	if(StringUtils.isNotBlank(msg.getSucceed())&&!"sfe01".equals(msg.getSucceed())){
		List<InternationalMerchantChannels> merChannel=this.commonService.list("from InternationalMerchantChannels mc where mc.merchantId='"+trade.getMerchantId()+"' and mc.channelId='"+tim.getChannelId()+"'");
		//List<InternationalMerchantChannels> merChannel=this.commonService.list("from InternationalMerchantChannels mc where mc.merchantId='"+trade.getMerchantId()+"' and mc.channelId='"+tim.getChannelId()+"'");
		
		if(merChannel!=null&&merChannel.size()>0){
			InternationalMerchantChannels mc=merChannel.get(0);
			logger.info("*********更新通道*************");
			trade.setTradeChannel(Long.valueOf(mc.getId()));
		}
		commonService.update(trade);
		if("1".equals(backSussess)){
			commonService.delete(tem);
			if("1001".equals((trade.getOrderNo()).substring(0,4))||"4136".equals((trade.getOrderNo()).substring(0,4))){
			TemporarySynThread ts=new TemporarySynThread("http://www.xingbill.com/synTradeInfo",trade.getMerchantOrderNo(), backSussess, backMessage);
			ts.start();
			}
			if("3918".equals((trade.getOrderNo()).substring(0,4))||"4110".equals((trade.getOrderNo()).substring(0,4))){
				TemporarySynThread ts=new TemporarySynThread("http://www.win4mall.com/OrderAutomatic",trade.getMerchantOrderNo(),backSussess, backMessage);
				ts.start();
			}
			if("4160".equals((trade.getOrderNo()).substring(0,4))||"4161".equals((trade.getOrderNo()).substring(0,4))){
				TemporarySynThread ts=new TemporarySynThread("http://www.ipasspay.biz/index.php/Thirdpay/Sfepay/notifyUrl",trade.getMerchantOrderNo(), backSussess, trade.getRemark());
				ts.start();
			}
			if("4165".equals((trade.getOrderNo()).substring(0,4))){
				TemporarySynThread ts=new TemporarySynThread("http://www.youkutuan.com/do.php?act=charge_okok",trade.getMerchantOrderNo(),backSussess,backMessage);
				ts.start();
			}
			if("4169".equals((trade.getOrderNo()).substring(0,4))){
				TemporarySynThread ts=new TemporarySynThread("http://www.jjqsc.com/PayResult.php",trade.getMerchantOrderNo(),backSussess, backMessage);
				ts.start();
			}
			if("4066".equals((trade.getOrderNo()).substring(0,4))||"4216".equals((trade.getOrderNo()).substring(0,4))){
				if("maxmaillots.org".equals(trade.getTradeUrl())){
					TemporarySynThread ts=new TemporarySynThread("http://www.maxmaillots.org/payment_online_feback.php",trade.getMerchantOrderNo(),backSussess, backMessage);
					ts.start();
				}else{
					TemporarySynThread ts=new TemporarySynThread("http://"+trade.getTradeUrl()+"/payment_online_feback.php",trade.getMerchantOrderNo(),backSussess, backMessage);
					ts.start();
				}
			}
		}else if("6".equals(backSussess)){
			commonService.delete(tem);
		}else{
			logger.info("更新vip交易失败："+trade.getOrderNo());
			tem.setRemark(msg.getResult());
			tem.setStatus("2");
			commonService.update(tem);
		}
		}
		}else if("GR".equals(channelName)){
			logger.info("进入Grepay通道");
			 GrePayMessage masaM=new GrePayMessage();
			 GrePayUtil masaU=new GrePayUtil();
			 masaM.setVersion("1.6");
			 masaM.setMerchantId("801128573993003");
			 masaM.setCharset("utf-8");
			 masaM.setLanguage("en");
			 masaM.setSignType("SHA256");
			 masaM.setMerchantOrderNo(trade.getOrderNo());
			 masaM.setGoodsName(ic.getProductInfo());
			 masaM.setGoodsDesc(ic.getProductInfo());
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
			 String channelId="";
				if("4".equals(cardNo.substring(0, 1))){
					masaM.setOrgCode("VISA");
					channelId="304";
				}
				if("5".equals(cardNo.substring(0, 1))){
					masaM.setOrgCode("MASTER");
					channelId="305";
				}
			InternationalTerminalManager tim=(InternationalTerminalManager) commonService.uniqueResult("from InternationalTerminalManager where id='"+channelId+"'");
			BASE64Encoder baseE=new BASE64Encoder();
			 masaM.setCardNumber(cardNo);
			 masaM.setCardHolderLastName(ic.getLastName());
			 masaM.setCardHolderFirstName(ic.getFirstName());
			 masaM.setCardExpirationMonth(tem.getExpirationMonth());
			 masaM.setCardExpirationYear("20"+tem.getExpirationYear());
			 masaM.setSecurityCode(tem.getCvv2());			 
			 
			 masaM.setCardHolderEmail(ic.getEmail());
			 masaM.setCardHolderPhoneNumber(ic.getPhone());
			 masaM.setBillName(ic.getFirstName()+" "+ic.getLastName());
			 masaM.setBillAddress(ic.getAddress());
			 masaM.setBillPostalCode(ic.getZipcode());
			 masaM.setBillCountry(ic.getCountry());
			 masaM.setBillState(ic.getState());//美国加拿大州县转换
			 masaM.setBillCity(ic.getCity());
			 masaM.setBillEmail(ic.getEmail());
			 masaM.setBillPhoneNumber(ic.getPhone());
			 masaM.setShippingName(ic.getShippingFullName());
			 masaM.setShippingAddress(ic.getShippingAddress());
			 masaM.setShippingPostalCode(ic.getShippingZip());
			 masaM.setShippingCountry(ic.getShippingCountry());
			 masaM.setShippingState(ic.getShippingState());//美国加拿大州县转换
			 masaM.setShippingCity(ic.getShippingCity());
			 masaM.setShippingEmail(ic.getShippingEmail());
			 masaM.setShippingPhoneNumber(ic.getShippingPhone());
			 masaM.setDeviceFingerprintID("m"+masaM.getMerchantId()+"_"+UUID.randomUUID().toString());
			 masaM.setRegisterUserEmail(ic.getEmail());
			 masaM.setRegisterTime(sdf.format(new Date()));
			 masaM.setRegisterIp(ic.getIp().split(",")[0]);
			 masaM.setRegisterTerminal("00");
			 masaM.setOrderIp(ic.getIp().split(",")[0]);
			 masaM.setOrderTerminal("00");
			 masaM.setMd5key("gE{Lq_TD");
			 masaU.get(masaM);

			logger.info("vip交易结果："+masaM.getRes_resultCode());
			String backSussess="";
			String backMessage="";
			if(masaM.getRes_resultCode().equals("10")){
				trade.setTradeState("1"
						+ trade.getTradeState().substring(1,
								trade.getTradeState().length()));
//				trade.setTradeState(StatusUtil.updateStatus(trade.getTradeState(), 15, "3"));
//				trade.setRemark("Payment Declined!" + tm.getErrorCode());
				trade.setVIPDisposePorson("System");
				trade.setVIPDisposeDate(new Date());
				trade.setVIPBatchNo(masaM.getRes_masapayOrderNo());
				trade.setRemark("Payment Success!");
				orderList=orderList+trade.getOrderNo()+"（Payment Success!）、";
				backSussess="1";
				backMessage="dtces";
				if(!"1001".equals((trade.getOrderNo()).substring(0,4))&&!"4136".equals((trade.getOrderNo()).substring(0,4))&&!"3918".equals((trade.getOrderNo()).substring(0,4))&&!"4110".equals((trade.getOrderNo()).substring(0,4))&&!"4212".equals((trade.getOrderNo()).substring(0,4))){
					logger.info("*******发送成功邮件："+trade.getOrderNo());
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
			}else if(masaM.getRes_resultCode().equals("00")){
				orderList=orderList+trade.getOrderNo()+"（Payment Pending!）、";
				trade.setTradeState("2"
						+ trade.getTradeState().substring(1,
								trade.getTradeState().length()));
				trade.setRemark("Payment Pending!");
				backSussess="6";
			}else{
				orderList=orderList+trade.getOrderNo()+"("+masaM.getRes_errMsg()+")、";
//				trade.setTradeState("0"
//						+ trade.getTradeState().substring(1,
//								trade.getTradeState().length()));
				if(!"sfe01".equals(masaM.getRes_resultCode())){
					trade.setRemark("1093high risk!" +masaM.getRes_errMsg());
				}
				backSussess="0";
				backMessage=trade.getRemark();
			}
	if(StringUtils.isNotBlank(masaM.getRes_resultCode())&&!"sfe01".equals(masaM.getRes_resultCode())){
		List<InternationalMerchantChannels> merChannel=this.commonService.list("from InternationalMerchantChannels mc where mc.merchantId='"+trade.getMerchantId()+"' and mc.channelId='"+tim.getChannelId()+"'");
		//List<InternationalMerchantChannels> merChannel=this.commonService.list("from InternationalMerchantChannels mc where mc.merchantId='"+trade.getMerchantId()+"' and mc.channelId='"+tim.getChannelId()+"'");
		
		if(merChannel!=null&&merChannel.size()>0){
			InternationalMerchantChannels mc=merChannel.get(0);
			logger.info("*********更新通道*************");
			trade.setTradeChannel(Long.valueOf(mc.getId()));
		}
		commonService.update(trade);
		if("1".equals(backSussess)){
			commonService.delete(tem);
			if("1001".equals((trade.getOrderNo()).substring(0,4))||"4136".equals((trade.getOrderNo()).substring(0,4))){
			TemporarySynThread ts=new TemporarySynThread("http://www.xingbill.com/synTradeInfo",trade.getMerchantOrderNo(), backSussess, backMessage);
			ts.start();
			}
			if("3918".equals((trade.getOrderNo()).substring(0,4))||"4110".equals((trade.getOrderNo()).substring(0,4))){
				TemporarySynThread ts=new TemporarySynThread("http://www.win4mall.com/OrderAutomatic",trade.getMerchantOrderNo(),backSussess, backMessage);
				ts.start();
			}
			if("4160".equals((trade.getOrderNo()).substring(0,4))||"4161".equals((trade.getOrderNo()).substring(0,4))){
				TemporarySynThread ts=new TemporarySynThread("http://www.ipasspay.biz/index.php/Thirdpay/Sfepay/notifyUrl",trade.getMerchantOrderNo(), backSussess, trade.getRemark());
				ts.start();
			}
			if("4165".equals((trade.getOrderNo()).substring(0,4))){
				TemporarySynThread ts=new TemporarySynThread("http://www.youkutuan.com/do.php?act=charge_okok",trade.getMerchantOrderNo(),backSussess,backMessage);
				ts.start();
			}
			if("4169".equals((trade.getOrderNo()).substring(0,4))){
				TemporarySynThread ts=new TemporarySynThread("http://www.jjqsc.com/PayResult.php",trade.getMerchantOrderNo(),backSussess, backMessage);
				ts.start();
			}
			if("4066".equals((trade.getOrderNo()).substring(0,4))||"4216".equals((trade.getOrderNo()).substring(0,4))){
				if("maxmaillots.org".equals(trade.getTradeUrl())){
					TemporarySynThread ts=new TemporarySynThread("http://www.maxmaillots.org/payment_online_feback.php",trade.getMerchantOrderNo(),backSussess, backMessage);
					ts.start();
				}else{
					TemporarySynThread ts=new TemporarySynThread("http://"+trade.getTradeUrl()+"/payment_online_feback.php",trade.getMerchantOrderNo(),backSussess, backMessage);
					ts.start();
				}
			}
		}else if("6".equals(backSussess)){
			commonService.delete(tem);
		}else{
			logger.info("更新vip交易失败："+trade.getOrderNo());
			tem.setRemark("GR*"+masaM.getRes_errMsg());
			tem.setStatus("2");
			commonService.update(tem);
		}
		}
		}else if("PC".equals(channelName)){
			 logger.info("进入PayClub通道");
			 	PayClubMessage msg=new PayClubMessage();
				PayClubUtil yu=new PayClubUtil();
				String channelId="";				
				if("5".equals(cardNo.substring(0, 1))){
					channelId="306";
				}
				InternationalTerminalManager tim=(InternationalTerminalManager) commonService.uniqueResult("from InternationalTerminalManager where id='"+channelId+"'");
				
				List<InternationalMerchantChannels> imc=this.commonService.list("from InternationalMerchantChannels mc where mc.merchantId='"+trade.getMerchantId()+"' and mc.channelId='"+tim.getChannelId()+"'");
				if(imc!=null&&imc.size()>0){
					InternationalMerchantChannels channel=imc.get(0);
					logger.info("*********更新通道使用费*************");
					trade.setChannelFee(channel.getChannelFee());
				}
				
				msg.setP_mid(tim.getMerchantNo());
				msg.setP_account_num(tim.getTerminalNo());
				msg.setP_transaction_type("SALE");
				msg.setP_order_num(trade.getOrderNo());
				msg.setP_currency("CNY");
				Double amountAndFee=trade.getRmbAmount();
				 if(trade.getChannelFee()!=null){
					amountAndFee=amountAndFee*(trade.getChannelFee()+1.0);
					amountAndFee = (double) (Math.round((double) amountAndFee * 100) / 100.00);
				 }
				msg.setP_amount((int)(amountAndFee*100)+"");
				msg.setP_card_num(cardNo);
				msg.setP_card_expmonth(tem.getExpirationMonth());
				msg.setP_card_expyear("20"+tem.getExpirationYear());
				msg.setP_card_csc(tem.getCvv2());
				msg.setP_card_issuingbank("cardbank");
				msg.setP_firstname(ic.getFirstName());
				msg.setP_lastname(ic.getLastName());
				msg.setP_user_email(ic.getEmail());
				msg.setP_user_phone(ic.getPhone());
				msg.setP_user_ipaddress(ic.getIp());
				msg.setP_trans_url(trade.getTradeUrl());
				msg.setP_return_url("www.sfepay.com");
				msg.setP_bill_country(ic.getCountry());
				msg.setP_bill_state(ic.getState());
				msg.setP_bill_city(ic.getCity());
				msg.setP_bill_address(ic.getAddress());
				msg.setP_bill_zip(ic.getZipcode());
				msg.setP_ship_firstname(ic.getFirstName());
				msg.setP_ship_lastname(ic.getLastName());
				msg.setP_ship_country(ic.getShippingCountry());
				msg.setP_ship_state(ic.getShippingState().trim());
				msg.setP_ship_city(ic.getShippingCity());
				msg.setP_ship_address(ic.getShippingAddress());
				msg.setP_ship_zip(ic.getShippingZip());
				msg.setP_product_name(ic.getProductInfo());
				msg.setP_product_num("1");
				msg.setP_product_desc(ic.getProductInfo());

				String sign=msg.getP_mid().trim()+msg.getP_account_num().trim()+msg.getP_order_num().trim()+msg.getP_currency().trim()+msg.getP_amount().trim()+tim.getHashcode();
				String strDes = getSha256(sign); 
				msg.setP_signmsg(strDes.toUpperCase());
				
				yu.get(msg);
			
			logger.info("vip交易结果："+msg.getP_pay_result());
			String backSussess="";
			String backMessage="";
			if(msg.getP_pay_result().equals("1")){
				trade.setTradeState("1"
						+ trade.getTradeState().substring(1,
								trade.getTradeState().length()));
//				trade.setTradeState(StatusUtil.updateStatus(trade.getTradeState(), 15, "3"));
//				trade.setRemark("Payment Declined!" + tm.getErrorCode());
				trade.setVIPDisposePorson("System");
				trade.setVIPDisposeDate(new Date());
				trade.setVIPBatchNo(msg.getP_trans_num());
				trade.setRemark("Payment Success!");
				orderList=orderList+trade.getOrderNo()+"（Payment Success!）、";
				backSussess="1";
				backMessage=tim.getBillingAddress();
				if(!"1001".equals((trade.getOrderNo()).substring(0,4))&&!"4136".equals((trade.getOrderNo()).substring(0,4))&&!"3918".equals((trade.getOrderNo()).substring(0,4))&&!"4110".equals((trade.getOrderNo()).substring(0,4))&&!"4212".equals((trade.getOrderNo()).substring(0,4))){
					logger.info("*******发送成功邮件："+trade.getOrderNo());
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
			}else if(msg.getP_pay_result().equals("-1")){
				orderList=orderList+trade.getOrderNo()+"（Payment Pending!）、";
				trade.setTradeState("2"
						+ trade.getTradeState().substring(1,
								trade.getTradeState().length()));
				trade.setRemark("Payment Pending!");
				backSussess="6";
			}else{
				orderList=orderList+trade.getOrderNo()+"("+msg.getP_pay_info()+")、";
//				trade.setTradeState("0"
//						+ trade.getTradeState().substring(1,
//								trade.getTradeState().length()));
				if(!"sfe01".equals(msg.getP_pay_result())){
					trade.setRemark("1093high risk!" +msg.getP_pay_info());
				}
				backSussess="0";
				backMessage=trade.getRemark();
			}
	if(StringUtils.isNotBlank(msg.getP_pay_result())&&!"sfe01".equals(msg.getP_pay_result())){
		List<InternationalMerchantChannels> merChannel=this.commonService.list("from InternationalMerchantChannels mc where mc.merchantId='"+trade.getMerchantId()+"' and mc.channelId='"+tim.getChannelId()+"'");
		//List<InternationalMerchantChannels> merChannel=this.commonService.list("from InternationalMerchantChannels mc where mc.merchantId='"+trade.getMerchantId()+"' and mc.channelId='"+tim.getChannelId()+"'");
		
		if(merChannel!=null&&merChannel.size()>0){
			InternationalMerchantChannels mc=merChannel.get(0);
			logger.info("*********更新通道*************");
			trade.setTradeChannel(Long.valueOf(mc.getId()));
		}
		commonService.update(trade);
		if("1".equals(backSussess)){
			commonService.delete(tem);
			if("1001".equals((trade.getOrderNo()).substring(0,4))||"4136".equals((trade.getOrderNo()).substring(0,4))){
				TemporarySynThread ts=new TemporarySynThread("http://www.xingbill.com/synTradeInfo",trade.getMerchantOrderNo(), backSussess, backMessage);
				ts.start();
			}
			if("3918".equals((trade.getOrderNo()).substring(0,4))||"4110".equals((trade.getOrderNo()).substring(0,4))){
				TemporarySynThread ts=new TemporarySynThread("http://www.win4mall.com/OrderAutomatic",trade.getMerchantOrderNo(),backSussess, backMessage);
				ts.start();
			}
			if("4160".equals((trade.getOrderNo()).substring(0,4))||"4161".equals((trade.getOrderNo()).substring(0,4))){
				TemporarySynThread ts=new TemporarySynThread("http://www.ipasspay.biz/index.php/Thirdpay/Sfepay/notifyUrl",trade.getMerchantOrderNo(), backSussess, trade.getRemark());
				ts.start();
			}
			if("4165".equals((trade.getOrderNo()).substring(0,4))){
				TemporarySynThread ts=new TemporarySynThread("http://www.youkutuan.com/do.php?act=charge_okok",trade.getMerchantOrderNo(),backSussess,backMessage);
				ts.start();
			}
			if("4169".equals((trade.getOrderNo()).substring(0,4))){
				TemporarySynThread ts=new TemporarySynThread("http://www.jjqsc.com/PayResult.php",trade.getMerchantOrderNo(),backSussess, backMessage);
				ts.start();
			}
			if("4066".equals((trade.getOrderNo()).substring(0,4))||"4216".equals((trade.getOrderNo()).substring(0,4))){
				if("maxmaillots.org".equals(trade.getTradeUrl())){
					TemporarySynThread ts=new TemporarySynThread("http://www.maxmaillots.org/payment_online_feback.php",trade.getMerchantOrderNo(),backSussess, backMessage);
					ts.start();
				}else{
					TemporarySynThread ts=new TemporarySynThread("http://"+trade.getTradeUrl()+"/payment_online_feback.php",trade.getMerchantOrderNo(),backSussess, backMessage);
					ts.start();
				}
			}
		}else if("6".equals(backSussess)){
			commonService.delete(tem);
		}else{
			logger.info("更新vip交易失败："+trade.getOrderNo());
			tem.setRemark("PC*"+msg.getP_pay_info());
			tem.setStatus("2");
			commonService.update(tem);
		}
		}
		}else if("MS".equals(channelName)){
			logger.info("进入Masapay通道");
			 MasaPayMessage masaM=new MasaPayMessage();
			 MasaPayUtil masaU=new MasaPayUtil();
			 masaM.setVersion("1.6");
			 String channelId="";
				if("4".equals(cardNo.substring(0, 1))){
					masaM.setOrgCode("VISA");
					channelId="288";
				}
				if("5".equals(cardNo.substring(0, 1))){
					masaM.setOrgCode("MASTER");
					channelId="289";
				}
			InternationalTerminalManager tim=(InternationalTerminalManager) commonService.uniqueResult("from InternationalTerminalManager where id='"+channelId+"'");
			
			List<InternationalMerchantChannels> imc=this.commonService.list("from InternationalMerchantChannels mc where mc.merchantId='"+trade.getMerchantId()+"' and mc.channelId='"+tim.getChannelId()+"'");
			if(imc!=null&&imc.size()>0){
				InternationalMerchantChannels channel=imc.get(0);
				logger.info("*********更新通道使用费*************");
				trade.setChannelFee(channel.getChannelFee());
			}
			
			 masaM.setMerchantId(tim.getMerchantNo());
			 masaM.setCharset("utf-8");
			 masaM.setLanguage("en");
			 masaM.setSignType("SHA256");
			 masaM.setMerchantOrderNo(trade.getOrderNo());
			 masaM.setGoodsName(ic.getProductInfo());
			 masaM.setGoodsDesc(ic.getProductInfo());
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
			 
			BASE64Encoder baseE=new BASE64Encoder();
			 masaM.setCardNumber(cardNo);
			 masaM.setCardHolderLastName(ic.getLastName());
			 masaM.setCardHolderFirstName(ic.getFirstName());
			 masaM.setCardExpirationMonth(tem.getExpirationMonth());
			 masaM.setCardExpirationYear("20"+tem.getExpirationYear());
			 masaM.setSecurityCode(tem.getCvv2());			 
			 
			 masaM.setCardHolderEmail(ic.getEmail());
			 masaM.setCardHolderPhoneNumber(ic.getPhone());
			 masaM.setBillName(ic.getFirstName()+" "+ic.getLastName());
			 masaM.setBillAddress(ic.getAddress());
			 masaM.setBillPostalCode(ic.getZipcode());
			 masaM.setBillCountry(ic.getCountry());
			 
			 if(ic.getState()!=null){
				 masaM.setBillState(ic.getState().trim());//美国加拿大州县转换
			 }else{
				 masaM.setBillState("state");
			 }
			 
			 masaM.setBillCity(ic.getCity());
			 masaM.setBillEmail(ic.getEmail());
			 masaM.setBillPhoneNumber(ic.getPhone());
			 masaM.setShippingName(ic.getShippingFullName());
			 masaM.setShippingAddress(ic.getShippingAddress());
			 masaM.setShippingPostalCode(ic.getShippingZip());
			 masaM.setShippingCountry(ic.getShippingCountry());
			
			 if(ic.getShippingState()!=null){
				 masaM.setShippingState(ic.getShippingState().trim());//美国加拿大州县转换
			 }else{
				 masaM.setShippingState("state");
			 }
	 
			 masaM.setShippingCity(ic.getShippingCity());
			 masaM.setShippingEmail(ic.getShippingEmail());
			 masaM.setShippingPhoneNumber(ic.getShippingPhone());
			 masaM.setDeviceFingerprintID("m"+masaM.getMerchantId()+"_"+UUID.randomUUID().toString());
			 masaM.setRegisterUserEmail(ic.getEmail());
			 masaM.setRegisterTime(sdf.format(new Date()));
			 masaM.setRegisterIp(ic.getIp().split(",")[0]);
			 masaM.setRegisterTerminal("00");
			 masaM.setOrderIp(ic.getIp().split(",")[0]);
			 masaM.setOrderTerminal("00");
			 masaM.setMd5key(tim.getHashcode());
			 masaU.get(masaM);

			logger.info("vip交易结果："+masaM.getRes_resultCode());
			String backSussess="";
			String backMessage="";
			if(masaM.getRes_resultCode().equals("10")){
				trade.setTradeState("1"
						+ trade.getTradeState().substring(1,
								trade.getTradeState().length()));
//				trade.setTradeState(StatusUtil.updateStatus(trade.getTradeState(), 15, "3"));
//				trade.setRemark("Payment Declined!" + tm.getErrorCode());
				trade.setVIPDisposePorson("System");
				trade.setVIPDisposeDate(new Date());
				trade.setVIPBatchNo(masaM.getRes_masapayOrderNo());
				trade.setRemark("Payment Success!");
				orderList=orderList+trade.getOrderNo()+"（Payment Success!）、";
				backSussess="1";
				backMessage=tim.getBillingAddress();
				if(!"1001".equals((trade.getOrderNo()).substring(0,4))&&!"4136".equals((trade.getOrderNo()).substring(0,4))&&!"3918".equals((trade.getOrderNo()).substring(0,4))&&!"4110".equals((trade.getOrderNo()).substring(0,4))&&!"4212".equals((trade.getOrderNo()).substring(0,4))){
					logger.info("*******发送成功邮件："+trade.getOrderNo());
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
			}else if(masaM.getRes_resultCode().equals("00")){
				orderList=orderList+trade.getOrderNo()+"（Payment Pending!）、";
				trade.setTradeState("2"
						+ trade.getTradeState().substring(1,
								trade.getTradeState().length()));
				if(StringUtils.isNotBlank(masaM.getRes_masapayOrderNo())){
					trade.setVIPAuthorizationNo(masaM.getRes_masapayOrderNo());
				}
				trade.setRemark("Payment Pending!");
				backSussess="6";
			}else{
				orderList=orderList+trade.getOrderNo()+"("+masaM.getRes_errMsg()+")、";
//				trade.setTradeState("0"
//						+ trade.getTradeState().substring(1,
//								trade.getTradeState().length()));
				if(!"sfe01".equals(masaM.getRes_resultCode())){
					trade.setRemark("1093high risk!" +masaM.getRes_errMsg());
				}
				backSussess="0";
				backMessage=trade.getRemark();
			}
	if(StringUtils.isNotBlank(masaM.getRes_resultCode())&&!"sfe01".equals(masaM.getRes_resultCode())){
		List<InternationalMerchantChannels> merChannel=this.commonService.list("from InternationalMerchantChannels mc where mc.merchantId='"+trade.getMerchantId()+"' and mc.channelId='"+tim.getChannelId()+"'");
		//List<InternationalMerchantChannels> merChannel=this.commonService.list("from InternationalMerchantChannels mc where mc.merchantId='"+trade.getMerchantId()+"' and mc.channelId='"+tim.getChannelId()+"'");
		
		if(merChannel!=null&&merChannel.size()>0){
			InternationalMerchantChannels mc=merChannel.get(0);
			logger.info("*********更新通道*************");
			trade.setTradeChannel(Long.valueOf(mc.getId()));
		}
		commonService.update(trade);
		if("1".equals(backSussess)){
			commonService.delete(tem);
			if("1001".equals((trade.getOrderNo()).substring(0,4))||"4136".equals((trade.getOrderNo()).substring(0,4))){
				TemporarySynThread ts=new TemporarySynThread("http://www.xingbill.com/synTradeInfo",trade.getMerchantOrderNo(), backSussess, backMessage);
				ts.start();
			}
			if("3918".equals((trade.getOrderNo()).substring(0,4))||"4110".equals((trade.getOrderNo()).substring(0,4))){
				TemporarySynThread ts=new TemporarySynThread("http://www.win4mall.com/OrderAutomatic",trade.getMerchantOrderNo(),backSussess, backMessage);
				ts.start();
			}
			if("4160".equals((trade.getOrderNo()).substring(0,4))||"4161".equals((trade.getOrderNo()).substring(0,4))){
				TemporarySynThread ts=new TemporarySynThread("http://www.ipasspay.biz/index.php/Thirdpay/Sfepay/notifyUrl",trade.getMerchantOrderNo(), backSussess, trade.getRemark());
				ts.start();
			}
			if("4165".equals((trade.getOrderNo()).substring(0,4))){
				TemporarySynThread ts=new TemporarySynThread("http://www.youkutuan.com/do.php?act=charge_okok",trade.getMerchantOrderNo(),backSussess,backMessage);
				ts.start();
			}
			if("4169".equals((trade.getOrderNo()).substring(0,4))){
				TemporarySynThread ts=new TemporarySynThread("http://www.jjqsc.com/PayResult.php",trade.getMerchantOrderNo(),backSussess, backMessage);
				ts.start();
			}
			if("4066".equals((trade.getOrderNo()).substring(0,4))||"4216".equals((trade.getOrderNo()).substring(0,4))){
				if("maxmaillots.org".equals(trade.getTradeUrl())){
					TemporarySynThread ts=new TemporarySynThread("http://www.maxmaillots.org/payment_online_feback.php",trade.getMerchantOrderNo(),backSussess, backMessage);
					ts.start();
				}else{
					TemporarySynThread ts=new TemporarySynThread("http://"+trade.getTradeUrl()+"/payment_online_feback.php",trade.getMerchantOrderNo(),backSussess, backMessage);
					ts.start();
				}
			}
		}else if("6".equals(backSussess)){
			commonService.delete(tem);
		}else{
			logger.info("更新vip交易失败："+trade.getOrderNo());
			tem.setRemark("MS*"+masaM.getRes_errMsg());
			tem.setStatus("2");
			commonService.update(tem);
		}
		}
	}else if("HP".equals(channelName)||"LP".equals(channelName)){
		logger.info("进入"+channelName+"通道");
		YoungPayMessage yp=new YoungPayMessage();
		YoungPayUtil ypu=new YoungPayUtil();
		yp.setTransType("2P_SALES");
		String channelId="";
		if("4".equals(cardNo.substring(0, 1))){
			yp.setCardType("VISA");
			if("HP".equals(channelName)){
			channelId="275";
			}else if("LP".equals(channelName)){
				channelId="278";
			}
		}else if("5".equals(cardNo.substring(0, 1))){
			yp.setCardType("MASTER");
			if("HP".equals(channelName)){
			channelId="276";
			}else if("LP".equals(channelName)){
				channelId="279";
			}
		}else if("3".equals(cardNo.substring(0, 1))){
			yp.setCardType("JCB");
			channelId="277";
		}
		InternationalTerminalManager tim=(InternationalTerminalManager) commonService.uniqueResult("from InternationalTerminalManager where id='"+channelId+"'");
		
		List<InternationalMerchantChannels> imc=this.commonService.list("from InternationalMerchantChannels mc where mc.merchantId='"+trade.getMerchantId()+"' and mc.channelId='"+tim.getChannelId()+"'");
		if(imc!=null&&imc.size()>0){
			InternationalMerchantChannels channel=imc.get(0);
			logger.info("*********更新通道使用费*************");
			trade.setChannelFee(channel.getChannelFee());
		}
		
		yp.setMerId(tim.getMerchantNo());
		yp.setB2mOrder(trade.getOrderNo());
		yp.setCardNo(cardNo);
		
		yp.setExpireDate(tem.getExpirationYear()+tem.getExpirationMonth());
		yp.setCvcCode(tem.getCvv2());
		yp.setB2mBank(ic.getUserBank());
		Double amountAndFee=trade.getRmbAmount();
		if(trade.getChannelFee()!=null){
			amountAndFee=amountAndFee*(trade.getChannelFee()+1.0);
			amountAndFee = (double) (Math.round((double) amountAndFee * 100) / 100.00);
		}
		yp.setB2mFee(amountAndFee+"");
		yp.setB2mCur("RMB");
		yp.setB2mWebsite("sfepay.com");
		yp.setB2mReturnUrl("https://www.sfepay.com");
		yp.setB2mNotifyUrl("https://www.sfepay.com");
		yp.setIp(ic.getIp().split(",")[0]);
		yp.setB2mCargoCountry(tem.getCountry().substring(0,3));
		yp.setB2mHolderCountry(tem.getCountry().substring(0,3));
		yp.setB2mCargoName(ic.getShippingFullName()+ic.getLastName());
		yp.setB2mPhone(ic.getShippingPhone());
		yp.setB2mCargoEmail(ic.getShippingEmail());
		yp.setB2mCargoState(ic.getShippingState());
		yp.setB2mCargoCity(ic.getShippingCity());
		yp.setB2mCargoAddr(ic.getShippingAddress());
		yp.setB2mCargoZip(ic.getShippingZip());
		yp.setB2mHolderName(ic.getShippingFullName()+ic.getLastName());
		yp.setB2mHolderEmail(ic.getShippingEmail());
		yp.setB2mHolderState(ic.getShippingState());
		yp.setB2mHolderCity(ic.getShippingCity());
		yp.setB2mHolderAddr(ic.getShippingAddress());
		yp.setB2mHolderZip(ic.getShippingZip());
		yp.setSignKey(tim.getHashcode());
		try {
			ypu.getHarMessage(yp, "0");
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} 
		logger.info("vip交易结果："+yp.getRes_statusCode());
		String backSussess="";
		String backMessage="";
		if(yp.getRes_statusCode().equals("success")){
			trade.setTradeState("1"
					+ trade.getTradeState().substring(1,
							trade.getTradeState().length()));
//			trade.setTradeState(StatusUtil.updateStatus(trade.getTradeState(), 15, "3"));
//			trade.setRemark("Payment Declined!" + tm.getErrorCode());
			trade.setVIPDisposePorson("System");
			trade.setVIPDisposeDate(new Date());
			trade.setRemark("Payment Success!");
			orderList=orderList+trade.getOrderNo()+"（Payment Success!）、";
			backSussess="1";
			backMessage=tim.getBillingAddress();
			if(!"1001".equals((trade.getOrderNo()).substring(0,4))&&!"4136".equals((trade.getOrderNo()).substring(0,4))&&!"3918".equals((trade.getOrderNo()).substring(0,4))&&!"4110".equals((trade.getOrderNo()).substring(0,4))&&!"4212".equals((trade.getOrderNo()).substring(0,4))){
				logger.info("*******发送成功邮件："+trade.getOrderNo());
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
			orderList=orderList+trade.getOrderNo()+"("+yp.getRes_errorCode()+")、";
//			trade.setTradeState("0"
//					+ trade.getTradeState().substring(1,
//							trade.getTradeState().length()));
			if(!"sfe01".equals(yp.getRes_errorCode())){
				trade.setRemark("1093high risk!" +yp.getRes_errorCode());
			}
			backSussess="0";
			backMessage=trade.getRemark();
		}
		if(StringUtils.isNotBlank(yp.getRes_errorCode())&&!"sfe01".equals(yp.getRes_errorCode())){
			List<InternationalMerchantChannels> merChannel=this.commonService.list("from InternationalMerchantChannels mc where mc.merchantId='"+trade.getMerchantId()+"' and mc.channelId='"+tim.getChannelId()+"'");
			if(merChannel!=null&&merChannel.size()>0){
				InternationalMerchantChannels mc=merChannel.get(0);
				logger.info("*********更新通道*************");
				trade.setTradeChannel(Long.valueOf(mc.getId()));
			}
			commonService.update(trade);
			if("1".equals(backSussess)){
				commonService.delete(tem);
				if("1001".equals((trade.getOrderNo()).substring(0,4))||"4136".equals((trade.getOrderNo()).substring(0,4))){
				TemporarySynThread ts=new TemporarySynThread("http://www.xingbill.com/synTradeInfo",trade.getMerchantOrderNo(), backSussess, backMessage);
				ts.start();
				}
				if("3918".equals((trade.getOrderNo()).substring(0,4))||"4110".equals((trade.getOrderNo()).substring(0,4))){
					TemporarySynThread ts=new TemporarySynThread("http://www.win4mall.com/OrderAutomatic",trade.getMerchantOrderNo(),backSussess, backMessage);
					ts.start();
				}
				if("4160".equals((trade.getOrderNo()).substring(0,4))||"4161".equals((trade.getOrderNo()).substring(0,4))){
					TemporarySynThread ts=new TemporarySynThread("http://www.ipasspay.biz/index.php/Thirdpay/Sfepay/notifyUrl",trade.getMerchantOrderNo(), backSussess, trade.getRemark());
					ts.start();
				}
				if("4165".equals((trade.getOrderNo()).substring(0,4))){
					TemporarySynThread ts=new TemporarySynThread("http://www.youkutuan.com/do.php?act=charge_okok",trade.getMerchantOrderNo(),backSussess,backMessage);
					ts.start();
				}
				if("4169".equals((trade.getOrderNo()).substring(0,4))){
					TemporarySynThread ts=new TemporarySynThread("http://www.jjqsc.com/PayResult.php",trade.getMerchantOrderNo(),backSussess, backMessage);
					ts.start();
				}
				if("4066".equals((trade.getOrderNo()).substring(0,4))||"4216".equals((trade.getOrderNo()).substring(0,4))){
					if("maxmaillots.org".equals(trade.getTradeUrl())){
						TemporarySynThread ts=new TemporarySynThread("http://www.maxmaillots.org/payment_online_feback.php",trade.getMerchantOrderNo(),backSussess, backMessage);
						ts.start();
					}else{
						TemporarySynThread ts=new TemporarySynThread("http://"+trade.getTradeUrl()+"/payment_online_feback.php",trade.getMerchantOrderNo(),backSussess, backMessage);
						ts.start();
					}
				}
			}else{
				logger.info("更新vip交易失败："+trade.getOrderNo());
				tem.setRemark(yp.getRes_errorCode());
				tem.setStatus("2");
				commonService.update(tem);
				}
			}
	}else if("WR".equals(channelName)){
		logger.info("进入WanRong通道");
		WRPayMessage wrp = new WRPayMessage();
	 	WRPayUtil wu = new WRPayUtil();
	 	wrp.setTransType("sales");
	 	wrp.setApiType("1");
	 	wrp.setTransModel("M");
	 	wrp.setEncryptionMode("SHA256");
	 	wrp.setCharacterSet("UTF8");
	 	wrp.setMerNo("1000041");
	 	wrp.setTerNo("88816");
	 	
		String channelId="";
		if("4".equals(cardNo.substring(0, 1))){				
			channelId="293";
		}
		if("5".equals(cardNo.substring(0, 1))){				
			channelId="294";
		}
		InternationalTerminalManager tim=(InternationalTerminalManager) commonService.uniqueResult("from InternationalTerminalManager where id='"+channelId+"'");
	 		 	
	 	List<InternationalMerchantChannels> imc=this.commonService.list("from InternationalMerchantChannels mc where mc.merchantId='"+trade.getMerchantId()+"' and mc.channelId='"+tim.getChannelId()+"'");
		if(imc!=null&&imc.size()>0){
			InternationalMerchantChannels channel=imc.get(0);
			logger.info("*********更新通道使用费*************");
			trade.setChannelFee(channel.getChannelFee());
		}
	 		 	
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
		wrp.setGoodsString(ic.getProductInfo());
		wrp.setCardCountry(ic.getCountry());
		wrp.setCardState(ic.getState());
		wrp.setCardCity(ic.getCity());
	 	wrp.setCardAddress(ic.getAddress());
	 	wrp.setCardZipCode(ic.getZipcode());
	 	wrp.setCardFullName(ic.getFirstName()+"."+ic.getLastName());
	 	wrp.setCardFullPhone(ic.getPhone());
	 	wrp.setCardEmail(ic.getEmail());
	 	wrp.setGrCountry(ic.getShippingCountry());
		wrp.setGrState(ic.getShippingState());
		wrp.setGrCity(ic.getShippingCity());
		wrp.setGrAddress(ic.getShippingAddress());
		wrp.setGrZipCode(ic.getShippingZip());
		wrp.setGrphoneNumber(ic.getShippingPhone());
		wrp.setGrPerName(ic.getShippingFullName()+"."+ic.getLastName());
	 	wrp.setGrEmail(ic.getShippingEmail());
	 	wrp.setCardNO(cardNo);
	 	wrp.setCvv(tem.getCvv2());
	 	wrp.setExpYear("20"+tem.getExpirationYear());
	 	wrp.setExpMonth(tem.getExpirationMonth());
	 	
	 	wrp.setPayIP(ic.getIp().split(",")[0]);
		wrp.setMerMgrURL("www.sfepay.com");
		wrp.setReturnURL("www.sfepay.com");
		wrp.setNotifyURL("https://www.sfepay.com/onekpay");
		wrp.setWebInfo("MSIE 10.0");
		wrp.setLanguage("en");
	 	wrp.setMerremark("");
	 	wu.get(wrp);			


		logger.info("vip交易结果："+wrp.getRespCode());
		String backSussess="";
		String backMessage="";
		if(wrp.getRespCode().equals("00")){
			trade.setTradeState("1"
					+ trade.getTradeState().substring(1,
							trade.getTradeState().length()));
//			trade.setTradeState(StatusUtil.updateStatus(trade.getTradeState(), 15, "3"));
//			trade.setRemark("Payment Declined!" + tm.getErrorCode());
			trade.setVIPDisposePorson("System");
			trade.setVIPDisposeDate(new Date());
			trade.setVIPBatchNo(wrp.getTerNo());
			trade.setRemark("Payment Success!");
			orderList=orderList+trade.getOrderNo()+"（Payment Success!）、";
			backSussess="1";
			backMessage=wrp.getAcquirer();
			if(!"1001".equals((trade.getOrderNo()).substring(0,4))&&!"4136".equals((trade.getOrderNo()).substring(0,4))&&!"3918".equals((trade.getOrderNo()).substring(0,4))&&!"4110".equals((trade.getOrderNo()).substring(0,4))&&!"4212".equals((trade.getOrderNo()).substring(0,4))){
				logger.info("*******发送成功邮件："+trade.getOrderNo());
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
		}else if(wrp.getRespCode().equals("02")||wrp.getRespCode().equals("03")){
			trade.setTradeState("2"
					+ trade.getTradeState().substring(1,
							trade.getTradeState().length()));
		}else{
			orderList=orderList+trade.getOrderNo()+"("+wrp.getRespMsg()+")、";
//			trade.setTradeState("0"
//					+ trade.getTradeState().substring(1,
//							trade.getTradeState().length()));
			if(!"sfe01".equals(wrp.getRespCode())){
				trade.setRemark("1093high risk!" +wrp.getRespMsg());
			}
			backSussess="0";
			backMessage=trade.getRemark();
		}
		if(StringUtils.isNotBlank(wrp.getRespCode())&&!"sfe01".equals(wrp.getRespCode())){
			List<InternationalMerchantChannels> merChannel=this.commonService.list("from InternationalMerchantChannels mc where mc.merchantId='"+trade.getMerchantId()+"' and mc.channelId='"+tim.getChannelId()+"'");
			if(merChannel!=null&&merChannel.size()>0){
				InternationalMerchantChannels mc=merChannel.get(0);
				logger.info("*********更新通道*************");
				trade.setTradeChannel(Long.valueOf(mc.getId()));
			}
			commonService.update(trade);
			if("1".equals(backSussess)){
				commonService.delete(tem);
				if("1001".equals((trade.getOrderNo()).substring(0,4))||"4136".equals((trade.getOrderNo()).substring(0,4))){
				TemporarySynThread ts=new TemporarySynThread("http://www.xingbill.com/synTradeInfo",trade.getMerchantOrderNo(), backSussess, backMessage);
				ts.start();
				}
				if("3918".equals((trade.getOrderNo()).substring(0,4))||"4110".equals((trade.getOrderNo()).substring(0,4))){
					TemporarySynThread ts=new TemporarySynThread("http://www.win4mall.com/OrderAutomatic",trade.getMerchantOrderNo(),backSussess, backMessage);
					ts.start();
				}
				if("4160".equals((trade.getOrderNo()).substring(0,4))||"4161".equals((trade.getOrderNo()).substring(0,4))){
					TemporarySynThread ts=new TemporarySynThread("http://www.ipasspay.biz/index.php/Thirdpay/Sfepay/notifyUrl",trade.getMerchantOrderNo(), backSussess, trade.getRemark());
					ts.start();
				}
				if("4165".equals((trade.getOrderNo()).substring(0,4))){
					TemporarySynThread ts=new TemporarySynThread("http://www.youkutuan.com/do.php?act=charge_okok",trade.getMerchantOrderNo(),backSussess,backMessage);
					ts.start();
				}
				if("4169".equals((trade.getOrderNo()).substring(0,4))){
					TemporarySynThread ts=new TemporarySynThread("http://www.jjqsc.com/PayResult.php",trade.getMerchantOrderNo(),backSussess, backMessage);
					ts.start();
				}
				if("4066".equals((trade.getOrderNo()).substring(0,4))||"4216".equals((trade.getOrderNo()).substring(0,4))){
					if("maxmaillots.org".equals(trade.getTradeUrl())){
						TemporarySynThread ts=new TemporarySynThread("http://www.maxmaillots.org/payment_online_feback.php",trade.getMerchantOrderNo(),backSussess, backMessage);
						ts.start();
					}else{
						TemporarySynThread ts=new TemporarySynThread("http://"+trade.getTradeUrl()+"/payment_online_feback.php",trade.getMerchantOrderNo(),backSussess, backMessage);
						ts.start();
					}
				}
			}else{
				logger.info("更新vip交易失败："+trade.getOrderNo());
				tem.setRemark(wrp.getRespMsg());
				tem.setStatus("2");
				commonService.update(tem);
			}
		}		
			}else if("HR".equals(channelName)){
				logger.info("进入HR通道");
				HRPayUtil hr=new HRPayUtil();
				HRPayMessage hrm=new HRPayMessage();
				String channelId="";
				if("4".equals(cardNo.substring(0, 1))){
					channelId="280";
				}
				if("5".equals(cardNo.substring(0, 1))){
					channelId="281";
				}
				if("3".equals(cardNo.substring(0, 1))){
					channelId="282";
				}
				InternationalTerminalManager tim=(InternationalTerminalManager) commonService.uniqueResult("from InternationalTerminalManager where id='"+channelId+"'");
				
				List<InternationalMerchantChannels> imc=this.commonService.list("from InternationalMerchantChannels mc where mc.merchantId='"+trade.getMerchantId()+"' and mc.channelId='"+tim.getChannelId()+"'");
				if(imc!=null&&imc.size()>0){
					InternationalMerchantChannels channel=imc.get(0);
					logger.info("*********更新通道使用费*************");
					trade.setChannelFee(channel.getChannelFee());
				}
				
				
				hrm.setMerNo(tim.getMerchantNo());
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
				hrm.setWebInfo("MSIE 10.0");
				hrm.setLanguage("En");
				hrm.setCardCountry(tem.getCountry().substring(3, 5));
				hrm.setCardState(ic.getState());
				hrm.setCardCity(ic.getCity());
				hrm.setCardAddress(ic.getAddress());
				hrm.setCardZipCode(ic.getZipcode());
				hrm.setPayIP(ic.getIp().split(",")[0]);
				hrm.setCardFirstName(ic.getFirstName());
				hrm.setCardLastName(ic.getLastName());
				hrm.setCardFullPhone(ic.getPhone());
				hrm.setGrCountry(tem.getCountry().substring(3, 5));
				hrm.setGrState(ic.getShippingState());
				hrm.setGrCity(ic.getShippingCity());
				hrm.setGrAddress(ic.getShippingAddress());
				hrm.setGrZipCode(ic.getShippingZip());
				hrm.setGrEmail(ic.getShippingEmail());
				hrm.setGrphoneNumber(ic.getShippingPhone());
				hrm.setGrFirstName(ic.getFirstName());
				hrm.setGrLastName(ic.getLastName());
				hrm.setpName(ic.getProductInfo());
				hrm.setMd5Key(tim.getHashcode());
				hrm.setCardNO(cardNo);
				hrm.setExpYear("20"+tem.getExpirationYear());
				hrm.setExpMonth(tem.getExpirationMonth());
				hrm.setCvv(tem.getCvv2());
				hr.get(hrm);
				logger.info("vip交易结果："+hrm.getRes_respCode());
				String backSussess="";
				String backMessage="";
				if(hrm.getRes_respCode().equals("00")){
					trade.setTradeState("1"
							+ trade.getTradeState().substring(1,
									trade.getTradeState().length()));
					trade.setTradeState(StatusUtil.updateStatus(trade.getTradeState(), 15, "3"));
			//					trade.setRemark("Payment Declined!" + tm.getErrorCode());
					trade.setVIPAuthorizationNo(hrm.getRes_tradeNo());
					trade.setRemark("Payment Success!");
					orderList=orderList+trade.getOrderNo()+"（Payment Success!）、";
					backSussess="1";
					backMessage=hrm.getRes_acquirer();
					if(!"1001".equals((trade.getOrderNo()).substring(0,4))&&!"4136".equals((trade.getOrderNo()).substring(0,4))&&!"3918".equals((trade.getOrderNo()).substring(0,4))&&!"4110".equals((trade.getOrderNo()).substring(0,4))&&!"4212".equals((trade.getOrderNo()).substring(0,4))){
						logger.info("*******发送成功邮件："+trade.getOrderNo());
						EmailInfo emailinfo = new EmailInfo();
						String mailinfo = emailinfo.getPaymentResultEmail(
								ic.getEmail(),
								trade.getTradeAmount(),
								getStates().getCurrencyTypeByNo(
										trade.getMoneyType().intValue()),
								trade.getTradeUrl(), trade.getTradeTime(),
								hrm.getRes_acquirer(), trade.getMerchantOrderNo(),
								trade.getOrderNo());
						try {
							CCSendMail.setSendMail(ic.getEmail(), mailinfo,
									"sfepay@sfepay.com");
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}else{
					orderList=orderList+trade.getOrderNo()+"("+hrm.getRes_respMsg()+"）、";
			//		trade.setTradeState("0"
			//				+ trade.getTradeState().substring(1,
			//						trade.getTradeState().length()));
					if(!"sfe01".equals(hrm.getRes_respCode())){
						trade.setRemark("1093high risk!" +hrm.getRes_respMsg());
					}
					backSussess="0";
					backMessage=trade.getRemark();
				}
			if(StringUtils.isNotBlank(hrm.getRes_respCode())&&!"sfe01".equals(hrm.getRes_respCode())){
			List<InternationalMerchantChannels> merChannel=this.commonService.list("from InternationalMerchantChannels mc where mc.merchantId='"+trade.getMerchantId()+"' and mc.channelId='"+tim.getChannelId()+"'");
			if(merChannel!=null&&merChannel.size()>0){
				InternationalMerchantChannels mc=merChannel.get(0);
				logger.info("*********更新通道*************");
				trade.setTradeChannel(Long.valueOf(mc.getId()));
			}
			commonService.update(trade);
			if("1".equals(backSussess)){
				commonService.delete(tem);
				if("1001".equals((trade.getOrderNo()).substring(0,4))||"4136".equals((trade.getOrderNo()).substring(0,4))){
				TemporarySynThread ts=new TemporarySynThread("http://www.xingbill.com/synTradeInfo",trade.getMerchantOrderNo(), backSussess, backMessage);
				ts.start();
				}
				if("3918".equals((trade.getOrderNo()).substring(0,4))||"4110".equals((trade.getOrderNo()).substring(0,4))){
					TemporarySynThread ts=new TemporarySynThread("http://www.win4mall.com/OrderAutomatic",trade.getMerchantOrderNo(),backSussess, backMessage);
					ts.start();
				}
				if("4160".equals((trade.getOrderNo()).substring(0,4))||"4161".equals((trade.getOrderNo()).substring(0,4))){
					TemporarySynThread ts=new TemporarySynThread("http://www.ipasspay.biz/index.php/Thirdpay/Sfepay/notifyUrl",trade.getMerchantOrderNo(), backSussess, trade.getRemark());
					ts.start();
				}
				if("4165".equals((trade.getOrderNo()).substring(0,4))){
					TemporarySynThread ts=new TemporarySynThread("http://www.youkutuan.com/do.php?act=charge_okok",trade.getMerchantOrderNo(),backSussess,backMessage);
					ts.start();
				}
				if("4169".equals((trade.getOrderNo()).substring(0,4))){
					TemporarySynThread ts=new TemporarySynThread("http://www.jjqsc.com/PayResult.php",trade.getMerchantOrderNo(),backSussess, backMessage);
					ts.start();
				}
				if("4066".equals((trade.getOrderNo()).substring(0,4))||"4216".equals((trade.getOrderNo()).substring(0,4))){
					if("maxmaillots.org".equals(trade.getTradeUrl())){
						TemporarySynThread ts=new TemporarySynThread("http://www.maxmaillots.org/payment_online_feback.php",trade.getMerchantOrderNo(),backSussess, backMessage);
						ts.start();
					}else{
						TemporarySynThread ts=new TemporarySynThread("http://"+trade.getTradeUrl()+"/payment_online_feback.php",trade.getMerchantOrderNo(),backSussess, backMessage);
						ts.start();
					}
				}
			}else{
				logger.info("更新vip交易失败："+trade.getOrderNo());
				tem.setRemark(hrm.getRes_respMsg());
				tem.setStatus("2");
				commonService.update(tem);
					}
				}									
			}									
	    }
	}
//			}
		toTemporaryInfo();
		msg="重跑成功！订单结果："+orderList;
		return SUCCESS;
	}
	// 网址交易基础信息
	public void getWebTradeDetail(){
		if(StringUtils.isNotBlank(tranUrl)){
			String hql="select w.executetime,w.webSiteType from InternationalWebchannels w where w.tradeWebsite like '%"+tranUrl.trim()+"%' order by w.executetime desc ";
			List list = this.commonService.list(hql);
			String hql2=" from InternationalRiskItems t where t.itemType='3'";
			List<InternationalRiskItems> list2=commonService.list(hql2);
			tradeNum = (Long) commonService
					.uniqueResult("select count(*) from InternationalTradeinfo t where t.tradeUrl like '%"+tranUrl.trim()+"%' and t.tradeTime<=to_date('20"+tradtime+"','yyyy-MM-dd hh24:mi:ss') ")+"";
			if(list!=null&&list.size()>0){
				Object[] obj=(Object[]) list.get(0);
				openTime=obj[0].toString();
				webType=obj[1]+"";
				for(InternationalRiskItems web:list2){
					if(StringUtils.isNotBlank(web.getItemName())&&obj[1]!=null){
						if(obj[1].toString().toLowerCase().indexOf(web.getItemName().toLowerCase())>=0){
							isRisk="1";
							break;
						}
					}else{
						isRisk="0";
					}
				}
			}
		}

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
			// 把MaxMind返回的参数打印出来,
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
	public String getMerchantNo() {
		return merchantNo;
	}
	public void setMerchantNo(String merchantNo) {
		this.merchantNo = merchantNo;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getQip() {
		return qip;
	}
	public void setQip(String qip) {
		this.qip = qip;
	}
	public String getQemail() {
		return qemail;
	}
	public void setQemail(String qemail) {
		this.qemail = qemail;
	}
	public List getTempDetail() {
		return tempDetail;
	}
	public void setTempDetail(List tempDetail) {
		this.tempDetail = tempDetail;
	}
	public String getTranUrl() {
		return tranUrl;
	}
	public void setTranUrl(String tranUrl) {
		this.tranUrl = tranUrl;
	}
	public String getTradtime() {
		return tradtime;
	}
	public void setTradtime(String tradtime) {
		this.tradtime = tradtime;
	}
	public String getIsRisk() {
		return isRisk;
	}
	public void setIsRisk(String isRisk) {
		this.isRisk = isRisk;
	}
	public String getWebType() {
		return webType;
	}
	public void setWebType(String webType) {
		this.webType = webType;
	}
	public String getOpenTime() {
		return openTime;
	}
	public void setOpenTime(String openTime) {
		this.openTime = openTime;
	}
	public String getTradeNum() {
		return tradeNum;
	}
	public void setTradeNum(String tradeNum) {
		this.tradeNum = tradeNum;
	}
	public String getTturl() {
		return tturl;
	}
	public void setTturl(String tturl) {
		this.tturl = tturl;
	}
	public String getD1() {
		return d1;
	}
	public void setD1(String d1) {
		this.d1 = d1;
	}
	public String getD2() {
		return d2;
	}
	public void setD2(String d2) {
		this.d2 = d2;
	}
	public String getD3() {
		return d3;
	}
	public void setD3(String d3) {
		this.d3 = d3;
	}

}
