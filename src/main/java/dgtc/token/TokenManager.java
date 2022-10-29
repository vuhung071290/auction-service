package dgtc.token;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import dgtc.config.AuthConfig;
import dgtc.entity.dto.Token;
import dgtc.repository.cache.TokenCache;
import dgtc.utils.ValidUtils;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

@Component
public class TokenManager {

  private final AuthConfig authConfig;
  private final TokenCache tokenCache;

  public TokenManager(AuthConfig authConfig, TokenCache tokenCache) {
    this.authConfig = authConfig;
    this.tokenCache = tokenCache;
  }

  public Token generateAdminToken(String domainName, String displayName) {
    Algorithm algorithm = Algorithm.HMAC256(authConfig.getSecretKey().getBytes());
    long expireTime = System.currentTimeMillis() + authConfig.getExpireTimeMills();

    String token =
        JWT.create()
            .withClaim("domainName", domainName)
            .withClaim("displayName", displayName)
            .withClaim("role", "admin")
            .withExpiresAt(new Date(expireTime))
            .sign(algorithm);

    return new Token(token, expireTime);
  }

  public Token generateUserToken(Long userId) {
    Algorithm algorithm = Algorithm.HMAC256(authConfig.getSecretKey().getBytes());
    long expireTime = System.currentTimeMillis() + authConfig.getExpireTimeMills();

    String token =
        JWT.create()
            .withClaim("role", "user")
            .withClaim("userId", userId)
            .withExpiresAt(new Date(expireTime))
            .sign(algorithm);

    return new Token(token, expireTime);
  }

  public Map<String, Claim> parseToken(String token) {
    DecodedJWT decoded = JWT.decode(token);
    return decoded.getClaims();
  }

  public UserToken parseUserTokenHaveBearer(String token) {
    String tokenNotBearer = token.substring("Bearer ".length());
    return parseUserToken(tokenNotBearer);
  }

  public UserToken parseUserToken(String token) {
    try {
      DecodedJWT decoded = JWT.decode(token);
      Map<String, Claim> claims = decoded.getClaims();
      String role = claims.get("role").asString();
      Long userId = claims.get("userId").asLong();
      if (ValidUtils.oneOfThemEmpty(role) || userId == null || userId < 0L) return null;
      boolean isExpire = decoded.getExpiresAt().before(new Date());
      return UserToken.create(userId, role, isExpire, isBlackList(userId, token));
    } catch (RuntimeException exception) {
      return null;
    }
  }

  public void addToBlackList(Long userId, String tokenHaveBearer) {
    String tokenNotBearer = tokenHaveBearer.substring("Bearer ".length());
    tokenCache.addToBlackList(String.valueOf(userId), tokenNotBearer);
  }

  public void addToBlackListWithNoBearer(Long userId, String tokenNoBearer) {
    tokenCache.addToBlackList(String.valueOf(userId), tokenNoBearer);
  }

  private boolean isBlackList(Long userId, String tokenNotBearer) {
    return tokenCache.isBlackList(String.valueOf(userId), tokenNotBearer);
  }
}
