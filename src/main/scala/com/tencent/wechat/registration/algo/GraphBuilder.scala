package com.tencent.wechat.registration.algo

import scala.collection.mutable.ArrayBuffer
import org.apache.spark.SparkContext
import org.apache.spark.graphx.Edge
import org.apache.spark.graphx.Graph
import com.tencent.wechat.registration.preprocessing.FeaturePartition
import com.tencent.wechat.registration.util.UserRegData
import com.tencent.wechat.registration.RConfig

object GraphBuilder {
  
    //def weightCal(u1: UserRegData, u2: UserRegData) : Long = {
    //    
    //}
  
    def buildGraph(sc : SparkContext, config : RConfig): Graph[Long, Long] = {
        val textFile = sc.textFile(config.inputFile);
    		val header = textFile.first();
    		val text_wo_head = textFile.filter(s => {s != header});
    		//Do the pre-processing from textFile to cleaned user data
    		
    		//Then, we partition the features, map and reduce to get calculated weights for this feature
    		//Or, we just use this to mark this edge need to be calculated
    
    		val data = text_wo_head.map(
    				s => new UserRegData(s));
    		
    		/*
    		JavaRDD<Tuple2<String, UserRegData>> partition_featrue_data = text_wo_head.map(
    				new Function<String, Tuple2<String, UserRegData> >() {
    			public Tuple2<String, UserRegData> call(String s) {
    				UserRegData u = new UserRegData(s);
    				return new Tuple2<String, UserRegData>(
    						u.getClient_ip(), u);
    			}
    		});*/
    		
    		val ip_partition = data.groupBy(
    				t => FeaturePartition.paritionIP(t.clientIp));
    				//partition_featrue_data.groupBy(t -> t._1());
    		
    		//this is the place where we save the relation between nodes
    		//def typeConversionMethod = {String => Long = _.toLong}
    		val node_neightbors = ip_partition.flatMap( { a => 
      		  var u       = a._2.iterator
      		  var uu      = a._2.iterator
      		  var res = ArrayBuffer[Edge[Long]]()
    		    if(a._2.size == 1)
    		        res.iterator
    		    else while(u.hasNext) {
      		      var dup      = u.duplicate
      		      uu           = dup._1
      		      u            = dup._2
      		      val value1   = u.next()
      		      while(uu.hasNext) {
      		          //println("**Checking**" + value1.id)
      		          val value2    = uu.next()
      		          if(value2.id != value1.id) {
          		          res += new Edge(value1.shortId, value2.shortId, 1L)
          		          //println("**MAKE EDGE** " + value1.id + "\t--to--\t" + value2.id)
      		          }
      		      }
      		  }
      		  res.iterator
    		});
    		Graph.fromEdges(node_neightbors, 0L)
    }
}
