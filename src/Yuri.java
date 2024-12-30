import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
	This is the main class. {@code Yuri} is a four key rhythm game. It mostly describes layout for the main scene by integrating other classes.
*/
public class Yuri extends Application {
    private static double WIDTH = 1000.0;
    private static double HEIGHT = 1000.0;
    private static Color BACKGROUND_COLOR = Nord.NIGHT_DARK;
    private static String SONG_NAME = "Cadente";
    private static String ARTIST_NAME = "Frozen Starfall";

    @Override
    public void start(Stage primary_stage) {
        Mini.Messenger.SONG_NAME = SONG_NAME;
        Mini.Messenger.ARTIST_NAME = ARTIST_NAME;

        primary_stage.setTitle("YURI!!!!!!!!!!!!!!!!");
        Group sparkle_clicky = new Group();
        Pane sparkle_holder = new Pane(sparkle_clicky);
        sparkle_holder.setPrefSize(WIDTH, HEIGHT);
        BorderPane most_area = new BorderPane();
        ChartManager charts = new ChartManager();
        LabelManager labels = new LabelManager();
        GameplayManager gameplay = new GameplayManager();
        most_area.setLeft(charts);
        most_area.setRight(labels);
        most_area.setCenter(gameplay);
        most_area.setBackground(Background.fill(BACKGROUND_COLOR));
        StackPane root_area = new StackPane(most_area, sparkle_holder);
        Scene main_scene = new Scene(root_area, WIDTH, HEIGHT);
        primary_stage.setScene(main_scene);
        primary_stage.show();
        // primary_stage.setMaximized(true);

        main_scene.setOnKeyPressed(event -> {
            gameplay.strike_note(event.getText());
        });

        main_scene.setOnMouseClicked(event -> {
            sparkle_clicky.getChildren().add(new Sparkle(
                    event.getSceneX(),
                    event.getSceneY(),
                    Nord.ALL_FROST));
        });

        AnimationTimer universal_timer = new AnimationTimer() {
            long start = System.nanoTime();

            public void handle(long now) {
                gameplay.distribute_timestep(start, now);
                charts.distribute_timestep();
                labels.distribute_timestep();
            }
        };
        universal_timer.start();

        Mini.Music cadente = new Mini.Music("../assets/" + Mini.Messenger.SONG_NAME.toLowerCase() + ".mp3");
        cadente.run();
    }
}
