package xpathquery;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;


public class ExtractXPathAttribute
{
		public static void main(String[] args) throws Exception
		{

				DocumentBuilderFactory documentumentBuilderFactory = DocumentBuilderFactory.newInstance();
				documentumentBuilderFactory.setNamespaceAware(true);
				DocumentBuilder documentumentBuilder = documentumentBuilderFactory.newDocumentBuilder();
				Document document = documentumentBuilder.parse("src/cricketTeam_info.xml");

				XPathFactory xpathFactory = XPathFactory.newInstance();
				XPath xpath = xpathFactory.newXPath();

				// get the type attribute of cricketer with name = 'Shami'
				XPathExpression expr = xpath.compile("//cricketer[name='Shami']/@type");
				String names = (String) expr.evaluate(document, XPathConstants.STRING);
				System.out.println("Righty attribute is : " + names);

				//get the type attribute of cricketers with role = 'Bowler'
				expr = xpath.compile("//cricketer[role='Bowler']/@type");
				NodeList nodes = (NodeList) expr.evaluate(document, XPathConstants.NODESET);
				for (int i = 0; i < nodes.getLength(); i++)
						System.out.println("Righty attribute is : " + nodes.item(i).getNodeValue());

		}
}

