package pl.einformatyka.mmse.cache;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.File;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;

public class JobResultCache {
	private String executedFileName = null;
	private String executingFileName = null;
	private static JobResultCache cache = null;
	private Properties executedCache = null;
	private Properties executingCache = null;

	public static JobResultCache getCache(String executed, String executing) {
		if (cache == null) {
			cache = new JobResultCache(executed, executing);
		}
		return cache;
	}

	public static JobResultCache getCache() {
		return JobResultCache.getCache("cache/executed", "cache/executing");
	}

	private JobResultCache(String executed, String executing) {
		executedFileName = executed;
		executingFileName = executing;
		executedCache = new Properties();
		executingCache = new Properties();
		InputStream input = null;

		try {
			input = new FileInputStream(executedFileName);
			executedCache.load(input);
			input.close();
		} catch (Exception e) {
		}

		try {
			input = new FileInputStream(executingFileName);
			executingCache.load(input);
			input.close();
		} catch (Exception e) {
		}
	}

	private String encrypt(String toEncrypt) {
		try {
			MessageDigest messageDigest = MessageDigest.getInstance("MD5");
			messageDigest.update(toEncrypt.getBytes());
			return new String(messageDigest.digest());
		} catch (NoSuchAlgorithmException e) {
			return null;
		}
	}

	private boolean checkFile(String directoryName) {
		File theDir = new File(directoryName).getParentFile();
		boolean result = true;

		if (!theDir.exists()) {
			result = false;

			try {
				theDir.mkdir();
				result = true;
			} catch (SecurityException se) {
			}
		}
		return true;
	}

	private boolean save(String fileName, Properties prop) {
		OutputStream output = null;

		try {
			if (checkFile(fileName)) {
				output = new FileOutputStream(fileName);
				output.write(new byte[] {});
				prop.store(output, "comments");
				output.close();
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean setExecuting(String boaJob, String username, Integer datasetId, Integer jobId) {
		String encrypted = encrypt(boaJob + username + datasetId.toString());
		if (encrypted == null) {
			return false;
		} else {
			executingCache.setProperty(encrypted, jobId.toString());

			return save(executingFileName, executingCache);
		}
	}

	public int changeToExecuted(String boaJob, String username, Integer datasetId) {
		String encrypted = encrypt(boaJob + username + datasetId.toString());
		int jobId = getExecuting(encrypted);
		if (jobId != -1) {
			executingCache.remove(encrypted);
			save(executingFileName, executingCache);
			if (set(boaJob, username, datasetId, jobId)) {
				return jobId;
			} else {
				return -1;
			}
		} else {
			return -1;
		}
	}

	public boolean set(String boaJob, String username, Integer datasetId, Integer jobId) {
		String encrypted = encrypt(boaJob + username + datasetId.toString());
		if (encrypted == null) {
			return false;
		} else {
			executedCache.setProperty(encrypted, jobId.toString());

			return save(executedFileName, executedCache);
		}
	}

	private int getExecuting(String encrypted) {
		try {
			return Integer.parseInt((String) executingCache.getProperty(encrypted, "-1"));
		} catch (Exception e) {
		}
		return -1;
	}

	public int get(String boaJob, String username, Integer datasetId) throws ExecutingException {
		String encrypted = encrypt(boaJob + username + datasetId.toString());
		if (encrypted != null) {
			try {
				return Integer.parseInt((String) executedCache.getProperty(encrypted));
			} catch (Exception e) {
				int jobId = getExecuting(encrypted);
				if (jobId != -1) {
					throw new ExecutingException(jobId);
				} else {
					return -1;
				}
			}
		} else {
			return -1;
		}
	}
}
