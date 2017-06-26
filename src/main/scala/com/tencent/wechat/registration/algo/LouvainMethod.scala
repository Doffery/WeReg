package com.tencent.wechat.registration.algo

import org.apache.spark.SparkContext
import scala.reflect.ClassTag
import org.apache.spark.graphx.Graph
import com.tencent.wechat.registration.RConfig
import com.tencent.wechat.registration.util.LouvainVertex
import com.esotericsoftware.kryo.KryoSerializable
import org.apache.spark.graphx.EdgeContext
import com.tencent.wechat.registration.util.LouvainVertex
import org.apache.spark.graphx.PartitionStrategy
import com.tencent.wechat.registration.util.LouvainVertex
import com.tencent.wechat.registration.util.LouvainVertex
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.graphx.VertexRDD
import com.tencent.wechat.registration.util.LouvainVertex
import org.apache.spark.graphx.Edge
import com.tencent.wechat.registration.util.LouvainVertex
import com.tencent.wechat.registration.util.LouvainVertex

class LouvainMethod extends Serializable{
    def buildLouvainGraph[VD : ClassTag](graph : Graph[VD, Long]): Graph[LouvainVertex, Long] = {
        val weights    = graph.aggregateMessages(
            (e: EdgeContext[VD, Long, Long]) => {
              e.sendToSrc(e.attr)
              e.sendToDst(e.attr)
            },
            (msg1: Long, msg2: Long) => msg1 + msg2)
        graph.outerJoinVertices(weights)(
            (vid, data, att) => {
                val weight  = att.getOrElse(0L)
                new LouvainVertex(vid, weight, 0, weight, false)
            }
            ).partitionBy(PartitionStrategy.EdgePartition2D)
    }
    

    /**
      * Creates the messages passed between each vertex to convey neighborhood community data.
      * Send messgae to both direction of the edges
      */
    def sendCommunityData(e: EdgeContext[LouvainVertex, Long, Map[(Long, Long), Long]]) = {
        val m1 = (Map((e.srcAttr.community, e.srcAttr.communitySigmaTot) -> e.attr))
        val m2 = (Map((e.dstAttr.community, e.dstAttr.communitySigmaTot) -> e.attr))
        e.sendToSrc(m2)
        e.sendToDst(m1)
    }
  
    /**
      * Merge neighborhood community data into a single message for each vertex
      * The message we finally get is a map,
      * using community id and tot as key, the weights ki.in as value
      */
    def mergeCommunityMessages(m1: Map[(Long, Long), Long], m2: Map[(Long, Long), Long]) = {
        val newMap = scala.collection.mutable.HashMap[(Long, Long), Long]()
    
        m1.foreach({ case (k, v) =>
            if (newMap.contains(k)) newMap(k) = newMap(k) + v
            else newMap(k) = v
        })
    
        m2.foreach({ case (k, v) =>
            if (newMap.contains(k)) newMap(k) = newMap(k) + v
            else newMap(k) = v
        })
    
        newMap.toMap
    }
    
    
    /**
      * Returns the change in modularity that would result from a vertex
      moving to a specified community.
      */
    def modularityGain(
        currCommunityId: Long,
        testCommunityId: Long,
        testSigmaTot: Long,
        edgeWeightInCommunity: Long,
        nodeWeight: Long,
        internalWeight: Long,
        totalEdgeWeight: Long): BigDecimal = {
    
        // this constant variable indicate this is checking adding a node or removing
        val isCurrentCommunity = currCommunityId.equals(testCommunityId)
        val M = BigDecimal(totalEdgeWeight)
        val k_i_in_L = if (isCurrentCommunity) edgeWeightInCommunity + internalWeight else edgeWeightInCommunity
        val k_i_in = BigDecimal(k_i_in_L)
        val k_i = BigDecimal(nodeWeight + internalWeight)
        val sigma_tot = if (isCurrentCommunity) BigDecimal(testSigmaTot) - k_i else BigDecimal(testSigmaTot)
    
        var deltaQ = BigDecimal(0.0)
    
        if (!(isCurrentCommunity && sigma_tot.equals(BigDecimal.valueOf(0.0)))) {
            deltaQ = k_i_in - (k_i * sigma_tot / M)
            //println(s"      $deltaQ = $k_i_in - ( $k_i * $sigma_tot / $M")
        }
    
        deltaQ
    }
    
    /**
      * Join vertices with community data form their neighborhood and
      select the best community for each vertex to maximize change in
      modularity.
      * Returns a new set of vertices with the updated vertex state.
      */
    def louvainVertJoin(
        louvainGraph: Graph[LouvainVertex, Long],
        msgRDD: VertexRDD[Map[(Long, Long), Long]],
        totalEdgeWeight: Broadcast[Long],
        even: Boolean): VertexRDD[LouvainVertex] = {
    
        // innerJoin[U, VD2](other: RDD[(VertexId, U)])(f: (VertexId, VD, U) => VD2): VertexRDD[VD2]
        louvainGraph.vertices.innerJoin(msgRDD)((vid, louvainData, communityMessages) => {
            var bestCommunity = louvainData.community
            val startingCommunityId = bestCommunity
            var maxDeltaQ = BigDecimal(0.0);
            var bestSigmaTot = 0L
      
            // VertexRDD[scala.collection.immutable.Map[(Long, Long),Long]]
            // e.g. (1,Map((3,10) -> 2, (6,4) -> 2, (2,8) -> 2, (4,8) -> 2, (5,8) -> 2))
            // e.g. communityId:3, sigmaTotal:10, communityEdgeWeight:2
            communityMessages.foreach({ case ((communityId, sigmaTotal), communityEdgeWeight) =>
                val deltaQ = modularityGain(
                    startingCommunityId,
                    communityId,
                    sigmaTotal,
                    communityEdgeWeight,
                    louvainData.nodeWeight,
                    louvainData.internalWeight,
                    totalEdgeWeight.value)
        
                //println(" communtiy: "+communityId+" sigma:"+sigmaTotal+"
                //edgeweight:"+communityEdgeWeight+" q:"+deltaQ)
                if (deltaQ > maxDeltaQ || (deltaQ > 0 && (deltaQ == maxDeltaQ &&
                    communityId > bestCommunity))) {
                    maxDeltaQ = deltaQ
                    bestCommunity = communityId
                    bestSigmaTot = sigmaTotal
                }
            })
      
            // only allow changes from low to high communties on even cycles and
            // high to low on odd cycles
            if (louvainData.community != bestCommunity && ((even &&
                  louvainData.community > bestCommunity) || (!even &&
                  louvainData.community < bestCommunity))) {
                //println("  "+vid+" SWITCHED from "+vdata.community+" to "+bestCommunity)
                louvainData.community = bestCommunity
                louvainData.communitySigmaTot = bestSigmaTot
                louvainData.changed = true
            }
            else {
                louvainData.changed = false
            }
      
            if (louvainData == null)
                println("vdata is null: " + vid)
      
            louvainData
        })
    }
    
    def findCommunity(
        sc: SparkContext,
        graph: Graph[LouvainVertex, Long],
        minProgress: Int = 1,
        progressCounter: Int = 1): (Double, Graph[LouvainVertex, Long], Int) = {
        
        var louvainGraph = graph.cache()
        var graphWeight  = louvainGraph.vertices.values.map(v => v.internalWeight + v.nodeWeight).reduce(_+_)
        var totalWeight = sc.broadcast(graphWeight)
        println("totalEdgeWeight: "+totalWeight.value)
        
        
        val maxIter    = 10000
        var iterCount  = 0
        var moving     = true
        var even       = false  
        var stop = 0
        var updatedLastPhase = 0L
        var updated    = 0L - minProgress
        
        // Should we do this again?
        // gather community information from each vertex's local neighborhood
        var communityRDD =
            louvainGraph.aggregateMessages(sendCommunityData, mergeCommunityMessages)
        
        var activeMessages = communityRDD.count() //materializes the msgRDD
                                                  //and caches it in memory
        do {
            iterCount     += 1
            moving        = false
            even          = !even
            

            // label each vertex with its best community based on neighboring
            // community information
            val labeledVertices = louvainVertJoin(louvainGraph, communityRDD,
                totalWeight, even).cache()
      
            // calculate new sigma total value for each community (total weight
            // of each community)
            val communityUpdate = labeledVertices
                .map({ case (vid, vdata) => (vdata.community, vdata.nodeWeight +
                  vdata.internalWeight)})
                .reduceByKey(_ + _).cache()
      
            // map each vertex ID to its updated community information
            val communityMapping = labeledVertices
                .map({ case (vid, vdata) => (vdata.community, vid)})
                .join(communityUpdate)
                .map({ case (community, (vid, sigmaTot)) => (vid, (community, sigmaTot))})
                .cache()
      
            // join the community labeled vertices with the updated community info
            val updatedVertices = labeledVertices.join(communityMapping).map({
                case (vertexId, (louvainData, communityTuple)) =>
                    val (community, communitySigmaTot) = communityTuple
                    louvainData.community = community
                    louvainData.communitySigmaTot = communitySigmaTot
                    (vertexId, louvainData)
            }).cache()
      
            updatedVertices.count()
            labeledVertices.unpersist(blocking = false)
            communityUpdate.unpersist(blocking = false)
            communityMapping.unpersist(blocking = false)
      
            val prevG = louvainGraph
      
            louvainGraph = louvainGraph.outerJoinVertices(updatedVertices)((vid, old, newOpt) => newOpt.getOrElse(old))
            louvainGraph.cache()
      
            // gather community information from each vertex's local neighborhood
            val oldMsgs = communityRDD
            communityRDD = louvainGraph.aggregateMessages(sendCommunityData, mergeCommunityMessages).cache()
            activeMessages = communityRDD.count() // materializes the graph
                                                  // by forcing computation
      
            oldMsgs.unpersist(blocking = false)
            updatedVertices.unpersist(blocking = false)
            prevG.unpersistVertices(blocking = false)
      
            // half of the communites can swtich on even cycles and the other half
            // on odd cycles (to prevent deadlocks) so we only want to look for
            // progess on odd cycles (after all vertcies have had a chance to
            // move)
            if (even) updated = 0
            updated = updated + louvainGraph.vertices.filter(_._2.changed).count
      
            if (!even) {
                println("  # vertices moved: " + java.text.NumberFormat.getInstance().format(updated))
        
                if (updated >= updatedLastPhase - minProgress) stop += 1
        
                updatedLastPhase = updated
            }
            
        } while(stop <= progressCounter && (even || (updated > 0 && iterCount < maxIter)))
          
        
        println("\nCompleted in " + iterCount + " cycles")
    
        // Use each vertex's neighboring community data to calculate the
        // global modularity of the graph
        val newVertices =
            louvainGraph.vertices.innerJoin(communityRDD)((vertexId, louvainData,
                communityMap) => {
                  // sum the nodes internal weight and all of its edges that are in
                  // its community
                  val community = louvainData.community
                  var accumulatedInternalWeight = louvainData.internalWeight
                  val sigmaTot = louvainData.communitySigmaTot.toDouble
                  def accumulateTotalWeight(totalWeight: Long, item: ((Long, Long), Long)) = {
                    val ((communityId, sigmaTotal), communityEdgeWeight) = item
                    if (louvainData.community == communityId)
                      totalWeight + communityEdgeWeight
                    else
                      totalWeight
                  }
          
                  accumulatedInternalWeight = communityMap.foldLeft(accumulatedInternalWeight)(accumulateTotalWeight)
                  val M = totalWeight.value
                  val k_i = louvainData.nodeWeight + louvainData.internalWeight
                  val q = (accumulatedInternalWeight.toDouble / M) - ((sigmaTot * k_i) / math.pow(M, 2))
                  //println(s"vid: $vid community: $community $q = ($k_i_in / $M) - ( ($sigmaTot * $k_i) / math.pow($M, 2) )")
                  if (q < 0)
                    0
                  else
                    q
              })
    
        val actualQ = newVertices.values.reduce(_ + _)
    
        // return the modularity value of the graph along with the
        // graph. vertices are labeled with their community
        (actualQ, louvainGraph, iterCount / 2)
    }
    
    def compressGraph(
        graph: Graph[LouvainVertex, Long], 
        debug: Boolean = true): Graph[LouvainVertex, Long] = {
        // aggregate the edge weights of self loops. edges with both src and dst in the same community.
        // WARNING  can not use graph.mapReduceTriplets because we are mapping to new vertexIds
        val internalEdgeWeights = graph.triplets.flatMap(et => {
            if (et.srcAttr.community == et.dstAttr.community) {
              Iterator((et.srcAttr.community, 2 * et.attr)) // count the weight from both nodes
            }
            else Iterator.empty
        }).reduceByKey(_ + _)
    
        // aggregate the internal weights of all nodes in each community
        val internalWeights = graph.vertices.values.map(vdata =>
            (vdata.community, vdata.internalWeight))
            .reduceByKey(_ + _)
    
        // join internal weights and self edges to find new interal weight of each community
        val newVertices = internalWeights.leftOuterJoin(internalEdgeWeights).map({ case (vid, (weight1, weight2Option)) =>
            val weight2 = weight2Option.getOrElse(0L)
            val state = new LouvainVertex()
            state.community = vid
            state.changed = false
            state.communitySigmaTot = 0L
            state.internalWeight = weight1 + weight2
            state.nodeWeight = 0L
            (vid, state)
        }).cache()
    
        // translate each vertex edge to a community edge
        val edges = graph.triplets.flatMap(et => {
            val src = math.min(et.srcAttr.community, et.dstAttr.community)
            val dst = math.max(et.srcAttr.community, et.dstAttr.community)
            if (src != dst) Iterator(new Edge(src, dst, et.attr))
            else Iterator.empty
        }).cache()
    
        // generate a new graph where each community of the previous graph is
        // now represented as a single vertex
        val compressedGraph = Graph(newVertices, edges)
            .partitionBy(PartitionStrategy.EdgePartition2D).groupEdges(_ + _)
    
        // calculate the weighted degree of each node
        val nodeWeights = compressedGraph.aggregateMessages(
            (e:EdgeContext[LouvainVertex,Long,Long]) => {
                e.sendToSrc(e.attr)
                e.sendToDst(e.attr)
            },
            (e1: Long, e2: Long) => e1 + e2
        )
    
        // fill in the weighted degree of each node
        // val louvainGraph = compressedGraph.joinVertices(nodeWeights)((vid,data,weight)=> {
        val louvainGraph = compressedGraph.outerJoinVertices(nodeWeights)((vid, data, weightOption) => {
            val weight = weightOption.getOrElse(0L)
            data.communitySigmaTot = weight + data.internalWeight
            data.nodeWeight = weight
            data
        }).cache()
        louvainGraph.vertices.count()
        louvainGraph.triplets.count() // materialize the graph
    
        newVertices.unpersist(blocking = false)
        edges.unpersist(blocking = false)
        louvainGraph
    }
  

    def finalSave(
        sc: SparkContext,
        config: RConfig,
        level: Int,
        qValues: Array[(Int, Double)],
        graph: Graph[LouvainVertex, Long]) = {
    
        val vertexSavePath = config.outputFile + "/level_" + level + "_vertices"
        val edgeSavePath = config.outputFile + "/level_" + level + "_edges"
    
        // save
        graph.vertices.saveAsTextFile(vertexSavePath)
        graph.edges.saveAsTextFile(edgeSavePath)
    
        // overwrite the q values at each level
        sc.parallelize(qValues, 1).saveAsTextFile(config.outputFile + "/qvalues_" + level)
    }
    
    def run[VD : ClassTag](graph : Graph[VD, Long], sc : SparkContext, config : RConfig): Unit = {
        var louvainGraph    = buildLouvainGraph(graph)
        
        var compressionLevel = -1 // number of times the graph has been compressed
        var qModularityValue = -1.0 // current modularity value
        var halt = false
    
        var qValues: Array[(Int, Double)] = Array()
    
        do {
            compressionLevel += 1
            println(s"\nStarting Louvain level $compressionLevel")
      
            // label each vertex with its best community choice at this level of compression
            val (currentQModularityValue, currentGraph, numberOfPasses) =
                findCommunity(sc, louvainGraph, config.minProgress, config.progressCounter)
      
            louvainGraph.unpersistVertices(blocking = false)
            louvainGraph = currentGraph
      
            println(s"qValue: $currentQModularityValue")
      
            qValues = qValues :+ ((compressionLevel, currentQModularityValue))
      
            //saveLevel(sc, config, compressionLevel, qValues, louvainGraph)
      
            // If modularity was increased by at least 0.001 compress the graph and repeat
            // halt immediately if the community labeling took less than 3 passes
            //println(s"if ($passes > 2 && $currentQ > $q + 0.001 )")
            if (numberOfPasses > 2 && currentQModularityValue > qModularityValue + 0.001) {
                qModularityValue = currentQModularityValue
                louvainGraph = compressGraph(louvainGraph)
            }
            else {
                halt = true
            }
      
        } while (!halt)
        finalSave(sc, config, compressionLevel, qValues, louvainGraph)
    }
}