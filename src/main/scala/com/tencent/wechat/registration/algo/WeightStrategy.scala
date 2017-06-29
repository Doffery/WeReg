package com.tencent.wechat.registration.algo

import com.tencent.wechat.registration.util.UserRegData
import com.tencent.wechat.registration.util.FeatureName

/*
 * The weighting strategy should return the similarity between user pairs.
 * If this similarity is smaller that certain bound, it should return 0
 * which represents that we do not build edge for this worker pair*/
object WeightStrategy {
    var weightMap      = scala.collection.mutable.HashMap[FeatureName.Value, Float]()
  
    def loadFeatureWeight() : Unit = {
        weightMap(FeatureName.ClientVersion) = 1
        weightMap(FeatureName.DeviceType) = 1
        weightMap(FeatureName.IPrefix) = 2
        weightMap(FeatureName.DeviceID) = 2
        weightMap(FeatureName.Wifi) = 2
        weightMap(FeatureName.Phone) = 2
        weightMap(FeatureName.NickName) = 2
    }
    
    def weightCal(u1: UserRegData, u2: UserRegData) : Long = {
        var weight    = 0.0
        if (u1.clientVersion == u2.clientVersion) weight += weightMap(FeatureName.ClientVersion)
        if (weight > 3) weight else 0
        1L
    }
}