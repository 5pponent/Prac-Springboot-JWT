package jwt.prac.controller

import jwt.prac.auth.JwtProvider
import jwt.prac.domain.User
import jwt.prac.model.LoginForm
import jwt.prac.model.TokenResponse
import jwt.prac.model.UserRequest
import jwt.prac.model.UserResponse
import jwt.prac.repository.UserRepository
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest
import kotlin.RuntimeException

@RestController
class BaseController(
    private val userRepository: UserRepository,
    private val jwtProvider: JwtProvider
) {

    @PostMapping("/login")
    fun login(@RequestBody form: LoginForm): TokenResponse =
        userRepository.findByUsernameAndPassword(form.username, form.password)?.let {
            TokenResponse(jwtProvider.createToken(it.username, 1 * 1000 * 60))
        } ?: throw RuntimeException("아이디/비밀번호를 확인해주세요.")

    @PostMapping("/join")
    fun join(@RequestBody form: UserRequest) {
        if (!form.checkPassword()) throw RuntimeException("비밀번호가 일치하지 않습니다.")
        userRepository.save(
            User(
                username = form.username,
                password = form.password,
                name = form.name
            )
        )
    }
}

@RestController
@RequestMapping("/api")
class ApiController(
    private val jwtProvider: JwtProvider,
    private val userRepository: UserRepository
) {

    @GetMapping("/info")
    fun getUserInfo(request: HttpServletRequest): UserResponse =
        jwtProvider.getToken(request)?.let { token ->
            userRepository.findByUsername(jwtProvider.getSubject(token))?.let { user ->
                UserResponse(user)
            } ?: throw RuntimeException("해당 유저가 존재하지 않습니다.")
        } ?: throw RuntimeException("로그인이 필요한 서비스입니다.")
}