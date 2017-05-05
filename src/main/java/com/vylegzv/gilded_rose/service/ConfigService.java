package com.vylegzv.gilded_rose.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * 
 * @author vella
 *
 */
@Service
public class ConfigService {

  @Value("${security.config.client}")
  private String client;

  @Value("${security.config.secret}")
  private String secret;

  @Value("${security.config.accessTokenValidSec}")
  private int accessTokenValidSeconds;

  @Value("${security.config.refreshTokenValidSec}")
  private int refreshTokenValidSeconds;

  public String getClient() {
    return client;
  }

  public String getSecret() {
    return secret;
  }

  public int getAccessTokenValidSeconds() {
    return accessTokenValidSeconds;
  }

  public int getRefreshTokenValidSeconds() {
    return refreshTokenValidSeconds;
  }
}
