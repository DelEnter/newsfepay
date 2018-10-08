package com.ecpss.action.refund;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jxl.Workbook;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ecpss.action.BaseAction;
import com.ecpss.excel.builder.RowResult;
import com.ecpss.excel.builder.jexcel.JExcelRowObjectBuilder;
import com.ecpss.excel.rule.impl.CellRuleImpl;
import com.ecpss.excel.rule.impl.RowRuleImpl;
import com.ecpss.model.refund.InternationalRefundHistory;
import com.ecpss.service.iservice.ImportExcelService;
import com.ecpss.vo.ImportRefundHistory;
import com.ecpss.web.PageInfo;


public class RefundCompareAction extends BaseAction{
	@Autowired
	@Qualifier("importExcelService")
	private ImportExcelService importExcelService;
	
	private File fileName;   //�ϴ����ļ�
	private String filesname;
	private PageInfo info = new PageInfo();
	private List<InternationalRefundHistory> rhList;
	private List batchnoList = new ArrayList();
	private String cardNo;
	private Long rhid;
	private String batchnos;
	/**
	 * ��������е��˿�excel����
	 * @return
	 */
	public String importRefundByBank(){
		try {
			
			if(importExcelService.getFileNameByRefund(filesname)){
				if(fileName!=null){
					Long begin=System.currentTimeMillis();
					int count=0;
					SimpleDateFormat time1=new SimpleDateFormat("yyMMddHHmmss");
					String batchno = time1.format(new Date());
					if(filesname.endsWith(".txt")){
						//��ȡ.txt�˿��ļ�
						InputStreamReader is = new InputStreamReader( new FileInputStream(fileName));
						BufferedReader in = new BufferedReader(is);
						String line = in.readLine();
						line = in.readLine();       // ��ȡ��һ��
				        while (line != null) {          // ��� line Ϊ��˵��������
				            if(line.length()>40){
				            	//line.substring(28, 44)  //����
				            	//line.substring(52, 64)  //���׽��
				            	//line.substring(80, 92)  //�˿���
				            	//line.substring(98, 104)  //��������
				            	//line.substring(19, 27) //�ն˺�
				            	//line.substring(92, 98)  //��Ȩ��
								InternationalRefundHistory r = new InternationalRefundHistory();
								r.setCardNo(line.substring(28, 44));
								r.setAuthorizationNo(line.substring(92, 98));
								r.setRefundAmount(Double.valueOf(line.substring(80, 92).trim())/100);
								r.setTerminalNo(line.substring(19, 27));
								r.setTradeTime(line.substring(98, 104));
								r.setTradeAmount(Double.valueOf(line.substring(52, 64).trim())/100);
								r.setFileName(filesname);
								r.setPhone(batchno);
								this.commonService.save(r);
								count++;
				            }
				            line = in.readLine();   // ��ȡ��һ��
				        }
				        is.close();
				        in.close();
					}else{
						Workbook workBook=Workbook.getWorkbook(fileName);
						RowRuleImpl rowRule = new RowRuleImpl();
						rowRule.addCellRule(new CellRuleImpl("A", "cardNo"));
						rowRule.addCellRule(new CellRuleImpl("B", "tradeAmount"));
						rowRule.addCellRule(new CellRuleImpl("C", "refundAmount"));
						rowRule.addCellRule(new CellRuleImpl("D", "tradeTime"));
						rowRule.addCellRule(new CellRuleImpl("E", "terminalNo"));
						rowRule.addCellRule(new CellRuleImpl("F", "authorizationNo"));
						JExcelRowObjectBuilder reveBuilder = new JExcelRowObjectBuilder();
						reveBuilder.setSheet(workBook.getSheet(0));
						reveBuilder.setTargetClass(ImportRefundHistory.class);
						reveBuilder.setRule(1, workBook.getSheet(0).getRows(), rowRule);
						
						RowResult<ImportRefundHistory>[] cons = reveBuilder.parseExcel();
						ImportRefundHistory rh;
						for (RowResult<ImportRefundHistory> rowResult : cons) {
							rh = rowResult.getRowObject();
							
							if(rh.getRefundAmount().length()>=rh.getRefundAmount().indexOf(".")+3){
								int refundAmountBy = rh.getRefundAmount().indexOf(".");
								rh.setRefundAmount(rh.getRefundAmount().substring(0,refundAmountBy+3));
							}else{
								rh.setRefundAmount(rh.getRefundAmount()+"0000");	
								int refundAmountBy = rh.getRefundAmount().indexOf(".");
								rh.setRefundAmount(rh.getRefundAmount().substring(0,refundAmountBy+3));
							}	
							System.out.println(rh.getRefundAmount());
							if(rh!=null){
								count++;
								this.importExcelService.saveRefundByBank(rh,filesname,batchno);
							}
						}
					}
					Long end=System.currentTimeMillis() - begin;
					messageAction = "�ļ�����ɹ�����ʱ:"+end+"����.�ϴ���"+count+"��.";
				}
			}else{
				this.messageAction = "���ļ����ϴ���.";
			}
			return SUCCESS;
		} catch (Exception e) {
			// TODO: handle exception
			messageAction="�����쳣.";
			e.printStackTrace();
			return SUCCESS;
		}
	}

	/**
	 * ��ѯ�б���ʾ
	 * @return
	 */
	public String refundByBankQuery(){
		StringBuffer sb = new StringBuffer();
		sb.append("select rh from InternationalRefundHistory rh where 1=1 ");
		if(StringUtils.isNotBlank(cardNo)){
			sb.append(" and rh.cardNo like '%"+cardNo.trim()+"%'");
		}
		if(StringUtils.isNotBlank(batchnos)){
			sb.append(" and rh.phone like '"+batchnos.trim()+"%'");
		}
		sb.append(" order by rh.tradeTime desc,rh.cardNo");
		info = this.commonService.listQueryResultByHql(sb.toString(), info);
		///��ѯ���ظ���
		StringBuffer sb1 = new StringBuffer();
		sb1.append("select a from InternationalRefundHistory a where 1=1  ");
		if(StringUtils.isNotBlank(cardNo)){
			sb1.append(" and a.cardNo like '%"+cardNo.trim()+"%'");
		}
		if(StringUtils.isNotBlank(batchnos)){
			sb1.append(" and a.phone like '%"+batchnos.trim()+"%'");
		}
		sb1.append(
				" and (a.cardNo,a.authorizationNo) in  " +
				"(select cardNo,authorizationNo " +
					"from InternationalRefundHistory " + 
				"group by cardNo,authorizationNo having count(*) > 1) order by a.cardNo,a.tradeTime");
		rhList = this.commonService.list(sb1.toString());
		
		String hql="select rh.phone from InternationalRefundHistory rh where rh.phone!=null group by rh.phone";
		batchnoList = this.commonService.list(hql);
		return SUCCESS;
	}
	
	
	/**
	 * �ɵ��ظ���
	 * @return
	 */
	public String deleteDouble(){
		this.commonService.delete(InternationalRefundHistory.class, rhid);
		return refundByBankQuery();
	}
	public File getFileName() {
		return fileName;
	}

	public void setFileName(File fileName) {
		this.fileName = fileName;
	}

	public PageInfo getInfo() {
		return info;
	}

	public void setInfo(PageInfo info) {
		this.info = info;
	}

	public List<InternationalRefundHistory> getRhList() {
		return rhList;
	}

	public void setRhList(List<InternationalRefundHistory> rhList) {
		this.rhList = rhList;
	}

	public String getFilesname() {
		return filesname;
	}

	public void setFilesname(String filesname) {
		this.filesname = filesname;
	}

	public String getCardNo() {
		return cardNo;
	}

	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}

	public Long getRhid() {
		return rhid;
	}

	public void setRhid(Long rhid) {
		this.rhid = rhid;
	}

	
	public List getBatchnoList() {
		return batchnoList;
	}

	public void setBatchnoList(List batchnoList) {
		this.batchnoList = batchnoList;
	}

	public String getBatchnos() {
		return batchnos;
	}

	public void setBatchnos(String batchnos) {
		this.batchnos = batchnos;
	}
}
