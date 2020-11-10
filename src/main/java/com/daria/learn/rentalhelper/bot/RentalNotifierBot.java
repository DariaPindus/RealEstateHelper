package com.daria.learn.rentalhelper.bot;

import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

public class RentalNotifierBot extends TelegramWebhookBot {

    private final String botUsername;
    private final String botToken;
    private final String botPath;

    private final BotHandlerFacade botHandlerFacade;

    public RentalNotifierBot(String botUsername, String botToken, String botPath, BotHandlerFacade botHandlerFacade) {
        this.botUsername = botUsername;
        this.botToken = botToken;
        this.botPath = botPath;
        this.botHandlerFacade = botHandlerFacade;
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
    public String getBotPath() {
        return botPath;
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        return botHandlerFacade.handleWebhookUpdate(update);
    }

}
