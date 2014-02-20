/*
 * Copyright 2006 Centenum Software Solutions, Inc. All rights reserved.
 * CENTENUM PROPRIETARY/CONFIDENTIAL. 
 */
package com.zccshome.spring_mvc_test.hibernate.common;

import java.io.Serializable;
import java.util.Collection;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Transactional;

/**
 * A hibernate/Spring framework implementation for IObjectFactory
 * 
 * @author zhang tiange
 * 
 */
@Transactional
public class PMHibernateImpl extends HibernateDaoSupport implements
		IPersistenceManager, BeanFactoryAware {

	public static IPersistenceManager getInstance() {
		ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext(
				new String[] { "HibernateApplicationContext.xml" });
		return (IPersistenceManager) ((BeanFactory) appContext)
				.getBean("objectFactory");
	}

	BeanFactory beanFactory;

	private Class getImplClass(Class clazz) {
		if (clazz.isInterface())
			return beanFactory.getBean(getBeanID(clazz)).getClass();
		else
			return clazz;
	}

	private String getBeanID(Class clazz) {
		int pos = clazz.getName().lastIndexOf('.') + 1;
		return clazz.getName().substring(pos);
	}

	public IModelObject create(Class clazz) {
		IModelObject bean = (IModelObject) beanFactory
				.getBean(getBeanID(clazz));
		return bean;
	}

	public IModelObject get(Class clazz, Serializable id) {
		IModelObject bean = (IModelObject) getHibernateTemplate().get(
				getImplClass(clazz), id);
		return bean;
	}

	public IModelObject load(IModelObject e) {
		getHibernateTemplate().load(e, e.getId());
		return e;
	}

	public void delete(IModelObject obj) {
		getHibernateTemplate().delete(obj);
	}

	public IModelObject save(IModelObject obj) {
		getSession().saveOrUpdate(obj);
		return obj;
	}

	public IModelObject create(IModelObject obj) {
		getSession().saveOrUpdate(obj);
		return obj;
	}

	public <T extends IModelObject> Collection<T> all(Class<T> clazz) {
		return getHibernateTemplate().find("from " + clazz.getName());
	}

	public boolean exist(IModelObject value) {
		throw new RuntimeException("not supported");
	}

	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.fudan.framework.persistence.IObjectFactory#deleteAll(java.lang.Class)
	 */
	public void deleteAll(Class clazz) {
		getHibernateTemplate().deleteAll(all(clazz));
	}

	public Criteria createCriteria(Class clazz) {
		return getHibernateSession().createCriteria(clazz);
	}

	public void save(IModelObject model, Serializable id) {
		// getSession().persist(model, id);
	}

	public Session getHibernateSession() {
		return getSession();
	}

	public void logicDelete(IModelObject modelObject) {
		save(modelObject);
	}

	@Override
	public Query createQuery(String hql) {
		return getHibernateSession().createQuery(hql);
	}
}
