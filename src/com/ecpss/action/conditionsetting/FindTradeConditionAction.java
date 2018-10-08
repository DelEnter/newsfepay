package com.ecpss.action.conditionsetting;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.ecpss.action.BaseAction;
import com.ecpss.model.log.SystemLog;
import com.ecpss.model.shop.InternationalMerchant;
import com.ecpss.model.shop.InternationalTradecondition;
import com.ecpss.vo.conditionsetting.InternationalTradeconditionLog;

public class FindTradeConditionAction extends BaseAction {

	private long merid; // �̻�Id

	// ��������ʵ�����ļ���
	private List<InternationalTradecondition> internationalTradeconditionList;

	private List<InternationalTradecondition> bigInternationalTradeconditionList;

	// ��������ʵ�����
	private InternationalTradecondition internationalTradecondition;

	private InternationalMerchant merchant; // �̻�ʵ��

	private String[] remark; // ��ע

	private Long[] tradenumber; // �ɽ��ױ���

	private Long[] cycle; // ����

	private Long[] iid; // �����������id

	private Long[] itemno; // ��������

	private String[] itemName; // ��������

	private String merno; // �̻���

	private String flag = "0";

	/**
	 * ȥ������������ҳ��
	 * 
	 * @return
	 */
	public String toConditionSetting() {
		return SUCCESS;
	}

	/**
	 * ȥ������������ҳ��,�������̻�id��ѯ(����)
	 * 
	 * @return
	 */
	public String toConditionSetting1() {
		// �����̻�id��ѯ�����������̻�����Ϣ
		internationalTradeconditionList = this.commonService
				.list("select it,im from InternationalTradecondition it,InternationalMerchant im where it.merchantId= '"
						+ merid + "' and im.id= '" + merid + "' ");
		if (internationalTradeconditionList.size() == 0
				|| internationalTradeconditionList.isEmpty()) {
			// ��ѯĬ�ϵ�����
			internationalTradeconditionList = this.commonService
					.list("select it,im from InternationalTradecondition it,InternationalMerchant im where it.merchantId is null and im.id= '"
							+ merid + "' ");
		}
		return SUCCESS;

	}

	/**
	 * �޸��̻�����������������
	 * 
	 * @return
	 */
	public String updateCondition1() {
		// �����̻�id��ѯ��������ֵ ���ؽ�������ֵ���϶���
		internationalTradeconditionList = this.commonService
				.list("from InternationalTradecondition it where it.merchantId = "
						+ merid);
		if (iid != null && !iid.equals("")) {
			// �����̻�������������vo ��¼����ǰ��״̬
			InternationalTradeconditionLog itl = new InternationalTradeconditionLog();
			// ������־ʵ�����
			SystemLog sl = new SystemLog();
			if (internationalTradeconditionList.size() == 0
					|| internationalTradeconditionList.isEmpty()) {
				System.out.println("����");
				for (int i = 0; i < iid.length; i++) {
					// ��ѯ��Ĭ�ϵ�����
					internationalTradecondition = (InternationalTradecondition) this.commonService
							.load(InternationalTradecondition.class, iid[i]);
					// ��¼����ǰ��״̬
					itl.setCycle(internationalTradecondition.getCycle());
					itl.setRemark(internationalTradecondition.getRemark());
					itl.setTradenumber(internationalTradecondition
							.getTradenumber());
					itl.setMerno(merno);
					// �����̻���������
					internationalTradecondition.setMerchantId(merid);
					internationalTradecondition.setItemName(itemName[i]);
					internationalTradecondition.setItemno(itemno[i]);
					internationalTradecondition.setTradenumber(tradenumber[i]);
					internationalTradecondition.setCycle(cycle[i]);
					internationalTradecondition.setRemark(remark[i]);
					this.commonService.save(internationalTradecondition);

					// ��¼�������״̬
					itl.setCycleafter(internationalTradecondition.getCycle());
					itl.setRemarkafter(internationalTradecondition.getRemark());
					itl.setTradenumberafter(internationalTradecondition
							.getTradenumber());

					// ������־����
					sl.setUserName(getUserBean().getUserName());
					sl.setOperTime(new Date());
					sl.setOperType("1");
					sl.setRemarks(itl.addInternationalTradecondit());
					sl.setRescReow("�����̻���������");
					this.commonService.save(sl);
				}
			} else {
				for (int i = 0; i < internationalTradeconditionList.size(); i++) {
					System.out.println("�޸�");
					InternationalTradecondition internationalTradecondition = internationalTradeconditionList
							.get(i);
					// ��¼����ǰ��״̬
					itl.setCycle(internationalTradecondition.getCycle());
					itl.setRemark(internationalTradecondition.getRemark());
					itl.setTradenumber(internationalTradecondition
							.getTradenumber());
					// �����̻���������
					internationalTradecondition.setTradenumber(tradenumber[i]);
					internationalTradecondition.setCycle(cycle[i]);
					internationalTradecondition.setRemark(remark[i]);
					this.commonService.update(internationalTradecondition);

					// ��¼�������״̬
					itl.setCycleafter(internationalTradecondition.getCycle());
					itl.setRemarkafter(internationalTradecondition.getRemark());
					itl.setTradenumberafter(internationalTradecondition
							.getTradenumber());
					itl.setMerno(merno);

					// ������Ӧ����
					sl.setUserName(this.getUserBean().getUserName());
					sl.setOperTime(new Date());
					sl.setOperType("2");
					sl.setRemarks(itl.getUpdateInternationalTradecondit());
					sl.setRescReow("�����̻���������");
					this.commonService.save(sl);
				}
			}
		}
		this.getLoaction().setReload(true);
		this.messageAction = "�޸ĳɹ�";
		return OPERATE_SUCCESS;
	}

	/**
	 * ��ѯ�̻���������
	 * 
	 * @return
	 */
	public String findCondition() {
		System.out.println("�̻���: " + merno);
		if (merno != null && !merno.equals("")) {
			InternationalMerchant internationalMerchant = (InternationalMerchant) commonService
					.uniqueResult("select im from InternationalMerchant im where im.merno= '"
							+ merno + "' ");
			if (internationalMerchant == null) {
				messageAction = "�̻��Ų�����!";
				return OPERATE_SUCCESS;
			}
			// �����̻�id��ѯ�����������̻�����Ϣ
			internationalTradeconditionList = this.commonService
					.list("select it,im from InternationalTradecondition it,InternationalMerchant im where it.merchantId= '"
							+ internationalMerchant.getId()
							+ "' and im.id= '"
							+ internationalMerchant.getId() + "' ");
			if (internationalTradeconditionList.size() == 0
					|| internationalTradeconditionList.isEmpty()) {
				// ��ѯĬ�ϵ�����
				internationalTradeconditionList = this.commonService
						.list("select it,im from InternationalTradecondition it,InternationalMerchant im where it.merchantId is null and im.id= '"
								+ internationalMerchant.getId() + "' ");
			}
		}
		return SUCCESS;
	}

	/**
	 * �޸��̻���������
	 * 
	 * @return
	 */
	public String updateCondition() {
		// �����̻�id��ѯ��������ֵ ���ؽ�������ֵ���϶���
		try {
			internationalTradeconditionList = this.commonService
					.list("from InternationalTradecondition it where it.merchantId = "
							+ merid);
			if (iid != null && !iid.equals("")) {
				// �����̻�������������vo ��¼����ǰ��״̬
				InternationalTradeconditionLog itl = new InternationalTradeconditionLog();
				// ������־ʵ�����
				SystemLog sl = new SystemLog();
				if (internationalTradeconditionList.size() == 0
						|| internationalTradeconditionList.isEmpty()) {
					System.out.println("����");
					for (int i = 0; i < iid.length; i++) {
						// ��ѯ��Ĭ�ϵ�����
						internationalTradecondition = (InternationalTradecondition) this.commonService
								.load(InternationalTradecondition.class, iid[i]);
						// ��¼����ǰ��״̬
						itl.setCycle(internationalTradecondition.getCycle());
						itl.setRemark(internationalTradecondition.getRemark());
						itl.setTradenumber(internationalTradecondition
								.getTradenumber());
						itl.setMerno(merno);
						// �����̻���������
						internationalTradecondition.setMerchantId(merid);
						internationalTradecondition.setItemName(itemName[i]);
						internationalTradecondition.setItemno(itemno[i]);
						internationalTradecondition
								.setTradenumber(tradenumber[i]);
						internationalTradecondition.setCycle(cycle[i]);
						internationalTradecondition.setRemark(remark[i]);
						this.commonService.save(internationalTradecondition);
						merno = itl.getMerno().toString();
						flag = "1";

						// ��¼�������״̬
						itl.setCycleafter(internationalTradecondition
								.getCycle());
						itl.setRemarkafter(internationalTradecondition
								.getRemark());
						itl.setTradenumberafter(internationalTradecondition
								.getTradenumber());

						// ������־����
						sl.setUserName(getUserBean().getUserName());
						sl.setMerno(merno);
						sl.setOperTime(new Date());
						sl.setOperType("1");
						sl.setRemarks(itl.addInternationalTradecondit());
						sl.setRescReow("�����̻���������");
						this.commonService.save(sl);
					}
				} else {
					for (int i = 0; i < internationalTradeconditionList.size(); i++) {
						System.out.println("�޸�");
						InternationalTradecondition internationalTradecondition = internationalTradeconditionList
								.get(i);
						// ��¼����ǰ��״̬
						itl.setCycle(internationalTradecondition.getCycle());
						itl.setRemark(internationalTradecondition.getRemark());
						itl.setTradenumber(internationalTradecondition
								.getTradenumber());
						itl.setMerno(merno);
						// �����̻���������
						internationalTradecondition
								.setTradenumber(tradenumber[i]);
						internationalTradecondition.setCycle(cycle[i]);
						internationalTradecondition.setRemark(remark[i]);
						this.commonService.update(internationalTradecondition);

						merno = itl.getMerno().toString();
						flag = "1";

						// ��¼�������״̬
						itl.setCycleafter(internationalTradecondition
								.getCycle());
						itl.setRemarkafter(internationalTradecondition
								.getRemark());
						itl.setTradenumberafter(internationalTradecondition
								.getTradenumber());

						// ������Ӧ����
						sl.setUserName(getUserBean().getUserName());
						sl.setMerno(merno);
						sl.setOperTime(new Date());
						sl.setOperType("2");
						sl.setRemarks(itl.getUpdateInternationalTradecondit());
						sl.setRescReow("�����̻���������");
						this.commonService.save(sl);
					}
				}
			}
			return SUCCESS;
		} catch (RuntimeException e) {
			e.printStackTrace();
			flag = "2";
			return SUCCESS;
		}
	}

	public List<InternationalTradecondition> getInternationalTradeconditionList() {
		return internationalTradeconditionList;
	}

	public void setInternationalTradeconditionList(
			List<InternationalTradecondition> internationalTradeconditionList) {
		this.internationalTradeconditionList = internationalTradeconditionList;
	}

	public InternationalTradecondition getInternationalTradecondition() {
		return internationalTradecondition;
	}

	public void setInternationalTradecondition(
			InternationalTradecondition internationalTradecondition) {
		this.internationalTradecondition = internationalTradecondition;
	}

	public List<InternationalTradecondition> getBigInternationalTradeconditionList() {
		return bigInternationalTradeconditionList;
	}

	public void setBigInternationalTradeconditionList(
			List<InternationalTradecondition> bigInternationalTradeconditionList) {
		this.bigInternationalTradeconditionList = bigInternationalTradeconditionList;
	}

	public long getMerid() {
		return merid;
	}

	public void setMerid(long merid) {
		this.merid = merid;
	}

	public InternationalMerchant getMerchant() {
		return merchant;
	}

	public void setMerchant(InternationalMerchant merchant) {
		this.merchant = merchant;
	}

	public String[] getRemark() {
		return remark;
	}

	public void setRemark(String[] remark) {
		this.remark = remark;
	}

	public Long[] getTradenumber() {
		return tradenumber;
	}

	public void setTradenumber(Long[] tradenumber) {
		this.tradenumber = tradenumber;
	}

	public Long[] getCycle() {
		return cycle;
	}

	public Long[] getIid() {
		return iid;
	}

	public void setIid(Long[] iid) {
		this.iid = iid;
	}

	public void setCycle(Long[] cycle) {
		this.cycle = cycle;
	}

	public String[] getItemName() {
		return itemName;
	}

	public void setItemName(String[] itemName) {
		this.itemName = itemName;
	}

	public String getMerno() {
		return merno;
	}

	public void setMerno(String merno) {
		this.merno = merno;
	}

	public Long[] getItemno() {
		return itemno;
	}

	public void setItemno(Long[] itemno) {
		this.itemno = itemno;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

}
