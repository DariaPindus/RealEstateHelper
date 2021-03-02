package com.daria.learn.rentalhelper.bot.handlers.statehandlers;

import com.daria.learn.rentalhelper.bot.BotMessageSource;
import com.daria.learn.rentalhelper.bot.exceptions.NoMatchingStateHandlersFoundException;
import com.daria.learn.rentalhelper.bot.handlers.BotStateEnum;
import com.daria.learn.rentalhelper.bot.persistence.UserRepository;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Locale;

@Component
public class StartedBotStateHandler implements UserBotStateHandler {

    private static final String SETUP_MESSAGE_KEY = "user.setup";
    private static final String START_MESSAGE_KEY = "user.start";

    private final UserRepository<Long> userRepository;
    private final BotMessageSource messageSource;

    public StartedBotStateHandler(UserRepository<Long> userRepository, BotMessageSource messageSource) {
        this.userRepository = userRepository;
        this.messageSource = messageSource;
    }

    @Override
    public BotStateEnum getBotStateEnum() {
        return BotStateEnum.STARTED;
    }

    @Override
    public boolean isApplicable(String userResponse) {
        return messageSource.isSameMessage(userResponse, START_MESSAGE_KEY) || messageSource.isSameMessage(userResponse, SETUP_MESSAGE_KEY) ;
    }

    @Override
    public SendMessage replyToMessage(Message message) {
        if (messageSource.isSameMessage(message.getText(), START_MESSAGE_KEY)) {
            return replyWithSubscription(message);
        } if (messageSource.isSameMessage(message.getText(), SETUP_MESSAGE_KEY)) {
            return replyWithPreferences(message);
        }
        throw new NoMatchingStateHandlersFoundException();
    }

    private SendMessage replyWithPreferences(Message message) {
        userRepository.setUserState(message.getChatId(), BotStateEnum.SET_PREFERENCES);
        String messageText = messageSource.getMessage("bot.set-preferences.reply", null, Locale.getDefault());
        SendMessage responseMessage = new SendMessage();
        responseMessage.setChatId(message.getChatId().toString());
        responseMessage.setText(messageText);
        return responseMessage;
    }

    private SendMessage replyWithSubscription(Message message) {
        userRepository.setUserState(message.getChatId(), BotStateEnum.SUBSCRIBED);
        String messageText = messageSource.getMessage("bot.subscribed.reply", null, Locale.getDefault());
        SendMessage responseMessage = new SendMessage();
        responseMessage.setChatId(message.getChatId().toString());
        responseMessage.setText(messageText);
        return responseMessage;
    }

}
