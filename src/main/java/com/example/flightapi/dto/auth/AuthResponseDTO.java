package com.example.flightapi.dto.auth;

import com.example.flightapi.dto.user.UserInfoDTO;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AuthResponseDTO {
    private String accessToken;
    private String refreshToken;
    private long accessTokenExpiresIn;
    private UserInfoDTO userInfo;
    
    public AuthResponseDTO(String accessToken, String refreshToken, long accessTokenExpiresIn, UserInfoDTO userInfo) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.accessTokenExpiresIn = accessTokenExpiresIn;
        this.userInfo = userInfo;
    }
    
    /**
     * 向后兼容的构造函数
     * @param token 访问令牌
     * @param userInfo 用户信息
     */
    public AuthResponseDTO(String token, UserInfoDTO userInfo) {
        this.accessToken = token;
        this.refreshToken = null;
        this.accessTokenExpiresIn = 0;
        this.userInfo = userInfo;
    }
}
