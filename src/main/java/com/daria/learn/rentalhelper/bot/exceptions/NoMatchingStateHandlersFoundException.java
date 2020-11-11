package com.daria.learn.rentalhelper.bot.exceptions;

public class NoMatchingStateHandlersFoundException extends BotException {
    private static final long serialVersionUID = -3118041684107064195L;

    @Override
    public String getExceptionMessageSource() {
        return "bot.exception.no-message-handler.reply";
    }
}
