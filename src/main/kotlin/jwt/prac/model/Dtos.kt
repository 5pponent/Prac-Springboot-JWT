package jwt.prac.model

import jwt.prac.domain.User

data class UserRequest(
    var username: String,
    var password: String,
    var passwordCheck: String,
    var name: String
) {
    fun checkPassword(): Boolean = this.passwordCheck == this.password
}

data class LoginForm(
    var username: String,
    var password: String
)

data class TokenResponse(var token: String)

data class UserResponse(
    var username: String,
    var name: String
) {
    constructor(user: User): this(
        username = user.username,
        name = user.name
    )
}