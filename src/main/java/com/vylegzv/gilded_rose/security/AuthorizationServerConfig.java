package com.vylegzv.gilded_rose.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.approval.UserApprovalHandler;
import org.springframework.security.oauth2.provider.token.TokenStore;

import com.vylegzv.gilded_rose.service.ConfigService;

/**
 * 
 * @author vella
 *
 */
@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig
    extends AuthorizationServerConfigurerAdapter {

  @Autowired
  ConfigService configService;

  @Autowired
  private TokenStore tokenStore;

  @Autowired
  private UserApprovalHandler userApprovalHandler;

  @Autowired
  @Qualifier("authenticationManagerBean")
  private AuthenticationManager authenticationManager;

  private static String REALM = "GILDED_ROSE_OAUTH_REALM";

  private static final String[] authorizedGrantTypes;
  private static final String[] authorities;

  static {
    authorizedGrantTypes = new String[] {"password", "authorization_code",
        "refresh_token", "implicit"};
    authorities = new String[] {"ROLE_CLIENT", "ROLE_TRUSTED_CLIENT"};
  }

  @Override
  public void configure(ClientDetailsServiceConfigurer clients)
      throws Exception {

    clients.inMemory().withClient(configService.getClient())
        .authorizedGrantTypes(authorizedGrantTypes).authorities(authorities)
        .scopes(Scopes.BUY.toString()).secret(configService.getSecret())
        .accessTokenValiditySeconds(configService.getAccessTokenValidSeconds())
        .refreshTokenValiditySeconds(
            configService.getRefreshTokenValidSeconds());
  }

  @Override
  public void configure(AuthorizationServerEndpointsConfigurer endpoints)
      throws Exception {
    endpoints.tokenStore(tokenStore).userApprovalHandler(userApprovalHandler)
        .authenticationManager(authenticationManager);
  }

  @Override
  public void configure(AuthorizationServerSecurityConfigurer oauthServer)
      throws Exception {
    oauthServer.realm(REALM + "/user");
  }
}
