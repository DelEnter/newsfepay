package com.ecpss.action.managevaluesetting;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.ServletActionContext;
import org.bouncycastle.asn1.ocsp.Request;

import com.ecpss.action.BaseAction;
import com.ecpss.model.log.SystemLog;
import com.ecpss.model.shop.InternationalMerchant;
import com.ecpss.model.shop.InternationalMerchantManager;
import com.ecpss.model.shop.InternationalWebchannels;
import com.ecpss.vo.log.InternationalMerchantManagerLog;

/**
 * �̻�����ֵ�趨
 * 
 * @author huhongguang
 * 
 */
public class ManageValueSettingAction extends BaseAction {

	/** �̻�id */
	private Long[] merid;

	/** �̻�����ֵʵ����� */
	private InternationalMerchantManager imm;

	/** �̻�����ֵʵ����� */
	public List<InternationalMerchantManager> internationalMerchantManagerList;

	private InternationalMerchant merchant; // �̻�ʵ��

	private Long[] merno; // �̻���

	private Long[] iid; // �̻�����ֵId

	private Long markValue[]; // ��ֵ
	private Long confirmValue[]; // ��ֵ

	private Long[] penQuota; // ���ʽ����޶�(���ߣ�

	private Long[] penQuotaLower; // ���ʽ����޶�(���ߣ�

	private Long[] dayQuota; // �콻���޶�

	private Long[] monthQuota; // �½����޶�

	private Long[] urlCount; // �½����޶�

	private Long merchantNo;

	private int flag;

	private String sign = "0";

	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

	/**
	 * �����̻�id��ѯ�̻�����ֵ(����)
	 * 
	 * @return
	 */
	public String findManageValueByMerchantID() {
		// �����̻�id��ѯ�̻�����ֵʵ�����
		imm = (InternationalMerchantManager) this.commonService
				.uniqueResult("select imm from InternationalMerchantManager imm where imm.merchantId = '"
						+ merid[0] + "' ");
		// �����̑�id��ԃ�̑�
		merchant = (InternationalMerchant) this.commonService
				.uniqueResult("select im from InternationalMerchant im where im.id= '"
						+ merid[0] + "' ");
		if (imm == null) {
			// ��ѯ��Ĭ�ϵ�һ��������ֵ���̻�����ֵ����
			imm = (InternationalMerchantManager) this.commonService
					.uniqueResult("select imm from InternationalMerchantManager imm where imm.merchantId is null");

		}
		this.getLoaction().setReload(true);
		return SUCCESS;
	}

	/**
	 * �����̻��Ų�ѯ�̻�����ֵ
	 * 
	 * @return
	 */
	public String findManageValueByMerchant() {
		System.out.println("==================" + sign);
		System.out.println("�̻���: " + merchantNo);
		// for (int i = 0; i < merchantNo.length; i++) {
		if (merchantNo == null || "".equals(merchantNo)) {
			internationalMerchantManagerList = commonService
					.list("select imm,im from InternationalMerchantManager imm,InternationalMerchant im "
							+ "where imm.merchantId=im.id and imm.merchantId is not null");
			return SUCCESS;
		}
		merchant = (InternationalMerchant) commonService
				.uniqueResult("select im from InternationalMerchant im where im.merno = '"
						+ merchantNo + "' ");
		if (merchant == null) {
			messageAction = "�̻��Ų�����";
			return SUCCESS;
		}

		Long merchantId = (Long) commonService
				.uniqueResult("select count(imm.id) from InternationalMerchantManager imm where imm.merchantId = '"
						+ merchant.getId() + "' ");
		// ����Ĭ�ϵ�һ��
		if (merchantId != 1) {
			internationalMerchantManagerList = commonService
					.list("select imm,im from InternationalMerchantManager imm,InternationalMerchant im where imm.merchantId is null and im.merno= '"
							+ merchantNo + "' ");
			flag = 1;
			return SUCCESS;
		}

		internationalMerchantManagerList = commonService
				.list("select imm,im from InternationalMerchantManager imm,InternationalMerchant im where imm.merchantId=im.id and im.merno= '"
						+ merchantNo + "' ");

		// }
		return SUCCESS;
	}

	/**
	 * ȥ�̻�����ҳ��
	 * 
	 * @return
	 */
	public String toManageValueByMerchant() {
		try {

			return SUCCESS;
		} catch (Exception e) {
			e.printStackTrace();
			this.messageAction = "��ת���̻�����ҳ�����!";
			return this.OPERATE_SUCCESS;
		}
	}

	/**
	 * �����̻�����ֵ(����)
	 * 
	 * @return
	 */
	public String saveManageValue1() {
		System.out.println("******************�����̻�����ֵ******************");
		InternationalMerchantManager immTemp = null;
		// �����̻�id��ѯ�̻�����ֵʵ�����
		immTemp = (InternationalMerchantManager) this.commonService
				.uniqueResult("select imm from InternationalMerchantManager imm where imm.merchantId = '"
						+ merid[0] + "' ");
		// �̻�id������
		if (immTemp == null) {
			// ��ѯ��Ĭ�ϵ�һ��������ֵ���̻�����ֵ����
			InternationalMerchantManager internationalMerchantManager = (InternationalMerchantManager) this.commonService
					.uniqueResult("select imm from InternationalMerchantManager imm where imm.merchantId is null ");
			// �����̑�����ֵvo����
			InternationalMerchantManagerLog iml = new InternationalMerchantManagerLog();
			// ��¼����ǰ��״̬
			iml.setDayQuota(internationalMerchantManager.getDayQuota());
			iml.setMarkValue(internationalMerchantManager.getMarkValue());
			// iml.setMerchantAddress(internationalMerchantManager
			// .getMerchantAddress());
			iml.setMerchantId(internationalMerchantManager.getMerchantId());
			iml.setMonthQuota(internationalMerchantManager.getMonthQuota());
			iml.setPenQuota(internationalMerchantManager.getPenQuota());
			iml.setMerno(merno[0].toString());

			// �����̻�����ʵ�����
			InternationalMerchantManager immSave = new InternationalMerchantManager();
			immSave.setMerchantId(merid[0]);
			// immSave.setMerchantAddress(imm.getMerchantAddress());
			immSave.setMarkValue((imm.getMarkValue()));
			immSave.setPenQuota(imm.getPenQuota());
			immSave.setDayQuota(imm.getDayQuota());
			immSave.setMonthQuota(imm.getMonthQuota());
			immSave.setRiskMoney(imm.getRiskMoney());
			immSave.setChannelMoney(imm.getChannelMoney());
			this.commonService.save(immSave);

			// ���º�
			iml.setDayQuotaafter(immSave.getDayQuota());
			iml.setMarkValueafter(immSave.getMarkValue());
			// iml.setMerchantAddressafter(immSave.getMerchantAddress());
			iml.setMerchantIdafter(immSave.getMerchantId());
			iml.setMonthQuotaafter(immSave.getMonthQuota());
			iml.setPenQuotaafter(immSave.getPenQuota());

			// ����ϵͳ��־����
			SystemLog sl = new SystemLog();
			sl.setUserName(getUserBean().getUserName());
			sl.setOperTime(new Date());
			sl.setOperType("1");
			sl.setRemarks(iml.getUpdateLog());
			sl.setRescReow("�����̻�����ֵ");
			this.commonService.save(sl);
			this.messageAction = "�̻�����ֵ����ɹ�";
			return OPERATE_SUCCESS;
		} else {
			// ��¼����ǰ��״̬
			InternationalMerchantManagerLog iml = new InternationalMerchantManagerLog();
			iml.setDayQuota(immTemp.getDayQuota());
			iml.setMarkValue(immTemp.getMarkValue());
			// iml.setMerchantAddress(immTemp.getMerchantAddress());
			iml.setMerchantId(immTemp.getMerchantId());
			iml.setMonthQuota(immTemp.getMonthQuota());
			iml.setPenQuota(immTemp.getPenQuota());
			iml.setMerno(merno[0].toString());

			// �����̻�����ʵ�����
			immTemp.setMerchantId(merid[0]);
			immTemp.setMerchantAddress(imm.getMerchantAddress());
			immTemp.setMarkValue((imm.getMarkValue()));
			immTemp.setPenQuota(imm.getPenQuota());
			immTemp.setDayQuota(imm.getDayQuota());
			immTemp.setMonthQuota(imm.getMonthQuota());
			immTemp.setRiskMoney(imm.getRiskMoney());
			immTemp.setChannelMoney(imm.getChannelMoney());
			this.commonService.update(immTemp);

			// ��¼�������״̬
			iml.setDayQuotaafter(immTemp.getDayQuota());
			iml.setMarkValueafter(immTemp.getMarkValue());
			// iml.setMerchantAddressafter(immTemp.getMerchantAddress());
			iml.setMerchantIdafter(immTemp.getMerchantId());
			iml.setMonthQuotaafter(immTemp.getMonthQuota());
			iml.setPenQuotaafter(immTemp.getPenQuota());

			// ������־����ֵ
			SystemLog sl = new SystemLog();
			sl.setUserName(getUserBean().getUserName());
			sl.setOperTime(new Date());
			sl.setOperType("2");
			sl.setRemarks(iml.getUpdateLog());
			sl.setRescReow("�����̻�����ֵ");
			this.commonService.save(sl);
			this.getLoaction().setReload(true);
			this.messageAction = "�̻�����ֵ����ɹ�";
			return this.OPERATE_SUCCESS;
		}

	}

	/**
	 * �����̻�����ֵ
	 * 
	 * @return
	 */
	public String saveManageValue() {
		InternationalMerchantManager immTemp = null;
		if (iid != null && !"".equals(iid)) {
			// �����̑�����ֵvo����
			InternationalMerchantManagerLog iml = new InternationalMerchantManagerLog();
			// ��¼����ǰ��״̬
			InternationalMerchantManager im = null;
			try {
				for (int i = 0; i < iid.length; i++) {
					if (merid.length != 0 && !merid.equals("")) {
						// �����̻�id��ѯ�̻�����ֵʵ�����
						immTemp = (InternationalMerchantManager) this.commonService
								.uniqueResult("select imm from InternationalMerchantManager imm where imm.merchantId = '"
										+ merid[i] + "' ");
					}
					// �̻�id������
					if (immTemp == null) {
						// ��ѯ��Ĭ�ϵ�һ��������ֵ���̻�����ֵ����
						InternationalMerchantManager internationalMerchantManager = (InternationalMerchantManager) this.commonService
								.uniqueResult("select imm from InternationalMerchantManager imm where imm.merchantId is null ");
						// ��¼����ǰ��״̬
						iml.setDayQuota(internationalMerchantManager
								.getDayQuota());
						iml.setMarkValue(internationalMerchantManager
								.getMarkValue());
						iml.setMerchantId(internationalMerchantManager
								.getMerchantId());
						iml.setMonthQuota(internationalMerchantManager
								.getMonthQuota());
						iml.setPenQuota(internationalMerchantManager
								.getPenQuota());
						iml.setPenQuotaLower(internationalMerchantManager
								.getPenQuotaLower());
						iml.setMerno(merchantNo.toString());

						// �����̻�����ʵ�����
						InternationalMerchantManager immSave = new InternationalMerchantManager();
						immSave.setMerchantId(merid[i]);
						// immSave.setMerchantAddress(imm.getMerchantAddress());
						immSave.setMarkValue(markValue[i]);
						immSave.setPenQuota(penQuota[i]);
						immSave.setPenQuotaLower(penQuotaLower[i]);
						immSave.setDayQuota(dayQuota[i]);
						immSave.setMonthQuota(monthQuota[i]);
//						immSave.setRiskMoney(r[i]);
//						immSave.setChannelMoney(monthQuota[i]);
						immSave.setConfirmValue(confirmValue[i]);
						immSave.setUrlCount(urlCount[i]);
						commonService.save(immSave);
						sign = "1";
						// ���º�
						iml.setDayQuotaafter(immSave.getDayQuota());
						iml.setMarkValueafter(immSave.getMarkValue());
						iml.setPenQuotaLowerafter(immSave.getPenQuotaLower());
						iml.setMerchantIdafter(immSave.getMerchantId());
						iml.setMonthQuotaafter(immSave.getMonthQuota());
						iml.setPenQuotaafter(immSave.getPenQuota());

						// ����ϵͳ��־����
						SystemLog sl = new SystemLog();
						sl.setUserName(getUserBean().getUserName());
						sl.setMerno(merno[i].toString());
						sl.setOperTime(new Date());
						sl.setOperType("1");
						sl.setRemarks(iml.getUpdateLog());
						sl.setRescReow("�����̻�����ֵ");
						commonService.save(sl);
						return SUCCESS;
					} else {
						// ��¼����ǰ��״̬
						iml.setDayQuota(immTemp.getDayQuota());
						iml.setMarkValue(immTemp.getMarkValue());
						iml.setPenQuotaLowerafter(immTemp.getPenQuotaLower());
						iml.setMerchantId(immTemp.getMerchantId());
						iml.setMonthQuota(immTemp.getMonthQuota());
						iml.setPenQuota(immTemp.getPenQuota());
						iml.setMerno(merno[i].toString());

						// �����̻�����ʵ�����
						immTemp = (InternationalMerchantManager) commonService
								.load(InternationalMerchantManager.class,
										iid[i]);
						immTemp.setMerchantId(merid[i]);
						immTemp.setMarkValue(markValue[i]);
						immTemp.setPenQuota(penQuota[i]);
						immTemp.setPenQuotaLower(penQuotaLower[i]);
						immTemp.setDayQuota(dayQuota[i]);
						immTemp.setMonthQuota(monthQuota[i]);
						immTemp.setConfirmValue(confirmValue[i]);
						immTemp.setUrlCount(urlCount[i]);
						commonService.update(immTemp);
						if (iid.length == 1) {
							merchantNo = Long.valueOf(iml.getMerno());
							sign = "1";
						} else if (merid.length == iid.length) {
							merchantNo = null;
							sign = "1";
						}
						// ��¼�������״̬
						iml.setDayQuotaafter(immTemp.getDayQuota());
						iml.setMarkValueafter(immTemp.getMarkValue());
						iml.setMerchantIdafter(immTemp.getMerchantId());
						iml.setMonthQuotaafter(immTemp.getMonthQuota());
						iml.setPenQuotaafter(immTemp.getPenQuota());
						iml.setPenQuotaafter(immTemp.getPenQuota());

						// ������־����ֵ
						SystemLog sl = new SystemLog();
						sl.setUserName(getUserBean().getUserName());
						sl.setMerno(merno[i].toString());
						sl.setOperTime(new Date());
						sl.setOperType("2");
						sl.setRemarks(iml.getUpdateLog());
						sl.setRescReow("�����̻�����ֵ");
						commonService.save(sl);
					}
				}
				return SUCCESS;
			} catch (RuntimeException e) {
				e.printStackTrace();
				sign = "1";
				return SUCCESS;
			}
		}
		return SUCCESS;
	}

	public InternationalMerchantManager getImm() {
		return imm;
	}

	public void setImm(InternationalMerchantManager imm) {
		this.imm = imm;
	}

	public List<InternationalMerchantManager> getInternationalMerchantManagerList() {
		return internationalMerchantManagerList;
	}

	public void setInternationalMerchantManagerList(
			List<InternationalMerchantManager> internationalMerchantManagerList) {
		this.internationalMerchantManagerList = internationalMerchantManagerList;
	}

	public InternationalMerchant getMerchant() {
		return merchant;
	}

	public void setMerchant(InternationalMerchant merchant) {
		this.merchant = merchant;
	}

	public Long[] getIid() {
		return iid;
	}

	public void setIid(Long[] iid) {
		this.iid = iid;
	}

	public Long[] getMarkValue() {
		return markValue;
	}

	public void setMarkValue(Long[] markValue) {
		this.markValue = markValue;
	}

	public Long[] getPenQuota() {
		return penQuota;
	}

	public void setPenQuota(Long[] penQuota) {
		this.penQuota = penQuota;
	}

	public Long[] getDayQuota() {
		return dayQuota;
	}

	public void setDayQuota(Long[] dayQuota) {
		this.dayQuota = dayQuota;
	}

	public Long[] getMonthQuota() {
		return monthQuota;
	}

	public void setMonthQuota(Long[] monthQuota) {
		this.monthQuota = monthQuota;
	}

	public Long[] getMerid() {
		return merid;
	}

	public void setMerid(Long[] merid) {
		this.merid = merid;
	}

	public Long[] getMerno() {
		return merno;
	}

	public void setMerno(Long[] merno) {
		this.merno = merno;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public Long getMerchantNo() {
		return merchantNo;
	}

	public void setMerchantNo(Long merchantNo) {
		this.merchantNo = merchantNo;
	}

	public Long[] getPenQuotaLower() {
		return penQuotaLower;
	}

	public void setPenQuotaLower(Long[] penQuotaLower) {
		this.penQuotaLower = penQuotaLower;
	}

	public Long[] getConfirmValue() {
		return confirmValue;
	}

	public void setConfirmValue(Long[] confirmValue) {
		this.confirmValue = confirmValue;
	}

	public Long[] getUrlCount() {
		return urlCount;
	}

	public void setUrlCount(Long[] urlCount) {
		this.urlCount = urlCount;
	}

}
