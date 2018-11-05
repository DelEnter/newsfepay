package com.ecpss.action.refund;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspWriter;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ecpss.action.BaseAction;
import com.ecpss.model.payment.InternationalCardholdersInfo;
import com.ecpss.model.payment.InternationalTradeinfo;
import com.ecpss.model.refund.InternationalRefundManager;
import com.ecpss.model.shop.InternationalExchangerate;
import com.ecpss.model.shop.InternationalMerchant;
import com.ecpss.service.iservice.RefundService;
import com.ecpss.tools.TableExport;
import com.ecpss.tools.TableExportFactory;
import com.ecpss.util.DownloadUtils;
import com.ecpss.util.GetBatchNo;
import com.ecpss.web.PageInfo;

public class MerRefundAutidingAction extends BaseAction implements ServletRequestAware{
	@Autowired
	@Qualifier("refundService")
	private RefundService refundService;
	
	private List auditingList;
	private List refundDetailList;
	private List list;
	private PageInfo info = new PageInfo();
	private String exportX;
	private Long merchantNo;
	private String orderNo;
	private String cardNo;
	private String startDate;
	private String endDate;
	private String authorizationNo;
	
	private Long [] refundIds;
	private int pagesize;
	private String batchNo;
	public List<InternationalMerchant> merchantList;
	private List<String> terminalNoList;
	private String terminalNo;
	private String refStatus;
	private Double applyRefund;
	/**
	 * 进入审核商户提交的退款记录
	 * @return
	 */
	public String toRefundAud(){
		terminalNoList = this.refundService.getTerminalNoByRefund(2L);
		merchantNo = 3809l;
		StringBuffer sb = new StringBuffer();
		sb.append("select rm,ti,c " +
				"from InternationalRefundManager rm," +
				"InternationalTradeinfo ti," +
				"InternationalMerchant m," +
				"InternationalCardholdersInfo c " +
				"where rm.tradeId=ti.id " +
				"and ti.merchantId=m.id " +
				"and c.tradeId=ti.id " +
				"and rm.refundState=2");  //已提交才可以审核
		if(merchantNo!=null){
			sb.append(" and m.merno="+merchantNo);
		}
		if(StringUtils.isNotBlank(orderNo)){
			sb.append(" and ti.orderNo='"+orderNo.trim()+"' ");
		}
		if(StringUtils.isNotBlank(terminalNo)){
			sb.append(" and ti.VIPTerminalNo='"+terminalNo+"' ");
		}
		if(StringUtils.isNotBlank(cardNo)){
			sb.append(" and c.cardNo='"+cardNo+"' ");
		}
		sb.append(" order by ti.VIPTerminalNo desc,ti.tradeTime ");
		info.setPageSize(500);
		info = this.commonService.listQueryResultByHql(sb.toString(), info);
		//auditingList = refundService.getAuditingRefund();
		return SUCCESS;
	}
	/**
	 * 审核退款
	 * @return
	 */
	public String auditingRefund(){
		for (Long r : refundIds) {
			InternationalRefundManager rm = (InternationalRefundManager) this.commonService.load(InternationalRefundManager.class, r);
			rm.setAuditingDate(new Date());
			rm.setRefundState("3");  //修改成已审核
			rm.setAuditingMan(getUserBean().getUserName());
			this.commonService.update(rm);
		}
		return toRefundAud();
	}
	/**
	 * 生成退款
	 * @return
	 * @throws IOException 
	 */
	public String toCreateRefund() throws IOException{
		StringBuffer hql = new StringBuffer();
		hql.append("select ti.VIPTerminalNo " +
				"from InternationalRefundManager rm," +
				"InternationalTradeinfo ti," +
				"InternationalMerchant m " +
				"where rm.tradeId=ti.id " +
				"and ti.merchantId=m.id " +
				"and rm.refundState in (3,6) " +
						"group by ti.VIPTerminalNo "); 
		terminalNoList = this.commonService.list(hql.toString());
		merchantList = this.refundService.getMerchantList();
		//if(merchantNo!=null || StringUtils.isNotBlank(orderNo) || StringUtils.isNotBlank(exportX)){
			info.setPageSize(pagesize);
			StringBuffer sb = new StringBuffer();
			sb.append("select rm,ti,ci,ic.channelName " +
					"from InternationalRefundManager rm," +
					"InternationalTradeinfo ti," +
					"InternationalMerchant m," +
					"InternationalCardholdersInfo ci,InternationalMerchantChannels mc,InternationalChannels ic " +
					"where rm.tradeId=ti.id " +
					"and ci.tradeId=ti.id " +
					"and ti.merchantId=m.id " +
					"and ti.tradeChannel=mc.id and mc.channelId=ic.id "+
					"and rm.refundState in (3,6) ");  //已提交才可以审核
			if(merchantNo!=null){
				sb.append(" and m.merno="+merchantNo);
			}
			if(StringUtils.isNotBlank(orderNo)){
				sb.append(" and ti.orderNo='"+orderNo.trim()+"' ");
			}
			if(StringUtils.isNotBlank(cardNo)){
				sb.append(" and ci.cardNo='"+cardNo+"' ");
			}
			if(StringUtils.isNotBlank(authorizationNo)){
				sb.append(" and ti.VIPAuthorizationNo='"+authorizationNo+"' ");
			}
			System.out.println(terminalNo);
			if(StringUtils.isNotBlank(terminalNo)){
				sb.append(" and ti.VIPTerminalNo='"+terminalNo+"' ");
			}
				
			sb.append(" order by ti.VIPTerminalNo,ti.tradeTime desc");
			if(exportX!=null && exportX.equals("1")){
				List<Object[]> objList  = commonService.list(sb.toString());
				this.processExport(objList);
				return null;
			}else{
				info = this.commonService.listQueryResultByHql(sb.toString(), info);
			}
	//	}
		return SUCCESS;
	}
	
	private void processExport(List<Object[]> oArray) throws IOException {		
		TableExport export = TableExportFactory.createExcelTableExport();
		export.addTitle(new String[]{"流水号","卡号","交易金额","交易金额RMB","退款金额","退款RMB","交易日期","终端号","授权号"});
		export.setTableName(GetBatchNo.getTime()+"退款记录");
		InternationalRefundManager r = new InternationalRefundManager();
		InternationalCardholdersInfo c = new InternationalCardholdersInfo();
		InternationalTradeinfo t = new InternationalTradeinfo();
		for(Object[] obj:oArray){
			r = (InternationalRefundManager) obj[0];
			t = (InternationalTradeinfo) obj[1];
			c = (InternationalCardholdersInfo) obj[2];
			export.addRow(new Object[]{t.getOrderNo(),getCarNo(c.getCardNo()),t.getTradeAmount(),t.getRmbAmount(),r.getRefundAmount(),r.getRefundRMBAmount(),t.getTradeTime(),t.getVIPTerminalNo(),t.getVIPAuthorizationNo()});	
		}
		OutputStream os = DownloadUtils.getResponseOutput(GetBatchNo.getTime()+"退款记录.xls");
		export.export(os);
		DownloadUtils.closeResponseOutput();
	}
	
	
	/**
	 * 生成退款
	 * @return
	 */
	public String createRefund(){
		GetBatchNo g = new GetBatchNo();
		String batch = g.getBatchNo();
		this.refundService.createRefund(refundIds,batch);
		batchNo = batch;
		return getRefundByBatchNo();
	}
	
	/**
	 * 根据批次号查询退款明细
	 * @return
	 */
	public String getRefundByBatchNo(){
		refundDetailList = this.refundService.getRefundDetailByBatchNo(batchNo,refStatus);
		return SUCCESS;
	}
	/**
	 * 统计出每一个批次号退款记录
	 * @return
	 */
	public String toRefundDispose(){
		//this.refundService.getRefundDispose();
		String hql = "select m.merno,r.batchNo,sum(r.refundAmount),sum(r.refundRMBAmount),count(r.id),r.refundDate "+
				"from InternationalRefundManager r,InternationalTradeinfo ti,InternationalMerchant m "+
				"where r.tradeId=ti.id "+
				"and ti.merchantId=m.id " +
				" ";
		if(merchantNo!=null){
			hql+=" and m.merno="+merchantNo+" ";
		}
		if(StringUtils.isNotBlank(refStatus)&&"0".equals(refStatus)){
			hql+="and r.refundState='7'";
		}
		if((StringUtils.isNotBlank(refStatus)&&"1".equals(refStatus))){
			hql+="and r.refundState in (4,5)";
		}
		if(StringUtils.isBlank(refStatus)){
			hql+="and r.refundState in (4,5,7)";
		}
		if(StringUtils.isNotBlank(orderNo)){
			hql+=" and ti.orderNo='"+orderNo+"' ";
		}
		if(StringUtils.isNotBlank(startDate)){
			hql+=" and r.refundDate>=to_date('"+startDate+"','yyyy-MM-dd') ";
		}
		if(StringUtils.isNotBlank(endDate)){   //结束日期
			hql+=" and r.refundDate<=to_date('"+endDate+"','yyyy-MM-dd') ";
		}
		hql+="group by m.merno,r.batchNo,r.refundDate " +
			"order by r.refundDate desc ";
		info = this.commonService.listQueryResultByHqlGroupBy(hql, info);
		return SUCCESS;
	}
	
	private InternationalTradeinfo tradeinfo=new InternationalTradeinfo();
	private Long tradeId;
	private Double refundAmount;
	/**
	 * 查询单笔退款
	 * @return
	 */
	public String querySingleRefund(){
		if(StringUtils.isNotBlank(orderNo)){
			String sql="select ti from InternationalTradeinfo ti where ti.orderNo='"+orderNo.trim()+"'";
			tradeinfo = (InternationalTradeinfo) this.commonService.uniqueResult(sql);
			List<InternationalRefundManager> rf = refundService.getRefundByTradeId(tradeinfo.getId());
			BigDecimal tradeAmout=new BigDecimal(tradeinfo.getTradeAmount());
			for(int i=0;i<rf.size();i++){
				InternationalRefundManager ref=rf.get(i);
				BigDecimal refundNum=new BigDecimal(ref.getRefundAmount());
				tradeAmout=tradeAmout.subtract(refundNum);
			}
			applyRefund=Double.valueOf(tradeAmout.toString());
		}
		return SUCCESS;
	}
	
	/**
	 * 提交退款申请进入预览表
	 * @return
	 */
	public String singleapplyRefund(){
		try{
			List<InternationalRefundManager> r = refundService.getRefundByTradeId(tradeId);
			InternationalTradeinfo ti = (InternationalTradeinfo) this.commonService.load(InternationalTradeinfo.class, tradeId);
			if(r.size()>0){
				BigDecimal tradeAmount=new BigDecimal(ti.getTradeAmount());
				for(InternationalRefundManager rf:r){
					BigDecimal refundNum=new BigDecimal(rf.getRefundAmount());
					tradeAmount=tradeAmount.subtract(refundNum);
				}
				System.out.println(tradeAmount.doubleValue());
				if(tradeAmount.doubleValue()==0.0||tradeAmount.doubleValue()<=0.0){
					//已经申请过退款
					this.messageAction="Application has been submitted.Please refund check in the view of history";
					return this.querySingleRefund();
				}
			}
			if(ti.getTradeState().substring(2, 3).equals("1")){
				//如果该交易已经拒付,不可以申请退款
				this.messageAction="The transaction has been chargeback.Can not apply for a refund.";
				return this.querySingleRefund();
			}
			InternationalExchangerate er = (InternationalExchangerate) this.commonService.load(InternationalExchangerate.class, ti.getTradeRate());
			InternationalRefundManager rm = new InternationalRefundManager();
			rm.setApplyDate(new Date());
			rm.setLastDate(new Date());
			rm.setRefundState("2");
			rm.setTradeId(tradeId);
			rm.setRefundAmount(refundAmount);
			if(refundAmount==ti.getTradeAmount().doubleValue()){
				rm.setRefundRMBAmount(ti.getRmbAmount());
			}else{
				rm.setRefundRMBAmount(refundAmount*er.getRate());
			}
			this.commonService.save(rm);
			this.messageAction="Submitted successfully, please enter the refund transaction has been submitted to management for confirmation.";
			return this.querySingleRefund();
		}catch(Exception e){
			e.printStackTrace();
			return ERROR;
		}
	}
	
	
	public List getAuditingList() {
		return auditingList;
	}

	public void setAuditingList(List auditingList) {
		this.auditingList = auditingList;
	}

	public PageInfo getInfo() {
		return info;
	}

	public void setInfo(PageInfo info) {
		this.info = info;
	}

	public Long getMerchantNo() {
		return merchantNo;
	}

	public void setMerchantNo(Long merchantNo) {
		this.merchantNo = merchantNo;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	public Long[] getRefundIds() {
		return refundIds;
	}
	public void setRefundIds(Long[] refundIds) {
		this.refundIds = refundIds;
	}
	public int getPagesize() {
		return pagesize;
	}
	public void setPagesize(int pagesize) {
		this.pagesize = pagesize;
	}
	public List getRefundDetailList() {
		return refundDetailList;
	}
	public void setRefundDetailList(List refundDetailList) {
		this.refundDetailList = refundDetailList;
	}
	public String getBatchNo() {
		return batchNo;
	}
	public void setBatchNo(String batchNo) {
		this.batchNo = batchNo;
	}
	public List<String> getTerminalNoList() {
		return terminalNoList;
	}
	public void setTerminalNoList(List<String> terminalNoList) {
		this.terminalNoList = terminalNoList;
	}
	public String getTerminalNo() {
		return terminalNo;
	}
	public void setTerminalNo(String terminalNo) {
		this.terminalNo = terminalNo;
	}
	public String getExportX() {
		return exportX;
	}
	public void setExportX(String exportX) {
		this.exportX = exportX;
	}
	public void setServletRequest(HttpServletRequest arg0) {
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
	public String getCardNo() {
		return cardNo;
	}
	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}
	public RefundService getRefundService() {
		return refundService;
	}
	public void setRefundService(RefundService refundService) {
		this.refundService = refundService;
	}
	public List getList() {
		return list;
	}
	public void setList(List list) {
		this.list = list;
	}
	public String getAuthorizationNo() {
		return authorizationNo;
	}
	public void setAuthorizationNo(String authorizationNo) {
		this.authorizationNo = authorizationNo;
	}
	public List<InternationalMerchant> getMerchantList() {
		return merchantList;
	}
	public void setMerchantList(List<InternationalMerchant> merchantList) {
		this.merchantList = merchantList;
	}
	public InternationalTradeinfo getTradeinfo() {
		return tradeinfo;
	}
	public void setTradeinfo(InternationalTradeinfo tradeinfo) {
		this.tradeinfo = tradeinfo;
	}
	public Long getTradeId() {
		return tradeId;
	}
	public void setTradeId(Long tradeId) {
		this.tradeId = tradeId;
	}
	public Double getRefundAmount() {
		return refundAmount;
	}
	public void setRefundAmount(Double refundAmount) {
		this.refundAmount = refundAmount;
	}
	public String getRefStatus() {
		return refStatus;
	}
	public void setRefStatus(String refStatus) {
		this.refStatus = refStatus;
	}
	public Double getApplyRefund() {
		return applyRefund;
	}
	public void setApplyRefund(Double applyRefund) {
		this.applyRefund = applyRefund;
	}
}
