package com.bookbookclub.bbc_user_service.emailverification.service;

import com.bookbookclub.bbc_user_service.emailverification.entity.EmailVerification;
import com.bookbookclub.bbc_user_service.emailverification.exception.EmailVerificationException;
import com.bookbookclub.bbc_user_service.emailverification.repository.EmailVerificationRepository;
import com.bookbookclub.bbc_user_service.user.config.EmailVerificationProperties;
import com.bookbookclub.bbc_user_service.user.exception.UserErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.*;

class EmailVerificationServiceTest {

    private RedisTemplate<String, String> redisTemplate;
    private JavaMailSender mailSender;
    private EmailVerificationRepository repository;
    private EmailVerificationProperties properties;
    private EmailVerificationService service;

    ValueOperations<String, String> valueOperations;

    @BeforeEach
    void setUp() {
        redisTemplate = mock(RedisTemplate.class);
        valueOperations = mock(ValueOperations.class); // ⭐ 추가
        given(redisTemplate.opsForValue()).willReturn(valueOperations); // ⭐ 추가

        mailSender = mock(JavaMailSender.class);
        repository = mock(EmailVerificationRepository.class);
        properties = mock(EmailVerificationProperties.class);

        given(properties.getTokenExpireMinutes()).willReturn(15L);
        given(properties.getFailExpireMinutes()).willReturn(10L);
        given(properties.getMaxFailCount()).willReturn(5);

        service = new EmailVerificationService(redisTemplate, mailSender, repository, properties);
    }
    @Test
    void 인증메일_정상발송() {
        // given
        String email = "test@email.com";

        // when
        service.sendVerificationEmail(email);

        // then
        verify(redisTemplate).opsForValue();
        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    void 인증성공시_DB저장_성공() {
        // given
        String token = "token123";
        String email = "test@email.com";
        String redisKey = "email:verify:" + token;

        given(redisTemplate.opsForValue().get(redisKey)).willReturn(email);
        given(repository.findByEmail(email)).willReturn(Optional.empty());

        // when
        boolean result = service.verifyEmail(token);

        // then
        assertThat(result).isTrue();
        verify(repository).save(any(EmailVerification.class));
        verify(redisTemplate).delete(redisKey);
    }

    @Test
    void 인증토큰_없을경우_예외() {
        // given
        String token = "invalidToken";
        given(redisTemplate.opsForValue().get("email:verify:" + token)).willReturn(null);

        // expect
        assertThatThrownBy(() -> service.verifyEmail(token))
                .isInstanceOf(EmailVerificationException.class)
                .hasMessageContaining(UserErrorCode.EMAIL_VERIFICATION_INVALID_TOKEN.getMessage());
    }

    @Test
    void 인증실패_5회초과시_예외() {
        // given
        String token = "token123";
        String email = "test@email.com";

        given(redisTemplate.opsForValue().get("email:verify:" + token)).willReturn(email);
        given(redisTemplate.opsForValue().increment("email:fail:" + email)).willReturn(5L);

        // expect
        assertThatThrownBy(() -> service.verifyEmail(token))
                .isInstanceOf(EmailVerificationException.class)
                .hasMessageContaining(UserErrorCode.EMAIL_VERIFICATION_LIMIT_EXCEEDED.getMessage());
    }

    @Test
    void 인증상태_확인_성공() {
        // given
        String email = "check@naver.com";
        given(repository.existsByEmailAndVerifiedIsTrue(email)).willReturn(true);

        // when
        boolean verified = service.isEmailVerified(email);

        // then
        assertThat(verified).isTrue();
    }
}
