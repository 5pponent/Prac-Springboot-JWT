package jwt.prac.auth

import io.jsonwebtoken.Header
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import java.lang.RuntimeException
import java.util.*
import javax.crypto.spec.SecretKeySpec
import javax.servlet.http.HttpServletRequest
import javax.xml.bind.DatatypeConverter

@Component
class JwtProvider {

    private val secretKey = "asijidoj2SDJFOScxbpxc[bwkpwqFDJGOsdapfwqepkxcbvo-42kpfkd["

    fun createToken(subject: String, expTime: Long): String {
        if (expTime <= 0) throw RuntimeException("만료시간은 0보다 커야 함")

        val signatureAlgorithm = SignatureAlgorithm.HS256
        val secretKeyByte = DatatypeConverter.parseBase64Binary(secretKey)
        val signingKey = SecretKeySpec(secretKeyByte, signatureAlgorithm.jcaName)

        return Jwts.builder()
            .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
            .setIssuer("opponent")
            .setSubject(subject)
            .setIssuedAt(Date())
            .setExpiration(Date(System.currentTimeMillis() + expTime))
            .signWith(signingKey)
            .compact()
    }

    fun getToken(request: HttpServletRequest): String? =
        request.getHeader(HttpHeaders.AUTHORIZATION)

    fun getSubject(token: String): String =
        Jwts.parserBuilder()
            .setSigningKey(DatatypeConverter.parseBase64Binary(secretKey))
            .build()
            .parseClaimsJws(token)
            .body.subject

    // 토큰의 유효성 + 만료일자 확인
    fun validateToken(token: String): Boolean {
        return try {
            !Jwts.parserBuilder()
                .setSigningKey(DatatypeConverter.parseBase64Binary(secretKey))
                .build()
                .parseClaimsJws(token)
                .body
                .expiration
                .before(Date())
        } catch (e: Exception) {
            false
        }
    }
}