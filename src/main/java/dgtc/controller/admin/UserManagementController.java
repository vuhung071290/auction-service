package dgtc.controller.admin;

import dgtc.common.exception.*;
import dgtc.entity.base.ListResponseData;
import dgtc.entity.base.Response;
import dgtc.entity.dto.handler.user.*;
import dgtc.service.user.UserService;
import dgtc.utils.RequestUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/api/user-management")
@ResponseBody
@RequiredArgsConstructor
public class UserManagementController {
  @Autowired private final UserService userService;

  @GetMapping("/users")
  @PreAuthorize("hasAuthority('USER_MANAGEMENT_VIEW')")
  public Response getUsers(@Valid UsersRequestData request, HttpServletRequest servletRequest) {
    try {
      Page<UserAdminResponseData> page =
          userService.getAllUser(request.getPage(), request.getSize());
      ListResponseData listRes =
          new ListResponseData(
              page.getContent(), page.getTotalElements(), request.getPage(), request.getSize());
      return new Response(listRes);
    } catch (ListUserException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
    }
  }

  @PostMapping("/users/{userId}/active")
  @PreAuthorize("hasAuthority('USER_MANAGEMENT_ACTIVE')")
  public Response activeUserWithoutSms(
      @RequestBody UserActiveRequestData req,
      @PathVariable String userId,
      HttpServletRequest servletRequest) {
    try {
      boolean res =
          userService.activeUserSuccess(
              Long.parseLong(userId), RequestUtils.getIpAddress(servletRequest).value());
      if (res) {
        return new Response(HttpStatus.NO_CONTENT.value(), null);
      } else {
        return new Response(HttpStatus.NOT_ACCEPTABLE.value(), null);
      }
    } catch (ActiveUserException exception) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, exception.getMessage());
    }
  }

  @GetMapping("/users/search")
  @PreAuthorize("hasAuthority('USER_MANAGEMENT_VIEW')")
  public Response searchUser(@Valid UserSearchRequestData req) {
    try {
      Page<UserAdminResponseData> page =
          userService.searchUser(req.getQuery(), req.getPage(), req.getSize());
      ListResponseData listRes =
          new ListResponseData(
              page.getContent(), page.getTotalElements(), req.getPage(), req.getSize());
      return new Response(listRes);
    } catch (SearchUserException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
    }
  }

  @PostMapping("/users/{userId}/otp-activate")
  @PreAuthorize("hasAuthority('USER_MANAGEMENT_ACTIVE')")
  public Response sendOtpSms(@PathVariable String userId, HttpServletRequest servletRequest) {
    try {
      if (userService.sendSmsAmazon(
          Long.parseLong(userId), RequestUtils.getIpAddress(servletRequest).value())) {
        return new Response(HttpStatus.OK.value(), null);
      } else {
        return new Response(HttpStatus.NOT_FOUND.value(), null);
      }

    } catch (SmsTokenException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
    }
  }

  @PostMapping("/users/{userId}/delete")
  @PreAuthorize("hasAuthority('USER_MANAGEMENT_DELETE')")
  public Response deleteUser(@PathVariable Long userId) {
    try {
      userService.deleteUser(userId);
      return new Response(HttpStatus.OK.value(), null);
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, null);
    }
  }

  @PostMapping("/users/create")
  @PreAuthorize("hasAuthority('USER_MANAGEMENT_CREATE')")
  public Response createUser(
      @Valid @RequestBody UserCreateRequestData req, HttpServletRequest servletRequest) {
    try {
      UserRegisterRequestData userCreated = UserRegisterRequestData.create(req);
      boolean isSendOtpToActive;
      if (req.getIsSendOtpToActive() == null) {
        isSendOtpToActive = false;
      } else {
        isSendOtpToActive = req.getIsSendOtpToActive();
      }
      userService.createUser(
          userCreated, RequestUtils.getIpAddress(servletRequest).value(), isSendOtpToActive);
      return new Response(HttpStatus.OK.value(), null);
    } catch (RegistrationException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
    }
  }

  @PutMapping("/users/{userId}/update")
  @PreAuthorize("hasAuthority('USER_MANAGEMENT_UPDATE')")
  public Response updateUserByAdmin(
      @Valid @RequestBody UserUpdateRequestData req,
      @PathVariable Long userId,
      HttpServletRequest servletRequest) {
    try {
      if (userService.updateUserByAdmin(
          userId, req, RequestUtils.getIpAddress(servletRequest).value())) {
        return new Response(HttpStatus.OK.value(), null);
      } else {
        return new Response(HttpStatus.NOT_ACCEPTABLE.value(), null);
      }
    } catch (UpdateUserInfoException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
    }
  }
}
