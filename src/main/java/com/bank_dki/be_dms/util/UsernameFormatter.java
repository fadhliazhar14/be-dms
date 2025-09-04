package com.bank_dki.be_dms.util;

import com.bank_dki.be_dms.entity.User;
import com.bank_dki.be_dms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UsernameFormatter {
    private final UserRepository userRepository;
    private final CurrentUserUtils currentUserUtils;

    public String getFormattedUsername () {
        User currentUser = userRepository.findByUserName(currentUserUtils.getCurrentUsername()).orElse(null);
        return currentUser != null ?
                currentUser.getUserJobCode() + " - " + currentUser.getUserName() :
                "null";
    }
}
