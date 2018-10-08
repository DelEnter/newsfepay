package com.ecpss.action.merchant;

import java.util.Date;
import java.util.List;

import com.ecpss.action.BaseAction;
import com.ecpss.model.MerchantRiskControl;
/**
 * �̻����ϵͳ
 * @author Administrator
 *
 */
public class ProhibitAreaAction extends BaseAction{
	
	
	private List<MerchantRiskControl> mrcList;
	
	private String [] area;
	private Long areaId;
	
	/**
	 * �����ֹ���׵���ҳ��
	 * @return
	 */
	public String prohibitAreaQuery(){
		String hql = "select mrc from MerchantRiskControl mrc where mrc.merchantId="+getMerchantBean().getMerchantId();
		mrcList = this.commonService.list(hql);
		return SUCCESS;
	}
	/**
	 * ��ӷ�ص���
	 * @return
	 */
	public String addArea(){
		if(area!=null){
			if(area.length>0){
				for (int i = 0; i < area.length; i++) {
					MerchantRiskControl mrc = new MerchantRiskControl();
					mrc.setArea(area[i]);
					mrc.setLastDate(new Date());
					mrc.setMerchantId(getMerchantBean().getMerchantId());
					String hql1 = "select mrc.area from MerchantRiskControl mrc where mrc.merchantId="+getMerchantBean().getMerchantId()+" and mrc.area='"+area[i]+"'";
					List list = this.commonService.list(hql1);
					if(list.size()==0){
						this.commonService.save(mrc);
					}
				}
			}
		}
		String hql = "select mrc from MerchantRiskControl mrc where mrc.merchantId="+getMerchantBean().getMerchantId();
		mrcList = this.commonService.list(hql);
		return SUCCESS;
	}
	
	public String deleteArea(){
		this.commonService.delete(MerchantRiskControl.class, areaId);
		String hql = "select mrc from MerchantRiskControl mrc where mrc.merchantId="+getMerchantBean().getMerchantId();
		mrcList = this.commonService.list(hql);
		return SUCCESS;
	}
	
	/**
	 * ��������ĳ������Ϣ
	 * @return
	 */
	public String singleShieldingTrade(){
		
		return SUCCESS;
	}
	
	
	public List<MerchantRiskControl> getMrcList() {
		return mrcList;
	}
	public void setMrcList(List<MerchantRiskControl> mrcList) {
		this.mrcList = mrcList;
	}
	public String[] getArea() {
		return area;
	}
	public void setArea(String[] area) {
		this.area = area;
	}
	public Long getAreaId() {
		return areaId;
	}
	public void setAreaId(Long areaId) {
		this.areaId = areaId;
	}
	
}
