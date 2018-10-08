package com.ecpss.model.payment;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.hibernate.annotations.Proxy;
//����Ԥ����
@Entity
@Table(name = "international_ViewSettlement")
@org.hibernate.annotations.Entity(selectBeforeUpdate = true, dynamicInsert = true, dynamicUpdate = true)
@Proxy(lazy = false)
public class ViewSettlement {

	@Id
	@TableGenerator(name = "sequenceTable", table = "application_sequence", pkColumnName = "seq_name", valueColumnName = "seq_value", pkColumnValue = "seq_ViewSettlement", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "sequenceTable")
	@Column(length = 20)
	private Long id;
	@Column(name = "batchno")
	private Long batchno;// ���κ�
	
	private Double balancemoney;//Ӧ����׽��
	
	/**
	 * δ�����˿�ܸ�����
	 */
	@Column(name = "refuce")
	private int refuce; 
	/**
	 * δ�����˿�ܸ�(�˿��)�Ľ��	
	 */
	@Column(name = "refuceMoney")
	private Double refuceMoney = 0d; 
	/**
	 * δ�����˿�ܸ�(���ײ���)�Ľ��
	 */
	@Column(name = "refuceMoneyAll")
	private Double refuceMoneyAll=0d;// 	
	/**
	 * �ѻ����˿�ܸ�
	 */
	@Column(name = "refuceComplement")
	private int refuceComplement;//
	/**
	 * �ѻ����˿�ܸ����˿�֣����
	 */
	@Column(name = "refuceComplementMoney")
	private Double refuceComplementMoney=0d;//
	/**
	 * �ѻ����˿�ܸ������ײ��֣����
	 */
	@Column(name = "refuceComplementMoneyAll")
	private Double refuceComplementMoneyAll=0d;//	
	
	
	@Column(name = "tradeNumber")
	private int tradeNumber; // �ɹ����ױ���
	@Column(name = "tradeMoney")
	private Double tradeMoney = 0d; // �ɹ����׽��
	@Column(name = "tradeNumberAll")	
	private int tradeNumberAll; // δ���㽻�ױ���
	@Column(name = "tradeMoneyAll")
	private Double tradeMoneyAll = 0d; // δ���㽻�׽��
	@Column(name = "noTuikuanNumber")
	private int noTuikuanNumber; // δ���㱾�ڲ����˿����
	@Column(name = "noTuiKuanMoney")
	private Double noTuiKuanMoney = 0d; // δ���㱾�ڲ����˿���
	@Column(name = "noTuikuanNumberall")
	private int noTuikuanNumberall; // δ���㱾��ȫ���˿����
	@Column(name = "noTuiKuanMoneyall")
	private Double noTuiKuanMoneyall = 0d; // δ���㱾��ȫ���˿���
	@Column(name = "tuiKuanMoney")
	private Double tuiKuanMoney = 0d; // �ѽ��㱾���˿���
	@Column(name = "tuiKuanNumber")
	private int tuiKuanNumber; // �ѽ��㱾���˿����
	@Column(name = "tuiKuanMoneyall")
	private Double tuiKuanMoneyall = 0d; // �ѽ��㱾�ڲ����˿���
	@Column(name = "tuiKuanNumberall")
	private int tuiKuanNumberall; // �ѽ��㱾��ȫ���˿����
	@Column(name = "freezeMoney")
	private Double freezeMoney = 0d; // �ѽ��㱾�ڶ�����
	@Column(name = "freezeNumber")
	private int freezeNumber; // �ѽ��㱾�ڶ������
	@Column(name = "thawMoney")
	private Double thawMoney = 0d; // �ⶳ���׽��
	@Column(name = "thawNumber")
	private int thawNumber; // �ⶳ���ױ���
	@Column(name = "protestMoney")
	private Double protestMoney = 0d; // �ѽ��㱾�ھܸ����
	@Column(name = "protestNumber")
	private int protestNumber; // �ѽ��㱾�ھܸ�����
	@Column(name = "noProtestMoney")
	private Double noProtestMoney = 0d; // δ���㱾�ھܸ����
	@Column(name = "noProtestNumber")
	private int noProtestNumber; // δ���㱾�ھܸ�����
	@Column(name = "procedureRate")
	private Double procedureRate = 0d; // ��������
	@Column(name = "protestFee")
	private Double protestFee = 0d; // �ܸ������
	@Column(name = "procedureFee")
	private Double procedureFee = 0d; // ������
	@Column(name = "bailmoney")
	private Double bailmoney = 0d; // ��֤����
	@Column(name = "bailrate")
	private Double bailrate = 0d; // ��֤�����
	@Column(name = "proceFeeRation")
	private Double proceFeeRation = 0d; // �����Ѷ���
	@Column(name = "tradeMoneykindName")
	private String tradeMoneykindName; // ���ױ�������
	@Column(name = "balancerate")
	private Double balancerate = 0d; // �������
	@Column(name = "channels")
	private String channels; // ͨ��
	@Column(name = "exchangedate")
	private Date exchangedate; // �����������
	@Column(name = "nowBalanceRate")
	private Double nowBalanceRate = 0d; // ���㵱��Ļ���(����ܸ�����ѵ�ʱ����)
	@Column(name = "rmbmoney")
	private Double rmbmoney = 0d; // ÿ�����ʶ�Ӧ��RMB���
	
	@Column(name = "riskFee")    
	private Double riskFee;//��ط���
	@Column(name = "channelFee")    
	private Double channelFee;//����
	
	private String rateTostring;

	private String produceRate;
	// Constructors

	public String getProduceRate() {
		return this.getProcedureRate().toString();
	}

	public String getRateTostring() {
		return this.getBalancerate().toString();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getTradeNumber() {
		return tradeNumber;
	}

	public void setTradeNumber(int tradeNumber) {
		this.tradeNumber = tradeNumber;
	}

	public Double getTradeMoney() {
		return tradeMoney;
	}

	public void setTradeMoney(Double tradeMoney) {
		this.tradeMoney = tradeMoney;
	}

	public int getTradeNumberAll() {
		return tradeNumberAll;
	}

	public void setTradeNumberAll(int tradeNumberAll) {
		this.tradeNumberAll = tradeNumberAll;
	}

	public Double getTradeMoneyAll() {
		return tradeMoneyAll;
	}

	public void setTradeMoneyAll(Double tradeMoneyAll) {
		this.tradeMoneyAll = tradeMoneyAll;
	}

	public int getNoTuikuanNumber() {
		return noTuikuanNumber;
	}

	public void setNoTuikuanNumber(int noTuikuanNumber) {
		this.noTuikuanNumber = noTuikuanNumber;
	}

	public Double getNoTuiKuanMoney() {
		return noTuiKuanMoney;
	}

	public void setNoTuiKuanMoney(Double noTuiKuanMoney) {
		this.noTuiKuanMoney = noTuiKuanMoney;
	}

	public int getNoTuikuanNumberall() {
		return noTuikuanNumberall;
	}

	public void setNoTuikuanNumberall(int noTuikuanNumberall) {
		this.noTuikuanNumberall = noTuikuanNumberall;
	}

	public Double getNoTuiKuanMoneyall() {
		return noTuiKuanMoneyall;
	}

	public void setNoTuiKuanMoneyall(Double noTuiKuanMoneyall) {
		this.noTuiKuanMoneyall = noTuiKuanMoneyall;
	}

	public Double getTuiKuanMoney() {
		return tuiKuanMoney;
	}

	public void setTuiKuanMoney(Double tuiKuanMoney) {
		this.tuiKuanMoney = tuiKuanMoney;
	}

	public int getTuiKuanNumber() {
		return tuiKuanNumber;
	}

	public void setTuiKuanNumber(int tuiKuanNumber) {
		this.tuiKuanNumber = tuiKuanNumber;
	}

	public Double getTuiKuanMoneyall() {
		return tuiKuanMoneyall;
	}

	public void setTuiKuanMoneyall(Double tuiKuanMoneyall) {
		this.tuiKuanMoneyall = tuiKuanMoneyall;
	}

	public int getTuiKuanNumberall() {
		return tuiKuanNumberall;
	}

	public void setTuiKuanNumberall(int tuiKuanNumberall) {
		this.tuiKuanNumberall = tuiKuanNumberall;
	}

	public Double getFreezeMoney() {
		return freezeMoney;
	}

	public void setFreezeMoney(Double freezeMoney) {
		this.freezeMoney = freezeMoney;
	}

	public int getFreezeNumber() {
		return freezeNumber;
	}

	public void setFreezeNumber(int freezeNumber) {
		this.freezeNumber = freezeNumber;
	}

	public Double getThawMoney() {
		return thawMoney;
	}

	public void setThawMoney(Double thawMoney) {
		this.thawMoney = thawMoney;
	}

	public int getThawNumber() {
		return thawNumber;
	}

	public void setThawNumber(int thawNumber) {
		this.thawNumber = thawNumber;
	}

	public Double getProtestMoney() {
		return protestMoney;
	}

	public void setProtestMoney(Double protestMoney) {
		this.protestMoney = protestMoney;
	}

	public int getProtestNumber() {
		return protestNumber;
	}

	public void setProtestNumber(int protestNumber) {
		this.protestNumber = protestNumber;
	}

	public Double getNoProtestMoney() {
		return noProtestMoney;
	}

	public void setNoProtestMoney(Double noProtestMoney) {
		this.noProtestMoney = noProtestMoney;
	}

	public int getNoProtestNumber() {
		return noProtestNumber;
	}

	public void setNoProtestNumber(int noProtestNumber) {
		this.noProtestNumber = noProtestNumber;
	}

	public Double getProcedureRate() {
		return procedureRate;
	}

	public void setProcedureRate(Double procedureRate) {
		this.procedureRate = procedureRate;
	}

	public Double getProtestFee() {
		return protestFee;
	}

	public void setProtestFee(Double protestFee) {
		this.protestFee = protestFee;
	}

	public Double getProcedureFee() {
		return procedureFee;
	}

	public void setProcedureFee(Double procedureFee) {
		this.procedureFee = procedureFee;
	}

	public Double getBailmoney() {
		return bailmoney;
	}

	public void setBailmoney(Double bailmoney) {
		this.bailmoney = bailmoney;
	}

	public Double getBailrate() {
		return bailrate;
	}

	public void setBailrate(Double bailrate) {
		this.bailrate = bailrate;
	}

	public Double getProceFeeRation() {
		return proceFeeRation;
	}

	public void setProceFeeRation(Double proceFeeRation) {
		this.proceFeeRation = proceFeeRation;
	}

	public String getTradeMoneykindName() {
		return tradeMoneykindName;
	}

	public void setTradeMoneykindName(String tradeMoneykindName) {
		this.tradeMoneykindName = tradeMoneykindName;
	}

	public Double getBalancerate() {
		return balancerate;
	}

	public void setBalancerate(Double balancerate) {
		this.balancerate = balancerate;
	}

	public String getChannels() {
		return channels;
	}

	public void setChannels(String channels) {
		this.channels = channels;
	}

	public Date getExchangedate() {
		return exchangedate;
	}

	public void setExchangedate(Date exchangedate) {
		this.exchangedate = exchangedate;
	}

	public Double getNowBalanceRate() {
		return nowBalanceRate;
	}

	public void setNowBalanceRate(Double nowBalanceRate) {
		this.nowBalanceRate = nowBalanceRate;
	}

	public Double getRmbmoney() {
		return rmbmoney;
	}

	public void setRmbmoney(Double rmbmoney) {
		this.rmbmoney = rmbmoney;
	}

	public Long getBatchno() {
		return batchno;
	}

	public void setBatchno(Long batchno) {
		this.batchno = batchno;
	}

	public int getRefuce() {
		return refuce;
	}

	public void setRefuce(int refuce) {
		this.refuce = refuce;
	}

	public Double getRefuceMoney() {
		return refuceMoney;
	}

	public void setRefuceMoney(Double refuceMoney) {
		this.refuceMoney = refuceMoney;
	}

	public Double getRefuceMoneyAll() {
		return refuceMoneyAll;
	}

	public void setRefuceMoneyAll(Double refuceMoneyAll) {
		this.refuceMoneyAll = refuceMoneyAll;
	}

	public int getRefuceComplement() {
		return refuceComplement;
	}

	public void setRefuceComplement(int refuceComplement) {
		this.refuceComplement = refuceComplement;
	}

	public Double getRefuceComplementMoney() {
		return refuceComplementMoney;
	}

	public void setRefuceComplementMoney(Double refuceComplementMoney) {
		this.refuceComplementMoney = refuceComplementMoney;
	}

	public Double getRefuceComplementMoneyAll() {
		return refuceComplementMoneyAll;
	}

	public void setRefuceComplementMoneyAll(Double refuceComplementMoneyAll) {
		this.refuceComplementMoneyAll = refuceComplementMoneyAll;
	}

	public Double getBalancemoney() {
		return balancemoney;
	}

	public void setBalancemoney(Double balancemoney) {
		this.balancemoney = balancemoney;
	}

	public Double getRiskFee() {
		return riskFee;
	}

	public void setRiskFee(Double riskFee) {
		this.riskFee = riskFee;
	}

	public Double getChannelFee() {
		return channelFee;
	}

	public void setChannelFee(Double channelFee) {
		this.channelFee = channelFee;
	}
}
