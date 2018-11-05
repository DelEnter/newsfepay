package com.ecpss.action.tradequery;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import vpn.VpnUtil;

import com.ecpss.action.BaseAction;
import com.ecpss.action.pay.TradeManager;
import com.ecpss.action.pay.tc.XMLGetMessage;
import com.ecpss.model.channel.InternationalChannels;
import com.ecpss.model.payment.InternationalCardholdersInfo;
import com.ecpss.model.payment.InternationalTradeinfo;
import com.ecpss.model.shop.InternationalMerchant;
import com.ecpss.model.shop.InternationalTerminalManager;
import com.ecpss.service.iservice.ChannelService;
import com.ecpss.tools.TableExport;
import com.ecpss.tools.TableExportFactory;
import com.ecpss.util.AES;
import com.ecpss.util.BOCPayResult;
import com.ecpss.util.CCSendMail;
import com.ecpss.util.DownloadUtils;
import com.ecpss.util.EmailInfo;
import com.ecpss.util.MD5;
import com.ecpss.util.StateUtils;
import com.ecpss.util.StringUtil;
import com.ecpss.web.PageInfo;
import com.ecpss.web.VcpUtil;

public class TradeQueryAction extends BaseAction{
	@Autowired
	@Qualifier("tradeManager")
	private TradeManager tradeManager;	
	@Autowired
	@Qualifier("channelService")
	private ChannelService channelService;
	
	private List<InternationalChannels> channelList;
	/**
	 * 分页对象
	 */
	private PageInfo info = new PageInfo();
	
	/**
	 * 查询条件参数
	 */
	private Long merchantno;
	private String orderNo;
	private String merchantOrderNo;
	private String disposeMan;
	private String startDate;
	private String endDate;
	private String isgoudui;
	private String isfreeze;
	private String istuikuan;
	private String ishuakuan;
	private String isProtest;
	private Long channelId;
	private String isresult;
	private String authorizeno;
	private String vipbacthno;
	private String istracking;
	private String terminalno;
	private String tradeurl;//交易网址
	/**
	 * 统计金额
	 */
	private List successfulAmount;
	private List failedAmount;
	private List totalAmount;
	private String isdownload;
	/**
	 * 交易查询显示
	 * @return
	 */
//	public String tradeQuery(){
//		channelList = channelService.getChannelList();
//		StringBuffer sb = new StringBuffer();
//		String selectquery="select ti,m,ci,c " ;
//		sb.append("from InternationalTradeinfo ti," +
//						"InternationalMerchant m," +
//						"InternationalCardholdersInfo ci," +
//						"InternationalMerchantChannels mc," +
//						"InternationalChannels c " +
//				"where ti.merchantId=m.id " +     //交易表商户ID
//				"and ci.tradeId=ti.id " +         //持卡人跟交易表
//				"and ti.tradeChannel=mc.id " +    //交易表商户通道
//				"and mc.channelId=c.id ");       //商户通道的通道
//		if(merchantno!=null){
//			sb.append(" and m.merno="+merchantno);
//		}
//		if(StringUtils.isNotBlank(orderNo)){
//			sb.append(" and ti.orderNo like '"+orderNo.trim()+"%'");
//		}
//		if(StringUtils.isNotBlank(merchantOrderNo)){
//			sb.append(" and ti.merchantOrderNo like '"+merchantOrderNo.trim()+"%'");
//		}
//		if(StringUtils.isNotBlank(disposeMan)){
//			sb.append(" and ti.VIPDisposePorson='"+disposeMan+"'");
//		}
//		if(StringUtils.isNotBlank(startDate)){  //开始日期
//			sb.append(" and ti.tradeTime>=to_date('"+startDate+"','yyyy-MM-dd hh24:mi:ss') ");
//		}
//		if(StringUtils.isNotBlank(endDate)){   //结束日期
//			sb.append(" and ti.tradeTime<=to_date('"+endDate+"','yyyy-MM-dd hh24:mi:ss') ");
//		}
//		if(channelId!=null){   //通道查询
//			sb.append(" and c.id="+channelId);
//		}
//		if(StringUtils.isNotBlank(isgoudui)){   //勾兑情况
//			sb.append(" and substr(ti.tradeState,5,1)='"+isgoudui+"'");
//		}
//		if(StringUtils.isNotBlank(isfreeze)){   //冻结情况
//			sb.append(" and substr(ti.tradeState,4,1)='"+isfreeze+"'");
//		}
//		if(StringUtils.isNotBlank(istuikuan)){   //退款情况
//			sb.append(" and substr(ti.tradeState,2,1)='"+istuikuan+"'");
//		}
//		if(StringUtils.isNotBlank(ishuakuan)){   //划款情况
//			sb.append(" and substr(ti.tradeState,8,1)='"+ishuakuan+"'");
//		}
//		if(StringUtils.isNotBlank(isProtest)){   //拒付情况
//			sb.append(" and substr(ti.tradeState,3,1)='"+isProtest+"'");
//		}
//		if(StringUtils.isNotBlank(isresult)){   //支付情况
//			sb.append(" and substr(ti.tradeState,1,1)='"+isresult+"'");
//		}
//		if(StringUtils.isNotBlank(authorizeno)){   //授权号
//			sb.append(" and ti.VIPAuthorizationNo='"+authorizeno+"'");
//		}
//		if(StringUtils.isNotBlank(vipbacthno)){   //批次号
//			sb.append(" and ti.VIPBatchNo='"+vipbacthno+"'");
//		}
//		String orderby = " order by ti.tradeTime desc";
//		//统计总计金额
//		String totalsql = "select sum(ti.tradeAmount),ti.moneyType "+sb.toString()+" group by ti.moneyType ";
//		String successfulsql = "select sum(ti.tradeAmount),ti.moneyType "+sb.toString()+" and substr(ti.tradeState,1,1)='1' group by ti.moneyType";
//		String failedsql = "select sum(ti.tradeAmount),ti.moneyType "+sb.toString()+" and substr(ti.tradeState,1,1)='0' group by ti.moneyType";
//		totalAmount=  commonService.list(totalsql);
//		successfulAmount= commonService.list(successfulsql);
//		failedAmount= commonService.list(failedsql);
//		info=commonService.listQueryResultByHql(selectquery+sb.toString()+orderby, info);
//		return SUCCESS;
//	}
	//交易查询详情+历史交易查询
	public String tradeQuery(){
		channelList = channelService.getChannelList();
		StringBuffer sb = new StringBuffer();
		String totleCount="select count(*)";
//		String selectquery="select ti,m,ci,c " ;
		String selectquery="SELECT  * FROM (SELECT A.*, ROWNUM RN FROM " +
				"(select ti.orderNo," +
				"ti.merchantOrderNo," +
				"m.merno," +
				"to_char(ti.tradeTime,'yy-mm-dd hh24:mi:ss')," +
				"ti.tradeAmount," +
				"ti.moneyType," +
				"ti.rmbAmount," +
				"c.channelName," +
				"ti.tradeState," +
				"ti.backCount," +
				"ti.isPicture," +
				"ti.isTrackNo," +
				"to_char(ti.VIPDisposeDate,'yy-mm-dd hh24:mi:ss')," +
				"ti.VIPDisposePorson," +
				"ti.VIPAuthorizationNo," +
				"ti.VIPBatchNo," +
				"ti.remark," +
				"ti.VIPTerminalNo," +
				"to_char(ti.applyTime,'yy-mm-dd hh24:mi:ss')," +
				"ti.matterDepict," +
				"ti.beginmoney," +
				"ti.pre_money_rmb," +
				"ti.id,"+
				"ti.tradeurl";
		sb.append(" from International_Tradeinfo ti left join " +
						"International_Merchant  m on ti.merchantId=m.id left join " +
						"International_CardholdersInfo ci on  ci.tradeId=ti.id left join " +
						"International_MerchantChannels mc on ti.tradeChannel=mc.id left join " +
						"International_Channels c on mc.channelId=c.id " +
				"where 1=1" );       //商户通道的通道
		if(merchantno!=null){
			sb.append(" and m.merno="+merchantno);
		}
		if(StringUtils.isNotBlank(orderNo)){
			sb.append(" and ti.orderNo like '"+orderNo.trim()+"%'");
		}
		if(StringUtils.isNotBlank(merchantOrderNo)){
			sb.append(" and ti.merchantOrderNo like '"+merchantOrderNo.trim()+"%'");
		}
		if(StringUtils.isNotBlank(disposeMan)){
			sb.append(" and ti.VIPDisposePorson='"+disposeMan+"'");
		}
		if(StringUtils.isNotBlank(startDate)){  //开始日期
			sb.append(" and ti.tradeTime>=to_date('"+startDate+"','yyyy-MM-dd hh24:mi:ss') ");
		}
		if(StringUtils.isNotBlank(endDate)){   //结束日期
			sb.append(" and ti.tradeTime<=to_date('"+endDate+" 23:59:59"+"','yyyy-MM-dd hh24:mi:ss') ");
		}
		if(channelId!=null){ //通道查询
			sb.append(" and c.id="+channelId);
		}
		
		if(StringUtils.isNotBlank(isgoudui)){   //勾兑情况
			sb.append(" and substr(ti.tradeState,5,1)='"+isgoudui+"'");
		}
		if(StringUtils.isNotBlank(isfreeze)){   //冻结情况
			sb.append(" and substr(ti.tradeState,4,1)='"+isfreeze+"'");
		}
		if(StringUtils.isNotBlank(istuikuan)){   //退款情况
			sb.append(" and substr(ti.tradeState,2,1)='"+istuikuan+"'");
		}
		if(StringUtils.isNotBlank(ishuakuan)){   //划款情况
			sb.append(" and substr(ti.tradeState,8,1)='"+ishuakuan+"'");
		}
		if(StringUtils.isNotBlank(isProtest)){   //拒付情况
			sb.append(" and substr(ti.tradeState,3,1)='"+isProtest+"'");
		}
		if(StringUtils.isNotBlank(isresult)){   //支付情况
			sb.append(" and substr(ti.tradeState,1,1)='"+isresult+"'");
		}
		if(StringUtils.isNotBlank(authorizeno)){   //授权号
			sb.append(" and ti.VIPAuthorizationNo='"+authorizeno+"'");
		}
		if(StringUtils.isNotBlank(vipbacthno)){   //批次号
			sb.append(" and ti.VIPBatchNo='"+vipbacthno+"'");
		}
		if(StringUtils.isNotBlank(terminalno)){   //批次号
			sb.append(" and ti.VIPTerminalNo='"+terminalno.trim()+"'");
		}
		if(StringUtils.isNotBlank(tradeurl)){   //交易网址
			sb.append(" and ti.tradeurl='"+tradeurl+"'");
		}
		if(StringUtils.isNotBlank(istracking)){   //批次号
			if(istracking.equals("2")){
				sb.append(" and ti.isTrackNo is null ");
			}else{
				sb.append(" and ti.isTrackNo is not null ");
			}
		}
		sb.append(" order by ti.tradeTime desc");
//	    sb.append(")  A WHERE ROWNUM <= "+(startResult+1)*maxResult+")WHERE RN >= " +(startResult)*maxResult+"");	
	   String listinfo= ")  A WHERE ROWNUM <= "+(info.getCurPage())*info.getPageSize()+")WHERE RN > " +(info.getCurPage()-1)*info.getPageSize()+"";		    
		//String orderby = " order by ti.tradeTime desc";
		StringBuffer sb2 = new StringBuffer();	
		
		sb2.append("from InternationalTradeinfo ti," +
		"InternationalMerchant m," +
		"InternationalCardholdersInfo ci," +
		"InternationalMerchantChannels mc," +
		"InternationalChannels c " +
		"where ti.merchantId=m.id " +     //交易表商户ID
		"and ci.tradeId=ti.id " +         //持卡人跟交易表
		"and ti.tradeChannel=mc.id " +    //交易表商户通道
		"and mc.channelId=c.id ");       //商户通道的通道
		if(merchantno!=null){
		sb2.append(" and m.merno="+merchantno);
		}
		if(StringUtils.isNotBlank(orderNo)){
		sb2.append(" and ti.orderNo like '"+orderNo.trim()+"%'");
		}
		if(StringUtils.isNotBlank(merchantOrderNo)){
		sb2.append(" and ti.merchantOrderNo like '"+merchantOrderNo.trim()+"%'");
		}
		if(StringUtils.isNotBlank(disposeMan)){
		sb2.append(" and ti.VIPDisposePorson='"+disposeMan+"'");
		}
		if(StringUtils.isNotBlank(startDate)){  //开始日期
		sb2.append(" and ti.tradeTime>=to_date('"+startDate+"','yyyy-MM-dd hh24:mi:ss') ");
		}
		if(StringUtils.isNotBlank(endDate)){   //结束日期
		sb2.append(" and ti.tradeTime<=to_date('"+endDate+" 23:59:59','yyyy-MM-dd hh24:mi:ss') ");
		}
		if(channelId!=null){   //通道查询
		sb2.append(" and c.id="+channelId);
		}
		if(StringUtils.isNotBlank(isgoudui)){   //勾兑情况
		sb2.append(" and substr(ti.tradeState,5,1)='"+isgoudui+"'");
		}
		if(StringUtils.isNotBlank(tradeurl)){   //交易网址
			sb2.append(" and ti.tradeUrl='"+tradeurl+"'");
		}
		if(StringUtils.isNotBlank(isfreeze)){   //冻结情况
		sb2.append(" and substr(ti.tradeState,4,1)='"+isfreeze+"'");
		}
		if(StringUtils.isNotBlank(istuikuan)){   //退款情况
		sb2.append(" and substr(ti.tradeState,2,1)='"+istuikuan+"'");
		}
		if(StringUtils.isNotBlank(ishuakuan)){   //划款情况
		sb2.append(" and substr(ti.tradeState,8,1)='"+ishuakuan+"'");
		}
		if(StringUtils.isNotBlank(isProtest)){   //拒付情况
		sb2.append(" and substr(ti.tradeState,3,1)='"+isProtest+"'");
		}
		if(StringUtils.isNotBlank(isresult)){   //支付情况
		sb2.append(" and substr(ti.tradeState,1,1)='"+isresult+"'");
		}
		if(StringUtils.isNotBlank(authorizeno)){   //授权号
		sb2.append(" and ti.VIPAuthorizationNo='"+authorizeno+"'");
		}
		if(StringUtils.isNotBlank(vipbacthno)){   //批次号
		sb2.append(" and ti.VIPBatchNo='"+vipbacthno+"'");
		}		
		if(StringUtils.isNotBlank(terminalno)){   //批次号
			sb2.append(" and ti.VIPTerminalNo='"+this.terminalno.trim()+"'");
		}		
				
		
		
		if(StringUtils.isNotBlank(this.query) && this.query.equals("0")){
			query = "1";
		}else{
			Long begin=System.currentTimeMillis();
			//统计总计金额
			String totalsql = "select sum(ti.tradeAmount),ti.moneyType "+sb2.toString()+" group by ti.moneyType ";
			String successfulsql = "select sum(ti.tradeAmount),ti.moneyType "+sb2.toString()+" and substr(ti.tradeState,1,1)='1' group by ti.moneyType";
			String failedsql = "select sum(ti.tradeAmount),ti.moneyType "+sb2.toString()+" and substr(ti.tradeState,1,1)='0' group by ti.moneyType";
			totalAmount=  commonService.list(totalsql);
			successfulAmount= commonService.list(successfulsql);
			failedAmount= commonService.list(failedsql);
		     List	list=this.commonService.getByList(selectquery+sb.toString()+listinfo);
		    info.setTotalCount(this.tradeManager.intBySql(totleCount+sb.toString()));
		    info.setResult(list);
		    Long end=System.currentTimeMillis() - begin;
		    System.out.println("查询交易时间"+end);
		}
		return SUCCESS;
	}	
	
	/**
	 * 权限不足的人看到的交易查询页面（交易查询）
	 * @return
	 */
	public String tradeQueryBy(){
		channelList = channelService.getChannelList();
		StringBuffer sb = new StringBuffer();
		String totleCount="select count(*)";
//		String selectquery="select ti,m,ci,c " ;
		String selectquery="SELECT  * FROM (SELECT A.*, ROWNUM RN FROM (select ti.orderNo,ti.merchantOrderNo,m.merno,to_char(ti.tradeTime,'yy-mm-dd hh24:mi:ss'),ti.tradeAmount,ti.moneyType,ti.rmbAmount,c.channelName,ti.tradeState,ti.backCount,ti.isPicture,ti.isTrackNo,to_char(ti.VIPDisposeDate,'yy-mm-dd hh24:mi:ss'),ti.VIPDisposePorson,ti.VIPAuthorizationNo,ti.VIPBatchNo,ti.remark,ti.VIPTerminalNo,to_char(ti.applyTime,'yy-mm-dd hh24:mi:ss'),ti.matterDepict,ti.beginmoney,ti.pre_money_rmb,ti.tradeurl,ci.cardNo,ci.shippingFullName,ci.email,ci.address,ci.ip,ci.shippingPhone";
		String downquery="select ti.orderNo,ti.merchantOrderNo,m.merno,to_char(ti.tradeTime,'yy-mm-dd hh24:mi:ss'),ti.tradeAmount,ti.moneyType,ti.rmbAmount,c.channelName,ti.tradeState,ti.backCount,ti.isPicture,ti.isTrackNo,to_char(ti.VIPDisposeDate,'yy-mm-dd hh24:mi:ss'),ti.VIPDisposePorson,ti.VIPAuthorizationNo,ti.VIPBatchNo,ti.remark,ti.VIPTerminalNo,to_char(ti.applyTime,'yy-mm-dd hh24:mi:ss'),ti.matterDepict,ti.beginmoney,ti.pre_money_rmb,ti.tradeurl,ci.cardNo,ci.shippingFullName,ci.email,ci.address,ci.ip,ci.shippingPhone,ci.country";
		sb.append(" from International_Tradeinfo ti left join " +
						"International_Merchant  m on ti.merchantId=m.id left join " +
						"International_CardholdersInfo ci on  ci.tradeId=ti.id left join " +
						"International_MerchantChannels mc on ti.tradeChannel=mc.id left join " +
						"International_Channels c on mc.channelId=c.id " +
				"where 1=1" );       //商户通道的通道
		if(merchantno!=null){
			sb.append(" and m.merno="+merchantno);
		}
		if(StringUtils.isNotBlank(orderNo)){
			sb.append(" and ti.orderNo like '"+orderNo.trim()+"%'");
		}
		if(StringUtils.isNotBlank(merchantOrderNo)){
			sb.append(" and ti.merchantOrderNo like '"+merchantOrderNo.trim()+"%'");
		}
		if(StringUtils.isNotBlank(disposeMan)){
			sb.append(" and ti.VIPDisposePorson='"+disposeMan+"'");
		}
		if(StringUtils.isNotBlank(startDate)){  //开始日期
			sb.append(" and ti.tradeTime>=to_date('"+startDate+"','yyyy-MM-dd hh24:mi:ss') ");
		}
		if(StringUtils.isNotBlank(endDate)){   //结束日期
			sb.append(" and ti.tradeTime<=to_date('"+endDate+"23:59:59"+"','yyyy-MM-dd hh24:mi:ss') ");
		}
		if(channelId!=null){   //通道查询
			sb.append(" and c.id="+channelId);
		}
		if(StringUtils.isNotBlank(isgoudui)){   //勾兑情况
			sb.append(" and substr(ti.tradeState,5,1)='"+isgoudui+"'");
		}
		if(StringUtils.isNotBlank(isfreeze)){   //冻结情况
			sb.append(" and substr(ti.tradeState,4,1)='"+isfreeze+"'");
		}
		if(StringUtils.isNotBlank(tradeurl)){   //交易网址
			sb.append(" and ti.tradeurl='"+tradeurl+"'");
		}
		if(StringUtils.isNotBlank(istuikuan)){   //退款情况
			sb.append(" and substr(ti.tradeState,2,1)='"+istuikuan+"'");
		}
		if(StringUtils.isNotBlank(ishuakuan)){   //划款情况
			sb.append(" and substr(ti.tradeState,8,1)='"+ishuakuan+"'");
		}
		if(StringUtils.isNotBlank(isProtest)){   //拒付情况
			sb.append(" and substr(ti.tradeState,3,1)='"+isProtest+"'");
		}
		if(StringUtils.isNotBlank(isresult)){   //支付情况
			sb.append(" and substr(ti.tradeState,1,1)='"+isresult+"'");
		}
		if(StringUtils.isNotBlank(authorizeno)){   //授权号
			sb.append(" and ti.VIPAuthorizationNo='"+authorizeno+"'");
		}
		if(StringUtils.isNotBlank(vipbacthno)){   //批次号
			sb.append(" and ti.VIPBatchNo='"+vipbacthno+"'");
		}
		if(StringUtils.isNotBlank(istracking)){   //批次号
			if(istracking.equals("2")){
				sb.append(" and ti.isTrackNo is null ");
			}else{
				sb.append(" and ti.isTrackNo is not null ");
			}
		}
		sb.append(" and substr(ti.tradeState,1,1)!=3 ");
		sb.append(" order by ti.tradeTime desc");
//	    sb.append(")  A WHERE ROWNUM <= "+(startResult+1)*maxResult+")WHERE RN >= " +(startResult)*maxResult+"");	
	   String listinfo= ")  A WHERE ROWNUM <= "+(info.getCurPage())*info.getPageSize()+")WHERE RN > " +(info.getCurPage()-1)*info.getPageSize()+"";		    
		String orderby = " order by ti.tradeTime desc";
		StringBuffer sb2 = new StringBuffer();	
		
		sb2.append("from InternationalTradeinfo ti," +
		"InternationalMerchant m," +
		"InternationalCardholdersInfo ci," +
		"InternationalMerchantChannels mc," +
		"InternationalChannels c " +
		"where ti.merchantId=m.id " +     //交易表商户ID
		"and ci.tradeId=ti.id " +         //持卡人跟交易表
		"and ti.tradeChannel=mc.id " +    //交易表商户通道
		"and mc.channelId=c.id ");       //商户通道的通道
		if(merchantno!=null){
		sb2.append(" and m.merno="+merchantno);
		}
		if(StringUtils.isNotBlank(orderNo)){
		sb2.append(" and ti.orderNo like '"+orderNo.trim()+"%'");
		}
		if(StringUtils.isNotBlank(merchantOrderNo)){
		sb2.append(" and ti.merchantOrderNo like '"+merchantOrderNo.trim()+"%'");
		}
		if(StringUtils.isNotBlank(disposeMan)){
		sb2.append(" and ti.VIPDisposePorson='"+disposeMan+"'");
		}
		if(StringUtils.isNotBlank(startDate)){  //开始日期
		sb2.append(" and ti.tradeTime>=to_date('"+startDate+"','yyyy-MM-dd hh24:mi:ss') ");
		}
		if(StringUtils.isNotBlank(endDate)){   //结束日期
		sb2.append(" and ti.tradeTime<=to_date('"+endDate+"','yyyy-MM-dd hh24:mi:ss') ");
		}
		if(channelId!=null){   //通道查询
		sb2.append(" and c.id="+channelId);
		}
		if(StringUtils.isNotBlank(isgoudui)){   //勾兑情况
		sb2.append(" and substr(ti.tradeState,5,1)='"+isgoudui+"'");
		}
		if(StringUtils.isNotBlank(isfreeze)){   //冻结情况
		sb2.append(" and substr(ti.tradeState,4,1)='"+isfreeze+"'");
		}
		if(StringUtils.isNotBlank(istuikuan)){   //退款情况
		sb2.append(" and substr(ti.tradeState,2,1)='"+istuikuan+"'");
		}
		if(StringUtils.isNotBlank(ishuakuan)){   //划款情况
		sb2.append(" and substr(ti.tradeState,8,1)='"+ishuakuan+"'");
		}
		if(StringUtils.isNotBlank(isProtest)){   //拒付情况
		sb2.append(" and substr(ti.tradeState,3,1)='"+isProtest+"'");
		}
		if(StringUtils.isNotBlank(isresult)){   //支付情况
		sb2.append(" and substr(ti.tradeState,1,1)='"+isresult+"'");
		}
		if(StringUtils.isNotBlank(tradeurl)){   //交易网址
			sb2.append(" and ti.tradeurl='"+tradeurl+"'");
		}
		if(StringUtils.isNotBlank(authorizeno)){   //授权号
		sb2.append(" and ti.VIPAuthorizationNo='"+authorizeno+"'");
		}
		if(StringUtils.isNotBlank(vipbacthno)){   //批次号
		sb2.append(" and ti.VIPBatchNo='"+vipbacthno+"'");
		}		
		if(StringUtils.isNotBlank(istracking)){   //批次号
			if(istracking.equals("2")){
				sb2.append(" and ti.isTrackNo is null ");
			}else{
				sb2.append(" and ti.isTrackNo is not null ");
			}
		}
		sb2.append(" and substr(ti.tradeState,1,1) != '3' ");
		if(StringUtils.isNotBlank(this.query) && this.query.equals("0")){
			query = "1";
		}else{
			if(merchantno!=null || StringUtils.isNotBlank(orderNo) || StringUtils.isNotBlank(merchantOrderNo) || StringUtils.isNotBlank(startDate) || StringUtils.isNotBlank(endDate)){
				if (StringUtils.isNotBlank(isdownload)) {
					List objList=this.commonService.getByList(downquery+sb.toString());
					this.downloadTradeQuery(objList);
					return null;
				}
				List list=this.commonService.getByList(selectquery+sb.toString()+listinfo);
			    info.setTotalCount(this.tradeManager.intBySql(totleCount+sb.toString()));
			    info.setResult(list);
			     
			}
		}
		return SUCCESS;
	}

	
	private String tradeid;
	public String processTrade(){
		

		StringBuffer logstr = new StringBuffer();
		String hql = "select t from InternationalTradeinfo t where t.id='"
				+ tradeid + "'";
		InternationalTradeinfo trade = new InternationalTradeinfo();
		List<InternationalTradeinfo> tradelist = this.commonService.list(hql);
		if (tradelist.size() > 0) {
			trade = tradelist.get(0);
			String chql = "select t from InternationalCardholdersInfo t where t.tradeId='"
					+ trade.getId() + "'";
			InternationalCardholdersInfo cardinfo = new InternationalCardholdersInfo();
			List<InternationalCardholdersInfo> cardlist = this.commonService
					.list(chql);
			if (cardlist.size() > 0) {
				cardinfo = cardlist.get(0);
				String cardNo; // 持卡人卡号
				String cardCVV2; // 持卡人cvv2
				String cardExDate; // 持卡人信用卡有效期
				String tradeOrderNo; // 交易流水号
				Double tradeAmount; // 交易RMB金额
				String tradeRMBAmount; // 交易RMB金额
				String posNumber; // 终端号
				String posMerchantNo;// 商户号
				String posBillingAdd; // 账单地址
				String tradeState; // 交易状态
				String cardhorderEmail; // 持卡人邮箱
				String tradeUrl;// 交易地址
				Date tradeTime;// 交易日期
				String merchantOrderNo; // 商户订单

				// tradeId = (BigDecimal) o[0];
				if (cardinfo.getCardNo().toString().trim().length() > 17) {
					cardNo = AES.getCarNo(cardinfo.getCardNo()
							.toString().trim());
				} else {
					cardNo = cardinfo.getCardNo().toString().trim();
				}
				if (cardinfo.getCvv2().toString().trim().length() > 3) {
					cardCVV2 = AES.getCarNo(cardinfo.getCvv2()
							.toString().trim());
				} else {
					cardCVV2 = cardinfo.getCvv2().toString().trim();
				}
				if (cardinfo.getExpiryDate().toString().trim().length() > 4) {
					cardExDate = AES.getCarNo(cardinfo.getExpiryDate()
							.toString().trim());
				} else {
					cardExDate = cardinfo.getExpiryDate().toString()
							.trim();
				}
				if (trade.getTradeState().startsWith("2")) {
					///连接ECPSS系统   s-to-s模式支付
			        java.text.DateFormat format2 = new java.text.SimpleDateFormat("yyyyMMddhhmmss");
			        String dateTime = format2.format(trade.getTradeTime());
			        MD5 md5 = new MD5();
					String md5src;  //加密字符串    
					String ReturnURL="http://www.sfepay.com";
				    md5src = "1002" + trade.getOrderNo()+"-1" + "3" + trade.getRmbAmount() + "en" + ReturnURL + dateTime + "sfepaymd";
				    String MD5info_; //MD5加密后的字符串
				    MD5info_ = md5.getMD5ofStr(md5src);
					String returnurl="https://security.sslepay.com/evippayment";
					URL url;
					System.out.println("post网址+++++++++++"+returnurl);
					try {
						url = new URL(returnurl);
						
						URLConnection connection = url.openConnection(); 
						connection.setDoOutput(true); 
						OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream(), "8859_1");
						String parte = 
						"cardnum="+cardNo+
						"&cvv2="+cardCVV2+
						"&month="+cardExDate.substring(0, 2)+
						"&year="+cardExDate.substring(2, 4)+
						"&cardbank=bank"+
						
						"&MerNo="+"1002"+
						"&BillNo="+trade.getOrderNo()+"-1"+
						"&Amount="+trade.getRmbAmount()+
						"&Currency="+"3"+
						"&email="+cardinfo.getEmail()+
						"&Language="+"en"+
						"&MD5info="+MD5info_+
						
						"&dateTime="+dateTime+
						"&ip="+cardinfo.getIp()+
						"&firstname="+cardinfo.getFirstName()+
						"&lastname="+cardinfo.getLastName()+
						"&phone="+cardinfo.getPhone()+
						"&zipcode="+cardinfo.getZipcode()+
						"&address="+cardinfo.getAddress()+
						"&city="+cardinfo.getCity()+
						"&state="+cardinfo.getState()+
						"&country="+cardinfo.getCountry()+
						"&products="+cardinfo.getProductInfo()+
						
						
						"&ReturnURL="+ReturnURL;
						
						out.write(parte); //这里组织提交信息 
						out.flush(); 
						out.close(); 
						//获取返回数据 
						BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream())); 
						String line = null; 
						StringBuffer content= new StringBuffer(); 
						while((line = in.readLine()) != null) 
						{ 
						   //line为返回值，这就可以判断是否成功、 
						    content.append(line);
						    System.out.println(content);
						} 
						Map responseFields = StringUtil.createMapFromResponse(content.toString().trim());
						// 金额:
						//String PaymentOrderNo = VcpUtil.null2unknown("PaymentOrderNo",responseFields);
						String Succeed = VcpUtil.null2unknown("Succeed",responseFields);
						String paymentmessage = VcpUtil.null2unknown("Result",responseFields);
						String billaddress = VcpUtil.null2unknown("BillingAddress",responseFields);
						if(Succeed.equals("88")){
			    			//支付成功
			    			String message = "Payment Success!";
							this.commonService
									.deleteBySql("update  international_tradeinfo t  set t.tradestate='1'||substr(t.tradestate,2,(length(t.tradestate)-1)),t.remark='"
											+ message
											+ "' where t.id='"
											+ trade.getId()
											+ "'");
							this.messageAction=message;
			    		}else if(Succeed.equals("CN")){
			    			this.messageAction="CN";
			    			//return this.OPERATE_SUCCESS;
			    		}else{
			    			String message = "Payment Declined!"+Succeed;
							this.commonService
									.deleteBySql("update  international_tradeinfo t  set t.tradestate='0'||substr(t.tradestate,2,(length(t.tradestate)-1)),t.remark='"
											+ paymentmessage
											+ "'  where t.id='"
											+ trade.getId()
											+ "'");
							this.messageAction=message;
							//return this.OPERATE_SUCCESS;
			    		}
						
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						String message = "Your payment is being processed";
						this.commonService
								.deleteBySql("update  international_tradeinfo t  set t.tradestate='0'||substr(t.tradestate,2,(length(t.tradestate)-1)) where t.id='"
										+ trade.getId() + "'");
						try {
							CCSendMail.setSendMail("89610614@qq.com", "sfepay can not connect to ecpss", "sfepay@sfepay.com");
						} catch (Exception ea) {
							return this.OPERATE_SUCCESS;
						}
						return this.OPERATE_SUCCESS;
					}
					
				
					
				} else {
					this.messageAction = "has process.";
				}
			} else {
				this.messageAction = "not found trade.";
			}
		} else {
			this.messageAction = "not found trade.";
		}
		this.messageAction = messageAction + logstr.toString();
		return this.OPERATE_SUCCESS;
	
	}
	public void downloadTradeQuery(List<Object[]> oArray) {
		TableExport export = TableExportFactory.createExcelTableExport();
		export.addTitle(new String[] { "", "流水号",
				"商户订单号", "商户号", "交易日期", "交易金额",
				"交易币种", "通道", "支付情况", "是否勾兑", "是否退款",
				"退款金额", "是否冻结", "是否划款", "是否拒付",
				"是否传图", "是否传号", "处理时间", "处理人", "授权号", "批次号",
				"终端号", "交易网址", "备注", "划款申请时间","原始金额","卡号","姓名","邮箱","地址","IP","电话","国家"});
		export.setTableName("Trade");
		int i = 1;
		for (Object[] obj : oArray) {
			String tuString="";
			if(obj[10]!=null){
				tuString="是";
			}else {
				tuString="否";
			}
			export.addRow(new Object[] {
					i,
					obj[0].toString(),
					obj[1].toString(),
					obj[2].toString(),
					obj[3].toString(),
					obj[4].toString(),
					getStates().getCurrencyTypeByNo(Integer.valueOf(obj[5].toString())),
					obj[7].toString(),
					getStates().getStateNameByClass(obj[8].toString(),1),
					getStates().getStateNameByClass(obj[8].toString(),5),
					getStates().getStateNameByClass(obj[8].toString(),2),
					obj[9].toString(),
					getStates().getStateNameByClass(obj[8].toString(),4),
					getStates().getStateNameByClass(obj[8].toString(),8),
					getStates().getStateNameByClass(obj[8].toString(),3),
					tuString,
					obj[11],
					obj[12],
					obj[13],
					obj[14],
					obj[15],
					obj[17],
					obj[22],
					obj[16],
					obj[18],
					obj[20],AES.getCarNo(obj[23].toString().trim()),obj[24],obj[25],obj[26],obj[27],obj[28],obj[29]});
			
			i++;
		}
		OutputStream os = DownloadUtils.getResponseOutput("Trade.xls");
		export.export(os);
		DownloadUtils.closeResponseOutput();
	}
	
	
	
	public PageInfo getInfo() {
		return info;
	}


	public void setInfo(PageInfo info) {
		this.info = info;
	}


	public ChannelService getChannelService() {
		return channelService;
	}


	public void setChannelService(ChannelService channelService) {
		this.channelService = channelService;
	}


	public List<InternationalChannels> getChannelList() {
		return channelList;
	}


	public void setChannelList(List<InternationalChannels> channelList) {
		this.channelList = channelList;
	}


	public Long getMerchantno() {
		return merchantno;
	}


	public void setMerchantno(Long merchantno) {
		this.merchantno = merchantno;
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


	public String getDisposeMan() {
		return disposeMan;
	}


	public void setDisposeMan(String disposeMan) {
		this.disposeMan = disposeMan;
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


	public String getIsgoudui() {
		return isgoudui;
	}


	public void setIsgoudui(String isgoudui) {
		this.isgoudui = isgoudui;
	}


	public String getIsfreeze() {
		return isfreeze;
	}


	public void setIsfreeze(String isfreeze) {
		this.isfreeze = isfreeze;
	}


	public String getIstuikuan() {
		return istuikuan;
	}


	public void setIstuikuan(String istuikuan) {
		this.istuikuan = istuikuan;
	}


	public String getIshuakuan() {
		return ishuakuan;
	}


	public void setIshuakuan(String ishuakuan) {
		this.ishuakuan = ishuakuan;
	}
	
	public String getTradeurl() {
		return tradeurl;
	}

	public void setTradeurl(String tradeurl) {
		this.tradeurl = tradeurl;
	}

	public String getIsProtest() {
		return isProtest;
	}


	public void setIsProtest(String isProtest) {
		this.isProtest = isProtest;
	}


	public Long getChannelId() {
		return channelId;
	}


	public void setChannelId(Long channelId) {
		this.channelId = channelId;
	}


	public String getIsresult() {
		return isresult;
	}


	public void setIsresult(String isresult) {
		this.isresult = isresult;
	}


	public String getAuthorizeno() {
		return authorizeno;
	}


	public void setAuthorizeno(String authorizeno) {
		this.authorizeno = authorizeno;
	}


	public String getVipbacthno() {
		return vipbacthno;
	}


	public void setVipbacthno(String vipbacthno) {
		this.vipbacthno = vipbacthno;
	}


	public List getSuccessfulAmount() {
		return successfulAmount;
	}


	public void setSuccessfulAmount(List successfulAmount) {
		this.successfulAmount = successfulAmount;
	}


	public List getFailedAmount() {
		return failedAmount;
	}


	public void setFailedAmount(List failedAmount) {
		this.failedAmount = failedAmount;
	}


	public List getTotalAmount() {
		return totalAmount;
	}


	public void setTotalAmount(List totalAmount) {
		this.totalAmount = totalAmount;
	}

	public String getIstracking() {
		return istracking;
	}

	public void setIstracking(String istracking) {
		this.istracking = istracking;
	}

	public String getTerminalno() {
		return terminalno;
	}

	public void setTerminalno(String terminalno) {
		this.terminalno = terminalno;
	}

	public String getTradeid() {
		return tradeid;
	}

	public void setTradeid(String tradeid) {
		this.tradeid = tradeid;
	}

	public String getIsdownload() {
		return isdownload;
	}

	public void setIsdownload(String isdownload) {
		this.isdownload = isdownload;
	}

	
	
	
}
