package com.tencent.wechat.registration.preprocessing

import com.tencent.wechat.registration.util.FeatureName

object OddFeatureContainer {
    //var omitList    = scala.collection.mutable.HashMap[FeatureName.Value, 
    //                        scala.collection.mutable.HashSet[String]]()
                            
    var omitList      = scala.collection.immutable.HashSet[String]("", "0:0:0:0:0:0")
    
    def checkFine(cstr : String) : Boolean = {
        !omitList(cstr)
    }
}