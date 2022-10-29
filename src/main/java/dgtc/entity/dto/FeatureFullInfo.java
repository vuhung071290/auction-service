package dgtc.entity.dto;

import dgtc.entity.datasource.admin.Feature;
import dgtc.entity.datasource.admin.FeatureAction;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/** @author hunglv */
@Getter
@Setter
public class FeatureFullInfo implements Serializable {
  private String featureId;
  private String featureName;
  private String description;
  private long createdDate;
  private List<FeatureAction> featureActions;

  public FeatureFullInfo(Feature feature, List<FeatureAction> featureActions) {
    this.featureId = feature.getFeatureId();
    this.featureName = feature.getFeatureName();
    this.description = feature.getDescription();
    this.createdDate = feature.getCreatedDate();
    this.featureActions = featureActions;
  }
}
