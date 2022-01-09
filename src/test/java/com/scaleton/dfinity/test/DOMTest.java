package com.scaleton.dfinity.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import com.scaleton.dfinity.candid.dom.DOMDeserializer;
import com.scaleton.dfinity.candid.dom.DOMSerializer;
import com.scaleton.dfinity.candid.dom.DOMUtils;
import com.scaleton.dfinity.candid.parser.IDLArgs;
import com.scaleton.dfinity.candid.parser.IDLType;
import com.scaleton.dfinity.candid.parser.IDLValue;

public final class DOMTest extends CandidAssert{
    // Instantiate the Factory
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    
	static {
		LOG = LoggerFactory.getLogger(DOMTest.class);
	}

	@Test
	public void test() {
		dbf.setNamespaceAware(true);
		dbf.setIgnoringElementContentWhitespace(true);
		
		this.testDom("SimpleNode.xml", null);
		this.testDom("ComplexNode.xml", null);
	}
	
	void testDom(String fileName, IDLType idlType) {
		try {
			Node domNode = this.readNode(fileName);
			
			IDLValue idlValue;
			
			if(idlType == null)
				idlValue = IDLValue.create(domNode,DOMSerializer.create());
			else
				idlValue = IDLValue.create(domNode,DOMSerializer.create(idlType));
			
			List<IDLValue> args = new ArrayList<IDLValue>();
			args.add(idlValue);

			IDLArgs idlArgs = IDLArgs.create(args);

			byte[] buf = idlArgs.toBytes();
			
			DOMDeserializer domDeserializer = DOMDeserializer.create(idlValue.getIDLType()).rootElement("http://scaleton.com/dfinity/candid","data");
			//domDeserializer = domDeserializer.setAttributes(true);
			
			Node domNodeResult = IDLArgs.fromBytes(buf).getArgs().get(0).getValue(domDeserializer, Node.class);
			
//			Assertions.assertTrue(domNode.isEqualNode(domNodeResult));

		} catch (SAXException | IOException | ParserConfigurationException e) {
			LOG.error(e.getLocalizedMessage(), e);
			Assertions.fail(e.getMessage());
		}		
	}
	
	Node readNode(String fileName) throws SAXException, IOException, ParserConfigurationException
	{
        // parse XML file
        DocumentBuilder db = dbf.newDocumentBuilder();

        Document doc = db.parse(getClass().getClassLoader().getResource(fileName).getFile());
 
		try {
			String domString = DOMUtils.getStringFromDocument(doc);
		} catch (TransformerException e) {

		}
        return doc.getDocumentElement();
	}
}
