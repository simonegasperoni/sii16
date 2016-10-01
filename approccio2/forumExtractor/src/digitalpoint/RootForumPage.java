package digitalpoint;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class RootForumPage extends Page {
	
	private String rootURL;	
	
	public RootForumPage(String url) throws IOException {
		super(url);
		this.rootURL = "https://hardforum.com/";
		
	}
	
	public List<Forum> getForums() throws IOException {
		
		List<Forum> forums = new ArrayList<Forum>();
		Document doc = this.getDoc();
		Elements elementi = doc.body().getElementsByTag("h3");
		
		
		for (Element e : elementi)	{
			String forumURL = e.select("a").attr("href");
			String name = e.text();
			if (forumURL.startsWith("forums"))
				forums.add(new Forum(this.rootURL + forumURL,name));			
		}
				
		return forums;
		
	}
	
	public String getRootURL() {
		return this.rootURL;
	}
	
}
