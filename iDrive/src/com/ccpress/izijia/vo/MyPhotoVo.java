package com.ccpress.izijia.vo;

import java.util.List;

/**
 * 我的照片
 * @author wangyi
 *
 */
public class MyPhotoVo {
	private String uid;
	private String tags;
	private String id;
	private String cover;
	private String title;
	private String desc;
	private String reply_count;
	private String status;
	private String create_time;
	private String pic_num;
	private String zan_num;
	private String type;
	
	private List<Pic>  pics;
	
	public static class Pic{
		private String uid;
		private String sort;
		private String updatetime;
		private String reply_count;
		private String status;
		private String pic_exif_lng;
		private String group_id;
		private String album_uid;
		private String upload_lng;
		private String pic_path;
		private String type;
		private String upload_address;
		private String createtime;
		private String id;
		private String pic_exif_time;
		private String upload_lat;
		private String pic_desc;
		private String pic_exif_lat;
		private String zan_num;
		private String like_num;
		private String upload_ip;
		
		public String getUid() {
			return uid;
		}
		public void setUid(String uid) {
			this.uid = uid;
		}
		public String getSort() {
			return sort;
		}
		public void setSort(String sort) {
			this.sort = sort;
		}
		public String getUpdatetime() {
			return updatetime;
		}
		public void setUpdatetime(String updatetime) {
			this.updatetime = updatetime;
		}
		public String getReply_count() {
			return reply_count;
		}
		public void setReply_count(String reply_count) {
			this.reply_count = reply_count;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		public String getPic_exif_lng() {
			return pic_exif_lng;
		}
		public void setPic_exif_lng(String pic_exif_lng) {
			this.pic_exif_lng = pic_exif_lng;
		}
		public String getGroup_id() {
			return group_id;
		}
		public void setGroup_id(String group_id) {
			this.group_id = group_id;
		}
		public String getAlbum_uid() {
			return album_uid;
		}
		public void setAlbum_uid(String album_uid) {
			this.album_uid = album_uid;
		}
		public String getUpload_lng() {
			return upload_lng;
		}
		public void setUpload_lng(String upload_lng) {
			this.upload_lng = upload_lng;
		}
		public String getPic_path() {
			return pic_path;
		}
		public void setPic_path(String pic_path) {
			this.pic_path = pic_path;
		}
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		public String getUpload_address() {
			return upload_address;
		}
		public void setUpload_address(String upload_address) {
			this.upload_address = upload_address;
		}
		public String getCreatetime() {
			return createtime;
		}
		public void setCreatetime(String createtime) {
			this.createtime = createtime;
		}
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public String getPic_exif_time() {
			return pic_exif_time;
		}
		public void setPic_exif_time(String pic_exif_time) {
			this.pic_exif_time = pic_exif_time;
		}
		public String getUpload_lat() {
			return upload_lat;
		}
		public void setUpload_lat(String upload_lat) {
			this.upload_lat = upload_lat;
		}
		public String getPic_desc() {
			return pic_desc;
		}
		public void setPic_desc(String pic_desc) {
			this.pic_desc = pic_desc;
		}
		public String getPic_exif_lat() {
			return pic_exif_lat;
		}
		public void setPic_exif_lat(String pic_exif_lat) {
			this.pic_exif_lat = pic_exif_lat;
		}
		public String getZan_num() {
			return zan_num;
		}
		public void setZan_num(String zan_num) {
			this.zan_num = zan_num;
		}
		public String getLike_num() {
			return like_num;
		}
		public void setLike_num(String like_num) {
			this.like_num = like_num;
		}
		public String getUpload_ip() {
			return upload_ip;
		}
		public void setUpload_ip(String upload_ip) {
			this.upload_ip = upload_ip;
		}
		
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCover() {
		return cover;
	}

	public void setCover(String cover) {
		this.cover = cover;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getReply_count() {
		return reply_count;
	}

	public void setReply_count(String reply_count) {
		this.reply_count = reply_count;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCreate_time() {
		return create_time;
	}

	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}

	public String getPic_num() {
		return pic_num;
	}

	public void setPic_num(String pic_num) {
		this.pic_num = pic_num;
	}

	public String getZan_num() {
		return zan_num;
	}

	public void setZan_num(String zan_num) {
		this.zan_num = zan_num;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<Pic> getPics() {
		return pics;
	}

	public void setPics(List<Pic> pics) {
		this.pics = pics;
	}
}
