package com.github.miachm.sods;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

class ImageObjectRegistry {
    private final Map<String, FileEntry> files = new HashMap<>();
    private final Map<String, List<SheetImage>> pendingImages = new HashMap<>();
    private int imageCounter = 1;

    void registerFile(String path, String mimeType, byte[] data) {
        if (path == null || data == null) {
            return;
        }
        files.put(path, new FileEntry(path, mimeType, data));
        List<SheetImage> pending = pendingImages.remove(path);
        if (pending != null) {
            for (SheetImage image : pending) {
                if (image == null) continue;
                image.setData(data);
                if (image.getMimeType() == null) {
                    image.setMimeType(mimeType);
                }
                if (image.getPath() == null) {
                    image.setPath(path);
                }
            }
        }
    }

    void registerImagePath(String path, SheetImage image) {
        if (path == null || image == null) {
            return;
        }
        FileEntry entry = files.get(path);
        if (entry != null) {
            image.setData(entry.data);
            if (image.getMimeType() == null) {
                image.setMimeType(entry.mimetype);
            }
            if (image.getPath() == null) {
                image.setPath(path);
            }
            return;
        }
        List<SheetImage> list = pendingImages.computeIfAbsent(path, key -> new ArrayList<>());
        if (!list.contains(image)) {
            list.add(image);
        }
    }

    void registerImage(SheetImage image) {
        if (image == null) {
            return;
        }
        String path = image.getPath();
        if (path == null || path.trim().isEmpty()) {
            String extension = extensionForMime(image.getMimeType());
            path = buildImagePath(extension);
            image.setPath(path);
        }
        if (image.getMimeType() == null) {
            image.setMimeType(mimeTypeForExtension(path));
        }
        byte[] data = image.getDataInternal();
        if (data != null) {
            files.put(path, new FileEntry(path, image.getMimeType(), data));
        }
    }

    Collection<FileEntry> getFiles() {
        return files.values();
    }

    private String buildImagePath(String extension) {
        String ext = (extension == null || extension.isEmpty()) ? "png" : extension;
        String path;
        do {
            path = "Pictures/Image" + imageCounter++ + "." + ext;
        } while (files.containsKey(path));
        return path;
    }

    private String extensionForMime(String mimeType) {
        if (mimeType == null) return "png";
        String normalized = mimeType.toLowerCase(Locale.US);
        if (normalized.contains("png")) return "png";
        if (normalized.contains("jpeg") || normalized.contains("jpg")) return "jpg";
        if (normalized.contains("gif")) return "gif";
        if (normalized.contains("bmp")) return "bmp";
        if (normalized.contains("svg")) return "svg";
        return "png";
    }

    private String mimeTypeForExtension(String path) {
        if (path == null) return null;
        String lower = path.toLowerCase(Locale.US);
        if (lower.endsWith(".png")) return "image/png";
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) return "image/jpeg";
        if (lower.endsWith(".gif")) return "image/gif";
        if (lower.endsWith(".bmp")) return "image/bmp";
        if (lower.endsWith(".svg")) return "image/svg+xml";
        return null;
    }
}
