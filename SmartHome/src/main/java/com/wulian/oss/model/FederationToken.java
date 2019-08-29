package com.wulian.oss.model;

/**
 * @ClassName: FederationToken
 * @Function: 获取临时Token
 * @date: 2015年10月16日
 * @author Puml
 * @email puml@wuliangroup.cn
 */
public class FederationToken {
	private String requestId;//
	private String accessKeyId;// Key
	private String accessKeySecret;// Secret
	private String securityToken;// 安全证书
	private long expiration;// 过期时间

	public FederationToken(String requestId, String ak, String sk,
			String sToken, long expiredTime) {
		this.requestId=requestId;
		this.accessKeyId = ak;
		this.accessKeySecret = sk;
		this.securityToken = sToken;
		this.expiration = expiredTime;
	}

	public FederationToken() {
		this.requestId="";
		this.accessKeyId = "";
		this.accessKeySecret = "";
		this.securityToken = "";
		this.expiration = 0;
	}

	public String getAccessKeyId() {
		return accessKeyId;
	}

	public void setAccessKeyId(String accessKeyId) {
		this.accessKeyId = accessKeyId;
	}

	public String getAccessKeySecret() {
		return accessKeySecret;
	}

	public void setAccessKeySecret(String accessKeySecret) {
		this.accessKeySecret = accessKeySecret;
	}

	public String getSecurityToken() {
		return securityToken;
	}

	public void setSecurityToken(String securityToken) {
		this.securityToken = securityToken;
	}

	public long getExpiration() {
		return expiration;
	}

	public void setExpiration(long expiration) {
		this.expiration = expiration;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}
}
