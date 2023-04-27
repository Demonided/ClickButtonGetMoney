package com.katoklizm.goldclickbutton.models

public class User {
    var name: String? = null
    var email: String? = null
    var pass: String? = null
    var phone: String? = null

    constructor() {}
    constructor(name: String?, email: String?, pass: String?, phone: String?) {
        this.name = name
        this.email = email
        this.pass = pass
        this.phone = phone
    }
}