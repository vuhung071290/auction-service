package dgtc.controller.user;

import dgtc.common.exception.*;
import dgtc.entity.base.Response;
import dgtc.entity.dto.Token;
import dgtc.entity.dto.handler.LoginResponseData;
import dgtc.entity.dto.handler.RefreshResponseData;
import dgtc.entity.dto.handler.user.*;
import dgtc.service.user.UserService;
import dgtc.utils.MessageUtils;
import dgtc.utils.RequestUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/api/v1")
@ResponseBody
@RequiredArgsConstructor
public class UserController {

  @Autowired private final UserService userService;

  @PostMapping("/register")
  public Response register(
      @Valid @RequestBody UserRegisterRequestData req, HttpServletRequest servletRequest) {
    try {
      UserRegisterResponse userRegisterResponse =
          userService.createUser(req, RequestUtils.getIpAddress(servletRequest).value(), true);

      return new Response(userRegisterResponse);
    } catch (RegistrationException exception) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, exception.getMessage());
    }
  }

  @PostMapping("/login")
  public ResponseEntity login(
      @Valid @RequestBody UserLoginRequestData req, HttpServletRequest servletRequest) {
    try {
      LoginResponseData loginResponseData =
          userService.verifyPhoneAndPassword(
              req, RequestUtils.getIpAddress(servletRequest).value());
      return ResponseEntity.status(HttpStatus.OK).body(loginResponseData);
    } catch (LoginException exception) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(new Response(exception.getStatus(), exception.getMessage()));
    }
  }

  @PostMapping("/logout")
  public Response logout(
      @RequestHeader(name = "Authorization") String token, HttpServletRequest servletRequest) {
    try {
      if (userService.clearToken(token)) {
        return new Response(HttpStatus.OK.value(), null);
      } else {
        return new Response(HttpStatus.NOT_ACCEPTABLE.value(), null);
      }
    } catch (LogoutException exception) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, exception.getMessage());
    }
  }

  @PostMapping("/refresh")
  public Response refresh(@Valid @RequestBody UserRefreshRequestData req) {
    try {
      Token token = userService.refreshToken(req.getPhoneNumber(), req.getToken());
      RefreshResponseData refreshResponseData = new RefreshResponseData();
      refreshResponseData.setToken(token.getToken());
      refreshResponseData.setExpireTime(token.getExpireTime());
      return new Response(refreshResponseData);
    } catch (RefreshTokenException exception) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, exception.getMessage());
    }
  }

  @PostMapping("/user/device-token")
  public Response deviceToken(
      @Valid @RequestBody UserDeviceTokenRequestData req,
      @RequestHeader(name = "Authorization") String token,
      HttpServletRequest servletRequest) {
    try {
      if (userService.updateDeviceToken(
          token, req.getToken(), RequestUtils.getIpAddress(servletRequest).value())) {
        return new Response(HttpStatus.OK.value(), null);
      } else {
        return new Response(HttpStatus.NOT_ACCEPTABLE.value(), null);
      }
    } catch (FirebaseUpdateException exception) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, exception.getMessage());
    }
  }

  @PutMapping("/me")
  public Response updateUser(
      @RequestBody UserUpdateRequestData req,
      @RequestHeader(name = "Authorization") String token,
      HttpServletRequest servletRequest) {
    try {
      if (userService.updateUser(token, req, RequestUtils.getIpAddress(servletRequest).value())) {
        return new Response(HttpStatus.OK.value(), null);
      } else {
        return new Response(HttpStatus.NOT_ACCEPTABLE.value(), null);
      }
    } catch (UpdateUserInfoException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
    }
  }

  @PutMapping("/me/password")
  public Response changePassword(
      @RequestBody @Valid UserChangePasswordRequestData req,
      @RequestHeader(name = "Authorization") String token,
      HttpServletRequest servletRequest) {
    try {
      if (userService.changePassword(
          token, req, RequestUtils.getIpAddress(servletRequest).value())) {
        return new Response(HttpStatus.OK.value(), null);
      } else {
        return new Response(HttpStatus.NOT_ACCEPTABLE.value(), null);
      }
    } catch (ChangePasswordUserException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
    }
  }

  @PostMapping("/user/forgot-password")
  public Response forgotPassword(
      @RequestBody @Valid UserForgotPasswordRequestData req, HttpServletRequest servletRequest) {
    try {
      if (userService.forgotPassword(req, RequestUtils.getIpAddress(servletRequest).value())) {
        return new Response(HttpStatus.OK.value(), MessageUtils.message("password.updatesuccess"));
      } else {
        return new Response(HttpStatus.NOT_ACCEPTABLE.value(), null);
      }
    } catch (ForgotPasswordException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
    }
  }

  @PostMapping("/user/resend-otp")
  public Response resendOtp(
      @RequestBody @Valid ResendOtpRequestData req, HttpServletRequest servletRequest) {
    try {
      String mess = userService.resendOtp(req, RequestUtils.getIpAddress(servletRequest).value());
      return new Response(HttpStatus.OK.value(), mess);

    } catch (ResendOTPException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
    }
  }

  @PostMapping("/me/active")
  public Response activeByOtpSms(
      @RequestBody @Valid UserActiveOtpRequestData req, HttpServletRequest servletRequest) {
    try {
      if (userService.updateActiveWithOtpSms(
          req, RequestUtils.getIpAddress(servletRequest).value())) {
        return new Response(HttpStatus.OK.value(), null);
      } else {
        return new Response(HttpStatus.NOT_ACCEPTABLE.value(), null);
      }
    } catch (ActiveUserException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
    }
  }

  @GetMapping("/me/profile")
  public Response getUserProfile(@RequestHeader(name = "Authorization") String token) {
    try {
      UserProfileResponse res = userService.getUserProfile(token);
      return new Response(res);
    } catch (GetUserProfileException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
    }
  }
}
