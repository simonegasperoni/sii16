package digitalpoint;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Forum extends RootForumPage {
	
	private String name;

	public Forum(String url, String name) throws IOException {
		super(url);
		this.name = name;
	}
	
	public List<Thread> getThreads(int n) throws IOException {
		
		List<Thread> threads = new ArrayList<Thread>();
		Document doc = this.getDoc();
		Elements elementi = doc.body().getElementsByTag("h3");
		int c = n;
		while (c > 0) {
			for (Element e : elementi)	{
			
				String threadURL = e.select("a").attr("href");
				String title = e.text();
				if (threadURL.startsWith("threads")) {
					threads.add(new Thread(this.getRootURL() + threadURL, this.name, title));
					c--;
				}
								
				if (!(c > 0)) 
					break;
			}
			
			if(c > 0) {				
				List<Thread> threadsR = new ArrayList<Thread>();
				if(this.getCurrentPage() != this.getTotalPages()) {
					Forum nextPage = new Forum(this.nextPageURL(), this.name);
					threadsR = nextPage.getThreads(c);
					threads.addAll(threadsR);
				}
				c = 0;
			}
		}
				
		return threads;
		
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
