package com.tencent.wechat.registration.util;

import java.io.Serializable;

public class Node implements Serializable {
	String			uid;
	double			degreeWeight;
	
	String			communityId;
	public Node(String id) {
		this.uid = id;
	}
	public Node(UserRegData ur) {
		this.uid = ur.getId();
		this.communityId = ur.getId();
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getCommunityId() {
		return communityId;
	}
	public void setCommunityId(String communityId) {
		this.communityId = communityId;
	}
	public double getDegreeWeight() {
		return degreeWeight;
	}
	public void setDegreeWeight(double degreeWeight) {
		this.degreeWeight = degreeWeight;
	}
}
