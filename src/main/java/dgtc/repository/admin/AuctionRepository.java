package dgtc.repository.admin;

import dgtc.entity.datasource.admin.Auction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/** @author hunglv */
@Repository
public interface AuctionRepository extends JpaRepository<Auction, String> {

  @Query(
      value =
          "SELECT a FROM Auction a WHERE a.createdDate >= ?1 AND a.createdDate <=?2 and a.name LIKE CONCAT('%',?3,'%')")
  Page<Auction> searchAuctionByCreatedDateAndName(
      long startSearchDate, long endSearchDate, String search, Pageable paging);

  @Query(
      value =
          "SELECT a FROM Auction a WHERE a.createdDate >= ?1 AND a.createdDate <=?2 and a.status = ?3")
  Page<Auction> searchAuctionByCreatedDateAndStatus(
      long startSearchDate, long endSearchDate, int status, Pageable paging);

  @Query(value = "SELECT a FROM Auction a WHERE a.createdDate >= ?1 AND a.createdDate <=?2")
  Page<Auction> searchAuctionByCreatedDate(
      long startSearchDate, long endSearchDate, Pageable paging);

  Optional<Auction> findByAuctionId(long auctionId);
}
