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
package me.qyh.blog.core.thymeleaf;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import me.qyh.blog.core.bean.ExportPage;
import me.qyh.blog.core.bean.ImportRecord;
import me.qyh.blog.core.bean.PathTemplateLoadRecord;
import me.qyh.blog.core.exception.LogicException;
import me.qyh.blog.core.pageparam.FragmentQueryParam;
import me.qyh.blog.core.pageparam.PageResult;
import me.qyh.blog.core.pageparam.TemplatePageQueryParam;
import me.qyh.blog.core.thymeleaf.DataTag;
import me.qyh.blog.core.thymeleaf.data.DataBind;
import me.qyh.blog.core.thymeleaf.template.Fragment;
import me.qyh.blog.core.thymeleaf.template.Page;
import me.qyh.blog.core.thymeleaf.template.PathTemplate;
import me.qyh.blog.core.thymeleaf.template.Template;

/**
 * 
 * @author Administrator
 *
 */
public interface TemplateService {

	/**
	 * 插入用户自定义模板片段
	 * 
	 * @param fragment
	 *            用户自定义模板片段
	 * @throws LogicException
	 */
	void insertFragment(Fragment fragment) throws LogicException;

	/**
	 * 删除用户自定义挂件
	 * 
	 * @param id
	 *            挂件id
	 * @throws LogicException
	 */
	void deleteFragment(Integer id) throws LogicException;

	/**
	 * 分页查询用户自定义模板片段
	 * 
	 * @param param
	 *            查询参数
	 * @return 模板片段分页
	 */
	PageResult<Fragment> queryFragment(FragmentQueryParam param);

	/**
	 * 更新自定义挂件
	 * 
	 * @param fragment
	 */
	void updateFragment(Fragment fragment) throws LogicException;

	/**
	 * 根据ID查询用户挂件
	 * 
	 * @param id
	 *            挂件ID
	 * @return null如果不存在
	 */
	Optional<Fragment> queryFragment(Integer id);

	/**
	 * 根据ID查询用户页面
	 * 
	 * @param id
	 * @return
	 */
	Optional<Page> queryPage(Integer id);

	/**
	 * 分页查询用户自定义页面
	 * 
	 * @param param
	 * @return
	 */
	PageResult<Page> queryPage(TemplatePageQueryParam param);

	/**
	 * 删除用户自定义页面
	 * 
	 * @param id
	 * @throws LogicException
	 */
	void deletePage(Integer id) throws LogicException;

	/**
	 * 保存页面模板
	 * 
	 * @param page
	 * @param register
	 *            路径注册器
	 * @throws LogicException
	 */
	void buildTpl(Page page) throws LogicException;

	/**
	 * 通过DATA_TAG标签查询数据
	 * 
	 * @param dataTag
	 * @param onlyCallable
	 * @return
	 * @throws LogicException
	 */
	Optional<DataBind<?>> queryData(DataTag dataTag, boolean onlyCallable) throws LogicException;

	/**
	 * 查询系统数据
	 * 
	 * @return
	 */
	List<String> queryDataTags();

	/**
	 * 根据模板名查询模板
	 * 
	 * @param templateName
	 *            模板页面
	 * @return
	 */
	Optional<Template> queryTemplate(String templateName);

	/**
	 * 根据空间导出页面
	 * 
	 * @param spaceId
	 *            空间Id
	 * @return
	 * @throws LogicException
	 *             空间不存在
	 */
	List<ExportPage> exportPage(Integer spaceId) throws LogicException;

	/**
	 * 导入模板
	 * 
	 * @param spaceId
	 *            空间Id
	 * @param exportPages
	 *            要导入的页面
	 * @param importOption
	 *            操作选择
	 * @throws LogicException
	 *             空间不存在
	 */
	List<ImportRecord> importPage(Integer spaceId, List<ExportPage> exportPages);

	/**
	 * 检查template是否和当前的template一致
	 * <p>
	 * <b> 这个方法执行期间的时候不应该出现写操作并行执行 所以consumer应该简短</b>
	 * </p>
	 * 
	 * @param templateName
	 * @param template
	 * @param consumer
	 */
	void compareTemplate(String templateName, Template template, Consumer<Boolean> consumer);

	/**
	 * 获取物理文件模板服务
	 * 
	 * @return
	 * @throws LogicException
	 *             服务不可用
	 */
	PathTemplateService getPathTemplateService() throws LogicException;

	/**
	 * 获取预览模板服务
	 * 
	 * @return 预览模板服务，不会为null
	 * 
	 */
	PreviewService getPreviewService();

	/**
	 * 物理文件模板服务
	 * 
	 * @author Administrator
	 *
	 */
	public interface PathTemplateService {

		/**
		 * 查询物理文件模版
		 * 
		 * @param pattern
		 *            查询表达式(正则)，如果为空或者为null则查询全局
		 * @return 只读的模版集合
		 */
		List<PathTemplate> queryPathTemplates(String pattern);

		/**
		 * 刷新路径
		 * 
		 * @param path
		 * @return
		 */
		List<PathTemplateLoadRecord> loadPathTemplateFile(String path);

		/**
		 * 根据模板名获取一个指定的模板
		 * 
		 * @return
		 */
		Optional<PathTemplate> getPathTemplate(String templateName);

		/**
		 * 注册一个预览页面
		 * 
		 * @param path
		 * @return 预览preview
		 * @throws LogicException
		 *             预览页面不存在，或者页面不能被预览
		 */
		PathTemplate registerPreview(String path) throws LogicException;
	}

	/**
	 * 模板预览服务
	 * <p>
	 * <b>页面只能在用户登录的情况下才能被预览</b>
	 * </p>
	 * 
	 * @author mhlx
	 *
	 */
	public interface PreviewService {
		/**
		 * 注册一个预览页面
		 * <p>
		 * 如果路径对应的mapping不存在，那么将会注册一个preview mapping
		 * </p>
		 * <p>
		 * 如果路径已经存在，那么访问path即可预览该页面
		 * </p>
		 * 
		 * @param path
		 *            模板映射路径
		 * @param template
		 *            用来预览的模板
		 * @throws LogicException
		 *             注册失败
		 */
		void registerPreview(String path, Template template) throws LogicException;

		/**
		 * 清空预览页面，删除mapping
		 */
		void clearPreview();

		/**
		 * 根据模板名查询模板
		 * 
		 * @param templateName
		 *            模板名
		 * @return
		 */
		Optional<Template> getTemplate(String templateName);
	}

}
