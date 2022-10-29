package dgtc.entity.dto.handler.property;

import lombok.Data;

/** @author hunglv */
@Data
public class CreatePropertyRequestData {

  private String name;

  private String description;

  private String images;

  private String registrationForms;

  private String auctionMinutes;

  private String contracts;

  private String createdUser;

  private String extraInfo;

  private long updatedDate;

  private long createdDate;
}
