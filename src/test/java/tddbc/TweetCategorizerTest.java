package tddbc;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.*;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.internal.matchers.TypeSafeMatcher;

public class TweetCategorizerTest {

	@Test
	public void 通常のTweetを受け取る() throws Exception {
		TweetCategorizer tc = new TweetCategorizer();
		String result = tc.categorize("2011/03/20 13:14:22\tbleis\tあいうえお");
		assertThat(result, is("Normal\tあいうえお"));
	}

	@Test
	public void ハッシュタグ1() throws Exception {
		TweetCategorizer tc = new TweetCategorizer();
		String result = tc.categorize("2011/03/20 13:14:22\thoge\tfuga #tag");
		assertThat(result, is("HashTag\tfuga #tag"));
	}
	@Test
	public void ハッシュタグ2() throws Exception {
		TweetCategorizer tc = new TweetCategorizer();
		String result = tc.categorize("2011/03/20 13:14:22\thoge\t#hogehoge fuga");
		assertThat(result, is("HashTag\t#hogehoge fuga"));
	}

	@Test
	public void ハッシュタグじゃない1() throws Exception {
		TweetCategorizer tc = new TweetCategorizer();
		String result = tc.categorize("2011/03/20 13:14:22\thoge\tfuga #");
		assertThat(result, is("Normal\tfuga #"));
	}

	@Test
	public void ハッシュタグじゃない2() throws Exception {
		TweetCategorizer tc = new TweetCategorizer();
		String result = tc.categorize("2011/03/20 13:14:22\thoge\tfuga # ");
		assertThat(result, is("Normal\tfuga # "));
	}

	@Test
	public void replyを含むTweetの時は先頭にReplyをつける() throws Exception {
		TweetCategorizer tc = new TweetCategorizer();
		String result = tc.categorize("2011/03/20 13:14:22\tbleis\t@tan_go238 hogehoge");
		assertThat(result, is("Reply\t@tan_go238 hogehoge"));
	}

	@Test
	public void Replyじゃない() throws Exception {
		TweetCategorizer tc = new TweetCategorizer();
		String result = tc.categorize("2011/03/20 13:14:22\tbleis\t@ irof hogehoge");
		assertThat(result, is("Normal\t@ irof hogehoge"));
	}

	@Test
	public void mentionを含むTweetの時は先頭にMentionをつける() throws Exception {
		TweetCategorizer tc = new TweetCategorizer();
		String result = tc.categorize("2011/03/20 13:14:22\ttango\tpiyo @irof");
		assertThat(result, is("Mention\tpiyo @irof"));
	}
	
	@Test
	public void Mentionじゃない() throws Exception {
		TweetCategorizer tc = new TweetCategorizer();
		String result = tc.categorize("2011/03/20 13:14:22\ttango\tpiyo @!irof");
		assertThat(result, is("Normal\tpiyo @!irof"));
	}

	@Test
	public void HashTagとReplyを含むTweetの時は先頭に両方をカンマ区切りにつなげる() throws Exception {
		TweetCategorizer tc = new TweetCategorizer();
		String result = tc
				.categorize("2011/03/20 13:14:22\tbackpaper0\t@tan_go238 ちょwww発表者しっかり！ #tddbc");
		assertThat(result, is("Reply,HashTag\t@tan_go238 ちょwww発表者しっかり！ #tddbc"));
	}

	@Ignore
	@Test
	public void ネットワークから取ってくる() throws Exception {
		TweetCategorizer tc = new TweetCategorizer();
		List<Tweet> list = tc.getTimeLine(1);
		assertThat(list, is(notNullValue()));
		assertThat(list.size(), is(20));
	}

	@Test
	public void 非公式RT_RT() throws Exception {
		TweetCategorizer tc = new TweetCategorizer();
		String result = tc.categorize("2011/03/20 13:14:22\tbackpaper0\tあいうえお RT @abc かきくけこ");
		assertThat(result, is("Mention,UnofficialRT\tあいうえお RT @abc かきくけこ"));
	}
	
	@Test
	public void ツイートをBeanにする() throws Exception {
		TweetCategorizer tc = new TweetCategorizer();
		String tweet = "2011/03/20 13:14:22\tbackpaper0\tあいうえお RT @abc かきくけこ";
		Tweet result = tc.convert(tweet);
		assertThat(result, is(instanceOf(Tweet.class)));
		assertThat(result.postedTime, is(DateOf.dateOf("2011/03/20")));
		assertThat(result.screenName, is("backpaper0"));
		assertThat(result.content, is("あいうえお RT @abc かきくけこ"));
	}

	@Test
	public void 最大２０件Tweetを取得する() throws Exception {
		TweetCategorizer tc = getTestInstance(1);
		List<Tweet> list = tc.getLastTimeLine(30);
		assertThat(list, is(notNullValue()));
		assertThat(list.size(), is(20));
	}

	@Ignore
	@Test
	public void ページング() throws Exception {
		TweetCategorizer tc = new TweetCategorizer();
		List<?> result1 = tc.getTimeLine(1);
		List<?> result2 = tc.getTimeLine(2);

		for (int i = 0; i < result1.size(); i++) {
			assertThat(result1.get(0).equals(result2.get(0)), is(false));
		}
	}

	@Test
	public void 複数ページでとる() throws Exception {
		TweetCategorizer tc = 複数ページ取得用();		
		List<Tweet> list = tc.getLastTimeLine(30);
		assertThat(list.size(), is(20));
		assertThat(list.get(0).content, is("page1"));
		assertThat(list.get(19).content, is("page3"));
	}

	/**
	 * 1ページ5件返す。
	 * 全て現在時間
	 * @return
	 */
	private TweetCategorizer 複数ページ取得用() {
		TweetCategorizer tc = new TweetCategorizer(){
			@Override
			public List<Tweet> getTimeLine(int page) throws MalformedURLException,
					IOException, URISyntaxException, ParseException {
				List<Tweet> tweets = new ArrayList<Tweet>(); 
				long currentTime = new Date().getTime();
				for(int i=0; i<7; i++){
					Tweet tweet = new Tweet();
					tweet.postedTime = new Date(currentTime);
					tweet.content = "page" + page;
					tweets.add(tweet);
				}
				return tweets;
			}
		};
		return tc;
	}

	private TweetCategorizer getTestInstance(final int interval) {
		TweetCategorizer tc = new TweetCategorizer(){
			@Override
			public List<Tweet> getTimeLine(int page) throws MalformedURLException,
					IOException, URISyntaxException, ParseException {
				List<Tweet> tweets = new ArrayList<Tweet>(); 
				long currentTime = new Date().getTime();
				for(int i=0; i<30; i++){
					Tweet tweet = new Tweet();
					currentTime -= (interval * 60 * 1000);
					tweet.postedTime = new Date(currentTime);
					tweets.add(tweet);
				}
				return tweets;
			}
		};
		return tc;
	}

	@Test
	public void 三十分越えたのTweetを取得しない() throws Exception {
		TweetCategorizer tc = getTestInstance(2);
		List<Tweet> list = tc.getLastTimeLine(30);
		Date validDate = new Date(new Date().getTime() - (60 * 1000 * 30));
		for(Tweet tweet : list) {
			assertThat(tweet.postedTime.before(validDate), is(false));
		}
	}
	
	

	static class DateOf extends TypeSafeMatcher<Date> {

		String expected;
		public DateOf(String expected) {
			this.expected = expected;
		}

		@Override
		public void describeTo(Description description) {
			description.appendValue(expected);
			
		}

		@Override
		public boolean matchesSafely(Date item) {
			return new SimpleDateFormat("yyyy/MM/dd").format(item).equals(expected);
		}

		public static Matcher<Date> dateOf(String expected) {
			return new DateOf(expected);
		}
	}
	
}
