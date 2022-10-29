package dgtc.repository.cache;

import dgtc.config.cache.CacheConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Slf4j
@Repository
@RequiredArgsConstructor
public class AuctionCache {

  private static final String AUCTION_STATUS_PREFIX = "auction:status:";

  private static final String AUCTION_STEP_30_STATUS_PREFIX = "auction:step:30:status:";

  private static final String AUCTION_STEP_30_TIMEOUT_PREFIX = "auction:step:30:timeout:";

  private static final String AUCTION_STEP_5X1_STATUS_PREFIX = "auction:step:5x1:status:";

  private static final String AUCTION_STEP_5X1_TIMEOUT_PREFIX = "auction:step:5x1:timeout:";

  private static final String AUCTION_STEP_5X2_STATUS_PREFIX = "auction:step:5x2:status:";

  private static final String AUCTION_STEP_5X2_TIMEOUT_PREFIX = "auction:step:5x2:timeout:";

  private static final String AUCTION_STEP_5X3_STATUS_PREFIX = "auction:step:5x3:status:";

  private static final String AUCTION_STEP_5X3_TIMEOUT_PREFIX = "auction:step:5x3:timeout:";

  private final CacheConfig cacheConfig;
  private final StringRedisTemplate redisTemplate;

  public void setAuctionStatus(String key, String value) {
    redisTemplate
        .boundValueOps(generateKey(AUCTION_STATUS_PREFIX, key))
        .set(value, cacheConfig.getTtlAuctionStatus(), cacheConfig.getTimeUnitAuctionStatus());
  }

  public String getAuctionStatus(String key) {
    return redisTemplate.boundValueOps(generateKey(AUCTION_STATUS_PREFIX, key)).get();
  }

  public void setAuctionStep30Status(String key, String value) {
    redisTemplate
        .boundValueOps(generateKey(AUCTION_STEP_30_STATUS_PREFIX, key))
        .set(
            value,
            cacheConfig.getTtlAuctionStepStatus(),
            cacheConfig.getTimeUnitAuctionStepStatus());
  }

  public String getAuctionStep30Status(String key) {
    return redisTemplate.boundValueOps(generateKey(AUCTION_STEP_30_STATUS_PREFIX, key)).get();
  }

  public void setAuctionStep30Timeout(String key, String value) {
    redisTemplate
        .boundValueOps(generateKey(AUCTION_STEP_30_TIMEOUT_PREFIX, key))
        .set(value, 30, TimeUnit.SECONDS);
  }

  public String getAuctionStep30Timeout(String key) {
    return redisTemplate.boundValueOps(generateKey(AUCTION_STEP_30_TIMEOUT_PREFIX, key)).get();
  }

  public void setAuctionStep5x1Status(String key, String value) {
    redisTemplate
        .boundValueOps(generateKey(AUCTION_STEP_5X1_STATUS_PREFIX, key))
        .set(
            value,
            cacheConfig.getTtlAuctionStepStatus(),
            cacheConfig.getTimeUnitAuctionStepStatus());
  }

  public String getAuctionStep5x1Status(String key) {
    return redisTemplate.boundValueOps(generateKey(AUCTION_STEP_5X1_STATUS_PREFIX, key)).get();
  }

  public void setAuctionStep5x1Timeout(String key, String value) {
    redisTemplate
        .boundValueOps(generateKey(AUCTION_STEP_5X1_TIMEOUT_PREFIX, key))
        .set(value, 5, TimeUnit.SECONDS);
  }

  public String getAuctionStep5x1Timeout(String key) {
    return redisTemplate.boundValueOps(generateKey(AUCTION_STEP_5X1_TIMEOUT_PREFIX, key)).get();
  }

  public void setAuctionStep5x2Status(String key, String value) {
    redisTemplate
        .boundValueOps(generateKey(AUCTION_STEP_5X2_STATUS_PREFIX, key))
        .set(
            value,
            cacheConfig.getTtlAuctionStepStatus(),
            cacheConfig.getTimeUnitAuctionStepStatus());
  }

  public String getAuctionStep5x2Status(String key) {
    return redisTemplate.boundValueOps(generateKey(AUCTION_STEP_5X2_STATUS_PREFIX, key)).get();
  }

  public void setAuctionStep5x2Timeout(String key, String value) {
    redisTemplate
        .boundValueOps(generateKey(AUCTION_STEP_5X2_TIMEOUT_PREFIX, key))
        .set(value, 5, TimeUnit.SECONDS);
  }

  public String getAuctionStep5x2Timeout(String key) {
    return redisTemplate.boundValueOps(generateKey(AUCTION_STEP_5X2_TIMEOUT_PREFIX, key)).get();
  }

  public void setAuctionStep5x3Status(String key, String value) {
    redisTemplate
        .boundValueOps(generateKey(AUCTION_STEP_5X3_STATUS_PREFIX, key))
        .set(
            value,
            cacheConfig.getTtlAuctionStepStatus(),
            cacheConfig.getTimeUnitAuctionStepStatus());
  }

  public String getAuctionStep5x3Status(String key) {
    return redisTemplate.boundValueOps(generateKey(AUCTION_STEP_5X3_STATUS_PREFIX, key)).get();
  }

  public void setAuctionStep5x3Timeout(String key, String value) {
    redisTemplate
        .boundValueOps(generateKey(AUCTION_STEP_5X3_TIMEOUT_PREFIX, key))
        .set(value, 5, TimeUnit.SECONDS);
  }

  public String getAuctionStep5x3Timeout(String key) {
    return redisTemplate.boundValueOps(generateKey(AUCTION_STEP_5X3_TIMEOUT_PREFIX, key)).get();
  }

  public void resetAllStep(String auctionId) {
    redisTemplate.delete(generateKey(AUCTION_STEP_30_STATUS_PREFIX, auctionId));
    redisTemplate.delete(generateKey(AUCTION_STEP_30_TIMEOUT_PREFIX, auctionId));
    redisTemplate.delete(generateKey(AUCTION_STEP_5X1_STATUS_PREFIX, auctionId));
    redisTemplate.delete(generateKey(AUCTION_STEP_5X1_TIMEOUT_PREFIX, auctionId));
    redisTemplate.delete(generateKey(AUCTION_STEP_5X2_STATUS_PREFIX, auctionId));
    redisTemplate.delete(generateKey(AUCTION_STEP_5X2_TIMEOUT_PREFIX, auctionId));
    redisTemplate.delete(generateKey(AUCTION_STEP_5X3_STATUS_PREFIX, auctionId));
    redisTemplate.delete(generateKey(AUCTION_STEP_5X3_TIMEOUT_PREFIX, auctionId));
  }

  public Long getAuctionStep30RemainTime(String auctionId) {
    return redisTemplate.getExpire(
        generateKey(AUCTION_STEP_30_TIMEOUT_PREFIX, auctionId), TimeUnit.MILLISECONDS);
  }

  public Long getAuctionStep5x1RemainTime(String auctionId) {
    return redisTemplate.getExpire(
        generateKey(AUCTION_STEP_5X1_TIMEOUT_PREFIX, auctionId), TimeUnit.MILLISECONDS);
  }

  public Long getAuctionStep5x2RemainTime(String auctionId) {
    return redisTemplate.getExpire(
        generateKey(AUCTION_STEP_5X2_TIMEOUT_PREFIX, auctionId), TimeUnit.MILLISECONDS);
  }

  public Long getAuctionStep5x3RemainTime(String auctionId) {
    return redisTemplate.getExpire(
        generateKey(AUCTION_STEP_5X3_TIMEOUT_PREFIX, auctionId), TimeUnit.MILLISECONDS);
  }

  private String generateKey(String prefixKey, String auctionId) {
    return new StringBuilder()
        .append(cacheConfig.getServiceCachePrefix())
        .append(":")
        .append(prefixKey)
        .append(auctionId)
        .toString();
  }
}
