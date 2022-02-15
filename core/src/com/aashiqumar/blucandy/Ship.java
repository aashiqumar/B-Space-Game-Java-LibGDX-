package com.aashiqumar.blucandy;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

abstract class Ship
{
    //SHIP CHARACTERISTICS

    float movementSpeed; //world unit per second
    int shield;

    //POSITION & DIMENSIONS


    Rectangle boundingBox;

    //GRAPHICS

    TextureRegion shipTextureRegion, shieldTextureRegion, laserTextureRegion;

    //LASER INFORMATION

    float laserWidth, laserHeight;
    float laserMovementSpeed;
    float timeBetweenShots;
    float timeSinceLastShot = 0;

    public Ship(float movementSpeed, int shield,  float width, float height, float xCenter,
                float yCenter, float laserMovementSpeed, float laserWidth, float laserHeight,
                float timeBetweenShots, TextureRegion shipTextureRegion, TextureRegion shieldTextureRegion, TextureRegion laserTextureRegion) {
        this.movementSpeed = movementSpeed;
        this.shield = shield;
        this.boundingBox = new Rectangle(xCenter - width/2, yCenter - width/2, width, height);
        this.shipTextureRegion = shipTextureRegion;
        this.shieldTextureRegion = shieldTextureRegion;
        this.laserTextureRegion = laserTextureRegion;
        this.laserHeight = laserHeight;
        this.laserWidth = laserWidth;
        this.laserMovementSpeed = laserMovementSpeed;
        this.timeBetweenShots = timeBetweenShots;

    }

    public void update(float delta)
    {

        timeSinceLastShot += delta;
    }

    public boolean canFireLaser ()
    {
        return (timeSinceLastShot - timeBetweenShots >= 0);


    }

    public  abstract Lasers[] fireLasers();

    public boolean intersects(Rectangle otherRectangle)
    {

        return boundingBox.overlaps(otherRectangle);
    }

    public boolean hitAndCheckDestroy(Lasers laser)
    {
        if (shield > 0)
        {
            shield -- ;
            return false;
        }

        return true;
    }

    public void  translate(float xChange, float yChange)
    {
        boundingBox.setPosition(boundingBox.x + xChange, boundingBox.y + yChange);
    }

    public void draw(Batch batch)
    {
        batch.draw(shipTextureRegion, boundingBox.x, boundingBox.y, boundingBox.width, boundingBox.height);
        if(shield > 0)
        {
            batch.draw(shieldTextureRegion, boundingBox.x, boundingBox.y, boundingBox.width, boundingBox.height);
        }
    }
}
