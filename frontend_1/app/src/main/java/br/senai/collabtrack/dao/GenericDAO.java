package br.senai.collabtrack.dao;

import android.database.Cursor;

import java.util.List;
import java.util.Objects;

/**
 * Created by kevin on 25/05/17.
 */

public interface GenericDAO<T, ID>{

    public ID save(T o);
    public ID update(T o);
    public int delete(ID id);
    public List<T> findAll();
    public T find(ID id);
    public List<T> parseList(Cursor cursor);
    public T parseObject(Cursor cursor);

}
