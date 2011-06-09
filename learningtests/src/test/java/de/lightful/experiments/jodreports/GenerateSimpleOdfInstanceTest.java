package de.lightful.experiments.jodreports;

import net.sf.jooreports.templates.DocumentTemplate;
import net.sf.jooreports.templates.DocumentTemplateFactory;
import org.testng.annotations.Test;

import java.io.FileOutputStream;
import java.net.URL;
import java.util.Map;

import static de.lightful.experiments.jodreports.TestDataFactory.createDataModel;

@Test
public class GenerateSimpleOdfInstanceTest {

  @Test
  public void testDocumentGeneration() throws Exception {
    DocumentTemplateFactory documentTemplateFactory = new DocumentTemplateFactory();
    final URL resource = this.getClass().getResource("SimpleTemplate.odt");
    DocumentTemplate template = documentTemplateFactory.getTemplate(resource.openStream());
    Map<String, Object> dataModel = createDataModel();
    template.createDocument(dataModel, new FileOutputStream("GenerateSimpleOdfInstanceTest.odt"));
  }
}
