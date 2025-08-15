package com.bank_dki.be_dms.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "cust")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CustId")
    private Short custId;
    
    @Column(name = "CustCifNumber", length = 100, nullable = false)
    private String custCifNumber;
    
    @Column(name = "CustStatus", length = 100)
    private String custStatus;
    
    @Column(name = "CustCabang", length = 100)
    private String custCabang;
    
    @Column(name = "CustGolNasabah", length = 100)
    private String custGolNasabah;
    
    @Column(name = "CustRisiko", length = 100)
    private String custRisiko;
    
    @Column(name = "CustGolPajak", length = 100)
    private String custGolPajak;
    
    @Column(name = "CustNoRek", length = 100)
    private String custNoRek;
    
    @Column(name = "CustTglBuka", length = 100)
    private String custTglBuka;
    
    @Column(name = "CustHubBank", length = 100)
    private String custHubBank;
    
    @Column(name = "CustSeqNumber", length = 100)
    private String custSeqNumber;
    
    @Lob
    @Column(name = "CustDocImage")
    private byte[] custDocImage;
    
    @Column(name = "CustDocImage_GXI", length = 2048)
    private String custDocImageGxi;
    
    @Column(name = "CustCreateDate")
    private LocalDateTime custCreateDate;
    
    @Column(name = "CustUpdateDate")
    private LocalDateTime custUpdateDate;
    
    @Column(name = "CustCreateBy", length = 100)
    private String custCreateBy;
    
    @Column(name = "CustUpdateBy", length = 100)
    private String custUpdateBy;
    
    // Personal Information
    @Column(name = "PrsnNama", length = 100)
    private String prsnNama;
    
    @Column(name = "PrsnJenisKelamin", length = 100)
    private String prsnJenisKelamin;
    
    @Column(name = "PrsnPendidikan", length = 100)
    private String prsnPendidikan;
    
    @Column(name = "PrsnTempatLahir", length = 100)
    private String prsnTempatLahir;
    
    @Column(name = "PrsnAgama", length = 100)
    private String prsnAgama;
    
    @Column(name = "PrsnStatusPernikahan", length = 100)
    private String prsnStatusPernikahan;
    
    @Column(name = "PrsnTanggalLahir", length = 100)
    private String prsnTanggalLahir;
    
    @Column(name = "PrsnWargaNegara", length = 100)
    private String prsnWargaNegara;
    
    @Column(name = "PrsnIbuKandung", length = 100)
    private String prsnIbuKandung;
    
    // Card Information
    @Column(name = "CardNik", length = 100)
    private String cardNik;
    
    @Column(name = "CardMasaBerlaku", length = 100)
    private String cardMasaBerlaku;
    
    @Column(name = "CardNpwp", length = 100)
    private String cardNpwp;
    
    // Address Information
    @Column(name = "AdrsStatusRumah", length = 100)
    private String adrsStatusRumah;
    
    @Column(name = "AdrsNomorHp", length = 100)
    private String adrsNomorHp;
    
    @Column(name = "AdrsTelpRumah", length = 100)
    private String adrsTelpRumah;
    
    @Column(name = "AdrsTipeAlamat", length = 100)
    private String adrsTipeAlamat;
    
    @Column(name = "AdrsSurel", length = 100)
    private String adrsSurel;
    
    @Column(name = "AdrsProvinsi", length = 100)
    private String adrsProvinsi;
    
    @Column(name = "AdrsKota", length = 100)
    private String adrsKota;
    
    @Column(name = "AdrsKecamatan", length = 100)
    private String adrsKecamatan;
    
    @Column(name = "AdrsKelurahan", length = 100)
    private String adrsKelurahan;
    
    @Column(name = "AdrsRw", length = 100)
    private String adrsRw;
    
    @Column(name = "AdrsRt", length = 100)
    private String adrsRt;
    
    @Column(name = "AdrsKodePos", length = 100)
    private String adrsKodePos;
    
    @Column(name = "AdrsAlamat1", length = 100)
    private String adrsAlamat1;
    
    @Column(name = "AdrsAlamat2", length = 100)
    private String adrsAlamat2;
    
    // Work Information
    @Column(name = "WorkBidangUsaha", length = 100)
    private String workBidangUsaha;
    
    @Column(name = "WorkInstansi", length = 100)
    private String workInstansi;
    
    @Column(name = "WorkKodePos", length = 100)
    private String workKodePos;
    
    @Column(name = "WorkKodeProf", length = 100)
    private String workKodeProf;
    
    @Column(name = "WorkAlamatInstansi", length = 100)
    private String workAlamatInstansi;
    
    @Column(name = "WorkNomorTelp", length = 100)
    private String workNomorTelp;
    
    @Column(name = "WorkStatusPekerjaan", length = 100)
    private String workStatusPekerjaan;
    
    // Contact Information
    @Column(name = "CntkNamaKontak", length = 100)
    private String cntkNamaKontak;
    
    @Column(name = "CntkNomorTelp", length = 100)
    private String cntkNomorTelp;
    
    @Column(name = "CntkSuamiIstri", length = 100)
    private String cntkSuamiIstri;
    
    @Column(name = "CntkProvinsi", length = 100)
    private String cntkProvinsi;
    
    @Column(name = "CntkKota", length = 100)
    private String cntkKota;
    
    @Column(name = "CntkAlamat", length = 100)
    private String cntkAlamat;
    
    @Column(name = "CntkHubungan", length = 100)
    private String cntkHubungan;
    
    // Finance Information
    @Column(name = "FnceSumberDana", length = 100)
    private String fnceSumberDana;
    
    @Column(name = "FnceTujuanPenggunaan", length = 100)
    private String fnceTujuanPenggunaan;
    
    @Column(name = "FncePengasilanPertahun", length = 100)
    private String fncePengasilanPertahun;
    
    @Column(name = "FnceTambahanPertahun", length = 100)
    private String fnceTambahanPertahun;
    
    @Column(name = "FnceJumlahSetor", length = 100)
    private String fnceJumlahSetor;
    
    @Column(name = "FnceJumlahTarik", length = 100)
    private String fnceJumlahTarik;
    
    @Column(name = "FnceMaxSetorPerbulan", length = 100)
    private String fnceMaxSetorPerbulan;
    
    @Column(name = "FnceMaxTarikPerbulan", length = 100)
    private String fnceMaxTarikPerbulan;
    
    // Others Information
    @Column(name = "OtrsKodeDinas", length = 100)
    private String otrsKodeDinas;
    
    @Column(name = "OtrsDataCNCDBI", length = 100)
    private String otrsDataCNCDBI;
    
    @Column(name = "OtrsDataCNCDU6", length = 100)
    private String otrsDataCNCDU6;
    
    @Column(name = "OtrsDataCNBI10", length = 100)
    private String otrsDataCNBI10;
    
    @Column(name = "OtrsDataCARGCD", length = 100)
    private String otrsDataCARGCD;
    
    @Column(name = "OtrsDataCNEML1", length = 100)
    private String otrsDataCNEML1;
    
    @Column(name = "OtrsDataCNXH01", length = 100)
    private String otrsDataCNXH01;
    
    @Column(name = "OtrsDataCNXH03", length = 100)
    private String otrsDataCNXH03;
    
    @Column(name = "CustAging", length = 100)
    private String custAging;
    
    @Column(name = "CustDeliverDate")
    private LocalDate custDeliverDate;
    
    @Column(name = "CustIsDeleted")
    private Boolean custIsDeleted;
    
    @Lob
    @Column(name = "CustFileBlob")
    private byte[] custFileBlob;
    
    @PrePersist
    protected void onCreate() {
        custCreateDate = LocalDateTime.now();
        custUpdateDate = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        custUpdateDate = LocalDateTime.now();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Customer customer = (Customer) o;
        return Objects.equals(custId, customer.custId) && 
               Objects.equals(custCifNumber, customer.custCifNumber);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(custId, custCifNumber);
    }
}