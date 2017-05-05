package com.vylegzv.gilded_rose.security;

/**
 * 
 * @author vella
 *
 */
public enum Roles {

  USER("USER");

  private final String role;

  private Roles(final String role) {
    this.role = role;
  }

  @Override
  public String toString() {
    return role;
  }
}
