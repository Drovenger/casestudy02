package geowars;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.physics.PhysicsWorld;
//import geowars.collision.BulletPortalHandler;
//import geowars.collision.PlayerCrystalHandler;
//import geowars.component.HealthComponent;
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
    protected void initInput() {
        onKey(KeyCode.W, () -> playerComponent.up());
        onKey(KeyCode.A, () -> playerComponent.left());
        onKey(KeyCode.S, () -> playerComponent.down());
        onKey(KeyCode.D, () -> playerComponent.right());

        onBtn(MouseButton.PRIMARY, () -> playerComponent.shoot(getInput().getMousePositionWorld()));
    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        vars.put("score", 0);
        vars.put("multiplier", 1);
        vars.put("kills", 0);
        vars.put("lives", 3);
        vars.put("weaponType", WeaponType.SINGLE);
    }

    @Override
    protected void initGame() {
        getGameWorld().addEntityFactory(new GeoWarsFactory());

        getGameScene().setBackgroundColor(Color.BLACK);

        spawn("Background");
        player = spawn("Player");
        playerComponent = player.getComponent(PlayerComponent.class);

        int dist = 100;

        getGameScene().getViewport().setBounds(-dist, -dist, getAppWidth() * 2 + dist, getAppHeight() * 2 + dist);
        getGameScene().getViewport().bindToEntity(player, getAppWidth() - dist, getAppHeight() - dist);

        getWorldProperties().<Integer>addListener("multiplier", (prev, now) -> {
            WeaponType current = geto("weaponType");
            WeaponType newType = WeaponType.fromMultiplier(geti("multiplier"));

            if (newType.isBetterThan(current)) {
                set("weaponType", newType);
            }
        });
        run(() -> spawn("Wanderer"), Duration.seconds(1));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
