package com.ecpss.model.shop;

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
@Table(name = "international_mailinfo")
@org.hibernate.annotations.Entity(selectBeforeUpdate = true, dynamicInsert = true, dynamicUpdate = true)
@Proxy(lazy = false)
public class InternationalMailInfo implements Serializable {
	private static final long	serialVersionUID	= 6348641281193287064L;
	@Id
	@TableGenerator(name = "sequenceTable", table = "application_sequence", pkColumnName = "seq_name", valueColumnName = "seq_value", pkColumnValue = "seq_mailinfo", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "sequenceTable")
	@Column(length = 20)
	private Long id;
	
	@Column(name = "cardhorderEmail",length=120)
	private String cardhorderEmail;     //�ֿ�������

	@Column(name = "sendEmail",length=120)
	private String sendEmail;     //��������

	@Column(name = "mailInfo",length=1000)
	private String mailInfo;     //�ʼ�����
	/**
	 * �˿�״̬
	 * 0   �ɹ�/ʧ�ܷ��͵���Ϣ
	 * 1   �˿�
	 * 2   ���ٵ���
	 */
	@Column(name = "type",nullable = true,length=2)
	private String type;     //�˿�״̬
	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getCardhorderEmail() {
		return cardhorderEmail;
	}
	public void setCardhorderEmail(String cardhorderEmail) {
		this.cardhorderEmail = cardhorderEmail;
	}
	public String getSendEmail() {
		return sendEmail;
	}
	public void setSendEmail(String sendEmail) {
		this.sendEmail = sendEmail;
	}
	public String getMailInfo() {
		return mailInfo;
	}
	public void setMailInfo(String mailInfo) {
		this.mailInfo = mailInfo;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public static long getSerialVersionUID() {
		return serialVersionUID;
	}
	
	
	
}
