package com.ecpss.action.merchant;

import org.apache.struts2.ServletActionContext;

import com.ecpss.action.BaseAction;

public class MerchantLogoutAction extends BaseAction{

	
	/**
	 * �û��˳�
	 * @return
	 * @throws Exception
	 */
	public String logout() throws Exception {
		ServletActionContext.getRequest().getSession().invalidate();
		return SUCCESS;
		
	}
}
