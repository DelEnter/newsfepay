package com.ecpss.action.bail;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ecpss.action.BaseAction;
import com.ecpss.action.pay.SfePayAction;
import com.ecpss.model.channel.InternationalChannels;
import com.ecpss.model.channel.InternationalMerchantChannels;
import com.ecpss.model.payment.InternationalBailhuakuan;
import com.ecpss.model.payment.InternationalCreateBaihuakuan;
import com.ecpss.model.payment.InternationalTradeinfo;
import com.ecpss.model.shop.InternationalMerchant;
import com.ecpss.model.shop.InternationalMoneykindname;
import com.ecpss.service.common.CommonService;
import com.ecpss.service.iservice.PermissionsService;
import com.ecpss.tools.TableExport;
import com.ecpss.tools.TableExportFactory;
import com.ecpss.util.DownloadUtils;
import com.ecpss.web.PageInfo;

public class BailBalanceAction extends BaseAction {
	Logger logger = Logger.getLogger(BailBalanceAction.class.getName());
	
	@Autowired
	@Qualifier("permissionsService")
	private PermissionsService permissionsService;
	@Autowired
	@Qualifier("commonService")
	private CommonService commonService;
	private Double totalMoney = 0d;

	private PageInfo info = new PageInfo();
	private String hql;
	private List list = new ArrayList();;
	private Object[] o;
	private Object[] disposeId;
	private Object[] id;
	private Object[] bank;
	private Object[] huakuanTime;
	private Object[] remark;
	private InternationalMerchant merchant = new InternationalMerchant();
	private InternationalTradeinfo trade = new InternationalTradeinfo();
	private InternationalBailhuakuan bailhua;
	private InternationalCreateBaihuakuan createBailhua;
	private StringBuffer sb = null;
	private List<Long> merchantNoList;
	private List traderesult;
	private String endDate;
	private String typesname = "";
	
	public List<Long> getMerchantNoList() {
		return merchantNoList;
	}

	public void setMerchantNoList(List<Long> merchantNoList) {
		this.merchantNoList = merchantNoList;
	}

	/**
	 * ��ȡδ���֤�������
	 */
	public String findNoHuakuanBail() {

		try {

			// �����̻��Ż�ȡ�̻�ÿ��ͨ���ı�֤���������
			/*
			 * hql = "SELECT chann.channelId, chann.balanceCycle FROM
			 * InternationalMerchantChannels chann," + " InternationalMerchant
			 * mer WHERE chann.merchantId=mer.id AND
			 * mer.merno="+merchant.getMerno()+""; o =
			 * (Object[])commonService.uniqueResult(hql); if(o!=null){
			 * //��֤���������(�ɹ���δ�˿δ�ܸ���δ���ᣬ�ѹ��ң���֤��δ������㱣֤���������) hql = "FROM
			 * InternationalTradeinfo t, InternationalMerchant merchant
			 * ,InternationalMerchantChannels chann " + "WHERE
			 * t.merchantId=merchant.id chann.merchantId=merchant.id" + "AND
			 * merchant.merno="+merchant.getMerno()+" AND
			 * substr(t.tradeState,1,1)='1' " + "AND
			 * substr(t.tradeState,2,1)='0' AND substr(t.tradeState,3,1)='0' " +
			 * "AND substr(t.tradeState,4,1)<>('1') AND
			 * substr(t.tradeState,5,1)='1' " + "AND
			 * substr(t.tradeState,10,1)='0' AND t.tradeChannel=chann.channelId+ " +
			 * "AND to_char(tradetime,'yyyy-mm-dd') < (select
			 * distinct(to_char(sysdate-chann.balanceCycle,'yyyy-mm-dd')) from
			 * InternationalTradeinfo)"; info =
			 * commonService.listQueryResultByHql(hql, info);
			 * info.setPageSize(20); }
			 */
			// ��֤���������(�ɹ���δ�˿δ�ܸ���δ���ᣬ�ѹ��ң���֤��δ������㱣֤���������)
			String hql2 = " select distinct merchant.merno FROM International_Tradeinfo t,International_Merchant merchant,International_MerchantChannels chann  "
					+

					"WHERE t.tradeChannel=chann.id and  merchant.id=t.merchantid "
					+ "AND   substr(t.tradeState,1,1)='1' "
					+ "AND substr(t.tradeState,2,1)in(0,2) AND substr(t.tradeState,3,1)='0' "
					+ "AND substr(t.tradeState,4,1)in(0,2) AND substr(t.tradeState,5,1)='1' "
					+ "AND substr(t.tradeState,8,1)='1'  "
					+ "AND substr(t.tradeState,10,1)='0'  "
					+ "AND to_char(tradetime,'yyyy-mm-dd') <to_char(sysdate-chann.bailCycle,'yyyy-mm-dd')";
					//+ "AND to_char(tradetime,'yyyy-mm-dd') <to_char(sysdate-chann.bailCycle,'yyyy-mm-dd')order by merchant.merno asc";
			this.merchantNoList = this.commonService.getByList(hql2);
			Collections.sort(this.merchantNoList);
			if (merchant.getMerno() != null) {
				String hql = "FROM InternationalTradeinfo t,InternationalMerchant merchant,InternationalMerchantChannels chann  "
						+

						"WHERE t.tradeChannel=chann.id and  merchant.id=t.merchantId "
						+ "AND   substr(t.tradeState,1,1)='1' "
						+ "AND merchant.merno='"
						+ merchant.getMerno()
						+ "'"
						+ "AND substr(t.tradeState,2,1)in(0,2) AND substr(t.tradeState,3,1)='0' "
						+ "AND substr(t.tradeState,4,1)in(0,2) AND substr(t.tradeState,5,1)='1' "
						+ "AND substr(t.tradeState,8,1)='1'  "
						+ "AND substr(t.tradeState,10,1)='0'  "
						+ "AND to_char(tradetime,'yyyy-mm-dd') <to_char(sysdate-chann.bailCycle,'yyyy-mm-dd') ";
				if (!this.endDate.equals("")) {
					hql = hql + " and to_char(tradetime,'yyyy-mm-dd')<='"
							+ this.endDate.trim() + "' ";
				}
				hql = hql + " order by tradetime ";
				traderesult = commonService.list(hql);
			}
			return SUCCESS;
		} catch (Exception e) {
			e.printStackTrace();
			this.messageAction = "��ȡδ���֤�������ʧ��";
			return this.OPERATE_ERROR;
		}
	}

	/**
	 * ��˱�֤�� ����˱�֤�����Ԥ����֤�𻮿��
	 */
	public String bailAuditing() {
		try {
			String temID = "";

			StringBuffer sbb = new StringBuffer();
			for (int i = 0; i < disposeId.length; i++) {

				// if(tems[i]==null) continue;

				// ���ﲻҪ���ͼ������д�ɣ�if(i == custNOs.length)
				if (i == (disposeId.length - 1))
					sbb.append("'" + disposeId[i] + "'"); // SQLƴװ�����һ�����ӡ�,����
				else if ((i % 500) == 0 && i > 0)
					sbb.append("'" + disposeId[i] + "' ) OR trade.id IN ( "); // ���ORA-01795����
				else
					sbb.append("'" + disposeId[i] + "', ");

			}

			temID = sbb.toString();
			logger.info("**************temID**************"+temID.toString());
			// ��ȡ�̻��տ�����Ϣ
			hql = "FROM InternationalMerchant mer WHERE mer.merno="
					+ merchant.getMerno() + "";
			merchant = (InternationalMerchant) commonService.uniqueResult(hql);

			hql = " select sum(trade.tradeAmount),sum(trade.backCount) ,trade.tradeChannel, count(trade.id)  FROM InternationalTradeinfo trade WHERE   trade.id in("
					+ temID + ") group by trade.tradeChannel";
			//hql = " select sum(trade.tradeAmount),sum(trade.backCount) ,trade.tradeChannel,trade.moneyType, count(trade.id)  FROM InternationalTradeinfo trade WHERE   trade.id in("
			//		+ temID + ") group by trade.tradeChannel,trade.moneyType";
			
			List<Object[]> ol = commonService.list(hql);
			logger.info("**************�ϵ�1**************");
			for (Object[] o : ol) {
				//logger.info("��֤��ѭ����"+(Long) o[3]);15
				InternationalCreateBaihuakuanVo cbh = new InternationalCreateBaihuakuanVo();
				//this.initSettlement(cbh, (Long) o[2],(Long) o[3]);
				this.initSettlement(cbh, (Long) o[2]);
				cbh.setTrademoney((Double) o[0]);
				cbh.setPartbailrefund((Double) o[1]);
				cbh.setTradenumber((Long) o[3]);
				this.list.add(cbh);
				this.totalMoney = this.totalMoney + cbh.getBalancemoney();
			}
			logger.info("**************�ϵ�2**************");
			// InternationalCreateBaihuakuan cbh = new
			// InternationalCreateBaihuakuan();
			// Double partRundBail = (Double)o[2]*(Double)o[5];
			// Double bailmoney = (Double)o[1]*(Double)o[5];
			// Double balanceMoney = (bailmoney-partRundBail)*(Double)o[6];
			// //hql = "FROM InternationalChannels";
			// cbh.setTradenumber((Long)o[0]);
			// cbh.setTrademoney((Double)o[1]);
			// cbh.setPartbailrefund(partRundBail);
			// cbh.setBailmoney(bailmoney);
			// cbh.setChannelName((String)o[3]);
			// cbh.setMoneykindname((String)o[4]);
			// cbh.setChannelsId((Long)o[8]);
			// cbh.setTrademoneykind((Long)o[9]);
			//		
			// cbh.setBailexchangerate((Double)o[5]);
			// cbh.setBalanceexchangerate((Double)o[6]);
			// cbh.setBalancemoney(balanceMoney);
			// System.out.println("(Long)o[9]------------"+(Long)o[9]);
			// cbh.setExchTime((Timestamp)o[7]);
			// cbh.setExchTime(()o[6]);
			hql = "SELECT min(trade.tradeTime), max(trade.tradeTime)FROM InternationalTradeinfo trade WHERE trade.id in("
					+ temID + ")";
			o = (Object[]) commonService.uniqueResult(hql);
			logger.info("**************�ϵ�3**************");
			HttpServletRequest request = ServletActionContext.getRequest();
			HttpSession session = request.getSession();
			session.setAttribute("list", list);
			session.setAttribute("disposeId", temID);
			session.setAttribute("merchant", merchant);
			this.messageAction = "��֤����˳ɹ�!";
			return SUCCESS;
		} catch (Exception e) {
			e.printStackTrace();
			this.messageAction = "��֤�����ʧ��!";
			return this.OPERATE_SUCCESS;
		}
	}

	/**
	 * ���ɱ�֤�𻮿��
	 */
	public String createBail() {
		try {
			HttpServletRequest request = ServletActionContext.getRequest();
			HttpSession session = request.getSession();
			StringBuffer sb1 = new StringBuffer();
			sb = new StringBuffer();
			String tem = (String) session.getAttribute("disposeId");
			sb1.append(tem);
			list = (List) session.getAttribute("list");
			for (int i = 0; i < list.size(); i++) {
				this.totalMoney = this.totalMoney
						+ ((InternationalCreateBaihuakuanVo) list.get(i))
								.getBalancemoney();
			}
			merchant = (InternationalMerchant) session.getAttribute("merchant");
			this.permissionsService.createBailMoney(sb1, list, merchant);

			// ���汣֤�𻮿��
			this.messageAction = "���ɱ�֤��ɹ�";
			return "success";
		} catch (Exception e) {
			e.printStackTrace();
			this.messageAction = "���ɱ�֤�𻮿��ʧ��";
			return this.OPERATE_ERROR;
		}
	}

	/**
	 * �鿴Ԥ�����֤��
	 */
	public String findHuakuanBailByMerchant() {
		try {
			if (bailhua == null) {
				bailhua = new InternationalBailhuakuan();
			}
			if (merchant == null) {
				merchant = new InternationalMerchant();
			}
			// ��ȡ�̻���Ϣ
			hql = "FROM InternationalMerchant mer WHERE mer.id='"
					+ this.getMerchantBean().getMerchantId() + "'";
			merchant = (InternationalMerchant) commonService.uniqueResult(hql);
			this.bailhua = (InternationalBailhuakuan) this.commonService
					.uniqueResult("from InternationalBailhuakuan t where t.batchno='"
							+ bailhua.getBatchno() + "'");
			hql = "FROM InternationalCreateBaihuakuan createbail  "
					+ "WHERE createbail.batchno='" + bailhua.getBatchno() + "'";
			list = commonService.list(hql);
			for (int i = 0; i < list.size(); i++) {
				this.totalMoney = this.totalMoney
						+ ((InternationalCreateBaihuakuan) list.get(i))
								.getBalancemoney();
			}

			return SUCCESS;
		} catch (Exception e) {
			e.printStackTrace();
			this.messageAction = "�鿴Ԥ����֤��ʧ��!";
			return this.OPERATE_ERROR;
		}
	}

	/**
	 * ��ϸ��֤�𻮿��
	 */
	public String findListBailbyMerchant() {
		try {
			// hql = "FROM InternationalTradeinfo trade, InternationalMerchant
			// mer, InternationalMoneykindname money, InternationalChannels
			// chann " +
			// "WHERE trade.batchNo="+bailhua.getBatchno()+" AND
			// trade.merchantId=mer.id AND trade.moneyType=money.moneykindno " +
			// "AND chann.id=trade.tradeChannel";
			hql = "FROM InternationalTradeinfo trade, InternationalMerchant mer,  InternationalMoneykindname money, InternationalChannels chann,InternationalMerchantChannels imc WHERE trade.batchNo='"
					+ bailhua.getBatchno()
					+ "' AND trade.merchantId=mer.id AND trade.tradeChannel=imc.id and imc.channelId=chann.id and trade.moneyType=money.moneykindno";
			list = commonService.list(hql);
			return SUCCESS;
		} catch (Exception e) {
			e.printStackTrace();
			this.messageAction = "��ϸ��֤�𻮿��";
			return this.OPERATE_ERROR;
		}
	}

	/**
	 * ��֤�𻮿��ѯ
	 */
	public String findNoHuakuanBailByMerchant() {
		try {
			/*
			 * if(merchant==null){ merchant = new InternationalMerchant(); }
			 * if(bailhua==null){ bailhua = new InternationalBailhuakuan(); } sb =
			 * new StringBuffer("FROM InternationalBailhuakuan bailhua,
			 * InternationalMerchant mer " + "WHERE bailhua.merchantId=mer.id");
			 * 
			 * if(merchant.getMerno()!=null){ sb.append(" AND
			 * mer.merno="+merchant.getMerno()+""); }
			 * if(bailhua.getBatchno()!=null){ sb.append(" AND
			 * bailhua.batchno="+bailhua.getBatchno()+""); } hql =
			 * sb.toString(); info = commonService.listQueryResultByHql(hql,
			 * info); huakuanTime = null; setHuakuanTime(null);
			 */

			if (bailhua == null) {
				bailhua = new InternationalBailhuakuan();
			}
			sb = new StringBuffer(
					" select bailhua FROM InternationalBailhuakuan bailhua "
							+ "WHERE bailhua.huakuantime <>null and  bailhua.merchantId='"
							+ this.getMerchantBean().getMerchantId() + "'");

			if (bailhua.getBatchno() != null) {
				sb.append(" AND bailhua.batchno=" + bailhua.getBatchno() + "");
			}
			sb.append(" order by bailhua.huakuantime desc ");
			hql = sb.toString();
			info = commonService.listQueryResultByHql(hql, info);
			huakuanTime = null;
			setHuakuanTime(null);
			return SUCCESS;
		} catch (Exception e) {
			e.printStackTrace();
			this.messageAction = "��֤�𻮿��ѯʧ��!";
			return this.OPERATE_ERROR;
		}
	}

	/**
	 * �鿴Ԥ�����֤��
	 */
	public String findPreviewBail() {
		try {
			if (bailhua == null) {
				bailhua = new InternationalBailhuakuan();
			}
			if (merchant == null) {
				merchant = new InternationalMerchant();
			}
			// ��ȡ�̻���Ϣ
			hql = "FROM InternationalMerchant mer WHERE mer.merno="
					+ merchant.getMerno() + "";
			merchant = (InternationalMerchant) commonService.uniqueResult(hql);
			this.bailhua = (InternationalBailhuakuan) this.commonService
					.uniqueResult("from InternationalBailhuakuan t where t.batchno='"
							+ bailhua.getBatchno() + "'");
			hql = "FROM InternationalCreateBaihuakuan createbail  "
					+ "WHERE createbail.batchno='" + bailhua.getBatchno() + "'";
			list = commonService.list(hql);
			for (int i = 0; i < list.size(); i++) {
				this.totalMoney = this.totalMoney
						+ ((InternationalCreateBaihuakuan) list.get(i))
								.getBalancemoney();
			}

			return SUCCESS;
		} catch (Exception e) {
			e.printStackTrace();
			this.messageAction = "�鿴Ԥ����֤��ʧ��!";
			return this.OPERATE_ERROR;
		}
	}

	/**
	 * ��ϸ��֤�𻮿��
	 */
	public String findListBail() {
		try {
			// hql = "FROM InternationalTradeinfo trade, InternationalMerchant
			// mer, InternationalMoneykindname money, InternationalChannels
			// chann " +
			// "WHERE trade.batchNo="+bailhua.getBatchno()+" AND
			// trade.merchantId=mer.id AND trade.moneyType=money.moneykindno " +
			// "AND chann.id=trade.tradeChannel";
			hql = "FROM InternationalTradeinfo trade, InternationalMerchant mer,  InternationalMoneykindname money, InternationalChannels chann,InternationalMerchantChannels imc WHERE trade.batchNo='"
					+ bailhua.getBatchno()
					+ "' AND trade.merchantId=mer.id AND trade.tradeChannel=imc.id and imc.channelId=chann.id and trade.moneyType=money.moneykindno";
			list = commonService.list(hql);
			return SUCCESS;
		} catch (Exception e) {
			e.printStackTrace();
			this.messageAction = "��ϸ��֤�𻮿��";
			return this.OPERATE_ERROR;
		}
	}

	/**
	 * ��֤�𻮿��ѯ
	 */
	public String findHuakuanBail() {
		try {
			if (merchant == null) {
				merchant = new InternationalMerchant();
			}
			if (bailhua == null) {
				bailhua = new InternationalBailhuakuan();
			}
			sb = new StringBuffer(
					"FROM InternationalBailhuakuan bailhua, InternationalMerchant mer "
							+ "WHERE bailhua.merchantId=mer.id");

			if (merchant.getMerno() != null) {
				sb.append(" AND mer.merno=" + merchant.getMerno() + "");
			}
			if (merchant.getAccountname()!=null) {
				if(!merchant.getAccountname().equals("")){
				sb.append(" AND mer.accountname ='" + merchant.getAccountname().trim() + "'");
				}               
			}			
			if (bailhua.getBatchno() != null) {
				sb.append(" AND bailhua.batchno=" + bailhua.getBatchno() + "");
			}
			
			if(this.typesname.equals("true")){
				sb.append(" and bailhua.huakuantime is null ");
				//sb.append(" order by t.createtabletime desc");
//				this.info.setPageSize(50);
//				this.info = this.commonService
//				.listQueryResultByHql(sb.toString(), info);
			}
			
			sb.append(" order by bailhua.ceatetabletime desc");
			hql = sb.toString();
			if (StringUtils.isNotBlank(isdownload)) {
				List<Object[]> objList = commonService.list(sb.toString());
				this.downloadTradeQuery(objList);
				return null;
			} else {
				info = commonService.listQueryResultByHql(hql, info);
				huakuanTime = null;
				setHuakuanTime(null);
			}
			return SUCCESS;
		} catch (Exception e) {
			e.printStackTrace();
			this.messageAction = "��֤�𻮿��ѯʧ��!";
			return this.OPERATE_ERROR;
		}
	}

	private String isdownload;

	/**
	 * �����̻����ײ�ѯ��¼
	 * 
	 * @return
	 */
	public void downloadTradeQuery(List<Object[]> oArray) {
		TableExport export = TableExportFactory.createExcelTableExport();
		export.addTitle(new String[] { "�̻���", "���κ�", "��֤���", "���������", "������",
				"�Ʊ�ʱ��", "��ʼ����ʱ��", "��������ʱ��", "��������", "������" });
		export.setTableName("��֤��");
		InternationalBailhuakuan t = new InternationalBailhuakuan();
		InternationalMerchant ci = new InternationalMerchant();
		int i = 1;
		for (Object[] obj : oArray) {
			t = (InternationalBailhuakuan) obj[0];
			ci = (InternationalMerchant) obj[1];
			export
					.addRow(new Object[] { ci.getMerno(), t.getBatchno(),
							t.getBailmoney(), t.getBalancemoney(),
							ci.getAccountname(), t.getCeatetabletime(),
							t.getTradestarttime(), t.getTradeendtime(),
							t.getHuakuantime(), t.getHuakuanbank() });
			i++;
		}
		OutputStream os = DownloadUtils.getResponseOutput("baozhengjin.xls");
		export.export(os);
		DownloadUtils.closeResponseOutput();
	}
	
	/**
	 * ��֤�𻮿�
	 */
	public String bailHuakuan() {
		try {
			Date d = new Date();
			for (int i = 0; i < disposeId.length; i++) {
				InternationalBailhuakuan huakuan = (InternationalBailhuakuan) commonService
						.load(InternationalBailhuakuan.class, Long
								.valueOf((String) disposeId[i]));
					huakuan.setHuakuantime(d);
					
				if(!remark[i].equals("")){
					huakuan.setRemark(remark[i].toString());
				} 
				commonService.update(huakuan);
			}
			this.messageAction = "��֤�𻮿�ɹ�!";
			this.findHuakuanBail();
			return SUCCESS;
		} catch (Exception e) {
			e.printStackTrace();
			this.messageAction = "��֤�𻮿�ʧ��!";
			return this.OPERATE_SUCCESS;
		}
	}

	/**
	 * @return the commonService
	 */
	public CommonService getCommonService() {
		return commonService;
	}

	/**
	 * @param commonService
	 *            the commonService to set
	 */
	public void setCommonService(CommonService commonService) {
		this.commonService = commonService;
	}

	/**
	 * @return the info
	 */
	public PageInfo getInfo() {
		return info;
	}

	/**
	 * @param info
	 *            the info to set
	 */
	public void setInfo(PageInfo info) {
		this.info = info;
	}

	/**
	 * @return the hql
	 */
	public String getHql() {
		return hql;
	}

	/**
	 * @param hql
	 *            the hql to set
	 */
	public void setHql(String hql) {
		this.hql = hql;
	}

	/**
	 * @return the merchant
	 */
	public InternationalMerchant getMerchant() {
		return merchant;
	}

	/**
	 * @param merchant
	 *            the merchant to set
	 */
	public void setMerchant(InternationalMerchant merchant) {
		this.merchant = merchant;
	}

	/**
	 * @return the bailhua
	 */
	public InternationalBailhuakuan getBailhua() {
		return bailhua;
	}

	/**
	 * @param bailhua
	 *            the bailhua to set
	 */
	public void setBailhua(InternationalBailhuakuan bailhua) {
		this.bailhua = bailhua;
	}

	/**
	 * @return the list
	 */
	public List getList() {
		return list;
	}

	/**
	 * @param list
	 *            the list to set
	 */
	public void setList(List list) {
		this.list = list;
	}

	/**
	 * @return the createBailhua
	 */
	public InternationalCreateBaihuakuan getCreateBailhua() {
		return createBailhua;
	}

	/**
	 * @param createBailhua
	 *            the createBailhua to set
	 */
	public void setCreateBailhua(InternationalCreateBaihuakuan createBailhua) {
		this.createBailhua = createBailhua;
	}

	/**
	 * @return the id
	 */
	public Object[] getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(Object[] id) {
		this.id = id;
	}

	/**
	 * @return the disposeId
	 */
	public Object[] getDisposeId() {
		return disposeId;
	}

	/**
	 * @param disposeId
	 *            the disposeId to set
	 */
	public void setDisposeId(Object[] disposeId) {
		this.disposeId = disposeId;
	}

	/**
	 * @return the o
	 */
	public Object[] getO() {
		return o;
	}

	/**
	 * @param o
	 *            the o to set
	 */
	public void setO(Object[] o) {
		this.o = o;
	}

	/**
	 * @return the bank
	 */
	public Object[] getBank() {
		return bank;
	}

	/**
	 * @param bank
	 *            the bank to set
	 */
	public void setBank(Object[] bank) {
		this.bank = bank;
	}

	/**
	 * @return the huakuanTime
	 */
	public Object[] getHuakuanTime() {
		return huakuanTime;
	}

	/**
	 * @param huakuanTime
	 *            the huakuanTime to set
	 */
	public void setHuakuanTime(Object[] huakuanTime) {
		this.huakuanTime = huakuanTime;
	}

	public List getTraderesult() {
		return traderesult;
	}

	public void setTraderesult(List traderesult) {
		this.traderesult = traderesult;
	}

	private void initSettlement(InternationalCreateBaihuakuanVo cbh,
			Long merchantchanelid) {
/*	private void initSettlement(InternationalCreateBaihuakuanVo cbh,
			Long merchantchanelid,Long monType) {*/
		// �̻�ͨ��
		logger.info("����1");
		cbh.setBalancemoneykind("�����");
		InternationalMerchantChannels imc = (InternationalMerchantChannels) this.commonService
				.load(InternationalMerchantChannels.class, merchantchanelid);
		cbh.setBailexchangerate(imc.getBailCharge());
		// ͨ��
		InternationalChannels ic = (InternationalChannels) this.commonService
				.load(InternationalChannels.class, imc.getChannelId());
		cbh.setChannelName(ic.getChannelName());

		InternationalMoneykindname ct = (InternationalMoneykindname) this.commonService
				.uniqueResult(" select imk from InternationalMoneykindname imk, InternationalMerchantCurrency imc where imk.id=imc.moneyKindNo and imc.merchanId='"
						+ imc.getMerchantId() + "'");
		cbh.setMoneykindname(ct.getMoneykindname());
		// ��ȡ�������
		List changerates = this.commonService
				.getByList("select t.id,t.rate,t.type from international_exchangerate t,international_moneykindname m  where t.moneykindno=m.id "
						+ "and t.moneykindno='"
						+ ct.getId()
						+ "'  and t.executetime<sysdate-1   and t.type='2' order by t.executetime desc  "); // �������
		// List changerates=this.commonService.getByList("select
		// t.id,t.rate,t.type from international_exchangerate t where
		// t.executetime in (select max(f.executetime) from
		// international_exchangerate f where f.moneykindno='"+ct.getId()+"' and
		// f.type='2')");
		Double nowBalanceRate = 0d;
		for (int i = 0; i < changerates.size(); i++) {
			Object[] tem = (Object[]) changerates.get(i);
			nowBalanceRate = Double.valueOf(tem[1].toString());
		}
		cbh.setBalanceexchangerate(nowBalanceRate);
		
	}

	public Double getTotalMoney() {
		return totalMoney;
	}

	public void setTotalMoney(Double totalMoney) {
		this.totalMoney = totalMoney;
	}

	public String getIsdownload() {
		return isdownload;
	}

	public void setIsdownload(String isdownload) {
		this.isdownload = isdownload;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public Object[] getRemark() {
		return remark;
	}

	public void setRemark(Object[] remark) {
		this.remark = remark;
	}

	public String getTypesname() {
		return typesname;
	}

	public void setTypesname(String typesname) {
		this.typesname = typesname;
	}

}
