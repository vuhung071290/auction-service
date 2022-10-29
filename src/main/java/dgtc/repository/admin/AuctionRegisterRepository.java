package dgtc.repository.admin;

import dgtc.entity.datasource.admin.AuctionRegister;
import dgtc.entity.datasource.admin.AuctionRegisterId;
import dgtc.entity.datasource.user.IAuctionsRegisterByOneUserAggregate;
import dgtc.entity.datasource.user.IUserRegisterAuctionAggregate;
import dgtc.entity.datasource.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuctionRegisterRepository
    extends JpaRepository<AuctionRegister, AuctionRegisterId> {
  @Query(
      value =
          "select a.userRegisterAuction as user, a.status as status from AuctionRegister a where a.auctionId = :auctionId")
  Page<IUserRegisterAuctionAggregate> usersRegisterAuction(
      @Param("auctionId") Long auctionId, Pageable paging);

  @Query(
      value =
          "select a.userRegisterAuction from AuctionRegister a where a.auctionId = :auctionId and a.status = :status")
  List<User> usersRegisterAuctionWithStatus(
      @Param("auctionId") Long auctionId, @Param("status") int status);

  @Query(
      value =
          "select a.auctionRegister as auction, a.status as status from AuctionRegister a where a.userId = :userId AND a.createdDate > :startQueryDate AND a.createdDate < :endQueryDate")
  List<IAuctionsRegisterByOneUserAggregate> auctionsRegisterByOneUser(
      @Param("userId") Long userId,
      @Param("startQueryDate") Long startQueryDate,
      @Param("endQueryDate") Long endQueryDate);
}
