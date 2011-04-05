package de.lightful.experiments.jodreports;

import com.artofsolving.jodconverter.DocumentConverter;
import com.artofsolving.jodconverter.openoffice.connection.OpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.connection.SocketOpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.converter.OpenOfficeDocumentConverter;
import net.sf.jooreports.templates.DocumentTemplate;
import net.sf.jooreports.templates.DocumentTemplateFactory;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static de.lightful.experiments.jodreports.LoanPartType.*;

@Test
public class TestDocumentGeneration {

  private List<Item> createCartItems() {
    return Arrays.asList(
        Item.create(100, "Digitalkamera", 5, new BigDecimal("129.95")),
        Item.create(250, "Druckerpapier 80g/m²", 25, new BigDecimal("4.90"))
    );
  }

  private List<LoanPart> createLoanParts() {
    return Arrays.asList(
        new LoanPart(new BigDecimal("40000"), 1, LINEAR),
        new LoanPart(new BigDecimal("140000"), 2, ANNUITY),
        new LoanPart(new BigDecimal("25000"), 3, CAPITAL_ENDOWMENT)
    );
  }

  private Map<String, Object> createDataModel() {
    Map<String, Object> dataModel = new HashMap<String, Object>();
    dataModel.put("testAttribut", "ABC");
    dataModel.put("firstName", "Peter der Fünfte");
    dataModel.put("lastName", "Parker von und zu Trallala");
    dataModel.put("name", "ThisIsTheName");
    dataModel.put("items", createCartItems());
    dataModel.put("loanparts", createLoanParts());
    return dataModel;
  }

  @Test
  public void testDocumentGeneration() throws Exception {
    DocumentTemplateFactory documentTemplateFactory = new DocumentTemplateFactory();
    final URL resource = this.getClass().getResource("template.odt");
    DocumentTemplate template = documentTemplateFactory.getTemplate(resource.openStream());
    Map<String, Object> dataModel = createDataModel();
    template.createDocument(dataModel, new FileOutputStream("document-instance.odt"));
  }


  @Test
  public void testConvertToPdf() throws Exception {
    OpenOfficeConnection connection = null;
    try {
      File inputFile = new File("document-instance.odt");
      File outputFile = new File("document-instance.pdf");

      // connect to an OpenOffice.org instance running on port 8100
      // run in shell: soffice -headless -accept="socket,host=127.0.0.1,port=8100;urp;" -nofirststartwizard
      // See: http://www.artofsolving.com/
      connection = new SocketOpenOfficeConnection(8100);
      connection.connect();

      // convert
      DocumentConverter converter = new OpenOfficeDocumentConverter(connection);
      converter.convert(inputFile, outputFile);
    }
    finally {
      // close the connection
      connection.disconnect();
    }
  }
}
