package dgtc.service.admin;

import dgtc.entity.base.ListResponseData;
import dgtc.entity.datasource.admin.Account;
import dgtc.repository.admin.AccountPermissionRepository;
import dgtc.repository.admin.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.transaction.Transactional;

/** @author hunglv */
@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {

  private final AccountRepository accountRepo;
  private final AccountPermissionRepository accountPermissionRepo;

  public ListResponseData searchAccountByDomain(String domain, int page, int size) {
    try {
      Page<Account> pageResult = getListAccount(domain, page, size);
      return new ListResponseData(
          pageResult.getContent(), pageResult.getTotalElements(), page, size);
    } catch (Exception ex) {
      return null;
    }
  }

  public Account getAccountInfo(String domainName) {
    return accountRepo.findById(domainName).orElse(null);
  }

  public Account saveAccount(Account account) {
    return accountRepo.save(account);
  }

  public Page<Account> getListAccount(String search, int page, int size) {
    Pageable paging = PageRequest.of(page - 1, size, Sort.by("domainName").ascending());
    try {
      if (StringUtils.isEmpty(search)) {
        return accountRepo.findAll(paging);
      } else {
        return accountRepo.searchAccountByDomain(search, paging);
      }
    } catch (Exception ex) {
      log.error("Exception: Get list account", ex);
    }
    return null;
  }

  @Transactional
  public boolean deleteAccount(Account account) {
    try {
      accountPermissionRepo.deleteAllByDomainNameEquals(account.getDomainName());
      accountRepo.delete(account);
      return true;
    } catch (Exception e) {
      return false;
    }
  }
}
