package dgtc.repository.admin;

import dgtc.entity.datasource.admin.Property;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/** @author hunglv */
@Repository
public interface PropertyRepository extends JpaRepository<Property, String> {

  @Query(
      value =
          "SELECT a FROM Property a WHERE a.createdDate >= ?1 AND a.createdDate <=?2 and a.name LIKE CONCAT('%',?3,'%')")
  Page<Property> searchPropertyByCreatedDateAndName(
      long startSearchDate, long endSearchDate, String search, Pageable paging);

  @Query(value = "SELECT a FROM Property a WHERE a.createdDate >= ?1 AND a.createdDate <=?2")
  Page<Property> searchPropertyByCreatedDate(
      long startSearchDate, long endSearchDate, Pageable paging);

  Optional<Property> findByPropertyId(long propertyId);
}
