package com.bank_dki.be_dms.service;

import com.bank_dki.be_dms.dto.CustomerDTO;
import com.bank_dki.be_dms.entity.Customer;
import com.bank_dki.be_dms.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerService {
    
    private final CustomerRepository customerRepository;
    
    public List<CustomerDTO> getAllCustomers() {
        return customerRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<CustomerDTO> getAllActiveCustomers() {
        return customerRepository.findAllActiveCustomers().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public Optional<CustomerDTO> getCustomerById(Short id) {
        return customerRepository.findById(id)
                .map(this::convertToDTO);
    }
    
    public Optional<CustomerDTO> getCustomerByCifNumber(String cifNumber) {
        return customerRepository.findByCustCifNumber(cifNumber)
                .map(this::convertToDTO);
    }
    
    public Optional<CustomerDTO> getCustomerByNik(String nik) {
        return customerRepository.findByCardNik(nik)
                .map(this::convertToDTO);
    }
    
    public Customer createCustomer(CustomerDTO customerDTO) {
        if (customerRepository.existsByCustCifNumber(customerDTO.getCustCifNumber())) {
            throw new RuntimeException("Customer with CIF Number " + customerDTO.getCustCifNumber() + " already exists!");
        }
        
        if (customerDTO.getCardNik() != null && customerRepository.existsByCardNik(customerDTO.getCardNik())) {
            throw new RuntimeException("Customer with NIK " + customerDTO.getCardNik() + " already exists!");
        }
        
        Customer customer = convertToEntity(customerDTO);
        return customerRepository.save(customer);
    }
    
    public Customer updateCustomer(Short id, CustomerDTO customerDTO) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        
        // Check if CIF is being changed and if it already exists
        if (!customer.getCustCifNumber().equals(customerDTO.getCustCifNumber()) && 
            customerRepository.existsByCustCifNumber(customerDTO.getCustCifNumber())) {
            throw new RuntimeException("Customer with CIF Number " + customerDTO.getCustCifNumber() + " already exists!");
        }
        
        // Check if NIK is being changed and if it already exists
        if (customerDTO.getCardNik() != null && 
            (customer.getCardNik() == null || !customer.getCardNik().equals(customerDTO.getCardNik())) && 
            customerRepository.existsByCardNik(customerDTO.getCardNik())) {
            throw new RuntimeException("Customer with NIK " + customerDTO.getCardNik() + " already exists!");
        }
        
        updateCustomerFromDTO(customer, customerDTO);
        return customerRepository.save(customer);
    }
    
    public void deleteCustomer(Short id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        customer.setCustIsDeleted(true);
        customerRepository.save(customer);
    }
    
    public List<CustomerDTO> getCustomersByStatus(String status) {
        return customerRepository.findByStatus(status).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<CustomerDTO> getCustomersByCabang(String cabang) {
        return customerRepository.findByCabang(cabang).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<CustomerDTO> searchCustomersByName(String nama) {
        return customerRepository.findByNamaContaining(nama).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    private CustomerDTO convertToDTO(Customer customer) {
        CustomerDTO dto = new CustomerDTO();
        dto.setCustId(customer.getCustId());
        dto.setCustCifNumber(customer.getCustCifNumber());
        dto.setCustStatus(customer.getCustStatus());
        dto.setCustCabang(customer.getCustCabang());
        dto.setCustGolNasabah(customer.getCustGolNasabah());
        dto.setCustRisiko(customer.getCustRisiko());
        dto.setCustGolPajak(customer.getCustGolPajak());
        dto.setCustNoRek(customer.getCustNoRek());
        dto.setCustTglBuka(customer.getCustTglBuka());
        dto.setCustHubBank(customer.getCustHubBank());
        dto.setCustSeqNumber(customer.getCustSeqNumber());
        dto.setCustCreateDate(customer.getCustCreateDate());
        dto.setCustUpdateDate(customer.getCustUpdateDate());
        dto.setCustCreateBy(customer.getCustCreateBy());
        dto.setCustUpdateBy(customer.getCustUpdateBy());
        
        // Personal Information
        dto.setPrsnNama(customer.getPrsnNama());
        dto.setPrsnJenisKelamin(customer.getPrsnJenisKelamin());
        dto.setPrsnPendidikan(customer.getPrsnPendidikan());
        dto.setPrsnTempatLahir(customer.getPrsnTempatLahir());
        dto.setPrsnAgama(customer.getPrsnAgama());
        dto.setPrsnStatusPernikahan(customer.getPrsnStatusPernikahan());
        dto.setPrsnTanggalLahir(customer.getPrsnTanggalLahir());
        dto.setPrsnWargaNegara(customer.getPrsnWargaNegara());
        dto.setPrsnIbuKandung(customer.getPrsnIbuKandung());
        
        // Card Information
        dto.setCardNik(customer.getCardNik());
        dto.setCardMasaBerlaku(customer.getCardMasaBerlaku());
        dto.setCardNpwp(customer.getCardNpwp());
        
        // Address Information
        dto.setAdrsStatusRumah(customer.getAdrsStatusRumah());
        dto.setAdrsNomorHp(customer.getAdrsNomorHp());
        dto.setAdrsTelpRumah(customer.getAdrsTelpRumah());
        dto.setAdrsTipeAlamat(customer.getAdrsTipeAlamat());
        dto.setAdrsSurel(customer.getAdrsSurel());
        dto.setAdrsProvinsi(customer.getAdrsProvinsi());
        dto.setAdrsKota(customer.getAdrsKota());
        dto.setAdrsKecamatan(customer.getAdrsKecamatan());
        dto.setAdrsKelurahan(customer.getAdrsKelurahan());
        dto.setAdrsRw(customer.getAdrsRw());
        dto.setAdrsRt(customer.getAdrsRt());
        dto.setAdrsKodePos(customer.getAdrsKodePos());
        dto.setAdrsAlamat1(customer.getAdrsAlamat1());
        dto.setAdrsAlamat2(customer.getAdrsAlamat2());
        
        // Work Information
        dto.setWorkBidangUsaha(customer.getWorkBidangUsaha());
        dto.setWorkInstansi(customer.getWorkInstansi());
        dto.setWorkKodePos(customer.getWorkKodePos());
        dto.setWorkKodeProf(customer.getWorkKodeProf());
        dto.setWorkAlamatInstansi(customer.getWorkAlamatInstansi());
        dto.setWorkNomorTelp(customer.getWorkNomorTelp());
        dto.setWorkStatusPekerjaan(customer.getWorkStatusPekerjaan());
        
        // Contact Information
        dto.setCntkNamaKontak(customer.getCntkNamaKontak());
        dto.setCntkNomorTelp(customer.getCntkNomorTelp());
        dto.setCntkSuamiIstri(customer.getCntkSuamiIstri());
        dto.setCntkProvinsi(customer.getCntkProvinsi());
        dto.setCntkKota(customer.getCntkKota());
        dto.setCntkAlamat(customer.getCntkAlamat());
        dto.setCntkHubungan(customer.getCntkHubungan());
        
        // Finance Information
        dto.setFnceSumberDana(customer.getFnceSumberDana());
        dto.setFnceTujuanPenggunaan(customer.getFnceTujuanPenggunaan());
        dto.setFncePengasilanPertahun(customer.getFncePengasilanPertahun());
        dto.setFnceTambahanPertahun(customer.getFnceTambahanPertahun());
        dto.setFnceJumlahSetor(customer.getFnceJumlahSetor());
        dto.setFnceJumlahTarik(customer.getFnceJumlahTarik());
        dto.setFnceMaxSetorPerbulan(customer.getFnceMaxSetorPerbulan());
        dto.setFnceMaxTarikPerbulan(customer.getFnceMaxTarikPerbulan());
        
        // Others Information
        dto.setOtrsKodeDinas(customer.getOtrsKodeDinas());
        dto.setOtrsDataCNCDBI(customer.getOtrsDataCNCDBI());
        dto.setOtrsDataCNCDU6(customer.getOtrsDataCNCDU6());
        dto.setOtrsDataCNBI10(customer.getOtrsDataCNBI10());
        dto.setOtrsDataCARGCD(customer.getOtrsDataCARGCD());
        dto.setOtrsDataCNEML1(customer.getOtrsDataCNEML1());
        dto.setOtrsDataCNXH01(customer.getOtrsDataCNXH01());
        dto.setOtrsDataCNXH03(customer.getOtrsDataCNXH03());
        dto.setCustAging(customer.getCustAging());
        dto.setCustDeliverDate(customer.getCustDeliverDate());
        dto.setCustIsDeleted(customer.getCustIsDeleted());
        
        return dto;
    }
    
    private Customer convertToEntity(CustomerDTO dto) {
        Customer customer = new Customer();
        updateCustomerFromDTO(customer, dto);
        return customer;
    }
    
    private void updateCustomerFromDTO(Customer customer, CustomerDTO dto) {
        customer.setCustCifNumber(dto.getCustCifNumber());
        customer.setCustStatus(dto.getCustStatus());
        customer.setCustCabang(dto.getCustCabang());
        customer.setCustGolNasabah(dto.getCustGolNasabah());
        customer.setCustRisiko(dto.getCustRisiko());
        customer.setCustGolPajak(dto.getCustGolPajak());
        customer.setCustNoRek(dto.getCustNoRek());
        customer.setCustTglBuka(dto.getCustTglBuka());
        customer.setCustHubBank(dto.getCustHubBank());
        customer.setCustSeqNumber(dto.getCustSeqNumber());
        customer.setCustCreateBy(dto.getCustCreateBy());
        customer.setCustUpdateBy(dto.getCustUpdateBy());
        
        // Personal Information
        customer.setPrsnNama(dto.getPrsnNama());
        customer.setPrsnJenisKelamin(dto.getPrsnJenisKelamin());
        customer.setPrsnPendidikan(dto.getPrsnPendidikan());
        customer.setPrsnTempatLahir(dto.getPrsnTempatLahir());
        customer.setPrsnAgama(dto.getPrsnAgama());
        customer.setPrsnStatusPernikahan(dto.getPrsnStatusPernikahan());
        customer.setPrsnTanggalLahir(dto.getPrsnTanggalLahir());
        customer.setPrsnWargaNegara(dto.getPrsnWargaNegara());
        customer.setPrsnIbuKandung(dto.getPrsnIbuKandung());
        
        // Card Information
        customer.setCardNik(dto.getCardNik());
        customer.setCardMasaBerlaku(dto.getCardMasaBerlaku());
        customer.setCardNpwp(dto.getCardNpwp());
        
        // Address Information
        customer.setAdrsStatusRumah(dto.getAdrsStatusRumah());
        customer.setAdrsNomorHp(dto.getAdrsNomorHp());
        customer.setAdrsTelpRumah(dto.getAdrsTelpRumah());
        customer.setAdrsTipeAlamat(dto.getAdrsTipeAlamat());
        customer.setAdrsSurel(dto.getAdrsSurel());
        customer.setAdrsProvinsi(dto.getAdrsProvinsi());
        customer.setAdrsKota(dto.getAdrsKota());
        customer.setAdrsKecamatan(dto.getAdrsKecamatan());
        customer.setAdrsKelurahan(dto.getAdrsKelurahan());
        customer.setAdrsRw(dto.getAdrsRw());
        customer.setAdrsRt(dto.getAdrsRt());
        customer.setAdrsKodePos(dto.getAdrsKodePos());
        customer.setAdrsAlamat1(dto.getAdrsAlamat1());
        customer.setAdrsAlamat2(dto.getAdrsAlamat2());
        
        // Work Information
        customer.setWorkBidangUsaha(dto.getWorkBidangUsaha());
        customer.setWorkInstansi(dto.getWorkInstansi());
        customer.setWorkKodePos(dto.getWorkKodePos());
        customer.setWorkKodeProf(dto.getWorkKodeProf());
        customer.setWorkAlamatInstansi(dto.getWorkAlamatInstansi());
        customer.setWorkNomorTelp(dto.getWorkNomorTelp());
        customer.setWorkStatusPekerjaan(dto.getWorkStatusPekerjaan());
        
        // Contact Information
        customer.setCntkNamaKontak(dto.getCntkNamaKontak());
        customer.setCntkNomorTelp(dto.getCntkNomorTelp());
        customer.setCntkSuamiIstri(dto.getCntkSuamiIstri());
        customer.setCntkProvinsi(dto.getCntkProvinsi());
        customer.setCntkKota(dto.getCntkKota());
        customer.setCntkAlamat(dto.getCntkAlamat());
        customer.setCntkHubungan(dto.getCntkHubungan());
        
        // Finance Information
        customer.setFnceSumberDana(dto.getFnceSumberDana());
        customer.setFnceTujuanPenggunaan(dto.getFnceTujuanPenggunaan());
        customer.setFncePengasilanPertahun(dto.getFncePengasilanPertahun());
        customer.setFnceTambahanPertahun(dto.getFnceTambahanPertahun());
        customer.setFnceJumlahSetor(dto.getFnceJumlahSetor());
        customer.setFnceJumlahTarik(dto.getFnceJumlahTarik());
        customer.setFnceMaxSetorPerbulan(dto.getFnceMaxSetorPerbulan());
        customer.setFnceMaxTarikPerbulan(dto.getFnceMaxTarikPerbulan());
        
        // Others Information
        customer.setOtrsKodeDinas(dto.getOtrsKodeDinas());
        customer.setOtrsDataCNCDBI(dto.getOtrsDataCNCDBI());
        customer.setOtrsDataCNCDU6(dto.getOtrsDataCNCDU6());
        customer.setOtrsDataCNBI10(dto.getOtrsDataCNBI10());
        customer.setOtrsDataCARGCD(dto.getOtrsDataCARGCD());
        customer.setOtrsDataCNEML1(dto.getOtrsDataCNEML1());
        customer.setOtrsDataCNXH01(dto.getOtrsDataCNXH01());
        customer.setOtrsDataCNXH03(dto.getOtrsDataCNXH03());
        customer.setCustAging(dto.getCustAging());
        customer.setCustDeliverDate(dto.getCustDeliverDate());
        customer.setCustIsDeleted(dto.getCustIsDeleted());
    }
}