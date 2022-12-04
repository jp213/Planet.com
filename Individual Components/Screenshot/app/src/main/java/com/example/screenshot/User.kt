package com.example.screenshot

/*
This class is used to store the information about each user. For now it holds their name and email
address but will be used to store more information about each user.
 */
class User {
    var firstName: String
    var lastName: String
    var email: String

    // Empty Constructor
    constructor() : this("", "", "")

    // Constructor when given name and email.
    constructor(firstName: String, lastName: String, email: String) {
        this.firstName = firstName
        this.lastName = lastName
        this.email = email
    }
}

