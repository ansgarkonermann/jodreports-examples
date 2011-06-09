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
import java.net.URL;
import java.util.Map;

import static de.lightful.experiments.jodreports.TestDataFactory.createDataModel;

@Test
public class GeneratePdfUsingSectionsTest {

  public static final String DOCUMENT_TEMPLATE_NAME = "PersonList.template.odt";
  public static final String DOCUMENT_INSTANCE_NAME = "PersonList.odt";
  public static final String DOCUMENT_PDF_NAME = "PersonList.pdf";

  @Test
  public void testDocumentGenerationWithSections() throws Exception {
    final URL resource = this.getClass().getResource(DOCUMENT_TEMPLATE_NAME);

    DocumentTemplateFactory documentTemplateFactory = new DocumentTemplateFactory();
    DocumentTemplate template = documentTemplateFactory.getTemplate(resource.openStream());

    Map<String, Object> dataModel = createDataModel();
    template.createDocument(dataModel, new FileOutputStream(DOCUMENT_INSTANCE_NAME));

    OpenOfficeConnection connection = null;
    try {
      File inputFile = new File(DOCUMENT_INSTANCE_NAME);
      File outputFile = new File(DOCUMENT_PDF_NAME);

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
}
