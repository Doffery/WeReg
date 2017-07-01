package com.tencent.wechat.registration.util

import com.esotericsoftware.kryo.KryoSerializable
import com.esotericsoftware.kryo.io.Output
import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.io.Input

class UserBriefData(userData : UserRegData) extends Serializable with KryoSerializable  {
    var id:				      String = userData.id
    var clientVersion:  Long   = userData.clientVersion
    var clientIp:       String = userData.clientIp
    var timestamp:      scala.Long   = userData.timestamp
    var phonePrefix:    String = userData.phonePrefix
    var nickName:       String = userData.nickName
    var deviceId:       String = userData.deviceId
    var deviceType:       String = userData.deviceType
    var ssidMac:        String = userData.ssidMac
  
    def this(uid : String) = {
      this(new UserRegData(""))
      this.id = uid
    }
    
    override def toString(): String = {
        "{id: " + id + "\t clientVersion: " + clientVersion.toString() + 
        "\t clientIp: " + clientIp + "\t timestamp: " + timestamp.toString() +
        "\t phonePrefix: " + phonePrefix + "\t nickName: " + nickName + 
        "\t deviceId: " + deviceId + "\t ssidMac: " + "\t deviceType: " + 
        deviceType + "\t ssidMac: " + ssidMac + "}"
    }
    
    override def write(kryo: Kryo, output: Output): Unit = {
      kryo.writeObject(output, this.id)
      kryo.writeObject(output, this.clientVersion)
      kryo.writeObject(output, this.clientIp)
      kryo.writeObject(output, this.timestamp)
      kryo.writeObject(output, this.phonePrefix)
      kryo.writeObject(output, this.nickName)
      kryo.writeObject(output, this.deviceId)
      kryo.writeObject(output, this.deviceType)
      kryo.writeObject(output, this.ssidMac)
    }
  
    override def read(kryo: Kryo, input: Input): Unit = {
      this.id = kryo.readObject(input, classOf[String])
      this.clientVersion = kryo.readObject(input, classOf[Long])
      this.clientIp = kryo.readObject(input, classOf[String])
      this.timestamp = kryo.readObject(input, classOf[Long])
      this.phonePrefix = kryo.readObject(input, classOf[String])
      this.nickName = kryo.readObject(input, classOf[String])
      this.deviceId = kryo.readObject(input, classOf[String])
      this.deviceType = kryo.readObject(input, classOf[String])
      this.ssidMac = kryo.readObject(input, classOf[String])
    }
}
