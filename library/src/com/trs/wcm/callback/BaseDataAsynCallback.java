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
public abstract class BaseDataAsynCallback implements IDataAsynCallback {

    @Override
    public void onDataChanged() {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * @see com.trs.wcm.callback.IDataAsynCallback#onDataLoad(java.lang.String,
     * boolean)
     */
    @Override
    public void onDataLoad(String _result, boolean bIsChanged) {
        this.onDataLoad(_result);
    }

    public void onDataLoad(String _result) {

    }

    @Override
    public void onError(String url) {

    }

}
