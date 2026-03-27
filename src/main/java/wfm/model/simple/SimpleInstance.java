package wfm.model.simple;

import wfm.model.AbstractInstance;
import wfm.model.AbstractService;

public final class SimpleInstance extends AbstractInstance {

    /**
     * Default constructor for creating a new instance.
     * At the moment this does not contain any parameters yes, but mandatory fields will be added.
     *
     * @param service The AbstractService for which the instance is made.
     * @since 0.0.1
     */
    public SimpleInstance(AbstractService service) {
        super(service);
    }
}
