package dgtc.entity.datasource.user;

import dgtc.entity.datasource.admin.Auction;

public interface IAuctionsRegisterByOneUserAggregate {
  Auction getAuction();

  int getStatus();
}
