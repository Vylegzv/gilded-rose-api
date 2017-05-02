package com.vylegzv.gilded_rose.domain;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 
 * @author vella
 *
 */
@Entity
@Table(name = "item")
public class Item {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id")
  private long id;
  @Column(name = "name")
  private String name;
  @Column(name = "description")
  private String description;
  @Column(name = "price")
  private int price;

  public Item() {}

  public Item(String name, String description, int price) {
    super();
    this.name = name;
    this.description = description;
    this.price = price;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public int getPrice() {
    return price;
  }

  public void setPrice(int price) {
    this.price = price;
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, description, price);
  }

  @Override
  public boolean equals(Object object) {
    if (object instanceof Item) {
      Item that = (Item) object;
      return Objects.equals(this.name, that.name)
          && Objects.equals(this.description, that.description)
          && this.price == that.price;
    } else {
      return false;
    }
  }

  @Override
  public String toString() {
    return String.format("Item [id=%d, name='%s', description='%s', price=%d]",
        id, name, description, price);
  }
}