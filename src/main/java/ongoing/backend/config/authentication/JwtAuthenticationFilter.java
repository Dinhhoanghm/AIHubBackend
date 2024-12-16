//package ongoing.backend.config.authentication;
//
//import ongoing.backend.config.model.SimpleSecurityUser;
//import org.apache.commons.lang3.math.NumberUtils;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.GenericFilterBean;
//
//import javax.servlet.FilterChain;
//import javax.servlet.ServletException;
//import javax.servlet.ServletRequest;
//import javax.servlet.ServletResponse;
//import javax.servlet.http.HttpServletRequest;
//import java.io.IOException;
//
//import static java.util.Collections.emptyList;
//import static org.apache.commons.lang3.StringUtils.isEmpty;
//
//@Component
//public class JwtAuthenticationFilter extends GenericFilterBean {
//    @Value("${spring.security.server.key}")
//    private String serverKey;
//    private final JwtService jwtService;
//
//    public JwtAuthenticationFilter(JwtService jwtService) {
//        this.jwtService = jwtService;
//    }
//
//    @Override
//    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
//        HttpServletRequest request = (HttpServletRequest) req;
//        UsernamePasswordAuthenticationToken authentication;
//        String header = request.getHeader("Authorization");
//        if (header != null && header.contains("Bearer")) {
//            boolean showLog = (request.getHeader("show_log") != null) &&
//                    request.getHeader("show_log").equalsIgnoreCase("true");
//            String token = header.replaceFirst("Bearer", "").trim();
//            authentication = jwtService.extractAuthentication(token, showLog);
//            SecurityContextHolder.getContext().setAuthentication(authentication);
//        } else if (request.getHeader("Server-Token") != null) {
//            String serverToken = request.getHeader("Server-Token");
//            String userId = request.getHeader("user-id");
//            String bsnId = request.getHeader("bsn-id");
//            if (serverToken.equalsIgnoreCase(serverKey) && !isEmpty(userId)) {
//                SimpleSecurityUser simpleSecurityUser = new SimpleSecurityUser()
//                        .setId(userId)
//                        .setBsnId(NumberUtils.createInteger(bsnId));
//                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userId,
//                        simpleSecurityUser, emptyList());
//                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
//            }
//        }
//        chain.doFilter(req, res);
//    }
//}
//
//
