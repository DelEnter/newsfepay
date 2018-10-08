package com.ecpss.model.channel;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Proxy;

import com.ecpss.model.PriEntity;

/**
 * �̻�ͨ������
 * @author Administrator
 *
 */
@Entity
@Table(name = "international_merchantChannels")
@org.hibernate.annotations.Entity(selectBeforeUpdate = true, dynamicInsert = true, dynamicUpdate = true)
@Proxy(lazy = false)
public class InternationalMerchantChannels extends PriEntity implements Serializable {
	private static final long	serialVersionUID	= 6348643438693287064L;
	@Id
	@TableGenerator(name = "sequenceTable", table = "application_sequence", pkColumnName = "seq_name", valueColumnName = "seq_value", pkColumnValue = "seq_merchantchannel", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "sequenceTable")
	@Column(length = 20)
	private Long id;
	
	@Column(name = "merchantId",nullable = true)
	private Long merchantId;     //�̻�
	
	@Column(name = "channelId",nullable = true)
	private Long channelId;     //ͨ��
	
	@Column(name = "executeTime")
	private Date executeTime;     //ִ��ʱ��
	
	@Column(name = "operator",nullable = true,length=50)
	private String operator;     //�Ʊ���
	
	@Column(name = "onoff",nullable = true,length=2)
	private String onoff;     //�Ƿ��   0:�ر�  1:��

	@Column(name = "balanceCycle",nullable = true)
	private Long balanceCycle;     //ͨ����������
	
	@Column(name = "balanceCharge",nullable = true)
	private Double balanceCharge;     //ͨ��������������
	
	@Column(name = "bailCycle",nullable = true)
	private Long bailCycle;     //ͨ����֤���������
	
	@Column(name = "bailCharge",nullable = true)
	private Double bailCharge;     //ͨ����֤�������������
	
	private String showbailCharge;//��ʾ�ı�֤����
	
	private String showbalanceCharge;//��ʾ����������
	
	@Column(name = "maxmind_lv1")
	private Double maxmind_lv1;
	
	@Column(name = "maxmind_lv2")
	private Double maxmind_lv2;
	
	@Column(name = "priority")
	private String priority;
	
	@Column(name = "channelFee")
	private Double channelFee;	//ͨ�����ӷ�
	
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

	public Date getExecuteTime() {
		return executeTime;
	}

	public void setExecuteTime(Date executeTime) {
		this.executeTime = executeTime;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public String getOnoff() {
		return onoff;
	}

	public void setOnoff(String onoff) {
		this.onoff = onoff;
	}

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public Long getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(Long merchantId) {
		this.merchantId = merchantId;
	}

	public Long getBalanceCycle() {
		return balanceCycle;
	}

	public void setBalanceCycle(Long balanceCycle) {
		this.balanceCycle = balanceCycle;
	}

	public Double getBalanceCharge() {
		return balanceCharge;
	}

	public void setBalanceCharge(Double balanceCharge) {
		this.balanceCharge = balanceCharge;
	}

	public Long getBailCycle() {
		return bailCycle;
	}

	public void setBailCycle(Long bailCycle) {
		this.bailCycle = bailCycle;
	}

	public Double getBailCharge() {
		return bailCharge;
	}

	public void setBailCharge(Double bailCharge) {
		this.bailCharge = bailCharge;
	}

	public String getShowbailCharge() {
		
		return this.getBailCharge().toString();
	}

	public String getShowbalanceCharge() {
		return this.getBalanceCharge().toString();
	}

	public Double getMaxmind_lv1() {
		return maxmind_lv1;
	}

	public void setMaxmind_lv1(Double maxmind_lv1) {
		this.maxmind_lv1 = maxmind_lv1;
	}

	public Double getMaxmind_lv2() {
		return maxmind_lv2;
	}

	public void setMaxmind_lv2(Double maxmind_lv2) {
		this.maxmind_lv2 = maxmind_lv2;
	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public Double getChannelFee() {
		return channelFee;
	}

	public void setChannelFee(Double channelFee) {
		this.channelFee = channelFee;
	}
}