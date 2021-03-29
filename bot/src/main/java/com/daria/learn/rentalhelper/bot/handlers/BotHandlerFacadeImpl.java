package com.daria.learn.rentalhelper.bot.handlers;

import com.daria.learn.rentalhelper.bot.exceptions.BotException;
import com.daria.learn.rentalhelper.bot.handlers.statehandlers.ExceptionalBotStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class BotHandlerFacadeImpl implements  BotHandlerFacade {

    private final static Logger log = LoggerFactory.getLogger(BotHandlerFacadeImpl.class);

    private final ExceptionalBotStateHandler exceptionalBotStateHandler;
    private final BotReplier botReplier;

    public BotHandlerFacadeImpl(ExceptionalBotStateHandler exceptionalBotStateHandler, BotReplier botReplier) {
        this.botReplier = botReplier;
        this.exceptionalBotStateHandler = exceptionalBotStateHandler;
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
        try {
            return botReplier.replyToMessage(message);
        } catch (BotException botException) {
            return exceptionalBotStateHandler.handleExceptionalResponse(message, botException);
        }
    }

    private SendMessage processCallbackQuery(CallbackQuery callbackQuery) {
        //TODO implement
        throw new UnsupportedOperationException();
    }
}
