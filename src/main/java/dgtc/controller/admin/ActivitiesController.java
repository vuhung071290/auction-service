package dgtc.controller.admin;

import dgtc.entity.base.ListResponseData;
import dgtc.entity.base.Response;
import dgtc.entity.datasource.admin.AccountActivity;
import dgtc.entity.dto.handler.SearchAccountRequestData;
import dgtc.service.admin.ActivityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;

/** @author hunglv */
@Slf4j
@RestController
@RequestMapping("/api/activities")
@ResponseBody
@RequiredArgsConstructor
public class ActivitiesController {

  private final ActivityService service;

  @GetMapping
  @PreAuthorize("hasAuthority('ACCOUNT_ACTIVITY_VIEW')")
  public Response getList(@Valid SearchAccountRequestData request) {
    boolean isSearch = false;

    if (!StringUtils.isEmpty(request.getSearch())) {
      if (!request.getField().equals("domainName")) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Field must be \"domainName\"");
      }
      isSearch = true;
    }

    Page<AccountActivity> pageResult;

    if (isSearch) {
      pageResult =
          service.searchListByDomain(request.getSearch(), request.getPage(), request.getSize());
    } else {
      pageResult = service.getList(request.getPage(), request.getSize());
    }

    if (pageResult == null) {
      throw new ResponseStatusException(
          HttpStatus.INTERNAL_SERVER_ERROR, "Error when get list accounts activity");
    }

    return new Response(
        new ListResponseData(
            pageResult.getContent(),
            pageResult.getTotalElements(),
            request.getPage(),
            request.getSize()));
  }
}
