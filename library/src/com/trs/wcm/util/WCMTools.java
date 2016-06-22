/*
 *	History				Who				What
 *  2013-8-15			huxiejin		reated.
 */
package com.trs.wcm.util;

import com.trs.util.StringUtil;

/**
 * 本类处理与WCM服务器模板发布相关一些规则的解析类，如：根据图片URL获取指定大小的图片URL，根据URL获取指定分页的URL等
 * 
 * @author huxiejin
 */
public class WCMTools {

	/**
	 * 根据传入的图片url地址，获取指定大小的图片url地址<br />
	 * 如：<br />
	 * 参数http://219.151.34.68/pub/vtibetmobile/xw/W4827409377394_100.jpg <br />
	 * 获取宽度为300的图片URL的结果为：<br />
	 * http://219.151.34.68/pub/vtibetmobile/xw/W4827409377394_300.jpg<br />
	 * 
	 * 参数http://219.151.34.68/pub/vtibetmobile/xw/W4827409377394.jpg <br />
	 * 获取宽度为300的图片URL的结果为：<br />
	 * http://219.151.34.68/pub/vtibetmobile/xw/W4827409377394_300.jpg
	 * 
	 * 
	 * @param imageUrl
	 *            图片的URL地址，可以是指定大小的地址，也可以是原始图片的url地址
	 * 
	 * @param imageSize
	 *            指定的宽度
	 * @return
	 */
	public static String getSpecifiedImageURL(String imageUrl, int imageSize) {

		if (StringUtil.isEmpty(imageUrl)) {
			return "";
		}

		return imageUrl.replaceFirst("(?:_[\\d]+)?(\\.[a-zA-Z]+)$", "_"
				+ imageSize + "$1");
	}

	/**
	 * 根据传入的图片url地址，获取原始图片url地址<br />
	 * 如：<br />
	 * 参数http://219.151.34.68/pub/vtibetmobile/xw/W4827409377394_100.jpg <br />
	 * 获取的原始图片URL的结果为：<br />
	 * http://219.151.34.68/pub/vtibetmobile/xw/W4827409377394.jpg<br />
	 * 
	 * 参数http://219.151.34.68/pub/vtibetmobile/xw/W4827409377394.jpg <br />
	 * 获取的原始图片URL的结果为：<br />
	 * http://219.151.34.68/pub/vtibetmobile/xw/W4827409377394.jpg
	 * 
	 * 
	 * @param specifiedURL
	 *            图片的URL地址，可以是指定大小的地址，也可以是原始图片的url地址
	 * 
	 * @return
	 */
	public static String getOriginalImageURL(String specifiedURL) {

		if (StringUtil.isEmpty(specifiedURL)) {
			return specifiedURL;
		}

		return specifiedURL.replaceFirst("(?:_[\\d]+)?(\\.[a-zA-Z]+)$", "$1");
	}

	public static String getPageURL(String url) {
		if (url.endsWith("/")) {
			url += "documents.json";
		}
		return url;
	}

	/**
	 * 根据传入的URL地址，获取指定页的URL地址，如：<br />
	 * 第1页URL：http://wcm7.trs.cn/pub/mywebsite/a/documents.json <br />
	 * 第2页URL：http://wcm7.trs.cn/pub/mywebsite/a/documents_1.json <br />
	 * 第3页URL：http://wcm7.trs.cn/pub/mywebsite/a/documents_2.json <br />
	 * 
	 * @param url
	 * @param pageIndex
	 * @return
	 */
	public static String getPageURL(String url, int pageIndex) {
		int position = url.lastIndexOf("/");
		String preOfUrl = url.substring(0, position);
		String fileName = "documents_" + pageIndex + ".json";
		return preOfUrl + "/" + fileName;
	}

}
