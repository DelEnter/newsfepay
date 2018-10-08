package com.ecpss.action.shop;

import java.util.Date;
import java.util.List;

import com.ecpss.action.BaseAction;
import com.ecpss.model.shop.InternationalAgentsMerchant;
import com.ecpss.model.shop.InternationalMerchant;

public class AgentsMerchantManagerAction extends BaseAction {

	private Long proMerno; // �����̻���
	private Long agentsMerNo; // ��ѯʱ��ʹ��
	private Long merno; // ���̻���
	private List agentsMerList;
	private String pwd; // ����
	private Long agentId; // id
	private List<Long> mernolist;
	
	private List<String> pwdList;

	/**
	 * ��������̻�����ҳ��
	 * 
	 * @return
	 */
	public String toAgentsMerchant() {
		mernolist = this.commonService
				.list("select distinct iam.agentsMerchantNo from InternationalAgentsMerchant iam");
		System.out.println(mernolist + "************************************");
		StringBuffer hql = new StringBuffer();
		hql
				.append("select iam,m from InternationalAgentsMerchant iam,InternationalMerchant m where iam.merchantId=m.id ");
		if (agentsMerNo != null && !agentsMerNo.equals("")) {
			hql.append("and iam.agentsMerchantNo = " + agentsMerNo);
		}
		agentsMerList = this.commonService.list(hql.toString());
		return SUCCESS;
	}

	/**
	 * ���һ���µĴ����̻��Ż��߸���������̻�������һ�����̻���
	 * 
	 * @return
	 */
	public String addAgentsMerchant() {
		if(merno == null || "".equals(merno)) {
			messageAction="�������̻���";
			return OPERATE_SUCCESS;
		}
//		// 1. �������̻��Ų�ѯ�̻����Ƿ����
		InternationalMerchant merchant = (InternationalMerchant) this.commonService
				.uniqueResult("select im from InternationalMerchant im where im.merno= '"
						+ merno + "' ");
		if (merchant == null || proMerno == null) {
			this.messageAction = "���̻��Ų����ڣ�����������Ĵ����̻��Ŵ���";
			getLoaction().setReload(true);
			return OPERATE_SUCCESS;
		}
		// һ���̻���ֻ�ܸ�һ���̻�����ʹ��
		Long count = (Long) this.commonService
				.uniqueResult("select count(iam.id) from InternationalAgentsMerchant iam where iam.merchantId = '"
						+ merchant.getId() + "' ");
		if (count == 1 && count != 0) {
			this.messageAction = "���̻��Ѿ�����,һ���̻���ֻ�ܸ�һ���̻�����ʹ��";
			getLoaction().setReload(true);
			return OPERATE_SUCCESS;
		} 
		// �����̻������ʵ�����
		InternationalAgentsMerchant iam = new InternationalAgentsMerchant();
		iam.setMerchantId(merchant.getId());
		iam.setAgentsMerchantNo(proMerno);
		iam.setLastDate(new Date());
		iam.setLastMan(getUserBean().getUserName());
		iam.setPassword(pwd);
		this.commonService.save(iam);
		messageAction="��ӳɹ�";
		this.getLoaction().setReload(true);
		return OPERATE_SUCCESS;

	}

	/**
	 * ɾ�������̻�
	 * 
	 * @return
	 */
	public String deleteAgentMerchant() {
		mernolist = this.commonService
				.list("select distinct iam.agentsMerchantNo from InternationalAgentsMerchant iam");
		InternationalAgentsMerchant iam = (InternationalAgentsMerchant) this.commonService
				.load(InternationalAgentsMerchant.class, agentId);
		if (iam != null) {
			this.commonService.delete(iam);
			this.messageAction="ɾ���ɹ�";
		}
		return OPERATE_SUCCESS;
	}

	public Long getProMerno() {
		return proMerno;
	}

	public void setProMerno(Long proMerno) {
		this.proMerno = proMerno;
	}

	public Long getAgentsMerNo() {
		return agentsMerNo;
	}

	public void setAgentsMerNo(Long agentsMerNo) {
		this.agentsMerNo = agentsMerNo;
	}

	public Long getMerno() {
		return merno;
	}

	public void setMerno(Long merno) {
		this.merno = merno;
	}

	public List getAgentsMerList() {
		return agentsMerList;
	}

	public void setAgentsMerList(List agentsMerList) {
		this.agentsMerList = agentsMerList;
	}

	public Long getAgentId() {
		return agentId;
	}

	public void setAgentId(Long agentId) {
		this.agentId = agentId;
	}

	public List<Long> getMernolist() {
		return mernolist;
	}

	public void setMernolist(List<Long> mernolist) {
		this.mernolist = mernolist;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

}
