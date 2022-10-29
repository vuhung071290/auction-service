package dgtc.service.admin;

import dgtc.common.enums.ActivityEnum;
import dgtc.entity.datasource.admin.AccountActivity;
import dgtc.repository.admin.AccountActivityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/** @author hunglv */
@Slf4j
@Service
@RequiredArgsConstructor
public class ActivityService {

  private final AccountActivityRepository accountActivityRepo;

  public void add(ActivityEnum action, String description) {
    String domainName =
        (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

    add(action, domainName, description);
  }

  public void add(ActivityEnum action, String domainName, String description) {
    AccountActivity accountActivity = new AccountActivity();
    accountActivity.setDomainName(domainName);
    accountActivity.setTimestamp(System.currentTimeMillis());
    accountActivity.setActionCode(action.getValue());
    accountActivity.setDescription(description);

    accountActivityRepo.save(accountActivity);
  }

  /**
   * Get list with paging and sort by timestamp desc
   *
   * @param page count from 1
   * @param size
   * @return
   */
  public Page<AccountActivity> getList(int page, int size) {
    Page<AccountActivity> result = null;
    Pageable paging = PageRequest.of(page - 1, size, Sort.by("timestamp").descending());
    try {
      result = accountActivityRepo.findAll(paging);
    } catch (Exception ex) {
      log.error("Exception: Get list account activity", ex);
    }
    return result;
  }

  /**
   * Search list with paging and sort by timestamp desc
   *
   * @param search
   * @param page count from 1
   * @param size
   * @return
   */
  public Page<AccountActivity> searchListByDomain(String search, int page, int size) {
    Page<AccountActivity> result = null;
    Pageable paging = PageRequest.of(page - 1, size, Sort.by("timestamp").descending());
    try {
      result = accountActivityRepo.searchByDomainName(search, paging);
    } catch (Exception ex) {
      log.error("Exception: Get list account activity", ex);
    }
    return result;
  }
}
