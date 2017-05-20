package world;

import java.util.List;

public interface IWorld<T extends IEntity> {

	void enter(List<? extends T> entities);

	void clear();

	void move(T entity, int scope);

	void moveTo(T entity, T target);

	List<? extends T> neighbourhoodOf(T entity, int scope);

	int getDimension();

}