/*
 * Copyright (C) 2012 - 2012 NHN Corporation
 * All rights reserved.
 *
 * This file is part of The nGrinder software distribution. Refer to
 * the file LICENSE which is part of The nGrinder distribution for
 * licensing details. The nGrinder distribution is available on the
 * Internet at http://nhnopensource.org/ngrinder
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT HOLDERS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.ngrinder.user.controller;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.ngrinder.common.controller.NGrinderBaseController;
import org.ngrinder.user.model.JsonBean;
import org.ngrinder.user.model.User;
import org.ngrinder.user.service.UserService;
import org.ngrinder.user.util.JSONUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;




@Controller
@RequestMapping("/user")
public class UserController extends NGrinderBaseController{
	

	@Autowired
	private UserService userService;
	

	@RequestMapping("/list")
	public String getUserList(ModelMap model,@RequestParam(required = false)  String roleName) {
		
		Map<String, List<User>> userMap = userService.getAllUserInGroup();

		List<JsonBean> jList = new ArrayList<JsonBean>();

		List<User> userList = new ArrayList<User>();

		JsonBean TopBean = new JsonBean("all", "0", "All Users", true);
		jList.add(TopBean);
		for (Map.Entry<String, List<User>> entry : userMap.entrySet()) {
			String id = entry.getKey();
			JsonBean bean = new JsonBean(id, "all", id, true);
			jList.add(bean);
			for (User user : entry.getValue()) {
				JsonBean leafBean = new JsonBean(user.getUserId(), id, user.getUserName(), true);
				jList.add(leafBean);
			}
			
			if("all".equals(roleName)||roleName==null){
				userList.addAll(entry.getValue());
			}else if(roleName.equals(id)){
				userList.addAll(entry.getValue());
			}
			
		}
		model.addAttribute("userList", userList);

		
		String jsonStr = JSONUtil.toJson(jList);
		model.addAttribute("jsonStr", jsonStr);

		return "user/userList";
	}

	@RequestMapping("/detail")
	public String getUserDetail(ModelMap model,@RequestParam(required = false)  String userId) {
		
		Map<String, List<User>> userMap = userService.getAllUserInGroup();

		List<JsonBean> jList = new ArrayList<JsonBean>();

		List<User> userList = new ArrayList<User>();

		JsonBean TopBean = new JsonBean("all", "0", "All Users", true);
		jList.add(TopBean);
		for (Map.Entry<String, List<User>> entry : userMap.entrySet()) {
			String id = entry.getKey();
			JsonBean bean = new JsonBean(id, "all", id, true);
			jList.add(bean);
			for (User user : entry.getValue()) {
				JsonBean leafBean = new JsonBean(user.getUserId(), id, user.getUserName(), true);
				jList.add(leafBean);
			}
			userList.addAll(entry.getValue());
			
		}
		model.addAttribute("userList", userList);

		
		String jsonStr = JSONUtil.toJson(jList);
		model.addAttribute("jsonStr", jsonStr);
		
		
		
		User user = userService.getUserById(userId);
		
		model.addAttribute("user", user);
		return "user/userDetail";
	}
	
	@RequestMapping("/save")
	public String saveOrUpdateUserDetail(ModelMap model,@ModelAttribute("user") User user) {
		
		if (!StringUtils.isEmpty(user.getUserId())) {
			userService.modifyUser(user);
		} else {
			userService.saveUser(user);
		}
		return "redirect:/user/list";
	}
	
	@RequestMapping("/delete")
	public String deleteUser(ModelMap model,@RequestParam String userId) {
		List<String> list = new ArrayList<String>();
		list.add(userId);
		userService.deleteUsers(list);
		return "redirect:/user/list";
	}
	
	
}