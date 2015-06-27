package at.ac.tuwien.qse.sepm.service.impl;

import at.ac.tuwien.qse.sepm.entities.Photo;

import static org.junit.Assert.*;

public class PhotoAggregatorTest extends AggregatorTest<Photo> {

    @Override protected Photo getValue() {
        return new Photo();
    }

    @Override protected Aggregator<Photo> getObject() {
        return new Aggregator<>();
    }
}