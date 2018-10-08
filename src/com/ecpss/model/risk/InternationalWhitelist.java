package com.ecpss.model.risk;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.hibernate.annotations.Proxy;



/**
 * �������ͱ�
 */
import com.ecpss.model.PriEntity;

/**
 * �����Ŀ��
 * @author jiahui
 *
 */

@Entity
@Table(name = "internationalwhitelist")
@org.hibernate.annotations.Entity(selectBeforeUpdate = true, dynamicInsert = true, dynamicUpdate = true)
@Proxy(lazy = false)
public class InternationalWhitelist extends PriEntity implements java.io.Serializable{
	private static final long	serialVersionUID	= 6348641281692587064L;
	@Id
	@TableGenerator(name = "sequenceTable", table = "application_sequence", pkColumnName = "seq_name", valueColumnName = "seq_value", pkColumnValue = "seq_whitelist", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "sequenceTable")
	@Column(length = 20)
	private Long id;
	@Column(name = "whitename")
	private String whitename; // ����������
	@Column(name = "whitetype")
	private String whitetype;//����������
	@Column(name = "risktype")
	private String risktype;//������
	@Column(name = "remark")
	private String remark;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getWhitename() {
		return whitename;
	}
	public void setWhitename(String whitename) {
		this.whitename = whitename;
	}
	public String getWhitetype() {
		return whitetype;
	}
	public void setWhitetype(String whitetype) {
		this.whitetype = whitetype;
	}
	public String getRisktype() {
		return risktype;
	}
	public void setRisktype(String risktype) {
		this.risktype = risktype;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}

}
