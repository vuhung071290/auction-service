package dgtc.entity.dto.handler.auction;

import dgtc.common.enums.AuctionStepEnum;
import lombok.Builder;
import lombok.Data;

/** @author hunglv */
@Data
@Builder
public class AuctionStep {
  private AuctionStepEnum auctionStepEnum;
  private Long remainTime;
}
