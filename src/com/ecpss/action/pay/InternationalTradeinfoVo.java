package com.ecpss.action.pay;

import java.io.Serializable;
import java.util.Date;

public class InternationalTradeinfoVo  implements Serializable {


	private Date lastDate;//����޸�ʱ��

	private Long lastMan;//��������
	private Long id;

	private String orderNo; // Ecpss��ˮ������

	private String merchantOrderNo; // �̻���ˮ������

	private Long merchantNo; // �̻���

	private Date tradeTime; // ����ʱ��

	private Double tradeAmount; // ���׽���ң�

	private Double rmbAmount; // ����ҽ��

	/**
	 * tradeState�ֶ�״̬ ��һλ�����׳ɹ�״̬ 0:ʧ��,1:�ɹ�,2������ �ڶ�λ:
	 */

	private String tradeState; // ����״̬����ֶ�,����: 012121

	private String tradeChannel; // ֧��ͨ�����
}
