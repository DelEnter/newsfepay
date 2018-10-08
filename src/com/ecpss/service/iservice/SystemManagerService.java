package com.ecpss.service.iservice;

import java.io.File;
import java.util.List;

import com.ecpss.action.pay.tc.XMLGetMessage;
import com.ecpss.model.payment.InternationalCardholdersInfo;
import com.ecpss.model.payment.InternationalTradeinfo;
import com.ecpss.model.shop.InternationalChargeBack;

public interface SystemManagerService {
	
	/**
	 * �����ն˺Ż�ȡ�ճɹ����ױ���
	 * @param TermialNo
	 * @return
	 */
	public Long getTradeCountByTerminalNo(String TermialNo);
	/**
	 * �����ն˺Ż�ȡ�³ɹ����ױ���
	 * @param TermialNo
	 * @return
	 */
	public Long getTradeCountByTerminalNoMonth(String TermialNo);
	
	/**
	 * �رճ������ñ������ն�
	 */
	public void closeTerminalNoByMore();
	
	/**
	 * �ϴ����и��ľܸ�
	 * @param fileName
	 */
	public String importChargeBack(File fileName,String batchno);
	
	/**
	 * ���ݾܸ�������еĿ��Ÿ�����ѯ�Ƿ������������
	 * @param cardNo
	 * @param amount
	 * @return
	 */
	public boolean getByCardNoAndAmount(String cardNo,Double amount);
	
	/**
	 * ���ݽ��ױ�Id�ܸ��ý���
	 * @param tradeId
	 */
	public void chargeBack(Long tradeId);
	/**
	 * �ܸ��������,�޸Ľ��ױ�Ϊ�ܸ�,����Ϊ�ڿ����߷��տ�
	 * @param cb
	 * @param isBackCard
	 * @param isRickCard
	 * @return
	 */
	public String chargeBackUpdate(InternationalChargeBack cb,Long tradeId,String isBackCard,String isRickCard);
	
	/**
	 * ���ݽ����ջ�ȡ�ý������µ��̻�
	 * @param cycle
	 * @return
	 */
	public List getMerchantNoByCycle(Long cycle);
	
	/**
	 * BOC�������״���
	 * @param tradeinfo
	 * @param cardinfo
	 * @return
	 */
	public XMLGetMessage BocCancelTrade(InternationalTradeinfo tradeinfo,InternationalCardholdersInfo cardinfo);
	
	/**
	 * BOC�˿��
	 * @param tradeinfo
	 * @param cardinfo
	 * @param refundamount
	 * @return
	 */
	public XMLGetMessage BocRefundTrade(InternationalTradeinfo tradeinfo,InternationalCardholdersInfo cardinfo,Double refundamount,String orderno);
}
