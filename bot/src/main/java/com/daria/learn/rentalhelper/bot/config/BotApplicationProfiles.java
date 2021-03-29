package com.daria.learn.rentalhelper.bot.config;

public interface BotApplicationProfiles {

    String WITH_MOCK_TG_BOT = "with_mock_bot";
    String WITH_REAL_TG_BOT = "!with_mock_bot";

    String LOCAL_USER_REPO = "local-user-repo";
    String NON_LOCAL_USER_REPO = "!local-user-repo";

    String TEST_PROFILE = "test";
    String NOT_TEST_PROFILE = "!test";
}
