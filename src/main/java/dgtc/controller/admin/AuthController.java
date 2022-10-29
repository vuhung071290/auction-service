package dgtc.controller.admin;

import dgtc.common.enums.ActivityEnum;
import dgtc.common.util.ActivityUtils;
import dgtc.entity.base.Response;
import dgtc.entity.datasource.admin.Account;
import dgtc.entity.datasource.admin.AccountPermission;
import dgtc.entity.dto.Token;
import dgtc.entity.dto.handler.AuthRequestData;
import dgtc.entity.dto.handler.AuthResponseData;
import dgtc.service.admin.*;
import dgtc.token.TokenManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

/** @author hunglv */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@ResponseBody
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;
  private final AccountService accountService;
  private final LoggerService loggerService;
  private final PermissionService permissionService;
  private final ActivityService activityService;
  private final TokenManager tokenManager;

  @PostMapping
  public Response auth(
      @Valid @RequestBody AuthRequestData req, HttpServletRequest httpServletRequest) {
    log.info(loggerService.logBegin(httpServletRequest));

    if (!authService.isValidLogin(req.getDomainName(), req.getPassword())) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "Domain name or password is not correct");
    }

    Account accInfo = accountService.getAccountInfo(req.getDomainName());
    Token token =
        tokenManager.generateAdminToken(accInfo.getDomainName(), accInfo.getDisplayName());
    List<AccountPermission> permissionList =
        permissionService.getAccountPermission(req.getDomainName());

    String ipAddress = httpServletRequest.getHeader("X-FORWARDED-FOR");
    if (ipAddress == null) {
      ipAddress = httpServletRequest.getRemoteAddr();
    }

    accInfo.setIpLogin(ipAddress);
    accInfo.setLastLogin(System.currentTimeMillis());
    accountService.saveAccount(accInfo);

    Response response = new Response(new AuthResponseData(accInfo, permissionList, token));
    activityService.add(
        ActivityEnum.USER_LOGIN, req.getDomainName(), ActivityUtils.getUserLogin(ipAddress));
    log.info(loggerService.logEnd(httpServletRequest, response));
    return response;
  }
}
