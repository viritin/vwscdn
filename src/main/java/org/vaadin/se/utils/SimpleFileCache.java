package org.vaadin.se.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.MessageDigest;
import java.util.Date;

/* 
 * Simple file-based cache implementation.
 *
 */
public class SimpleFileCache {

    /**
     * Interface for implementing different cache file invalidation strategies.
     *
     *
     */
    public static interface InvalidationRule {

        public boolean isValid(Object cacheKey, long storedAt);
    }

    /**
     * Interface for implementing different cache file naming strategies.
     *
     *
     */
    public static interface FilenameGenerator {

        String getFileNameFor(Object cacheKey);
    }

    /**
     * Cache file naming based on MD5 of the cache key.
     *
     *
     */
    public static class MD5FilenameGenerator implements FilenameGenerator {

        public String getFileNameFor(Object cacheKey) {
            return MD5(cacheKey.toString());
        }

        protected String MD5(String string) {
            return calculateMD5(string);
        }
    }

    /**
     * Cache item invalidation based on time.
     *
     *
     */
    public static class ExpirationRule implements InvalidationRule {

        private long cacheTime;

        public ExpirationRule(long cacheTime) {
            this.cacheTime = cacheTime;
        }

        public boolean isValid(Object cacheKey, long storedAt) {
            return storedAt > (new Date().getTime() - cacheTime);
        }
    }

    public static final long DEFAULT_CACHE_TIME = 1000 * 60 * 60 * 24; // a day
    private long cacheTime = DEFAULT_CACHE_TIME;

    private File cacheDir;
    private InvalidationRule expireRule;
    private FilenameGenerator filenameGenerator;

    public SimpleFileCache(File cacheDirectory, boolean createIfNotExist) {
        if (cacheDirectory == null) {
            throw new IllegalArgumentException(
                    "Cache directory cannot be null.");
        }

        if (!cacheDirectory.exists()) {
            // Missing. Create if requested.
            if (createIfNotExist) {
                cacheDirectory.mkdirs();
            }
        } else {
            // Found. Validate.
            if (!cacheDirectory.isDirectory() || !cacheDirectory.canWrite()) {
                throw new IllegalArgumentException("Invalid cache directory:"
                        + cacheDirectory + ".");
            }
        }

        this.cacheDir = cacheDirectory;

        // Default expire strategy is only based on cache time.
        this.expireRule = new ExpirationRule(cacheTime);

        // Default file naming strategy is using MD5
        this.filenameGenerator = new MD5FilenameGenerator();
    }

    public Object get(Object key) {
        return get(key, expireRule);
    }

    public Object get(Object key, InvalidationRule expireRule) {
        String cacheName = filenameGenerator.getFileNameFor(key);
        File cacheFile = new File(cacheDir, cacheName);
        if (cacheFile.exists()
                && expireRule.isValid(key, cacheFile.lastModified())) {
            try {
                return readFromCache(cacheFile);
            } catch (Exception e) {
                throw new RuntimeException("Cannot read cache file "
                        + cacheFile.getAbsolutePath(), e);

            }
        }
        return null;
    }

    protected Object readFromCache(File cacheFile)
            throws FileNotFoundException, IOException, ClassNotFoundException {
        ObjectInputStream in = null;
        try {
            in = new ObjectInputStream(new FileInputStream(cacheFile));
            return in.readObject();
        } finally {
            if (in != null) {
                in.close();
            }
        }
    }

    protected void writeToCacheFile(File cacheFile, Object data)
            throws FileNotFoundException, IOException {
        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(
                cacheFile));
        try {
            out.writeObject(data);
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    public void store(Object cacheKey, Object data) {
        String fileName = filenameGenerator.getFileNameFor(cacheKey);
        File cacheFile = new File(cacheDir, fileName);
        if (!cacheFile.exists()) {
            try {
                cacheFile.createNewFile();
            } catch (IOException e) {
                new RuntimeException("Cannot create cache file "
                        + cacheFile.getAbsolutePath(), e);
            }
        }
        try {
            writeToCacheFile(cacheFile, data);
        } catch (Exception e) {
            new RuntimeException("Cannot write cache file "
                    + cacheFile.getAbsolutePath(), e);
        }
    }

    private static String calculateMD5(String string) {
        try {
            byte[] bytesOfMessage = string.getBytes("UTF-8");
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] thedigest = md.digest(bytesOfMessage);

            StringBuffer hexString = new StringBuffer();

            for (int i = 0; i < thedigest.length; i++) {
                String hex = Integer.toHexString(0xFF & thedigest[i]);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("MD5 failed", e);
        }
    }

    public void setCacheTime(long cacheTime) {
        this.cacheTime = cacheTime;
        if (cacheTime >= 0) {
            this.expireRule = new ExpirationRule(cacheTime);
        }
    }

    public long getCacheTime() {
        return cacheTime;
    }

    public void setExpireRule(InvalidationRule expireRule) {
        this.expireRule = expireRule;
        if (this.expireRule == null) {
            this.expireRule = new ExpirationRule(cacheTime);
        }
    }

    public InvalidationRule getExpireRule() {
        return expireRule;
    }

    public void setFilenameGenerator(FilenameGenerator filenameGenerator) {
        this.filenameGenerator = filenameGenerator;
        if (this.filenameGenerator == null) {
            this.filenameGenerator = new MD5FilenameGenerator();
        }
    }

    public FilenameGenerator getFilenameGenerator() {
        return filenameGenerator;
    }

}
