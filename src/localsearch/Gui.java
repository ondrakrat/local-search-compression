package localsearch;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;

/**
 * @author Ondřej Kratochvíl
 */
public class Gui extends JFrame {

    private ImageImplement panel;

    public void start(int width, int height, Image image) {
        panel = new ImageImplement(new ImageIcon(image).getImage());
        add(panel);
        setVisible(true);
        setSize(width + 50, height + 50);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLayout(new FlowLayout(FlowLayout.CENTER));
        setTitle("Processing...");
    }

    public void finish() {
        setTitle("Done!");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }

    public void update() {
        panel.repaint();
    }

    private class ImageImplement extends JPanel {
        private Image img;

        public ImageImplement(Image img) {
            this.img = img;
            Dimension size = new Dimension(img.getWidth(null), img.getHeight(null));
            setPreferredSize(size);
            setMinimumSize(size);
            setMaximumSize(size);
            setSize(size);
        }

        public void setImg(Image img) {
            this.img = img;
        }

        @Override
        public void paintComponent(Graphics g) {
            g.drawImage(img, 0, 0, null);
        }
    }
}
