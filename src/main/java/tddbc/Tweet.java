package tddbc;

import java.util.Date;

public class Tweet {

	Date postedTime;
	String screenName;
	String content;
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((content == null) ? 0 : content.hashCode());
		result = prime * result
				+ ((postedTime == null) ? 0 : postedTime.hashCode());
		result = prime * result
				+ ((screenName == null) ? 0 : screenName.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		Tweet other = (Tweet) obj;
		if (content == null) {
			if (other.content != null) return false;
		} else if (!content.equals(other.content)) return false;
		if (postedTime == null) {
			if (other.postedTime != null) return false;
		} else if (!postedTime.equals(other.postedTime)) return false;
		if (screenName == null) {
			if (other.screenName != null) return false;
		} else if (!screenName.equals(other.screenName)) return false;
		return true;
	}

	
}
