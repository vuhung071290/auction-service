package dgtc.service.admin;

import dgtc.entity.base.ListResponseData;
import dgtc.entity.base.SearchRequestData;
import dgtc.entity.datasource.admin.Account;
import dgtc.entity.datasource.admin.AccountPermission;
import dgtc.entity.datasource.admin.Feature;
import dgtc.entity.datasource.admin.FeatureAction;
import dgtc.entity.dto.FeatureFullInfo;
import dgtc.entity.dto.handler.SaveAccountPermissionRequestData;
import dgtc.repository.admin.AccountPermissionRepository;
import dgtc.repository.admin.FeatureActionRepository;
import dgtc.repository.admin.FeatureRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

/** @author hunglv */
@Slf4j
@Service
@RequiredArgsConstructor
public class PermissionService {

  private final FeatureRepository featureRepo;
  private final FeatureActionRepository featureActionRepo;
  private final AccountPermissionRepository accountPermissionRepo;
  private final AccountService accountService;

  public ListResponseData getListFeatures(SearchRequestData request) {
    Pageable paging =
        PageRequest.of(
            request.getPage() - 1, request.getSize(), Sort.by("createdDate").ascending());
    try {
      List<FeatureFullInfo> listFeatureFull = new ArrayList<>();
      Page<Feature> pageFeatures = featureRepo.findAll(paging);
      List<Feature> listFeature = pageFeatures.getContent();
      for (Feature feature : listFeature) {
        List<FeatureAction> listFeatureAction =
            featureActionRepo.getFeatureActionByFeatureIdEquals(feature.getFeatureId());
        listFeatureFull.add(new FeatureFullInfo(feature, listFeatureAction));
      }

      return new ListResponseData(
          listFeatureFull, pageFeatures.getTotalElements(), request.getPage(), request.getSize());
    } catch (Exception e) {
      return null;
    }
  }

  public ListResponseData getAllListFeatures() {
    try {
      List<FeatureFullInfo> listFeatureFull = new ArrayList<>();
      List<Feature> listFeature = featureRepo.findAll(Sort.by("createdDate").ascending());
      for (Feature feature : listFeature) {
        List<FeatureAction> listFeatureAction =
            featureActionRepo.getFeatureActionByFeatureIdEquals(feature.getFeatureId());
        listFeatureFull.add(new FeatureFullInfo(feature, listFeatureAction));
      }

      return new ListResponseData(listFeatureFull, listFeature.size(), 1, listFeature.size());
    } catch (Exception e) {
      return null;
    }
  }

  public ListResponseData getAllListFeatureActionByDomainName(String domainName) {
    try {
      List<AccountPermission> permissionList =
          accountPermissionRepo.getAllByDomainNameEquals(domainName);
      return new ListResponseData(permissionList, permissionList.size(), 1, permissionList.size());
    } catch (Exception e) {
      return null;
    }
  }

  public List<AccountPermission> getAllPermissionByDomainName(String domainName) {
    try {
      return accountPermissionRepo.getAllByDomainNameEquals(domainName);
    } catch (Exception e) {
      return null;
    }
  }

  public Feature getFeature(String featureId) {
    try {
      return featureRepo.findById(featureId).orElse(null);
    } catch (Exception e) {
      return null;
    }
  }

  public Feature saveFeature(Feature entity) {
    return featureRepo.save(entity);
  }

  @Transactional
  public boolean deleteFeature(Feature entity) {
    try {
      featureActionRepo.deleteAllByFeatureIdEquals(entity.getFeatureId());
      featureRepo.delete(entity);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  public boolean hasFeatureGranted(String featureId) {
    List<AccountPermission> list = accountPermissionRepo.getAllByFeatureIdEquals(featureId);
    return list.size() > 0;
  }

  public FeatureAction getFeatureAction(String featureActionId) {
    try {
      return featureActionRepo.findById(featureActionId).orElse(null);
    } catch (Exception e) {
      return null;
    }
  }

  public FeatureAction saveFeatureAction(FeatureAction entity) {
    return featureActionRepo.save(entity);
  }

  public boolean hasFeatureActionGranted(String featureActionId) {
    return accountPermissionRepo.countAccountPermissionByFeatureActionId(featureActionId) > 0;
  }

  public boolean deleteFeatureAction(FeatureAction entity) {
    try {
      featureActionRepo.delete(entity);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  public List<AccountPermission> getAccountPermission(String domainName) {
    try {
      return accountPermissionRepo.getAllByDomainNameEquals(domainName);
    } catch (Exception e) {
      return null;
    }
  }

  @Transactional
  public void saveUserPermission(String domainName, SaveAccountPermissionRequestData requestData) {
    Account accInfo = accountService.getAccountInfo(domainName);
    if (accInfo == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account was not found");
    }

    for (String f : requestData.getAdd()) {
      FeatureAction featureAction = featureActionRepo.findById(f).orElse(null);
      Long count =
          accountPermissionRepo.countAccountPermissionByDomainNameEqualsAndFeatureActionIdEquals(
              domainName, f);
      if (count > 0) {
        continue;
      }
      if (featureAction == null) {
        throw new ResponseStatusException(
            HttpStatus.NOT_FOUND, "Feature Action \"" + f + "\" was not found");
      }
      accountPermissionRepo.save(
          new AccountPermission(
              domainName, featureAction.getFeatureActionId(), featureAction.getFeatureId()));
    }

    for (String f : requestData.getRemove()) {
      FeatureAction featureAction = featureActionRepo.findById(f).orElse(null);
      Long count =
          accountPermissionRepo.countAccountPermissionByDomainNameEqualsAndFeatureActionIdEquals(
              domainName, f);
      if (count == 0) {
        continue;
      }
      if (featureAction == null) {
        throw new ResponseStatusException(
            HttpStatus.NOT_FOUND, "Feature Action \"" + f + "\" was not found");
      }
      accountPermissionRepo.delete(
          new AccountPermission(
              domainName, featureAction.getFeatureActionId(), featureAction.getFeatureId()));
    }
  }
}
