package com.ecpss.action.log;

import java.util.List;

import com.ecpss.action.BaseAction;
import com.ecpss.model.log.SystemLog;
import com.ecpss.web.PageInfo;

public class SystemLogAction extends BaseAction {

	/** ϵͳ��־ʵ����� */
	private SystemLog systemLog;

	/** ����ϵͳ��־����ļ��� */
	private List<SystemLog> systemLogList;

	private String hql;

	private PageInfo info = new PageInfo();
	
	private String operTime;
	private String recordtime; // ��¼ʱ��

	private Long id; // ϵͳ��־��id

	private String merno; // �̻���

	/** ��ת��ϵͳ��־����ҳ�� */
	public String toSystemLog() {
		return SUCCESS;
	}

	/**
	 * ��־������
	 * 
	 * @return
	 */
	public String systemLogManager() {
		StringBuffer hql = new StringBuffer();
		hql.append("from SystemLog i where 1=1 ");
		
		if ((systemLog.getOperTime() != null)
				&& (!systemLog.getOperTime().equals(""))) {
			
			hql.append(" and i.operTime >= to_date('" + operTime+"','yyyy-MM-dd hh24:mi:ss') " );
		}
		if ((this.recordtime != null) && (!this.recordtime.equals(""))) {
			hql.append(" and i.operTime <= to_date('" + recordtime+" 23:59:59"+"','yyyy-MM-dd hh24:mi:ss') " );
		}
		if ((systemLog.getUserName() != null)
				&& (!systemLog.getUserName().trim().equals(""))) {
			hql.append(" and i.userName like '%" + systemLog.getUserName()
					+ "%'");
		}
		if (merno != null
				&& (!merno.trim().equals(""))) {
			hql.append(" and i.merno =  '" + merno + "' ");

		}
		hql.append(" order by i.operTime desc ");
		this.info = this.commonService.listQueryResultByHql(hql.toString(),
				info);
		return SUCCESS;
	}

	/**
	 * ��ת��ϵͳ��־��ϸҳ��
	 * 
	 * @return
	 */
	public String toSystemLogDeatilById() {
		systemLog = (SystemLog) this.commonService.load(SystemLog.class, id);
		System.out.println(systemLog.getMerno());
		return SUCCESS;

	}

	public SystemLog getSystemLog() {
		return systemLog;
	}

	public void setSystemLog(SystemLog systemLog) {
		this.systemLog = systemLog;
	}

	public List<SystemLog> getSystemLogList() {
		return systemLogList;
	}

	public void setSystemLogList(List<SystemLog> systemLogList) {
		this.systemLogList = systemLogList;
	}

	public String getHql() {
		return hql;
	}

	public void setHql(String hql) {
		this.hql = hql;
	}

	public PageInfo getInfo() {
		return info;
	}

	public void setInfo(PageInfo info) {
		this.info = info;
	}


	public String getRecordtime() {
		return recordtime;
	}

	public void setRecordtime(String recordtime) {
		this.recordtime = recordtime;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getMerno() {
		return merno;
	}

	public void setMerno(String merno) {
		this.merno = merno;
	}

}
