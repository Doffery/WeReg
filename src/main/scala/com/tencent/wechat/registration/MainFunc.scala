package com.tencent.wechat.registration

import org.apache.spark.SparkConf
import org.apache.spark.api.java.JavaSparkContext
import org.apache.spark.graphx.Graph
import com.tencent.wechat.registration.algo.LouvainMethod
import com.tencent.wechat.registration.algo.GraphBuilder
import org.apache.spark.SparkContext


case class RConfig (
    inputFile      :String = "",
    outputFile     :String = "",
    master         :String = "local",
    appName        :String = "TryLouvain",
    parallelism    :Int = -1,
    minProgress    :Int = 1000,
    progressCounter:Int = 1,
    delimiter      :String = "\t"
)

object MainFunc {
    def main(args: Array[String]): Unit ={
        val config      = RConfig("file:///D:/data/plusid_t_tmp_wxregister_alldata_20170604.csv",
            "file:///D:/data/output" + System.currentTimeMillis.toString())
        val conf        = new SparkConf().setAppName(config.appName)
                                         .setMaster("local[*]")
                                         .set("spark.executor.memory", "6g")
        val sc          = new SparkContext(conf)
        
        val graph       = GraphBuilder.buildGraph(sc, config)
        val louvain     = new LouvainMethod()
        louvain.run(graph, sc, config);
    }
}
