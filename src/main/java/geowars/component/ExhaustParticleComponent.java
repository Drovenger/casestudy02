package geowars.component;

import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.particle.ParticleComponent;
import com.almasb.fxgl.particle.ParticleEmitter;
import javafx.geometry.Point2D;
import javafx.scene.effect.BlendMode;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.random;
import static com.almasb.fxgl.dsl.FXGL.texture;

public class ExhaustParticleComponent extends ParticleComponent {

    private PlayerComponent playerComponent;

    private double t = 0.0;
    private boolean up = true;

    public ExhaustParticleComponent(ParticleEmitter emitter) {
        super(emitter);
    }

    @Override
    public void onAdded() {
        var emitter = getEmitter();
        emitter.setMaxEmissions(Integer.MAX_VALUE);
        emitter.setNumParticles(2);
        emitter.setEmissionRate(1);
        emitter.setBlendMode(BlendMode.ADD);
        emitter.setSourceImage(texture("particles/spark_04.png", 32, 32).multiplyColor(Color.BLUEVIOLET));
        emitter.setAllowParticleRotation(true);
        emitter.setSize(2, 24);
        emitter.setScaleFunction(i -> FXGLMath.randomPoint2D().multiply(0.01));
        emitter.setExpireFunction(i -> Duration.seconds(random(0.25, 1.25)));
        emitter.setAccelerationFunction(() -> Point2D.ZERO);

        emitter.setSpawnPointFunction(i -> {
            Vec2 offset = Vec2.fromAngle(playerComponent.getEntity().getRotation());

            return offset.mulLocal(-15).toPoint2D();
        });
        emitter.setVelocityFunction(i -> {
            var direction = Vec2.fromAngle(playerComponent.getEntity().getRotation());
            return direction.normalizeLocal().negateLocal().toPoint2D().multiply(10);
        });

        emitter.setControl(p -> {
            var x = p.position.x;
            var y = p.position.y;

            var noiseValue = FXGLMath.noise2D(x * 0.02 * t, y * 0.02 * t);
            var angle = FXGLMath.toDegrees((noiseValue + 1) * Math.PI * 1.5);

            angle %= 360.0;

            var v = Vec2.fromAngle(angle).normalizeLocal().mulLocal(FXGLMath.random(1.0, 15));

            var vx = p.velocity.x * 0.9f + v.x * 0.1f;
            var vy = p.velocity.y * 0.9f + v.y * 0.1f;

            p.velocity.x = vx;
            p.velocity.y = vy;
        });
    }

    @Override
    public void onUpdate(double tpf) {
        super.onUpdate(tpf);

        if (up) {
            t += tpf;
        } else {
            t -= tpf;
        }

        if (t > 7) {
            up = false;
        }

        if (t < 1) {
            up = true;
        }
    }
}
