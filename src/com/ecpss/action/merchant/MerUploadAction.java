package com.ecpss.action.merchant;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import jxl.Workbook;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ecpss.action.BaseAction;
import com.ecpss.excel.builder.RowResult;
import com.ecpss.excel.builder.jexcel.JExcelRowObjectBuilder;
import com.ecpss.excel.rule.impl.CellRuleImpl;
import com.ecpss.excel.rule.impl.RowRuleImpl;
import com.ecpss.model.payment.InternationalCardholdersInfo;
import com.ecpss.model.payment.InternationalTradeinfo;
import com.ecpss.model.shop.InternationalMerchant;
import com.ecpss.service.common.CommonService;
import com.ecpss.service.iservice.ShopManagerService;
import com.ecpss.tools.TableExport;
import com.ecpss.tools.TableExportFactory;
import com.ecpss.util.DownloadUtils;
import com.ecpss.util.EmailInfo;
import com.ecpss.vo.ImportTrackingOrder;
import com.ecpss.web.PageInfo;
import com.opensymphony.xwork2.ActionContext;

public class MerUploadAction extends BaseAction {
	@Autowired
	@Qualifier("commonService")
	private CommonService commonService;
	@Autowired
	@Qualifier("shopManagerService")
	private ShopManagerService shopManagerService;

	private InternationalTradeinfo trade;
	private PageInfo info;
	private String hql;
	private String uploadFileName;
	private File upload;
	private String mail;

	private String merchantOrderNo;
	private String orderNo;
	private String startDate;
	private String endDate;
	private String isdownload;

	private File trackingName;

	/**
	 * ��ѯ�ϴ�ͼƬ
	 */
	public String findUploadPicture() {
		try {
			if (info == null) {
				info = new PageInfo();
			}
			hql = "FROM InternationalTradeinfo trade WHERE substr(trade.tradeState,1,1)=1 AND trade.merchantId="
					+ getMerchantBean().getMerchantId() + "";
			info = commonService.listQueryResultByHql(hql, info);

			return SUCCESS;
		} catch (Exception e) {
			e.printStackTrace();
			this.messageAction = "�ϴ�ͼƬ��ѯʧ��";
			return this.OPERATE_SUCCESS;
		}
	}

	/**
	 * ��ת���ϴ�ͼƬҳ��
	 */
	public String toUploadPicture() {
		try {
			return SUCCESS;
		} catch (Exception e) {
			e.printStackTrace();
			this.messageAction = "��ת���ϴ�ͼƬҳ��ʧ��";
			return this.OPERATE_SUCCESS;
		}
	}

	/**
	 * �ϴ�
	 */
	public String uploadPicture() {
		try {
			FileOutputStream fos = null;
			FileInputStream fis = null;
			File target = null;
			String targetDirectory = null;
			if (uploadFileName == null || uploadFileName.equals("")) {
				return INPUT;

			} else {
				targetDirectory = "D:\\\\upload\\\\";// this.getSavePath();
				// targetFileName = uploadFileName;
				// this.setDir(targetDirectory + uploadFileName);//�Ѵ�˫б�ܵ�·��д��������
				target = new File(targetDirectory, uploadFileName);
				FileUtils.copyFile(upload, target);// ��tomcat ��ʱ��upload·��,copy
				// ��Ŀ��target·��
				fos = new FileOutputStream(target);// ���ļ�д��Ŀ��·��
				fis = new FileInputStream(getUpload());
			}

			byte[] buffer = new byte[1024];
			int len = 0;
			while ((len = fis.read(buffer)) > 0) {
				fos.write(buffer, 0, len);
			}

			// System.out.println("uploadFileName---------"+uploadFileName);
			InternationalTradeinfo tradeinfo = (InternationalTradeinfo) commonService
					.load(InternationalTradeinfo.class, trade.getId());
			tradeinfo.setIsPicture(targetDirectory + "" + uploadFileName);
			commonService.update(tradeinfo);
			this.messageAction = "�ϴ�ͼƬ�ɹ�";
			return SUCCESS;
		} catch (Exception e) {
			e.printStackTrace();
			this.messageAction = "�ϴ�ͼƬʧ��";
			return this.OPERATE_SUCCESS;
		}
	}

	/**
	 * �鿴�ϴ����ٵ���
	 */
	public String findUploadNumber() throws Exception {
		try {
			if (info == null) {
				info = new PageInfo();
			}
			StringBuffer sb = new StringBuffer(
					"select trade FROM InternationalTradeinfo trade "
							+ "where substr(trade.tradeState,1,1)=1 and substr(trade.tradeState,2,1)!=1 "
							+ " and substr(trade.tradeState,3,1)!=1 "
							+ " and substr(trade.tradeState,2,1)!=1 "
							+ "and trade.merchantId="
							+ getMerchantBean().getMerchantId());
			if (StringUtils.isNotBlank(orderNo)) {
				sb.append(" and trade.orderNo like '" + orderNo.trim() + "%' ");
			}
			if (StringUtils.isNotBlank(merchantOrderNo)) {
				sb.append(" and trade.merchantOrderNo like '"
						+ merchantOrderNo.trim() + "%'");
			}
			if (StringUtils.isNotBlank(startDate)) {
				sb.append(" and trade.tradeTime>=to_date('" + startDate
						+ "','yyyy-MM-dd') ");
			}
			if (StringUtils.isNotBlank(endDate)) {
				sb.append(" and trade.tradeTime<=to_date('" + endDate
						+ " 23:59:59" + "','yyyy-MM-dd hh24:mi:ss')");
			}
			if (StringUtils.isNotBlank(isdownload)) {
				sb.append(" and trade.isTrackNo is null "
						+ " order by trade.tradeTime ");
				List<Object> objList = commonService.list(sb.toString());
				this.downloadTradeQuery(objList);
				return null;
			} else {
				sb.append(" order by trade.isTrackNo desc,trade.tradeTime ");
				info = commonService.listQueryResultByHql(sb.toString(), info);
				return SUCCESS;
			}
		} catch (Exception e) {
			e.printStackTrace();
			this.messageAction = "�鿴�ϴ����ٵ���ʧ��";
			return this.OPERATE_SUCCESS;
		}
	}

	/**
	 * ������Ҫ�ϴ��������ŵĽ���
	 * 
	 * @param oArray
	 * @return
	 */
	public void downloadTradeQuery(List<Object> oArray) throws IOException {
		TableExport export = TableExportFactory.createExcelTableExport();
		export.addTitle(new String[] { "���", "������ˮ��", "�̻�������", "��������", "�������",
				"��ݹ�˾", "���ٵ���", "��ַ" });
		export.setTableName("tracking");
		InternationalTradeinfo t = new InternationalTradeinfo();
		int i = 1;
		for (Object obj : oArray) {
			t = (InternationalTradeinfo) obj;
			export.addRow(new Object[] { i, t.getOrderNo(),
					t.getMerchantOrderNo(), t.getTradeTime(),
					t.getTradeAmount(), "", "", t.getTradeUrl() });
			i++;
		}
		OutputStream os = DownloadUtils.getResponseOutput("tracking.xls");
		export.export(os);
		DownloadUtils.closeResponseOutput();
	}

	// ȷ�������ϴ�
	public String confirmImport() throws Exception {
		try {
			if (trackingName != null) {
				Workbook workBook = Workbook.getWorkbook(trackingName);
				RowRuleImpl rowRule = new RowRuleImpl();
				rowRule.addCellRule(new CellRuleImpl("B", "orderNo"));
				rowRule.addCellRule(new CellRuleImpl("F", "trackingType"));
				rowRule.addCellRule(new CellRuleImpl("G", "trackingNo"));
				JExcelRowObjectBuilder reveBuilder = new JExcelRowObjectBuilder();
				reveBuilder.setSheet(workBook.getSheet(0));
				reveBuilder.setTargetClass(ImportTrackingOrder.class);
				reveBuilder.setRule(1, workBook.getSheet(0).getRows(), rowRule);

				int succCount = 0;
				int failedCount = 0;
				int sameCount = 0;
				RowResult<ImportTrackingOrder>[] cons = reveBuilder
						.parseExcel();
				ImportTrackingOrder track;
				InternationalMerchant merchant = (InternationalMerchant) this.commonService
						.load(InternationalMerchant.class, getMerchantBean()
								.getMerchantId());
				for (RowResult<ImportTrackingOrder> rowResult : cons) {
					// ��ȡexcel���ݵ��뵽bean ImportTrackingOrder
					track = rowResult.getRowObject();
					
					String oql1 = "select ti from InternationalTradeinfo ti where ti.isTrackNo='"+track.getTrackingType()
					+ track.getTrackingNo()+"' and ti.merchantId = "
						+ getMerchantBean().getMerchantId()
						+ " ";
					List<InternationalTradeinfo> list1 = commonService.list(oql1);
					if(list1.size()==0){
						// �жϸ��ٵ����ͺ͸��ٺ��Ƿ�Ϊ��,�յĻ������ϴ�
						String oql = "select ti from InternationalTradeinfo ti where ti.isTrackNo is null and ti.merchantId = "
								+ getMerchantBean().getMerchantId()
								+ " and ti.orderNo = '"
								+ track.getOrderNo().trim()
								+ "' ";
						List<InternationalTradeinfo> list = commonService.list(oql);
						if (list.size() != 0) {
							InternationalTradeinfo ti = (InternationalTradeinfo) list
									.get(0);
							if (StringUtils.isNotBlank(track.getTrackingType())
									&& StringUtils
											.isNotBlank(track.getTrackingNo())) {
								if (track.getTrackingType().equals("EMS")
										|| track.getTrackingType().equals("DHL")
										|| track.getTrackingType().equals("UPS")
										|| track.getTrackingType().equals("TNT")
										|| track.getTrackingType().equals("FedEx")
										|| track.getTrackingType().equals("DMS")
										|| track.getTrackingType().equals("USPS")
										|| track.getTrackingType().equals(
												"ChinaPost")
										|| track.getTrackingType().equals("HkPost")) {
									ti.setIsTrackNo(track.getTrackingType()
											+ track.getTrackingNo());
									commonService.update(ti);
									
									succCount++;
								}
							} else {
								failedCount++;
							}
						} else {
							failedCount++;
						}
					}else{
						sameCount++;
					}
				}
				messageAction = "�ɹ����" + succCount + "��,ʧ��" + failedCount
						+ "��,��"+sameCount+"�����ٵ����Ѿ�����.������������Ϣ��д���ϴ������ֶ���ӵ���";
				return SUCCESS;
			} else {
				messageAction = "��ѡ��һ����Ҫ�ϴ����ļ�";
				return SUCCESS;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return INPUT;
		}
	}

	/**
	 * �����ϴ�����
	 * 
	 * @return
	 */
	public String toImportTracking() {
		return SUCCESS;
	}

	/**
	 * �鿴���ظ��ٵ���
	 */
	public String download() {
		try {
			return SUCCESS;
		} catch (Exception e) {
			e.printStackTrace();
			this.messageAction = "���ظ��ٵ���ʧ��";
			return this.OPERATE_SUCCESS;
		}
	}

	/**
	 * �ϴ����ٵ���
	 */
	public String uploadNumber() {
		try {
			InternationalTradeinfo tradeInfo = (InternationalTradeinfo) commonService
					.load(InternationalTradeinfo.class, trade.getId());
			tradeInfo.setIsTrackNo(mail + trade.getIsTrackNo());
			commonService.update(tradeInfo);
			this.messageAction = "�ϴ����ٵ��ųɹ�!";
			return SUCCESS;
		} catch (Exception e) {
			e.printStackTrace();
			this.messageAction = "�ϴ����ٵ���ʧ��";
			return this.OPERATE_SUCCESS;
		}
	}

	/**
	 * ��ת���ϴ����ٵ���ҳ��
	 */
	public String toUploadNumber() {
		try {
			hql = "FROM InternationalTradeinfo trade WHERE trade.id="
					+ trade.getId() + "";
			trade = (InternationalTradeinfo) commonService.uniqueResult(hql);
			return SUCCESS;
		} catch (Exception e) {
			e.printStackTrace();
			this.messageAction = "�ϴ����ٵ���ʧ��";
			return this.OPERATE_SUCCESS;
		}
	}

	public static HttpServletRequest getHttpServletRequest() {
		return (HttpServletRequest) ActionContext.getContext().get(
				ServletActionContext.HTTP_REQUEST);
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
	 * @return the trade
	 */
	public InternationalTradeinfo getTrade() {
		return trade;
	}

	/**
	 * @param trade
	 *            the trade to set
	 */
	public void setTrade(InternationalTradeinfo trade) {
		this.trade = trade;
	}

	/**
	 * @return the mail
	 */
	public String getMail() {
		return mail;
	}

	/**
	 * @param mail
	 *            the mail to set
	 */
	public void setMail(String mail) {
		this.mail = mail;
	}

	/**
	 * @return the uploadFileName
	 */
	public String getUploadFileName() {
		return uploadFileName;
	}

	/**
	 * @param uploadFileName
	 *            the uploadFileName to set
	 */
	public void setUploadFileName(String uploadFileName) {
		this.uploadFileName = uploadFileName;
	}

	/**
	 * @return the upload
	 */
	public File getUpload() {
		return upload;
	}

	/**
	 * @param upload
	 *            the upload to set
	 */
	public void setUpload(File upload) {
		this.upload = upload;
	}

	public String getMerchantOrderNo() {
		return merchantOrderNo;
	}

	public void setMerchantOrderNo(String merchantOrderNo) {
		this.merchantOrderNo = merchantOrderNo;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getIsdownload() {
		return isdownload;
	}

	public void setIsdownload(String isdownload) {
		this.isdownload = isdownload;
	}

	public File getTrackingName() {
		return trackingName;
	}

	public void setTrackingName(File trackingName) {
		this.trackingName = trackingName;
	}
}
