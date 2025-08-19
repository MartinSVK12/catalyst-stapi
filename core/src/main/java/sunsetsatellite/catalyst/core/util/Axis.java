package sunsetsatellite.catalyst.core.util;

public enum Axis {
    X,Y,Z;

    public boolean isVertical() {
        return this == Y;
    }
}
