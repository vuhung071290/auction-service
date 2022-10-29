package dgtc.entity.dto.handler.auction;

import lombok.Data;

/** @author hunglv */
@Data
public class EditAuctionRequestData {

  private String name;

  private String description;

  private int auctionMethod;

  private float registerFee;

  private float depositFee;

  private float startPrice;

  private float stepPrice;

  private long startRegisterDate;

  private long endRegisterDate;

  private long propertyId;

  private String extraInfo;
}
