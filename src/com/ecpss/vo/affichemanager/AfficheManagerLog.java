package com.ecpss.vo.affichemanager;

import java.util.Date;

public class AfficheManagerLog {

	private Long id; // ���ݱ�id ����

	private String afficheContext; // ��������

	private Date affichedate; // ����ʱ��

	private String url; // ��������

	private String afficheContextafter; // ��¼�޸ĺ�Ĺ�������

	private String urlafter; // ��¼�޸ĺ������

	private Date affichedateafter; // ��¼�����޸ĺ��ʱ��

	public String getSaveAfficheLog() {
		StringBuffer sb = new StringBuffer();
		if (this.afficheContext != null && !(this.afficheContext.equals(""))) {
			sb.append("��������:" + this.afficheContext + "\r\n");
		}
		if (this.affichedate != null && !(this.affichedate.equals(""))) {
			sb.append("����ʱ��:" + this.affichedate + "\r\n");
		}
		if (this.url != null && !(this.url.equals(""))) {
			sb.append("���ӵ�ַ:" + this.url + "\r\n");
		}
		System.out.println(sb);
		return sb.toString();
	}

	/**
	 * ��¼�޸�ǰ���״̬
	 * @return
	 */
	public String getUpdateAfficheLog() {
		StringBuffer sb = new StringBuffer();
		if (this.afficheContext != null
				&& this.afficheContext != this.afficheContextafter) {
			sb.append("��������:" + this.afficheContext + "------------>"
					+ this.afficheContextafter + "\r\n");
		}
		if (this.affichedate != null && !(this.affichedate.equals(""))) {
			sb.append("����ʱ��:" + this.affichedate + "------------>"
					+ this.affichedateafter + "\r\n");
		}
		if (this.url != null && !(this.url.equals(""))) {
			sb.append("��������:" + this.url + "------------>"
					+ this.urlafter + "\r\n");
		}
		System.out.println(sb);
		return sb.toString();
	}
	
	/**
	 * ��¼ɾ������ַ
	 * @return
	 */
	public String getDeleteAfficheLog() {
		StringBuffer sb = new StringBuffer();
		if (this.afficheContext != null && !(this.afficheContext.equals(""))) {
			sb.append("��������:" + this.afficheContext + "\r\n");
		}
		if (this.affichedate!= null && !(this.affichedate.equals(""))) {
			sb.append("����ʱ��:" + this.affichedate + "\r\n");
		}
		if (this.url != null && !(this.url.equals(""))) {
			sb.append("��������:" + this.url + "\r\n");
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

	public String getAfficheContext() {
		return afficheContext;
	}

	public void setAfficheContext(String afficheContext) {
		this.afficheContext = afficheContext;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getAfficheContextafter() {
		return afficheContextafter;
	}

	public void setAfficheContextafter(String afficheContextafter) {
		this.afficheContextafter = afficheContextafter;
	}

	public String getUrlafter() {
		return urlafter;
	}

	public void setUrlafter(String urlafter) {
		this.urlafter = urlafter;
	}

	public Date getAffichedate() {
		return affichedate;
	}

	public void setAffichedate(Date affichedate) {
		this.affichedate = affichedate;
	}

	public Date getAffichedateafter() {
		return affichedateafter;
	}

	public void setAffichedateafter(Date affichedateafter) {
		this.affichedateafter = affichedateafter;
	}

}
