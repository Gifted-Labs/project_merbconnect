package com.merbsconnect.util;

import com.merbsconnect.academics.exception.ResourceNotFoundException;
import com.merbsconnect.authentication.domain.TokenType;
import com.merbsconnect.authentication.domain.User;
import com.merbsconnect.authentication.domain.VerificationToken;
import com.merbsconnect.authentication.repository.UserRepository;
import com.merbsconnect.authentication.service.TokenService;
import com.merbsconnect.email.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenResendFactory {

    private final EmailService emailService;
    private final TokenService tokenService;
    private final UserRepository userRepository;

    public void resendToken(String email, TokenType tokenType) throws BadRequestException {
        User user = getUserByEmail(email);

        switch (tokenType) {
            case VERIFICATION:
                resendVerificationToken(user);
                break;
            case PASSWORD_RESET:
                resendPasswordResetToken(user);
                break;
            default:
                throw new BadRequestException("Unsupported token type: " + tokenType);
        }
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    private void resendVerificationToken(User user) throws BadRequestException {
        if (user.isEnabled()) {
            throw new BadRequestException("Account is already verified");
        }
        VerificationToken token = tokenService.generateVerificationToken(user);
        emailService.sendVerificationEmail(user, token.getToken());
    }

    private void resendPasswordResetToken(User user) {
        VerificationToken token = tokenService.generatePasswordResetToken(user);
        emailService.sendPasswordResetEmail(user, token);
    }
}
