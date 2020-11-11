package com.daria.learn.rentalhelper.bot.handle;

import com.daria.learn.rentalhelper.bot.handle.statehandle.DefaultBotStateHandler;
import com.daria.learn.rentalhelper.bot.handle.statehandle.UserBotStateHandler;
import com.daria.learn.rentalhelper.bot.model.OfferMessage;
import com.daria.learn.rentalhelper.bot.persistence.UserCache;
import com.daria.learn.rentalhelper.rentals.domain.RentalOfferDTO;
import org.apache.commons.lang3.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class BotHandlerFacadeImpl implements  BotHandlerFacade {

    private final static Logger log = LoggerFactory.getLogger(BotHandlerFacadeImpl.class);

    private final UserCache userCache;
    private final Set<UserBotStateHandler> stateHandlers;
    private final DefaultBotStateHandler defaultBotStateHandler;

    public BotHandlerFacadeImpl(UserCache userCache, Set<UserBotStateHandler> stateHandlers, DefaultBotStateHandler defaultBotStateHandler) {
        this.userCache = userCache;
        this.stateHandlers = stateHandlers;
        this.defaultBotStateHandler = defaultBotStateHandler;
    }

    @Override
    public SendMessage handleMessageUpdate(Update update) {
        SendMessage replyMessage = null;


//        if (update.hasCallbackQuery()) {
//            log.info("New callbackQuery from User: {} with data: {}", update.getCallbackQuery().getFrom().getUserName(),
//                    update.getCallbackQuery().getData());
//            return processCallbackQuery(update.getCallbackQuery());
//        }


        Message message = update.getMessage();
        if (message != null && message.hasText()) {
            log.info("New message from User:{}, chatId: {},  with text: {}",
                    message.getFrom().getUserName(), message.getChatId(), message.getText());
            replyMessage = handleInputMessage(message);
        }

        return replyMessage;
    }

    private SendMessage handleInputMessage(Message message) {
        UserBotStateHandler botStateHandler = stateHandlers.stream().filter(stateHandler -> stateHandler.isApplicable(message.getText())).findFirst().orElse(defaultBotStateHandler);
        return botStateHandler.handleResponse(message);
    }

    private SendMessage processCallbackQuery(CallbackQuery callbackQuery) {
        //TODO implement
        throw new NotImplementedException();
    }
}
