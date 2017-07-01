package com.tencent.wechat.registration.preprocessing

object FeaturePartition {
    
    def ipParse24Prefix(sp : String) : String = {
        if(sp.lastIndexOf('.') != -1)
            sp.substring(0, sp.lastIndexOf('.'))
        else sp
    }
    
  	def partitionIP(ip : String) : String = {
  		  ipParse24Prefix(ip)
  	}
  	
  	def partitionDeviceID(did : String) : String = {
  	    did
  	}
  	
  	def partitionWifi(wifi : String) : String = {
  	    wifi
  	}
  	
  	def partitionNickName(name : String) : String = {
  	    name
  	}
}