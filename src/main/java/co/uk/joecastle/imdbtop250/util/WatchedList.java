package co.uk.joecastle.imdbtop250.util;

import co.uk.joecastle.imdbtop250.entity.Watched;

import java.util.ArrayList;
import java.util.Collection;

public class WatchedList extends ArrayList<Watched> {

    public WatchedList() {
        super();
    }

    public WatchedList(Collection<? extends Watched> c) {
        super(c);
    }

    public boolean isWatched(String title) {
        return stream()
                .anyMatch(watched -> watched.getTitle().equals(title) && watched.getWatched());
    }

    public long watchedCount() {
        return stream()
                .filter(Watched::getWatched)
                .count();
    }

}
