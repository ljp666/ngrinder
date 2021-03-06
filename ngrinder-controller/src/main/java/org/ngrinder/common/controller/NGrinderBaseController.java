/* 
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
package org.ngrinder.common.controller;

import static org.ngrinder.common.util.NoOp.noOp;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.ngrinder.common.constant.NGrinderConstants;
import org.ngrinder.common.exception.NGrinderRuntimeException;
import org.ngrinder.infra.config.Config;
import org.ngrinder.model.User;
import org.ngrinder.operation.service.AnnouncementService;
import org.ngrinder.user.service.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

/**
 * Controller base containning widely used methods.
 * 
 * @author JunHo Yoon
 * @since 3.0
 */
public class NGrinderBaseController implements NGrinderConstants {

	public static final String ERROR_PAGE = "errors/error";

	protected static final int DEFAULT_PAGE_LIMIT = 20;

	private static String successJson;

	private static String errorJson;

	private static Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create();

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private UserContext userContext;

	@Autowired
	private Config config;

	@Autowired
	private AnnouncementService announcementService;

	@PostConstruct
	void initJSON() {
		JsonObject rtnJson = new JsonObject();
		rtnJson.addProperty(JSON_SUCCESS, true);
		successJson = rtnJson.toString();
		rtnJson.addProperty(JSON_SUCCESS, false);
		errorJson = rtnJson.toString();
	}

	/**
	 * Get the current user.
	 * 
	 * @return current user
	 */
	public User getCurrentUser() {
		return userContext.getCurrentUser();
	}

	/**
	 * Provide the current login user as a model attribute. If it's not found, return empty user.
	 * 
	 * @return login user
	 */
	@ModelAttribute("currentUser")
	public User currentUser() {
		try {
			return getCurrentUser();
		} catch (AuthenticationCredentialsNotFoundException e) {
			// Fall through
			noOp();
		}
		return new User();
	}

	/**
	 * Provide the announcement content as a model attribute.
	 * 
	 * @return announcement content
	 */
	@ModelAttribute("announcement")
	public String announcement() {
		return announcementService.getAnnouncement();
	}

	/**
	 * Provide the announcement content as a model attribute.
	 * 
	 * @return announcement content
	 */
	@ModelAttribute("announcement_new")
	public boolean announcementNew() {
		return announcementService.getAnnouncementIsNew();
	}

	/**
	 * Provide the boolean value representing that it's clustered or not as a model attributes.
	 * 
	 * @return clustered mark
	 */
	@ModelAttribute("clustered")
	public boolean clustered() {
		return config.isCluster();
	}

	/**
	 * Provide the help URL as a model attribute.
	 * 
	 * @return help URL
	 */
	@ModelAttribute("helpUrl")
	public String helpUrl() {
		return config.getHelpUrl();
	}

	/**
	 * Provide the announcement hide cookie value as a model attribute.
	 * 
	 * @param annoucnementHide
	 *            true if hidden.
	 * @return announcement content
	 */
	@ModelAttribute("announcement_hide")
	public boolean announcement(
			@CookieValue(value = "announcement_hide", defaultValue = "false") boolean annoucnementHide) {
		return annoucnementHide;
	}

	/**
	 * Get the message from messageSource by the given key.
	 * 
	 * @param key
	 *            key of message
	 * @return the found message. If not found, the error message will return.
	 */
	protected String getMessages(String key) {
		Locale locale = null;
		String message = null;
		try {
			locale = new Locale(getCurrentUser().getUserLanguage());
			message = messageSource.getMessage(key, null, locale);
		} catch (Exception e) {
			return "Getting message error for key " + key;
		}
		return message;
	}

	/**
	 * Return the success json message.
	 * 
	 * @param message
	 *            message
	 * @return json message
	 */
	public String returnSuccess(String message) {
		JsonObject rtnJson = new JsonObject();
		rtnJson.addProperty(JSON_SUCCESS, true);
		rtnJson.addProperty(JSON_MESSAGE, message);
		return rtnJson.toString();
	}

	/**
	 * Return the error json message.
	 * 
	 * @param message
	 *            message
	 * @return json message
	 */
	public String returnError(String message) {
		JsonObject rtnJson = new JsonObject();
		rtnJson.addProperty(JSON_SUCCESS, false);
		rtnJson.addProperty(JSON_MESSAGE, message);
		return rtnJson.toString();
	}

	/**
	 * Return the raw success json message.
	 * 
	 * @return json message
	 */
	public String returnSuccess() {
		return successJson;
	}

	/**
	 * Return the raw error json message.
	 * 
	 * @return json message
	 */
	public String returnError() {
		return errorJson;
	}

	/**
	 * Convert the given list into a json message.
	 * 
	 * @param list
	 *            list
	 * @return json message
	 */
	public String toJson(List<?> list) {
		return gson.toJson(list);
	}

	/**
	 * Convert the given object into a json message.
	 * 
	 * @param obj
	 *            object
	 * @return json message
	 */
	public String toJson(Object obj) {
		return gson.toJson(obj);
	}

	/**
	 * Convert the given object into a json message.
	 * 
	 * @param <T>
	 *            content type
	 * @param content
	 *            content
	 * @param header
	 *            header value map
	 * @return json message
	 */
	public <T> HttpEntity<T> toHttpEntity(T content, MultiValueMap<String, String> header) {
		return new HttpEntity<T>(content, header);
	}

	/**
	 * Convert the given object into a {@link HttpEntity} containing the converted json message.
	 * 
	 * @param content
	 *            content
	 * @return {@link HttpEntity} class containing the converted json message
	 */
	public HttpEntity<String> toJsonHttpEntity(Object content) {
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.set("content-type", "application/json; charset=UTF-8");
		responseHeaders.setPragma("no-cache");
		return toHttpEntity(toJson(content), responseHeaders);
	}

	/**
	 * Convert the given string into {@link HttpEntity} containing the converted json message.
	 * 
	 * @param content
	 *            content
	 * @return json message
	 */
	public HttpEntity<String> toJsonHttpEntity(String content) {
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.set("content-type", "application/json; charset=UTF-8");
		responseHeaders.setPragma("no-cache");
		return toHttpEntity(content, responseHeaders);
	}

	/**
	 * Convert the given map into the json message.
	 * 
	 * @param map
	 *            map
	 * @return json message
	 */
	public String toJson(Map<Object, Object> map) {
		return gson.toJson(map);
	}

	/**
	 * Exception handler to forward to front page showing the error message box.
	 * 
	 * @param e
	 *            occurred exception
	 * @return modal and view having the exception message
	 */
	@ExceptionHandler({ NGrinderRuntimeException.class })
	public ModelAndView handleException(NGrinderRuntimeException e) {
		ModelAndView modelAndView = new ModelAndView("forward:/");
		modelAndView.addObject("exception", e.getMessage());
		return modelAndView;
	}

	/**
	 * Get {@link Config} object.
	 * 
	 * @return config
	 */
	public Config getConfig() {
		return config;
	}
}
