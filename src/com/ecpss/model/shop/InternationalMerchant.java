package com.ecpss.model.shop;

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
@Table(name = "INTERNATIONAL_MERCHANT")
@org.hibernate.annotations.Entity(selectBeforeUpdate = true, dynamicInsert = true, dynamicUpdate = true)
@Proxy(lazy = false)
public class InternationalMerchant implements java.io.Serializable{
	@Id
	@TableGenerator(name = "sequenceTable", table = "application_sequence", pkColumnName = "seq_name", valueColumnName = "seq_value", pkColumnValue = "seq_merchant", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "sequenceTable")
	@Column(length = 20)
	private Long id;
	
	@Column(name = "merno", nullable = true, length = 6)
	private Long merno;//�̻���
	
	@Column(name = "username", nullable = true, length = 20)
	private String username;//��¼��
	
	@Column(name = "password", nullable = true, length = 50)
	private String password;//����
	
	@Column(name = "md5key", nullable = true, length = 20)
	private String md5key;//md5k
	
	@Column(name = "mername", nullable = true, length = 20)
	private String mername;//�̻���
	
	@Column(name = "certificateno", nullable = true, length = 20)
	private String certificateno;//���֤��
	
	@Column(name = "meradress", nullable = true, length = 100)
	private String meradress;//�̻���ַ
	
	@Column(name = "merphone", nullable = true, length = 20)
	private String merphone;//�̻��绰
	
	@Column(name = "merfax", nullable = true, length = 20)
	private String merfax;//�̻�����
	
	@Column(name = "mermobile", nullable = true, length = 15)
	private String mermobile;//�̻�
	
	@Column(name = "meremail", nullable = true, length = 50)
	private String meremail;//�̻�����
	
	@Column(name = "merqq", nullable = true, length = 15)
	private String merqq;//�̻�QQ
	
	@Column(name = "mermsn", nullable = true, length = 20)
	private String mermsn;//�̻�msn
	
	@Column(name = "website", nullable = true, length = 100)
	private String website;	//�̻���ַ
	
	@Column(name = "mertype", nullable = true, length = 2)
	private Long mertype;//�˻�����
	
	@Column(name = "bank", nullable = true, length = 20)
	private String bank;//��������
	
	@Column(name = "accountname", nullable = true, length = 20)
	private String accountname;//������
	
	@Column(name = "cardno", nullable = true, length = 20)
	private String cardno;//�����˺�
	
	@Column(name = "businesstype", nullable = true, length = 2)
	private Long businesstype;//�˻�����
	
	@Column(name = "businessyears", nullable = true, length = 3)
	private Long businessyears;//��ҵ����
	
	@Column(name = "gatheringway", nullable = true, length = 100)
	private String gatheringway;//��ǰ���տʽ

	@Column(name = "linkman", nullable = true, length = 20)
	private String linkman;//��ϵ��
	
	@Column(name = "linkmanmobile", nullable = true, length = 15)
	private String linkmanmobile;//��ϵ�ֻ�
	
	@Column(name = "linkmanadress", nullable = true, length = 100)
	private String linkmanadress;//��ϵ��ַ
	
	@Column(name = "linkmanphone", nullable = true, length = 15)
	private String linkmanphone;//��ϵ���ֻ���
	
	@Column(name = "linkmanfax", nullable = true, length = 15)
	private String linkmanfax;//��ϵ�˴���
	
	@Column(name = "linkmanemail", nullable = true, length = 20)
	private String linkmanemail;//��ϵ������
	
	@Column(name = "linkmanqq", nullable = true, length = 20)
	private String linkmanqq;//��ϵ��QQ
	
	@Column(name = "linkmanmsn", nullable = true, length = 20)
	private String linkmanmsn;//��ϵ��msn
	
	@Column(name = "linkmancertificateno", nullable = true, length = 20)
	private String linkmancertificateno;//
	
	@Column(name = "salesman", nullable = true, length = 20)
	private String salesman;//ҵ��Ա
	
	@Column(name = "mertypename", nullable = true, length = 20)
	private String mertypename;//�̻�����
	
	@Column(name = "businesstypename", nullable = true, length = 20)
	private String businesstypename;//��ҵ����
	
	//0 δ��ͨ��1��ͨ
	@Column(name = "isopen", nullable = true, length = 20)
	private String isopen;	
	/* ռλ                 ״̬                                     ����
	 * 1                 �Ƿ��͸��ٵ����ʼ�                   0 �����ʼ���1 �������ʼ�
	 * 2                �Ƿ��ͻ�ֹ��                        0������  1 ����
	 * 3                ���Ϳͷ���ϵ��ʽ                        0����    1 ������
	 * 4              ֧��ʱ�Ƿ���ʾ����                        0��ʾ    1����ʾ
	 * 5              �ɹ����Ƿ����ʼ����̻�                   0 ���ͣ�1 ������
	 * 6             �ܸ��������Ƿ����ʼ����̻�                0����  1������
	 * 7              ʵʱͨ���Ƿ������                        0 Ĭ��  1 ֱ��ʧ��
	 */
	@Column(name = "statutes", nullable = true, length = 20)
	private String statutes;		
	
	@Column(name = "errorCount")
	private Long errorCount;
	
	@Column(name = "loginTime")
	private Date loginTime;    //�ϴε�½ʱ��
	
	@Column(name = "modifyPwdTime")
	private Date modifyPwdTime;    //���һ���޸�����ʱ��   3���²��޸����������޸�����
	
	@Column(name = "monthTradeMoney")
	private Double monthTradeMoney;       //�̻��½����޶�
	
	@Column(name = "processModifyDate")
	private Date processModifyDate;       //�̻����һ���޸Ĵ�����״̬����

	@Column(name = "processModifyCount")
	private Long processModifyCount;       //�̻����һ���޸Ĵ�����״̬����
	
	//�¼��ֶ�
	
	@Column(name = "openTime")			//�̻���ͨʱ��
	private Date openTime;
	
	@Column(name = "annualFee")			//�̻����
	private Double annualFee;
	
	@Column(name = "remark")			//��ע
	private String remark;
		
	@Column(name = "expmerchantno", nullable = true, length = 10)
	private Long expmerchantno;								//�����̻���
	
	@Column(name = "expuername", nullable = true, length = 20)
	private String expuername;								    //�û���
	
	@Column(name = "exppassword", nullable = true, length = 500)
	private String exppassword;									//����
	
	@Column(name = "expopenstatus", nullable = true, length = 5)
	private String expopenstatus;                               //�Ƿ�ͨ��������
	
	@Column(name = "discountfee", nullable = true, length = 10)
	private String discountfee;                                 //�ۿ���
	
	@Column(name = "exppay", nullable = true, length = 10)
	private String exppay;										//�Ƿ�������������
	
	@Column(name = "exppaystatus", nullable = true, length = 10)
	private String exppaystatus;									////�Ƿ�ͨ��������
	
	@Column(name = "assuretime", nullable = true, length = 10)
	private Long assuretime;									//������
	
	@Column(name = "uploadFileName", nullable = true, length = 500)
	private String uploadFileName;	//���֤��Ƭ·��
	
	@Column(name = "Dishonor")//�ܸ�������
	private Double Dishonor;
	
	public Double getDishonor() {
		return Dishonor;
	}
	public void setDishonor(Double dishonor) {
		Dishonor = dishonor;
	}
	public String getUploadFileName() {
		return uploadFileName;
	}
	public void setUploadFileName(String uploadFileName) {
		this.uploadFileName = uploadFileName;
	}
	public Long getExpmerchantno() {
		return expmerchantno;
	}
	public void setExpmerchantno(Long expmerchantno) {
		this.expmerchantno = expmerchantno;
	}
	public String getExpuername() {
		return expuername;
	}
	public void setExpuername(String expuername) {
		this.expuername = expuername;
	}
	public String getExppassword() {
		return exppassword;
	}
	public void setExppassword(String exppassword) {
		this.exppassword = exppassword;
	}
	public String getExpopenstatus() {
		return expopenstatus;
	}
	public void setExpopenstatus(String expopenstatus) {
		this.expopenstatus = expopenstatus;
	}
	public String getDiscountfee() {
		return discountfee;
	}
	public void setDiscountfee(String discountfee) {
		this.discountfee = discountfee;
	}
	public Long getMerno() {
		return merno;
	}
	public void setMerno(Long merno) {
		this.merno = merno;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getMername() {
		return mername;
	}
	public void setMername(String mername) {
		this.mername = mername;
	}
	public String getCertificateno() {
		return certificateno;
	}
	public void setCertificateno(String certificateno) {
		this.certificateno = certificateno;
	}
	public String getMeradress() {
		return meradress;
	}
	public void setMeradress(String meradress) {
		this.meradress = meradress;
	}
	public String getMerphone() {
		return merphone;
	}
	public void setMerphone(String merphone) {
		this.merphone = merphone;
	}
	public String getMerfax() {
		return merfax;
	}
	public void setMerfax(String merfax) {
		this.merfax = merfax;
	}
	public String getMermobile() {
		return mermobile;
	}
	public void setMermobile(String mermobile) {
		this.mermobile = mermobile;
	}
	public String getMeremail() {
		return meremail;
	}
	public void setMeremail(String meremail) {
		this.meremail = meremail;
	}
	public String getMerqq() {
		return merqq;
	}
	public void setMerqq(String merqq) {
		this.merqq = merqq;
	}
	public String getMermsn() {
		return mermsn;
	}
	public void setMermsn(String mermsn) {
		this.mermsn = mermsn;
	}
	public String getWebsite() {
		return website;
	}
	public void setWebsite(String website) {
		this.website = website;
	}
	public String getLinkman() {
		return linkman;
	}
	public void setLinkman(String linkman) {
		this.linkman = linkman;
	}
	public String getLinkmanmobile() {
		return linkmanmobile;
	}
	public void setLinkmanmobile(String linkmanmobile) {
		this.linkmanmobile = linkmanmobile;
	}
	public Long getMertype() {
		return mertype;
	}
	public void setMertype(Long mertype) {
		this.mertype = mertype;
	}
	public String getBank() {
		return bank;
	}
	public void setBank(String bank) {
		this.bank = bank;
	}
	public String getAccountname() {
		return accountname;
	}
	public void setAccountname(String accountname) {
		this.accountname = accountname;
	}
	public String getCardno() {
		return cardno;
	}
	public void setCardno(String cardno) {
		this.cardno = cardno;
	}
	public Long getBusinesstype() {
		return businesstype;
	}
	public void setBusinesstype(Long businesstype) {
		this.businesstype = businesstype;
	}
	public Long getBusinessyears() {
		return businessyears;
	}
	public void setBusinessyears(Long businessyears) {
		this.businessyears = businessyears;
	}
	public String getGatheringway() {
		return gatheringway;
	}
	public void setGatheringway(String gatheringway) {
		this.gatheringway = gatheringway;
	}
	public String getMd5key() {
		return md5key;
	}
	public void setMd5key(String md5key) {
		this.md5key = md5key;
	}
	public String getLinkmanadress() {
		return linkmanadress;
	}
	public void setLinkmanadress(String linkmanadress) {
		this.linkmanadress = linkmanadress;
	}
	public String getLinkmanphone() {
		return linkmanphone;
	}
	public void setLinkmanphone(String linkmanphone) {
		this.linkmanphone = linkmanphone;
	}
	public String getLinkmanfax() {
		return linkmanfax;
	}
	public void setLinkmanfax(String linkmanfax) {
		this.linkmanfax = linkmanfax;
	}
	public String getLinkmanemail() {
		return linkmanemail;
	}
	public void setLinkmanemail(String linkmanemail) {
		this.linkmanemail = linkmanemail;
	}
	public String getLinkmanqq() {
		return linkmanqq;
	}
	public void setLinkmanqq(String linkmanqq) {
		this.linkmanqq = linkmanqq;
	}
	public String getLinkmanmsn() {
		return linkmanmsn;
	}
	public void setLinkmanmsn(String linkmanmsn) {
		this.linkmanmsn = linkmanmsn;
	}
	public String getLinkmancertificateno() {
		return linkmancertificateno;
	}
	public void setLinkmancertificateno(String linkmancertificateno) {
		this.linkmancertificateno = linkmancertificateno;
	}
	public String getSalesman() {
		return salesman;
	}
	public void setSalesman(String salesman) {
		this.salesman = salesman;
	}
	public String getMertypename() {
		return mertypename;
	}
	public void setMertypename(String mertypename) {
		this.mertypename = mertypename;
	}
	public String getBusinesstypename() {
		return businesstypename;
	}
	public void setBusinesstypename(String businesstypename) {
		this.businesstypename = businesstypename;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getIsopen() {
		return isopen;
	}
	public void setIsopen(String isopen) {
		this.isopen = isopen;
	}
	public Long getErrorCount() {
		return errorCount;
	}
	public void setErrorCount(Long errorCount) {
		this.errorCount = errorCount;
	}
	public Date getLoginTime() {
		return loginTime;
	}
	public void setLoginTime(Date loginTime) {
		this.loginTime = loginTime;
	}
	public Date getModifyPwdTime() {
		return modifyPwdTime;
	}
	public void setModifyPwdTime(Date modifyPwdTime) {
		this.modifyPwdTime = modifyPwdTime;
	}
	public String getStatutes() {
		return statutes;
	}
	public void setStatutes(String statutes) {
		this.statutes = statutes;
	}
	public Double getMonthTradeMoney() {
		return monthTradeMoney;
	}
	public void setMonthTradeMoney(Double monthTradeMoney) {
		this.monthTradeMoney = monthTradeMoney;
	}
	public Date getProcessModifyDate() {
		return processModifyDate;
	}
	public void setProcessModifyDate(Date processModifyDate) {
		this.processModifyDate = processModifyDate;
	}
	public Long getProcessModifyCount() {
		return processModifyCount;
	}
	public void setProcessModifyCount(Long processModifyCount) {
		this.processModifyCount = processModifyCount;
	}
	public Date getOpenTime() {
		return openTime;
	}
	public void setOpenTime(Date openTime) {
		this.openTime = openTime;
	}
	public Double getAnnualFee() {
		return annualFee;
	}
	public void setAnnualFee(Double annualFee) {
		this.annualFee = annualFee;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getExppay() {
		return exppay;
	}
	public void setExppay(String exppay) {
		this.exppay = exppay;
	}
	public String getExppaystatus() {
		return exppaystatus;
	}
	public void setExppaystatus(String exppaystatus) {
		this.exppaystatus = exppaystatus;
	}
	public Long getAssuretime() {
		return assuretime;
	}
	public void setAssuretime(Long assuretime) {
		this.assuretime = assuretime;
	}
	
}
