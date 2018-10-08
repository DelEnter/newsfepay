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

//����ͳ�Ʊ�
@Entity
@Table(name = "InternationalSettlment")
@org.hibernate.annotations.Entity(selectBeforeUpdate = true, dynamicInsert = true, dynamicUpdate = true)
@Proxy(lazy = false)
public class InternationalSettlment {
	@Id
	@TableGenerator(name = "sequenceTable", table = "application_sequence", pkColumnName = "seq_name", valueColumnName = "seq_value", pkColumnValue = "seq_InternationalSettlment", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "sequenceTable")
	@Column(length = 20)
	private Long id;
	@Column(name = "merchantno")
	private Long merchantno;
	@Column(name = "batchno")
	private Long batchno;
	@Column(name = "ordercount")
	private Double ordercount;//���׽��(δ�����)
	@Column(name = "trademoneyname")
	private String trademoneyname;//���ױ���
	@Column(name = "refundmentmoney")
	private Double refundmentmoney;//������
	@Column(name = "huakuanbankname")    
	private String huakuanbankname;//��������
	@Column(name = "riskFee")    
	private Double riskFee;//��ط���
	@Column(name = "channelFee")    
	private Double channelFee;//����
	@Column(name = "huakuantime")
	private Date huakuantime;
	@Column(name = "createtabletime")
	private Date createtabletime;
	@Column(name = "remark")
	private String remark;
	@Column(name = "freezecount")
	private Double freezecount;//����RMB���
	@Column(name = "istrue")
	private String istrue;//0 δ���   1 ���ͨ��	
	
	@Column(name = "disposeman")
	private String disposeman;//������

	@Column(name = "disposedate")
	private Date disposedate;//����date
	
	public String getIstrue() {
		return istrue;
	}

	public void setIstrue(String istrue) {
		this.istrue = istrue;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getMerchantno() {
		return merchantno;
	}

	public void setMerchantno(Long merchantno) {
		this.merchantno = merchantno;
	}

	public Long getBatchno() {
		return batchno;
	}

	public void setBatchno(Long batchno) {
		this.batchno = batchno;
	}

	public Double getOrdercount() {
		return ordercount;
	}

	public void setOrdercount(Double ordercount) {
		this.ordercount = ordercount;
	}

	public String getTrademoneyname() {
		return trademoneyname;
	}

	public void setTrademoneyname(String trademoneyname) {
		this.trademoneyname = trademoneyname;
	}

	public Double getRefundmentmoney() {
		return refundmentmoney;
	}

	public void setRefundmentmoney(Double refundmentmoney) {
		this.refundmentmoney = refundmentmoney;
	}


	public Date getHuakuantime() {
		return huakuantime;
	}

	public void setHuakuantime(Date huakuantime) {
		this.huakuantime = huakuantime;
	}



	public Date getCreatetabletime() {
		return createtabletime;
	}

	public void setCreatetabletime(Date createtabletime) {
		this.createtabletime = createtabletime;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Double getFreezecount() {
		return freezecount;
	}

	public void setFreezecount(Double freezecount) {
		this.freezecount = freezecount;
	}

	public String getHuakuanbankname() {
		return huakuanbankname;
	}

	public void setHuakuanbankname(String huakuanbankname) {
		this.huakuanbankname = huakuanbankname;
	}

	public String getDisposeman() {
		return disposeman;
	}

	public void setDisposeman(String disposeman) {
		this.disposeman = disposeman;
	}

	public Date getDisposedate() {
		return disposedate;
	}

	public void setDisposedate(Date disposedate) {
		this.disposedate = disposedate;
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
