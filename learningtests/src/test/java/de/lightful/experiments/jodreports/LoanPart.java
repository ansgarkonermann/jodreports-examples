package de.lightful.experiments.jodreports;

import java.math.BigDecimal;

public class LoanPart {

  private BigDecimal amount;
  private int number;
  private LoanPartType type;

  LoanPart(BigDecimal amount, int number, LoanPartType type) {
    this.amount = amount;
    this.number = number;
    this.type = type;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public int getConsecutiveNumber() {
    return number;
  }

  public LoanPartType getType() {
    return type;
  }

}
