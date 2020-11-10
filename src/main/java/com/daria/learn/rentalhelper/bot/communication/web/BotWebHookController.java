package com.daria.learn.rentalhelper.bot.communication.web;

import com.daria.learn.rentalhelper.bot.RentalNotifierBot;
import org.springframework.web.bind.annotation.*;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

@RestController
public class BotWebHookController {

    private final RentalNotifierBot notifierBot;

    public BotWebHookController(RentalNotifierBot notifierBot) {
        this.notifierBot = notifierBot;
    }

    @PostMapping(value = "/")
    public BotApiMethod<?> onUpdateReceived(@RequestBody Update update) {
        return notifierBot.onWebhookUpdateReceived(update);
    }

    @PostMapping
    public BotApiMethod<?> onUpdateReceived2(@RequestBody Update update) {
        return notifierBot.onWebhookUpdateReceived(update);
    }
}
