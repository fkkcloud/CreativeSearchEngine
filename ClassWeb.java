//
// Class to storage web page datas
//
// Copyright 2014 Jae Hyun Yoo <fkkcloud@gmail.com>
//

public class WebObject{
  
  public PVector pos;
  public String  url;
  public PImage  urlimg;

  private Map<String, Float> similMap;
  private WordCount wordCount;  
  private PFont font;
  private float maxval;
  private float minval;
  
  WebObject( WordCount wordCount )
  {
    this.font      = createFont( "Arial", 16, true );
    this.wordCount = wordCount;
    this.pos       = new PVector(0, 0, 0);
    this.url       = wordCount.getUrl();
    this.urlimg    = null;
    this.similMap  = new TreeMap<String, Float>();
  }
  
  // returns size
  public Integer size()
  {
    return this.wordCount.size();
  }
  
  // draw with PVector
  public void draw( PVector pos )
  {
    fill(0);
    text(this.url, pos.x, pos.y, pos.z); 
    textFont(font,40);
  }
  
  // draw with internal data
  public void draw()
  {
    float sum        = remap( this.sumCloseness(), 0, 10, 1, 3 );
    float mult_scale = 200;
    
    fill(227, 0, 120, 200);
  
    Cube cube = new Cube( sum, sum, sum,
                         this.pos.x * mult_scale,
                         this.pos.y * mult_scale,
                         this.pos.z * mult_scale );
    cube.drawCube();
    
    fill(50, 100, 200, 220);
    
    String mainTitle = this.wordCount.title;
    if( mainTitle.length() > 34 )
      mainTitle = String.format("%s...", mainTitle.substring(0, 34));
      
    text( mainTitle ,
         this.pos.x * mult_scale,
         this.pos.y * mult_scale,
         this.pos.z * mult_scale );
  }
  
  // wordCount setter
  public void setWordCount(WordCount wordCount )
  {
    this.wordCount = wordCount;
  }
  
  // wordCount getter
  public WordCount getWordCount()
  {
    return this.wordCount;
  }
  
  // image setter
  public void setImage(String imgPath)
  {
    this.urlimg = loadImage( imgPath );
  }
  
  // image getter
  public PImage getImage()
  {
    return this.urlimg;
  }
  
  // pos setter
  public void setPos(PVector pos)
  {
    this.pos.set(pos.x, pos.y, pos.z);
  }
  
  // pos getter
  public PVector getPos()
  {
    return this.pos;
  } 
  
  // url setter
  public void setUrl(String url)
  {
    this.url = url;
  }
  
  // url getter
  public String getUrl()
  {
    return this.url;
  }
    
  // similarity sum
  public float sumCloseness()
  {
    float sum = 0.0;
    for ( Map.Entry entry : this.similMap.entrySet() )
        sum += (Float)entry.getValue();
        
        return sum;
  }
  
  // similarity getter
  public float getSimilarity( WebObject obj )
  {
      return similMap.get( obj.getUrl() );
  }
  
  // calculating closeness
  public void calculateCloseness( WebObject webObj )
  {
    StatObject stats = new StatObject();
    float closeness  = stats.pearson(webObj, this);
    closeness        = remap( closeness, -1, 1, 0, 1 );
    
    if ( Float.isNaN(closeness) )
      closeness = 0.0;
      
    this.similMap.put( webObj.getUrl(), closeness );
  }
  
}