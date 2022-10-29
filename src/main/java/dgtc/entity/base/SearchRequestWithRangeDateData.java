package dgtc.entity.base;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.LocalDateTime;

/** @author hunglv */
@Getter
@Setter
public class SearchRequestWithRangeDateData extends SearchRequestData {

  /** startDateSearch */
  private Long startDateSearch;

  /** endDateSearch */
  private Long endDateSearch;

  /**
   * Ex: searchDateRange : 30 => startDateSearch = today ; endDateSearch = today + 30 days
   * searchDateRange : -30 => startDateSearch = today - 30 days ; endDateSearch = today
   */
  private Integer searchDateRange;

  /** The field that search with [startDateSearch - endDateSearch] */
  private String fieldDate;

  public void init() {
    super.init();

    if (startDateSearch == null || endDateSearch == null) {
      LocalDateTime localDateTime = LocalDateTime.now();

      if (searchDateRange == null) {
        startDateSearch = Timestamp.valueOf(localDateTime.minusDays(30)).getTime();
        endDateSearch = Timestamp.valueOf(localDateTime).getTime();
      } else {
        if (searchDateRange > 0) {
          startDateSearch = Timestamp.valueOf(localDateTime).getTime();
          endDateSearch = Timestamp.valueOf(localDateTime.plusDays(searchDateRange)).getTime();
        } else {
          startDateSearch = Timestamp.valueOf(localDateTime.minusDays(searchDateRange)).getTime();
          endDateSearch = Timestamp.valueOf(localDateTime).getTime();
        }
      }
    }

    if (fieldDate == null) {
      fieldDate = "createdDate";
    }
  }
}
