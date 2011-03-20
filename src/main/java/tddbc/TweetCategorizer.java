package tddbc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class TweetCategorizer {

	private static final String TAB = "¥t";

	public String categorize(String tweet) {
		int idx = tweet.indexOf(TAB);
		idx = tweet.indexOf(TAB, idx + 1);
		String content = tweet.substring(idx + TAB.length());

		TweetBuilder tb = new TweetBuilder();
		for (Category cat : Category.values()) {
			if (cat.match(content)) tb.append(cat);
		}

		return tb.asString(content);
	}

	class TweetBuilder {
		List<Category> list = new ArrayList<Category>();

		void append(Category cat) {
			list.add(cat);
		}

		String asString(String content) {
			if (list.isEmpty()) list.add(Category.Normal);

			StringBuilder sb = new StringBuilder();
			for (Category cat : list) {
				if (sb.length() != 0) sb.append(",");
				sb.append(cat);
			}
			return sb.append(TAB).append(content).toString();
		}

	}

	enum Category {
		Normal, Reply {
			@Override
			boolean match(String content) {
				return content.matches("@\\w+.*");
			}
		},
		Mention {
			@Override
			boolean match(String content) {
				return content.matches(".+@\\w+.*");
			}
		},
		HashTag {
			@Override
			boolean match(String content) {
				return content.matches(".*#\\w+.*");
			}
		},
		;
		boolean match(String content) {
			return false;
		}
	}

	public List<String> getTimeLine() throws MalformedURLException, IOException, URISyntaxException {
		URI uri = new URI("http://192.168.1.40:4567/public_timeline");
		URLConnection connection = uri.toURL().openConnection();
		BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
		
		List<String> list = new ArrayList<String>();
		while(true){
			String buffer = reader.readLine();
			if(buffer == null){
				break;
			}
			list.add(buffer);
		}
		
		return list;
	}

}
