package com.aashiqumar.blucandy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;


import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Locale;


class GameScreen implements Screen {

    //SCREEN

    private Camera camera;
    private Viewport viewport;


    //GRAPHICS

    private SpriteBatch batch;
    private TextureAtlas textureAtlas;
    private Texture explosionTexture;

    private TextureRegion[] backgrounds;
    private float backgroundHeight;


    private TextureRegion playerShipTextureRegion, playerShieldTextureRegion, enemyShipTextureRegion, enemyShieldTextureRegion,
            playerLaserTextureRegion, enemyLaserTextureRegion;


    //TIMING

    private float[] backgroundOffset = {0, 0, 0, 0};
    private float backgroundMaxScrollingSpeed;
    private float timeBetweenEnemySpawns = 1f;
    private float enemySpawnTimer = 0;


    //WORLD PARAMETERS

    private final float WORLD_WIDTH = 72;
    private final float WORLD_HEIGHT = 128;
    private final float TOUCH_MOVEMENT_THRESHOLD = 5F;

    //GAME OBJECTS

    private PlayerShip playerShip;
    private LinkedList<EnemyShip> enemyShipList;
    private LinkedList<Lasers> playerlaserlist;
    private LinkedList<Lasers> enemylaserlist;
    private LinkedList<Explosion> explosionList;

    private int Score = 0;

    //HUD

    BitmapFont font;
    float hudVerticalMargin;
    float hudLeftX, hudRightX, hudCenterX, hudRow1Y, hudRow2Y;
    float hudSectionWidth;





    GameScreen() {

        camera = new OrthographicCamera();
        viewport = new StretchViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);

        //SETUP THE TEXTURE ATLAS

        textureAtlas = new TextureAtlas("images.atlas");

        //SETTING UP THE BACKGROUND

        backgrounds = new TextureRegion[5];

        backgrounds[0] = textureAtlas.findRegion("Starscape00");
        backgrounds[1] = textureAtlas.findRegion("Starscape01");
        backgrounds[2] = textureAtlas.findRegion("Starscape02");
        backgrounds[3] = textureAtlas.findRegion("Starscape03");



        backgroundHeight = WORLD_HEIGHT * 2;
        backgroundMaxScrollingSpeed = (float) (WORLD_HEIGHT) / 4;

        //INITIALIZE TEXTURE REGIONS

        //PLAYER

        playerShipTextureRegion = textureAtlas.findRegion("playerShip2_orange");
        playerShieldTextureRegion = textureAtlas.findRegion("shield1");
        playerLaserTextureRegion = textureAtlas.findRegion("laserGreen03");



        //ENEMY


        enemyShipTextureRegion = textureAtlas.findRegion("enemyRed1");
        enemyShieldTextureRegion = textureAtlas.findRegion("shield2");
        enemyLaserTextureRegion = textureAtlas.findRegion("laserRed13");
        enemyShieldTextureRegion.flip(false, true);

        //EXPLOSION TEXTURE

        explosionTexture = new Texture("explosion.png");

        //SETUP GAME OBJECTS

        playerShip = new PlayerShip(48, 10, 0.4f, 4, 45,
                0.4f, 10, 10,
                WORLD_WIDTH / 2, WORLD_HEIGHT / 4, playerShipTextureRegion, playerShieldTextureRegion, playerLaserTextureRegion);

        enemyShipList = new LinkedList<>();


        playerlaserlist = new LinkedList<Lasers>();
        enemylaserlist = new LinkedList<>();
        explosionList = new LinkedList<>();


        batch = new SpriteBatch();

        prepareHUD();


    }

    private void prepareHUD()
    {
        //CREATE A BITMAP FONT FORM OUR FONT FILE

        FreeTypeFontGenerator fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("EdgeOfTheGalaxyRegular-OVEa6.otf"));
        FreeTypeFontGenerator.FreeTypeFontParameter fontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

        fontParameter.size = 85;
        fontParameter.borderWidth = 2f;
        fontParameter.color = new Color(1, 1, 1, 0.3f);
        fontParameter.borderColor = new Color(0,0,0,0.3f);

        font = fontGenerator.generateFont(fontParameter);

        //SCALE THE FONT TO FIT WORLD

        font.getData().setScale(0.05f);

        //CALCULATION HUD MARGINS, ETC.

        hudVerticalMargin = font.getCapHeight() / 2;
        hudLeftX = hudVerticalMargin;
        hudRightX = WORLD_WIDTH * 2/3 - hudLeftX;
        hudCenterX = WORLD_WIDTH / 3;
        hudRow1Y = WORLD_HEIGHT - hudVerticalMargin;
        hudRow2Y = hudRow1Y - hudVerticalMargin - font.getCapHeight();
        hudSectionWidth = WORLD_WIDTH / 3;


    }


    @Override
    public void render (float delta) {

        batch.begin();

        //SCROLLING BACKGROUND

        renderBackground(delta);

        detectInput(delta);
        playerShip.update(delta);

        spawnEnemyShips(delta);

        ListIterator<EnemyShip> enemyShipListIterator = enemyShipList.listIterator();

        while (enemyShipListIterator.hasNext()) {

            EnemyShip enemyShip = enemyShipListIterator.next();

            moveEnemies(enemyShip, delta);
            enemyShip.update(delta);
            enemyShip.draw(batch);
        }

        //ENEMY SHIP

        playerShip.draw(batch);

        //LASERS

        renderLasers(delta);

        //DETECT COLLISIONS BETWEEN LASERS AND SHIPS

        detectCollisions();

        //EXPLOSIONS

        renderExplosions(delta);

        //HUD RENDERING

        updateAndRenderExplosionsHUD();


        batch.end();

    }

    private void updateAndRenderExplosionsHUD()
    {
        //RENDER TOP LEVEL LABELS

        font.draw(batch, "Score", hudLeftX, hudRow1Y, hudSectionWidth, Align.left, false);
        //font.draw(batch, "Lives", hudCenterX, hudRow1Y, hudSectionWidth, Align.center, false);
        font.draw(batch, "Lives", hudRightX, hudRow1Y, hudSectionWidth, Align.right, false);

        //RENDER SECOND ROW

        font.draw(batch, String.format(Locale.getDefault(), "%06d ", Score), hudLeftX, hudRow2Y, hudSectionWidth, Align.left, false);
        font.draw(batch, String.format(Locale.getDefault(), "%02d", playerShip.shield ), hudRightX, hudRow2Y, hudSectionWidth, Align.right, false);
        //font.draw(batch, String.format(Locale.getDefault(), "%02d", playerShip.lives), hudRightX, hudRow2Y, hudSectionWidth, Align.right, false);


    }

    private void spawnEnemyShips(float delta)
    {
        enemySpawnTimer += delta;

        if(enemySpawnTimer > timeBetweenEnemySpawns) {
            enemyShipList.add(new EnemyShip(30, 1, 0.9f, 4, 30,
                    0.9f, 10, 10,
                    BCapp.random.nextFloat() * (WORLD_WIDTH - 10) + 5, WORLD_HEIGHT - 1,
                    enemyShipTextureRegion, enemyShieldTextureRegion, enemyLaserTextureRegion));

            enemySpawnTimer -= timeBetweenEnemySpawns;
        }
    }

    private void detectInput(float delta) {

        //KEYBOARD INPUT

        //STRATEGY : DETERMINE THE MAX DISTANCE THE SHIP CAN MOVE

        //CHECK EACH KEY THAT MATTERS AND MOVE ACCORDINGLY

        float leftLimit, rightLimit, upLimit, downLimit;

        leftLimit = -playerShip.boundingBox.x;
        downLimit = -playerShip.boundingBox.y;
        rightLimit = WORLD_WIDTH - playerShip.boundingBox.x - playerShip.boundingBox.width;
        upLimit = (float)WORLD_HEIGHT / 2 - playerShip.boundingBox.y - playerShip.boundingBox.height;

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && rightLimit > 0)
        {
            playerShip.translate(Math.min(playerShip.movementSpeed * delta, rightLimit), 0f);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.UP) && upLimit > 0)
        {
            playerShip.translate(0f, Math.min(playerShip.movementSpeed * delta, upLimit));
        }

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && leftLimit < 0)
        {
            playerShip.translate(Math.max(-playerShip.movementSpeed * delta, leftLimit), 0f);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.DOWN) && downLimit < 0)
        {
            playerShip.translate(0f, Math.max(-playerShip.movementSpeed * delta, downLimit));
        }

        //TOUCH INPUT (AND ALSO MOUSE)

        if (Gdx.input.isTouched())
        {
            //GET THE SCREEN POSITION OF THE TOUCH

            float xTouchPixels = Gdx.input.getX();
            float yTouchPixels = Gdx.input.getY();

            //CONVERT TO WORLD POSITION

            Vector2 touchPoint = new Vector2(xTouchPixels, yTouchPixels);
            touchPoint = viewport.unproject(touchPoint);

            //CALCULATE THE X AND Y DIFFERENCES

            Vector2 playerShipCenter = new Vector2(playerShip.boundingBox.x + playerShip.boundingBox.width/2,
                    playerShip.boundingBox.y + playerShip.boundingBox.height/2);

            float touchDistance = touchPoint.dst(playerShipCenter);

            if (touchDistance > TOUCH_MOVEMENT_THRESHOLD)
            {
                float xTouchDifference = touchPoint.x - playerShipCenter.x;
                float yTouchDifference = touchPoint.y - playerShipCenter.y;

                float xMove = xTouchDifference / touchDistance * playerShip.movementSpeed * delta;
                float yMove = yTouchDifference / touchDistance * playerShip.movementSpeed * delta;

                if (xMove > 2) xMove = Math.min(xMove, rightLimit);
                else xMove = Math.max(xMove, leftLimit);

                if (yMove > 2) yMove = Math.min(yMove, upLimit);
                else yMove = Math.max(yMove, downLimit);

                playerShip.translate(xMove, yMove);



            }


            //SCALE TO THE MAXIMUM SPEED OF THE SHIP


        }



    }

    private void moveEnemies(EnemyShip enemyShip, float delta)
    {
        //CHECK EACH KEY THAT MATTERS AND MOVE ACCORDINGLY

        float leftLimit, rightLimit, upLimit, downLimit;

        leftLimit = -enemyShip.boundingBox.x;
        downLimit = (float)WORLD_HEIGHT/2 - enemyShip.boundingBox.y;
        rightLimit = WORLD_WIDTH - enemyShip.boundingBox.x - enemyShip.boundingBox.width;
        upLimit = WORLD_HEIGHT / 2 - enemyShip.boundingBox.y - enemyShip.boundingBox.height;


        float xMove = enemyShip.getDirectionVector().x * enemyShip.movementSpeed * delta;
        float yMove = enemyShip.getDirectionVector().y * enemyShip.movementSpeed* delta;

        if (xMove > 0) xMove = Math.min(xMove, rightLimit);
        else xMove = Math.max(xMove, leftLimit);

        if (yMove > 0) yMove = Math.min(yMove, upLimit);
        else yMove = Math.max(yMove, downLimit);

        enemyShip.translate(xMove, yMove);




    }


    private void detectCollisions()
    {
        //FOR EACH PLAYER LASER, CHECK WHETHER IT INTERSECTS AN ENEMY SHIP

        ListIterator<Lasers> laserListIterator = playerlaserlist.listIterator();
        while(laserListIterator.hasNext())
        {
            Lasers laser = laserListIterator.next();

            ListIterator<EnemyShip> enemyShipListIterator = enemyShipList.listIterator();
            while (enemyShipListIterator.hasNext())
            {
                EnemyShip enemyShip = enemyShipListIterator.next();

                if (enemyShip.intersects(laser.boundingBox)) {
                    if (enemyShip.hitAndCheckDestroy(laser))
                    {
                        enemyShipListIterator.remove();
                        explosionList.add(
                                new Explosion(explosionTexture, new Rectangle(enemyShip.boundingBox), 07f));

                        Score += 10;
                        playerShip.lives = 3;

                        if (playerShip.shield < 0)
                        {

                                playerShip.lives = 2;


                        }

                    }
                    laserListIterator.remove();
                    break;
                }
            }
        }

        //FOR EACH PLAYER LASER, CHECK WHETHER IT INTERSECTS AN PLAYER SHIP

        laserListIterator = enemylaserlist.listIterator();
        while(laserListIterator.hasNext())
        {
            Lasers laser = laserListIterator.next();

            if (playerShip.intersects(laser.boundingBox))
            {
                //CONTACT WITH PLAYER SHIP

                if (playerShip.hitAndCheckDestroy(laser))
                {
                    explosionList.add(
                            new Explosion(explosionTexture, new Rectangle(playerShip.boundingBox), 1.6f));

                    playerShip.shield = 10;
                }

                laserListIterator.remove();

                break;
            }
        }

    }

    private void renderExplosions(float delta)
    {
        ListIterator<Explosion> explosionListIterator = explosionList.listIterator();
        while (explosionListIterator.hasNext())
        {
            Explosion explosion = explosionListIterator.next();
            explosion.update(delta);

            if (explosion.isFinished())
            {
                explosionListIterator.remove();
            }

            else
            {
                explosion.draw(batch);
            }
        }
    }

    private void renderLasers(float delta)
    {

        //CREATE NEW LASERS

        //PLAYER LASER

        if (playerShip.canFireLaser())
        {
            Lasers[] lasers = playerShip.fireLasers();
            for (Lasers laser: lasers)
            {
                playerlaserlist.add(laser);
            }
        }

        //ENEMY LASER

        ListIterator<EnemyShip> enemyShipListIterator = enemyShipList.listIterator();
        while (enemyShipListIterator.hasNext())
        {
            EnemyShip enemyShip = enemyShipListIterator.next();

            if (enemyShip.canFireLaser()) {
                Lasers[] lasers = enemyShip.fireLasers();
                for (Lasers laser : lasers) {
                    enemylaserlist.add(laser);
                }
            }
        }


        //DRAW LASERs & REMOVE OLD LASERS

        //PLayer

        ListIterator<Lasers> iterator = playerlaserlist.listIterator();
        while(iterator.hasNext())
        {
            Lasers laser = iterator.next();
            laser.draw(batch);
            laser.boundingBox.y += laser.movementSpeed* delta;

            if(laser.boundingBox.y + WORLD_HEIGHT < 0)
            {
                iterator.remove();
            }
        }

        //Enemy

        ListIterator<Lasers> iterator1 = enemylaserlist.listIterator();
        while(iterator1.hasNext())
        {
            Lasers laser = iterator1.next();
            laser.draw(batch);
            laser.boundingBox.y -= laser.movementSpeed* delta;

            if(laser.boundingBox.y + WORLD_HEIGHT < 0)
            {
                iterator1.remove();
            }
        }

    }

    private void renderBackground(float delta)
    {
        backgroundOffset[0] += delta * backgroundMaxScrollingSpeed / 8;
        backgroundOffset[1] += delta * backgroundMaxScrollingSpeed / 4;
        backgroundOffset[2] += delta * backgroundMaxScrollingSpeed / 2;
        backgroundOffset[3] += delta * backgroundMaxScrollingSpeed;

        for (int layer = 0; layer < backgroundOffset.length; layer++)
        {
            if (backgroundOffset[layer] > WORLD_HEIGHT)
            {
                backgroundOffset[layer] = 0;
            }

            batch.draw(backgrounds[layer], 0, -backgroundOffset[layer], WORLD_WIDTH, WORLD_HEIGHT);

            batch.draw(backgrounds[layer], 0, -backgroundOffset[layer] + WORLD_HEIGHT, WORLD_WIDTH, WORLD_HEIGHT);
        }

    }

    @Override
    public void resize(int width, int height) {

        viewport.update(width, height, true);
        batch.setProjectionMatrix(camera.combined);


    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }

    @Override
    public void show() {

    }
}
