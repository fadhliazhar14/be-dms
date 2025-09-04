package com.bank_dki.be_dms.service;

import com.bank_dki.be_dms.dto.LogCreateDTO;
import com.bank_dki.be_dms.entity.Log;
import com.bank_dki.be_dms.entity.User;
import com.bank_dki.be_dms.repository.LogRepository;
import com.bank_dki.be_dms.repository.UserRepository;
import com.bank_dki.be_dms.util.CurrentUserUtils;
import com.bank_dki.be_dms.util.UsernameFormatter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogService {
    private final LogRepository logRepository;
    private final CurrentUserUtils currentUserUtils;
    private final UsernameFormatter usernameFormatter;

    public void createLog(LogCreateDTO logCreateDTO) {

        Log log = new Log();
        log.setLogName(logCreateDTO.getLogName().name());
        log.setLogNote(logCreateDTO.getLogNote());
        log.setLogCreateBy(usernameFormatter.getFormattedUsername());
        log.setUserId(currentUserUtils.getCurrentUserId().shortValue());

        logRepository.save(log);
    }
}
