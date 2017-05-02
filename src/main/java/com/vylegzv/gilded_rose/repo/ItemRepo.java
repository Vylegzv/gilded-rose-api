package com.vylegzv.gilded_rose.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.vylegzv.gilded_rose.domain.Item;

/**
 * 
 * @author vella
 *
 */
@Repository
public interface ItemRepo extends CrudRepository<Item, Long> {

}
