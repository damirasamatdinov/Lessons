package uz.asamatdin.lesson_one.model;

/**
 * Shama menen 30.01.2016 sag'at 12:35
 * user ta'repten jaratildi. :)
 */
public interface IModel {

    int createVbo();

    void storeDataInAttributeList(Object[] data);

    void unbindVbo();

    void deleteVbo(int vboIdx);

    void clear();

}
