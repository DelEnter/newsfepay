package com.ecpss.action.manager;

import com.ecpss.action.BaseAction;
import com.ecpss.model.user.User;

public class ManagerAction extends BaseAction {
	private User user;

	// ��ת����Ա����ҳ��
	public String toManager() {
		return SUCCESS;
	}

	// ��������Ա
	public String toAddManager() {
		return SUCCESS;
	}

	// ��������Ա
	public String addManager() {
		this.commonService.save(user);
		return this.OPERATE_SUCCESS;
	}
	// 

}
