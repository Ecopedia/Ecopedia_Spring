package com.ecopedia.server.global.auth.authDto;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginRequest {
    private String nickname;
    private String password;
}
