package de.lightful.experiments.jodreports;

import com.artofsolving.jodconverter.DefaultDocumentFormatRegistry;
import com.artofsolving.jodconverter.DocumentConverter;
import com.artofsolving.jodconverter.DocumentFormat;
import com.artofsolving.jodconverter.openoffice.connection.OpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.connection.SocketOpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.converter.OpenOfficeDocumentConverter;
import com.artofsolving.jodconverter.openoffice.converter.StreamOpenOfficeDocumentConverter;
import net.sf.jooreports.templates.DocumentTemplate;
import net.sf.jooreports.templates.DocumentTemplateFactory;
import org.apache.commons.io.output.NullOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static de.lightful.experiments.jodreports.LoanPartType.ANNUITY;
import static de.lightful.experiments.jodreports.LoanPartType.CAPITAL_ENDOWMENT;
import static de.lightful.experiments.jodreports.LoanPartType.LINEAR;
import static org.fest.assertions.Assertions.assertThat;

@Test
public class TestDocumentGeneration {

  public static final int SPEED_TEST_ITERATIONS = 100;
  public static final String OPENOFFICE_HOST = "dev.emma-nl.hypoport.local";

  private Logger logger = LoggerFactory.getLogger(TestDocumentGeneration.class);
  private static final int INPUT_BUFFER_SIZE = 131072;

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
    dataModel.put("borrowers", createPersons());
    return dataModel;
  }

  private List<Person> createPersons() {
    return new ArrayList<Person>() {
      {
        add(new Person("Peter", 41));
        add(new Person("Susi", 33));
        add(new Person("James", 67));
      }
    };
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
  public void testDocumentGenerationWithSections() throws Exception {
    DocumentTemplateFactory documentTemplateFactory = new DocumentTemplateFactory();
    final URL resource = this.getClass().getResource("PersonList.odt");
    DocumentTemplate template = documentTemplateFactory.getTemplate(resource.openStream());
    Map<String, Object> dataModel = createDataModel();
    template.createDocument(dataModel, new FileOutputStream("PersonList-instance.odt"));
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
      if (connection.isConnected()) {
        connection.disconnect();
      }
    }
  }

  @Test
  public void testConvertToPdfUsingStreams() throws Exception {
    OpenOfficeConnection connection = null;
    try {
      File inputFile = new File("document-instance.odt");
      File outputFile = new File("document-instance.pdf");
      InputStream inputStream = new FileInputStream(inputFile);
      OutputStream outputStream = new FileOutputStream(outputFile);

      // connect to an OpenOffice.org instance running on port 8100
      // run in shell: soffice -headless -accept="socket,host=127.0.0.1,port=8100;urp;" -nofirststartwizard
      // See: http://www.artofsolving.com/
      connection = new SocketOpenOfficeConnection(OPENOFFICE_HOST, 8100);
      connection.connect();
      assertThat(connection.isConnected()).as("Connection to " + OPENOFFICE_HOST + " connection status: connected?").isTrue();

      // convert
      final DefaultDocumentFormatRegistry formatRegistry = new DefaultDocumentFormatRegistry();
      DocumentConverter converter = new StreamOpenOfficeDocumentConverter(connection, formatRegistry);
      final DocumentFormat odtFileFormat = formatRegistry.getFormatByFileExtension("odt");
      final DocumentFormat pdfFileFormat = formatRegistry.getFormatByFileExtension("pdf");
      converter.convert(inputStream, odtFileFormat, outputStream, pdfFileFormat);
    }
    finally {
      // close the connection
      if (connection.isConnected()) {
        connection.disconnect();
      }
    }
  }

  @Test
  public void testConversionSpeedWithStreams() throws Exception {
    OpenOfficeConnection connection = null;
    try {
      File inputFile = new File("document-instance.odt");
      File outputFile = new File("document-instance.pdf");

      // connect to an OpenOffice.org instance running on port 8100
      // run in shell: soffice -headless -accept="socket,host=127.0.0.1,port=8100;urp;" -nofirststartwizard
      // See: http://www.artofsolving.com/
      connection = new SocketOpenOfficeConnection(OPENOFFICE_HOST, 8100);
      connection.connect();
      assertThat(connection.isConnected()).as("Connection to " + OPENOFFICE_HOST + " connection status: connected?").isTrue();

      // convert
      final DefaultDocumentFormatRegistry formatRegistry = new DefaultDocumentFormatRegistry();
      DocumentConverter converter = new StreamOpenOfficeDocumentConverter(connection, formatRegistry);
      final DocumentFormat odtFileFormat = formatRegistry.getFormatByFileExtension("odt");
      final DocumentFormat pdfFileFormat = formatRegistry.getFormatByFileExtension("pdf");
      final long timestampAtStart = System.currentTimeMillis();
      for (int i = 0; i < SPEED_TEST_ITERATIONS; i++) {
        InputStream inputStream = new FileInputStream(inputFile);
        OutputStream outputStream = new NullOutputStream();
        converter.convert(inputStream, odtFileFormat, outputStream, pdfFileFormat);
      }
      final long timestampAtEnd = System.currentTimeMillis();

      final long duration = timestampAtEnd - timestampAtStart;
      logger.info("Duration: " + duration + "ms (=> " + duration * 1.0d / SPEED_TEST_ITERATIONS + "ms per document generation)");
    }
    finally {
      // close the connection
      if (connection.isConnected()) {
        connection.disconnect();
      }
    }
  }
}
