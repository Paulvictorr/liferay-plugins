/**
 * Copyright (c) 2000-2008 Liferay, Inc. All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.liferay.wol.service.persistence;

import com.liferay.portal.SystemException;
import com.liferay.portal.kernel.dao.DynamicQuery;
import com.liferay.portal.kernel.dao.DynamicQueryInitializer;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.StringMaker;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.model.ModelListener;

import com.liferay.portlet.service.BasePersistence;
import com.liferay.portlet.service.FinderCache;
import com.liferay.portlet.service.HibernateUtil;
import com.liferay.portlet.service.PropsUtil;

import com.liferay.util.dao.hibernate.QueryPos;
import com.liferay.util.dao.hibernate.QueryUtil;

import com.liferay.wol.NoSuchJIRAChangeGroupException;
import com.liferay.wol.model.JIRAChangeGroup;
import com.liferay.wol.model.impl.JIRAChangeGroupImpl;
import com.liferay.wol.model.impl.JIRAChangeGroupModelImpl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.hibernate.Query;
import org.hibernate.Session;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * <a href="JIRAChangeGroupPersistenceImpl.java.html"><b><i>View Source</i></b></a>
 *
 * @author Brian Wing Shun Chan
 *
 */
public class JIRAChangeGroupPersistenceImpl extends BasePersistence
	implements JIRAChangeGroupPersistence {
	public JIRAChangeGroup create(long jiraChangeGroupId) {
		JIRAChangeGroup jiraChangeGroup = new JIRAChangeGroupImpl();

		jiraChangeGroup.setNew(true);
		jiraChangeGroup.setPrimaryKey(jiraChangeGroupId);

		return jiraChangeGroup;
	}

	public JIRAChangeGroup remove(long jiraChangeGroupId)
		throws NoSuchJIRAChangeGroupException, SystemException {
		Session session = null;

		try {
			session = openSession();

			JIRAChangeGroup jiraChangeGroup = (JIRAChangeGroup)session.get(JIRAChangeGroupImpl.class,
					new Long(jiraChangeGroupId));

			if (jiraChangeGroup == null) {
				if (_log.isWarnEnabled()) {
					_log.warn("No JIRAChangeGroup exists with the primary key " +
						jiraChangeGroupId);
				}

				throw new NoSuchJIRAChangeGroupException(
					"No JIRAChangeGroup exists with the primary key " +
					jiraChangeGroupId);
			}

			return remove(jiraChangeGroup);
		}
		catch (NoSuchJIRAChangeGroupException nsee) {
			throw nsee;
		}
		catch (Exception e) {
			throw HibernateUtil.processException(e);
		}
		finally {
			closeSession(session);
		}
	}

	public JIRAChangeGroup remove(JIRAChangeGroup jiraChangeGroup)
		throws SystemException {
		if (_listeners != null) {
			for (ModelListener listener : _listeners) {
				listener.onBeforeRemove(jiraChangeGroup);
			}
		}

		jiraChangeGroup = removeImpl(jiraChangeGroup);

		if (_listeners != null) {
			for (ModelListener listener : _listeners) {
				listener.onAfterRemove(jiraChangeGroup);
			}
		}

		return jiraChangeGroup;
	}

	protected JIRAChangeGroup removeImpl(JIRAChangeGroup jiraChangeGroup)
		throws SystemException {
		Session session = null;

		try {
			session = openSession();

			session.delete(jiraChangeGroup);

			session.flush();

			return jiraChangeGroup;
		}
		catch (Exception e) {
			throw HibernateUtil.processException(e);
		}
		finally {
			closeSession(session);

			FinderCache.clearCache(JIRAChangeGroup.class.getName());
		}
	}

	public JIRAChangeGroup update(JIRAChangeGroup jiraChangeGroup)
		throws SystemException {
		if (_log.isWarnEnabled()) {
			_log.warn(
				"Using the deprecated update(JIRAChangeGroup jiraChangeGroup) method. Use update(JIRAChangeGroup jiraChangeGroup, boolean merge) instead.");
		}

		return update(jiraChangeGroup, false);
	}

	public JIRAChangeGroup update(JIRAChangeGroup jiraChangeGroup, boolean merge)
		throws SystemException {
		boolean isNew = jiraChangeGroup.isNew();

		if (_listeners != null) {
			for (ModelListener listener : _listeners) {
				if (isNew) {
					listener.onBeforeCreate(jiraChangeGroup);
				}
				else {
					listener.onBeforeUpdate(jiraChangeGroup);
				}
			}
		}

		jiraChangeGroup = updateImpl(jiraChangeGroup, merge);

		if (_listeners != null) {
			for (ModelListener listener : _listeners) {
				if (isNew) {
					listener.onAfterCreate(jiraChangeGroup);
				}
				else {
					listener.onAfterUpdate(jiraChangeGroup);
				}
			}
		}

		return jiraChangeGroup;
	}

	public JIRAChangeGroup updateImpl(
		com.liferay.wol.model.JIRAChangeGroup jiraChangeGroup, boolean merge)
		throws SystemException {
		Session session = null;

		try {
			session = openSession();

			if (merge) {
				session.merge(jiraChangeGroup);
			}
			else {
				if (jiraChangeGroup.isNew()) {
					session.save(jiraChangeGroup);
				}
			}

			session.flush();

			jiraChangeGroup.setNew(false);

			return jiraChangeGroup;
		}
		catch (Exception e) {
			throw HibernateUtil.processException(e);
		}
		finally {
			closeSession(session);

			FinderCache.clearCache(JIRAChangeGroup.class.getName());
		}
	}

	public JIRAChangeGroup findByPrimaryKey(long jiraChangeGroupId)
		throws NoSuchJIRAChangeGroupException, SystemException {
		JIRAChangeGroup jiraChangeGroup = fetchByPrimaryKey(jiraChangeGroupId);

		if (jiraChangeGroup == null) {
			if (_log.isWarnEnabled()) {
				_log.warn("No JIRAChangeGroup exists with the primary key " +
					jiraChangeGroupId);
			}

			throw new NoSuchJIRAChangeGroupException(
				"No JIRAChangeGroup exists with the primary key " +
				jiraChangeGroupId);
		}

		return jiraChangeGroup;
	}

	public JIRAChangeGroup fetchByPrimaryKey(long jiraChangeGroupId)
		throws SystemException {
		Session session = null;

		try {
			session = openSession();

			return (JIRAChangeGroup)session.get(JIRAChangeGroupImpl.class,
				new Long(jiraChangeGroupId));
		}
		catch (Exception e) {
			throw HibernateUtil.processException(e);
		}
		finally {
			closeSession(session);
		}
	}

	public List<JIRAChangeGroup> findByJiraUserId(String jiraUserId)
		throws SystemException {
		boolean finderClassNameCacheEnabled = JIRAChangeGroupModelImpl.CACHE_ENABLED;
		String finderClassName = JIRAChangeGroup.class.getName();
		String finderMethodName = "findByJiraUserId";
		String[] finderParams = new String[] { String.class.getName() };
		Object[] finderArgs = new Object[] { jiraUserId };

		Object result = null;

		if (finderClassNameCacheEnabled) {
			result = FinderCache.getResult(finderClassName, finderMethodName,
					finderParams, finderArgs, getSessionFactory());
		}

		if (result == null) {
			Session session = null;

			try {
				session = openSession();

				StringMaker query = new StringMaker();

				query.append(
					"FROM com.liferay.wol.model.JIRAChangeGroup WHERE ");

				if (jiraUserId == null) {
					query.append("author IS NULL");
				}
				else {
					query.append("author = ?");
				}

				query.append(" ");

				query.append("ORDER BY ");

				query.append("created DESC");

				Query q = session.createQuery(query.toString());

				QueryPos qPos = QueryPos.getInstance(q);

				if (jiraUserId != null) {
					qPos.add(jiraUserId);
				}

				List<JIRAChangeGroup> list = q.list();

				FinderCache.putResult(finderClassNameCacheEnabled,
					finderClassName, finderMethodName, finderParams,
					finderArgs, list);

				return list;
			}
			catch (Exception e) {
				throw HibernateUtil.processException(e);
			}
			finally {
				closeSession(session);
			}
		}
		else {
			return (List<JIRAChangeGroup>)result;
		}
	}

	public List<JIRAChangeGroup> findByJiraUserId(String jiraUserId, int begin,
		int end) throws SystemException {
		return findByJiraUserId(jiraUserId, begin, end, null);
	}

	public List<JIRAChangeGroup> findByJiraUserId(String jiraUserId, int begin,
		int end, OrderByComparator obc) throws SystemException {
		boolean finderClassNameCacheEnabled = JIRAChangeGroupModelImpl.CACHE_ENABLED;
		String finderClassName = JIRAChangeGroup.class.getName();
		String finderMethodName = "findByJiraUserId";
		String[] finderParams = new String[] {
				String.class.getName(),
				
				"java.lang.Integer", "java.lang.Integer",
				"com.liferay.portal.kernel.util.OrderByComparator"
			};
		Object[] finderArgs = new Object[] {
				jiraUserId,
				
				String.valueOf(begin), String.valueOf(end), String.valueOf(obc)
			};

		Object result = null;

		if (finderClassNameCacheEnabled) {
			result = FinderCache.getResult(finderClassName, finderMethodName,
					finderParams, finderArgs, getSessionFactory());
		}

		if (result == null) {
			Session session = null;

			try {
				session = openSession();

				StringMaker query = new StringMaker();

				query.append(
					"FROM com.liferay.wol.model.JIRAChangeGroup WHERE ");

				if (jiraUserId == null) {
					query.append("author IS NULL");
				}
				else {
					query.append("author = ?");
				}

				query.append(" ");

				if (obc != null) {
					query.append("ORDER BY ");
					query.append(obc.getOrderBy());
				}

				else {
					query.append("ORDER BY ");

					query.append("created DESC");
				}

				Query q = session.createQuery(query.toString());

				QueryPos qPos = QueryPos.getInstance(q);

				if (jiraUserId != null) {
					qPos.add(jiraUserId);
				}

				List<JIRAChangeGroup> list = (List<JIRAChangeGroup>)QueryUtil.list(q,
						getDialect(), begin, end);

				FinderCache.putResult(finderClassNameCacheEnabled,
					finderClassName, finderMethodName, finderParams,
					finderArgs, list);

				return list;
			}
			catch (Exception e) {
				throw HibernateUtil.processException(e);
			}
			finally {
				closeSession(session);
			}
		}
		else {
			return (List<JIRAChangeGroup>)result;
		}
	}

	public JIRAChangeGroup findByJiraUserId_First(String jiraUserId,
		OrderByComparator obc)
		throws NoSuchJIRAChangeGroupException, SystemException {
		List<JIRAChangeGroup> list = findByJiraUserId(jiraUserId, 0, 1, obc);

		if (list.size() == 0) {
			StringMaker msg = new StringMaker();

			msg.append("No JIRAChangeGroup exists with the key {");

			msg.append("jiraUserId=" + jiraUserId);

			msg.append(StringPool.CLOSE_CURLY_BRACE);

			throw new NoSuchJIRAChangeGroupException(msg.toString());
		}
		else {
			return list.get(0);
		}
	}

	public JIRAChangeGroup findByJiraUserId_Last(String jiraUserId,
		OrderByComparator obc)
		throws NoSuchJIRAChangeGroupException, SystemException {
		int count = countByJiraUserId(jiraUserId);

		List<JIRAChangeGroup> list = findByJiraUserId(jiraUserId, count - 1,
				count, obc);

		if (list.size() == 0) {
			StringMaker msg = new StringMaker();

			msg.append("No JIRAChangeGroup exists with the key {");

			msg.append("jiraUserId=" + jiraUserId);

			msg.append(StringPool.CLOSE_CURLY_BRACE);

			throw new NoSuchJIRAChangeGroupException(msg.toString());
		}
		else {
			return list.get(0);
		}
	}

	public JIRAChangeGroup[] findByJiraUserId_PrevAndNext(
		long jiraChangeGroupId, String jiraUserId, OrderByComparator obc)
		throws NoSuchJIRAChangeGroupException, SystemException {
		JIRAChangeGroup jiraChangeGroup = findByPrimaryKey(jiraChangeGroupId);

		int count = countByJiraUserId(jiraUserId);

		Session session = null;

		try {
			session = openSession();

			StringMaker query = new StringMaker();

			query.append("FROM com.liferay.wol.model.JIRAChangeGroup WHERE ");

			if (jiraUserId == null) {
				query.append("author IS NULL");
			}
			else {
				query.append("author = ?");
			}

			query.append(" ");

			if (obc != null) {
				query.append("ORDER BY ");
				query.append(obc.getOrderBy());
			}

			else {
				query.append("ORDER BY ");

				query.append("created DESC");
			}

			Query q = session.createQuery(query.toString());

			QueryPos qPos = QueryPos.getInstance(q);

			if (jiraUserId != null) {
				qPos.add(jiraUserId);
			}

			Object[] objArray = QueryUtil.getPrevAndNext(q, count, obc,
					jiraChangeGroup);

			JIRAChangeGroup[] array = new JIRAChangeGroupImpl[3];

			array[0] = (JIRAChangeGroup)objArray[0];
			array[1] = (JIRAChangeGroup)objArray[1];
			array[2] = (JIRAChangeGroup)objArray[2];

			return array;
		}
		catch (Exception e) {
			throw HibernateUtil.processException(e);
		}
		finally {
			closeSession(session);
		}
	}

	public List<JIRAChangeGroup> findByJiraIssueId(long jiraIssueId)
		throws SystemException {
		boolean finderClassNameCacheEnabled = JIRAChangeGroupModelImpl.CACHE_ENABLED;
		String finderClassName = JIRAChangeGroup.class.getName();
		String finderMethodName = "findByJiraIssueId";
		String[] finderParams = new String[] { Long.class.getName() };
		Object[] finderArgs = new Object[] { new Long(jiraIssueId) };

		Object result = null;

		if (finderClassNameCacheEnabled) {
			result = FinderCache.getResult(finderClassName, finderMethodName,
					finderParams, finderArgs, getSessionFactory());
		}

		if (result == null) {
			Session session = null;

			try {
				session = openSession();

				StringMaker query = new StringMaker();

				query.append(
					"FROM com.liferay.wol.model.JIRAChangeGroup WHERE ");

				query.append("issueid = ?");

				query.append(" ");

				query.append("ORDER BY ");

				query.append("created DESC");

				Query q = session.createQuery(query.toString());

				QueryPos qPos = QueryPos.getInstance(q);

				qPos.add(jiraIssueId);

				List<JIRAChangeGroup> list = q.list();

				FinderCache.putResult(finderClassNameCacheEnabled,
					finderClassName, finderMethodName, finderParams,
					finderArgs, list);

				return list;
			}
			catch (Exception e) {
				throw HibernateUtil.processException(e);
			}
			finally {
				closeSession(session);
			}
		}
		else {
			return (List<JIRAChangeGroup>)result;
		}
	}

	public List<JIRAChangeGroup> findByJiraIssueId(long jiraIssueId, int begin,
		int end) throws SystemException {
		return findByJiraIssueId(jiraIssueId, begin, end, null);
	}

	public List<JIRAChangeGroup> findByJiraIssueId(long jiraIssueId, int begin,
		int end, OrderByComparator obc) throws SystemException {
		boolean finderClassNameCacheEnabled = JIRAChangeGroupModelImpl.CACHE_ENABLED;
		String finderClassName = JIRAChangeGroup.class.getName();
		String finderMethodName = "findByJiraIssueId";
		String[] finderParams = new String[] {
				Long.class.getName(),
				
				"java.lang.Integer", "java.lang.Integer",
				"com.liferay.portal.kernel.util.OrderByComparator"
			};
		Object[] finderArgs = new Object[] {
				new Long(jiraIssueId),
				
				String.valueOf(begin), String.valueOf(end), String.valueOf(obc)
			};

		Object result = null;

		if (finderClassNameCacheEnabled) {
			result = FinderCache.getResult(finderClassName, finderMethodName,
					finderParams, finderArgs, getSessionFactory());
		}

		if (result == null) {
			Session session = null;

			try {
				session = openSession();

				StringMaker query = new StringMaker();

				query.append(
					"FROM com.liferay.wol.model.JIRAChangeGroup WHERE ");

				query.append("issueid = ?");

				query.append(" ");

				if (obc != null) {
					query.append("ORDER BY ");
					query.append(obc.getOrderBy());
				}

				else {
					query.append("ORDER BY ");

					query.append("created DESC");
				}

				Query q = session.createQuery(query.toString());

				QueryPos qPos = QueryPos.getInstance(q);

				qPos.add(jiraIssueId);

				List<JIRAChangeGroup> list = (List<JIRAChangeGroup>)QueryUtil.list(q,
						getDialect(), begin, end);

				FinderCache.putResult(finderClassNameCacheEnabled,
					finderClassName, finderMethodName, finderParams,
					finderArgs, list);

				return list;
			}
			catch (Exception e) {
				throw HibernateUtil.processException(e);
			}
			finally {
				closeSession(session);
			}
		}
		else {
			return (List<JIRAChangeGroup>)result;
		}
	}

	public JIRAChangeGroup findByJiraIssueId_First(long jiraIssueId,
		OrderByComparator obc)
		throws NoSuchJIRAChangeGroupException, SystemException {
		List<JIRAChangeGroup> list = findByJiraIssueId(jiraIssueId, 0, 1, obc);

		if (list.size() == 0) {
			StringMaker msg = new StringMaker();

			msg.append("No JIRAChangeGroup exists with the key {");

			msg.append("jiraIssueId=" + jiraIssueId);

			msg.append(StringPool.CLOSE_CURLY_BRACE);

			throw new NoSuchJIRAChangeGroupException(msg.toString());
		}
		else {
			return list.get(0);
		}
	}

	public JIRAChangeGroup findByJiraIssueId_Last(long jiraIssueId,
		OrderByComparator obc)
		throws NoSuchJIRAChangeGroupException, SystemException {
		int count = countByJiraIssueId(jiraIssueId);

		List<JIRAChangeGroup> list = findByJiraIssueId(jiraIssueId, count - 1,
				count, obc);

		if (list.size() == 0) {
			StringMaker msg = new StringMaker();

			msg.append("No JIRAChangeGroup exists with the key {");

			msg.append("jiraIssueId=" + jiraIssueId);

			msg.append(StringPool.CLOSE_CURLY_BRACE);

			throw new NoSuchJIRAChangeGroupException(msg.toString());
		}
		else {
			return list.get(0);
		}
	}

	public JIRAChangeGroup[] findByJiraIssueId_PrevAndNext(
		long jiraChangeGroupId, long jiraIssueId, OrderByComparator obc)
		throws NoSuchJIRAChangeGroupException, SystemException {
		JIRAChangeGroup jiraChangeGroup = findByPrimaryKey(jiraChangeGroupId);

		int count = countByJiraIssueId(jiraIssueId);

		Session session = null;

		try {
			session = openSession();

			StringMaker query = new StringMaker();

			query.append("FROM com.liferay.wol.model.JIRAChangeGroup WHERE ");

			query.append("issueid = ?");

			query.append(" ");

			if (obc != null) {
				query.append("ORDER BY ");
				query.append(obc.getOrderBy());
			}

			else {
				query.append("ORDER BY ");

				query.append("created DESC");
			}

			Query q = session.createQuery(query.toString());

			QueryPos qPos = QueryPos.getInstance(q);

			qPos.add(jiraIssueId);

			Object[] objArray = QueryUtil.getPrevAndNext(q, count, obc,
					jiraChangeGroup);

			JIRAChangeGroup[] array = new JIRAChangeGroupImpl[3];

			array[0] = (JIRAChangeGroup)objArray[0];
			array[1] = (JIRAChangeGroup)objArray[1];
			array[2] = (JIRAChangeGroup)objArray[2];

			return array;
		}
		catch (Exception e) {
			throw HibernateUtil.processException(e);
		}
		finally {
			closeSession(session);
		}
	}

	public List<JIRAChangeGroup> findWithDynamicQuery(
		DynamicQueryInitializer queryInitializer) throws SystemException {
		Session session = null;

		try {
			session = openSession();

			DynamicQuery query = queryInitializer.initialize(session);

			return query.list();
		}
		catch (Exception e) {
			throw HibernateUtil.processException(e);
		}
		finally {
			closeSession(session);
		}
	}

	public List<JIRAChangeGroup> findWithDynamicQuery(
		DynamicQueryInitializer queryInitializer, int begin, int end)
		throws SystemException {
		Session session = null;

		try {
			session = openSession();

			DynamicQuery query = queryInitializer.initialize(session);

			query.setLimit(begin, end);

			return query.list();
		}
		catch (Exception e) {
			throw HibernateUtil.processException(e);
		}
		finally {
			closeSession(session);
		}
	}

	public List<JIRAChangeGroup> findAll() throws SystemException {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	public List<JIRAChangeGroup> findAll(int begin, int end)
		throws SystemException {
		return findAll(begin, end, null);
	}

	public List<JIRAChangeGroup> findAll(int begin, int end,
		OrderByComparator obc) throws SystemException {
		boolean finderClassNameCacheEnabled = JIRAChangeGroupModelImpl.CACHE_ENABLED;
		String finderClassName = JIRAChangeGroup.class.getName();
		String finderMethodName = "findAll";
		String[] finderParams = new String[] {
				"java.lang.Integer", "java.lang.Integer",
				"com.liferay.portal.kernel.util.OrderByComparator"
			};
		Object[] finderArgs = new Object[] {
				String.valueOf(begin), String.valueOf(end), String.valueOf(obc)
			};

		Object result = null;

		if (finderClassNameCacheEnabled) {
			result = FinderCache.getResult(finderClassName, finderMethodName,
					finderParams, finderArgs, getSessionFactory());
		}

		if (result == null) {
			Session session = null;

			try {
				session = openSession();

				StringMaker query = new StringMaker();

				query.append("FROM com.liferay.wol.model.JIRAChangeGroup ");

				if (obc != null) {
					query.append("ORDER BY ");
					query.append(obc.getOrderBy());
				}

				else {
					query.append("ORDER BY ");

					query.append("created DESC");
				}

				Query q = session.createQuery(query.toString());

				List<JIRAChangeGroup> list = (List<JIRAChangeGroup>)QueryUtil.list(q,
						getDialect(), begin, end);

				if (obc == null) {
					Collections.sort(list);
				}

				FinderCache.putResult(finderClassNameCacheEnabled,
					finderClassName, finderMethodName, finderParams,
					finderArgs, list);

				return list;
			}
			catch (Exception e) {
				throw HibernateUtil.processException(e);
			}
			finally {
				closeSession(session);
			}
		}
		else {
			return (List<JIRAChangeGroup>)result;
		}
	}

	public void removeByJiraUserId(String jiraUserId) throws SystemException {
		for (JIRAChangeGroup jiraChangeGroup : findByJiraUserId(jiraUserId)) {
			remove(jiraChangeGroup);
		}
	}

	public void removeByJiraIssueId(long jiraIssueId) throws SystemException {
		for (JIRAChangeGroup jiraChangeGroup : findByJiraIssueId(jiraIssueId)) {
			remove(jiraChangeGroup);
		}
	}

	public void removeAll() throws SystemException {
		for (JIRAChangeGroup jiraChangeGroup : findAll()) {
			remove(jiraChangeGroup);
		}
	}

	public int countByJiraUserId(String jiraUserId) throws SystemException {
		boolean finderClassNameCacheEnabled = JIRAChangeGroupModelImpl.CACHE_ENABLED;
		String finderClassName = JIRAChangeGroup.class.getName();
		String finderMethodName = "countByJiraUserId";
		String[] finderParams = new String[] { String.class.getName() };
		Object[] finderArgs = new Object[] { jiraUserId };

		Object result = null;

		if (finderClassNameCacheEnabled) {
			result = FinderCache.getResult(finderClassName, finderMethodName,
					finderParams, finderArgs, getSessionFactory());
		}

		if (result == null) {
			Session session = null;

			try {
				session = openSession();

				StringMaker query = new StringMaker();

				query.append("SELECT COUNT(*) ");
				query.append(
					"FROM com.liferay.wol.model.JIRAChangeGroup WHERE ");

				if (jiraUserId == null) {
					query.append("author IS NULL");
				}
				else {
					query.append("author = ?");
				}

				query.append(" ");

				Query q = session.createQuery(query.toString());

				QueryPos qPos = QueryPos.getInstance(q);

				if (jiraUserId != null) {
					qPos.add(jiraUserId);
				}

				Long count = null;

				Iterator<Long> itr = q.list().iterator();

				if (itr.hasNext()) {
					count = itr.next();
				}

				if (count == null) {
					count = new Long(0);
				}

				FinderCache.putResult(finderClassNameCacheEnabled,
					finderClassName, finderMethodName, finderParams,
					finderArgs, count);

				return count.intValue();
			}
			catch (Exception e) {
				throw HibernateUtil.processException(e);
			}
			finally {
				closeSession(session);
			}
		}
		else {
			return ((Long)result).intValue();
		}
	}

	public int countByJiraIssueId(long jiraIssueId) throws SystemException {
		boolean finderClassNameCacheEnabled = JIRAChangeGroupModelImpl.CACHE_ENABLED;
		String finderClassName = JIRAChangeGroup.class.getName();
		String finderMethodName = "countByJiraIssueId";
		String[] finderParams = new String[] { Long.class.getName() };
		Object[] finderArgs = new Object[] { new Long(jiraIssueId) };

		Object result = null;

		if (finderClassNameCacheEnabled) {
			result = FinderCache.getResult(finderClassName, finderMethodName,
					finderParams, finderArgs, getSessionFactory());
		}

		if (result == null) {
			Session session = null;

			try {
				session = openSession();

				StringMaker query = new StringMaker();

				query.append("SELECT COUNT(*) ");
				query.append(
					"FROM com.liferay.wol.model.JIRAChangeGroup WHERE ");

				query.append("issueid = ?");

				query.append(" ");

				Query q = session.createQuery(query.toString());

				QueryPos qPos = QueryPos.getInstance(q);

				qPos.add(jiraIssueId);

				Long count = null;

				Iterator<Long> itr = q.list().iterator();

				if (itr.hasNext()) {
					count = itr.next();
				}

				if (count == null) {
					count = new Long(0);
				}

				FinderCache.putResult(finderClassNameCacheEnabled,
					finderClassName, finderMethodName, finderParams,
					finderArgs, count);

				return count.intValue();
			}
			catch (Exception e) {
				throw HibernateUtil.processException(e);
			}
			finally {
				closeSession(session);
			}
		}
		else {
			return ((Long)result).intValue();
		}
	}

	public int countAll() throws SystemException {
		boolean finderClassNameCacheEnabled = JIRAChangeGroupModelImpl.CACHE_ENABLED;
		String finderClassName = JIRAChangeGroup.class.getName();
		String finderMethodName = "countAll";
		String[] finderParams = new String[] {  };
		Object[] finderArgs = new Object[] {  };

		Object result = null;

		if (finderClassNameCacheEnabled) {
			result = FinderCache.getResult(finderClassName, finderMethodName,
					finderParams, finderArgs, getSessionFactory());
		}

		if (result == null) {
			Session session = null;

			try {
				session = openSession();

				Query q = session.createQuery(
						"SELECT COUNT(*) FROM com.liferay.wol.model.JIRAChangeGroup");

				Long count = null;

				Iterator<Long> itr = q.list().iterator();

				if (itr.hasNext()) {
					count = itr.next();
				}

				if (count == null) {
					count = new Long(0);
				}

				FinderCache.putResult(finderClassNameCacheEnabled,
					finderClassName, finderMethodName, finderParams,
					finderArgs, count);

				return count.intValue();
			}
			catch (Exception e) {
				throw HibernateUtil.processException(e);
			}
			finally {
				closeSession(session);
			}
		}
		else {
			return ((Long)result).intValue();
		}
	}

	protected void initDao() {
		String[] listenerClassNames = StringUtil.split(GetterUtil.getString(
					PropsUtil.get(
						"value.object.listener.com.liferay.wol.model.JIRAChangeGroup")));

		if (listenerClassNames.length > 0) {
			try {
				List<ModelListener> listeners = new ArrayList<ModelListener>();

				for (String listenerClassName : listenerClassNames) {
					listeners.add((ModelListener)Class.forName(
							listenerClassName).newInstance());
				}

				_listeners = listeners.toArray(new ModelListener[listeners.size()]);
			}
			catch (Exception e) {
				_log.error(e);
			}
		}
	}

	private static Log _log = LogFactory.getLog(JIRAChangeGroupPersistenceImpl.class);
	private ModelListener[] _listeners;
}