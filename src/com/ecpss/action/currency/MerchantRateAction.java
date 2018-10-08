/*package com.ecpss.action.currency;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;

import com.ecpss.action.BaseAction;
import com.ecpss.model.shop.InternationalMerchantCurrency;
import com.ecpss.service.common.CommonService;

public class MerchantRateAction extends BaseAction{
	@Qualifier("commonService")
	private CommonService commonService;
	private String hql;
	private List list = new ArrayList();
	private StringBuffer sb;
	private InternationalMerchantCurrency merchantCurrency;
	private InternationalMerchantCurrency mc;
	*//**
	 * ����̻�����
	 *//*
	public String saveMerchantCurrency(){
		try{
			//�жϸ��̻��Ƿ��Ѿ������˸ý��ױ���
			hql = "FROM InternationalMerchantCurrency c WHERE c.moneyKindNo="+merchantCurrency.getMoneyKindNo()+" " +
					"AND c.merchanId="+merchantCurrency.getMerchanId()+"";
			list = commonService.list(hql);
			if(list.size()>0){
				this.messageAction = "�ñ��ָ��̻��Ѿ����ù�!";
				return this.OPERATE_SUCCESS;
			}
			if(merchantCurrency.getMerchanId()==null || merchantCurrency.getMerchanId().equals("")){
				this.messageAction = "�̻��Ų���Ϊ��!";
				return this.OPERATE_SUCCESS;
			}
			commonService.save(merchantCurrency);
			this.messageAction="�����̻����ֳɹ�";
			return this.OPERATE_SUCCESS;
		}catch(Exception e){
			e.printStackTrace();
			this.messageAction="�����̻�����ʧ��";
			return this.OPERATE_SUCCESS;
		}
	}
	
	*//**
	 * ɾ���̻�����
	 *//*
	public String deleteMerchantCurrency(){
		try{
			commonService.delete(merchantCurrency);
			this.messageAction="ɾ���̻����ֳɹ�";
			return this.OPERATE_SUCCESS;
		}catch(Exception e){
			e.printStackTrace();
			this.messageAction="ɾ���̻����ֳɹ�";
			return this.OPERATE_SUCCESS;
		}
	}
	*//**
	 * ��ת���޸��̻�����ҳ��
	 *//*
	public String toUpdateMerchantCurrency(){
		try{	
			mc = (InternationalMerchantCurrency)
			commonService.load(InternationalMerchantCurrency.class, merchantCurrency.getId());
			merchantCurrency = mc;
			hql = "FROM InternationalMoneykindname";
			list = commonService.list(hql);
			return SUCCESS;
		}catch(Exception e){
			e.printStackTrace();
			this.messageAction="��ѯ�̻�����ʧ��";
			return this.OPERATE_SUCCESS;
		}
	}
	*//**
	 * �޸��̻�����
	 *//*
	public String updateMerchantCurrency(){
		try{
			//�жϸ��̻��Ƿ��Ѿ������˸ý��ױ���
			hql = "FROM InternationalMerchantCurrency c WHERE c.moneyKindNo="+merchantCurrency.getMoneyKindNo()+" " +
					"AND c.merchanId="+merchantCurrency.getMerchanId()+"";
			list = commonService.list(hql);
			if(list.size()>0){
				this.messageAction = "�ñ��ָ��̻��Ѿ����ù�!";
				return this.OPERATE_SUCCESS;
			}
			if(merchantCurrency.getMerchanId()==null || merchantCurrency.getMerchanId().equals("")){
				this.messageAction = "�̻��Ų���Ϊ��!";
				return this.OPERATE_SUCCESS;
			}
			InternationalMerchantCurrency currency = (InternationalMerchantCurrency)
			commonService.load(InternationalMerchantCurrency.class, merchantCurrency.getId());
			
			currency.setMerchanId(merchantCurrency.getMerchanId());
			currency.setMoneyKindNo(merchantCurrency.getMoneyKindNo());
			commonService.update(currency);
			
			this.messageAction="�̻������޸ĳɹ�";
			return this.OPERATE_SUCCESS;
		}catch(Exception e){
			e.printStackTrace();
			this.messageAction="�̻������޸ĳɹ�";
			return this.OPERATE_SUCCESS;
		}
	}
	*//**
	 * ��ѯ�̻�����
	 *//*
	public String findMerchantCurrency(){
		try{
			sb = new StringBuffer("FROM InternationalMerchantCurrency c, InternationalMoneykindname m" +
					" WHERE c.moneyKindNo= m.moneykindno");
			if(merchantCurrency!=null){
				if(merchantCurrency.getMerchanId()!=null){
					sb.append(" AND merchanId="+merchantCurrency.getMerchanId()+"");
				}
			}
			hql = sb.toString();
			list = commonService.list(hql);
			return SUCCESS;
		}catch(Exception e){
			e.printStackTrace();
			this.messageAction="��ѯ�̻�����ʧ��";
			return this.OPERATE_SUCCESS;
		}
	}
	*//**
	 * ��ת�������̻�����
	 *//*
	public String toMerchantCurrency(){
		try{
			hql = "FROM InternationalMoneykindname";
			list = commonService.list(hql);
			return SUCCESS;
		}catch(Exception e){
			e.printStackTrace();
			this.messageAction="��ѯ�̻�����ʧ��";
			return this.OPERATE_SUCCESS;
		}
	}
	
	*//**
	 * @return the commonService
	 *//*
	public CommonService getCommonService() {
		return commonService;
	}
	*//**
	 * @param commonService the commonService to set
	 *//*
	public void setCommonService(CommonService commonService) {
		this.commonService = commonService;
	}

	*//**
	 * @return the merchantCurrency
	 *//*
	

	*//**
	 * @return the list
	 *//*
	public List getList() {
		return list;
	}

	*//**
	 * @param list the list to set
	 *//*
	public void setList(List list) {
		this.list = list;
	}

	*//**
	 * @return the mc
	 *//*
	public InternationalMerchantCurrency getMc() {
		return mc;
	}

	*//**
	 * @param mc the mc to set
	 *//*
	public void setMc(InternationalMerchantCurrency mc) {
		this.mc = mc;
	}

	*//**
	 * @return the merchantCurrency
	 *//*
	public InternationalMerchantCurrency getMerchantCurrency() {
		return merchantCurrency;
	}

	*//**
	 * @param merchantCurrency the merchantCurrency to set
	 *//*
	public void setMerchantCurrency(InternationalMerchantCurrency merchantCurrency) {
		this.merchantCurrency = merchantCurrency;
	}
}
*/