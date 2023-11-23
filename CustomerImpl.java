package com.comp301.a08shopping;

import com.comp301.a08shopping.events.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerImpl implements Customer {
  private final String name;
  private final List<ReceiptItem> receiptItemList;
  private double budget;

  public CustomerImpl(String name, double budget) {
    if (name == null || budget <= 0.0 || name == "") {
      throw new IllegalArgumentException("Parameters Invalid");
    }
    this.name = name;
    this.budget = budget;
    this.receiptItemList = new ArrayList<>();
  }

  @Override
  public String getName() {
    return this.name;
  }

  @Override
  public double getBudget() {
    return this.budget;
  }

  @Override
  public void purchaseProduct(Product product, Store store) {
    if (product == null || store == null) {
      throw new IllegalArgumentException("Parameters invalid");
    }
    if (store.getSalePrice(product) > budget) {
      throw new IllegalStateException("You don't have enough money");
    }
    budget -= store.getSalePrice(product);
    store.purchaseProduct(product);
    ReceiptItemImpl receipt =
        new ReceiptItemImpl(product.getName(), store.getSalePrice(product), store.getName());
    receiptItemList.add(receipt);
  }

  @Override
  public List<ReceiptItem> getPurchaseHistory() {
    ArrayList<ReceiptItem> list = new ArrayList<ReceiptItem>();
    list.addAll(receiptItemList);
    return list;
  }

  @Override
  public void update(StoreEvent event) {
    String productName = event.getProduct().getName();
    String storeName = event.getStore().getName();
    if (event instanceof BackInStockEvent) {
      System.out.println(productName + " is back in stock at " + storeName);
    }
    if (event instanceof OutOfStockEvent) {
      System.out.println(productName + " is now out of stock at " + storeName);
    }
    if (event instanceof PurchaseEvent) {
      System.out.println("Someone purchased " + productName + " at " + storeName);
    }
    if (event instanceof SaleEndEvent) {
      System.out.println("The sale for " + productName + " at " + storeName + " has ended");
    }
    if (event instanceof SaleStartEvent) {
      System.out.println("New sale for " + productName + " at " + storeName + "!");
    }
  }
}
