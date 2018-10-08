package com.ecpss.excel;

import jxl.CellFeatures;
import jxl.format.Colour;
import jxl.write.WritableCell;
import jxl.write.WritableCellFeatures;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WriteException;

/**
 * ��ExcelUtils.java��ʵ���������ṩһЩ����excel�ķ���
 * @author yepeng Aug 25, 2008 3:45:27 PM
 */
public class ExcelUtils {
	/**
	 * ������ĸ��A��B��AB��AC�����excel���кţ���0��ʼ
	 * @param pos
	 * @return
	 */
	public static int getColumn(String pos){
		int A_1 = 64;// A - 1
		String upperPos = pos.toUpperCase();
		char[] posChars = upperPos.toCharArray();
		int intPos = 0;
		for (int i = 0; i < posChars.length; i++) {
			char c = posChars[i];
			intPos = intPos + (c - A_1) * (int)Math.pow(26, posChars.length -i -1 );
		}
		return intPos - 1;
	}
	/**
	 * ��������ڣ�����WritableCellFeatures
	 * @param cell
	 */
	private static CellFeatures prepareCellFeatures(WritableCell cell){
		if(cell.getCellFeatures() == null){
			cell.setCellFeatures(new WritableCellFeatures());
		}
		return cell.getCellFeatures();
	}
	/**
	 * ���ע��
	 * @param cell
	 * @param comment
	 */
	public static WritableCell setComment(WritableCell cell,String comment){
		prepareCellFeatures(cell).setComment(comment);
		return cell;
	}
	/**
	 * ����б���ɫ�ĸ�ʽ
	 * @param cell
	 * @param comment
	 */
	public static WritableCellFormat newBackgroundCF(Colour backgroundColor){
		WritableCellFormat cellFormat = new WritableCellFormat();
		try {
			cellFormat.setBackground(backgroundColor);
		} catch (WriteException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
		return cellFormat;
	}
	/**
	 * �����������ɫ�ĸ�ʽ
	 * @param cell
	 * @param comment
	 */
	public static WritableCellFormat newFontColorCF(Colour fontColor){
		WritableFont colorFont = new WritableFont(WritableFont.ARIAL, 10);
		try {
			colorFont.setColour(fontColor);
		} catch (WriteException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
		WritableCellFormat cellFormat = new WritableCellFormat();
		cellFormat.setFont(colorFont);
		return cellFormat;
	}
	/**
	 * ����б���ɫ��������ɫ�ĸ�ʽ
	 * @param cell
	 * @param comment
	 */
	public static WritableCellFormat newGroundAndFontColorCF(Colour fontColor,Colour backgroundColor){
		WritableFont colorFont = new WritableFont(WritableFont.ARIAL, 10);
		WritableCellFormat cellFormat = new WritableCellFormat();
		try {
			colorFont.setColour(fontColor);
			cellFormat.setFont(colorFont);
			cellFormat.setBackground(backgroundColor);
		} catch (WriteException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
		
		return cellFormat;
	}
}
