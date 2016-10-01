package digitalpoint;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;


public class Main {

	public static void main(String[] args) throws IOException {
		
		//necessario lo slash finale se non è una pagina in una lista
		String seed = "https://hardforum.com/forums/vr-head-mounted-displays.142/";
		
		Forum videoCards = new Forum(seed, "VR and Mounted-Displays");
		//System.out.println("current page: " + videoCards.getCurrentPage());
		//System.out.println("total pages: " + videoCards.getTotalPages());
		//System.out.println("this url: " + videoCards.getUrl());
		//System.out.println("Next Page url: " + videoCards.nextPageURL());
		
		
		//RootForumPage root = new RootForumPage(seed);
		//System.out.println(root.getDoc());
		
		//RootForumPage videoCards = new RootForumPage(seed);
		//PrintWriter writer = new PrintWriter("RootPage.txt", "UTF-8");
		//writer.println(videoCards.getDoc().toString());
		//writer.close();
		
		
		List<Thread> threads = new ArrayList<Thread>();
		
		
		
		List<Post> posts = new ArrayList<Post>();
		
		
		threads = videoCards.getThreads(100);
		/*
		PrintWriter threadWriter = new PrintWriter("exampleThread.txt", "UTF-8");
		threadWriter.println(threads.get(0).getDoc().toString());
		threadWriter.close();
		*/
		for (Thread e : threads) {
			List<Post> temp = new ArrayList<Post>();
			temp = e.getPosts(1);
			posts.addAll(temp);
		}
		
		PrintWriter writer = new PrintWriter("VRPosts.txt", "UTF-8");
		
		for (Post e : posts) {
			String line = e.stringifyCSV();
			//System.out.println(line);
			writer.println(line);
		}
		
		writer.close();
		
		
		/*
		System.out.println(doc.body().toString());
		
		List<Forum> forums = root.getForums();		
		for (Forum e : forums)	{			
			System.out.println(e.getName());			
		}
		 
		*/

	}

}
