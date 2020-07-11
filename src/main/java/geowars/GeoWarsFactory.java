package geowars;

import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.dsl.components.RandomMoveComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;

import static com.almasb.fxgl.dsl.FXGL.*;
import static geowars.GeoWarsType.PLAYER;
import static geowars.GeoWarsType.WANDERER;

public class GeoWarsFactory implements EntityFactory {
    //    private final GeoWarsConfig config;
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

    @Spawns("Player")
    public Entity spawnPlayer(SpawnData data) {
        return entityBuilder()
                .type(PLAYER)
                .at(getAppWidth() / 2, getAppHeight() / 2)
                .viewWithBBox("Player.png")
                .build();
    }

    @Spawns("Wanderer")
    public Entity spawnWanderer(SpawnData data) {
//        boolean red = FXGLMath.randomBoolean((float) config.getRedEnemyChance());
        var t = texture("Wanderer.png", 80, 80).brighter();
        return entityBuilder()
                .type(WANDERER)
                .at(getRandomSpawnPoint())
                .bbox(new HitBox(new Point2D(20, 20), BoundingShape.box(40, 40)))
                .view(t)
                //.with(new HealthComponent(red ? config.getRedEnemyHealth() : config.getEnemyHealth()))
                .with(new CollidableComponent(true))
                //.with(new WandererComponent(moveSpeed, t, texture("wanderer_overlay.png", 80, 80)))
                .build();
    }
}
