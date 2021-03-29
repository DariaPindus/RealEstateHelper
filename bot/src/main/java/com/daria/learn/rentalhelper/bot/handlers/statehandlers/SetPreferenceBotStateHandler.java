package com.daria.learn.rentalhelper.bot.handlers.statehandlers;

import com.daria.learn.rentalhelper.bot.config.BotMessageSource;
import com.daria.learn.rentalhelper.bot.exceptions.CannotParseUserPreferenceResponseException;
import com.daria.learn.rentalhelper.bot.handlers.BotStateEnum;
import com.daria.learn.rentalhelper.bot.domain.UserPreference;
import com.daria.learn.rentalhelper.bot.persistence.UserRepository;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class SetPreferenceBotStateHandler implements UserBotStateHandler {

    private final String NUMBER_REGEX =  "[0-9][0-9,\\.]+";
    private final String ANSWER_REGEX = "[1-3]\\.\\s?[0-9+-]*";

    private final UserRepository<Long> userCache;
    private final BotMessageSource messageSource;

    public SetPreferenceBotStateHandler(UserRepository<Long> userCache, BotMessageSource messageSource) {
        this.userCache = userCache;
        this.messageSource = messageSource;
    }

    @Override
    public BotStateEnum getBotStateEnum() {
        return BotStateEnum.SET_PREFERENCES;
    }

    @Override
    public boolean isApplicable(String userResponse) {
        Pattern pattern = Pattern.compile(ANSWER_REGEX, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(userResponse);
        return matcher.find() || wasCancelled(userResponse) || shouldBeCleared(userResponse);
    }

    @Override
    public SendMessage replyToMessage(Message message) {
        String userResponse = message.getText();

        Locale userLocale = userCache.getUserLocale(message.getChatId());
        String messageText =  messageSource.getMessage("bot.saved-preferences.reply", null, userLocale);
        if (wasCancelled(userResponse)) {
            messageText = messageSource.getMessage("bot.cancelled.reply", null, userLocale);
        } else if (shouldBeCleared(userResponse)) {
            messageText = messageSource.getMessage("bot.cleared-preferences.reply", null, userLocale);
            userCache.setUserPreference(message.getChatId(), null);
            userCache.setUserState(message.getChatId(), BotStateEnum.SUBSCRIBED);
        } else {
            UserPreference userPreference = tryParseUserPreference(userResponse);
            userCache.setUserPreference(message.getChatId(), userPreference);
            userCache.setUserState(message.getChatId(), BotStateEnum.SUBSCRIBED);
        }

        SendMessage responseMessage = new SendMessage();
        responseMessage.setChatId(message.getChatId().toString());
        responseMessage.setText(messageText);
        return responseMessage;
    }

    private boolean wasCancelled(String userResponse) {
        return messageSource.isSameMessage(userResponse, "user.cancel");
    }

    private boolean shouldBeCleared(String userResponse) {
        return messageSource.isSameMessage(userResponse, "user.clean");
    }

    private UserPreference tryParseUserPreference(String userResponse) {
        try {
            Double price = null;
            Integer area = null;
            Boolean furnished = null;

            String[] preferences = userResponse.split(";");

            for (String preference : preferences) {
                int preferenceIndex = Integer.parseInt(preference.substring(0, preference.indexOf(".")));
                if (preferenceIndex < 1 || preferenceIndex > 3) {
                    throw new IllegalStateException();
                }

                String preferenceValue = preference.substring(preference.indexOf(".") + 1).trim();
                if (preferenceIndex == 1)
                    price = parseNumber(preferenceValue);
                if (preferenceIndex == 2)
                    area = parseNumber(preferenceValue).intValue();
                if (preferenceIndex == 3)
                    furnished = parseFurnished(preferenceValue);
            }

            return new UserPreference(price, null, area, furnished);
        } catch (Exception ex) {
            throw new CannotParseUserPreferenceResponseException();
        }
    }

    private Boolean parseFurnished(String preference) {
        String formatted = preference.toLowerCase();
        if (!formatted.equals("+") && !formatted.equals("-") && !formatted.equals("furnished") && !formatted.equals("unfurnished"))
            throw new CannotParseUserPreferenceResponseException();
        return formatted.equals("+") || formatted.equals("furnished");
    }

    private Double parseNumber(String preferenceStr) {
        try {
            Pattern pattern = Pattern.compile(NUMBER_REGEX, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(preferenceStr);
            return matcher.find() ? NumberFormat.getNumberInstance(Locale.UK).parse(matcher.group()).doubleValue() : 0;
        } catch (Exception ex) {
            System.out.println("exception [parsePrice] " + ex.getMessage());
            return 0.0;
        }
    }
}
