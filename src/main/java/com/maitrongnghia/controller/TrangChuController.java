package com.maitrongnghia.controller;

import java.util.ArrayList;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import org.hibernate.query.Query;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import com.maitrongnghia.entity.User;
import com.maitrongnghia.entity.Word;

@Controller
public class TrangChuController {

	@PersistenceContext
	private EntityManager entityManager;

	@RequestMapping(path = "/", method = RequestMethod.GET)
	public String trangChu(ModelMap modelMap) {
		return "login";
	}

	@RequestMapping(path = "/add", method = RequestMethod.POST)
	@Transactional
	public String add(@RequestParam String tienganh, @RequestParam String tiengviet, @RequestParam String word,
			@RequestParam String type, @RequestParam String role, ModelMap modelMap) {
		String sql;
		TypedQuery query;
		Word word1 = new Word();
		word1.setTienganh(tienganh);
		word1.setTiengviet(tiengviet);
		entityManager.persist(word1);
		modelMap.addAttribute("status", "Them phan tu thanh cong !");
		return "redirect:search?word=" + word + "&type=" + type + "&role=" + role + "&pageCurrent=";
	}

	@RequestMapping(path = "/update", method = RequestMethod.GET)
	@Transactional
	public String updte(@RequestParam String tiengviet, @RequestParam Integer id, @RequestParam String word,
			@RequestParam String type, @RequestParam String role, ModelMap modelMap) {
		String sql;
		TypedQuery<Word> query;
		sql = "UPDATE Word SET tiengviet=:tiengviet1 WHERE id=:id1";
		query = (TypedQuery<Word>) entityManager.createQuery(sql);
		query.setParameter("tiengviet1", tiengviet);
		query.setParameter("id1", id);
		query.executeUpdate();
		modelMap.addAttribute("status", "Cap nhat thanh cong !");
		return "redirect:search?word=" + word + "&type=" + type + "&role=" + role + "&pageCurrent=";
	}

	@RequestMapping(path = "/trangchinh", method = RequestMethod.POST)
	@Transactional
	public String checkLogin(@RequestParam String username, @RequestParam String password, ModelMap modelMap) {
		String hql = "FROM User WHERE username =:username1 and  password =:password1";
		TypedQuery<User> query = (TypedQuery<User>) entityManager.createQuery(hql);
		query.setParameter("username1", username);
		query.setParameter("password1", password);
		User user;
		try {
			user = (User) query.getSingleResult();
		} catch (NoResultException e) {
			return "login";
		}

		if (user != null) {
			if (user.getRole().equals("1")) {

				return "adminSearch";
			} else if (user.getRole().equals("2")) {
				return "userSearch";
			}
		}

		return "login";

	}

	@RequestMapping(path = "/delete", method = RequestMethod.GET)
	@Transactional
	public String delete(@RequestParam Integer id, @RequestParam String word, @RequestParam String type,
			@RequestParam String role, ModelMap modelMap) {
		String sql;
		TypedQuery<Word> query;
		sql = "DELETE FROM Word WHERE id =:id1";
		try {
			query = (Query) entityManager.createQuery(sql);
			query.setParameter("id1", id);
			query.executeUpdate();

		} catch (NullPointerException e) {

		}

		return "redirect:search?word=" + word + "&type=" + type + "&role=" + role + "&pageCurrent=";
	}

	@RequestMapping(path = "/search", method = RequestMethod.GET)
	@Transactional
	public String search(@RequestParam String word, @RequestParam String type, @RequestParam String role,
			@RequestParam String pageCurrent, ModelMap modelMap) {

		ArrayList<Word> arr = new ArrayList<Word>();
		Word wordObject;
		String sql1, sql2;
		long count = 0;
		TypedQuery<Word> query1;
		TypedQuery<Long> query2;
		if (type.equals("anhviet")) {
			try {
				sql2 = "SELECT Count(*)FROM Word WHERE tienganh like '%" + word + "%'";
				query2 = (TypedQuery<Long>) entityManager.createQuery(sql2);
				count = query2.getSingleResult();
				sql1 = "FROM Word WHERE tienganh like '%" + word + "%'";
				query1 = (TypedQuery<Word>) entityManager.createQuery(sql1);
				if (count > 4) {
					if (pageCurrent.equals("")) {
						query1.setFirstResult(1);
						query1.setMaxResults(4);
					} else {
						query1.setFirstResult(Integer.parseInt(pageCurrent) * 4 - 3);
						query1.setMaxResults(4);
					}
				}

				arr = (ArrayList<Word>) query1.getResultList();
			} catch (NoResultException e) {
				if (role.equals("admin")) {
					return "adminSearch";
				} else {
					return "userSearch";
				}
			}
		} else if (type.equals("vietanh")) {
			try {
				sql2 = "SELECT Count(*)FROM Word WHERE tiengviet like '%" + word + "%'";
				query2 = (TypedQuery<Long>) entityManager.createQuery(sql2);
				count = (Long) query2.getSingleResult();
				sql1 = "FROM Word WHERE tiengviet like '%" + word + "%'";
				query1 = (TypedQuery<Word>) entityManager.createQuery(sql1);
				if (count > 4) {
					if (pageCurrent.equals("")) {
						query1.setFirstResult(1);
						query1.setMaxResults(4);
					} else {
						query1.setFirstResult(Integer.parseInt(pageCurrent) * 4 - 3);
						query1.setMaxResults(4);
					}
				}

				arr = (ArrayList<Word>) query1.getResultList();
			} catch (NoResultException e) {
				if (role.equals("admin")) {
					return "adminSearch";
				} else {
					return "userSearch";
				}
			}
		} else {
			try {
				sql2 = "SELECT Count(*) FROM Word WHERE tienganh like '%" + word + "%'" + "or" + " tienganh like '%"
						+ word + "%'";
				query2 = (TypedQuery<Long>) entityManager.createQuery(sql2);
				count = (Long) query2.getSingleResult();
				sql1 = "FROM Word WHERE tienganh like '%" + word + "%'" + "or" + " tienganh like '%" + word + "%'";
				query1 = (TypedQuery<Word>) entityManager.createQuery(sql1);
				if (count > 4) {
					if (pageCurrent.equals("")) {
						query1.setFirstResult(1);
						query1.setMaxResults(4);
					} else {
						query1.setFirstResult(Integer.parseInt(pageCurrent) * 4 - 3);
						query1.setMaxResults(4);
					}
				}
				arr = (ArrayList<Word>) query1.getResultList();
			} catch (NoResultException e) {
				if (role.equals("admin")) {
					return "adminSearch";
				} else {
					return "userSearch";
				}
			}
		}
		modelMap.addAttribute("listWord", arr);
		modelMap.addAttribute("word", word);
		modelMap.addAttribute("type", type);
		modelMap.addAttribute("count", count);
		modelMap.addAttribute("pageCurrent", pageCurrent);

		if (role.equals("admin")) {
			return "adminSearch";
		} else {
			return "userSearch";
		}
	}
}