package com.ecpss.action.moneytypesetting;

import java.util.List;

import com.ecpss.action.BaseAction;
import com.ecpss.model.shop.InternationalMerchantCurrency;

/**
 * ��������
 * 
 * @author huhongguang
 * 
 */
public class MoneyTypeSettingAction extends BaseAction {

	private Long merid; // �̻�id

	private List<InternationalMerchantCurrency> moneyTypeList; // �̻����ּ���

	private InternationalMerchantCurrency internationalMerchantCurrency; // �̻�����ʵ��

	/**
	 * ��ת���̻����ֹ���ҳ��
	 * 
	 * @return
	 */
	public String toMerCreditMoneyType() {
		moneyTypeList = this.commonService
				.list("from InternationalMerchantCurrency im where im.merchanId = "
						+ merid);
		this.getLoaction().setReload(true);
		return SUCCESS;
	}

	public Long getMerid() {
		return merid;
	}

	public void setMerid(Long merid) {
		this.merid = merid;
	}

	public List<InternationalMerchantCurrency> getMoneyTypeList() {
		return moneyTypeList;
	}

	public void setMoneyTypeList(
			List<InternationalMerchantCurrency> moneyTypeList) {
		this.moneyTypeList = moneyTypeList;
	}

	public InternationalMerchantCurrency getInternationalMerchantCurrency() {
		return internationalMerchantCurrency;
	}

	public void setInternationalMerchantCurrency(
			InternationalMerchantCurrency internationalMerchantCurrency) {
		this.internationalMerchantCurrency = internationalMerchantCurrency;
	}

}
