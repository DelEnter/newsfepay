package com.ecpss.action.tradedispose;

import java.sql.Date;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ecpss.action.BaseAction;
import com.ecpss.model.payment.InternationalTradeinfo;
import com.ecpss.model.shop.InternationalMerchant;
import com.ecpss.service.common.CommonService;
import com.ecpss.util.DateUtil;
import com.ecpss.vo.UserBean;
import com.ecpss.web.PageInfo;
import com.opensymphony.xwork2.ActionContext;
public class ProtestAction extends BaseAction{
	
	@Autowired
	@Qualifier("commonService")
	private CommonService commonService;
	private PageInfo info;
	private String hql;
	private Object[] disposeId;
	private StringBuffer sb;
	private InternationalTradeinfo tradeinfo;
	private InternationalMerchant merchant;
	private String startTime;
	private String endTime;
	private List list;
	private Double totalAmout;
	/**
	 * ��ѯδ�ľܸ���¼ (�ɹ���δ�ܸ�)
	 */
	public String findNoProtest(){
		try{
			if(info==null){
				info = new PageInfo();
			}
			sb = new StringBuffer("FROM InternationalTradeinfo t, InternationalMerchant m WHERE " +
					"t.merchantId = m.id AND substr(t.tradeState,1,1)='1' AND substr(t.tradeState,3,1)='0'");
			if(tradeinfo!=null){
				if(merchant.getMerno()!=null ){
					sb.append(" AND m.merno="+merchant.getMerno()+"");
				}
				if(tradeinfo.getOrderNo()!=null && !tradeinfo.getOrderNo().equals("")){
					sb.append(" AND t.orderNo="+tradeinfo.getOrderNo().trim()+"");
				}
				if(startTime!=null && !startTime.equals("")){
					sb.append(" AND t.tradeTime>=to_date('"+startTime+"', 'yyyy-mm-dd')");
				}
				if(endTime!=null && !endTime.equals("")){
					sb.append(" AND t.tradeTime<=to_date('"+endTime+"', 'yyyy-mm-dd')");
				}
			}
			hql = sb.toString();
			info.setPageSize(20);
			info = commonService.listQueryResultByHql(hql, info);
			return SUCCESS;
		}catch(Exception e){
			e.printStackTrace();
			this.messageAction = "��ѯδ�ܸ���¼����!";
			return this.OPERATE_ERROR;
		}
	}
	/**
	 * �ܸ�����
	 */
	public String protestDispose(){
		try{
			//��ȡ����������
			
			UserBean user = (UserBean)ActionContext.getContext().getSession().get("user");
			StringBuffer sb1 = new StringBuffer();
			sb = new StringBuffer();
			for(int i=0; i<disposeId.length; i++){
				sb.append(disposeId[i]+",");
			}
			//java.util.Date protestDate = new  java.util.Date();
			String value = sb.toString();
			//System.out.println("value-----------"+value.subSequence(0, value.length()-1));
			sb1.append(value.subSequence(0, value.length()-1));
			hql = "update international_tradeinfo a set " +
					"a.tradestate=substr(a.tradestate,1,2)||'1'||substr(a.tradestate,4, length(a.tradestate)-3), " +
					"a.protesttime=to_date('"+DateUtil.getDateTime()+"', 'yyyy-mm-dd hh24:mi:ss'), protestPerson='"+user.getUserName()+"'" +
					" where a.id in("+sb1.toString()+")";
			commonService.deleteBySql(hql);
			this.messageAction = "�ܸ������ɹ�!";
			return this.OPERATE_SUCCESS;
		}catch(Exception e){
			e.printStackTrace();
			this.messageAction = "�ܸ�����ʧ��!";
			return this.OPERATE_ERROR;
		}
	}
	/**
	 * ��ѯ�ܸ�����
	 */
	public String findProtestDispose(){
		try{
			//hql = "FROM InternationalTradeinfo t, InternationalMoneykindname m WHERE t.moneyType = m.moneykindno";
			if(info==null){
				info = new PageInfo();
			}
		
			sb = new StringBuffer("FROM InternationalTradeinfo t, InternationalMoneykindname m, InternationalMerchant mer " +
					"WHERE t.merchantId = mer.id AND substr(t.tradeState,3,1)='1' AND t.moneyType = m.moneykindno");
			if(tradeinfo!=null){
				if(merchant.getMerno()!=null ){
					sb.append(" AND mer.merno="+merchant.getMerno()+"");
				}
				if(tradeinfo.getOrderNo()!=null && !tradeinfo.getOrderNo().equals("")){
					sb.append(" AND t.orderNo="+tradeinfo.getOrderNo().trim()+"");
				}
				if(startTime!=null && !startTime.equals("")){
					sb.append(" AND t.tradeTime>=to_date('"+startTime+"', 'yyyy-mm-dd')");
				}
				if(endTime!=null && !endTime.equals("")){
					sb.append(" AND t.tradeTime<=to_date('"+endTime+"', 'yyyy-mm-dd')");
				}
			}
			hql = sb.toString();
			info.setPageSize(20);
			info = commonService.listQueryResultByHql(hql, info);
			StringBuffer sb2=new StringBuffer();
			sb2.append("select sum(t.rmbAmount)");
			sb2.append(sb.toString());
			hql = sb2.toString();
			totalAmout = (Double)commonService.uniqueResult(hql);
			//totalAmout ;
			
			return SUCCESS;
		}catch(Exception e){
			e.printStackTrace();
			this.messageAction = "��ѯ�ܸ�����ʧ��!";
			return this.OPERATE_ERROR;
		}
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
	 * @return the disposeId
	 */
	public Object[] getDisposeId() {
		return disposeId;
	}
	/**
	 * @param disposeId the disposeId to set
	 */
	public void setDisposeId(Object[] disposeId) {
		this.disposeId = disposeId;
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
	/**
	 * @return the startTime
	 */
	
	/**
	 * @return the totalAmout
	 */
	public Double getTotalAmout() {
		return totalAmout;
	}
	/**
	 * @param totalAmout the totalAmout to set
	 */
	public void setTotalAmout(Double totalAmout) {
		this.totalAmout = totalAmout;
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
	 * @return the startTime
	 */
	public String getStartTime() {
		return startTime;
	}
	/**
	 * @param startTime the startTime to set
	 */
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	/**
	 * @return the endTime
	 */
	public String getEndTime() {
		return endTime;
	}
	/**
	 * @param endTime the endTime to set
	 */
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
}
