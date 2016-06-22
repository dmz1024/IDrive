package com.ccpress.izijia.vo;

import java.util.List;

/**
 * 
 * @Des: 我的线路添加照片
 * @author Rhino 
 * @version V1.0 
 * @created  2015年7月9日 上午10:10:15
 */
public class AddPhotoVo {
	
	private String time;
	
	/**数量为固定的三张*/
	private List<photo> photos;
	
	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public List<photo> getPhotos() {
		return photos;
	}

	public void setPhotos(List<photo> photos) {
		this.photos = photos;
	}

	public static class photo{
		private String path;

		public String getPath() {
			return path;
		}

		public void setPath(String path) {
			this.path = path;
		}
	}
}
