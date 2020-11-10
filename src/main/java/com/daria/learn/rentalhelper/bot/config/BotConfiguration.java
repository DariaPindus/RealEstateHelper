package com.daria.learn.rentalhelper.bot.config;

import com.daria.learn.rentalhelper.bot.BotHandlerFacade;
import com.daria.learn.rentalhelper.bot.RentalNotifierBot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.bots.DefaultBotOptions;

@Configuration
public class BotConfiguration {

    @Value("${tgbot.proxyHost}")
    private String proxyHost;
    @Value("${tgbot.proxyPort}")
    private String proxyPort;
    @Value("${tgbot.proxyType}")
    private String proxyType;
    @Bean
    RentalNotifierBot rentalNotifierBot(@Value("${tgbot.username}")String botUsername,
                                        @Value("${tgbot.token}")String botToken,
                                        @Value("${tgbot.webHookPath}") String botWebhookPath,
                                        @Autowired BotHandlerFacade botHandlerFacade) {
        DefaultBotOptions options = new DefaultBotOptions();

        options.setProxyHost(proxyHost);
        options.setProxyPort(Integer.parseInt(proxyPort));
        options.setProxyType(DefaultBotOptions.ProxyType.valueOf(proxyType));
        return new RentalNotifierBot(options, botUsername, botToken, botWebhookPath, botHandlerFacade);
    }
}
