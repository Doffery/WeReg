package com.tencent.wechat.registration.util;

import java.io.Serializable;
import java.util.ArrayList;

public class NeighborList implements Serializable {
	ArrayList<Neighbor> neightbors;
	
	public ArrayList<Neighbor> getNeighbors() {
		return neightbors;
	}
	
	public void add(Neighbor nbor) {
		neightbors.add(nbor);
	}
	
	public NeighborList addAll(NeighborList nl) {
		neightbors.addAll(nl.getNeighbors());
		return this;
	}
}
