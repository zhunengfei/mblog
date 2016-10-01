package me.qyh.blog.ui.data;

import java.util.Calendar;

import org.springframework.beans.factory.annotation.Autowired;

import me.qyh.blog.bean.ArticleDateFile;
import me.qyh.blog.bean.ArticleDateFiles;
import me.qyh.blog.bean.ArticleDateFiles.ArticleDateFileMode;
import me.qyh.blog.entity.Space;
import me.qyh.blog.exception.LogicException;
import me.qyh.blog.security.UserContext;
import me.qyh.blog.service.ArticleService;
import me.qyh.blog.ui.Params;

public class ArticleDateFilesDataTagProcessor extends DataTagProcessor<ArticleDateFiles> {

	@Autowired
	private ArticleService articleService;

	private static final String MODE = "mode";

	public ArticleDateFilesDataTagProcessor(String name, String dataName) {
		super(name, dataName);
	}

	@Override
	protected ArticleDateFiles buildPreviewData(Attributes attributes) {
		ArticleDateFiles files = new ArticleDateFiles();
		files.setMode(getMode(attributes));
		Calendar cal = Calendar.getInstance();
		ArticleDateFile file1 = new ArticleDateFile();
		file1.setBegin(cal.getTime());
		file1.setCount(1);
		files.addArticleDateFile(file1);

		ArticleDateFile file2 = new ArticleDateFile();
		cal.add(Calendar.MONTH, -1);
		file2.setBegin(cal.getTime());
		file2.setCount(2);
		files.addArticleDateFile(file2);

		files.calDate();

		return files;
	}

	@Override
	protected ArticleDateFiles query(Space space, Params params, Attributes attributes) throws LogicException {
		ArticleDateFileMode mode = getMode(attributes);
		return articleService.queryArticleDateFiles(space, mode, UserContext.get() != null);
	}

	private ArticleDateFileMode getMode(Attributes attributes) {
		ArticleDateFileMode mode = ArticleDateFileMode.YM;
		String v = attributes.get(MODE);
		if (v != null)
			try {
				mode = ArticleDateFileMode.valueOf(v);
			} catch (Exception e) {
			}
		return mode;
	}

}