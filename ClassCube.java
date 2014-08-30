// 
// This class is to give config of cube for draing
//
// Copyright 2014 Jae Hyun Yoo <fkkcloud@gmail.com>
//

class Cube 
{

  public float w, h, d;
  public float shiftX, shiftY, shiftZ;

  Cube( float w, float h, float d, float shiftX, float shiftY, float shiftZ )
  {
    this.w = w;
    this.h = h;
    this.d = d;
    this.shiftX = shiftX;
    this.shiftY = shiftY;
    this.shiftZ = shiftZ;
  }

  // each cube face
  void drawCube()
  {
    beginShape( QUADS );
    // Front face
    vertex(-w * 0.5 + shiftX, -h * 0.5 + shiftY, -d * 0.5 + shiftZ);
    vertex(w + shiftX, -h * 0.5 + shiftY, -d * 0.5 + shiftZ);
    vertex(w + shiftX, h + shiftY, -d * 0.5 + shiftZ);
    vertex(-w * 0.5 + shiftX, h + shiftY, -d * 0.5 + shiftZ);

    // Back face
    vertex(-w * 0.5 + shiftX, -h * 0.5 + shiftY, d + shiftZ); 
    vertex(w + shiftX, -h * 0.5 + shiftY, d + shiftZ);
    vertex(w + shiftX, h + shiftY, d + shiftZ);
    vertex(-w * 0.5 + shiftX, h + shiftY, d + shiftZ);

    // Left face
    vertex(-w * 0.5 + shiftX, -h * 0.5 + shiftY, -d * 0.5 + shiftZ);
    vertex(-w * 0.5 + shiftX, -h * 0.5 + shiftY, d + shiftZ);
    vertex(-w * 0.5 + shiftX, h + shiftY, d + shiftZ);
    vertex(-w * 0.5 + shiftX, h + shiftY, -d * 0.5 + shiftZ);

    // Right face
    vertex(w + shiftX, -h * 0.5 + shiftY, -d * 0.5 + shiftZ);
    vertex(w + shiftX, -h * 0.5 + shiftY, d + shiftZ);
    vertex(w + shiftX, h + shiftY, d + shiftZ);
    vertex(w + shiftX, h + shiftY, -d * 0.5 + shiftZ);

    // Top face
    vertex(-w * 0.5 + shiftX, -h * 0.5 + shiftY, -d * 0.5 + shiftZ);
    vertex(w + shiftX, -h * 0.5 + shiftY, -d * 0.5 + shiftZ); 
    vertex(w + shiftX, -h * 0.5 + shiftY, d + shiftZ);
    vertex(-w * 0.5 + shiftX, -h * 0.5 + shiftY, d + shiftZ); 

    // Bottom face
    vertex(-w * 0.5 + shiftX, h + shiftY, -d * 0.5 + shiftZ);
    vertex(w + shiftX, h + shiftY, -d * 0.5 + shiftZ);
    vertex(w + shiftX, h + shiftY, d + shiftZ);
    vertex(-w * 0.5 + shiftX, h + shiftY, d + shiftZ);

    endShape();

    // Add some rotation to each box for pizazz.
    rotateY( radians(1) );
    rotateX( radians(1) );
    rotateZ( radians(1) );
  }
}
