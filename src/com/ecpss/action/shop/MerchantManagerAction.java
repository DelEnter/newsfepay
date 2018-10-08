package com.ecpss.action.shop;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ecpss.action.BaseAction;
import com.ecpss.model.shop.InternationalMerchantManager;
import com.ecpss.service.common.CommonService;

public class MerchantManagerAction extends BaseAction{
	@Autowired
	@Qualifier("commonService")
	private CommonService commonService;
	private InternationalMerchantManager merman;
	private List list = new ArrayList();
	private String hql;
	/**
	 * ��ת���̻������ֵҳ��
	 */
	public String toMerchantManagerValue(){
		try{
			return SUCCESS;
		}catch(Exception e)
		{
			e.printStackTrace();
			this.messageAction = "��ת���̻������ֵҳ��ʧ��";
			return this.OPERATE_ERROR;
		}
	}
	/**
	 * ��ѯ�̻������ֵ
	 */
	public String findManagerValue(){
		try{
			if(merman==null){
				merman = new InternationalMerchantManager();
			}
			long tem=merman.getMerchantId();
			hql = "FROM InternationalMerchantManager WHERE merchantid="+merman.getMerchantId()+"";
			list = commonService.list(hql);
			if(list.size()==0){
				hql = "FROM InternationalMerchantManager WHERE merchantid is null";
				list = commonService.list(hql);
				if(list.size()!=0){
					merman = (InternationalMerchantManager)list.get(0);
					merman.setMerchantId(tem);
				}
			}
			return SUCCESS;
		}catch(Exception e){
			e.printStackTrace();
			this.messageAction="��ѯ�̻�����ʧ��";
			return this.OPERATE_SUCCESS;
		}
	}
	/**
	 * ��ѯ��Ҫ�޸ĵ��̻������ֵ
	 */
	public String findUpManagerValue(){
		try{
			Long merchantId = merman.getMerchantId();
			merman = (InternationalMerchantManager)
			commonService.load(InternationalMerchantManager.class, merman.getId());
			merman.setMerchantId(merchantId);
			return SUCCESS;
		}catch(Exception e){
			e.printStackTrace();
			this.messageAction="ϵͳ�����쳣!";
			return this.OPERATE_SUCCESS;
		}
	}
	/**
	 * �޸��̻������ֵ
	 * @return
	 */
	public String updateManagerValue(){
		try{
			//������ݿ��и��̻���������ֱ���޸ģ����û����ֱ�����
			hql = "FROM InternationalMerchantManager WHERE merchantid="+merman.getMerchantId()+"";
			list = commonService.list(hql);
			if(list.size()==0){
				commonService.save(merman);
			}else{
				InternationalMerchantManager mm = (InternationalMerchantManager)
				commonService.load(InternationalMerchantManager.class, merman.getId());
				mm.setMerchantId(merman.getMerchantId());
				mm.setDayQuota(merman.getDayQuota());
				mm.setMarkValue(merman.getMarkValue());
				mm.setMerchantAddress(merman.getMerchantAddress());
				mm.setMonthQuota(merman.getMonthQuota());
				mm.setPenQuota(merman.getPenQuota());
				commonService.update(mm);
			}
			this.messageAction="�̻������ֵ�޸ĳɹ�!";
			return this.OPERATE_SUCCESS;
		}catch(Exception e){
			e.printStackTrace();
			this.messageAction="�̻������ֵ�޸�ʧ��!";
			return this.OPERATE_SUCCESS;
		}
	}
	
	public CommonService getCommonService() {
		return commonService;
	}
	public void setCommonService(CommonService commonService) {
		this.commonService = commonService;
	}
	public List getList() {
		return list;
	}
	public void setList(List list) {
		this.list = list;
	}
	public InternationalMerchantManager getMerman() {
		return merman;
	}
	public void setMerman(InternationalMerchantManager merman) {
		this.merman = merman;
	}
}
