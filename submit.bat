spark-submit --class com.tencent.wechat.registration.MainFunc --master local[*] --driver-memory 5g --conf spark.network.timeout=600s target\scala-2.11\try-sbt_2.11-0.1-SNAPSHOT.jar
