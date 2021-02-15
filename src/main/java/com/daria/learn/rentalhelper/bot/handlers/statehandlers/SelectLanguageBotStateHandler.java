package com.daria.learn.rentalhelper.bot.handlers.statehandlers;

import com.daria.learn.rentalhelper.bot.BotMessageSource;
import com.daria.learn.rentalhelper.bot.handlers.BotStateEnum;
import com.daria.learn.rentalhelper.bot.persistence.UserRepository;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Component
public class SelectLanguageBotStateHandler implements UserBotStateHandler {

    private final UserRepository<Long> userCache;
    private final BotMessageSource messageSource;
    private final List<String> supportedLocales;

    public SelectLanguageBotStateHandler(UserRepository<Long> userCache, BotMessageSource messageSource) {
        this.userCache = userCache;
        this.messageSource = messageSource;
        this.supportedLocales = messageSource.getSupportedLocales().stream().map(l -> l.getLanguage().toLowerCase()).collect(Collectors.toList());
    }

    @Override
    public BotStateEnum getBotStateEnum() {
        return BotStateEnum.SELECT_LANG;
    }

    @Override
    public boolean isApplicable(String userResponse) {
        String formattedResponse = userResponse.trim().toLowerCase();
        return supportedLocales.stream().anyMatch(formattedResponse::equals);
    }

    @Override
    public SendMessage replyToMessage(Message message) {
        String locale = message.getText();
        Locale userLocale = new Locale(locale);
        userCache.setUserLocale(message.getChatId(), userLocale);
        String messageText = messageSource.getMessage("bot.lang-selected.reply", new Object[]{locale}, userLocale);
        SendMessage responseMessage = new SendMessage();
        responseMessage.setChatId(message.getChatId().toString());
        responseMessage.setText(messageText);
        userCache.setUserState(message.getChatId(), BotStateEnum.STARTED);
        return responseMessage;
    }
}
