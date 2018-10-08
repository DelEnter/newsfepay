package com.ecpss.action.mersetting;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ecpss.action.BaseAction;
import com.ecpss.model.channel.InternationalChannels;
import com.ecpss.model.channel.InternationalMerchantChannels;
import com.ecpss.model.log.SystemLog;
import com.ecpss.model.shop.InternationalMerchant;
import com.ecpss.service.iservice.ChannelService;
import com.ecpss.vo.channel.InternationalWebchannelsLog;

public class MerChannelSettingAction extends BaseAction {
	
	@Autowired
	@Qualifier("channelService")
	private ChannelService channelService;
	
	private Long merid;            //�̻�id
	private List merChannelList;
	private Long merChannelId;
	private String onoff;
	private InternationalMerchant merchant;
	private List<InternationalChannels> channelList;
	
	private InternationalMerchantChannels merchannel;
	
	private String merno;  //�̻���
	private Long merchantno;
	private String channelName;		//ͨ������
	/**
	 * ��ת���̻�ͨ������ҳ��
	 * @return
	 */
	public String toMerChannel(){
		if(merchantno!=null){
			String hlq="select m.id from InternationalMerchant m where m.merno="+merchantno;
			merid = (Long) this.commonService.uniqueResult(hlq);
			merChannelList = channelService.getMerChannelList(merid);
		}else{
			merChannelList = channelService.getMerChannelList(merid);
			String h="select m.merno from InternationalMerchant m where m.id="+merid;
			merchantno = (Long) this.commonService.uniqueResult(h);
		}
		this.getLoaction().setReload(true);
		return SUCCESS;
	}
	/**
	 * ��ת������̻�ͨ��,�����̻���ID
	 * @return
	 */
	public String toAddMerChannel(){
		merchant = (InternationalMerchant) this.commonService.load(InternationalMerchant.class, merid);
		channelList = channelService.getChannelList();
		return SUCCESS;
	}
	/**
	 * ����̻�ͨ����Ϣ.����������Ϣ
	 * @return
	 */
	public String addMerChannel(){
		merchannel.setExecuteTime(new Date());
		merchannel.setLastDate(new Date());
		merchannel.setLastMan(getUserBean().getUserName());
		merchannel.setOnoff("1");
		this.commonService.save(merchannel);
		this.messageAction="����ͨ���ɹ�";		
		return this.OPERATE_SUCCESS;
	}
	/**
	 * ��ת���̻�ͨ���޸�ҳ��
	 * @return
	 */
	public String toUpdateMerChannel(){
		merchant = (InternationalMerchant) this.commonService.load(InternationalMerchant.class, merid);
		channelList = channelService.getChannelList();
		
		merchannel = (InternationalMerchantChannels) this.commonService.load(InternationalMerchantChannels.class, merChannelId);
		
		return SUCCESS; 
	}
	/**
	 * �޸��̻�ͨ����Ϣ
	 * @return
	 */
	public String updateMerChannel(){
		InternationalMerchantChannels mc = (InternationalMerchantChannels) this.commonService.load(InternationalMerchantChannels.class, merChannelId);
		InternationalMerchant m = (InternationalMerchant) this.commonService.load(InternationalMerchant.class, mc.getMerchantId());
		// ������־ʵ�����
		SystemLog sl = new SystemLog();
		
		// �����̻�ͨ��vo ��¼����ǰ��״̬
		InternationalWebchannelsLog iwl = new InternationalWebchannelsLog();
		//1.��¼����ǰ��״̬
		iwl.setLastDate(mc.getLastDate());
		iwl.setLastMan(mc.getLastMan());
		iwl.setBailCharge(mc.getBailCharge());
		iwl.setBailCycle(mc.getBailCycle());
		iwl.setBalanceCharge(mc.getBalanceCharge());
		iwl.setBalanceCycle(mc.getBalanceCycle());
		iwl.setChannelId(mc.getChannelId());
//		//����ͨ������
//		iwl.setChannelName(channelName);
		iwl.setExecuteTime(mc.getExecuteTime());
		iwl.setMerchantId(mc.getMerchantId());
		iwl.setOnoff(mc.getOnoff());
		iwl.setOperator(mc.getOperator());
		iwl.setMerno(m.getMerno()+"");
		
		mc.setBailCycle(merchannel.getBailCycle());
		mc.setBailCharge(merchannel.getBailCharge());
		mc.setBalanceCharge(merchannel.getBalanceCharge());
		mc.setBalanceCycle(merchannel.getBalanceCycle());
		mc.setChannelId(merchannel.getChannelId());
		mc.setMaxmind_lv1(merchannel.getMaxmind_lv1());
		mc.setMaxmind_lv2(merchannel.getMaxmind_lv2());
		mc.setPriority(merchannel.getPriority());
		mc.setChannelFee(merchannel.getChannelFee());
		mc.setExecuteTime(new Date());
		this.commonService.update(mc);
		
		//2.��¼�������״̬ 
		iwl.setBailChargeafter(mc.getBailCharge());
		iwl.setBailCycleafter(mc.getBailCycle());
		iwl.setBalanceChargeafter(mc.getBalanceCharge());
		iwl.setBalanceCycleafter(mc.getBalanceCycle());
		iwl.setChannelIdafter(mc.getChannelId());
		//iwl.setChannelNameatter(channelName);
		iwl.setExecuteTimeafter(mc.getExecuteTime());
		iwl.setMerchantIdafter(mc.getMerchantId());
		iwl.setOnoffafter(mc.getOnoff());
		iwl.setOperatorafter(mc.getOperator());
		
		
		
		// ������־����ֵ
		sl.setUserName(this.getUserBean().getUserName());
		sl.setOperTime(new Date());
		sl.setOperType("2");
		sl.setMerno(iwl.getMerno());
		sl.setRemarks(iwl.getUpdateInternationalWebchannelsLog());
		sl.setRescReow("�޸��̻�ͨ��");
		this.commonService.save(sl);
		this.messageAction="�޸ĳɹ�";		
		return this.OPERATE_SUCCESS;
	}
	/**
	 * �̻�ͨ������
	 * @return
	 */
	public String openorclose(){
		System.out.println(onoff);
		if(onoff.equals("1")){
			onoff="0";
			this.messageAction="ͨ���ѹر�";
		}else if(onoff.equals("0")){
			onoff="1";
			this.messageAction="ͨ���ѿ�ͨ";
		}
		InternationalMerchantChannels mc = (InternationalMerchantChannels) this.commonService.load(InternationalMerchantChannels.class, merChannelId);
		mc.setOnoff(onoff);
		mc.setExecuteTime(new Date());
		this.commonService.update(mc);
		return this.OPERATE_SUCCESS;
	}
	
	public ChannelService getChannelService() {
		return channelService;
	}

	public void setChannelService(ChannelService channelService) {
		this.channelService = channelService;
	}

	public List getMerChannelList() {
		return merChannelList;
	}

	public void setMerChannelList(List merChannelList) {
		this.merChannelList = merChannelList;
	}

	public Long getMerid() {
		return merid;
	}

	public void setMerid(Long merid) {
		this.merid = merid;
	}
	public InternationalMerchant getMerchant() {
		return merchant;
	}
	public void setMerchant(InternationalMerchant merchant) {
		this.merchant = merchant;
	}
	public List<InternationalChannels> getChannelList() {
		return channelList;
	}
	public void setChannelList(List<InternationalChannels> channelList) {
		this.channelList = channelList;
	}
	public InternationalMerchantChannels getMerchannel() {
		return merchannel;
	}
	public void setMerchannel(InternationalMerchantChannels merchannel) {
		this.merchannel = merchannel;
	}
	public Long getMerChannelId() {
		return merChannelId;
	}
	public void setMerChannelId(Long merChannelId) {
		this.merChannelId = merChannelId;
	}
	public String getOnoff() {
		return onoff;
	}
	public void setOnoff(String onoff) {
		this.onoff = onoff;
	}
	public String getMerno() {
		return merno;
	}
	public void setMerno(String merno) {
		this.merno = merno;
	}
	public String getChannelName() {
		return channelName;
	}
	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}
	public Long getMerchantno() {
		return merchantno;
	}
	public void setMerchantno(Long merchantno) {
		this.merchantno = merchantno;
	}
	
}
