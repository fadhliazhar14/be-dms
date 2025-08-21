package com.bank_dki.be_dms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDTO {
    private Short custId;
    private String custCifNumber;
    private String custStatus;
    private String custCabang;
    private String custGolNasabah;
    private String custRisiko;
    private String custGolPajak;
    private String custNoRek;
    private String custTglBuka;
    private String custHubBank;
    private String custSeqNumber;
    private LocalDateTime custCreateDate;
    private LocalDateTime custUpdateDate;
    private String custCreateBy;
    private String custUpdateBy;
    private String custFilePath;
    private String custFileName;
    
    // Personal Information
    private String prsnNama;
    private String prsnJenisKelamin;
    private String prsnPendidikan;
    private String prsnTempatLahir;
    private String prsnAgama;
    private String prsnStatusPernikahan;
    private String prsnTanggalLahir;
    private String prsnWargaNegara;
    private String prsnIbuKandung;
    
    // Card Information
    private String cardNik;
    private String cardMasaBerlaku;
    private String cardNpwp;
    
    // Address Information
    private String adrsStatusRumah;
    private String adrsNomorHp;
    private String adrsTelpRumah;
    private String adrsTipeAlamat;
    private String adrsSurel;
    private String adrsProvinsi;
    private String adrsKota;
    private String adrsKecamatan;
    private String adrsKelurahan;
    private String adrsRw;
    private String adrsRt;
    private String adrsKodePos;
    private String adrsAlamat1;
    private String adrsAlamat2;
    
    // Work Information
    private String workBidangUsaha;
    private String workInstansi;
    private String workKodePos;
    private String workKodeProf;
    private String workAlamatInstansi;
    private String workNomorTelp;
    private String workStatusPekerjaan;
    
    // Contact Information
    private String cntkNamaKontak;
    private String cntkNomorTelp;
    private String cntkSuamiIstri;
    private String cntkProvinsi;
    private String cntkKota;
    private String cntkAlamat;
    private String cntkHubungan;
    
    // Finance Information
    private String fnceSumberDana;
    private String fnceTujuanPenggunaan;
    private String fncePengasilanPertahun;
    private String fnceTambahanPertahun;
    private String fnceJumlahSetor;
    private String fnceJumlahTarik;
    private String fnceMaxSetorPerbulan;
    private String fnceMaxTarikPerbulan;
    
    // Others Information
    private String otrsKodeDinas;
    private String otrsDataCNCDBI;
    private String otrsDataCNCDU6;
    private String otrsDataCNBI10;
    private String otrsDataCARGCD;
    private String otrsDataCNEML1;
    private String otrsDataCNXH01;
    private String otrsDataCNXH03;
    private String custAging;
    private LocalDate custDeliverDate;
    private Boolean custIsDeleted;
}