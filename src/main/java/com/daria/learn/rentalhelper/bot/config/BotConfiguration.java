package com.daria.learn.rentalhelper.bot.config;

import com.daria.learn.rentalhelper.bot.RentalNotifierBot;
import com.daria.learn.rentalhelper.bot.handle.BotHandlerFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;

import java.util.Locale;

@Configuration
public class BotConfiguration {

    @Bean
    RentalNotifierBot rentalNotifierBot(@Value("${tgbot.username}")String botUsername,
                                        @Value("${tgbot.token}")String botToken,
                                        @Value("${tgbot.webHookPath}") String botWebhookPath,
                                        @Autowired BotHandlerFacade botHandlerFacade) {
        return new RentalNotifierBot(botUsername, botToken, botWebhookPath, botHandlerFacade);
    }

    @Bean
    public ResourceBundleMessageSource messageSource() {
        Locale.setDefault(new Locale("ru"));

        var source = new ResourceBundleMessageSource();
        source.setBasenames("messages/replies");
        source.setDefaultEncoding("UTF-8");
//        source.setUseCodeAsDefaultMessage(true);

        return source;
    }
}
