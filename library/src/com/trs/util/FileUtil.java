/*
 *	History				Who				What
 *  2012-2-10			Administrator			Created.
 */
package com.trs.util;

import java.io.*;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Title: TRS 内容协作平台（TRS WCM） <BR>
 * Description: <BR>
 * TODO <BR>
 * Copyright: Copyright (c) 2004-2012 北京拓尔思信息技术股份有限公司 <BR>
 * Company: www.trs.com.cn <BR>
 * 
 * @author liuhm
 * @version 1.0
 */
public class FileUtil {

	public static String FILE_WRITING_ENCODING = "UTF-8";

	public static String FILE_READING_ENCODING = "UTF-8";

	/**
	 * Deletes a file path, and all the files and subdirectories in this path
	 * will also be deleted.
	 * 
	 * @param _path
	 *            the specified path.
	 * @return <code>true</code> if the path exists and has been deleted
	 *         successfully; <code>false</code> othewise.
	 */
	public static boolean deleteDir(File _path) {
		// 1. to check whether the path exists
		if (!_path.exists()) {
			return false;
		}

		// 2. to delete the files in the path
		if (_path.isDirectory()) {
			// if _path is not a dir,files=null
			File[] files = _path.listFiles();
			File aFile;
			for (int i = 0; i < files.length; i++) {
				aFile = files[i];
				if (aFile.isDirectory()) {
					deleteDir(aFile);
				} else {
					aFile.delete();
				}
			}// endfor
		}

		// 3. to delete the path self
		return _path.delete();
	}

	/**
	 * 删除指定的文件
	 * 
	 * @param _sFilePathName
	 *            指定的文件名
	 * @return
	 */
	public static boolean deleteFile(String _sFilePathName) {
		File file = new File(_sFilePathName);
		return file.exists() ? file.delete() : false;
	}

	/**
	 * 检查文件是否存在
	 * 
	 * @param _sPathFileName
	 * @return
	 */
	public static boolean fileExists(String _sPathFileName) {
		File file = new File(_sPathFileName);
		return file.exists();
	}

	public static String readFile(String _sFileName, String _sEncoding)
			throws Exception {
		FileReader fileReader = null;
		StringBuffer buffContent = null;
		String sLine;

		FileInputStream fis = null;
		BufferedReader buffReader = null;
		if (_sEncoding == null || "".equals(_sEncoding)) {
			_sEncoding = FILE_READING_ENCODING;
		}

		try {
			fis = new FileInputStream(_sFileName);
			buffReader = new BufferedReader(new InputStreamReader(fis,
					_sEncoding));
			boolean zFirstLine = "UTF-8".equalsIgnoreCase(_sEncoding);
			while ((sLine = buffReader.readLine()) != null) {
				if (buffContent == null) {
					buffContent = new StringBuffer();
				} else {
					buffContent.append("\n");
				}
				if (zFirstLine) {
					sLine = removeBomHeaderIfExists(sLine);
					zFirstLine = false;
				}
				buffContent.append(sLine);
			}// end while
			return (buffContent == null ? "" : buffContent.toString());
		} catch (FileNotFoundException ex) {
			throw new Exception("要读取得文件没有找到！", ex);
		} catch (IOException ex) {
			throw new Exception("读文件时错误！", ex);
		} finally {
			// 增加异常时资源的释放
			try {
				if (fileReader != null)
					fileReader.close();
				if (buffReader != null)
					buffReader.close();
				if (fis != null)
					fis.close();
			} catch (Exception ex) {
			}
		}
	}

	public static boolean writeFile(String _sFileName, String _sFileContent,
			String _sFileEncoding) throws Exception {
		// 1.创建目录
		// FIXME: 这里获取到的文件名会重复, 也会包含非法字符, 会导致写文件出错
		String sPath = extractFilePath(_sFileName);
		if (!pathExists(sPath)) {
			makeDir(sPath, true);
		}

		String sFileEncoding = (_sFileEncoding == null || "".equals("")) ? FILE_WRITING_ENCODING
				: _sFileEncoding;
		boolean bRet = false;
		FileOutputStream fos = null;
		Writer outWriter = null;
		try {
			fos = new FileOutputStream(_sFileName);
			outWriter = new OutputStreamWriter(fos, sFileEncoding); // 指定编码方式
			outWriter.write(_sFileContent); // 写操作
			bRet = true;
		} catch (Exception ex) {
			// CMyLog.e("FileHelper", "写文件[" + _sFileName + "]发生异常");
			throw new Exception("写文件错误", ex);
		} finally {
			try {
				if (outWriter != null) {
					outWriter.flush();
					outWriter.close();
				}
				if (fos != null)
					fos.close();
			} catch (Exception ex) {
			}
		}
		return bRet;
	}

	/**
	 * 从文件的完整路径名（路径+文件名）中提取 路径（包括：Drive+Directroy )
	 * 
	 * @param _sFilePathName
	 * @return
	 */
	public static String extractFilePath(String _sFilePathName) {
		int nPos = _sFilePathName.lastIndexOf('/');
		if (nPos < 0) {
			nPos = _sFilePathName.lastIndexOf('\\');
		}

		return (nPos >= 0 ? _sFilePathName.substring(0, nPos + 1) : "");
	}

	/**
	 * 从文件的完整路径名（路径+文件名）中提取文件名(包含扩展名) <br>
	 * 如：d:\path\file.ext --> file.ext
	 * 
	 * @param _sFilePathName
	 * @return
	 */
	public static String extractFileName(String _sFilePathName) {
		return extractFileName(_sFilePathName, File.separator);
	}

	/**
	 * 从文件的完整路径名（路径+文件名）中提取文件名(包含扩展名) <br>
	 * 如：d:\path\file.ext --> file.ext
	 * 
	 * @param _sFilePathName
	 *            全文件路径名
	 * @param _sFileSeparator
	 *            文件分隔符
	 * @return
	 */
	public static String extractFileName(String _sFilePathName,
			String _sFileSeparator) {
		int nPos = -1;
		if (_sFileSeparator == null) {
			nPos = _sFilePathName.lastIndexOf(File.separatorChar);
			if (nPos < 0) {
				nPos = _sFilePathName
						.lastIndexOf(File.separatorChar == '/' ? '\\' : '/');
			}
		} else {
			nPos = _sFilePathName.lastIndexOf(_sFileSeparator);
		}

		if (nPos < 0) {
			return _sFilePathName;
		}

		return _sFilePathName.substring(nPos + 1);
	}

	/**
	 * 从文件名或者文件的完整路径中解析出文件的后缀
	 * 
	 * @param _sFilePathName
	 * @return
	 */
	public static String extractFileExt(String _sFilePathName) {
		int nPos = _sFilePathName.lastIndexOf('.');
		return (nPos >= 0 ? _sFilePathName.substring(nPos + 1) : "");
	}

	/**
	 * 检查指定文件的路径是否存在
	 * 
	 * @param _sPathFileName
	 *            文件名称(含路径）
	 * @return 若存在，则返回true；否则，返回false
	 */
	public static boolean pathExists(String _sPathFileName) {
		String sPath = extractFilePath(_sPathFileName);
		return fileExists(sPath);
	}

	/**
	 * 移除字符串中的BOM前缀
	 * 
	 * @param _sLine
	 *            需要处理的字符串
	 * @return 移除BOM后的字符串.
	 */
	private static String removeBomHeaderIfExists(String _sLine) {
		if (_sLine == null) {
			return null;
		}
		String line = _sLine;
		if (line.length() > 0) {
			char ch = line.charAt(0);
			// 使用while是因为用一些工具看到过某些文件前几个字节都是0xfffe.
			// 0xfeff,0xfffe是字节序的不同处理.JVM中,一般是0xfeff
			while ((ch == 0xfeff || ch == 0xfffe)) {
				line = line.substring(1);
				if (line.length() == 0) {
					break;
				}
				ch = line.charAt(0);
			}
		}
		return line;
	}

	/**
	 * 创建目录
	 * 
	 * @param _sDir
	 *            目录名称
	 * @param _bCreateParentDir
	 *            如果父目录不存在，是否创建父目录
	 * @return
	 */
	public static boolean makeDir(String _sDir, boolean _bCreateParentDir) {
		boolean zResult = false;
		File file = new File(_sDir);
		if (_bCreateParentDir)
			zResult = file.mkdirs(); // 如果父目录不存在，则创建所有必需的父目录
		else
			zResult = file.mkdir(); // 如果父目录不存在，不做处理
		if (!zResult)
			zResult = file.exists();
		return zResult;
	}

	/**
	 * 从文件的完整路径名（路径+文件名）中提取:主文件名（不包括路径和扩展名）
	 * 
	 * @param _sFilePathName
	 * @return
	 */
	public static String extractMainFileName(String _sFilePathName) {
		String sFileMame = extractFileName(_sFilePathName);
		int nPos = sFileMame.lastIndexOf('.');
		if (nPos > 0) {
			return sFileMame.substring(0, nPos);
		}
		return sFileMame;
	}

	public static void unzip(String zipFile, String destination)
			throws IOException { // 参数为文件所在路径,比如zipFile
		// 为E:\ABC.ZIP,destination为E:\
		ZipFile zip = new ZipFile(zipFile);
		Enumeration<?> en = zip.entries();
		ZipEntry entry = null;
		byte[] buffer = new byte[8192];
		int length = -1;
		InputStream input = null;
		BufferedOutputStream bos = null;
		File file = null;

		while (en.hasMoreElements()) {
			entry = (ZipEntry) en.nextElement();
			if (entry.isDirectory()) {
				continue;
			}
			input = zip.getInputStream(entry);
			file = new File(destination, entry.getName());
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}
			bos = new BufferedOutputStream(new FileOutputStream(file));

			while (true) {
				length = input.read(buffer);
				if (length == -1)
					break;
				bos.write(buffer, 0, length);
			}
			bos.close();
			input.close();

		}
		zip.close();
	}

}
