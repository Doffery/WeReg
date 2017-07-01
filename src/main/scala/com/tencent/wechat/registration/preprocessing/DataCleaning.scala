package com.tencent.wechat.registration.preprocessing

object DataCleaning {
    def ipParse(sp : String) : String = {
        sp
    }
    
    /*
     * Mainly focus on */
    def deviceTypeCleaning(dt : String) : String = {
        if (dt.indexOf('.') != -1)
            dt.substring(0, dt.indexOf('.'))
        else dt
    }
    
    def phonePrefix(pn : String) : String = {
        if(pn.size > 3)
            pn.substring(0, pn.size-4)
        else pn
    }
}