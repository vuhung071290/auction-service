package dgtc.security;

import dgtc.config.AppConfig;
import dgtc.pers.UserPersistence;
import dgtc.service.admin.AccountService;
import dgtc.service.admin.AuthService;
import dgtc.service.admin.PermissionService;
import dgtc.token.TokenManager;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/** @author hunglv */
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Component
@RequiredArgsConstructor
public class RestSecurity extends WebSecurityConfigurerAdapter {

  private final AccountService accountService;
  private final AuthService authService;
  private final PermissionService permissionService;
  private final AppConfig appConfig;
  private final TokenManager tokenManager;
  private final UserPersistence userPersistence;

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.cors()
        .and()
        .csrf()
        .disable()
        .authorizeRequests()
        .antMatchers(
            HttpMethod.POST,
            "/api/auth",
            "/api/v1/register",
            "/api/v1/login",
            "/api/v1/refresh",
            "/api/v1/me/active",
            "/api/v1/user/forgot-password",
            "/api/v1/user/resend-otp")
        .permitAll()
        .antMatchers(
            HttpMethod.GET,
            "/api/config",
            "/api/storage/view-image/**",
            "/api/storage/download-file/**",
            "/api/auction/**")
        .permitAll()
        .antMatchers("/api/**")
        .authenticated()
        .antMatchers("/api/v1/**")
        .authenticated()
        .and()
        .addFilter(
            new JWTAuthorizationFilter(
                accountService,
                userPersistence,
                tokenManager,
                permissionService,
                authenticationManager()))
        // this disables session creation on Spring Security
        .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    CorsConfiguration config = new CorsConfiguration();
    config.applyPermitDefaultValues().addAllowedMethod(HttpMethod.POST);
    config.addAllowedMethod(HttpMethod.PUT);
    config.addAllowedMethod(HttpMethod.GET);
    config.addAllowedMethod(HttpMethod.DELETE);
    config.addAllowedHeader("*");
    config.addAllowedOrigin("*");
    source.registerCorsConfiguration("/**", config);
    return source;
  }
}
