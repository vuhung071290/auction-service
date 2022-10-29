package dgtc.common.genid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public final class GenIdUtil {
  private static final Logger LOGGER = LogManager.getLogger(GenIdUtil.class);

  @Autowired private GenIDConsumer genIDConsumer;

  /**
   * generate uid
   *
   * @return unique ID
   */
  public long genUID() {
    try {
      final GenIDResult genIDResult = genIDConsumer.getGenIDResult();
      if (genIDResult.result) {
        return genIDResult.id;
      } else {
        // make another try
        final GenIDResult secondResult = genIDConsumer.getGenIDResult();
        if (secondResult.result) {
          return genIDResult.id;
        }
      }
      LOGGER.error("genUID result is false");
    } catch (Exception ex) {
      LOGGER.error("genUID exception", ex);
    }
    // should not go here
    throw new RuntimeException("genUID result is 0");
  }
}
