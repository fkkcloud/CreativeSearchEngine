//
// Class using SearchObject Class to create data to be used
// for simulation.
//
// Copyright 2014 Jae Hyun Yoo <fkkcloud@gmail.com>
//

public class WordCount
{

  public String url;
  public String title;
  
  private Map<String, Integer> wordCount;
 
  WordCount(String url, String title)
  {
    this.wordCount = new TreeMap<String, Integer> ();
    this.url       = url;
    this.title     = title;
  }
  
  // url getter
  public String getUrl()
  {
    return this.url;
  }
  
  // title getter
  public String getTitle()
  {
    return this.title;
  }
  
  // title setter
  public void setTitle(String s)
  {
    this.title = s;
  }
  
  // wordCount getter
  public Map<String, Integer> getWordCount()
  {
    return this.wordCount;
  }
  
  // wordCount setter - WordCount Object
  public void setWordCount(WordCount wordCount)
  {
    this.wordCount = wordCount.getWordCount();
  }
  
  // wordCount setter - Map<String, Integer>
  public void setWordCount(Map<String, Integer> wordCountMap)
  {
    this.wordCount = wordCountMap;
  }
  
  // calculate for wordCount
  public void calculate()
  {
    String[] source = loadStrings( getUrlFixed(this.url) );

    // create empty word storage for each loop
    List<String> wordList = new ArrayList<String>();
  
    try 
    {
      // loop for each url's line
      for (int i=0; i<source.length; i++) 
      {
        // create parser of each line of url's string
        Document doc = Jsoup.parse(source[i]);
    
        // Take out html tags with .text() function.
        String text = doc.text();
        
        // See if there is enough txt to collect
        if (text.length() > 15) // threshold for 15 letters and above
          getWordlist(wordList, text);
      }
    } 
    catch (Exception e)
    {
      System.out.println(e);
      return;
    }
    
    Map<String, Integer> wordCountmp = new TreeMap<String, Integer>();
      
    // create wordCount Map
    for (String word : wordList) 
    {
      
      if ( wordCountmp.get(word) == null ) 
      {
        wordCountmp.put(word, 1);
      } 
      else if ( wordCountmp.get(word) != null ) 
      {
        wordCountmp.put( word, wordCountmp.get(word) + 1 );
      }
      
    }
    
    // find max count of the wordCount map to find %
    float maxval = maxVal( wordCountmp );
    
    // delete words that have count below 12% and above 62% 
    deleteByThreshold( wordCountmp, maxval, 0.12, 0.62 );
    
    // IMPLEMENT NEEDED add title's word as biggest numbers
    titleWordsAdd( wordCountmp, maxval, this.title );
     
    // sort by values - USING CUSTOM FUNCTION
    wordCount = sortByValues( wordCountmp );
 }

 // size getter
 public Integer size()
 {
   return this.wordCount.size();
 }
}


// STATIC METHODS
static <K extends Comparable,V extends Comparable> Map<K,V> sortByValues(Map<K,V> map)
{
    List<Map.Entry> entries = new LinkedList<Map.Entry>( map.entrySet() );
  
    Collections.sort( entries, new Comparator() 
    {
        public int compare( Object o1, Object o2 ) 
        {
          Map.Entry e1 = (Map.Entry)o1;
          Map.Entry e2 = (Map.Entry)o2;
          Integer first = (Integer)e1.getValue();  
          Integer second = (Integer)e2.getValue();

          return second.compareTo(first);
        }
    });
    
    //LinkedHashMap will keep the keys in the order they are inserted
    //which is currently sorted on natural ordering
    Map<K,V> sortedMap = new LinkedHashMap<K,V>();
  
    for(Map.Entry entry: entries)
        sortedMap.put( (K)entry.getKey(), (V)entry.getValue() );

    return sortedMap;
}


//remove all the useless words from the stringList - pass as reference
static void removeUselessWords( List<String> lsStr )
{
  // delete non-good length words from list
  for ( int idw=0; idw<lsStr.size(); idw++ )
  {

    if ( lsStr.get(idw).length() > 12 || lsStr.get(idw).length() < 3 )
      lsStr.remove( lsStr.get(idw) );

  }
}

// remove all the string except a-z, A-Z and single whitespaces. make em all lower case
static String getWordsOnly( String str )
{
  // with this regex, most of code "word" will be part of above 80% and below 15%
  String regex  = "[^a-zA-Z\\s]*";
  Pattern r     = Pattern.compile(regex);
  String regex2 = "\\s+";
  Pattern r2    = Pattern.compile(regex2);
  
  // Cleaning bad characters and make em lower case
  Matcher m  = r.matcher(str);
  String out = m.replaceAll("");
  Matcher m2 = r2.matcher(out);
  out        = m2.replaceAll(" ");

  return out.toLowerCase();
}

// get url string fixed
static String getUrlFixed( String s )
{
  return s.substring( 1, s.length() - 1 );
}

// find maxval from amp
static Integer maxVal( Map<String, Integer> map )
{
  if (map.size() == 0)
    return 0;
  
  Map.Entry maxEntry = null;
  for ( Map.Entry entry : map.entrySet() )
  {
    if ( maxEntry == null ||
        (Integer)entry.getValue() > (Integer)maxEntry.getValue() )
    {
      maxEntry = entry;
    }
  }
  
  return (Integer)maxEntry.getValue();
}

// delete strong candidate of repetitive or unique words
static void deleteByThreshold( Map<String, Integer> map, float maxval, float minthres, float maxthres )
{
  // Get a set of the entries
  Set set = map.entrySet();

  // Get an iterator
  Iterator it = set.iterator();
  
  // Display elements
  while ( it.hasNext() ) 
  {
   Map.Entry entry = (Map.Entry)it.next();
   float eval = (Integer)entry.getValue() / maxval;
   
   if (eval > maxthres || eval < minthres) 
      it.remove();
  }
}

static void getWordlist(List<String> wordList, String text)
{
  String out = getWordsOnly( text );

  // splitted array into list
  List<String> wordslist = new ArrayList<String>( Arrays.asList(out.split( "\\s" )) );
  
  // removeUselessWords
  removeUselessWords( wordslist );
  
  // covert wordslist list into string array
  String[] words = wordslist.toArray( new String[wordslist.size()] );
  
  for (String s : words)
    wordList.add(s);
  
  for (int wid = 0; wid<words.length - 1; wid++)
    wordList.add(String.format( "%s %s", words[wid], words[wid + 1]) );
    
}

static void titleWordsAdd( Map<String, Integer> map, float maxval, String text )
{
   String title = getWordsOnly(text);
   List<String> wordList = new ArrayList<String>();
   
   getWordlist( wordList, title );
   
   for ( String s : wordList )
     map.put( s, (int)maxval );
}

