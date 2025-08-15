package com.bank_dki.be_dms.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Entity
@Table(name = "nomor")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Nomor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "NomorId")
    private Short nomorId;
    
    @Column(name = "NomorLast", nullable = false)
    private Short nomorLast;
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Nomor nomor = (Nomor) o;
        return Objects.equals(nomorId, nomor.nomorId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(nomorId);
    }
}