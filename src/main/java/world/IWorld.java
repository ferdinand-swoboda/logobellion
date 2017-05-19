package world;

import java.util.List;

public interface IWorld<T extends IEntity> {

	void add(T entity);

	void remove(T entity);

	void move(T entity, int scope);

	void moveTo(T entity, T target);

	List<T> neighbourhoodOf(T entity, int scope);

	int getDimension();

	void setDimension(int dimension);

}