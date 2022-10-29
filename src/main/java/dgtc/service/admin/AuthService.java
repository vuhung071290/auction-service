package dgtc.service.admin;

import dgtc.config.AuthConfig;
import dgtc.entity.datasource.admin.Account;
import dgtc.repository.admin.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/** @author hunglv */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

  private final AccountRepository accountRepo;
  private final AuthConfig authConfig;
  private final GoogleAuthService googleAuthService;

  public boolean isValidLogin(String domainName, String password) {

    Account account = accountRepo.findById(domainName).orElse(null);

    if (account == null) {
      log.info("account={} is not existed", domainName);
      return false;
    }

    if (authConfig.isUseGoogleAuth()) {
      return googleAuthService.verify(domainName, password);
    }

    return true;
  }
}
