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
package me.qyh.blog.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Objects;
import java.util.function.Predicate;

import me.qyh.blog.core.config.Constants;
import me.qyh.blog.core.exception.SystemException;

public class FileUtils {
	public static final Path HOME_DIR = Paths.get(System.getProperty("user.home"));

	/**
	 * 博客用来存放临时文件的文件夹
	 */
	private static final Path TEMP_DIR = HOME_DIR.resolve("blog_temp");

	static {
		forceMkdir(TEMP_DIR);
	}

	private FileUtils() {

	}

	/**
	 * 采用UUID创造一个文件，这个文件为系统临时文件，会通过定时任务删除
	 * 
	 * @param ext
	 *            文件后缀
	 * @return 临时文件
	 * @see FileUtils#clearAppTemp(Predicate)
	 */
	public static Path appTemp(String ext) {
		String name = StringUtils.uuid() + "." + ext;
		try {
			return Files.createFile(TEMP_DIR.resolve(name));
		} catch (IOException e) {
			throw new SystemException(e.getMessage(), e);
		}
	}

	/**
	 * write bits to path
	 * 
	 * @param bytes
	 * @param file
	 * @throws IOException
	 */
	public static void write(byte[] bytes, Path path) throws IOException {
		Files.write(path, bytes, StandardOpenOption.WRITE);
	}

	/**
	 * 获取文件的后缀名
	 * 
	 * @param fullName
	 * @return
	 */
	public static String getFileExtension(String fullName) {
		String fileName = new File(fullName).getName();
		int dotIndex = fileName.lastIndexOf('.');
		return (dotIndex == -1) ? "" : fileName.substring(dotIndex + 1);
	}

	/**
	 * 获取文件的后缀名
	 * 
	 * @param fullName
	 * @return
	 */
	public static String getFileExtension(Path path) {
		String fileName = path.getFileName().toString();
		int dotIndex = fileName.lastIndexOf('.');
		return (dotIndex == -1) ? "" : fileName.substring(dotIndex + 1);
	}

	/**
	 * 获取文件名(不包括后缀)
	 * 
	 * @param file
	 * @return
	 */
	public static String getNameWithoutExtension(String file) {
		String fileName = new File(file).getName();
		int dotIndex = fileName.lastIndexOf('.');
		return (dotIndex == -1) ? fileName : fileName.substring(0, dotIndex);
	}

	/**
	 * 删除文件|文件夹
	 * 
	 * @param file
	 * @return
	 */
	public static boolean deleteQuietly(Path path) {
		if (path == null || !Files.exists(path)) {
			return true;
		}
		try {
			Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					Files.delete(file);
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
					Files.delete(dir);
					return FileVisitResult.CONTINUE;
				}
			});
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 创建一个文件
	 * 
	 * @param path
	 */
	public static void createFile(Path path) {
		synchronized (FileUtils.class) {
			if (!Files.exists(path)) {
				try {
					Files.createDirectories(path.getParent());
					Files.createFile(path);
				} catch (IOException e) {
					throw new SystemException("创建文件夹：" + path + "失败:" + e.getMessage(), e);
				}
			} else {
				if (!Files.isRegularFile(path)) {
					throw new SystemException("目标位置" + path + "已经存在文件，但不是文件");
				}
			}
		}
	}

	/**
	 * 创建一个文件夹，如果失败，抛出异常
	 * 
	 * @param parentFile
	 */
	public static void forceMkdir(Path path) {
		synchronized (FileUtils.class) {
			if (!Files.exists(path)) {
				try {
					Files.createDirectories(path);
				} catch (IOException e) {
					throw new SystemException("创建文件夹：" + path + "失败:" + e.getMessage(), e);
				}
			} else {
				if (!Files.isDirectory(path)) {
					throw new SystemException("目标位置" + path + "已经存在文件，但不是文件夹");
				}
			}
		}
	}

	/**
	 * 拷贝一个文件
	 * 
	 * @param source
	 * @param target
	 * @throws IOException
	 */
	public static void copy(Path source, Path target) throws IOException {
		forceMkdir(target.getParent());
		Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
	}

	/**
	 * 移动一个文件
	 * 
	 * @param png
	 * @param dest
	 * @throws IOException
	 */
	public static void move(Path source, Path target) throws IOException {
		forceMkdir(target.getParent());
		Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
	}

	/**
	 * is write to os
	 * 
	 * @param is
	 * @param os
	 * @throws IOException
	 */
	// copied from Files
	public static void write(InputStream source, OutputStream sink) throws IOException {
		Objects.requireNonNull(source);
		Objects.requireNonNull(sink);
		try {
			byte[] buf = new byte[8192];
			int n;
			while ((n = source.read(buf)) > 0) {
				sink.write(buf, 0, n);
			}
			sink.flush();
		} finally {
			try {
				source.close();
			} catch (IOException e) {
			}
		}
	}

	/**
	 * 获取子文件
	 * <p>
	 * eg:sub(Paths.get("h:/123"), "/123/414\\wqeqw") ==>
	 * h:\123\123\414\wqeqw(windows)
	 * </p>
	 * 
	 * @param p
	 * @param sub
	 * @return
	 */
	public static Path sub(Path p, String sub) {
		Objects.requireNonNull(p);
		Objects.requireNonNull(sub);
		return p.resolve(cleanPath(sub));
	}

	/**
	 * 删除系统临时文件夹内符合条件的文件
	 * 
	 * @param predicate
	 */
	public static void clearAppTemp(Predicate<Path> predicate) {
		Objects.requireNonNull(predicate);
		try {
			Files.walkFileTree(TEMP_DIR, new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {

					if (predicate.test(file)) {
						try {
							Files.delete(file);
						} catch (Exception e) {
						}
					}
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
					return FileVisitResult.CONTINUE;
				}
			});
		} catch (IOException e) {
			// ignore
		}
	}

	/**
	 * 删除连续的 '/'，开头和结尾的'/'
	 * 
	 * <pre>
	 * clean("\\\\////123/\\\\////456//\\\\////789.txt") = '123/456/789.txt';
	 * </pre>
	 * 
	 * @param path
	 * @return
	 */
	public static String cleanPath(String path) {
		String cleaned = Validators.cleanPath(path);
		if ("/".equals(path)) {
			return "";
		}
		if (cleaned.startsWith("/")) {
			cleaned = cleaned.substring(1, cleaned.length());
		}
		if (cleaned.endsWith("/")) {
			cleaned = cleaned.substring(0, cleaned.length() - 1);
		}
		return cleaned;
	}

	/**
	 * 要创建的文件是否在缩略图文件夹中
	 * 
	 * @param dest
	 * @param parent
	 * @return
	 */
	public static boolean isSub(Path dest, Path parent) {
		try {
			String canonicalP = parent.toFile().getCanonicalPath();
			String canonicalC = dest.toFile().getCanonicalPath();
			return canonicalP.equals(canonicalC)
					|| canonicalC.regionMatches(false, 0, canonicalP, 0, canonicalP.length());
		} catch (IOException e) {
			throw new SystemException(e.getMessage(), e);
		}
	}

	/**
	 * Path to string
	 * 
	 * @param is
	 * @return
	 */
	public static String toString(Path path) throws IOException {
		return new String(Files.readAllBytes(path), Constants.CHARSET);
	}
}
