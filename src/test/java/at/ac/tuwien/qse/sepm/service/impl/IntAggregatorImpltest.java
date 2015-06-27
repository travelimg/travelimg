package at.ac.tuwien.qse.sepm.service.impl;

public class IntAggregatorImpltest extends AggregatorTest<Integer> {

    @Override protected Integer getValue() {
        return 43;
    }

    @Override protected Aggregator<Integer> getObject() {
        return new Aggregator<>();
    }
}
