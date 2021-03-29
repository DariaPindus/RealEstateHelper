package com.daria.learn.rentalhelper.bot.exceptions;

public abstract class BotException extends RuntimeException {
    public abstract String getExceptionMessageSource();
}
