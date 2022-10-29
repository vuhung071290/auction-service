package dgtc.common.genid;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Component
public class GenIDConsumer implements DisposableBean {
  private static final Logger LOGGER = LogManager.getLogger(GenIDConsumer.class);

  private final GenIDConfig config;
  private final int percentLeftThreshold = 20;
  private final long timeoutDurInSeconds = 30L;
  private final Object quickLockConsumerObj = new Object();
  private long prefixMultiply;
  private GenIDBuff buffer;
  private volatile int thisRangeCurrent;
  private volatile int thisRangeCountLeft = 0;
  private Date thisRangeDate;
  private Date thisRangeExpired;

  private volatile int warningThreshold = 0;
  private long thisRangePrefix;

  private GenIDProducer producer;

  @Autowired
  public GenIDConsumer(GenIDConfig config, GenIDDAO genIDDAO) {
    this.config = config;
    int prefixMulLenght = config.postFixLength;
    prefixMultiply = 1;
    for (int i = 0; i < prefixMulLenght; i++) {
      prefixMultiply *= 10;
    }

    buffer = new GenIDBuff();
    producer = new GenIDProducer(buffer, genIDDAO);
    producer.start();
  }

  public GenIDResult getGenIDResult() {
    GenIDResult result = new GenIDResult();

    while (true) {
      synchronized (quickLockConsumerObj) {
        if (thisRangeCountLeft <= warningThreshold && buffer.isNextRangeBufferd == false) {
          if (buffer.longLockNextRange.tryLock()) {
            try {
              synchronized (producer) {
                producer.notify();
              }
            } finally {
              buffer.longLockNextRange.unlock();
            }
          }
        }
      }

      try {
        if (buffer.semaphoreTotalCountLeft.tryAcquire(timeoutDurInSeconds, TimeUnit.SECONDS)) {
          synchronized (quickLockConsumerObj) {
            if (thisRangeCountLeft == 0) {
              thisRangeCountLeft = buffer.nextRangeSize;
              thisRangeCurrent = buffer.nextRangeStart;
              thisRangeDate = new Date(buffer.nextRangeDate.getTime());
              thisRangeExpired = new Date(buffer.nextRangeExpiredDate.getTime());
              buffer.isNextRangeBufferd = false;

              String prefix = "1";
              if (StringUtils.isNotBlank(config.ymdPrefixFormat)) {
                DateFormat dateFormat = new SimpleDateFormat(config.ymdPrefixFormat);
                prefix = dateFormat.format(thisRangeDate);
              }
              thisRangePrefix = Long.valueOf(prefix) * prefixMultiply;
              warningThreshold = (percentLeftThreshold * thisRangeCountLeft) / 100;
            }

            thisRangeCountLeft--;
            thisRangeCurrent++;

            Date now = new Date(System.currentTimeMillis());

            if (now.compareTo(thisRangeExpired) <= 0) {
              result.id = thisRangePrefix + thisRangeCurrent;
              result.result = true;
              return result;
            } else {
              continue;
            }
          }
        } else {
          result.id = 0;
          result.result = false;
          LOGGER.error("GenIDConsumer tryAcquire fail: ");
          return result;
        }
      } catch (Exception ex) {
        result.id = 0;
        result.result = false;
        LOGGER.error("GenIDConsumer exception: ", ex);
        return result;
      }
    }
  }

  @Override
  public void destroy() {
    producer.shutDown();
    producer.interrupt();
  }
}
