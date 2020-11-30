package com.daria.learn.rentalhelper;

import com.daria.learn.rentalhelper.bot.BotMessageSource;
import com.daria.learn.rentalhelper.bot.handle.BotHandlerFacadeImpl;
import com.daria.learn.rentalhelper.bot.handle.BotStateEnum;
import com.daria.learn.rentalhelper.bot.model.UserPreference;
import com.daria.learn.rentalhelper.bot.persistence.UserCache;
import com.daria.learn.rentalhelper.common.ApplicationProfiles;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.Locale;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@SpringBootTest
@TestPropertySource(locations="classpath:test.properties")
@AutoConfigureMockMvc
@ActiveProfiles({ApplicationProfiles.TEST_PROFILE})
public class RentalNotifierBotIT {

    @Autowired
    BotHandlerFacadeImpl botHandlerFacade;

    @Autowired
    BotMessageSource botMessageSource;

    @Autowired
    UserCache<Long> userCache;

    /**
     * 1. user starts conversation - language selection is sent
     * 2. user start conversation incorrectly - ?
     * 3. user answer incorrectly after /start - ask for retry is shown
     * 4. user sets his language - it is saved in his cache, message about next step (start or set up is shown)
     * 5. user enters something wrong - message about it is shown, no info is updated
     * 6. user chooses "start" - he is subscribed
     * 7. user then chooses "set up" - message about setup is chosen
     * 8. user enters something wrong - message about it is shown, no info is updated
     * 9. user enters some info - it is updated correctly in his profile
     * 10. user enters "set up" again, enters some unparsable info - message about retry is shown, nothing is updated
     * 11. users enters "clear" - his info is cleared, he is still subscribed
     */
    @Test
    public void testStartCommunication(){
        Long chatId = new Random().nextLong();
        User user = new User(new Random().nextInt(), "Tester", false);

        assertTrue(userCache.getSubscribedUserInfos().isEmpty());
        assertTrue(userCache.getUserPreference(chatId).isEmpty());
        Update initUpdate = getUpdate(user, chatId, "/start");
        SendMessage initReplyMessage = botHandlerFacade.handleMessageUpdate(initUpdate);
        String initReply = initReplyMessage.getText();
        assertEquals(botMessageSource.getMessage("bot.inited.reply", null, Locale.getDefault()), initReply);
        assertEquals(BotStateEnum.SELECT_LANG, userCache.getUserState(chatId).get());

        String userLocaleName = "EN";
        Locale userLocale = new Locale(userLocaleName);
        Update chooseLangMessage = getUpdate(user, chatId, userLocaleName);
        String chooseLangResponse = botHandlerFacade.handleMessageUpdate(chooseLangMessage).getText();
//        assertEquals(botMessageSource.getMessage("bot.lang-selected.reply", new String[]{userLocaleName}, userLocale), chooseLangResponse);
        assertEquals(userLocale, userCache.getUserLocale(chatId));
        assertEquals(BotStateEnum.STARTED, userCache.getUserState(chatId).get());

        Update wrongMessage = getUpdate(user, chatId, "bla bla bla");
        String randomMessageResponse = botHandlerFacade.handleMessageUpdate(wrongMessage).getText();
        assertEquals(botMessageSource.getMessage("bot.exception.no-message-handler.reply", null, userLocale), randomMessageResponse);
        assertEquals(BotStateEnum.STARTED, userCache.getUserState(chatId).get());

        Update startMessage = getUpdate(user, chatId, "START ");
        String startResponse = botHandlerFacade.handleMessageUpdate(startMessage).getText();
        assertEquals(botMessageSource.getMessage("bot.subscribed.reply", null, userLocale), startResponse);
        assertEquals(BotStateEnum.SUBSCRIBED, userCache.getUserState(chatId).get());
        assertTrue(userCache.getUserPreference(chatId).isEmpty());

        Update setUpMessage = getUpdate(user, chatId, "set up");
        String setupResponse = botHandlerFacade.handleMessageUpdate(setUpMessage).getText();
        assertEquals(botMessageSource.getMessage("bot.set-preferences.reply", null, userLocale), setupResponse);
        assertEquals(BotStateEnum.SET_PREFERENCES, userCache.getUserState(chatId).get());

        Update wrongSetupUpdate = getUpdate(user, chatId, "1. hello; 5.e83924");
        String wrongSetupResponse = botHandlerFacade.handleMessageUpdate(wrongSetupUpdate).getText();
        assertEquals(botMessageSource.getMessage("bot.wrong-input.reply", null, userLocale), wrongSetupResponse);
        assertEquals(BotStateEnum.SET_PREFERENCES, userCache.getUserState(chatId).get());

        Update correctSetupUpdate = getUpdate(user, chatId, "2. 60;1.1200");
        String correctSetupResponse = botHandlerFacade.handleMessageUpdate(correctSetupUpdate).getText();
        UserPreference userPreference = userCache.getUserPreference(chatId).get();
        assertEquals(botMessageSource.getMessage("bot.saved-preferences.reply", null, userLocale), correctSetupResponse);
        assertEquals(1200.0, userPreference.getMaxPrice(), 0.001);
        assertEquals(60, (int)userPreference.getMinArea());
        assertEquals(BotStateEnum.SUBSCRIBED, userCache.getUserState(chatId).get());


        Update cleanPreferencesUpdate = getUpdate(user, chatId, "clean");
        String cleanPreferencesResponse = botHandlerFacade.handleMessageUpdate(cleanPreferencesUpdate).getText();
        assertEquals(botMessageSource.getMessage("bot.cleared-preferences.reply", null, userLocale), cleanPreferencesResponse);
        assertTrue(userCache.getUserPreference(chatId).isEmpty());
        assertEquals(BotStateEnum.SUBSCRIBED, userCache.getUserState(chatId).get());
    }


    private Update getUpdate(User user, Long chatId, String text) {
        Update update = new Update();
        update.setMessage(getMessage(user, chatId, text));
        return update;
    }

    private Message getMessage(User user, Long chatId, String text) {
        String chatType = "private";
        Message message = new Message();
        message.setFrom(user);
        message.setChat(new Chat(chatId, chatType));
        message.setText(text);
        return message;
    }
}
