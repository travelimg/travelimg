package at.ac.tuwien.qse.sepm.entities;

import java.util.ArrayList;

/**
 * Created by christoph on 07.05.15.
 */
public class ExifList {
    private ArrayList<Exif> list;

    public ExifList(){

        list = new ArrayList<Exif>();
    }


    public boolean addKord(Exif e){
        return this.list.add(e);
    }
    public boolean removeKord(Exif e){
        return this.list.remove(e);
    }
    public ArrayList<Exif> getKoordinaten(){
        return this.list;
    }
    public int size(){
        return this.list.size();
    }
}

