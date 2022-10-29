package dgtc.repository.cache;

import dgtc.config.cache.CacheConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.util.TextUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class TokenCache {

  private static final String PREFIX_BLACKLIST = "blacklist:";

  private final CacheConfig cacheConfig;
  private final StringRedisTemplate redisTemplate;

  public void addToBlackList(String userId, String token) {
    if (TextUtils.isEmpty(userId) || TextUtils.isEmpty(token)) return;

    redisTemplate
        .boundValueOps(generateBlackListKey(userId, token))
        .set("true", cacheConfig.getTtlTokenList(), cacheConfig.getTimeUnitTokenList());
  }

  public boolean isBlackList(String userId, String token) {
    if (TextUtils.isEmpty(token) || TextUtils.isEmpty(userId)) return true;
    String value = redisTemplate.boundValueOps(generateBlackListKey(userId, token)).get();
    return !TextUtils.isEmpty(value) && "true".equals(value);
  }

  private String generateBlackListKey(String userId, String token) {
    return new StringBuilder()
        .append(cacheConfig.getServiceCachePrefix())
        .append(":")
        .append(PREFIX_BLACKLIST)
        .append(userId)
        .append(":")
        .append(token)
        .toString();
  }
}
