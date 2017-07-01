package com.tencent.wechat.registration.algo

import scala.collection.mutable.ArrayBuffer
import org.apache.spark.SparkContext
import org.apache.spark.graphx.Edge
import org.apache.spark.graphx.VertexId
import org.apache.spark.graphx.Graph
import com.tencent.wechat.registration.preprocessing.FeaturePartition
import com.tencent.wechat.registration.util.UserRegData
import com.tencent.wechat.registration.RConfig
import scala.reflect.ClassTag
import org.apache.spark.rdd.RDD
import com.tencent.wechat.registration.util.UserBriefData
import com.tencent.wechat.registration.util.UserBriefData
import com.tencent.wechat.registration.preprocessing.OddFeatureContainer
import org.apache.spark.graphx.PartitionStrategy

object GraphBuilder {
  
    def makeEdge(s: String, data: Iterable[UserRegData]) : TraversableOnce[Edge[Double]] = {
        var u       = data.iterator
        var uu      = data.iterator
        var res = ArrayBuffer[Edge[Double]]()
        if(data.size == 1 || s.length() == 0 || s == "0:0:0:0:0:0")
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
                    val weight = WeightStrategy.weightCal(value1, value2)
                    if(weight >= 0.999999) {
                        if(value1.shortId < value2.shortId)
                            res += new Edge(value1.shortId, value2.shortId, weight)
                        else res += new Edge(value2.shortId, value1.shortId, weight)
                    }
                    //println("**MAKE EDGE** " + value1.id + "\t--to--\t" + value2.id)
                }
            }
        }
        res.iterator
    }
    
    def buildGraph(
        sc : SparkContext, 
        config : RConfig): (Graph[Long, Double], RDD[(Long, UserBriefData)]) = {
        val textFile = sc.textFile(config.inputFile);
        val header = textFile.first();
        val text_wo_head = textFile.filter(s => {s != header});
        //Do the pre-processing from textFile to cleaned user data

        //Then, we partition the features, map and reduce to get calculated weights for this feature
        //Or, we just use this to mark this edge need to be calculated

        val data = text_wo_head.map(
            s => new UserRegData(s)).cache();
        //val vertices : RDD[(VertexId, UserBriefData)] = data.map(t => (t.shortId, new UserBriefData(t)))
        val idmaps = data.map(t => (t.shortId, new UserBriefData(t)))
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
           t => FeaturePartition.partitionIP(t.clientIp)).flatMap( t => makeEdge(t._1, t._2) )

       val device_partition = data.groupBy(
           t => FeaturePartition.partitionDeviceID(t.deviceId)).flatMap( t => makeEdge(t._1, t._2) )
       
       val wifi_partition = data.groupBy(
           t => FeaturePartition.partitionWifi(t.ssidMac)).flatMap( t => makeEdge(t._1, t._2) )
           
       val nickname_partition = data.groupBy(
           t => FeaturePartition.partitionNickName(t.nickName)).flatMap( t => makeEdge(t._1, t._2) )
           
       data.unpersist(blocking = false)
       //this is the place where we save the relation between nodes
       //def typeConversionMethod = {String => Long = _.toLong}
       val node_neightbors = ip_partition.union(device_partition)
                                         .union(wifi_partition)
                                         .union(nickname_partition)
       (Graph.fromEdges(node_neightbors, 0L)
             .partitionBy(PartitionStrategy.EdgePartition2D)
             .groupEdges((a,b) => a), idmaps)
       //.outerJoinVertices(vertices)(
       //(vid, data, att) => {
       //    att.getOrElse(new UserBriefData(new UserRegData("")))
       //})
    }
    
    def buildTestGraph(
        sc : SparkContext, 
        config : RConfig): (Graph[Long, Double], RDD[(Long, UserBriefData)]) = {
        val textFile = sc.textFile(config.testInputFile).cache()
        val node_neightbors = textFile.map(row => {
            val tokens = row.split(config.delimiter).map(_.trim())
            def typeConversionMethod: String => Long = _.toLong
            tokens.length match {
                case 2 => new Edge(typeConversionMethod(tokens(0)),
                  typeConversionMethod(tokens(1)), 1.0)
                case 3 => new Edge(typeConversionMethod(tokens(0)),
                  typeConversionMethod(tokens(1)), tokens(2).toDouble)
                case _ => throw new IllegalArgumentException("invalid input line: " + row)
            }
        }).distinct()
        val idmaps = node_neightbors.flatMap(edge => {
            var nodes = new ArrayBuffer[(Long, UserBriefData)]()
            nodes += ((edge.srcId.toLong, new UserBriefData(edge.srcId.toString())))
            nodes += ((edge.dstId.toLong, new UserBriefData(edge.dstId.toString())))
            nodes.iterator
        }).reduceByKey({case (u1, u2) => u1})

       (Graph.fromEdges(node_neightbors, 0L), idmaps)
    }
}
