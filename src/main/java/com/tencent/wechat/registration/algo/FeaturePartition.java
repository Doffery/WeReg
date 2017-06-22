package com.tencent.wechat.registration.algo;

/*
 * This class provide functions to split the data according to their features
 * Then the algo can leverage this partition to build graph
 * 
 * Intuitively, these functions should provide input of data and some parameters to do the partition.
 * 
 * Also this function should have the input to indicate the weights of this partition?
 * So with the map and reduce, we can calculate the weights of edges*/
public class FeaturePartition {
	public static String paritionIP(String ip) {
		return ip;
	}
	
	public static String paritionIP(String ip, String dataline) {
		return ip;
	}
}
