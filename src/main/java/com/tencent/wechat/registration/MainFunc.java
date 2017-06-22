package com.tencent.wechat.registration;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFlatMapFunction;

import com.tencent.wechat.registration.algo.FeaturePartition;
import com.tencent.wechat.registration.util.Community;
import com.tencent.wechat.registration.util.Neighbor;
import com.tencent.wechat.registration.util.NeighborList;
import com.tencent.wechat.registration.util.Node;
import com.tencent.wechat.registration.util.UserRegData;

import scala.Tuple2;

public class MainFunc {

	static void composeFeaturePartition() {
		
	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SparkConf conf = new SparkConf().setAppName("JavaWordCount").setMaster("local");
		JavaSparkContext sc = new JavaSparkContext(conf);
		JavaRDD<String> textFile = sc.textFile("file:///D:/data/t_tmp_wxregister_alldata_20170604.csv");
		String header = textFile.first();
		JavaRDD<String> text_wo_head = textFile.filter(new Function<String, Boolean>() {
			public Boolean call(String s) {
				return s != header;
			}
		});
		//Do the pre-processing from textFile to cleaned user data
		
		//Then, we partition the features, map and reduce to get calculated weights for this feature
		//Or, we just use this to mark this edge need to be calculated

		JavaRDD<UserRegData> data = text_wo_head.map(
				new Function<String, UserRegData> () {
			public UserRegData call(String s) {
				return new UserRegData(s);
			}
		});
		
		/*
		JavaRDD<Tuple2<String, UserRegData>> partition_featrue_data = text_wo_head.map(
				new Function<String, Tuple2<String, UserRegData> >() {
			public Tuple2<String, UserRegData> call(String s) {
				UserRegData u = new UserRegData(s);
				return new Tuple2<String, UserRegData>(
						u.getClient_ip(), u);
			}
		});*/
		
		JavaPairRDD<String, Iterable<UserRegData> > ip_partition = data.groupBy(
				t -> FeaturePartition.paritionIP(t.getClientIp()));
				//partition_featrue_data.groupBy(t -> t._1());
		
		//this is the place where we save the relation between nodes
		JavaPairRDD<Node, Neighbor> node_neightbors = ip_partition.flatMapToPair(
				new PairFlatMapFunction<Tuple2<String, Iterable<UserRegData>>, Node, Neighbor> () {
			@Override
			public Iterator<Tuple2<Node, Neighbor>> call(Tuple2<String, Iterable<UserRegData>> t) throws Exception {
				// TODO Auto-generated method stub
				ArrayList<Tuple2<Node, Neighbor> > res = new ArrayList<>();
				Iterator<UserRegData> it = t._2().iterator();
				for(UserRegData ur : t._2()) {
					for(UserRegData ul : t._2()) {
						if(!ur.equals(ul)) {
							// actually we need to analyze the similarity between users here and added it as a weight
							res.add(new Tuple2<Node, Neighbor>(
									new Node(ul.getId()), new Neighbor(new Node(ur.getId()))));
						}
					}
				}
				return res.iterator();
			}
		});

		JavaRDD<Node> nodes = data.map(t -> new Node(t));
		JavaPairRDD<String, Iterable<Node> > nodes_group_by_community = nodes.groupBy(
				t -> t.getCommunityId());
		
		JavaRDD<Community> communities = data.map(t -> new Community(t.getId()));
		//JavaRDD<Community> 
		
		// Graph is builded here
		JavaPairRDD<Community, Neighbor> node_pair = node_neightbors.mapToPair(
				t -> new Tuple2<Community, Neighbor>(new Community(t._1().getUid()), t._2()));
		JavaPairRDD<Community, NeighborList> community_node = node_pair.aggregateByKey(
				new NeighborList(),
				new Function2<NeighborList, Neighbor, NeighborList>() {
					public NeighborList call(NeighborList nl, Neighbor n) {
						nl.add(n);
						return nl;
					}
				}, 
				new Function2<NeighborList, NeighborList, NeighborList>() {
					@Override
					public NeighborList call(NeighborList v1, NeighborList v2) throws Exception {
						// TODO Auto-generated method stub
						v1.addAll(v2);
						return v1;
					}
				});
		
		//JavaPairRDD<Node, Choice> node_commnity_candidate = community_node
		
		// partition_featrue_data.groupByKey().map();
		sc.close();
	}
}
