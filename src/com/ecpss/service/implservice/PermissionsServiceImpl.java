package com.ecpss.service.implservice;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecpss.action.bail.InternationalCreateBaihuakuanVo;
import com.ecpss.dao.common.CommonDao;
import com.ecpss.model.payment.InternationalBailhuakuan;
import com.ecpss.model.payment.InternationalCreateBaihuakuan;
import com.ecpss.model.payment.InternationalSettlment;
import com.ecpss.model.payment.InternationalTradeinfo;
import com.ecpss.model.payment.TradeDetails;
import com.ecpss.model.payment.ViewSettlement;
import com.ecpss.model.permissions.Resource;
import com.ecpss.model.permissions.Role;
import com.ecpss.model.shop.InternationalMerchant;
import com.ecpss.service.common.CommonService;
import com.ecpss.service.iservice.PermissionsService;
import com.ecpss.util.GetBatchNo;
import com.ecpss.vo.InternationalHuakuan;
import com.ecpss.vo.UserBean;
import com.opensymphony.xwork2.ActionContext;

@Service("permissionsService")
@Transactional
public class PermissionsServiceImpl implements PermissionsService {
	@Autowired
	@Qualifier("commonDao")
	private CommonDao commonDao;
	@Autowired
	@Qualifier("commonService")
	private CommonService commonService;

	public List<Resource> getResource() {
		List<Resource> list = this.commonDao.list("from  Resource");
		return list;
	}

	public List<Role> getRoleList() {
		// List<Role> getrole = this.commonDao.getSql("select {SYSTEMROLE.*}
		// from SYSTEMROLE_2010_02 {SYSTEMROLE}","SYSTEMROLE",new Role());
		List<Role> getrole2 = this.commonDao.getSql(
				"select {SYSTEMROLE.*} from SYSTEMROLE {SYSTEMROLE}",
				"SYSTEMROLE", new Role());
		// List<Role> getrole3 = this.commonDao.getSql("select {payst.*} from
		// SYSTEMROLE {payst}","payst",new Role());

		return getrole2;

	}

	public void addRole(Role role) {
		this.commonDao.save(role);
	}

	public List getRoleUser() {
		List list = this.commonDao
				.getBySql("select e.USER_NAME, w.ROLENAME from employee e left join  roleuser r  on e.id=r.user_id ,systemrole w where w.id=r.role_id ");
		return list;
	}

	public void setTelement(Map map, String remark) {
		Long batchno = (Long) map.get("batchno");
		String normulID = (String) map.get("nurmalID");
		List viewList = (List) map.get("viewList");
		Long merchantNo = (Long) map.get("merchantId");
		Double dishonor =  (Double)map.get("dishonor");
		// ��������(�ѻ���ĳ�����)
		List<InternationalTradeinfo> list1 = this.commonDao
				.list("from InternationalTradeinfo a where a.id in ("
						+ normulID + ")");
		// �쳣���ӣ�����δ����ȫ���˿δ����ܸ�
		List<InternationalTradeinfo> list2 = this.commonDao
				.list("from InternationalTradeinfo t where t.merchantId='"
						+ merchantNo
						+ "' and ( (substr(t.tradeState,2,1)=1 and substr(t.tradeState,8,1)=0 ) or (substr(t.tradeState,3,1)=1 and substr(t.tradeState,8,1)=0 ) ) ");
       //�쳣���ӣ������ѻ�����Ǽ����쳣����
		List list3 = this.commonDao
		.list("select t from InternationalTradeinfo t ,HuakuanedException h  where t.merchantId ='"+merchantNo+"' and h.tradeId=t.id ");		
		
		// ������ϸ
		for (int i = 0; i < list1.size(); i++) {
			InternationalTradeinfo tem2 = list1.get(i);
			TradeDetails td = new TradeDetails();
			if (tem2.getBackCount() != null) {
				td.setBackAcount(tem2.getBackCount());
			}			
			td.setBatchno(batchno);
			if (tem2.getTradeChannel() != null) {
				td.setChannels(tem2.getTradeChannel() + "");
			}
			if (tem2.getTradeTime() != null) {
				td.setTradetime(tem2.getTradeTime());
			}
			if (tem2.getIsPicture() != null) {
				td.setIspicture(tem2.getIsPicture());
			}
			if (tem2.getIsTrackNo() != null) {
				td.setIstrackno(tem2.getIsTrackNo());
			}
			// td.setDetail(tem2.ge);
			if (tem2.getMerchantId() != null) {
				td.setMerchantno(tem2.getMerchantId());
			}
			if (tem2.getMerchantOrderNo() != null) {
				td.setMerchantorderno(tem2.getMerchantOrderNo());
			}
			if (tem2.getMoneyType() != null) {
				td.setMoneytype(tem2.getMoneyType());
			}
			if (tem2.getTradeAmount() != null) {
				td.setOrdercount(tem2.getTradeAmount());
			}
			if (tem2.getRemark() != null) {
				td.setRemark(tem2.getRemark());
			}
			if (tem2.getRmbAmount() != null) {
				td.setRmbmoney(tem2.getRmbAmount());
			}
			if (tem2.getOrderNo() != null) {
				td.setRorderno(tem2.getOrderNo());
			}
			td.setTradeState(tem2.getTradeState());

			this.commonDao.save(td);
		}
		for (int k = 0; k < list2.size(); k++) {
			InternationalTradeinfo tem2 = list2.get(k);
			TradeDetails td = new TradeDetails();
			td.setBatchno(batchno);
			if (tem2.getBackCount() != null) {
				td.setBackAcount(tem2.getBackCount());
			}			
			if (tem2.getTradeChannel() != null) {
				td.setChannels(tem2.getTradeChannel() + "");
			}
			if (tem2.getTradeTime() != null) {
				td.setTradetime(tem2.getTradeTime());
			}
			if (tem2.getIsPicture() != null) {
				td.setIspicture(tem2.getIsPicture());
			}
			if (tem2.getIsTrackNo() != null) {
				td.setIstrackno(tem2.getIsTrackNo());
			}
			// td.setDetail(tem2.ge);
			if (tem2.getMerchantId() != null) {
				td.setMerchantno(tem2.getMerchantId());
			}
			if (tem2.getMerchantOrderNo() != null) {
				td.setMerchantorderno(tem2.getMerchantOrderNo());
			}
			if (tem2.getMoneyType() != null) {
				td.setMoneytype(tem2.getMoneyType());
			}
			if (tem2.getTradeAmount() != null) {
				td.setOrdercount(tem2.getTradeAmount());
			}
			if (tem2.getRemark() != null) {
				td.setRemark(tem2.getRemark());
			}
			if (tem2.getRmbAmount() != null) {
				td.setRmbmoney(tem2.getRmbAmount());
			}
			if (tem2.getOrderNo() != null) {
				td.setRorderno(tem2.getOrderNo());
			}
			td.setTradeState(tem2.getTradeState());
			this.commonDao.save(td);
		}
		for (int i = 0; i < list3.size(); i++) {
			InternationalTradeinfo tem2 = (InternationalTradeinfo)list3.get(i);
			TradeDetails td = new TradeDetails();
			td.setBatchno(batchno);
			if (tem2.getBackCount() != null) {
				td.setBackAcount(tem2.getBackCount());
			}			
			if (tem2.getTradeChannel() != null) {
				td.setChannels(tem2.getTradeChannel() + "");
			}
			if (tem2.getTradeTime() != null) {
				td.setTradetime(tem2.getTradeTime());
			}
			if (tem2.getIsPicture() != null) {
				td.setIspicture(tem2.getIsPicture());
			}
			if (tem2.getIsTrackNo() != null) {
				td.setIstrackno(tem2.getIsTrackNo());
			}
			// td.setDetail(tem2.ge);
			if (tem2.getMerchantId() != null) {
				td.setMerchantno(tem2.getMerchantId());
			}
			if (tem2.getMerchantOrderNo() != null) {
				td.setMerchantorderno(tem2.getMerchantOrderNo());
			}
			if (tem2.getMoneyType() != null) {
				td.setMoneytype(tem2.getMoneyType());
			}
			if (tem2.getTradeAmount() != null) {
				td.setOrdercount(tem2.getTradeAmount());
			}
			if (tem2.getRemark() != null) {
				td.setRemark(tem2.getRemark());
			}
			if (tem2.getRmbAmount() != null) {
				td.setRmbmoney(tem2.getRmbAmount());
			}
			if (tem2.getOrderNo() != null) {
				td.setRorderno(tem2.getOrderNo());
			}
			td.setTradeState(tem2.getTradeState());

			this.commonDao.save(td);
		}		
		// �������ʵ�
		Double ordercount = 0d;
		String trademoneyname = "";
		Double Fundmentmoney = 0d;
		Double freezecount = 0d;

		for (int i = 0; i < viewList.size(); i++) {
			InternationalHuakuan hf = (InternationalHuakuan) viewList.get(i);
			ordercount += hf.getTradeMoneyAll();
			trademoneyname = hf.getTradeMoneykindName();
			Fundmentmoney = Fundmentmoney + hf.getBalancemoney();
			// freezecount=freezecount+hf.getTradeMoneyAll()*hf.getBalancerate();
			hf.setDishonor(dishonor);
			freezecount = freezecount + hf.getRmbmoney();
		}

		InternationalSettlment is = new InternationalSettlment();
		is.setMerchantno(merchantNo);
		is.setBatchno(batchno);
		is.setOrdercount(ordercount);// ���׽��(δ�����)
		is.setTrademoneyname(trademoneyname);// ���ױ���
		is.setRefundmentmoney(Fundmentmoney);// ������
		/*is.setRiskFee(riskRmbmoney);//��ط���
		is.setChannelFee(channelFee);//����*/
		is.setFreezecount(freezecount);// ����RMB���
		is.setCreatetabletime(new Date());
		if (remark.equals("") || remark == null) {

		} else {
			is.setRemark(remark);
		}
		is.setDisposedate(new Date());
		UserBean use = (UserBean)ActionContext.getContext().getSession().get("user");
		is.setDisposeman(use.getUserName());
		
		is.setIstrue("0");
		// is.setProtestmoney(protestmoney);//�ܸ�
		// is.setBailmoney(bailmoney)bailmoney//��֤��
		// // createtabletime//
		// is.setProcedurefee(procedurefee)procedurefee//������
		// is.setBalancemoneyname(balancemoneyname)balancemoneyname//�������
		// is.setBalancemoney(balancemoney)balancemoney//������
		// huakuantime//
		// huakuanbankname

		this.commonDao.save(is);
		// ���滮��Ԥ��

		for (int h = 0; h < viewList.size(); h++) {
			InternationalHuakuan ih = (InternationalHuakuan) viewList.get(h);
			ih.setDishonor(dishonor);
			ViewSettlement tem = new ViewSettlement();
			tem.setBailmoney(ih.getBailmoney());
			tem.setRefuce(ih.getRefuce());
			tem.setRefuceMoney(ih.getRefuceMoney());
			tem.setRefuceMoneyAll(ih.getRefuceMoneyAll());
			tem.setRefuceComplement(ih.getRefuceComplement());
			tem.setRefuceComplementMoney(ih.getRefuceComplementMoney());
			tem.setRefuceComplementMoneyAll(ih.getRefuceComplementMoneyAll());
			tem.setBalancemoney(ih.getBalancemoney());
			
 			tem.setBatchno(batchno);
			tem.setBailrate(ih.getBailrate());
			tem.setBalancerate(ih.getBalancerate());
			tem.setChannels(ih.getChannels());

			tem.setExchangedate(ih.getExchangedate());
			tem.setFreezeMoney(ih.getFreezeMoney());
			tem.setFreezeNumber(ih.getFreezeNumber());
			tem.setNoProtestMoney(ih.getNoProtestMoney());

			tem.setNoProtestNumber(ih.getNoProtestNumber());
			tem.setNoTuiKuanMoney(ih.getNoTuiKuanMoney());
			tem.setNoTuikuanNumber(ih.getNoTuikuanNumber());
			tem.setNoTuiKuanMoneyall(ih.getNoTuiKuanMoneyall());

			tem.setNoTuikuanNumberall(ih.getNoTuikuanNumberall());
			tem.setNowBalanceRate(ih.getNowBalanceRate());
			tem.setProcedureFee(ih.getProcedureFee());
			tem.setProcedureRate(ih.getProcedureRate());

			tem.setProceFeeRation(ih.getProceFeeRation());
			tem.setProtestFee(ih.getProtestFee());
			tem.setProtestMoney(ih.getProtestMoney());
			tem.setProtestNumber(ih.getProtestNumber());

			
			tem.setRmbmoney(ih.getRmbmoney());
			/*tem.setRiskFee(riskRmbmoney);
			tem.setChannelFee(channelFee);*/
			tem.setThawMoney(ih.getThawMoney());
			tem.setThawNumber(ih.getThawNumber());
			tem.setTradeMoney(ih.getTradeMoney());

			tem.setTradeMoneyAll(ih.getTradeMoneyAll());
			tem.setTradeMoneykindName(ih.getTradeMoneykindName());
			tem.setTradeNumber(ih.getTradeNumber());
			tem.setTradeNumberAll(ih.getTradeNumberAll());

			tem.setTuiKuanMoney(ih.getTuiKuanMoney());
			tem.setTuiKuanMoneyall(ih.getTuiKuanMoneyall());
			tem.setTuiKuanNumber(ih.getTuiKuanNumber());
			tem.setTuiKuanNumberall(ih.getTuiKuanNumberall());
			this.commonDao.save(tem);
		}
		// ����״̬
//		this.commonDao
//				.deleteBySql("update international_tradeinfo t set t.tradestate=substr(t.tradestate,1,7)||'1'||substr(t.tradestate,9,length(t.tradestate)-8) where t.id in(1,2,3)");
		// ɾ���쳣���е�����
	    this.commonDao.deleteBySql("update huakuanedexception t set t.isaudit='1' where t.tradeid in (select f.id from international_tradeinfo f ,huakuanedexception g where g.tradeid=f.id and f.merchantid='"+merchantNo+"')");     

	}

	public void noPassSettlement(Long beatchNo) {
	     InternationalSettlment ist=(InternationalSettlment)this.commonDao.list("from InternationalSettlment t where t.batchno='"+beatchNo+"' ").get(0);
	//ɾ��������ϸ	
     this.commonDao.deleteBySql("delete from international_TradeDetails t where t.batchno='"+beatchNo+"'");	
    //ɾ������Ԥ��
     this.commonDao.deleteBySql("delete from international_viewsettlement t where t.batchno='"+beatchNo+"'");      
     //ɾ�������ܱ�
     this.commonDao.deleteBySql("delete from InternationalSettlment t where t.batchno='"+beatchNo+"'");
     //�����쳣��Ϊ����״̬

    this.commonDao.deleteBySql("update huakuanedexception t set t.isaudit='0' where t.tradeid in (select f.id from international_tradeinfo f ,huakuanedexception g where g.tradeid=f.id and f.merchantid='"+ist.getMerchantno()+"')");     
	}
    public void createBailMoney(StringBuffer sb1,List list,InternationalMerchant merchant ){
    	Date ceatetabletime = GetBatchNo.getTime();
    	String batchno = GetBatchNo.getBatchNo();
    	String checkbatchno=" from InternationalBailhuakuan t where t.batchno='"+batchno+"'";
    	List checkList=this.commonService.list(checkbatchno);
    	if(checkList.size()>0){
    		batchno=GetBatchNo.getBatchNo();
    	}
    	if(checkList.size()==0){
		String hql = "update international_tradeinfo trade set " +
		  "trade.tradestate=substr(trade.tradestate,1,9)||'1'||substr(trade.tradestate,11, length(trade.tradestate)-10)," +
		  "trade.batchno="+batchno+" WHERE trade.id in("+sb1.toString()+")";
		commonService.deleteBySql(hql);
		
		hql = "SELECT min(trade.tradeTime), max(trade.tradeTime)FROM InternationalTradeinfo trade WHERE trade.id in("+sb1.toString()+")";
		Object[] o = (Object[])commonService.uniqueResult(hql); 
		//�������ɱ�֤�𻮿��
		List<InternationalCreateBaihuakuanVo> createBail = list;
		InternationalBailhuakuan bh = new InternationalBailhuakuan();
        Double totalMoney=0d;	
        Double totalTradeMoney=0d;
        Double totalRefuld=0d;
		for(InternationalCreateBaihuakuanVo bail2 : createBail){
			InternationalCreateBaihuakuan bail= new InternationalCreateBaihuakuan();
			bail.setBatchno(Long.valueOf(batchno));//
			bail.setCeatetabletime(ceatetabletime);//
			bail.setTradestarttime((Timestamp)o[0]);//
			bail.setTradeendtime((Timestamp)o[1]);//
			bail.setBailexchangerate(bail2.getBailexchangerate());//��֤�����
			bail.setBailmoney(bail2.getBailmoney());//��֤���
			bail.setBalanceexchangerate(bail2.getBalanceexchangerate());//�������
			bail.setBalancemoneykind(bail2.getBalancemoneykind());//�������
			bail.setChannelName(bail2.getChannelName());//ͨ������
			bail.setMoneykindname(bail2.getMoneykindname());//���ױ���
			bail.setPartbailrefund(bail2.getPartbailrefund());//�����˿֤��
			bail.setTradenumber(bail2.getTradenumber());//���ױ���
			bail.setTrademoney(bail2.getTrademoney());//���׽��
			bail.setBalancemoney(bail2.getBalancemoney());//Ӧ������
//			bail.setMerchantId(merchant.getId());
			commonService.save(bail);

			bh.setBalanceexchangerate(bail.getBailexchangerate());
			bh.setMerchantId(merchant.getId());
 			totalMoney=totalMoney+bail2.getBalancemoney();
			totalTradeMoney=totalTradeMoney+bail2.getBailmoney();
			totalRefuld=totalRefuld+bail2.getPartbailrefund();
		}
		bh.setBalancemoney(totalMoney);	//������	
		bh.setBailmoney(totalTradeMoney-totalRefuld);//�ܽ��׽��
		bh.setCeatetabletime(ceatetabletime);		
		bh.setBatchno(Long.valueOf(batchno));		
		bh.setTradestarttime((Timestamp)o[0]);
		bh.setTradeendtime((Timestamp)o[1]);
		UserBean use = (UserBean)ActionContext.getContext().getSession().get("user");
		bh.setDisposeman(use.getUserName());
		commonService.save(bh);
    	}
    }
	public void passSettlement(Long beatchNo) { 
	     InternationalSettlment ist=(InternationalSettlment)this.commonDao.list("from InternationalSettlment t where t.batchno='"+beatchNo+"' ").get(0);
		
		//���»���״̬
		InternationalSettlment li=(InternationalSettlment)	this.commonDao.list("from InternationalSettlment t where t.batchno='"+beatchNo+"'").get(0);
		li.setIstrue("1");
		this.commonDao.saveOrUpdate(li);
		//���½��ױ��е�״̬Ϊ�ѻ���
		
		
		String temid="";
		StringBuffer sbb = new StringBuffer("");	
		List mormal = this.commonService.getByList("select t.id from international_tradeinfo t, international_tradedetails f where t.orderno=f.rorderno and f.batchno='"+beatchNo+"'");
		for(int i=0 ;i<mormal.size();i++){
			BigDecimal tem = (BigDecimal)mormal.get(i);

			    if(i == (mormal.size()-1)){
			    	
			        sbb.append("'" + tem.intValue() + "'");   
			    }//SQLƴװ�����һ�����ӡ�,����
			    else if((i%500)==0 && i>0){
			        sbb.append("'" +  tem.intValue() + "' ) OR t.id IN ( ");    }//���ORA-01795����
			    else{
			        sbb.append("'" + tem.intValue() + "', ");}
				
				
				
		}
		temid = sbb.toString();
		this.commonDao.deleteBySql("update international_tradeinfo t set t.tradestate=substr(t.tradestate,1,7)||'1'||substr(t.tradestate,9,length(t.tradestate)-8) where t.id in("+temid+")");
//ɾ���Ѿ����뻮�����쳣����
		   this.commonDao.deleteBySql(" delete huakuanedexception t where t.isaudit='1' and t.tradeid in (select f.id from international_tradeinfo f ,huakuanedexception g where g.tradeid=f.id and f.merchantid='"+ist.getMerchantno()+"')");     
		
	}

}
