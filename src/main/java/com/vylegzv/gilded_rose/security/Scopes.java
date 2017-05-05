package com.vylegzv.gilded_rose.security;

/**
 * 
 * @author vella
 *
 */
public enum Scopes {

  BUY("buy");

  private final String scope;

  private Scopes(final String scope) {
    this.scope = scope;
  }

  @Override
  public String toString() {
    return scope;
  }
}
