package com.ecpss.action.merchant;

import java.security.MessageDigest;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import sun.misc.BASE64Encoder;

import com.ecpss.action.BaseAction;
import com.ecpss.action.pay.TradeManager;
import com.ecpss.model.affiche.AfficheManager;
import com.ecpss.model.log.SystemLog;
import com.ecpss.model.shop.InternationalAgentsMerchant;
import com.ecpss.model.shop.InternationalMerchant;
import com.ecpss.model.shop.ShopOpera;
import com.ecpss.service.iservice.MerchantManagerService;
import com.ecpss.util.AES;
import com.ecpss.util.DateUtil;
import com.ecpss.vo.MerchantBean;
import com.ecpss.web.PageInfo;
import com.opensymphony.xwork2.ActionContext;

public class MerchantLoginAction extends BaseAction{
	@Autowired
	@Qualifier("merchantManagerService")
	private MerchantManagerService merchantManagerService;
/*	
	@Autowired
	@Qualifier("tradeManager")
	private TradeManager tradeManager;
	*/
	private Long merno;
	private String username;
	private String password;
	private String vercode;
	private MerchantBean merchantBean = new MerchantBean();
	
	private String oldLoginTime;
	private Long charbackCount;
	private Long compliantCount;
	private AfficheManager afficheManager=new AfficheManager();
/*	
	private List successfulAmount;
	private List todaySuccessAmout;
	private List todayAmout;
	private List bailAmout;
	private PageInfo info = new PageInfo();
	private String successRate;*/
	
	
	
/*	public List getSuccessfulAmount() {
		return successfulAmount;
	}

	public void setSuccessfulAmount(List successfulAmount) {
		this.successfulAmount = successfulAmount;
	}

	public List getTodaySuccessAmout() {
		return todaySuccessAmout;
	}

	public void setTodaySuccessAmout(List todaySuccessAmout) {
		this.todaySuccessAmout = todaySuccessAmout;
	}

	public List getTodayAmout() {
		return todayAmout;
	}

	public void setTodayAmout(List todayAmout) {
		this.todayAmout = todayAmout;
	}

	public List getBailAmout() {
		return bailAmout;
	}

	public void setBailAmout(List bailAmout) {
		this.bailAmout = bailAmout;
	}

	public PageInfo getInfo() {
		return info;
	}

	public void setInfo(PageInfo info) {
		this.info = info;
	}

	public String getSuccessRate() {
		return successRate;
	}

	public void setSuccessRate(String successRate) {
		this.successRate = successRate;
	}*/

	public String toLogin(){
		return SUCCESS;
	}
	
	/**
	 * �̻���½�̻������̨
	 * @return
	 * @throws Exception 
	 */
	public String goHome()
	{
		
		return null;
	}
	public String merLogin() throws Exception{
		//��ȡ��֤��
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpSession session = request.getSession();
		String ver2 = (String)session.getAttribute("rand");
		/**
		 * ����û���null,�Ǿ�˵���Ǵ����̻��Ż������������
		 */
		if(StringUtils.isNotBlank(username)){
			InternationalMerchant m1 = this.merchantManagerService.getMerchantUser(merno, username,null);
			ShopOpera shopopera1 = this.merchantManagerService.getMerchantOpera(merno, username, null);
			
			//����������ǿյľ�˵��û���ҵ�����û�
			if(m1==null && shopopera1==null){
				this.messageAction="Merchant user input not correctly, please make sure that the registration information is correct.";
				this.messageEnAction="�û���Ϣ�������,��ȷ�Ϻ���������.";
				ActionContext.getContext().put("userNotExist", true);
				return INPUT;
			}else if(vercode!=ver2 && !vercode.equals(ver2)){
				this.messageAction="The au-code you input is not correct, please input again.";
				this.messageEnAction="��֤���������,����������.";
				return INPUT;
			}
			
			/// hql1 ��ѯ����ǰ��½�û�������������ϴδ���ʱ��С�뵱ǰ30����
			String hql11 = "select m from InternationalMerchant m " +
					" where m.merno="+merno+" " +
					" and m.username='"+username+"' " +
					" and m.errorCount>=5 " +
					" and round(to_number(sysdate-m.loginTime)*1440)<30";
			
			String hql22="select s from ShopOpera s,InternationalMerchant m where s.merchantId=m.id " +
							" and s.userName='"+username+"' " +
							" and m.merno="+merno+
							" and s.errorCount>=5 " +
							" and round(to_number(sysdate-s.loginTime)*1440)<30";
			int merchanterror = this.commonService.list(hql11).size();
			int operaerror = this.commonService.list(hql22).size();
			if(merchanterror==1 || operaerror==1){
				this.messageAction="Sorry, the times you input the wrong password are too much, please login again 30 minutes later.";
				this.messageEnAction="��������������,�ʻ�����30����.";
				return INPUT;
			}
			//����û����ھ�ȥ��֤����
			MessageDigest md5;
			BASE64Encoder base64en = new BASE64Encoder();
			md5 = MessageDigest.getInstance("MD5");
			String passwords = base64en.encode(md5.digest(password.getBytes("utf-8")));
			InternationalMerchant m = this.merchantManagerService.getMerchantUser(merno, username, passwords);
			ShopOpera shopopera = this.merchantManagerService.getMerchantOpera(merno, username, passwords);
			if(m!=null){
				//�ж��̻��Ƿ�ͨ,���û�п�ͨ��ʾ��ͨ
				if(m.getIsopen().equals("0")){
					
					this.messageAction="The merchant account hasn't been opened, please contact the Ecpss customer service to open merchant account.";
					this.messageEnAction="�̻�δ��ͨ,����ϵҵ��Ա��ͨ.";
					return INPUT;
				}
				if(m.getLoginTime()!=null){
					oldLoginTime = DateUtil.formatDateTime(m.getLoginTime());///��ȡ�ϴε�½ʱ��
				}else{
					oldLoginTime = DateUtil.formatDateTime(new Date());///��ȡ�ϴε�½ʱ��
				}
				charbackCount=merchantManagerService.getMerchantChargeBackCount(m.getId());
				compliantCount = merchantManagerService.getMerchantComplaintCount(m.getId());
				merchantBean.setMerchantId(m.getId());
				merchantBean.setMerChantNo(m.getMerno());
				merchantBean.setMerchantUserName(m.getUsername());
				afficheManager=(AfficheManager) this.commonService.uniqueResult("from AfficheManager t where t.affichedate=( select max(affichedate) from AfficheManager)");
				getHttpServletRequest().getSession().setAttribute("merchantBean", merchantBean);//�̻�ʵ��
				getHttpServletRequest().getSession().setAttribute("charbackCount",charbackCount);//�ܸ�����
				getHttpServletRequest().getSession().setAttribute("compliantCount",compliantCount);//Ͷ�߱���
				getHttpServletRequest().getSession().setAttribute("oldLoginTime",oldLoginTime);//�ϴε�¼ʱ��
				getHttpServletRequest().getSession().setAttribute("afficheManager",afficheManager);//�ϴε�¼ʱ��
				m.setLoginTime(new Date());
				m.setErrorCount(0L);
				this.commonService.update(m);
				SystemLog sl=new SystemLog();
				sl.setMerno(this.getMerchantBean().getMerChantNo()+"");
				sl.setOperTime(new Date());
				sl.setRemarks("�̻���Ϊ��"+this.getMerchantBean().getMerChantNo()+"����¼�û���Ϊ��"+m.getUsername()+"��¼ʱ��Ϊ:"+new Date()+"IP��ַ�ǣ�"+this.getIpAddr(request));
				sl.setRescReow("�̻���½");
				sl.setOperType("6");
				sl.setUserName(m.getUsername());
				this.commonService.save(sl);
				return SUCCESS;
			}
			else if(shopopera!=null){
				if(shopopera.getLoginTime()!=null){
					oldLoginTime = DateUtil.formatDateTime(shopopera.getLoginTime());///��ȡ�ϴε�½ʱ��
				}else{
					oldLoginTime = DateUtil.formatDateTime(new Date());///��ȡ�ϴε�½ʱ��
				}
				//charbackCount=merchantManagerService.getMerchantChargeBackCount(shopopera.getMerchantId());
				//compliantCount = merchantManagerService.getMerchantComplaintCount(shopopera.getMerchantId());
				merchantBean.setMerchantId(shopopera.getMerchantId());
				merchantBean.setMerChantNo(merno);
				merchantBean.setMerchantUserName(shopopera.getUserName());
				getHttpServletRequest().getSession().setAttribute("merchantBean", merchantBean);
				shopopera.setLoginTime(new Date());
				shopopera.setErrorCount(0L);
				this.commonService.update(shopopera);
				SystemLog sl=new SystemLog();
				sl.setMerno(this.getMerchantBean().getMerChantNo()+"");
				sl.setOperTime(new Date());
				sl.setRemarks("�̻���Ϊ��"+this.getMerchantBean().getMerChantNo()+"����¼�û���Ϊ��"+shopopera.getUserName()+"��¼ʱ��Ϊ:"+new Date()+"IP��ַ�ǣ�"+this.getIpAddr(request));
				sl.setRescReow("�̻���½");
				sl.setOperType("6");
				sl.setUserName(shopopera.getUserName());
				this.commonService.save(sl);				
				return SUCCESS;
			}
			else{
				//����������������ʾ����
				Long count = 0L;
				if(m==null && m1!=null){
					m1.setLoginTime(new Date());
					m1.setErrorCount(m1.getErrorCount()+1L);
					this.commonService.update(m1);
					count = m1.getErrorCount();
				}else if(shopopera==null && shopopera1!=null){
					shopopera1.setLoginTime(new Date());
					shopopera1.setErrorCount(shopopera1.getErrorCount()+1L);
					this.commonService.update(shopopera1);
					count = shopopera1.getErrorCount();
				}
				this.messageAction="The first "+count+" times the password input incorrectly. If you input wrong password more than 5 times, the account will be locked 30 minutes.";
				this.messageEnAction="��"+count+"���������,�������5���ʻ�������30����.";
				return INPUT;
			}
			
		}else{
			List<InternationalAgentsMerchant> am = this.merchantManagerService.getAgentsMerchant(merno, password);
			if(am.size()==0){
				ActionContext.getContext().put("userNotExist", true);	
				return INPUT;
			}else{
				merchantBean.setMerChantNo(am.get(0).getAgentsMerchantNo());
				getHttpServletRequest().getSession().setAttribute("merchantBean", merchantBean);
				return "agents";
			}
		}
	}

	public String tonglianmerLogin() throws Exception{
		//��ȡ��֤��
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpSession session = request.getSession();
		String ver2 = (String)session.getAttribute("rand");
		/**
		 * ����û���null,�Ǿ�˵���Ǵ����̻��Ż������������
		 */
		if(StringUtils.isNotBlank(username)){
			InternationalMerchant m1 = this.merchantManagerService.getMerchantUser(merno, username,null);
			ShopOpera shopopera1 = this.merchantManagerService.getMerchantOpera(merno, username, null);
			
			//����������ǿյľ�˵��û���ҵ�����û�
			if(m1==null && shopopera1==null){
				this.messageAction="Merchant user input not correctly, please make sure that the registration information is correct.";
				this.messageEnAction="�û���Ϣ�������,��ȷ�Ϻ���������.";
				ActionContext.getContext().put("userNotExist", true);
				return INPUT;
			}else if(vercode!=ver2 && !vercode.equals(ver2)){
				this.messageAction="The au-code you input is not correct, please input again.";
				this.messageEnAction="��֤���������,����������.";
				return INPUT;
			}
			
			/// hql1 ��ѯ����ǰ��½�û�������������ϴδ���ʱ��С�뵱ǰ30����
			String hql11 = "select m from InternationalMerchant m " +
					" where m.merno="+merno+" " +
					" and m.username='"+username+"' " +
					" and m.errorCount>=5 " +
					" and round(to_number(sysdate-m.loginTime)*1440)<30";
			
			String hql22="select s from ShopOpera s,InternationalMerchant m where s.merchantId=m.id " +
							" and s.userName='"+username+"' " +
							" and m.merno="+merno+
							" and s.errorCount>=5 " +
							" and round(to_number(sysdate-s.loginTime)*1440)<30";
			int merchanterror = this.commonService.list(hql11).size();
			int operaerror = this.commonService.list(hql22).size();
			if(merchanterror==1 || operaerror==1){
				this.messageAction="Sorry, the times you input the wrong password are too much, please login again 30 minutes later.";
				this.messageEnAction="��������������,�ʻ�����30����.";
				return INPUT;
			}
			//����û����ھ�ȥ��֤����
			MessageDigest md5;
			BASE64Encoder base64en = new BASE64Encoder();
			md5 = MessageDigest.getInstance("MD5");
			String passwords = base64en.encode(md5.digest(password.getBytes("utf-8")));
			InternationalMerchant m = this.merchantManagerService.getMerchantUser(merno, username, passwords);
			ShopOpera shopopera = this.merchantManagerService.getMerchantOpera(merno, username, passwords);
			if(m!=null){
				//�ж��̻��Ƿ�ͨ,���û�п�ͨ��ʾ��ͨ
				if(m.getIsopen().equals("0")){
					
					this.messageAction="The merchant account hasn't been opened, please contact the Ecpss customer service to open merchant account.";
					this.messageEnAction="�̻�δ��ͨ,����ϵҵ��Ա��ͨ.";
					return INPUT;
				}
				if(m.getLoginTime()!=null){
					oldLoginTime = DateUtil.formatDateTime(m.getLoginTime());///��ȡ�ϴε�½ʱ��
				}else{
					oldLoginTime = DateUtil.formatDateTime(new Date());///��ȡ�ϴε�½ʱ��
				}
				charbackCount=merchantManagerService.getMerchantChargeBackCount(m.getId());
				compliantCount = merchantManagerService.getMerchantComplaintCount(m.getId());
				merchantBean.setMerchantId(m.getId());
				merchantBean.setMerChantNo(m.getMerno());
				merchantBean.setMerchantUserName(m.getUsername());
				afficheManager=(AfficheManager) this.commonService.uniqueResult("from AfficheManager t where t.affichedate=( select max(affichedate) from AfficheManager)");
				getHttpServletRequest().getSession().setAttribute("merchantBean", merchantBean);//�̻�ʵ��
				getHttpServletRequest().getSession().setAttribute("charbackCount",charbackCount);//�ܸ�����
				getHttpServletRequest().getSession().setAttribute("compliantCount",compliantCount);//Ͷ�߱���
				getHttpServletRequest().getSession().setAttribute("oldLoginTime",oldLoginTime);//�ϴε�¼ʱ��
				getHttpServletRequest().getSession().setAttribute("afficheManager",afficheManager);//�ϴε�¼ʱ��
				m.setLoginTime(new Date());
				m.setErrorCount(0L);
				this.commonService.update(m);
				SystemLog sl=new SystemLog();
				sl.setMerno(this.getMerchantBean().getMerChantNo()+"");
				sl.setOperTime(new Date());
				sl.setRemarks("�̻���Ϊ��"+this.getMerchantBean().getMerChantNo()+"����¼�û���Ϊ��"+m.getUsername()+"��¼ʱ��Ϊ:"+new Date()+"IP��ַ�ǣ�"+this.getIpAddr(request));
				sl.setRescReow("�̻���½");
				sl.setOperType("6");
				sl.setUserName(m.getUsername());
				this.commonService.save(sl);
				return SUCCESS;
			}
			else if(shopopera!=null){
				if(shopopera.getLoginTime()!=null){
					oldLoginTime = DateUtil.formatDateTime(shopopera.getLoginTime());///��ȡ�ϴε�½ʱ��
				}else{
					oldLoginTime = DateUtil.formatDateTime(new Date());///��ȡ�ϴε�½ʱ��
				}
				//charbackCount=merchantManagerService.getMerchantChargeBackCount(shopopera.getMerchantId());
				//compliantCount = merchantManagerService.getMerchantComplaintCount(shopopera.getMerchantId());
				merchantBean.setMerchantId(shopopera.getMerchantId());
				merchantBean.setMerChantNo(merno);
				merchantBean.setMerchantUserName(shopopera.getUserName());
				getHttpServletRequest().getSession().setAttribute("merchantBean", merchantBean);
				shopopera.setLoginTime(new Date());
				shopopera.setErrorCount(0L);
				this.commonService.update(shopopera);
				SystemLog sl=new SystemLog();
				sl.setMerno(this.getMerchantBean().getMerChantNo()+"");
				sl.setOperTime(new Date());
				sl.setRemarks("�̻���Ϊ��"+this.getMerchantBean().getMerChantNo()+"����¼�û���Ϊ��"+shopopera.getUserName()+"��¼ʱ��Ϊ:"+new Date()+"IP��ַ�ǣ�"+this.getIpAddr(request));
				sl.setRescReow("�̻���½");
				sl.setOperType("6");
				sl.setUserName(shopopera.getUserName());
				this.commonService.save(sl);				
				return SUCCESS;
			}
			else{
				//����������������ʾ����
				Long count = 0L;
				if(m==null && m1!=null){
					m1.setLoginTime(new Date());
					m1.setErrorCount(m1.getErrorCount()+1L);
					this.commonService.update(m1);
					count = m1.getErrorCount();
				}else if(shopopera==null && shopopera1!=null){
					shopopera1.setLoginTime(new Date());
					shopopera1.setErrorCount(shopopera1.getErrorCount()+1L);
					this.commonService.update(shopopera1);
					count = shopopera1.getErrorCount();
				}
				this.messageAction="The first "+count+" times the password input incorrectly. If you input wrong password more than 5 times, the account will be locked 30 minutes.";
				this.messageEnAction="��"+count+"���������,�������5���ʻ�������30����.";
				return INPUT;
			}
			
		}else{
			List<InternationalAgentsMerchant> am = this.merchantManagerService.getAgentsMerchant(merno, password);
			if(am.size()==0){
				ActionContext.getContext().put("userNotExist", true);	
				return INPUT;
			}else{
				merchantBean.setMerChantNo(am.get(0).getAgentsMerchantNo());
				getHttpServletRequest().getSession().setAttribute("merchantBean", merchantBean);
				return "agents";
			}
		}
	}
	
	
	/*
	 * 
	 * 
	 */
/*	public String getTranInfo(){
		MerchantBean bean=(MerchantBean) getHttpServletRequest().getSession().getAttribute("merchantBean");
		String username = bean.getMerchantUserName();
		Long merno  = bean.getMerChantNo();
		InternationalMerchant m1 = this.merchantManagerService.getMerchantUser(merno, username,null);
		ShopOpera shopopera = this.merchantManagerService.getMerchantOpera(merno, username, null);
		SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd"); 
		Date d=new Date(); 
		String afterDate=df.format(new Date(d.getTime() - 10 * 24 * 60 * 60 * 1000));
		if(m1!=null||shopopera!=null){
			StringBuffer sb = new StringBuffer();
			String totleCount = "select count(*)";
			String selectquery = "SELECT  * FROM (SELECT A.*, ROWNUM RN FROM "
					+ "(select ti.orderNo," + // 1
					"ti.merchantOrderNo," + // 2
					"m.merno," + // 3
					"to_char(ti.tradeTime,'yy-mm-dd hh24:mi:ss')," + // 4
					"ti.tradeAmount," + // 5
					"ti.moneyType," + // 6
					"ti.rmbAmount," + // 7
					"c.channelName," + // 8
					"ti.tradeState," + // 9
					"ti.backCount," + // 10
					"ti.isPicture," + // 11
					"ti.isTrackNo," + // 12
					"ti.remark," + // 13
					"ti.id," + // 14
					"ci.address," + // 15
					"ci.shippingAddress, " + // 16
					"ti.tradeUrl,ci.cardNo,ci.email,ci.ip"; // 17
			sb
					.append(" from International_Tradeinfo ti left join "
							+ "International_Merchant  m on ti.merchantId=m.id left join "
							+ "International_CardholdersInfo ci on  ci.tradeId=ti.id left join "
							+ "International_MerchantChannels mc on ti.tradeChannel=mc.id left join "
							+ "International_Channels c on mc.channelId=c.id "
							+ "where 1=1 and substr(ti.tradeState,1,1)!='3' and ti.tradeTime>="
							+ "to_date('" + afterDate+ " 00:00:00','yyyy-MM-dd hh24:mi:ss') and (ti.remark like '%����%' or ti.remark like '%Success%' or ti.remark like '%Not sufficient funds%' or ti.remark like '%Insufficient Funds%' or ti.remark like '%�����оܾ�%' or ti.remark like '%���ʳ���%' or ti.remark like '%CSC Validation Error%' or ti.remark like '%Transaction not permitted on card%' or ti.remark like '%Invalid Encryption value%' or ti.remark like '%�� �� ��%' or ti.remark like '%�޷�����%' or ti.remark like '%����Ȩ��%' or ti.remark like '%9914%' or ti.remark like '%9954%' or ti.remark like '%99N7%' or ti.remark like '%9962%' or ti.remark like '%9904%' or ti.remark like '%�߷���%' or ti.remark like '%Payment Declined��0%' or ti.remark like '%timeOut%' )"); // �̻�ͨ����ͨ��
			sb.append(" and  m.id='" + bean.getMerchantId() + "' ");
			sb.append(" order by ti.tradeTime desc");
			String listinfo = ")  A WHERE ROWNUM <= " + (info.getCurPage())
					* info.getPageSize() + ")WHERE RN > " + (info.getCurPage() - 1)
					* info.getPageSize() + "";
			String orderby = " order by ti.tradeTime desc";
			StringBuffer sb2 = new StringBuffer();

			sb2.append("from InternationalTradeinfo ti,"
					+ "InternationalMerchant m,"
					+ "InternationalCardholdersInfo ci,"
					+ "InternationalMerchantChannels mc,"
					+ "InternationalChannels c "
					+ "where m.id=ti.merchantId and m.id= '"
					+ bean.getMerchantId() + // ���ױ��̻�ID
					"' and ci.tradeId=ti.id " + // �ֿ��˸����ױ�
					"and ti.tradeChannel=mc.id " + // ���ױ��̻�ͨ��
					"and mc.channelId=c.id and substr(ti.tradeState,1,1) not in('2','3') and ti.tradeTime>= "
					+ "to_date('" + afterDate+ " 00:00:00','yyyy-MM-dd hh24:mi:ss') and (ti.remark like '%����%' or ti.remark like '%Success%' or ti.remark like '%Not sufficient funds%' or ti.remark like '%Insufficient Funds%' or ti.remark like '%�����оܾ�%' or ti.remark like '%���ʳ���%' or ti.remark like '%CSC Validation Error%' or ti.remark like '%Transaction not permitted on card%' or ti.remark like '%Invalid Encryption value%' or ti.remark like '%�� �� ��%' or ti.remark like '%�޷�����%' or ti.remark like '%����Ȩ��%' or ti.remark like '%9914%' or ti.remark like '%9954%' or ti.remark like '%99N7%' or ti.remark like '%9962%' or ti.remark like '%9904%' )");// �̻�ͨ����ͨ��
			StringBuffer sb3 = new StringBuffer("from International_Tradeinfo ti,"
					+ "International_Merchant m,"
					+ "International_CardholdersInfo ci,"
					+ "International_MerchantChannels mc,"
					+ "International_Channels c "
					+ "where m.id=ti.merchantId and m.id= '"
					+ bean.getMerchantId() + // ���ױ��̻�ID
					"' and ci.tradeId=ti.id " + // �ֿ��˸����ױ�
					"and ti.tradeChannel=mc.id " + // ���ױ��̻�ͨ��
					"and mc.channelId=c.id and substr(ti.tradeState,1,1) not in('2','3') and ti.tradeTime>= "
					+ "to_date('" + df.format(new Date())+ " 00:00:00','yyyy-MM-dd hh24:mi:ss') and (ti.remark like '%����%' or ti.remark like '%Success%' or ti.remark like '%Not sufficient funds%' or ti.remark like '%Insufficient Funds%' or ti.remark like '%�����оܾ�%' or ti.remark like '%���ʳ���%' or ti.remark like '%CSC Validation Error%' or ti.remark like '%Transaction not permitted on card%' or ti.remark like '%Invalid Encryption value%' or ti.remark like '%�� �� ��%' or ti.remark like '%�޷�����%' or ti.remark like '%����Ȩ��%' or ti.remark like '%9914%' or ti.remark like '%9954%' or ti.remark like '%99N7%' or ti.remark like '%9962%' or ti.remark like '%9904%' )");
					//ͳ�Ƶ����ܽ��
					String todaytotalsql="select sum(ti.tradeAmount),ti.moneyType,count(ti.id) "+sb3.toString()+" group by ti.moneyType ";
					//ͳ�Ƶ��ճɹ����
					String todaySuctotalsql="select sum(ti.tradeAmount),ti.moneyType,count(ti.id) "+sb3.toString()+" and substr(ti.tradeState,1,1)='1' group by ti.moneyType ";
					//ͳ��ʮ�����ڵĳɹ��ܽ��
					String successfulsql = "select sum(ti.tradeAmount),ti.moneyType "
							+ sb2.toString()
							+ " and substr(ti.tradeState,1,1)='1' group by ti.moneyType";
					successfulAmount = commonService.list(successfulsql);
					todayAmout=commonService.getByList(todaytotalsql);
					todaySuccessAmout=commonService.getByList(todaySuctotalsql);
					String bailsql="select sum(px.traAmount),px.mtype from (select sum(ti.tradeAmount*mc.bailcharge) as traAmount,ti.moneyType as mtype,mc.channelId as chaId "+sb3.toString()
							+" and substr(ti.tradeState,1,1)='1' group by ti.moneyType,mc.channelId) px group by px.mtype";
					bailAmout=commonService.getByList(bailsql);
					List<Object[]> list = this.commonService.getByList(selectquery
							+ sb.toString() + listinfo);
					info.setTotalCount(this.tradeManager.intBySql(totleCount
							+ sb.toString()));
					if(todayAmout.size()>0&&todaySuccessAmout.size()>0){
						Object[] objsum=(Object[]) todayAmout.get(0);
						Object[] objsuc=(Object[]) todaySuccessAmout.get(0);
						int traderCount=Integer.parseInt(objsum[2].toString());
						int traderSucCount=Integer.parseInt(objsuc[2].toString());
						NumberFormat numberFormat = NumberFormat.getInstance();  
				        // ���þ�ȷ��С�����2λ  
				        numberFormat.setMaximumFractionDigits(2);  
				        successRate = numberFormat.format((float) traderSucCount / (float) traderCount * 100);
					}else if(todayAmout.size()>0&&todaySuccessAmout.size()==0){
						successRate="0.00";
					}else{
						successRate="100";
					}
					if(list.size()>0){
					for(Object[] obj:list){
						AES aes=new AES();
						String cardNo=aes.getCarNo(obj[17].toString());
						if ((cardNo.startsWith("30")
									|| cardNo.startsWith("35"))&& cardNo.length() == 16) {
							obj[17] = "JCB";
						}
						else if (cardNo.startsWith("4") && cardNo.length() == 16) {
								obj[17] = "VISA";
						}
						else if (cardNo.startsWith("5") && cardNo.length() == 16) {
								obj[17] = "Master";
						}
						else if ((cardNo.startsWith("34") || cardNo
									.startsWith("37"))&&cardNo.length() == 15) {
								obj[17] = "AMEX";
						}
						else if ((cardNo.startsWith("36")
									|| cardNo.startsWith("300")
									|| cardNo.startsWith("305") || cardNo
									.startsWith("38"))
									&& cardNo.length() == 14) {
								obj[17] = "Dinners";
							}else{
								obj[17] = "Dinners";
							}
						}
					}
					info.setResult(list);
		
		}else{
			return "timeout";
		}
		return SUCCESS;
	}
	*/
	public static HttpServletRequest getHttpServletRequest(){
		return (HttpServletRequest)ActionContext.getContext().get(ServletActionContext.HTTP_REQUEST);
	}

	public MerchantManagerService getMerchantManagerService() {
		return merchantManagerService;
	}

	public void setMerchantManagerService(
			MerchantManagerService merchantManagerService) {
		this.merchantManagerService = merchantManagerService;
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

	public MerchantBean getMerchantBean() {
		return merchantBean;
	}

	public void setMerchantBean(MerchantBean merchantBean) {
		this.merchantBean = merchantBean;
	}

	public String getOldLoginTime() {
		return oldLoginTime;
	}

	public void setOldLoginTime(String oldLoginTime) {
		this.oldLoginTime = oldLoginTime;
	}

	public Long getCharbackCount() {
		return charbackCount;
	}

	public void setCharbackCount(Long charbackCount) {
		this.charbackCount = charbackCount;
	}

	public Long getCompliantCount() {
		return compliantCount;
	}

	public void setCompliantCount(Long compliantCount) {
		this.compliantCount = compliantCount;
	}

	public String getVercode() {
		return vercode;
	}

	public void setVercode(String vercode) {
		this.vercode = vercode;
	}
	public AfficheManager getAfficheManager() {
		return afficheManager;
	}

	public void setAfficheManager(AfficheManager afficheManager) {
		this.afficheManager = afficheManager;
	}

	public static String getIpAddr(HttpServletRequest request) {
		String ip = request.getHeader("X-Forwarded-For");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_CLIENT_IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_X_FORWARDED_FOR");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		// System.out.println("ip----------------"+ip);
		return ip;

	}	
}
