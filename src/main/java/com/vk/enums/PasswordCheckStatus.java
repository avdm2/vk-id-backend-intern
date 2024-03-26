package com.vk.enums;

import lombok.Getter;

@Getter
public enum PasswordCheckStatus {

    VERY_WEAK("very weak"), // length < 8
    WEAK("weak"), // contains only digits; length >= 8
    MEDIUM("medium"), // contains letters and digits; length >= 8
    GOOD("good"), // contains letters (lower and upper), digits and special characters; length >= 8
    PERFECT("perfect"); // contains letters (lower and upper), digits and special characters; length >= 16

    private final String status;

    PasswordCheckStatus(String status) {
        this.status = status;
    }
}
