package com.merbsconnect.authentication.service.impl;


import com.merbsconnect.authentication.domain.TokenType;
import com.merbsconnect.authentication.domain.User;
import com.merbsconnect.authentication.domain.VerificationToken;
import com.merbsconnect.authentication.repository.UserRepository;
import com.merbsconnect.authentication.repository.VerificationTokenRepository;
import com.merbsconnect.authentication.service.RateLimitService;
import com.merbsconnect.authentication.service.TokenService;
import com.merbsconnect.email.service.EmailService;
import com.merbsconnect.util.TokenGenerator;
import com.merbsconnect.util.TokenValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenServiceImpl implements TokenService {

    private final VerificationTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final TokenGenerator tokenGenerator;
    private final TokenValidator tokenValidator;
    private final RateLimitService rateLimitService;
    private final EmailService emailService;

    @Override
    @Transactional
    public VerificationToken generateVerificationToken(User user) {
        cleanupExistingTokens(user, TokenType.VERIFICATION);
        return tokenGenerator.generate(user, TokenType.VERIFICATION);
    }

    @Override
    @Transactional
    public VerificationToken generatePasswordResetToken(User user) {
        cleanupExistingTokens(user, TokenType.PASSWORD_RESET);
        return tokenGenerator.generate(user, TokenType.PASSWORD_RESET);

    }

    @Override
    public VerificationToken validateToken(String tokenValue, TokenType expectedType) {
        return tokenValidator.validate(tokenValue, expectedType);
    }

    @Override
    public void resendVerificationToken(String email) throws BadRequestException {
        User user = findUserByEmail(email);
        validateUserNotVerified(user);
        rateLimitService.checkRateLimit(user, TokenType.VERIFICATION);

        VerificationToken token = generateVerificationToken(user);
        emailService.sendVerificationEmail(user, token.getToken());

        log.info("Verification token resent to user: {}", email);
    }

    private void cleanupExistingTokens(User user, TokenType tokenType) {
        List<VerificationToken> existingTokens = tokenRepository
                .findAllByUserAndTokenTypeAndVerifiedAtIsNull(user, tokenType);

        if  (!existingTokens.isEmpty()){
            tokenRepository.deleteAll(existingTokens);
            log.debug("Cleaned up {} existing {} tokens for user: {}",
                    existingTokens.size(), tokenType, user.getEmail());
        }
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }

    private void validateUserNotVerified(User user) throws BadRequestException {
        if (user.isEnabled()) {
            throw new BadRequestException("Account is already verified");
        }
    }

}
