package eXigence.persistence.dao;

import javax.persistence.EntityManager;

public interface IHibernateDAO extends IDAO
{
	/**
	 * Setter f�r den EntityManager
	 * 
	 * @param entityManager
	 *            Die zu setzende EntityManager-Referenz
	 */
	public void setEntityManager(EntityManager entityManager);
}
