package at.ac.tuwien.qse.sepm.entities;

import java.util.ArrayList;

/**
 * Created by christoph on 06.05.15.
 */
public class KoordList {
    private ArrayList<Koordinate> list;

    public KoordList(){
        list = new ArrayList<Koordinate>();
    }


    public boolean addKord(Koordinate k){
        return this.list.add(k);
    }
    public boolean removeKord(Koordinate k){
        return this.list.remove(k);
    }
    public ArrayList<Koordinate> getKoordinaten(){
        return this.list;
    }
    public int size(){
        return this.list.size();
    }
}
