package xpathquery;
import java.io.IOException;
import java.util.ArrayList;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.xml.sax.SAXException;
import javax.xml.xpath.*;

public class XMLquery{
		private DocumentBuilderFactory domFactory;
		private DocumentBuilder builder;
		private Document doc;
		public XMLquery(String path) throws ParserConfigurationException, SAXException, IOException{
			domFactory = DocumentBuilderFactory.newInstance();
			domFactory.setNamespaceAware(true);
			builder=domFactory.newDocumentBuilder();
			doc = builder.parse(path);
		}
		
		public ArrayList<String> getAttributes() throws XPathExpressionException{
			ArrayList<String> attributes=new ArrayList<String>();
			XPathFactory factory = XPathFactory.newInstance();
			XPath xpath = factory.newXPath();
			XPathExpression expr = xpath.compile("/forum/post/@discussion");
			Object result = expr.evaluate(doc, XPathConstants.NODESET);
			NodeList nodes = (NodeList) result; 
			for (int i = 0; i < nodes.getLength(); i++) {
				attributes.add(nodes.item(i).getNodeValue());
			}
			return attributes;
		}
		public ArrayList<String> getPosts(String discussion) throws XPathExpressionException, SAXException, IOException{
			ArrayList<String> posts=new ArrayList<String>();
			XPathFactory factory = XPathFactory.newInstance();
			XPath xpath = factory.newXPath();
			XPathExpression expr = xpath.compile("/forum/post[@discussion='"+discussion+"']/text()");
			Object result = expr.evaluate(doc, XPathConstants.NODESET);
			NodeList nodes = (NodeList) result; 
			for (int i = 0; i < nodes.getLength(); i++) {
				posts.add(nodes.item(i).getNodeValue());
			}
			return posts;
		}
}