package com.daria.learn.rentalhelper.bot.exceptions;

public class ExceededMaximumSubscriptionsAmountException extends BotException {

    private static final long serialVersionUID = -1043384684777124312L;

    @Override
    public String getExceptionMessageSource() {
        return "bot.exception.max-subscription-amount.reply";
    }
}
