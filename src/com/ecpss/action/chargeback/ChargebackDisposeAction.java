package com.ecpss.action.chargeback;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ecpss.action.BaseAction;
import com.ecpss.action.filemanager.FileUpLoadAction;
import com.ecpss.model.payment.InternationalCardholdersInfo;
import com.ecpss.model.payment.InternationalTradeinfo;
import com.ecpss.model.shop.InternationalChargeBack;
import com.ecpss.service.iservice.SystemManagerService;
import com.ecpss.tools.TableExport;
import com.ecpss.tools.TableExportFactory;
import com.ecpss.util.DateUtil;
import com.ecpss.util.DownloadUtils;
import com.ecpss.util.StatusUtil;
import com.ecpss.web.PageInfo;

public class ChargebackDisposeAction extends BaseAction{
	@Autowired
	@Qualifier("systemManagerService")
	private SystemManagerService systemManagerService;
	/**
	 * ��ҳʹ�õĶ���
	 */
	private PageInfo info = new PageInfo();
	/**
	 * �г�����excel�е�����û����ecpssϵͳ���ҵ��������ݵľܸ�������
	 */
	private List<InternationalChargeBack> cbByNoTradeIdList;

	/**
	 * �ظ�������
	 */
	private List sameList;
	/**
	 * �ϴ��ļ���
	 */
	private File fileName;  
	/**
	 * ���ױ�id
	 */
	private Long tradeId;
	/**
	 * �ܸ�������id
	 */
	private Long chargebackId;
	private InternationalChargeBack cb;
	private InternationalTradeinfo ti;
	private String isRickCard;
	private String isBackCard;
	
	/**
	 * �����ܸ�����
	 */
	private Long [] orders;
	
	/**
	 * �����̻������ļ�
	 */
	private String inputPath; // ָ��Ҫ�����ص��ļ�·�� 
	private InputStream inputStream;
	private String fileNameByup;
	/**
	 * ��ѯ����
	 */
	private String orderNo;
	private String cardNo;
	private Long merchantNo;
	private String startDate;
	private String endDate;
	private String startImportDate;  //��������
	private String endImportDate;    //��������
	private String batchno;
	private String isProtest;
	
	
	private List<String> batchnoList;
	
	
	/**
	 * ��ʾ���ܸ����״����ѯ
	 */
	public String chargebackTradeQuery(){
		StringBuffer sb = new StringBuffer();
		sb.append("select cb,ti from InternationalChargeBack cb,InternationalTradeinfo ti " +
				"where cb.tradeId=ti.id " +
				" and cb.isChargeBack!=4 ");
		if(StringUtils.isNotBlank(orderNo)){
			sb.append(" and ti.orderNo='"+orderNo.trim()+"' ");
		}
		if(merchantNo!=null){
			sb.append(" and ti.orderNo like '"+merchantNo+"%'");
		}
		if(StringUtils.isNotBlank(cardNo)){
			sb.append(" and cb.cardNoBy like '%"+cardNo.trim()+"%' ");
		}
		if(StringUtils.isNotBlank(batchno)){
			sb.append(" and cb.upbatchno='"+batchno.trim()+"' ");
		}
		if(StringUtils.isNotBlank(isProtest)){   //�ܸ����
			sb.append(" and substr(ti.tradeState,3,1)='"+isProtest+"'");
		}
		if(StringUtils.isNotBlank(startDate)){  //��ʼ����
			sb.append(" and ti.tradeTime>=to_date('"+startDate+"','yyyy-MM-dd') ");
		}
		if(StringUtils.isNotBlank(endDate)){   //��������
			sb.append(" and ti.tradeTime<=to_date('"+endDate+" 23:59:59','yyyy-MM-dd hh24:mi:ss') ");
		}
		if(StringUtils.isNotBlank(startImportDate)){  //��ʼ����
			sb.append(" and cb.importDate>=to_date('"+startImportDate+"','yyyy-MM-dd') ");
		}
		if(StringUtils.isNotBlank(endImportDate)){   //��������
			sb.append(" and cb.importDate<=to_date('"+endImportDate+" 23:59:59','yyyy-MM-dd hh24:mi:ss') ");
		}
		sb.append(" order by substr(ti.tradeState,3,4),ti.protestTime desc");
		
		if(StringUtils.isNotBlank(this.query) && this.query.equals("0")){
			query = "1";
			//isProtest = "0";
		}else{
		info = this.commonService.listQueryResultByHql(sb.toString(),info);
		}

		
		String hql3="select distinct cb.upbatchno from InternationalChargeBack cb where cb.upbatchno like '1%' and cb.isChargeBack!=4 order by cb.upbatchno desc";
		batchnoList = this.commonService.list(hql3);
		
		return SUCCESS;
	}
	
	
	/**
	 * �ϴ����и��ľܸ������ĵ�
	 * @return
	 */
	public String importChargebackTrade(){
		try {
			//this.messageAction = systemManagerService.importChargeBack(fileName);
		} catch (Exception e) {
			e.printStackTrace();
			
			return SUCCESS;
		}
		return SUCCESS;
	}

	/**
	 * �޸Ľ��׾ܸ�����
	 * @return
	 */
	public String chargeBack(){
		InternationalTradeinfo ti = (InternationalTradeinfo) this.commonService.load(InternationalTradeinfo.class, this.tradeId);
		ti.setTradeState(StatusUtil.updateStatus(ti.getTradeState(), 3, "1"));
		ti.setProtestTime(new Date());
		this.commonService.update(ti);
		return chargebackTradeQuery();
	}

	/**
	 * ��ת���޸ľܸ�����
	 * @return
	 */
	public String toUpdateChargeback(){
		cb = (InternationalChargeBack) this.commonService.load(InternationalChargeBack.class, this.chargebackId);
		if(cb.getTradeId()!=null){
			ti = (InternationalTradeinfo) this.commonService.load(InternationalTradeinfo.class, cb.getTradeId());
		}
		return SUCCESS;
	}
	/**
	 * �޸ľܸ�
	 * @return
	 */
	public String updateChargeBack(){
		String hql = "select ti.id from InternationalTradeinfo ti where ti.orderNo='"+ti.getOrderNo()+"' ";
		Long tid = (Long) this.commonService.uniqueResult(hql);
		if(tid!=null){
			String hql1 = "select c.cardNo from InternationalCardholdersInfo c where c.tradeId="+tid;
			String cardNo = (String) this.commonService.uniqueResult(hql1);
			InternationalChargeBack cb1 = (InternationalChargeBack) this.commonService.load(InternationalChargeBack.class, this.chargebackId);
			//���ݽ��ױ��id��ѯ��������׶�Ӧ�Ŀ���,Ȼ����ϴ��ľܸ���������Ŀ��ŶԱ� 
			//���һֱ�͸��ľܸ�,Ȼ����Ĵ˽���״̬
			if(getCarNo(cardNo.trim()).equals(cb1.getCardNoBy().trim())){
				cb1.setRemark(cb.getRemark());
				cb1.setIsChargeBack("0");
				cb1.setTradeId(tid);
				this.messageAction = this.systemManagerService.chargeBackUpdate(cb1, tid, isBackCard, isRickCard);
			}else{
				this.messageAction = "������Ľ�����ˮ�Ŷ�Ӧ�Ŀ��Ÿ��ϴ��ľܸ����Ų�һ��.";
			}
		}else{
			this.messageAction = "�鲻����Ӧ�Ľ���";
		}
		return this.OPERATE_SUCCESS;
	}
	
	/**
	 * �����ܸ�
	 * @return
	 */
	public String batchChargeBack(){
		if(orders.length>0){
			for (int i = 0; i < orders.length; i++) {
				InternationalChargeBack cb1 = (InternationalChargeBack) this.commonService.load(InternationalChargeBack.class, orders[i]);
				String hql1 = "select c.cardNo from InternationalCardholdersInfo c where c.tradeId="+cb1.getTradeId();
				String cardNo = (String) this.commonService.uniqueResult(hql1);
//				if(getCarNo(cardNo.trim()).equals(cb1.getCardNoBy().trim())){
					cb1.setIsChargeBack("0");
					this.messageAction = this.systemManagerService.chargeBackUpdate(cb1, cb1.getTradeId(), "false", "false");
//				}
			}
		}
		return this.OPERATE_SUCCESS;
	}
	
	
	/**
	 * ɾ���Ǿܸ�����
	 * @return
	 */
	public String deleteChargeBack(){
		this.commonService.delete(InternationalChargeBack.class,this.chargebackId);
		return OPERATE_SUCCESS;
	}
	
	/**
	 * �����̻��ϴ��������ļ�
	 * @return
	 * @throws IOException 
	 */
	public String downloadCom() throws IOException{
		InternationalChargeBack c = (InternationalChargeBack) this.commonService.load(InternationalChargeBack.class, chargebackId);
		InputStream stream = FileUpLoadAction.class.getClassLoader().getResourceAsStream("/ecpss.properties");
		Properties p = new Properties();
		p.load(stream);
		inputPath=p.getProperty("upload_dir")+"/"+c.getComplaintsFileroute();
		String orgName = FilenameUtils.getBaseName(c.getComplaintsFileName())+"."+FilenameUtils.getExtension(c.getComplaintsFileroute());
		fileNameByup = new String(orgName.getBytes("GBK"),"iso-8859-1");
		return SUCCESS;
	}
	public InputStream getInputStream() throws Exception {
		inputStream = new FileInputStream(inputPath);
		// ͨ�� ServletContext��Ҳ����application ����ȡ���� 
		return inputStream ; 
	}
	
	
	private String cardnum;  //����
	private List chargebackList;
	
	/**
	 * ������ѯ�ܸ�
	 * @return
	 */
	public String toChargeBackList(){
		StringBuffer sb = new StringBuffer();
		sb.append("select ti,m,ci,c from InternationalTradeinfo ti," +
						"InternationalMerchant m," +
						"InternationalCardholdersInfo ci," +
						"InternationalMerchantChannels mc," +
						"InternationalChannels c " +
				"where ti.merchantId=m.id " +     //���ױ��̻�ID
				"and ci.tradeId=ti.id " +         //�ֿ��˸����ױ�
				"and ti.tradeChannel=mc.id " +    //���ױ��̻�ͨ��
				"and mc.channelId=c.id ");       //�̻�ͨ����ͨ��
		
		if(StringUtils.isNotBlank(orderNo)){
			sb.append(" and ti.orderNo like '"+orderNo.trim()+"%'");
		}
		if(StringUtils.isNotBlank(cardnum)){
			sb.append(" and ci.cardNo='"+setCarNo(cardnum.trim())+"'");
		}
		
		sb.append(" order by ti.tradeTime desc");
		if(StringUtils.isNotBlank(orderNo) || StringUtils.isNotBlank(cardnum)){
			chargebackList = this.commonService.list(sb.toString());
		}
		return SUCCESS;
	}
	
	/**
	 * ���е��ʾܸ����״���
	 * @return
	 */
	public String toSingleChargeback(){
		ti = (InternationalTradeinfo) this.commonService.load(InternationalTradeinfo.class, this.tradeId);
		return SUCCESS;
	}
	
	/**
	 * ���е��ʽ��׾ܸ�
	 * @return
	 */
	public String singleChargeBack(){
		String hql2 = "select c.id from InternationalChargeBack c where c.tradeId="+tradeId;
		List list = this.commonService.list(hql2);
		if(list.size()>0){
			this.messageAction = "�ܸ����Ѵ��ڴ˽���";
		}else{
			SimpleDateFormat time1=new SimpleDateFormat("yyMMddHHmmss");

			String batchno = time1.format(new Date());
			String hql1 = "select c.cardNo from InternationalCardholdersInfo c where c.tradeId="+tradeId;
			String cardNo = (String) this.commonService.uniqueResult(hql1);
			ti = (InternationalTradeinfo) this.commonService.load(InternationalTradeinfo.class, this.tradeId);
			cb.setTradeId(ti.getId());
			cb.setAuthorizationNoBy(ti.getVIPAuthorizationNo());
			cb.setTradeAmountBy(ti.getRmbAmount());
			cb.setTradeDateBy(ti.getTradeTime().toString());
			cb.setCardNoBy(getCarNo(cardNo));
			cb.setImportDate(new Date());
			cb.setIsChargeBack("0");
			cb.setUpbatchno(batchno);
			this.commonService.save(cb);
			InternationalChargeBack cb1 = (InternationalChargeBack) this.commonService.load(InternationalChargeBack.class, cb.getId());
			//���ݽ��ױ��id��ѯ��������׶�Ӧ�Ŀ���,Ȼ����ϴ��ľܸ���������Ŀ��ŶԱ� 
			//���һֱ�͸��ľܸ�,Ȼ����Ĵ˽���״̬
			this.messageAction = this.systemManagerService.chargeBackUpdate(cb1, tradeId, isBackCard, isRickCard);
		}
		return this.OPERATE_SUCCESS;
	}
	
	private String isdownload;
	private List sameListBy;
	/**
	 * һ�����ε���������
	 * @return
	 */
	public String getBatchCB(){
		if(fileName!=null){
			SimpleDateFormat time1=new SimpleDateFormat("yyMMddHHmmss");
			batchno = time1.format(new Date());
			this.messageAction = systemManagerService.importChargeBack(fileName,batchno);
		}
		StringBuffer sb = new StringBuffer();
		sb.append("select cb,ti from InternationalChargeBack cb,InternationalTradeinfo ti " +
				"where cb.tradeId=ti.id " +
				" and cb.isChargeBack=4 " +
				" and substr(ti.tradeState,3,1)=0 ");
		if(StringUtils.isNotBlank(batchno)){
			sb.append(" and cb.upbatchno='"+batchno.trim()+"' ");
		}
		sb.append(" order by substr(ti.tradeState,2,1) desc,substr(ti.tradeState,3,1),ti.protestTime desc");
		//��ȡδ�ܸ���������Ҫ�ܸ��Ľ���
		if(StringUtils.isNotBlank(isdownload)){
			List<Object[]> objList  = commonService.list(sb.toString());
			this.downloadCB(objList);
			return null;
		}else{
			info = this.commonService.listQueryResultByHql(sb.toString(),info);
		}
		//��ȡ�Ѿ��ܸ��Ľ���
		StringBuffer sb1 = new StringBuffer();
		sb1.append("select cb,ti from InternationalChargeBack cb,InternationalTradeinfo ti " +
				"where cb.tradeId=ti.id " +
				" and cb.isChargeBack=4 " +
				" and substr(ti.tradeState,3,1)=1 ");
		if(StringUtils.isNotBlank(batchno)){
			sb1.append(" and cb.upbatchno='"+batchno.trim()+"' ");
		}
		sb1.append(" order by substr(ti.tradeState,3,1),ti.protestTime desc");
		this.sameList = this.commonService.list(sb1.toString());
		
		String hql="select cb from InternationalChargeBack cb where cb.tradeId is null and cb.isChargeBack=4 ";
		cbByNoTradeIdList = this.commonService.list(hql);
		//��ȡ���κ��б�
		String hql3="select distinct cb.upbatchno from InternationalChargeBack cb where cb.upbatchno like '1%' and cb.isChargeBack=4 order by cb.upbatchno desc";
		batchnoList = this.commonService.list(hql3);
		//��ȡ�ظ�����
		StringBuffer sb4 = new StringBuffer();
		sb4.append("select cb,ti from InternationalChargeBack cb,InternationalTradeinfo ti " +
				"where 1=1  and ti.id=cb.tradeId and cb.isChargeBack=4 ");
		
		sb4.append(
				" and (cb.cardNoBy,cb.authorizationNoBy,cb.tradeAmountBy) in  " +
				"(select cardNoBy,authorizationNoBy,tradeAmountBy " +
					"from InternationalChargeBack " + 
				"group by cardNoBy,authorizationNoBy,tradeAmountBy having count(*) > 1) order by importDate desc");
		sameListBy = this.commonService.list(sb4.toString());
		
		
		this.getLoaction().setReload(true);
		return SUCCESS;
	}
	
	/**
	 * �����̻����ײ�ѯ��¼
	 * @return
	 */
	public void downloadCB(List<Object[]> oArray) {
		TableExport export = TableExportFactory.createExcelTableExport();
		export.addTitle(new String[]{"����","���׽��","����RMB���","�˿���","����","�ն˺�",
				"��Ȩ��","����","��Ʒ���","�Ƿ��˿�","�Ƿ�ܸ�","��ˮ��","���κ�","�ܸ�����"});
		export.setTableName("CB-"+DateUtil.getDateTime());
		InternationalTradeinfo t = new InternationalTradeinfo();
		InternationalChargeBack ci = new InternationalChargeBack();
		int i=1;
		Double refundamount=0D;
		for(Object[] obj:oArray){
			t = (InternationalTradeinfo) obj[1];
			cb = (InternationalChargeBack) obj[0];
			if(t.getTradeState().substring(1, 2).equals("2")){
				refundamount=0D;
			}else{
				refundamount=t.getRmbAmount();
			}
			export.addRow(new Object[]{cb.getCardNoBy(),t.getTradeAmount(),t.getRmbAmount(),refundamount,t.getTradeTime(),t.getVIPTerminalNo(),
					t.getVIPAuthorizationNo(),cb.getRef(),cb.getProductNo(),getStates().getStateName(t.getTradeState(), 2),getStates().getStateName(t.getTradeState(), 3),t.getOrderNo(),cb.getUpbatchno(),cb.getRemark()});	
			i++;
		}
		OutputStream os = DownloadUtils.getResponseOutput("CB-"+DateUtil.getDateTime()+".xls");
		export.export(os);
		DownloadUtils.closeResponseOutput();
	}
	
	
	public PageInfo getInfo() {
		return info;
	}


	public void setInfo(PageInfo info) {
		this.info = info;
	}


	public File getFileName() {
		return fileName;
	}


	public void setFileName(File fileName) {
		this.fileName = fileName;
	}


	public SystemManagerService getSystemManagerService() {
		return systemManagerService;
	}


	public void setSystemManagerService(SystemManagerService systemManagerService) {
		this.systemManagerService = systemManagerService;
	}


	public List<InternationalChargeBack> getCbByNoTradeIdList() {
		return cbByNoTradeIdList;
	}


	public void setCbByNoTradeIdList(List<InternationalChargeBack> cbByNoTradeIdList) {
		this.cbByNoTradeIdList = cbByNoTradeIdList;
	}


	public Long getTradeId() {
		return tradeId;
	}


	public void setTradeId(Long tradeId) {
		this.tradeId = tradeId;
	}


	public Long getChargebackId() {
		return chargebackId;
	}


	public void setChargebackId(Long chargebackId) {
		this.chargebackId = chargebackId;
	}


	public InternationalChargeBack getCb() {
		return cb;
	}


	public void setCb(InternationalChargeBack cb) {
		this.cb = cb;
	}


	public InternationalTradeinfo getTi() {
		return ti;
	}


	public void setTi(InternationalTradeinfo ti) {
		this.ti = ti;
	}


	public String getInputPath() {
		return inputPath;
	}


	public void setInputPath(String inputPath) {
		this.inputPath = inputPath;
	}


	public String getFileNameByup() {
		return fileNameByup;
	}


	public void setFileNameByup(String fileNameByup) {
		this.fileNameByup = fileNameByup;
	}


	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}


	public String getIsRickCard() {
		return isRickCard;
	}


	public void setIsRickCard(String isRickCard) {
		this.isRickCard = isRickCard;
	}


	public String getIsBackCard() {
		return isBackCard;
	}


	public void setIsBackCard(String isBackCard) {
		this.isBackCard = isBackCard;
	}


	public String getOrderNo() {
		return orderNo;
	}


	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}


	public Long getMerchantNo() {
		return merchantNo;
	}


	public void setMerchantNo(Long merchantNo) {
		this.merchantNo = merchantNo;
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


	public Long[] getOrders() {
		return orders;
	}


	public void setOrders(Long[] orders) {
		this.orders = orders;
	}


	public List getSameList() {
		return sameList;
	}


	public void setSameList(List sameList) {
		this.sameList = sameList;
	}


	public String getCardnum() {
		return cardnum;
	}


	public void setCardnum(String cardnum) {
		this.cardnum = cardnum;
	}


	public List getChargebackList() {
		return chargebackList;
	}


	public void setChargebackList(List chargebackList) {
		this.chargebackList = chargebackList;
	}


	public String getBatchno() {
		return batchno;
	}


	public void setBatchno(String batchno) {
		this.batchno = batchno;
	}


	public String getIsProtest() {
		return isProtest;
	}


	public void setIsProtest(String isProtest) {
		this.isProtest = isProtest;
	}


	public List<String> getBatchnoList() {
		return batchnoList;
	}


	public void setBatchnoList(List<String> batchnoList) {
		this.batchnoList = batchnoList;
	}


	public String getIsdownload() {
		return isdownload;
	}


	public void setIsdownload(String isdownload) {
		this.isdownload = isdownload;
	}


	public List getSameListBy() {
		return sameListBy;
	}


	public void setSameListBy(List sameListBy) {
		this.sameListBy = sameListBy;
	}


	public String getStartImportDate() {
		return startImportDate;
	}


	public void setStartImportDate(String startImportDate) {
		this.startImportDate = startImportDate;
	}


	public String getEndImportDate() {
		return endImportDate;
	}


	public void setEndImportDate(String endImportDate) {
		this.endImportDate = endImportDate;
	}


}
