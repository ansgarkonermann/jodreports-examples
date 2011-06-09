package de.lightful.experiments.jodreports;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static de.lightful.experiments.jodreports.LoanPartType.ANNUITY;
import static de.lightful.experiments.jodreports.LoanPartType.CAPITAL_ENDOWMENT;
import static de.lightful.experiments.jodreports.LoanPartType.LINEAR;

public class TestDataFactory {

  public static Map<String, Object> createDataModel() {
    Map<String, Object> dataModel = new HashMap<String, Object>();
    dataModel.put("testAttribut", "ABC");
    dataModel.put("firstName", "Peter der Fünfte");
    dataModel.put("lastName", "Parker von und zu Trallala");
    dataModel.put("name", "ThisIsTheName");
    dataModel.put("items", createCartItems());
    dataModel.put("loanparts", createLoanParts());
    dataModel.put("borrowers", createPersons());
    return dataModel;
  }

  public static List<Item> createCartItems() {
    return Arrays.asList(
        Item.create(100, "Digitalkamera", 5, new BigDecimal("129.95")),
        Item.create(250, "Druckerpapier 80g/m²", 25, new BigDecimal("4.90"))
    );
  }

  public static List<LoanPart> createLoanParts() {
    return Arrays.asList(
        new LoanPart(new BigDecimal("40000"), 1, LINEAR),
        new LoanPart(new BigDecimal("140000"), 2, ANNUITY),
        new LoanPart(new BigDecimal("25000"), 3, CAPITAL_ENDOWMENT)
    );
  }

  public static List<Person> createPersons() {
    return new ArrayList<Person>() {
      {
        add(new Person("Peter", 41));
        add(new Person("Susi", 33));
        add(new Person("James", 67));
      }
    };
  }
}
