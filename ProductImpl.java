package com.comp301.a08shopping;

public class ProductImpl implements Product {
  private final String name;
  private final double basePrice;
  private final double discount;

  public ProductImpl(String name, double basePrice) {
    if (name == null) {
      throw new IllegalArgumentException("Name cannot be null.");
    }
    if (basePrice == 0.0) {
      throw new IllegalArgumentException("Base price cannot be 0.");
    }
    this.name = name;
    this.basePrice = basePrice;
    this.discount = 0.0;
  }

  @Override
  public String getName() {
    return this.name;
  }

  @Override
  public double getBasePrice() {
    return this.basePrice;
  }
}
