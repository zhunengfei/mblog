/*
 * Copyright 2016 qyh.me
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package me.qyh.blog.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import me.qyh.blog.core.bean.JsonResult;
import me.qyh.blog.core.exception.LogicException;
import me.qyh.blog.core.message.Message;
import me.qyh.blog.core.pageparam.FragmentQueryParam;
import me.qyh.blog.core.pageparam.SpaceQueryParam;
import me.qyh.blog.core.service.SpaceService;
import me.qyh.blog.core.thymeleaf.TemplateService;
import me.qyh.blog.core.thymeleaf.template.Fragment;
import me.qyh.blog.web.controller.form.FragmentQueryParamValidator;
import me.qyh.blog.web.controller.form.FragmentValidator;

@Controller
@RequestMapping("mgr/template/fragment")
public class FragmentMgrController extends BaseMgrController {

	@Autowired
	private TemplateService templateService;
	@Autowired
	private FragmentQueryParamValidator fragmentParamValidator;
	@Autowired
	private FragmentValidator fragmentValidator;
	@Autowired
	private SpaceService spaceService;

	@InitBinder(value = "fragmentQueryParam")
	protected void initFragmentQueryParamBinder(WebDataBinder binder) {
		binder.setValidator(fragmentParamValidator);
	}

	@InitBinder(value = "fragment")
	protected void initFragmentBinder(WebDataBinder binder) {
		binder.setValidator(fragmentValidator);
	}

	@GetMapping("index")
	public String index(@Validated FragmentQueryParam fragmentQueryParam, BindingResult result, Model model) {
		if (result.hasErrors()) {
			fragmentQueryParam = new FragmentQueryParam();
			fragmentQueryParam.setCurrentPage(1);
		}
		model.addAttribute("page", templateService.queryFragment(fragmentQueryParam));
		model.addAttribute("spaces", spaceService.querySpace(new SpaceQueryParam()));
		return "mgr/template/fragment";
	}

	@GetMapping("list")
	@ResponseBody
	public JsonResult listJson(@Validated FragmentQueryParam fragmentQueryParam, BindingResult result, Model model) {
		if (result.hasErrors()) {
			fragmentQueryParam = new FragmentQueryParam();
			fragmentQueryParam.setCurrentPage(1);
		}
		return new JsonResult(true, templateService.queryFragment(fragmentQueryParam));
	}

	@PostMapping("create")
	@ResponseBody
	public JsonResult create(@RequestBody @Validated final Fragment fragment) throws LogicException {
		if (fragment.isGlobal()) {
			fragment.setSpace(null);
		}
		templateService.insertFragment(fragment);
		return new JsonResult(true, new Message("fragment.user.create.success", "创建成功"));
	}

	@PostMapping("delete")
	@ResponseBody
	public JsonResult delete(@RequestParam("id") Integer id) throws LogicException {
		templateService.deleteFragment(id);
		return new JsonResult(true, new Message("fragment.user.delete.success", "删除成功"));
	}

	@PostMapping("update")
	@ResponseBody
	public JsonResult update(@RequestBody @Validated final Fragment fragment) throws LogicException {
		if (fragment.isGlobal()) {
			fragment.setSpace(null);
		}
		templateService.updateFragment(fragment);
		return new JsonResult(true, new Message("fragment.user.update.success", "更新成功"));
	}

	@GetMapping("get/{id}")
	@ResponseBody
	public JsonResult get(@PathVariable("id") Integer id) throws LogicException {
		return templateService.queryFragment(id).map(fragment -> new JsonResult(true, fragment))
				.orElse(new JsonResult(false));
	}
}
