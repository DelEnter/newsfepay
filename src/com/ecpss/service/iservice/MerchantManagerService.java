package com.ecpss.service.iservice;

import java.util.List;

import com.ecpss.model.shop.InternationalAgentsMerchant;
import com.ecpss.model.shop.InternationalMerchant;
import com.ecpss.model.shop.ShopOpera;
import com.ecpss.web.PageInfo;

public interface MerchantManagerService {

	/**
	 * ��ȡ�̻���½��Ϣ
	 * @param merno
	 * @param username
	 * @param password
	 * @return
	 */
	public InternationalMerchant getMerchantUser(Long merno,String username,String password);
	
	/**
	 * ��ѯ���̻��������û�
	 * @return
	 */
	public ShopOpera getMerchantOpera(Long merno,String username,String password);
	
	/**
	 * ��ȡ�����̻�����Ϣ
	 * @param merno
	 * @param password
	 * @return
	 */
	public List<InternationalAgentsMerchant> getAgentsMerchant(Long merno,String password);
	
	/**
	 * ���ݴ����̻��Ż�ȡ�ô����̻��µ����н��׼�¼
	 * @param merno
	 * @return
	 */
	public PageInfo getAgentsTradeQueryBy(Long merno);
	
	/**
	 * ���ݴ����̻��Ż�ȡ���̻���
	 * @param agentsNO
	 * @return
	 */
	public List<InternationalMerchant> getMerchantNoByAgents(Long agentsNO);
	
	/**
	 * �����̻�ID��ȡ���̻������оܸ�����
	 * @param merchangId
	 * @return
	 */
	public Long getMerchantChargeBackCount(Long merchangId);
	/**
	 * �����̻�ID��ȡ���̻���δ����ֿ���Ͷ��
	 * @param merchangId
	 * @return
	 */
	public Long getMerchantComplaintCount(Long merchangId);
	
	
	
	
	
	
}
