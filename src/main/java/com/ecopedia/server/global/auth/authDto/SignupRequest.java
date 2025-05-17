package com.ecopedia.server.global.auth.authDto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SignupRequest {
    private String nickname;
    private String password;
}