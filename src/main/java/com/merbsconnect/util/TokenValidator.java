package com.merbsconnect.util;

import com.merbsconnect.authentication.domain.TokenType;
import com.merbsconnect.authentication.domain.VerificationToken;
import com.merbsconnect.authentication.repository.VerificationTokenRepository;
import com.merbsconnect.exception.InvalidTokenException;
import com.merbsconnect.exception.TokenExpiredException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class TokenValidator {

    private final VerificationTokenRepository tokenRepository;

    public VerificationToken validate(String tokenValue, TokenType expectedType) {
        VerificationToken token = findToken(tokenValue);
        validateTokenType(token, expectedType);
        validateNotExpired(token);
        validateNotUsed(token);

        return token;
    }


    private VerificationToken findToken(String tokenValue) {
        return tokenRepository.findByToken(tokenValue)
                .orElseThrow(() -> new InvalidTokenException("Invalid token"));
    }

    private void validateTokenType(VerificationToken token, TokenType expectedType) {
        if (token.getTokenType() != expectedType){
            throw new InvalidTokenException("Token is not valid for this operation");
        }
    }

    private void validateNotExpired(VerificationToken token) {
        if (token.isExpired()){
            throw new TokenExpiredException("Token has expired");
        }
    }

    private void validateNotUsed(VerificationToken token) {
        if (token.isUsed()) {
            throw new InvalidTokenException("Token has already used");
        }
    }
}
