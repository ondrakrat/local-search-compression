package localsearch;

/**
 * @author Ondřej Kratochvíl
 */
public class Circle {

    // center coordinates
    private int x;
    private int y;
    private int diameter;
    private int colour;

    public Circle(int x, int y, int diameter, int colour) {
        this.x = x;
        this.y = y;
        this.diameter = diameter;
        this.colour = colour;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getDiameter() {
        return diameter;
    }

    public void setDiameter(int diameter) {
        this.diameter = diameter;
    }

    public int getColour() {
        return colour;
    }

    public void setColour(int colour) {
        this.colour = colour;
    }
}
