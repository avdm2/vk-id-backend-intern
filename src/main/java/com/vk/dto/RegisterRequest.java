package com.vk.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class RegisterRequest {

    private String email;
    private String password;
}
