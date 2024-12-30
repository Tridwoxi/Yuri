import java.util.Random;
import javafx.animation.AnimationTimer;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.CubicCurve;
import javafx.scene.shape.StrokeLineCap;

/**
    <p>A {@code Sparkle} is a multicolored animated burst composed of {@code Stream}s that originate at its center. A stream is defined as a curve connecting a set of {@code Point}s. Each timestep, points move in space and streams fade. Sparkles are used as decoration e.g., when a note is hit. 
    
    Unlike many objects seen in the rest of this project, sparkles are created directly; no "SparkleManager" exists. This is because sparkles are temporary, are never told anything after their creation, and the current situation is good enough and seems to work fine for now.</p>
 */
public class Sparkle extends Group {
    private Random random = new Random();

    // Constants
    private int NUM_STREAMS = 20;
    private double STROKE_WIDTH = 3;

    // Variables
    private Color[] colors;
    private double center_x;
    private double center_y;
    private Stream[] streams;

    public Sparkle(double center_x, double center_y, Color[] colors) {
        // Content
        this.center_x = center_x;
        this.center_y = center_y;

        // Styles
        this.colors = colors;

        // Children
        streams = create_streams();
        getChildren().addAll(streams);

        play_animation();
    }

    private Stream[] create_streams() {
        Stream[] streams = new Stream[NUM_STREAMS];
        for (int i = 0; i < streams.length; i++) {
            streams[i] = new Stream();
        }
        return streams;
    }

    private void play_animation() {
        AnimationTimer timer = new AnimationTimer() {
            long last_update = System.nanoTime();

            @Override
            public void handle(long now) {
                double elapsed_seconds = (now - last_update) / 1e9;
                for (int i = 0; i < streams.length; i++) {
                    streams[i].handle_timestep(elapsed_seconds);
                    if (streams[i].getOpacity() <= 0) {
                        getChildren().removeAll(streams[i]);
                    }
                }
                last_update = now;
            }
        };
        timer.start();
    }

    private class Stream extends CubicCurve {
        private double opacity = 1;
        private StrokeLineCap stroke_line_cap = StrokeLineCap.ROUND;
        private Color color = colors[random.nextInt(colors.length)];
        private double direction = random.nextDouble(2 * Math.PI);
        private double fade_rate = generate_fade_rate();

        private Point[] points;

        public Stream() {
            setStroke(color);
            setStrokeWidth(STROKE_WIDTH);
            setStrokeLineCap(stroke_line_cap);
            setFill(Color.TRANSPARENT);

            points = create_points();
        }

        private double generate_fade_rate() {
            // We require fade rate to have some minimum value because if a
            // stream stays on screen for too long, it drifts further than it
            // should and looks a little weird
            double minimum = 1.0;
            double offer = 0.0;
            while (offer < minimum) {
                offer = Math.abs(random.nextGaussian(2, 0.4));
            }
            return offer;
        }

        private Point[] create_points() {
            double overall_delay = 0.1;
            Point[] points = new Point[4]; // CubicCurve has 4 points
            points[0] = new Point(overall_delay * (3 / 3), direction);
            points[1] = new Point(overall_delay * (2 / 3), direction);
            points[2] = new Point(overall_delay * (1 / 3), direction);
            points[3] = new Point(overall_delay * (0 / 3), direction);
            return points;
        }

        private void set_shape() {
            setStartX(center_x + points[0].get_x_displacement());
            setStartY(center_y + points[0].get_y_displacement());
            setControlX1(center_x + points[1].get_x_displacement());
            setControlY1(center_y + points[1].get_y_displacement());
            setControlX2(center_x + points[2].get_x_displacement());
            setControlY2(center_y + points[2].get_y_displacement());
            setEndX(center_x + points[3].get_x_displacement());
            setEndY(center_y + points[3].get_y_displacement());
        }

        private void set_effects(double elapsed_seconds) {
            opacity -= elapsed_seconds * fade_rate;
            setOpacity(opacity <= 0 ? 0 : opacity);
        }

        private void handle_timestep(double elapsed_seconds) {
            for (int i = 0; i < points.length; i++) {
                points[i].handle_timestep(elapsed_seconds);
            }
            set_shape();
            set_effects(elapsed_seconds);
        }
    }

    private class Point {

        private double delay_seconds;
        private double magnitute = 0;
        private double direction;

        private double drag_rate = random.nextGaussian(5, 0.5);
        private double velocity = random.nextGaussian(2, 0.5);
        private double magnitude_jitter = random.nextGaussian(0, 0.5);
        private double direction_jitter = random.nextGaussian(0, 0.01);

        public Point(double delay_seconds, double direction) {
            this.delay_seconds = delay_seconds;
            this.direction = direction;
        }

        private double get_x_displacement() {
            return magnitute * Math.cos(direction);
        }

        private double get_y_displacement() {
            return magnitute * Math.sin(direction);
        }

        private void handle_timestep(double elapsed_seconds) {
            if (delay_seconds >= 0) {
                delay_seconds -= elapsed_seconds;
            } else {
                velocity -= drag_rate * elapsed_seconds;
                velocity = (velocity <= 0) ? 0 : velocity;
                magnitute += velocity;
                magnitute += magnitude_jitter;
                direction += direction_jitter;
            }
        }
    }
}
