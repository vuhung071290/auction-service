package dgtc.controller.admin;

import dgtc.config.AppConfig;
import dgtc.entity.base.Response;
import dgtc.entity.dto.handler.ConfigResponseData;
import dgtc.service.admin.LoggerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/** @author hunglv */
@Slf4j
@RestController
@RequestMapping("/api/config")
@ResponseBody
@RequiredArgsConstructor
public class ConfigController {

  private final LoggerService loggerService;
  private final AppConfig appConfig;

  @GetMapping
  public Response getConfig(HttpServletRequest httpServletRequest) {
    log.info(loggerService.logBegin(httpServletRequest));

    ConfigResponseData config = new ConfigResponseData();

    config.setVersion(appConfig.getVersion());

    Response response = new Response(config);
    log.info(loggerService.logEnd(httpServletRequest, response));
    return response;
  }
}
