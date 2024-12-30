import java.util.ArrayList;
import javafx.geometry.Pos;
import javafx.scene.layout.Background;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

/**
    A {@code} LabelManager} is the high-level interface for managing Labels. Calling `distribute_timestep` will have it tell its children to poll Messenger and figure out what they're supposed to look like.

    A {@code Label} is a box with two pieces of stylized text in it. These are a description (e.g., "Score") and a value (e.g., "123").
 */
public class LabelManager extends VBox {
    private double LABEL_SPACING = 20.0;
    private Color BACKGROUND_COLOR = Nord.NIGHT_DARKEST;
    private double WIDTH = 200.0;

    private ArrayList<Label> labels;

    public LabelManager() {
        setAlignment(Pos.CENTER);
        setPrefWidth(WIDTH);
        setBackground(Background.fill(BACKGROUND_COLOR));

        labels = new ArrayList<>();
        for (Mini.LabelType label_type : Mini.LabelType.values()) {
            labels.add(new Label(label_type));
            getChildren().add(labels.getLast());
            getChildren().add(new Mini.Spacer(0.0, LABEL_SPACING));
        }
        getChildren().removeLast();
    }

    /** Informs all Label children to update themselves */
    public void distribute_timestep() {
        for (Label firalabel : labels) {
            firalabel.handle_timestep();
        }
    }
}

class Label extends VBox {
    private double LABEL_SIZE = 20.0;
    private double CONTENT_SIZE = 40.0;

    private Mini.LabelType label_type;
    private Mini.Text description;
    private Mini.Text value;

    public Label(Mini.LabelType label_type) {
        this.label_type = label_type;

        setAlignment(Pos.CENTER);

        description = new Mini.Text(label_type.get_label(), LABEL_SIZE);
        value = new Mini.Text(label_type.get_contents(), CONTENT_SIZE);
        getChildren().addAll(description, value);
    }

    /** Updates content of value */
    public void handle_timestep() {
        String text;
        switch (label_type) {
            case Mini.LabelType.PLAY_COMBO:
                text = Integer.toString(Mini.Messenger.get_combo());
                break;
            case Mini.LabelType.PLAY_SCORE:
                text = Integer.toString(Mini.Messenger.get_hits());
                break;
            default:
                return;
        }
        value.setText(text);
    }
}
