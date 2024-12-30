import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;
import javafx.geometry.Pos;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

/**
    A {@code} NoteManager} is the high-level interface for managing Notes. It is the most complex of the Managers: it adds, removes, and updates notes, and draws a trace.

    The chart object it holds is a list of Longs read in from a `.yrct` file; see "cadente.yrct" as an example. That file also contains the file format specification. It details when to create new notes. `oversight`, `distribute_timestep`, and `spawn_note` should be called by an animation timer; `strike_note` should be attached to an event.

    The {@code Note}s are circles that know their status and if they can be struck or not. Like many other features, it is hooked up to a timer by "time since start" rather than the previous position to ensure the notes are synced up with the music.
 */
public class NoteManager extends StackPane {
    private static double WIDTH = 60.0;
    private static double HITBOX_SIZE = 100.0;
    private static double HEIGHT = 600.0;
    private static double FALLING_TIME_SEC = 2;
    private static Color TRACE_COLOR = Nord.FROST_TEAL;

    private ArrayList<Note> notes;
    private Pane note_area;
    private Mini.LetterType letter_type;
    private LinkedList<Long> chart;

    public NoteManager(Mini.LetterType letter_type) {
        this.letter_type = letter_type;
        notes = new ArrayList<>();
        chart = load_chart();

        setPrefSize(WIDTH, HEIGHT);
        setAlignment(Pos.TOP_CENTER);

        note_area = new Pane();
        VBox note_area_centering = new VBox(note_area);
        note_area_centering.setAlignment(Pos.CENTER);
        note_area.setPrefSize(WIDTH, HEIGHT);

        Line line = new Line(0.0, 0.0, 0.0, HEIGHT - WIDTH);
        line.setStroke(TRACE_COLOR);
        line.setEffect(new DropShadow(10.0, TRACE_COLOR));

        Circle circle = new Circle(WIDTH / 2.0);
        circle.setFill(Nord.NIGHT_DARK);
        circle.setStroke(TRACE_COLOR);
        circle.setEffect(new DropShadow(10.0, TRACE_COLOR));

        Mini.Text label = new Mini.Text(letter_type.toString(), 18.0);

        StackPane target = new StackPane(circle, label);
        target.setAlignment(Pos.CENTER);

        VBox trace = new VBox(line, target);
        trace.setAlignment(Pos.CENTER);

        getChildren().addAll(trace, note_area_centering);
    }

    // @formatter:off
    public static double get_height() { return HEIGHT; }
    public static double get_hitbox_size() { return HITBOX_SIZE; }
    public static double get_falling_time() { return FALLING_TIME_SEC; }
    // @formatter:on

    /** Notify all notes to relocate to the appropiate position. */
    public void distribute_timestep() {
        for (Note note : notes) {
            note.handle_timestep();
        }
    }

    /** Manage notes and delete them as necessary. */
    public void oversight() {
        if (notes.isEmpty()) {
            return;
        }
        Note first = notes.getFirst();

        if (first.get_status() == Mini.NoteStatus.ON_FLOOR) {
            note_area.getChildren().remove(first);
            notes.remove(first);
            Mini.Messenger.add_loss();
        }
    }

    /** Loads chart from external JSON */
    private LinkedList<Long> load_chart() {
        LinkedList<Long> chart = new LinkedList<>();
        long delay = 0;
        long beat_spacing = 0;

        String chart_file_path = "../assets/" + Mini.Messenger.SONG_NAME.toLowerCase() + ".yrct";
        try (Scanner scanner = new Scanner(new File(chart_file_path))) {
            delay = (long) (1e9 * scanner.nextDouble());
            beat_spacing = (long) ((1e9 / scanner.nextInt()) * 60);

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.equals(letter_type.toString())) {
                    while (scanner.hasNextLong()) {
                        long beat = scanner.nextLong();
                        chart.addLast(delay + beat_spacing * beat);
                    }
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return chart;
    }

    /** Consults the chart to determine if it's time to spawn a note */
    public void consult_chart(long start, long now) {
        if (chart.isEmpty()) {
            return;
        }

        long first = chart.getFirst();
        if (first < now - start) {
            note_area.getChildren().add(new Note());
            notes.add((Note) note_area.getChildren().getLast());
            chart.removeFirst();
        }
    }

    /** Handle attempt to hit note (presumably kepress); update Messenger */
    public boolean strike_note(String letter) {
        if (!letter.equalsIgnoreCase(letter_type.toString())) {
            return false;
        }
        if (notes.isEmpty()) {
            Mini.Messenger.add_miss();
            return false;
        }

        Note first = notes.getFirst();
        if (first.get_status() == Mini.NoteStatus.ON_TARGET) {
            note_area.getChildren().remove(first);
            notes.remove(first);
            Mini.Messenger.add_hit();
            return true;
        } else {
            Mini.Messenger.add_miss();
            return false;
        }
    }
}

class Note extends Circle {
    private static Color[] LINE_COLORS = Nord.ALL_AURORA;
    private static Color FILL_COLOR = Nord.NIGHT_DARK;
    private static double RADIUS = 15.0;
    private static double SHADOW_RADIUS = 10.0;

    private static int color_index;
    private Color line_color;
    private long creation_time;
    private double y_displacement;
    private Mini.NoteStatus status;
    private double opacity;

    public Note() {
        this.creation_time = System.nanoTime();
        this.y_displacement = 0.0;
        this.opacity = 0.0;
        this.status = Mini.NoteStatus.ON_TRACE;
        this.line_color = LINE_COLORS[color_index++ % LINE_COLORS.length];

        setOpacity(opacity);
        setStroke(line_color);
        setRadius(RADIUS);
        setFill(FILL_COLOR);
        setEffect(new DropShadow(SHADOW_RADIUS, line_color));
    }

    // @formatter:off
    public Mini.NoteStatus get_status() { return status; }
    // @formatter:on

    public void handle_timestep() {
        y_displacement = ((double) (System.nanoTime() - creation_time) / NoteManager.get_falling_time() / 1e9)
                * NoteManager.get_height();

        setOpacity(Math.min(1.0, (10 * y_displacement) / NoteManager.get_height()));
        relocate(15.0, y_displacement);

        double distance_to_bottom = NoteManager.get_height() - y_displacement;

        if (distance_to_bottom <= 15.0) {
            status = Mini.NoteStatus.ON_FLOOR;
        } else if (distance_to_bottom <= NoteManager.get_hitbox_size()) {
            status = Mini.NoteStatus.ON_TARGET;
        } else {
            status = Mini.NoteStatus.ON_TRACE;
        }
    }
}
