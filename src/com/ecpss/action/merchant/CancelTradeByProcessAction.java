package com.ecpss.action.merchant;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.ecpss.action.BaseAction;
import com.ecpss.model.payment.InternationalTemporaryTradInfo;
import com.ecpss.model.payment.InternationalTradeinfo;
import com.ecpss.model.shop.InternationalMerchant;
import com.ecpss.util.StatusUtil;

public class CancelTradeByProcessAction extends BaseAction{

	private String orderNo;   //��ѯ��ˮ��
	private List tradeList;
	/**
	 * ��ȡ���������б�
	 * ����������ˮ�Ų�ѯ
	 * @return
	 */
	public String processTradeQuery(){
		StringBuffer sb = new StringBuffer();
		sb.append("select ti from InternationalTradeinfo ti where 1=1 and ti.tradeState like '2%' ");
		if(StringUtils.isNotBlank(orderNo)){
			sb.append(" and ti.orderNo='"+orderNo.trim()+"'");
			tradeList = this.commonService.list(sb.toString());
		}
		return SUCCESS;
	}
	
	/**
	 * ȡ������
	 * ÿ��ֻ��ȡ��һ��
	 * @return
	 */
	public String cancelTrade(){
		InternationalMerchant merchant = (InternationalMerchant) this.commonService.load(InternationalMerchant.class, getMerchantBean().getMerchantId());
		if(merchant!=null){
			if(merchant.getProcessModifyDate()==null){
				Calendar calendar = Calendar.getInstance();//��ʱ��ӡ����ȡ����ϵͳ��ǰʱ��
		        calendar.add(Calendar.DATE, -1);    //�õ�ǰһ��
				merchant.setProcessModifyDate(calendar.getTime());
				merchant.setProcessModifyCount(0L);
				this.commonService.update(merchant);
			}
			SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
			String date = sf.format(new Date());
			//�����һ��ȡ���������� �뵱ǰ����һ���ľͲ���ȡ��
			if(!sf.format(merchant.getProcessModifyDate()).equals(date)){
				String hql = "select ti from InternationalTradeinfo ti,InternationalMerchant m where " +
				" ti.merchantId=m.id " +
				"and ti.orderNo='"+orderNo.trim()+"' " +
						"and m.id="+merchant.getId();
				InternationalTradeinfo tradeinfo = (InternationalTradeinfo) this.commonService.uniqueResult(hql);
				if(tradeinfo!=null){
					tradeinfo.setTradeState(StatusUtil.updateStatus(tradeinfo.getTradeState(), 1, "0"));
					tradeinfo.setRemark("�̻�Ҫ��ȡ��");
					tradeinfo.setVIPDisposeDate(new Date());
					tradeinfo.setVIPDisposePorson(getMerchantBean().getMerchantUserName());
					this.commonService.update(tradeinfo);
					merchant.setProcessModifyDate(new Date());
					merchant.setProcessModifyCount(merchant.getProcessModifyCount()+1L);
					this.commonService.update(merchant);
					InternationalTemporaryTradInfo tem=(InternationalTemporaryTradInfo) commonService.uniqueResult("from InternationalTemporaryTradInfo where orderNo='"+tradeinfo.getOrderNo()+"'");
					if(StringUtils.isNotBlank(tem.getOrderNo())){
						commonService.delete(tem);
					}
					this.messageAction="ȡ���ɹ�,һ��ֻ��ȡ��һ��.лл";
				}else{
					this.messageAction="�޴˽���,ȷ�ϵ�½��Ϣ�Ƿ���ȷ.";
				}
			}else{
				this.messageAction="ÿ��ֻ��ȡ��һ�ʴ�������,����Ҫȡ����������ϵ�ͷ�����.лл.";
			}
		}
		return SUCCESS;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public List getTradeList() {
		return tradeList;
	}

	public void setTradeList(List tradeList) {
		this.tradeList = tradeList;
	}
}
