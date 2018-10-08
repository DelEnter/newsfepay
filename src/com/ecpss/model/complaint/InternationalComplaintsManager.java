package com.ecpss.model.complaint;

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

/**
 * �ͻ�Ͷ�߹���
 * @author Administrator
 *
 */
@Entity
@Table(name = "international_complaints")
@org.hibernate.annotations.Entity(selectBeforeUpdate = true, dynamicInsert = true, dynamicUpdate = true)
@Proxy(lazy = false)
public class InternationalComplaintsManager extends PriEntity implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1000000000000000005L;
	@Id
	@TableGenerator(name = "sequenceTable", table = "application_sequence", pkColumnName = "seq_name", valueColumnName = "seq_value", pkColumnValue = "seq_complaintsmanager", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "sequenceTable")
	private Long id;
	@Column(name = "merchantNo",nullable = true)
	private Long merchantNo;                   //�̻���
	@Column(name = "orderNo",nullable = true)
	private String orderNo;               //���׺�
	@Column(name = "merchantOrderNo",nullable = true)
	private String merchantOrderNo;               //�̻����׺�
	@Column(name = "cmEmail",nullable = true)
	private String cmEmail;               //����Ͷ��ʹ���ʼ�
	@Column(name = "cmType",nullable = true,length=50)
	private String cmType;               //����Ͷ��ʹ���ʼ�
	@Column(name = "processingResults",nullable = true)
	private String processingResults;     //������:0,δ����,1,�Ѵ���
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getMerchantNo() {
		return merchantNo;
	}
	public void setMerchantNo(Long merchantNo) {
		this.merchantNo = merchantNo;
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
	public String getCmEmail() {
		return cmEmail;
	}
	public void setCmEmail(String cmEmail) {
		this.cmEmail = cmEmail;
	}
	public String getProcessingResults() {
		return processingResults;
	}
	public void setProcessingResults(String processingResults) {
		this.processingResults = processingResults;
	}
	public String getCmType() {
		return cmType;
	}
	public void setCmType(String cmType) {
		this.cmType = cmType;
	}

	
}