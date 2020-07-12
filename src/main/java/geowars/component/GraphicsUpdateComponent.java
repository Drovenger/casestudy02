package geowars.component;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.AccumulatedUpdateComponent;
import javafx.scene.canvas.GraphicsContext;

public class GraphicsUpdateComponent extends AccumulatedUpdateComponent {

    private GraphicsContext g;

    public GraphicsUpdateComponent(GraphicsContext g) {
        // skip 3 frames (update every 4th frame)
        super(3);

        this.g = g;
    }

    @Override
    public void onAccumulatedUpdate(double tpfSum) {
        g.clearRect(0, 0, FXGL.getAppWidth(), FXGL.getAppHeight());
    }
}
