package com.ecpss.service.iservice;

import java.util.List;

import com.ecpss.model.payment.InternationalTradeinfo;
import com.ecpss.model.refund.InternationalRefundManager;
import com.ecpss.model.shop.InternationalMerchant;

public interface RefundService {
	
	/**
	 * ������ˮ�����ź��̻������ò�ѯ���� 
	 * @param tradeNo
	 * @param merOrderNo
	 * @return
	 */
	public InternationalTradeinfo getTradeByMerNo(String tradeNo,String merOrderNo);
	/**
	 * ��ȡ�̻�������������״̬���˿��¼����Ԥ��ȷ���ύ�˿�
	 * @return
	 */
	public List getRefundPreview();
	
	/**
	 * ���ݽ���ID��ѯ���˿��¼
	 * @param tradeId
	 * @return
	 */
	public List<InternationalRefundManager> getRefundByTradeId(Long tradeId);
	
	/**
	 * �����˿�
	 * @param refundIds
	 */
	public void createRefund(Long [] refundIds,String batchNo);
	/**
	 * �������κŲ�ѯ���˿��
	 * @param batchNo
	 * @return
	 */
	public List getRefundDetailByBatchNo(String batchNo,String refStatus);
	
	/**
	 * ��ȡ�Ѿ����ڵ�����˵��˿��¼
	 * @return
	 */
	public List<InternationalMerchant> getMerchantList();
	
	/**
	 * �����˿�״̬��ȡ��ͬ״̬���˿��¼�е��ն˺�
	 * @param refundstate
	 * @return
	 */
	public List<String> getTerminalNoByRefund(Long refundstate);
		
	/**
	 * �����˿�����ļ�
	 * @return
	 */
	public String batchRefund();

	/**
	 * �����˿�����ļ�   MIGS �˿�
	 * @return
	 */
	public String batchRefundByVC();


}
