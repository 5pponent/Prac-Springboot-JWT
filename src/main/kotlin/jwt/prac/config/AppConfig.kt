package jwt.prac.config

import jwt.prac.auth.JwtProvider
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Configuration
class AppConfig(private val jwtProvider: JwtProvider): WebMvcConfigurer {

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry
            .addInterceptor(JwtInterceptor(jwtProvider))
            .excludePathPatterns("/login", "/join")
    }
}

class JwtInterceptor(private val jwtProvider: JwtProvider): HandlerInterceptor {

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean =
        jwtProvider.getToken(request)?.let { jwtProvider.validateToken(it) }
            ?: throw RuntimeException("로그인이 필요한 서비스입니다.")
}