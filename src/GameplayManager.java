import java.util.ArrayList;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
    A {@code GameplayManager} contains various {@link NoteManager}s and enables the spawning of {@link Sparkle}s. It listens for keypresses and passes those to the NoteManager.
 */

public class GameplayManager extends StackPane {
    private static Color BACKGROUND_COLOR = Nord.NIGHT_DARK;
    private static double TRACE_SPACING = 60.0;
    private static double BACKDROP_FONT_SIZE = 40.0;

    private Pane sparkle_area;
    private Group sparkles;

    private ArrayList<NoteManager> note_managers;

    public GameplayManager() {
        setAlignment(Pos.CENTER);
        sparkles = new Group();
        sparkle_area = new Pane(sparkles);
        sparkle_area.setPrefSize(800, 800);

        getChildren()
                .addAll(
                        create_backdrop(),
                        create_note_managers_and_spacing(),
                        sparkle_area);
    }

    /** Pass a timestep down the chain of command */
    public void distribute_timestep(long start, long now) {
        for (NoteManager note_manager : note_managers) {
            note_manager.distribute_timestep();
            note_manager.consult_chart(start, now);
            note_manager.oversight();
        }
    }

    /** Pass a KeyEvent down the chain of command */
    public void strike_note(String letter) {
        for (NoteManager note_manager : note_managers) {
            if (note_manager.strike_note(letter)) {
                add_sparkle(
                        note_manager.getLayoutX() + 30,
                        note_manager.getLayoutY() + 700);
            }
        }
    }

    /** Adds a sparkle at the specific location */
    public void add_sparkle(double x, double y) {
        sparkles.getChildren().add(new Sparkle(x, y, Nord.ALL_AURORA));
    }

    /** Why do I even write docstrings when the function... whatever. */
    private HBox create_note_managers_and_spacing() {
        note_managers = new ArrayList<>();
        HBox gameplay_region = new HBox();
        for (Mini.LetterType letter : Mini.LetterType.values()) {
            note_managers.add(new NoteManager(letter));
            gameplay_region.getChildren().add(note_managers.getLast());
            gameplay_region
                    .getChildren()
                    .add(new Mini.Spacer(TRACE_SPACING, 0.0));
        }
        gameplay_region.getChildren().removeLast();
        gameplay_region.setAlignment(Pos.CENTER);
        return gameplay_region;
    }

    /** Creates the backdrop (artist and song) */
    private StackPane create_backdrop() {
        // Extra newline is for spacing without worrying about absolute position
        Mini.Text song = new Mini.Text("\n" + Mini.Messenger.SONG_NAME, BACKDROP_FONT_SIZE);
        Mini.Text artist = new Mini.Text(Mini.Messenger.ARTIST_NAME + "\n", BACKDROP_FONT_SIZE);
        song.setFont(new Font("Apple Chancery", BACKDROP_FONT_SIZE));
        artist.setFont(new Font("Apple Chancery", BACKDROP_FONT_SIZE));

        song.setFill(BACKGROUND_COLOR);
        song.setEffect(new DropShadow(10.0, Nord.SNOW_DARK));
        artist.setFill(BACKGROUND_COLOR);
        artist.setEffect(new DropShadow(10.0, Nord.SNOW_DARK));

        StackPane backdrop = new StackPane(song, artist);
        StackPane.setAlignment(song, Pos.TOP_CENTER);
        StackPane.setAlignment(artist, Pos.BOTTOM_CENTER);
        return backdrop;
    }
}
