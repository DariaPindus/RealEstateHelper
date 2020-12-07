package com.daria.learn.rentalhelper.bot.config;

import com.daria.learn.rentalhelper.bot.RentalNotifierBot;
import com.daria.learn.rentalhelper.bot.handle.BotHandlerFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

@Configuration
public class BotConfiguration {

    @Bean
    RentalNotifierBot rentalNotifierBot(@Value("${tgbot.username}")String botUsername,
                                        @Value("${tgbot.token}")String botToken,
                                        @Autowired BotHandlerFacade botHandlerFacade) {
        return new RentalNotifierBot(botUsername, botToken, botHandlerFacade);
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
