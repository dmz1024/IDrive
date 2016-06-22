package com.trs.constants;

public enum ContentType {
		Tab("1000"),
		Document("01"),
		Picture("02"),
		Grid("03"),
		Map("04"),
		SinaWeibo("05"),
		Stock("06"),
		Html("07"),
		TencentWeibo("08"),
		ProjectDefined("2001");

		private String typeCode;

		private ContentType(String typeCode) {
			this.typeCode = typeCode;
		}

		public static ContentType getType(String typeCode){
			for(ContentType type: values()){
				if(type.typeCode.equals(typeCode)){
					return type;
				}
			}

			return ProjectDefined;
		}

		public String getTypeCode() {
			return typeCode;
		}
	}
