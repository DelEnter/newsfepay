package com.ecpss.action.express;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import sun.misc.BASE64Encoder;

import com.ecpss.action.BaseAction;
import com.ecpss.action.pay.TradeManager;
import com.ecpss.action.tradequery.HjRecQueryAction;
import com.ecpss.model.affiche.AfficheManager;
import com.ecpss.model.log.SystemLog;
import com.ecpss.model.payment.InternationalTradeinfo;
import com.ecpss.model.shop.InternationalAgentsMerchant;
import com.ecpss.model.shop.InternationalExpress;
import com.ecpss.model.shop.InternationalMerchant;
import com.ecpss.model.shop.ShopOpera;
import com.ecpss.service.common.CommonService;
import com.ecpss.service.express.ExpressService;
import com.ecpss.service.iservice.MerchantManagerService;
import com.ecpss.util.AES;
import com.ecpss.util.DateUtil;
import com.ecpss.vo.MerchantBean;
import com.ecpss.web.PageInfo;
import com.opensymphony.xwork2.ActionContext;
import com.sun.mail.iap.Response;

public class ExpressAction extends BaseAction{
	Logger logger = Logger.getLogger(ExpressAction.class.getName());
	@Autowired
	@Qualifier("commonService")
	private CommonService commonService;
	
	private String merNo;
	private String orderNo;
	private String exp_Code;	
	private String status;	
	private String message;
		
	private String expressStatus;//����״̬��δ�������ѷ���������Ͷ��           
	private String expressNo;//��������
	private String amount;//��������

	private String orders;
	
	public String express(){
			HttpServletRequest request = ServletActionContext.getRequest();
			InternationalTradeinfo trade=(InternationalTradeinfo) commonService.uniqueResult("from InternationalTradeinfo where orderNo='"+this.getOrderNo()+"'");
			logger.info("*******��ʼ��ѯ����************:"+this.getOrderNo());
			if(trade == null){
				trade = new InternationalTradeinfo();
				status = "01";
				message = "�޷��������Ķ���!";
			}else{								
				logger.info("*************������Ϣ**************��"+trade.getTradeState());
				if(trade.getTradeState().substring(0, 1).equals("0")){
					status = "01";
					message = "�޷��������Ķ���!";
				}else if(trade.getTradeState().substring(0, 1).equals("1")){
					status = "00";
					message = "��������!";
				}
			}
			
		return SUCCESS;
	}
	
	public String expressSta(){
		HttpServletRequest request = ServletActionContext.getRequest();
		logger.info("********��ѯ���״̬����************");		
		InternationalTradeinfo trade=(InternationalTradeinfo) commonService.uniqueResult("from InternationalTradeinfo where orderNo='"+this.getOrderNo()+"'");		
		if(trade == null){
			trade = new InternationalTradeinfo();
			status="01";			
			message="ͬ�����ݳ����쳣!";
		}else{
			InternationalExpress Exp=(InternationalExpress) commonService.uniqueResult("from InternationalExpress where tradeid='"+trade.getId()+"'");
			logger.info("********���״̬************,�̻�������ˮ��"+this.getOrderNo()+"   ����״̬��" +this.getExpressStatus()+"   �������ţ�" +this.getExpressNo()+"   �������ã�"+this.getAmount());
			if (Exp == null)
			{
				Exp = new InternationalExpress();
				Exp.setTradeid(trade.getId());			
				Exp.setExpresstype("fenghuang");						
				Exp.setCreatetime(new Date());					
				commonService.save(Exp); 
			}
			
			if(this.getExpressStatus().equals("δ����")){
				Exp.setExpressstaus("0");
			}else if(this.getExpressStatus().equals("�ѷ���")){
				Exp.setExpressstaus("1");
			}else if(this.getExpressStatus().equals("������")){
				Exp.setExpressstaus("2");
			}else if(this.getExpressStatus().equals("��Ͷ��")){
				Exp.setExpressstaus("3");
			}
			
			if(this.getAmount()==null){
				amount="0";	
				Exp.setExpressamout(Double.valueOf(this.getAmount()).doubleValue());
			}else{
				Exp.setExpressamout(Double.valueOf(this.getAmount()).doubleValue());
			}
			Exp.setExpressno(this.getExpressNo());
			Exp.setOperatertime(new Date());		
			
			commonService.update(Exp);
			
			status="00";
			
			message="����ͬ���ɹ�!";
			
			logger.info("********���״̬���³ɹ�***********");
			
		}
		
		return SUCCESS;
	}
	
	public void expressPaySta() throws Exception{
		HttpServletRequest request = ServletActionContext.getRequest();
		logger.info("*********�����̻����뵣��***********:"+merNo);		
		InternationalMerchant mer = (InternationalMerchant) commonService.uniqueResult("from InternationalMerchant where merNo='"+this.getMerNo()+"'");
		JSONObject json = new JSONObject();
		if(mer != null&&StringUtils.isNotBlank(mer.getExpopenstatus())){
			mer.setExppay("1");
			json.put("status", 00);
			json.put("message", "���ͳɹ�");
		}else{
			mer.setExppay("0");
			json.put("status", 01);
			json.put("message", "����ʧ��");
		}
		HttpServletResponse response = ServletActionContext.getResponse();
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out = response.getWriter();
		out.print(json.toString());
	}
	
	public void expressPayOrder() throws Exception{
		HttpServletRequest request = ServletActionContext.getRequest();
		logger.info("*********�����̻����뵣��***********:"+merNo+orders);		
		JSONArray json = JSONArray.fromObject(orders);
	
		for(int i = 0; i < json.size(); i++) {
		    JSONObject object = (JSONObject) json.get(i);
		    String postcode = object.getString("postcode");
		    String phone = object.getString("phone");
		    String mobile = object.getString("mobile");
		}
	
		HttpServletResponse response = ServletActionContext.getResponse();
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out = response.getWriter();
		out.print(json.toString());
	}
	
	public String getMerNo() {
		return merNo;
	}

	public void setMerNo(String merNo) {
		this.merNo = merNo;
	}

	public String getExpressStatus() {
		return expressStatus;
	}

	public void setExpressStatus(String expressStatus) {
		this.expressStatus = expressStatus;
	}

	public String getExpressNo() {
		return expressNo;
	}

	public void setExpressNo(String expressNo) {
		this.expressNo = expressNo;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getOrders() {
		return orders;
	}

	public void setOrders(String orders) {
		this.orders = orders;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getExp_Code() {
		return exp_Code;
	}

	public void setExp_Code(String exp_Code) {
		this.exp_Code = exp_Code;
	}

	public CommonService getCommonService() {
		return commonService;
	}

	public void setCommonService(CommonService commonService) {
		this.commonService = commonService;
	}

}
