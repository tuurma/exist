package org.exist.xmldb.test;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import junit.framework.TestCase;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.XMLResource;

public class ResourceTest extends TestCase {

	private final static String URI = "xmldb:exist:///db";
	private final static String DRIVER = "org.exist.xmldb.DatabaseImpl";

	/**
	 * Constructor for XMLDBTest.
	 * @param arg0
	 */
	public ResourceTest(String arg0) {
		super(arg0);
	}

	public void testReadResource() {
		try {
			Collection testCollection =
				DatabaseManager.getCollection(URI + "/test");
			assertNotNull(testCollection);
			String[] resources = testCollection.listResources();
			assertEquals(resources.length, testCollection.getResourceCount());

			System.out.println("reading " + resources[0]);
			XMLResource doc =
				(XMLResource) testCollection.getResource("test.xml");
			assertNotNull(doc);

			System.out.println("testing XMLResource.getContentAsSAX()");
			StringWriter sout = new StringWriter();
			OutputFormat format = new OutputFormat("xml", "ISO-8859-1", true);
			format.setLineWidth(60);
			XMLSerializer xmlout = new XMLSerializer(sout, format);
			doc.getContentAsSAX(xmlout);
			System.out.println("----------------------------------------");
			System.out.println(sout.toString());
			System.out.println("----------------------------------------");
		} catch (XMLDBException e) {
			fail(e.getMessage());
		}
	}

	public void testReadDOM() {
		try {
			Collection testCollection =
				DatabaseManager.getCollection(URI + "/test");
			assertNotNull(testCollection);

			XMLResource doc =
				(XMLResource) testCollection.getResource("r_and_j.xml");
			assertNotNull(doc);
			Element elem = (Element) doc.getContentAsDOM();
			assertNotNull(elem);
			assertEquals(elem.getNodeName(), "PLAY");
			System.out.println("root element: " + elem.getNodeName());
			NodeList children = elem.getChildNodes();
			Node node;
			for (int i = 0; i < children.getLength(); i++) {
				node = children.item(i);
				assertNotNull(node);
				System.out.println("child: " + node.getNodeName());
			}
		} catch (XMLDBException e) {
			fail(e.getMessage());
		}
	}

	public void testSetContentAsSAX() {
		try {
			Collection testCollection =
				DatabaseManager.getCollection(URI + "/test");
			assertNotNull(testCollection);

			XMLResource doc =
				(XMLResource) testCollection.createResource(
					"test.xml",
					"XMLResource");
			String xml =
				"<test><title>Title</title>"
					+ "<para>Paragraph1</para>"
					+ "<para>Paragraph2</para>"
					+ "</test>";
			ContentHandler handler = doc.setContentAsSAX();
			SAXParserFactory saxFactory = SAXParserFactory.newInstance();
			saxFactory.setNamespaceAware(true);
			saxFactory.setValidating(false);
			SAXParser sax = saxFactory.newSAXParser();
			XMLReader reader = sax.getXMLReader();
			reader.setContentHandler(handler);
			reader.parse(new InputSource(new StringReader(xml)));
			testCollection.storeResource(doc);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	public void testSetContentAsDOM() {
		try {
			Collection testCollection =
				DatabaseManager.getCollection(URI + "/test");
			assertNotNull(testCollection);

			XMLResource doc =
				(XMLResource) testCollection.createResource(
					"dom.xml",
					"XMLResource");
			String xml =
				"<test><title>Title</title>"
					+ "<para>Paragraph1</para>"
					+ "<para>Paragraph2</para>"
					+ "</test>";
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = docFactory.newDocumentBuilder();
			Document dom = builder.parse(new InputSource(new StringReader(xml)));
			doc.setContentAsDOM(dom.getDocumentElement());
			testCollection.storeResource(doc);
		} catch(Exception e) {
			fail(e.getMessage());
		}
	}
}