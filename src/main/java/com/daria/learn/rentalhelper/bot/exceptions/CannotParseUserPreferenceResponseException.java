package com.daria.learn.rentalhelper.bot.exceptions;

public class CannotParseUserPreferenceResponseException extends BotException {
    private static final long serialVersionUID = 1705171825424456338L;

    @Override
    public String getExceptionMessageSource() {
        return "bot.wrong-input.reply";
    }
}
