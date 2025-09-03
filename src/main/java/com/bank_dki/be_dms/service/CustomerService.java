package com.bank_dki.be_dms.service;

import com.bank_dki.be_dms.CustomerStatus;
import com.bank_dki.be_dms.dto.PageRequestDTO;
import com.bank_dki.be_dms.dto.PageResponseDTO;
import com.bank_dki.be_dms.dto.CustomerDTO;
import com.bank_dki.be_dms.entity.Customer;
import com.bank_dki.be_dms.entity.Nomor;
import com.bank_dki.be_dms.entity.Task;
import com.bank_dki.be_dms.entity.User;
import com.bank_dki.be_dms.exception.BusinessValidationException;
import com.bank_dki.be_dms.exception.ResourceNotFoundException;
import com.bank_dki.be_dms.repository.CustomerRepository;
import com.bank_dki.be_dms.repository.NomorRepository;
import com.bank_dki.be_dms.repository.TaskRepository;
import com.bank_dki.be_dms.repository.UserRepository;
import com.bank_dki.be_dms.util.CurrentUserUtils;
import com.bank_dki.be_dms.util.PageUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
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
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final NomorRepository nomorRepository;
    private final UserRepository userRepository;
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

    public CustomerDTO getCustomerByCustNoRek(String custNoRek) {
        Customer customer = customerRepository.findByCustNoRek(custNoRek)
                .orElseThrow(() -> new ResourceNotFoundException("Customer with account number " + custNoRek + " not found."));

        return convertToDTO(customer);
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
        String CUST_STATUS_DELIVER = "Deliver";

        try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            if (file.isEmpty()) {
                throw new BusinessValidationException("File is empty");
            }

            CSVParser parser = CSVFormat.DEFAULT
                    .withFirstRecordAsHeader()
                    .parse(reader);

            // Ambil semua header dari file
            Map<String, Integer> headerMap = parser.getHeaderMap();

            // Daftar header yang wajib ada
            List<String> requiredHeaders = List.of("Nama", "CIF Number", "Account Number");

            // Cari header yang hilang
            List<String> missingHeaders = requiredHeaders.stream()
                    .filter(h -> !headerMap.containsKey(h))
                    .toList();

            if (!missingHeaders.isEmpty()) {
                throw new BusinessValidationException("Missing required columns: " + missingHeaders);
            }

            List<Customer> customers = new ArrayList<>();
            for (CSVRecord record : parser) {
                Customer customer = new Customer();

                customer.setPrsnNama(record.get("Nama"));
                customer.setCustCifNumber(record.get("CIF Number"));
                customer.setCustNoRek(record.get("Account Number"));
                customer.setCustCreateBy(currentUploaderUsername);
                customer.setCustStatus(CUST_STATUS_DELIVER);
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

        try(ByteArrayOutputStream out = new ByteArrayOutputStream();) {
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
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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

    public void registerCustomer(Short id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new BusinessValidationException("Customer not found"));

        String custSeqNumber = handlingSequenceNumber(customer);

        customer.setCustSeqNumber(custSeqNumber);
        customer.setCustStatus(CustomerStatus.REGISTER.getLabel());
        customer.setCustUpdateBy(currentUserUtils.getCurrentUsername());
        customerRepository.save(customer);
    }

    public void updateCustomerStatusPengkinian(Short id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new BusinessValidationException("Customer not found"));

        customer.setCustStatus(CustomerStatus.PENGKINIAN.getLabel());
        customer.setCustUpdateBy(currentUserUtils.getCurrentUsername());
        customerRepository.save(customer);
    }

    public void updateCustomerStatusPengkaitan(Short id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new BusinessValidationException("Customer not found"));

        customer.setCustStatus(CustomerStatus.PENGKAITAN.getLabel());
        customer.setCustUpdateBy(currentUserUtils.getCurrentUsername());
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
        dto.setCustFilePath(customer.getCustFilePath());
        dto.setCustFileName(customer.getCustFileName());
        
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

    private String handlingSequenceNumber(Customer customer) {
        short last = nomorRepository.findMaxNomorLast();
        short nextNumber = (short) (last + 1);

        Nomor nomor = new Nomor();
        nomor.setNomorLast(nextNumber);
        nomorRepository.save(nomor);

        LocalDate deliverDate = customer.getCustDeliverDate();
        String formattedSeqDeliverDate = deliverDate.format(DateTimeFormatter.ofPattern("yyyy/MM"));
        String formattedSeqNumber = String.format("%06d", nextNumber);
        User currentUser = userRepository.findByUserName(currentUserUtils.getCurrentUsername()).orElse(null);
        String currentJobCode = currentUser != null ? currentUser.getUserJobCode() : "WI000";
        return  currentJobCode + "/" + formattedSeqDeliverDate + "/" + formattedSeqNumber;
    }
}