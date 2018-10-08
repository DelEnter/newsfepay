package com.ecpss.action.express;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ecpss.action.BaseAction;
import com.ecpss.action.ExpressOrderThread;
import com.ecpss.action.ExpressUserThread;
import com.ecpss.model.payment.InternationalCardholdersInfo;
import com.ecpss.model.payment.InternationalTradeinfo;
import com.ecpss.model.shop.InternationalExpress;
import com.ecpss.model.shop.InternationalMerchant;
import com.ecpss.service.common.CommonService;

public class ExpressInfoAction extends BaseAction {
	
	@Autowired
	@Qualifier("commonService")
	private CommonService commonService;
	
	private String merid;
	
	//��������       �Ƿ��
	private String pt_code;//ƽ̨��ʶ(sfepay)
	private String customercode;//ƽ̨�̻���(mer.getMerNo)
	
	private String orders;
	
	private String exp_Code;
	private String expressStatus;//����״̬��δ�������ѷ���������Ͷ��
	private String orderNo;//Ǩ��ͨ������ˮ��            
	private String expressNo;//��������
	private String amout;//��������
	
	private String status;	
	private String message;
	
	private String expordermess;
	
	private String address;//	��ַmer.meradress
	private String true_name;//��ʵ����mer.mername
	private String phone;//�绰mer.mermobile
	
	private String expopenstatus;

	Logger logger = Logger.getLogger(ExpressInfoAction.class.getName());	

	
	public String expressOrder(){
		logger.info("********�µ�************");
		InternationalTradeinfo trade=(InternationalTradeinfo) commonService.uniqueResult("from InternationalTradeinfo where orderNo='"+this.getOrderNo()+"'");
		InternationalMerchant mer = (InternationalMerchant) commonService.uniqueResult("from InternationalMerchant where id='"+trade.getMerchantId()+"'");
		InternationalCardholdersInfo card = (InternationalCardholdersInfo) commonService.uniqueResult("from InternationalCardholdersInfo where tradeid='"+trade.getId()+"'");
			this.setPt_code("sfepay");
			this.setCustomercode(String.valueOf(mer.getMerno()));
	
			///JSONObject json = JSONObject.fromObject(card);
			
			JSONObject json = new JSONObject();
			
			json.put("pt_id", this.getOrderNo());			
			json.put("order_id", String.valueOf(trade.getId()));
			json.put("name", card.getShippingFullName());
			json.put("postcode", card.getShippingZip());
			json.put("phone", card.getPhone());
			json.put("mobile", card.getShippingPhone());
			json.put("country", card.getShippingCountry());	
			json.put("province", card.getShippingState());
			json.put("city", card.getShippingCity());
			json.put("street", card.getShippingAddress());
			json.put("email", card.getShippingEmail());
			//json.put("goods", card.getProductInfo());
			
			JSONObject a = new JSONObject();
			a.put("cnname", card.getProductInfo());
			a.put("enname", card.getProductInfo());
			a.put("count", "1");
			a.put("unit", card.getProductInfo());
			a.put("weight", "");
			a.put("delcarevalue", trade.getTradeAmount());
			a.put("sellurl", trade.getTradeUrl());
			a.put("description", card.getProductInfo());
			
			json.put("goods", "["+a.toString()+"]");
			
			//json.put("goods", a.toString());
			
			orders = json.toString();
			
			logger.info("********������Ϣ***********,�������ϣ�"+orders);

			//ExpressOrderThread  eut = new ExpressOrderThread(this.getPt_code(), this.getCustomercode(), orders,"http://139.224.211.58:8083/v1/order/push",commonService,trade);
			ExpressOrderThread  eut = new ExpressOrderThread(this.getPt_code(), this.getCustomercode(), orders,"http://localhost:8090/expressPayOrder",commonService,trade);
			
			eut.start();	
			
		return SUCCESS;
	}
	
	
	public String expressOpen(){
		InternationalMerchant mer = (InternationalMerchant) commonService.uniqueResult("from InternationalMerchant where id='"+merid+"'");
		merid = String.valueOf(mer.getMerno());
		if(mer.getExpopenstatus() == null ||mer.getExpopenstatus().equals("0") ){
			status = "0";
			message = "δ��ͨ!";
		}else if(mer.getExpopenstatus().equals("1")){
			status = "1";
			message = "�ѿ�ͨ!";
		}
	
		return SUCCESS;
	}
	
	
	public String expressExpOrder(){
		InternationalTradeinfo trade=(InternationalTradeinfo) commonService.uniqueResult("from InternationalTradeinfo where orderNo='"+this.getOrderNo()+"'");
		this.setExpordermess(trade.getExpordermess());
		return SUCCESS;
	}
	
	public String expressReg(){
		InternationalMerchant merchant = (InternationalMerchant) commonService.uniqueResult("from InternationalMerchant where merno='"+merid+"'");
		
		 /** �û�����µ���ץȡǨ��ͨ��ˮ�ţ���ʽ������̺ţ����ƾ����̻��ű���3604���Լ��û����������͹���
		 * ����ͨ���߳�ȥ����ʸ��Ľӿڵ�ַ��ͬ���̻���Ϣ��ͬ���û��������룩
		 * */
		 
		if(this.expopenstatus.equals("1") && merchant.getExpuername()==null){
			this.setPt_code("sfepay");
			this.setCustomercode(String.valueOf(merchant.getMerno()));
			this.setAddress(merchant.getMeradress());
			this.setTrue_name(merchant.getMername());
			this.setPhone(merchant.getMermobile());
			ExpressUserThread  eut = new ExpressUserThread(commonService,merchant,this.getPt_code(), this.getCustomercode(), this.getAddress(), this.getTrue_name(), this.getPhone(),"http://139.224.211.58:8083/v1/user/info");
			eut.start();
		}
		return SUCCESS;
	}
	
	
	public String getExpopenstatus() {
		return expopenstatus;
	}


	public void setExpopenstatus(String expopenstatus) {
		this.expopenstatus = expopenstatus;
	}


	public String getAddress() {
		return address;
	}


	public void setAddress(String address) {
		this.address = address;
	}


	public String getTrue_name() {
		return true_name;
	}


	public void setTrue_name(String true_name) {
		this.true_name = true_name;
	}


	public String getPhone() {
		return phone;
	}


	public void setPhone(String phone) {
		this.phone = phone;
	}


	public String getMerid() {
		return merid;
	}


	public void setMerid(String merid) {
		this.merid = merid;
	}


	public String getPt_code() {
		return pt_code;
	}


	public void setPt_code(String pt_code) {
		this.pt_code = pt_code;
	}


	public String getCustomercode() {
		return customercode;
	}


	public void setCustomercode(String customercode) {
		this.customercode = customercode;
	}


	public String getOrders() {
		return orders;
	}


	public void setOrders(String orders) {
		this.orders = orders;
	}


	public String getExpressStatus() {
		return expressStatus;
	}


	public void setExpressStatus(String expressStatus) {
		this.expressStatus = expressStatus;
	}


	public String getOrderNo() {
		return orderNo;
	}


	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}


	public String getExpressNo() {
		return expressNo;
	}


	public void setExpressNo(String expressNo) {
		this.expressNo = expressNo;
	}


	public String getAmout() {
		return amout;
	}


	public void setAmout(String amout) {
		this.amout = amout;
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


	public String getExpordermess() {
		return expordermess;
	}


	public void setExpordermess(String expordermess) {
		this.expordermess = expordermess;
	}
	
}
