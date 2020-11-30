package com.daria.learn.rentalhelper.bot;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class BotMessageSource {

    private final MessageSource messageSource;
    private final List<Locale> supportedLocales;

    public BotMessageSource(MessageSource messageSource, @Value("${tgbot.supportedLanguages}") String supportedLanguagesStr) {
        this.messageSource = messageSource;
        this.supportedLocales = Arrays.stream(Optional.ofNullable(supportedLanguagesStr).map(s -> s.split(",")).orElse(new String[]{Locale.UK.getLanguage()}))
                .map(Locale::new).collect(Collectors.toList());
    }

    public List<Locale> getSupportedLocales() {
        return supportedLocales;
    }

    public String getMessage(String messageKey, Object[] args, Locale userLocale) {
        return messageSource.getMessage(messageKey, args, userLocale);
    }

    public boolean isSameMessage(String userMessage, String messageKey) {
        String formattedMessage = userMessage.trim().toLowerCase();
        return supportedLocales.stream().anyMatch(locale -> messageSource.getMessage(messageKey, null, locale).equals(formattedMessage));
    }
}
