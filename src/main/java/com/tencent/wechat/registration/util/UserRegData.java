package com.tencent.wechat.registration.util;

import java.io.Serializable;

import com.tencent.wechat.registration.preprocessing.*;

/*
 * 	uinhash_  �û�id               
	clientversion_ �ͻ��˰汾      
	clientip_           �ͻ���ip
	agentip_            agenIP,�ɺ���
	timestamp_            ʱ���          
	regid                       �ֻ��Ŷ�
	timezone_           ʱ��
	language_           ����
	nickname_           �ǳ�
	regcountry_         ע�����
	realcountry_        �ɺ���
	alias_              alias��΢�ź�
	hasheadimg_         ͷ��
	clientseqid_        seqid���ɺ���
	adsource_           Ӳ��adsourceID
	androidid_          �豸androidid
	macaddr_            mac��ַ
	androidinstallref_    Ӳ����װ��ַ
	googleaid_          googleaid
	bundleid_           ��װ��bundleid
	deviceid_           �豸id
	devicetype_         �豸����
	pwdhash_            ����hash
	timezonename_       ʱ����
	regmobilecountryid_  �ֻ�����id
	regmobileprovinceid_ �ֻ�ʡ��id
	regmobilecityid_    �ֻ�����id
	regipcountryid_     ע��IP����id
	regipprovinceid_    ע��IPʡ��id
	regipcityid_        ע��IP����id
	imei_               imei
	cpu_                cpu
	cpuflags_           CPU���
	imsi_               imsi
	ssid_               ssid wifi����
	bssid_              bssid wifiapi��mac��ַ
 * */


public class UserRegData implements Serializable {
	String 		id;
	int 		clientVersion;
	String 		clientIp;
	String		agentIp;
	int 		timestamp;
	String		phonePrefix;
	String		timezone;
	String		lang;
	String		nickName;
	String		regcountry;
	String		realcountry;
	String		alias;
	Boolean		heading;
	String		adsource;
	String		androidId;
	String		macAdd;
	String		googleAid;
	String		deviceId;
	String		deviceType;
	String		pwdHash;
	int			phoneCountry;
	int			phoneProvince;
	int			phoneCity;
	int			ipCountry;
	int			ipProvince;
	int			ipCity;
	String		imei;
	String		cpu;
	String		cpuFlag;
	String		ssid;
	String		ssidMac;
	
	public UserRegData(String s) {
		String ssplit[] = s.split("\t");
		this.id = ssplit[0];
		this.clientVersion = Integer.parseInt(ssplit[1]);
		this.clientIp = IPParse.ipParse(Integer.parseInt(ssplit[2]));
		this.timestamp = Integer.parseInt(ssplit[4]);
		this.phonePrefix = ssplit[5];
		this.nickName = ssplit[8];
		this.adsource = ssplit[14];
		this.androidId = ssplit[15];
		this.macAdd = ssplit[16];
		this.deviceId = ssplit[20];
		this.deviceType = ssplit[21];
		this.pwdHash = ssplit[22];
		this.phoneCountry = Integer.parseInt(ssplit[24]);
		this.phoneProvince = Integer.parseInt(ssplit[25]);
		this.phoneCity = Integer.parseInt(ssplit[26]);
		this.ipCountry = Integer.parseInt(ssplit[27]);
		this.ipProvince = Integer.parseInt(ssplit[28]);
		this.ipCity = Integer.parseInt(ssplit[29]);
		this.imei = ssplit[30];
		this.ssid = ssplit[34];
		this.ssidMac = ssplit[35];
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getClientVersion() {
		return clientVersion;
	}

	public void setClientVersion(int clientVersion) {
		this.clientVersion = clientVersion;
	}

	public String getClientIp() {
		return clientIp;
	}

	public void setClientIp(String clientIp) {
		this.clientIp = clientIp;
	}

	public String getAgentIp() {
		return agentIp;
	}

	public void setAgentIp(String agentIp) {
		this.agentIp = agentIp;
	}

	public int getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(int timestamp) {
		this.timestamp = timestamp;
	}

	public String getPhonePrefix() {
		return phonePrefix;
	}

	public void setPhonePrefix(String phonePrefix) {
		this.phonePrefix = phonePrefix;
	}

	public String getTimezone() {
		return timezone;
	}

	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getRegcountry() {
		return regcountry;
	}

	public void setRegcountry(String regcountry) {
		this.regcountry = regcountry;
	}

	public String getRealcountry() {
		return realcountry;
	}

	public void setRealcountry(String realcountry) {
		this.realcountry = realcountry;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public Boolean getHeading() {
		return heading;
	}

	public void setHeading(Boolean heading) {
		this.heading = heading;
	}

	public String getAdsource() {
		return adsource;
	}

	public void setAdsource(String adsource) {
		this.adsource = adsource;
	}

	public String getAndroidId() {
		return androidId;
	}

	public void setAndroidId(String androidId) {
		this.androidId = androidId;
	}

	public String getMacAdd() {
		return macAdd;
	}

	public void setMacAdd(String macAdd) {
		this.macAdd = macAdd;
	}

	public String getGoogleAid() {
		return googleAid;
	}

	public void setGoogleAid(String googleAid) {
		this.googleAid = googleAid;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

	public String getPwdHash() {
		return pwdHash;
	}

	public void setPwdHash(String pwdHash) {
		this.pwdHash = pwdHash;
	}

	public int getPhoneCountry() {
		return phoneCountry;
	}

	public void setPhoneCountry(int phoneCountry) {
		this.phoneCountry = phoneCountry;
	}

	public int getPhoneProvince() {
		return phoneProvince;
	}

	public void setPhoneProvince(int phoneProvince) {
		this.phoneProvince = phoneProvince;
	}

	public int getPhoneCity() {
		return phoneCity;
	}

	public void setPhoneCity(int phoneCity) {
		this.phoneCity = phoneCity;
	}

	public int getIpCountry() {
		return ipCountry;
	}

	public void setIpCountry(int ipCountry) {
		this.ipCountry = ipCountry;
	}

	public int getIpProvince() {
		return ipProvince;
	}

	public void setIpProvince(int ipProvince) {
		this.ipProvince = ipProvince;
	}

	public int getIpCity() {
		return ipCity;
	}

	public void setIpCity(int ipCity) {
		this.ipCity = ipCity;
	}

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public String getCpu() {
		return cpu;
	}

	public void setCpu(String cpu) {
		this.cpu = cpu;
	}

	public String getCpuFlag() {
		return cpuFlag;
	}

	public void setCpuFlag(String cpuFlag) {
		this.cpuFlag = cpuFlag;
	}

	public String getSsid() {
		return ssid;
	}

	public void setSsid(String ssid) {
		this.ssid = ssid;
	}

	public String getSsidMac() {
		return ssidMac;
	}

	public void setSsidMac(String ssidMac) {
		this.ssidMac = ssidMac;
	}
	
}
