package com.tencent.wechat.registration.preprocessing;

import com.tencent.wechat.registration.util.*;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;

public class PreProcessing {
	JavaSparkContext psc;
	JavaRDD<String> pTextFile;
	
	public PreProcessing(JavaSparkContext sc, JavaRDD<String> textFile) {
	}
}
