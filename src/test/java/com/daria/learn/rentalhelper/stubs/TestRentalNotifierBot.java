package com.daria.learn.rentalhelper.stubs;

import com.daria.learn.rentalhelper.bot.RentalNotifierBot;
import com.daria.learn.rentalhelper.bot.domain.BotOutgoingMessage;
import com.daria.learn.rentalhelper.common.ApplicationProfiles;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

@Profile(ApplicationProfiles.WITH_MOCK_TG_BOT)
@Component
public class TestRentalNotifierBot implements RentalNotifierBot {

    @Override
    public void sendMessagesToUsers(List<BotOutgoingMessage> messageList) {
        // intentionally left blank
    }
}
