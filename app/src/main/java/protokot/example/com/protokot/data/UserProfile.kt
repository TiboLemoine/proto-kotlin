package protokot.example.com.protokot.data

/**
 * Network object representing a user profile
 */
class UserProfile(val id : String, val email : String, val books : List<Int>, val canBorrow : Boolean, val lastName : String, val firstName : String)