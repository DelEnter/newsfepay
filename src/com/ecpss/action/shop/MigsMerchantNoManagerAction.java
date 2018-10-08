package com.ecpss.action.shop;

import java.util.Date;
import java.util.List;

import com.ecpss.action.BaseAction;
import com.ecpss.model.channel.InternationalMigsMerchantNo;
import com.ecpss.util.DateUtil;

public class MigsMerchantNoManagerAction extends BaseAction{

	private List<InternationalMigsMerchantNo> migsMerchantNoList;
	private InternationalMigsMerchantNo migsMerchantNo;
	private Long migsId;
	private List statisticsList;         //��ͳ��
	private List dayStatisticsList;      //��ͳ��
	private List monthStatisticsList;    //��ͳ��
	/**
	 * ��ת����3Dͨ�����̻������б�
	 * @return
	 */
	public String toMigsMerchantList(){
		String daytime = DateUtil.getDate();
		String hql = "from InternationalMigsMerchantNo mmn order by mmn.cardtype";		
		migsMerchantNoList = this.commonService.list(hql);
		return SUCCESS;
	}
	/**
	 * ͳ��ҳ��
	 * @return
	 */
	public String toStatisticsList(){
		String daytime = DateUtil.getDate();
		String hql1 = "select mmn.bankMerchantId,count(mmn.bankMerchantId) from InternationalMigsMerchantNo mmn,InternationalTradeinfo ti " +
						"where ti.VIPTerminalNo=mmn.bankMerchantId " +
						"and substr(ti.tradeState,1,1)=1 " ;
		String query = " and trunc(ti.tradeTime,'dd')=trunc(to_date('"+daytime+"','yyyy-MM-dd') ,'dd') ";		
		String monthquery = " and trunc(ti.tradeTime,'mm')=trunc(to_date('"+daytime+"','yyyy-MM-dd') ,'mm') ";		
		String group = " group by mmn.bankMerchantId ";
		statisticsList = this.commonService.list(hql1+group);
		dayStatisticsList = this.commonService.list(hql1+query+group);
		monthStatisticsList = this.commonService.list(hql1+monthquery+group);
		return SUCCESS;
	}
	/**
	 * ����
	 * @return
	 */
	public String toAddMigsMerchantNo(){
		
		return SUCCESS;
	}

	/**
	 * add
	 * 
	 * @return
	 */
	public String addMigsMerchantNo(){
		if(migsMerchantNo.getId() == null){
			migsMerchantNo.setLastDate(new Date());
			migsMerchantNo.setLastMan(getUserBean().getUserName());
			migsMerchantNo.setIsuses("0");
			migsMerchantNo.setOnoff("1");
			this.commonService.saveOrUpdate(migsMerchantNo);
			this.messageAction = "����ͨ���ɹ�";
		}else{
			InternationalMigsMerchantNo m = (InternationalMigsMerchantNo) this.commonService.load(InternationalMigsMerchantNo.class, migsMerchantNo.getId());
			m.setAccessCode(migsMerchantNo.getAccessCode());
			m.setBankMerchantId(migsMerchantNo.getBankMerchantId());
			m.setBankName(migsMerchantNo.getBankName());
			m.setBankUrl(migsMerchantNo.getBankUrl());
			m.setCheckUrl(migsMerchantNo.getCheckUrl());
			m.setCheckUserName(migsMerchantNo.getCheckUserName());
			m.setCheckUserPwd(migsMerchantNo.getCheckUserPwd());
			m.setLastDate(new Date());
			m.setCardtype(migsMerchantNo.getCardtype());
			m.setBillingaddress(migsMerchantNo.getBillingaddress());
			m.setLastMan(getUserBean().getUserName());
			this.commonService.update(m);
			this.messageAction = "�޸ĳɹ�";
		}
		
		return this.OPERATE_SUCCESS;
	}
	/**
	 * �޸�ҳ��
	 * @return
	 */
	public String toUpdateMigs(){
		migsMerchantNo = (InternationalMigsMerchantNo) this.commonService.load(InternationalMigsMerchantNo.class, migsId);
		return SUCCESS;
	}
	/**
	 * ��ͨ��ر�
	 * @return
	 */
	public String onoff(){
		InternationalMigsMerchantNo m = (InternationalMigsMerchantNo) this.commonService.load(InternationalMigsMerchantNo.class, migsId);
		if(m.getOnoff().equals("1")){
			m.setOnoff("0");
			this.messageAction="�ѹر�";
		}else{
			m.setOnoff("1");
			this.messageAction="�ѿ�ͨ";
		}
		this.commonService.update(m);
		return this.OPERATE_SUCCESS;
	}
	
	public List<InternationalMigsMerchantNo> getMigsMerchantNoList() {
		return migsMerchantNoList;
	}


	public void setMigsMerchantNoList(
			List<InternationalMigsMerchantNo> migsMerchantNoList) {
		this.migsMerchantNoList = migsMerchantNoList;
	}
	public InternationalMigsMerchantNo getMigsMerchantNo() {
		return migsMerchantNo;
	}
	public void setMigsMerchantNo(InternationalMigsMerchantNo migsMerchantNo) {
		this.migsMerchantNo = migsMerchantNo;
	}
	public Long getMigsId() {
		return migsId;
	}
	public void setMigsId(Long migsId) {
		this.migsId = migsId;
	}
	public List getStatisticsList() {
		return statisticsList;
	}
	public void setStatisticsList(List statisticsList) {
		this.statisticsList = statisticsList;
	}
	public List getDayStatisticsList() {
		return dayStatisticsList;
	}
	public void setDayStatisticsList(List dayStatisticsList) {
		this.dayStatisticsList = dayStatisticsList;
	}
	public List getMonthStatisticsList() {
		return monthStatisticsList;
	}
	public void setMonthStatisticsList(List monthStatisticsList) {
		this.monthStatisticsList = monthStatisticsList;
	}
	
	
	
}
