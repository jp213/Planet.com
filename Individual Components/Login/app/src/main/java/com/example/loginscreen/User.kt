package com.example.loginscreen

class User(firstName : String, lastName: String, email : String) {
    lateinit var firstName :String
    lateinit var lastName : String
    lateinit var email : String


    init {
        this.email = email
        this.firstName = firstName
        this.lastName = lastName
    }
}