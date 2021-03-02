package com.daria.learn.rentalhelper;

import com.daria.learn.rentalhelper.common.ApplicationProfiles;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@TestPropertySource(locations="classpath:test.properties")
@SpringBootTest
@ActiveProfiles({ApplicationProfiles.TEST_PROFILE, ApplicationProfiles.WITH_MOCK_TG_BOT})
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RentalNotifierTest {
}
