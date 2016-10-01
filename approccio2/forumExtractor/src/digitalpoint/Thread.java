package digitalpoint;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Thread extends Page {
	
	private String title;
	private String category;

	public Thread(String url, String category, String title) throws IOException {
		super(url);
		this.setCategory(category);
		this.setTitle(title);
	}
	
	public List<Post> getPosts(int n) throws IOException {
		return this.getPostsAux(n, n);
	}
	
	public List<Post> getPostsAux(int o, int n) throws IOException {
		
		List<Post> posts = new ArrayList<Post>();
		Document doc = this.getDoc();
		Elements elementi = doc.body().getElementsByClass("messageContent");
		int c = n;
		while (c > 0) {
			for (Element e : elementi)	{
				String content = e.text();
				if (n == o) 
					posts.add(new Post(true, this.category, this.title, content));
				else
					posts.add(new Post(false, this.category, this.title, content));
				c--;
				if (!(c > 0)) 
					break;
			}
			if(c > 0) {				
				List<Post> postsR = new ArrayList<Post>();
				if(this.getCurrentPage() != this.getTotalPages()) {
					Thread nextPage = new Thread(this.nextPageURL(), this.category, this.title);
					postsR = nextPage.getPostsAux(o, c);
					posts.addAll(postsR);
				}
				c = 0;
			}
		}
				
		return posts;
		
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

}
