package com.ecpss.action.merchant;

import java.util.List;

import com.ecpss.action.BaseAction;
import com.ecpss.model.shop.InternationalWebchannels;

/**
 * �̻���ַaction
 * 
 * @author huhongguang
 * 
 */
public class MerchantWebSiteAction extends BaseAction {

	private String merno; // �̻���

	private List<InternationalWebchannels> internationalWebchannelsList; // ����̻���ַ�ļ���

	/**
	 * ȥ�̻���ַ��ѯҳ��
	 * 
	 * @return
	 */
	public String toMerchantWebSite() {
		return SUCCESS;
	}

	/**
	 * �����̻�id��ѯ�̻���ַ
	 * 
	 * @return
	 */
	public String findMerchantWebSiteByMerchantId() {
		if (merno != null && !merno.trim().equals("")) {
			internationalWebchannelsList = this.commonService
					.list("from com.ecpss.model.shop.InternationalWebchannels i where "
							+ "i.merchanid=(select f.id from com.ecpss.model.shop.InternationalMerchant f where f.merno = "
							+ merno + ")");
			System.out.println(internationalWebchannelsList.size());
		}
		return SUCCESS;
	}

	public String getMerno() {
		return merno;
	}

	public void setMerno(String merno) {
		this.merno = merno;
	}

	public List<InternationalWebchannels> getInternationalWebchannelsList() {
		return internationalWebchannelsList;
	}

	public void setInternationalWebchannelsList(
			List<InternationalWebchannels> internationalWebchannelsList) {
		this.internationalWebchannelsList = internationalWebchannelsList;
	}

}
