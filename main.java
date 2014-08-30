//
// Informative Generative Art of Machine Learning
//
// This is program to collect web pages for a query and 
// create creative search result by clustering them with 
// similar stories to show people how the result can be
// grouped and to explore more diversity.
//
// Dependency : jsoup, gson, faroo search engine
//
// Copyright 2014 Jae Hyun Yoo <fkkcloud@gmail.com>
//

import java.util.*
import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.Math.*;

import com.google.gson.*;
import org.jsoup.*;

// GLOBAL SWITCHES
int cacheUse      = 0;
int cacheGenerate = 0;

// GLOBAL VARIABLES
List<WordCount> wordCounts = new ArrayList<WordCount>();
List<WebObject> webObjs    = new ArrayList<WebObject>();
String defaultQuery        = "Naked";

float mouseWheelVal    = 0;
float xmag, ymag       = 0;
float newXmag, newYmag = 0; 
float zPos             = -15;
float angle;

void setup() {
  size(1080, 720, P3D);
  if (frame != null)
    frame.setResizable(true);
  
  // query
  SearchObj  result = new SearchObj( defaultQuery, 10 );

  List<String> urls = new ArrayList<String>();
  Map<String, String> titles = new TreeMap<String, String>();

  result.getUrls(urls);
  result.getTitles(titles);

  // Cache generation for OPENGL test
  if (cacheGenerate == 1)
  {
    // write to file
    PrintWriter output;
    output = createWriter("resource.txt");
    
    // initialize WordCount Objects 
    initWordCountObjs(urls, titles);
    initWebCountObjs();
    
    // write data to txt
    for ( WordCount wordCount : wordCounts )
    {
      output.println( getStrFixed(wordCount.getUrl()) );
      for ( Map.Entry entry : wordCount.getWordCount().entrySet() )
          output.println(entry);
          
      output.println(String.format("#%s", getStrFixed(wordCount.getTitle())));
    } 
    
    // close file
    output.close();
  }
  
  // General calculation
  if (cacheUse == 0 && cacheGenerate == 0)
  {
    // init 
    initWordCountObjs( urls, titles );
    initWebCountObjs();
    initSimilarity();
    multiDimenScale( webObjs );
  }
    
  // Using cache for OPENGL
  if (cacheUse == 1)
  {
    List<String> cachedData = new ArrayList<String>();
    
    String[] lines = loadStrings("resource.txt");
    for (String s : lines)
      cachedData.add(s);  

    // initialize wordCount for the first time
    WordCount wordCount = new WordCount( "", "" );
    TreeMap<String, Integer> wordCountmap = new TreeMap<String, Integer>();

    // To update wordMass correctly
    int initial = 0;

    // run through the collect line from file and fill wordMass
    for (String s : cachedData)
    {

      if ( s.matches("https:(.*)") || s.matches("http:(.*)") ) 
      {

        if (initial > 0) 
        {
          wordCount.setWordCount(wordCountmap);
          wordCounts.add(wordCount);
          // creates completly new
          wordCountmap = new TreeMap<String, Integer>();
        }
        wordCount = new WordCount(s, ""); // HAVE TO PUT A WAY TO FIND TITLE in cached text.
        initial++;

      } 
      else if ( s.matches("#(.*)") )
      {
        wordCount.setTitle(s);
      } 
      else 
      {
        String[] parts = s.split("=");
        wordCountmap.put( parts[0], Integer.parseInt(parts[1]) );
      }

    }
    
    // add last wordCount map IMPORTANT
    wordCount.setWordCount( wordCountmap );
    wordCounts.add( wordCount );
  }
  
  // Using cache
  if (cacheUse == 1)
  {
    initWebCountObjs();
    initSimilarity();
    multiDimenScale( webObjs );
  }
}


// mouse wheel control for zoom in zoom out
void mouseWheel(MouseEvent event) 
{
  mouseWheelVal = event.getCount();
}


// draw() function

void draw()
{
  background(45);
  noStroke();
 
  // cube objects of web object
  pushMatrix(); 
  
  zPos -= mouseWheelVal * 0.5;
  translate(width * 0.5, height * 0.5, zPos);

  // Rotate around y and x axes
  rotateY(radians(angle));
  rotateX(radians(angle));
  
  // Used in rotate function calls above
  angle += 0.2;

  // draw web objects
   for ( WebObject webObj : webObjs )
     webObj.draw();
  
  popMatrix(); 
}

// initialize wordCount with given urls
void initWordCountObjs( List<String> urls, Map<String, String> titles )
{
  for( Map.Entry entry : titles.entrySet() )
  {

    WordCount wordCount = new WordCount( (String)entry.getKey(), titles.get(entry.getKey()) );
    wordCount.calculate();
    wordCounts.add( wordCount );
    
    Iterator it = wordCount.getWordCount().entrySet().iterator();  
    
    int iter = 0;
    while ( it.hasNext() && iter < 2 ) 
    {

      Map.Entry pairs = (Map.Entry)it.next();

      SearchObj  result = new SearchObj( pairs.getKey().toString(), 6 );
      
      List<String> urlsInner = new ArrayList<String>();
      result.getUrls( urlsInner );
      Map<String, String> titlesInner = new TreeMap<String, String>();
      result.getTitles( titlesInner );
      
      for ( Map.Entry entry2 : titlesInner.entrySet() )
      {

        WordCount wordCount2nd = new WordCount( (String)entry2.getKey(),
                                                 titlesInner.get(entry2.getKey()) );
        wordCount2nd.calculate();
        wordCounts.add( wordCount2nd );

      }
      
      iter++; 
    }
  }
}

// initialize web count objs
void initWebCountObjs()
{
  for( WordCount wordCount : wordCounts )
  {
     WebObject webObj = new WebObject( wordCount );
     Random rand      = new Random();
     
     int min          = 0;
     float  x         = rand.nextFloat();
     float  y         = rand.nextFloat();
     float  z         = rand.nextFloat();
     PVector pos      = new PVector(x, y, z);
     
     webObj.setPos( pos );
     webObjs.add( webObj );
  }
}

// initialize similarity of each web objects
void initSimilarity()
{
  for ( WebObject webObj1 : webObjs )
  {
    for ( WebObject webObj2 : webObjs )
    {
      webObj1.calculateCloseness( webObj2 );
    }
  }
}

// clustering calculation
void multiDimenScale( List<WebObject> webObjs )
{

  List<Float> fakedist = new ArrayList<Float>();
  
  int n        = webObjs.size();
  int iter_max = n * n;
  
  for (int i=0; i<iter_max; i++)
    fakedist.add(0.0);
  
  int iteration = 50;
  for (int i=0; i < iteration; ++i)
  {

    // Find projected distances
    int id = 0;
    for (WebObject webObj1 : webObjs)
    { 
      for (WebObject webObj2 : webObjs)
      {
        float distan = distance( webObj1, webObj2 );

        if ( distan == 0.0 )
        {
          fakedist.set( id, 0.1 );
        }
        else
        {
          fakedist.set( id, distan );
        }

        id++;
      }
    }
       
    // Move Points
    List<PVector> grad = new ArrayList<PVector>();
    for (int pid=0; pid<n; pid++)
    {
      PVector pos = new PVector(0.0, 0.0, 0.0);
      grad.add(pos);
    }
    
    float totalerror = 0;
    id = 0;
    int k = 0;
    
    for ( WebObject webObj1 : webObjs )
    {     
      for ( WebObject webObj2 : webObjs )
      {

        if ( webObj1.getUrl() == webObj2.getUrl() )
        {
          continue;
        } 
        else 
        {
          float realdist = webObj1.getSimilarity(webObj2);
          
          // Error is percent defference between the distances
          float errorterm;
          
          if (realdist < 0.001)
          {
            errorterm = 0.0;
          } 
          else 
          {
            errorterm = (fakedist.get(id)-realdist)/realdist;
            
            // Each point needs to be moved away from or towards the other
            // point in proportion to how much error it has
            PVector p_x = new PVector(grad.get(k).x + ( (webObj1.pos.x - webObj2.pos.x) / fakedist.get(id) ) * errorterm, 
                                      grad.get(k).y, 
                                      grad.get(k).z);
                                      
            grad.set(k, p_x);
            PVector p_y = new PVector(grad.get(k).x, 
                                      grad.get(k).y + ( (webObj1.pos.y - webObj2.pos.y) / fakedist.get(id) ) * errorterm, 
                                      grad.get(k).z);
                                      
            grad.set(k, p_y);  
            PVector p_z = new PVector(grad.get(k).x, 
                                      grad.get(k).y, 
                                      grad.get(k).z + ( (webObj1.pos.z - webObj2.pos.z) / fakedist.get(id) ) * errorterm);
            grad.set(k, p_z);
          }
          
          // Keep Track of the total error
          totalerror += abs(errorterm);
           
        }
        id++;
        
      }
    k++;
    
    }
    
   float mult = 200;
   float rate = 0.001;
   
    // Move each of the points by the learning rate times the gradient
    for (int m = 0; m < n; m++)
    {
      PVector oldPos = webObjs.get(m).pos;
      PVector newPos = new PVector(oldPos.x - (rate * grad.get(m).x), 
                                   oldPos.y - (rate * grad.get(m).y), 
                                   oldPos.z - (rate * grad.get(m).z));
                                   
      webObjs.get(m).setPos(newPos);
    }

  }
}

// STATIC METHODS
static float distance(WebObject webObj1, WebObject webObj2)
{
  float x = (webObj1.pos.x - webObj2.pos.x) * (webObj1.pos.x - webObj2.pos.x);
  float y = (webObj1.pos.y - webObj2.pos.y) * (webObj1.pos.y - webObj2.pos.y);
  float z = (webObj1.pos.z - webObj2.pos.z) * (webObj1.pos.z - webObj2.pos.z);
  return (x + y + z);
}


static String getStrFixed(String s)
{
  return s.substring(1, s.length() - 1);
}

static float remap(float value, float oldmin, float oldmax, float newmin, float newmax)
{
  return newmin + (value - oldmin) * (newmax - newmin) / (oldmax - oldmin);
}