package com.ecpss.action;

import java.util.List;

import com.ecpss.vo.MerchantBean;
import com.opensymphony.xwork2.ActionContext;


public class CheckUserNameAction extends BaseAction{
	private Object jsonData;
	/**
	 * ���ݳֿ��������ȡ�ĳֿ��˽�����Ϣ
	 * @return
	 */
	public String getUsernameBy(){
		String username = ((String[])ActionContext.getContext().getParameters().get("username"))[0];
		String hql="select u.userName from User u where u.userName='"+username.trim()+"'";
		List list = this.commonService.list(hql);
		if(list.size()>0){
			jsonData = "1";
		}else{
			jsonData = "0";
		}
		return SUCCESS;
	}
	public Object getJsonData() {
		return jsonData;
	}
	public void setJsonData(Object jsonData) {
		this.jsonData = jsonData;
	}
}
