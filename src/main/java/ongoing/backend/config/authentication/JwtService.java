//package ongoing.backend.config.authentication;
//
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.SignatureAlgorithm;
//import lombok.extern.log4j.Log4j2;
//import ongoing.backend.config.model.SimpleSecurityUser;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.stereotype.Service;
//
//import java.util.*;
//
//import static ongoing.backend.config.jackson.JsonMapper.getObjectMapper;
//import static ongoing.backend.config.jackson.json.JsonObject.mapFrom;
//
//
//@Log4j2
//@Service
//public class JwtService {
//    @Value("${spring.security.jwt.secret-key}")
//    private String secretKey;
//    @Value("${spring.security.jwt.expired-in}")
//    private Long expiresIn;
//
//    public UsernamePasswordAuthenticationToken extractAuthentication(String token, boolean showLog) {
//        try {
//            SimpleSecurityUser user = extractSecurityUser(token);
//            if (user != null) {
//                user.setShowLog(showLog);
//                return new UsernamePasswordAuthenticationToken(user.getId(), user, getAuthorities(user));
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//        return null;
//    }
//
//
//    public SimpleSecurityUser extractSecurityUser(String token) {
//        if (token == null || StringUtils.isBlank(token)) return null;
//        try {
//            Claims body = Jwts.parser()
//                    .setSigningKey(secretKey)
//                    .parseClaimsJws(token)
//                    .getBody();
//
//            String userString = (String) body.get("user");
//            return getObjectMapper().readValue(userString, SimpleSecurityUser.class);
//        } catch (Exception e) {
//            log.error("");
//            return null;
//        }
//    }
//
//
//    private List<SimpleGrantedAuthority> getAuthorities(SimpleSecurityUser user) {
//        return Optional.ofNullable(user.getRoles())
//                .orElse(Collections.emptyList())
//                .stream()
//                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
//                .toList();
//    }
//
//    public String generateJwt(SimpleSecurityUser securityUser) {
//        Map<String, Object> claims = new HashMap<>();
//        putAuthentication(claims, securityUser);
//
//        return Jwts.builder()
//                .setSubject(securityUser.getId())
//                .setClaims(claims)
//                .setExpiration(new Date(System.currentTimeMillis() + expiresIn))
//                .signWith(SignatureAlgorithm.HS256, secretKey)
//                .compact();
//    }
//
//    private void putAuthentication(Map<String, Object> claims, SimpleSecurityUser securityUser) {
//        claims.put("user", mapFrom(securityUser).encode());
//    }
//}
