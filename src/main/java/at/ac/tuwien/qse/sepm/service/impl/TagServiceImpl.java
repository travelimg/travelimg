package at.ac.tuwien.qse.sepm.service.impl;

import at.ac.tuwien.qse.sepm.dao.DAOException;
import at.ac.tuwien.qse.sepm.dao.TagDAO;
import at.ac.tuwien.qse.sepm.entities.Photo;
import at.ac.tuwien.qse.sepm.entities.Tag;
import at.ac.tuwien.qse.sepm.entities.validators.ValidationException;
import at.ac.tuwien.qse.sepm.service.ServiceException;
import at.ac.tuwien.qse.sepm.service.TagService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TagServiceImpl implements TagService {

    private static final Logger LOGGER = LogManager.getLogger();

    @Autowired private TagDAO tagDAO;

    /**
     * Create a Tag in the data store.
     *
     * @param tag Tag which to create; must not be null; must not already have an id
     * @return the created Tag
     * @throws ServiceException If the Tag can not be created or the data store fails to
     *      create a record.
     */
    @Override
    public Tag create(Tag tag) throws ServiceException {
        try {
            return tagDAO.create(tag);
        } catch (DAOException ex) {
            throw new ServiceException(ex);
        } catch (ValidationException ex) {
            throw new ServiceException(ex);
        }
    }


    /**
     * Delete an existing Tag.
     *
     * @param tag Specifies which Tag to delete by providing the id;
     *            must not be null;
     *            <tt>tag.id</tt> must not be null;
     * @throws ServiceException If the Tag can not be deleted or the data store fails to
     *     delete the record.
     */
    @Override
    public void delete(Tag tag) throws ServiceException {
        try {
            tagDAO.delete(tag);
        } catch (DAOException ex) {
            throw new ServiceException(ex);
        }
    }

    @Override public Tag readName(Tag tag) throws ServiceException {
        try {
            return tagDAO.readName(tag);
        } catch (DAOException e) {
            e.printStackTrace();
            throw new ServiceException(e);
        }
    }

    /**
     * Return a list of all existing tags.
     *
     * @return the list of all available tags
     * @throws ServiceException if retrieval failed
     */
    @Override
    public List<Tag> getAllTags() throws ServiceException {
        LOGGER.debug("Retrieving all tags...");
        try {
            return tagDAO.readAll();
        } catch (DAOException e) {
            throw new ServiceException(e);
        }
    }
    /**
     * Returns a list of the most used Tags, from a list of photos
     * @param l the list of photos
     * @return a list of tag s , (the hot 5)
     * @throws ServiceException if retrieval failed
     */
    @Override public List<Tag> getMostWantet(List<Photo> l) throws ServiceException {
        HashMap<Tag,Integer> tagcounter = new HashMap<>();
        for(Photo p: l){
            for(Tag t: p.getTags()){

                if(tagcounter.size()==0){
                    tagcounter.put(t,1);
                }else{
                    boolean neu = true;
                    for(Tag ta:tagcounter.keySet()){
                        if(ta.getId() == t.getId()){
                            tagcounter.replace(ta,tagcounter.get(ta),tagcounter.get(ta)+1);
                            neu = false;
                        }
                    }
                    if(neu) tagcounter.put(t,1);
                }
            }
        }

        List<Tag> returnList = new ArrayList<>();
        for( int i =0; i<5; i++) {
            Tag tag = null;
            if (tagcounter.size() != 0) {
                for (Tag t : tagcounter.keySet()) {
                    if (tag == null) {
                        tag = t;
                    } else {
                        if (tagcounter.get(tag) < tagcounter.get(t)) {
                            tag = t;
                        }
                    }
                }
                returnList.add(tag);
                tagcounter.remove(tag);

            }
        }
        return returnList;
    }
}
