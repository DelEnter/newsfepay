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
	 * 获取未划款保证金的数据
	 */
	public String findNoHuakuanBail() {

		try {

			// 根据商户号获取商户每个通道的保证金结算周期
			/*
			 * hql = "SELECT chann.channelId, chann.balanceCycle FROM
			 * InternationalMerchantChannels chann," + " InternationalMerchant
			 * mer WHERE chann.merchantId=mer.id AND
			 * mer.merno="+merchant.getMerno()+""; o =
			 * (Object[])commonService.uniqueResult(hql); if(o!=null){
			 * //保证金结算条件(成功，未退款，未拒付，未冻结，已勾兑，保证金未划款，满足保证金结算周期) hql = "FROM
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
			// 保证金结算条件(成功，未退款，未拒付，未冻结，已勾兑，保证金未划款，满足保证金结算周期)
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
			this.messageAction = "获取未划款保证金的数据失败";
			return this.OPERATE_ERROR;
		}
	}

	/**
	 * 审核保证金 （审核保证金就是预览保证金划款表）
	 */
	public String bailAuditing() {
		try {
			String temID = "";

			StringBuffer sbb = new StringBuffer();
			for (int i = 0; i < disposeId.length; i++) {

				// if(tems[i]==null) continue;

				// 这里不要犯低级错误而写成：if(i == custNOs.length)
				if (i == (disposeId.length - 1))
					sbb.append("'" + disposeId[i] + "'"); // SQL拼装，最后一条不加“,”。
				else if ((i % 500) == 0 && i > 0)
					sbb.append("'" + disposeId[i] + "' ) OR trade.id IN ( "); // 解决ORA-01795问题
				else
					sbb.append("'" + disposeId[i] + "', ");

			}

			temID = sbb.toString();
			logger.info("**************temID**************"+temID.toString());
			// 获取商户收款人信息
			hql = "FROM InternationalMerchant mer WHERE mer.merno="
					+ merchant.getMerno() + "";
			merchant = (InternationalMerchant) commonService.uniqueResult(hql);

			hql = " select sum(trade.tradeAmount),sum(trade.backCount) ,trade.tradeChannel, count(trade.id)  FROM InternationalTradeinfo trade WHERE   trade.id in("
					+ temID + ") group by trade.tradeChannel";
			//hql = " select sum(trade.tradeAmount),sum(trade.backCount) ,trade.tradeChannel,trade.moneyType, count(trade.id)  FROM InternationalTradeinfo trade WHERE   trade.id in("
			//		+ temID + ") group by trade.tradeChannel,trade.moneyType";
			
			List<Object[]> ol = commonService.list(hql);
			logger.info("**************断点1**************");
			for (Object[] o : ol) {
				//logger.info("保证金循环："+(Long) o[3]);15
				InternationalCreateBaihuakuanVo cbh = new InternationalCreateBaihuakuanVo();
				//this.initSettlement(cbh, (Long) o[2],(Long) o[3]);
				this.initSettlement(cbh, (Long) o[2]);
				cbh.setTrademoney((Double) o[0]);
				cbh.setPartbailrefund((Double) o[1]);
				cbh.setTradenumber((Long) o[3]);
				this.list.add(cbh);
				this.totalMoney = this.totalMoney + cbh.getBalancemoney();
			}
			logger.info("**************断点2**************");
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
			logger.info("**************断点3**************");
			HttpServletRequest request = ServletActionContext.getRequest();
			HttpSession session = request.getSession();
			session.setAttribute("list", list);
			session.setAttribute("disposeId", temID);
			session.setAttribute("merchant", merchant);
			this.messageAction = "保证金审核成功!";
			return SUCCESS;
		} catch (Exception e) {
			e.printStackTrace();
			this.messageAction = "保证金审核失败!";
			return this.OPERATE_SUCCESS;
		}
	}

	/**
	 * 生成保证金划款表
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

			// 保存保证金划款表
			this.messageAction = "生成保证金成功";
			return "success";
		} catch (Exception e) {
			e.printStackTrace();
			this.messageAction = "生成保证金划款表失败";
			return this.OPERATE_ERROR;
		}
	}

	/**
	 * 查看预览划款保证金
	 */
	public String findHuakuanBailByMerchant() {
		try {
			if (bailhua == null) {
				bailhua = new InternationalBailhuakuan();
			}
			if (merchant == null) {
				merchant = new InternationalMerchant();
			}
			// 获取商户信息
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
			this.messageAction = "查看预览保证金失败!";
			return this.OPERATE_ERROR;
		}
	}

	/**
	 * 明细保证金划款表
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
			this.messageAction = "明细保证金划款表";
			return this.OPERATE_ERROR;
		}
	}

	/**
	 * 保证金划款查询
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
			this.messageAction = "保证金划款查询失败!";
			return this.OPERATE_ERROR;
		}
	}

	/**
	 * 查看预览划款保证金
	 */
	public String findPreviewBail() {
		try {
			if (bailhua == null) {
				bailhua = new InternationalBailhuakuan();
			}
			if (merchant == null) {
				merchant = new InternationalMerchant();
			}
			// 获取商户信息
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
			this.messageAction = "查看预览保证金失败!";
			return this.OPERATE_ERROR;
		}
	}

	/**
	 * 明细保证金划款表
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
			this.messageAction = "明细保证金划款表";
			return this.OPERATE_ERROR;
		}
	}

	/**
	 * 保证金划款查询
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
			this.messageAction = "保证金划款查询失败!";
			return this.OPERATE_ERROR;
		}
	}

	private String isdownload;

	/**
	 * 下载商户交易查询记录
	 * 
	 * @return
	 */
	public void downloadTradeQuery(List<Object[]> oArray) {
		TableExport export = TableExportFactory.createExcelTableExport();
		export.addTitle(new String[] { "商户号", "批次号", "保证金额", "结算人民币", "开户人",
				"制表时间", "开始交易时间", "结束交易时间", "划款日期", "划款行" });
		export.setTableName("保证金");
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
	 * 保证金划款
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
			this.messageAction = "保证金划款成功!";
			this.findHuakuanBail();
			return SUCCESS;
		} catch (Exception e) {
			e.printStackTrace();
			this.messageAction = "保证金划款失败!";
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
		// 商户通道
		logger.info("测试1");
		cbh.setBalancemoneykind("人民币");
		InternationalMerchantChannels imc = (InternationalMerchantChannels) this.commonService
				.load(InternationalMerchantChannels.class, merchantchanelid);
		cbh.setBailexchangerate(imc.getBailCharge());
		// 通道
		InternationalChannels ic = (InternationalChannels) this.commonService
				.load(InternationalChannels.class, imc.getChannelId());
		cbh.setChannelName(ic.getChannelName());

		InternationalMoneykindname ct = (InternationalMoneykindname) this.commonService
				.uniqueResult(" select imk from InternationalMoneykindname imk, InternationalMerchantCurrency imc where imk.id=imc.moneyKindNo and imc.merchanId='"
						+ imc.getMerchantId() + "'");
		cbh.setMoneykindname(ct.getMoneykindname());
		// 获取结算汇率
		List changerates = this.commonService
				.getByList("select t.id,t.rate,t.type from international_exchangerate t,international_moneykindname m  where t.moneykindno=m.id "
						+ "and t.moneykindno='"
						+ ct.getId()
						+ "'  and t.executetime<sysdate-1   and t.type='2' order by t.executetime desc  "); // 结算汇率
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
