package dgtc.service.admin;

import dgtc.common.util.GsonUtils;
import dgtc.entity.base.Response;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

/** @author hunglv */
@Service
public class LoggerService {
  public String logBegin(HttpServletRequest request) {
    String domainName =
        (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String requestURI = request.getRequestURI();
    String requestMethod = request.getMethod();
    String requestParams = GsonUtils.toJson(request.getParameterMap());
    return String.format(
        "%s: %s %s: request %s", domainName, requestMethod, requestURI, requestParams);
  }

  public String logEnd(HttpServletRequest request, Response response) {
    String domainName =
        (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String requestURI = request.getRequestURI();
    String requestMethod = request.getMethod();
    return String.format(
        "%s: %s %s: response %s",
        domainName, requestMethod, requestURI, GsonUtils.toJson(response));
  }
}
