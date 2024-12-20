package com.homecloud.entity.enums;


import org.apache.commons.lang3.ArrayUtils;

public enum FileTypeEnums {

    VIDEO(FileCategoryEnums.VIDEO, 1, new String[]{".mp4",  ".rmvb", ".mkv", ".avi"}, "video"),
    MUSIC(FileCategoryEnums.MUSIC, 2, new String[]{".mp3",".ape", ".wav", ".mp2", ".flac", ".ra", ".midi", ".wma", ".aac"}, "music"),
    IMAGE(FileCategoryEnums.IMAGE, 3, new String[]{ ".jpg", ".png", ".xmp", ".gif", ".bmp", ".dds",".tiff", ".psd", ".pdt", ".webp", ".jpeg", ".dds", ".svg"}, "image"),
    PDF(FileCategoryEnums.DOC, 4, new String[]{".pdf"}, "pdf"),
    WORD(FileCategoryEnums.DOC, 5, new String[]{".docx"}, "word"),
    EXCEL(FileCategoryEnums.DOC, 6, new String[]{".xlsx"}, "excel"),
    TXT(FileCategoryEnums.DOC, 7, new String[]{".txt"}, "txt"),
    PROGRAME(FileCategoryEnums.OTHERS, 8, new String[]{".h", ".c", ".hpp", ".css", ".hxx",  ".cc", ".c++", ".html", ".cxx", ".m", ".o", ".cpp", ".s", ".dll",
            ".java", ".js", ".ts",  ".json", ".scss", ".vue", ".jsx",  ".class", ".sql",".cs", ".md", ".xml"}, "CODE"),
    ZIP(FileCategoryEnums.OTHERS, 9, new String[]{"rar", ".gz", ".zip",  ".mpq", ".cab",  ".lzh",".jar",  ".7z",".tar",  ".ace", ".uue", ".arj", ".bz",  ".iso"}, "compressed"),
    OTHERS(FileCategoryEnums.OTHERS, 10, new String[]{}, "others");

    private FileCategoryEnums category;
    private Integer type;
    private String[] suffixs;
    private String desc;

    FileTypeEnums(FileCategoryEnums category, Integer type, String[] suffixs, String desc) {
        this.category = category;
        this.type = type;
        this.suffixs = suffixs;
        this.desc = desc;
    }

    public static FileTypeEnums getFileTypeBySuffix(String suffix) {
        for (FileTypeEnums item : FileTypeEnums.values()) {
            if (ArrayUtils.contains(item.getSuffixs(), suffix)) {
                return item;
            }
        }
        return FileTypeEnums.OTHERS;
    }

    public static FileTypeEnums getByType(Integer type) {
        for (FileTypeEnums item : FileTypeEnums.values()) {
            if (item.getType().equals(type)) {
                return item;
            }
        }
        return null;
    }

    public String[] getSuffixs() {
        return suffixs;
    }

    public FileCategoryEnums getCategory() {
        return category;
    }

    public Integer getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }
}
