package com.tencent.wechat.registration

import org.apache.spark.SparkConf
import org.apache.spark.api.java.JavaSparkContext
import org.apache.spark.graphx.Graph
import com.tencent.wechat.registration.algo.LouvainMethod
import com.tencent.wechat.registration.algo.GraphBuilder
import org.apache.spark.SparkContext
import com.tencent.wechat.registration.util.DataFileType


case class RConfig (
    inputFile      :String = "",
    outputFile     :String = "",
    testInputFile  :String = "",
    dataFileType   :DataFileType.Value = DataFileType.DataVisor,
    master         :String = "local",
    appName        :String = "TryLouvain",
    parallelism    :Int = -1,
    minProgress    :Int = 1000,
    progressCounter:Int = 1,
    delimiter      :String = "\t"
)

object MainFunc {
    def main(args: Array[String]): Unit ={
        val config      = RConfig(
//                                  "file:///D:/data/plusid_t_tmp_reg_data_sync_to_data_visor_20170604.txt",
//                                  "file:///D:/data/output" + System.currentTimeMillis.toString(),
//                                  "file:///D:/data/test_small_edges.csv"
                                  "file:///root/data/tip_plusid_t_tmp_reg_data_sync_to_data_visor_20170604.txt",
                                  "file:///root/output/output" + System.currentTimeMillis.toString(),
                                  "file:///root/data/test_small_edges.csv"
                                  )
        val conf        = new SparkConf().setAppName(config.appName)
                                         .setMaster("local[*]")
        val sc          = new SparkContext(conf)
        
        val (graph, idMaps) = GraphBuilder.buildGraph(sc, config)
        //val (graph, idMaps) = GraphBuilder.buildTestGraph(sc, config)
        val louvain     = new LouvainMethod()
        louvain.run(graph, idMaps, sc, config);
    }
}
