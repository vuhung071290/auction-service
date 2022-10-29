package dgtc.controller.admin;

import dgtc.common.enums.ActivityEnum;
import dgtc.common.util.GsonUtils;
import dgtc.entity.base.ListResponseData;
import dgtc.entity.base.Response;
import dgtc.entity.base.SearchRequestData;
import dgtc.entity.datasource.admin.Feature;
import dgtc.entity.datasource.admin.FeatureAction;
import dgtc.entity.dto.handler.*;
import dgtc.service.admin.ActivityService;
import dgtc.service.admin.LoggerService;
import dgtc.service.admin.PermissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/** @author hunglv */
@Slf4j
@RestController
@RequestMapping("/api/permissions")
@ResponseBody
@RequiredArgsConstructor
public class PermissionsController {

  private final PermissionService permissionService;
  private final ActivityService activityService;
  private final LoggerService loggerService;

  @GetMapping(
      value = "/features",
      params = {"page", "size"})
  @PreAuthorize("hasAuthority('PERMISSION_MNG_VIEW')")
  public Response getListFeatures(
      @Valid SearchRequestData request, HttpServletRequest httpServletRequest) {
    log.info(loggerService.logBegin(httpServletRequest));

    ListResponseData listResponseData;
    if (request.getSize() == null && request.getPage() == null) {
      listResponseData = permissionService.getAllListFeatures();
    } else {
      listResponseData = permissionService.getListFeatures(request);
    }

    Response response = new Response(listResponseData);
    log.info(loggerService.logEnd(httpServletRequest, response));
    return response;
  }

  @GetMapping(
      value = "/features",
      params = {})
  @PreAuthorize("hasAuthority('PERMISSION_MNG_VIEW')")
  public Response getAllListFeatures(HttpServletRequest httpServletRequest) {
    log.info(loggerService.logBegin(httpServletRequest));
    ListResponseData listResponseData = permissionService.getAllListFeatures();
    Response response = new Response(listResponseData);
    log.info(loggerService.logEnd(httpServletRequest, response));
    return response;
  }

  @PostMapping("/features")
  @PreAuthorize("hasAuthority('PERMISSION_MNG_ADD')")
  public Response createNewFeature(
      @Valid @RequestBody CreateFeatureRequestData request, HttpServletRequest httpServletRequest) {
    log.info(loggerService.logBegin(httpServletRequest));

    Feature featureInfo = permissionService.getFeature(request.getFeatureId());
    if (featureInfo != null) {
      throw new ResponseStatusException(HttpStatus.FOUND, "Feature existed");
    }

    featureInfo = new Feature(request);
    Feature result = permissionService.saveFeature(featureInfo);
    if (result == null) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Can't save feature");
    }

    activityService.add(ActivityEnum.PERMISSION_FEATURE_ADD, GsonUtils.toJson(result));

    Response response =
        new Response("Feature \"" + request.getFeatureName() + "\" has been successfully created");
    log.info(loggerService.logEnd(httpServletRequest, response));
    return response;
  }

  @PutMapping("/features/{featureId}")
  @PreAuthorize("hasAuthority('PERMISSION_MNG_EDIT')")
  public Response editFeature(
      @PathVariable String featureId,
      @Valid @RequestBody EditFeatureRequestData request,
      HttpServletRequest httpServletRequest) {
    log.info(loggerService.logBegin(httpServletRequest));

    Feature featureInfo = permissionService.getFeature(featureId);
    if (featureInfo == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Feature did not exist");
    }

    featureInfo.setFeatureName(request.getFeatureName());
    featureInfo.setDescription(request.getDescription());

    Feature result = permissionService.saveFeature(featureInfo);
    if (result == null) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Can't save feature");
    }

    activityService.add(ActivityEnum.PERMISSION_FEATURE_EDIT, GsonUtils.toJson(result));

    Response response =
        new Response("Feature \"" + request.getFeatureName() + "\" has been successfully updated");
    log.info(loggerService.logEnd(httpServletRequest, response));
    return response;
  }

  @DeleteMapping("features/{featureId}")
  @PreAuthorize("hasAuthority('PERMISSION_MNG_DELETE')")
  public Response deleteFeature(
      @PathVariable String featureId, HttpServletRequest httpServletRequest) {
    log.info(loggerService.logBegin(httpServletRequest));

    Feature featureInfo = permissionService.getFeature(featureId);
    if (featureInfo == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Feature did not exist");
    }

    if (permissionService.hasFeatureGranted(featureId)) {
      throw new ResponseStatusException(
          HttpStatus.NOT_ACCEPTABLE, "Feature has action which granted to user");
    }

    boolean result = permissionService.deleteFeature(featureInfo);
    if (!result) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Can't delete feature");
    }

    activityService.add(ActivityEnum.PERMISSION_FEATURE_DELETE, featureId);

    return new Response(
        "Feature \"" + featureInfo.getFeatureName() + "\" has been successfully deleted");
  }

  @PostMapping("/actions")
  @PreAuthorize("hasAuthority('PERMISSION_MNG_ADD')")
  public Response createNewFeatureAction(
      @Valid @RequestBody CreateFeatureActionRequestData request,
      HttpServletRequest httpServletRequest) {
    log.info(loggerService.logBegin(httpServletRequest));

    FeatureAction featureActionInfo =
        permissionService.getFeatureAction(request.getFeatureActionId());
    if (featureActionInfo != null) {
      throw new ResponseStatusException(HttpStatus.FOUND, "Feature Action existed");
    }

    featureActionInfo = new FeatureAction(request);
    FeatureAction result = permissionService.saveFeatureAction(featureActionInfo);
    if (result == null) {
      throw new ResponseStatusException(
          HttpStatus.INTERNAL_SERVER_ERROR, "Can't save feature action");
    }

    activityService.add(ActivityEnum.PERMISSION_FEATURE_ACTION_ADD, GsonUtils.toJson(result));

    return new Response(
        "Feature action \"" + request.getFeatureActionName() + "\" has been successfully created");
  }

  @PutMapping("/actions/{featureActionId}")
  @PreAuthorize("hasAuthority('PERMISSION_MNG_EDIT')")
  public Response editFeatureAction(
      @PathVariable String featureActionId,
      @Valid @RequestBody EditFeatureActionRequestData request,
      HttpServletRequest httpServletRequest) {
    log.info(loggerService.logBegin(httpServletRequest));

    FeatureAction featureActionInfo = permissionService.getFeatureAction(featureActionId);
    if (featureActionInfo == null) {
      throw new ResponseStatusException(HttpStatus.FOUND, "Feature Action did not exist");
    }

    featureActionInfo.setFeatureActionName(request.getFeatureActionName());
    featureActionInfo.setDescription(request.getDescription());

    FeatureAction result = permissionService.saveFeatureAction(featureActionInfo);
    if (result == null) {
      throw new ResponseStatusException(
          HttpStatus.INTERNAL_SERVER_ERROR, "Can't save feature action");
    }

    activityService.add(ActivityEnum.PERMISSION_FEATURE_ACTION_EDIT, GsonUtils.toJson(result));

    return new Response(
        "Feature action \"" + request.getFeatureActionName() + "\" has been successfully updated");
  }

  @DeleteMapping("actions/{featureActionId}")
  @PreAuthorize("hasAuthority('PERMISSION_MNG_DELETE')")
  public Response deleteFeatureAction(
      @PathVariable String featureActionId, HttpServletRequest httpServletRequest) {
    log.info(loggerService.logBegin(httpServletRequest));

    FeatureAction featureActionInfo = permissionService.getFeatureAction(featureActionId);
    if (featureActionInfo == null) {
      throw new ResponseStatusException(HttpStatus.FOUND, "Feature Action did not exist");
    }

    if (permissionService.hasFeatureActionGranted(featureActionId)) {
      throw new ResponseStatusException(
          HttpStatus.NOT_ACCEPTABLE, "Feature action had been granted to user");
    }

    boolean result = permissionService.deleteFeatureAction(featureActionInfo);
    if (!result) {
      throw new ResponseStatusException(
          HttpStatus.INTERNAL_SERVER_ERROR, "Can't delete feature action");
    }

    activityService.add(ActivityEnum.PERMISSION_FEATURE_ACTION_DELETE, featureActionId);

    return new Response(
        "Feature action \""
            + featureActionInfo.getFeatureActionName()
            + "\" has been successfully deleted");
  }

  @GetMapping("/account/{domainName}")
  @PreAuthorize("hasAuthority('PERMISSION_MNG_VIEW')")
  public Response getListFeaturesOfAccount(
      @PathVariable String domainName, HttpServletRequest httpServletRequest) {
    log.info(loggerService.logBegin(httpServletRequest));

    ListResponseData listResponseData =
        permissionService.getAllListFeatureActionByDomainName(domainName);

    Response response = new Response(listResponseData);
    log.info(loggerService.logEnd(httpServletRequest, response));
    return response;
  }

  @PostMapping("/account/{domainName}")
  @PreAuthorize("hasAuthority('PERMISSION_MNG_GRANT')")
  public Response saveAccountPermission(
      @PathVariable String domainName,
      @Valid @RequestBody SaveAccountPermissionRequestData request,
      HttpServletRequest httpServletRequest) {
    log.info(loggerService.logBegin(httpServletRequest));

    if (request.getAdd().size() == 0 && request.getRemove().size() == 0) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "At least \"add\" or \"remove\" has elements");
    }

    permissionService.saveUserPermission(domainName, request);

    activityService.add(ActivityEnum.PERMISSION_ACCOUNT_GRANT, GsonUtils.toJson(request));

    return new Response("New permissions have been successfully granted to " + domainName);
  }
}
