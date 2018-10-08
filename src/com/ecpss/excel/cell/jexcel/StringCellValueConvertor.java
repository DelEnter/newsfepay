package com.ecpss.excel.cell.jexcel;
import jxl.Cell;

import com.ecpss.excel.cell.CellValueConvertor;

/**
 * ��SimpleJExcelCellValue.java��ʵ�����������stringֵ
 * @author yepeng Jun 30, 2008 6:54:27 PM
 */
public class StringCellValueConvertor implements CellValueConvertor<String,Cell> {

	public String getCellValue(Cell cell) {
		if(cell == null){
			return null;
		}
		return cell.getContents();
	}
}
