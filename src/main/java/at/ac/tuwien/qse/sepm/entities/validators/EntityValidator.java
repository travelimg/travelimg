package at.ac.tuwien.qse.sepm.entities.validators;


public interface EntityValidator<E> {

    /**
     * Validate given entity
     *
     * @param entity Entity to validate.
     * @throws ValidationException if the entity can not be validated.
     */
    void validate(E entity) throws ValidationException;
}
