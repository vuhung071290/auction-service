package dgtc.controller.admin;

import dgtc.common.enums.ActivityEnum;
import dgtc.common.util.GsonUtils;
import dgtc.entity.base.ListResponseData;
import dgtc.entity.base.Response;
import dgtc.entity.datasource.admin.Account;
import dgtc.entity.dto.handler.CreateAccountRequestData;
import dgtc.entity.dto.handler.EditAccountRequestData;
import dgtc.entity.dto.handler.SearchAccountRequestData;
import dgtc.service.admin.AccountService;
import dgtc.service.admin.ActivityService;
import dgtc.service.admin.LoggerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/** @author hunglv */
@Slf4j
@RestController
@RequestMapping("/api/accounts")
@ResponseBody
@RequiredArgsConstructor
public class AccountsController {

  private final AccountService service;
  private final ActivityService activityService;
  private final LoggerService loggerService;

  @GetMapping
  @PreAuthorize("hasAuthority('ACCOUNT_MNG_VIEW')")
  public Response getListAccount(
      @Valid SearchAccountRequestData request, HttpServletRequest httpServletRequest) {
    log.info(loggerService.logBegin(httpServletRequest));
    boolean isSearch = false;

    if (!StringUtils.isEmpty(request.getSearch())) {
      if (!request.getField().equals("domainName")) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Field must be \"domainName\"");
      }
      isSearch = true;
    }

    ListResponseData listResponse =
        service.searchAccountByDomain(
            isSearch ? request.getSearch() : "", request.getPage(), request.getSize());

    if (listResponse == null) {
      throw new ResponseStatusException(
          HttpStatus.INTERNAL_SERVER_ERROR, "Error when get list accounts");
    }

    Response response = new Response(listResponse);
    log.info(loggerService.logEnd(httpServletRequest, response));
    return response;
  }

  @GetMapping("/{domainName}")
  @PreAuthorize("hasAuthority('ACCOUNT_MNG_VIEW')")
  public Response getAccountInfo(@PathVariable String domainName, HttpServletRequest request) {

    Account accInfo = service.getAccountInfo(domainName);
    if (accInfo == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account was not found.");
    }
    return new Response(accInfo);
  }

  @PostMapping
  @PreAuthorize("hasAuthority('ACCOUNT_MNG_ADD')")
  public Response createAccount(@Valid @RequestBody CreateAccountRequestData request) {
    Account accInfo = service.getAccountInfo(request.getDomainName());
    if (accInfo != null) {
      throw new ResponseStatusException(HttpStatus.FOUND, "Account was existed");
    }

    accInfo = new Account(request);
    Account result = service.saveAccount(accInfo);
    if (result == null) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Can't save account");
    }

    activityService.add(ActivityEnum.ACCOUNT_ADD, GsonUtils.toJson(result));

    return new Response("@" + request.getDomainName() + " has been successfully created");
  }

  @PutMapping("/{domainName}")
  @PreAuthorize("hasAuthority('ACCOUNT_MNG_EDIT')")
  public Response editAccount(
      @PathVariable String domainName, @Valid @RequestBody EditAccountRequestData request) {
    Account accInfo = service.getAccountInfo(domainName);
    if (accInfo == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account was not found");
    }

    accInfo.setDisplayName(request.getDisplayName());
    accInfo.setEmail(request.getEmail());
    accInfo.setIsActive(request.isActive());

    Account result = service.saveAccount(accInfo);
    if (result == null) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Can't save account");
    }

    activityService.add(ActivityEnum.ACCOUNT_EDIT, GsonUtils.toJson(result));

    return new Response("@" + domainName + " has been successfully updated");
  }

  @DeleteMapping("/{domainName}")
  @PreAuthorize("hasAuthority('ACCOUNT_MNG_DELETE')")
  public Response deleteAccount(@PathVariable String domainName) {
    String principal =
        (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    if (domainName.equals(principal)) {
      throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "You can't delete yourself");
    }

    Account accInfo = service.getAccountInfo(domainName);
    if (accInfo == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account was not found");
    }

    boolean result = service.deleteAccount(accInfo);
    if (!result) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Can't delete account");
    }

    activityService.add(ActivityEnum.ACCOUNT_DELETE, domainName);

    return new Response("@" + domainName + " has been successfully deleted");
  }
}
