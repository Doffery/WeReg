package com.tencent.wechat.registration.util;

import java.io.Serializable;
import java.util.ArrayList;

public class Community implements Serializable {
	String				communityId;
	
	ArrayList<Node> 	nodes;
	double				inWeight;
	double				tocWeight;
	
	
	public Community(String id) {
		this.communityId = id;
	}
	
	public void add(Node node) {
		nodes.add(node);
		//inWeight += nodes.
		tocWeight += node.getDegreeWeight();
	}
	
	public String getCommunityId() {
		return communityId;
	}

	public void setCommunityId(String communityId) {
		this.communityId = communityId;
	}
}
