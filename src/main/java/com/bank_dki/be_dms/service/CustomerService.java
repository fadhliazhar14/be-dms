package com.bank_dki.be_dms.service;

import com.bank_dki.be_dms.common.PageRequestDTO;
import com.bank_dki.be_dms.common.PageResponseDTO;
import com.bank_dki.be_dms.dto.CustomerDTO;
import com.bank_dki.be_dms.entity.Customer;
import com.bank_dki.be_dms.exception.BusinessValidationException;
import com.bank_dki.be_dms.repository.CustomerRepository;
import com.bank_dki.be_dms.util.CurrentUserUtils;
import com.bank_dki.be_dms.util.PageUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final CurrentUserUtils currentUserUtils;


    public List<CustomerDTO> getAllCustomers() {
        String username = currentUserUtils.getCurrentUsername();
        boolean isAdmin = currentUserUtils.hasRole("ROLE_ADMIN");
        List<Customer> customers = isAdmin ? customerRepository.findAll() : customerRepository.findByCustCreateBy(username);

        return customers.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<CustomerDTO> getAllActiveCustomers() {
        String username = currentUserUtils.getCurrentUsername();
        boolean isAdmin = currentUserUtils.hasRole("ROLE_ADMIN");
        List<Customer> customers = isAdmin ? customerRepository.findAllActiveCustomers() : customerRepository.findByCustCreateBy(username);

        return customers.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public PageResponseDTO<CustomerDTO> getAllActiveCustomers(PageRequestDTO pageRequest) {
        String username = currentUserUtils.getCurrentUsername();
        boolean isAdmin = currentUserUtils.hasRole("ROLE_ADMIN");
        Pageable pageable = PageUtil.createPageable(pageRequest);

        Page<Customer> customerPage = customerRepository.findAllWithSearchAndDateRange(username, isAdmin, pageRequest.getSearch(), pageRequest.getDateFrom(), pageRequest.getDateTo(), pageable);

        List<CustomerDTO> customerResponses = customerPage.getContent().stream()
                .map(this::convertToDTO)
                .toList();

        return PageUtil.createPageResponse(
                customerResponses,
                pageable,
                customerPage.getTotalElements()
        );
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

    @Transactional
    public void saveCustomersFromCsv(MultipartFile file) {
        String currentUploaderUsername = currentUserUtils.getCurrentUsername();
        String CUST_STATUS_UNREGISTERED = "Unregistered";

        try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            if (file.isEmpty()) {
                throw new BusinessValidationException("File is empty");
            }

            Iterable<CSVRecord> records = CSVFormat.DEFAULT
                    .withFirstRecordAsHeader()
                    .parse(reader);

            List<Customer> customers = new ArrayList<>();
            for (CSVRecord record : records) {
                Customer customer = new Customer();
                customer.setCustCifNumber(record.get("custCifNumber"));

                customer.setCustStatus(record.get(CUST_STATUS_UNREGISTERED));
                customer.setCustCabang(record.get("custCabang"));
                customer.setCustGolNasabah(record.get("custGolNasabah"));
                customer.setCustRisiko(record.get("custRisiko"));
                customer.setCustGolPajak(record.get("custGolPajak"));
                customer.setCustNoRek(record.get("custNoRek"));
                customer.setCustTglBuka(record.get("custTglBuka"));
                customer.setCustHubBank(record.get("custHubBank"));
                customer.setCustCreateBy(currentUploaderUsername);

                // Personal Information
                customer.setPrsnNama(record.get("prsnNama"));
                customer.setPrsnJenisKelamin(record.get("prsnJenisKelamin"));
                customer.setPrsnPendidikan(record.get("prsnPendidikan"));
                customer.setPrsnTempatLahir(record.get("prsnTempatLahir"));
                customer.setPrsnAgama(record.get("prsnAgama"));
                customer.setPrsnStatusPernikahan(record.get("prsnStatusPernikahan"));
                customer.setPrsnTanggalLahir(record.get("prsnTanggalLahir"));
                customer.setPrsnWargaNegara(record.get("prsnWargaNegara"));
                customer.setPrsnIbuKandung(record.get("prsnIbuKandung"));

                // Card Information
                customer.setCardNik(record.get("cardNik"));
                customer.setCardMasaBerlaku(record.get("cardMasaBerlaku"));
                customer.setCardNpwp(record.get("cardNpwp"));

                // Address Information
                customer.setAdrsStatusRumah(record.get("adrsStatusRumah"));
                customer.setAdrsNomorHp(record.get("adrsNomorHp"));
                customer.setAdrsTelpRumah(record.get("adrsTelpRumah"));
                customer.setAdrsTipeAlamat(record.get("adrsTipeAlamat"));
                customer.setAdrsSurel(record.get("adrsSurel"));
                customer.setAdrsProvinsi(record.get("adrsProvinsi"));
                customer.setAdrsKota(record.get("adrsKota"));
                customer.setAdrsKecamatan(record.get("adrsKecamatan"));
                customer.setAdrsKelurahan(record.get("adrsKelurahan"));
                customer.setAdrsRw(record.get("adrsRw"));
                customer.setAdrsRt(record.get("adrsRt"));
                customer.setAdrsKodePos(record.get("adrsKodePos"));
                customer.setAdrsAlamat1(record.get("adrsAlamat1"));
                customer.setAdrsAlamat2(record.get("adrsAlamat2"));

                // Work Information
                customer.setWorkBidangUsaha(record.get("workBidangUsaha"));
                customer.setWorkInstansi(record.get("workInstansi"));
                customer.setWorkKodePos(record.get("workKodePos"));
                customer.setWorkKodeProf(record.get("workKodeProf"));
                customer.setWorkAlamatInstansi(record.get("workAlamatInstansi"));
                customer.setWorkNomorTelp(record.get("workNomorTelp"));
                customer.setWorkStatusPekerjaan(record.get("workStatusPekerjaan"));

                // Contact Information
                customer.setCntkNamaKontak(record.get("cntkNamaKontak"));
                customer.setCntkNomorTelp(record.get("cntkNomorTelp"));
                customer.setCntkSuamiIstri(record.get("cntkSuamiIstri"));
                customer.setCntkProvinsi(record.get("cntkProvinsi"));
                customer.setCntkKota(record.get("cntkKota"));
                customer.setCntkAlamat(record.get("cntkAlamat"));
                customer.setCntkHubungan(record.get("cntkHubungan"));

                // Finance Information
                customer.setFnceSumberDana(record.get("fnceSumberDana"));
                customer.setFnceTujuanPenggunaan(record.get("fnceTujuanPenggunaan"));
                customer.setFncePengasilanPertahun(record.get("fncePengasilanPertahun"));
                customer.setFnceTambahanPertahun(record.get("fnceTambahanPertahun"));
                customer.setFnceJumlahSetor(record.get("fnceJumlahSetor"));
                customer.setFnceJumlahTarik(record.get("fnceJumlahTarik"));
                customer.setFnceMaxSetorPerbulan(record.get("fnceMaxSetorPerbulan"));
                customer.setFnceMaxTarikPerbulan(record.get("fnceMaxTarikPerbulan"));

                // Others Information
                customer.setOtrsKodeDinas(record.get("otrsKodeDinas"));
                customer.setOtrsDataCNCDBI(record.get("otrsDataCNCDBI"));
                customer.setOtrsDataCNCDU6(record.get("otrsDataCNCDU6"));
                customer.setOtrsDataCNBI10(record.get("otrsDataCNBI10"));
                customer.setOtrsDataCARGCD(record.get("otrsDataCARGCD"));
                customer.setOtrsDataCNEML1(record.get("otrsDataCNEML1"));
                customer.setOtrsDataCNXH01(record.get("otrsDataCNXH01"));
                customer.setOtrsDataCNXH03(record.get("otrsDataCNXH03"));
                customer.setCustAging("0");
                customer.setCustDeliverDate(LocalDate.now());
                customer.setCustIsDeleted(false);

                customers.add(customer);
            }

            customerRepository.saveAll(customers);
        } catch (IOException e) {
            throw new BusinessValidationException("Failed to parse CSV file");
        }
    }

    public byte[] downloadCustomersToCsv(PageRequestDTO pageRequest) {
        String username = currentUserUtils.getCurrentUsername();
        boolean isAdmin = currentUserUtils.hasRole("ROLE_ADMIN");
        Pageable pageable = PageUtil.createPageable(pageRequest);

        List<Customer> customers = isAdmin ? customerRepository.findAll() :
                customerRepository.findAllWithSearchAndDateRange(
                        username,
                        false,
                        pageRequest.getSearch(),
                        pageRequest.getDateFrom(),
                        pageRequest.getDateTo(),
                        pageable
                ).getContent();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(out);

        // Header
        writer.println("NasabahId,SEQ Number,NasabahNama,NasabahCIF,NasabahAccount,StatusName,Operator,NasabahDeliveryDate");

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (Customer c : customers) {
            writer.printf("%s,%s,%s,%s,%s,%s,%s,%s%n",
                    c.getCustId(),
                    c.getCustSeqNumber(),
                    c.getPrsnNama(),
                    c.getCustCifNumber(),
                    c.getCustNoRek(),
                    c.getCustStatus(),
                    c.getCustCreateBy(),
                    c.getCustDeliverDate() != null ? c.getCustDeliverDate().format(dateFormatter) : ""
            );
        }

        writer.flush();
        return out.toByteArray();
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