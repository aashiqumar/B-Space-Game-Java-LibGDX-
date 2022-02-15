package com.aashiqumar.blucandy;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

class EnemyShip extends Ship {

    Vector2 directionVector;
    float timeSinceLastDChange = 0;
    float directionCFrequency= 0.75f;

    public EnemyShip(float movementSpeed,
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

        directionVector = new Vector2(0, -1);

    }

    public Vector2 getDirectionVector()
    {
        return directionVector;
    }

    private void randomizeDirectionVector()
    {
        double bearing = BCapp.random.nextDouble() * 6.283185; // 0 to 2*pi (22/7)

        directionVector.x = (float)Math.sin(bearing);
        directionVector.y = (float)Math.cos(bearing);
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        timeSinceLastDChange += delta;

        if(timeSinceLastDChange > directionCFrequency)
        {
            randomizeDirectionVector();
            timeSinceLastDChange -= directionCFrequency;
        }
    }

    @Override
    public Lasers[] fireLasers()
    {
        Lasers[] laser = new Lasers[2];

        laser[0] = new Lasers(boundingBox.x + boundingBox.width * 0.25f, boundingBox.y - laserHeight, laserWidth, laserHeight,
                laserMovementSpeed, laserTextureRegion);

        laser[1] = new Lasers(boundingBox.x + boundingBox.width * 0.70f, boundingBox.y - laserHeight, laserWidth,laserHeight,
                laserMovementSpeed, laserTextureRegion);

        timeSinceLastShot = 0;

        return laser;
    }

    @Override
    public void draw(Batch batch)
    {
        batch.draw(shipTextureRegion, boundingBox.x, boundingBox.y, boundingBox.width, boundingBox.height);
        if(shield > 0)
        {
            batch.draw(shieldTextureRegion, boundingBox.x, boundingBox.y - boundingBox.height * 0.2f, boundingBox.width, boundingBox.height);
        }
    }
}

