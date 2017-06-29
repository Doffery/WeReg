package com.tencent.wechat.registration.util

import com.tencent.wechat.registration.preprocessing.IPParse
import java.lang.Long
import com.esotericsoftware.kryo.KryoSerializable
import com.esotericsoftware.kryo.io.Output
import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.io.Input


class UserRegData(
    s: String,
    dType: DataFileType.Value = DataFileType.DataVisor) extends Serializable with KryoSerializable {
    var shortId:        scala.Long   = 0L
    var id:				      String = ""
    var clientVersion:  scala.Long   = 0L
    var clientIp:       String = ""
    var agentIp:        String = ""
    var timestamp:      scala.Long   = 0L
    var phonePrefix:    String = ""
    var timezone:       String = ""
    var lang:           String = ""
    var nickName:       String = ""
    var regcountry:     String = ""
    var realcountry:    String = ""
    var alias:          String = ""
    var heading:        Boolean= false
    var adsource:       String = ""
    var androidId:      String = ""
    var macAdd:         String = ""
    var googleAid:      String = ""
    var deviceId:       String = ""
    var deviceType:     String = ""
    var pwdHash:        String = ""
    var phoneCountry:   String = ""
    var phoneProvince:  String = ""
    var phoneCity:      String = ""
    var ipCountry:      String = ""
    var ipProvince:     String = ""
    var ipCity:         String = ""
    var imei:           String = ""
    var cpu:            String = ""
    var cpuFlag:        String = ""
    var ssid:           String = ""
    var ssidMac:        String = ""
    
    def this(s: String) = this(s, DataFileType.Full)
    
    //println(s)
    
    if(s != "") {
        val ssplit = s.split("\t", -1);
        if(dType == DataFileType.Full) {
            this.id = ssplit(0);
            this.clientVersion = Long.parseLong(ssplit(1));
            this.clientIp = IPParse.ipParse(ssplit(2));
            this.timestamp = Long.parseLong(ssplit(4));
            this.phonePrefix = ssplit(5);
            this.nickName = ssplit(8);
            this.adsource = ssplit(14);
            this.androidId = ssplit(15);
            this.macAdd = ssplit(16);
            this.deviceId = ssplit(20);
            this.deviceType = ssplit(21);
            this.pwdHash = ssplit(22);
            this.phoneCountry = (ssplit(24));
            this.phoneProvince = (ssplit(25));
            this.phoneCity = (ssplit(26));
            this.ipCountry = (ssplit(27));
            this.ipProvince = (ssplit(28));
            this.ipCity = (ssplit(29));
            this.imei = ssplit(30);
            this.ssid = ssplit(34);
            this.ssidMac = ssplit(35);
            this.shortId = Long.parseLong(ssplit.last)
        } else if(dType == DataFileType.DataVisor) {
            this.id = ssplit(1);
            this.timestamp = Long.parseLong(ssplit(2));
            this.clientIp = IPParse.ipParse(ssplit(4));
            this.clientVersion = Long.parseLong(ssplit(5));
            this.phonePrefix = ssplit(6);
            this.nickName = ssplit(10);
            this.ssidMac = ssplit(12);
            this.deviceId = ssplit(14);
            this.deviceType = ssplit(16)
            this.shortId = Long.parseLong(ssplit.last)
        }
        //println("success")
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
      kryo.writeObject(output, this.shortId)
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
      this.shortId = kryo.readObject(input, classOf[Long])
    }
}
