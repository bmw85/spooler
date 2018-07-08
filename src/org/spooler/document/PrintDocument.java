package org.spooler.document;

/**
 * Документ который может быть напечатан с помощью
 */
public interface PrintDocument {
    PaperSize getPaperSize();
    int getPrintTime();
    String getDocumentTypeName();
}
