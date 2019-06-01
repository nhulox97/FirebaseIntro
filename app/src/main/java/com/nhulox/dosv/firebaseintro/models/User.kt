package com.nhulox.dosv.firebaseintro.models

class User {
    var uid: String? = null
    var email: String? = null
    var displayName: String? = null


    constructor(uid: String, email: String, displayName: String){
        this.uid = uid
        this.email = email
        this.displayName = displayName
    }

    fun toMap():HashMap<String, Any>{
        var result = HashMap<String, Any>()
        result["uid"] = uid!!
        result["email"] = email!!
        result["displayName"] = displayName!!

        return result
    }
}