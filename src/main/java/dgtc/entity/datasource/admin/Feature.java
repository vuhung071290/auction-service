package dgtc.entity.datasource.admin;

import dgtc.entity.dto.handler.CreateFeatureRequestData;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/** @author hunglv */
@Getter
@Setter
@Entity
@Table(name = "feature")
@NoArgsConstructor
public class Feature implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @Column(name = "feature_id")
  private String featureId;

  @Column(name = "feature_name")
  private String featureName;

  @Column(name = "description")
  private String description;

  @Column(name = "created_date")
  private Long createdDate;

  public Feature(CreateFeatureRequestData request) {
    this.featureId = request.getFeatureId();
    this.featureName = request.getFeatureName();
    this.description = request.getDescription();
    this.createdDate = System.currentTimeMillis();
  }
}
