package dgtc.repository.admin;

import dgtc.entity.datasource.admin.BankInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BankInfoRepository extends JpaRepository<BankInfo, String> {}
