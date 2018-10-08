package com.ecpss.web;

import java.io.Serializable;

/**
 * ��ҳ����. ������ǰҳ���ݼ���ҳ��Ϣ���ܼ�¼��.
 * 
 */
@SuppressWarnings("unchecked")
public class PageInfo implements Serializable {
	private static final long	serialVersionUID	= 6796458141843445623L;

	private int					curPage;

	private int					totalCount;								// �ܼ�¼��

	private int					pageSize;									// ÿҳ�ļ�¼��

	private Object				data;										// ��ǰҳ�д�ŵļ�¼,����һ��ΪList

	private static int			ROWS_PER_PAGE		= 20;

	public PageInfo() {
		curPage = 1;
		totalCount = 0;
		pageSize = ROWS_PER_PAGE;
	}

	/**
	 * ȡ�ܼ�¼��.
	 */
	public long getTotalCount() {
		return this.totalCount;
	}

	/**
	 * �����ܼ�¼��.
	 */
	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	/**
	 * ȡ��ǰҳ�еļ�¼.
	 */
	public Object getResult() {
		return data;
	}

	/**
	 * ȡ��ǰҳ�еļ�¼.
	 */
	public void setResult(Object data) {
		this.data=data;
	}

	/**
	 * ȡ��ҳ��.
	 */
	public int getTotalPageCount() {
		if (totalCount % pageSize == 0)
			return totalCount / pageSize;
		else
			return totalCount / pageSize + 1;
	}

	public void setCurPage(int curPage) {
		this.curPage = curPage;
	}

	public int getCurPage() {
		return curPage;
	}

	public void setPageSize(int rows) {
		if (rows > 0) {
			pageSize = rows;
		} else {
			pageSize = ROWS_PER_PAGE;
		}
	}

	public int getPageSize() {
		return pageSize;
	}

	public int getDefaultRowsPerPage() {
		return ROWS_PER_PAGE;
	}
	/**
	 * ��ȡ��һҳ��һ�����������ݼ���λ��.
	 * 
	 * @param pageNo
	 *            ��1��ʼ��ҳ��
	 * @param pageSize
	 *            ÿҳ��¼����
	 * @return ��ҳ��һ������
	 */
	public int getStartOfPage() {
		return (curPage - 1) * pageSize;
	}
	public static void main(String[] args){
		PageInfo pi=new PageInfo();
		pi.setTotalCount(21);
		pi.setCurPage(2);
		System.out.println(pi.getStartOfPage());
		System.out.println(pi.getPageSize());
	}
}