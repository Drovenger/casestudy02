package geowars;

import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.*;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.particle.ParticleEmitters;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import geowars.component.*;
//import geowars.component.enemy.BouncerComponent;
//import geowars.component.enemy.NewRunnerComponent;
//import geowars.component.enemy.SeekerComponent;
import geowars.component.enemy.WandererComponent;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.*;
import static geowars.GeoWarsType.*;

public class GeoWarsFactory implements EntityFactory {
    private final GeoWarsConfig config;

    public GeoWarsFactory() {
        config = new GeoWarsConfig();
    }

    private static final int SPAWN_DISTANCE = 50;
    /**
     * These correspond to top-left, top-right, bottom-right, bottom-left.
     */
    private Point2D[] spawnPoints = new Point2D[]{
            new Point2D(SPAWN_DISTANCE, SPAWN_DISTANCE),
            new Point2D(getAppWidth() - SPAWN_DISTANCE, SPAWN_DISTANCE),
            new Point2D(getAppWidth() - SPAWN_DISTANCE, getAppHeight() - SPAWN_DISTANCE),
            new Point2D(SPAWN_DISTANCE, getAppHeight() - SPAWN_DISTANCE)
    };

    private Point2D getRandomSpawnPoint() {
        return spawnPoints[FXGLMath.random(0, 3)];
    }

    @Spawns("Background")
    public Entity spawnBackground(SpawnData data) {
        Canvas canvas = new Canvas(getAppWidth(), getAppHeight());
        canvas.getGraphicsContext2D().setStroke(new Color(0.138, 0.138, 0.375, 0.56));

        return entityBuilder()
//                .type(GRID)
                .from(data)
                .view(canvas)
//                .with(new GraphicsUpdateComponent(canvas.getGraphicsContext2D()))
//                .with(new GridComponent(canvas.getGraphicsContext2D()))
                .build();
    }

    @Spawns("Player")
    public Entity spawnPlayer(SpawnData data) {
        var e = entityBuilder()
                .type(PLAYER)
                .at(getAppWidth() / 2, getAppHeight() / 2)
                .viewWithBBox("Player.png")
                .collidable()
                .with(new KeepOnScreenComponent().bothAxes())
                .with(new PlayerComponent(config.getPlayerSpeed()))
                .build();
        return e;
    }

    @Spawns("Bullet")
    public Entity spawnBullet(SpawnData data) {
        if (!getSettings().isExperimentalNative()) {
            play("shoot" + (int) (Math.random() * 8 + 1) + ".wav");
        }

        return entityBuilder(data)
                .type(BULLET)
                .viewWithBBox("Bullet.png")
                .with(new CollidableComponent(true))
                .with(new ProjectileComponent(data.get("direction"), 1200))
                .with(new BulletComponent())
                .with(new OffscreenCleanComponent())
                .build();
    }

    @Spawns("Wanderer")
    public Entity spawnWanderer(SpawnData data) {
        boolean red = FXGLMath.randomBoolean((float) config.getRedEnemyChance());

        int moveSpeed = red ? config.getRedEnemyMoveSpeed()
                : FXGLMath.random(100, config.getWandererMaxMoveSpeed());

        var t = texture(red ? "RedWanderer.png" : "Wanderer.png", 80, 80).brighter();

        return entityBuilder()
                .type(WANDERER)
                .at(getRandomSpawnPoint())
                .bbox(new HitBox(new Point2D(20, 20), BoundingShape.box(40, 40)))
                .view(t)
                .with(new HealthComponent(red ? config.getRedEnemyHealth() : config.getEnemyHealth()))
                .with(new CollidableComponent(true))
                .with(new WandererComponent(moveSpeed, t, texture("wanderer_overlay.png", 80, 80)))
                .build();
    }
}
