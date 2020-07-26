package geowars.component.enemy;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.Texture;
import com.almasb.fxgl.time.LocalTimer;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class SeekerComponent extends Component {

    // in seconds
    private static double seekerAdjustDelay = 5;

    private Point2D velocity = Point2D.ZERO;
    private Entity player;
    private Entity seeker;

    private LocalTimer adjustDirectionTimer = FXGL.newLocalTimer();
    private Duration adjustDelay = Duration.seconds(seekerAdjustDelay);

    private LocalTimer swapTexturesTimer = FXGL.newLocalTimer();
    private Duration swapTexturesDelay = Duration.seconds(0.2);

    private Texture overlay;
    private Texture texture;
    private Texture saturatedTexture;

    private int moveSpeed;

    public SeekerComponent(Entity player, int moveSpeed, Texture texture,Texture overlay) {
        this.player = player;
        this.moveSpeed = moveSpeed;
        this.texture = texture;
        saturatedTexture = overlay.toColor(Color.RED);

        if (seekerAdjustDelay > 0) {
            seekerAdjustDelay -= 0.15;
            if (seekerAdjustDelay < 0) {
                seekerAdjustDelay = 0;
            }
        }
    }

    @Override
    public void onAdded() {
        seeker = entity;
        adjustVelocity(0.016);

        overlay = FXGL.texture("Seeker_overlay.png", 60, 60).toColor(Color.WHITE);
    }

    @Override
    public void onUpdate(double tpf) {
        move(tpf);
        rotate();

        if (swapTexturesTimer.elapsed(swapTexturesDelay)) {
            swapTextures();
            swapTexturesTimer.capture();
        }
    }

    private void move(double tpf) {
        if (adjustDirectionTimer.elapsed(adjustDelay)) {
            adjustVelocity(tpf);
            adjustDirectionTimer.capture();
        }

        seeker.translate(velocity);
    }

    private void adjustVelocity(double tpf) {
        Point2D directionToPlayer = player.getCenter()
                .subtract(seeker.getCenter())
                .normalize()
                .multiply(moveSpeed);

        velocity = velocity.add(directionToPlayer).multiply(tpf);
    }

    private void rotate() {
        if (!velocity.equals(Point2D.ZERO)) {
            seeker.rotateToVector(velocity);
        }
    }

    private void swapTextures() {
        if (overlay.getScene() == null) {
            entity.getViewComponent().addChild(overlay);
        } else {
            entity.getViewComponent().removeChild(overlay);
        }
    }
}
