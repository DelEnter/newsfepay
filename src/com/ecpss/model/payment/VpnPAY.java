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

@Entity
@Table(name = "international_VPNPAY")
@org.hibernate.annotations.Entity(selectBeforeUpdate = true, dynamicInsert = true, dynamicUpdate = true)
@Proxy(lazy = false)
public class VpnPAY {

	@Id
	@TableGenerator(name = "sequenceTable", table = "application_sequence", pkColumnName = "seq_name", valueColumnName = "seq_value", pkColumnValue = "seq_international_VPNPAY", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "sequenceTable")
	@Column(length = 20)
	private Long id;
	// ������
	@Column(name = "refNo")
	private String refNo;

	// INVOICE ��
	@Column(name = "Invoice")
	private String Invoice;

	// ����ID
	@Column(name = "tradeId")
	private Long tradeId;

	// ��������
	/*
	 * 1 �˿ 2 ���� ,3 ,Ԥ��Ȩ��4.EDC ���� 5.DCC ���� 6.��ѯ��7 DCC��ɣ�8 EDC��� 9 ϵͳ����
	 * 
	 */
	@Column(name = "operatorType")
	private String operatorType;

	// ����״̬
	/*
	 * ������
	 * 
	 */
	@Column(name = "operatorStatus")
	private String operatorStatus;
	// ������
	@Column(name = "operatorMan")
	private String operatorMan;
	// ����ʱ��
	@Column(name = "operaterTime")
	private Date operaterTime;
	// ����ʱ��
	@Column(name = "applyTime")
	private Date applyTime;
	// ���ʱ��
	@Column(name = "auditTime")
	private Date auditTime;
	// ��ע
	@Column(name = "remark")
	private String remark;

	@Column(name = "refundAmount", nullable = true)
	private Double refundAmount; // ���׽�RMB��

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getRefNo() {
		return refNo;
	}

	public void setRefNo(String refNo) {
		this.refNo = refNo;
	}

	public String getInvoice() {
		return Invoice;
	}

	public void setInvoice(String invoice) {
		Invoice = invoice;
	}

	public Long getTradeId() {
		return tradeId;
	}

	public void setTradeId(Long tradeId) {
		this.tradeId = tradeId;
	}

	public String getOperatorType() {
		return operatorType;
	}

	public void setOperatorType(String operatorType) {
		this.operatorType = operatorType;
	}

	public String getOperatorStatus() {
		return operatorStatus;
	}

	public void setOperatorStatus(String operatorStatus) {
		this.operatorStatus = operatorStatus;
	}

	public String getOperatorMan() {
		return operatorMan;
	}

	public void setOperatorMan(String operatorMan) {
		this.operatorMan = operatorMan;
	}

	public Date getOperaterTime() {
		return operaterTime;
	}

	public void setOperaterTime(Date operaterTime) {
		this.operaterTime = operaterTime;
	}

	public Date getApplyTime() {
		return applyTime;
	}

	public void setApplyTime(Date applyTime) {
		this.applyTime = applyTime;
	}

	public Date getAuditTime() {
		return auditTime;
	}

	public void setAuditTime(Date auditTime) {
		this.auditTime = auditTime;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Double getRefundAmount() {
		return refundAmount;
	}

	public void setRefundAmount(Double refundAmount) {
		this.refundAmount = refundAmount;
	}

}
