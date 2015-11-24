package com.badlogic.gdx;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import java.util.Iterator;


public class GameScreen implements Screen, InputProcessor {
    final DonutRoll game;

    SpriteBatch batch;
    Sprite sprite, spriteML, spriteRL, spriteLL, spriteMU, spriteRU, spriteLU;
    Texture img, img2, img3, hand;
    World world;
    Body body, bodyML, bodyRL, bodyLL, bodyMU, bodyRU, bodyLU;
    Body bodyEdgeScreen;
    Box2DDebugRenderer debugRenderer;
    Matrix4 debugMatrix;
    OrthographicCamera camera;
    BitmapFont font;
    Music twoJump;
    int jump_counter = 0;
    boolean movingRight = false;
    boolean movingLeft = false;
    boolean holdingDown = false;
    boolean drawSprite = true;
    Texture dropHands;
    Array<Rectangle> hands;
    long lastDropTime;
    State state = State.RUN;

    final float PIXELS_TO_METERS = 100f;


    public GameScreen(final DonutRoll gam) {
        this.game = gam;


        batch = new SpriteBatch();
        img = new Texture("donut.png");
        img2 = new Texture("platform.png");
        img3 = new Texture("platform2.png");
        hand = new Texture(Gdx.files.internal("hand.png"));
        twoJump = Gdx.audio.newMusic(Gdx.files.internal("2jump.wav"));
        twoJump.setVolume(0.1f);

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 900, 700);

        //define the donut, labelled as sprite because it is the most important sprite
        sprite = new Sprite(img);
        sprite.setPosition(-sprite.getWidth() / 2, -sprite.getHeight() - 460 / 2);

        //define the middle lower platform, hence the spriteML
        spriteML = new Sprite(img2);
        spriteML.setPosition(-spriteML.getWidth() / 2, -spriteML.getHeight() - 430 / 2);

        //define the right lower platform, hence the spriteRL
        spriteRL = new Sprite(img3);
        spriteRL.setPosition(-spriteRL.getWidth() + MathUtils.random(500, 850) / 2, -spriteRL.getHeight() - 200 / 2);

        //define the left lower platform, hence the spriteLL
        spriteLL = new Sprite(img3);
        spriteLL.setPosition(-spriteLL.getWidth() + MathUtils.random(-500, -250)  / 2, -spriteLL.getHeight() - 200 / 2);

        //define the middle upper platform, hence the spriteMU
        spriteMU = new Sprite(img2);
        spriteMU.setPosition(-spriteMU.getWidth()/ 2, -spriteMU.getHeight() + 40  / 2);

        //define the right upper platform, hence the spriteRU
        spriteRU = new Sprite(img3);
        spriteRU.setPosition(-spriteRU.getWidth() + MathUtils.random(500, 850) / 2, -spriteRU.getHeight() + 235 / 2);

        //define the left upper platform, hence the spriteLU
        spriteLU = new Sprite(img3);
        spriteLU.setPosition(-spriteLU.getWidth() + MathUtils.random(-500, -250)  / 2, -spriteLU.getHeight() + 235 / 2);

        world = new World(new Vector2(0, -15f), true);

        //creating the donut
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set((sprite.getX() + sprite.getWidth() / 2) /
                        PIXELS_TO_METERS,
                (sprite.getY() + sprite.getHeight() / 2) / PIXELS_TO_METERS);

        body = world.createBody(bodyDef);

        CircleShape shape = new CircleShape();
        shape.setRadius(sprite.getWidth() / 2f / PIXELS_TO_METERS);

        FixtureDef fixtureDefDonut = new FixtureDef();
        fixtureDefDonut.shape = shape;
        fixtureDefDonut.density = 0.1f;
        //fixtureDefDonut.restitution = 0f;

        body.createFixture(fixtureDefDonut);
        shape.dispose();

        //creating the middle lower platform, defined by ML
        BodyDef bodyDefMLPlatform = new BodyDef();
        bodyDefMLPlatform.type = BodyDef.BodyType.StaticBody;
        bodyDefMLPlatform.position.set((spriteML.getX() + spriteML.getWidth() / 2) /
                        PIXELS_TO_METERS,
                (spriteML.getY() + spriteML.getHeight() / 2) / PIXELS_TO_METERS);

        PolygonShape shapeML = new PolygonShape();
        shapeML.setAsBox(spriteML.getWidth() / 2.3f / PIXELS_TO_METERS,
                spriteML.getHeight() / 2.5f / PIXELS_TO_METERS);

        bodyML = world.createBody(bodyDefMLPlatform);

        FixtureDef fixtureDefMLPlatform = new FixtureDef();
        fixtureDefMLPlatform.shape = shapeML;
        fixtureDefMLPlatform.density = 0.1f;
        //spriteML = new Sprite(img3);
        //fixtureDefPlatform.restitution = 0.5f;

        bodyML.createFixture(fixtureDefMLPlatform);
        shapeML.dispose();

        //creating the right lower platform, defined by RL
        BodyDef bodyDefRLPlatform = new BodyDef();
        bodyDefRLPlatform.type = BodyDef.BodyType.StaticBody;
        bodyDefRLPlatform.position.set((spriteRL.getX() + spriteRL.getWidth() / 2) /
                        PIXELS_TO_METERS,
                (spriteRL.getY() + spriteRL.getHeight() / 2) / PIXELS_TO_METERS);

        PolygonShape shapeRL = new PolygonShape();
        shapeRL.setAsBox(spriteRL.getWidth() / 2.3f / PIXELS_TO_METERS,
                spriteRL.getHeight() / 2.5f / PIXELS_TO_METERS);

        bodyRL = world.createBody(bodyDefRLPlatform);

        FixtureDef fixtureDefRLPlatform = new FixtureDef();
        fixtureDefRLPlatform.shape = shapeRL;
        fixtureDefRLPlatform.density = 0.1f;
        //fixtureDefRLPlatform.restitution = 0.5f;

        bodyRL.createFixture(fixtureDefRLPlatform);
        shapeRL.dispose();

        //creating the left lower platform, defined by LL
        BodyDef bodyDefLLPlatform = new BodyDef();
        bodyDefLLPlatform.type = BodyDef.BodyType.StaticBody;
        bodyDefLLPlatform.position.set((spriteLL.getX() + spriteLL.getWidth() / 2) /
                        PIXELS_TO_METERS,
                (spriteLL.getY() + spriteLL.getHeight() / 2) / PIXELS_TO_METERS);

        PolygonShape shapeLL = new PolygonShape();
        shapeLL.setAsBox(spriteLL.getWidth() / 2.3f / PIXELS_TO_METERS,
                spriteLL.getHeight() / 2.5f / PIXELS_TO_METERS);

        bodyLL = world.createBody(bodyDefLLPlatform);

        FixtureDef fixtureDefLLPlatform = new FixtureDef();
        fixtureDefLLPlatform.shape = shapeLL;
        fixtureDefLLPlatform.density = 0.1f;
        //fixtureDefLLPlatform.restitution = 0.5f;

        bodyLL.createFixture(fixtureDefLLPlatform);
        shapeLL.dispose();


        //creating the middle upper platform, defined by MU
        BodyDef bodyDefMUPlatform = new BodyDef();
        bodyDefMUPlatform.type = BodyDef.BodyType.StaticBody;
        bodyDefMUPlatform.position.set((spriteMU.getX() + spriteMU.getWidth() / 2) /
                        PIXELS_TO_METERS,
                (spriteMU.getY() + spriteMU.getHeight() / 2) / PIXELS_TO_METERS);

        PolygonShape shapeMU = new PolygonShape();
        shapeMU.setAsBox(spriteMU.getWidth() / 2.3f / PIXELS_TO_METERS,
                spriteMU.getHeight() / 2.5f / PIXELS_TO_METERS);

        bodyMU = world.createBody(bodyDefMUPlatform);

        FixtureDef fixtureDefMUPlatform = new FixtureDef();
        fixtureDefMUPlatform.shape = shapeMU;
        fixtureDefMUPlatform.density = 0.1f;
        //fixtureDefMUPlatform.restitution = 0.5f;

        bodyMU.createFixture(fixtureDefMUPlatform);
        shapeMU.dispose();


        //creating the right upper platform, defined by RU
        BodyDef bodyDefRUPlatform = new BodyDef();
        bodyDefRUPlatform.type = BodyDef.BodyType.StaticBody;
        bodyDefRUPlatform.position.set((spriteRU.getX() + spriteRU.getWidth() / 2) /
                        PIXELS_TO_METERS,
                (spriteRU.getY() + spriteRU.getHeight() / 2) / PIXELS_TO_METERS);

        PolygonShape shapeRU = new PolygonShape();
        shapeRU.setAsBox(spriteRU.getWidth() / 2.3f / PIXELS_TO_METERS,
                spriteRU.getHeight() / 2.5f / PIXELS_TO_METERS);

        bodyRU = world.createBody(bodyDefRUPlatform);

        FixtureDef fixtureDefRUPlatform = new FixtureDef();
        fixtureDefRUPlatform.shape = shapeRU;
        fixtureDefRUPlatform.density = 0.1f;
        //fixtureDefRUPlatform.restitution = 0.5f;

        bodyRU.createFixture(fixtureDefRUPlatform);
        shapeRU.dispose();

        //creating the left upper platform, defined by LU
        BodyDef bodyDefLUPlatform = new BodyDef();
        bodyDefLUPlatform.type = BodyDef.BodyType.StaticBody;
        bodyDefLUPlatform.position.set((spriteLU.getX() + spriteLU.getWidth() / 2) /
                        PIXELS_TO_METERS,
                (spriteLU.getY() + spriteLU.getHeight() / 2) / PIXELS_TO_METERS);

        PolygonShape shapeLU = new PolygonShape();
        shapeLU.setAsBox(spriteLU.getWidth() / 2.3f / PIXELS_TO_METERS,
                spriteLU.getHeight() / 2.5f / PIXELS_TO_METERS);

        bodyLU = world.createBody(bodyDefLUPlatform);

        FixtureDef fixtureDefLUPlatform = new FixtureDef();
        fixtureDefLUPlatform.shape = shapeLU;
        fixtureDefLUPlatform.density = 0.1f;
        //fixtureDefLMPlatform.restitution = 0.5f;

        bodyLU.createFixture(fixtureDefLUPlatform);
        shapeLU.dispose();

        //end working on the platforms

        //start work on screen edges
        //bodyDefBase and edgeShapeBase work together they are bottom border
        BodyDef bodyDefBase = new BodyDef();
        bodyDefBase.type = BodyDef.BodyType.StaticBody;
        float wB = Gdx.graphics.getWidth() / PIXELS_TO_METERS;
        float hB = Gdx.graphics.getHeight() / PIXELS_TO_METERS - 1 / PIXELS_TO_METERS;
        bodyDefBase.position.set(0, 0);
        FixtureDef fixtureDefBase = new FixtureDef();

        EdgeShape edgeShapeBase = new EdgeShape();
        edgeShapeBase.set(-wB / 2, -hB / 2, wB / 2, -hB / 2);
        fixtureDefBase.shape = edgeShapeBase;

        bodyEdgeScreen = world.createBody(bodyDefBase);
        bodyEdgeScreen.createFixture(fixtureDefBase);
        edgeShapeBase.dispose();


        //bodyDefRight and edgeShapeRight work together they are right border
        BodyDef bodyDefRight = new BodyDef();
        //1.5708 radians is exactly 90 degrees
        //angle is defined by radians
        bodyDefRight.angle = 1.5708f;
        bodyDefRight.type = BodyDef.BodyType.StaticBody;
        float wRi = Gdx.graphics.getWidth() / PIXELS_TO_METERS;
        float hRi = Gdx.graphics.getHeight() / PIXELS_TO_METERS + 200 / PIXELS_TO_METERS;
        bodyDefRight.position.set(0, 0);
        FixtureDef fixtureDefRight = new FixtureDef();

        EdgeShape edgeShapeRight = new EdgeShape();
        edgeShapeRight.set(-wRi / 2, -hRi / 2, wRi / 2, -hRi / 2);
        fixtureDefRight.shape = edgeShapeRight;

        bodyEdgeScreen = world.createBody(bodyDefRight);
        bodyEdgeScreen.createFixture(fixtureDefRight);
        edgeShapeRight.dispose();


        //bodyDefLeft and edgeShapeLeft work together they are left border
        BodyDef bodyDefLeft = new BodyDef();
        //1.5708 radians is exactly 90 degrees
        //angle is defined by radians
        bodyDefLeft.angle = 1.5708f;
        bodyDefLeft.type = BodyDef.BodyType.StaticBody;
        float wL = Gdx.graphics.getWidth() / PIXELS_TO_METERS;
        // Set the height to just 25 pixels above the bottom of the screen so we can see the edge in the
        // debug renderer will need the 25 changed to 50 and turned on
        // 1600 is defining where the border is in location to the screen?
        float hL = Gdx.graphics.getHeight() / PIXELS_TO_METERS - 1600 / PIXELS_TO_METERS;
        bodyDefLeft.position.set(0, 0);
        FixtureDef fixtureDefLeft = new FixtureDef();

        EdgeShape edgeShapeLeft = new EdgeShape();
        edgeShapeLeft.set(-wL / 2, -hL / 2, wL / 2, -hL / 2);
        fixtureDefLeft.shape = edgeShapeLeft;

        bodyEdgeScreen = world.createBody(bodyDefLeft);
        bodyEdgeScreen.createFixture(fixtureDefLeft);
        edgeShapeLeft.dispose();

        //bodyDefRoof and edgeShapeRoof work together they are bottom border
        BodyDef bodyDefRoof = new BodyDef();
        bodyDefRoof.type = BodyDef.BodyType.StaticBody;
        float wR = Gdx.graphics.getWidth() / PIXELS_TO_METERS;
        //placed a little below the top of the screen so a score board and other info cant be overlapped by the donut
        float hR = Gdx.graphics.getHeight() / PIXELS_TO_METERS -1350 / PIXELS_TO_METERS;
        bodyDefRoof.position.set(0, 0);
        FixtureDef fixtureDefRoof = new FixtureDef();

        EdgeShape edgeShapeRoof = new EdgeShape();
        edgeShapeRoof.set(-wR / 2, -hR / 2, wR / 2, -hR / 2);
        fixtureDefRoof.shape = edgeShapeRoof;

        bodyEdgeScreen = world.createBody(bodyDefRoof);
        bodyEdgeScreen.createFixture(fixtureDefRoof);
        edgeShapeRoof.dispose();

        //end work on screen edges

        Gdx.input.setInputProcessor(this);

        debugRenderer = new Box2DDebugRenderer();
        font = new BitmapFont();
        font.setColor(Color.FIREBRICK);
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.
                getHeight());

        dropHands = new Texture(Gdx.files.internal("hand.png"));


        PolygonShape handShape = new PolygonShape();
        handShape.setAsBox(hand.getWidth(), hand.getHeight());
        // create the hands array and spawn the first hand
        hands = new Array<Rectangle>();
        spawnhand();
    }


    private void spawnhand() {
        Rectangle hand = new Rectangle();
        hand.x = MathUtils.random(-580 + 64, 820 - 64);
        hand.y = 700;
        hand.width = 64;
        hand.height = 64;
        hands.add(hand);
        lastDropTime = TimeUtils.nanoTime();
    }

    @Override
    public void render(float delta) {

        camera.update();

        // tell the SpriteBatch to render in the
        // coordinate system specified by the camera.
        batch.setProjectionMatrix(camera.combined);


        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(camera.combined);
        //debugMatrix = batch.getProjectionMatrix().cpy().scale(PIXELS_TO_METERS, PIXELS_TO_METERS, 0);

        //pause/resume
        if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            if (state == State.RUN) {
                setGameState(State.PAUSE);
            }
            else {
                setGameState(State.RUN);
            }
        }

        batch.begin();

        if (drawSprite) {
            batch.draw(spriteML, spriteML.getX(), spriteML.getY(), spriteML.getOriginX(),
                    spriteML.getOriginY(),
                    spriteML.getWidth(), spriteML.getHeight(), spriteML.getScaleX(), spriteML.
                            getScaleY(), spriteML.getRotation());

            batch.draw(spriteRL, spriteRL.getX(), spriteRL.getY(), spriteRL.getOriginX(),
                    spriteRL.getOriginY(),
                    spriteRL.getWidth(), spriteRL.getHeight(), spriteRL.getScaleX(), spriteRL.
                            getScaleY(), spriteRL.getRotation());

            batch.draw(spriteLL, spriteLL.getX(), spriteLL.getY(), spriteLL.getOriginX(),
                    spriteLL.getOriginY(),
                    spriteLL.getWidth(), spriteLL.getHeight(), spriteLL.getScaleX(), spriteLL.
                            getScaleY(), spriteLL.getRotation());

            batch.draw(spriteMU, spriteMU.getX(), spriteMU.getY(), spriteMU.getOriginX(),
                    spriteMU.getOriginY(),
                    spriteMU.getWidth(), spriteMU.getHeight(), spriteMU.getScaleX(), spriteMU.
                            getScaleY(), spriteMU.getRotation());

            batch.draw(spriteRU, spriteRU.getX(), spriteRU.getY(), spriteRU.getOriginX(),
                    spriteRU.getOriginY(),
                    spriteRU.getWidth(), spriteRU.getHeight(), spriteRU.getScaleX(), spriteRU.
                            getScaleY(), spriteRU.getRotation());

            batch.draw(spriteLU, spriteLU.getX(), spriteLU.getY(), spriteLU.getOriginX(),
                    spriteLU.getOriginY(),
                    spriteLU.getWidth(), spriteLU.getHeight(), spriteLU.getScaleX(), spriteLU.
                            getScaleY(), spriteLU.getRotation());

            for (Rectangle hand : hands) {
                batch.draw(dropHands, hand.x, hand.y);
            }

            batch.draw(sprite, sprite.getX(), sprite.getY(), sprite.getOriginX(),
                    sprite.getOriginY(),
                    sprite.getWidth(), sprite.getHeight(), sprite.getScaleX(), sprite.
                            getScaleY(), sprite.getRotation());
        }

        //This can be changed to be a scoring feature. Currently we do not have the muchkins or a collision
        //detection with the hands so a scoring feature is pointless unlit we implement these features.
        //for now this is used to help us adjust the donut's movements/speeds
        font.draw(batch,
                " Donut Demo: X_VELOCITY: " + body.getLinearVelocity().x + " Y_VELOCITY: " + body.getLinearVelocity().y,
                -Gdx.graphics.getWidth() / 2,
                Gdx.graphics.getHeight() / 2);
        batch.end();

        switch (state)
        {
            //if the state is RUN the game will continue to work without stopping
            case RUN:
                // Step the physics simulation forward at a rate of 60hz
                world.step(1f / 60f, 6, 2);

                sprite.setPosition((body.getPosition().x * PIXELS_TO_METERS) - sprite.
                                getWidth() / 2,
                        (body.getPosition().y * PIXELS_TO_METERS) - sprite.getHeight() / 2);

                sprite.setRotation((float) Math.toDegrees(body.getAngle()));

                //middle lower platform
                spriteML.setPosition((bodyML.getPosition().x * PIXELS_TO_METERS) - spriteML.
                                getWidth() / 2,
                        (bodyML.getPosition().y * PIXELS_TO_METERS) - spriteML.getHeight() / 2);

                spriteML.setRotation((float) Math.toDegrees(bodyML.getAngle()));

                //right lower platform
                spriteRL.setPosition((bodyRL.getPosition().x * PIXELS_TO_METERS) - spriteRL.
                                getWidth() / 2,
                        (bodyRL.getPosition().y * PIXELS_TO_METERS) - spriteLL.getHeight() / 2);

                spriteLL.setRotation((float) Math.toDegrees(bodyRL.getAngle()));

                //left lower platform
                spriteLL.setPosition((bodyLL.getPosition().x * PIXELS_TO_METERS) - spriteLL.
                                getWidth() / 2,
                        (bodyLL.getPosition().y * PIXELS_TO_METERS) - spriteLL.getHeight() / 2);

                spriteLL.setRotation((float) Math.toDegrees(bodyLL.getAngle()));

                // middle upper platform
                spriteMU.setPosition((bodyMU.getPosition().x * PIXELS_TO_METERS) - spriteMU.
                                getWidth() / 2,
                        (bodyMU.getPosition().y * PIXELS_TO_METERS) - spriteMU.getHeight() / 2);

                spriteMU.setRotation((float) Math.toDegrees(bodyMU.getAngle()));

                // right upper platform
                spriteRU.setPosition((bodyRU.getPosition().x * PIXELS_TO_METERS) - spriteRU.
                                getWidth() / 2,
                        (bodyRU.getPosition().y * PIXELS_TO_METERS) - spriteRU.getHeight() / 2);

                spriteRU.setRotation((float) Math.toDegrees(bodyRU.getAngle()));

                // left upper platform
                spriteLU.setPosition((bodyLU.getPosition().x * PIXELS_TO_METERS) - spriteLU.
                                getWidth() / 2,
                        (bodyLU.getPosition().y * PIXELS_TO_METERS) - spriteLU.getHeight() / 2);

                spriteLU.setRotation((float) Math.toDegrees(bodyLU.getAngle()));

                //set jump_counter to 0 if at rest vertically
                if (body.getLinearVelocity().y == 0) {
                    jump_counter = 0;
                }
                if (movingRight)
                    body.setLinearVelocity(2f, body.getLinearVelocity().y);

                if (movingLeft)
                    body.setLinearVelocity(-2f, body.getLinearVelocity().y);

                if (holdingDown)
                    body.setLinearVelocity(0f, body.getLinearVelocity().y);

                //debugRenderer.render(world, debugMatrix);


                // check if we need to create a new hand
                //1000000000 is the original time, increasing the number makes the hands spawn slower
                //needs to be adjusted still
                if (TimeUtils.nanoTime() - lastDropTime > 1000040000)
                    spawnhand();

                Iterator<Rectangle> iter = hands.iterator();
                while (iter.hasNext()) {
                    Rectangle hand = iter.next();
                    hand.y -= 200 * Gdx.graphics.getDeltaTime();
                    if (hand.y + 64 < -300)
                        iter.remove();
                    //current work on hands collisions
                    //if (hand.overlaps(sprite.getX(), sprite.getY()))
                    //	iter.remove();
                }
                break;
            case PAUSE:
                //is state is PAUSE, the game will pause and display "PAUSED"
                game.batch.begin();
                game.font.setColor(Color.BLUE);
                game.font.draw(game.batch, "PAUSED", 430 , 500);
                game.batch.end();
                break;
        }


    }


    @Override
    public boolean keyDown(int keycode) {
        //this allows the player to complete a double jump without jumping too high
        if(keycode == Input.Keys.UP) {
            if(jump_counter == 2){

            }
            else if(jump_counter == 1){
                if(body.getLinearVelocity().y < 0){
                    body.applyForceToCenter(0f, 12f, true);
                    jump_counter++;
                    twoJump.play();

                }
                else {
                    body.applyForceToCenter(0f, 7f, true);
                    jump_counter++;
                    twoJump.play();
                }
            }
            else {
                body.applyForceToCenter(0f, 10f, true);
                jump_counter++;

            }
        }
        //this will help the player stop the donut when it is on a platform or the ground
        if(keycode == Input.Keys.DOWN) {
            if(body.getLinearVelocity().y == 0) {
                holdingDown = true;
            }
            else
                holdingDown = false;
        }
        if(keycode == Input.Keys.RIGHT)
            movingRight = true;
        if(keycode == Input.Keys.LEFT)
            movingLeft = true;

        return true;
    }

    @Override
    public boolean keyUp(int keycode) {

        if(keycode == Input.Keys.RIGHT)
            movingRight = false;
        if(keycode == Input.Keys.LEFT)
            movingLeft = false;
        if(keycode == Input.Keys.DOWN)
            holdingDown = false;
        if(keycode == Input.Keys.ESCAPE || keycode == Input.Keys.NUM_1)
            drawSprite = !drawSprite;

        return true;
    }

    //Most of these are useless methods from here on, they are necessary to have to run

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        body.applyForce(1f,1f,screenX,screenY,true);
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }


    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void show() {
        // start the playback of the background music
        // when the screen is shown
        //Music.play();
    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {
        this.state = State.PAUSE;
    }

    @Override
    public void resume() {
        this.state = State.RUN;
    }

    public void setGameState(State s) {
        this.state = s;
    }

    @Override
    public void dispose() {
        img.dispose();
        img2.dispose();
        img3.dispose();
        world.dispose();
    }
}