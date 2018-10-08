package com.ecpss.util;
import java.io.File;

import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.PageOrientation;
import jxl.format.PaperSize;
import jxl.format.UnderlineStyle;
import jxl.format.VerticalAlignment;
import jxl.write.DateFormat;
import jxl.write.Label;
import jxl.write.NumberFormat;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import java.util.Date;
public class ExcelUtil {
	
	public void getBackFeeExcel(Long merchantNo, Long batchNo, Long merchantorderNo, String rorderNo, Date applyfortimes, Float orderCount, String isresultName,String isFreezeName, String isTuikuanName, String isHuakuanName, String remark){
		int count = 2;
		try {
			// ���ļ�
			WritableWorkbook book1 = Workbook.createWorkbook(new File("E:/temp/output1.xls")); 
			//  ������Ϊ����һҳ���Ĺ���������0��ʾ���ǵ�һҳ
			WritableSheet  sheet = book1.createSheet("Sheet1",0);
			//����ҳ�����
			sheet.setPageSetup(PageOrientation.LANDSCAPE.LANDSCAPE,PaperSize.A4,0.5d,0.5d);
			sheet.setColumnView(120, 120);
			sheet.setRowView(120, 120);		
			/** ����ʽ ***********************/
			Label labelthmerno = new Label(0, 0, "�̻���");
			Label labelthbatchno = new Label(7, 0, "���κ�");
			Label labelthmernoorder = new Label(0, 1, "��ˮ��");
			Label labelthmeoorder = new Label(1, 1, "������");
			Label labelthapplyfortime = new Label(2, 1, "����");
			Label labelthcount = new Label(3, 1, "���");
			Label labelthstatus = new Label(4, 1, "״̬");
			Label labelthisfreeze = new Label(5, 1, "�Ƿ񶳽�");
			Label labelthistuikuan = new Label(6, 1, "�Ƿ��˿�");
			Label labelthishuakuan = new Label(7, 1, "�Ƿ񻮿�");
			Label labelthremark = new Label(8, 1, "��ע");
			// ������õĵ�Ԫ����ӵ���������
			sheet.addCell(labelthmerno);
			sheet.addCell(labelthbatchno);
			sheet.addCell(labelthmernoorder);
			sheet.addCell(labelthmeoorder);
			sheet.addCell(labelthapplyfortime);
			sheet.addCell(labelthcount);
			sheet.addCell(labelthstatus);
			sheet.addCell(labelthisfreeze);
			sheet.addCell(labelthistuikuan);
			sheet.addCell(labelthishuakuan);
			sheet.addCell(labelthremark);
		
			try {
				 int column=0;   
				 sheet.setColumnView(column++, 15);   
				 sheet.setColumnView(column++, 20);   
				 sheet.setColumnView(column++, 10);   
				 sheet.setColumnView(column++, 10); 
				 sheet.setColumnView(column++, 10);   
				 sheet.setColumnView(column++, 10);   
				 sheet.setColumnView(column++, 10);   
				 sheet.setColumnView(column++, 10);
				 sheet.setColumnView(column++, 10); 
				
				/** ����ʽ ***********************/
				Label labelmerno = new Label(1,0,String.valueOf(merchantNo));
				Label lablebatchno =  new Label(8, 0, String.valueOf(batchNo));
				/** д���� ***********************/
				Label labelmernoorder = new Label(0, count, String.valueOf(merchantorderNo));
				Label labelmeoorder = new Label(1, count,rorderNo);
				//Label labelapplyfortime = new Label(2, 2+j, "����");
				jxl.write.DateTime applyfortime = new jxl.write.DateTime(2,count,applyfortimes);
				//Label labelcount = new Label(3, 2+j, "���");
				jxl.write.Number labelcount = new jxl.write.Number(3, count, orderCount);
				Label labelstatus = new Label(4, count, isresultName);
				Label labelisfreeze = new Label(5, count, isFreezeName);
				Label labelistuikuan = new Label(6, count, isTuikuanName);
				Label labelishuakuan = new Label(7, count, isHuakuanName);
				Label labelremark = new Label(8, count, remark);	
				// ������õĵ�Ԫ����ӵ���������
				sheet.addCell(labelmerno);
				sheet.addCell(lablebatchno);
				sheet.addCell(labelmernoorder);
				sheet.addCell(labelmeoorder);
				sheet.addCell(applyfortime);
				sheet.addCell(labelcount);
				sheet.addCell(labelstatus);
				sheet.addCell(labelisfreeze);
				sheet.addCell(labelistuikuan);
				sheet.addCell(labelishuakuan);
				sheet.addCell(labelremark);	
				count++;
			}
			catch (Exception e) {
				System.out.println(e);
			}
	
		
			//�����κŲ��뵽����
		
			book1.write();
			book1.close();
			String cmd="rundll32 url.dll FileProtocolHandler file://E:/temp/output1.xls"; 
			Process p = Runtime.getRuntime().exec(cmd); 
		
		} catch (Exception e) {
			e.printStackTrace();
			// this.addFieldError("repeat","�����Ѿ������ˣ���");
		
		}
	
	}
	
	public void getExcel(Date huaKuanTime, Long batchNo , Long merchantNo, String accountName, String bank, String cardNo, Float tradeMoney, Integer tradeNumber, Float noTuiKuanMoney, Integer noTuikuanNumber, Float tuiKuanMoney, Integer tuiKuanNumber, Float freezeMoney, Integer freezeNumber, Float thawMoney, Integer thawNumber, Float procedureRate, Float procedureFee,Float countMoney, String depict, String remark)throws Exception{
		
			File tempFile=new File("e:/temp/output.xls");
			//File tempFile=new File("huaKuan.xls");
			WritableWorkbook workbook = Workbook.createWorkbook(tempFile);   
			WritableSheet sheet = workbook.createSheet("TestCreateExcel", 0);    
			  
			//һЩ��ʱ����������д��excel��   
			Label l=null;   
			jxl.write.Number n=null;   
			jxl.write.DateTime d=null;   
			
			//������������
			WritableFont BoldFont  =   new  WritableFont(WritableFont.ARIAL,  14 , WritableFont.BOLD);  
			WritableFont headerFont = new WritableFont(WritableFont.ARIAL, 12, WritableFont.BOLD, 
			    false, UnderlineStyle.NO_UNDERLINE, jxl.format.Colour.BLUE);    
			WritableCellFormat headerFormat = new WritableCellFormat (headerFont);    
			   
			WritableFont titleFont = new WritableFont(WritableFont.ARIAL, 14, WritableFont.NO_BOLD,
			     false, UnderlineStyle.NO_UNDERLINE, jxl.format.Colour.RED);   
			WritableCellFormat titleFormat = new WritableCellFormat (titleFont);    
			   
			WritableFont detFont = new WritableFont(WritableFont.ARIAL, 10, WritableFont.NO_BOLD, 
			    false, UnderlineStyle.NO_UNDERLINE, jxl.format.Colour.BLACK);    
			WritableCellFormat detFormat = new WritableCellFormat (detFont);    
			NumberFormat nf=new NumberFormat("0.00000"); //����Number�ĸ�ʽ   
			WritableCellFormat priceFormat = new WritableCellFormat (detFont, nf);    
			   
			DateFormat df=new DateFormat("yyyy-MM-dd");//�������ڵ�   
			WritableCellFormat dateFormat = new WritableCellFormat (detFont, df);    
			   
			//���ڱ���    
			WritableCellFormat wcf_title  =   new  WritableCellFormat(BoldFont);   
	        wcf_title.setBorder(Border.NONE, BorderLineStyle.THIN);  //  ����    
	        wcf_title.setVerticalAlignment(VerticalAlignment.CENTRE);  //  ��ֱ����    
	        wcf_title.setAlignment(Alignment.CENTRE);  //  ˮƽ����    
	        wcf_title.setWrap( true );  
			
	        WritableFont NormalFont  =   new  WritableFont(WritableFont.ARIAL,  13 ); 
			WritableCellFormat wcf_merge  =   new  WritableCellFormat(NormalFont);   
		    wcf_merge.setBorder(Border.ALL, BorderLineStyle.THIN);  //  ����    
		    wcf_merge.setVerticalAlignment(VerticalAlignment.CENTRE);  //  ��ֱ����    
		    wcf_merge.setAlignment(Alignment.LEFT);   
		    wcf_merge.setWrap( true );  
			 //ʣ�µ����飬��������������ݺ͸�ʽ����һЩ��Ԫ���ټӵ�sheet��   
	       
	        //����
			 sheet.addCell( new  Label( 0 ,  0 ,  " Ԥ�����ɻ���� " ,   wcf_title));
			 //ʵ�ֿ���
			 sheet.mergeCells( 0 ,  0 , 3 , 0 ); 
			 //���ø߶�
			 sheet.setRowView( 0 ,  1000 ,  false ); 
			 //add Title   
			 int column=0;   
			 int i=0;  
			 column=0;
			 l=new Label(column++, 1, "����ʱ�䣺"+huaKuanTime+"                                     ���κţ�"+batchNo+"", detFormat);
			 //���ø߶�
			 sheet.setRowView( 1 ,  500 ,  false ); 
			 //ʵ�ֿ���
			 sheet.mergeCells( 0 ,  1 , 3 , 1 );   
			 sheet.addCell(l);
			 /**
			  * �̻���
			  */
			 column=0;
			 l=new Label(column++, 2, "�̻���", wcf_merge);
			 sheet.addCell(l);   
			 n=new jxl.write.Number(column++, 2, merchantNo, wcf_merge);  
			 sheet.addCell(n);   
			 //ʵ�ֿ���
			 sheet.mergeCells( 1 ,  2 , 3 , 2 ); 
			 //���ø߶�
			 sheet.setRowView( 2 ,  600 ,  false );
			 
			 /**
			  *�տ��� 
			  */
			 column=0;   
			 l=new Label(column++, i+3, "�տ���", wcf_merge);   
			 //ʵ�ֿ���
			 sheet.mergeCells( 0 ,  3 , 0 , 5 ); 
			 sheet.addCell(l);   
			 //���ø߶�
			 sheet.setRowView( 3 ,  600 ,  false );
			 
			 l=new Label(column++, i+3, "������", wcf_merge);   
			 sheet.addCell(l);   
			 l=new Label(column++, i+3, accountName, wcf_merge);   
			 sheet.addCell(l);   
			 //ʵ�ֿ���
			 sheet.mergeCells( 2 ,  3 , 3 , 3 ); 
			 //���ø߶�
			 sheet.setRowView( 3 ,  600 ,  false );
	 
			 i++;
			 column=1;   
			 l=new Label(column++, i+3, "������ ", wcf_merge);   
			 sheet.addCell(l);   
			 l=new Label(column++, i+3, bank, wcf_merge);   
			 sheet.addCell(l);      
			 //ʵ�ֿ���
			 sheet.mergeCells( 2 ,  4 , 3 , 4 ); 
			 //���ø߶�
			 sheet.setRowView( 4 ,  600 ,  false );
			 
			 i++;   
			 column=1;   
			 l=new Label(column++, i+3, "�ʺ� ", wcf_merge);   
			 sheet.addCell(l);   
			 l=new Label(column++, i+3, cardNo, wcf_merge);   
			 sheet.addCell(l);   
			 //ʵ�ֿ���
			 sheet.mergeCells( 2 ,  5 , 3 , 5 ); 
			 //���ø߶�
			 sheet.setRowView( 5 ,  600 ,  false );

			 /**
			  * ��Ŀ
			  */
			
			 i++;
			 column=0;   
			 l=new Label(column++, i+3, "��Ŀ", wcf_merge);   
			 sheet.addCell(l); 
			 column=3;
			 l=new Label(column++, i+3, "��ע", wcf_merge);   
			 sheet.addCell(l); 
			 //���ø߶�
			 sheet.setRowView( 6 ,  600 ,  false );

			 /**
			  * ���ڽ��㽻��
			  */
			
			 i++;
			 column=0;   
			 l=new Label(column++, i+3, "���ڽ��㽻��", wcf_merge);   
			 //ʵ�ֿ���
			 sheet.mergeCells( 0 ,  7 , 0 , 8 );
			 sheet.addCell(l); 
			 //���ø߶�
			 sheet.setRowView( 7 ,  600 ,  false  );
			 
			 /*����*/   
			 l=new Label(column++, i+3, "����", wcf_merge);   
			 //ʵ�ֿ���
			 sheet.mergeCells(1 ,  7 , 1 , 7 );
			 sheet.addCell(l); 
			 n=new jxl.write.Number(column++, i+3, tradeNumber, wcf_merge); 
			 sheet.addCell(n); 
			 l=new Label(column++, i+3, "", wcf_merge);   
			 sheet.addCell(l); 
			 
			 //���ø߶�
			 sheet.setRowView( 8 ,  600 ,  false  );
			 
			 /*���*/ 
			 i++;
			 column=1;  
			 l=new Label(column++, i+3, "���", wcf_merge);   
			 //ʵ�ֿ���
			 sheet.mergeCells(1 ,  8 , 1 , 8 );
			 sheet.addCell(l); 
			 n=new jxl.write.Number(column++, i+3, tradeMoney, wcf_merge); 
			 sheet.addCell(n); 
			 l=new Label(column++, i+3, "", wcf_merge);   
			 sheet.addCell(l); 
			 //���ø߶�
			 sheet.setRowView( 9 ,  600 ,  false  );
			 
			 /**
			  * δ���㱾���˿�
			  */
			 
			 i++;
			 column=0;   
			 l=new Label(column++, i+3, "δ���㱾���˿�", wcf_merge);   
			 //ʵ�ֿ���
			 sheet.mergeCells( 0 ,  9 , 0 , 10 );
			 sheet.addCell(l); 
			 //���ø߶�
			 sheet.setRowView( 10 ,  600 ,  false  );
			 
			 
			 /*����*/   
			 l=new Label(column++, i+3, "����", wcf_merge);   
			 //ʵ�ֿ���
			 sheet.mergeCells(1 ,  9 , 1 , 9 );
			 sheet.addCell(l); 
			 if(noTuikuanNumber!=null){
				 n=new jxl.write.Number(column++, i+3, noTuikuanNumber, wcf_merge); 
			 }else{
				 n=new jxl.write.Number(column++, i+3, 0, wcf_merge); 
			 }
			 sheet.addCell(n); 
			 l=new Label(column++, i+3, "", wcf_merge);   
			 sheet.addCell(l); 
			 //���ø߶�
			 sheet.setRowView( 10 ,  600 ,  false  );
			 
			 /*���*/ 
			 i++;
			 column=1;  
			 l=new Label(column++, i+3, "���", wcf_merge);   
			 //ʵ�ֿ���
			 sheet.mergeCells(1 ,  10 , 1 , 10 );
			 sheet.addCell(l); 
			 if(noTuiKuanMoney!=null){
				 n=new jxl.write.Number(column++, i+3, noTuiKuanMoney, wcf_merge); 
			 }else{
				 n=new jxl.write.Number(column++, i+3, 0, wcf_merge); 
			 }
			
			 sheet.addCell(n); 
			 l=new Label(column++, i+3, "", wcf_merge);   
			 sheet.addCell(l); 
			 //���ø߶�
			 sheet.setRowView( 11 ,  600 ,  false  );
			 
			 /**
			  * �Ѿ����㱾���˿�
			  */		 
			 i++;
			 column=0;   
			 l=new Label(column++, i+3, "�Ѿ��㱾���˿�", wcf_merge);   
			 //ʵ�ֿ���
			 sheet.mergeCells( 0 ,  11 , 0 , 12 );
			 sheet.addCell(l); 
			 //���ø߶�
			 sheet.setRowView( 12 ,  600 ,  false  );
			 
			 
			 /*����*/   
			 l=new Label(column++, i+3, "����", wcf_merge);   
			 //ʵ�ֿ���
			 sheet.mergeCells(1 ,  113 , 1 , 11 );
			 sheet.addCell(l); 
			 if(tuiKuanNumber!=null){
				 n=new jxl.write.Number(column++, i+3, tuiKuanNumber, wcf_merge); 
			 }else{
				 n=new jxl.write.Number(column++, i+3, 0, wcf_merge); 
			 }
			
			 sheet.addCell(n); 
			 l=new Label(column++, i+3, "", wcf_merge);   
			 sheet.addCell(l); 
			 //���ø߶�
			 sheet.setRowView( 12 ,  600 ,  false  );
			 
			 /*���*/ 
			 i++;
			 column=1;  
			 l=new Label(column++, i+3, "���", wcf_merge);   
			 //ʵ�ֿ���
			 sheet.mergeCells(1 ,  12 , 1 , 12 );
			 sheet.addCell(l); 
			 if(tuiKuanMoney!=null){
				 n=new jxl.write.Number(column++, i+3, tuiKuanMoney, wcf_merge); 
			 }else{
				 n=new jxl.write.Number(column++, i+3, 0, wcf_merge); 
			 }
			 
			 sheet.addCell(n); 
			 l=new Label(column++, i+3, "", wcf_merge);   
			 sheet.addCell(l); 
			 //���ø߶�
			 sheet.setRowView( 13 ,  600 ,  false  );
			 
			 /**
			  * �Ѿ����㱾�ڶ���
			  */		 
			 i++;
			 column=0;   
			 l=new Label(column++, i+3, "�Ѿ��㱾�ڶ���", wcf_merge);   
			 //ʵ�ֿ���
			 sheet.mergeCells( 0 ,  13 , 0 , 14 );
			 sheet.addCell(l); 
			 //���ø߶�
			 sheet.setRowView( 14 ,  600 ,  false  );
			 
			 
			 /*����*/   
			 l=new Label(column++, i+3, "����", wcf_merge);   
			 //ʵ�ֿ���
			 sheet.mergeCells(1 ,  13 , 1 , 13 );
			 sheet.addCell(l); 
			 if(tuiKuanNumber!=null){
				 n=new jxl.write.Number(column++, i+3, tuiKuanNumber, wcf_merge); 
			 }else{
				 n=new jxl.write.Number(column++, i+3, 0, wcf_merge); 
			 }
			 
			 sheet.addCell(n); 
			 l=new Label(column++, i+3, "", wcf_merge);   
			 sheet.addCell(l); 
			 //���ø߶�
			 sheet.setRowView( 14 ,  600 ,  false  );
			 
			 /*���*/ 
			 i++;
			 column=1;  
			 l=new Label(column++, i+3, "���", wcf_merge);   
			 //ʵ�ֿ���
			 sheet.mergeCells(1 ,  14 , 1 , 14 );
			 sheet.addCell(l); 
			 if(freezeMoney!=null){
				 n=new jxl.write.Number(column++, i+3, freezeMoney, wcf_merge); 
			 }else{
				 n=new jxl.write.Number(column++, i+3, 0, wcf_merge); 
			 }
			
			 sheet.addCell(n); 
			 l=new Label(column++, i+3, "", wcf_merge);   
			 sheet.addCell(l); 
			 //���ø߶�
			 sheet.setRowView( 15 ,  600 ,  false  );
			 
			 /**
			  * ���ύ��
			  */		 
			 i++;
			 column=0;   
			 l=new Label(column++, i+3, " ���ύ��", wcf_merge);   
			 //ʵ�ֿ���
			 sheet.mergeCells( 0 ,  15 , 0 , 16 );
			 sheet.addCell(l); 
			 
			 //���ø߶�
			 sheet.setRowView( 16 ,  600 ,  false  );
			 
			 
			 /*����*/   
			 l=new Label(column++, i+3, "����", wcf_merge);   
			 //ʵ�ֿ���
			 sheet.mergeCells(1 ,  15 , 1 , 15 );
			 sheet.addCell(l); 
			 if(thawNumber!=null){
				 n=new jxl.write.Number(column++, i+3, thawNumber, wcf_merge); 
			 }else{
				 n=new jxl.write.Number(column++, i+3, 0, wcf_merge); 
			 }
			 
			 sheet.addCell(n); 
			 l=new Label(column++, i+3, "", wcf_merge);   
			 sheet.addCell(l); 
			 //���ø߶�
			 sheet.setRowView( 16 ,  600 ,  false  );
			 
			 /*���*/ 
			 i++;
			 column=1;  
			 l=new Label(column++, i+3, "���", wcf_merge);   
			 //ʵ�ֿ���
			 sheet.mergeCells(1 ,  16 , 1 , 16 );
			 sheet.addCell(l); 
			 if(thawMoney!=null){
				 n=new jxl.write.Number(column++, i+3, thawMoney, wcf_merge); 
			 }else{
				 n=new jxl.write.Number(column++, i+3, 0, wcf_merge); 
			 }
			
			 sheet.addCell(n); 
			 l=new Label(column++, i+3, "", wcf_merge);   
			 sheet.addCell(l); 
			 //���ø߶�
			 sheet.setRowView( 17 ,  600 ,  false  );
			 
			 /**
			  * ��������
			  */
			 i++;
			 column=0;  
			 l=new Label(column++, i+3, "��������", wcf_merge);   
			 sheet.addCell(l); 
			 n=new jxl.write.Number(column++, i+3, procedureRate, wcf_merge);   
			 sheet.addCell(n); 
			 column=3;  
			
			 //ʵ�ֿ���
			 sheet.mergeCells(1 ,  17 , 2 , 17 );
			 l=new Label(column++, i+3, "", wcf_merge);   
			 sheet.addCell(l); 
			 //���ø߶�
			 sheet.setRowView( 18 ,  600 ,  false  );
			 /**
			  * ������
			  */
			 i++;
			 column=0;  
			 l=new Label(column++, i+3, "������", wcf_merge);   
			 sheet.addCell(l); 
			 n=new jxl.write.Number(column++, i+3, procedureFee, wcf_merge);  
			 sheet.addCell(n);
			 column=3;  
			 //ʵ�ֿ���
			 sheet.mergeCells(1 ,  18 , 2 , 18 );
			 l=new Label(column++, i+3, "", wcf_merge);   
			 sheet.addCell(l); 
			 //���ø߶�
			 sheet.setRowView( 19 ,  600 ,  false  );

			 
			 /**
			  * Ӧ������
			  */
			 i++;
			 column=0;  
			 l=new Label(column++, i+3, "Ӧ������(�����)", wcf_merge);   
			 sheet.addCell(l); 
			 n=new jxl.write.Number(column++, i+3, countMoney, wcf_merge);  
			 sheet.addCell(n);
			 //ʵ�ֿ���
			 sheet.mergeCells(1 ,  19 ,3 , 19 ); 
			 /**
			  * ����
			  */
			 i++;
			 column=0;  
			 l=new Label(column++, i+3, "����", wcf_merge);   
			 sheet.addCell(l); 
			 if(depict!=null){
				 l=new Label(column++, i+3, depict, wcf_merge); 
			 }else{
				 l=new Label(column++, i+3, "", wcf_merge); 
			 }
			 
			 
			 //ʵ�ֿ���
			 
			 sheet.mergeCells(1 ,  20 , 3 , 20 );
			 sheet.addCell(l);
			 //���ø߶�
			 sheet.setRowView( 20 ,  800 ,  false  );
			 
			 //�����еĿ��   
			 column=0;   
			 sheet.setColumnView(column++, 22);   
			 sheet.setColumnView(column++, 15);   
			 sheet.setColumnView(column++, 30);   
			 sheet.setColumnView(column++, 20);   
			 workbook.write();   
			 workbook.close();   
			 //�Զ���Excel�ĵ�
			 String cmd="rundll32 url.dll FileProtocolHandler file://e:/temp/output.xls"; 
			 Process p = Runtime.getRuntime().exec(cmd); 

	}
}
