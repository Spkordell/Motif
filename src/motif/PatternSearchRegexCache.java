/**
 * 
 */
package motif;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author Steve Kordell
 *
 */
public class PatternSearchRegexCache {
	
	private static PatternSearchRegexCache patternSearchRegexCache;
	private List<Pattern> compiledRegexCache;
	
	private PatternSearchRegexCache() {
		compiledRegexCache = new ArrayList<Pattern>(3000); 
	}
	
	public static PatternSearchRegexCache getInstance() {
		return patternSearchRegexCache != null ? patternSearchRegexCache : (patternSearchRegexCache = new PatternSearchRegexCache());
	}
	
	public Pattern getRegex(int i) { //min i is 2
		if (this.compiledRegexCache.size() <= (i-2)) {
			this.compiledRegexCache.add(Pattern.compile("((\\d+\\s){"+i+"}).*\\1"));
		}
		return this.compiledRegexCache.get(i-2);
	}
	
}
