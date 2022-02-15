package com.aashiqumar.blucandy;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

class Lasers {

    //POSITION AND DIMENSIONS

    Rectangle boundingBox;

    //LASER PHYSICAL CHARACTERISTICS

    float movementSpeed; //WORLD UNITS PER SECOND

    //GRAPHICS

    TextureRegion textureRegion;

    public Lasers(float xCenter, float yBottom, float width, float height, float movementSpeed, TextureRegion textureRegion) {
        this.boundingBox = new Rectangle(xCenter - width / 2, yBottom - width / 2, width, height);
        this.movementSpeed = movementSpeed;
        this.textureRegion = textureRegion;
    }

    public void draw(Batch batch1) {
        batch1.draw(textureRegion, boundingBox.x, boundingBox.y, boundingBox.width, boundingBox.height);

    }

}