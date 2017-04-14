package org.diylc.app;

import java.io.File;
import java.util.Arrays;
import java.util.Objects;

import javax.swing.filechooser.FileFilter;

import lombok.Getter;

public enum FileFilterEnum {

  PNG("PNG Images (*.png)", "png"),
  PDF("PDF Files (*.pdf)", "pdf"),
  DIY("DIY Project Files (*.diy)", "diy"),
  EXCEL("Excel Workbooks (*.xls)", "xls"),
  CSV("Comma Separated Files (*.csv)", "csv"),
  HTML("HTML Files (*.html)", "html"),
  IMAGES("Image Files (*.png, *.jpg, *.gif)", "png", "jpg", "gif");

  @Getter
  private FileFilter filter;
  
  @Getter
  private String[] extensions;

  private FileFilterEnum(final String description, final String... extensions) {
    this.extensions = extensions;
    filter = new FileFilter() {

      @Override
      public boolean accept(File f) {
        if (f.isDirectory()) {
          return true;
        }
        String fileName = f.getName();
        final String fileExt = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();

        return Arrays.stream(extensions)
            .filter(Objects::nonNull)
            .filter(ext -> ext.equals(fileExt))
            .findFirst()
            .orElse(null) != null;
      }

      @Override
      public String getDescription() {
        return description;
      }
    };
  }

}
