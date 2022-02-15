package com.aashiqumar.blucandy;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

class PlayerShip extends Ship {

    int lives;

    public PlayerShip(float movementSpeed,
                      int shield,
                      float laserWidth,
                      float laserHeight,
                      float laserMovementSpeed,
                      float timeBetweenShots,
                      float width,
                      float height,
                      float xCenter,
                      float yCenter,
                      TextureRegion shipTextureRegion, TextureRegion shieldTextureRegion, TextureRegion laserTextureRegion) {
        super(movementSpeed, shield, width, height, xCenter, yCenter,
                laserMovementSpeed, laserWidth, laserHeight, timeBetweenShots, shipTextureRegion, shieldTextureRegion, laserTextureRegion);

        lives = 3;
    }

    @Override
    public Lasers[] fireLasers()
    {
        Lasers[] laser = new Lasers[2];

        laser[0] = new Lasers(boundingBox.x + boundingBox.width * 0.05f, boundingBox.y + boundingBox.height * 0.45f,
                laserWidth, laserHeight,
                laserMovementSpeed, laserTextureRegion);

        laser[1] = new Lasers(boundingBox.x + boundingBox.width * 0.82f, boundingBox.y + boundingBox.height * 0.45f,
                laserWidth,laserHeight,
                laserMovementSpeed, laserTextureRegion);

        timeSinceLastShot = 0;


        return laser;
    }
}
