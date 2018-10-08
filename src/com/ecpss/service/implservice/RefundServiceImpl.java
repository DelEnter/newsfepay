package com.ecpss.service.implservice;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sun.misc.BASE64Encoder;
import vpn.GooPayMessage;
import vpn.GooPayUtil;
import vpn.HJPayMessage;
import vpn.HJPayUtil;
import vpn.HJWPayMessage;
import vpn.HJWPayUtil;
import vpn.MotoDCCMessage;
import vpn.VpnUtil_Moto;
import vpn.YinlianMessage;
import vpn.YinlianUtil;

import com.ecpss.action.BaseAction;
import com.ecpss.action.filemanager.FileUpLoadAction;
import com.ecpss.action.pay.DirectCarderInfoAction;
import com.ecpss.action.pay.TradeManager;
import com.ecpss.dao.common.CommonDao;
import com.ecpss.model.FileManager;
import com.ecpss.model.channel.InternationalChannels;
import com.ecpss.model.payment.HuakuanedException;
import com.ecpss.model.payment.InternationalCardholdersInfo;
import com.ecpss.model.payment.InternationalTradeinfo;
import com.ecpss.model.refund.InternationalRefundManager;
import com.ecpss.model.shop.InternationalMerchant;
import com.ecpss.model.shop.InternationalTerminalManager;
import com.ecpss.service.iservice.RefundService;
import com.ecpss.service.iservice.ShopManagerService;
import com.ecpss.util.AES;
import com.ecpss.util.DateUtil;
import com.ecpss.util.GetBatchNo;
import com.ecpss.util.MD5;
import com.ecpss.util.StateUtils;
import com.ecpss.util.StatusUtil;
import com.ecpss.util.UploadUtils;
import com.ecpss.vo.MerchantBean;
import com.ecpss.vo.UserBean;
import com.opensymphony.xwork2.ActionContext;

@Service("refundService")
@Transactional
public class RefundServiceImpl implements RefundService{
	
	Logger logger = Logger.getLogger(RefundServiceImpl.class.getName());
	@Autowired
	@Qualifier("commonDao")
	private CommonDao commonDao;
	@Autowired
	@Qualifier("shopManagerService")
	private ShopManagerService shopManagerService;
//	@Autowired
//	@Qualifier("tradeManager")
//	private TradeManager tradeManager;
	public InternationalTradeinfo getTradeByMerNo(String tradeNo, String merOrderNo) {
		MerchantBean merbean = (MerchantBean)ActionContext.getContext().getSession().get("merchantBean");
		StringBuffer hql = new StringBuffer("select ti " +
		"from InternationalTradeinfo ti " +
		"where ti.merchantId=" + merbean.getMerchantId() + 
				" and substr(ti.tradeState,1,1)='1' ");
		if(StringUtils.isNotBlank(tradeNo)){
			hql.append(" and ti.orderNo='"+tradeNo.trim()+"' ");
		}
		if(StringUtils.isNotBlank(merOrderNo)){
			hql.append(" and ti.merchantOrderNo='"+merOrderNo.trim()+"' ");
		}
		InternationalTradeinfo ti = null;
		List<InternationalTradeinfo> list = this.commonDao.list(hql.toString());
		System.out.println("hql---------------"+hql);
		if(list.size()>0){
			ti = list.get(0);
		}
		return ti;
	}
	public List getRefundPreview() {
		StringBuffer sb = new StringBuffer();
		MerchantBean merchantbean = (MerchantBean)ActionContext.getContext().getSession().get("merchantBean");
		sb.append("select rm,ti " +
				"from InternationalRefundManager rm," +
				"InternationalTradeinfo ti " +
				"where rm.tradeId=ti.id " +
				"and rm.refundState=1" +  //������ſ��Խ���Ԥ���˿�
				"and ti.merchantId="+merchantbean.getMerchantId()+" order by rm.applyDate desc " );
		List list = this.commonDao.list(sb.toString());
		return list;
	}

	public List<InternationalRefundManager> getRefundByTradeId(Long tradeId){
		String hql="select rm from InternationalRefundManager rm where rm.tradeId="+tradeId;
		List<InternationalRefundManager> rm =  this.commonDao.list(hql);
//		InternationalRefundManager r = null;
//		if(rm.size()>0){
//			r = rm.get(0);
//		}
		return rm;
	}
	
	public void createRefund(Long [] refundIds,String batchNo) {
		try{
			Date d = new Date();
			UserBean user = (UserBean)ActionContext.getContext().getSession().get("user");
			for (Long r : refundIds) {
				InternationalRefundManager rm = (InternationalRefundManager) this.commonDao.load(InternationalRefundManager.class, r);
				InternationalTradeinfo ti  = (InternationalTradeinfo) this.commonDao.load(InternationalTradeinfo.class, rm.getTradeId());
				InternationalCardholdersInfo c = (InternationalCardholdersInfo) this.commonDao.uniqueResult("from InternationalCardholdersInfo where tradeId='"+ti.getId()+"'");
//				List<InternationalTerminalManager> listtm=this.commonDao.list("from InternationalTerminalManager where terminalNo='"+ti.getVIPTerminalNo()+"'");
				List listtm=this.commonDao.list("select it,ic.channelName from InternationalMerchantChannels im,InternationalTerminalManager it,InternationalChannels ic where im.channelId=ic.id and it.channelId=ic.id and im.id='"+ti.getTradeChannel()+"' and it.onoff='1'");
				InternationalTerminalManager tm=new InternationalTerminalManager();
				String chnals="0";
				String chnalType="";
				if(listtm.size()>0){
				Object[] tem = (Object[]) listtm.get(0);
				tm=(InternationalTerminalManager)tem[0];
//				InternationalChannels cl=(InternationalChannels) this.commonDao.load(InternationalChannels.class,tm.getChannelId());
				String chanelName1=tem[1].toString();
				chnals = chanelName1.split("-")[0];
				chnalType = chanelName1.split("-")[1];
				}
				logger.info("�˿����:"+ti.getOrderNo());
				if("VPN".equals(chnals)){
//				if(StringUtils.isNotBlank(ti.getVIPAuthorizationNo())){
					logger.info("***����VPN�˿�ͨ��***");
//					YinlianMessage yl=new YinlianMessage();
//					YinlianUtil yu=new YinlianUtil();
//					SimpleDateFormat sdf =new SimpleDateFormat("yyyyMMddHHmmss");
//					yl.setTrnxDatetime(sdf.format(new Date()));
//					yl.setCardNo(getCarNo(c.getCardNo()));
//					yl.setAmt(this.return12Amount(rm.getRefundRMBAmount()+""));
//					int posNo=this.tradeManager.intBySql("SELECT POS_SEQUENCE.NEXTVAL FROM DUAL");
//					logger.info("pos��ˮ�ţ�"+posNo);
//					yl.setPosFlwNo(ti.getVIPBatchNo());
//					yl.setReferenceNo(ti.getRef_No());
//					yl.setAuthResCode("");
//					yl.setTermId(ti.getVIPTerminalNo());
//					yl.setMerchId(tm.getMerchantNo());
//					yl.setResCode("");
//					yl.setOriginalPosNo(ti.getVIPBatchNo());
//					yl.setOriginalDate(ti.getBoc_time());
//					yu.getYLRefundMessage(yl);
					MotoDCCMessage dcc=new MotoDCCMessage();
					dcc.setTrans_Type("refund");
					dcc.setMerchant_Id(tm.getMerchantNo());
					dcc.setAuthor_Str(tm.getAuthcode());
					dcc.setTerminal_Id(ti.getVIPTerminalNo());
					dcc.setInvoice_No(ti.getVIPAuthorizationNo());
					dcc.setCurrency_Code_T("156");
					BigDecimal AmountOri=new BigDecimal(Double.toString(ti.getRmbAmount())).multiply(new BigDecimal(Double.toString(100.0)));
					BigDecimal AmountLoc=new BigDecimal(Double.toString(rm.getRefundRMBAmount())).multiply(new BigDecimal(Double.toString(100.0)));
					dcc.setAmount_Ori(String.valueOf(AmountOri.intValue()));
					dcc.setAmount_Loc(String.valueOf(AmountLoc.intValue()));
					dcc.setCard_No(getCarNo(c.getCardNo()));
					SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd");  
					dcc.setTran_Date_Ori(sdf.format(ti.getTradeTime()));
					dcc.setRef_No(ti.getRef_No());
					dcc.setHashCode(tm.getHashcode());
					dcc.setOrder_No(ti.getOrderNo());
					dcc.setCustom(ti.getOrderNo());
					VpnUtil_Moto vpn = new VpnUtil_Moto();
					dcc = vpn.getDCCvalue(dcc, "5");
					logger.info("***�˿����***"+dcc.getResp_Code());
					if("0000".equals(dcc.getResp_Code())){
						BigDecimal backCount=new BigDecimal(ti.getBackCount());
						BigDecimal refundCount=new BigDecimal(rm.getRefundAmount());
						backCount=backCount.add(refundCount);
						if(Double.valueOf(backCount.toString())>ti.getTradeAmount()){
							ti.setBackCount(ti.getTradeAmount());
						}else{
						ti.setBackCount(Double.valueOf(backCount.toString()));
						}
						if(ti.getBackCount()<ti.getTradeAmount()){
							ti.setTradeState(StatusUtil.updateStatus(ti.getTradeState(), 2, "2"));
						}else{
							ti.setTradeState(StatusUtil.updateStatus(ti.getTradeState(), 2, "1"));
						}
						this.commonDao.update(ti);
						if(ti.getBackCount()<ti.getTradeAmount()){
							rm.setRefundState("5");
						}else{
							rm.setRefundState("4");
						}
						rm.setRefundDate(d);
						rm.setBatchNo(batchNo);
						rm.setLastMan(user.getUserName());
						rm.setLastDate(d);
						this.commonDao.update(rm);
					
						//**********************������Ѿ�������Ľ����˿���뻮���쳣��************************
							if(ti.getTradeState().substring(7,8).equals("1")){
								String ehql="select h.id from HuakuanedException h where h.tradeType in (4,5) and h.tradeId="+ti.getId();
								List list = this.commonDao.list(ehql);
								if(list.size()==0){
									HuakuanedException he = new HuakuanedException();
									if(rm.getRefundAmount()<ti.getTradeAmount()){
										he.setTradeType("4");
									}else{
										he.setTradeType("5");
									}
									he.setTradeId(ti.getId());
									he.setIsAudit("0");
									he.setLastDate(new Date());
									this.commonDao.save(he);
								}else{
									HuakuanedException hk  = (HuakuanedException) this.commonDao.load(HuakuanedException.class, new Long(list.get(0).toString()));
									if(ti.getBackCount()<ti.getTradeAmount()){
										hk.setTradeType("4");
									}else{
										hk.setTradeType("5");
									}
									this.commonDao.update(hk);
								}
							}
					}else{
							rm.setRefundState("7");
							rm.setRefundDate(d);
							rm.setBatchNo(batchNo);
							rm.setLastMan(user.getUserName());
							rm.setLastDate(d);
							this.commonDao.update(rm);
						}
				}else if("HJ".equals(chnals)){
					logger.info("****����HJ�˿�ͨ��******");
					HJPayUtil HJ=new HJPayUtil();
					HJPayMessage hm=new HJPayMessage();
					hm.setAcctNo(tm.getMerchantNo());
					hm.setAgent_AcctNo(tm.getTerminalNo());
					hm.setOrderID(ti.getOrderNo());
					hm.setCurrCode("156");
					Double amountAndFee=rm.getRefundRMBAmount();
					if(ti.getChannelFee()!=null){
						amountAndFee=amountAndFee*(ti.getChannelFee()+1.0);
						amountAndFee = (double) (Math.round((double) amountAndFee * 100) / 100.00);
					}
					hm.setAmount((int)(amountAndFee *100)+"");
					hm.setIpAddress(c.getIp());
					hm.setCardType(chnalType);
					hm.setCardPAN(getCarNo(c.getCardNo()));
					hm.setPname(c.getProductInfo());
					hm.setCname(c.getFirstName()+c.getLastName());
					hm.setExpDate("2111");
					hm.setCvv2("111");
					hm.setIssCountry(c.getCountry());
					hm.setBaddress(c.getAddress());
					hm.setPostCode(c.getZipcode());
					hm.setIversion("V5.0");
					hm.setTelephone(c.getPhone());
					hm.setRetURL("www.sfepay.com");
					hm.setEmail(c.getEmail());
					String jiamiqian="";
					jiamiqian=tm.getHashcode()+ hm.getAcctNo() + hm.getOrderID() + hm.getAmount() + hm.getCurrCode();
					MessageDigest getMd5;
					BASE64Encoder base64E = new BASE64Encoder();
					String value = null;
					try {
						getMd5 = MessageDigest.getInstance("MD5");
						value =  base64E.encode(getMd5.digest(jiamiqian.getBytes("UTF-8")));
					} catch (Exception e) {
						e.printStackTrace();
					}
					System.out.println("����ǰ��"+jiamiqian);
					System.out.println("���ܺ�"+value);
					hm.setHashValue(value);
					hm.setTxnID(ti.getVIPAuthorizationNo());
					hm.setTxnType("03");
					HJ.get(hm);
					if("00".equals(hm.getRes_success())){
						BigDecimal backCount=new BigDecimal(ti.getBackCount());
						BigDecimal refundCount=new BigDecimal(rm.getRefundAmount());
						backCount=backCount.add(refundCount);
						if(Double.valueOf(backCount.toString())>ti.getTradeAmount()){
							ti.setBackCount(ti.getTradeAmount());
						}else{
						ti.setBackCount(Double.valueOf(backCount.toString()));
						}
						if(ti.getBackCount()<ti.getTradeAmount()){
							ti.setTradeState(StatusUtil.updateStatus(ti.getTradeState(), 2, "2"));
						}else{
							ti.setTradeState(StatusUtil.updateStatus(ti.getTradeState(), 2, "1"));
						}
						this.commonDao.update(ti);
						if(ti.getBackCount()<ti.getTradeAmount()){
							rm.setRefundState("5");
						}else{
							rm.setRefundState("4");
						}
						rm.setRefundDate(d);
						rm.setBatchNo(batchNo);
						rm.setLastMan(user.getUserName());
						rm.setLastDate(d);
						this.commonDao.update(rm);
					
						//**********************������Ѿ�������Ľ����˿���뻮���쳣��************************
						if(ti.getTradeState().substring(7,8).equals("1")){
							String ehql="select h.id from HuakuanedException h where h.tradeType in (4,5) and h.tradeId="+ti.getId();
							List list = this.commonDao.list(ehql);
							if(list.size()==0){
								HuakuanedException he = new HuakuanedException();
								if(rm.getRefundAmount()<ti.getTradeAmount()){
									he.setTradeType("4");
								}else{
									he.setTradeType("5");
								}
								he.setTradeId(ti.getId());
								he.setIsAudit("0");
								he.setLastDate(new Date());
								this.commonDao.save(he);
							}else{
								HuakuanedException hk  = (HuakuanedException) this.commonDao.load(HuakuanedException.class, new Long(list.get(0).toString()));
								if(ti.getBackCount()<ti.getTradeAmount()){
									hk.setTradeType("4");
								}else{
									hk.setTradeType("5");
								}
								this.commonDao.update(hk);
							}
						}
					}else{
						rm.setRefundState("7");
						rm.setRefundDate(d);
						rm.setBatchNo(batchNo);
						rm.setLastMan(user.getUserName());
						rm.setLastDate(d);
						this.commonDao.update(rm);
					}
				 }
//				else if("HW".equals(chnals)){
//						logger.info("****����HW�˿�ͨ��******");
//						HJWPayUtil hjw=new HJWPayUtil();
//						HJWPayMessage hwm=new HJWPayMessage();
//						hwm.setMerchantId(tm.getMerchantNo());
//						hwm.setMd5key(tm.getHashcode());
//						GetBatchNo ut = new GetBatchNo();
//						hwm.setBillNo(ut.getOrderinfo(ti.getOrderNo().substring(0, 4)));
//						hwm.setJcTradeId(ti.getVIPAuthorizationNo());
//						Double amountAndFee=rm.getRefundRMBAmount();
//						if(ti.getChannelFee()!=null){
//							amountAndFee=amountAndFee*(ti.getChannelFee()+1.0);
//							amountAndFee = (double) (Math.round((double) amountAndFee * 100) / 100.00);
//						}
//						hwm.setAmount(amountAndFee+"");
//						hwm.setTradType("2");
//						hjw.get(hwm);
//						if("00".equals(hwm.getRes_responseCode())){
//							BigDecimal backCount=new BigDecimal(ti.getBackCount());
//							BigDecimal refundCount=new BigDecimal(rm.getRefundAmount());
//							backCount=backCount.add(refundCount);
//							ti.setBackCount(Double.valueOf(backCount.toString()));
//							if(ti.getBackCount()<ti.getTradeAmount()){
//								ti.setTradeState(StatusUtil.updateStatus(ti.getTradeState(), 2, "2"));
//							}else{
//								ti.setTradeState(StatusUtil.updateStatus(ti.getTradeState(), 2, "1"));
//							}
//							this.commonDao.update(ti);
//							if(ti.getBackCount()<ti.getTradeAmount()){
//								rm.setRefundState("5");
//							}else{
//								rm.setRefundState("4");
//							}
//							rm.setRefundDate(d);
//							rm.setBatchNo(batchNo);
//							rm.setLastMan(user.getUserName());
//							rm.setLastDate(d);
//							this.commonDao.update(rm);
//						
//							//**********************������Ѿ�������Ľ����˿���뻮���쳣��************************
//							if(ti.getTradeState().substring(7,8).equals("1")){
//								String ehql="select h.id from HuakuanedException h where h.tradeType in (4,5) and h.tradeId="+ti.getId();
//								List list = this.commonDao.list(ehql);
//								if(list.size()==0){
//									HuakuanedException he = new HuakuanedException();
//									if(rm.getRefundAmount()<ti.getTradeAmount()){
//										he.setTradeType("4");
//									}else{
//										he.setTradeType("5");
//									}
//									he.setTradeId(ti.getId());
//									he.setIsAudit("0");
//									he.setLastDate(new Date());
//									this.commonDao.save(he);
//								}else{
//									HuakuanedException hk  = (HuakuanedException) this.commonDao.load(HuakuanedException.class, new Long(list.get(0).toString()));
//									if(ti.getBackCount()<ti.getTradeAmount()){
//										hk.setTradeType("4");
//									}else{
//										hk.setTradeType("5");
//									}
//									this.commonDao.update(hk);
//								}
//							}
//						}else{
//							rm.setRefundState("7");
//							rm.setRefundDate(d);
//							rm.setBatchNo(batchNo);
//							rm.setLastMan(user.getUserName());
//							rm.setLastDate(d);
//							this.commonDao.update(rm);
//						}
//					 }
//				 else if("GP".equals(chnals)){
//							logger.info("****����GP�˿�ͨ��******");
//							GooPayMessage msg=new GooPayMessage();
//							GooPayUtil yu=new GooPayUtil();
//							msg.setTrans_Type("refund");
//							msg.setBillNo(ti.getOrderNo());
//							Double oriAmountAndFee=ti.getRmbAmount();
//							Double amountAndFee=rm.getRefundRMBAmount();
//							if(ti.getChannelFee()!=null){
//								amountAndFee=amountAndFee*(ti.getChannelFee()+1.0);
//								oriAmountAndFee=oriAmountAndFee*(ti.getChannelFee()+1.0);
//								oriAmountAndFee = (double) (Math.round((double) oriAmountAndFee * 100) / 100.00);
//								amountAndFee = (double) (Math.round((double) amountAndFee * 100) / 100.00);
//							}
//							msg.setOriAmount(oriAmountAndFee+"");
//							msg.setRefundAmount(amountAndFee+"");
//							msg.setGrn(ti.getVIPBatchNo());
//							msg.setMerchantMID("2110");
//							MD5 md5 = new MD5();
//							String md5Hash=msg.getMerchantMID()+msg.getBillNo() +msg.getGrn() +msg.getOriAmount() +msg.getRefundAmount()+"Yu^HJXBd";
//							msg.setHASH(md5.getMD5ofStr(md5Hash));
//							
//							yu.get(msg);
//							if("00".equals(msg.getSucceed())){
//								BigDecimal backCount=new BigDecimal(ti.getBackCount());
//								BigDecimal refundCount=new BigDecimal(rm.getRefundAmount());
//								backCount=backCount.add(refundCount);
//								ti.setBackCount(Double.valueOf(backCount.toString()));
//								if(ti.getBackCount()<ti.getTradeAmount()){
//									ti.setTradeState(StatusUtil.updateStatus(ti.getTradeState(), 2, "2"));
//								}else{
//									ti.setTradeState(StatusUtil.updateStatus(ti.getTradeState(), 2, "1"));
//								}
//								this.commonDao.update(ti);
//								if(ti.getBackCount()<ti.getTradeAmount()){
//									rm.setRefundState("5");
//								}else{
//									rm.setRefundState("4");
//								}
//								rm.setRefundDate(d);
//								rm.setBatchNo(batchNo);
//								rm.setLastMan(user.getUserName());
//								rm.setLastDate(d);
//								this.commonDao.update(rm);
//							
//								//**********************������Ѿ�������Ľ����˿���뻮���쳣��************************
//								if(ti.getTradeState().substring(7,8).equals("1")){
//									String ehql="select h.id from HuakuanedException h where h.tradeType in (4,5) and h.tradeId="+ti.getId();
//									List list = this.commonDao.list(ehql);
//									if(list.size()==0){
//										HuakuanedException he = new HuakuanedException();
//										if(rm.getRefundAmount()<ti.getTradeAmount()){
//											he.setTradeType("4");
//										}else{
//											he.setTradeType("5");
//										}
//										he.setTradeId(ti.getId());
//										he.setIsAudit("0");
//										he.setLastDate(new Date());
//										this.commonDao.save(he);
//									}else{
//										HuakuanedException hk  = (HuakuanedException) this.commonDao.load(HuakuanedException.class, new Long(list.get(0).toString()));
//										if(ti.getBackCount()<ti.getTradeAmount()){
//											hk.setTradeType("4");
//										}else{
//											hk.setTradeType("5");
//										}
//										this.commonDao.update(hk);
//									}
//								}
//							}else{
//								rm.setRefundState("7");
//								rm.setRefundDate(d);
//								rm.setBatchNo(batchNo);
//								rm.setLastMan(user.getUserName());
//								rm.setLastDate(d);
//								this.commonDao.update(rm);
//							}
//						 }
				 else{
					BigDecimal backCount=new BigDecimal(ti.getBackCount());
					BigDecimal refundCount=new BigDecimal(rm.getRefundAmount());
					logger.info("��ʷ�˿��"+backCount+"    �����˿��"+refundCount);
					backCount=backCount.add(refundCount);
					logger.info("��ʷ�˿���+�����˿���="+backCount);
					if(Double.valueOf(backCount.toString())>ti.getTradeAmount()){
						ti.setBackCount(ti.getTradeAmount());
					}else{
					ti.setBackCount(Double.valueOf(backCount.toString()));
					}
					if(ti.getBackCount()<ti.getTradeAmount()){
						ti.setTradeState(StatusUtil.updateStatus(ti.getTradeState(), 2, "2"));
					}else{
						ti.setTradeState(StatusUtil.updateStatus(ti.getTradeState(), 2, "1"));
					}
					this.commonDao.update(ti);
					if(ti.getBackCount()<ti.getTradeAmount()){
						rm.setRefundState("5");
					}else{
						rm.setRefundState("4");
					}
					rm.setRefundDate(d);
					rm.setBatchNo(batchNo);
					rm.setLastMan(user.getUserName());
					rm.setLastDate(d);
					this.commonDao.update(rm);
				
					//**********************������Ѿ�������Ľ����˿���뻮���쳣��************************
						if(ti.getTradeState().substring(7,8).equals("1")){
							String ehql="select h.id from HuakuanedException h where h.tradeType in (4,5) and h.tradeId="+ti.getId();
							List list = this.commonDao.list(ehql);
							if(list.size()==0){
								HuakuanedException he = new HuakuanedException();
								if(rm.getRefundAmount()<ti.getTradeAmount()){
									he.setTradeType("4");
								}else{
									he.setTradeType("5");
								}
								he.setTradeId(ti.getId());
								he.setIsAudit("0");
								he.setLastDate(new Date());
								this.commonDao.save(he);
							}else{
								HuakuanedException hk  = (HuakuanedException) this.commonDao.load(HuakuanedException.class, new Long(list.get(0).toString()));
								if(ti.getBackCount()<ti.getTradeAmount()){
									hk.setTradeType("4");
								}else{
									hk.setTradeType("5");
								}
								this.commonDao.update(hk);
							}
						}
//						refundSynThread rf=new refundSynThread(ti.getOrderNo(), rm.getRefundRMBAmount()+"");
//						rf.start();
				}
			}
			
			}catch(Exception e){
				e.printStackTrace();
			}
//		Date d = new Date();
//		UserBean user = (UserBean)ActionContext.getContext().getSession().get("user");
//		for (Long r : refundIds) {
//			InternationalRefundManager rm = (InternationalRefundManager) this.commonDao.load(InternationalRefundManager.class, r);
//			InternationalTradeinfo ti  = (InternationalTradeinfo) this.commonDao.load(InternationalTradeinfo.class, rm.getTradeId());
//			ti.setBackCount(rm.getRefundAmount());
//			if(rm.getRefundAmount()<ti.getTradeAmount()){
//				ti.setTradeState(StatusUtil.updateStatus(ti.getTradeState(), 2, "2"));
//			}else{
//				ti.setTradeState(StatusUtil.updateStatus(ti.getTradeState(), 2, "1"));
//			}
//			this.commonDao.update(ti);
//			if(rm.getRefundAmount()<ti.getTradeAmount()){
//				rm.setRefundState("5");
//			}else{
//				rm.setRefundState("4");
//			}
//			rm.setRefundDate(d);
//			rm.setBatchNo(batchNo);
//			rm.setLastMan(user.getUserName());
//			rm.setLastDate(d);
//			this.commonDao.update(rm);
////			//**********************����˿���Ϣ�ȴ������ʼ�***************************
////			String hql="select c from InternationalCardholdersInfo c where c.tradeId="+ti.getId();
////			List<InternationalCardholdersInfo> cList = (List<InternationalCardholdersInfo>) this.commonDao.list(hql);
////			if(cList.size()==1){
////				EmailInfo emaiinfo= new EmailInfo();
////				String maininfo=emaiinfo.getRefundEmail(cList.get(0).getEmail(), ti);
////				shopManagerService.addSendMessages(cList.get(0).getEmail(), "refund@ecpss.cc", maininfo, "1");
////			}
//			
//			//**********************������Ѿ�������Ľ����˿���뻮���쳣��************************
//			if(ti.getTradeState().substring(7,8).equals("1")){
//				String ehql="select h.id from HuakuanedException h where h.tradeType in (4,5) and h.tradeId="+ti.getId();
//				List list = this.commonDao.list(ehql);
//				if(list.size()==0){
//					HuakuanedException he = new HuakuanedException();
//					if(rm.getRefundAmount()<ti.getTradeAmount()){
//						he.setTradeType("4");
//					}else{
//						he.setTradeType("5");
//					}
//					he.setTradeId(ti.getId());
//					he.setIsAudit("0");
//					he.setLastDate(new Date());
//					this.commonDao.save(he);
//				}
//			}
//		}
	}

	public List getRefundDetailByBatchNo(String batchNo,String refStatus) {
		StringBuffer sb = new StringBuffer();
		sb.append("select rm,ti " +
				"from InternationalRefundManager rm," +
				"InternationalTradeinfo ti " +
				"where rm.tradeId=ti.id " +
				"and rm.batchNo='"+batchNo.trim()+"' ");
				if(StringUtils.isNotBlank(refStatus)&&"0".equals(refStatus)){
					sb.append("and rm.refundState='7'");
				}
				if(StringUtils.isNotBlank(refStatus)&&"1".equals(refStatus)){
					sb.append("and rm.refundState in(4,5)");
				}
				if(StringUtils.isBlank(refStatus)){
					sb.append("and rm.refundState in(4,5,7)");
				}
		sb.append("order by rm.refundDate desc");
		List list = this.commonDao.list(sb.toString());
		return list;
	}
	
	public List<InternationalMerchant> getMerchantList(){
		StringBuffer sb = new StringBuffer();
		sb.append("select distinct m from InternationalMerchant m,InternationalTradeinfo ti,InternationalRefundManager rm ");
		sb.append("where rm.tradeId=ti.id and ti.merchantId=m.id and rm.refundState in(3,6) ");
		List<InternationalMerchant> list = this.commonDao.list(sb.toString());
		return list;
	}

	public List<String> getTerminalNoByRefund(Long refundstate) {
		StringBuffer sb = new StringBuffer();
		sb.append("select ti.VIPTerminalNo " +
				"from InternationalRefundManager rm," +
				"InternationalTradeinfo ti," +
				"InternationalMerchant m " +
				"where rm.tradeId=ti.id " +
				"and ti.merchantId=m.id " +
				"and rm.refundState="+refundstate+" " +
						"group by ti.VIPTerminalNo"); 
		return this.commonDao.list(sb.toString());
	}
	
	public String batchRefund() {
		StringBuffer sb = new StringBuffer();
		sb.append("select rm,ti,m,ci " +
				"from InternationalRefundManager rm," +
				"InternationalTradeinfo ti," +
				"InternationalMerchant m," +
				"InternationalCardholdersInfo ci " +
				"where rm.tradeId=ti.id " +
				"and ci.tradeId=ti.id " +
				"and ti.merchantId=m.id " +
				"and rm.refundState=3 ");  //�����ͨ����
		sb.append(" and (ti.VIPTerminalNo like '7777%' or ti.VIPTerminalNo like '7669%' or ti.VIPTerminalNo like '8889%' or ti.VIPTerminalNo like '07%') ");
		sb.append(" order by ti.VIPTerminalNo,rm.applyDate ");
		System.out.println(sb.toString());
		List<Object[]> objList  = this.commonDao.list(sb.toString());
		InternationalTradeinfo ti;
		InternationalRefundManager rm;
		InternationalMerchant merchant;
		InternationalCardholdersInfo ci;
		StringBuffer write = new StringBuffer();
		String merchantidby = null;
		Boolean flag=true;
		String merchantId9 = null;
		for(Object[] obj:objList){
			try{
					
				StringBuffer writeRefund = new StringBuffer();
				rm = (InternationalRefundManager) obj[0];
				ti = (InternationalTradeinfo) obj[1];
				merchant = (InternationalMerchant) obj[2];
				ci = (InternationalCardholdersInfo) obj[3];
				String merchantId = (String) this.commonDao.list("select tm.merchantNo from InternationalTerminalManager tm where tm.terminalNo='"+ti.getVIPTerminalNo()+"' ").get(0);
				if(flag){
					merchantId9 = merchantId.substring(0, 9);
					flag = false;
				}
				if(StringUtils.isNotBlank(merchantidby)){
					if(!merchantidby.equals(merchantId)){
						//***********************ͷ��Ϣ************************************
						//��һλ ��1��ͷ (1λ)
						writeRefund.append("1");
						//�ڶ�λ ����(YYYYMMDD)(8λ)
						SimpleDateFormat timeh=new SimpleDateFormat("yyyyMMdd"); 
						writeRefund.append(timeh.format(new Date()));
						//����λ M =Merchan    B =Bank
						writeRefund.append("M");
						//����λ ���Unit Type=B ��д�������ţ�������дSpace(7)
						writeRefund.append("       ");
						//����λ �̻���
						writeRefund.append(merchantId);
						writeRefund.append("\n");
					}
				}else{
					//***********************ͷ��Ϣ************************************
					//��һλ ��1��ͷ (1λ)
					writeRefund.append("1");
					//�ڶ�λ ����(YYYYMMDD)(8λ)
					SimpleDateFormat timeh=new SimpleDateFormat("yyyyMMdd"); 
					writeRefund.append(timeh.format(new Date()));
					//����λ M =Merchan    B =Bank
					writeRefund.append("M");
					//����λ ���Unit Type=B ��д�������ţ�������дSpace(7)
					writeRefund.append("       ");
					//����λ �̻���
					writeRefund.append(merchantId);
					writeRefund.append("\n");
					merchantidby = merchantId;
					
				}
				
				
				//****************************************�˿����Ϣ********************************
				//��һλ��2��ͷ(1λ)
				writeRefund.append("2");
				//�ڶ�λ�Ƿ�DCC,����EDC(3λ)
				if(ti.getVIPTerminalNo().startsWith("7777") || ti.getVIPTerminalNo().startsWith("07")){
					writeRefund.append("DCC");
				}else{
					writeRefund.append("EDC");
				}
				//����λ�ն˺Ŷ�Ӧ���̻���(15λ)
				writeRefund.append(merchantId);
				//����λ�ն˺�(8λ)
				writeRefund.append(ti.getVIPTerminalNo().trim());
				//����λ   1���ո�
				writeRefund.append(" ");
				//����λ  ����(16λ)
				
				writeRefund.append(AES.getCarNo(ci.getCardNo().trim()));
				//����λ  3���ո�
				writeRefund.append("   ");
				//�ڰ�λ ��Ч��(4λ)  YYMM
				writeRefund.append(ci.getExpiryDate().substring(2, 4)+ci.getExpiryDate().substring(0, 2));
				//�ھ�λ  1���ո�
				writeRefund.append(" ");
				//��ʮλ ����/�˿� ��������ҽ��Decimal place(2)    
				writeRefund.append(return12Amount(rm.getRefundRMBAmount().toString()));
				//��ʮһλ DCC���۽�����ҽ��Decimal place(2)  �˿����ҽ��׽��
				if(ti.getVIPTerminalNo().startsWith("7777")){
					if(rm.getRefundAmount()!=null){
						writeRefund.append(return12Amount(rm.getRefundAmount().toString()));
					}else{
						writeRefund.append("000000000000");
					}
				}else{
					writeRefund.append("000000000000");
				}
				//��ʮ��λ DCC������һ��Ҵ���  (����д)  (3λ)
				writeRefund.append("   ");
				//��ʮ��λ 1���ո�
				writeRefund.append(" ");
				//��ʮ��λ ԭ��������ҽ��Decimal place(2)  (12λ)
				writeRefund.append(return12Amount(ti.getRmbAmount().toString()));
				//��ʮ��λ ��Ȩ���� (6λ)
				writeRefund.append(ti.getVIPAuthorizationNo());
				//��ʮ��λ ԭ��������YYMMDD  (6λ)
				SimpleDateFormat time=new SimpleDateFormat("yyMMdd"); 
				if(ti.getVIPDisposeDate()!=null){
					writeRefund.append(time.format(ti.getVIPDisposeDate()));
				}else{
					writeRefund.append(time.format(ti.getTradeTime()));
				}
				//��ʮ��λ ԭ����Ʊ�ݺ� (6λ)
				writeRefund.append(ti.getVIPBatchNo());
				//��ʮ��λ ��������  (7λ)
				writeRefund.append("       ");
				//��ʮ��λ  ��C��������Sales�� / ��D�����˿�Refund��
				writeRefund.append("D ");
				//�ڶ�ʮλ   1���ո�  (1λ)
				writeRefund.append(" ");
				//�ڶ���һλ  ���д����� (2λ)Space(2)  ����˾������û�Ӧ��
				//00���ɹ�����
				///01��	�˿������ԭʼ���׽��
				//02��	�޷�ƥ��ԭʼ����
				//03��	ƥ����ԭʼ����
				//04��	�˿�����ڿ��˽��
				writeRefund.append("  ");
				//�ڶ�ʮ��λ   1���ո�  (1λ)
				writeRefund.append(" ");
				//�ڶ�ʮ��λ   ����Ԥ���ֶ� (20λ)  ECPSSϵͳ��ˮ���� 16887201041520162623425
				writeRefund.append(getOrderNoByRefund(ti.getOrderNo()));
				//�ڶ�ʮ��λ   1���ո�  (1λ)
				writeRefund.append(" ");
				//�ڶ�ʮ��λ   �˿��������ʱ��    YYMMDDHHMMSS
				SimpleDateFormat time3=new SimpleDateFormat("yyMMddHHmmss"); 
				writeRefund.append(time3.format(rm.getApplyDate()));
				
				//һ�ʽ�����������Ժ���
				writeRefund.append("\n");
				
				//д�뵽�˿��ļ��иı��˿�״̬
				write.append(writeRefund.toString());
				rm.setRefundState("6");
				this.commonDao.update(rm);
			}catch (Exception e) {
				continue;
			}
		}
		
		//��β��9���   
		write.append("9000000            000000");
		
		//����txt�ļ�
		try{   
			SimpleDateFormat time1=new SimpleDateFormat("yyyyMMddHHmmss");
			//�ļ���
			String imageFileName = "REFUND_"+merchantId9+"_"+time1.format(new Date())+"(�����˿�).txt";
			InputStream stream = FileUpLoadAction.class.getClassLoader().getResourceAsStream("/ecpss.properties");
			Properties p = new Properties();
			p.load(stream);
			File refund=new File(UploadUtils.getRefundUploadDir());
			if(!refund.exists()){
				refund.mkdir();				
			}
			File da = new File(refund+"/"+DateUtil.getDate());
			if(!da.exists()){
				da.mkdir();
			}
			File imageFile = new File(UploadUtils.getRefundUploadDir()+"/"+ UploadUtils.getUploadFileOpPath(imageFileName));
			FileOutputStream   fos=new   FileOutputStream(imageFile);
			DataOutputStream   out=new   DataOutputStream(fos);   
			out.writeBytes(write.toString()); 
			
			//�����ļ�   ����ʹ��
			FileManager filemanager = new FileManager();
			filemanager.setFilename(imageFileName);
			filemanager.setFileroute(UploadUtils.getUploadFileOpPath(imageFileName));
			filemanager.setFilesize(imageFile.length());
			filemanager.setFiledate(new Date());
			filemanager.setUsetype("0");
			filemanager.setDownloadcount(0L);
			this.commonDao.save(filemanager);
			out.close();
		}catch(Exception e) {
			System.out.println("ʧ��");
			e.printStackTrace();
		}
		return write.toString();
	}
	
	
	public String batchRefundByVC() {
		StringBuffer sb = new StringBuffer();
		sb.append("select rm,ti,m,ci " +
				"from InternationalRefundManager rm," +
				"InternationalTradeinfo ti," +
				"InternationalMerchant m," +
				"InternationalCardholdersInfo ci " +
				"where rm.tradeId=ti.id " +
				"and ci.tradeId=ti.id " +
				"and ti.merchantId=m.id " +
				"and rm.refundState=3 " +//�����ͨ����
				" and ci.cardNo like '5%' ");  
		sb.append(" and ti.VIPTerminalNo like '021814%' ");
		sb.append(" order by ti.VIPTerminalNo,rm.applyDate ");
		System.out.println(sb.toString());
		List<Object[]> objList  = this.commonDao.list(sb.toString());
		InternationalTradeinfo ti;
		InternationalRefundManager rm;
		InternationalMerchant merchant;
		InternationalCardholdersInfo ci;
		StringBuffer write = new StringBuffer();
		String merchantidby = null;
		Boolean flag=true;
		String merchantId9 = null;
		for(Object[] obj:objList){
			try{
					
				StringBuffer writeRefund = new StringBuffer();
				rm = (InternationalRefundManager) obj[0];
				ti = (InternationalTradeinfo) obj[1];
				merchant = (InternationalMerchant) obj[2];
				ci = (InternationalCardholdersInfo) obj[3];
				//�̻���
				String merchantId=ti.getVIPTerminalNo().substring(0, 9)+"000001";
				//�ն˺�
				String terminal = "555500"+ti.getVIPTerminalNo().substring(7, 9);
//				String merchantId = (String) this.commonDao.list("select tm.merchantNo from InternationalTerminalManager tm where tm.terminalNo='"+ti.getVIPTerminalNo()+"' ").get(0);
				if(flag){
					merchantId9 = merchantId.substring(0, 9);
					flag = false;
				}
				if(StringUtils.isNotBlank(merchantidby)){
					if(!merchantidby.equals(merchantId)){
						//***********************ͷ��Ϣ************************************
						//��һλ ��1��ͷ (1λ)
						writeRefund.append("1");
						//�ڶ�λ ����(YYYYMMDD)(8λ)
						SimpleDateFormat timeh=new SimpleDateFormat("yyyyMMdd"); 
						writeRefund.append(timeh.format(new Date()));
						//����λ M =Merchan    B =Bank
						writeRefund.append("M");
						//����λ ���Unit Type=B ��д�������ţ�������дSpace(7)
						writeRefund.append("       ");
						//����λ �̻���
						writeRefund.append(merchantId);
						writeRefund.append("\n");
					}
				}else{
					//***********************ͷ��Ϣ************************************
					//��һλ ��1��ͷ (1λ)
					writeRefund.append("1");
					//�ڶ�λ ����(YYYYMMDD)(8λ)
					SimpleDateFormat timeh=new SimpleDateFormat("yyyyMMdd"); 
					writeRefund.append(timeh.format(new Date()));
					//����λ M =Merchan    B =Bank
					writeRefund.append("M");
					//����λ ���Unit Type=B ��д�������ţ�������дSpace(7)
					writeRefund.append("       ");
					//����λ �̻���
					writeRefund.append(merchantId);
					writeRefund.append("\n");
					merchantidby = merchantId;
					
				}
				
				
				//****************************************�˿����Ϣ********************************
				//��һλ��2��ͷ(1λ)
				writeRefund.append("2");
				//�ڶ�λ�Ƿ�DCC,����EDC(3λ)
				if(ti.getVIPTerminalNo().startsWith("7777")){
					writeRefund.append("DCC");
				}else{
					writeRefund.append("EDC");
				}
				//����λ�ն˺Ŷ�Ӧ���̻���(15λ)
				writeRefund.append(merchantId);
				//����λ�ն˺�(8λ)
				writeRefund.append(terminal);
				//����λ   1���ո�
				writeRefund.append(" ");
				//����λ  ����(16λ)
				writeRefund.append(AES.getCarNo(ci.getCardNo().trim()));
				//����λ  3���ո�
				writeRefund.append("   ");
				//�ڰ�λ ��Ч��(4λ)  YYMM
				writeRefund.append(ci.getExpiryDate().substring(2, 4)+ci.getExpiryDate().substring(0, 2));
				//�ھ�λ  1���ո�
				writeRefund.append(" ");
				//��ʮλ ����/�˿� ��������ҽ��Decimal place(2)    
				writeRefund.append(return12Amount(rm.getRefundRMBAmount().toString()));
				//��ʮһλ DCC���۽�����ҽ��Decimal place(2)  �˿����ҽ��׽��
				if(ti.getVIPTerminalNo().startsWith("7777")){
					if(rm.getRefundAmount()!=null){
						writeRefund.append(return12Amount(rm.getRefundAmount().toString()));
					}else{
						writeRefund.append("000000000000");
					}
				}else{
					writeRefund.append("000000000000");
				}
				//��ʮ��λ DCC������һ��Ҵ���  (����д)  (3λ)
				writeRefund.append("   ");
				//��ʮ��λ 1���ո�
				writeRefund.append(" ");
				//��ʮ��λ ԭ��������ҽ��Decimal place(2)  (12λ)
				writeRefund.append(return12Amount(ti.getRmbAmount().toString()));
				//��ʮ��λ ��Ȩ���� (6λ)
				writeRefund.append(ti.getVIPAuthorizationNo());
				//��ʮ��λ ԭ��������YYMMDD  (6λ)
				SimpleDateFormat time=new SimpleDateFormat("yyMMdd"); 
				if(ti.getVIPDisposeDate()!=null){
					writeRefund.append(time.format(ti.getVIPDisposeDate()));
				}else{
					writeRefund.append(time.format(ti.getTradeTime()));
				}
				//��ʮ��λ ԭ����Ʊ�ݺ� (6λ)
				writeRefund.append("XXXXXX");
				//��ʮ��λ ��������  (7λ)
				writeRefund.append("       ");
				//��ʮ��λ  ��C��������Sales�� / ��D�����˿�Refund��
				writeRefund.append("D ");
				//�ڶ�ʮλ   1���ո�  (1λ)
				writeRefund.append(" ");
				//�ڶ���һλ  ���д����� (2λ)Space(2)  ����˾������û�Ӧ��
				//00���ɹ�����
				///01��	�˿������ԭʼ���׽��
				//02��	�޷�ƥ��ԭʼ����
				//03��	ƥ����ԭʼ����
				//04��	�˿�����ڿ��˽��
				writeRefund.append("  ");
				//�ڶ�ʮ��λ   1���ո�  (1λ)
				writeRefund.append(" ");
				//�ڶ�ʮ��λ   ����Ԥ���ֶ� (20λ)  ECPSSϵͳ��ˮ���� 16887201041520162623425
				writeRefund.append(getOrderNoByRefund(ti.getOrderNo()));
				//�ڶ�ʮ��λ   1���ո�  (1λ)
				writeRefund.append(" ");
				//�ڶ�ʮ��λ   �˿��������ʱ��    YYMMDDHHMMSS
				SimpleDateFormat time3=new SimpleDateFormat("yyMMddHHmmss"); 
				writeRefund.append(time3.format(rm.getApplyDate()));
				
				//һ�ʽ�����������Ժ���
				writeRefund.append("\n");
				
				//д�뵽�˿��ļ��иı��˿�״̬
				write.append(writeRefund.toString());
				rm.setRefundState("6");
				this.commonDao.update(rm);
			}catch (Exception e) {
				continue;
			}
		}
		
		//��β��9���   
		write.append("9000000            000000");
		
		//����txt�ļ�
		try{   
			SimpleDateFormat time1=new SimpleDateFormat("yyyyMMddHHmmss");
			//�ļ���
			String imageFileName = "REFUND_"+merchantId9+"_"+time1.format(new Date())+"(MIGS�����˿�).txt";
			InputStream stream = FileUpLoadAction.class.getClassLoader().getResourceAsStream("/ecpss.properties");
			Properties p = new Properties();
			p.load(stream);
			File refund=new File(UploadUtils.getRefundUploadDir());
			if(!refund.exists()){
				refund.mkdir();				
			}
			File da = new File(refund+"/"+DateUtil.getDate());
			if(!da.exists()){
				da.mkdir();
			}
			File imageFile = new File(UploadUtils.getRefundUploadDir()+"/"+ UploadUtils.getUploadFileOpPath(imageFileName));
			FileOutputStream   fos=new   FileOutputStream(imageFile);
			DataOutputStream   out=new   DataOutputStream(fos);   
			out.writeBytes(write.toString()); 
			
			//�����ļ�   ����ʹ��
			FileManager filemanager = new FileManager();
			filemanager.setFilename(imageFileName);
			filemanager.setFileroute(UploadUtils.getUploadFileOpPath(imageFileName));
			filemanager.setFilesize(imageFile.length());
			filemanager.setFiledate(new Date());
			filemanager.setUsetype("0");
			filemanager.setDownloadcount(0L);
			this.commonDao.save(filemanager);
			out.close();
		}catch(Exception e) {
			System.out.println("ʧ��");
			e.printStackTrace();
		}
		return write.toString();
	}

	/**
	 * ������ˮ���Ž�ȡ��20λ
	 * @param Orderno
	 * @return
	 */
	public String getOrderNoByRefund(String orderno){
		String orderNoByRefund = "                    ";
		if(orderno.length()>20){
			orderNoByRefund = orderno.substring(orderno.length()-20,orderno.length());
		}else{
			for (int i = 0; i < 20-orderno.length(); i++) {
				orderNoByRefund+="0";
			}
			orderNoByRefund = orderNoByRefund.trim() + orderno;
		}
		return orderNoByRefund;
	}
	
	
	public String return12Amount(String refundRMBMoney){
		String refundRMB = "000000000000";
		String zero_12 = "000000000000";
		DecimalFormat decimalFormat = new DecimalFormat("##############0.00");
		if(StringUtils.isNotBlank(refundRMBMoney) && refundRMBMoney.replace(".", "").matches("\\d+")){		
				String refundRMBStr = Double.valueOf((decimalFormat.format(Double.valueOf(refundRMBMoney))))*100+"";
				String refundRMB_0 = zero_12 + refundRMBStr.substring(0, refundRMBStr.indexOf("."));
				refundRMB = refundRMB_0.substring(refundRMB_0.length()-12, refundRMB_0.length());
		}
		return refundRMB;
	}
public String  getCarNo(String message){
		
		if(message.length()>16){
			return AES.getCarNo(message);
		}else{
			return message;
		}
		
		
	}

//public TradeManager getTradeManager() {
//	return tradeManager;
//}
//
//public void setTradeManager(TradeManager tradeManager) {
//	this.tradeManager = tradeManager;
//}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
