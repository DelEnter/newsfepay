package com.ecpss.action.websitemanager;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ecpss.action.BaseAction;
import com.ecpss.model.shop.InternationalIsAuditWeb;
import com.ecpss.model.shop.InternationalMerchant;
import com.ecpss.model.shop.InternationalWebchannels;
import com.ecpss.service.common.CommonService;
import com.ecpss.web.PageInfo;

import oracle.net.aso.l;

/**
 * 商户网址管理
 * 
 * @author huhongguang
 * 
 * 
 * 
 */
public class MerchantWebSiteManagerAction extends BaseAction {

	private String hql;
	private PageInfo info = new PageInfo();//分页
	private InternationalWebchannels website = new InternationalWebchannels(); // 网址实体对象

	private InternationalMerchant merchant = new InternationalMerchant(); // 商户实体
	/** 商户网址实体对象 */
	private List<InternationalWebchannels> merchantWebSiteList;

	/** 商户实体对象集合 */
	private List<InternationalMerchant> internationalMerchantList;
	private InternationalIsAuditWeb isAuditWeb=new InternationalIsAuditWeb();
	private String merNo;//商户号
	private String auditStatus;//审核状态
	private String ids;
	private String remark;
	private String webId;
	private List webSiteTypeList;
	private String webSiteType;
	private String webtype;//网站类型
	private List webTypeList;
	

	/**
	 * 去网址管理主界面
	 * 
	 * @return
	 */
	public String toWebSiteManager() {
		try {
			return SUCCESS;
		} catch (Exception e) {
			e.printStackTrace();
			this.messageAction = "跳转失败";
			return this.OPERATE_SUCCESS;
		}
	}

	/**
	 * 网址管理主界面
	 * 
	 * @return
	 */
	public String webSiteManager() {//查询语句根据搜索的条件去查询
		StringBuffer hql = new StringBuffer();
			hql.append("select iw,im from InternationalWebchannels iw,InternationalMerchant im where iw.merchanid=im.id");//查询所有网址语句
		if (merchant.getMerno() != null && !"".equals(merchant.getMerno())) {//是否有商户号
			// hql.delete(0, hql.length());
			hql.append(" and im.merno= '" + merchant.getMerno() + "' ");//拼接
		}
		if ((website.getWebsite() != null)//是否有审核状态
				&& (!website.getWebsite().equals(""))) {
			// hql.delete(0, hql.length());
			hql.append(" and iw.website like '%" + website.getWebsite() + "%'");//拼接
		}
		if ((website.getTradeWebsite() != null)//是否有网站的类型
				&& (!website.getTradeWebsite().equals(""))) {
			// hql.delete(0, hql.length());
			hql.append(" and iw.tradeWebsite like '%"
					+ website.getTradeWebsite() + "%'");//拼接
		}
		info.setPageSize(2);
		info = commonService.listQueryResultByHql(hql.toString(), info);
		return SUCCESS;
	}

	/**
	 * 去修改网址管理主界面
	 * 
	 * @return
	 */
	public String toUpdateWebSiteManager() {
		website = (InternationalWebchannels) this.commonService.load(
				InternationalWebchannels.class, website.getId());
		Long mer = (Long) commonService
				.uniqueResult("select im.merno from InternationalMerchant im, InternationalWebchannels iw where iw.merchanid=im.id and iw.merchanid= '"
						+ website.getMerchanid() + "' ");
		merchant.setMerno(mer);
		return SUCCESS;
	}

	/**
	 * 修改网址管理主界面
	 * 
	 * @return
	 */
	public String updateWebSiteManager() {
		InternationalWebchannels iw = (InternationalWebchannels) this.commonService
				.load(InternationalWebchannels.class, website.getId());
		iw.setWebsite(website.getWebsite());
		iw.setTradeWebsite(website.getTradeWebsite());
		iw.setExecutetime(new Date());
		iw.setOperator(getUserBean().getUserName());
		// iw.setRemark(website.getRemark());
		this.commonService.update(iw);
		this.messageAction = "修改成功";
		getLoaction().setReload(true);
		return this.OPERATE_SUCCESS;
	}

	// 删除网址管理
	public String deleteWebSiteManager() {
		website = (InternationalWebchannels) this.commonService.load(
				InternationalWebchannels.class, website.getId());
		this.commonService.delete(website);
		this.messageAction = "删除网址成功";
		return this.OPERATE_SUCCESS;

	}

	/**
	 * 跳转到添加网址管理页面
	 * 
	 * @return
	 */
	public String toAddWebSiteManager() {
		if (merchant.getMerno() != null && !"".equals(merchant.getMerno())) {
			merchant = (InternationalMerchant) commonService
					.uniqueResult("select im from InternationalMerchant im where im.merno = '"
							+ merchant.getMerno() + "' ");
			this.getLoaction().setReload(true);
		}
		return SUCCESS;
	}

	/**
	 * 新增网址管理
	 * 
	 * @return
	 */
	public String addWebSite() {
		if (merchant.getMerno() == null || "".equals(merchant.getMerno())) {
			messageAction = "请输入商户号";
			return OPERATE_SUCCESS;
		}
		// 根据商户号查询商户表
		merchant = (InternationalMerchant) this.commonService
				.uniqueResult("from InternationalMerchant im where im.merno = '"
						+ merchant.getMerno() + "'  ");

		if (merchant == null) {
			messageAction = "商户号不存在";
			return OPERATE_SUCCESS;
		}
		// 获取商户实体对象
		website.setMerchanid(merchant.getId());
		// 保存网址管理
		website.setExecutetime(new Date());
		website.setOperator(getUserBean().getUserName());
		// website.setRemark(website.getRemark());
		this.commonService.save(website);
		messageAction = "添加成功";
		getLoaction().setReload(true);
		return OPERATE_SUCCESS;
	}
	/*
	 * 去商户网址审核(jiahui)
	 */
	public String toAuditWebSiteManager(){
		StringBuffer hql=new StringBuffer("select t.id,i.merno,t.tradeWebsite,t.website,t.createtime,t.isAudit,t.operator,t.remark from InternationalMerchant i,InternationalIsAuditWeb t where i.id=t.merchanid ");
		webTypeList = getWebSiteTypesList();
		
		if(StringUtils.isNotBlank(merNo)){
			hql.append(" and i.merno='"+merNo+"'");
		}
		if(StringUtils.isNotBlank(auditStatus)){
			hql.append(" and t.isAudit='"+auditStatus+"'");
		}
		if(StringUtils.isNotBlank(webtype)){
			if(StringUtils.isNotBlank(webtype.substring(9))){
				hql.append(" and t.remark='"+webtype.substring(9)+"'");			
			}		
		}
		hql.append(" order by t.isAudit asc,t.createtime desc");
		info = commonService.listQueryResultByHql(hql.toString(), info);
		return SUCCESS;
	}
	/*
	 * 去网址分类(jiahui)
	 */
	public String toWebSiteType(){
		webSiteTypeList=getWebSiteTypesList();
		return SUCCESS;
	}
	/*
	 * 商户网址审核(jiahui)
	 */
	public String auditWebSiteManager(){
		if(StringUtils.isNotBlank(ids)){
			try {
			String[] id=ids.split(",");
			for(int i=0;i<id.length;i++){//根据ID从这个表1中查出这条记录
				InternationalIsAuditWeb isAudit=(InternationalIsAuditWeb)commonService.uniqueResult("from InternationalIsAuditWeb where id='"+id[i]+"'");
				InternationalWebchannels webChannels=new InternationalWebchannels();//创建表2对象 给表2 对象赋值
				webChannels.setExecutetime(new Date());// ：执行时间
				webChannels.setMerchanid(isAudit.getMerchanid());//：商户ID
				webChannels.setOperator(this.getUserBean().getUserName());//
				webChannels.setTradeWebsite(isAudit.getTradeWebsite());//：交易网址
				webChannels.setWebsite(isAudit.getWebsite());//：返回网址
				webChannels.setIsblack("0");//isblack为：0
				webChannels.setWebSiteType(new String(webSiteType.trim().getBytes("iso-8859-1"),"utf-8"));//：返回网址类型
				this.commonService.save(webChannels);//调用save方法（针对表二）
				isAudit.setIsAudit("1");//给表1 对象赋值
				isAudit.setOperator(this.getUserBean().getUserName());//操作员
				isAudit.setWebcannelId(webChannels.getId());//webcannelId赋值
				isAudit.setRemark(new String(webSiteType.trim().getBytes("iso-8859-1"),"utf-8"));//网址类别
				this.commonService.update(isAudit);//调用update方法（针对表二）
			}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		this.messageAction = "添加网址分类成功";
		return OPERATE_SUCCESS;
	}
	/*
	 * 商户网址审核(jiahui)针对全选(商户号、网址类型、审核状态 )
	 */
	public String auditWebSiteManager1(){
		//查询出商户号所对应的ID号；
		if(merNo!=null){
			try{
				InternationalMerchant mer=(InternationalMerchant) commonService.uniqueResult("from InternationalMerchant where merno='"+merNo+"'");
				if(mer!=null){
					String hql="from InternationalIsAuditWeb where merchanid='"+mer.getId()+"'"+" and isaudit='"+auditStatus+"'";
					List<InternationalIsAuditWeb> list=commonService.list(hql);//根据商户的ID查询出所有的信息记录
					for(int i=0;i<list.size();i++){
						InternationalIsAuditWeb isAudit = list.get(i);
						InternationalWebchannels webChannels=new InternationalWebchannels();//创建表2对象 给表2 对象赋值
						webChannels.setExecutetime(new Date());// ：执行时间
						webChannels.setMerchanid(isAudit.getMerchanid());//：商户ID
						webChannels.setOperator(this.getUserBean().getUserName());//
						webChannels.setTradeWebsite(isAudit.getTradeWebsite());//：交易网址
						webChannels.setWebsite(isAudit.getWebsite());//：返回网址
						webChannels.setIsblack("0");//isblack为：0
						webChannels.setWebSiteType(new String(webSiteType.trim().getBytes("iso-8859-1"),"utf-8"));//：返回网址类型
						this.commonService.save(webChannels);//调用save方法（针对表二）
						isAudit.setIsAudit("1");//给表1 对象赋值
						isAudit.setOperator(this.getUserBean().getUserName());//操作员
						isAudit.setWebcannelId(webChannels.getId());//webcannelId赋值
						isAudit.setRemark(new String(webSiteType.trim().getBytes("iso-8859-1"),"utf-8"));//网址类别
						this.commonService.update(isAudit);//调用update方法（针对表二）
					}
				}
			}catch(Exception e){
				e.printStackTrace();
			}	
		}
		this.messageAction = "添加网址分类成功";
		return OPERATE_SUCCESS;
	}
	//拒绝
	public String notAuditWebSiteManager(){
		try {
		if(StringUtils.isNotBlank(ids)){//判断ids是不是null
			String[] id=ids.split(",");
			for(int i=0;i<id.length;i++){//循环ids
				InternationalIsAuditWeb isAudit=(InternationalIsAuditWeb)commonService.uniqueResult("from InternationalIsAuditWeb where id='"+id[i]+"'");
				isAudit.setIsAudit("2");
				isAudit.setOperator(this.getUserBean().getUserName());
				isAudit.setRemark(new String(remark.getBytes("iso-8859-1"),"utf-8").trim());
				this.commonService.update(isAudit);
			}
		}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		toAuditWebSiteManager();
		return SUCCESS;
	}
	//拒绝针对全选
	public String notAuditWebSiteManager1(){
		
		try {
		if(merNo!=null){
			InternationalMerchant mer=(InternationalMerchant) commonService.uniqueResult("from InternationalMerchant where merno='"+merNo+"'");
			if(mer!=null){
				String hql="from InternationalIsAuditWeb where merchanid='"+mer.getId()+"'"+" and isaudit='"+auditStatus+"'";
				List<InternationalIsAuditWeb> list=commonService.list(hql);//根据商户的ID查询出所有的信息记录
				for(int i=0;i<list.size();i++){
					InternationalIsAuditWeb isAudit=list.get(i);
					isAudit.setIsAudit("2");
					isAudit.setOperator(this.getUserBean().getUserName());
					isAudit.setRemark(new String(remark.getBytes("iso-8859-1"),"utf-8").trim());
					this.commonService.update(isAudit);
				}
			}
		}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		toAuditWebSiteManager();
		return SUCCESS;
	}
	//删除
	public String delAuditWebSiteManager(){
		if(StringUtils.isNotBlank(ids)){
			String[] id=ids.split(",");
			for(int i=0;i<id.length;i++){
				InternationalIsAuditWeb isAudit=(InternationalIsAuditWeb)commonService.uniqueResult("from InternationalIsAuditWeb where id='"+id[i]+"'");
				this.commonService.delete(isAudit);
			}
		}
		toAuditWebSiteManager();
		return SUCCESS;
	}
	//删除   针对全选
	public String delAuditWebSiteManager1(){
		if(merNo!=null){
			InternationalMerchant mer=(InternationalMerchant) commonService.uniqueResult("from InternationalMerchant where merno='"+merNo+"'");
			if(mer!=null){
				String hql="from InternationalIsAuditWeb where merchanid='"+mer.getId()+"'"+" and isaudit='"+auditStatus+"'";
				List<InternationalIsAuditWeb> list=commonService.list(hql);//根据商户的ID查询出所有的信息记录
				for(int i=0;i<list.size();i++){
					InternationalIsAuditWeb isAudit=list.get(i);
					this.commonService.delete(isAudit);
				}
			}
		}
		toAuditWebSiteManager();
		return SUCCESS;
	}
	//修改已审核的状态为重新审核
	public String againAuditWebSiteManager(){
		if(StringUtils.isNotBlank(ids)){
			String[] id=ids.split(",");
			for(int i=0;i<id.length;i++){
				InternationalIsAuditWeb isAudit=(InternationalIsAuditWeb)commonService.uniqueResult("from InternationalIsAuditWeb where id='"+id[i]+"'");
				if("1".equals(isAudit.getIsAudit())&&isAudit.getWebcannelId()!=null){
					InternationalWebchannels web=(InternationalWebchannels) commonService.uniqueResult("from InternationalWebchannels where id='"+isAudit.getWebcannelId()+"'");
					if(web!=null){
						commonService.delete(web);
					}
				}
				isAudit.setOperator("");
				isAudit.setWebcannelId(null);
				isAudit.setIsAudit("0");
				this.commonService.update(isAudit);
			}
		}
		toAuditWebSiteManager();
		return SUCCESS;
	}
	//修改已审核的状态为重新审核针对全选
	public String againAuditWebSiteManager1(){
		if(merNo!=null){
			InternationalMerchant mer=(InternationalMerchant) commonService.uniqueResult("from InternationalMerchant where merno='"+merNo+"'");
			if(mer!=null){
				String hql="from InternationalIsAuditWeb where merchanid='"+mer.getId()+"'"+" and isaudit='"+auditStatus+"'";
				List<InternationalIsAuditWeb> list=commonService.list(hql);//根据商户的ID查询出所有的信息记录
				for(int i=0;i<list.size();i++){
					InternationalIsAuditWeb isAudit=list.get(i);
					if("1".equals(isAudit.getIsAudit())&&isAudit.getWebcannelId()!=null){
						InternationalWebchannels web=(InternationalWebchannels) commonService.uniqueResult("from InternationalWebchannels where id='"+isAudit.getWebcannelId()+"'");
						if(web!=null){
							commonService.delete(web);
						}
					}
					isAudit.setOperator("");
					isAudit.setWebcannelId(null);
					isAudit.setIsAudit("0");
					this.commonService.update(isAudit);
				}	
			}
		}
		toAuditWebSiteManager();
		return SUCCESS;
	}
	//去待审核网址修改
	public String toModifyWebSite(){
		isAuditWeb=(InternationalIsAuditWeb) commonService.uniqueResult("from InternationalIsAuditWeb where id='"+webId+"'");
		return SUCCESS;
		
	}
	//待审核网址修改
	public String modifyWebSite(){
		InternationalIsAuditWeb isWeb=(InternationalIsAuditWeb) commonService.load(InternationalIsAuditWeb.class, isAuditWeb.getId());
		isWeb.setTradeWebsite(isAuditWeb.getTradeWebsite());
		isWeb.setWebsite(isAuditWeb.getWebsite());
		commonService.update(isWeb);
		toAuditWebSiteManager();
		return SUCCESS;
		
	}
	public List getWebSiteTypesList(){
		String hql="select distinct(t.webSiteType) from InternationalWebchannels t where t.webSiteType is not null order by t.webSiteType asc";
		List list=commonService.list(hql);
		return list;
	}
	public CommonService getCommonService() {
		return commonService;
	}

	public void setCommonService(CommonService commonService) {
		this.commonService = commonService;
	}

	public String getHql() {
		return hql;
	}

	public void setHql(String hql) {
		this.hql = hql;
	}

	public PageInfo getInfo() {
		return info;
	}

	public void setInfo(PageInfo info) {
		this.info = info;
	}

	public InternationalWebchannels getWebsite() {
		return website;
	}

	public void setWebsite(InternationalWebchannels website) {
		this.website = website;
	}

	public InternationalMerchant getMerchant() {
		return merchant;
	}

	public void setMerchant(InternationalMerchant merchant) {
		this.merchant = merchant;
	}

	public List<InternationalWebchannels> getMerchantWebSiteList() {
		return merchantWebSiteList;
	}

	public void setMerchantWebSiteList(
			List<InternationalWebchannels> merchantWebSiteList) {
		this.merchantWebSiteList = merchantWebSiteList;
	}

	public List<InternationalMerchant> getInternationalMerchantList() {
		return internationalMerchantList;
	}

	public void setInternationalMerchantList(
			List<InternationalMerchant> internationalMerchantList) {
		this.internationalMerchantList = internationalMerchantList;
	}

	public String getMerNo() {
		return merNo;
	}

	public void setMerNo(String merNo) {
		this.merNo = merNo;
	}

	public String getAuditStatus() {
		return auditStatus;
	}

	public void setAuditStatus(String auditStatus) {
		this.auditStatus = auditStatus;
	}

	public String getIds() {
		return ids;
	}

	public void setIds(String ids) {
		this.ids = ids;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getWebId() {
		return webId;
	}

	public void setWebId(String webId) {
		this.webId = webId;
	}

	public InternationalIsAuditWeb getIsAuditWeb() {
		return isAuditWeb;
	}

	public void setIsAuditWeb(InternationalIsAuditWeb isAuditWeb) {
		this.isAuditWeb = isAuditWeb;
	}

	public List getWebSiteTypeList() {
		return webSiteTypeList;
	}

	public void setWebSiteTypeList(List webSiteTypeList) {
		this.webSiteTypeList = webSiteTypeList;
	}

	public String getWebSiteType() {
		return webSiteType;
	}

	public void setWebSiteType(String webSiteType) {
		this.webSiteType = webSiteType;
	}

	public String getWebtype() {
		return webtype;
	}

	public void setWebtype(String webtype) {
		this.webtype = webtype;
	}

	public List getWebTypeList() {
		return webTypeList;
	}

	public void setWebTypeList(List webTypeList) {
		this.webTypeList = webTypeList;
	}

}
