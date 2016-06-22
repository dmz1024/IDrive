package com.ccpress.izijia.vo;

import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.Expose;

import android.graphics.Bitmap;

/**
 * 线路详情
 * 
 * @author wangyi
 * 
 */
public class LineDetailVo implements Serializable {

	private Summary summary;

	private List<Travel> travel;

	private List<BespokeVo> viewspot;

	public Summary getSummary() {
		return summary;
	}

	public void setSummary(Summary summary) {
		this.summary = summary;
	}

	public List<Travel> getTravel() {
		return travel;
	}

	public void setTravel(List<Travel> travel) {
		this.travel = travel;
	}

	public List<BespokeVo> getViewspot() {
		return viewspot;
	}

	public void setViewspot(List<BespokeVo> viewspot) {
		this.viewspot = viewspot;
	}

	public static class Summary implements Serializable{
		private String desc;
		private String image;
		private String lid;
		private String line;
		private String title;
		private String type;
		private String url;

		public String getDesc() {
			return desc;
		}

		public void setDesc(String desc) {
			this.desc = desc;
		}

		public String getImage() {
			return image;
		}

		public void setImage(String image) {
			this.image = image;
		}

		public String getLid() {
			return lid;
		}

		public void setLid(String lid) {
			this.lid = lid;
		}

		public String getLine() {
			return line;
		}

		public void setLine(String line) {
			this.line = line;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

	}

	public static class Travel implements Serializable{
		private String date;
		private String title;

		private List<Images> images;

		public String getDate() {
			return date;
		}

		public void setDate(String date) {
			this.date = date;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public List<Images> getImages() {
			return images;
		}

		public void setImages(List<Images> images) {
			this.images = images;
		}

		public static class Images implements Serializable{

			private String desc;
			private String image;
			private String id;
			@Expose
			private Bitmap bitmap;
			
			@Expose
			private Long date;
			
			@Expose
			private String imagePath;
			
			public String getImagePath() {
				return imagePath;
			}

			public void setImagePath(String imagePath) {
				this.imagePath = imagePath;
			}

			public Bitmap getBitmap() {
				return bitmap;
			}

			public void setBitmap(Bitmap bitmap) {
				this.bitmap = bitmap;
			}

			public String getId() {
				return id;
			}

			public void setId(String id) {
				this.id = id;
			}

			public String getDesc() {
				return desc;
			}

			public void setDesc(String desc) {
				this.desc = desc;
			}

			public Long getDate() {
				return date;
			}

			public void setDate(Long date) {
				this.date = date;
			}

			public String getImage() {
				return image;
			}

			public void setImage(String image) {
				this.image = image;
			}

		}
	}

}
