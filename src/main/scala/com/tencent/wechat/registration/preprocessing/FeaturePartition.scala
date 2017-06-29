package com.tencent.wechat.registration.preprocessing

object FeaturePartition {
    
    def ipParse24Prefix(sp : String) : String = {
        sp.substring(0, sp.lastIndexOf('.'))
    }
    
  	def partitionIP(ip : String) : String = {
  		  ipParse24Prefix(ip)
  	}
  	
  	def partitionDevice(did : String) : String = {
  	    did
  	}
  	
  	def partitionWifi(wifi : String) : String = {
  	    wifi
  	}
  	
  	def nickName(name : String) : String = {
  	    name
  	}
}