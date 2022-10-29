/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dgtc.common.genid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Interval;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

// import java.text.SimpleDateFormat;

public class GenIDProducer extends Thread {
  private static final Logger LOGGER = LogManager.getLogger(GenIDProducer.class);
  private static final SimpleDateFormat GENID_PARSE_SDF =
      new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
  private static final SimpleDateFormat DATE_FORMAT_GMT7 =
      new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

  static {
    DATE_FORMAT_GMT7.setTimeZone(TimeZone.getTimeZone("GMT+7"));
  }

  private GenIDDAO genIDDAO;
  private GenIDBuff buffer;
  private boolean running = true;

  public GenIDProducer(GenIDBuff buffer, GenIDDAO genIDDAO) {
    this.buffer = buffer;
    this.genIDDAO = genIDDAO;
  }

  public static Date getCurDateWithMilisec() throws ParseException {
    Date date = new Date(System.currentTimeMillis());
    Date result = GENID_PARSE_SDF.parse(DATE_FORMAT_GMT7.format(date));
    return result;
  }

  public void run() {
    while (running) {
      GetRangeIDResult result = new GetRangeIDResult();
      try {
        buffer.longLockNextRange.lock();
        try {
          if (buffer.isNextRangeBufferd == false) {
            result = genIDDAO.getRangeID();

            if (result.pResult == 0) {
              //              LOGGER.error("Call getRangeID return 0");
              continue;
            } else {
              //              LOGGER.info("getRangeID: pResult=" + result.pResult
              //                       + " pStart=" + result.pStart
              //                       + " pSize=" + result.pSize
              //                       + " pDate=" + simpleDateFormat.format(result.pDate)
              //                       + " pDateTime=" + simpleDateFormat.format(result.pDateTime));
            }

            Date now = getCurDateWithMilisec();
            Date dbNow = result.pDateTime;

            buffer.nextRangeDate = result.pDate;
            buffer.nextRangeStart = result.pStart;
            buffer.nextRangeSize = result.pSize;

            DateTime jodaNextRangeDate = new DateTime(buffer.nextRangeDate);
            DateTime jodaDBNow = new DateTime(dbNow);
            Interval interval = new Interval(jodaDBNow, jodaNextRangeDate.plusDays(1));
            DateTime jodaNow = new DateTime(now);
            DateTime jodaNextRangeExpiredDate = jodaNow.plus(interval.toDuration());

            buffer.nextRangeExpiredDate = jodaNextRangeExpiredDate.toDate();
            buffer.isNextRangeBufferd = true;
            buffer.semaphoreTotalCountLeft.release(buffer.nextRangeSize);
          }
        } finally {
          buffer.longLockNextRange.unlock();
        }
      } catch (Exception e) {
        LOGGER.error("GenIDProducer exception ", e);
        continue;
      } finally {
        try {
          synchronized (this) {
            this.wait();
          }
        } catch (Exception ex) {
        }
      }
    }
  }

  public void shutDown() {
    running = false;
  }
}
