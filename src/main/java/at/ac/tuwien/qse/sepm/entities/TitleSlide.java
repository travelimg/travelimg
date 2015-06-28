package at.ac.tuwien.qse.sepm.entities;


public class TitleSlide extends Slide {

    private int color;

    public TitleSlide(Integer id, Integer slideshowId, Integer order, String caption, int color) {
        super(id, slideshowId, order, caption);

        this.color = color;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    @Override public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        TitleSlide that = (TitleSlide) o;

        return color == that.color;

    }

    @Override public int hashCode() {
        return color;
    }

    @Override
    public String toString() {
        return "TitleSlide{" +
                "color=" + color +
                "} " + super.toString();
    }
}
