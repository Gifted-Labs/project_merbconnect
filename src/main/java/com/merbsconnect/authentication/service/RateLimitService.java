package com.merbsconnect.authentication.service;

import com.merbsconnect.authentication.domain.TokenType;
import com.merbsconnect.authentication.domain.User;

public interface RateLimitService {

    void checkRateLimit(User user, TokenType tokenType);


}
