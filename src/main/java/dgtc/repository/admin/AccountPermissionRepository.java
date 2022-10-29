package dgtc.repository.admin;

import dgtc.entity.datasource.admin.AccountPermission;
import dgtc.entity.datasource.admin.AccountPermissionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/** @author hunglv */
@Repository
public interface AccountPermissionRepository
    extends JpaRepository<AccountPermission, AccountPermissionId> {

  List<AccountPermission> getAllByFeatureIdEquals(String featureId);

  List<AccountPermission> getAllByDomainNameEquals(String domainName);

  void deleteAllByDomainNameEquals(String domainName);

  Long countAccountPermissionByDomainNameEqualsAndFeatureActionIdEquals(
      String domainName, String featureActionId);

  Long countAccountPermissionByFeatureActionId(String featureActionId);
}
