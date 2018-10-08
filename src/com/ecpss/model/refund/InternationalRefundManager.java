package com.ecpss.model.refund;

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
@Table(name = "international_refundmanager")
@org.hibernate.annotations.Entity(selectBeforeUpdate = true, dynamicInsert = true, dynamicUpdate = true)
@Proxy(lazy = false)
public class InternationalRefundManager extends PriEntity implements Serializable {
	private static final long	serialVersionUID	= 6348641281193287064L;
	@Id
	@TableGenerator(name = "sequenceTable", table = "application_sequence", pkColumnName = "seq_name", valueColumnName = "seq_value", pkColumnValue = "seq_refundmanager", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "sequenceTable")
	@Column(length = 20)
	private Long id;
	
	@Column(name = "tradeId",nullable = true)
	private Long tradeId;     //����id
	
	@Column(name = "refundNo", nullable = true, length = 50)
	private String refundNo; // �˿���ˮ������
	/**
	 * �˿�״̬
	 * 0   δ����
	 * 1   ������
	 * 2   ���ύ
	 * 3   �����
	 * 6   ���ɹ������˿�
	 * 4   ȫ���˿�
	 * 5   �����˿�
	 */
	@Column(name = "refundState",nullable = true,length=2)
	private String refundState;     //�˿�״̬
	
	@Column(name = "refundAmount",nullable = true)
	private Double refundAmount;     //�˿���

	@Column(name = "refundRMBAmount",nullable = true)
	private Double refundRMBAmount;     //�˿���
	
	@Column(name = "applyDate",nullable = true)
	private Date applyDate;     //����ʱ��
	
	@Column(name = "auditingDate",nullable = true)
	private Date auditingDate;     //���ʱ��
	
	@Column(name = "refundDate",nullable = true)
	private Date refundDate;     //�˿�ʱ��
	
	@Column(name = "batchNo",nullable = true,length=30)
	private String batchNo;     //�˿����κ�

	@Column(name = "auditingMan",nullable = true,length=20)
	private String auditingMan;     //�����
	
	@Column(name = "remark",nullable = true)
	private String remark;     //��ע

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getTradeId() {
		return tradeId;
	}

	public void setTradeId(Long tradeId) {
		this.tradeId = tradeId;
	}

	public String getRefundState() {
		return refundState;
	}

	public void setRefundState(String refundState) {
		this.refundState = refundState;
	}

	public Date getApplyDate() {
		return applyDate;
	}

	public void setApplyDate(Date applyDate) {
		this.applyDate = applyDate;
	}

	public Date getAuditingDate() {
		return auditingDate;
	}

	public void setAuditingDate(Date auditingDate) {
		this.auditingDate = auditingDate;
	}

	public Date getRefundDate() {
		return refundDate;
	}

	public void setRefundDate(Date refundDate) {
		this.refundDate = refundDate;
	}

	public String getBatchNo() {
		return batchNo;
	}

	public void setBatchNo(String batchNo) {
		this.batchNo = batchNo;
	}

	public String getAuditingMan() {
		return auditingMan;
	}

	public void setAuditingMan(String auditingMan) {
		this.auditingMan = auditingMan;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public Double getRefundAmount() {
		return refundAmount;
	}

	public void setRefundAmount(Double refundAmount) {
		this.refundAmount = refundAmount;
	}

	public Double getRefundRMBAmount() {
		return refundRMBAmount;
	}

	public void setRefundRMBAmount(Double refundRMBAmount) {
		this.refundRMBAmount = refundRMBAmount;
	}

	public String getRefundNo() {
		return refundNo;
	}

	public void setRefundNo(String refundNo) {
		this.refundNo = refundNo;
	}
	
	
}
