package org.spooler.document;

/**
 * Текстовый документ
 */
public class TextPrintDocument extends AbstractPrintDocument {

    public TextPrintDocument() {
        super("TextDocument", PaperSize.A4, 5656);
    }
}
