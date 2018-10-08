package com.ecpss.vo.channel;

import java.util.Date;

import javax.persistence.Column;

import com.ecpss.model.PriEntity;
import com.ecpss.util.DateUtil;

public class InternationalWebchannelsLog extends PriEntity {

	private Long id;

	private Long merchantId; // �̻�

	private Long channelId; // ͨ��

	private Date executeTime; // ִ��ʱ��

	private String operator; // �Ʊ���

	private String onoff; // �Ƿ�� 0:�ر� 1:��

	private Long balanceCycle; // ͨ����������

	private String channelName; // ͨ������

	private Double balanceCharge; // ͨ��������������

	private Long bailCycle; // ͨ����֤���������

	private Double bailCharge; // ͨ����֤�������������

	private Long merchantIdafter;

	private Long channelIdafter;

	private Date executeTimeafter;

	private String operatorafter;

	private String onoffafter;

	private Long balanceCycleafter;

	private Double balanceChargeafter;

	private Long bailCycleafter;

	private Double bailChargeafter;

	private String merno; // �̻���

	private String channelNameatter; // ͨ������

	public String getUpdateInternationalWebchannelsLog() {
		StringBuffer sb = new StringBuffer("���õ��̻�Ϊ��" + this.merno + "\r\n");
		if (this.bailCycle != null
				&& !(this.bailCycle.equals(this.bailCycleafter))) {
			sb.append("��֤���������:" + this.bailCycle + "------------>"
					+ this.bailCycleafter + "\r\n");
		}
		if(this.balanceCharge != null && !(this.balanceCharge.equals(this.balanceChargeafter))) {
			sb.append("��֤��������:" + this.bailCharge + "------------>"
					+ this.bailChargeafter + "\r\n");
			
		}
		
		if (this.balanceCycle != null
				&& !(this.balanceCycle.equals(this.balanceCycleafter))) {
			sb.append("��������:" + this.balanceCycle + "------------>"
					+ this.balanceCycleafter + "\r\n");
		}
		// ------------------------------------
		if (this.channelId != null
				&& !(this.channelId.equals(this.channelIdafter))) {
			 sb.append("ͨ�����:" + this.channelId + "------------>"
			 + this.channelIdafter + "\r\n");
//			sb.append("ͨ��:" + this.channelName + "------------>"
//					+ this.channelNameatter + "\r\n");
		}

		if (this.executeTime != null
				&& !(this.executeTime.equals(this.executeTimeafter))) {

			sb.append("ִ��ʱ��:" + DateUtil.formatDateTime(this.executeTime) + "------------>"
					+ DateUtil.formatDateTime(this.executeTimeafter) + "\r\n");
		}
		// ----------------------------------------------------
		if (this.merchantId != null
				& !(this.merchantId.equals(this.merchantIdafter))) {
			sb.append("�̻�:" + this.merchantId + "------------>"
					+ this.merchantIdafter + "\r\n");
		}
		if (this.onoff != null && !(this.onoff.equals(this.onoffafter))) {
			sb.append("�Ƿ��:" + this.onoff + "------------>" + this.onoffafter
					+ "\r\n");
		}
		if (this.operator != null
				&& !(this.operator.equals(this.operatorafter))) {
			sb.append("�Ʊ���:" + this.operator + "------------>"
					+ this.operatorafter + "\r\n");
		}
		System.out.println(sb);
		return sb.toString();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(Long merchantId) {
		this.merchantId = merchantId;
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

	public Long getMerchantIdafter() {
		return merchantIdafter;
	}

	public void setMerchantIdafter(Long merchantIdafter) {
		this.merchantIdafter = merchantIdafter;
	}

	public Long getChannelIdafter() {
		return channelIdafter;
	}

	public void setChannelIdafter(Long channelIdafter) {
		this.channelIdafter = channelIdafter;
	}

	public Date getExecuteTimeafter() {
		return executeTimeafter;
	}

	public void setExecuteTimeafter(Date executeTimeafter) {
		this.executeTimeafter = executeTimeafter;
	}

	public String getOperatorafter() {
		return operatorafter;
	}

	public void setOperatorafter(String operatorafter) {
		this.operatorafter = operatorafter;
	}

	public String getOnoffafter() {
		return onoffafter;
	}

	public void setOnoffafter(String onoffafter) {
		this.onoffafter = onoffafter;
	}

	public Long getBalanceCycleafter() {
		return balanceCycleafter;
	}

	public void setBalanceCycleafter(Long balanceCycleafter) {
		this.balanceCycleafter = balanceCycleafter;
	}

	public Double getBalanceChargeafter() {
		return balanceChargeafter;
	}

	public void setBalanceChargeafter(Double balanceChargeafter) {
		this.balanceChargeafter = balanceChargeafter;
	}

	public Long getBailCycleafter() {
		return bailCycleafter;
	}

	public void setBailCycleafter(Long bailCycleafter) {
		this.bailCycleafter = bailCycleafter;
	}

	public Double getBailChargeafter() {
		return bailChargeafter;
	}

	public void setBailChargeafter(Double bailChargeafter) {
		this.bailChargeafter = bailChargeafter;
	}

	public String getMerno() {
		return merno;
	}

	public void setMerno(String merno) {
		this.merno = merno;
	}

	public String getChannelNameatter() {
		return channelNameatter;
	}

	public void setChannelNameatter(String channelNameatter) {
		this.channelNameatter = channelNameatter;
	}

	public String getChannelName() {
		return channelName;
	}

	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}

}
