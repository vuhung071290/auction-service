package dgtc.entity.datasource.admin;

import dgtc.entity.dto.handler.CreateFeatureActionRequestData;
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
@Table(name = "feature_action")
@NoArgsConstructor
public class FeatureAction implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @Column(name = "feature_action_id")
  private String featureActionId;

  @Column(name = "feature_id")
  private String featureId;

  @Column(name = "feature_action_name")
  private String featureActionName;

  @Column(name = "description")
  private String description;

  @Column(name = "created_date")
  private Long createdDate;

  public FeatureAction(CreateFeatureActionRequestData request) {
    this.featureActionId = request.getFeatureActionId();
    this.featureId = request.getFeatureId();
    this.featureActionName = request.getFeatureActionName();
    this.description = request.getDescription();
    this.createdDate = System.currentTimeMillis();
  }
}
