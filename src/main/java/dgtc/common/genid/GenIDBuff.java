/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dgtc.common.genid;

import java.util.Date;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

public class GenIDBuff {
  public final ReentrantLock longLockNextRange = new ReentrantLock();
  public final Semaphore semaphoreTotalCountLeft = new Semaphore(0);
  public volatile boolean isNextRangeBufferd = false;
  public volatile int nextRangeStart = 0;
  public volatile int nextRangeSize = 0;
  public Date nextRangeDate = null;
  public Date nextRangeExpiredDate = null;
}
