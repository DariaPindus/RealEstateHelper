package com.daria.learn.rentalhelper.bot.mock;

import com.daria.learn.rentalhelper.bot.config.BotApplicationProfiles;
import com.daria.learn.rentalhelper.bot.notification.RentalNotifierBot;
import com.daria.learn.rentalhelper.bot.domain.BotOutgoingMessage;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

@Profile(BotApplicationProfiles.WITH_MOCK_TG_BOT)
@Component
public class TestRentalNotifierBot implements RentalNotifierBot {

    @Override
    public void sendMessagesToUsers(List<BotOutgoingMessage> messageList) {
        // intentionally left blank
    }
}
