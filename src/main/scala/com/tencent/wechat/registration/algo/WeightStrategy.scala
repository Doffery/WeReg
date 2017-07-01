package com.tencent.wechat.registration.algo

import com.tencent.wechat.registration.util.UserRegData
import com.tencent.wechat.registration.util.FeatureName
import com.tencent.wechat.registration.preprocessing.OddFeatureContainer

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
        weightMap(FeatureName.DeviceID) = 3
        weightMap(FeatureName.Wifi) = 2
        weightMap(FeatureName.Phone) = 2
        weightMap(FeatureName.NickName) = 2
    }
    
    def weightCal(u1: UserRegData, u2: UserRegData) : Double = {
        loadFeatureWeight()
        var weight    = 0.0
        if (u1.clientVersion == u2.clientVersion) weight += weightMap(FeatureName.ClientVersion)
        if (u1.deviceType == u2.deviceType) weight += weightMap(FeatureName.DeviceType)
        if (u1.clientIp == u2.clientIp) weight += weightMap(FeatureName.IPrefix)
        if (u1.deviceId == u2.deviceId && OddFeatureContainer.checkFine(u1.deviceId))
            weight += weightMap(FeatureName.DeviceID)
        if (u1.ssidMac == u2.ssidMac && OddFeatureContainer.checkFine(u1.ssidMac)) 
            weight += weightMap(FeatureName.Wifi)
        if (u1.phonePrefix == u2.phonePrefix) weight += weightMap(FeatureName.Phone)
        if (u1.nickName == u2.nickName) weight += weightMap(FeatureName.NickName)
        if (weight > 3) weight else 0
    }
}