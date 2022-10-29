package dgtc.repository.admin;

import dgtc.entity.datasource.admin.AccountActivity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/** @author hunglv */
@Repository
public interface AccountActivityRepository extends JpaRepository<AccountActivity, Long> {

  @Query(value = "SELECT a FROM AccountActivity a WHERE a.domainName LIKE CONCAT('%',?1,'%')")
  Page<AccountActivity> searchByDomainName(String domainName, Pageable paging);
}
