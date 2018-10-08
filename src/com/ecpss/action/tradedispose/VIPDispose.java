package com.ecpss.action.tradedispose;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import sun.misc.BASE64Encoder;

import com.ecpss.action.BaseAction;
import com.ecpss.model.channel.InternationalChannels;
import com.ecpss.model.payment.InternationalCardholdersInfo;
import com.ecpss.model.payment.InternationalTradeinfo;
import com.ecpss.model.shop.InternationalMerchant;
import com.ecpss.model.shop.InternationalTerminalManager;
import com.ecpss.model.user.User;
import com.ecpss.service.common.CommonService;
import com.ecpss.service.iservice.ShopManagerService;
import com.ecpss.util.EmailInfo;
import com.ecpss.util.MD5;
import com.ecpss.util.StatusUtil;
import com.ecpss.web.PageInfo;
import com.opensymphony.xwork2.ActionContext;

public class VIPDispose extends BaseAction{
	@Autowired
	@Qualifier("commonService")
	private CommonService commonService;
	@Autowired
	@Qualifier("shopManagerService")
	private ShopManagerService shopManagerService;
	private String hql;
	private PageInfo info;
	private StringBuffer sb;
	private InternationalTradeinfo trade;
	private InternationalTradeinfo tradeinfo;
	
	private InternationalCardholdersInfo cardinfo;
	private InternationalMerchant merchant;
	private InternationalChannels channels;
	private User user ;
	private List list = new ArrayList();
	private Object[] o;
	private List<String> terminalList;
	private String isresult;
	/**
	 * ��ѯ�������� (�ɹ�״̬Ϊ������)
	 */
	public String findTradeDeal(){
		try{			
			Long begin=System.currentTimeMillis();

			String terminalNo = (String)ActionContext.getContext().getSession().get("terminalNo");
			String select = "select distinct t.terminalNo from InternationalTerminalManager t," +
					"InternationalTradeinfo ti where ti.VIPTerminalNo=t.terminalNo and substr(ti.tradeState,1,1)='2'  and t.isauto<>1 ";
			this.terminalList = this.commonService.list(select);
			if(info==null){
				info = new PageInfo();
			}
			sb = new StringBuffer("select t,c from InternationalTradeinfo t, InternationalCardholdersInfo c " +
					"WHERE t.id=c.tradeId AND  substr(t.tradeState,1,1)='2'" +
					" ");
			if(trade==null){
				trade = new InternationalTradeinfo();
			}
			if(merchant==null){
				merchant = new InternationalMerchant();
			}
			if(cardinfo==null){
				cardinfo = new InternationalCardholdersInfo();
			}
			
			if(merchant.getMerno()!=null ){
				sb.append(" AND substr(t.orderNo,1,4)='"+merchant.getMerno()+"' ");
			}
			if(StringUtils.isNotBlank(trade.getVIPTerminalNo())){
				sb.append(" AND t.VIPTerminalNo="+trade.getVIPTerminalNo()+"");
			}else if(StringUtils.isNotBlank(terminalNo)){
				sb.append(" AND t.VIPTerminalNo='"+terminalNo.trim()+"' ");
				trade.setVIPTerminalNo(terminalNo);
			}
			if(trade.getOrderNo()!=null && !trade.getOrderNo().trim().equals("")){
				sb.append(" AND t.orderNo="+trade.getOrderNo().trim()+"");
			}
			sb.append(" order by t.tradeTime");
			hql = sb.toString();
			if(StringUtils.isNotBlank(this.query) || this.query.equals("0")){
				query="1";
			}else{
				info = commonService.listQueryResultByHql(hql, info);
			}
			Long end=System.currentTimeMillis() - begin;
			System.out.println("-----------------"+end);
			return SUCCESS;
		}catch(Exception e){
			e.printStackTrace();
			this.messageAction="��ѯ��������ʧ��";
			return this.OPERATE_ERROR;
		}
	}
	/**
	 * ��ת�����״���ҳ��
	 */
	public String toTradeDeal(){
		try{
			if(trade==null){
				trade = new InternationalTradeinfo();
			}
			/*if(channels==null){
				channels = new InternationalChannels();
			}*/
			//tradeinfo = (InternationalTradeinfo)
			//ActionContext.getContext().getSession().get("tradeinfo");
			//trade.setVIPTerminalNo(tradeinfo.getVIPBatchNo());
			hql = "select card.cardNo, card.expiryDate, card.cvv2, t.orderNo, t.rmbAmount, chann.channelName, t.id,t.VIPTerminalNo" +
					" FROM InternationalTradeinfo t, InternationalCardholdersInfo card, InternationalMerchantChannels merchan, " +
					"InternationalChannels chann" +
					" WHERE t.id=card.tradeId " +
					"AND t.tradeChannel = merchan.id " +
					"and merchan.channelId=chann.id " +
					" AND t.id="+trade.getId()+"";
			o = (Object[])commonService.uniqueResult(hql);
			
			return SUCCESS;
		}catch(Exception e){
			e.printStackTrace();
			this.messageAction="��ת�����״���ҳ��ʧ��";
			return this.OPERATE_ERROR;
		}
	}
	/**
	 * ���״���
	 */
	public String tradeDeal(){
		try{
			// ��ȡϵͳʱ��
			//Date disposeDate =  GetBatchNo.getTime();
			InternationalTradeinfo tradeinfo = (InternationalTradeinfo)commonService.load(InternationalTradeinfo.class, trade.getId());
			tradeinfo.setVIPAuthorizationNo(trade.getVIPAuthorizationNo());
			tradeinfo.setVIPBatchNo(trade.getVIPBatchNo());
			tradeinfo.setVIPDisposePorson(getUserBean().getUserName());
			//tradeinfo.setVIPTerminalNo(tra.getVIPTerminalNo());
			tradeinfo.setVIPDisposeDate(new Date());
			tradeinfo.setRemark(trade.getRemark());
			tradeinfo.setDCCTradeAmount(trade.getDCCTradeAmount());
			tradeinfo.setDCCTradeCurrency(trade.getDCCTradeCurrency());
			tradeinfo.setTradeState(StatusUtil.updateStatus(tradeinfo.getTradeState(), 1, isresult)); 			
			commonService.update(tradeinfo);
			//this.findTradeDeal();
			//******************������Ҫ���ֿ��˷����ʼ�***********************
			//��ӵȴ������ʼ�
			List<InternationalTerminalManager> tmm = this.commonService.list("select tm from InternationalTerminalManager tm where tm.terminalNo='"+tradeinfo.getVIPTerminalNo()+"' ");
			String billingaddress = null;
			String terminalNo=null;
			if(tmm.size()>0){
				InternationalTerminalManager tm = tmm.get(0);
				billingaddress=tm.getBillingAddress();
				terminalNo = tm.getTerminalNo();
			}
			if(isresult.equals("1")){
				String hql="select c.email from InternationalCardholdersInfo c where c.tradeId="+tradeinfo.getId();
				String cemail = (String) this.commonService.uniqueResult(hql);
				EmailInfo emaiinfo= new EmailInfo();
				String mailinfo = emaiinfo.getPaymentResultEmail(cemail, tradeinfo.getTradeAmount(),
						getStates().getCurrencyTypeByNo(tradeinfo.getMoneyType().intValue()), 
						tradeinfo.getTradeUrl(), tradeinfo.getTradeTime(),billingaddress, tradeinfo.getMerchantOrderNo(), tradeinfo.getOrderNo());
				shopManagerService.addSendMessages(cemail, "ecpss@ecpss.cc", mailinfo, "0");
				this.messageAction="Message sent successfully";
			}
			
			//******************�����ʼ�����********************************
			
			
			//******************�������ͷ���ֵ���̻���վ**********************
			
			try {
				//------------------��ȡ���̻�post���ݵĲ�������ֵ
				String merchantorderno;
				String ordercount;
				String currency;
				String succeed;
				String message;
				String md5info;
				String MD5key;
				//---��ȡ�����̻���ֵ
				merchantorderno=tradeinfo.getMerchantOrderNo();
				ordercount = tradeinfo.getTradeAmount()+"";
				currency=tradeinfo.getMoneyType().toString();
				if(isresult.equals("1")){
					succeed = "88";
					message = "Successful";
				}else{
					succeed = "0";
					message = "Failed";
				}
				InternationalMerchant merchant = (InternationalMerchant) this.commonService.load(InternationalMerchant.class, tradeinfo.getMerchantId());
				MD5key = merchant.getMd5key();
				//�Ѵ�����������̻�
				MD5 md5 = new MD5();
				String md5src = merchant.getMerno()+ merchantorderno + currency + ordercount + succeed + tradeinfo.getLastMan() + MD5key;
				md5info = md5.getMD5ofStr(md5src);
				//----------------���̻���վpost����-----------
				URL url;
				System.out.println("post��ַ+++++++++++"+tradeinfo.getReturnUrl());
				url = new URL(tradeinfo.getReturnUrl());
				URLConnection connection = url.openConnection(); 
				connection.setDoOutput(true); 
				OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream(), "8859_1");
				String parte = "batchno="+tradeinfo.getVIPBatchNo()+"&authorizationNo="+tradeinfo.getVIPAuthorizationNo()+"&MerNo="+merchant.getMerno()+"&dateTime="+tradeinfo.getLastMan()+"&BillNo="+merchantorderno+"&Amount="+ordercount+"&Currency="+currency+"&Succeed="+succeed+"&Result="+message+"&MD5info="+md5info;
				out.write(parte); //������֯�ύ��Ϣ 
				out.flush(); 
				out.close(); 
				//��ȡ�������� 
				BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream())); 
				String line = null; 
				StringBuffer content= new StringBuffer(); 
				while((line = in.readLine()) != null) 
				{ 
				   //lineΪ����ֵ����Ϳ����ж��Ƿ�ɹ��� 
				    content.append(line);
				    //System.out.println(content);
				} 
			} catch (Exception e) {
				ActionContext.getContext().getSession().put("terminalNo",terminalNo);
				return SUCCESS;
			} 
			System.out.println("ִ�е�����");
			ActionContext.getContext().getSession().put("terminalNo",terminalNo);
			return SUCCESS;
		}catch(Exception e){
			e.printStackTrace();
			this.messageAction="����ʧ��!";
			return this.OPERATE_ERROR;
		}
	}
	/**
	 * ��ת��VIP���״����½
	 */
	public String toVIPLong(){
		try{
			//hql = "FROM InternationalChannels";
			//list = commonService.list(hql);
			
			return SUCCESS;
		}catch(Exception e){
			e.printStackTrace();
			this.messageAction="��ת��VIP���״����½ʧ��!";
			return this.OPERATE_ERROR;
		}
	}
	/**
	 * VIP���״����½
	 */
	public String VIPLong(){
		try{
			MessageDigest md5;
			BASE64Encoder base64en = new BASE64Encoder();
			md5 = MessageDigest.getInstance("MD5");
			String passwords = base64en.encode(md5.digest(user.getPassword().getBytes("utf-8")));
			hql = "FROM User u WHERE u.userName='"+user.getUserName()+"' AND u.password='"+passwords+"'";
			list = commonService.list(hql);
			if(list.size()!=0){
			//	InternationalTradeinfo tradeinfo = new InternationalTradeinfo();
				//tradeinfo.setVIPTerminalNo(trade.getVIPTerminalNo());
				//tradeinfo.setTradeChannel(channels.getId());
			//	ActionContext.getContext().getSession().put("tradeinfo",tradeinfo);
				return SUCCESS;
			}else{
				this.messageAction = "�û��������벻ƥ��!";
				return this.OPERATE_ERROR;
			}
			
		}catch(Exception e){
			e.printStackTrace();
			this.messageAction = "VIP���״����½ʧ��";
			return this.OPERATE_ERROR;
		}
	}
	/**6
	 * VIP���״�����Ȩ�˵�½
	 */
	public String VIPAuthorizationLong(){
		try{
			MessageDigest md5;
			BASE64Encoder base64en = new BASE64Encoder();
			md5 = MessageDigest.getInstance("MD5");
			String passwords = base64en.encode(md5.digest(user.getPassword().getBytes("utf-8")));
			hql = "FROM User u WHERE u.userName='"+user.getUserName()+"' AND u.password='"+passwords+"'";
			list = commonService.list(hql);
			if(list.size()!=0){
				this.findTradeDeal();
				return SUCCESS;
			}else{
				this.messageAction = "�û��������벻ƥ��!";
				return this.OPERATE_ERROR;
			}
		}catch(Exception e){
			e.printStackTrace();
			this.messageAction = "VIP���״�����Ȩ�˵�½ʧ��";
			return this.OPERATE_ERROR;
		}
	}
	/**
	 * @return the commonService
	 */
	public CommonService getCommonService() {
		return commonService;
	}
	/**
	 * @param commonService the commonService to set
	 */
	public void setCommonService(CommonService commonService) {
		this.commonService = commonService;
	}
	/**
	 * @return the trade
	 */
	public InternationalTradeinfo getTrade() {
		return trade;
	}
	/**
	 * @param trade the trade to set
	 */
	public void setTrade(InternationalTradeinfo trade) {
		this.trade = trade;
	}
	/**
	 * @return the cardinfo
	 */
	public InternationalCardholdersInfo getCardinfo() {
		return cardinfo;
	}
	/**
	 * @param cardinfo the cardinfo to set
	 */
	public void setCardinfo(InternationalCardholdersInfo cardinfo) {
		this.cardinfo = cardinfo;
	}
	/**
	 * @return the info
	 */
	public PageInfo getInfo() {
		return info;
	}
	/**
	 * @param info the info to set
	 */
	public void setInfo(PageInfo info) {
		this.info = info;
	}
	/**
	 * @return the list
	 */
	public List getList() {
		return list;
	}
	/**
	 * @param list the list to set
	 */
	public void setList(List list) {
		this.list = list;
	}
	/**
	 * @return the user
	 */
	public User getUser() {
		return user;
	}
	/**
	 * @param user the user to set
	 */
	public void setUser(User user) {
		this.user = user;
	}
	/**
	 * @return the merchant
	 */
	public InternationalMerchant getMerchant() {
		return merchant;
	}
	/**
	 * @param merchant the merchant to set
	 */
	public void setMerchant(InternationalMerchant merchant) {
		this.merchant = merchant;
	}
	/**
	 * @return the channels
	 */
	/**
	 * @return the channels
	 */
	public InternationalChannels getChannels() {
		return channels;
	}
	/**
	 * @param channels the channels to set
	 */
	public void setChannels(InternationalChannels channels) {
		this.channels = channels;
	}
	/**
	 * @return the o
	 */
	public Object[] getO() {
		return o;
	}
	/**
	 * @param o the o to set
	 */
	public void setO(Object[] o) {
		this.o = o;
	}
	/**
	 * @return the tradeinfo
	 */
	public InternationalTradeinfo getTradeinfo() {
		return tradeinfo;
	}
	/**
	 * @param tradeinfo the tradeinfo to set
	 */
	public void setTradeinfo(InternationalTradeinfo tradeinfo) {
		this.tradeinfo = tradeinfo;
	}
	public List<String> getTerminalList() {
		return terminalList;
	}
	public void setTerminalList(List<String> terminalList) {
		this.terminalList = terminalList;
	}
	public String getIsresult() {
		return isresult;
	}
	public void setIsresult(String isresult) {
		this.isresult = isresult;
	}

}
