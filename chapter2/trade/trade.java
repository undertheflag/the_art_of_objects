//*************************************************************************
/**
 * Trade.java - A class describing a Trade
 *
 *   Copyright (C) 1998-2000    Yun-Tung Lau
 *   All Rights Reserved.  See the license file in the home 
 *   directory of this package for important license information.
 */
//*************************************************************************

/**
 * This class is an association class between Supplier and Buyer.
 * Note that this implementation does not enforce the candidate key
 * requirement for association class.  Thus the same pair of supplier
 * and buyer may be associated with mulitple trades.  
 * <P>
 * To make implementation consistent with the requirement, one may 
 * have a collection of all trade and dynamically check for duplication.
 * Readers are encouraged to implement this feature.
 */
public class Trade {

  // Attributes in the Trade class
  private String product;
  private int quantity;
  private double total_price;

  // Links to Supplier and Buyer
  private Supplier supplier;
  private Buyer buyer;

  // discount for large quantitites
  private double discount = 0.0;

  /** 
   * Constructs a Trade object.  It also sets backward links from
   * the supplier and buyer objects for referential integrity.
   *
   * @param product	product of the Trade
   * @param quantity	quantity of the Trade
   * @param supplier	supplier of the Trade
   * @param buyer	buyer of the Trade
   */
  public Trade(String product, int quantity,
  		Supplier supplier, Buyer buyer) {
    this.product = product; 
    this.quantity = quantity;
    this.supplier = supplier;
    this.buyer = buyer;

    // set backward links for referential integrity
    supplier.addTrade(this);
    buyer.addTrade(this);
  }

  /** 
   * Returns the product of this Trade.
   *
   * @return the product of this Trade
   */
  public String getProduct() { 
    return product; 
  }

  /** 
   * Returns the quantity of this Trade.
   *
   * @return the quantity of this Trade
   */
  public int getQuantity() {
    return quantity; 
  }

  /** 
   * Returns the total price of this Trade.
   *
   * @return the total price of this Trade
   */
  public double getTotalPrice() {
    return total_price; 
  }

  /** 
   * Returns the supplier of this Trade.
   *
   * @return the supplier of this Trade
   */
  public Supplier getSupplier() {
    return supplier; 
  }

  /** 
   * Returns the buyer of this Trade.
   *
   * @return the buyer of this Trade
   */
  public Buyer getBuyer() {
    return buyer; 
  }

  /** 
   * Returns the unit price in this Trade.  In real life, this
   * operation may connect to a database server to retrieve the
   * data.
   *
   * @return the unit price for the product in this Trade.  Zero
   * 	means unknown.
   */
  public double getUnitPrice() {
    double unit_price;

    if ((product).equalsIgnoreCase("soap")) {
      unit_price = 4.85;  // hard coded here
    } else if ((product).equalsIgnoreCase("pen")) {
      unit_price = 0.33;  // hard coded here
    } else {
      unit_price = 0.0;  // zero means unknown
    }
    return unit_price; 
  }

  /** 
   * Returns whether a discount applies to this Trade.  This 
   * operation contains the business rule for discounts.  It
   * sets the discount as well.
   *
   * @return true if a discount applies to this Trade
   */
  public boolean isDiscounted() {
    if (quantity >= 20) {
      discount = 0.10;  // 10%

    } else if (quantity >= 50) {
      discount = 0.15;  // 15%
    }

    if (discount > 0.0) return true;
    else return false;
  }

  /** 
   * Calculates and sets the total price of this Trade.
   *
   */
  public void calculatePrice() { 
    // Retrieves the unit price
    double unit_price = getUnitPrice();

    total_price = unit_price * quantity;

    if (isDiscounted()) {
      total_price = total_price * ( 1.0 - discount );
    }
  }

  /** 
   * Sets the quantity of this Trade.
   *
   * @param quantity		the quantity of this Trade
   */
  public void setQuantity(int quantity) { 
    this.quantity = quantity; 
  }

  /** 
   * Returns the information of this Trade as a string.
   *
   * @return the information of this Trade
   */
  public String toString() { 
    String s = "Product: " + product + "\n";
    s += "Quantity: " + quantity + "\n";
    s += "Total Price: " + total_price;
    if (discount > 0.0) {
      s += " (" + discount * 100 + "% discount applied)";
    }
    s += "\n";
    s += "Supplier: " + supplier.getName();
    s += "  Buyer: " + buyer.getName();
    s += "\n";
    return s;
  }

  /** 
   * Shows the input string.
   *
   * @s the string to be shown
   */
  public static void show(String s) { 
    System.out.println(s);
  }


  /**
   * A test that returns a string.  Note it is a static method.
   *
   * @return result of the test as a string
   * @exception Exception if any exception is thrown
   */
  public static String test() throws Exception {
    String s;

    Supplier s1 = new Supplier("General Store", "9 Main St");
    Supplier s2 = new Supplier("Special Store", "11 Water St");

    Buyer b1 = new Buyer("Mary", "101 Pearl St");
    Buyer b2 = new Buyer("John", "40 Water St");

    Trade t1 = new Trade("soap", 10, s1, b2);
    Trade t2 = new Trade("pen", 80, s1, b1);
    Trade t3 = new Trade("soap", 22, s1, b1);
    Trade t4 = new Trade("pen", 5, s1, b2);

    t1.calculatePrice();
    t2.calculatePrice();
    t3.calculatePrice();
    t4.calculatePrice();

    s = t1.toString();
    s += "\n";
    s += t2.toString();
    s += "\n";
    s += t3.toString();
    s += "\n";
    s += t4.toString();

    s += "\n\n... List of Suppliers:";
    s += "\n>>> " + s1.toString();
    s += "\n>>> " + s2.toString();

    return s;
  }

  /**
   * Main method for testing.
   *
   * @exception Exception if any exception is thrown
   */
  public static void main(String argv[]) throws Exception {
    show(test());
  }

}

