package com.ecpss.excel.rule;

import com.ecpss.excel.cell.CellValueConvertor;

/**
 * ��CellRule.java��ʵ��������
 * @author xurong Jun 30, 2008 8:22:03 PM
 */
public interface CellRule {
	public int getColumn();
	public String getProperty();
	public CellValueConvertor getCellValueConvertor();
}
