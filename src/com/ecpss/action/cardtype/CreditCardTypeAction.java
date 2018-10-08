package com.ecpss.action.cardtype;

import java.util.Date;
import java.util.List;

import com.ecpss.action.BaseAction;
import com.ecpss.model.cardtype.InternationalCreditCardType;

public class CreditCardTypeAction extends BaseAction {
	
	private List<InternationalCreditCardType> creditCardList;
	private InternationalCreditCardType creditCardType;
	private Long carditCardId;
	private String cardName;
	/**
	 * ��ת�����ÿ��б�
	 * @return
	 */
	public String toCreditCard(){
		String hql="from InternationalCreditCardType";
		creditCardList = this.commonService.list(hql);
		this.getLoaction().setReload(true);
		return SUCCESS;
	}

	/**
	 * ��ת����ӿ�������ҳ��
	 * @return
	 */
	public String toAddCreditCard(){
		return SUCCESS;
	}
	/**
	 * ��ӿ�������
	 * @return
	 */
	public String addCreditCard(){
		creditCardType.setLastDate(new Date());
		creditCardType.setLastMan(getUserBean().getUserName());
		this.commonService.save(creditCardType);
		this.messageAction="Successful";		
		return this.OPERATE_SUCCESS;
	}
	/**
	 * ��ת���޸Ŀ�������ҳ��
	 * @return
	 */
	public String toUpdateCreditCard(){
		creditCardType = (InternationalCreditCardType) this.commonService.load(InternationalCreditCardType.class, carditCardId);
		return SUCCESS;
	}
	/**
	 * �޸Ŀ���
	 * @return
	 */
	public String updateCreditCard(){
		InternationalCreditCardType c = (InternationalCreditCardType) this.commonService.load(InternationalCreditCardType.class, carditCardId);
		c.setCardName(creditCardType.getCardName());
		c.setShortName(creditCardType.getShortName());
		c.setLastDate(new Date());
		c.setLastMan(getUserBean().getUserName());
		this.commonService.update(c);
		this.messageAction="update successful";		
		return this.OPERATE_SUCCESS;
	}
	public List<InternationalCreditCardType> getCreditCardList() {
		return creditCardList;
	}

	public void setCreditCardList(List<InternationalCreditCardType> creditCardList) {
		this.creditCardList = creditCardList;
	}

	public InternationalCreditCardType getCreditCardType() {
		return creditCardType;
	}

	public void setCreditCardType(InternationalCreditCardType creditCardType) {
		this.creditCardType = creditCardType;
	}

	public Long getCarditCardId() {
		return carditCardId;
	}

	public void setCarditCardId(Long carditCardId) {
		this.carditCardId = carditCardId;
	}

	public String getCardName() {
		return cardName;
	}

	public void setCardName(String cardName) {
		this.cardName = cardName;
	}
	
}
