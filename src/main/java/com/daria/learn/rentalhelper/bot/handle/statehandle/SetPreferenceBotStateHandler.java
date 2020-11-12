package com.daria.learn.rentalhelper.bot.handle.statehandle;

import com.daria.learn.rentalhelper.bot.exceptions.CannotParseUserPreferenceResponseException;
import com.daria.learn.rentalhelper.bot.handle.BotStateEnum;
import com.daria.learn.rentalhelper.bot.model.UserPreference;
import com.daria.learn.rentalhelper.bot.persistence.UserCache;
import org.springframework.context.MessageSource;
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

    private final UserCache userCache;
    private final MessageSource messageSource;

    public SetPreferenceBotStateHandler(UserCache userCache, MessageSource messageSource) {
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

        String messageText =  messageSource.getMessage("bot.saved-preferences.reply", null, Locale.getDefault());
        if (wasCancelled(userResponse)) {
            messageText = messageSource.getMessage("bot.cancelled.reply", null, Locale.getDefault());
        } else if (shouldBeCleared(userResponse)) {
            messageText = messageSource.getMessage("bot.cleared-preferences.reply", null, Locale.getDefault());
            userCache.setUserPreferenceFromMessage(message, null);
            userCache.setUserStateFromMessage(message, BotStateEnum.SUBSCRIBED);
        } else {
            UserPreference userPreference = tryParseUserPreference(userResponse);
            userCache.setUserPreferenceFromMessage(message, userPreference);
            userCache.setUserStateFromMessage(message, BotStateEnum.SUBSCRIBED);
        }

        SendMessage responseMessage = new SendMessage();
        responseMessage.setChatId(message.getChatId().toString());
        responseMessage.setText(messageText);
        return responseMessage;
    }

    private boolean wasCancelled(String userResponse) {
        return userResponse.toLowerCase().trim().equals("отменить");
    }

    private boolean shouldBeCleared(String userResponse) {
        return userResponse.toLowerCase().trim().equals("очистить");
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
        if (!preference.equals("+") && !preference.equals("-"))
            throw new CannotParseUserPreferenceResponseException();
        return preference.equals("+");
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
