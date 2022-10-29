package dgtc.scheduling;

import dgtc.service.admin.AuctionService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AuctionTask implements Runnable {

  private Long auctionId;

  private AuctionService auctionService;

  public AuctionTask(Long auctionId, AuctionService auctionService) {
    this.auctionId = auctionId;
    this.auctionService = auctionService;
  }

  @Override
  public void run() {
    log.info("Auction {}, running on thread {}", auctionId, Thread.currentThread().getName());
    auctionService.checkAuctionSteps(auctionId);
  }
}
