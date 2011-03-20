package tddbc;

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

}
