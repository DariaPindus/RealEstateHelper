package com.daria.learn.rentalhelper.common;

public interface ApplicationProfiles {
    String TEST_PROFILE = "test";
    String NOT_TEST_PROFILE = "!test";

    String WITH_MOCK_TG_BOT = "with_mock_bot";
    String WITH_REAL_TG_BOT = "!with_mock_bot";

    String LOCAL_USER_REPO = "local-user-repo";
    String NON_LOCAL_USER_REPO = "!local-user-repo";
}
