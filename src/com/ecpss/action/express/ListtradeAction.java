package com.ecpss.action.express;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.DelayQueue;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import vpn.IPassPayTemporMessage;
import vpn.IpassPayTemporary;
import vpn.MSqueryorder;
import vpn.MSrefundorder;
import vpn.Msqueryorderutil;
import vpn.Msrefundorderutil;
import vpn.ZMTTradeurlMessage;
import vpn.ZMTTradeurlUtil;

import com.ecpss.action.BaseAction;
import com.ecpss.action.TemporarySynThread;
import com.ecpss.model.payment.InternationalCardholdersInfo;
import com.ecpss.model.payment.InternationalTradeinfo;
import com.ecpss.model.refund.InternationalRefundManager;
import com.ecpss.model.shop.InternationalMerchant;
import com.ecpss.service.common.CommonService;
import com.ecpss.service.iservice.ShopManagerService;
import com.ecpss.util.AES;

public class ListtradeAction extends BaseAction {
	
	@Autowired
	@Qualifier("shopManagerService")
	private ShopManagerService shopManagerService;
	@Autowired
	@Qualifier("commonService")
	private CommonService commonService;
	private String merNo;
	private String starttime;
	private String endtime;
	
	private String orders;
	private String orderNo;
	private String merchantOrderNo;
	private String shippingdate;
	private String cardtype;
	private String email;
	private String ip;
	private String amount;
	private String currencyCode;
	private String terNo;
	private String stauts;
	private String istuikuan;
	private String isjufu;
	private String ishuakuan;
	private String ischuanhao;
	private String tradeurl;
	private String remark;
	
	private String respCode;
	private String respMsg;
	private int responseCode;
	private String message;
	
	private String status;
	private String transactionid;
	private String orderid;
	private String currency;
	private String originalcurrency;
	private String originalamount;
	private String signature;
	
	private String billno;
	private String succeed;
	private String orderinfo;
	
	
	public String resultCode;
	public String errCode;
	public String errMsg;
	
	private String refundOrderNo;
	//private String merchantOrderNo;
	private String masapayRefundOrderNo;
	private String refundAmount;
	
	private String TransactionId;
	private String OrderId;
	private String Status;
	private String TransactionAmount;
	
	Logger logger = Logger.getLogger(ListtradeAction.class.getName());	

	public void listtrade() throws IOException{
		HttpServletRequest request = ServletActionContext.getRequest();
		logger.info("********3918或4110商户订单查询结果**********:"+merNo+starttime+endtime);		
		if((merNo.equals("4110")||merNo.equals("3918"))&&StringUtils.isNotBlank(starttime)&&StringUtils.isNotBlank(endtime)){			
			StringBuffer sb = new StringBuffer();	
			String selectquery = "select ti.orderNo," + // 1
					"ti.merchantOrderNo," + // 2				
					"to_char(ti.tradeTime,'yy-mm-dd hh24:mi:ss')," + // 3
					"ci.cardNo," +//4
					"ci.email," +//5
					"ci.ip," +//6
					"ti.tradeAmount," + // 7
					"ti.moneyType," + // 8				
					"c.channelName," + // 9
					"ti.tradeState," + // 10
					"ti.backCount," + // 11
					"ti.matterDepict," +//12
					"ti.isPicture," + // 13
					"ti.isTrackNo," + // 14
					"ti.tradeUrl," + // 15							
					"ti.remark"; // 16				
			sb
					.append(" from International_Tradeinfo ti left join "
							+ "International_Merchant  m on ti.merchantId=m.id left join "
							+ "International_CardholdersInfo ci on  ci.tradeId=ti.id left join "
							+ "International_MerchantChannels mc on ti.tradeChannel=mc.id left join "
							+ "International_Channels c on mc.channelId=c.id "
							+ "where 1=1 "); // 商户通道的通道
			sb.append(" and  m.merno='" + merNo + "' ");
			if (StringUtils.isNotBlank(starttime)) {
				sb.append(" and ti.tradeTime>=to_date('" + starttime
						+ " 00:00:00','yyyy-MM-dd hh24:mi:ss') ");
			}
			if (StringUtils.isNotBlank(endtime)) {
				sb.append(" and ti.tradeTime<=to_date('" + endtime + " 23:59:59"
						+ "','yyyy-MM-dd hh24:mi:ss') ");
			}
			List<Object[]> list = this.commonService.getByList(selectquery
					+ sb.toString());	
			JSONObject json = new JSONObject();
			json.put("merNo", merNo);
			JSONArray array = new JSONArray();

			for(Object[] obj:list){					
				AES aes=new AES();
				String cardNo="";
				if(obj[3]!=null){
					cardNo=aes.getCarNo(obj[3].toString());
				}else{
					cardNo="ERROR";
				}
				if ((cardNo.startsWith("30")
							|| cardNo.startsWith("35"))&& cardNo.length() == 16) {
					obj[3] = "JCB";
				}else if (cardNo.startsWith("4") && cardNo.length() == 16) {
						obj[3] = "VISA";
				}else if (cardNo.startsWith("5") && cardNo.length() == 16) {
						obj[3] = "Master";
				}else{
					obj[3] = "ERROR";
				}
				
				if ((obj[7].toString().equals("1"))){
					obj[7] = "USD";
				}else if((obj[7].toString().equals("3"))){
					obj[7] = "RMB";
				}
				
				if ((obj[9].toString().substring(1, 2).equals("0"))){
					obj[10] = "未退款";
				}else if ((obj[9].toString().substring(1, 2).equals("1"))){
					obj[10] = "已退款";
				}else if ((obj[9].toString().substring(1, 2).equals("2"))){
					obj[10] = "部分退款";
				}
				
				if ((obj[9].toString().substring(2, 3).equals("0"))){
					obj[11] = "未拒付";
				}else if ((obj[9].toString().substring(2, 3).equals("1"))){
					obj[11] = "已拒付";
				}
				
				if ((obj[9].toString().substring(7, 8).equals("0"))){
					obj[12] = "未划款";
				}else if ((obj[9].toString().substring(7, 8).equals("1"))){
					obj[12] = "已划款";
				}
				
				if ((obj[9].toString().substring(0, 1).equals("0"))){
					obj[9] = "失败";
				}else if ((obj[9].toString().substring(0, 1).equals("1"))){
					obj[9] = "成功";
				}else if ((obj[9].toString().substring(0, 1).equals("2"))){
					obj[9] = "待处理";
				}else if ((obj[9].toString().substring(0, 1).equals("3"))){
					obj[9] = "取消";
				}
				
				if(obj[13] == null){
					obj[13] = "否";
				}	
				Map<String, Object> map = new HashedMap();
				map.put("orderNo", obj[0]);
				map.put("merchantOrderNo", obj[1]);
				map.put("shippingdate", obj[2]);
				map.put("cardtype", obj[3]);
				map.put("email", obj[4]);
				map.put("ip", obj[5]);
				map.put("amount", obj[6]);
				map.put("currencyCode", obj[7]);
				map.put("terNo", obj[8]);
				map.put("stauts", obj[9]);
				map.put("istuikuan", obj[10]);
				map.put("isjufu", obj[11]);
				map.put("ishuakuan", obj[12]);
				map.put("ischuanhao", obj[13]);
				map.put("tradeurl", obj[14]);
				map.put("remark", obj[15]);
				array.add(map);
			}		
			json.put("orders", array);
			orders = json.toString();
			HttpServletResponse response = ServletActionContext.getResponse();
			response.setContentType("text/html;charset=utf-8");
			PrintWriter out = response.getWriter();
			out.print(orders);
		}else{
			orders = "请输入正确的查询条件";
			HttpServletResponse response = ServletActionContext.getResponse();
			response.setContentType("text/html;charset=utf-8");
			PrintWriter out = response.getWriter();
			out.print(orders);
		}
	}
	
	public void onekpay() throws Exception{
		HttpServletRequest request = ServletActionContext.getRequest();
		logger.info("onekpay异步返回信息:"+respCode);
		logger.info("orderNo:"+orderNo+"respCode:"+respCode+"respMsg:"+respMsg);
		InternationalTradeinfo trade=(InternationalTradeinfo) commonService.uniqueResult("from InternationalTradeinfo where orderNo='"+orderNo+"'");
		InternationalMerchant mer=(InternationalMerchant) commonService.uniqueResult("from InternationalMerchant where id='"+trade.getMerchantId()+"'");
		InternationalCardholdersInfo card=(InternationalCardholdersInfo) commonService.uniqueResult("from InternationalCardholdersInfo where tradeId='"+trade.getId()+"'");
		
		String cardNo=AES.getCarNo(card.getCardNo());		
		String expiryDate = AES.getCarNo(card.getExpiryDate());
		String month = expiryDate.substring(0,2);
		String year = expiryDate.substring(2,4);
		String cvv = AES.getCarNo(card.getCvv2());
			
		StringBuffer buffer = new StringBuffer(trade.getTradeState());
		 	if(respCode.equals("00")){				 		
		 		buffer.replace(0, 1, "1");	
		 		trade.setRemark("Payment Success!");
		 		this.responseCode=88;
		 		/*MD5info = trade.getMerchantOrderNo()
						+ trade.getMoneyType() + ordercountValue
						+ responseCode + MD5key;
				md5Value = md5.getMD5ofStr(MD5info);*/
		 		this.message = "Payment Success!";		 		
		 	}else if(respCode.equals("02")||respCode.equals("03")){
		 		buffer.replace(0, 1, "2");
		 		trade.setRemark("Waiting processing!");
		 		this.responseCode=19;
		 		this.message = "Waiting processing!";		 		
		 	}else if(respCode.equals("01")){
		 		buffer.replace(0, 1, "0");
		 		trade.setRemark("Payment Declined!");
		 		this.responseCode=0;
		 		this.message = "Payment Declined!"; 
				shopManagerService.addTemporaryTradInfo(trade.getOrderNo(), year, month,cvv,card.getCountry(),mer.getMd5key(), card.getIp(),"MSIE 10.0","WR"+respMsg);
		 	}
		 trade.setTradeState(buffer.toString());
		 commonService.update(trade);
		 logger.info("**************支付返回码*************"+responseCode);
		 ServletOutputStream out = ServletActionContext.getResponse().getOutputStream();
		 out.print("OK");
		 //return "success";
	 }
	
	public void Gofpay() throws Exception{	
		HttpServletRequest request = ServletActionContext.getRequest();  	      
/*		Map<String, String[]> map=request.getParameterMap();
		Iterator<Entry<String, String[]>> iter=map.entrySet().iterator();
		
		while (iter.hasNext()) {
			Entry<String, String[]> enter = iter.next();
			String key = enter.getKey();
			String value = enter.getValue()[0];
			logger.info("key: " + key);
			logger.info("value: " + value);
			
		}*/
		logger.info("Gofpay异步返回信息:"+status);
		logger.info("TransactionId:"+transactionid+"OrderId:"+orderid+"stauts:"+status+"Signature:"+signature);
		InternationalTradeinfo trade=(InternationalTradeinfo) commonService.uniqueResult("from InternationalTradeinfo where orderNo='"+orderid+"'");
		InternationalMerchant mer=(InternationalMerchant) commonService.uniqueResult("from InternationalMerchant where id='"+trade.getMerchantId()+"'");
		InternationalCardholdersInfo card=(InternationalCardholdersInfo) commonService.uniqueResult("from InternationalCardholdersInfo where tradeId='"+trade.getId()+"'");
		/*if(!"4132".equals(orderid.substring(0, 4))){
			InternationalTemporaryTradInfo tm= (InternationalTemporaryTradInfo) commonService.uniqueResult("from InternationalTemporaryTradInfo where orderNo='"+orderid+"'");		
			 cardNo=AES.getCarNo(card.getCardNo());		
			 month = tm.getExpirationMonth();
			 year = tm.getExpirationYear();
			 cvv = tm.getCvv2();
		}*/
		StringBuffer buffer = new StringBuffer(trade.getTradeState());
		 	if(status.equals("Success")){				 		
		 		buffer.replace(0, 1, "1");	
		 		trade.setRemark("Payment Success!");
		 		this.responseCode=88;
		 		/*MD5info = trade.getMerchantOrderNo()
						+ trade.getMoneyType() + ordercountValue
						+ responseCode + MD5key;
				md5Value = md5.getMD5ofStr(MD5info);*/
		 		this.message = "Payment Success!";	
				if("1001".equals((trade.getOrderNo()).substring(0,4))||"4136".equals((trade.getOrderNo()).substring(0,4))){
					TemporarySynThread ts=new TemporarySynThread("http://www.xingbill.com/synTradeInfo",trade.getMerchantOrderNo(), "1", message);
					ts.start();
					}
				if("4160".equals((trade.getOrderNo()).substring(0,4))||"4161".equals((trade.getOrderNo()).substring(0,4))){
					//TemporarySynThread ts=new TemporarySynThread("http://www.ipasspay.biz/index.php/Thirdpay/Sfepay/notifyUrl",trade.getMerchantOrderNo(), "1", trade.getRemark());
					//ts.start();
				 	IPassPayTemporMessage ipass = new IPassPayTemporMessage();
				 	IpassPayTemporary tt = new IpassPayTemporary();
				 	ipass.setOrderNo(trade.getMerchantOrderNo());
				 	ipass.setRes_orderStatus("1");
				 	ipass.setRemark(trade.getRemark());
				 	tt.get(ipass);
				}
				
		 	//}else if(!status.equals("Completed")){
		 	}else if(status.equals("Failure")){
		 		buffer.replace(0, 1, "0");
		 		trade.setRemark("Payment Declined!");
		 		this.responseCode=0;
		 		this.message = "Payment Declined!";
		 		/*if(!"4132".equals(orderid.substring(0, 4))){
		 			shopManagerService.addTemporaryTradInfo(trade.getOrderNo(), year, month,cvv,card.getCountry(),mer.getMd5key(), card.getIp(),"MSIE 10.0","GQ"+trade.getRemark());
		 		}*/
				if("1001".equals((trade.getOrderNo()).substring(0,4))||"4136".equals((trade.getOrderNo()).substring(0,4))){
					TemporarySynThread ts=new TemporarySynThread("http://www.xingbill.com/synTradeInfo",trade.getMerchantOrderNo(), "0", message);
					ts.start();
				}
				if("4160".equals((trade.getOrderNo()).substring(0,4))||"4161".equals((trade.getOrderNo()).substring(0,4))){
					/*TemporarySynThread ts=new TemporarySynThread("http://www.ipasspay.biz/index.php/Thirdpay/Sfepay/notifyUrl",trade.getMerchantOrderNo(), "0", trade.getRemark());
					ts.start();*/
				 	IPassPayTemporMessage ipass = new IPassPayTemporMessage();
				 	IpassPayTemporary tt = new IpassPayTemporary();
				 	ipass.setOrderNo(trade.getMerchantOrderNo());
				 	ipass.setRes_orderStatus("0");
				 	ipass.setRemark(trade.getRemark());
				 	tt.get(ipass);
				}
		 	}
		 trade.setTradeState(buffer.toString());
		 commonService.update(trade);
		 logger.info("**************支付返回码*************"+responseCode);
		 ServletOutputStream out = ServletActionContext.getResponse().getOutputStream();
		 out.print("OK");
		 //return "success";
	 }

	public void Wintopay() throws Exception{
		HttpServletRequest request = ServletActionContext.getRequest();
		//logger.info(request+"");
		logger.info("Wintopay:"+request.getQueryString()+"");
		logger.info("Wintopay异步返回信息:"+request.getParameter("billno"));
		//wintopay的异步通知
		logger.info("billno:"+billno+"succeed:"+succeed+"orderinfo:"+orderinfo);
		InternationalTradeinfo trade=(InternationalTradeinfo) commonService.uniqueResult("from InternationalTradeinfo where orderNo='"+request.getParameter("billno")+"'");
		InternationalMerchant mer=(InternationalMerchant) commonService.uniqueResult("from InternationalMerchant where id='"+trade.getMerchantId()+"'");
		InternationalCardholdersInfo card=(InternationalCardholdersInfo) commonService.uniqueResult("from InternationalCardholdersInfo where tradeId='"+trade.getId()+"'");
/*		
		String cardNo=AES.getCarNo(card.getCardNo());		
		String expiryDate = AES.getCarNo(card.getExpiryDate());
		String month = expiryDate.substring(0,2);
		String year = expiryDate.substring(2,4);
		String cvv = AES.getCarNo(card.getCvv2());
		*/	
		StringBuffer buffer = new StringBuffer(trade.getTradeState());
		 	if(request.getParameter("succeed").equals("1")){				 		
		 		buffer.replace(0, 1, "1");	
		 		trade.setRemark("Payment Success!");
		 		this.responseCode=88;
		 		/*MD5info = trade.getMerchantOrderNo()
						+ trade.getMoneyType() + ordercountValue
						+ responseCode + MD5key;
				md5Value = md5.getMD5ofStr(MD5info);*/		 
				if("1001".equals((trade.getOrderNo()).substring(0,4))||"4136".equals((trade.getOrderNo()).substring(0,4))){
					TemporarySynThread ts=new TemporarySynThread("http://www.xingbill.com/synTradeInfo",trade.getMerchantOrderNo(), "1", message);
					ts.start();
				}
				if("4160".equals((trade.getOrderNo()).substring(0,4))||"4161".equals((trade.getOrderNo()).substring(0,4))){
/*					TemporarySynThread ts=new TemporarySynThread("http://www.ipasspay.biz/index.php/Thirdpay/Sfepay/notifyUrl",trade.getMerchantOrderNo(), "1", trade.getRemark());
					ts.start();*/
				 	IPassPayTemporMessage ipass = new IPassPayTemporMessage();
				 	IpassPayTemporary tt = new IpassPayTemporary();
				 	ipass.setOrderNo(trade.getMerchantOrderNo());
				 	ipass.setRes_orderStatus("1");
				 	ipass.setRemark(trade.getRemark());
				 	tt.get(ipass);
				}
				if("4169".equals((trade.getOrderNo()).substring(0,4))){
					TemporarySynThread ts=new TemporarySynThread("http://www.jjqsc.com/PayResult.php",trade.getMerchantOrderNo(), "1", trade.getRemark());
					ts.start();
				}
				if("4165".equals((trade.getOrderNo()).substring(0,4))){
					TemporarySynThread ts=new TemporarySynThread("http://www.youkutuan.com/do.php?act=charge_okok",trade.getMerchantOrderNo(), "1", trade.getRemark());
					ts.start();
				}
				if("3918".equals((trade.getOrderNo()).substring(0,4))||"4110".equals((trade.getOrderNo()).substring(0,4))){
					TemporarySynThread ts=new TemporarySynThread("http://www.win4mall.com/OrderAutomatic",trade.getMerchantOrderNo(),"1", trade.getRemark());
					ts.start();
				}
				if("4066".equals((trade.getOrderNo()).substring(0,4))||"4216".equals((trade.getOrderNo()).substring(0,4))){
					if("maxmaillots.org".equals(trade.getTradeUrl())){
						TemporarySynThread ts=new TemporarySynThread("http://www.maxmaillots.org/payment_online_feback.php",trade.getMerchantOrderNo(),"1", trade.getRemark());
						ts.start();
					}else{
						TemporarySynThread ts=new TemporarySynThread("http://"+trade.getTradeUrl()+"/payment_online_feback.php",trade.getMerchantOrderNo(),"1", trade.getRemark());
						ts.start();
					}
				}
		 	}else if(request.getParameter("succeed").equals("0")){
		 		buffer.replace(0, 1, "0");
		 		trade.setRemark("Payment Declined!");
		 		this.responseCode=0;
		 		this.message = "Payment Declined!"; 
				if("1001".equals((trade.getOrderNo()).substring(0,4))||"4136".equals((trade.getOrderNo()).substring(0,4))){
					TemporarySynThread ts=new TemporarySynThread("http://www.xingbill.com/synTradeInfo",trade.getMerchantOrderNo(), "0", message);
					ts.start();
				}
				if("4160".equals((trade.getOrderNo()).substring(0,4))||"4161".equals((trade.getOrderNo()).substring(0,4))){
					/*TemporarySynThread ts=new TemporarySynThread("http://www.ipasspay.biz/index.php/Thirdpay/Sfepay/notifyUrl",trade.getMerchantOrderNo(), "0", trade.getRemark());
					ts.start();*/
				 	IPassPayTemporMessage ipass = new IPassPayTemporMessage();
				 	IpassPayTemporary tt = new IpassPayTemporary();
				 	ipass.setOrderNo(trade.getMerchantOrderNo());
				 	ipass.setRes_orderStatus("0");
				 	ipass.setRemark(trade.getRemark());
				 	tt.get(ipass);
				}		
				if("4169".equals((trade.getOrderNo()).substring(0,4))){
					TemporarySynThread ts=new TemporarySynThread("http://www.jjqsc.com/PayResult.php",trade.getMerchantOrderNo(), "0", trade.getRemark());
					ts.start();
				}
				if("4165".equals((trade.getOrderNo()).substring(0,4))){
					TemporarySynThread ts=new TemporarySynThread("http://www.youkutuan.com/do.php?act=charge_okok",trade.getMerchantOrderNo(), "0", trade.getRemark());
					ts.start();
				}
				if("3918".equals((trade.getOrderNo()).substring(0,4))||"4110".equals((trade.getOrderNo()).substring(0,4))){
					TemporarySynThread ts=new TemporarySynThread("http://www.win4mall.com/OrderAutomatic",trade.getMerchantOrderNo(),"0", trade.getRemark());
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
		 		//shopManagerService.addTemporaryTradInfo(trade.getOrderNo(), year, month,cvv,card.getCountry(),mer.getMd5key(), card.getIp(),"MSIE 10.0","WR"+respMsg);
		 	}
		 trade.setTradeState(buffer.toString());
		 commonService.update(trade);
		 logger.info("**************支付返回码*************"+responseCode);
		 ServletOutputStream out = ServletActionContext.getResponse().getOutputStream();
		 out.print("success");
		 //return "success";
	 }
	
	public void ZMTtradeurl() throws Exception{
		String sql = "select distinct(t.tradewebsite) from International_Webchannels t";
		List<Object[]> list = this.commonService.getByList(sql);
		//JSONArray jsonarry = new JSONArray();
		ZMTTradeurlMessage ip = new ZMTTradeurlMessage();
		ZMTTradeurlUtil ipurl = new ZMTTradeurlUtil();
		for (int i = 0; i < list.size(); i++) {
			ip.setWebSite(list.get(i)+"");
			ipurl.get(ip);
		}
	 }
	
	
	public void IPassPay() throws Exception{
		HttpServletRequest request = ServletActionContext.getRequest();		
		//logger.info("Wintopay异步返回信息:"+billno);
		//logger.info("billno:"+billno+"succeed:"+succeed+"orderinfo:"+orderinfo);
		
		Map<String, String[]> map=request.getParameterMap();
		Iterator<Entry<String, String[]>> iter=map.entrySet().iterator();
		
		while (iter.hasNext()) {
			Entry<String, String[]> enter = iter.next();
			String key = enter.getKey();
			String value = enter.getValue()[0];
			logger.info("key: " + key);
			logger.info("value: " + value);			
		}		
		logger.info("Wintopay异步返回信息:"+request);
	
	}
	
	public void masapay() throws Exception{
		HttpServletRequest request = ServletActionContext.getRequest();
		logger.info("masapay异步返回信息:"+resultCode);
		logger.info("merchantOrderNo:"+merchantOrderNo+"resultCode:"+resultCode+"errCode:"+errCode+"errMsg:"+errMsg);
		InternationalTradeinfo trade=(InternationalTradeinfo) commonService.uniqueResult("from InternationalTradeinfo where orderNo='"+merchantOrderNo+"'");
		InternationalMerchant mer=(InternationalMerchant) commonService.uniqueResult("from InternationalMerchant where id='"+trade.getMerchantId()+"'");
		InternationalCardholdersInfo card=(InternationalCardholdersInfo) commonService.uniqueResult("from InternationalCardholdersInfo where tradeId='"+trade.getId()+"'");
		
/*		String cardNo=AES.getCarNo(card.getCardNo());		
		String expiryDate = AES.getCarNo(card.getExpiryDate());
		String month = expiryDate.substring(0,2);
		String year = expiryDate.substring(2,4);
		String cvv = AES.getCarNo(card.getCvv2());*/
							
		StringBuffer buffer = new StringBuffer(trade.getTradeState());
		Thread.sleep(2*1000);		

		 	if(resultCode.equals("10")){				 		
		 		buffer.replace(0, 1, "1");	
		 		trade.setRemark("Payment Success!");
		 		this.responseCode=88;
		 		/*MD5info = trade.getMerchantOrderNo()
						+ trade.getMoneyType() + ordercountValue
						+ responseCode + MD5key;
				md5Value = md5.getMD5ofStr(MD5info);*/
		 		this.message = "Payment Success!";	
		 		
				if("1001".equals((trade.getOrderNo()).substring(0,4))||"4136".equals((trade.getOrderNo()).substring(0,4))){
					TemporarySynThread ts=new TemporarySynThread("http://www.xingbill.com/synTradeInfo",trade.getMerchantOrderNo(), "1", message);
					ts.start();
				}
				if("4160".equals((trade.getOrderNo()).substring(0,4))||"4161".equals((trade.getOrderNo()).substring(0,4))){
					/*TemporarySynThread ts=new TemporarySynThread("http://www.ipasspay.biz/index.php/Thirdpay/Sfepay/notifyUrl",trade.getMerchantOrderNo(), "1", trade.getRemark());
					ts.start();*/
				 	IPassPayTemporMessage ipass = new IPassPayTemporMessage();
				 	IpassPayTemporary tt = new IpassPayTemporary();
				 	ipass.setOrderNo(trade.getMerchantOrderNo());
				 	ipass.setRes_orderStatus("1");
				 	ipass.setRemark(trade.getRemark());
				 	tt.get(ipass);
				}
				if("4169".equals((trade.getOrderNo()).substring(0,4))){
					TemporarySynThread ts=new TemporarySynThread("http://www.jjqsc.com/PayResult.php",trade.getMerchantOrderNo(), "1", trade.getRemark());
					ts.start();
				}
				if("4165".equals((trade.getOrderNo()).substring(0,4))){
					TemporarySynThread ts=new TemporarySynThread("http://www.youkutuan.com/do.php?act=charge_okok",trade.getMerchantOrderNo(), "1", trade.getRemark());
					ts.start();
				}
				if("3918".equals((trade.getOrderNo()).substring(0,4))||"4110".equals((trade.getOrderNo()).substring(0,4))){
					if("5872".equals(trade.getTradeChannel()+"")) {
						TemporarySynThread ts = new TemporarySynThread("http://www.win4mall.com/OrderAutomatic", trade.getMerchantOrderNo(), "1", "99BILL*bisunhealth");
						ts.start();
					}else{
						TemporarySynThread ts = new TemporarySynThread("http://www.win4mall.com/OrderAutomatic", trade.getMerchantOrderNo(), "1", "MAS*huashoulin.com");
						ts.start();
					}
				}
				if("4066".equals((trade.getOrderNo()).substring(0,4))||"4216".equals((trade.getOrderNo()).substring(0,4))){
					if("maxmaillots.org".equals(trade.getTradeUrl())){
						TemporarySynThread ts=new TemporarySynThread("http://www.maxmaillots.org/payment_online_feback.php",trade.getMerchantOrderNo(),"1", trade.getRemark());
						ts.start();
					}else{
						TemporarySynThread ts=new TemporarySynThread("http://"+trade.getTradeUrl()+"/payment_online_feback.php",trade.getMerchantOrderNo(),"1", trade.getRemark());
						ts.start();
					}
				}
		 	}else if(resultCode.equals("12")){	
		 		String code = String.valueOf(buffer.charAt(0));
		 		if(!"1".equals(code)){
			 		buffer.replace(0, 1, "2");
			 		trade.setRemark("Waiting processing*MS!");
			 		this.responseCode=19;
			 		this.message = "Waiting processing*MS!";	
		 		}
		 	}else if(resultCode.equals("11")){
		 		buffer.replace(0, 1, "0");
		 		trade.setRemark(errMsg);
		 		this.responseCode=0;
		 		this.message = "Payment Declined!"; 
		 		
				if("1001".equals((trade.getOrderNo()).substring(0,4))||"4136".equals((trade.getOrderNo()).substring(0,4))){
					TemporarySynThread ts=new TemporarySynThread("http://www.xingbill.com/synTradeInfo",trade.getMerchantOrderNo(), "0", message);
					ts.start();
				}
				if("4160".equals((trade.getOrderNo()).substring(0,4))||"4161".equals((trade.getOrderNo()).substring(0,4))){
					/*TemporarySynThread ts=new TemporarySynThread("http://www.ipasspay.biz/index.php/Thirdpay/Sfepay/notifyUrl",trade.getMerchantOrderNo(), "0", trade.getRemark());
					ts.start();*/
				 	IPassPayTemporMessage ipass = new IPassPayTemporMessage();
				 	IpassPayTemporary tt = new IpassPayTemporary();
				 	ipass.setOrderNo(trade.getMerchantOrderNo());
				 	ipass.setRes_orderStatus("0");
				 	ipass.setRemark(trade.getRemark());
				 	tt.get(ipass);
				}
				if("4169".equals((trade.getOrderNo()).substring(0,4))){
					TemporarySynThread ts=new TemporarySynThread("http://www.jjqsc.com/PayResult.php",trade.getMerchantOrderNo(), "0", trade.getRemark());
					ts.start();
				}
				if("4165".equals((trade.getOrderNo()).substring(0,4))){
					TemporarySynThread ts=new TemporarySynThread("http://www.youkutuan.com/do.php?act=charge_okok",trade.getMerchantOrderNo(), "0", trade.getRemark());
					ts.start();
				}
				if("3918".equals((trade.getOrderNo()).substring(0,4))||"4110".equals((trade.getOrderNo()).substring(0,4))){
					TemporarySynThread ts=new TemporarySynThread("http://www.win4mall.com/OrderAutomatic",trade.getMerchantOrderNo(),"0", trade.getRemark());
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
				//shopManagerService.addTemporaryTradInfo(trade.getOrderNo(), year, month,cvv,card.getCountry(),mer.getMd5key(), card.getIp(),"MSIE 10.0","MS"+errMsg);
		 	}
		 trade.setTradeState(buffer.toString());
		 commonService.update(trade);
		 logger.info("**************支付返回码*************"+responseCode);
		 ServletOutputStream out = ServletActionContext.getResponse().getOutputStream();
		 out.print("OK");		 
	 }
	
	public void masapayquery() throws Exception{
		//String sql = "select distinct(internatio1_.orderno) from international_refundmanager internatio0_, INTERNATIONAL_TRADEINFO internatio1_, INTERNATIONAL_MERCHANT internatio2_, international_cardholdersinfo internatio3_ where internatio0_.tradeId=internatio1_.id and internatio1_.merchantId=internatio2_.id and internatio3_.tradeId=internatio1_.id and internatio0_.applydate>=to_date('2018-10-31','yyyy-MM-dd hh24:mi:ss') and internatio2_.merno=4161";
		//String sql = "select distinct(internatio1_.orderno) from international_refundmanager internatio0_, INTERNATIONAL_TRADEINFO internatio1_, INTERNATIONAL_MERCHANT internatio2_ where internatio0_.tradeId=internatio1_.id and internatio1_.merchantId=internatio2_.id and internatio0_.applydate>=to_date('2010-10-31','yyyy-MM-dd hh24:mi:ss') and internatio2_.merno=3674";
		StringBuffer sb = new StringBuffer();	
		String selectquery = "select distinct(ti.orderNo) " + // 1		
		sb
				.append("from International_Tradeinfo ti,"+
						"International_RefundManager rm," +						
						"International_Merchant m," +
						"International_CardholdersInfo c " +
						"where rm.tradeId=ti.id " +
						"and ti.merchantId=m.id " +
						"and c.tradeId=ti.id " +
						"and rm.applydate >=to_date('2018-11-02', 'yyyy-MM-dd hh24:mi:ss') "+
						"and m.merno=4161");	
		
		
		List list = this.commonService.getByList(selectquery);
		logger.info("masapay查询数据条数："+list.size()+"");
		MSqueryorder trade = new MSqueryorder();
		Msqueryorderutil tt = new Msqueryorderutil();

	 	trade.setVersion("1.0");
	 	trade.setMerchantId("801128553113051");
	 	trade.setCharset("utf-8");
	 	trade.setLanguage("en");
	 	trade.setSignType("SHA256");
	 	trade.setQueryType("1");
		for (int i = 0; i < list.size(); i++) {
			 	String sign ="version=1.0&merchantId=801128553113051&charset=utf-8&language=en&signType=SHA256&queryType=1&merchantOrderNo="+list.get(i).toString()+"&key=K_iTBOu~";		 	
			 	trade.setSignMsg(getSha256(sign)); 
			 	trade.setMerchantOrderNo(list.get(i).toString());
			 	String res = tt.get(trade);	
			 	InternationalTradeinfo order = (InternationalTradeinfo) commonService.uniqueResult("from InternationalTradeinfo where orderNo='"+list.get(i).toString()+"'");
			 	
			 	JSONObject json = JSONObject.fromObject(res);
			 	String a = json.getJSONArray("list").getString(0);
			 	JSONObject json2 = JSONObject.fromObject(a);
			 	String b = json2.getString("masapayOrderNo");
			 	//System.out.println(trade.getMerchantOrderNo());
			 	order.setVIPAuthorizationNo(b);
			 	commonService.update(order);
		}
	}
	
	public void masapayref() throws Exception{
		StringBuffer sb = new StringBuffer();	
		String selectquery = "select distinct(rm.id) " + // 1		
		sb
				.append("from International_Tradeinfo ti,"+
						"International_RefundManager rm," +						
						"International_Merchant m," +
						"International_CardholdersInfo c " +
						"where rm.tradeId=ti.id " +
						"and ti.merchantId=m.id " +
						"and c.tradeId=ti.id " +
						"and rm.applydate >=to_date('2018-11-02', 'yyyy-MM-dd hh24:mi:ss') "+
						"and m.merno=4161");	
		
		
		List list = this.commonService.getByList(selectquery);
		logger.info("masapay查询数据条数："+list.size()+"");
		MSrefundorder trade = new MSrefundorder();
		Msrefundorderutil tt = new Msrefundorderutil();

	 	trade.setVersion("1.9");
	 	trade.setMerchantId("801128553113051");
	 	trade.setCharset("utf-8");
	 	trade.setLanguage("en");
	 	trade.setSignType("SHA256");
		for (int i = 0; i < list.size(); i++) {
				
				InternationalRefundManager refund = (InternationalRefundManager) commonService.uniqueResult("from InternationalRefundManager where id='"+list.get(i).toString()+"'");
				InternationalTradeinfo order = (InternationalTradeinfo) commonService.uniqueResult("from InternationalTradeinfo where id='"+refund.getTradeId()+"'");
				trade.setRefundOrderNo(refund.getRefundNo());
				trade.setMerchantOrderNo(order.getOrderNo());
				trade.setMasapayOrderNo(order.getVIPAuthorizationNo());
				//trade.setRefundAmount(refund.getRefundRMBAmount()+"");
				Double amountAndFee=refund.getRefundRMBAmount();
				/*if(refund.getRefundRMBAmount()!=null){
					amountAndFee=amountAndFee*(order.getChannelFee()+1.0);
					amountAndFee = (double) (Math.round((double) amountAndFee * 100) / 100.00);
				 }*/
				trade.setRefundAmount((int)(amountAndFee*100)+"");
				trade.setCurrencyCode("CNY");
				Date currentTime = new Date();
				SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddhhmmss");
				String dateString = formatter.format(currentTime);
				trade.setRefundSubmitTime(dateString);
				trade.setCallbackUrl("http://www.sfepay.com/masapayrefund");
				
				String sign = "version=1.9&merchantId=801128553113051&charset=utf-8&language=en&signType=SHA256&refundOrderNo="+refund.getRefundNo()+"&merchantOrderNo="+order.getOrderNo()+"&masapayOrderNo="+order.getVIPAuthorizationNo()+"&refundAmount="+trade.getRefundAmount()+"&currencyCode=CNY&refundSubmitTime="+dateString+"&callbackUrl=http://www.sfepay.com/masapayrefund&key=K_iTBOu~";

			 	trade.setSignMsg(getSha256(sign)); 
			 	
			 	
			 	String res = tt.get(trade);	
	 	
			 	JSONObject json = JSONObject.fromObject(res);
			 	
			 	//System.out.println(trade.getMerchantOrderNo());
			 	if("10".equals(json.getString("resultCode"))){
			 		refund.setRemark("Success!");
			 	}else{
			 		refund.setRemark(json.getString("errorMsg"));
			 	}
			 	commonService.update(refund);
		}
	}
	
	public void masapayrefund() throws Exception{
		HttpServletRequest request = ServletActionContext.getRequest();
		logger.info("masapay退款异步返回信息:"+resultCode);
		logger.info("merchantOrderNo:"+merchantOrderNo+"*****resultCode:"+resultCode+"*****refundAmount:"+refundAmount+"*****masapayRefundOrderNo"+masapayRefundOrderNo+"*****refundOrderNo"+refundOrderNo);
		//InternationalTradeinfo trade=(InternationalTradeinfo) commonService.uniqueResult("from InternationalTradeinfo where orderNo='"+merchantOrderNo+"'");
		InternationalRefundManager refund = (InternationalRefundManager) commonService.uniqueResult("from InternationalRefundManager where refundNo='"+refundOrderNo.toString()+"'");
		if("10".equals(resultCode)){
			//InternationalTradeinfo trade=(InternationalTradeinfo) commonService.uniqueResult("from InternationalTradeinfo where orderNo='"+merchantOrderNo+"'");
			/*StringBuffer buffer = new StringBuffer(trade.getTradeState());
			buffer.replace(1, 2, "1");*/
			refund.setRemark("refund ss");
		}else{
			refund.setRemark("refund ff"+errMsg.toString());
		}
		commonService.update(refund);
		
		ServletOutputStream out = ServletActionContext.getResponse().getOutputStream();
		out.print("OK");
	}
	
	public void quanqiupay() throws Exception{
		HttpServletRequest request = ServletActionContext.getRequest();
		logger.info("Gofpay异步返回信息:"+request.getParameter("Status"));
		logger.info("TransactionId:"+TransactionId+"OrderId:"+OrderId+"stauts:"+Status+"TransactionAmount:"+TransactionAmount);
		InternationalTradeinfo trade=(InternationalTradeinfo) commonService.uniqueResult("from InternationalTradeinfo where orderNo='"+OrderId.toString()+"'");
		InternationalMerchant mer=(InternationalMerchant) commonService.uniqueResult("from InternationalMerchant where id='"+trade.getMerchantId()+"'");
		InternationalCardholdersInfo card=(InternationalCardholdersInfo) commonService.uniqueResult("from InternationalCardholdersInfo where tradeId='"+trade.getId()+"'");
		
/*		String cardNo=AES.getCarNo(card.getCardNo());		
		String expiryDate = AES.getCarNo(card.getExpiryDate());
		String month = expiryDate.substring(0,2);
		String year = expiryDate.substring(2,4);
		String cvv = AES.getCarNo(card.getCvv2());*/
							
		StringBuffer buffer = new StringBuffer(trade.getTradeState());
		Thread.sleep(2*1000);		

		 	if(Status.equals("Success")){				 		
		 		buffer.replace(0, 1, "1");	
		 		trade.setRemark("Payment Success!");
		 		this.responseCode=88;
		 		/*MD5info = trade.getMerchantOrderNo()
						+ trade.getMoneyType() + ordercountValue
						+ responseCode + MD5key;
				md5Value = md5.getMD5ofStr(MD5info);*/
		 		this.message = "Payment Success!";	
		 		
				if("1001".equals((trade.getOrderNo()).substring(0,4))||"4136".equals((trade.getOrderNo()).substring(0,4))){
					TemporarySynThread ts=new TemporarySynThread("http://www.xingbill.com/synTradeInfo",trade.getMerchantOrderNo(), "1", message);
					ts.start();
				}
				if("4160".equals((trade.getOrderNo()).substring(0,4))||"4161".equals((trade.getOrderNo()).substring(0,4))){
					/*TemporarySynThread ts=new TemporarySynThread("http://www.ipasspay.biz/index.php/Thirdpay/Sfepay/notifyUrl",trade.getMerchantOrderNo(), "1", trade.getRemark());
					ts.start();*/
				 	IPassPayTemporMessage ipass = new IPassPayTemporMessage();
				 	IpassPayTemporary tt = new IpassPayTemporary();
				 	ipass.setOrderNo(trade.getMerchantOrderNo());
				 	ipass.setRes_orderStatus("1");
				 	ipass.setRemark(trade.getRemark());
				 	tt.get(ipass);
				}
				if("4169".equals((trade.getOrderNo()).substring(0,4))){
					TemporarySynThread ts=new TemporarySynThread("http://www.jjqsc.com/PayResult.php",trade.getMerchantOrderNo(), "1", trade.getRemark());
					ts.start();
				}
				if("4165".equals((trade.getOrderNo()).substring(0,4))){
					TemporarySynThread ts=new TemporarySynThread("http://www.youkutuan.com/do.php?act=charge_okok",trade.getMerchantOrderNo(), "1", trade.getRemark());
					ts.start();
				}
				if("3918".equals((trade.getOrderNo()).substring(0,4))||"4110".equals((trade.getOrderNo()).substring(0,4))){
						TemporarySynThread ts = new TemporarySynThread("http://www.win4mall.com/OrderAutomatic", trade.getMerchantOrderNo(), "1", "JIAMEI-EC");
						ts.start();					
				}
				if("4066".equals((trade.getOrderNo()).substring(0,4))||"4216".equals((trade.getOrderNo()).substring(0,4))){
					if("maxmaillots.org".equals(trade.getTradeUrl())){
						TemporarySynThread ts=new TemporarySynThread("http://www.maxmaillots.org/payment_online_feback.php",trade.getMerchantOrderNo(),"1", trade.getRemark());
						ts.start();
					}else{
						TemporarySynThread ts=new TemporarySynThread("http://"+trade.getTradeUrl()+"/payment_online_feback.php",trade.getMerchantOrderNo(),"1", trade.getRemark());
						ts.start();
					}
				}
		 	}else if(Status.equals("Pending")||Status.equals("Processing")){	
		 		String code = String.valueOf(buffer.charAt(0));
		 		if(!"1".equals(code)){
			 		buffer.replace(0, 1, "2");
			 		trade.setRemark("Waiting processing*QP!");
			 		this.responseCode=19;
			 		this.message = "Waiting processing*QP!";	
		 		}
		 	}else{
		 		buffer.replace(0, 1, "0");
		 		trade.setRemark(errMsg);
		 		this.responseCode=0;
		 		this.message = "Payment Declined!"; 
		 		
				if("1001".equals((trade.getOrderNo()).substring(0,4))||"4136".equals((trade.getOrderNo()).substring(0,4))){
					TemporarySynThread ts=new TemporarySynThread("http://www.xingbill.com/synTradeInfo",trade.getMerchantOrderNo(), "0", message);
					ts.start();
				}
				if("4160".equals((trade.getOrderNo()).substring(0,4))||"4161".equals((trade.getOrderNo()).substring(0,4))){
					/*TemporarySynThread ts=new TemporarySynThread("http://www.ipasspay.biz/index.php/Thirdpay/Sfepay/notifyUrl",trade.getMerchantOrderNo(), "0", trade.getRemark());
					ts.start();*/
				 	IPassPayTemporMessage ipass = new IPassPayTemporMessage();
				 	IpassPayTemporary tt = new IpassPayTemporary();
				 	ipass.setOrderNo(trade.getMerchantOrderNo());
				 	ipass.setRes_orderStatus("0");
				 	ipass.setRemark(trade.getRemark());
				 	tt.get(ipass);
				}
				if("4169".equals((trade.getOrderNo()).substring(0,4))){
					TemporarySynThread ts=new TemporarySynThread("http://www.jjqsc.com/PayResult.php",trade.getMerchantOrderNo(), "0", trade.getRemark());
					ts.start();
				}
				if("4165".equals((trade.getOrderNo()).substring(0,4))){
					TemporarySynThread ts=new TemporarySynThread("http://www.youkutuan.com/do.php?act=charge_okok",trade.getMerchantOrderNo(), "0", trade.getRemark());
					ts.start();
				}
				if("3918".equals((trade.getOrderNo()).substring(0,4))||"4110".equals((trade.getOrderNo()).substring(0,4))){
					TemporarySynThread ts=new TemporarySynThread("http://www.win4mall.com/OrderAutomatic",trade.getMerchantOrderNo(),"0", trade.getRemark());
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
				//shopManagerService.addTemporaryTradInfo(trade.getOrderNo(), year, month,cvv,card.getCountry(),mer.getMd5key(), card.getIp(),"MSIE 10.0","MS"+errMsg);
		 	}
		 trade.setTradeState(buffer.toString());
		 commonService.update(trade);
		 logger.info("**************支付返回码*************"+responseCode);
		 ServletOutputStream out = ServletActionContext.getResponse().getOutputStream();
		 out.print("OK");		 
	 }
	
	
	
	
/*	public void doGet(){
		String sql = "select distinct(t.tradewebsite) from International_Webchannels t where merchanid = "+"4705";
		List<Object[]> list = this.commonService.getByList(sql);	
        InputStream instream=null;
        
    try {     
        HttpClient httpclient = new DefaultHttpClient();
		for (int i = 0; i < list.size(); i++) {			
			String url = "https://admin.ftpbill.com/AdMiN/WebSiteServlet?merNo=" + "1426" + "&website=" + list.get(i) ;
			//map.put("webSite", list.get(i));
	           
        //String url = "https://admin.ftpbill.com/AdMiN/WebSiteServlet?merNo=" + "1426" + "&website=" +  ;
			HttpGet getmethod = new HttpGet(url);
		
        
			HttpResponse response = httpclient.execute(getmethod);
		
			HttpEntity entity = response.getEntity();
    
			if (entity != null) {
				System.out.println(EntityUtils.toString(entity,"UTF-8"));
				instream = entity.getContent();
        }
         
        int len;
        byte[] tmp = new byte[2048];
        while ((len = instream.read(tmp)) != -1) {
            System.out.println(tmp.toString());
        }
        }

		} catch (Exception e) {
        e.printStackTrace();
		}finally{
        try {
            instream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    	} 

	}*/	
	
	public String getBillno() {
		return billno;
	}

	public String getResultCode() {
		return resultCode;
	}

	public void setResultCode(String resultCode) {
		this.resultCode = resultCode;
	}

	public String getErrCode() {
		return errCode;
	}

	public void setErrCode(String errCode) {
		this.errCode = errCode;
	}

	public String getErrMsg() {
		return errMsg;
	}

	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}

	public void setBillno(String billno) {
		this.billno = billno;
	}

	public String getSucceed() {
		return succeed;
	}

	public void setSucceed(String succeed) {
		this.succeed = succeed;
	}

	public String getOrderinfo() {
		return orderinfo;
	}

	public void setOrderinfo(String orderinfo) {
		this.orderinfo = orderinfo;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getTransactionid() {
		return transactionid;
	}

	public void setTransactionid(String transactionid) {
		this.transactionid = transactionid;
	}

	public String getOrderid() {
		return orderid;
	}

	public void setOrderid(String orderid) {
		this.orderid = orderid;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getOriginalcurrency() {
		return originalcurrency;
	}

	public void setOriginalcurrency(String originalcurrency) {
		this.originalcurrency = originalcurrency;
	}

	public String getOriginalamount() {
		return originalamount;
	}

	public void setOriginalamount(String originalamount) {
		this.originalamount = originalamount;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public ShopManagerService getShopManagerService() {
		return shopManagerService;
	}

	public void setShopManagerService(ShopManagerService shopManagerService) {
		this.shopManagerService = shopManagerService;
	}

	public String getRespCode() {
		return respCode;
	}

	public void setRespCode(String respCode) {
		this.respCode = respCode;
	}

	public String getRespMsg() {
		return respMsg;
	}

	public void setRespMsg(String respMsg) {
		this.respMsg = respMsg;
	}

	public int getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public CommonService getCommonService() {
		return commonService;
	}


	public void setCommonService(CommonService commonService) {
		this.commonService = commonService;
	}


	public String getMerNo() {
		return merNo;
	}


	public void setMerNo(String merNo) {
		this.merNo = merNo;
	}


	public String getStarttime() {
		return starttime;
	}


	public void setStarttime(String starttime) {
		this.starttime = starttime;
	}


	public String getEndtime() {
		return endtime;
	}


	public void setEndtime(String endtime) {
		this.endtime = endtime;
	}


	public String getOrders() {
		return orders;
	}


	public void setOrders(String orders) {
		this.orders = orders;
	}


	public String getOrderNo() {
		return orderNo;
	}


	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}


	public String getMerchantOrderNo() {
		return merchantOrderNo;
	}


	public void setMerchantOrderNo(String merchantOrderNo) {
		this.merchantOrderNo = merchantOrderNo;
	}


	public String getShippingdate() {
		return shippingdate;
	}


	public void setShippingdate(String shippingdate) {
		this.shippingdate = shippingdate;
	}


	public String getCardtype() {
		return cardtype;
	}


	public void setCardtype(String cardtype) {
		this.cardtype = cardtype;
	}


	public String getEmail() {
		return email;
	}


	public void setEmail(String email) {
		this.email = email;
	}


	public String getIp() {
		return ip;
	}


	public void setIp(String ip) {
		this.ip = ip;
	}


	public String getAmount() {
		return amount;
	}


	public void setAmount(String amount) {
		this.amount = amount;
	}


	public String getCurrencyCode() {
		return currencyCode;
	}


	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}


	public String getTerNo() {
		return terNo;
	}


	public void setTerNo(String terNo) {
		this.terNo = terNo;
	}


	public String getStauts() {
		return stauts;
	}


	public void setStauts(String stauts) {
		this.stauts = stauts;
	}


	public String getIstuikuan() {
		return istuikuan;
	}


	public void setIstuikuan(String istuikuan) {
		this.istuikuan = istuikuan;
	}


	public String getIsjufu() {
		return isjufu;
	}


	public void setIsjufu(String isjufu) {
		this.isjufu = isjufu;
	}


	public String getIshuakuan() {
		return ishuakuan;
	}


	public void setIshuakuan(String ishuakuan) {
		this.ishuakuan = ishuakuan;
	}


	public String getIschuanhao() {
		return ischuanhao;
	}


	public void setIschuanhao(String ischuanhao) {
		this.ischuanhao = ischuanhao;
	}


	public String getTradeurl() {
		return tradeurl;
	}


	public void setTradeurl(String tradeurl) {
		this.tradeurl = tradeurl;
	}


	public String getRemark() {
		return remark;
	}


	public void setRemark(String remark) {
		this.remark = remark;
	}
	
	public String getRefundOrderNo() {
		return refundOrderNo;
	}

	public void setRefundOrderNo(String refundOrderNo) {
		this.refundOrderNo = refundOrderNo;
	}

	public String getMasapayRefundOrderNo() {
		return masapayRefundOrderNo;
	}

	public void setMasapayRefundOrderNo(String masapayRefundOrderNo) {
		this.masapayRefundOrderNo = masapayRefundOrderNo;
	}

	public String getRefundAmount() {
		return refundAmount;
	}

	public void setRefundAmount(String refundAmount) {
		this.refundAmount = refundAmount;
	}

	public String getTransactionId() {
		return TransactionId;
	}

	public void setTransactionId(String transactionId) {
		TransactionId = transactionId;
	}

	public String getOrderId() {
		return OrderId;
	}

	public void setOrderId(String orderId) {
		OrderId = orderId;
	}

	public String getTransactionAmount() {
		return TransactionAmount;
	}

	public void setTransactionAmount(String transactionAmount) {
		TransactionAmount = transactionAmount;
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
}
