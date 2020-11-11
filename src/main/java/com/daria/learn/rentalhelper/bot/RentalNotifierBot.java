package com.daria.learn.rentalhelper.bot;

import com.daria.learn.rentalhelper.bot.handle.BotHandlerFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import javax.annotation.PostConstruct;
import java.util.List;

public class RentalNotifierBot extends TelegramLongPollingBot {

    private static final Logger log = LoggerFactory.getLogger(RentalNotifierBot.class);

    private final String botUsername;
    private final String botToken;

    private final BotHandlerFacade botHandlerFacade;

    public RentalNotifierBot(String botUsername, String botToken, BotHandlerFacade botHandlerFacade) {
        this.botUsername = botUsername;
        this.botToken = botToken;
        this.botHandlerFacade = botHandlerFacade;
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
        SendMessage message = botHandlerFacade.handleMessageUpdate(update);
        sendMessage(message);
    }

    @Override
    public void onUpdatesReceived(List<Update> updates) {
        log.info("TelegramBot onUpdatesReceived {}", updates);
        for (Update update : updates) {
            onUpdateReceived(update);
        }
    }

    public void sendMessagesToUsers(List<SendMessage> messageList) {
        for (SendMessage message : messageList) {
            sendMessage(message);
        }
    }

    private void sendMessage(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Error sending message " + e.getMessage());
        }
    }
}
