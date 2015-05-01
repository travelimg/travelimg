package at.ac.tuwien.qse.sepm.dao;

import at.ac.tuwien.qse.sepm.entities.Tag;

import java.util.List;

public interface TagDAO {

    public Tag create(Tag t) throws DAOException;
    public Tag read(Tag t) throws DAOException;
    public void update(Tag t) throws DAOException;
    public void delete(Tag t) throws DAOException;
    public List<Tag> readAll() throws DAOException;

}
