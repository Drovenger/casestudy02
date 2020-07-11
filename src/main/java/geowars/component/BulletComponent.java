package geowars.component;

import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.dsl.components.ExpireCleanComponent;
import com.almasb.fxgl.dsl.components.ProjectileComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.components.BoundingBoxComponent;
import geowars.GeoWarsType;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.*;


public class BulletComponent extends Component {

    private static final Color PARTICLE_COLOR = Color.YELLOW.brighter();
    private static final Duration PARTICLE_DURATION = Duration.seconds(1.2);

    static {
        //ExhaustParticleComponent.colorImage(PARTICLE_COLOR);
    }

    private BoundingBoxComponent bbox;

    private Point2D velocity;

    private Entity lastPortal = null;

    public Entity getLastPortal() {
        return lastPortal;
    }

    public void setLastPortal(Entity lastPortal) {
        this.lastPortal = lastPortal;
    }

    @Override
    public void onAdded() {
        velocity = entity.getComponent(ProjectileComponent.class).getVelocity();
    }

    @Override
    public void onUpdate(double tpf) {
//        byType(GeoWarsType.GRID).forEach(g -> {
//            var grid = g.getComponent(GridComponent.class);
//            grid.applyExplosiveForce(velocity.magnitude() / 60 * 18, bbox.getCenterWorld(), 80 * 60 * tpf);
//        });

        if (bbox.getMinXWorld() < 0) {
            spawnParticles(0, bbox.getCenterWorld().getY(), 1, FXGLMath.random(-1.0f, 1.0f));

        } else if (bbox.getMaxXWorld() > getAppWidth()) {
            spawnParticles(getAppWidth(), bbox.getCenterWorld().getY(), -1, FXGLMath.random(-1.0f, 1.0f));

        } else if (bbox.getMinYWorld() < 0) {
            spawnParticles(bbox.getCenterWorld().getX(), 0, FXGLMath.random(-1.0f, 1.0f), 1);

        } else if (bbox.getMaxYWorld() > getAppHeight()) {
            spawnParticles(bbox.getCenterWorld().getX(), getAppHeight(), FXGLMath.random(-1.0f, 1.0f), -1);
        }
    }

    private void spawnParticles(double x, double y, double dirX, double dirY) {
        entityBuilder()
                .at(x, y)
                //.view(new Texture(ExhaustParticleComponent.coloredImages.get(PARTICLE_COLOR)))
                .with(new ProjectileComponent(new Point2D(dirX, dirY), FXGLMath.random(150, 280)))
                .with(new ExpireCleanComponent(PARTICLE_DURATION))
                .with(new ParticleControl())
                .buildAndAttach();
    }

    private static class ParticleControl extends Component {
        @Override
        public void onUpdate(double tpf) {
            ProjectileComponent control = entity.getComponent(ProjectileComponent.class);
            control.setSpeed(control.getSpeed() * (1 - 3 * tpf));
        }
    }
}

