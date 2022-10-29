package dgtc.service.admin;

import dgtc.entity.base.ListResponseData;
import dgtc.entity.datasource.admin.Property;
import dgtc.entity.dto.handler.property.SearchPropertyRequestData;
import dgtc.repository.admin.PropertyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

/** @author hunglv */
@Slf4j
@Service
@RequiredArgsConstructor
public class PropertyService {

  private final PropertyRepository propertyRepo;

  public ListResponseData getListProperty(SearchPropertyRequestData request) {
    try {
      Page<Property> pageResult = searchProperty(request);
      return new ListResponseData(
          pageResult.getContent(),
          pageResult.getTotalElements(),
          request.getPage(),
          request.getSize());
    } catch (Exception ex) {
      return null;
    }
  }

  public Page<Property> searchProperty(SearchPropertyRequestData request) {
    Sort sort =
        request.getDirection().equals("asc")
            ? Sort.by(request.getFieldDate()).ascending()
            : Sort.by(request.getFieldDate()).descending();

    Pageable paging = PageRequest.of(request.getPage() - 1, request.getSize(), sort);
    try {
      if (StringUtils.isNotEmpty(request.getField())) {
        if (request.getFieldDate().equals("createdDate") && request.getField().equals("name")) {
          return propertyRepo.searchPropertyByCreatedDateAndName(
              request.getStartDateSearch(),
              request.getEndDateSearch(),
              request.getSearch(),
              paging);
        }
      } else {
        if (request.getFieldDate().equals("createdDate")) {
          return propertyRepo.searchPropertyByCreatedDate(
              request.getStartDateSearch(), request.getEndDateSearch(), paging);
        }
      }
    } catch (Exception ex) {
      log.error("Exception: Get list property", ex);
    }
    return null;
  }

  public Property getPropertyInfo(String propertyId) {
    return propertyRepo.findByPropertyId(Long.parseLong(propertyId)).orElse(null);
  }

  public Property saveProperty(Property property) {
    return propertyRepo.save(property);
  }

  public boolean deleteProperty(Property property) {
    try {
      propertyRepo.delete(property);
      return true;
    } catch (Exception e) {
      return false;
    }
  }
}
