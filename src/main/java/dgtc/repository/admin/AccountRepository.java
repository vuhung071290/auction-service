package dgtc.repository.admin;

import dgtc.entity.datasource.admin.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/** @author hunglv */
@Repository
public interface AccountRepository extends JpaRepository<Account, String> {

  @Query(value = "SELECT a FROM Account a WHERE a.domainName LIKE CONCAT('%',?1,'%')")
  Page<Account> searchAccountByDomain(String domainName, Pageable paging);
}
