package com.daria.learn.rentalhelper.bot.config;

import com.daria.learn.rentalhelper.bot.RentalNotifierBot;
import com.daria.learn.rentalhelper.bot.TelegramRentalNotifierBot;
import com.daria.learn.rentalhelper.bot.handlers.BotHandlerFacade;
import com.daria.learn.rentalhelper.common.ApplicationProfiles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.env.Environment;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

@Configuration
public class BotConfiguration {

    @Bean
    @Profile(ApplicationProfiles.WITH_REAL_TG_BOT)
    RentalNotifierBot rentalNotifierBot(@Value("${tgbot.username}")String botUsername,
                                        @Value("${tgbot.token}")String botToken,
                                        @Autowired BotHandlerFacade botHandlerFacade,
                                        @Autowired Environment environment) {
        return new TelegramRentalNotifierBot(botUsername, botToken, botHandlerFacade, environment);
    }

    @Bean
    public ResourceBundleMessageSource messageSource() {
        Locale.setDefault(Locale.UK);

        var source = new ResourceBundleMessageSource();
        source.setBasenames("messages/replies");
        source.setDefaultEncoding("UTF-8");
//        source.setUseCodeAsDefaultMessage(true);

        return source;
    }

    @Bean
    public List<Locale> supportedLocales(@Value("tgbot.supportedLanguages") String supportedLanguagesStr) {
        return Arrays.stream(Optional.ofNullable(supportedLanguagesStr).map(s -> s.split(",")).orElse(new String[]{Locale.UK.getLanguage()}))
                .map(Locale::new).collect(Collectors.toList());
    }
}
