package dgtc.entity.datasource.admin;

import com.google.common.collect.ImmutableSet;
import dgtc.entity.dto.handler.property.CreatePropertyRequestData;
import dgtc.entity.dto.handler.property.EditPropertyRequestData;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Set;

/** @author hunglv */
@Getter
@Setter
@Entity
@Table(name = "property")
@NoArgsConstructor
public class Property implements Serializable {

  public static final Set<String> VALID_SEARCH_FIELD =
      ImmutableSet.<String>builder().add("name").build();
  public static final Set<String> VALID_SEARCH_DATE_FIELD =
      ImmutableSet.<String>builder().add("createdDate").build();
  private static final long serialVersionUID = 1L;
  @Id
  @Column(name = "property_id")
  private long propertyId;
  @Column(name = "name")
  private String name;
  @Column(name = "description")
  private String description;
  @Column(name = "images")
  private String images;
  @Column(name = "registration_forms")
  private String registrationForms;
  @Column(name = "auction_minutes")
  private String auctionMinutes;
  @Column(name = "contracts")
  private String contracts;
  @Column(name = "created_user")
  private String createdUser;
  @Column(name = "extra_info")
  private String extraInfo;
  @Column(name = "updated_date")
  private long updatedDate;
  @Column(name = "created_date")
  private long createdDate;

  public Property(CreatePropertyRequestData request, long propertyId) {
    this.propertyId = propertyId;
    this.name = request.getName();
    this.description = request.getDescription();
    this.images = request.getImages();
    this.registrationForms = request.getRegistrationForms();
    this.auctionMinutes = request.getAuctionMinutes();
    this.contracts = request.getContracts();
    this.createdUser = request.getCreatedUser();
    this.extraInfo = request.getExtraInfo();
    this.updatedDate = 0L;
    this.createdDate = System.currentTimeMillis();
  }

  public Property(EditPropertyRequestData request, Property savedProperty) {
    this.propertyId = savedProperty.getPropertyId();
    this.name = request.getName();
    this.description = request.getDescription();
    this.images = request.getImages();
    this.registrationForms = request.getRegistrationForms();
    this.auctionMinutes = request.getAuctionMinutes();
    this.contracts = request.getContracts();
    this.createdUser = savedProperty.getCreatedUser();
    this.updatedDate = System.currentTimeMillis();
    this.createdDate = savedProperty.getCreatedDate();
  }
}
