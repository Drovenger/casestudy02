package geowars.collision;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.physics.CollisionHandler;
import javafx.geometry.Point2D;
import javafx.util.Duration;

import static geowars.GeoWarsType.CRYSTAL;
import static geowars.GeoWarsType.PLAYER;

public class PlayerCrystalHandler extends CollisionHandler {

    public PlayerCrystalHandler() {
        super(PLAYER, CRYSTAL);
    }

    @Override
    protected void onCollisionBegin(Entity player, Entity crystal) {

        crystal.getComponent(CollidableComponent.class).setValue(false);

        FXGL.inc("multiplier", +1);

        FXGL.animationBuilder()
                .duration(Duration.seconds(0.75))
                .onFinished(crystal::removeFromWorld)
                .interpolator(Interpolators.BACK.EASE_IN())
                .scale(crystal)
                .from(new Point2D(crystal.getScaleX(), crystal.getScaleY()))
                .to(new Point2D(0, 0))
                .buildAndPlay();
    }
}

