package tddbc;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.junit.Assert.assertThat;

import java.net.HttpURLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Test;
import org.junit.internal.matchers.TypeSafeMatcher;

public class TweetCategorizerTest {

	@Test
	public void 通常のTweetを受け取る() throws Exception {
		TweetCategorizer tc = new TweetCategorizer();
		String result = tc.categorize("2011/03/20 13:14:22¥tbleis¥tあいうえお");
		assertThat(result, is("Normal¥tあいうえお"));
	}

	@Test
	public void ハッシュタグ1() throws Exception {
		TweetCategorizer tc = new TweetCategorizer();
		String result = tc.categorize("2011/03/20 13:14:22¥thoge¥tfuga #tag");
		assertThat(result, is("HashTag¥tfuga #tag"));
	}
	@Test
	public void ハッシュタグ2() throws Exception {
		TweetCategorizer tc = new TweetCategorizer();
		String result = tc.categorize("2011/03/20 13:14:22¥thoge¥t#hogehoge fuga");
		assertThat(result, is("HashTag¥t#hogehoge fuga"));
	}

	@Test
	public void ハッシュタグじゃない1() throws Exception {
		TweetCategorizer tc = new TweetCategorizer();
		String result = tc.categorize("2011/03/20 13:14:22¥thoge¥tfuga #");
		assertThat(result, is("Normal¥tfuga #"));
	}

	@Test
	public void ハッシュタグじゃない2() throws Exception {
		TweetCategorizer tc = new TweetCategorizer();
		String result = tc.categorize("2011/03/20 13:14:22¥thoge¥tfuga # ");
		assertThat(result, is("Normal¥tfuga # "));
	}

	@Test
	public void replyを含むTweetの時は先頭にReplyをつける() throws Exception {
		TweetCategorizer tc = new TweetCategorizer();
		String result = tc.categorize("2011/03/20 13:14:22¥tbleis¥t@tan_go238 hogehoge");
		assertThat(result, is("Reply¥t@tan_go238 hogehoge"));
	}

	@Test
	public void Replyじゃない() throws Exception {
		TweetCategorizer tc = new TweetCategorizer();
		String result = tc.categorize("2011/03/20 13:14:22¥tbleis¥t@ irof hogehoge");
		assertThat(result, is("Normal¥t@ irof hogehoge"));
	}

	@Test
	public void mentionを含むTweetの時は先頭にMentionをつける() throws Exception {
		TweetCategorizer tc = new TweetCategorizer();
		String result = tc.categorize("2011/03/20 13:14:22¥ttango¥tpiyo @irof");
		assertThat(result, is("Mention¥tpiyo @irof"));
	}
	
	@Test
	public void Mentionじゃない() throws Exception {
		TweetCategorizer tc = new TweetCategorizer();
		String result = tc.categorize("2011/03/20 13:14:22¥ttango¥tpiyo @!irof");
		assertThat(result, is("Normal¥tpiyo @!irof"));
	}

	@Test
	public void HashTagとReplyを含むTweetの時は先頭に両方をカンマ区切りにつなげる() throws Exception {
		TweetCategorizer tc = new TweetCategorizer();
		String result = tc
				.categorize("2011/03/20 13:14:22¥tbackpaper0¥t@tan_go238 ちょwww発表者しっかり！ #tddbc");
		assertThat(result, is("Reply,HashTag¥t@tan_go238 ちょwww発表者しっかり！ #tddbc"));
	}
	
	@Test
	public void ネットワークから取ってくる() throws Exception {
		TweetCategorizer tc = new TweetCategorizer();
		List<String> list = tc.getTimeLine();
		assertThat(list, is(notNullValue()));
		assertThat(list.size(), is(20));
	}

	@Test
	public void 非公式RT_RT() throws Exception {
		TweetCategorizer tc = new TweetCategorizer();
		String result = tc.categorize("2011/03/20 13:14:22¥tbackpaper0¥tあいうえお RT @abc かきくけこ");
		assertThat(result, is("Mention,UnofficialRT¥tあいうえお RT @abc かきくけこ"));
	}
	
	@Test
	public void ツイートをBeanにする() throws Exception {
		TweetCategorizer tc = new TweetCategorizer();
		String tweet = "2011/03/20 13:14:22¥tbackpaper0¥tあいうえお RT @abc かきくけこ";
		Tweet result = tc.convert(tweet);
		assertThat(result, is(instanceOf(Tweet.class)));
		assertThat(result.postedTime, is(DateOf.dateOf("2011/03/20")));
		assertThat(result.screenName, is("backpaper0"));
		assertThat(result.content, is("あいうえお RT @abc かきくけこ"));
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
