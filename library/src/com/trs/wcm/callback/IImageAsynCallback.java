/*
 *	History				Who				What
 *  2012-5-17			wuyang			Created.
 */
package com.trs.wcm.callback;

import android.graphics.drawable.Drawable;

/**
 * Title: TRS 内容协作平台（TRS WCM）<BR>
 * Description: <BR>
 * Copyright: Copyright (c) 2004-2013 北京拓尔思信息技术股份有限公司<BR>
 * Company: 北京拓尔思信息技术股份有限公司(www.trs.com.cn)<BR>
 * 
 * @author wuyang
 * @version 1.0
 */
public interface IImageAsynCallback {

	public void onImageLoad(Drawable drawable, String url);

	public void onError(String url);
}
