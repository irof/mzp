package tddbc;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.junit.Assert.assertThat;

import org.junit.Test;

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
	
}
