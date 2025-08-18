package com.bank_dki.be_dms.repository;

import com.bank_dki.be_dms.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Short> {
    Optional<Customer> findByCustCifNumber(String custCifNumber);
    Optional<Customer> findByCardNik(String cardNik);
    Optional<Customer> findByCardNpwp(String cardNpwp);
    
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

    @Query("""
    SELECT c 
    FROM Customer c 
    WHERE 
        (:search IS NULL OR :search = '' OR 
        LOWER(c.prsnNama) LIKE LOWER(CONCAT('%', :search, '%')) OR
        c.custNoRek LIKE CONCAT('%', :search, '%'))
        AND (:dateFrom IS NULL OR c.custDeliverDate >= :dateFrom)
        AND (:dateTo IS NULL OR c.custDeliverDate <= :dateTo)
""")
    Page<Customer> findAllWithSearchAndDateRange(@Param("search") String search, @Param("dateFrom") LocalDate dateFrom, @Param("dateTo") LocalDate dateTo, Pageable pageable);
}