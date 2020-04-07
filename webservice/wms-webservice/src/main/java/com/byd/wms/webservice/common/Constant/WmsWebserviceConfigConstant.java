package com.byd.wms.webservice.common.Constant;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "webservice.webcloud")
public class WmsWebserviceConfigConstant {
	private String webCloudPath;
	private WebCloudMethod webCloudMethod;
	private String liKUPath;
	private LiKuMethod liKuMethod;
	
	public static class LiKuMethod {
		private String liKuOutInstruction;

		public String getLiKuOutInstruction() {
			return liKuOutInstruction;
		}

		public void setLiKuOutInstruction(String liKuOutInstruction) {
			this.liKuOutInstruction = liKuOutInstruction;
		}
	}
	
	public static class WebCloudMethod {
		private String deliveryData;
	    private String deliveryByBarcode;
	    private String deliveryDetailData;
	    
		public String getDeliveryData() {
			return deliveryData;
		}
		public void setDeliveryData(String deliveryData) {
			this.deliveryData = deliveryData;
		}
	    public String getDeliveryByBarcode() {
			return deliveryByBarcode;
		}
		public void setDeliveryByBarcode(String deliveryByBarcode) {
			this.deliveryByBarcode = deliveryByBarcode;
		}
		public String getDeliveryDetailData() {
			return deliveryDetailData;
		}
		public void setDeliveryDetailData(String deliveryDetailData) {
			this.deliveryDetailData = deliveryDetailData;
		}
	}

	public String getWebCloudPath() {
		return webCloudPath;
	}

	public void setWebCloudPath(String webCloudPath) {
		this.webCloudPath = webCloudPath;
	}

	public WebCloudMethod getWebCloudMethod() {
		return webCloudMethod;
	}

	public void setWebCloudMethod(WebCloudMethod webCloudMethod) {
		this.webCloudMethod = webCloudMethod;
	}

	public String getLiKUPath() {
		return liKUPath;
	}

	public void setLiKUPath(String liKUPath) {
		this.liKUPath = liKUPath;
	}

	public LiKuMethod getLiKuMethod() {
		return liKuMethod;
	}

	public void setLiKuMethod(LiKuMethod liKuMethod) {
		this.liKuMethod = liKuMethod;
	}
}
