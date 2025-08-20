package com.bank_dki.be_dms.repository;

import com.bank_dki.be_dms.entity.Nomor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface NomorRepository extends JpaRepository<Nomor, Short> {
    @Query("SELECT COALESCE(MAX(n.nomorLast), 0) FROM Nomor n")
    Short findMaxNomorLast();
}
