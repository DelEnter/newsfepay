package com.ecpss.service.iservice;

import com.ecpss.vo.ImportRefundHistory;

public interface ImportExcelService {
	
	/**
	 * �жϿ����ڷ��ںڿ������
	 * @param card
	 * @return
	 */
	public boolean getBackCardBean(String card);
	
	/**
	 * ��Ӻڿ���Ϣ
	 * @param IP
	 * @param cardNo
	 * @param Email
	 * @param cookie
	 * @param remark
	 */
	public void saveBackCardInfo(String IP,String cardNo,String Email,String cookie,String remark);
	/**
	 * ��Ӹ߷��տ�����Ϣ
	 * @param IP
	 * @param cardNo
	 * @param Email
	 * @param cookie
	 * @param remark
	 */
	public void saveRickCardInfo(String IP,String cardNo,String Email,String cookie,String remark);
	
	/**
	 * �����˿��ϴ���¼
	 * @param rh
	 */
	public void saveRefundByBank(ImportRefundHistory rh,String fileName,String batchno);
	
	public Boolean getFileNameByRefund(String filename);
	
}
