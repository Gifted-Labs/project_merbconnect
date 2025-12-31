package com.merbsconnect.authentication.service;


import com.merbsconnect.authentication.domain.TokenType;
import com.merbsconnect.authentication.domain.User;
import com.merbsconnect.authentication.domain.VerificationToken;
import org.apache.coyote.BadRequestException;

public interface TokenService {

    VerificationToken generateVerificationToken(User user);

    VerificationToken generatePasswordResetToken(User user);

    VerificationToken validateToken(String tokenValue, TokenType expectedType);

}
