package dgtc.repository.admin;

import dgtc.entity.datasource.admin.AuctionHistorical;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AuctionHistoricalRepository extends JpaRepository<AuctionHistorical, String> {
  @Query(value = "SELECT a FROM AuctionHistorical a WHERE a.auctionId = ?1")
  Page<AuctionHistorical> getAuctionHistoricalByAuctionId(Long auctionId, Pageable paging);

  void deleteAllByAuctionIdEquals(Long auctionId);
}
