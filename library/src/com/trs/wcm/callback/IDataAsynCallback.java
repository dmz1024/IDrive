/*
 *	History				Who				What
 *  2012-5-17			wuyang			Created.
 */
package com.trs.wcm.callback;

/**
 * Title: TRS 内容协作平台（TRS WCM）<BR>
 * Description: <BR>
 * Copyright: Copyright (c) 2004-2013 北京拓尔思信息技术股份有限公司<BR>
 * Company: 北京拓尔思信息技术股份有限公司(www.trs.com.cn)<BR>
 * 
 * @author wuyang
 * @version 1.0
 */
public interface IDataAsynCallback {

    /**
     * 当请求本地数据时，异步请求时间戳，如果时间戳发生变化，则调用此方法
     */
    public void onDataChanged();

	/**
	 * 异步请求时的回调方法
	 * 
	 * @param _result
	 *            一般表示返回的json字符串
	 * @param bIsChanged
	 *            表示_result相比上次请求时是否发生变化
	 */
	public void onDataLoad(String result, boolean bIsChanged);

	/**
	 * 
	 * @param result
	 */
	public void onError(String result);
}
