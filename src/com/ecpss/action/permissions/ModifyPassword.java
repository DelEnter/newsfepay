package com.ecpss.action.permissions;

import java.security.MessageDigest;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import sun.misc.BASE64Encoder;

import com.ecpss.action.BaseAction;
import com.ecpss.model.log.SystemLog;
import com.ecpss.model.user.User;

public class ModifyPassword extends BaseAction{
	
	private User users;
	
	private String userName;
	private String password1;
	private String newpwd1;
	private String newpwd2;
	
	
	
	/**
     * �޸�����
     * @return
     */
    public String toModifyPwd(){
    	if(null==getUserBean().getUserName()){
    		this.messageAction="��½������ߵ�½��ʱ,�����µ�½������޸��������.";
    		return INPUT;
    	}
    	String hql="select user from User user where user.userName='"+getUserBean().getUserName()+"' ";
    	users = (User) this.commonService.uniqueResult(hql);
    	return SUCCESS;
    }
    
    
    /**
     * �޸�����
     * @return
     * @throws Exception 
     */
    public String modifyPwd() throws Exception{
    	//����û����ھ�ȥ��֤����
		MessageDigest md5;
		BASE64Encoder base64en = new BASE64Encoder();
		md5 = MessageDigest.getInstance("MD5");
		String passwords = base64en.encode(md5.digest(password1.getBytes("utf-8")));
    	int a =this.commonService.list("from User u where u.userName='"+userName.trim()+"' and u.password='"+passwords+"'").size();
    	if(a==1){
    		User user = (User)this.commonService.list("from User u where u.userName='"+userName.trim()+"' and u.password='"+passwords+"'").get(0);
    		if(StringUtils.isNotBlank(newpwd1) && StringUtils.isNotBlank(newpwd2)){
    			if(newpwd1.equals(newpwd2)){
    				String pwdmd5=base64en.encode(md5.digest(newpwd1.getBytes("utf-8")));
    				String s="select s from SystemLog s where s.remarks='"+pwdmd5+"' order by s.operTime desc ";
    				List<SystemLog> sllist = this.commonService.list(s);
    				int i = 1;
    				if(sllist.size()>0){
    					for (SystemLog systemLog : sllist) {
    						//�ж�����Ĵ��Ѿ��޸Ĺ�������  
    						if(i>4){
    							break;
    						}
    						//�뵱ǰ����Ա��Ƿ�һ��,���һֱ �ͷ��� ��ʾ�û�
    						if(systemLog.getRemarks().equals(pwdmd5)){
    							this.messageAction="Current password and there is the same as the previous four";
    		    				return INPUT;
    						}
    						i++;
						}
    				}
    				user.setPassword(pwdmd5);
    				user.setModifyPwdTime(new Date());
    				this.commonService.update(user);
    				SystemLog sl=new SystemLog();
    				//sl.setMerno("");
    				sl.setOperTime(user.getModifyPwdTime());
    				sl.setRemarks(user.getPassword());
    				sl.setRescReow("�޸�����");
    				sl.setOperType("4");
    				sl.setUserName(this.getUserBean().getUserName());
    				this.commonService.save(sl);
    			}else{
    				this.messageAction="Enter the new password twice inconsistent";
    				return INPUT;
    			}
    		}else{
    			this.messageAction="Please enter a new password";
    			return INPUT;
    		}
    	}else{
    		this.messageAction="Original password error.";
    		return INPUT;
    	}
    	this.messageAction="�޸ĳɹ�,���µ�½.";
    	return SUCCESS;
    }
    
	
	public String getUserName() {
		return userName;
	}


	public void setUserName(String userName) {
		this.userName = userName;
	}


	public String getPassword1() {
		return password1;
	}


	public void setPassword1(String password1) {
		this.password1 = password1;
	}


	public String getNewpwd1() {
		return newpwd1;
	}


	public void setNewpwd1(String newpwd1) {
		this.newpwd1 = newpwd1;
	}


	public String getNewpwd2() {
		return newpwd2;
	}


	public void setNewpwd2(String newpwd2) {
		this.newpwd2 = newpwd2;
	}


	public User getUsers() {
		return users;
	}


	public void setUsers(User users) {
		this.users = users;
	}
}
