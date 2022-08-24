package com.nitesh.coinman.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;


import java.util.ArrayList;
import java.util.Random;

public class CoinMan extends ApplicationAdapter {
    SpriteBatch batch;
    Texture background;
    Texture[] man;
    int manState = 0;
    int pause = 0;
    float gravity = 0.2f;
    float velocity = 0;
    int manY = 0;
    Random random, rand;
    Rectangle manRectangle;
    int score = 0;
    int Max_Score;
    //Score
    BitmapFont font, max_score;
    int gameState = 0;

    //gameover state man
    Texture game_over_man;
    int game_over_pause = 0;

    // getting coins while playing
    ArrayList<Integer> coinXs = new ArrayList<Integer>();
    ArrayList<Integer> coinYs = new ArrayList<Integer>();
    //making coin area as rectangle
    ArrayList<Rectangle> coinRectangle = new ArrayList<>();
    Texture coin;
    int coinCount;

    //getting bombs while playing
    ArrayList<Integer> bombXs = new ArrayList<Integer>();
    ArrayList<Integer> bombYs = new ArrayList<Integer>();
    //making coin area as rectangle
    ArrayList<Rectangle> bombRectangle = new ArrayList<>();
    Texture bomb;
    int bombCount;

    //Setting sounds
    Music coin_music;
    Music bomb_music;

    Sound coin_sound;
    Sound bomb_sound;
    Sound touch_sound;


    @Override
    public void create() {
        batch = new SpriteBatch();
        background = new Texture("bg.png");
        man = new Texture[4];
        man[0] = new Texture("frame-1.png");
        man[1] = new Texture("frame-2.png");
        man[2] = new Texture("frame-3.png");
        man[3] = new Texture("frame-4.png");

        manY = Gdx.graphics.getHeight() / 2;

        coin = new Texture("coin.png");
        bomb = new Texture("bomb.png");
        game_over_man = new Texture("dizzy-1.png");

        random = new Random();
        rand = new Random();

        //Setting Score design
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(10);

        //Setting MAX score
        max_score = new BitmapFont();
        max_score.setColor(Color.WHITE);
        max_score.getData().setScale(5);

        //Touch Audio
        coin_music = Gdx.audio.newMusic(Gdx.files.internal("smb_coin.mp3"));
        bomb_music = Gdx.audio.newMusic(Gdx.files.internal("smb_bump.mp3"));


        coin_sound = Gdx.audio.newSound(Gdx.files.internal("smb_coin.mp3"));
        bomb_sound = Gdx.audio.newSound(Gdx.files.internal("smb_bump.mp3"));
        touch_sound = Gdx.audio.newSound(Gdx.files.internal("touch_sound.mp3"));

        //Setting High Score
        check_hi_score();


    }

    public void makeCoin() {
        float Height = random.nextFloat() * Gdx.graphics.getHeight();
        coinYs.add((int) Height);
        coinXs.add(Gdx.graphics.getWidth());
    }

    public void getBomb() {
        float hight = rand.nextFloat() * Gdx.graphics.getHeight();
        bombYs.add((int) hight);
        bombXs.add(Gdx.graphics.getWidth());
    }

    @Override
    public void render() {
        batch.begin();
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        //Checking game state
        if (gameState == 1) {
            //GAME IS ALIVE

            //getting bombs
            if (bombCount < 250) {
                bombCount++;
            } else {
                bombCount = 0;
                getBomb();
            }
            bombRectangle.clear();
            for (int i = 0; i < bombYs.size(); i++) {
                batch.draw(bomb, bombXs.get(i), bombYs.get(i));
                bombXs.set(i, bombXs.get(i) - 16);
                bombRectangle.add(new Rectangle(bombXs.get(i), bombYs.get(i), bomb.getWidth(), bomb.getHeight()));
            }

            //Getting coins
            if (coinCount < 100) {
                coinCount++;
            } else {
                coinCount = 0;
                makeCoin();
            }

            coinRectangle.clear();
            for (int i = 0; i < coinYs.size(); i++) {
                batch.draw(coin, coinXs.get(i), coinYs.get(i));
                coinXs.set(i, coinXs.get(i) - 8);
                coinRectangle.add(new Rectangle(coinXs.get(i), coinYs.get(i), coin.getWidth(), coin.getHeight()));
            }


            if (Gdx.input.justTouched()) {
                velocity = -13;
                touch_sound.play(1.0f);
            }

            if (pause < 2) {
                pause++;
            } else {
                pause = 0;
                if (manState < 3) {
                    manState++;
                } else {
                    manState = 0;
                }
            }

            velocity += gravity;
            manY -= velocity;
            if (manY <= 0) {
                manY = 0;
            }


        } else if (gameState == 0) {
            //Waiting to start
            if (Gdx.input.justTouched()) {
                gameState = 1;
                touch_sound.play(1.0f);
            }

        } else if (gameState == 2) {
            //GAME OVER
            if (game_over_pause < 1) {
                game_over_pause++;
            } else {
                game_over_pause = 0;
                if (Gdx.input.justTouched()) {
                    touch_sound.play();
                    gameState = 1;
                    manY = Gdx.graphics.getHeight() / 2;
                    score = 0;
                    velocity = 0;
                    coinXs.clear();
                    coinYs.clear();
                    bombXs.clear();
                    bombYs.clear();
                    coinRectangle.clear();
                    coinCount = 0;
                    bombRectangle.clear();
                    bombCount = 0;
                }
            }

        }

        if (gameState == 2) {
            batch.draw(game_over_man, Gdx.graphics.getWidth() / 2 - game_over_man.getWidth() / 2, manY);
        } else {
            batch.draw(man[manState], Gdx.graphics.getWidth() / 2 - man[manState].getWidth() / 2, manY);
        }


        //making man area as rectangle
        manRectangle = new Rectangle(Gdx.graphics.getWidth() / 2 - man[manState].getWidth() / 2, manY, man[manState].getWidth(), man[manState].getHeight());

        //trying to know whether the man has touched the coin or not
        for (int i = 0; i < coinRectangle.size(); i++) {
            //checks whether the man and coin has overlapped?
            if (Intersector.overlaps(manRectangle, coinRectangle.get(i))) {
                score++;
                if(Max_Score<=score){
                    Max_Score = score;
                }

                setMax_score(Max_Score);
                coin_sound.play(1.0f);
                coinRectangle.remove(i);
                coinXs.remove(i);
                coinYs.remove(i);

                break;

            }
        }


        //trying to know whether the man has touched the bomb or not
        for (int i = 0; i < bombRectangle.size(); i++) {
            //checks whether the man and bomb has overlapped?
            if (Intersector.overlaps(manRectangle, bombRectangle.get(i))) {
                Gdx.app.log("Bomb!", "collision!");
                gameState = 2;
                bomb_sound.play(1.0f);
                bombRectangle.remove(i);
                bombXs.remove(i);
                bombYs.remove(i);

            }

        }

        //Displaying Score
        font.draw(batch, String.valueOf(score), 100, 200);
        max_score.draw(batch, String.valueOf("HI : " + Max_Score), 850, 170);

        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();

    }
    public void setMax_score(int HI_SCORE){
        Preferences preferences = Gdx.app.getPreferences("HI-SCORE");
        preferences.putInteger("HI_SCORE",HI_SCORE);
        preferences.flush();
    }

    public void check_hi_score(){
        Preferences preferences = Gdx.app.getPreferences("HI-SCORE");
        int hi =preferences.getInteger("HI_SCORE",0);
        if(hi>0){
            Max_Score=hi;
        }
    }



}
