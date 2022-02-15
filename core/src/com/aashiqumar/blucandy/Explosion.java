package com.aashiqumar.blucandy;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

class Explosion {

    private Animation<TextureRegion> explosionAnimation;
    private float explosionTimer;

    private com.badlogic.gdx.math.Rectangle boundingBox;

    Explosion (Texture texture, Rectangle boundingBox, float totalAnimationTime)
    {
        this.boundingBox = boundingBox;

        //SPLIT TEXTURE

        TextureRegion[][] textureRegion2D = TextureRegion.split(texture, 64, 64);

        // CONVERT TO 1D ARRAY

        TextureRegion[] textureRegion1D = new TextureRegion[16];
        int index = 0;

        for (int i = 0; i < 4; i++)
        {
            for (int j = 0; j < 4; j++)
            {
                textureRegion1D[index] = textureRegion2D[j][j];
                index++;
            }
        }

        explosionAnimation = new Animation<TextureRegion>(totalAnimationTime/16, textureRegion1D);
        explosionTimer = 0;

    }

    public void update (float delta)
    {
        explosionTimer += delta;
    }

    public void draw (SpriteBatch batch)
    {
        batch.draw(explosionAnimation.getKeyFrame(explosionTimer), boundingBox.x, boundingBox.y, boundingBox.width, boundingBox.height);
    }

    public boolean isFinished()
    {
        return explosionAnimation.isAnimationFinished(explosionTimer);
    }
}
