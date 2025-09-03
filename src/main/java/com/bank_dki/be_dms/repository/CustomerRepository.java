package com.bank_dki.be_dms.repository;

import com.bank_dki.be_dms.dto.CustomerDocDTO;
import com.bank_dki.be_dms.dto.CustomerStatusCountDto;
import com.bank_dki.be_dms.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Short> {
    Optional<Customer> findByCustCifNumber(String custCifNumber);
    Optional<Customer> findByCustNoRek(String custNoRek);
    Optional<Customer> findByCardNik(String cardNik);
    Optional<Customer> findByCardNpwp(String cardNpwp);
    List<Customer> findByCustCreateBy(String username);
    
    @Query("SELECT c FROM Customer c WHERE c.custIsDeleted = false OR c.custIsDeleted IS NULL")
    List<Customer> findAllActiveCustomers();
    
    @Query("SELECT c FROM Customer c WHERE c.custStatus = :status")
    List<Customer> findByStatus(@Param("status") String status);
    
    @Query("SELECT c FROM Customer c WHERE c.custCabang = :cabang")
    List<Customer> findByCabang(@Param("cabang") String cabang);
    
    @Query("SELECT c FROM Customer c WHERE c.prsnNama LIKE %:nama%")
    List<Customer> findByNamaContaining(@Param("nama") String nama);
    
    Boolean existsByCustCifNumber(String custCifNumber);
    Boolean existsByCardNik(String cardNik);
    Boolean existsByCardNpwp(String cardNpwp);
    
    @Query("SELECT c.custStatus, COUNT(c) FROM Customer c WHERE c.custIsDeleted = false OR c.custIsDeleted IS NULL GROUP BY c.custStatus")
    List<Object[]> countCustomersByStatus();

    @Query("SELECT c.custStatus, COUNT(c) " +
            "FROM Customer c " +
            "WHERE (c.custIsDeleted = false OR c.custIsDeleted IS NULL) " +
            "AND c.custCreateDate BETWEEN :startDate AND :endDate " +
            "AND (:isAdmin = true OR c.custCreateBy = :createdBy)" +
            "GROUP BY c.custStatus")
    List<Object[]> countCustomersByStatusBetween(
            @Param("isAdmin") boolean isAdmin,
            @Param("createdBy") String createdBy,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query("""
    SELECT new com.bank_dki.be_dms.dto.CustomerStatusCountDto$CategoriesForOperator(
        CASE WHEN c.custStatus = 'Scanning' THEN 'Scanning' ELSE 'Unscanned' END,
        COUNT(c)
    )
    FROM Customer c
    WHERE (c.custIsDeleted = false OR c.custIsDeleted IS NULL)
      AND c.custCreateDate BETWEEN :startDate AND :endDate
      AND c.custCreateBy = :createdBy
    GROUP BY CASE WHEN c.custStatus = 'Scanning' THEN 'Scanning' ELSE 'Unscanned' END
""")
    List<CustomerStatusCountDto.CategoriesForOperator> getCategoriesForOperator(
            @Param("createdBy") String createdBy,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);


    @Query("""
    SELECT 
        SUM(CASE WHEN c.custStatus = 'Deliver' THEN 1 ELSE 0 END),
        SUM(CASE WHEN c.custStatus <> 'Deliver' THEN 1 ELSE 0 END)
    FROM Customer c
    WHERE (c.custIsDeleted = false OR c.custIsDeleted IS NULL)
      AND c.custCreateDate BETWEEN :startDate AND :endDate
      AND (:isAdmin = true OR c.custCreateBy = :createdBy)
""")
    Object getTotalAndCompleted(
            @Param("isAdmin") boolean isAdmin,
            @Param("createdBy") String createdBy,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query("""
    SELECT c 
    FROM Customer c 
    WHERE 
        (:search IS NULL 
        OR :search = ''  
        OR LOWER(c.prsnNama) LIKE LOWER(CONCAT('%', :search, '%'))
        OR c.custNoRek LIKE CONCAT('%', :search, '%'))
        AND (c.custIsDeleted = false OR c.custIsDeleted IS NULL)
        AND (:dateFrom IS NULL OR c.custDeliverDate >= :dateFrom)
        AND (:dateTo IS NULL OR c.custDeliverDate <= :dateTo)
        AND (
            :isAdmin = true
            OR c.custCreateBy = :createdBy
        )
""")
    Page<Customer> findAllWithSearchAndDateRange(
            @Param("createdBy") String createdBy,
            @Param("isAdmin") boolean isAdmin,
            @Param("search") String search,
            @Param("dateFrom") LocalDate dateFrom,
            @Param("dateTo") LocalDate dateTo,
            Pageable pageable
    );

    @Query("SELECT new com.bank_dki.be_dms.dto.CustomerDocDTO(c.custFilePath, c.custFileName) " +
            "FROM Customer c WHERE c.custId = :custId")
    Optional<CustomerDocDTO> findCustFileByCustId(@Param("custId") Short custId);
}