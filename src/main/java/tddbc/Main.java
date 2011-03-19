package tddbc;

import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

public class Main {

	/**
	 * @param args
	 * @throws TwitterException 
	 */
	public static void main(String[] args) throws TwitterException {
		Twitter twitter = new TwitterFactory().getInstance();
		String userId = "backpaper0";
		ResponseList<Status> list = twitter.getUserTimeline(userId);
		TweetCategorizer tc = new TweetCategorizer();
		for(Status status : list){
			String result = tc.categorize(userId + "¥t" + status.getText());
			System.out.println(result);
		}
	}

}
