package dgtc.repository.admin;

import dgtc.entity.datasource.admin.FeatureAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/** @author hunglv */
@Repository
public interface FeatureActionRepository extends JpaRepository<FeatureAction, String> {

  List<FeatureAction> getFeatureActionByFeatureIdEquals(String featureId);

  void deleteAllByFeatureIdEquals(String featureId);
}
