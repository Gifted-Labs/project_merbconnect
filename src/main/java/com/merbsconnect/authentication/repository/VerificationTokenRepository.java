package com.merbsconnect.authentication.repository;

import com.merbsconnect.authentication.domain.TokenType;
import com.merbsconnect.authentication.domain.User;
import com.merbsconnect.authentication.domain.VerificationToken;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

    Optional<VerificationToken> findByToken(String token);

    List<VerificationToken> findAllByUserAndTokenTypeAndVerifiedAtIsNull(
            User user,
            TokenType tokenType
    );

    List<VerificationToken> findAllByUserAndTokenTypeAndCreatedAtAfter(
            User user,
            TokenType tokenType,
            LocalDateTime createdAfter
    );

    long countByUserAndTokenTypeAndCreatedAtAfter(
            User user,
            TokenType tokenType,
            LocalDateTime createdAfter
    );

    @Modifying
    @Transactional
    int deleteByExpiresAtBeforeOrVerifiedAtIsNotNullAndCreatedAtBefore(
            LocalDateTime expiresAtBefore,
            LocalDateTime createdAtBefore
    );

}
