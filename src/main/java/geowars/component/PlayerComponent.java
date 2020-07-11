package geowars.component;

import com.almasb.fxgl.entity.component.Component;

public class PlayerComponent extends Component {
    private int playerSpeed;
    private double speed;


    public PlayerComponent(int playerSpeed) {
        this.playerSpeed = playerSpeed;
    }

    public void left() {
        entity.translateX(-speed);
    }

    public void right() {
        entity.translateX(speed);
    }

    public void up() {
        entity.translateY(-speed);
    }

    public void down() {
        entity.translateY(speed);
    }
}
