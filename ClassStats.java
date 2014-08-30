//
// Class for Pearson Correlation
//
// Copyright 2014 Jae Hyun Yoo <fkkcloud@gmail.com>
//

public class StatObject
{

  private List<String> keyList;
  
  StatObject()
  {
    keyList = new ArrayList<String>();
  }
  
  private float sum( Map<String, Integer> map )
  {
    float sum = 0;
    
    for ( String k : this.keyList )
      sum = sum + map.get(k);
    
    return sum;
   }
    
  private float sum_square( Map<String, Integer> map )
  {
    float sum_square = 0;
    
    for (String k : this.keyList)
	sum_square = sum_square + ( map.get(k) * map.get(k) );
      
    return sum_square;
  }
  
  private float sum_product( Map<String, Integer> map1, Map<String, Integer> map2 )
  {
    float sum_product = 0;
    
    for (String k : this.keyList)
      sum_product = sum_product + ( map1.get(k) * map2.get(k) );
    
    return sum_product;
  }

  public float pearson( WebObject obj1, WebObject obj2 )
  {    
    for ( Map.Entry entry : obj1.wordCount.wordCount.entrySet() )
    {
      if ( obj2.wordCount.wordCount.get( entry.getKey() ) != null )
        keyList.add( (String)entry.getKey() );
    }
    
    int iter_size = keyList.size();
    
    // General sums
    float sum1   = sum( v1.wordCount.wordCount );
    float sum2   = sum( v2.wordCount.wordCount );
    
    // Sums of the squares
    float sum1Sq = sum_square( v1.wordCount.wordCount );
    float sum2Sq = sum_square( v2.wordCount.wordCount );
    
    // Sum of the products
    float pSum   = sum_product( v1.wordCount.wordCount, v2.wordCount.wordCount );
    
    // Calculate r (Pearson score)
    float num    = pSum - ( ( sum1 * sum2 ) / iter_size );
      
    float val    = ( sum1Sq - (sum1 * sum1) / iter_size ) *
                   ( sum2Sq - (sum2 * sum2) / iter_size );
      
    float den    = sqrt(val);
      
    if (den == 0)
      return 0;
    
    return ( num / den );
  }
}


