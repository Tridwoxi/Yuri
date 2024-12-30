import java.io.File;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

/**
    The {@code Mini} class contains accesory classes that are too small to deserve their own file.
 */
public class Mini {
    private static String DEFAULT_FONT = "Fira Code";

    /** A {@code Spacer} takes up space to help position other nodes. */
    public static class Spacer extends Rectangle {
        public Spacer(double x, double y) {
            super(x, y);
            setFill(Color.TRANSPARENT);
        }
    }

    /** A {@code Text} is javafx's text but with my prefferred styles. */
    public static class Text extends javafx.scene.text.Text {
        // Namespaces are one honking great idea -- let's do more of those! 
        // `Mini.Text` vs `javafx.scene.text.Text` prevents confusion
        public Text(String contents, double font_size) {
            super(contents);
            setFont(Font.font(DEFAULT_FONT, font_size));
            setFill(Nord.SNOW_LIGHT);
            setTextAlignment(TextAlignment.CENTER);
        }
    }

    /** The {@code NoteStatus} enum labels where a note is. */
    public enum NoteStatus {
        ON_TRACE,
        ON_TARGET,
        ON_FLOOR,
    }

    /** The {@code ChartType} enum is of statistical measures. */
    public enum ChartType {
        POWER("Power"),
        SIGNIFICANCE("Significance");

        private String name;

        ChartType(String name) {
            this.name = name;
        }

        public String toString() {
            return name;
        }
    }

    /** The {@code LabelType} enum is of scores or songs. */
    public enum LabelType {
        // HARDCODED VALUES FOR DEMONSTRATION
        SONG_BPM("BPM", "128"), // Tool: https://getsongbpm.com/tools/audio
        SONG_DIFFICULTY("Difficulty", "3"),
        PLAY_COMBO("Combo", "0"),
        PLAY_SCORE("Score", "0");

        private String label;
        private String contents;

        LabelType(String label, String contents) {
            this.label = label;
            this.contents = contents;
        }

        // @formatter:off
        public String get_label() { return label; }
        public String get_contents() { return contents; }
        // @formatter:on
    }

    /** The {@code LetterType} enum is of the keys you use. */
    public enum LetterType {
        D("D"), F("F"), J("J"), K("K");

        private String letter;

        LetterType(String letter) {
            this.letter = letter;
        }

        public String toString() {
            return letter;
        }
    }

    /** 
        The {@code Messenger} class passes around values. 
    
        Java tends to restrict the scope of variables (which is usually a nice thing) but sometimes I can't pass things around. Global variables are useful, on occasion. I think there's a beans module that could help, but haven't gotten around to learning it.
    */
    public abstract static class Messenger {
        public static String SONG_NAME;
        public static String ARTIST_NAME;

        private static int hits = 0;
        private static int misses = 0;
        private static int losses = 0;
        private static int combo = 0;

        // @formatter:off
        public static void add_hit() { hits++; combo++; }
        public static void add_miss() { misses++; }
        public static void add_loss() { losses++; combo = 0; }
        public static int get_hits() { return hits; }
        public static int get_misses() { return misses; }
        public static int get_losses() { return losses; }
        public static int get_combo() { return combo; }
        // @formatter:on
    }

    /* Plays music. Only works some of the time for some reason. I don't know why. */
    public static class Music extends Thread {
        private String music_file;

        public Music(String music_file) {
            this.music_file = music_file;
        }

        public void run() {
            Media sound = new Media(new File(music_file).toURI().toString());
            MediaPlayer media_player = new MediaPlayer(sound);
            media_player.play();
        }
    }

}
