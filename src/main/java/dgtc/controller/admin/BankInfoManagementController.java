package dgtc.controller.admin;

import dgtc.common.exception.CreateBankInfoException;
import dgtc.common.exception.GetBankInfoByIdException;
import dgtc.common.exception.GetBankInfoException;
import dgtc.common.exception.UpdateBankInfoException;
import dgtc.entity.base.ListResponseData;
import dgtc.entity.base.Response;
import dgtc.entity.datasource.admin.BankInfo;
import dgtc.entity.dto.handler.bankinfo.BankInfoRequestData;
import dgtc.entity.dto.handler.bankinfo.CreateBankInfoRequestData;
import dgtc.entity.dto.handler.bankinfo.UpdateBankInfoRequestData;
import dgtc.service.admin.BankInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/api/bank")
@ResponseBody
@RequiredArgsConstructor
public class BankInfoManagementController {

  private final BankInfoService bankInfoService;

  @PostMapping
  @PreAuthorize("hasAuthority('BANK_INFO_MNG_ADD')")
  public Response createAccount(@Valid @RequestBody CreateBankInfoRequestData req) {
    try {
      bankInfoService.create(req);
      return new Response(HttpStatus.OK.value(), null);
    } catch (CreateBankInfoException e) {
      return new Response(HttpStatus.NOT_ACCEPTABLE.value(), e.getMessage());
    }
  }

  @GetMapping
  @PreAuthorize("hasAuthority('BANK_INFO_MNG_VIEW')")
  public Response getBankAccounts(@Valid BankInfoRequestData req) {
    try {
      ListResponseData listRes = bankInfoService.getBankAccount(req.getPage(), req.getSize());
      return new Response(listRes);
    } catch (GetBankInfoException e) {
      return new Response(HttpStatus.NOT_ACCEPTABLE.value(), e.getMessage());
    }
  }

  @GetMapping(("/{accountNumber}"))
  @PreAuthorize("hasAuthority('BANK_INFO_MNG_VIEW')")
  public Response getAccountBankById(@PathVariable String accountNumber) {
    try {
      BankInfo bankInfo = bankInfoService.getAccountBankById(accountNumber);
      return new Response(bankInfo);
    } catch (GetBankInfoByIdException e) {
      return new Response(HttpStatus.NOT_ACCEPTABLE.value(), e.getMessage());
    }
  }

  @PutMapping("/{accountNumber}")
  @PreAuthorize("hasAuthority('BANK_INFO_MNG_VIEW')")
  public Response updateAccount(
      @PathVariable String accountNumber, @RequestBody @Valid UpdateBankInfoRequestData req) {
    try {
      bankInfoService.updateBank(accountNumber, req);
      return new Response(HttpStatus.OK.value(), null);
    } catch (UpdateBankInfoException e) {
      return new Response(HttpStatus.NOT_ACCEPTABLE.value(), e.getMessage());
    }
  }
}
