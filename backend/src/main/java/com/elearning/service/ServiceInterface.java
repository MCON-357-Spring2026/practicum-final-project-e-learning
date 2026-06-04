package com.elearning.service;

import java.util.List;
import java.util.Optional;

/**
 * Generic service interface defining standard CRUD operations.
 *
 * @param <T> the entity type managed by this service
 */
public interface ServiceInterface<T> {
    /**
     * Retrieves all entities.
     *
     * @return list of all entities
     */
    List<T> getAll();

    /**
     * Retrieves an entity by its ID.
     *
     * @param id the entity ID
     * @return an Optional containing the entity if found
     */
    Optional<T> getById(String id);

    /**
     * Creates a new entity.
     *
     * @param obj the entity to create
     * @return the created entity
     */
    T create(T obj);

    /**
     * Partially updates an existing entity.
     *
     * @param id  the entity ID
     * @param obj the entity containing updated fields
     * @return an Optional containing the updated entity if found
     */
    Optional<T> update(String id, T obj);

    /**
     * Fully replaces an existing entity.
     *
     * @param id  the entity ID
     * @param obj the replacement entity
     * @return an Optional containing the replaced entity if found
     */
    Optional<T> replace(String id, T obj);

    /**
     * Deletes an entity by its ID.
     *
     * @param id the entity ID
     * @return true if the entity was deleted, false if not found
     */
    boolean delete(String id);
}
