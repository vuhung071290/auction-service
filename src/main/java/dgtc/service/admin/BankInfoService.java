package dgtc.service.admin;

import dgtc.common.exception.CreateBankInfoException;
import dgtc.common.exception.GetBankInfoByIdException;
import dgtc.common.exception.GetBankInfoException;
import dgtc.common.exception.UpdateBankInfoException;
import dgtc.entity.base.ListResponseData;
import dgtc.entity.datasource.admin.BankInfo;
import dgtc.entity.dto.handler.bankinfo.CreateBankInfoRequestData;
import dgtc.entity.dto.handler.bankinfo.UpdateBankInfoRequestData;
import dgtc.repository.admin.BankInfoRepository;
import dgtc.utils.MessageUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class BankInfoService {
  @Autowired private final BankInfoRepository bankInfoRepository;

  public void create(CreateBankInfoRequestData req) throws CreateBankInfoException {
    boolean isExist = false;
    try {
      BankInfo newBankInfo =
          BankInfo.create(req.getAccountNumber(), req.getNameAccount(), req.getBranch());
      isExist = bankInfoRepository.existsById(newBankInfo.getAccountNumber());
      if (!isExist) {
        bankInfoRepository.save(newBankInfo);
        return;
      }
    } catch (Exception e) {
      if (e instanceof CreateBankInfoException) {
        throw new CreateBankInfoException(e.getMessage());
      }
      throw new CreateBankInfoException(MessageUtils.message("accountnumber.createerror"));
    }
    throw new CreateBankInfoException(MessageUtils.message("accountnumber.exist"));
  }

  public ListResponseData getBankAccount(int page, int size) throws GetBankInfoException {
    try {
      Pageable paging = PageRequest.of(page - 1, size, Sort.by("createdDate").descending());
      Page<BankInfo> pageData = bankInfoRepository.findAll(paging);
      ListResponseData listRes =
          new ListResponseData(pageData.getContent(), pageData.getTotalElements(), page, size);
      return listRes;
    } catch (Exception e) {
      throw new GetBankInfoException(MessageUtils.message("error.mess"));
    }
  }

  public BankInfo getAccountBankById(String accountNumber) throws GetBankInfoByIdException {
    BankInfo bankInfo = bankInfoRepository.findById(accountNumber).orElse(null);
    if (bankInfo == null) {
      throw new GetBankInfoByIdException(MessageUtils.message("accountnumber.notexist"));
    }
    try {
      return bankInfo;
    } catch (Exception e) {
      throw new GetBankInfoByIdException(MessageUtils.message("error.mess"));
    }
  }

  public void updateBank(String accountNumber, UpdateBankInfoRequestData req)
      throws UpdateBankInfoException {
    BankInfo bankInfo = bankInfoRepository.findById(accountNumber).orElse(null);
    if (bankInfo == null) {
      throw new UpdateBankInfoException(MessageUtils.message("accountnumber.notexist"));
    }
    try {
      boolean isUpdate = false;
      if (req.getActive() != null) {
        bankInfo.setIsActive(req.getActive());
        isUpdate = true;
      }
      if (req.getNameAccount() != null) {
        bankInfo.setNameAccount(req.getNameAccount());
        isUpdate = true;
      }
      if (req.getBranch() != null) {
        bankInfo.setBranch(req.getBranch());
        isUpdate = true;
      }
      if (isUpdate) {
        bankInfo.setUpdatedDate(System.currentTimeMillis());
        bankInfoRepository.save(bankInfo);
      }
    } catch (Exception e) {
      throw new UpdateBankInfoException(MessageUtils.message("error.mess"));
    }
  }
}
