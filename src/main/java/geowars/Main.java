package geowars;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.physics.PhysicsWorld;
import geowars.collision.PlayerCrystalHandler;
import geowars.component.HealthComponent;
import geowars.component.PlayerComponent;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.geometry.Point2D;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.Map;
import java.util.function.Supplier;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.almasb.fxgl.dsl.FXGLForKtKt.getInput;
import static geowars.GeoWarsType.*;

public class Main extends GameApplication {

    private Entity player;
    private PlayerComponent playerComponent;

    public Entity getPlayer() {
        return player;
    }

    @Override
    protected void initSettings(GameSettings settings) {
        var isRelease = false;

        settings.setWidth(1280);
        settings.setHeight(720);
        settings.setTitle("FXGL Geometry Wars");
        settings.setVersion("1.1.0");
        settings.setIntroEnabled(isRelease);
        settings.setMainMenuEnabled(isRelease);
        settings.setConfigClass(GeoWarsConfig.class);
        settings.setExperimentalNative(false);
        settings.setManualResizeEnabled(true);
        settings.setApplicationMode(isRelease ? ApplicationMode.RELEASE : ApplicationMode.DEVELOPER);

        if (!settings.isExperimentalNative()) {
            settings.setFontUI("game_font_7.ttf");
        }
    }

    @Override
    protected void onPreInit() {
        // preload explosion sprite sheet
        getAssetLoader().loadTexture("explosion.png", 80 * 48, 80);

        if (!getSettings().isExperimentalNative()) {
            getSettings().setGlobalSoundVolume(0.2);
            getSettings().setGlobalMusicVolume(0.2);

            loopBGM("bgm.mp3");
        }
    }

    @Override
    protected void initInput() {
        onKey(KeyCode.W, () -> playerComponent.up());
        onKey(KeyCode.A, () -> playerComponent.left());
        onKey(KeyCode.S, () -> playerComponent.down());
        onKey(KeyCode.D, () -> playerComponent.right());

        onBtn(MouseButton.PRIMARY, () -> playerComponent.shoot(getInput().getMousePositionWorld()));
    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        vars.put("score", 70000);
        vars.put("multiplier", 1);
        vars.put("kills", 0);
        vars.put("lives", 300);
        vars.put("weaponType", WeaponType.SINGLE);
    }

    @Override
    protected void initGame() {
        getGameWorld().addEntityFactory(new GeoWarsFactory());

        getGameScene().setBackgroundColor(Color.BLACK);

        spawn("Background");
        player = spawn("Player");
        playerComponent = player.getComponent(PlayerComponent.class);

        int dist = 200;

        getGameScene().getViewport().setBounds(-dist, -dist, getAppWidth() + dist, getAppHeight() + dist);
        getGameScene().getViewport().bindToEntity(player, getAppWidth() / 2, getAppHeight() / 2);

        getWorldProperties().<Integer>addListener("multiplier", (prev, now) -> {
            WeaponType current = geto("weaponType");
            WeaponType newType = WeaponType.fromMultiplier(geti("multiplier"));

            if (newType.isBetterThan(current)) {
                set("weaponType", newType);
            }
        });

        getWorldProperties().<Integer>addListener("lives", (prev, now) -> {
            if (now == 0)
                getDialogService().showMessageBox("Demo Over. Your score: " + geti("score"), getGameController()::exit);
        });

        eventBuilder()
                .when((Supplier<Boolean>) () -> geti("score") >= 10000)
                .thenFire((Supplier<Event>) () -> {
                    run(() -> spawn("Bouncer"), Duration.seconds(5));
                    return new Event(EventType.ROOT);
                })
                .buildAndStart();

        eventBuilder()
                .when((Supplier<Boolean>) () -> geti("score") >= 50000)
                .thenFire((Supplier<Event>) () -> {
                    run(() -> spawn("Seeker"), Duration.seconds(2));
                    return new Event(EventType.ROOT);
                })
                .buildAndStart();

        eventBuilder()
                .when((Supplier<Boolean>) () -> geti("score") >= 70000)
                .thenFire((Supplier<Event>) () -> {
                    run(() -> spawn("Runner"), Duration.seconds(3));
                    return new Event(EventType.ROOT);
                })
                .buildAndStart();

        run(() -> spawn("Wanderer"), Duration.seconds(1.5));
    }

    @Override
    protected void initPhysics() {
        PhysicsWorld physics = getPhysicsWorld();

        CollisionHandler bulletEnemy = new CollisionHandler(BULLET, WANDERER) {
            @Override
            protected void onCollisionBegin(Entity bullet, Entity enemy) {
                bullet.removeFromWorld();

                HealthComponent hp = enemy.getComponent(HealthComponent.class);
                hp.setValue(hp.getValue() - 1);

                if (hp.getValue() == 0) {
                    onDeath(enemy);
                    enemy.removeFromWorld();
                }
            }
        };

        physics.addCollisionHandler(bulletEnemy);
        physics.addCollisionHandler(bulletEnemy.copyFor(BULLET, SEEKER));
        physics.addCollisionHandler(bulletEnemy.copyFor(BULLET, RUNNER));
        physics.addCollisionHandler(bulletEnemy.copyFor(BULLET, BOUNCER));
        physics.addCollisionHandler(new PlayerCrystalHandler());

        CollisionHandler playerEnemy = new CollisionHandler(PLAYER, WANDERER) {
            @Override
            protected void onCollisionBegin(Entity a, Entity b) {

                getGameScene().getViewport().shakeTranslational(8);

                a.setPosition(getRandomPoint());
                b.removeFromWorld();
                deductScoreDeath();
            }
        };

        physics.addCollisionHandler(playerEnemy);
        physics.addCollisionHandler(playerEnemy.copyFor(PLAYER, SEEKER));
        physics.addCollisionHandler(playerEnemy.copyFor(PLAYER, RUNNER));
        physics.addCollisionHandler(playerEnemy.copyFor(PLAYER, BOUNCER));
    }

    @Override
    protected void initUI() {
        Text scoreText = getUIFactoryService().newText("", Color.WHITE, 28);
        scoreText.setTranslateX(60);
        scoreText.setTranslateY(70);
        scoreText.textProperty().bind(getip("score").asString());
        scoreText.setStroke(Color.GOLD);

        Text multText = getUIFactoryService().newText("", Color.WHITE, 28);
        multText.setTranslateX(60);
        multText.setTranslateY(90);
        multText.textProperty().bind(getip("multiplier").asString("x %d"));

        var livesText = getUIFactoryService().newText("", Color.WHITE, 24.0);
        livesText.setTranslateX(60);
        livesText.setTranslateY(110);
        livesText.textProperty().bind(getip("lives").asString("Lives: %d"));

        getGameScene().addUINodes(multText, scoreText, livesText);

        Text beware = getUIFactoryService().newText("Beware! Seekers get smarter every spawn!", Color.AQUA, 38);

        addUINode(beware);

        centerText(beware);

        animationBuilder()
                .duration(Duration.seconds(2))
                .autoReverse(true)
                .repeat(2)
                .fadeIn(beware)
                .buildAndPlay();
    }

    private Point2D getRandomPoint() {
        return new Point2D(Math.random() * getAppWidth(), Math.random() * getAppHeight());
    }

    private void addScoreKill(Point2D enemyPosition) {
        inc("kills", +1);

        if (geti("kills") == 15) {
            set("kills", 0);
            inc("multiplier", +1);
        }

        final int multiplier = geti("multiplier");

        inc("score", +100*multiplier);

        var shadow = new DropShadow(25, Color.WHITE);

        Text bonusText = getUIFactoryService().newText("+100" + (multiplier == 1 ? "" : "x" + multiplier), Color.color(1, 1, 1, 0.8), 24);
        bonusText.setStroke(Color.GOLD);
        bonusText.setEffect(shadow);

        var e = entityBuilder()
                .at(enemyPosition)
                .view(bonusText)
                .buildAndAttach();

        animationBuilder()
                .onFinished(() -> e.removeFromWorld())
                .interpolator(Interpolators.EXPONENTIAL.EASE_OUT())
                .translate(e)
                .from(enemyPosition)
                .to(enemyPosition.subtract(0, 65))
                .buildAndPlay();

        animationBuilder()
                .duration(Duration.seconds(0.35))
                .autoReverse(true)
                .repeat(2)
                .interpolator(Interpolators.BOUNCE.EASE_IN())
                .scale(e)
                .from(new Point2D(1, 1))
                .to(new Point2D(1.2, 0.85))
                .buildAndPlay();
    }

    private void deductScoreDeath() {
        inc("lives", -1);
        inc("score", -1000);
        set("kills", 0);
        set("multiplier", 1);

        Text bonusText = getUIFactoryService().newText("-1000", Color.WHITE, 20);

        addUINode(bonusText, 1100, 70);

        animationBuilder()
                .duration(Duration.seconds(0.5))
                .onFinished(() -> {
                    removeUINode(bonusText);
                })
                .interpolator(Interpolators.EXPONENTIAL.EASE_IN())
                .translate(bonusText)
                .from(new Point2D(bonusText.getTranslateX(), bonusText.getTranslateY()))
                .to(new Point2D(bonusText.getTranslateX(), 0))
                .buildAndPlay();
    }

    public void onDeath(Entity entity) {
        spawn("Explosion", entity.getCenter());
        spawn("Crystal", entity.getCenter());

        addScoreKill(entity.getCenter());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
