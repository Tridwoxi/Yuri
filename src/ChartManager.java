import java.util.ArrayList;
import javafx.geometry.Pos;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;

/**
    A {@code ChartManager} is the high-level interface for managing charts. Calling `distribute_timestep` will have it tell its children to poll the messenger and figure out what they're supposed to look like.

    A {@code Chart} is a graphic used for displaying either statistical power (the chance that if there was a note, you hit it) or significance (the chance that if you hit, you hit a note and not thin air). It consists of a background, partial cover, outline, and label.
 */
public class ChartManager extends HBox {
    private double CHART_SPACING = 20.0;
    private Color BACKGROUND_COLOR = Nord.NIGHT_DARKEST;
    private double WIDTH = 200.0;

    private ArrayList<Chart> charts;

    public ChartManager() {
        setAlignment(Pos.CENTER);
        setPrefWidth(WIDTH);
        setBackground(Background.fill(BACKGROUND_COLOR));

        charts = new ArrayList<>();
        for (Mini.ChartType chart_type : Mini.ChartType.values()) {
            charts.add(new Chart(chart_type));
            getChildren().add(charts.getLast());
            getChildren().add(new Mini.Spacer(CHART_SPACING, 0.0));
        }
        getChildren().removeLast();
    }

    /** Informs all chart children to update themselves */
    public void distribute_timestep() {
        for (Chart chart : charts) {
            chart.handle_timestep();
        }
    }
}

class Chart extends StackPane {
    private static double CHART_HEIGHT = 300.0;
    private static double CHART_WIDTH = 30.0;
    private static double FONT_SIZE = 10.0;
    private static double BACKGROUND_CONTRAST = 0.75;

    private Mini.ChartType chart_type;
    private Rectangle cover;

    public Chart(Mini.ChartType chart_type) {
        this.chart_type = chart_type;

        getChildren().addAll(
                create_background(),
                create_cover_area(),
                create_outline(),
                create_label());
    }

    /** Calculates and updates fraction of chart blocked */
    public void handle_timestep() {
        double num_hits = (double) Mini.Messenger.get_hits();
        double num_misses = (double) Mini.Messenger.get_misses();
        double num_losses = (double) Mini.Messenger.get_losses();

        double fraction;
        if (num_hits <= 0.1) { // Prevent zero division error
            fraction = 1.0;
        } else if (chart_type == Mini.ChartType.POWER) {
            fraction = 1.0 - num_hits / (num_hits + num_losses);
        } else if (chart_type == Mini.ChartType.SIGNIFICANCE) {
            fraction = 1.0 - num_hits / (num_hits + num_misses);
        } else {
            throw new RuntimeException("What is a " + chart_type.toString());
        }

        cover.setHeight(CHART_HEIGHT * fraction);
    }

    /** Creates gradient background of chart */
    private Rectangle create_background() {
        LinearGradient linear_gradient = new LinearGradient(
                0.0,
                0.0,
                0.0,
                1.0,
                true,
                CycleMethod.NO_CYCLE,
                new Stop(0.0, Nord.FROST_TEAL),
                new Stop(1.0, Nord.FROST_BLUE));
        Rectangle background = new Rectangle();
        background.setFill(linear_gradient);
        background.setWidth(CHART_WIDTH);
        background.setHeight(CHART_HEIGHT);
        background.setOpacity(BACKGROUND_CONTRAST);
        return background;
    }

    /** Creates greyish overlay of chart */
    private AnchorPane create_cover_area() {
        cover = new Rectangle(); // Global
        cover.setFill(Nord.NIGHT_DARK);
        cover.setWidth(CHART_WIDTH);
        cover.setHeight(CHART_HEIGHT);
        cover.setOpacity(BACKGROUND_CONTRAST);
        AnchorPane cover_area = new AnchorPane(cover);
        AnchorPane.setTopAnchor(cover, 0.0);
        cover_area.setMaxSize(CHART_WIDTH, CHART_HEIGHT);
        return cover_area;
    }

    /** Creates border of chart */
    private Rectangle create_outline() {
        Rectangle outline = new Rectangle();
        outline.setWidth(CHART_WIDTH);
        outline.setHeight(CHART_HEIGHT);
        outline.setStroke(Nord.SNOW_LIGHT);
        outline.setFill(Nord.TRANSPARENT);
        return outline;
    }

    /** Creates text label */
    private Mini.Text create_label() {
        Mini.Text label = new Mini.Text(chart_type.toString(), FONT_SIZE);
        label.setRotate(-90.0);
        label.setEffect(new DropShadow(10.0, Nord.NIGHT_DARK));
        return label;
    }
}
