package tddbc;

public class TweetCategorizer {

	private static final String TAB = "¥t";

	public String categorize(String tweet) {
		int idx = tweet.indexOf(TAB);
		String content = tweet.substring(idx + 2);

		TweetBuilder tb = new TweetBuilder();
		
		if (isReply(content)) tb.append(Category.Reply);
		if (isHashTag(content)) tb.append(Category.HashTag);
		if (isMention(content)) tb.append(Category.Mention);

		return tb.asString(content);
	}

	class TweetBuilder {
		StringBuilder sb = new StringBuilder();
		
		void append(Category cat) {
			if (sb.length() != 0) sb.append(",");
			sb.append(cat);
		}
		
		String asString(String content) {
			if(sb.length() == 0){
				sb.append(Category.Normal);
			}
			return sb.append(TAB).append(content).toString();
		}

	}
	
	private boolean isMention(String content) {
		return content.matches(".+@.+");
	}

	private boolean isReply(String content) {
		return content.startsWith("@");
	}
	
	private boolean isHashTag(String content){
		return content.contains("#"); 
	}
	
	enum Category{
		Normal, HashTag, Reply, Mention;
	}

}
