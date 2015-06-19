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

    @Override
    public String toString() {
        return "TitleSlide{" +
                "color=" + color +
                '}';
    }
}
