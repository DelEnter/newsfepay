package com.ecpss.action.merchant;

import java.security.MessageDigest;
import java.util.Date;
import java.util.Random;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import sun.misc.BASE64Encoder;

import com.ecpss.action.BaseAction;
import com.ecpss.model.shop.InternationalMerchant;
import com.ecpss.service.iservice.MerchantManagerService;
import com.ecpss.util.CCSendMail;
import com.ecpss.util.EmailInfo;

public class ForgotPasswordAction extends BaseAction{
	@Autowired
	@Qualifier("merchantManagerService")
	private MerchantManagerService merchantManagerService;
	
	private Long merno;
	private String userName;
	private String email;
	
	/**
	 * ��������
	 * @return
	 * @throws Exception 
	 */
	public String resetPassword() throws Exception{
		if(StringUtils.isNotBlank(userName) && merno!=null && StringUtils.isNotBlank(email)){
			//��ѯ�Ƿ���ڸ��û�
			InternationalMerchant merchant = this.merchantManagerService.getMerchantUser(merno, userName,null);
			if(merchant!=null){
				//�Ա������Ƿ��ע��ʱ�����Ӧ
				if(merchant.getMeremail().trim().toLowerCase().equals(email.trim().toLowerCase())){
					String radStr = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
					StringBuffer generateRandStr = new StringBuffer();
					Random rand = new Random();
					for (int i = 0; i < 8; i++) {
						int randNum = rand.nextInt(36);
						generateRandStr.append(radStr.substring(randNum, randNum + 1));
					}
					MessageDigest md5;
					BASE64Encoder base64en = new BASE64Encoder();
					md5 = MessageDigest.getInstance("MD5");
					String passwords = base64en.encode(md5.digest(generateRandStr.toString()
							.getBytes("utf-8")));
					merchant.setPassword(passwords);
					merchant.setModifyPwdTime(new Date());
					commonService.update(merchant);
					//���̻������ʼ�֪ͨ������Ϣ
					EmailInfo emailinfo=new EmailInfo();
					//System.out.println("Password is : " + passwords);
					//System.out.println(emailinfo.getResetPwdByEmail(merchant, generateRandStr.toString()));
					CCSendMail.setSendMail(merchant.getMeremail(), emailinfo.getResetPwdByEmail(merchant, generateRandStr.toString()), "ecpss@ecpss.cc");
					this.messageAction="�������Ѿ�����������֤����,���������鿴�����µ�½.";
					return  SUCCESS;
				}else{
					//�����������֤����ȷ
					this.messageAction="E-mail��֤ʧ��.������ע��Email.";
					return INPUT;
				}
			}else{
				this.messageAction="������̻���,�û�������ȷ.";
				return INPUT;
			}
		}else{
			//��Ϣ��������д,����Ϊ��
			this.messageAction="��Ϣ����������,����Ϊ��.";
			return INPUT;
		}
	}
	
	
	public Long getMerno() {
		return merno;
	}


	public void setMerno(Long merno) {
		this.merno = merno;
	}


	public String getUserName() {
		return userName;
	}


	public void setUserName(String userName) {
		this.userName = userName;
	}


	public String getEmail() {
		return email;
	}


	public void setEmail(String email) {
		this.email = email;
	}


	public MerchantManagerService getMerchantManagerService() {
		return merchantManagerService;
	}


	public void setMerchantManagerService(
			MerchantManagerService merchantManagerService) {
		this.merchantManagerService = merchantManagerService;
	}


	
	
	
}
