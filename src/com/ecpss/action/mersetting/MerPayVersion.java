package com.ecpss.action.mersetting;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ecpss.action.BaseAction;
import com.ecpss.model.shop.InternationalMerchant;
import com.ecpss.model.shop.InternationalPayVersion;
import com.ecpss.service.common.CommonService;

public class MerPayVersion extends BaseAction{
	@Autowired
	@Qualifier("commonService")
	private CommonService commonService;
	private InternationalPayVersion payVer = new InternationalPayVersion();
	private InternationalMerchant mer = new InternationalMerchant();
	private List list;
	private String flag;
	private String hql;
	private Object[] obj;
	

	/**
	 * ��ת������̻�֧���汾ҳ��
	 */
	public String toSaveMerPayVersion(){
		try{
			return SUCCESS;
		}catch(Exception e){
			e.printStackTrace();
			this.messageAction = "ϵͳ�����쳣";
			return this.OPERATE_ERROR;
		}
	}
	
	/**
	 * ����̻�֧���汾
	 */
	public String saveMerPayVersion(){
		try{
			if(mer.getMerno()!=null){
				hql = "FROM InternationalMerchant WHERE merno="+mer.getMerno()+"";
				InternationalMerchant mer = (InternationalMerchant)commonService.list(hql).get(0);
				if(mer!=null){
					payVer.setMerchantId(mer.getId());
					payVer.setLastDate(new Date());
					payVer.setLastMan(getUserBean().getUserName());
					commonService.save(payVer);
					//this.flag = "1";
					this.messageAction = "����̻�֧��ģ��ɹ�!";
				}else{
					this.messageAction = "�̻��Ų�����!";
				}
			}else if(StringUtils.isNotBlank(payVer.getVersion())){
				payVer.setMerchantId(mer.getId());
				payVer.setLastDate(new Date());
				payVer.setLastMan(getUserBean().getUserName());
				commonService.save(payVer);
				this.messageAction = "����̻�֧��ģ��ɹ�!";
			}
			return this.OPERATE_SUCCESS;
		}catch(Exception e){
			e.printStackTrace();
			//this.flag = "2";
			this.messageAction = "ϵͳ�����쳣!";
			return this.OPERATE_SUCCESS;
		}
	}
	
	/**
	 * ɾ���̻�֧���汾
	 */
	public String deleteMerPayVersion(){
		try{
			commonService.delete(payVer);
			this.messageAction = "ɾ���̻�֧��ģ��ɹ�!";
			return this.OPERATE_SUCCESS;
		}catch(Exception e){
			e.printStackTrace();
			this.messageAction = "ϵͳ�����쳣!";
			return this.OPERATE_SUCCESS;
		}
	}
	/**
	 * ��ת���޸��̻�֧���汾
	 */
	public String toUpdateMerPayVersion(){
		try{
			String hql1="select merchantId,version,verurl,id from InternationalPayVersion where id="+payVer.getId();
			obj = (Object[]) commonService.uniqueResult(hql1);
			if(obj[0]!=null){
			hql = "select mer.merno,pay.version,pay.verurl,pay.id FROM InternationalPayVersion pay, InternationalMerchant mer WHERE  mer.id=pay.merchantId " +
					" AND pay.id="+payVer.getId()+"";
			obj = (Object[])commonService.uniqueResult(hql);
			}
			return SUCCESS;
		}catch(Exception e){
			e.printStackTrace();
			this.messageAction = "ϵͳ�����쳣";
			return this.OPERATE_ERROR;
		}
	}
	/**
	 * �޸��̻�֧���汾
	 */
	public String updateMerPayVersion(){
		try{
			InternationalPayVersion pay = (InternationalPayVersion)
			commonService.load(InternationalPayVersion.class, payVer.getId());
			pay.setLastDate(new Date());
			pay.setLastMan(getUserBean().getUserName());
			pay.setVersion(payVer.getVersion());
			pay.setVerurl(payVer.getVerurl());
			commonService.update(pay);
			this.messageAction = "�޸��̻�֧��ģ��ɹ�!";
			return this.OPERATE_SUCCESS;
		}catch(Exception e){
			e.printStackTrace();
			this.messageAction = "ϵͳ�����쳣";
			return this.OPERATE_ERROR;
		}
	}
	
	/**
	 * ��ѯ�̻�֧���汾
	 */
	public String findMerPayVersion(){
		try{
			StringBuffer sb = new StringBuffer("select mer.merno,pay.id,pay.version,pay.verurl,pay.lastMan,pay.lastDate FROM INTERNATIONAL_PayVersion pay left join INTERNATIONAL_MERCHANT mer on mer.id=pay.merchantId");
			if(mer.getMerno()!=null){
				sb.append(" AND mer.merno="+mer.getMerno()+"");
			}
			list = commonService.getByList(sb.toString());
			return SUCCESS;
		}catch(Exception e){
			e.printStackTrace();
			this.messageAction = "ϵͳ�����쳣";
			return this.OPERATE_ERROR;
		}
	}
	public InternationalPayVersion getPayVer() {
		return payVer;
	}
	public void setPayVer(InternationalPayVersion payVer) {
		this.payVer = payVer;
	}
	public InternationalMerchant getMer() {
		return mer;
	}
	public void setMer(InternationalMerchant mer) {
		this.mer = mer;
	}
	public String getFlag() {
		return flag;
	}
	public void setFlag(String flag) {
		this.flag = flag;
	}

	public List getList() {
		return list;
	}

	public void setList(List list) {
		this.list = list;
	}
	
	public Object[] getObj() {
		return obj;
	}

	public void setObj(Object[] obj) {
		this.obj = obj;
	}

	public CommonService getCommonService() {
		return commonService;
	}

	public void setCommonService(CommonService commonService) {
		this.commonService = commonService;
	}
}
