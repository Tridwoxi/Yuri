import javafx.scene.paint.Color;

/**
    The {@code Nord} class implements various colors from https://www.nordtheme.com/. This provides the colorscheme for the project.
*/
public class Nord {
    // Background    
    public static final Color NIGHT_DARK = Color.web("2E3440");
    public static final Color NIGHT_LIGHT = Color.web("4B556A");

    // Text?
    public static final Color SNOW_DARK = Color.web("D8DEE9");
    public static final Color SNOW_LIGHT = Color.web("ECEFF4");

    // Core
    public static final Color FROST_TEAL = Color.web("8EBCBB");
    public static final Color FROST_CYAN = Color.web("86C0D1");
    public static final Color FROST_CORNFLOWER = Color.web("80A0C2");
    public static final Color FROST_BLUE = Color.web("5D80AE");

    // Decorative
    public static final Color AURORA_RED = Color.web("C16069");
    public static final Color AURORA_ORANGE = Color.web("D2876D");
    public static final Color AURORA_YELLOW = Color.web("ECCC87");
    public static final Color AURORA_GREEN = Color.web("A2BF8A");
    public static final Color AURORA_PURPLE = Color.web("B58DAE");

    // Custom; not present in original Nord theme
    public static final Color NIGHT_DARKEST = Color.web("2A2E38");
    public static final Color TRANSPARENT = Color.TRANSPARENT;

    // Collections
    public static final Color[] ALL_NIGHTSNOW = {
            NIGHT_DARK,
            NIGHT_LIGHT,
            SNOW_DARK,
            SNOW_LIGHT,
    }; // ALL_NIGHTSNOW isn't used anywhere, but it's nice to have for completeness?
    public static final Color[] ALL_FROST = {
            FROST_TEAL,
            FROST_CYAN,
            FROST_CORNFLOWER,
            FROST_BLUE,
    };
    public static final Color[] ALL_AURORA = {
            AURORA_RED,
            AURORA_ORANGE,
            AURORA_YELLOW,
            AURORA_GREEN,
            AURORA_PURPLE,
    };
}
