package drawing;

import java.awt.*;
import java.util.function.Consumer;

public class LaunchAwtApp extends Frame {
    public static Consumer<Graphics> paintImpl = null;

    @Override
    public void paint(final Graphics g) {
        if (paintImpl != null) {
            paintImpl.accept(g);
        }
    }
}