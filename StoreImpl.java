package com.comp301.a08shopping;

import com.comp301.a08shopping.events.*;
import com.comp301.a08shopping.exceptions.OutOfStockException;
import com.comp301.a08shopping.exceptions.ProductNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StoreImpl implements Store {
  private final String name;
  private final List<StoreObserver> storeObserverList;
  private final HashMap<Product, Integer> inventoryHash;
  private final HashMap<Product, Double> discountHash;

  public StoreImpl(String name) {
    if (name == null) {
      throw new IllegalArgumentException("Name invalid");
    }
    this.name = name;
    this.storeObserverList = new ArrayList<>();
    this.inventoryHash = new HashMap<>();
    this.discountHash = new HashMap<>();
  }

  @Override
  public String getName() {
    return this.name;
  }

  @Override
  public void addObserver(StoreObserver observer) {
    if (observer == null) {
      throw new IllegalArgumentException("Null observer");
    }
    this.storeObserverList.add(observer);
  }

  @Override
  public void removeObserver(StoreObserver observer) {
    if (observer == null) {
      throw new IllegalArgumentException("Null observer");
    }
    this.storeObserverList.remove(observer);
  }

  @Override
  public List<Product> getProducts() {
    List<Product> newList = new ArrayList<>();
    newList.addAll(inventoryHash.keySet());
    return newList;
  }

  @Override
  public Product createProduct(String name, double basePrice, int inventory) {
    if (name == null || basePrice < 0 || inventory < 0) {
      throw new IllegalArgumentException("Invalid Argument.");
    }
    ProductImpl product = new ProductImpl(name, basePrice);
    inventoryHash.put(product, inventory);
    discountHash.put(product, 0.0);
    return product;
  }

  @Override
  public ReceiptItem purchaseProduct(Product product) {
    if (product == null) {
      throw new IllegalArgumentException("The product is null.");
    }
    if (!inventoryHash.containsKey(product)) {
      throw new ProductNotFoundException("Product not found.");
    }
    if (!getIsInStock(product)) {
      throw new OutOfStockException("Out of stock.");
    }
    PurchaseEvent purchase = new PurchaseEvent(product, this);
    notify(purchase);
    inventoryHash.replace(product, inventoryHash.get(product) - 1);
    ReceiptItem receipt = new ReceiptItemImpl(product.getName(), getSalePrice(product), getName());
    if (!getIsInStock(product)) {
      notify(new OutOfStockEvent(product, this));
    }
    return receipt;
  }

  @Override
  public void restockProduct(Product product, int numItems) {
    if (product == null || numItems < 0) {
      throw new IllegalArgumentException("Invalid parameter");
    }
    if (!inventoryHash.containsKey(product)) {
      throw new ProductNotFoundException("Product is not sold at the store.");
    }
    if (!getIsInStock(product)) {
      notify(new BackInStockEvent(product, this));
    }
    inventoryHash.replace(product, getProductInventory(product) + numItems);
  }

  @Override
  public void startSale(Product product, double percentOff) {
    if (product == null) {
      throw new IllegalArgumentException("Product is null.");
    }
    if (!inventoryHash.containsKey(product)) {
      throw new ProductNotFoundException("Product is not sold at the store.");
    }
    if (percentOff > 1.0 || percentOff < 0.0) {
      throw new IllegalArgumentException("Discount not allowed.");
    }
    discountHash.replace(product, percentOff);
    notify(new SaleStartEvent(product, this));
  }

  @Override
  public void endSale(Product product) {
    if (product == null) {
      throw new IllegalArgumentException("Product is null.");
    }
    if (!inventoryHash.containsKey(product)) {
      throw new ProductNotFoundException("Product is not sold at the store.");
    }
    discountHash.replace(product, 0.0);
    notify(new SaleEndEvent(product, this));
  }

  @Override
  public int getProductInventory(Product product) {
    if (product == null) {
      throw new IllegalArgumentException("Product is null.");
    }
    if (!inventoryHash.containsKey(product)) {
      throw new ProductNotFoundException("Product is not sold at the store.");
    }
    return inventoryHash.get(product);
  }

  @Override
  public boolean getIsInStock(Product product) {
    if (product == null) {
      throw new IllegalArgumentException("Product is null.");
    }
    if (!inventoryHash.containsKey(product)) {
      throw new ProductNotFoundException("Product is not sold at the store.");
    }
    return getProductInventory(product) > 0;
  }

  @Override
  public double getSalePrice(Product product) {
    if (product == null) {
      throw new IllegalArgumentException("Product is null.");
    }
    if (!inventoryHash.containsKey(product)) {
      throw new ProductNotFoundException("Product is not sold at the store.");
    }
    double afterDiscount = 1.0 - discountHash.get(product);
    return product.getBasePrice() * afterDiscount;
  }

  @Override
  public boolean getIsOnSale(Product product) {
    if (product == null) {
      throw new IllegalArgumentException("Product is null.");
    }
    if (!inventoryHash.containsKey(product)) {
      throw new ProductNotFoundException("Product is not sold at the store.");
    }
    return getSalePrice(product) < product.getBasePrice();
  }

  public void notify(StoreEvent e) {
    for (StoreObserver o : storeObserverList) {
      o.update(e);
    }
  }
}
