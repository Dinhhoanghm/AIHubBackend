//package ongoing.backend.config.authentication;
//
//
//import ongoing.backend.config.jackson.json.JsonObject;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.web.AuthenticationEntryPoint;
//import org.springframework.stereotype.Component;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.util.HashMap;
//import java.util.Map;
//
//import static org.springframework.http.HttpStatus.UNAUTHORIZED;
//
//@Component
//public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
//
//    @Override
//    public void commence(HttpServletRequest request, HttpServletResponse response,
//                         AuthenticationException authException) throws IOException {
//        response.setStatus(UNAUTHORIZED.value());
//        Map<String, Object> data = new HashMap<>();
//        data.put("code", UNAUTHORIZED.value());
//        data.put("message", "INVALID_USERNAME_PASS");
//        response.getOutputStream().println(new JsonObject(data).encode());
//    }
//}
