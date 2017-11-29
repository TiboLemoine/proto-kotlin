package protokot.example.com.protokot.data

/**
 * Network object representing a request for account creation
 */
class CreateUserRequest(val firstName : String, val lastName : String, val email : String, val password : String)