package com.ecpss.action.merchant;

import java.io.OutputStream;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ecpss.action.BaseAction;
import com.ecpss.model.payment.InternationalCardholdersInfo;
import com.ecpss.model.payment.InternationalTradeinfo;
import com.ecpss.model.refund.InternationalRefundManager;
import com.ecpss.model.shop.InternationalMerchant;
import com.ecpss.service.iservice.MerchantManagerService;
import com.ecpss.tools.TableExport;
import com.ecpss.tools.TableExportFactory;
import com.ecpss.util.DownloadUtils;
import com.ecpss.web.PageInfo;

public class AgentsMerTardeQueryAction extends BaseAction{
	@Autowired
	@Qualifier("merchantManagerService")
	private MerchantManagerService merchantManagerService;
	/**
	 * ��ҳ��ǩʹ�ö���
	 */
	private PageInfo info = new PageInfo();
	/**
	 * ��ȡ�̻������ܽ��ͳ��list
	 */
	private List amountStatistic;
	/**
	 * ����ʹ���ж�
	 */
	private String isdownload;
	/**
	 * �̻���id
	 */
	private Long merchantId;
	/**
	 * ��ѯ���ô����µ������̻����б�
	 */
	private List<InternationalMerchant> meridList;
	//********���ײ�ѯ����******/
	private String startDate;
	private String endDate;
	private String orderNo;
	private String merchantOrderNo;
	private String isresult;
	private String istuikuan;
	private String isdongjie;                                  
	private String isjufu;
	private String ishuakuan;
	private String isgoudui;
	private String tradeUrl;
	
	/**
	 * �̻���̨���ײ�ѯ
	 * @return
	 */
	public String agentsTradeQuery(){
		meridList = merchantManagerService.getMerchantNoByAgents(getMerchantBean().getMerChantNo());
		StringBuffer sb = new StringBuffer();
		String select = "select ti,m.merno,ci.email,c.channelName,ci.address,ci.shippingAddress ";
		sb.append("from InternationalTradeinfo ti," +
						"InternationalMerchant m," +
						"InternationalCardholdersInfo ci," +
						"InternationalMerchantChannels mc," +
						"InternationalChannels c " +
				"where ti.merchantId=m.id " +     //���ױ��̻�ID
				"and ci.tradeId=ti.id " +         //�ֿ��˸����ױ�
				"and ti.tradeChannel=mc.id " +    //���ױ��̻�ͨ��
				"and mc.channelId=c.id ");      //�̻�ͨ����ͨ��
		if(merchantId==null){
			sb.append("and m.id in (select am.merchantId " +
				"from InternationalAgentsMerchant am " +
				"where am.agentsMerchantNo="+getMerchantBean().getMerChantNo()+" " +
						") "); 
		}else{
			sb.append("and m.id="+merchantId);
		}
		if(StringUtils.isNotBlank(startDate)){
			sb.append(" and ti.tradeTime>=to_date('"+startDate+" 00:00:00','yyyy-MM-dd hh24:mi:ss') ");
		}
		if(StringUtils.isNotBlank(endDate)){
			sb.append(" and ti.tradeTime<=to_date('"+endDate+" 23:59:59','yyyy-MM-dd hh24:mi:ss') ");
		}
		if(StringUtils.isNotBlank(orderNo)){
			sb.append(" and ti.orderNo like '"+orderNo+"%'");
		}
		if(StringUtils.isNotBlank(merchantOrderNo)){
			sb.append(" and ti.merchantOrderNo like '"+merchantOrderNo+"%'");
		}
		if (StringUtils.isNotBlank(isdongjie)) { // �������
			sb.append(" and substr(ti.tradeState,4,1)='" + isdongjie + "'");
		}
		if (StringUtils.isNotBlank(istuikuan)) { // �˿����
			sb.append(" and substr(ti.tradeState,2,1)='" + istuikuan + "'");
		}
		if (StringUtils.isNotBlank(ishuakuan)) { // �������
			sb.append(" and substr(ti.tradeState,8,1)='" + ishuakuan + "'");
		}
		if (StringUtils.isNotBlank(isjufu)) { // �ܸ����
			sb.append(" and substr(ti.tradeState,3,1)='" + isjufu + "'");
		}
		if (StringUtils.isNotBlank(isresult)) { // ֧�����
			sb.append(" and substr(ti.tradeState,1,1)='" + isresult + "'");
		}
		//������
		if(StringUtils.isNotBlank(isdownload)){
			List<Object[]> objList  = commonService.list(sb.toString()+" order by ti.tradeTime desc ");
			this.downloadTradeQuery(objList);
			return null;
		}else{
		String amount = "select substr(ti.tradeState,1,1),sum(ti.tradeAmount),ti.moneyType ";
		String groupby = " group by substr(ti.tradeState,1,1),ti.moneyType";
		amountStatistic =  this.commonService.list(amount+sb.toString()+groupby);
		//if(StringUtils.isNotBlank(isdownload)){
		//	List<Object[]> objList  = commonService.list(select+sb.toString());
		//	this.downloadTradeQuery(objList);
		//}else{
			info=commonService.listQueryResultByHql(select+sb.toString()+" order by ti.tradeTime desc ", info);
		//}
		return SUCCESS;}
	}
	/**
	 * �����̻����ײ�ѯ��¼
	 * @return
	 */
	public String downloadTradeQuery(List<Object[]> oArray){
		TableExport export = TableExportFactory.createExcelTableExport();
		export.addTitle(new String[]{"Index","Order No.","Merchant Order No.","Merchant No.","Trade Time",
				"Amount","Currency","Payment Result","Freeze",
				"Refund","ChargeBack","Settlement","Check",
				"Tracking No.","Trade website",
				"Full Name","Country","City","State","Address","zipcode","phone",
				"Remark","Item Information"});
		export.setTableName("Trade");
		InternationalTradeinfo t = new InternationalTradeinfo();
		InternationalCardholdersInfo ci = new InternationalCardholdersInfo();
		int i=1;
		for(Object[] obj:oArray){
			t = (InternationalTradeinfo) obj[0];
			ci = (InternationalCardholdersInfo) obj[2];
			String merno = t.getOrderNo().substring(0, 4);
			export.addRow(new Object[]{i,t.getOrderNo(),t.getMerchantOrderNo(),merno,t.getTradeTime(),
					t.getTradeAmount(),getStates().getCurrencyTypeByNo(t.getMoneyType().intValue()),getStates().getStateNameEndown(t.getTradeState(),1),getStates().getStateNameEndown(t.getTradeState(),4),
					getStates().getStateNameEndown(t.getTradeState(),2),getStates().getStateNameEndown(t.getTradeState(),3),getStates().getStateNameEndown(t.getTradeState(),8),getStates().getStateNameEndown(t.getTradeState(),5),
					t.getIsTrackNo(),t.getTradeUrl(),
					ci.getShippingFullName(),ci.getCountry(),ci.getCity(),ci.getState(),ci.getAddress(),ci.getZipcode(),ci.getPhone(),
					t.getRemark(),ci.getProductInfo()});	
			i++;
		}
		OutputStream os = DownloadUtils.getResponseOutput("Trade.xls");
		export.export(os);
		DownloadUtils.closeResponseOutput();
		return null;
	}
	
	public PageInfo getInfo() {
		return info;
	}

	public void setInfo(PageInfo info) {
		this.info = info;
	}

	public List getAmountStatistic() {
		return amountStatistic;
	}

	public void setAmountStatistic(List amountStatistic) {
		this.amountStatistic = amountStatistic;
	}
	public String getIsdownload() {
		return isdownload;
	}
	public void setIsdownload(String isdownload) {
		this.isdownload = isdownload;
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
	public String getIsresult() {
		return isresult;
	}
	public void setIsresult(String isresult) {
		this.isresult = isresult;
	}
	public String getIstuikuan() {
		return istuikuan;
	}
	public void setIstuikuan(String istuikuan) {
		this.istuikuan = istuikuan;
	}
	public String getIsdongjie() {
		return isdongjie;
	}
	public void setIsdongjie(String isdongjie) {
		this.isdongjie = isdongjie;
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
	public String getIsgoudui() {
		return isgoudui;
	}
	public void setIsgoudui(String isgoudui) {
		this.isgoudui = isgoudui;
	}
	public String getTradeUrl() {
		return tradeUrl;
	}
	public void setTradeUrl(String tradeUrl) {
		this.tradeUrl = tradeUrl;
	}
	public Long getMerchantId() {
		return merchantId;
	}
	public void setMerchantId(Long merchantId) {
		this.merchantId = merchantId;
	}
	public List<InternationalMerchant> getMeridList() {
		return meridList;
	}
	public void setMeridList(List<InternationalMerchant> meridList) {
		this.meridList = meridList;
	}


}
