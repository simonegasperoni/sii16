package digitalpoint;

import java.io.IOException;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;



public class Page {
	
	private String url;
	private Document doc;
	
	
	public Page(String url) throws IOException {
		
		this.url = url;
		this.doc = this.downloadDoc();
		
	}
	
	public String nextPageURL() {
		int currentPage = this.getCurrentPage();
		int totalPages = this.getTotalPages();
		if (currentPage < totalPages) {
			int nextPage = currentPage+1;
			String url = this.url;
			if (currentPage == 1) {
				url = url + "page-" + nextPage;
			}
			else {
				while (url.charAt(url.length()-1) != '-') {
					url = url.substring(0,url.length()-1);
				}
				url = url + nextPage;				
			}
			return url;
		}
		else return this.url;
	}
	
	public Document downloadDoc() throws IOException {
		System.out.println("Sto scaricando: " + this.url);
		return Jsoup.connect(this.url).get();		
	}
	
	public int getCurrentPage() {
		try {
		Element e = this.doc.getElementsByClass("pageNavHeader").get(0);
		PageNavHeader pageNav = new PageNavHeader(e.text());
		return pageNav.getCurrentPage();
		} catch (Exception e) {
			return 1;
		}
	}
	
	public int getTotalPages() {
		try {
		Element e = this.doc.getElementsByClass("pageNavHeader").get(0);
		PageNavHeader pageNav = new PageNavHeader(e.text());
		return pageNav.getTotalPages();
		} catch (Exception e) {
			return 1;
		}
	}
	
	public String getUrl() {
		return url;
	}

	public void setName(String url) {
		this.url = url;
	}

	public Document getDoc() {
		return doc;
	}

	public void setDoc(Document doc) {
		this.doc = doc;
	}
	
}
	
