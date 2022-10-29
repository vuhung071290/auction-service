package dgtc.security;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.Claim;
import dgtc.common.util.GsonUtils;
import dgtc.entity.base.Response;
import dgtc.entity.datasource.admin.Account;
import dgtc.entity.datasource.admin.AccountPermission;
import dgtc.entity.datasource.user.User;
import dgtc.pers.UserPersistence;
import dgtc.service.admin.AccountService;
import dgtc.service.admin.PermissionService;
import dgtc.token.TokenManager;
import dgtc.token.UserToken;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.util.StringUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** @author hunglv */
public class JWTAuthorizationFilter extends BasicAuthenticationFilter {

  private final Logger logger = LogManager.getLogger(JWTAuthorizationFilter.class);

  private final AccountService accountService;
  private final PermissionService permissionService;
  private final TokenManager tokenManager;
  private final UserPersistence userPersistence;

  public JWTAuthorizationFilter(
      AccountService accountService,
      UserPersistence userPersistence,
      TokenManager tokenManager,
      PermissionService permissionService,
      AuthenticationManager authManager) {
    super(authManager);
    this.accountService = accountService;
    this.permissionService = permissionService;
    this.tokenManager = tokenManager;
    this.userPersistence = userPersistence;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest req, HttpServletResponse res, FilterChain chain)
      throws IOException, ServletException {
    String header = req.getHeader("Authorization");
    if (header == null || !header.startsWith("Bearer ")) {
      chain.doFilter(req, res);
      return;
    }

    try {
      UsernamePasswordAuthenticationToken authentication = getAuthentication(req);
      SecurityContextHolder.getContext().setAuthentication(authentication);
      chain.doFilter(req, res);
    } catch (TokenExpiredException ex) {
      int status = HttpStatus.UNAUTHORIZED.value();
      res.setStatus(status);
      res.getWriter()
          .write(GsonUtils.toJson(new Response(status, "Token was expired. Please login again.")));
    }
  }

  private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request)
      throws TokenExpiredException {
    String token = request.getHeader("Authorization").replace("Bearer ", "");

    if (StringUtils.isEmpty(token)) {
      return null;
    }

    // parse the token
    Map<String, Claim> claims = tokenManager.parseToken(token);
    String role = claims.get("role").asString();
    if ("user".equals(role)) {
      return verifyForUser(token);
    } else {
      return verifyForAdmin(token);
    }
  }

  private UsernamePasswordAuthenticationToken verifyForUser(String token)
      throws TokenExpiredException {
    UserToken userToken = tokenManager.parseUserToken(token);
    Long userId = userToken.getUserId();
    if (userToken == null || userToken.isInValid()) {
      throw new TokenExpiredException("Expire token");
    }
    User user = userPersistence.getUserById(userId);
    if (user == null) {
      logger.info("User is not found");
      return null;
    }
    List<SimpleGrantedAuthority> authorities = new ArrayList<>();
    authorities.add(new SimpleGrantedAuthority(userToken.getRole()));
    if (user.getIsActive()) {
      authorities.add(new SimpleGrantedAuthority("USER_IS_ACTIVE"));
    }
    return new UsernamePasswordAuthenticationToken(user.getPhoneNumber(), null, authorities);
  }

  private UsernamePasswordAuthenticationToken verifyForAdmin(String token) {
    Map<String, Claim> claims = tokenManager.parseToken(token);
    String domainName = claims.get("domainName").asString();

    Account account = accountService.getAccountInfo(domainName);
    if (account == null || !account.getIsActive()) {
      logger.info("User {} is disabled", domainName);
      return null;
    }

    List<AccountPermission> permissionList =
        permissionService.getAllPermissionByDomainName(domainName);
    List<SimpleGrantedAuthority> authorities = new ArrayList<>();
    for (AccountPermission a : permissionList) {
      authorities.add(new SimpleGrantedAuthority(a.getFeatureActionId()));
    }

    return new UsernamePasswordAuthenticationToken(domainName, null, authorities);
  }
}
