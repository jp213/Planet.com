package com.example.loginscreen


class User {
    var firstName :String
    var lastName : String
    var email : String
    constructor() : this("","","")

    constructor(firstName : String, lastName: String, email : String) {
        this.firstName = firstName
        this.lastName = lastName
        this.email = email
    }
}

