package com.ecpss.model.payment;

import java.io.Serializable;
import java.util.Date;


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
@Table(name = "INTERNATIONAL_TRADEINFO")
@org.hibernate.annotations.Entity(selectBeforeUpdate = true, dynamicInsert = true, dynamicUpdate = true)
@Proxy(lazy = false)
public class InternationalTradeinfo extends PriEntity implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6348641281692587065L;

	@Id
	@TableGenerator(name = "sequenceTable", table = "application_sequence", pkColumnName = "seq_name", valueColumnName = "seq_value", pkColumnValue = "seq_tradeinfo", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "sequenceTable")
	@Column(length = 20)
	private Long id;

	@Column(name = "orderNo", nullable = true, length = 50)
	private String orderNo; // Ecpss��ˮ������

	@Column(name = "merchantOrderNo", nullable = true, length = 100)
	private String merchantOrderNo; // �̻���ˮ������

	@Column(name = "merchantId", nullable = true)
	private Long merchantId; // �̻�id

	@Column(name = "tradeTime")
	private Date tradeTime; // ����ʱ��

	@Column(name = "tradeAmount", nullable = true)
	private Double tradeAmount; // ���׽���ң�

	@Column(name = "rmbAmount")
	private Double rmbAmount; // ����ҽ��

	/** ����      ռλ��     ״̬
	 * ����״̬	 1	       0 ʧ��  1 �ɹ�  2 ������   3 ȡ��  4 ��ȷ�� 5δ���� 
     * �˿�	     2	       0 δ�˿�  1���˿�  2�����˿�
	 * �ܸ�	     3	       0 δ�ܸ�   1�Ѿܸ� 
	 * ����	     4	       0 δ����   1 �Ѷ���  2 �ⶳ  3��˽ⶳ
	 * ����	     5	       0 δ����  1 �ѹ���
	 * ����	     6	       0δ����   1������
	 * ���	     7	       0 δ���   1����� 2 ���ⵥ
	 * �Ƿ񻮿�	 8	       0 δ����   1�ѻ���
	 * ��֤�����	 9	       0 δ���    1 �����
	 * ��֤�𻮿�	 10	       0 δ����    1 �ѻ���
	 * ����״̬   11         0����      1 ����  2 ���κŲ��� 3 ��Ȩ�Ų���
	 * ����ֵ״̬  12        0 �ѷ���   1 δ����  
	 * �Զ������� 13       0δ����   1�Ѵ���  
	 * �Ƿ���Ҫ�Զ�Ԥ��Ȩ14   0����Ҫ   1��Ҫ   2����  3��� 4 ��� 5 ��˲�ͨ�� 6 ���ڱ�ȡ�� 7 �Ѿ������
	 * ������״̬���Ƿ���Ҫ�Զ�����Ԥ��Ȩ��15    3.�Ѿ����������Ȩ��ɣ�
	 */
	@Column(name = "tradeState")
	private String tradeState; // ����״̬����ֶ�,����: 012121

	@Column(name = "tradeChannel", nullable = true)
	private Long tradeChannel; // �̻�ͨ��ID

	@Column(name = "tradeRate", nullable = true)
	private Long tradeRate; // ���׻���	 

	@Column(name = "balanceRate", nullable = true)
	private Long balanceRate; // �������	
	
	
	
	@Column(name = "isPicture")    
	private String isPicture;//�Ƿ��ϴ�ͼƬ
	@Column(name = "isTrackNo") 	
	private String isTrackNo;//�Ƿ��ϴ����ٵ��� 
	@Column(name = "remark") 
	private String remark;//��ע
	@Column(name = "gouduiTime") 	
	private Date gouduiTime;//����ʱ��
	@Column(name = "moneyType") 	
	private Long moneyType ;//��������
	@Column(name = "batchNo") 	
	private String batchNo;//���κ�

	@Column(name = "matterDepict") 	
	private String matterDepict;//��˱�ע
	
	@Column(name = "auditingDate") 	
	private Date auditingDate;//���ʱ��
	
	@Column(name = "protestTime") 	
	private Date protestTime;//�ܸ�ʱ��
	@Column(name = "protestPerson") 	
	private String protestPerson ;//�ܸ�������
	@Column(name = "balancetime") 	
	private Date balancetime;//����ʱ��
	@Column(name = "backCount") 	
	private  Double backCount;//�˿���
	@Column(name = "applyTime")
	private Date applyTime;   //���뻮��ʱ��

	@Column(name = "VIPTerminalNo",length=20)
	private String VIPTerminalNo;   //VIPͨ�������ն˺�
	
	@Column(name = "VIPBatchNo",length=30)
	private String VIPBatchNo;   //VIPͨ���������κ�

	@Column(name = "VIPAuthorizationNo",length=20)
	private String VIPAuthorizationNo;   //VIP��Ȩ��

	@Column(name = "VIPDisposePorson",length=20)
	private String VIPDisposePorson;   //VIP������

	@Column(name = "VIPDisposeDate",length=20)
	private Date VIPDisposeDate;   //VIP����ʱ��
	
	@Column(name = "DCCTradeCurrency",length=10)
	private String DCCTradeCurrency;
	
	@Column(name = "DCCTradeAmount")
	private Double DCCTradeAmount;
	
	/*@Column(name = "merTradeUrl",length=1000)
	private String merTradeUrl;
*/
	@Column(name = "tradeUrl",length=20)	
    private String tradeUrl;//���׵�ַ
	
	@Column(name = "returnUrl",length=20)    
    private String returnUrl;//���ص�ַ
	
	@Column(name = "ref_No",length=20)    
    private String ref_No;//���ص�ַ	
	
	@Column(name = "pre_money")
	private Double pre_money;//�����ҽ��
	
	@Column(name = "beginmoney")
	private Double beginmoney;//ԭʼ��ҽ��	
	
	@Column(name = "pre_money_rmb")
	private Double pre_money_rmb;//ԭʼRMB���	
	
	@Column(name = "boc_rrn")
	private String boc_rrn;       // ����TCͨ�� RRN ��
	
	@Column(name = "boc_invoice")
	private String boc_invoice;       // ����TCͨ�� ƾ֤��
	
	@Column(name = "boc_date")
	private String boc_date;       // ����TCͨ�� ��������
	
	@Column(name = "boc_time")
	private String boc_time;       // ����TCͨ�� ����ʱ��
	
	@Column(name = "token_id") 	
	private String token_id;//boc token
	
	@Column(name = "channelFee") 	
	private Double channelFee;//ͨ�����ӷ�
	
	@Column(name = "expordersta") 	
	private  String expordersta;//�µ�״̬

	@Column(name = "expordermess") 	
	private  String expordermess;//�µ�������Ϣ
	
	@Column(name = "csid") 	
	private  String csid;//csid���
	
	public String getCsid() {
		return csid;
	}

	public void setCsid(String csid) {
		this.csid = csid;
	}

	public String getToken_id() {
		return token_id;
	}

	public void setToken_id(String token_id) {
		this.token_id = token_id;
	}

	public String getTradeUrl() {
		return tradeUrl;            
	}

	public void setTradeUrl(String tradeUrl) {
		this.tradeUrl = tradeUrl;
	}

	public String getReturnUrl() {
		return returnUrl;
	}

	public void setReturnUrl(String returnUrl) {
		this.returnUrl = returnUrl;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getMerchantOrderNo() {
		return merchantOrderNo;
	}

	public void setMerchantOrderNo(String merchantOrderNo) {
		this.merchantOrderNo = merchantOrderNo;
	}
	public Long getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(Long merchantId) {
		this.merchantId = merchantId;
	}

	public Date getTradeTime() {
		return tradeTime;
	}

	public void setTradeTime(Date tradeTime) {
		this.tradeTime = tradeTime;
	}

	public Double getTradeAmount() {
		return tradeAmount;
	}

	public void setTradeAmount(Double tradeAmount) {
		this.tradeAmount = tradeAmount;
	}

	public Double getRmbAmount() {
		return rmbAmount;
	}

	public void setRmbAmount(Double rmbAmount) {
		this.rmbAmount = rmbAmount;
	}

	public String getTradeState() {
		return tradeState;
	}

	public void setTradeState(String tradeState) {
		this.tradeState = tradeState;
	}


	public Long getTradeChannel() {
		return tradeChannel;
	}

	public void setTradeChannel(Long tradeChannel) {
		this.tradeChannel = tradeChannel;
	}

	public Date getGouduiTime() {
		return gouduiTime;
	}

	public void setGouduiTime(Date gouduiTime) {
		this.gouduiTime = gouduiTime;
	}

	public Long getMoneyType() {
		return moneyType;
	}

	public void setMoneyType(Long moneyType) {
		this.moneyType = moneyType;
	}

	public String getBatchNo() {
		return batchNo;
	}

	public void setBatchNo(String batchNo) {
		this.batchNo = batchNo;
	}

	public String getMatterDepict() {
		return matterDepict;
	}

	public void setMatterDepict(String matterDepict) {
		this.matterDepict = matterDepict;
	}

	public Date getProtestTime() {
		return protestTime;
	}

	public void setProtestTime(Date protestTime) {
		this.protestTime = protestTime;
	}

	public String getProtestPerson() {
		return protestPerson;
	}

	public void setProtestPerson(String protestPerson) {
		this.protestPerson = protestPerson;
	}

	public Date getBalancetime() {
		return balancetime;
	}

	public void setBalancetime(Date balancetime) {
		this.balancetime = balancetime;
	}

	public Double getBackCount() {
		return backCount;
	}

	public void setBackCount(Double backCount) {
		this.backCount = backCount;
	}

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public Long getTradeRate() {
		return tradeRate;
	}

	public void setTradeRate(Long tradeRate) {
		this.tradeRate = tradeRate;
	}

	public String getIsPicture() {
		return isPicture;
	}

	public void setIsPicture(String isPicture) {
		this.isPicture = isPicture;
	}

	public String getIsTrackNo() {
		return isTrackNo;
	}

	public void setIsTrackNo(String isTrackNo) {
		this.isTrackNo = isTrackNo;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Date getApplyTime() {
		return applyTime;
	}

	public void setApplyTime(Date applyTime) {
		this.applyTime = applyTime;
	}

	public Long getBalanceRate() {
		return balanceRate;
	}

	public void setBalanceRate(Long balanceRate) {
		this.balanceRate = balanceRate;
	}

	public String getVIPBatchNo() {
		return VIPBatchNo;
	}

	public void setVIPBatchNo(String batchNo) {
		VIPBatchNo = batchNo;
	}

	public String getVIPAuthorizationNo() {
		return VIPAuthorizationNo;
	}

	public void setVIPAuthorizationNo(String authorizationNo) {
		VIPAuthorizationNo = authorizationNo;
	}


	public String getVIPDisposePorson() {
		return VIPDisposePorson;
	}

	public void setVIPDisposePorson(String disposePorson) {
		VIPDisposePorson = disposePorson;
	}

	public Date getVIPDisposeDate() {
		return VIPDisposeDate;
	}

	public void setVIPDisposeDate(Date disposeDate) {
		VIPDisposeDate = disposeDate;
	}

	/**
	 * @return the vIPTerminalNo
	 */
	public String getVIPTerminalNo() {
		return VIPTerminalNo;
	}

	/**
	 * @param terminalNo the vIPTerminalNo to set
	 */
	public void setVIPTerminalNo(String terminalNo) {
		VIPTerminalNo = terminalNo;
	}

	public Date getAuditingDate() {
		return auditingDate;
	}

	public void setAuditingDate(Date auditingDate) {
		this.auditingDate = auditingDate;
	}

	public String getDCCTradeCurrency() {
		return DCCTradeCurrency;
	}

	public void setDCCTradeCurrency(String tradeCurrency) {
		DCCTradeCurrency = tradeCurrency;
	}

	public Double getDCCTradeAmount() {
		return DCCTradeAmount;
	}

	public void setDCCTradeAmount(Double tradeAmount) {
		DCCTradeAmount = tradeAmount;
	}

	public String getRef_No() {
		return ref_No;
	}

	public void setRef_No(String ref_No) {
		this.ref_No = ref_No;
	}

	public Double getPre_money() {
		return pre_money;
	}

	public void setPre_money(Double pre_money) {
		this.pre_money = pre_money;
	}

	public Double getBeginmoney() {
		return beginmoney;
	}

	public void setBeginmoney(Double beginmoney) {
		this.beginmoney = beginmoney;
	}

	public Double getPre_money_rmb() {
		return pre_money_rmb;
	}

	public void setPre_money_rmb(Double pre_money_rmb) {
		this.pre_money_rmb = pre_money_rmb;
	}

	public String getBoc_rrn() {
		return boc_rrn;
	}

	public void setBoc_rrn(String boc_rrn) {
		this.boc_rrn = boc_rrn;
	}

	public String getBoc_invoice() {
		return boc_invoice;
	}

	public void setBoc_invoice(String boc_invoice) {
		this.boc_invoice = boc_invoice;
	}

	public String getBoc_date() {
		return boc_date;
	}

	public void setBoc_date(String boc_date) {
		this.boc_date = boc_date;
	}

	public String getBoc_time() {
		return boc_time;
	}

	public void setBoc_time(String boc_time) {
		this.boc_time = boc_time;
	}

	public Double getChannelFee() {
		return channelFee;
	}

	public void setChannelFee(Double channelFee) {
		this.channelFee = channelFee;
	}

	public String getExpordersta() {
		return expordersta;
	}

	public void setExpordersta(String expordersta) {
		this.expordersta = expordersta;
	}

	public String getExpordermess() {
		return expordermess;
	}

	public void setExpordermess(String expordermess) {
		this.expordermess = expordermess;
	}




	/**
	 * @return the merTradeUrl
	 */
	/*public String getMerTradeUrl() {
		return merTradeUrl;
	}

	*//**
	 * @param merTradeUrl the merTradeUrl to set
	 *//*
	public void setMerTradeUrl(String merTradeUrl) {
		this.merTradeUrl = merTradeUrl;
	}
*/

}
