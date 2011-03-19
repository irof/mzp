package tddbc;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class TweetCategorizerTest {

	@Test
	public void 通常のTweetを受け取る() throws Exception {
		TweetCategorizer tc = new TweetCategorizer();
		String result = tc.categorize("bleis¥tあいうえお");
		assertThat(result , is("Normal¥tあいうえお"));
	}
	
	@Test
	public void ハッシュタグを含むTweetを受け取る() throws Exception {
		TweetCategorizer tc = new TweetCategorizer();
		String result = tc.categorize("hoge¥tfuga #tag");
		assertThat(result, is("HashTag¥tfuga #tag"));
	}
	
	@Test
	public void replyを含むTweetの時は先頭にReplyをつける() throws Exception {
		TweetCategorizer tc = new TweetCategorizer();
		String result = tc.categorize("bleis¥t@irof hogehoge");
		assertThat(result, is("Reply¥t@irof hogehoge"));
	}

	@Test
	public void mentionを含むTweetの時は先頭にMentionをつける() throws Exception {
		TweetCategorizer tc = new TweetCategorizer();
		String result = tc.categorize("tango¥tpiyo @irof");
		assertThat(result, is("Mention¥tpiyo @irof"));
	}
	
	@Test
	public void HashTagとReplyを含むTweetの時は先頭に両方をカンマ区切りにつなげる() throws Exception {
		TweetCategorizer tc = new TweetCategorizer();
		String result = tc.categorize("backpaper0¥t@tan_go238 ちょwww発表者しっかり！ #tddbc");
		assertThat(result, is("Reply,HashTag¥t@tan_go238 ちょwww発表者しっかり！ #tddbc"));
	}
}
