package com.ecpss.service.express;

import java.util.List;

import com.ecpss.model.payment.InternationalTradeinfo;

public interface ExpressService {
	//���ݷ�ʴ�������ˮ����INTERNATIONAL_TRADEINFO�в�һ����û�ж�Ӧ��orderNo	
	public List<InternationalTradeinfo> select(Long orderNo);
}
