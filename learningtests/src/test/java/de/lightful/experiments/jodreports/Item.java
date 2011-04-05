package de.lightful.experiments.jodreports;

import java.math.BigDecimal;

public class Item {

  private String description;
  private Integer number;
  private Integer quantity;
  private BigDecimal price;

  Item() {
  }

  public static Item create(Integer number, String description, Integer quantity, BigDecimal price) {
    Item item = new Item();
    item.number = number;
    item.description = description;
    item.quantity = quantity;
    item.price = price;
    return item;
  }

  public String getDescription() {
    return description;
  }

  public Integer getNumber() {
    return number;
  }

  public BigDecimal getPrice() {
    return price;
  }

  public Integer getQuantity() {
    return quantity;
  }
}
