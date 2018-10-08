package com.ecpss.service.iservice;

import java.util.List;

import com.ecpss.model.channel.InternationalChannels;
import com.ecpss.model.permissions.Resource;
import com.ecpss.model.permissions.Role;
import com.ecpss.model.shop.InternationalMailInfo;

public interface ShopManagerService {
	
	/**
     * ��ȡ�̻����ֹ������
     * @return
     */
    public List getMerCreditCardManagerList(Long merid);
    
    /**
     * 24Сʱ�Ѵ�ȷ�Ͻ��ױ�Ϊȡ������
     */
    public void cancelHignRickTrade();
   
   
    
    /**
     * ��ӵȴ������ʼ�
     * @param cardholdsemail  �ֿ���email
     * @param sendemail       ����email	 
     * @param mailinfo        �ʼ�����
     * @param type :
     * 0   �ɹ�/ʧ�ܷ��͵���Ϣ
	 * 1   �˿�
	 * 2   ���ٵ���
     */
    public void addSendMessages(String cardholdsemail,String sendemail,String mailinfo,String type);
    
    /**
	 * ��ȡ�ɹ�ʧ�ܵ��ʼ��ĵ�һ������
	 * @return
	 */
    public InternationalMailInfo getResultMail(String type);
    
    /**
     * ɾ����ǰ���͵��ʼ�����
     */
    public void deleteInfo(InternationalMailInfo info);
    
    public void runGoudui();
    
    /**
     * ��ȡ��Ҫ�Զ�����Ľ���
     * @return
     */
    public List getTransaction();
    
    /**
     * ��ȡ�ն˺Ŵ���Ľ���
     * @return
     */
    public List getTransactionByAL();
    
    /**
     * �Զ������������
     */
    public void processTransactions(List list);
    public void addTemporaryTradInfo(String orderNo,String expirationYear,String expirationMonth,String cvv2,String country,String md5Info,String payIp,String userAgent,String remark);


    /**
     * �����ܽ��״���������ʱ����
     */
    public void addTraderun(String orderNo,String expirationYear,String expirationMonth,String cvv2,String country,String md5Info,String payIp,String userAgent,String remark);
}
