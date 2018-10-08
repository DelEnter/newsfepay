package com.ecpss.model.shop;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.hibernate.annotations.Proxy;

import com.ecpss.model.PriEntity;

@Entity
@Table(name = "International_TerminalManager")
@org.hibernate.annotations.Entity(selectBeforeUpdate = true, dynamicInsert = true, dynamicUpdate = true)
@Proxy(lazy = false)
public class InternationalTerminalManager extends PriEntity implements java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 13232332L;

	@Id
	@TableGenerator(name = "sequenceTable", table = "application_sequence", pkColumnName = "seq_name", valueColumnName = "seq_value", pkColumnValue = "seq_terminalmanager", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "sequenceTable")
	@Column(length = 20)
	private Long id;
	
	@Column(name = "channelId", nullable = true)
	private Long channelId;   //ͨ��ID
	
	@Column(name = "creditCardId", nullable = true)
	private Long creditCardId; //����id
	
	@Column(name = "terminalNo", nullable = true, length = 20)
	private String terminalNo; //�ն˺�

	@Column(name = "andterminalNo", nullable = true)
	private String andterminalNo; //�����ն˺�
	
	@Column(name = "merchantNo", nullable = true, length = 20)
	private String merchantNo; //�̻���

	@Column(name = "billingAddress", nullable = true, length = 50)
	private String billingAddress; //���ն��µ��˵���ַ
	
	@Column(name = "onoff", nullable = true, length = 20)
	private String onoff; //�Ƿ�ͨ  1��ͨ  0�ر�

	@Column(name = "isuses", nullable = true, length = 2)
	private String isuses; //�Ƿ�ʹ��
	
	@Column(name = "tradeTimes")
	private Long tradeTimes; //���ױ���
	
	@Column(name = "isauto", nullable = true, length = 2)
	private String isauto; //�Ƿ��Զ�������
	@Column(name = "authcode")
	private String authcode; //��Ȩ��
	
	@Column(name = "hashcode", nullable = true, length = 2)
	private String hashcode; //HASH��	
	
	@Column(name = "netstate", nullable = true, length = 2)
	private String netstate; //��������
	
	@Column(name = "banktype", nullable = true, length = 2)
	private String banktype; //��������
	
	@Column(name = "bankbackremark")
	private String bankBackRemark; //���з��ر�ע���������ܽ��׿��ƣ�
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getChannelId() {
		return channelId;
	}

	public void setChannelId(Long channelId) {
		this.channelId = channelId;
	}

	public Long getCreditCardId() {
		return creditCardId;
	}

	public void setCreditCardId(Long creditCardId) {
		this.creditCardId = creditCardId;
	}

	public String getTerminalNo() {
		return terminalNo;
	}

	public void setTerminalNo(String terminalNo) {
		this.terminalNo = terminalNo;
	}

	public String getOnoff() {
		return onoff;
	}

	public void setOnoff(String onoff) {
		this.onoff = onoff;
	}

	public String getBillingAddress() {
		return billingAddress;
	}

	public void setBillingAddress(String billingAddress) {
		this.billingAddress = billingAddress;
	}

	public String getIsuses() {
		return isuses;
	}

	public void setIsuses(String isuses) {
		this.isuses = isuses;
	}

	public String getMerchantNo() {
		return merchantNo;
	}

	public void setMerchantNo(String merchantNo) {
		this.merchantNo = merchantNo;
	}

	public Long getTradeTimes() {
		return tradeTimes;
	}

	public void setTradeTimes(Long tradeTimes) {
		this.tradeTimes = tradeTimes;
	}

	public String getAndterminalNo() {
		return andterminalNo;
	}

	public void setAndterminalNo(String andterminalNo) {
		this.andterminalNo = andterminalNo;
	}

	public String getIsauto() {
		return isauto;
	}

	public void setIsauto(String isauto) {
		this.isauto = isauto;
	}

	public String getHashcode() {
		return hashcode;
	}

	public void setHashcode(String hashcode) {
		this.hashcode = hashcode;
	}

	public String getAuthcode() {
		return authcode;
	}

	public void setAuthcode(String authcode) {
		this.authcode = authcode;
	}

	public String getNetstate() {
		return netstate;
	}

	public void setNetstate(String netstate) {
		this.netstate = netstate;
	}

	public String getBanktype() {
		return banktype;
	}

	public void setBanktype(String banktype) {
		this.banktype = banktype;
	}

	public String getBankBackRemark() {
		return bankBackRemark;
	}

	public void setBankBackRemark(String bankBackRemark) {
		this.bankBackRemark = bankBackRemark;
	}


	
}