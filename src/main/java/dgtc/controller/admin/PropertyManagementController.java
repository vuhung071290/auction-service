package dgtc.controller.admin;

import dgtc.common.genid.GenIdUtil;
import dgtc.common.util.GsonUtils;
import dgtc.entity.base.ListResponseData;
import dgtc.entity.base.Response;
import dgtc.entity.datasource.admin.Property;
import dgtc.entity.dto.handler.property.CreatePropertyRequestData;
import dgtc.entity.dto.handler.property.EditPropertyRequestData;
import dgtc.entity.dto.handler.property.SearchPropertyRequestData;
import dgtc.service.admin.PropertyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/** @author hunglv */
@Slf4j
@RestController
@RequestMapping("/api/property-management")
@ResponseBody
@RequiredArgsConstructor
public class PropertyManagementController {

  @Autowired private final GenIdUtil genIdUtil;

  private final PropertyService propertyService;

  @GetMapping
  @PreAuthorize("hasAuthority('PROPERTY_MNG_VIEW')")
  public Response getListProperty(
      @Valid SearchPropertyRequestData request, HttpServletRequest httpServletRequest) {

    request.init();

    if (StringUtils.isNotEmpty(request.getField())) {
      if (!Property.VALID_SEARCH_FIELD.contains(request.getField())) {
        throw new ResponseStatusException(
            HttpStatus.BAD_REQUEST,
            "Field must in list " + GsonUtils.toJson(Property.VALID_SEARCH_FIELD));
      }
    }

    if (!Property.VALID_SEARCH_DATE_FIELD.contains(request.getFieldDate())) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST,
          "FieldDate must in list " + GsonUtils.toJson(Property.VALID_SEARCH_DATE_FIELD));
    }

    ListResponseData listResponse = propertyService.getListProperty(request);

    if (listResponse == null) {
      throw new ResponseStatusException(
          HttpStatus.INTERNAL_SERVER_ERROR, "Error when get list property");
    }

    Response response = new Response(listResponse);
    return response;
  }

  @GetMapping("/{propertyId}")
  @PreAuthorize("hasAuthority('PROPERTY_MNG_VIEW')")
  public Response getPropertyInfo(@PathVariable String propertyId, HttpServletRequest request) {

    Property propertyInfo = propertyService.getPropertyInfo(propertyId);
    if (propertyInfo == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Property was not found.");
    }
    return new Response(propertyInfo);
  }

  @PostMapping
  @PreAuthorize("hasAuthority('PROPERTY_MNG_ADD')")
  public Response createProperty(@Valid @RequestBody CreatePropertyRequestData request) {
    Property newProperty = new Property(request, genIdUtil.genUID());
    Property result = propertyService.saveProperty(newProperty);
    if (result == null) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Can't save property");
    }

    return new Response("Property " + request.getName() + " has been successfully created");
  }

  @PutMapping("/{propertyId}")
  @PreAuthorize("hasAuthority('PROPERTY_MNG_EDIT')")
  public Response editProperty(
      @PathVariable String propertyId, @Valid @RequestBody EditPropertyRequestData request) {
    Property propertyInfo = propertyService.getPropertyInfo(propertyId);
    if (propertyInfo == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Property was not found");
    }
    Property editProperty = new Property(request, propertyInfo);
    Property result = propertyService.saveProperty(editProperty);
    if (result == null) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Can't save property");
    }

    return new Response("Property " + request.getName() + " has been successfully updated");
  }

  @DeleteMapping("/{propertyId}")
  @PreAuthorize("hasAuthority('PROPERTY_MNG_DELETE')")
  public Response deleteProperty(@PathVariable String propertyId) {
    Property propertyInfo = propertyService.getPropertyInfo(propertyId);
    if (propertyInfo == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Property was not found");
    }

    boolean result = propertyService.deleteProperty(propertyInfo);
    if (!result) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Can't delete Property");
    }

    return new Response("Property has been successfully deleted");
  }
}
