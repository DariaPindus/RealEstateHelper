package com.daria.learn.rentalhelper.bot;

import com.daria.learn.rentalhelper.bot.domain.BotOutgoingMessage;
import com.daria.learn.rentalhelper.bot.handlers.BotHandlerFacade;
import com.daria.learn.rentalhelper.common.ApplicationProfiles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import javax.annotation.PostConstruct;
import java.util.List;

public class TelegramRentalNotifierBot extends TelegramLongPollingBot implements RentalNotifierBot {

    private static final Logger log = LoggerFactory.getLogger(TelegramRentalNotifierBot.class);

    private final String botUsername;
    private final String botToken;
    private final Environment environment;

    private final BotHandlerFacade botHandlerFacade;

    public TelegramRentalNotifierBot(String botUsername, String botToken, BotHandlerFacade botHandlerFacade, Environment environment) {
        this.botUsername = botUsername;
        this.botToken = botToken;
        this.botHandlerFacade = botHandlerFacade;
        this.environment = environment;
    }

    @PostConstruct
    public void initBot() {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(this);
        } catch (TelegramApiException e) {
            log.error("Error initializing Telegram Bot {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        log.info("TelegramBot onUpdateReceived {}", update);
        SendMessage message = botHandlerFacade.handleMessageUpdate(update);
        sendMessage(message);
    }

    public void sendMessagesToUsers(List<BotOutgoingMessage> messageList) {
         messageList.stream()
                 .map(BotOutgoingMessage::toTelegramMessage)
                 .forEach(this::sendMessage);
    }

    private void sendMessage(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Error sending message " + e.getMessage());
        }
    }

    @Override
    public void clearWebhook() throws TelegramApiRequestException {
        if (!environment.acceptsProfiles(Profiles.of(ApplicationProfiles.TEST_PROFILE))) {
            super.clearWebhook();
        }
    }
}
