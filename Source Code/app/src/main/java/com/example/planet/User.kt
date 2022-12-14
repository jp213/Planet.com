package com.example.planet


/*
This class is used to store the information about each user. For now it holds their name and email
address but will be used to store more information about each user.
 */
class User {
    var firstName: String
    var lastName: String
    var email: String
    var screenshots : List<String>



    // Empty Constructor
    constructor() : this("", "", "", ArrayList<String>())

    // Constructor when given name and email.
    constructor(firstName: String, lastName: String, email: String, screenshots : List<String>) {
        this.firstName = firstName
        this.lastName = lastName
        this.email = email
        this.screenshots = screenshots
    }
}