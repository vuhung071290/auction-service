package dgtc.repository.admin;

import dgtc.entity.datasource.admin.Feature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** @author hunglv */
@Repository
public interface FeatureRepository extends JpaRepository<Feature, String> {}
