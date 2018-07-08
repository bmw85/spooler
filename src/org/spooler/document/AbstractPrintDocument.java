package org.spooler.document;

/**
 * Абстрактный класс печатного документа для класса Spooler
 */
abstract public class AbstractPrintDocument implements PrintDocument {
    private final int printTime;
    private final PaperSize paperSize;
    private final String documentTypeName;


    /**
     * @param documentTypeName Название типа документа
     * @param paperSize        Размер бумаги
     * @param printTime        Продолжительность печати
     */
    protected AbstractPrintDocument(String documentTypeName, PaperSize paperSize, int printTime) {
        this.documentTypeName = documentTypeName;
        this.paperSize = paperSize;
        this.printTime = printTime;
    }


    public int getPrintTime() {
        return printTime;
    }

    public PaperSize getPaperSize() {
        return paperSize;
    }

    public String getDocumentTypeName() {
        return documentTypeName;
    }
};

