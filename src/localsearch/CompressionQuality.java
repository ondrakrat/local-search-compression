package localsearch;

/**
 * @author Ondřej Kratochvíl
 */
public enum CompressionQuality {

    LOW(10, 4),
    MEDIUM(5, 2),
    HIGH(1, 1);

    private final int factor;
    private final double diameterFactor;

    CompressionQuality(int factor, double diameterFactor) {
        this.factor = factor;
        this.diameterFactor = diameterFactor;
    }

    public int getFactor() {
        return factor;
    }

    public double getDiameterFactor() {
        return diameterFactor;
    }
}
